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
package cc.creativecomputing.demo.math.spline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.math.spline.CCSplineEditor;

public class CCCatmulRomSplineDrawEditDemo extends CCApp {
	
	private CCSplineEditor _mySplineEditor;
	private CCSpline _mySpline;

	@Override
	public void setup() {
		CCMath.randomSeed(0);
		
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		_mySplineEditor = new CCSplineEditor(this, _mySpline);
		
		addControls("app", "app", _mySplineEditor);
		g.pointSize(8);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		
		_mySplineEditor.draw(g);
		if(_mySpline.points().size() < 4)return;
		
//		g.beginShape(CCDrawMode.LINES);
//		
//		
//		for(int i = 0; i <= 100; i++){
//			float myBlend = i / 100f;
//			
////			CCVector3f myLastPoint1 =  _mySpline.interpolate(myBlend == 1 ? 0.9f : myBlend - 0.1f);
////			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
////			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.1f : myBlend + 0.1f);
////			CCVector3f myNormal = CCVecMath.normal(myLastPoint1, myLastPoint2, myLastPoint3);
//			
//			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
//			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.01f : myBlend + 0.01f);
//			
//			CCVector2f myDir = new CCVector2f(myLastPoint3.x - myLastPoint2.x, myLastPoint3.y - myLastPoint2.y);
//			myDir = myDir.cross().normalize();
//			
//			CCVector3f myNormal = CCVecMath.normal(myLastPoint2.clone().add(0,1,0), myLastPoint2, myLastPoint3);
//			CCVector3f myPoint1 = myLastPoint2.clone().add(myDir.clone().scale(-10));
//			CCVector3f myPoint2 = myLastPoint2.clone().add(myDir.clone().scale(10));
//			g.vertex(myPoint1);
//			g.vertex(myPoint2);
//		}
//			
//		
//		g.endShape();
		
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCatmulRomSplineDrawEditDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

