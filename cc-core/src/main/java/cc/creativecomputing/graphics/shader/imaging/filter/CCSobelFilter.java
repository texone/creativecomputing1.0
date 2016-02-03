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

public class CCSobelFilter extends CCImageFilter {

	private CCShaderBuffer _myOutput;
	private CCGLSLShader _myShader;
	
	@CCControl(name = "shift_value")
	private boolean _cShift = true;
	
	public CCSobelFilter(CCGraphics theGraphics, CCTexture2D theInput) {
		super(theGraphics, theInput);
		_myOutput = new CCShaderBuffer(32, 3, 2, theInput.width(), theInput.height());
		_myOutput.clear();
		
		_myShader = new CCGLSLShader (CCIOUtil.classPath(this, "shader/sobel_vp.glsl"), CCIOUtil.classPath(this, "shader/sobel_fp.glsl"));
		//_myShader = new CCGLSLShader (CCIOUtil.classPath (this, "sobel_vp.glsl"), CCIOUtil.classPath(this, "sobel_fp.glsl"));
		_myShader.load();
	}

	public CCTexture2D input() {
		return _myInput;
	}
	
	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
	
	public CCTexture2D outputBrightness() {
		return _myOutput.attachment(1);
	}

	@Override
	public void update(float theDeltaTime) {
		_myGraphics.texture(0, _myInput);	
		_myShader.start();
		_myShader.uniform1i ("IN0", 0);
		
		_myShader.uniform ("offset", _cShift);

		_myOutput.draw();
		_myShader.end();
		_myGraphics.noTexture();
	}
}
