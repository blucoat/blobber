package com.pitchforkbunnies.blobber;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.MipMap;
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
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureID());
		
		byte[] data = tex.getTextureData();
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
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
