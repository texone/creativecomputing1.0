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
package cc.creativecomputing.graphics.shader.fx.refraction;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTextureCubeMap;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCGLSLRefractionShader extends CCGLSLShader{
	
	private CCGraphics _myGraphics;
	private CCTextureCubeMap _myCubeMap;
	
	@CCControl(name = "eta", min = 0, max = 2)
	private float _cEta;
	
	@CCControl(name = "fresnel power", min = 0, max = 10)
	private float _cFresnelPower;

	/**
	 * @param theVertexShaderFile
	 * @param theFragmentShaderFile
	 */
	public CCGLSLRefractionShader(CCGraphics theG, CCTextureCubeMap theCubeMap) {
		super(
			CCIOUtil.classPath(CCGLSLRefractionShader.class, "refract_vert.glsl"), 
			CCIOUtil.classPath(CCGLSLRefractionShader.class, "refract_frag.glsl")
		);
		load();
		_myGraphics = theG;
		_myCubeMap = theCubeMap;
	}

	public void cubeMap(CCTextureCubeMap theCubeMap) {
		_myCubeMap = theCubeMap;
	}
	
	private float f() {
		return ((1f - _cEta) * (1f - _cEta)) / ((1f + _cEta) * (1f + _cEta));
	}
	
	@Override
	public void start() {
		_myGraphics.texture(_myCubeMap);
		super.start();
		uniform1i("cubemap", 0);
		uniform1f("f", f());
		uniform1f("eta", _cEta);
		uniform1f("fresnelPower", _cFresnelPower);
	}
	
	@Override
	public void end() {
		super.end();
		_myGraphics.noTexture();
	}
}
