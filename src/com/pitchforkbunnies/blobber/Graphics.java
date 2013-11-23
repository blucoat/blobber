package com.pitchforkbunnies.blobber;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;


public class Graphics {
	
	private boolean lightingEnabled = false;
	
	private int vaoID, vboID, indexID, p1ID, p2ID, vsID, fs1ID, fs2ID, sizeID, offsetID, transformID, textureID, lightmapID;
	private FloatBuffer transformBuffer = BufferUtils.createFloatBuffer(16);
	private Matrix4f transform = new Matrix4f();
	private Texture currentTexture = null;
	
	private static final int TARGET_HEIGHT = 300; 
	
	/**
	 * Converts from pixels to screen units
	 * @param pixels length in pixels
	 * @return the length relative to the screen height
	 */
	public static float pixelsToUnits(int pixels) {
		return (float) pixels / TARGET_HEIGHT;
	}
	
	public Graphics() {
		initShaders();
		initBuffers();
		
		glEnable(GL_BLEND);
	}
	
	public void setLightmap(LightMap map) {
		if(map == null) {
			lightingEnabled = false;
		} else { 
			lightingEnabled = true;
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, map.getFrameBuffer());
		}
	}
	
	private void initBuffers() {
		float[] vertices = {
			-1, 1, 0, 1,
			-1, -1, 0, 1,
			1, -1, 0, 1,
			1, 1, 0, 1
		};
			
		float[] st = {
			0, 0,
			0, 1,
			1, 1,
			1, 0
		};
			
		FloatBuffer fb = BufferUtils.createFloatBuffer(24);
		for(int i = 0; i < 4; i++) {
			fb.put(vertices, i * 4, 4);
			fb.put(st, i * 2, 2);
		}
		fb.flip();
			
		byte[] indices = {
			0, 1, 2,
			0, 2, 3
		};
			
		ByteBuffer bb = BufferUtils.createByteBuffer(6);
		bb.put(indices);
		bb.flip();
			
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
			
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
			
		glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 24, 0);	//vertex data
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 24, 16);	//st coords
			
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
			
		indexID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bb, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void initShaders() {
		vsID = loadShader("vertex.glsl", GL_VERTEX_SHADER);
		fs1ID = loadShader("fragment1.glsl", GL_FRAGMENT_SHADER);
		fs2ID = loadShader("fragment2.glsl", GL_FRAGMENT_SHADER);
		
		p1ID = glCreateProgram();
		glAttachShader(p1ID, vsID);
		glAttachShader(p1ID, fs1ID);
		
		p2ID = glCreateProgram();
		glAttachShader(p2ID, vsID);
		glAttachShader(p2ID, fs2ID);
		
		glBindAttribLocation(p2ID, 0, "in_Position");
		glBindAttribLocation(p2ID, 1, "in_ST");
		
		glLinkProgram(p1ID);
		glValidateProgram(p1ID);
		
		transformID = glGetUniformLocation(p1ID, "transform");
		offsetID = glGetUniformLocation(p1ID, "offset");
		sizeID = glGetUniformLocation(p1ID, "size");
		
		glLinkProgram(p2ID);
		glValidateProgram(p2ID);
		
		transformID = glGetUniformLocation(p2ID, "transform");
		offsetID = glGetUniformLocation(p2ID, "offset");
		sizeID = glGetUniformLocation(p2ID, "size");
		
		ARBShaderObjects.glUseProgramObjectARB(p1ID);
		textureID = ARBShaderObjects.glGetUniformLocationARB(p1ID, "texture_diffuse");
		ARBShaderObjects.glUniform1iARB(textureID, 0);
		lightmapID = ARBShaderObjects.glGetUniformLocationARB(p1ID, "lightmap");
		ARBShaderObjects.glUniform1iARB(lightmapID, 1);
	}
	
	public static int loadShader(String ref, int type) {
		InputStream in = Graphics.class.getClassLoader().getResourceAsStream("com/pitchforkbunnies/blobber/shaders/" + ref);
		if(in == null)
			throw new RuntimeException("Could not load file: " + ref);
		
		StringBuilder str = new StringBuilder();
		String line;
		
		//fancy new try-with-resources as of Java 1.7
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			while((line = reader.readLine()) != null)
				str.append(line).append("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		int id = glCreateShader(type);
		glShaderSource(id, str);
		glCompileShader(id);
		return id;
	}
	
	public void begin() {
		glUseProgram(lightingEnabled ? p1ID : p2ID);
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void end() {
		glUseProgram(0);
		
		glBindVertexArray(0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		currentTexture = null;
	}
	
	public void bindTexture(Texture tex) {
		if(currentTexture != tex) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, tex.getTextureID());
			currentTexture = tex;
		}
	}
	
	/**
	 * Draws a subset of the texture currently bound to the screen.
	 * @param dx destination x
	 * @param dy destination y
	 * @param sx source x
	 * @param sy source y
	 * @param w width of subsection
	 * @param h height of subsection
	 */
	public void draw(float dx, float dy, int sx, int sy, int w, int h, float rot) {
		if(currentTexture == null) {
			//Politely remind the programmer that he is retarded
			System.out.println("You dunderface the texture is null!");
			return;
		}
		
		int tw = currentTexture.getImageWidth();
		int th = currentTexture.getImageHeight();
		
		glUniform2f(offsetID, (float) sx / tw, (float) sy / th);
		glUniform2f(sizeID, (float) w / tw, (float) h / th);
		
		int sw = Display.getWidth();
		int sh = Display.getHeight();
		float scale = (float) sh / TARGET_HEIGHT;
		
		
		Matrix4f.setIdentity(transform);
		Matrix4f.translate(new Vector2f(dx * 2.0f * sh / sw - 1, dy * -2.0f + 1), transform, transform);
		Matrix4f.scale(new Vector3f(w * scale / sw, h * scale / sh, 1), transform, transform);
		Matrix4f.rotate(rot * (float) Math.PI / 180, new Vector3f(0, 0, 1), transform, transform);
		transform.store(transformBuffer);
		transformBuffer.flip();
		glUniformMatrix4(transformID, true, transformBuffer);
		
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
	}
	
	/**
	 * Clears the screen to the default background color.
	 */
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Gets the height of the screen, in screen heights (always 1)
	 * @return the height of the screen
	 */
	public static float getHeight() {
		return 1;
	}
	
	/**
	 * Gets the width of the screen, in screen heights (the aspect ratio)
	 * @return the width of the screen
	 */
	public static float getWidth() {
		return (float) Display.getWidth() / Display.getHeight();
	}
	
	public void release() {
		glUseProgram(0);
		
		glDetachShader(p1ID, vsID);
		glDetachShader(p1ID, fs1ID);
		
		glDetachShader(p2ID, vsID);
		glDetachShader(p2ID, fs2ID);
		
		glDeleteProgram(p1ID);
		glDeleteShader(vsID);
		glDeleteShader(fs1ID);
		glDeleteShader(fs2ID);
		
		glDeleteBuffers(vboID);
		glDeleteBuffers(indexID);
		glDeleteVertexArrays(vaoID);
	}
}
