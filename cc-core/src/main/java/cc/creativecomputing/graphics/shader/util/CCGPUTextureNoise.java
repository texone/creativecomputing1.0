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
package cc.creativecomputing.graphics.shader.util;

import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCGPUTextureNoise {

private static CCGPUTextureNoise noise;
	
	public static void attachFragmentNoise(final CCCGShader theShader){
	    CGparameter mNoiseTextureParam = theShader.fragmentParameter("noiseTexture");
	    
	    if(noise == null) noise = new CCGPUTextureNoise();
	    
	    theShader.texture(mNoiseTextureParam, noise._myNoiseTexture.id());
	}
	
	public static void attachVertexNoise(final CCCGShader theShader){
	    CGparameter myNoiseTextureParam = theShader.vertexParameter("noiseTexture");
	    
	    if(noise == null) noise = new CCGPUTextureNoise();
	    
	    theShader.texture(myNoiseTextureParam, noise._myNoiseTexture.id());
	}
	
	private CCTexture2D _myNoiseTexture;
	
	private CCGPUTextureNoise() {
		_myNoiseTexture = new CCTexture2D(CCTextureIO.newTextureData(CCIOUtil.classPath(this,"util/noise.png")));
		_myNoiseTexture.wrap(CCTextureWrap.REPEAT);
		_myNoiseTexture.textureFilter(CCTextureFilter.LINEAR);
		
	}
}
