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
package cc.creativecomputing.graphics.shader.postprocess.deferred;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCDirectionalLight extends CCDeferredLight{
	//direction of the light
	private CCVector3f _myLightDirection;
	
	public CCDirectionalLight(CCColor theColor, CCVector3f theDirection) {
		super(theColor);
		_myLightDirection = theDirection;
	}
	
	public CCDirectionalLight() {
		this(new CCColor(), new CCVector3f());
	}
	
	public CCVector3f lightDirection() {
		return _myLightDirection;
	}
	
	public void lightDirection(float theX, float theY, float theZ) {
		_myLightDirection.set(theX, theY, theZ);
	}
	
	public void lightDirection(CCVector3f theDirection) {
		_myLightDirection.set(theDirection);
	}
	
	@CCControl(name = "x", min = -1, max = 1)
	public void x(float theX) {
		_myLightDirection.x = theX;
	}
	
	@CCControl(name = "y", min = -1, max = 1)
	public void y(float theY) {
		_myLightDirection.y = theY;
	}
	
	@CCControl(name = "z", min = -1, max = 1)
	public void z(float theZ) {
		_myLightDirection.z = theZ;
	}
}
