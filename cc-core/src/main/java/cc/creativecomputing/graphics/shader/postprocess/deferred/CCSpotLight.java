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
public class CCSpotLight extends CCPointLight{
	
	private CCVector3f _myDirection;
	
	private float _myConeAngle;
	
	private float _mySpotDecayExponent;
	
	public CCSpotLight(
		CCColor theColor, 
		CCVector3f thePosition,
		CCVector3f theDirection,
		float theRadius,
		float theIntensity
	) {
		super(theColor, thePosition, theRadius, theIntensity);
		_myDirection = theDirection;
	}
	
	public CCSpotLight() {
		this(new CCColor(), new CCVector3f(), new CCVector3f(0, 0,1), 10, 1.0f);
	}
	
	public CCVector3f direction() {
		return _myDirection;
	}
	
	public void direction(float theX, float theY, float theZ) {
		_myDirection.set(theX, theY, theZ);
	}
	
	public void direction(CCVector3f theDirection) {
		_myDirection.set(theDirection);
	}
	
	@CCControl(name = "direction x", min = -1, max = 1)
	public void directionX(float theX) {
		_myDirection.x = theX;
	}
	
	@CCControl(name = "direction y", min = -1, max = 1)
	public void directionY(float theY) {
		_myDirection.y = theY;
	}
	
	@CCControl(name = "direction z", min = -1, max = 1)
	public void directionZ(float theZ) {
		_myDirection.z = theZ;
	}
	
	@CCControl(name = "cone angle", min = 0, max = 90)
	public void coneAngle(float theConeAngle) {
		_myConeAngle = theConeAngle;
	}
	
	public float coneAngle() {
		return _myConeAngle;
	}
	
	@CCControl(name = "spot decay exponent", min = 0, max = 10)
	public void spotDecayExponent(float theRadius) {
		_mySpotDecayExponent = theRadius;
	}
	
	public float spotDecayExponent() {
		return _mySpotDecayExponent;
	}
}
