package com.pitchforkbunnies.blobber.core;

import org.newdawn.slick.opengl.Texture;

/**
 * Class representing one tile in level-space
 * @author James
 *
 */
public class Tile {
	public static final int TILE_WIDTH_P = 32;
	public static final float TILE_WIDTH_H = Graphics.pixelsToUnits(TILE_WIDTH_P); 
	
	public int x, y;
	public Level level;
	public boolean walkable;
	public Sprite sprite;
	
	/**
	 * Constructs a new tile at the given location in the level (does NOT update the level's tile array)
	 * @param level
	 * @param x
	 * @param y
	 */
	public Tile(Level level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
		sprite = new Sprite(level.bundle.textures.numbers, 0, 0, 20, 20, 1, 1);
	}
	
	/**
	 * Advances the tile one step further in time (1/60 seconds)
	 */
	public void tick() {
		sprite.tick();
	}
	
	/**
	 * Draws the tile at the given position
	 * @param g The graphics handle to use
	 * @param x The left edge of the sprite
	 * @param y The top edge of the sprite
	 */
	public void render(Graphics g, float x, float y) {
		sprite.drawAt(g, x + sprite.getWidth() * 0.5f, y + sprite.getHeight() * 0.5f);
	}
	
	/**
	 * Determines if the specified entity can collides with this tile
	 * @param e The entity to check
	 * @return <code>true</code> if the entity can collide, <code>false</code> otherwise
	 */
	public boolean collides(Entity e) {
		return !walkable;
	}
	
	/**
	 * Convenience method to set the walkable property
	 * @param b Whether to make the tile walkable
	 * @return pointer to the tile for convenience
	 */
	public Tile setWalkable(boolean b) {
		walkable = b;
		return this;
	}
	
	/**
	 * Sets the graphical representation of the tile
	 * @param id The index of the first frame of the animation
	 * @param frames The number of frames in the animation
	 * @param period The period of the animation
	 * @return pointer to the tile for convenience
	 */
	public Tile setSprite(int id, int frames, int period) {
		Texture tex = level.bundle.textures.tiles;
		int w = tex.getImageWidth() / TILE_WIDTH_P;
		sprite = new Sprite(tex, 
				TILE_WIDTH_P * (id % w), 
				TILE_WIDTH_P * (id / w), 
				TILE_WIDTH_P, 
				TILE_WIDTH_P, 
				frames, period);
		return this;
	}

	/**
	 * Method to be overridden by specific tiles, triggers when an entity walks on the tile 
	 * @param e
	 */
	public void onWalk(Entity e) {
	}
}
