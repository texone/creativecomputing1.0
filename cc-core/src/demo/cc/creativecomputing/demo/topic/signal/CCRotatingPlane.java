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
package cc.creativecomputing.demo.topic.signal;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCRotatingPlane extends CCApp {
	
private CCPlane3f _myPlane;
	
	@CCControl(name = "distance", min = -0.5f, max = 0.5f, external = true)
	public float _cDistance = 0;
	
	@CCControl(name = "rotation", min = 0, max = 360, external = true)
	public float _cRotation = 0;
	
	@CCControl(name = "rotation2", min = 0, max = 360 * 5, external = true)
	public float _cRotation2 = 0;
	
	@CCControl(name = "angle", min = -60, max = 60, external = true)
	public float _cAngle = 0;
	
	@CCControl(name = "x", min = -600, max = 600, external = true)
	public float _cX = 0;
	
	@CCControl(name = "y", min = -600, max = 600, external = true)
	public float _cY = 0;
	
	@CCControl(name = "z", min = -600, max = 600, external = true)
	public float _cZ = 0;
	
	@CCControl(name = "angleAdd", min = -60, max = 60, external = true)
	public float _cAngleAdd = 0;
	
	//@CCControl(name = "speedMod", min = -1, max = 1, external = true)
	public float _cSpeedMod = 0f;
	
	@CCControl(name = "mirror")
	public boolean _cMirror = false;
	
	@CCControl(name = "double")
	public boolean _cDouble = false;
	
//	@CCControl(name = "rotate x", min = -1, max = 1, external = true)
	public float _cRotateX = 0;
	
	private CCArcball _myArcball;

	private CCMatrix4f _myMatrix;
	private CCVector3f _myPoint1 = new CCVector3f(0,0,0);
	private CCVector3f _myPoint2 = new CCVector3f(1,0,0);
	private CCVector3f _myPoint3 = new CCVector3f(0,0,1);

	@Override
	public void setup() {
		addControls("app", "app", this);
		_myPlane = new CCPlane3f();
		_myPlane.drawScale(100);
		_myMatrix = new CCMatrix4f();
		_myArcball = new CCArcball(this);
	}
	

	@Override
	public void update(final float theDeltaTime) {
		
		_myPoint1 = new CCVector3f(0,0,0);
		_myPoint2 = new CCVector3f(100,0,0);
		_myPoint3 = new CCVector3f(0,0,100);
		
		
		_myMatrix.reset();
		
		float myAngle = CCMath.radians(_cRotation + _cRotation2);
		float mySineMod = CCMath.cos(myAngle * 2 + CCMath.PI);
		float mySineBlend = (mySineMod + 1) / 2;
		float mySpeedMod = CCMath.sin(myAngle * 2);
		_myMatrix.translate(_cX, _cY, _cZ);
		_myMatrix.rotateY(myAngle + mySpeedMod * _cSpeedMod);
		_myMatrix.rotateX(CCMath.radians(_cAngle + _cAngleAdd * mySineBlend));
		_myMatrix.transform(_myPoint1);
		_myMatrix.transform(_myPoint2);
		_myMatrix.transform(_myPoint3);
		
		_myPlane.setPlanePoints(_myPoint1, _myPoint2, _myPoint3);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		g.pushMatrix();
		g.applyMatrix(_myMatrix);

		g.color(255,100);
		g.pushMatrix();
		g.rotateX(90);
		g.rect(-100,-100,200,200);
		g.popMatrix();
		g.popMatrix();
		
		_myPlane.draw(g);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRotatingPlane.class);
		myManager.settings().size(900, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

