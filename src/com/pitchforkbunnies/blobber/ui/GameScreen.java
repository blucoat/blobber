package com.pitchforkbunnies.blobber.ui;

import org.lwjgl.opengl.Display;

import com.pitchforkbunnies.blobber.core.Graphics;
import com.pitchforkbunnies.blobber.core.Level;
import com.pitchforkbunnies.blobber.core.ResourceBundle;
import com.pitchforkbunnies.blobber.core.Screen;

public class GameScreen extends Screen {

	private Level level;
	
	public GameScreen(Level level, ResourceBundle bundle) {
		super(bundle);
		loadLevel(level);
	}
	
	public void loadLevel(Level newLevel) {
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
		if(Display.wasResized()) {
			level.lightmap.rebuild();
		}
		
		//Do this before any rendering
		level.fixCamera(g);
		
		level.lightmap.render();
		g.setLightmap(level.lightmap);
	}
	
	@Override
	public void render(Graphics g) {
		level.render(g);
		//Draw UI here
	}

}
