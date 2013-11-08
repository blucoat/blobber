package com.pitchforkbunnies.blobber;

public class GameScreen extends Screen {

	private Level level;
	
	public GameScreen(ResourceBundle bundle, Level level) {
		super(bundle);
		this.level = level;
	}
	
	@Override
	public Screen tick() {
		level.tick();
		return null;
	}

	@Override
	public void render(Graphics g) {
		level.render(g);
		//Draw UI here
	}

}
