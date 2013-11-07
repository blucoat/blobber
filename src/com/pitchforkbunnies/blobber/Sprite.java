package com.pitchforkbunnies.blobber;

import org.newdawn.slick.opengl.Texture;

public class Sprite {
	private Texture tex;
	private int sx, sy, w, h;
	
	/**
	 * Constructs a new sprite with the given dimensions 
	 * @param tex the spritesheet to use
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param w the width
	 * @param h the height
	 */
	public Sprite(Texture tex, int x, int y, int w, int h) {
		this.tex = tex;
		this.sx = x;
		this.sy = y;
		this.w = w;
		this.h = h;
	}
	
	public void drawAt(Graphics g, float x, float y) {
		g.bindTexture(tex);
		g.draw(x, y, sx, sy, w, h, 0);
	}
	
	public float getWidth() {
		return w;
	}
	
	public float getHeight() {
		return h;
	}
	
}
