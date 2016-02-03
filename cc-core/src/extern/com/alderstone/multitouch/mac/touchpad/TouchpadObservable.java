//  Copyright 2009 Wayne Keenan
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
//  TouchpadObservable.java
//
//  Created by Wayne Keenan on 27/05/2009.
//
package com.alderstone.multitouch.mac.touchpad;

import java.util.*;

import cc.creativecomputing.input.touch.CCTouch;
import cc.creativecomputing.input.touch.CCTouchProvider;

public class TouchpadObservable extends CCTouchProvider {

	private static volatile Object initGuard;
	private static volatile boolean loaded = false;
	
	private static boolean isInitialized = false;
	private static TouchpadObservable instance;

	public TouchpadObservable() {
		startupNative();
		instance = this;
	}

	static {
		initGuard = new Object();
		System.loadLibrary("GlulogicMT");
	}

	private static void startupNative() {
		synchronized (initGuard) {
			if (!loaded) {
				loaded = true;
				registerListener();
				ShutdownHook shutdownHook = new ShutdownHook();
				Runtime.getRuntime().addShutdownHook(shutdownHook);
			}
		}
	}

	private static void shutdownNative() {
		deregisterListener();
	}

	public static void mtcallback(
		int frame, 
		double timestamp, 
		int id, 
		int state, 
		float size, 
		float x, float y, 
		float dx, float dy, 
		float angle, 
		float majorAxis, float minorAxis
	) {
		instance.update(frame, timestamp, id, state, size, x, y, dx, dy, angle, majorAxis, minorAxis);
	}

	// native methods

	native static int registerListener();

	native static int deregisterListener();

	// shutdown hook
	static class ShutdownHook extends Thread {
		public void run() {
			shutdownNative();
		}
	}
	
	private Map<Integer, CCTouch> _myTouchMap = new HashMap<Integer, CCTouch>();

	// Observer interface code
	
	private static final int UNKNOWN_1 = 1;
	private static final int HOVER = 2;
	private static final int TAP = 3;
	private static final int PRESSED = 4;
	private static final int PRESSING = 5;
	private static final int RELEASING = 6;
	private static final int RELEASED = 7;

	public void update(
		int theFrame, double theTimeStamp, 
		int theID, int theState, float theSize, 
		float theX, float theY, 
		float theVelocityX, float theVelocityY, 
		float theAngle, 
		float theMajorAxis, float theMinorAxis
	) {
		switch (theState) {
		case UNKNOWN_1:
			CCTouch myTouch = new CCTouch(theID);
			myTouch.frame(theFrame);
			myTouch.timeStamp(theTimeStamp);
			myTouch.size(theSize);
			myTouch.position().set(theX, theY);
			myTouch.velocity().set(theVelocityX, theVelocityY);
			myTouch.angle(theAngle);
			myTouch.majorAxis(theMajorAxis);
			myTouch.minorAxis(theMinorAxis);
			_myTouchManager.onTouchAdd(myTouch);
			break;
		case PRESSED:
		case PRESSING:
		case RELEASING:
		case TAP:
			myTouch = _myTouchManager.touch(theID);
			myTouch.frame(theFrame);
			myTouch.timeStamp(theTimeStamp);
			myTouch.size(theSize);
			myTouch.position().set(theX, theY);
			myTouch.velocity().set(theVelocityX, theVelocityY);
			myTouch.angle(theAngle);
			myTouch.majorAxis(theMajorAxis);
			myTouch.minorAxis(theMinorAxis);
			_myTouchManager.onTouchUpdate(myTouch);
			break;
		case HOVER:
			System.out.println("HOVER");
			break;
		
		case RELEASED:
			myTouch = _myTouchManager.touch(theID);
			_myTouchManager.onTouchRemove(myTouch);
			break;
		default:
			System.out.println("UNKNOWN");
			break;
		}
		// setChanged();
		// notifyObservers(new CCTouch(frame, timestamp, id, state, size, x, y, dx, dy, angle, majorAxis, minorAxis));
	}

}
