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
package cc.creativecomputing.skeleton.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCPolygon2f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;

/**
 * Use this class to check when a skeleton is inside a defined area.
 * @author christianriekoff
 *
 */
public class CCSkeletonInteractionArea implements CCSkeletonManagerListener{
	
	private class CCInteractionSkeleton{
		private CCSkeleton _mySkeleton;
		private boolean _myIsInInteractionArea;
		
		public CCInteractionSkeleton(CCSkeleton theSkeleton, boolean theIsInInterActionArea) {
			_mySkeleton = theSkeleton;
			_myIsInInteractionArea = theIsInInterActionArea;
		}
	}
	
	private static final float MAX_X = 2000;
	private static final float MAX_Z = 10000;
	
	@CCControl(name = "active")
	private boolean _cIsActive = true;

	@CCControl(name = "x1", min = -MAX_X, max = MAX_X)
	private float _cX1 = 0;

	@CCControl(name = "z1", min = -MAX_Z, max = MAX_Z)
	private float _cZ1 = 0;

	@CCControl(name = "x2", min = -MAX_X, max = MAX_X)
	private float _cX2 = 0;

	@CCControl(name = "z2", min = -MAX_Z, max = MAX_Z)
	private float _cZ2 = 0;

	@CCControl(name = "x3", min = -MAX_X, max = MAX_X)
	private float _cX3 = 0;

	@CCControl(name = "z3", min = -MAX_Z, max = MAX_Z)
	private float _cZ3 = 0;

	@CCControl(name = "x4", min = -MAX_X, max = MAX_X)
	private float _cX4 = 0;

	@CCControl(name = "z4", min = -MAX_Z, max = MAX_Z)
	private float _cZ4 = 0;
	
	@CCControl(name = "height", min = 0, max = 10000)
	private float _cHeight = 0;
	
	@CCControl(name = "xOffset", min = -MAX_X, max = MAX_X)
	private float _cXOffset = 0;
	
	@CCControl(name = "zOffset", min = -MAX_Z, max = MAX_Z)
	private float _cZOffset = 0;
	
	private CCSkeletonManager _mySkeletonManager;
	
	private CCListenerManager<CCSkeletonManagerListener> _myEvents = CCListenerManager.create(CCSkeletonManagerListener.class);
	
	private Map<Integer,CCInteractionSkeleton> _mySkeletonMap = new HashMap<Integer,CCInteractionSkeleton>();
	
	private List<CCSkeleton> _mySkeletons = new ArrayList<>();
	
	private CCPolygon2f _myInteractionArea;
	
	private String _myJointID;
	
	private float _myScale = 1;
	private float _myXTranslation;
	private float _myYTranslation;
	private float _myZTranslation;
	
	private float _myBottomY;
	
	public CCSkeletonInteractionArea(CCSkeletonManager theSkeletonManager, String theCenterJointID) {
		_myJointID = theCenterJointID;
		_mySkeletonManager = theSkeletonManager;
		_mySkeletonManager.events().add(this);
		_myInteractionArea = new CCPolygon2f();
	}
	
	public CCSkeletonInteractionArea(CCSkeletonManager theSkeletonManager) {
		this(theSkeletonManager, CCSkeletonJointType.SPINE.colladaID());
	}
	
	public CCListenerManager<CCSkeletonManagerListener> events(){
		return _myEvents;
	}
	
	private void addVertex(float theX, float theY){
		_myInteractionArea.addVertex((theX + _myXTranslation) * _myScale, (theY + _myZTranslation) * _myScale);
	}
	
	private void newSkeleton(CCSkeleton theSkeleton){
		_mySkeletons.add(theSkeleton);
		_myEvents.proxy().onNewSkeleton(theSkeleton);
	}
	
	private void lostSkeleton(CCSkeleton theSkeleton){
		_mySkeletons.remove(theSkeleton);
		_myEvents.proxy().onLostSkeleton(theSkeleton);
	}

	public List<CCSkeleton> skeletons(){
		return _mySkeletons;
	}
	
