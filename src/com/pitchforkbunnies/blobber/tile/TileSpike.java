package com.pitchforkbunnies.blobber.tile;

import com.pitchforkbunnies.blobber.core.Entity;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Tile;

public class TileSpike extends Tile {

	public TileSpike(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(true);
		setSprite(2, 1, 1);
	}
	
	@Override
	public void onWalk(Entity e) {
		e.die();
	}
	
}
