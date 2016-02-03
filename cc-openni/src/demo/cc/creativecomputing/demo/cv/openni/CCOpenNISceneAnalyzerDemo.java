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
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.math.util.CCArcball;

/**
 * @author christianriekoff
 * 
 */
public class CCOpenNISceneAnalyzerDemo extends CCApp {

	private CCOpenNI _myOpenNI;

	private CCOpenNISceneAnalyzer _mySceneGenerator;
	
	private CCArcball _myArcball;

	public void setup() {
		_myArcball = new CCArcball(this);
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("brandspace_record1.oni");
		_mySceneGenerator = _myOpenNI.createSceneAnalyzer();
		_myOpenNI.start();
	}
	
	@Override
	public void update(float theDeltaTime) {
	}

	public void draw() {
		_myArcball.draw(g);
		g.clear();
		_mySceneGenerator.floorPlane().draw(g);
		g.image(_mySceneGenerator.texture(), 0, 0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISceneAnalyzerDemo.class);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}
