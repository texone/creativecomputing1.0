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
package cc.creativecomputing.skeleton;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCSkeletonJoint{
	private CCMatrix4f _myTransform;
	private CCMatrix4f _myWorldTransform;
	private CCMatrix4f _myInverseBindMatrix;
	private CCMatrix4f _mySkinningMatrix;
	
	CCVector3f _myPosition;
	CCVector3f _myVelocity;
	
	String _myID;
	private int _myIndex;
	
	private CCSkeletonJoint _myParentJoint;
	private List<CCSkeletonJoint> _myChildJoints = new ArrayList<CCSkeletonJoint>();
	
	public CCSkeletonJoint(CCMatrix4f theTransform, CCMatrix4f theInverseBindMatrix, String theID, int theIndex, CCSkeletonJoint theParentJoint) {
		_myTransform = theTransform;
		_myInverseBindMatrix = theInverseBindMatrix;
		_myOriginalBindMatrix = _myInverseBindMatrix.clone();
		_myPosition = new CCVector3f();
		_myVelocity = new CCVector3f();
		_myID = theID;
		_myIndex = theIndex;
		_myParentJoint = theParentJoint;
	}
	
	private CCMatrix4f _myOriginalBindMatrix;
	
	CCSkeletonJoint(CCSkeletonJoint theJoint){
		_myTransform = theJoint._myTransform.clone();
		_myOriginalBindMatrix = theJoint._myOriginalBindMatrix.clone();
		_myInverseBindMatrix = theJoint._myInverseBindMatrix.clone();
		_myPosition = theJoint._myPosition.clone();
		_myVelocity = theJoint._myVelocity.clone();
		_myID = theJoint._myID;
		_myIndex = theJoint._myIndex;
		_myParentJoint = theJoint._myParentJoint;
		_myWorldTransform = theJoint._myWorldTransform;
		_mySkinningMatrix = theJoint.skinningMatrix();

		for(CCSkeletonJoint myChildJoint:theJoint._myChildJoints) {
			_myChildJoints.add(myChildJoint.clone());
		}
	}
	
	public void resetInverseBindMatrix(){
		_myInverseBindMatrix.set(_myOriginalBindMatrix);
	}
	
	public CCMatrix4f inverseBindMatrix(){
		return _myInverseBindMatrix;
	}
	
	public int index() {
		return _myIndex;
	}
	
	public List<CCSkeletonJoint> childJoints(){
		return _myChildJoints;
	}
	
	public void addChildJoint(CCSkeletonJoint theJoint) {
		_myChildJoints.add(theJoint);
	}
	
	private void updateWorldTransform() {
		CCVector3f myNewPosition = _myWorldTransform.transform(new CCVector3f());
		if(!_myPosition.equals(new CCVector3f()) && !_myPosition.equals(myNewPosition)) {
			_myVelocity = CCVecMath.subtract(myNewPosition, _myPosition);
		}
		_myPosition.set(myNewPosition);
		
		_mySkinningMatrix = CCMatrix4f.multiply(_myWorldTransform, _myInverseBindMatrix);
	}
	
	public void updateMatrices() {
		if(_myParentJoint!=null) {
			_myWorldTransform = CCMatrix4f.multiply(_myParentJoint._myWorldTransform, _myTransform);
		} else {
			_myWorldTransform = new CCMatrix4f(_myTransform);//.multiply(_myBindSkinMatrix,_myTransform);
		}
		updateWorldTransform() ;
		
		for(CCSkeletonJoint myChild:_myChildJoints) {
			myChild.updateMatrices();
		}
	}
	
	public CCVector3f position() {
		return _myPosition;
	}
	public CCMatrix4f wordlTransform() {
		return _myWorldTransform;
	}
	
	public CCVector3f velocity() {
		return _myVelocity;
	}
	
	public CCMatrix4f worldTransform(){
		return _myWorldTransform;
	}
	
	public void drawOrientation(CCGraphics g, float theLength) {
		if(_myWorldTransform == null) {
			return;
		}
		g.pushMatrix();

		// set the local coordsys
//		g.applyMatrix(theJoint.orientation());
		g.applyMatrix(_myWorldTransform);
		
		// coordsys lines are 100mm long
		// x - r
		g.color(1f, 0f, 0f);
		g.line(0, 0, 0, theLength, 0, 0);
		// y - g
		g.color(0f, 1f, 0f);
		g.line(0, 0, 0, 0, theLength, 0);
		// z - b
		g.color(0, 0, 1f);
		g.line(0, 0, 0, 0, 0, theLength);
		g.popMatrix();
	}
	
	public void worldMatrix(CCMatrix4f theMatrix4f) {
		_myWorldTransform = theMatrix4f;
		updateWorldTransform();
	}
	
	public CCMatrix4f skinningMatrix() {
		return _mySkinningMatrix;
	}
	
	public CCMatrix4f transform() {
		return _myTransform;
	}
	
	public CCSkeletonJoint clone() {
		return new CCSkeletonJoint(this);
	}

	/**
	 * @return
	 */
	public String id() {
		return _myID;
	}
	
	
}
