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
 * Defines a point light. 
 * The x, y, and z parameters set the position of the light.
 * @author texone
 *
 */
public class CCPointLight extends CCLight{
	
	private CCColor _myDiffuse;
	private CCColor _myAmbient;
	private CCVector3f _myPosition;
	
	public CCPointLight(
		final float theRed, final float theGreen, final float theBlue, 
		final float theX, final float theY, final float theZ
	){
		super();	
		_myDiffuse = new CCColor(theRed,theGreen,theBlue);
		_myAmbient = new CCColor(theRed,theGreen,theBlue);
		_myPosition = new CCVector3f(theX,theY,theZ);
	}
	
	public CCPointLight(final CCColor theDiffuse, final CCVector3f theDirection){
		_myDiffuse = theDiffuse;
		_myAmbient = theDiffuse;
		_myPosition = theDirection;
	}
	
	public CCVector3f position(){
		return _myPosition;
	}
	
	@Override
	public void draw(CCGraphics g) {
		glLightEnable(g.gl);
		if(!_myIsEnabled)return;
		
		glLightFallOff(g.gl);
		glLightAmbient(g.gl, _myAmbient);
		glLightDiffuse(g.gl,_myDiffuse);
		glLightPosition(g.gl,_myPosition);
		glLightSpecular(g.gl);
	}

	public void diffuse(final CCColor theDiffuseColor){
		_myDiffuse = theDiffuseColor;
	}
	
	public void diffuse(final float theRed, final float theGreen, final float theBlue){
		_myDiffuse.red(theRed);
		_myDiffuse.green(theGreen);
		_myDiffuse.blue(theBlue);
	}
	
	public void ambient(final CCColor theDiffuseColor){
		_myAmbient = theDiffuseColor;
	}
	
	public void ambient(final float theRed, final float theGreen, final float theBlue){
		_myAmbient.red(theRed);
		_myAmbient.green(theGreen);
		_myAmbient.blue(theBlue);
	}
	
	public CCColor diffuse(){
		return _myDiffuse;
	}
}
