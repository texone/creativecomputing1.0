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

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonRecorder implements CCSkeletonManagerListener{
	
	private class CCSkeletonRecord{
		private CCSkeleton _mySkeleton;
		private CCXMLElement _myNode;
		private int _myFrame;
		private float _myTime;
		
		public CCSkeletonRecord(CCSkeleton theSkeleton) {
			_mySkeleton = theSkeleton;
			_myNode = _myXML.createChild("skeletonRecord");
			_myNode.addAttribute("skeletonID", _mySkeleton.id());
		}
		
		public void update(final float theDeltaTime) {
			if(!_myIsRecording)return;

			
			CCXMLElement myDataXML = new CCXMLElement("skeleton");
			myDataXML.addAttribute("frame", _myFrame);
			myDataXML.addAttribute("time", _myTime);
			_myTime+= theDeltaTime;
			_myFrame++;
			
			for(CCSkeletonJoint myJoint:_mySkeleton.joints()) {
				CCVector3f myPosition = myJoint.position();
				CCXMLElement myJointXML = myDataXML.createChild("joint");
				myJointXML.addAttribute("type", myJoint.id());
				myJointXML.addAttribute("x", myPosition.x);
				myJointXML.addAttribute("y", myPosition.y);
				myJointXML.addAttribute("z", myPosition.z);
			}
			_myNode.addChild(myDataXML);
		}
		
		public void beginRecording() {
			_myTime = 0;
			_myNode = _myXML.createChild("userRecord");
			_myNode.addAttribute("skeletonID", _mySkeleton.id());
		}
		
		public void endRecording() {
			CCXMLIO.saveXMLElement(_myNode, _myFile+"_"+_myCount++ +".xml");
		}
	}
	
	private Map<Integer, CCSkeletonRecord> _myRecordMap = new HashMap<Integer, CCSkeletonRecorder.CCSkeletonRecord>();
	
	private CCXMLElement _myXML;
	private String _myFile;
	private int _myCount;
	private boolean _myIsRecording;

	public CCSkeletonRecorder(CCSkeletonManager theSkeletonManager, String theFile) {
		theSkeletonManager.events().add(this);
		_myFile = theFile;
		_myCount = 0;
		_myXML = new CCXMLElement("userSkeletons");
		_myIsRecording = false;
	}
	
	public void startRecording() {
		_myIsRecording = true;
		for(CCSkeletonRecord myRecord:_myRecordMap.values()) {
			myRecord.beginRecording();
		}
	}
	
	public void endRecording() {
		_myIsRecording = false;
		for(CCSkeletonRecord myRecord:_myRecordMap.values()) {
			myRecord.endRecording();
		}
	}
	
	public boolean isRecording() {
		return _myIsRecording;
	}
	
	public void update(float theDeltaTime) {
		for(CCSkeletonRecord myRecord:_myRecordMap.values()) {
			myRecord.update(theDeltaTime);
		}
	}

	@Override
	public void onNewSkeleton(CCSkeleton theSkeleton) {
		_myRecordMap.put(theSkeleton.id(),new CCSkeletonRecord(theSkeleton));
	}

	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		CCSkeletonRecord myRecord = _myRecordMap.remove(theSkeleton.id());
		if(!_myIsRecording || myRecord == null)return;
		myRecord.endRecording();
	}
}
