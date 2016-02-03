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
package cc.creativecomputing.protocol.proxymatrix;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author maxgoettner
 *
 */
public class CCProxiMatrixCalibrator implements CCTouchListener{
	
	@CCControl (name = "calibrator x", min = 0, max = 1200)
	private float _cInitX = 0;
	
	@CCControl (name = "calibrator y", min = -500, max = 500)
	private float _cInitY = 0;
	
	@CCControl (name = "calibrator scale x", min = 1, max = 400)
	private float _cInitScaleX = 0;
	
	@CCControl (name = "calibrator scale y", min = 1, max = 400)
	private float _cInitScaleY = 0;
	
	
	
	
	private CCProxiMatrix _myProxiMatrix;
	private CCGraphics _myGraphics;
	
	public float scaleX, scaleY, offsetX, offsetY;
	public float _myScreenX, _myScreenY, _myScaleX, _myScaleY, _myScreenWidth, _myScreenHeight;
	
	int nPoints = 4;
	int iterator = 0;
	
	private CCTouch recordingTouch = null;
	private long time;
	private long wait = 1000*1000*1000;
	
	CCVector2f displayPoints[]  = new CCVector2f[nPoints];
	CCVector2f recordedPoints[] = new CCVector2f[nPoints];
	
	pointStatus status[] = {pointStatus.UNSET, pointStatus.UNSET, pointStatus.UNSET, pointStatus.UNSET};
	
	private class Point{
		CCVector2f displayCoords;
		CCVector2f recordedCoords;
		pointStatus status;
		public Point(){
			displayCoords = new CCVector2f(0,0);
			recordedCoords = new CCVector2f(0,0);
			status = pointStatus.UNSET;
		}
	}
	enum pointStatus{
		UNSET, PENDING, SET
	}
	
	private class Transformation {
		CCVector2f pos;
		CCVector2f scale;
		
		public Transformation() {
			pos = new CCVector2f(0,0);
			scale = new CCVector2f(1,1);
		}
	}
	
	private Transformation screenTransformation     = new Transformation();         // transform to (0,0) on screen
	private Transformation initTransformation       = new Transformation();			// transform to display calibration points 
	private Transformation calibratedTransformation = new Transformation();			// final transform
	
	
	Point points[] = new Point[nPoints];
	
	
	public CCProxiMatrixCalibrator (CCGraphics g, CCProxiMatrix theProxiMatrix, float screenX, float screenY, float screenWidth, float screenHeight) {
		_myProxiMatrix = theProxiMatrix;
		_myProxiMatrix.addListener(this);
		_myScreenX = screenX;             //start position of screen
		_myScreenY = screenY;
		
		_myScreenWidth  = screenWidth;    // screen size
		_myScreenHeight = screenHeight;
		
		_myScaleX = 5f;
		_myScaleY = 5f;
		
		_myGraphics = g;
		points[0] = new Point();
		points[1] = new Point();
		points[2] = new Point();
		points[3] = new Point();
		
		points[0].displayCoords = new CCVector2f (0, 0);	// absolute position 
		points[1].displayCoords = new CCVector2f (1, 0);
		points[2].displayCoords = new CCVector2f (0, 1);
		points[3].displayCoords = new CCVector2f (1, 1);
		
		screenTransformation.pos = new CCVector2f (-screenWidth/2, -screenHeight/2);
	}
	
	
	public void startCalibration() {
		reset();
	}
	
	
	private void reset() {
		for (Point p : points) {
			p.status = pointStatus.UNSET;
		}
		calibratedTransformation = new Transformation();
	}
	
	private boolean checkFinished() {
		return ( points[0].status == pointStatus.SET && points[1].status == pointStatus.SET && points[2].status == pointStatus.SET && points[3].status == pointStatus.SET);
	}
	
	private void update() {
		initTransformation.pos.x = _cInitX;
		initTransformation.pos.y = _cInitY;
		initTransformation.scale.x = _cInitScaleX;
		initTransformation.scale.y = _cInitScaleY;
	}
	
