package com.pitchforkbunnies.blobber.core;

public class ResourceBundle {
	public TextureBundle textures;
	public SoundBundle sounds;
	
	/**
	 * Constructs a new resource bundle, loading textures and sounds (not yet)
	 */
	public ResourceBundle() {
		textures = new TextureBundle();
		sounds = new SoundBundle();
	}
	
	/**
	 * Releases the resources loaded
	 */
	public void release() {
		textures.release();
		sounds.release();
	}
}
