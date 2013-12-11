package com.pitchforkbunnies.blobber.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

public class InputManager {
	private static boolean[] keys = new boolean[Keyboard.KEYBOARD_SIZE];
	
	public static void init() throws LWJGLException {
		Keyboard.create();
		Keyboard.enableRepeatEvents(false);
	}
	
	public static void update() {
		for(int i = 0; i < keys.length; i++)
			keys[i] = false;
		
		Keyboard.poll();
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState())
				keys[Keyboard.getEventKey()] = true;
		}
	}
	
	public static boolean keyPressed(int keyCode) {
		return keys[keyCode];
	}
	
	public static boolean keyDown(int keyCode) {
		return Keyboard.isKeyDown(keyCode);
	}
	
	public static void destroy() {
		Keyboard.destroy();
	}
}
