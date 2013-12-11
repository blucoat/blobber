package com.pitchforkbunnies.blobber.core;


public abstract class Screen {
	protected ResourceBundle bundle;
	
	public Screen(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	/**
	 * Progresses the game one step forward in time (1/60 seconds)
	 * @return The new screen to switch to, or null to keep the current screen
	 */
	public abstract Screen tick();
	
	/**
	 * Draws the screen to the display.
	 * @param g The graphics handle to use for drawing
	 */
	public abstract void render(Graphics g);

	public void renderLight(Graphics g) {
		g.setLightmap(null);
	}
}
