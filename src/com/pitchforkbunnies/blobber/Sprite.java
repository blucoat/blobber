package com.pitchforkbunnies.blobber;

import org.newdawn.slick.opengl.Texture;

public class Sprite {
	private Texture tex;
	private int sx, sy, w, h, frames, period;
	private int t = 0, f = 0;
	private boolean playing = true;
	
	/**
	 * Constructs a new sprite with the given dimensions 
	 * @param tex the spritesheet to use
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param w the width
	 * @param h the height
	 * @param frames the number of frames of animation
	 * @param period the number of ticks between each frame
	 */
	public Sprite(Texture tex, int x, int y, int w, int h, int frames, int period) {
		this.tex = tex;
		this.sx = x;
		this.sy = y;
		this.w = w;
		this.h = h;
		this.frames = frames;
		this.period = period;
	}
	
	/**
	 * Steps the animation cycle one tick forward
	 */
	public void tick() {
		if(playing) {
			if(++t == period) {
				t = 0;
				if(++f == frames)
					f = 0;
			}
		}
	}
	
	/**
	 * Draws the sprite at the given location
	 * @param g the graphics handle to use
	 * @param x the x-coordinate of the center, relative to the screen's height
	 * @param y the y-coordinate of the center, relative to the screen's height
	 */
	public void drawAt(Graphics g, float x, float y) {
		g.bindTexture(tex);
		int spritesNotOnRow = f - (tex.getImageWidth() - sx) / w + 1;
		if(spritesNotOnRow <= 0) {
			g.draw(x, y, sx + w * f, sy, w, h, 0);
		} else {
			int spritesPerRow = tex.getImageWidth() / w;
			g.draw(x, y, (spritesNotOnRow - 1) % spritesPerRow * w, sy + (spritesNotOnRow / spritesPerRow + 1) * h, w, h, 0);
		}
	}
	
	/**
	 * Gets the width of the sprite, relative to the screen's height
	 * @return The width of the sprite
	 */
	public float getWidth() {
		return Graphics.pixelsToUnits(w);
	}
	
	/**
	 * Gets the height of the sprite, relative to the screen's height
	 * @return The height of the sprite
	 */
	public float getHeight() {
		return Graphics.pixelsToUnits(h);
	}
	
	/**
	 * Sets the state of the animation
	 * @param b Whether the animation should play or not
	 */
	public void setPlaying(boolean b) {
		playing = b;
	}
	
	/**
	 * Jumps to the specified frame
	 * @param f The frame to jump to
	 */
	public void gotoFrame(int f) {
		if(f < frames && f >= 0) {
			this.f = f;
			t = 0;
		}
	}
	
}
