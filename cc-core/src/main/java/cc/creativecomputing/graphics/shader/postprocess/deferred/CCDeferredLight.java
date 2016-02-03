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

/**
 * @author christianriekoff
 *
 */
public abstract class CCDeferredLight {

	//color of the light 
	protected CCColor _myColor;
	
	protected float _mySpecularIntensity = 0.0f;
	
	protected float _mySpecularPower = 255.0f;
	
	public CCDeferredLight(CCColor theColor) {
		_myColor = theColor;
	}
	
	public CCColor color() {
		return _myColor;
	}
	
	@CCControl(name = "red", min = 0, max = 1)
	public void red(float theRed) {
		_myColor.r = theRed;
	}
	
	@CCControl(name = "green", min = 0, max = 1)
	public void green(float theGreen) {
		_myColor.g = theGreen;
	}
	
	@CCControl(name = "blue", min = 0, max = 1)
	public void blue(float theBlue) {
		_myColor.b = theBlue;
	}
	
	@CCControl(name = "specular intensity", min = 0, max = 1)
	public void specularIntensity(float theSpecularIntensity){
		_mySpecularIntensity = theSpecularIntensity;
	}
	
	public float specularIntensity(){
		return _mySpecularIntensity;
	}
	
	@CCControl(name = "specular power", min = 0, max = 255)
	public void specularPower(float theSpecularPower){
		_mySpecularPower = theSpecularPower;
	}
	
	public float specularPower(){
		return _mySpecularPower;
	}
}
