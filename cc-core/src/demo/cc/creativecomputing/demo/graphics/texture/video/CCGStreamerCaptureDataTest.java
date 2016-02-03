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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.video.CCGStreamerCapture;
import cc.creativecomputing.graphics.texture.video.CCGStreamerCapture.CCGStreamerCaptureResolution;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;

public class CCGStreamerCaptureDataTest extends CCApp {
	
	private CCGStreamerCapture _myData;
	private CCVideoTexture<CCGStreamerCapture> _myTexture;

	@Override
	public void setup() {
		for(String myDevice : CCGStreamerCapture.list()) {
			CCLog.info(myDevice);
		}
		
		_myData = new CCGStreamerCapture(this, 640, 480, 30);
		_myData.start();
		
		for(CCGStreamerCaptureResolution myResolution:_myData.resolutions()) {
			CCLog.info("RES");
			CCLog.info(myResolution.width+":"+myResolution.height);
			CCLog.info(myResolution.fps);
		}
		
		_myTexture = new CCVideoTexture<CCGStreamerCapture>(_myData);
	}
	
	float _myTime = 0;
	float _myNoise = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime; 
		_myNoise = CCMath.noise(_myTime * 0.1f);
	}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
//		g.blend(CCBlendMode.ADD);
		g.texture(_myTexture);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-width/2, -height/2, -0.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2, -height/2, 1.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2,  height/2, 1.5f * _myNoise, 1.5f *_myNoise);
		g.vertex(-width/2,  height/2, -0.5f * _myNoise, 1.5f *_myNoise);
		g.endShape();
		g.noTexture();
		
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height()/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGStreamerCaptureDataTest.class);
		myManager.settings().size(1640, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

