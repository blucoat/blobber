package com.pitchforkbunnies.blobber;

public class Entity {
	public float x, y, width, height, dx, dy;
	public Level level;
	public Sprite sprite;
	public boolean isDead = false;
	public boolean onGround = false;
	
	public Entity(Level level) {
		this.level = level;
	}
	
	/**
	 * Draws the entity to the screen in the specified position
	 * @param g the graphics handle to use for drawing 
	 * @param x the left edge of the entity
	 * @param y the top edge of the entity
	 */
	public void render(Graphics g, float x, float y) {
		sprite.drawAt(g, x + sprite.getWidth() * .5f, y + sprite.getHeight() * .5f);
	}
	
	/**
	 * Advances the entity one step forward in time.
	 */
	public void tick() {
		sprite.tick();
		
		int xsteps = (int) Math.abs(dx * 100);
		float xx = dx / xsteps;
		for(int i = 0; i < xsteps; i++) {
			if(!level.collides(this, x + xx, y)) {
				x += xx;
			} else {
				dx = 0;
				break;
			}
		}
		
		onGround = false;
		
		int ysteps = (int) Math.abs(dy * 100);
		float yy = dy / ysteps;
		for(int i = 0; i < ysteps; i++) {
			if(!level.collides(this, x, y + yy)) {
				y += yy;
			} else {
				if(dy > 0)
					onGround = true;
				dy = 0;
				break;
			}
		}
		
		dx *= 0.25f;
	}
}
