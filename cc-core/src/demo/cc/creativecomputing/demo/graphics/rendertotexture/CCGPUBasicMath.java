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
package cc.creativecomputing.demo.graphics.rendertotexture;

import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Demo showing basic gpgpu programming, data is fed into a texture
 * and than used for calculation inside a shader
 * @author christianriekoff
 *
 */
public class CCGPUBasicMath extends CCApp {
	
	private CCShaderBuffer _myShaderTexture;
	private CCShaderBuffer _myInputTexture;
	
	private CCCGShader _myShader;
	private CGparameter _myInputTextureParameter;
	
	int texSize = 5;

	public void setup() {
		_myShader = new CCCGShader(null,"demo/gpu/math/basic.fp");
		_myShader.load();
		
		_myInputTextureParameter = _myShader.fragmentParameter("input");

		_myInputTexture = new CCShaderBuffer(32,4,texSize,texSize);
		
		FloatBuffer inputData = FloatBuffer.allocate(texSize*texSize*4);
	    for (int i=0; i<texSize*texSize; i++){
	    	inputData.put(i);
	    	inputData.put(i);
	    	inputData.put(i);
	    	inputData.put(i);
	    }
	    
	    inputData.rewind();
	    _myInputTexture.loadData(inputData);
	    

		_myShaderTexture = new CCShaderBuffer(32,4,4,texSize, texSize);
		_myShaderTexture.clear();

		// always deactivate blending otherwise pixels get messed up in calculations
		g.noBlend();
		_myShader.texture(_myInputTextureParameter, _myInputTexture.attachment(0).id());
		_myShader.start();
		_myShaderTexture.draw();
		_myShader.end();
		
		FloatBuffer outputData0 = _myShaderTexture.getData(0);
		FloatBuffer outputData1 = _myShaderTexture.getData(1);
		FloatBuffer outputData2 = _myShaderTexture.getData(2);
		FloatBuffer outputData3 = _myShaderTexture.getData(3);
			
		System.out.printf("input\toutput0\toutput1\toutput2\toutput3\n");
		    for (int i = 0; i < texSize * texSize * 4; i++)
		    	System.err.printf("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", inputData.get(), outputData0.get(), outputData1.get(), outputData2.get(), outputData3.get());
		
	}
	
	boolean first = true;

	public void draw() {

		g.clearColor(0);
		g.clear();
		
		
		g.color(255);
		g.image(_myShaderTexture.attachment(0), 0,0,200,200);
		g.image(_myInputTexture.attachment(0), -width/2, -height/2,100,100);

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUBasicMath.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
