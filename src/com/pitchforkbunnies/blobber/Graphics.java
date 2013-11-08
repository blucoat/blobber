package com.pitchforkbunnies.blobber;

import static org.lwjgl.opengl.GL11.*;
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
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Graphics {
	
	private int vaoID, vboID, indexID, pID, vsID, fsID, sizeID, offsetID, transformID;
	private FloatBuffer transformBuffer = BufferUtils.createFloatBuffer(16);
	private Matrix4f transform = new Matrix4f();
	private Texture currentTexture = null;
	
	private static final int TARGET_HEIGHT = 100; 
	
	public Graphics() {
		initShaders();
		initBuffers();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
		pID = glCreateProgram();
		glAttachShader(pID, loadShader("vertex.glsl", GL_VERTEX_SHADER));
		glAttachShader(pID, loadShader("fragment.glsl", GL_FRAGMENT_SHADER));
		
		glBindAttribLocation(pID, 0, "in_Position");
		glBindAttribLocation(pID, 1, "in_ST");
		
		glLinkProgram(pID);
		glValidateProgram(pID);
		
		transformID = glGetUniformLocation(pID, "transform");
		offsetID = glGetUniformLocation(pID, "offset");
		sizeID = glGetUniformLocation(pID, "size");
	}
	
	private int loadShader(String ref, int type) {
		InputStream in = getClass().getClassLoader().getResourceAsStream("com/pitchforkbunnies/blobber/shaders/" + ref);
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
		glUseProgram(pID);
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
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
		
		System.out.println("tex size: " + tw);
		System.out.println("other size: " + currentTexture.getTextureWidth());
		
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
	public float getHeight() {
		return 1;
	}
	
	/**
	 * Gets the width of the screen, in screen heights (the aspect ratio)
	 * @return the width of the screen
	 */
	public float getWidth() {
		return (float) Display.getWidth() / Display.getHeight();
	}
	
	public void release() {
		glDeleteProgram(pID);
		glDeleteShader(vsID);
		glDeleteShader(fsID);
		
		glDeleteBuffers(vboID);
		glDeleteBuffers(indexID);
		glDeleteVertexArrays(vaoID);
	}
}
