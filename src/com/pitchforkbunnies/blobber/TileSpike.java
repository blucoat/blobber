package com.pitchforkbunnies.blobber;

public class TileSpike extends Tile {

	public TileSpike(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(true);
		setSprite(1, 1, 1);
	}
	
	@Override
	public void onWalk(Entity e) {
		System.out.println("OW");
	}
	
}
