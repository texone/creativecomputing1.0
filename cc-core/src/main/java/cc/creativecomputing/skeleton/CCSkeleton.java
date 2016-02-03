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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVector3f;

public class CCSkeleton {
	
	private class CCSkeletonLimb{
		CCSkeletonJoint _myJoint1;
		CCSkeletonJoint _myJoint2;
		
		CCSkeletonLimb(CCSkeletonJoint theJoint1, CCSkeletonJoint theJoint2){
			_myJoint1 = theJoint1;
			_myJoint2 = theJoint2;
		}
	}
	
	private Map<String, CCSkeletonJoint> _myJointMap = new HashMap<String, CCSkeletonJoint>();
	private List<CCSkeletonJoint> _myJoints = new ArrayList<CCSkeletonJoint>();
	private List<CCSkeletonLimb> _myLimbs = new ArrayList<CCSkeletonLimb>();
	private CCSkeletonJoint _myRoot;
	
	private CCMatrix4f _myBindSkinMatrix;
	
	private int _myID = 0;
	private float _mySkinScale = 1f;
	
	private float _myBaseSize = 1f;
	
	public CCSkeleton(CCMatrix4f theBindSkinMatrix) {
		_myBindSkinMatrix = new CCMatrix4f(theBindSkinMatrix);
	}
	
	public void baseSize(float theBaseSize){
		_myBaseSize = theBaseSize;
	}
	
	public float skinScale(){
		return _mySkinScale;
	}
	
	private float distance(String theJoint1, String theJoint2){
		CCVector3f myJoint1 = joint(theJoint1).position();
		CCVector3f myJoint2 = joint(theJoint2).position();
		return  myJoint1.distance(myJoint2);
	}
	
	public float getCalculatedSize() {
		float mySize = 0;
		mySize += distance("Head", "Neck");
		mySize += distance("Neck", "Spine");
		mySize += distance("Spine", "Hips");
		mySize += distance("LeftUpLeg", "LeftLeg");
		mySize += distance("LeftLeg", "LeftFoot");
		return mySize;
	}
	
	protected CCSkeleton() {
	}
	
	CCSkeleton(CCSkeleton theSkeleton){
		_myBindSkinMatrix = theSkeleton._myBindSkinMatrix.clone();
		_myRoot = theSkeleton._myRoot.clone();
		addJoint(_myRoot);
		addJoints(_myRoot);
		_myBaseSize = getCalculatedSize();
	}
	
	public CCPlane3f bodyPlane(){
		return new CCPlane3f(
			joint(CCSkeletonJointType.HIPS).position(),
			joint(CCSkeletonJointType.LEFT_SHOULDER).position(),
			joint(CCSkeletonJointType.RIGHT_SHOULDER).position()
		);
	}
	
	public CCMatrix4f bindSkinMatrix() {
		return _myBindSkinMatrix;
	}
	
	public void id(int theID){
		_myID = theID;
	}
	
	public int id(){
		return _myID;
	}
	
	private void addJoints(CCSkeletonJoint theParent) {
		for(CCSkeletonJoint theChild:theParent.childJoints()) {
			addJoint(theChild);
			addLimb(theParent, theChild);
			addJoints(theChild);
		}
	}
	
	public void rootJoint(CCSkeletonJoint theRoot) {
		_myRoot = theRoot;
	}
	
	public CCSkeletonJoint rootJoint(){
		return _myRoot;
	}
	
	public List<CCSkeletonJoint> joints(){
		return _myJoints;
	}
	
	public void update(float theDeltaTime){
		_mySkinScale = _myBaseSize / getCalculatedSize();
	}
	
	public void addJoint(CCSkeletonJoint theJoint) {
		_myJointMap.put(theJoint.id(), theJoint);
		_myJoints.add(theJoint);
	}
	
	public void addLimb(CCSkeletonJoint theJoint1, CCSkeletonJoint theJoint2) {
		_myLimbs.add(new CCSkeletonLimb(theJoint1, theJoint2));
	}
	
	public List<CCMatrix4f> skinningMatrices(){
		return skinningMatrices(_myJointMap.keySet());
	}
	
	public List<CCMatrix4f> skinningMatrices(Collection<String> theJointNames){
		
		List<CCMatrix4f> myResult = new ArrayList<CCMatrix4f>();
		myResult.add(_myBindSkinMatrix);
		for(String myName:theJointNames){
			CCSkeletonJoint myJoint = joint(myName);
			myJoint.resetInverseBindMatrix();
			myJoint.inverseBindMatrix().invScale(_mySkinScale);
			myResult.add(myJoint.skinningMatrix());
		}
		return myResult;
	}
	
	public CCSkeletonJoint joint(CCSkeletonJointType theType){
		return joint(theType.colladaID());
	}
	
	public CCSkeletonJoint joint(String theJointID) {
		return _myJointMap.get(theJointID);
	}
	
	public void draw(CCGraphics g) {
		g.pushAttribute();
		for(CCSkeletonLimb myBone:_myLimbs) {
			g.line(myBone._myJoint1.position(), myBone._myJoint2.position());
		}
		
		g.pointSize(5);
		for(CCSkeletonJoint myJoint:_myJointMap.values()) {
			g.point(myJoint.position());
		}
		g.popAttribute();
	}
	
	public void drawOrientations(CCGraphics g, float theLength) {
		g.pushMatrix();
		for(CCSkeletonJoint myJoint:_myJointMap.values()) {
			myJoint.drawOrientation(g, theLength);
		}
		g.popMatrix();
	}
	
	public void updateMatrices() {
		_myRoot.updateMatrices();
	}
	
	public CCSkeleton clone() {
		return new CCSkeleton(this);
	}
}
