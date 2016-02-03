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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCGPUBloom {
	private CCRenderBuffer _myRenderTexture;
	private CCGLSLShader _myBloomShader;
	
	@CCControl(name = "highlightRange", min = 0, max = 1)
	private float _cHighlightColor;
	
	@CCControl(name = "highlightScale", min = 0, max = 10)
	private float _cHighlightScale;
	
	@CCControl(name = "highlightPow", min = 0, max = 10)
	private float _cHighlightPow;
	
	@CCControl(name = "debug bloom")
	private boolean _cDebugBloom = false;
	
	@CCControl(name = "apply bloom")
	private boolean _cApplyBloom = false;
	
	public final static float MAXIMUM_BLUR_RADIUS = 50;
	
	@CCControl(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	@CCControl(name = "blur radius 2", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius2 = MAXIMUM_BLUR_RADIUS;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	
	private int _myWidth;
	private int _myHeight;
	
	private CCGraphics g;
	
	public CCGPUBloom(CCGraphics theG, int theWidth, int theHeight) {
		g = theG;
		_myWidth = theWidth;
		_myHeight = theHeight;
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
		myAttributes.samples(8);
		_myRenderTexture = new CCRenderBuffer(g, myAttributes, _myWidth, _myHeight);
		
		_myBlur = new CCGPUSeperateGaussianBlur(10, _myWidth, _myHeight, 1);
		
		_myBloomShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/bloom_vert.glsl"), 
			CCIOUtil.classPath(this, "shader/bloom_frag.glsl")
		);
		_myBloomShader.load();
		
	}
	
	public CCGPUBloom(CCApp theApp) {
		this(theApp.g, theApp.width, theApp.height);
	}
	
	public void start() {
		if(!_cApplyBloom)return;
		_myRenderTexture.beginDraw();
	}
	
	public void endCapture(){
		if(!_cApplyBloom)return;
		_myRenderTexture.endDraw();
	}
	
	public void blur(){
		_myBlur.radius(_cBlurRadius);
		g.clearColor(0);
		g.clear();
		g.color(255);
		_myBlur.beginDraw(g);
		g.clear();
		g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
		_myBlur.endDraw(g);
	}
	
	public CCTexture2D blurredTexture() {
		return _myBlur.blurredTexture();
	}
	
	public void drawBlurredTexture() {
		g.image(_myBlur.blurredTexture(), -_myWidth/2, -_myHeight/2, _myWidth, _myHeight);
	}
	
	public void bloom(){
		g.clear();
		if(!_cDebugBloom) {
			g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
			
		}
		g.blend(CCBlendMode.ADD);
		_myBloomShader.start();
		_myBloomShader.uniform1i("texture", 0);
		_myBloomShader.uniform1f("highlightRange", _cHighlightColor);
		_myBloomShader.uniform1f("highlightScale", _cHighlightScale);
		_myBloomShader.uniform1f("highlightPow", _cHighlightPow);

		g.image(_myBlur.blurredTexture(), -_myWidth/2, -_myHeight/2, _myWidth, _myHeight);
		_myBloomShader.end();
		g.blend();
	}
	
	public void apply(){
		if(!_cApplyBloom)return;
		blur();
		
		
		
		bloom();
	}
	
	public void end() {
		endCapture();
		apply();
	}
	
	public void startBlur(CCGraphics g) {
		_myRenderTexture.beginDraw();
	}
	
	public void endBlur(CCGraphics g) {
		_myRenderTexture.endDraw();

		_myBlur.radius(_cBlurRadius2);
		g.color(255);
		_myBlur.beginDraw(g);
		g.clear();
		g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
		_myBlur.endDraw(g);
		
	}
}
