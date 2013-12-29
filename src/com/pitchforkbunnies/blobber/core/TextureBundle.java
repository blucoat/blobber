package com.pitchforkbunnies.blobber.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Holds all the textures in one class
 * @author James
 *
 */
public class TextureBundle {

	private List<Texture> textures = new ArrayList<Texture>();
	
	public Texture test = loadTexture("test.png");
	public Texture numbers = loadTexture("numbers.png");
	public Texture sprites = loadTexture("sprites.png");
	public Texture tiles = loadTexture("tiles.png");
	
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
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureID());
		
		byte[] data = tex.getTextureData();
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		//Set mipmapping to use linear filtering, set this to nearest if we want pixel-style gramphics
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		textures.add(tex);
		
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
