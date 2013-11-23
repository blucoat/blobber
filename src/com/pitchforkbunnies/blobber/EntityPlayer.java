package com.pitchforkbunnies.blobber;

import org.lwjgl.input.Keyboard;

public class EntityPlayer extends Entity {
	
	public EntityPlayer(Level level) {
		super(level);
		width = 30.0f / 32;
		height = 30.0f / 32;
		sprite = new Sprite(level.bundle.textures.sprites, 0, 0, 30, 30, 1, 1);
		canWallJump = true;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(onLeftWall) {
				dy -= 0.2;
			} else if(onRightWall) {
				dy -= 0.2;
			} else if(onGround) {
				dy -= 0.2;
			}
				
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) dx -= 0.05;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) dx += 0.05;
	}
}
