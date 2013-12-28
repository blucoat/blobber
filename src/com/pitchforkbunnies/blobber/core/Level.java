package com.pitchforkbunnies.blobber.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.pitchforkbunnies.blobber.entity.EntityChargerEnemy;
import com.pitchforkbunnies.blobber.entity.EntityPlayer;
import com.pitchforkbunnies.blobber.tile.TileSpike;
import com.pitchforkbunnies.blobber.tile.TileTrigger;

public abstract class Level {
	public ResourceBundle bundle;
	public int width, height, spawnx, spawny;
	public Tile[][] tiles;
	public List<Entity> entities = new ArrayList<Entity>();
	public List<LightSource> lights = new ArrayList<LightSource>();
	public float gravity = 0.01f;
	public EntityPlayer player;
	public LightMap lightmap;
	public Level next = null;
	
	private String ref;
	
	//upper-left corner of the viewport in tile space
	public float xo = 0, yo = 0;
	
	/**
	 * Loads a level from the given image file
	 * @param ref the path to the image file representing the level
	 */
	public Level(String ref, ResourceBundle bundle) {
		this.ref = ref;
		this.bundle = bundle;
		loadLevel(ref);
		
		player = new EntityPlayer(this);
		player.x = spawnx;
		player.y = spawny;
		entities.add(player);
	}
	
	public void reset() {
		entities.clear();
		lights.clear();
		
		loadLevel(ref);
		
		player = new EntityPlayer(this);
		player.x = spawnx;
		player.y = spawny;
		entities.add(player);
	}
	
	private void loadLevel(String ref) {
		InputStream in = getClass().getClassLoader().getResourceAsStream(ref);
		if(in == null)
			throw new RuntimeException("Could not load level image: " + ref);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		width = img.getWidth(null);
		height = img.getHeight(null);
		tiles = new Tile[width][height];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				tiles[i][j] = getTileFromColor(img.getRGB(i, j), i, j);
			}
		}
	}
	
	private Tile getTileFromColor(int color, int x, int y) {
		color = color & 0xFFFFFF;
		switch(color) {
		case 0x000000: return new Tile(this, x, y).setWalkable(false).setSprite(4, 1, 1);
		case 0xFFFFFF: return new Tile(this, x, y).setWalkable(true).setSprite(1, 1, 1);
		case 0x0026FF:
			spawnx = x;
			spawny = y;
			return new Tile(this, x, y).setWalkable(true).setSprite(1, 1, 1);
		case 0xFF0000: return new TileTrigger(this, x, y);
		case 0x00FF21: return new TileSpike(this, x, y);
		case 0xFFFF00:
			spawnEntity(new EntityChargerEnemy(this), x, y);
			return new Tile(this, x, y).setWalkable(true).setSprite(1, 1, 1);
			
		default:
			lights.add(new LightSource(
					x + .5f, 
					y + .5f, 
					((color >> 16) & 0xFF) / 256f, 
					((color >> 8) & 0xFF) / 256f,
					(color & 0xFF) / 256f));
			return new Tile(this, x, y).setWalkable(true).setSprite(1, 1, 1);
		}
	}
	
	private void spawnEntity(Entity e, float x, float y) {
		e.x = x;
		e.y = y;
		entities.add(e);
	}
	
	/**
	 * Advances the game logic one step further (1/60 seconds)
	 */
	public void tick() {
		for(Entity e : entities) {
			e.tick();
			e.dy += gravity;
		}
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e1 = entities.get(i);
			for(int j = i + 1; j < entities.size(); j++) {
				Entity e2 = entities.get(j);
				if((e1.x + e1.width > e2.x && e1.x + e1.width < e2.x + e2.width || e2.x + e2.width > e1.x && e2.x + e2.width < e1.x + e1.width) && 
						(e1.y + e1.height > e2.y && e1.y + e1.height < e2.y + e2.height || e2.y + e2.height > e1.y && e2.y + e2.height < e1.y + e1.height)) {
					e1.onCollide(e2);
					e2.onCollide(e1);
				}
			}
		}
		
		for(Tile[] ta : tiles) {
			for(Tile t : ta)
				t.tick();
		}
		
		for(int i = entities.size() - 1; i >= 0; i--) {
			if(entities.get(i).isDead) {
				if(entities.get(i) == player) {
					reset();
					return;
				}
				
				entities.remove(i);
			}
		}
	}
	
	/**
	 * Renders the level (tiles, entities, player)
	 * @param g The graphics handle to use
	 */
	public void render(Graphics g) {
		lightmap.setAttenuation(.2f, .5f, 1);
		lightmap.setAmbientLight(.05f, .05f, .1f);
		
		int right = (int) Math.min(width - 1, xo + Graphics.getWidth() / Tile.TILE_WIDTH_H);
		int left = (int) Math.min(height - 1, yo + Graphics.getHeight() / Tile.TILE_WIDTH_H);
		
		for(int i = (int) xo; i <= right; i++) {
			for(int j = (int) yo; j <= left; j++) {
				if(i >= 0 && j >= 0)
					tiles[i][j].render(g, (i - xo) * Tile.TILE_WIDTH_H, (j - yo) * Tile.TILE_WIDTH_H);
			}
		}
		
		for(Entity e : entities) {
			float xx = (e.x - xo) * Tile.TILE_WIDTH_H;
			float yy = (e.y - yo) * Tile.TILE_WIDTH_H;
			float w = e.width * Tile.TILE_WIDTH_H;
			float h = e.height * Tile.TILE_WIDTH_H;
			
			if(xx + w > 0 && yy + h > 0 && xx < Graphics.getWidth() && yy < Graphics.getHeight()) {
				e.render(g, xx, yy);
			}
		}
	}
	
	/**
	 * Determines if the entity will collide with terrain at the given point
	 * @param e The entity to check
	 * @return <code>true</code> if the entity collides, <code>false</code> otherwise
	 */
	public boolean collides(Entity e, float x, float y) {
		if(x < 0 || y < 0 || x + e.width >= width || y + e.height >= height)
			return true;
		
		int xSamples = (int) (e.width + 1), ySamples = (int) (e.height + 1);
		float xStep = e.width / xSamples, yStep = e.height / ySamples;
		for(int xx = 0; xx <= xSamples; xx++) {
			for(int yy = 0; yy <= ySamples; yy++) {
				if(tiles[(int) (x + xx * xStep)][(int) (y + yy * yStep)].collides(e)) return true;
			}
		}
		return false;
	}

	public abstract void trigger(int x, int y);
	
	public void fixCamera(Graphics g) {
		xo = player.x - 0.5f * Graphics.getWidth() / Tile.TILE_WIDTH_H;
		yo = player.y - 0.5f * Graphics.getHeight() / Tile.TILE_WIDTH_H;
		if(xo < 0) xo = 0;
		if(yo < 0) yo = 0;
		if(xo > width - Graphics.getWidth() / Tile.TILE_WIDTH_H) 
			xo = width - Graphics.getWidth() / Tile.TILE_WIDTH_H;
		if(yo > height - Graphics.getHeight() / Tile.TILE_WIDTH_H) 
			yo = height - Graphics.getHeight() / Tile.TILE_WIDTH_H;
	}
}
