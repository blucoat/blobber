package com.pitchforkbunnies.blobber;

public class TileEnd extends Tile {

	public TileEnd(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(true);
		setSprite(1, 1, 1);
	}
	
	@Override
	public void onWalk(Entity e) {
		if(e == level.player)
			level.win();
	}

}
