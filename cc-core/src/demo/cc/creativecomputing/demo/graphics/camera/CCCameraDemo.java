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
package cc.creativecomputing.demo.graphics.camera;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCCameraDemo extends CCApp {
	
	@CCControl(name = "camera near", min = 0, max = 10000)
	private float _cCameraNear = 10;
	
	@CCControl(name = "camera far", min = 0, max = 10000)
	private float _cCameraFar = 1000;
	
	@CCControl(name = "frustum offset x", min = -500, max = 500)
	private float _cFrustumOffsetX = 0;
	
	@CCControl(name = "frustum offset y", min = -500, max = 500)
	private float _cFrustumOffsetY = 0;
	
	@CCControl(name = "x rotation", min = -CCMath.HALF_PI, max = CCMath.HALF_PI)
	private float _cCameraXrotation = 0;
	
	@CCControl(name = "y rotation", min = 0, max = CCMath.TWO_PI)
	private float _cCameraYrotation = 0;
	
	@CCControl(name = "z rotation", min = 0, max = CCMath.TWO_PI)
	private float _cCameraZrotation = 0;
	
	@CCControl(name = "x", min = -500, max = 500)
	private float _cX = 0;
	
	@CCControl(name = "y", min = -500, max = 500)
	private float _cY = 0;
	
	@CCControl(name = "z", min = -500, max = 500)
	private float _cZ = 0;
	
	@CCControl(name = "draw camera")
	private boolean _cDrawCamera = false;
	
	
	@CCControl(name = "fov", min = 0, max = CCMath.PI)
	private float _cCameraFov = CCMath.radians(60);
	
	private CCCamera _myCamera;
	private CCFrustum _myFrustum;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myCamera = new CCCamera(g);
		_myFrustum = new CCFrustum(_myCamera);
		
		_myArcball = new CCArcball(this);
		
		addControls("camera", "camera", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myCamera.near(_cCameraNear);
		_myCamera.far(_cCameraFar);
		
		_myCamera.frustumOffset().x = _cFrustumOffsetX;
		_myCamera.frustumOffset().y = _cFrustumOffsetY;
		
		_myCamera.position().x = _cX;
		_myCamera.position().y = _cY;
		_myCamera.position().z = _cZ;
		
		_myCamera.xRotation(_cCameraXrotation);
		_myCamera.yRotation(_cCameraYrotation);
		_myCamera.zRotation(_cCameraZrotation);
		
		_myCamera.fov(_cCameraFov);
		
		_myFrustum.updateFromCamera();
	}

	@Override
	public void draw() {
		if(_cDrawCamera) {
			_myCamera.draw(g);
		}else {
			_myArcball.draw(g);
		}
		
		g.clear();
		g.polygonMode(CCPolygonMode.LINE);
		g.box(140);
		
		_myFrustum.drawLines(g);
		_myFrustum.drawNormals(g);
		_myFrustum.drawPoints(g);
		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCameraDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
