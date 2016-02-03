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
 * Ambient light doesn't come from a specific direction, the rays have light have bounced 
 * around so much that objects are evenly lit from all sides. Ambient lights are almost 
 * always used in combination with other types of lights.
 * @author texone
 *
 */
public class CCAmbientLight extends CCLight{
	
	private CCColor _myAmbient;
	private CCVector3f _myPosition;
	
	/**
	 * Creates an ambient light using the given color.
	 */
	public CCAmbientLight(final float theRed, final float theGreen, final float theBlue){
		this(theRed, theGreen, theBlue, 0 ,0 , 0);
	}

	public CCAmbientLight(
		final float theRed, final float theGreen, final float theBlue, 
		final float theX, final float theY, final float theZ
	){
		super();
		_myAmbient = new CCColor(theRed, theGreen, theBlue);
		_myPosition = new CCVector3f(theX,theY,theZ);
	}
	
	public CCAmbientLight(final CCColor theColor){
		this(theColor, new CCVector3f());
	}
	
	public CCAmbientLight(final CCColor theColor, final CCVector3f thePosition){
		_myAmbient = theColor;
		_myPosition = thePosition;
	}
	
	public void ambient(final float theRed, final float theGreen, final float theBlue){
		_myAmbient = new CCColor(theRed, theGreen, theBlue);
	}
	
	@Override
	public void draw(CCGraphics g){
		glLightEnable(g.gl);
		if(!_myIsEnabled)return;
		glLightAmbient(g.gl, _myAmbient);
		glLightFallOff(g.gl);
		glLightPosition(g.gl, _myPosition);
		glLightSpecular(g.gl);
	}
}
