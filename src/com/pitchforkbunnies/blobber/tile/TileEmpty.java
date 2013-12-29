package com.pitchforkbunnies.blobber.tile;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Tile;

public class TileEmpty extends Tile {

	public TileEmpty(Level level, int x, int y) {
		super(level, x, y);
		setWalkable(true);
		setSprite(3, 1, 1);
	}

}
