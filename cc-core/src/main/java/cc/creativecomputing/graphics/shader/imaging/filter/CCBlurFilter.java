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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public class CCBlurFilter extends CCImageFilter{
	
	@CCControl(name = "radius", min = 0, max = 10)
	private float _cBlurRadius = 0;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	private CCShaderBuffer _myOutput;
	
	private int _myWidth;
	private int _myHeight;

	public CCBlurFilter(CCGraphics theGraphics, CCTexture2D theInput, float theMaximumRadius, int theScale) {
		super(theGraphics, theInput);
		_myWidth = _myInput.width() * theScale;
		_myHeight = _myInput.height() * theScale;
		
		_myOutput = new CCShaderBuffer (_myWidth, _myHeight);
		_myBlur = new CCGPUSeperateGaussianBlur (20, _myWidth, _myHeight);
		_myBlur.beginDraw(theGraphics);
		_myBlur.endDraw(theGraphics);
	}
	
	public CCBlurFilter(CCGraphics theGraphics, CCTexture2D theInput, float theMaximumRadius){
		this(theGraphics, theInput, theMaximumRadius, 1);
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
	public CCTexture2D input() {
		return _myInput;
	}
	
	public void setRadius (float theRadius) {
		_cBlurRadius = theRadius;
	}
	

	@Override
	public void update(float theDeltaTime) {
		
		_myGraphics.pushAttribute();
		_myBlur.radius(_cBlurRadius * _myBlur.maxRadius());
		_myBlur.beginDraw(_myGraphics);
		_myGraphics.image(_myInput, -_myInput.width()/2, -_myInput.height()/2, _myWidth, _myHeight);
		_myBlur.endDraw (_myGraphics);
		_myGraphics.popAttribute();
		
		_myOutput.beginDraw();
		_myGraphics.clear();
		_myGraphics.image(_myBlur.blurredTexture(), 0,0);
		_myOutput.endDraw();
	}
}
