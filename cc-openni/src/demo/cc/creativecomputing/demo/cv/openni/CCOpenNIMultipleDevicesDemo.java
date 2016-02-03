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

public class CCOpenNIMultipleDevicesDemo extends CCApp {
	
	private CCOpenNI _myOpenNI0;
	private CCOpenNI _myOpenNI1;
	private CCOpenNIDepthGenerator _myDepthGenerator0;
	private CCOpenNIDepthGenerator _myDepthGenerator1;

	@Override
	public void setup() {
		_myOpenNI0 = new CCOpenNI(this,0);
		_myOpenNI1 = new CCOpenNI(this,1);
//		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator0 = _myOpenNI0.createDepthGenerator();
		_myDepthGenerator1 = _myOpenNI1.createDepthGenerator();
		_myOpenNI0.start();
		_myOpenNI1.start();
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myDepthGenerator0.texture(),-640,-240);
		g.image(_myDepthGenerator1.texture(),   0,-240);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIMultipleDevicesDemo.class);
		myManager.settings().size(1280, 480);
		myManager.start();
	}
}

