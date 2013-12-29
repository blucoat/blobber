package com.pitchforkbunnies.blobber.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

/**
 * Class with static methods to poll input from the keyboard and (eventually) mouse
 * @author James
 *
 */
public class InputManager {
	private static boolean[] keys = new boolean[Keyboard.KEYBOARD_SIZE];
	
	/**
	 * Initialize the keyboard handle
	 * @throws LWJGLException if the keyboard cannot be initialized
	 */
	public static void init() throws LWJGLException {
		Keyboard.create();
		Keyboard.enableRepeatEvents(false);
	}
	
	/**
	 * Updates the state of the keyboard, should be called every frame
	 */
	public static void update() {
		for(int i = 0; i < keys.length; i++)
			keys[i] = false;
		
		Keyboard.poll();
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState())
				keys[Keyboard.getEventKey()] = true;
		}
	}
	
	/**
	 * Check if the given key was pressed this frame
	 * @param keyCode
	 * @return
	 */
	public static boolean keyPressed(int keyCode) {
		return keys[keyCode];
	}
	
	/**
	 * Check if the given key is currently being held
	 * @param keyCode
	 * @return
	 */
	public static boolean keyDown(int keyCode) {
		return Keyboard.isKeyDown(keyCode);
	}
	
	/**
	 * Completely obliterate the user's keyboard, mouse and other peripherals (unless it's a trackball, those are cool)
	 */
	public static void destroy() {
		Keyboard.destroy();
	}
}
