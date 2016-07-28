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
package cc.creativecomputing.simulation.particles.render;

import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUDisplayShader extends CCCGShader{
	
	CGparameter _myPointSizeParameter;
	CGparameter _myMinPointSizeParameter;
	CGparameter _myMaxPointSizeParameter;
	
	CGparameter _myTangHalfFovParameter;
	
	public CCGPUDisplayShader(final String theVertexFile, final String theFragmentFile) {
		super(theVertexFile, theFragmentFile);
		
		_myPointSizeParameter = vertexParameter("_uPointSize");
		
		_myTangHalfFovParameter = vertexParameter("tanHalfFov");
		load();
		
		pointSize(1f);
	}
	
	public CCGPUDisplayShader(){
		this(
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/points/display.vp"),
			CCIOUtil.classPath(CCGPUDisplayShader.class, "shader/points/display.fp")
		);
		
		
	}
	
	public void pointSize(final float thePointSize) {
		parameter(_myPointSizeParameter, thePointSize);
	}
	
	public void tangHalfFov(final float theTangHalfFov) {
		parameter(_myTangHalfFovParameter, theTangHalfFov);
	}
}
