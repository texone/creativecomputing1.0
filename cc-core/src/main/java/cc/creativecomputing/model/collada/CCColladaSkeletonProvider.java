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
package cc.creativecomputing.model.collada;

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.model.collada.CCColladaAnimation.CCColladaAnimationKeyFrame;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonProvider;

/*
 * 
 */
public class CCColladaSkeletonProvider implements CCSkeletonProvider{

	private List<CCColladaAnimation> _myAnimations;
	
	private CCSkeleton _mySkeleton;
	
	private CCSkeletonManager _mySkeletonManager;
	
	private boolean _myPlayAnimation = true;
	
	public CCColladaSkeletonProvider(List<CCColladaAnimation> theAnimations, CCColladaSkinController theSkin, CCColladaSceneNode theNode) {
		_myAnimations = theAnimations;
		_mySkeleton = new CCColladaSkeleton(theSkin, theNode);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.model.skeleton.CCSkeletonProvider#applySkeletonManager(cc.creativecomputing.model.skeleton.CCSkeletonManager)
	 */
	@Override
	public void applySkeletonManager(CCSkeletonManager theSkeletonManager) {
		_mySkeletonManager = theSkeletonManager;
		_mySkeletonManager.newSkeleton(_mySkeleton);
	}
	
	public void playAnimation(boolean thePlayAnimation){
		_myPlayAnimation = thePlayAnimation;
	}
	
	private void applyAnimation(CCColladaAnimation myAnimation) {
		for(int i = 1; i < myAnimation.keyFrames().size();i++) {
			CCColladaAnimationKeyFrame myFrame = myAnimation.keyFrames().get(i);
			if(myFrame.time() > _myTime) {
				String myTarget = myAnimation.target();
				myTarget = myTarget.substring(0, myTarget.lastIndexOf("/"));
				
				if(_mySkeleton.joint(myTarget) == null)continue;
				
				CCColladaAnimationKeyFrame myFrame2 = myAnimation.keyFrames().get(i - 1);
				float myBlend = CCMath.norm(_myTime, myFrame2.time(), myFrame.time());
				
				CCMatrix4f myBlendedTransform = CCMatrix4f.blend(myBlend, myFrame2.matrix(), myFrame.matrix());
				_mySkeleton.joint(myTarget).transform().set(myBlendedTransform);
				return;
			}
		}
		_mySkeleton.updateMatrices();
	}
	
	public CCSkeleton skeleton() {
		return _mySkeleton;
	}
	
	private float _myTime;
	
	public void update(final float theDeltaTime) {
		if(!_myPlayAnimation)return;
		_myTime+= theDeltaTime * 0.1;
		
		for(CCColladaAnimation myAnimation:_myAnimations) {
			applyAnimation(myAnimation);
		}
	}
	
	public void time(float theTime) {
		_myTime = theTime;
		for(CCColladaAnimation myAnimation:_myAnimations) {
			applyAnimation(myAnimation);
		}
	}
}
