package com.pitchforkbunnies.blobber.tile;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Tile;

public class TileWall extends Tile {

	public TileWall(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(false);
		setSprite(Math.random() < 0.5 ? 0 : 1, 1, 1);
	}

}
