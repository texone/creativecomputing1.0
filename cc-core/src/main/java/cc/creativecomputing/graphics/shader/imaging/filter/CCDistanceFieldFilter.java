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

public class CCDistanceFieldFilter extends CCImageFilter {

	CCGLSLShader   _myShaderStage1;
	CCGLSLShader   _myShaderStage2;
	CCGLSLShader   _myShaderStage3;
	
	
	CCShaderBuffer _myOutputStage1;
	CCShaderBuffer _myOutputStage2;
	
	CCBlurFilter _myBlurFilter;
	
	
	CCTexture2D    _myInput;
	
	@CCControl(name = "thresh", min = 0f, max = 1f)
	private float _cThresh = 1;
	
	@CCControl(name = "offset", min = 0f, max = 1f)
	private float _cOffset = 0f;
	
	@CCControl(name = "iterations", min = 1, max = 200)
	private int _cNSteps = 1;
	
	
	public CCDistanceFieldFilter(CCGraphics theGraphics, CCTexture2D theInput) {
		super(theGraphics, theInput);
		
		//_myBlurFilter   = new CCBlurFilter (theGraphics, theInput, 10);

		
		_myOutputStage1 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage2 = new CCShaderBuffer(theInput.width(), theInput.height());
		
		_myShaderStage1 = new CCGLSLShader (
			CCIOUtil.classPath(this, "shader/distanceFieldInit_vp.glsl"), 
			CCIOUtil.classPath(this, "shader/distanceFieldInit_fp.glsl")
		);
		_myShaderStage1.load();
	
		_myShaderStage2 = new CCGLSLShader (
			CCIOUtil.classPath(this, "shader/filter/distanceFieldPropagate_vp.glsl"), 
			CCIOUtil.classPath(this, "shader/imaging/filter/distanceFieldPropagate_fp.glsl")
		);
		_myShaderStage2.load();
	
	//	_myShaderStage3 = new CCGLSLShader ("imaging/filter/maskFilter_vp.glsl", "imaging/filter/maskFilter_fp.glsl");
	//	_myShaderStage3.load();
		
		_myInput = theInput;
	}

	@Override
	public CCTexture2D output() {
		return _myOutputStage1.attachment(0);
	}

	public CCTexture2D input() {
		return _myInput;
	}
	
	@Override
	public void update(float theDeltaTime) {
		
		_myBlurFilter.update(theDeltaTime);
		
		// 1st stage init gradient
		_myGraphics.clear();
		_myShaderStage1.start();
		_myGraphics.texture(0, _myBlurFilter.output());
		
		_myShaderStage1.uniform1i ("width",  _myOutputStage1.width());
		_myShaderStage1.uniform1i ("height", _myOutputStage1.height());
		
		_myOutputStage1.draw();
		
		_myShaderStage1.end();
		_myGraphics.noTexture();
		
		
		_myGraphics.clear();
		
		/*
		_myShaderStage3.start();
		_myGraphics.texture(0, _myOutputStage1.attachment(0));
		_myGraphics.texture(1, _myInput);
		_myShaderStage3.uniform1i ("Field", 0);
		_myShaderStage3.uniform1i ("Mask", 1);
		
		_myShaderStage3.uniform1f ("thresh",  _cThresh);
		_myShaderStage3.uniform1f ("offset", _cOffset);
		
		_myOutputStage2.draw();
		
		_myShaderStage3.end();
		_myGraphics.noTexture();
		*/
		
	
		/*
		// propagate thru whole texture
		for (int i=0; i<_cNSteps; i++) {
			_myShaderStage2.start();
			_myGraphics.texture (0, _myOutputStage1.attachment(0));	
					
			_myShaderStage2.uniform1i ("Field", 0);
			
			_myOutputStage2.draw();
			
			_myShaderStage2.end();
			_myGraphics.noTexture();
			
			_myOutputStage1.beginDraw();
			_myGraphics.image(_myOutputStage2.attachment(0), 0, 0);
			_myOutputStage1.endDraw();
		}
		*/
	}
}
