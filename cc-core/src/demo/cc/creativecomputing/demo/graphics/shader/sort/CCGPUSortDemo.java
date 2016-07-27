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
package cc.creativecomputing.demo.graphics.shader.sort;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.util.CCFormatUtil;

/**
 * 
 * @author christianriekoff
 * 
 */
@SuppressWarnings("unused")
public class CCGPUSortDemo extends CCApp {
	
	private CCShaderBuffer _myShaderTexture;
	private CCShaderBuffer _myTempShaderTexture;
	private CCGLSLShader _mySortHoriShader;
	private CCGLSLShader _mySortVertiShader;
	private CCGLSLShader _myInitShader;
	private CCGLSLShader _myDisplayShader;
	
	private CCTexture2D _myTexture;
	private CCGStreamerMovie _myData;
	private CCVideoTexture<CCGStreamerMovie> _myVideoTexture;
	
	@CCControl(name = "threshold", min = 0, max = 1)
	private float _cThreshold = 0;
	@CCControl(name = "red", min = 0, max = 1)
	private float _cRed = 0;
	@CCControl(name = "green", min = 0, max = 1)
	private float _cGreen = 0;
	@CCControl(name = "blue", min = 0, max = 1)
	private float _cBlue = 0;

	private CCSimplexNoise _myNoise;
	
	public void setup() {
		CCGraphics.debug();
		addControls("app", "app", this);
		
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/waltz.jpg"), CCTextureTarget.TEXTURE_RECT);
		_myTexture.textureFilter(CCTextureFilter.NEAREST);
//		
		
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/chris_bryan_films_phantom_reel_1280x720.mp4"));//
//		_myData.loop();
//		_myData.rate(theSpeed);
		
//		System.out.println(_myData.width() + ":"+_myData.height());
		
//		_myTexture = new CCTexture2D(1280, 720, CCTextureTarget.TEXTURE_RECT);
//		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		
		
//		_myData = new CCGStreamerCapture(this, 640, 480, 30);
//		_myData.start();
		
//		_myTexture = new CCVideoTexture<CCGStreamerMovie>(_myData, CCTextureTarget.TEXTURE_RECT, new CCTextureAttributes());
//		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		
		_mySortHoriShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "sort_vp.glsl"), 
			CCIOUtil.classPath(this, "sort_fp.glsl")
		);
		_mySortHoriShader.load();

		_mySortVertiShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "sort_vert_vp.glsl"), 
			CCIOUtil.classPath(this, "sort_vert_fp.glsl")
		);
		_mySortVertiShader.load();
		
		_myInitShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "init_vp.glsl"), 
			CCIOUtil.classPath(this, "init_fp.glsl")
		);
		_myInitShader.load();
		
		_myDisplayShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "draw_vp.glsl"), 
			CCIOUtil.classPath(this, "draw_fp.glsl")
		);
		_myDisplayShader.load();
		
		_myShaderTexture = new CCShaderBuffer(32, 3, _myTexture.width(), _myTexture.height(), CCTextureTarget.TEXTURE_RECT);
//		_myShaderTexture.mustFlipVertically(true);
		
		_myInitShader.start();
		_myShaderTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x < _myTexture.width(); x++) {
			for(float y = 0; y < _myTexture.height(); y++) {
				g.textureCoords(x + 0.5f, y + 0.5f , 0f);
//				g.color(1f);
				g.vertex(x,y);
//				System.out.println(x+":" +y);
			}
		}
		g.endShape();
		_myShaderTexture.endDraw();
		_myInitShader.end();

		g.clearColor(0);
		
		_myTempShaderTexture = new CCShaderBuffer(32, 3, _myTexture.width(), _myTexture.height(), CCTextureTarget.TEXTURE_RECT);
//		_myTempShaderTexture.mustFlipVertically(true);
		
		

		g.strokeWeight(3);
		
		_myNoise = new CCSimplexNoise();
