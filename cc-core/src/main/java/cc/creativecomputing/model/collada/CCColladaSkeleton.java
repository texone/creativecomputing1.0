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

import cc.creativecomputing.model.collada.CCColladaSkinController.CCColladaSkinJoint;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;

/*
 * 
 */
public class CCColladaSkeleton extends CCSkeleton{

	/**
	 * @param theSkeleton
	 */
	public CCColladaSkeleton(CCColladaSkinController theSkin, CCColladaSceneNode theNode) {
		super(theSkin.bindShapeMatrix());
		
		CCSkeletonJoint myRootJoint = createSkeletonJoint(theSkin, theNode, null);
		rootJoint(myRootJoint);
		addJoint(myRootJoint);
		
		addChildNodes(theSkin, myRootJoint, theNode);
		
		updateMatrices();
		
		baseSize(getCalculatedSize());
	}

	private CCSkeletonJoint createSkeletonJoint(
		CCColladaSkinController theSkin, 
		CCColladaSceneNode theElement, 
		CCSkeletonJoint theParentJoint
	) {
		CCColladaSkinJoint mySkinJoint = theSkin.joint(theElement.id());
		return new CCSkeletonJoint(theElement.matrix(), mySkinJoint.inverseBindMatrix(), theElement.id(), mySkinJoint.index(), theParentJoint);
	}
		
	private void addChildNodes(
		CCColladaSkinController theSkin, 
		CCSkeletonJoint theParentJoint, 
		CCColladaSceneNode theParentNode
	) {
		for(CCColladaSceneNode myChild:theParentNode.children()) {
			if(!theSkin.hasJoint(myChild.id()))continue;
				
			CCSkeletonJoint myJoint = createSkeletonJoint(theSkin, myChild, theParentJoint);
			
			theParentJoint.addChildJoint(myJoint);
				
			addJoint(myJoint);
			addLimb(theParentJoint, myJoint);
			
			addChildNodes(theSkin, myJoint, myChild);
		}
	}
		
}
