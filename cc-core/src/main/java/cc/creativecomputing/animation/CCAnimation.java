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
package cc.creativecomputing.animation;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.math.easing.CCEasing.CCEaseMode;

public class CCAnimation {
	private static int ourIdCounter;

	public static interface CCAnimationListener {
		public void onPlay(CCAnimation theAnimation);

		public void onFinish(CCAnimation theAnimation);

		public void onCancel(CCAnimation theAnimation);

		public void onProgress(CCAnimation theAnimation, float theProgress);
	}
	
	public static abstract class CCAnimationAdapter implements CCAnimationListener{

		@Override
		public void onPlay(CCAnimation theAnimation) {}

		@Override
		public void onFinish(CCAnimation theAnimation) {}

		@Override
		public void onCancel(CCAnimation theAnimation) {}

		@Override
		public void onProgress(CCAnimation theAnimation, float theProgress) {}
		
	}

	protected CCListenerManager<CCAnimationListener> _myEvents = CCListenerManager.create(CCAnimationListener.class);

	protected float _myProgress;
	private boolean _myRunning;
	private boolean _myFinished;
	private boolean _myLoop;
	protected float _myStartTime;
	protected float _myDuration;
	protected float _myDelay;
	protected float _myProgressTime;
	protected int _myId;

	private CCEasing _myEasing;
	private CCEaseMode _myEasingMode;
	
	public CCAnimation(float theDuration, CCEaseFormular theFormular, CCEaseMode theMode){
		_myDuration = theDuration;
		_myDelay = 0;
		_myEasing = theFormular.easing();
		_myEasingMode = theMode;
		_myId = ourIdCounter++;
		_myRunning = false;
		_myFinished = false;
		_myLoop = false;
	}
	
	public CCAnimation(float theDuration) {
		this(theDuration, CCEaseFormular.LINEAR, CCEaseMode.IN);
	}

	public CCAnimation() {
		this(0);
	}

	public CCListenerManager<CCAnimationListener> events() {
		return _myEvents;
	}

	public void update(float theTime) {
//		CCLog.info("Anim #: " + _myId + " " + theTime+":" + _myStartTime);
		_myProgressTime = CCMath.max(0, theTime - _myStartTime);
		if (_myDuration == 0.0f) {
			_myProgress = 1.0f;
		} else {
			_myProgress = _myEasing.ease(_myEasingMode, _myProgressTime / _myDuration);
		}
		_myEvents.proxy().onProgress(this, Math.min(_myProgress, 1.0f));
		if (_myProgressTime >= _myDuration) {
			finish(theTime);
		}
	}

	public void play(float theStartTime) {
//		CCLog.info("Anim #" + _myId + " set to play");

		_myStartTime = theStartTime;
		_myProgressTime = _myEasing.ease(_myEasingMode, 0.0f);
		_myProgress = _myProgressTime;
		_myRunning = true;
		_myFinished = false;

		_myEvents.proxy().onPlay(this);
	}

	public void finish(float theTime) {
		if (_myFinished) {
			return;
		}
		if (!_myRunning) {
			play(theTime);
		}
		finishAnimation(theTime);
	}

	public void cancel() {
		_myRunning = false;
		_myFinished = true;
		_myEvents.proxy().onCancel(this);
	}

	public boolean isRunning() {
		return _myRunning;
	}

	public boolean isFinished() {
		return _myFinished;
	}

	public void loop(boolean theLoop) {
		_myLoop = theLoop;
	}

	public float duration() {
		return _myDuration;
	}

	public void duration(float theDuration) {
		 _myDuration = theDuration;
	}
	
	public float progress(){
		return _myProgress;
	}

	public float delay() {
		return _myDelay;
	}

	public void delay(float theDelay) {
		 _myDelay = theDelay;
	}
	
	public void easeMode(CCEaseMode theEaseMode){
		_myEasingMode = theEaseMode;
	}
	
	public void easeFormular(CCEaseFormular theEaseFormular){
		_myEasing = theEaseFormular.easing();
	}

	public int id() {
		return _myId;
	}

	protected void setEasingMode(CCEaseFormular theFormular, CCEaseMode theEasingMode) {
		_myEasing = theFormular.easing();
		_myEasingMode = theEasingMode;
	}

	protected void finishAnimation(float theTime) {
		_myProgressTime = _myDuration;
		_myProgress = _myEasing.ease(_myEasingMode, 1.0f);
		_myEvents.proxy().onFinish(this);
		if (_myLoop) {
//			CCLog.info("Anim #" + _myId + " finished and looping");
			play(theTime);
		} else {
//			CCLog.info("Anim #" + _myId + " finished");
			_myRunning = false;
			_myFinished = true;
		}
	}
}
