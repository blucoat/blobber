package com.pitchforkbunnies.blobber;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class BlobberGame {
	
	private static final boolean VSYNC_IN_WINDOWED = true;	//stops screen tearing when aero is disabled, causes stuttering when it is
	
	private Screen currentScreen;
	private Graphics graphics;
	private ResourceBundle bundle;
	
	public void start() {
		init();
		gameLoop();
		cleanup();
	}
	
	private void init() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setVSyncEnabled(VSYNC_IN_WINDOWED);
			Display.setTitle("Blobber 3: Revelations");
			Display.create();
			
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		} catch (LWJGLException e) {			
			e.printStackTrace();
			System.exit(-1);
		}
		
		graphics = new Graphics();
		bundle = new ResourceBundle();
		currentScreen = new GameScreen(new LevelTest(bundle), bundle);
	}
	
	private void gameLoop() {
		double secondsToDo = 0;
		double lastTime = (double) Sys.getTime() / Sys.getTimerResolution();
		double secondsPerFrame = 1.0 / 60;
		int ticks = 0, frames = 0;
		
		while(!Display.isCloseRequested()) {
			double now = (double) Sys.getTime() / Sys.getTimerResolution();
			secondsToDo += now - lastTime;
			lastTime = now;
			while(secondsToDo > secondsPerFrame) {
				secondsToDo -= secondsPerFrame;
				Screen next = currentScreen.tick();
				if(next != null)
					currentScreen = next;
				
				if(ticks++ % 60 == 0) {
					System.out.println(frames + "fps");
					frames = 0;
				}
			}
			
			graphics.clear();
			graphics.begin();
			currentScreen.render(graphics);
			graphics.end();
			
			frames++;
			Display.update();
			Display.sync(60);
		}
	}
	
	private void cleanup() {
		graphics.release();
		bundle.release();
		Display.destroy();
	}
	
	public static void main(String[] args) {
		BlobberGame game = new BlobberGame();
		game.start();
	}

}
