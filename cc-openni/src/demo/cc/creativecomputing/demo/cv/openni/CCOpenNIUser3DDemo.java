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
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.math.util.CCArcball;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUser3DDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	private CCOpenNIDepthGenerator _myDepthGenerator;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");

		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		
		_myOpenNI.start();
	}
	
	@Override
	public void update(float theDeltaTime) {
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		
		_myArcball.draw(g);

		g.color(255);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.drawSkeleton(g);
//			myUser.drawOrientations(g, 50);
//			myUser.drawVelocities(g,500);
		}
		
		g.popMatrix();
		
		g.text(frameRate,0,0);
		g.image(_myDepthGenerator.texture(),-width/2, 0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIUser3DDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(60);
		myManager.start();
	}
}
