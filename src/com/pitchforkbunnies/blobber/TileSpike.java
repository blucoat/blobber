package com.pitchforkbunnies.blobber;

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
