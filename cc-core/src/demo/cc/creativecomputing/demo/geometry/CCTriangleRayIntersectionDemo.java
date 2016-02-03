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
package cc.creativecomputing.demo.geometry;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.geometry.CCTriangleIntersector;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCTriangleRayIntersectionDemo extends CCApp {
	
	private CCTriangleIntersector _myTriangleIntersector;
	
	@CCControl(name = "ray x", min = -1, max = 1)
	private float _cRayDirX = 0;
	@CCControl(name = "ray y", min = -1, max = 1)
	private float _cRayDirY = 0;
	@CCControl(name = "ray z", min = -1, max = 1)
	private float _cRayDirZ = 0;
	
	private class CCVectorControl{
		@CCControl(name = "x", min = -500, max = 500)
		private float _cX = 0;
		@CCControl(name = "y", min = -500, max = 500)
		private float _cY = 0;
		@CCControl(name = "z", min = -500, max = 500)
		private float _cZ = 0;
	}
	
	@CCControl(name="vec a")
	private CCVectorControl _cControlA = new CCVectorControl();
	@CCControl(name="vec b")
	private CCVectorControl _cControlB = new CCVectorControl();
	@CCControl(name="vec c")
	private CCVectorControl _cControlC = new CCVectorControl();
	
	private CCRay3f _myRay;
	
	private CCVector3f _myA;
	private CCVector3f _myB;
	private CCVector3f _myC;
	
	private CCVector3f _myIntersection = null;
	
	private CCPlane3f _myFloorPlane;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myTriangleIntersector = new CCTriangleIntersector();
		_myRay = new CCRay3f(new CCVector3f(), new CCVector3f(1f,0,0));
		
		_myA = new CCVector3f();
		_myB = new CCVector3f();
		_myC = new CCVector3f();
		
		addControls("app", "app", this);
		
		_myFloorPlane = new CCPlane3f(new CCVector3f(), new CCVector3f(0,1,0));
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myRay.direction().set(_cRayDirX, _cRayDirY, _cRayDirZ).normalize();
		
		_myA.set(_cControlA._cX, _cControlA._cY, _cControlA._cZ);
		_myB.set(_cControlB._cX, _cControlB._cY, _cControlB._cZ);
		_myC.set(_cControlC._cX, _cControlC._cY, _cControlC._cZ);
		
		CCTriangleIntersector.CCTriangleIntersectionData myIntersectResult = _myTriangleIntersector.intersectsRay(_myRay, _myA, _myB, _myC);
		if(myIntersectResult == null) {
			_myIntersection = null;
		}else {
			_myIntersection = _myRay.pointAtDistance(myIntersectResult.t);
		}
	}

	@Override
	public void draw() {
		g.clear();
		
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		_myArcball.draw(g);
		
		g.color(1f,0.25f);
		_myFloorPlane.draw(g);
		
		g.color(1f,1f);
		g.line(
			_myRay.origin().x, 
			_myRay.origin().y, 
			_myRay.origin().z,
			_myRay.origin().x + _myRay.direction().x * 100,
			_myRay.origin().y + _myRay.direction().y * 100,
			_myRay.origin().z + _myRay.direction().z * 100
		);
		
		g.color(1f,0.25f);
		g.line(
			_myRay.origin().x, 
			_myRay.origin().y, 
			_myRay.origin().z,
			_myRay.origin().x + _myRay.direction().x * 1000,
			_myRay.origin().y + _myRay.direction().y * 1000,
			_myRay.origin().z + _myRay.direction().z * 1000
		);
		
		g.color(1f,1f);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myA);
		g.vertex(_myB);
		g.vertex(_myC);
		g.endShape();
		
		g.color(1f,0.25f);
		g.beginShape(CCDrawMode.TRIANGLES);
		g.vertex(_myA);
		g.vertex(_myB);
		g.vertex(_myC);
		g.endShape();

		if(_myIntersection != null) {
			g.color(255, 0, 0);
			g.ellipse(_myIntersection, 10);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTriangleRayIntersectionDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