	public void draw() {
		update();
		
		transform(_myGraphics, screenTransformation);
		transform(_myGraphics, initTransformation);

		_myGraphics.pushAttribute();
		for (Point p : points) {
			if (p.status==pointStatus.UNSET)
				_myGraphics.color(1f,0,0);
			if (p.status==pointStatus.PENDING)
				_myGraphics.color(1f,1f,0);
			if (p.status==pointStatus.SET)
				_myGraphics.color(0,1f,0);
			_myGraphics.pointSize(15);
			_myGraphics.point (p.displayCoords);
		}
		_myGraphics.popAttribute();
	}
	
	public void onTouchPress (CCTouch theTouch) {
		
		System.out.println("t on");
		if (recordingTouch == null && !checkFinished()) {
			recordingTouch = theTouch;
			points[iterator].status = pointStatus.PENDING;
			time = System.nanoTime();
		}
	}
	
	public void onTouchMove (CCTouch theTouch) {
		
	}
	
	public void onTouchRelease (CCTouch theTouch) {
		if (recordingTouch==null)
			return;
		if (recordingTouch.equals(theTouch)) {
			recordingTouch = null;
			if (System.nanoTime()-time > wait) {
				System.out.println("rel");
				points[iterator].status = pointStatus.SET;
				points[iterator].recordedCoords = new CCVector2f (theTouch.relativePosition().x*_myProxiMatrix.width(), theTouch.relativePosition().y*_myProxiMatrix.height());
				iterator = (iterator+1)%nPoints;
			}
		}
	}

	public void transform (CCGraphics g) {
		update();
		calibratedTransformation.pos = new CCVector2f (0, 0);
		calibratedTransformation.scale = new CCVector2f (13.2, 22.8);
		//System.out.println("pos = "+calibratedTransformation.pos.x+" "+calibratedTransformation.pos.y);
		//System.out.println("scl = "+calibratedTransformation.scale.x+" "+calibratedTransformation.scale.y);
		transform (g, screenTransformation); 
		g.translate (initTransformation.pos.x, initTransformation.pos.y); // move (0,0) of proximatrix to (0,0) cal point
		transform (g, calibratedTransformation);
	}

	private void transform (CCGraphics g, Transformation t) {
		g.translate (t.pos.x, t.pos.y);
		g.scale(t.scale.x, t.scale.y);
	}

	
	public void setScreen(int screenW, int screenH) {
		_myScreenWidth = screenW;
		_myScreenHeight = screenH;
	}


	public void calcCalibration() {
		
		System.out.println("--------- display ---------");
		System.out.println (points[0].displayCoords.x+" "+points[0].displayCoords.y);
		System.out.println (points[1].displayCoords.x+" "+points[1].displayCoords.y);
		System.out.println (points[2].displayCoords.x+" "+points[2].displayCoords.y);
		System.out.println (points[3].displayCoords.x+" "+points[3].displayCoords.y);
		
		if (checkFinished()) {
			System.out.println("--------- recorded ---------");
			System.out.println (points[0].recordedCoords.x+" "+points[0].recordedCoords.y);
			System.out.println (points[1].recordedCoords.x+" "+points[1].recordedCoords.y);
			System.out.println (points[2].recordedCoords.x+" "+points[2].recordedCoords.y);
			System.out.println (points[3].recordedCoords.x+" "+points[3].recordedCoords.y);
			
			calibratedTransformation.scale.x = initTransformation.scale.x / (points[1].recordedCoords.x - points[0].recordedCoords.x);
			calibratedTransformation.scale.y = initTransformation.scale.y / (points[2].recordedCoords.y - points[1].recordedCoords.y);
			
			calibratedTransformation.pos.x = -points[0].recordedCoords.x * calibratedTransformation.scale.x;
			calibratedTransformation.pos.y = -points[0].recordedCoords.y * calibratedTransformation.scale.y;
			
			System.out.println ("--------- transformed ---------");
			System.out.println (calibratedTransformation.pos.x+" "+calibratedTransformation.pos.y);
			System.out.println (calibratedTransformation.scale.x+" "+calibratedTransformation.scale.y);
		}
	}
}
