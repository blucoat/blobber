package com.pitchforkbunnies.blobber;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

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
			ContextAttribs attribs = new ContextAttribs(3, 2)
				.withProfileCore(true)
				.withForwardCompatible(true);
			
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setVSyncEnabled(VSYNC_IN_WINDOWED);
			Display.setTitle("Blobber 3: Revelations");
			Display.setResizable(true);
			Display.create(new PixelFormat(), attribs);
			
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Current OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		
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
			if(Display.wasResized())
				GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			
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
			currentScreen.renderLight(graphics);
			graphics.begin();
			currentScreen.render(graphics);
			graphics.end();
			
			frames++;
			Display.update();
			Display.sync(60);
			
			int error = GL11.glGetError();
			if(error != GL11.GL_NO_ERROR)
				System.out.println(GLU.gluErrorString(error));
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
