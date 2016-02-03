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
package cc.creativecomputing.demo.cv.openni.depth;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNI.CCOpenNIListener;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;

public class CCOpenNIDepthGeneratorDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	

	private CCOpenNI _myOpenNI2;
	private CCOpenNIDepthGenerator _myDepthGenerator2;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
//		_myOpenNI.openFileRecording("Captured.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myOpenNI.start();

//		_myOpenNI2 = new CCOpenNI(this);
//		_myOpenNI2.openFileRecording("Captured_feet2.oni");
//		_myDepthGenerator2 = _myOpenNI2.createDepthGenerator();
//		_myOpenNI2.start();
		
		_myOpenNI.events().add(new CCOpenNIListener() {
			
			@Override
			public void lostDevice() {
				System.exit(0);
			}
		});
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myDepthGenerator.texture(),-640,-240);
//		g.image(_myDepthGenerator2.texture(),0,-240);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_S:
			CCScreenCapture.capture("export/frame" + frameCount+".png", width, height);
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIDepthGeneratorDemo.class);
		myManager.settings().size(1280, 480);
		myManager.start();
	}
}

