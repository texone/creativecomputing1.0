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
package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRecorder;
import cc.creativecomputing.events.CCKeyEvent;

public class CCOpenNIRecorderDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIRecorder _myRecorder;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
//		_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myRecorder = _myOpenNI.createRecorder();
		_myOpenNI.start();
	}

	@Override
	public void update(final float theDeltaTime) {
		_myRecorder.captureFrame();
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.image(_myDepthGenerator.texture(),-320,-240);
		g.color(255,0,0);
		if(_myRecorder.isCapturing())g.rect(0,0,20,20);
	}
	
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_R:
			if(_myRecorder.isCapturing()) {
				_myRecorder.stop();
			}else {
				_myRecorder.start();
			}
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIRecorderDemo.class);
		myManager.settings().size(640, 480);
		myManager.start();
	}
}

