package com.pitchforkbunnies.blobber.tile;

import com.pitchforkbunnies.blobber.core.Entity;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Tile;

public class TileTrigger extends Tile {

	public TileTrigger(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(true);
		setSprite(3, 1, 1);
	}
	
	@Override
	public void onWalk(Entity e) {
		if(e == level.player)
			level.trigger(x, y);
	}

}
