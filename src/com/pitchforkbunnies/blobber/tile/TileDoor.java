package com.pitchforkbunnies.blobber.tile;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.Tile;

public class TileDoor extends Tile {
	
	private boolean open = false;
	
	public TileDoor(Level level, int x, int y) {
		super(level, x, y);
		setOpen(false);
	}
	
	public void setOpen(boolean open) {
		this.open = open;
		walkable = open;
		setSprite(open ? 3 : 0, 1, 1);
	}
	
	public boolean isOpen() {
		return open;
	}
}
