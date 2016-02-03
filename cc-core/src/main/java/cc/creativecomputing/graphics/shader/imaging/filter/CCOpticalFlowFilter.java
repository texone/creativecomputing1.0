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
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

/**
 * This filter implements the Horn-Schunck algorithm to calculate the optical flow between 2 input images I(k), I(k-1).
 * 
 * In the first step it estimates the spatial partial derivatives Ix(k) and Iy(k) with a sobel kernel (output values are written
 * to the r and g channel) and the time derivative It(x,y,k) = sum [I(x+i, y+j, k) - I(x+i, y+j, k-1)] as a weighted 
 * sum of neighborhood pixel differences.
 * 
 * In the second step and optimal flow is approximated 
 * 
 * The third shader stage applies thresholding and an offset to the output.
 *
 * @author max goettner
 * @demo cc.creativecomputing.demo.cc.creativecomputing.demo.graphics.shader.filter.CCOpticalFlowDemo
 */


public class CCOpticalFlowFilter extends CCImageFilter{
	
	CCGLSLShader   _myShaderStage1;
	CCGLSLShader   _myShaderStage2;
	CCGLSLShader   _myShaderStage3;
	CCBlurFilter   _myBlurInputStage;
	
	CCShaderBuffer _myOutputStage1;
	CCShaderBuffer _myOutputStage2;
	CCShaderBuffer _myOutputStage3;
	
	CCShaderBuffer outTmp;
	CCShaderBuffer bufTmp;
	CCShaderBuffer _myLastInput;
	CCShaderBuffer _myPreBlurInput;

	@CCControl(name = "threshold", min = 0f, max = 0.1f)
	private float _cThresh;
	
	@CCControl(name = "offset", min = 0f, max = 1f)
	private float _cOffset;
	
	/*
	@CCControl(name = "blur radius", min = 0f, max = 10f)
	private float _cBlurRadius = 1f;
	*/
	@CCControl(name = "gain", min = 0f, max = 10f)
	private float _cGain = 1f;
	
	private int nSteps = 4;
	
	public CCOpticalFlowFilter(CCGraphics theGraphics, CCTexture2D theInput) {
		super(theGraphics, theInput);
		
		//_myBlurInputStage = new CCBlurFilter (theGraphics, theInput, 10);
		//_myBlurInputStage.setRadius(2f);
		
		//_myInput = _myBlurInputStage.output();
		_myOutputStage1 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage1.clear();
		
		_myOutputStage2 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage2.clear();
		
		_myOutputStage3 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage3.clear();
		
		_myShaderStage1 = new CCGLSLShader (CCIOUtil.classPath(this, "shader/partialDerivatives_vp.glsl"), CCIOUtil.classPath(this, "shader/partialDerivatives_fp.glsl"));
		_myShaderStage1.load();
		
		_myShaderStage2 = new CCGLSLShader (CCIOUtil.classPath(this, "shader/hornSchunck_vp.glsl"), CCIOUtil.classPath(this, "shader/hornSchunck_fp.glsl"));
		_myShaderStage2.load();
		
		_myShaderStage3 = new CCGLSLShader (CCIOUtil.classPath(this, "shader/offset_vp.glsl"), CCIOUtil.classPath(this, "shader/offset_fp.glsl"));
		_myShaderStage3.load();
		
		outTmp = new CCShaderBuffer(_myOutputStage1.width(), _myOutputStage1.height());
		bufTmp = new CCShaderBuffer(_myOutputStage1.width(), _myOutputStage1.height());
		_myLastInput = new CCShaderBuffer(_myInput.width(), _myInput.height());
		_myLastInput.clear();
		
		outTmp.clear();
		bufTmp.clear();
	}

	@Override
	public CCTexture2D output() {
		return _myOutputStage3.attachment(0);
	}
	
	public CCTexture2D outputUnshifted() {
		return _myOutputStage2.attachment(0);
	}
	

	public CCTexture2D input() {
		return _myInput;
	}
	
	public void setGain(float theGain) {
		_cGain = theGain;
	}
	

	@Override
	public void update(float theDeltaTime) {

		// init step, calculate Ex, Ey, Et from current and last input frame
		//_myBlurInputStage.update(theDeltaTime);
		
		_myGraphics.clear();
		
		_myShaderStage1.start();
		_myGraphics.texture (0, _myInput);	
		_myGraphics.texture (1, _myLastInput.attachment(0));	
		_myShaderStage1.uniform1i ("IN0", 0);
		_myShaderStage1.uniform1i ("IN1", 1);
		_myShaderStage1.uniform1f ("gain", _cGain);
		
		_myOutputStage1.draw();
		
		_myShaderStage1.end();
		_myGraphics.noTexture();

		// initalize output to zeros
		outTmp.clear();
		
		// iterate to find v, u
		for (int i=0; i<nSteps; i++) {

			_myGraphics.clear();
			_myShaderStage2.start();
			
			_myGraphics.texture (0, outTmp.attachment(0));	
			_myGraphics.texture (1, _myOutputStage1.attachment(0));	
			_myShaderStage2.uniform1i ("UV", 0);
			_myShaderStage2.uniform1i ("E_xyt", 1);
			_myOutputStage2.draw();
			
			_myShaderStage2.end();
			_myGraphics.noTexture();
			
			outTmp.beginDraw();
			_myGraphics.image(_myOutputStage2.attachment(0), 0, 0);
			outTmp.endDraw();
		}
		
		_myShaderStage3.start();
		_myGraphics.texture (0, _myOutputStage2.attachment(0));	
		_myShaderStage3.uniform1i ("IN0", 0);
		_myShaderStage3.uniform1f ("offset", 0.5f);
		_myShaderStage3.uniform1f ("gain", 0.5f);
		_myOutputStage3.draw();
		_myShaderStage3.end();
		_myGraphics.noTexture();
		
		// keep input for next update call
		_myLastInput.beginDraw();
		_myGraphics.image (_myInput, 0, 0);
		_myLastInput.endDraw();
	}
}
