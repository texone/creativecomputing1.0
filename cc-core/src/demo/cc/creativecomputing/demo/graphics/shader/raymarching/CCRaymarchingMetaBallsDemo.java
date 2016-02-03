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
package cc.creativecomputing.demo.graphics.shader.raymarching;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;

public class CCRaymarchingMetaBallsDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCTexture1D _myTexture;
	
	private CCShaderBuffer _myShaderBuffer;
	private CCMatrix4f _myCameraTransformation = new CCMatrix4f();
	private CCVector3f _myCameraPosition = new CCVector3f(0.0f, 0.0f, 5.0f);

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			null, 
			new String[] {
				CCIOUtil.classPath(this,"isocube.glsl"), 
				CCIOUtil.classPath(this,"raymarcher.glsl")
			}
		);
		_myShader.load();
		
		CCTextureData myData = new CCTextureData(5,1);
		myData.setPixel(0,0,CCColor.createFromInteger(0x0065356B));
		myData.setPixel(1,0,CCColor.createFromInteger(0x00AB434F));
		myData.setPixel(2,0,CCColor.createFromInteger(0x00C76347));
		myData.setPixel(3,0,CCColor.createFromInteger(0x00FFA24C));
		myData.setPixel(4,0,CCColor.createFromInteger(0x00519183));
		
		_myTexture = new CCTexture1D(myData);
		
		_myShaderBuffer = new CCShaderBuffer(width, height);
		
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		_myShaderBuffer.beginDraw();
		g.clear();
		g.texture(0,_myTexture);
		
		_myShader.start();
		_myShader.uniform1f("time", frameCount / 100f);
		_myShader.uniform2f("resolution", width, height);
		_myShader.uniform1i("sampler0",0);
		_myShader.uniformMatrix4f("cameraTransformation", _myCameraTransformation);
		_myShader.uniform3f("cameraPosition", _myCameraPosition);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(0.0f, 0.0f);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(width, 0.0f);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(width, height);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(0.0f, height);
        g.endShape();
        
        _myShader.end();
        _myShaderBuffer.endDraw();
        g.noTexture();
        
        g.clear();
        g.image(_myShaderBuffer.attachment(0), -width/2, -height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRaymarchingMetaBallsDemo.class);
		myManager.settings().size(800, 600);
		myManager.settings().vsync(true);
		myManager.start();
	}
}

