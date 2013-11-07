package com.pitchforkbunnies.blobber;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class BlobberGame {

	public BlobberGame() {
		initDisplay();
		TextureBundle bundle = new TextureBundle();
		Graphics g = new Graphics();
		Texture tex = bundle.test;
		int rot = 0, x = 400, y = 300;
		while(!Display.isCloseRequested()) {
			GL11.glClearColor(1, 1, 1, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			g.begin();
			g.bindTexture(tex);
			g.draw(x, y, 0, 0, tex.getImageWidth(), tex.getImageWidth(), rot);
			g.end();
			
			rot += 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) y -= 5;
			if(Keyboard.isKeyDown(Keyboard.KEY_A)) x -= 5;
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) y += 5;
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) x += 5;
			
			Display.update();
			Display.sync(60);
		}
		g.release();
		g = null;
		bundle.release();
		bundle = null;
	}
	
	private void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
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
