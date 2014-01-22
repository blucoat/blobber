package com.pitchforkbunnies.blobber.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

/**
 * Class that "manages" lighting logic
 * @author James
 *
 */
public class LightMap {
	private FloatBuffer buffer;
	
	private int vsID, fsID, pID, vaoID, vboID, frameID, textureID, indexID;
	
	public static final int SUBTILES = 2;
	
	private int width, height;
	
	private Level level;
	private LightTile[][] lightlevels;
	
	/**
	 * Constructs a new lightmap around the given level.
	 * @param level The level to base the lighting on
	 */
	public LightMap(Level level) {
		setLevel(level);
		initFrameBuffer();
		initShaders();
		initBuffers();
	}
	
	/**
	 * Resizes the lightmap to match the screen
	 */
	public void rebuild() {
		release();
		initFrameBuffer();
		initShaders();
		initBuffers();
	}
	
	private void initFrameBuffer() {
		frameID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameID);
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, Display.getWidth(), Display.getHeight(), 0, GL_RGBA, GL_UNSIGNED_INT, (java.nio.ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);
		
		//glViewport(0, 0, Display.getWidth(), Display.getHeight());
		//glClear(GL_COLOR_BUFFER_BIT);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	private void initShaders() {
		vsID = Graphics.loadShader("lightingVS.glsl", GL_VERTEX_SHADER);
		fsID = Graphics.loadShader("lightingFS.glsl", GL_FRAGMENT_SHADER);

		pID = glCreateProgram();
		glAttachShader(pID, vsID);
		glAttachShader(pID, fsID);
				
		glBindAttribLocation(pID, 0, "in_Position");
		glBindAttribLocation(pID, 1, "in_Color");
		
