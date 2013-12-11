package com.pitchforkbunnies.blobber.entity;

import com.pitchforkbunnies.blobber.core.Entity;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Sprite;

public class EntityBasicEnemy extends Entity {
	private int timeToJump = 200;
	private boolean jumpingLeft = false, jumping = false;
	
	public EntityBasicEnemy(Level level) {
		super(level);
		width = 30.0f / 32;
		height = 30.0f / 32;
		sprite = new Sprite(level.bundle.textures.sprites, 32, 32, 30, 30, 1, 1);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(onGround) {
			jumping = false;
			
			if(--timeToJump == 0) {
				timeToJump = 200;
				dy -= 0.2;
				jumping = true;
				jumpingLeft = level.player.x < x;
			}
		}
		
		if(jumping) {
			if(jumpingLeft) {
				dx -= 0.05;
			} else {
				dx += 0.05;
			}
		}
	}
}
