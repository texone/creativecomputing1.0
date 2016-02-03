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

import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.net.CCNetOut;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;
import cc.creativecomputing.skeleton.util.CCSkeletonIO.CCSkeletonEvent;
import cc.creativecomputing.util.CCStopWatch;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonTransmitter implements CCSkeletonManagerListener{
	
	private class CCSkeletonEncoder{
		private CCSkeleton _mySkeleton;
		private int _myFrame;
		private float _myTime;
		
		public CCSkeletonEncoder(CCSkeleton theSkeleton) {
			_mySkeleton = theSkeleton;
			beginSending();
		}
		
		private void sendSkeleton(CCSkeletonEvent theEvent){
			if(!_myIsActive)return;
			
			CCXMLElement myDataXML = new CCXMLElement(CCSkeletonIO.SKELETON_NODE);
			myDataXML.addAttribute(CCSkeletonIO.FRAME_ATTRIBUTE, _myFrame);
			myDataXML.addAttribute(CCSkeletonIO.TIME_ATTRIBUTE, _myTime);
			myDataXML.addAttribute(CCSkeletonIO.ID_ATTRIBUTE, _mySkeleton.id());
			myDataXML.addAttribute(CCSkeletonIO.EVENT_ATTRIBUTE, theEvent.toString());
			_myFrame++;
//			CCLog.info("transmmit skeleton:"+_mySkeleton.id());
			for(CCSkeletonJoint myJoint:_mySkeleton.joints()) {
				CCVector3f myPosition = myJoint.position();
				
				CCVector3f myTransformedPosition = _myTransform.transform(myPosition.clone());
//				CCLog.info(myJoint.id()+":"+myPosition);
				CCXMLElement myJointXML = myDataXML.createChild(CCSkeletonIO.JOINT_NODE);
				myJointXML.addAttribute(CCSkeletonIO.JOINT_TYPE_ATTRIBUTE, myJoint.id());
				myJointXML.addAttribute(CCSkeletonIO.JOINT_X_ATTRIBUTE, myTransformedPosition.x);
				myJointXML.addAttribute(CCSkeletonIO.JOINT_Y_ATTRIBUTE, myTransformedPosition.y);
				myJointXML.addAttribute(CCSkeletonIO.JOINT_Z_ATTRIBUTE, myTransformedPosition.z);
			}
			_myNetOut.send(myDataXML);
		}
		
		public void update(final float theDeltaTime) {
			_myTime+= theDeltaTime;
			sendSkeleton(CCSkeletonEvent.UPDATE);
		}
		
		public void beginSending() {
			_myTime = 0;
			sendSkeleton(CCSkeletonEvent.NEW);
		}
		
		public void endSending() {
			sendSkeleton(CCSkeletonEvent.LOST);
		}
	}
	
	private Map<Integer, CCSkeletonEncoder> _myEncoderMap = new HashMap<Integer, CCSkeletonTransmitter.CCSkeletonEncoder>();
	private CCNetOut<?, CCXMLElement> _myNetOut;
	private boolean _myIsActive;
	
	private CCMatrix4f _myTransform = new CCMatrix4f();

	public CCSkeletonTransmitter(CCNetOut<?, CCXMLElement> theNetOut) {
		_myNetOut = theNetOut;
		_myIsActive = false;
	}
	
	public CCMatrix4f transform(){
		return _myTransform;
	}
	
	public void start() {
		_myNetOut.connect();
		_myIsActive = true;
		for(CCSkeletonEncoder myEndcoder:_myEncoderMap.values()) {
			myEndcoder.beginSending();
		}
	}
	
	public void end() {
		_myNetOut.dispose();
		_myIsActive = false;
		for(CCSkeletonEncoder myEncoder:_myEncoderMap.values()) {
			myEncoder.endSending();
		}
	}
	
	public boolean isSending() {
		return _myIsActive;
	}
	
	public void update(float theDeltaTime) {
		CCStopWatch.instance().startWatch("send");
		for(CCSkeletonEncoder myEncoder:_myEncoderMap.values()) {
			myEncoder.update(theDeltaTime);
		}
		CCStopWatch.instance().endWatch("send");
	}

	@Override
	public void onNewSkeleton(CCSkeleton theSkeleton) {
		_myEncoderMap.put(theSkeleton.id(),new CCSkeletonEncoder(theSkeleton));
	}

	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		CCSkeletonEncoder myEnCoder = _myEncoderMap.remove(theSkeleton.id());
		if(!_myIsActive || myEnCoder == null)return;
		myEnCoder.endSending();
	}
}
