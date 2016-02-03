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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openni.ActiveHandEventArgs;
import org.openni.GeneralException;
import org.openni.HandsGenerator;
import org.openni.IObservable;
import org.openni.IObserver;
import org.openni.InactiveHandEventArgs;
import org.openni.StatusException;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * A Hands Generator node is a Generator that tracks hand points.
 * @author christianriekoff
 * 
 */
public class CCOpenNIHandGenerator extends CCOpenNIGenerator<HandsGenerator>{
	
	private Map<Integer, CCOpenNIHand> _myHandMap = new HashMap<Integer, CCOpenNIHand>();
	
	private CCListenerManager<CCOpenNIHandListener> _myListenerManager = new CCListenerManager<CCOpenNIHandListener>(CCOpenNIHandListener.class);
	private int _myHistorySize = 0;
	
	private CCMatrix4f _myTransformationMatrix;
	
	public CCOpenNIHandGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI);
		_myTransformationMatrix = theOpenNI.transformationMatrix();
	}

	@Override
	HandsGenerator create(CCOpenNI theOpenNI) {
		try {
			HandsGenerator myHandGenerator;
			if(theOpenNI.deviceQuery() == null)myHandGenerator = HandsGenerator.create(theOpenNI.context());
			else myHandGenerator = HandsGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
			myHandGenerator.getHandCreateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {
				
				public void update(IObservable<ActiveHandEventArgs> theObservable, ActiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					CCVector3f myPosition = convert(theArgs.getPosition());
					_myListenerManager.proxy().onCreateHands(myID, myPosition, myTime);
					
					CCLog.info("onCreateHands - handId: " + myID + ", pos: " + myPosition + ", time:" + myTime);

					CCOpenNIHand myHand = new CCOpenNIHand(myID, _myTransformationMatrix, _myHistorySize);
					_myHandMap.put(myID, myHand);
					_myHandMap.get(myID).position(myPosition);
				}
			});
			
			myHandGenerator.getHandUpdateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {

				public void update(IObservable<ActiveHandEventArgs> theObservable, ActiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					CCVector3f myPosition = convert(theArgs.getPosition());
					
					_myListenerManager.proxy().onUpdateHands(myID, myPosition, myTime);
					
					CCLog.info("onUpdateHandsCb - handId: " + myID + ", pos: " + myPosition + ", time:" + myTime);
					_myHandMap.get(myID).position(myPosition);
				}
				
			});
			
			myHandGenerator.getHandDestroyEvent().addObserver(new IObserver<InactiveHandEventArgs>() {

				public void update(IObservable<InactiveHandEventArgs> theObservable, InactiveHandEventArgs theArgs) {
					int myID = theArgs.getId();
					float myTime = theArgs.getTime();
					_myListenerManager.proxy().onDestroyHands(myID, myTime);
					
					CCLog.info("onDestroyHandsCb - handId: " + myID + ", time:" + myTime);
					_myHandMap.remove(myID);
				}});
			return myHandGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCListenerManager<CCOpenNIHandListener> events(){
		return _myListenerManager;
	}
	
	/**
	 * Change smoothing factor. Smoothing factor, in the range 0..1. 
	 * 0 Means no smoothing, 1 means infinite smoothing. Inside the 
	 * range is generator dependent.
	 * @param theSmoothing Smoothing factor, in the range 0..1
	 */
	public void smoothing(float theSmoothing) {
		try {
			_myGenerator.SetSmoothing(theSmoothing);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Start tracking at a specific position.
	 * @param thePosition
	 */
	public void startTracking(CCVector3f thePosition) {
		try {
			_myGenerator.StartTracking(convert(thePosition));
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public void historySize(int theHistorySize) {
		_myHistorySize = theHistorySize;
		
		for(CCOpenNIHand myHand:_myHandMap.values()) {
			myHand.historySize(theHistorySize);
		}
	}
	
	public Collection<CCOpenNIHand> hands(){
		return _myHandMap.values();
	}
}
