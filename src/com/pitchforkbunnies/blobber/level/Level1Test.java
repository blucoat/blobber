package com.pitchforkbunnies.blobber.level;

import com.pitchforkbunnies.blobber.core.Graphics;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.ResourceBundle;
import com.pitchforkbunnies.blobber.tile.TileDoor;

public class Level1Test extends Level {
	public Level1Test(ResourceBundle bundle) {
		super("com/pitchforkbunnies/blobber/res/level6.png", bundle);
		
	}
	
	@Override
	public void trigger(int x, int y) {
		if(x == 30 && y == 19) {
			next = new Level2Test(bundle);
		} else {
			((TileDoor) tiles[35][20]).setOpen(true);
			((TileDoor) tiles[36][20]).setOpen(true);
			((TileDoor) tiles[37][20]).setOpen(true);
			((TileDoor) tiles[38][20]).setOpen(true);
		}
			
	}

	@Override
	public void render(Graphics g){
		lightmap.setAttenuation(2f, .5f, 1);
		super.render(g);
		
	}

}
