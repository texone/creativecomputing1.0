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
package cc.creativecomputing.graphics.util;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCCamera;


/**
 * Adds a game like camera mover to the application. <b>LEFT</b> and <b>RIGHT</b>
 * key are used to move the camera along its local x axis. <b>UP</b> and <b>DOWN</b>
 * control the movement along the cameras z axis.
 * @author texone
 *
 */
public class CCCameraMover implements CCKeyListener, CCUpdateListener, CCMouseMotionListener{

	private CCCamera _myCamera;
	
	private float _myAmount = 1;
	
	private float _myMoveX = 0;
	private float _myMoveZ = 0;
	private float _myZoom = 0;
	
	private float _myScale = 1000000;
	
	public CCCameraMover(final CCApp theApp){
		this(theApp, theApp.g.camera());
	}
	
	public CCCameraMover(final CCCamera theCamera){
		_myCamera = theCamera;
	}
	
	public CCCameraMover(final CCAbstractWindowApp theApp, final CCCamera theCamera){
		theApp.addKeyListener(this);
		theApp.addMouseMotionListener(this);
		theApp.addUpdateListener(this);
		_myCamera = theCamera;
	}
	
	public void scale(final float theScale){
		_myScale = theScale;
	}
	
	///////////////////////////////////////////
	//
	// DEFINE CAMERA KEY CONTROL
	//
	///////////////////////////////////////////

	public void keyPressed(CCKeyEvent theE) {
		switch(theE.keyCode()){
		// check for movement in x direction
		case VK_LEFT:
			_myMoveX = -_myAmount;
			break;
		case VK_RIGHT:
			_myMoveX = _myAmount;
			break;
		// check for movement in z direction
		case VK_UP:
			_myMoveZ = -_myAmount;
			break;
		case VK_DOWN:
			_myMoveZ = _myAmount;
			break;
		case VK_A:
			_myZoom = -_myAmount;
			break;
		case VK_S:
			_myZoom = _myAmount;
			break;
		default:
		}
	}

	public void keyReleased(CCKeyEvent theE) {
		switch(theE.keyCode()){
		// uncheck for movement in x direction
		case VK_LEFT:
			_myMoveX = 0;
			break;
		case VK_RIGHT:
			_myMoveX = 0;
			break;
		// uncheck for movement in z direction
		case VK_UP:
			_myMoveZ = 0;
			break;
		case VK_DOWN:
			_myMoveZ = 0;
			break;
		case VK_A:
			_myZoom = 0;
			break;
		case VK_S:
			_myZoom = 0;
			break;
		default:
		}
	}

	public void keyTyped(CCKeyEvent theE) {
	}
	
	///////////////////////////////////////////
	//
	// DEFINE CAMERA MOUSE CONTROL
	//
	///////////////////////////////////////////

	public void mouseDragged(CCMouseEvent theMouseEvent) {
		_myCamera.rotateY((theMouseEvent.x() - theMouseEvent.px())/100f);
		_myCamera.rotateX((theMouseEvent.y() - theMouseEvent.py())/100f);
	}

	public void mouseMoved(CCMouseEvent theMouseEvent) {}

	public void update(float theDeltaTime) {
		_myCamera.moveX(_myMoveX*theDeltaTime/1000 * _myScale);
		_myCamera.moveZ(_myMoveZ*theDeltaTime/1000 * _myScale);
		
		_myCamera.zoom(_myZoom*theDeltaTime/10);
	}

}
