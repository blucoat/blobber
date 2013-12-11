package com.pitchforkbunnies.blobber;

import org.lwjgl.input.Keyboard;

public class EntityPlayer extends Entity {
	
	private int jumpTimer = 0;
	private int leftWallTimer = 0;
	private int rightWallTimer = 0;
	
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
		
		if(leftWallTimer > 0) leftWallTimer--;
		if(rightWallTimer > 0) rightWallTimer--;
		
		if(onLeftWall)
			leftWallTimer = 5;
		if(onRightWall) 
			rightWallTimer = 5;
		
		if(InputManager.keyPressed(Keyboard.KEY_SPACE)) {
			if(leftWallTimer > 0) {
				leftWallTimer = 0;
				dy -= 0.2;
				dx += 0.1;
				jumpTimer = 15;
				level.bundle.sounds.jump.playAsSoundEffect(1, 1, false);
			} else if(rightWallTimer > 0) {
				rightWallTimer = 0;
				dy -= 0.2;
				dx -= 0.1;
				jumpTimer = 15;
				level.bundle.sounds.jump.playAsSoundEffect(1, 1, false);
			} else if(onGround) {
				dy -= 0.2;
				level.bundle.sounds.jump.playAsSoundEffect(1, 1, false);
			}
				
		}	
		
		if(jumpTimer == 0) {
			dx *= 0.25f;
			if(InputManager.keyDown(Keyboard.KEY_A)) dx -= 0.05;
			if(InputManager.keyDown(Keyboard.KEY_D)) dx += 0.05;
		} else {
			jumpTimer--;
			dx *= 0.9f;
			if(InputManager.keyDown(Keyboard.KEY_A)) dx -= 0.01;
			if(InputManager.keyDown(Keyboard.KEY_D)) dx += 0.01;
		}
	}
	
	@Override
	public void die() {
		x = level.spawnx;
		y = level.spawny;
		dx = 0;
		dy = 0;
	}
}