		glLinkProgram(pID);
		glValidateProgram(pID);
	}
	
	private void initBuffers() {
		width = (int) (Graphics.getWidth() / Tile.TILE_WIDTH_H * SUBTILES) + 1;
		height = (int) (Graphics.getHeight() / Tile.TILE_WIDTH_H * SUBTILES) + 1;
		
		//set default values
		float[] values = new float[width * height * 32];
		buffer = BufferUtils.createFloatBuffer(width * height * 32);
		buffer.put(values);
		buffer.flip();
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 32, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 32, 16);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindVertexArray(0);
		
		byte[] indices = {
				0, 1, 2,
				2, 1, 3
		};
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(width * height * 6);
		
		for(int i = 0; i < width * height; ++i) {
			for(int j = 0; j < 6; ++j)	
				indexBuffer.put(indices[j] + i * 4);
		}
		
		indexBuffer.flip();
		
		indexID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Sets the lightmap to be based on the given level 
	 * @param level The level to calculate light around
	 */
	public void setLevel(Level level) {
		this.level = level;
		lightlevels = new LightTile[level.width * SUBTILES][level.height * SUBTILES];
		
		for(int i = 0; i < lightlevels.length; ++i) {
			for(int j = 0; j < lightlevels[0].length; ++j) {
				lightlevels[i][j] = new LightTile();
			}
		}
	}
	
	/**
	 * Gets the texture containing the lightmap
	 * @return the OpenGL pointer to the texture, NOT the actual framebuffer
	 */
	public int getFrameBuffer() {
		return textureID;
	}
	
	private class LightTile {
		public float r = 0, g = 0, b = 0;
		public boolean isSource = false;
	}
	
	/**
	 * Lights given tile up, setting it as a source of light 
	 * @param x
	 * @param y
	 * @param r
	 * @param g
	 * @param b
	 */
	public void lightTile(int x, int y, int r, int g, int b) {
		x /= 2;
		y /= 2;
		LightTile tile = lightlevels[x][y];
		
		tile.isSource = true;
		tile.r = Math.max(tile.r, r);
		tile.g = Math.max(tile.g, g);
		tile.b = Math.max(tile.b, b);
		
		updateTile(x + 1, y);
		updateTile(x - 1, y);
		updateTile(x, y + 1);
		updateTile(x, y - 1);
	}
	
	/**
	 * Removes any light source at the given position and updates the lightmap
	 * @param x
	 * @param y
	 */
	public void removeLights(int x, int y) {
		lightlevels[x][y].isSource = false;
		updateTile(x, y);
	}
	
	private void updateTile(int x, int y) {
		if(x < 0 || y < 0 || x >= lightlevels.length || y >= lightlevels[0].length)
			return;
		
		LightTile tile = lightlevels[x][y];
		float maxr = 0, maxg = 0, maxb = 0;
		float opacity = level.tiles[x / SUBTILES][y / SUBTILES].opacity;
		System.out.println(opacity);
		
		if(tile.isSource) {
			maxr = tile.r;
			maxg = tile.g;
			maxb = tile.b;
		}
		
		if(x < lightlevels.length - 1) {
				maxr = Math.max(maxr, lightlevels[x + 1][y].r - opacity);
				maxg = Math.max(maxg, lightlevels[x + 1][y].g - opacity);
				maxb = Math.max(maxb, lightlevels[x + 1][y].b - opacity);
		}
		
		if(x > 0) {
			maxr = Math.max(maxr, lightlevels[x - 1][y].r - opacity);
			maxg = Math.max(maxg, lightlevels[x - 1][y].g - opacity);
			maxb = Math.max(maxb, lightlevels[x - 1][y].b - opacity);
		}
		
		if(y < lightlevels[0].length - 1) {
			maxr = Math.max(maxr, lightlevels[x][y + 1].r - opacity);
			maxg = Math.max(maxg, lightlevels[x][y + 1].g - opacity);
			maxb = Math.max(maxb, lightlevels[x][y + 1].b - opacity);				
		}
		
		if(y > 0) {
			maxr = Math.max(maxr, lightlevels[x][y - 1].r - opacity);
			maxg = Math.max(maxg, lightlevels[x][y - 1].g - opacity);
			maxb = Math.max(maxb, lightlevels[x][y - 1].b - opacity);				
		}
		
		if(maxr != tile.r || maxg != tile.g || maxb != tile.b) {
			tile.r = maxr;
			tile.g = maxg;
			tile.b = maxb;
			
			updateTile(x + 1, y);
			updateTile(x - 1, y);
			updateTile(x, y + 1);
			updateTile(x, y - 1);
		}
	}
	
	/**
	 * Renders the lightmap to a framebuffer
	 */
	public void render() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameID);
		
		glUseProgram(pID);
		
		glDisable(GL_BLEND);
		
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT);
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		
		int xa = (int) (level.xo * SUBTILES);
		int xb = (int) ((level.xo + Graphics.getWidth() / Tile.TILE_WIDTH_H) * SUBTILES) + 1;
		
		int ya = (int) (level.yo * SUBTILES);
		int yb = (int) ((level.yo + Graphics.getHeight() / Tile.TILE_WIDTH_H) * SUBTILES) + 1;
		
		//stop any weird corner-case bugs
		xa = Math.max(0, xa);
		xb = Math.min(level.width * SUBTILES, xb);
		ya = Math.max(0, ya);
		yb = Math.min(level.height * SUBTILES, yb);
		
		buffer.clear();
		
		for(int i = xa; i < xb; ++i) {
			for(int j = ya; j < yb; ++j) {
				
				//System.out.println(i + ", " + j + ", " + lightlevels[i][j].r);
				
				int ix1 = Math.round((i / (float) SUBTILES - level.xo) * Tile.TILE_WIDTH_H * Display.getHeight());
				int iy1 = Math.round((j / (float) SUBTILES - level.yo) * Tile.TILE_WIDTH_H * Display.getHeight());
				int ix2 = Math.round(((i + 1) / (float) SUBTILES - level.xo) * Tile.TILE_WIDTH_H * Display.getHeight());
				int iy2 = Math.round(((j + 1) / (float) SUBTILES - level.yo) * Tile.TILE_WIDTH_H * Display.getHeight());
				
				float x1 = (float) ix1 / Display.getWidth() * 2 - 1;
				float y2 = (float) iy1 / Display.getHeight() * -2 + 1;
				float x2 = (float) ix2 / Display.getWidth() * 2 - 1;
				float y1 = (float) iy2 / Display.getHeight() * -2 + 1;
				
				LightTile tile = lightlevels[i][j];
				
				float[] data = {
						x1, y1, 0, 1, tile.r, tile.g, tile.b, 1,
						x1, y2, 0, 1, tile.r, tile.g, tile.b, 1,
						x2, y1, 0, 1, tile.r, tile.g, tile.b, 1,
						x2, y2, 0, 1, tile.r, tile.g, tile.b, 1
				};
				
				buffer.put(data);
			}
		}
		
		buffer.flip();
		
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glDrawElements(GL_TRIANGLES, width * height * 6, GL_UNSIGNED_INT, 0);
		
		glBindVertexArray(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glUseProgram(0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Be free, my child
	 */
	public void release() {
		glDeleteFramebuffers(frameID);
		glDeleteTextures(textureID);
		
		glUseProgram(0);
		glDetachShader(pID, vsID);
		glDetachShader(pID, fsID);
		glDeleteProgram(pID);
		
		glDeleteBuffers(vboID);
		glDeleteVertexArrays(vaoID);
	}
	
}