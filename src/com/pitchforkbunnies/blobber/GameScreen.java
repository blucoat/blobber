package com.pitchforkbunnies.blobber;

public class GameScreen extends Screen {

	private Level level;
	private LightMap lightmap;
	
	public GameScreen(Level level, ResourceBundle bundle) {
		super(bundle);
		this.level = level;
		lightmap = new LightMap(level);
		lightmap.setAmbientLight(.05f, .05f, .1f);
		lightmap.setAttenuation(1, 0, 1);
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
		lightmap.renderLight(level.player.x + level.player.width / 2, level.player.y + level.player.height / 2, 1f, 0.2f, 0);
		lightmap.renderLight(16, 16, 0.5f, 0.5f, 1.0f);
		lightmap.end();
		
		g.setLightmap(lightmap);
	}
	
	@Override
	public void render(Graphics g) {
		level.render(g);
		//Draw UI here
	}

}
