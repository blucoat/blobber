package com.pitchforkbunnies.blobber.core;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;

/**
 * Holder for sound files
 * @author James
 *
 */
public class SoundBundle {
	public Audio jump = loadSound("com/pitchforkbunnies/blobber/res/jump.wav", "WAV");
	
	private Audio loadSound(String ref, String type) {
		InputStream in = getClass().getClassLoader().getResourceAsStream(ref);
		if(in == null)
			throw new RuntimeException("Could not find audio file: " + ref);
		try {
			return AudioLoader.getAudio(type, in);	//temporary
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Free up all teh sound files
	 */
	public void release() {
		AL.destroy();
	}
}
