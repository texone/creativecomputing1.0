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

import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCOpenNIUserDataRecordProvider extends CCOpenNIDataProvider{

	private Map<SkeletonJoint, List<CCVector3f>> _myJointPositions = new HashMap<SkeletonJoint, List<CCVector3f>>();
	private List<SkeletonJoint> _myJointsToTrack = new ArrayList<SkeletonJoint>();

	
	CCOpenNIUserDataRecordProvider(String theFile, CCMatrix4f theTransformationMatrix) {

		_myTransformationMatrix = theTransformationMatrix;
		
		CCXMLElement myUserXML = CCXMLIO.createXMLElement(theFile);
		
		CCXMLElement mySkeletonXML0 = myUserXML.child(0);
		for(CCXMLElement myJointXML:mySkeletonXML0) {
			SkeletonJoint mySkeletonJoint = SkeletonJoint.valueOf(myJointXML.attribute("type"));
			CCOpenNIUserJointType myJointType = CCOpenNIUserJointType.valueOf(myJointXML.attribute("type"));
			_myJointMap.put(myJointType, new CCOpenNIUserJoint(myJointType));
			_myJointsToTrack.add(mySkeletonJoint);
			_myJointPositions.put(mySkeletonJoint, new ArrayList<CCVector3f>());
		}
		_myID = myUserXML.intAttribute("userID");
//		for(CCXMLElement mySkeletonXML:myUserXML) {
//			for(CCXMLElement myJointXML:mySkeletonXML) {
//				SkeletonJoint myType = SkeletonJoint.valueOf(myJointXML.attribute("type"));
//				CCVector3f myPosition = new CCVector3f(
//					myJointXML.floatAttribute("x"),
//					myJointXML.floatAttribute("y"),
//					myJointXML.floatAttribute("z")
//				);
//			}
//		}
	}
	
	public SkeletonJointPosition position(SkeletonJoint theJoint) throws StatusException {
		return null;
	};
	
	@Override
	public void update(float theDeltaTime) {
	}

	@Override
	public boolean needsUpdate() {
		// TODO Auto-generated method stub
		return false;
	}
}
