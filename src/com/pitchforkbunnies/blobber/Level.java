package com.pitchforkbunnies.blobber;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public abstract class Level {
	public ResourceBundle bundle;
	public int width, height;
	public Tile[][] tiles;
	public List<Entity> entities = new ArrayList<Entity>();
	public float gravity = 0.01f;
	public EntityPlayer player;
	
	//upper-left corner of the viewport in tile space
	public float xo = 0, yo = 0;
	
	/**
	 * Loads a level from the given image file
	 * @param ref the path to the image file representing the level
	 */
	public Level(String ref, ResourceBundle bundle) {
		this.bundle = bundle;
		player = new EntityPlayer(this);
		entities.add(player);
		loadLevel(ref);
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
		switch(color & 0xFFFFFF) {
		case 0x000000: return new Tile(this, x, y).setWalkable(false).setSprite(2, 1, 1);
		case 0xFFFFFF: return new Tile(this, x, y).setWalkable(true).setSprite(1, 1, 1);
		default: return new Tile(this, x, y);
		}
	}
	
	/**
	 * Advances the game logic one step further (1/60 seconds)
	 */
	public void tick() {
		for(Entity e : entities) {
			e.tick();
			e.dy += gravity;
		}
		
		for(Tile[] ta : tiles) {
			for(Tile t : ta)
				t.tick();
		}
	}
	
	/**
	 * Renders the level (tiles, entities, player)
	 * @param g The graphics handle to use
	 */
	public void render(Graphics g) {
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
		
		if(tiles[(int) x][(int) y].collides(e)) return true;
		if(tiles[(int) (x + e.width)][(int) y].collides(e)) return true;
		if(tiles[(int) x][(int) (y + e.height)].collides(e)) return true;
		if(tiles[(int) (x + e.width)][(int) (y + e.height)].collides(e)) return true;
		
		return false;
	}

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
