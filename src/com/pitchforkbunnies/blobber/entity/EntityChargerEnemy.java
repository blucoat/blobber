package com.pitchforkbunnies.blobber.entity;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Sprite;

public class EntityChargerEnemy extends Enemy {

	private int chargeCooldown = 0, chargeToGo = 0;
	boolean chargingLeft;
	
	private Sprite left, right;
	
	public EntityChargerEnemy(Level level) {
		super(level);
		width = 30.0f / 32;
		height = 30.0f / 32;
		left = new Sprite(level.bundle.textures.sprites, 32, 32, 30, 30, 1, 1);
		right = new Sprite(level.bundle.textures.sprites, 64, 32, 30, 30, 1, 1);
		sprite = left;
		canFreeFall = false;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(chargeToGo > 0) {
			dx += chargingLeft ? -0.2 : 0.2;
			chargeToGo--;
		} else if(chargeCooldown > 0) {
			chargeCooldown--;
		} else if(playerWithinRange) {
			chargeToGo = 10;
			chargeCooldown = 200;
			chargingLeft = level.player.x < x;
			sprite = chargingLeft ? left : right;
		}
	}
}
