package cc.creativecomputing.skeleton.util;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.net.CCNetIn;
import cc.creativecomputing.net.CCNetListener;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonProvider;
import cc.creativecomputing.skeleton.CCSkeletonTransformCalculator;
import cc.creativecomputing.skeleton.util.CCSkeletonIO.CCSkeletonEvent;
import cc.creativecomputing.xml.CCXMLElement;

public class CCSkeletonReceiver implements CCSkeletonProvider, CCNetListener<CCXMLElement>{
	
	private class CCSkeletonDecoder{
		
		private CCSkeletonTransformCalculator _myCalculator;
		private CCSkeleton _mySkeleton;
		
		private CCSkeletonDecoder(int theID, CCXMLElement theSkeletonXML){
			_mySkeleton = _mySkeletonManager.baseSkeleton().clone();
			_mySkeleton.id(theID);
			_myCalculator = new CCSkeletonTransformCalculator(_mySkeleton, _mySkeletonManager.filter());
			decode(theSkeletonXML);
			setSkeleton();
			_mySkeleton.updateMatrices();
		}
		
		private long _myLastTime = System.nanoTime();
		
		private void decode(CCXMLElement theSkeletonXML){
			long myTime = System.nanoTime();
			long myTimeDiff = myTime - _myLastTime;
			_myLastTime = myTimeDiff;
			float myDeltaTime = (float)(myTimeDiff / 10e9);
			for(CCXMLElement myJointXML:theSkeletonXML){
				String myJointID = myJointXML.attribute(CCSkeletonIO.JOINT_TYPE_ATTRIBUTE);
				CCVector3f myPosition = new CCVector3f(
					myJointXML.floatAttribute(CCSkeletonIO.JOINT_X_ATTRIBUTE),
					myJointXML.floatAttribute(CCSkeletonIO.JOINT_Y_ATTRIBUTE),
					myJointXML.floatAttribute(CCSkeletonIO.JOINT_Z_ATTRIBUTE)
				);
				_myCalculator.position(myJointID, myPosition, myDeltaTime);
			}
			_myLastTime = System.currentTimeMillis();
		}
		
		private void setSkeleton(){
			_myCalculator.update(0);
		}
		
		private CCSkeleton skeleton(){
			return _mySkeleton;
		}
	}


	private CCSkeletonManager _mySkeletonManager;
	private CCNetIn<?, CCXMLElement> _mySkeletonReceiver;
	private Lock _myLock;
	
	private List<CCSkeleton> _myNewSkeletons = new ArrayList<>();
	private List<CCSkeleton> _myLostSkeletons = new ArrayList<>();

	protected Map<Integer, CCSkeletonDecoder> _myUserControllerMap = new HashMap<>();
	
	public CCSkeletonReceiver(CCNetIn<?, CCXMLElement> theSkeletonReceiver){
		_mySkeletonReceiver = theSkeletonReceiver;
		_mySkeletonReceiver.addListener(this);
		_myLock = new ReentrantLock();
	}

	@Override
	public void update(float theDeltaTime) {
		_myLock.lock();
		for(CCSkeleton mySkeleton:_myNewSkeletons){
			_mySkeletonManager.newSkeleton(mySkeleton);
		}
		_myNewSkeletons.clear();
		for(CCSkeletonDecoder myDecoder:new ArrayList<>(_myUserControllerMap.values())){
			myDecoder.setSkeleton();
			long myTime = System.currentTimeMillis() - myDecoder._myLastTime;
			if(myTime > 1000){
				_myUserControllerMap.remove(myDecoder._mySkeleton.id());
				_myLostSkeletons.add(myDecoder._mySkeleton);
			}
		}
		for(CCSkeleton mySkeleton:_myLostSkeletons){
			_mySkeletonManager.lostSkeleton(mySkeleton);
		}
		_myLostSkeletons.clear();
		_myLock.unlock();
	}

	@Override
	public void applySkeletonManager(CCSkeletonManager theSkeletonManager) {
		_mySkeletonManager = theSkeletonManager;
	}
	
	@Override
	public void messageReceived(CCXMLElement theMessage, SocketAddress theSender, long theTime) {
		CCSkeletonEvent myEvent = CCSkeletonEvent.valueOf(theMessage.attribute(CCSkeletonIO.EVENT_ATTRIBUTE));
		int myID = theMessage.intAttribute(CCSkeletonIO.ID_ATTRIBUTE);
		CCSkeletonDecoder myDecoder;
		switch(myEvent){
		case NEW:
			myDecoder = new CCSkeletonDecoder(myID, theMessage);
			_myLock.lock();
			_myUserControllerMap.put(myID, myDecoder);
			_myNewSkeletons.add(myDecoder.skeleton());
			_myLock.unlock();
			break;
		case UPDATE:
			_myLock.lock();
			myDecoder = _myUserControllerMap.get(myID);
			if(myDecoder == null){
				myDecoder = new CCSkeletonDecoder(myID, theMessage);
				_myUserControllerMap.put(myID, myDecoder);
				_myNewSkeletons.add(myDecoder.skeleton());
			}
			myDecoder.decode(theMessage);
			_myLock.unlock();
			break;
		case LOST:
			_myLock.lock();
			myDecoder = _myUserControllerMap.remove(myID);
			myDecoder.decode(theMessage);
			_myLostSkeletons.add(myDecoder.skeleton());
			_myLock.unlock();
			break;
		}
	}

	public void start(){
		_mySkeletonReceiver.startListening();
	}
}
