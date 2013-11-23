package com.pitchforkbunnies.blobber;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;


public class LightMap {
	private List<Vertex> vertices = new ArrayList<Vertex>();
	private List<Segment> segments = new ArrayList<Segment>();
	
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(36);
	
	private int vsID, fsID, pID, vaoID, vboID, colorID, attenuationID, frameID, textureID;
	private int wallvaoID, wallvboID, wallIndexID, wallID, wallvsID, wallfsID, wallColorID, wallAttenuationID;
	
	private Level level;
	
	public LightMap(Level level) {
		setLevel(level);
		initFrameBuffer();
		initShaders();
		initBuffers();
	}
	
	private void initFrameBuffer() {
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Display.getWidth(), Display.getHeight(), 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		frameID = glGenFramebuffersEXT();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameID);
		glFramebufferTexture2D(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, textureID, 0);
		
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		//glClearColor(.5f, .5f, .5f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	private void initShaders() {
		vsID = Graphics.loadShader("lightingVS.glsl", GL_VERTEX_SHADER);
		fsID = Graphics.loadShader("lightingFS.glsl", GL_FRAGMENT_SHADER);
		
		wallvsID = Graphics.loadShader("lightingWallVS.glsl", GL_VERTEX_SHADER);
		wallfsID = Graphics.loadShader("lightingWallFS.glsl", GL_FRAGMENT_SHADER);
		
		pID = glCreateProgram();
		glAttachShader(pID, vsID);
		glAttachShader(pID, fsID);
		
		wallID = glCreateProgram();
		glAttachShader(wallID, wallvsID);
		glAttachShader(wallID, wallfsID);
		
		glBindAttribLocation(pID, 0, "in_Position");
		glBindAttribLocation(pID, 1, "in_Displacement");
		
		glBindAttribLocation(wallID, 0, "in_Position");
		glBindAttribLocation(wallID, 1, "in_Displacement");
		glBindAttribLocation(wallID, 2, "in_Normal");
		glBindAttribLocation(wallID, 3, "in_Depth");
		
		glLinkProgram(pID);
		glValidateProgram(pID);
		
		glLinkProgram(wallID);
		glValidateProgram(wallID);
		
		colorID = glGetUniformLocation(pID, "color");
		attenuationID = glGetUniformLocation(pID, "attenuation");
		
