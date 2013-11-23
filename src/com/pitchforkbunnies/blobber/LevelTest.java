package com.pitchforkbunnies.blobber;

public class LevelTest extends Level {
	public LevelTest(ResourceBundle bundle) {
		super("com/pitchforkbunnies/blobber/res/level.png", bundle);
		player.x = 10;
		player.y = 10;
		EntityBasicEnemy enemy = new EntityBasicEnemy(this);
		enemy.x = 12;
		enemy.y = 10;
		entities.add(enemy);
	}
}
