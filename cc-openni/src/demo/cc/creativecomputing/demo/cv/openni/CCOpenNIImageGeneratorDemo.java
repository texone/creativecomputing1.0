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
import cc.creativecomputing.cv.openni.CCOpenNIImageGenerator;

public class CCOpenNIImageGeneratorDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIImageGenerator _myImageGenerator;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myImageGenerator = _myOpenNI.imageGenerator();
		_myOpenNI.start();
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myImageGenerator.texture(),-320,-240);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIImageGeneratorDemo.class);
		myManager.settings().size(640, 480);
		myManager.start();
	}
}

