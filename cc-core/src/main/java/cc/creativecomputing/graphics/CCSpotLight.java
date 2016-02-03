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

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import cc.creativecomputing.math.CCVector3f;


/**
 * The x, y, and z parameters specify the position of the light and nx, ny, nz specify the direction or light. 
 * The angle parameter affects angle of the spotlight cone.
 * @author texone
 *
 */
public class CCSpotLight extends CCLight{
	
	private CCColor _myDiffuse;
	private CCVector3f _myPosition;
	private CCVector3f _myDirection;
	
	private float _mySpotAngle;
	private float _mySpotConcentration;
	
	public CCSpotLight(
		final float theRed, final float theGreen, final float theBlue, 
		final float theXposition, final float theYposition, final float theZposition,
		final float theXdirection, final float theYdirection, final float theZdirection,
		final float theSpotAngle, final float theSpotConcentration
	){
		super();	
		_myDiffuse = new CCColor(theRed,theGreen,theBlue);
		_myPosition = new CCVector3f(theXposition,theYposition,theZposition);
		_myDirection = new CCVector3f(theXdirection,theYdirection,theZdirection);
		_mySpotAngle = theSpotAngle;
		_mySpotConcentration = theSpotConcentration;
	}
	
	public CCSpotLight(
		final CCColor theDiffuse, 
		final CCVector3f thePosition,
		final CCVector3f theDirection,
		final float theSpotAngle, final float theSpotConcentration
	){
		_myDiffuse = theDiffuse;
		_myPosition = thePosition;
		_myDirection = theDirection;
		_mySpotAngle = theSpotAngle;
		_mySpotConcentration = theSpotConcentration;
	}

	private void glLightSpotDirection(GL2 gl,final CCVector3f theDirection){
		gl.glLightfv(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_SPOT_DIRECTION, new float[] {theDirection.x,theDirection.y,theDirection.z},0);
	}

	private void glLightSpotAngle(GL2 gl, final float theAngle){
		gl.glLightf(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_SPOT_CUTOFF, theAngle);
	}

	private void glLightSpotConcentration(GL2 gl, final float theConcentration){
		gl.glLightf(GLLightingFunc.GL_LIGHT0 + _myLightID, GLLightingFunc.GL_SPOT_EXPONENT, theConcentration);
	}
	
	@Override
	public void draw(CCGraphics g) {
		glLightEnable(g.gl);
		if(!_myIsEnabled)return;
		
		glLightFallOff(g.gl);
		glLightNoAmbient(g.gl);
		glLightDiffuse(g.gl,_myDiffuse);
		glLightPosition(g.gl,_myPosition);
		glLightSpecular(g.gl);
		
		glLightSpotDirection(g.gl, _myDirection);
		glLightSpotAngle(g.gl, _mySpotAngle);
		glLightSpotConcentration(g.gl, _mySpotConcentration);
	}

}
