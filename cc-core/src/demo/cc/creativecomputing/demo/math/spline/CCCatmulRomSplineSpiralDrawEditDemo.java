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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.math.spline.CCSplineEditor;
import cc.creativecomputing.math.util.CCArcball;

public class CCCatmulRomSplineSpiralDrawEditDemo extends CCApp {
	
	private CCSplineEditor _mySplineEditor;
	private CCSpline _mySpline;
	
	@CCControl(name = "radius", min = 0, max = 200)
	private float _cRadius = 0;
	
	@CCControl(name = "frequency", min = 0, max = 200)
	private float _cFrequency = 0;

	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		CCMath.randomSeed(0);
		
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		_mySplineEditor = new CCSplineEditor(this, _mySpline, "demo/math/spline.xml");
		
		addControls("app", "app", this);
		g.pointSize(8);
		
		_myArcball = new CCArcball(this);
	}
	
	

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_mySplineEditor.draw(g);
		if(_mySpline.points().size() < 4)return;
		
		g.color(255,100);
		g.beginShape(CCDrawMode.LINES);
		
		
		for(int i = 0; i <= 100; i++){
			float myBlend = i / 100f;
			
//			CCVector3f myLastPoint1 =  _mySpline.interpolate(myBlend == 1 ? 0.9f : myBlend - 0.1f);
//			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
//			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.1f : myBlend + 0.1f);
//			CCVector3f myNormal = CCVecMath.normal(myLastPoint1, myLastPoint2, myLastPoint3);
			
			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.01f : myBlend + 0.01f);
			
			CCVector2f myDir = new CCVector2f(myLastPoint3.x - myLastPoint2.x, myLastPoint3.y - myLastPoint2.y);
			myDir = myDir.cross().normalize();
			
			CCVector3f myPoint1 = myLastPoint2.clone().add(myDir.clone().scale(-10));
			CCVector3f myPoint2 = myLastPoint2.clone().add(myDir.clone().scale(10));
			g.vertex(myPoint1);
			g.vertex(myPoint2);
		}
			
		
		g.endShape();
		
		g.color(255);
		g.beginShape(CCDrawMode.LINE_STRIP);
		
		float myMaxPoints = 100 * _mySpline.points().size();
		for(int i = 0; i <= myMaxPoints; i++){
			float myBlend = i / myMaxPoints;
			
//			CCVector3f myLastPoint1 =  _mySpline.interpolate(myBlend == 1 ? 0.9f : myBlend - 0.1f);
//			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
//			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.1f : myBlend + 0.1f);
//			CCVector3f myNormal = CCVecMath.normal(myLastPoint1, myLastPoint2, myLastPoint3);
			
			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.01f : myBlend + 0.01f);
			
			CCVector2f myDir = new CCVector2f(myLastPoint3.x - myLastPoint2.x, myLastPoint3.y - myLastPoint2.y);
			myDir = myDir.cross().normalize();
			
			float myAngle = myBlend * CCMath.TWO_PI * _cFrequency;
			
			CCVector3f myX = new CCVector3f(myDir).scale(CCMath.sin(myAngle) * _cRadius);
			CCVector3f myY = new CCVector3f(0,0,1).scale(CCMath.cos(myAngle) * _cRadius);
			
			CCVector3f myPoint1 = myLastPoint2.clone().add(myX).add(myY);
			g.vertex(myPoint1);
		}
			
		
		g.endShape();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCAbstractGraphicsApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_L:
			_mySplineEditor.loadSplines(CCIOUtil.selectInput());
			break;
		case VK_S:
			_mySplineEditor.saveSplines(CCIOUtil.selectOutput());
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCatmulRomSplineSpiralDrawEditDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

