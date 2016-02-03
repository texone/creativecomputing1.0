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
package cc.creativecomputing.demo.graphics.shader.filteredlines;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * TODO fix this and use geometry shader
 * @author christianriekoff
 *
 */
public class CCGPULineAntiAliasing{

	private CCVector4f[] _myWeights = new CCVector4f[8];
	
	private int[] _myIndices = new int[18];
	
	private CCTexture2D _myFilterTexture;
	
	private CCCGShader _myLineShader;
	private CGparameter _myRadiusParameter;
	private CGparameter _myAspectParameter;
	private CGparameter _myFilterTextureParameter;
	
	private CCApp _myApp;
	
	public CCGPULineAntiAliasing(final CCApp theApp) {
		_myApp = theApp;
		_myFilterTexture = createFilterTexture(100);
		
		_myLineShader = new CCCGShader(
			CCIOUtil.classPath(this, "lineantialiasing.vp"),
			CCIOUtil.classPath(this, "lineantialiasing.fp")
		);
		_myRadiusParameter = _myLineShader.vertexParameter("radius");
		_myAspectParameter = _myLineShader.vertexParameter("aspect");
		_myFilterTextureParameter = _myLineShader.fragmentParameter("filterTexture");
		_myLineShader.load();
		_myLineShader.parameter(_myAspectParameter, (float)_myApp.width/ _myApp.height);
		_myLineShader.texture(_myFilterTextureParameter, _myFilterTexture.id());
		
		_myWeights[0] = new CCVector4f(1.0f, 0.0f, -1.0f, -1.0f );
        _myWeights[1 ] = new CCVector4f(1.0f, 0.0f, -1.0f,  1.0f );
        _myWeights[2 ] = new CCVector4f(1.0f, 0.0f,  0.0f, -1.0f );
        _myWeights[3 ] = new CCVector4f(1.0f, 0.0f,  0.0f,  1.0f );
        _myWeights[4 ] = new CCVector4f(0.0f, 1.0f,  0.0f, -1.0f );
        _myWeights[5 ] = new CCVector4f(0.0f, 1.0f,  0.0f,  1.0f );
        _myWeights[6 ] = new CCVector4f(0.0f, 1.0f,  1.0f, -1.0f );
        _myWeights[7 ] = new CCVector4f(0.0f, 1.0f,  1.0f,  1.0f );
        
        _myIndices[0] = 0;
        _myIndices[1] = 2;
        _myIndices[2] = 3;
        _myIndices[3] = 0;
        _myIndices[4] = 3;
        _myIndices[5] = 1;

        _myIndices[6] = 2;
        _myIndices[7] = 4;
        _myIndices[8] = 5;
        _myIndices[9] = 2;
        _myIndices[10] = 5;
        _myIndices[11] = 3;

        _myIndices[12] = 4;
        _myIndices[13] = 6;
        _myIndices[14] = 7;
        _myIndices[15] = 4;
        _myIndices[16] = 7;
        _myIndices[17] = 5;
	}
	
	/**
	 * Like the step function but provides a smooth transition from a to b
	 * using the cubic function 3x^2 - 2x^3.
	 * @param a
	 * @param b
	 * @param t
	 * @return
	 */
	private float smoothStep(float a, float b, float t) {
		if (t <= a)
			return 0;
		if (t >= b)
			return 1;
		t = (t - a) / (b - a); // normalize t to 0..1
		return t * t * (3 - 2 * t);
	}
	
	private CCTexture2D createFilterTexture(final int theTextureSize) {
		CCTextureData myData = new CCTextureData(theTextureSize,theTextureSize);
		
		// Fill in the filter texture
		for (int x = 0; x < theTextureSize; x++) {
			for (int y = 0; y < theTextureSize; y++) {
				float t = CCMath.sqrt(x * x + y * y) / theTextureSize;
				t = CCMath.min(t, 1.0f);
				t = CCMath.max(t, 0.0f);
				t = 1 - t;
				t = smoothStep(0.0f, 1.0f, t);
//				t = CCMath.pow(t, 1f);
				myData.setPixel(x,y, new CCColor(t));
			}
		}
		CCTexture2D myFilterTexture = new CCTexture2D(myData);
		myFilterTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	    return myFilterTexture;
	}
	
	public CCTexture2D filterTexture() {
		return _myFilterTexture;
	}
	
	public void radius(final float theRadius) {
		_myLineShader.parameter(_myRadiusParameter, theRadius / _myApp.width);
	}
	
	public void drawLine( CCVector3f p0, CCVector3f p1, float radius, float aspect ) {
	    for(int myIndex:_myIndices) {
	    	_myApp.g.textureCoords(0, p1);
	    	_myApp.g.textureCoords(1,_myWeights[myIndex]);
	    	_myApp.g.vertex(p0);
	    }
	}
	
	public void begin() {
		_myLineShader.start();
	}
	
	public void end() {
		_myLineShader.end();
	}
}
