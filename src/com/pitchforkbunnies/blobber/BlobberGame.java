package com.pitchforkbunnies.blobber;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class BlobberGame {

	public BlobberGame() {
		initDisplay();
		TextureBundle bundle = new TextureBundle();
		Graphics g = new Graphics();
		float x = 2.0f/3, y = 0.5f;
		
		Sprite sprite = new Sprite(bundle.test, 0, 0, 
				bundle.test.getImageWidth(), bundle.test.getImageHeight());
		
		while(!Display.isCloseRequested()) {
			if(Display.wasResized())
				GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			
			GL11.glClearColor(1, 1, 1, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			g.begin();
			sprite.drawAt(g, x, y);
			g.end();
			
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) y -= .01;
			if(Keyboard.isKeyDown(Keyboard.KEY_A)) x -= .01;
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) y += .01;
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) x += .01;
			
			Display.update();	//will hang until vsync
		}
		g.release();
		g = null;
		bundle.release();
		bundle = null;
	}
	
	private void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setVSyncEnabled(true);
			Display.setResizable(true);
			Display.setTitle("Blobber 3: Revelations");
			Display.create();
		} catch (LWJGLException e) {			
			e.printStackTrace();
			System.exit(-1);
		}
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glViewport(0, 0, 800, 600);
	}
	
	public static void main(String[] args) {
		//Entry point
		new BlobberGame();
	}

}
