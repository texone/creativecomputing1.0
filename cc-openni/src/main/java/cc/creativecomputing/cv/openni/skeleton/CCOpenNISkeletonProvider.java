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
package cc.creativecomputing.cv.openni.skeleton;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator.CCUserListener;
import cc.creativecomputing.math.signal.filter.CCFilterManager;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonProvider;

public class CCOpenNISkeletonProvider implements CCSkeletonProvider, CCUserListener{
	
	@CCControl(name = "fix limb length")
	private boolean _cFixLimbLength = true;
	
	@CCControl(name = "rescale size")
	private boolean _cRescaleSize = true;
	
	@CCControl(name = "leg fixer")
	private CCOpenNISkeletonLegFixer _myLegFixer = new CCOpenNISkeletonLegFixer();

	private CCOpenNIUserGenerator _myUserGenerator;
	private CCSkeletonManager _mySkeletonManager;
	

	protected Map<Integer, CCOpenNISkeletonMapper> _myUserControllerMap = new HashMap<Integer, CCOpenNISkeletonMapper>();
	
	public CCOpenNISkeletonProvider(CCOpenNIUserGenerator theUserGenerator){
		_myUserGenerator = theUserGenerator;
		_myUserGenerator.events().add(this);
	}
	
	CCFilterManager filter(){
		return _mySkeletonManager.filter();
	}
	
	public CCOpenNISkeletonLegFixer legFixer(){
		return _myLegFixer;
	}
	
	@Override
	public void applySkeletonManager(CCSkeletonManager theSkeletonManager) {
		_mySkeletonManager = theSkeletonManager;
	}

	@Override
	public void onNewUser(CCOpenNIUser theUser) {
		CCSkeleton mySkeleton = _mySkeletonManager.baseSkeleton().clone();
		
		CCOpenNISkeletonMapper mySkeletonMapper = new CCOpenNISkeletonMapper(theUser, mySkeleton, this);
		mySkeletonMapper.fixLimbLength(_cFixLimbLength);
		mySkeletonMapper.rescaleSize(_cRescaleSize);
		mySkeletonMapper.skeleton().bindSkinMatrix().reset();
		mySkeletonMapper.update(1f);
		_myUserControllerMap.put(theUser.id(), mySkeletonMapper);
		_mySkeletonManager.newSkeleton(mySkeletonMapper.skeleton());
	}

	@Override
	public void onLostUser(CCOpenNIUser theUser) {
		if(theUser == null)return; 
		if(!_myUserControllerMap.containsKey(theUser.id()))return;
		
		CCOpenNISkeletonMapper mySkeletonMapper = _myUserControllerMap.remove(theUser.id());
		_mySkeletonManager.lostSkeleton(mySkeletonMapper.skeleton());
	}
	
	@Override
	public void onEnterUser(CCOpenNIUser theUser) {
		onNewUser(theUser);
	}
	
	@Override
	public void onExitUser(CCOpenNIUser theUser) {
		onLostUser(theUser);
	}

	public void update(float theDeltaTime) {
		for (CCOpenNISkeletonMapper mySkeletonMapper : _myUserControllerMap.values()) {
			mySkeletonMapper.fixLimbLength(_cFixLimbLength);
			mySkeletonMapper.rescaleSize(_cRescaleSize);
			mySkeletonMapper.skeleton().bindSkinMatrix().reset();
			mySkeletonMapper.update(theDeltaTime);
		}
	}

}
