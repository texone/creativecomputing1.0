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
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

public class CCPotentialFilter extends CCImageFilterFIR{
	
	private CCGLSLShader _myShader;
	
	@CCControl(name = "a0", min = -1, max = 1)
	private float _cA0 = 0;
	
	@CCControl(name = "a1", min = -1, max = 1)
	private float _cA1 = 0;
	
	@CCControl(name = "b1", min = -1, max = 1)
	private float _cB1 = 0;
	
	@CCControl(name = "b2", min = -1, max = 1)
	private float _cB2 = 0;
	
	@CCControl(name = "out gain", min = 0f, max = 5f)
	private float _cGain = 0;
	
	@CCControl(name = "out threshold", min = 0f, max = 1f)
	private float _cThreshold = 0;
	
	
	
	public CCPotentialFilter (CCGraphics theGraphics, CCTexture2D theInput) {
		super (theGraphics, theInput, 3);
		_myShader = new CCGLSLShader (CCIOUtil.classPath(this, "shader/potfield_vp.glsl"),CCIOUtil.classPath(this, "shader/potfield_fp.glsl"));
		_myShader.load();	
	}
	
	public void update (float theDeltaTime) {
		
		_myInput.pushInput (_myGraphics, _myLatestInput);
		_myShader.start();
		_myGraphics.texture (0, _myInput.getData(0).attachment(0));	
		_myGraphics.texture (1, _myInput.getData(1).attachment(0));	
		_myGraphics.texture (2, _myOutput.getData(0).attachment(0));	
		_myGraphics.texture (3, _myOutput.getData(1).attachment(0));	
		
		
		_myShader.uniform1i ("IN0", 0);
		_myShader.uniform1i ("IN1", 1);
		_myShader.uniform1i ("OUT1", 2);
		_myShader.uniform1i ("OUT2", 3);

		_myShader.uniform1f ("a0", _cA0);
		_myShader.uniform1f ("a1", _cA1);
		_myShader.uniform1f ("b1", _cB1);
		_myShader.uniform1f ("b2", _cB2);
		_myShader.uniform1f ("gain", _cGain);
		_myShader.uniform1f ("thresh", _cThreshold);
		
		
		
		_myOutput.rShift();
		_myOutput.getData(0).draw();
		
		_myShader.end();
		_myGraphics.noTexture();
	}
}
