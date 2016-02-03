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
package cc.creativecomputing.cv.openni.util;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * The openni floor plane detection is not working under all circumstances, this class can be used
 * to calculate the floor plane based on the sample probes in the depth image, for a correct floor plane
 * all the defined three points have to be on the floor plane, so no image segmentation is done, and the 
 * floor is not detected computationally but defined by the user.
 * @author christianriekoff
 *
 */
public class CCOpenNIFloorPlaneDetector {
	@CCControl(name = "x 0", min = 0, max = 1)
	private float _cX0 = 0;
	@CCControl(name = "y0", min = 0, max = 1)
	private float _cY0 = 0;

	@CCControl(name = "x 1", min = 0, max = 1)
	private float _cX1 = 0;	
	@CCControl(name = "y1", min = 0, max = 1)
	private float _cY1 = 0;

	@CCControl(name = "x 2", min = 0, max = 1)
	private float _cX2 = 0;	
	@CCControl(name = "y2", min = 0, max = 1)
	private float _cY2 = 0;
	
	@CCControl(name = "plane smoothing")
	private float _cPlaneSmoothing = 0;
	@CCControl(name = "plane alpha")
	private float _cPlaneAlpha = 0;
	
	@CCControl(name = "calculate")
	private boolean _cCalculate = false;
	
	@CCControl(name = "draw debug")
	private boolean _cDrawDebug = false;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	private CCPlane3f _myFloorPlane;
	private CCVector3f _myPoint0;
	private CCVector3f _myPoint1;
	private CCVector3f _myPoint2;
	
	private CCMatrix4f _myOpenNITransformation;
	
	private CCVector3f _myNormal;
	private float _myConstant;
	
	public CCOpenNIFloorPlaneDetector(CCOpenNI theOpenNI) {
		_myOpenNI = theOpenNI;
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		
		_myOpenNITransformation = new CCMatrix4f();
		
		_myFloorPlane = new CCPlane3f(new CCVector3f(0,1,0),0);
		_myNormal = new CCVector3f(0,1,0);
		_myConstant = 0;
	}
	
	public CCMatrix4f transformation() {
		return _myOpenNITransformation;
	}
	
	private void calculateTransformation(){
		if(!_cCalculate)return;
		
		CCVector3f myNormal = new CCVector3f();
		float myConstant = 0;
		
		if(_myDepthGenerator.depthMap() == null)return;
		
		int myX0 = (int)CCMath.blend(0, _myDepthGenerator.width() - 1, _cX0);
		int myY0 = (int)CCMath.blend(0, _myDepthGenerator.height() - 1, _cY0);
		
		int myX1 = (int)CCMath.blend(0, _myDepthGenerator.width() - 1, _cX1);
		int myY1 = (int)CCMath.blend(0, _myDepthGenerator.height() - 1, _cY1);
		
		int myX2 = (int)CCMath.blend(0, _myDepthGenerator.width() - 1, _cX2);
		int myY2 = (int)CCMath.blend(0, _myDepthGenerator.height() - 1, _cY2);
		
		CCVector3f[] _myPoints0 = _myDepthGenerator.depthMapRealWorld(1, myX0, myY0, 1, 1, false);
		CCVector3f[] _myPoints1 = _myDepthGenerator.depthMapRealWorld(1, myX1, myY1, 1, 1, false);
		CCVector3f[] _myPoints2 = _myDepthGenerator.depthMapRealWorld(1, myX2, myY2, 1, 1, false);
		
		if(_myPoints0 == null || _myPoints1 == null || _myPoints2 == null)return;
		
		_myPoint0 = _myPoints0[0];
		_myPoint1 = _myPoints1[0];
		_myPoint2 =	_myPoints2[0];
		
		CCPlane3f myPlane = new CCPlane3f(_myPoint0, _myPoint1, _myPoint2);
		if(myPlane.normal().y < 0) {
			myNormal.subtract(myPlane.normal());
			myConstant -= myPlane.constant();
		}else {
			myNormal.add(myPlane.normal());
			myConstant += myPlane.constant();
		}
		
		myNormal.normalize();
	
		_myNormal.set(CCVecMath.blend(_cPlaneSmoothing, myNormal, _myNormal).normalize());
		_myConstant = CCMath.blend(myConstant, _myConstant, _cPlaneSmoothing);
	}
	
	public void update(float theDeltaTime) {
		calculateTransformation();
		_myOpenNITransformation = new CCMatrix4f();
		_myOpenNITransformation.rotateX(-CCVecMath.angle(_myNormal, new CCVector3f(0,1,0)));
		_myOpenNITransformation.translate(0,_myConstant,0);
	}
	
	public void draw(CCGraphics g) {
		if(_myFloorPlane == null)return;
		if(_myPoint0 == null)return;
		
		g.pushMatrix();
		g.applyMatrix(_myOpenNI.transformationMatrix().clone().invert());
		g.color(255,0,0);
		g.pointSize(10);
		g.beginShape(CCDrawMode.POINTS);
		g.vertex(_myPoint0);
		g.vertex(_myPoint1);
		g.vertex(_myPoint2);
		g.endShape();
		g.pointSize(1);
		g.popMatrix();
		
		_myFloorPlane.drawScale(5000);
		g.color(1f, _cPlaneAlpha);
		_myFloorPlane.draw(g);
		
		
	}
}
