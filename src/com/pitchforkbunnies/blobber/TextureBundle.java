package com.pitchforkbunnies.blobber;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Holds all the textures in one class
 * @author James
 *
 */
public class TextureBundle {

	public Texture test = loadTexture("test.png");
	
	private List<Texture> textures = new ArrayList<Texture>();
	
	private Texture loadTexture(String ref) {
		InputStream in = TextureBundle.class.getClassLoader().getResourceAsStream("com/pitchforkbunnies/blobber/res/" + ref);
		if(in == null)
			throw new RuntimeException("Could not load file: " + ref);
		Texture tex = null;
		try {
			tex = TextureLoader.getTexture("PNG", in);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return tex;
	}
	
	/**
	 * Delete everything after we're done.
	 */
	public void release() {
		for(Texture tex : textures)
			tex.release();
		textures.clear();
	}
}
