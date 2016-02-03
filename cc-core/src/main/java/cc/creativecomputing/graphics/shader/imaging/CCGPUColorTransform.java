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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMatrix4f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUColorTransform extends CCCGShader{
	
	private CGparameter _myColorMatrixParameter;
	
//	private List<Float> _myMatrix = new ArrayList<Float>();
	
	private CCMatrix4f _myMatrix4f = new CCMatrix4f();

	public CCGPUColorTransform(final CCGraphics g) {
		super(null, CCIOUtil.classPath(CCGPUColorTransform.class,"imaging/colortransform.fp"));
		load();
		
		_myColorMatrixParameter = fragmentParameter("colorMatrix");
		matrix(_myColorMatrixParameter,_myMatrix4f);
	}
	
	public void reset() {
		_myMatrix4f.reset();
		matrix(_myColorMatrixParameter,_myMatrix4f);
	}
	
	public void contrast(final float theContrast) {
		_myMatrix4f.translate(theContrast, theContrast, theContrast);
		matrix(_myColorMatrixParameter,_myMatrix4f);
	}
	
	public void brightness(final float theBrightness) {
		_myMatrix4f.scale(theBrightness);
		matrix(_myColorMatrixParameter,_myMatrix4f);
	}
}
