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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.signal.filter.CCFilterManager;

public class CCSkeletonManager {
	
	public static interface CCSkeletonManagerListener{
		public void update(float theDeltaTime);
		public void onNewSkeleton(CCSkeleton theSkeleton);
		public void onLostSkeleton(CCSkeleton theSkeleton);
	}
	
	private Map<Integer, CCSkeleton> _mySkeletonMap = new HashMap<Integer, CCSkeleton>();
	
	private List<CCSkeleton> _myNewSkeletons = new ArrayList<CCSkeleton>();
	private List<CCSkeleton> _myLostSkeletons = new ArrayList<CCSkeleton>();
	
	private CCListenerManager<CCSkeletonManagerListener> _myEvents = new CCListenerManager<CCSkeletonManagerListener>(CCSkeletonManagerListener.class);
	
	private CCFilterManager _myFilterManager;
	
	private CCSkeletonProvider _myProvider;
	
	private CCSkeleton _myBaseSkeleton;
	
	public CCSkeletonManager(CCApp theApp, CCSkeleton theBaseSkeleton, CCSkeletonProvider theSkeletonProvider){
		_myBaseSkeleton = theBaseSkeleton;
		_myProvider = theSkeletonProvider;
		_myProvider.applySkeletonManager(this);
		_myFilterManager = new CCFilterManager(theApp);
	}
	
	public CCSkeleton baseSkeleton(){
		return _myBaseSkeleton;
	}
	
	public CCListenerManager<CCSkeletonManagerListener> events(){
		return _myEvents;
	}
	
	public CCFilterManager filter(){
		return _myFilterManager;
	}
	
	public Collection<CCSkeleton> skeletons(){
		return _mySkeletonMap.values();
	}
	
	public void newSkeleton(CCSkeleton theSkeleton){
		theSkeleton.baseSize(_myBaseSkeleton.getCalculatedSize());
		_mySkeletonMap.put(theSkeleton.id(), theSkeleton);
		_myNewSkeletons.add(theSkeleton);
	}
	
	public void lostSkeleton(CCSkeleton theSkeleton){
		_mySkeletonMap.remove(theSkeleton.id());
		_myLostSkeletons.add(theSkeleton);
	}
	
	public void update(float theDeltaTime){
		_myProvider.update(theDeltaTime);
		_myFilterManager.update(theDeltaTime);
		
		synchronized (_myNewSkeletons) {
			for(CCSkeleton myNewSkeleton:_myNewSkeletons) {
				_myEvents.proxy().onNewSkeleton(myNewSkeleton);
			}
			_myNewSkeletons.clear();
		}
		
		synchronized (_myLostSkeletons) {
			for(CCSkeleton myLostSkeleton:_myLostSkeletons) {
				_myEvents.proxy().onLostSkeleton(myLostSkeleton);
			}
			_myLostSkeletons.clear();
		}

		_myEvents.proxy().update(theDeltaTime);
	}
}
