package com.pitchforkbunnies.blobber;


public class GameScreen extends Screen {

	private Level level;
	private LightMap lightmap;
	
	public GameScreen(Level level, ResourceBundle bundle) {
		super(bundle);
		this.level = level;
		lightmap = new LightMap(level);
	}
	
	@Override
	public Screen tick() {
		level.tick();
		return null;
	}

	@Override
	public void renderLight(Graphics g) {
		//Do this before any rendering
		level.fixCamera(g);
		
		lightmap.begin();
		lightmap.renderLight(level.player.x + level.player.width / 2, level.player.y + level.player.height / 2, 1.0f, 0.5f, 0.0f, 1.0f);
		lightmap.renderLight(15.5f, 15.5f, 0.5f, 0.5f, 1.0f, 1.0f);
		//lightmap.drawTriangle(11, 17, 15, 17, 11, 21);
		lightmap.end();
		g.setLightmap(lightmap);
	}
	
	@Override
	public void render(Graphics g) {
		level.render(g);
		//Draw UI here
	}

}
