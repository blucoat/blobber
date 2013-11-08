package com.pitchforkbunnies.blobber;

public abstract class Level {
	/**
	 * Loads a level from the given image file
	 * @param ref the path to the image file representing the level
	 */
	public Level(String ref) {
		
	}
	
	/**
	 * Advances the game logic one step further (1/60 seconds)
	 */
	public void tick() {
		
	}
	
	/**
	 * Renders the level (tiles, entities, player)
	 * @param g The graphics handle to use
	 */
	public void render(Graphics g) {
		
	}
}