	public void update(final float theDeltaTime) {
		_myEvents.proxy().update(theDeltaTime);
		
		_myInteractionArea.vertices().clear();
		addVertex(_cX1, _cZ1);
		addVertex(_cX2, _cZ2);
		addVertex(_cX3, _cZ3);
		addVertex(_cX4, _cZ4);
		addVertex(_cX1, _cZ1);
		
		for (CCVector2f myVertex : _myInteractionArea.vertices()) {
			myVertex.add(_cXOffset, _cZOffset);
		}
		
		for(CCInteractionSkeleton mySkeleton:_mySkeletonMap.values()) {
			boolean myIsInInteractionArea = isSkeletonInArea(mySkeleton._mySkeleton);
			if(myIsInInteractionArea && !mySkeleton._myIsInInteractionArea) {
				newSkeleton(mySkeleton._mySkeleton);
			}
			if(!myIsInInteractionArea && mySkeleton._myIsInInteractionArea) {
				lostSkeleton(mySkeleton._mySkeleton);
			}
			mySkeleton._myIsInInteractionArea = myIsInInteractionArea;
		}
	}
	
	public void scale(float theScale){
		_myScale = theScale;
	}
	
	public void translation(float theXTranslation, float theYTranslation, float theZTranslation){
		_myXTranslation = theXTranslation;
		_myYTranslation = theYTranslation;
		_myZTranslation = theZTranslation;
	}
	
	public void bottomY(float theBottomY){
		_myBottomY = theBottomY;
	}
	
	public void draw(CCGraphics g) {
		g.pushAttribute();
		if(_cIsActive){
			g.color(255,0,0);
		}else{
			g.color(255);
		}
		g.pushMatrix();
		g.beginShape(CCDrawMode.LINES);
		float myY1 = (_myBottomY + _myYTranslation) * _myScale;
		float myY2 = (_myBottomY + _myYTranslation + _cHeight) * _myScale;
		for(int i = 0; i < _myInteractionArea.vertices().size() - 1;i++){
			CCVector2f myVertex0 = _myInteractionArea.vertices().get(i);
			CCVector2f myVertex1 = _myInteractionArea.vertices().get(i + 1);
			g.vertex(myVertex0.x, myY1, myVertex0.y); 
			g.vertex(myVertex0.x, myY2, myVertex0.y);

			g.vertex(myVertex0.x, myY1, myVertex0.y); 
			g.vertex(myVertex1.x, myY1, myVertex1.y);

			g.vertex(myVertex0.x, myY2, myVertex0.y); 
			g.vertex(myVertex1.x, myY2, myVertex1.y);
		}
		g.endShape();
		g.popMatrix();
		g.popAttribute();
	}
	
	private boolean isSkeletonInArea(CCSkeleton theSkeleton) {
		if(!_cIsActive)return true;
		CCSkeletonJoint myJoint = theSkeleton.joint(_myJointID);
		return _myInteractionArea.isInShape(myJoint.position().x, myJoint.position().z);
	}
	
	@Override
	public void onNewSkeleton(CCSkeleton theSkeleton) {
		CCInteractionSkeleton myInteractionSkeleton = new CCInteractionSkeleton(theSkeleton, isSkeletonInArea(theSkeleton));
		if(myInteractionSkeleton._myIsInInteractionArea) {
			newSkeleton(myInteractionSkeleton._mySkeleton);
		}
		_mySkeletonMap.put(theSkeleton.id(), myInteractionSkeleton);
	}

	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		CCInteractionSkeleton myInteractionSkeleton = _mySkeletonMap.remove(theSkeleton.id());
		if(myInteractionSkeleton!= null && myInteractionSkeleton._myIsInInteractionArea) {
			lostSkeleton(myInteractionSkeleton._mySkeleton);
		}
	}

	public void area(
		float theX1, float theZ1, 
		float theX2, float theZ2, 
		float theX3, float theZ3, 
		float theX4, float theZ4
	) {
		_cX1 = theX1; _cZ1 = theZ1;
		_cX2 = theX2; _cZ2 = theZ2;
		_cX3 = theX3; _cZ3 = theZ3;
		_cX4 = theX4; _cZ4 = theZ4;
	}
}
