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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.model.collada.CCColladaSkinController.CCColladaSkinJoint;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonSkin;

/*
 * 
 */
public class CCColladaSkeletonSkin extends CCSkeletonSkin{

	private CCColladaTriangles _myTriangles;
	private CCColladaSkinController _mySkin;
	private List<String> _myJointNames;
	
	public CCColladaSkeletonSkin(CCColladaSkinController theSkinController) {
		super(CCDrawMode.TRIANGLES);

		_myTriangles = theSkinController.geometry().triangles().get(0);
		_mySkin = theSkinController; 
		
		_myMesh.vertices(_myTriangles.positions());
		if(_myTriangles.hasNormals()) {
			_myMesh.normals(_myTriangles.normals());
		}
		weights(_mySkin.weights());
		weightIndices(_mySkin.indices());
		
		_myJointNames = new ArrayList<>();
		for(CCColladaSkinJoint myJoint:_mySkin.joints()){
			_myJointNames.add(myJoint.name());
		}
	}
	
	public List<String> jointNames(){
		return _myJointNames;
	}
	
	public String skinName(){
		return _mySkin.id();
	}
	
	@Override
	public List<CCMatrix4f> skinningMatrices(CCSkeleton theSkeleton) {
		return theSkeleton.skinningMatrices(_myJointNames);
	}

}
