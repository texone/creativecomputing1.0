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
package cc.creativecomputing.demo.graphics.shader.volume;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVolumeBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;

public class CCVolumeBufferDemo extends CCApp {
	
	@CCControl(name = "volume x", min = -500, max = 500)
	private float _cX = 0;
	
	@CCControl(name = "volume y", min = -500, max = 500)
	private float _cY = 0;
	
	@CCControl(name = "volume z", min = -500, max = 500)
	private float _cZ = 0;
	
	@CCControl(name = "volume width", min = 0, max = 500)
	private float _cWidth = 0;
	
	@CCControl(name = "volume height", min = 0, max = 500)
	private float _cHeight = 0;
	
	@CCControl(name = "volume depth", min = 0, max = 500)
	private float _cDepth = 0;
	
	@CCControl(name = "color scale", min = 0, max = 1)
	private float _cColorScale = 0;
	
	private CCVolumeBuffer _myVolumeBuffer;
	private CCGLSLShader _myVolumeFillShader;
	private CCGLSLShader _myVolumeDrawShader;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.format(CCPixelFormat.RGBA);
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA16F);
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
		_myVolumeBuffer = new CCVolumeBuffer(myAttributes, 32, 32, 32);
		
		_myVolumeFillShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "volumeFiller_vp.glsl"), 
			CCIOUtil.classPath(this, "volumeFiller_fp.glsl")
		);
		_myVolumeFillShader.load();
		
		_myVolumeDrawShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "volumeDrawer_vp.glsl"), 
			CCIOUtil.classPath(this, "volumeDrawer_fp.glsl")
		);
		_myVolumeDrawShader.load();
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myVolumeBuffer.beginDraw(g);
		_myVolumeFillShader.start();
		_myVolumeFillShader.uniform3f("volumePosition", _cX, _cY, _cZ);
		_myVolumeFillShader.uniform3f("volumeScale", _cWidth, _cHeight, _cDepth);
		_myVolumeFillShader.uniform3f("checkPosition", mouseX - width/2, height/2 - mouseY, 0);
		_myVolumeFillShader.uniform1f("colorScale", _cColorScale);
		for (int z = 0; z < _myVolumeBuffer.depth(); z++) {
			// attach texture slice to FBO
			// render
			_myVolumeFillShader.uniform1f("z", (float)z/_myVolumeBuffer.depth());
			_myVolumeBuffer.drawSlice(g, z);
		}
		_myVolumeFillShader.end();
		_myVolumeBuffer.endDraw(g);
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myVolumeBuffer,0,0);
		
		_myArcball.draw(g);
		g.texture(0,_myVolumeBuffer);
		_myVolumeDrawShader.start();
		_myVolumeDrawShader.uniform1i("cubeSampler", 0);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x < _myVolumeBuffer.width();x++) {
			for(float y = 0; y < _myVolumeBuffer.height();y++) {
				for(float z = 0; z < _myVolumeBuffer.depth();z++) {
					float myX = x / _myVolumeBuffer.width();
					float myY = y / _myVolumeBuffer.width();
					float myZ = z / _myVolumeBuffer.width();
					g.textureCoords(0, myX, myY, myZ);
					g.vertex(
						_cX + myX * _cWidth,
						_cY + myY * _cHeight,
						_cZ + myZ * _cDepth
					);
				}
			}
		}
		g.endShape();
		_myVolumeDrawShader.end();
		g.noTexture();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCVolumeBufferDemo.class);
		myManager.settings().size(1500, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

