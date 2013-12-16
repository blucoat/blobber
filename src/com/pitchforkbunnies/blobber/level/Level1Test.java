package com.pitchforkbunnies.blobber.level;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.ResourceBundle;

public class Level1Test extends Level {
	public Level1Test(ResourceBundle bundle) {
		super("com/pitchforkbunnies/blobber/res/level4.png", bundle);
	}
	
	@Override
	public void trigger(int x, int y) {
		next = new Level2Test(bundle);
	}
}
