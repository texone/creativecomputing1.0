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
package cc.creativecomputing.cv.openni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openni.SkeletonJoint;
import org.openni.SkeletonJointPosition;
import org.openni.StatusException;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;

public abstract class CCOpenNIDataProvider{
	static CCOpenNIUserJointType[][] limbs = new CCOpenNIUserJointType[][]{
		{CCOpenNIUserJointType.HEAD, CCOpenNIUserJointType.NECK},

		{CCOpenNIUserJointType.NECK, CCOpenNIUserJointType.LEFT_SHOULDER},
		{CCOpenNIUserJointType.LEFT_SHOULDER, CCOpenNIUserJointType.LEFT_ELBOW},
		{CCOpenNIUserJointType.LEFT_ELBOW, CCOpenNIUserJointType.LEFT_HAND},

		{CCOpenNIUserJointType.NECK, CCOpenNIUserJointType.RIGHT_SHOULDER},
		{CCOpenNIUserJointType.RIGHT_SHOULDER, CCOpenNIUserJointType.RIGHT_ELBOW},
		{CCOpenNIUserJointType.RIGHT_ELBOW, CCOpenNIUserJointType.RIGHT_HAND},

		{CCOpenNIUserJointType.LEFT_SHOULDER, CCOpenNIUserJointType.TORSO},
		{CCOpenNIUserJointType.RIGHT_SHOULDER, CCOpenNIUserJointType.TORSO},
		
		{CCOpenNIUserJointType.LEFT_HIP, CCOpenNIUserJointType.RIGHT_HIP},

		{CCOpenNIUserJointType.LEFT_HIP, CCOpenNIUserJointType.TORSO},
		{CCOpenNIUserJointType.LEFT_HIP, CCOpenNIUserJointType.LEFT_KNEE},
		{CCOpenNIUserJointType.LEFT_KNEE, CCOpenNIUserJointType.LEFT_FOOT},

		{CCOpenNIUserJointType.RIGHT_HIP, CCOpenNIUserJointType.TORSO},
		{CCOpenNIUserJointType.RIGHT_HIP, CCOpenNIUserJointType.RIGHT_KNEE},
		{CCOpenNIUserJointType.RIGHT_KNEE, CCOpenNIUserJointType.RIGHT_FOOT},
	};
	
	protected boolean _myIsCalibrated = false;
	protected boolean _myIsTracking = false;
	protected boolean _myIsVisible = true;
	
	protected CCVector3f _myCenterOfMass = new CCVector3f();
	
	protected CCAABB _myBoundingBox = new CCAABB();
	
	protected Map<CCOpenNIUserJointType, CCOpenNIUserJoint>_myJointMap = new HashMap<CCOpenNIUserJointType, CCOpenNIUserJoint>();
	
	protected List<CCOpenNIUserJoint> _myUserJoints;
	
	protected List<CCOpenNIUserLimb> _myLimbs = new ArrayList<CCOpenNIUserLimb>();
	
	protected CCMatrix4f _myTransformationMatrix;
	
	protected int _myID;
	
	public abstract SkeletonJointPosition position(SkeletonJoint theJoint) throws StatusException;
	
	public void update(final float theDeltaTime) {

		_myBoundingBox = new CCAABB(_myJointMap.get(CCOpenNIUserJointType.TORSO).position(), new CCVector3f());
		for (CCOpenNIUserJoint myJoint : _myJointMap.values()) {
			_myBoundingBox.checkSize(myJoint.position());
		}
	}
	
	/**
	 * Method to check if a user is visible, a user is not visible if he exits the scene
	 * @return <code>true</code> if the user is visible otherwise <code>false</code>
	 */
	public boolean isVisible() {
		return _myIsVisible;
	}
	
	protected void isVisible(boolean theIsVisible) {
		_myIsVisible = theIsVisible;
	}
	
	public List<CCOpenNIUserLimb> limbs(){
		return _myLimbs;
	}
	
	boolean needsUpdate() {
		return true;//_myIsCalibrated && _myIsTracking;
	}
	
	
	
	public CCAABB boundingBox() {
		return _myBoundingBox;
	}
	
	public CCVector3f centerOfMass() {
		return _myCenterOfMass;
	}
	
	public int id() {
		return _myID;
	}
	
	/**
	 * gets the orientation of a joint
	 * 
	 * @param userId int
	 * @param joint int
	 * @param jointOrientation PMatrix3D
	 * @return The confidence of this joint float
	 */
	public CCOpenNIUserJoint joint(CCOpenNIUserJointType theType) {
		return _myJointMap.get(theType);
	}
	
	public List<CCOpenNIUserJoint> joints(){
		return _myUserJoints;
	}

	// draw the skeleton with the selected joints
	public void drawSkeleton(CCGraphics g) {
		g.beginShape(CCDrawMode.LINES);
		for(CCOpenNIUserLimb myLimb:_myLimbs) {
			myLimb.draw(g);
		}
		g.endShape();

		for(CCOpenNIUserJoint myJoint:_myJointMap.values()) {
			g.ellipse(myJoint.position(), 10);
		}
		
		g.ellipse(_myCenterOfMass, 20);
	}
}
