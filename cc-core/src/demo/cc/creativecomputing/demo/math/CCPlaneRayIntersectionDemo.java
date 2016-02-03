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
package cc.creativecomputing.demo.math;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCPlaneRayIntersectionDemo extends CCApp {

	@CCControl(name = "norm x", min = -1, max = 1)
	private static float _cNormX = 0;

	@CCControl(name = "norm y", min = -1, max = 1)
	private static float _cNormY = 0;

	@CCControl(name = "norm z", min = -1, max = 1)
	private static float _cNormZ = 0;

	@CCControl(name = "planeConstant", min = -400, max = 400)
	private static float _cPlaneConstant = 0;
	
	private CCPlane3f _myPlane;
	private CCArcball _myArcball;
	
	private List<CCRay3f> _myRays = new ArrayList<CCRay3f>();
	private List<CCVector3f> _myIntersections = new ArrayList<CCVector3f>();
	
	@Override
	public void setup() {
		addControls("app", "app", this);
		_myPlane = new CCPlane3f();
		_myArcball = new CCArcball(this);
		
		for(int i = 0; i < 20;i++){
			_myRays.add(
				new CCRay3f(
					new CCVector3f(CCMath.random(-500,500), 300, CCMath.random(-500,500)), 
					new CCVector3f(0, -1,0)
				)
			);
		}
		
		g.pointSize(3);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myPlane.normal().set(_cNormX, _cNormY, _cNormZ).normalize();
		_myPlane.constant(_cPlaneConstant);
		
		_myIntersections.clear();
		for(CCRay3f myRay:_myRays){
			CCVector3f myIntersection = _myPlane.intersection(myRay);
			if(myIntersection != null) _myIntersections.add(myIntersection);
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		g.color(255,100);
		_myPlane.draw(g);
		for(CCRay3f myRay:_myRays){
			myRay.draw(g);
		}
		
		g.clearDepthBuffer();
		g.color(255,0,0);
		for(CCVector3f myIntersection:_myIntersections){
			g.point(myIntersection);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPlaneRayIntersectionDemo.class);
		myManager.settings().size(1200, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

