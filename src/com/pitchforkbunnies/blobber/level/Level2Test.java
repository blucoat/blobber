package com.pitchforkbunnies.blobber.level;

import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.ResourceBundle;

public class Level2Test extends Level {
	public Level2Test(ResourceBundle bundle) {
		super("com/pitchforkbunnies/blobber/res/level3.png", bundle);
	}
	
	@Override
	public void trigger(int x, int y) {
		next = new Level1Test(bundle);
	}
}
