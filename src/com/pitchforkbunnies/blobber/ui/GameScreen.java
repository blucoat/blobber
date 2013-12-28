package com.pitchforkbunnies.blobber.ui;

import com.pitchforkbunnies.blobber.core.Graphics;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.LightMap;
import com.pitchforkbunnies.blobber.core.LightSource;
import com.pitchforkbunnies.blobber.core.ResourceBundle;
import com.pitchforkbunnies.blobber.core.Screen;

public class GameScreen extends Screen {

	private Level level;
	private LightMap lightmap;
	private int t = 0;
	
	public GameScreen(Level level, ResourceBundle bundle) {
		super(bundle);
		loadLevel(level);
	}
	
	public void loadLevel(Level newLevel) {
		lightmap = new LightMap(newLevel);
		newLevel.lightmap = lightmap;
		level = newLevel;
	}
	
	@Override
	public Screen tick() {
		level.tick();
		if(level.next != null) {
			loadLevel(level.next);
		}
		return null;
	}

	@Override
	public void renderLight(Graphics g) {
		//Do this before any rendering
		level.fixCamera(g);
		
		if(++t == 180)
			t = 1;
		
		lightmap.begin();
		lightmap.renderSun(60, 1, .5f, 0);
		for(LightSource ls : level.lights) {
			lightmap.renderLight(ls.x, ls.y, ls.r, ls.g, ls.b);
		}
		lightmap.end();
		
		g.setLightmap(lightmap);
	}
	
	@Override
	public void render(Graphics g) {
		level.render(g);
		//Draw UI here
	}

}
