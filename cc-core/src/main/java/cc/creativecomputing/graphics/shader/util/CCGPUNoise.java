/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.graphics.shader.util;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Use this class to apply noise to your shader, to get noise into your shader, you have
 * to include the simplex.fp shader file that handles all the noise calculation, this might 
 * be simplified in a later step. This class than sets all the values and textures that are
 * needed to calculate the noise values. To apply the setup you just have to call the 
 * constructor and pass the shader program that uses the noise. Make sure you call the 
 * constructor after you have loaded the shader, otherwise the textures used by the noise are not
 * set correctly.
 * @author artcom
 *
 */
public class CCGPUNoise {
	
	private static CCGPUNoise noise;
	
	public static void attachFragmentNoise(final CCCGShader theShader){
	    CGparameter myPermTextureParam = theShader.fragmentParameter("permTexture");
	    CGparameter mySimplexTextureParam = theShader.fragmentParameter("simplexTexture");
	    CGparameter myGradTextureParam = theShader.fragmentParameter("gradTexture");
	    
	    if(noise == null) noise = new CCGPUNoise();
	    
	    theShader.texture(myPermTextureParam, noise.permTextureID[0]);
	    theShader.texture(mySimplexTextureParam, noise.simplexTextureID[0]);
	    theShader.texture(myGradTextureParam, noise.gradTextureID[0]);
	}
	
	public static void attachVertexNoise(final CCCGShader theShader){
	    CGparameter myPermTextureParam = theShader.vertexParameter("permTexture");
	    CGparameter mySimplexTextureParam = theShader.vertexParameter("simplexTexture");
	    CGparameter myGradTextureParam = theShader.vertexParameter("gradTexture");
	    
	    if(noise == null) noise = new CCGPUNoise();
	    
	    theShader.texture(myPermTextureParam, noise.permTextureID[0]);
	    theShader.texture(mySimplexTextureParam, noise.simplexTextureID[0]);
	    theShader.texture(myGradTextureParam, noise.gradTextureID[0]);
	}
	
	private static int[] perm = { 
		151, 160, 137,  91,  90,  15, 131,  13, 201,  95,  96,  53, 194, 233,   7, 225, 
		140,  36, 103,  30,  69, 142,   8,  99,  37, 240,  21,  10,  23, 190,   6, 148, 
		247, 120, 234,  75,   0,  26, 197,  62,  94, 252, 219, 203, 117,  35,  11,  32, 
		 57, 177,  33,  88, 237, 149,  56,  87, 174,  20, 125, 136, 171, 168,  68, 175, 
		 74, 165,  71, 134, 139,  48,  27, 166,  77, 146, 158, 231,  83, 111, 229, 122, 
		 60, 211, 133, 230, 220, 105,  92,  41,  55,  46, 245,  40, 244, 102, 143,  54, 
		 65,  25,  63, 161,   1, 216,  80,  73, 209,  76, 132, 187, 208,  89,  18, 169, 
		200, 196, 135, 130, 116, 188, 159,  86, 164, 100, 109, 198, 173, 186,   3,  64, 
		 52, 217, 226, 250, 124, 123,   5, 202,  38, 147, 118, 126, 255,  82,  85, 212, 
		207, 206,  59, 227,  47,  16,  58,  17, 182, 189,  28,  42, 223, 183, 170, 213, 
		119, 248, 152,   2,  44, 154, 163,  70, 221, 153, 101, 155, 167,  43, 172,   9, 
		129,  22,  39, 253,  19,  98, 108, 110,  79, 113, 224, 232, 178, 185, 112, 104, 
		218, 246,  97, 228, 251,  34, 242, 193, 238, 210, 144,  12, 191, 179, 162, 241, 
		 81,  51, 145, 235, 249,  14, 239, 107,  49, 192, 214,  31, 181, 199, 106, 157, 
		184,  84, 204, 176, 115, 121,  50,  45, 127,   4, 150, 254, 138, 236, 205,  93, 
		222, 114,  67,  29,  24,  72, 243, 141, 128, 195,  78,  66, 215,  61, 156, 180 
	};

