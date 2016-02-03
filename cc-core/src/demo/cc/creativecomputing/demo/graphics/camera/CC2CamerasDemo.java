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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CC2CamerasDemo extends CCApp {
	
	private class CCDemoCamera{
		@CCControl(name = "camera near", min = 0, max = 10000)
		private float _cCameraNear = 10;
		
		@CCControl(name = "camera far", min = 0, max = 10000)
		private float _cCameraFar = 1000;
		
		@CCControl(name = "frustum offset x", min = -50, max = 50)
		private float _cFrustumOffsetX = 0;
		
		@CCControl(name = "frustum offset y", min = -50, max = 50)
		private float _cFrustumOffsetY = 0;
		
		@CCControl(name = "x rotation", min = -CCMath.HALF_PI, max = CCMath.HALF_PI)
		private float _cCameraXrotation = 0;
		
		@CCControl(name = "y rotation", min = 0, max = CCMath.TWO_PI)
		private float _cCameraYrotation = 0;
		
		@CCControl(name = "z rotation", min = 0, max = CCMath.TWO_PI)
		private float _cCameraZrotation = 0;

		@CCControl(name = "fov", min = 0, max = CCMath.PI)
		private float _cCameraFov = CCMath.radians(60);
		
		private CCCamera _myCamera;
		private CCFrustum _myFrustum;
		
		public CCDemoCamera(final CCViewport theViewport) {
			_myCamera = new CCCamera(g);
			_myCamera.viewport(theViewport);
			_myFrustum = new CCFrustum(_myCamera);
		}
		
		public void update(final float theDeltaTime) {
			_myCamera.near(_cCameraNear);
			_myCamera.far(_cCameraFar);
			
			_myCamera.frustumOffset().x = _cFrustumOffsetX;
			_myCamera.frustumOffset().y = _cFrustumOffsetY;
			
			_myCamera.xRotation(_cCameraXrotation);
			_myCamera.yRotation(_cCameraYrotation);
			_myCamera.zRotation(_cCameraZrotation);
			
			_myCamera.fov(_cCameraFov);
			
			_myFrustum.updateFromCamera();
		}
		
		public void draw(CCGraphics g) {
			_myCamera.draw(g);
		}
		
		public void drawFrustum(CCGraphics g) {
			_myFrustum.drawLines(g);
			_myFrustum.drawNormals(g);
			_myFrustum.drawPoints(g);
		}
	}
	
	private CCDemoCamera _myCamera1;
	private CCDemoCamera _myCamera2;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myArcball = new CCArcball(this);
		
		_myCamera1 = new CCDemoCamera(new CCViewport(0, 0, 400, 300));
		_myCamera2 = new CCDemoCamera(new CCViewport(400, 0, 400, 300));
		addControls("camera", "camera1",0, _myCamera1);
		addControls("camera", "camera2",1, _myCamera2);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myCamera1.update(theDeltaTime);
		_myCamera2.update(theDeltaTime);
	}
	
	private boolean _myDrawCamera = false;

	@Override
	public void draw() {
		g.clear();
		g.polygonMode(CCPolygonMode.LINE);
		if(_myDrawCamera) {
			_myCamera1.draw(g);
			g.box(140);
			_myCamera2.draw(g);
			g.box(140);
		}else {
			_myArcball.draw(g);
			g.box(140);
			
			_myCamera1.drawFrustum(g);
			_myCamera2.drawFrustum(g);
		}
		
		g.polygonMode(CCPolygonMode.FILL);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_C:
			_myDrawCamera = !_myDrawCamera;
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CC2CamerasDemo.class);
		myManager.settings().size(800, 300);
		myManager.start();
	}
}