//		g.perspective(95, width / (float) height, 10, 150000);
	}
	
	float myX = 0;
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {

//		_myTexture.data(_myData);
		
		g.texture(0,_myTexture);
		g.texture(1,_myShaderTexture.attachment(0));
		_mySortHoriShader.start();
		_mySortHoriShader.uniform1i("data", 0);
		_mySortHoriShader.uniform1i("lookUp", 1);
		_mySortHoriShader.uniform1f("threshold", _cThreshold) ;
		_mySortHoriShader.uniform1f("red", _cRed);
		_mySortHoriShader.uniform1f("green", _cGreen);
		_mySortHoriShader.uniform1f("blue", _cBlue);
		_mySortHoriShader.uniform1i("frameMod", frameCount %2);
		_myTempShaderTexture.draw();
		_mySortHoriShader.end();
		g.noTexture();
		
		CCShaderBuffer mySwap = _myTempShaderTexture;
		_myTempShaderTexture = _myShaderTexture;
		_myShaderTexture = mySwap;

		g.texture(0,_myTexture);
		g.texture(1,_myShaderTexture.attachment(0));
		_mySortVertiShader.start();
		_mySortVertiShader.uniform1i("data", 0);
		_mySortVertiShader.uniform1i("lookUp", 1);
		_mySortVertiShader.uniform1f("threshold", _cThreshold);
		_mySortVertiShader.uniform1f("red", _cRed);
		_mySortVertiShader.uniform1f("green", _cGreen);
		_mySortVertiShader.uniform1f("blue", _cBlue);
		_mySortVertiShader.uniform1i("frameMod", frameCount %2);
		_myTempShaderTexture.draw();
		_mySortVertiShader.end();
		g.noTexture();
		
		mySwap = _myTempShaderTexture;
		_myTempShaderTexture = _myShaderTexture;
		_myShaderTexture = mySwap;
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		g.translate(-width/2, -height/2);
		// set the scene pos
		g.texture(0,_myTexture);
		g.texture(1,_myShaderTexture.attachment(0));
		_myDisplayShader.start();
		_myDisplayShader.uniform1i("data", 0);
		_myDisplayShader.uniform1i("lookUp", 1);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0, _myTexture.height());
		g.vertex(0, 0);

		g.textureCoords(0, _myTexture.width(), _myTexture.height());
		g.vertex(_myTexture.width(), 0);

		g.textureCoords(0, _myTexture.width(), 0);
		g.vertex(_myTexture.width(), _myTexture.height());

		g.textureCoords(0, 0, 0);
		g.vertex(0, _myTexture.height());
		
		g.endShape();
		_myDisplayShader.end();
		g.noTexture();
		g.popMatrix();
		
		g.image(_myTexture, -width/2 + _myTexture.width(),-height/2);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCAbstractGraphicsApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_R:
			_myInitShader.start();
			_myShaderTexture.beginDraw();
			g.clearColor(0);
			g.clear();
			g.beginShape(CCDrawMode.POINTS);
			for(float x = 0; x < _myTexture.width(); x++) {
				for(float y = 0; y < _myTexture.height(); y++) {
					g.textureCoords(x + 0.5f, y + 0.5f , 0f);
//					g.color(1f);
					g.vertex(x,y);
//					System.out.println(x+":" +y);
				}
			}
			g.endShape();
			_myShaderTexture.endDraw();
			_myInitShader.end();
			
			_myInitShader.start();
			_myTempShaderTexture.beginDraw();
			g.clearColor(0);
			g.clear();
			g.beginShape(CCDrawMode.POINTS);
			for(float x = 0; x < _myTexture.width(); x++) {
				for(float y = 0; y < _myTexture.height(); y++) {
					g.textureCoords(x + 0.5f, y + 0.5f , 0f);
//					g.color(1f);
					g.vertex(x,y);
//					System.out.println(x+":" +y);
				}
			}
			g.endShape();
			_myTempShaderTexture.endDraw();
			_myInitShader.end();
			break;
		case VK_S:
			CCScreenCapture.capture("export/gpusort/"+CCFormatUtil.nf(frameCount, 5)+".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUSortDemo.class);
		myManager.settings().size(1920, 1000);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(2);
		myManager.start();
	}
}
