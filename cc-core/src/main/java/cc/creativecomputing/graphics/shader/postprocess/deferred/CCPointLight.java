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
public class CCPointLight extends CCDeferredLight{
	
	//this is the position of the light
	private CCVector3f _myPosition;
	
	//how far does this light reach
	private float _myRadius;
	
	//control the brightness of the light
	private float _myIntensity = 1.0f;
	
	public CCPointLight(
		CCColor theColor, 
		CCVector3f theLightPosition,
		float theLightRadius,
		float theLightIntensity
	) {
		super(theColor);
		_myPosition = theLightPosition;
		_myRadius = theLightRadius;
		_myIntensity = theLightIntensity;
	}
	
	public CCPointLight() {
		this(new CCColor(255), new CCVector3f(), 10, 1.0f);
	}
	
	public CCVector3f position() {
		return _myPosition;
	}
	
	public void position(float theX, float theY, float theZ) {
		_myPosition.set(theX, theY, theZ);
	}
	
	public void position(CCVector3f theDirection) {
		_myPosition.set(theDirection);
	}
	
	@CCControl(name = "x", min = -1000, max = 1000)
	public void x(float theX) {
		_myPosition.x = theX;
	}
	
	@CCControl(name = "y", min = -1000, max = 1000)
	public void y(float theY) {
		_myPosition.y = theY;
	}
	
	@CCControl(name = "z", min = -1000, max = 1000)
	public void z(float theZ) {
		_myPosition.z = theZ;
	}
	
	@CCControl(name = "intensity", min = 0, max = 1)
	public void intensity(float theIntensity) {
		_myIntensity = theIntensity;
	}
	
	public float intensity() {
		return _myIntensity;
	}
	
	@CCControl(name = "radius", min = 0, max = 1000)
	public void radius(float theRadius) {
		_myRadius = theRadius;
	}
	
	public float radius() {
		return _myRadius;
	}
}
