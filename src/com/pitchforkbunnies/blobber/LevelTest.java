package com.pitchforkbunnies.blobber;

public class LevelTest extends Level {
	public LevelTest(ResourceBundle bundle) {
		super("com/pitchforkbunnies/blobber/res/level.png", bundle);
		player.x = 10;
		player.y = 10;
	}
}