		wallColorID = glGetUniformLocation(wallID, "color");
		wallAttenuationID = glGetUniformLocation(wallID, "attenuation");
	}
	
	private void initBuffers() {
		//set default values
		float[] values = new float[36];
		buffer.put(values);
		buffer.flip();
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 24, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 24, 16);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindVertexArray(0);
		
		buffer.rewind();
		
		wallvaoID = glGenVertexArrays();
		glBindVertexArray(wallvaoID);
		
		wallvboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, wallvboID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 36, 0);	//position
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 36, 16);	//displacement
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 36, 24);	//normal
		glVertexAttribPointer(3, 1, GL_FLOAT, false, 36, 32);	//depth
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindVertexArray(0);
		
		byte[] indices = {
				0, 1, 2,
				2, 1, 3
		};
		
		ByteBuffer indexBuffer = BufferUtils.createByteBuffer(6);
		indexBuffer.put(indices);
		indexBuffer.flip();
		
		wallIndexID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, wallIndexID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void setLevel(Level level) {
		this.level = level;
		
		segments.clear();
		vertices.clear();
		for(int i = 0; i < level.width; i++) {
			for(int j = 0; j < level.height; j++) {
				if(i == 0 || (level.tiles[i][j].walkable != level.tiles[i - 1][j].walkable))
					addSegment(i, j, i, j + 1);
				if(j == 0 || (level.tiles[i][j].walkable != level.tiles[i][j - 1].walkable))
					addSegment(i, j, i + 1, j);
			}
		}
		
		for(int i = 0; i < level.width; i++) {
			addSegment(i, level.height, i + 1, level.height);
		}
		
		for(int i = 0; i < level.height; i++) {
			addSegment(level.width, i, level.width, i + 1);
		}
	}
	
	private void addSegment(int x1, int y1, int x2, int y2) {
		Vertex v1 = addVertex(x1, y1);
		Vertex v2 = addVertex(x2, y2);
		Segment s = new Segment();
		s.start = v1;
		s.end = v2;
		segments.add(s);
		v1.segments.add(s);
		v2.segments.add(s);
	}
	
	private Vertex addVertex(int x, int y) {
		for(Vertex v : vertices) {
			if(v.x == x && v.y == y)
				return v;
		}
		Vertex v = new Vertex();
		v.x = x;
		v.y = y;
		vertices.add(v);
		return v;
	}
	
	public void begin() {
		glBindFramebufferEXT(GL_FRAMEBUFFER, frameID);
		glClear(GL_COLOR_BUFFER_BIT);
		glBlendFunc(GL_ONE, GL_ONE);
	}
	
	public void end() {
		glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
	}
	
	public void renderLight(float x, float y, float r, float g, float b, float intensity) {
		
		//calculate angles
		for(Vertex v : vertices) {
			v.angle = (float) Math.atan2(v.y - y, v.x - x);
		}
		
		//sort vertices using some dumb O(n^2) thing I just made up
		for(int i = 0; i < vertices.size(); i++) {
			for(int j = i + 1; j < vertices.size(); j++) {
				if(vertices.get(i).angle > vertices.get(j).angle) {
					Vertex temp = vertices.get(i);
					vertices.set(i, vertices.get(j));
					vertices.set(j, temp);
				}
			}
		}
		
		//order start, end and dist of segments
		for(Segment s : segments) {
			float xx = (s.start.x + s.end.x) / 2 - x;
			float yy = (s.start.y + s.end.y) / 2 - y;
			s.dist = xx * xx + yy * yy;
			
			float da = s.end.angle - s.start.angle;
			if(da < -Math.PI) da += Math.PI * 2;
			if(da > Math.PI) da -= Math.PI * 2;
			if(da < 0) {
				Vertex temp = s.start;
				s.start = s.end;
				s.end = temp;
			}
		}
		
		List<Segment> open = new ArrayList<Segment>();
		Segment lastSegment = null, currentSegment = null;
		float lastX = 0, lastY = 0;
		
		//first pass is to populate open list, second to render
		for(int i = 0; i < 2; i++) {
			for(Vertex v : vertices) {
				//set last segment to current one
				lastSegment = currentSegment;
				
				//update open list
				for(Segment s : v.segments) {
					if(v == s.start) {
						int j;
						for(j = 0; j < open.size(); j++) {
							if(open.get(j).dist > s.dist)
								break;
						}
						open.add(j, s);
					} else if(open.contains(s)) {
						open.remove(s);
					}
				}
				
				if(!open.isEmpty())
					currentSegment = open.get(0);
				else
					System.out.println("this should not happen");
				
				float xx, yy, t, ix, iy;
				if(i != 0) {
					//find intersection with last segment
					xx = lastSegment.end.x - lastSegment.start.x;
					yy = lastSegment.end.y - lastSegment.start.y;
					t = (xx * (lastSegment.start.y - y) - yy * (lastSegment.start.x - x)) /
							(xx * (v.y - y) - yy * (v.x - x));
					ix = x + (v.x - x) * t;
					iy = y + (v.y - y) * t;
					
					drawTriangle(x, y, ix, iy, lastX, lastY, r, g, b, intensity);
				}
				
				if(currentSegment != null) {
					//find intersection with current segment, store it to lastX, lastY
					//we can re-use variables because i'm lazy
					xx = currentSegment.end.x - currentSegment.start.x;
					yy = currentSegment.end.y - currentSegment.start.y;
					t = (xx * (currentSegment.start.y - y) - yy * (currentSegment.start.x - x)) /
							(xx * (v.y - y) - yy * (v.x - x));
					lastX = x + (v.x - x) * t;
					lastY = y + (v.y - y) * t;
				}
			}	
		}
	}
	
	public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float r, float g, float b, float intensity) {
		glUseProgram(pID);
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glUniform4f(colorID, r, g, b, intensity);
		glUniform3f(attenuationID, 1, 0, .5f);
		
		float sx1 = (x1 - level.xo) * Tile.TILE_WIDTH_H / Graphics.getWidth() * 2 - 1;
		float sy1 = (y1 - level.yo) * Tile.TILE_WIDTH_H / Graphics.getHeight() * -2 + 1;
		float sx2 = (x2 - level.xo) * Tile.TILE_WIDTH_H / Graphics.getWidth() * 2 - 1;
		float sy2 = (y2 - level.yo) * Tile.TILE_WIDTH_H / Graphics.getHeight() * -2 + 1;
		float sx3 = (x3 - level.xo) * Tile.TILE_WIDTH_H / Graphics.getWidth() * 2 - 1;
		float sy3 = (y3 - level.yo) * Tile.TILE_WIDTH_H / Graphics.getHeight() * -2 + 1;
		
		float[] values = {
				sx1, sy1, 0, 1, 0, 0,
				sx2, sy2, 0, 1, x2 - x1, y2 - y1,
				sx3, sy3, 0, 1, x3 - x1, y3 - y1
		};
		
		buffer.clear();
		buffer.put(values);
		buffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindVertexArray(vaoID);
		glDrawArrays(GL_TRIANGLES, 0, 3);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glUseProgram(wallID);
		
		glUniform4f(wallColorID, r, g, b, intensity);
		glUniform3f(wallAttenuationID, 1, 0, .5f);
		
		glBindVertexArray(wallvaoID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, wallIndexID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		
		float nx = y3 - y2;
		float ny = x2 - x3;
		float length = (float) Math.sqrt(nx * nx + ny * ny);
		nx /= length;
		ny /= length;
		
		float snx = nx * Tile.TILE_WIDTH_H * 2 / Graphics.getWidth();
		float sny = ny * Tile.TILE_WIDTH_H * -2 / Graphics.getHeight();
		
		float[] quad = {
				sx2, sy2, 0, 1, x2 - x1, y2 - y1, nx, ny, 1,
				sx3, sy3, 0, 1, x3 - x1, y3 - y1, nx, ny, 1,
				sx2 - snx, sy2 - sny, 0, 1, x2 - x1, y2 - y1, nx, ny, 0,
				sx3 - snx, sy3 - sny, 0, 1, x3 - x1, y3 - y1, nx, ny, 0
		};
		
		buffer.clear();
		buffer.put(quad);
		buffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, wallvboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
		
		glUseProgram(0);
		glBindVertexArray(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
	}
	
	public int getFrameBuffer() {
		return textureID;
	}
	
	public void release() {
		glDeleteFramebuffersEXT(frameID);
		glDeleteTextures(textureID);
		
		glUseProgram(0);
		glDetachShader(pID, vsID);
		glDetachShader(pID, fsID);
		glDeleteProgram(pID);
		
		glDeleteBuffers(vboID);
		glDeleteVertexArrays(vaoID);
	}
}

class Segment {
	public Vertex start, end;
	public float dist;	//distance^2
}

class Vertex {
	public float x, y, angle;
	public List<Segment> segments = new ArrayList<Segment>();
}