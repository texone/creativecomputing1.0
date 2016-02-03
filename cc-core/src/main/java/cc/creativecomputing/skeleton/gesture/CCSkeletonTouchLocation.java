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
package cc.creativecomputing.skeleton.gesture;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCMovingAverage;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonTouchLocation {
	
	public static interface CCSkeletonTouchLocationListener{
		public void onOver(CCSkeletonTouchLocation theLocation);
		
		public void onOut(CCSkeletonTouchLocation theLocation);
		
		public void onSelect(CCSkeletonTouchLocation theLocation);
		
		public void onDeselect(CCSkeletonTouchLocation theLocation);
	}
	
	private CCMovingAverage<Float> _mySourceProximity;
	
	private float _mySelectProgress;
	
	protected CCVector3f _myPosition;
	protected CCVector3f _my2DPosition;
	
	private boolean _myIsSelectable = true;
	
	protected String _myName;
	
	private CCListenerManager<CCSkeletonTouchLocationListener> _myEvents = CCListenerManager.create(CCSkeletonTouchLocationListener.class);
	
	public CCSkeletonTouchLocation(String theName, CCVector3f thePosition) {
		_myName = theName;
		
		_myPosition = thePosition;
		_my2DPosition = new CCVector3f();
		
		_mySourceProximity = CCMovingAverage.floatAverage(0.9f);
		_mySourceProximity.skipRange(10f);
	}
	
	public void update(float theDeltaTime) {
		_mySelectProgress -= theDeltaTime;
		_mySelectProgress = CCMath.saturate(_mySelectProgress);
	}
	
	public CCListenerManager<CCSkeletonTouchLocationListener> events(){
		return _myEvents;
	}
	
	void onOver() {
		_myEvents.proxy().onOver(this);
	}
	
	void onOut() {
		_myEvents.proxy().onOut(this);
	}
	
	void onSelect() {
		_myEvents.proxy().onSelect(this);
	}
	
	void onDeselect() {
		_myEvents.proxy().onDeselect(this);
	}
	
	/**
	 * Returns the proximity of the touch location to a touch source this is based on the highlight radius of the 
	 * touchinteractionmanager. If a source in the highlight radius of the location the source proximity is rising dependent 
	 * how far the source is away. This value is relative.
	 * @return proximity of the closest touch source from 0 to 1
	 */
	public float sourceProximity() {
		return _mySourceProximity.value();
	}
	
	void sourceProximity(float theBlend, float theSmooth) {
		_mySourceProximity.weight(theSmooth);
		_mySourceProximity.update(theBlend);
	}
	
	void selectProgress(float theSelectProgress) {
		_mySelectProgress = theSelectProgress;
	}
	
	public float selectProgress() {
		return _mySelectProgress;
	}
	
	public String name() {
		return _myName;
	}
	
	public CCVector3f position() {
		return _myPosition;
	}
	
	public CCVector3f position2D() {
		return _my2DPosition;
	}
	
	public boolean isSelectable() {
		return _myIsSelectable;
	}
	
	public void isSelectable(boolean theIsSelectable) {
		_myIsSelectable = theIsSelectable;
	}
}
