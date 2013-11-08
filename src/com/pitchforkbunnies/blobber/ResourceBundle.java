package com.pitchforkbunnies.blobber;

public class ResourceBundle {
	public TextureBundle textures;
	
	/**
	 * Constructs a new resource bundle, loading textures and sounds (not yet)
	 */
	public ResourceBundle() {
		textures = new TextureBundle();
	}
	
	/**
	 * Releases the resources loaded
	 */
	public void release() {
		textures.release();
	}
}
