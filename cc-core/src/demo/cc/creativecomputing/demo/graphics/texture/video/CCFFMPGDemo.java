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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.texture.video.CCFFMPGMovie;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCFFMPGDemo extends CCApp {
	
	private CCFFMPGMovie _myData;
	private CCVideoTexture<CCFFMPGMovie> _myTexture;

	@Override
	public void setup() {
		
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120116_spline2_fine2_1356x136_jpg.mov"));
		_myData = new CCFFMPGMovie(this, CCIOUtil.dataPath("demo/videos/kaki.mov"));//
//		_myData = new CCGStreamerMovie(this, "http://cabspotting.org/movies/lines-sf4hr.mpg");
//		_myData.loop();
		_myData.printInfo();
		_myTexture = new CCVideoTexture<CCFFMPGMovie>(_myData);
	}
	
	float _myTime = 0;
	float _myNoise = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime; 
		_myNoise = CCMath.noise(_myTime * 0.1f);
	}
	
	@Override
	public void mousePressed(CCMouseEvent theMouseEvent) {
		float myTime = theMouseEvent.x() / (float)width * _myData.duration();
		_myData.time(myTime);
	}
	
	@Override
	public void mouseDragged(CCMouseEvent theMouseEvent) {
		float myTime = theMouseEvent.x() / (float)width * _myData.duration();
		_myData.time(myTime);
	}
	
	private boolean _myLoop = false;
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_S:
			_myData.stop();
			break;
		case VK_R:
			_myData.play();
			break;
		case VK_P:
			_myData.pause();
			break;
		case VK_L:
			_myLoop = !_myLoop;
			_myData.loop(_myLoop);
			break;
		}
	}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
		
//		g.texture(_myTexture);
//		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(-width/2, -height/2, -0.5f * _myNoise, -0.5f *_myNoise);
//		g.vertex( width/2, -height/2, 1.5f * _myNoise, -0.5f *_myNoise);
//		g.vertex( width/2,  height/2, 1.5f * _myNoise, 1.5f *_myNoise);
//		g.vertex(-width/2,  height/2, -0.5f * _myNoise, 1.5f *_myNoise);
//		g.endShape();
//		g.noTexture();
		g.color(255);
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height()/2);
		
		g.color(255,0,0);
		g.line(-g.width / 2, 0, g.width/2, 0);
		float myPos = CCMath.map(_myData.time(), 0, _myData.duration(), -g.width / 2, g.width / 2);
		
		g.line(myPos, -5, myPos, 5);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFFMPGDemo.class);
		myManager.settings().size(320 * 2, 212 * 2);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