	/**
	 * These are Ken Perlin's proposed gradients for 3D noise. I kept them for
	 * better consistency with the reference implementation, but there is really
	 * no need to pad this to 16 gradients for this particular implementation.
	 * If only the "proper" first 12 gradients are used, they can be extracted
	 * from the grad4[][] array: grad3[i][j] == grad4[i2][j], 0<=i<=11, j=0,1,2
	 */
	private static int[][] grad3 = {
		{ 0, 1,  1 }, {  0,  1, -1 }, {  0, -1, 1 }, {  0, -1, -1 }, 
		{ 1, 0,  1 }, {  1,  0, -1 }, { -1,  0, 1 }, { -1,  0, -1 }, 
		{ 1, 1,  0 }, {  1, -1,  0 }, { -1,  1, 0 }, { -1, -1,  0 }, // 12 cube edges
		{ 1, 0, -1 }, { -1,  0, -1 }, {  0, -1, 1 }, {  0,  1,  1 } // 4 more to make 16
	}; 

	/**
	 * These are my own proposed gradients for 4D noise. They are the
	 * coordinates of the midpoints of each of the 32 edges of a tesseract, just
	 * like the 3D noise gradients are the midpoints of the 12 edges of a cube.
	 */
	private static int[][] grad4 = { 
		{  0,  1, 1, 1 }, {  0,  1,  1, -1 }, {  0,  1, -1, 1 }, {  0,  1, -1, -1 }, // 32 tesseract edges
		{  0, -1, 1, 1 }, {  0, -1,  1, -1 }, {  0, -1, -1, 1 }, {  0, -1, -1, -1 }, 
		{  1,  0, 1, 1 }, {  1,  0,  1, -1 }, {  1,  0, -1, 1 }, {  1,  0, -1, -1 }, 
		{ -1,  0, 1, 1 }, { -1,  0,  1, -1 }, { -1,  0, -1, 1 }, { -1,  0, -1, -1 }, 
		{  1,  1, 0, 1 }, {  1,  1,  0, -1 }, {  1, -1,  0, 1 }, {  1, -1,  0, -1 }, 
		{ -1,  1, 0, 1 }, { -1,  1,  0, -1 }, { -1, -1,  0, 1 }, { -1, -1,  0, -1 }, 
		{  1,  1, 1, 0 }, {  1,  1, -1,  0 }, {  1, -1,  1, 0 }, {  1, -1, -1,  0 }, 
		{ -1,  1, 1, 0 }, { -1,  1, -1,  0 }, { -1, -1,  1, 0 }, { -1, -1, -1,  0 } 
	};

