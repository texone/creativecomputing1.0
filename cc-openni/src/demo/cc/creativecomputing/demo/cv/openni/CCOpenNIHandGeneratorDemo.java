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
import cc.creativecomputing.cv.openni.CCOpenNIHand;
import cc.creativecomputing.cv.openni.CCOpenNIHandGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

/**
 * This demos shows how to use the gesture/hand generator. 
 * It's not the most reliable yet, a two hands example will follow
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIHandGeneratorDemo extends CCApp {
	
	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;

	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	private CCOpenNIHandGenerator _myHandGenerator;
	private CCOpenNIRenderer _myRenderer;

	public void setup() {
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_mySceneAnalyzer = _myOpenNI.createSceneAnalyzer();
		_myHandGenerator = _myOpenNI.createHandGenerator();
		_myHandGenerator.historySize(30);
		
		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height());
	}

	public void draw() {
		g.clear();

		_myArcball.draw(g);

		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		g.pushMatrix();
		g.applyMatrix(_myOpenNI.transformationMatrix());
		_myRenderer.drawSceneMesh(g, _myDepthGenerator.texture(), _mySceneAnalyzer.texture());
		g.popMatrix();
		
		g.color(255, 0, 0, 200);
		
		for(CCOpenNIHand myHand:_myHandGenerator.hands()) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (CCVector3f myPosition : myHand.history()) {
				g.vertex(myPosition);
			}
			g.endShape();
			
			g.color(255, 0, 0);
			g.strokeWeight(4);
			g.point(myHand.position());
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIHandGeneratorDemo.class);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}
