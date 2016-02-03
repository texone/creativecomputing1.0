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

import cc.creativecomputing.math.CCVector3f;

/**
 * Directional light comes from one direction and is stronger when hitting a s
 * urface squarely and weaker if it hits at a a gentle angle. After hitting a 
 * surface, a directional lights scatters in all directions. 
 * The nx, ny, and nz parameters specify the direction the light is facing. 
 * For example, setting ny to -1 will cause the geometry to be lit from below 
 * (the light is facing directly upward).
 * @author texone
 *
 */
public class CCDirectionalLight extends CCLight{
	
	private CCColor _myDiffuse;
	private CCColor _myAmbient;
	private CCVector3f _myDirection;
	
	public CCDirectionalLight(
		final float theRed, final float theGreen, final float theBlue, 
		final float theX, final float theY, final float theZ
	){
		super();	
		_myDiffuse = new CCColor(theRed,theGreen,theBlue);
		_myAmbient = new CCColor(theRed,theGreen,theBlue);
		_myDirection = new CCVector3f(theX,theY,theZ);
	}
	
	public CCDirectionalLight(final CCColor theDiffuse, final CCVector3f theDirection){
		_myDiffuse = theDiffuse;
		_myDirection = theDirection;
	}
	
	@Override
	public void draw(CCGraphics g) {
		glLightEnable(g.gl);
		if(!_myIsEnabled)return;
		
		glLightFallOff(g.gl);
		glLightAmbient(g.gl, _myAmbient);
		glLightDiffuse(g.gl,_myDiffuse);
		glLightDirection(g.gl,_myDirection);
		glLightSpecular(g.gl);
	}

	/**
	 * Returns the direction of the light
	 * @return
	 */
	public CCVector3f direction(){
		return _myDirection;
	}
	
	/**
	 * Sets the direction of the light
	 * @param theDirection
	 */
	public void direction(final CCVector3f theDirection){
		_myDirection = theDirection;
	}
	
	/**
	 * Returns the diffuse color of the light
	 * @return
	 */
	public CCColor diffuse(){
		return _myDiffuse;
	}
	
	/**
	 * Sets the diffuse color of the light
	 * @param theColor
	 */
	public void diffuse(final CCColor theColor){
		_myDiffuse.set(theColor);
	}
	
	public void diffuse(final float theRed, final float theGreen, final float theBlue){
		_myDiffuse.set(theRed, theGreen, theBlue);
	}
	
	public void ambient(final CCColor theDiffuseColor){
		_myAmbient = theDiffuseColor;
	}
	
	public void ambient(final float theRed, final float theGreen, final float theBlue){
		_myAmbient.red(theRed);
		_myAmbient.green(theGreen);
		_myAmbient.blue(theBlue);
	}
}
