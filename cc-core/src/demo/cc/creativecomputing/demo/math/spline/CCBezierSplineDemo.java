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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCBezierSpline;

public class CCBezierSplineDemo extends CCApp {
	
	private CCBezierSpline _mySpline;
	
	@CCControl(name = "interpolation", min = 0, max = 1)
	private float _cInterpolation = 0;

	@Override
	public void setup() {
		CCMath.randomSeed(0);
		_mySpline = new CCBezierSpline(true);
		_mySpline.addPoint(randomPoint());
		_mySpline.addControlPoints(randomPoint(), randomPoint(), randomPoint());
		_mySpline.addControlPoints(randomPoint(), randomPoint());
		_mySpline.addControlPoints(randomPoint(), randomPoint());
		
		addControls("app", "app", this);
		g.pointSize(8);
	}
	
	private CCVector3f randomPoint(){
		return new CCVector3f(CCMath.random(-width/3, width/3), CCMath.random(-height/3, height/3));
	}
	
	@CCControl(name = "close")
	public void closeCurve(boolean theIsClosed){
		_mySpline.isClosed(theIsClosed);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		
		g.color(255);
		_mySpline.draw(g);
		
		
		g.beginShape(CCDrawMode.POINTS);
		g.color(255,0,0);
		g.vertex(_mySpline.points().get(0));
		g.color(255);
		for(int i = 1; i < _mySpline.points().size() - 1;i++){
			g.vertex(_mySpline.points().get(i));
		}
		g.color(0,0,255);
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 1));
		g.endShape();
		
		g.beginShape(CCDrawMode.LINES);
		g.color(255);
		g.vertex(_mySpline.points().get(0));
		g.vertex(_mySpline.points().get(1));
		for(int i = 3; i < _mySpline.points().size() - 1; i+=3){
			g.vertex(_mySpline.points().get(i-1));
			g.vertex(_mySpline.points().get(i));
			g.vertex(_mySpline.points().get(i));
			g.vertex(_mySpline.points().get(i+1));
		}
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 2));
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 1));
		g.color(0,0,255);
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 1));
		g.endShape();
		
		CCVector3f myInterploatedValue = _mySpline.interpolate(_cInterpolation);
		g.ellipse(myInterploatedValue, 10);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBezierSplineDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

