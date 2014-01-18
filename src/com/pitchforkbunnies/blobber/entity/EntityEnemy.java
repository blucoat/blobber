package com.pitchforkbunnies.blobber.entity;

import com.pitchforkbunnies.blobber.core.Entity;
import com.pitchforkbunnies.blobber.core.Level;

public class EntityEnemy extends EntityLiving {
	
	public boolean playerWithinRange = false;
	public float range = 30.0f;
	
	public EntityEnemy(Level level) {
		super(level);
	}
	
	@Override
	public void tick() {
		super.tick();
		playerWithinRange = Math.abs(level.player.x - x) + Math.abs(level.player.y - y) < range;
	}
	
	@Override
	public void onCollide(Entity e) {
		if(e instanceof EntityPlayer)
			e.die();
	}

}