	/**
	 * This is a look-up table to speed up the decision on which simplex we are
	 * in inside a cube or hypercube "cell" for 3D and 4D simplex noise. It is
	 * used to avoid complicated nested conditionals in the GLSL code. The table
	 * is indexed in GLSL with the results of six pair-wise comparisons beween
	 * the components of the P=(x,y,z,w) coordinates within a hypercube cell. c1
	 * = x>=y ? 32 : 0; c2 = x>=z ? 16 : 0; c3 = y>=z ? 8 : 0; c4 = x>=w ? 4 :
	 * 0; c5 = y>=w ? 2 : 0; c6 = z>=w ? 1 : 0; offsets =
	 * simplex[c1+c2+c3+c4+c5+c6]; o1 = step(160,offsets); o2 =
	 * step(96,offsets); o3 = step(32,offsets); (For the 3D case, c4, c5, c6 and
	 * o3 are not needed.)
	 */
	private static int[][] simplex4 = {
		{   0,  64, 128, 192 }, {   0, 64, 192, 128 }, {   0,   0,  0,   0 }, {   0, 128, 192, 64 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {  64, 128, 192,  0 },
		{   0, 128,  64, 192 }, {   0,  0,   0,   0 }, {   0, 192, 64, 128 }, {   0, 192, 128, 64 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {  64, 192, 128,  0 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 }, 
		{  64, 128,   0, 192 }, {   0,  0,   0,   0 }, {  64, 192,  0, 128 }, {   0,   0,   0,  0 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, { 128, 192,  0,  64 }, { 128, 192,  64,  0 }, 
		{  64,   0, 128, 192 }, {  64,  0, 192, 128 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 }, 
		{   0,   0,   0,   0 }, { 128,  0, 192,  64 }, {   0,   0,  0,   0 }, { 128,  64, 192,  0 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 }, 
		{   0,   0,   0,   0 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 }, 
		{ 128,   0,  64, 192 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 },
		{ 192,   0,  64, 128 }, { 192,  0, 128,  64 }, {   0,   0,  0,   0 }, { 192,  64, 128,  0 }, 
		{ 128,  64,   0, 192 }, {   0,  0,   0,   0 }, {   0,   0,  0,   0 }, {   0,   0,   0,  0 },
		{ 192,  64,   0, 128 }, {   0,  0,   0,   0 }, { 192, 128,  0,  64 }, { 192, 128,  64,  0 }
	};

	private int[] permTextureID = new int[1];
	private int[] simplexTextureID = new int[1];
	private int[] gradTextureID = new int[1];
	
	private CCGPUNoise(){
		GL2 gl = CCGraphics.currentGL();
		initPermTexture(gl, permTextureID);
	    initSimplexTexture(gl, simplexTextureID);
	    initGradTexture(gl, gradTextureID);
	}

	/**
	 * initPermTexture(GLuinttexID) - create and load a 2D texture for a combined index permutation and gradient lookup
	 * table. This texture is used for 2D and 3D noise, both classic and simplex.
	 */
	private void initPermTexture(GL gl, int[] texID) {
		ByteBuffer pixels;
		int i, j;

		gl.glGenTextures(1, texID, 0); // Generate a unique texture ID
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]); // Bind the texture to texture unit 0

		pixels = ByteBuffer.allocateDirect(256 * 256 * 4);
		for (i = 0; i < 256; i++)
			for (j = 0; j < 256; j++) {
				int offset = (i * 256 + j) * 4;
				int value = perm[(j + perm[i]) & 0xFF];
				pixels.put(offset, (byte) (grad3[value & 0x0F][0] * 64 + 64)); // Gradient x
				pixels.put(offset + 1, (byte) (grad3[value & 0x0F][1] * 64 + 64)); // Gradient y
				pixels.put(offset + 2, (byte) (grad3[value & 0x0F][2] * 64 + 64)); // Gradient z
				pixels.put(offset + 3, (byte) value); // Permuted index
			}

		// GLFW texture loading functions won't work here - we need
		// GL.GL_NEAREST lookup.
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 256, 256, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	}

	/**
	 * initSimplexTexture(GLuinttexID) - create and load a 1D texture for a simplex traversal order lookup table. This
	 * is used for simplex noise only, and only for 3D and 4D noise where there are more than 2 simplices. (3D simplex
	 * noise has 6 cases to sort out, 4D simplex noise has 24 cases.)
	 */
	private void initSimplexTexture(GL2 gl, int[] texID) {
		gl.glGenTextures(1, texID, 0); // Generate a unique texture ID
		gl.glBindTexture(GL2.GL_TEXTURE_1D, texID[0]); // Bind the texture to texture unit 1

		ByteBuffer simplexBuffer = ByteBuffer.allocate(64 * 4);
		for (int[] myBytes : simplex4) {
			for (int myByte : myBytes) {
				simplexBuffer.put((byte) myByte);
			}
		}
		simplexBuffer.rewind();

		// GLFW texture loading functions won't work here - we need
		// GL.GL_NEAREST lookup.
		gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL.GL_RGBA, 64, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, simplexBuffer);
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	}

	/**
	 * initGradTexture(GLuinttexID) - create and load a 2D texture for a 4D gradient lookup table. This is used for 4D
	 * noise only.
	 */
	private void initGradTexture(GL gl, int[] texID) {
		ByteBuffer pixels;
		int i, j;

		gl.glGenTextures(1, texID, 0); // Generate a unique texture ID
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]); // Bind the texture to texture unit 2

		pixels = ByteBuffer.allocateDirect(256 * 256 * 4);
		for (i = 0; i < 256; i++)
			for (j = 0; j < 256; j++) {
				int offset = (i * 256 + j) * 4;
				int value = perm[(j + perm[i]) & 0xFF];
				pixels.put(offset, (byte) (grad4[value & 0x1F][0] * 64 + 64)); // Gradient x
				pixels.put(offset + 1, (byte) (grad4[value & 0x1F][1] * 64 + 64)); // Gradient y
				pixels.put(offset + 2, (byte) (grad4[value & 0x1F][2] * 64 + 64)); // Gradient z
				pixels.put(offset + 3, (byte) (grad4[value & 0x1F][3] * 64 + 64)); // Gradient
			}

		// GLFW texture loading functions won't work here - we need GL.GL_NEAREST lookup.
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 256, 256, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixels);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

	}
}
