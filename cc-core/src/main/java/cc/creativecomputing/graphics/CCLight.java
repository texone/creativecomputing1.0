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
package cc.creativecomputing.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import cc.creativecomputing.math.CCVector3f;


public abstract class CCLight {
	
	protected static int lightCount;
	protected static int MAX_LIGHTS;
	
	static{
		IntBuffer lights = IntBuffer.allocate(1);
		CCGraphics.currentGL().glGetIntegerv(GL2.GL_MAX_LIGHTS, lights);
		MAX_LIGHTS = lights.get();
	}
	
	protected final int _myLightID;
	protected boolean _myIsEnabled = true;
	protected CCColor _mySpecular = null;
	
	protected float _myCurrentLightFalloffConstant = 1;
	protected float _myCurrentLightFalloffLinear = 0;
	protected float _myCurrentLightFalloffQuadratic = 0;

	public CCLight() {
		checkNumberOfLights();
		_myLightID = lightCount++;
	}
	
	public CCLight(final int theLightID) {
		_myLightID = theLightID;
	}
	
	/**
	 * Checks that the number of lights is not bigger than allowed by OPENGL
	 * @param i_lightCount number of lights
	 */
	private void checkNumberOfLights(){
		if (lightCount == MAX_LIGHTS)
			throw new RuntimeException("can only create " + MAX_LIGHTS + " lights");
	}

	/**
	 * Enables a light in OPENGL
	 */
	protected void glLightEnable(GL2 gl){
		if(_myIsEnabled)gl.glEnable(GLLightingFunc.GL_LIGHT0 + _myLightID);
		
		else gl.glDisable(GLLightingFunc.GL_LIGHT0 + _myLightID);
	}

	private FloatBuffer lightBuffer = FloatBuffer.allocate(4);

	/**
	 * Sets the ambience for the actual light
	 */
	protected void glLightAmbient(GL2 gl, CCColor theColor){
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_AMBIENT, theColor.array(),0);
	}

	/**
	 * Sets the ambience of the actual light to none
	 * @param num
	 */
	protected void glLightNoAmbient(GL2 gl){
		// hopefully buffers are filled with zeroes..
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_AMBIENT, new CCColor().array(),0);
	}

	/**
	 * Sets the defuse for the actual light
	 * @param i_r
	 * @param i_g
	 * @param i_b
	 */
	protected void glLightDiffuse(GL2 gl, CCColor theDiffuse){
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_DIFFUSE, theDiffuse.array(),0);
	}

	/**
	 * Sets the position of the actual light
	 * @param thePosition
	 */
	protected void glLightPosition(GL2 gl,final CCVector3f thePosition){
		lightBuffer.put(thePosition.x);
		lightBuffer.put(thePosition.y);
		lightBuffer.put(thePosition.z);
		lightBuffer.put(1);
		lightBuffer.rewind();
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_POSITION, lightBuffer);
	}

	/**
	 * Sets the position of the actual light
	 * @param theDirection
	 */
	protected void glLightDirection(GL2 gl,final CCVector3f theDirection){
		lightBuffer.put(theDirection.x);
		lightBuffer.put(theDirection.y);
		lightBuffer.put(theDirection.z);
		lightBuffer.put(0);
		lightBuffer.rewind();
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_POSITION, lightBuffer);
	}

	protected void glLightSpecular(GL2 gl){
		if(_mySpecular != null)
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_SPECULAR, _mySpecular.array(),0);
	}

	protected void glLightFallOff(GL2 gl){
		gl.glLightf(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_CONSTANT_ATTENUATION, _myCurrentLightFalloffConstant);
		gl.glLightf(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_LINEAR_ATTENUATION, _myCurrentLightFalloffLinear);
		gl.glLightf(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_QUADRATIC_ATTENUATION, _myCurrentLightFalloffQuadratic);
	}

	/**
	 * Sets the fall off rates for point lights, spot lights, and ambient lights. 
	 * The parameters are used to determine the fall off with the following equation:
	 * 
	 * <blockquote><code>
	 * d = distance from light position to vertex position<br>
	 * fall off = 1 / (CONSTANT + d * LINEAR + (d*d) * QUADRATIC)</code></blockquote>
	 * 
	 * The default value if LightFalloff(1.0, 0.0, 0.0). Thinking about an ambient light with a 
	 * fall off can be tricky. It is used, for example, if you wanted a region of your scene to be
	 * lit ambiently one color and another region to be lit ambiently by another color, 
	 * you would use an ambient light with location and fall off. You can think of it as a 
	 * point light that doesn't care which direction a surface is facing. Only non negative values 
	 * are accepted.
	 * @shortdesc Sets the fall off rates for point lights, spot lights, and ambient lights. 
	 * @param theConstant constant value for determining fall off
	 * @param theLinear linear value for determining fall off
	 * @param theQuadratic quadratic value for determining fall off
	 */
	public void fallOff(final float theConstant, final float theLinear, final float theQuadratic){
		if(theConstant < 0 || theLinear < 0 || theQuadratic < 0)throw new CCGraphicsException("Only use non negative fall off values.");
		_myCurrentLightFalloffConstant = theConstant;
		_myCurrentLightFalloffLinear = theLinear;
		_myCurrentLightFalloffQuadratic = theQuadratic;
	}

	/**
	 * Sets the specular color for lights. Specular refers to light which bounces 
	 * off a surface in a preferred direction (rather than bouncing in all directions 
	 * like a diffuse light) and is used for creating highlights. The specular quality 
	 * of a light interacts with the specular material qualities set through the 
	 * <b>specular()</b> and <b>shininess()</b> functions.
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 */
	public void specular(final float theRed, final float theGreen, final float theBlue){
		_mySpecular = new CCColor(theRed,theGreen,theBlue);
	}
	public void specular(CCColor theSpecular){
		_mySpecular = theSpecular;
	}

	public int getID() {
		return _myLightID;
	}
	
	public abstract void draw(final CCGraphics g);

}
