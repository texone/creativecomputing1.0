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

import org.openni.GeneralException;
import org.openni.GestureGenerator;
import org.openni.GestureProgressEventArgs;
import org.openni.GestureRecognizedEventArgs;
import org.openni.IObservable;
import org.openni.IObserver;
import org.openni.StatusException;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * An object that enables specific body or hand gesture tracking
 * @author christianriekoff
 * 
 */
public class CCOpenNIGestureGenerator extends CCOpenNIGenerator<GestureGenerator>{
	
	public static String WAVE = "Wave";
	public static String CLICK = "Click";
	public static String RAISE_HAND = "RaiseHand";

//	private CCOpenNIHandGenerator _myHandGenerator;
	
	@SuppressWarnings("unused")
	private float _myProgress;
	@SuppressWarnings("unused")
	private String _myGesture;
	
	private CCListenerManager<CCOpenNIGestureListener> _myEvents = new CCListenerManager<CCOpenNIGestureListener>(CCOpenNIGestureListener.class);

	/**
	 * @param theOpenNI
	 * @param theHandGenerator
	 */
	public CCOpenNIGestureGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.openni2.CCOpenNIGenerator#create(org.OpenNI.Context)
	 */
	@Override
	GestureGenerator create(CCOpenNI theOpenNI) {
		try {
			GestureGenerator myGestureGenerator;
			if(theOpenNI.deviceQuery() == null)myGestureGenerator = GestureGenerator.create(theOpenNI.context());
			else myGestureGenerator = GestureGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
			
			myGestureGenerator.getGestureRecognizedEvent().addObserver(new IObserver<GestureRecognizedEventArgs>() {
				
				public void update(IObservable<GestureRecognizedEventArgs> theObservable, GestureRecognizedEventArgs theArgs) {
					String myGesture = theArgs.getGesture();
					CCVector3f myIdPosition = convert(theArgs.getIdPosition());
					CCVector3f myEndPosition3D = convert(theArgs.getEndPosition());
					
					CCLog.info("onRecognizeGesture - strGesture: " + myGesture + ", idPosition: " + myIdPosition + ", endPosition:" + myEndPosition3D);
					_myEvents.proxy().onRecognizeGesture(myGesture, myIdPosition, myEndPosition3D);
					_myGesture = myGesture;
				}
			});
			
			myGestureGenerator.getGestureProgressEvent().addObserver(new IObserver<GestureProgressEventArgs>() {

				public void update(IObservable<GestureProgressEventArgs> theObservable, GestureProgressEventArgs theArgs) {
					String myGesture = theArgs.getGesture();
					CCVector3f myPosition = convert(theArgs.getPosition());
					float myProgress = theArgs.getProgress();
					
					CCLog.info("onProgressGesture - strGesture: " + myGesture + ", position: " + myPosition + ", progress:" + myProgress);
					_myEvents.proxy().onProgressGesture(myGesture, myPosition, myProgress);
					_myGesture = myGesture;
					_myProgress = myProgress;
				}});
			return myGestureGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCListenerManager<CCOpenNIGestureListener> events(){
		return _myEvents;
	}
	
	/**
	 * Get the names of the gestures that are currently active.
	 * @return
	 */
	public String[] activeGestures() {
		try {
			return _myGenerator.getAllActiveGestures();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Get the names of all gestures available.
	 * @return
	 */
	public String[] enumerateAllGestures() {
		try {
			return _myGenerator.enumerateAllGestures();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Check if a specific gesture is available in this generator.
	 * @param theGesture
	 * @return
	 */
	public boolean isGestureAvailable(String theGesture) {
		return _myGenerator.isGestureAvailable(theGesture);
	}
	
	/**
	 * Check if the specific gesture supports 'in progress' callbacks.
	 * @param theGesture
	 * @return
	 */
	public boolean isGestureProgressSupported(String theGesture) {
		return _myGenerator.isGestureProgressSupported(theGesture);
	}
	
	/**
	 * Turn on a gesture. Once turned on, the generator will start looking for this gesture.
	 * @param theGestures
	 */
	public void addGestures(String...theGestures) {
		for(String theGesture:theGestures) {
			try {
				_myGenerator.addGesture(theGesture);
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
	
	/**
	 * Turn off a gesture. Once turned off, the generator will stop looking for this gesture.
	 * @param theGestures
	 */
	public void removeGestures(String...theGestures) {
		for(String theGesture:theGestures) {
			try {
				_myGenerator.removeGesture(theGesture);
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}
}
