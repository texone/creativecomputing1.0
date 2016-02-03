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
package cc.creativecomputing;

import java.util.Timer;
import java.util.TimerTask;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.util.logging.CCLog;


/** 
 * An Animator subclass which attempts to achieve a target
 * frames-per-second rate to avoid using all CPU time. The target FPS
 * is only an estimate and is not guaranteed. 
 * @invisible
 **/
public class CCUpdateAnimator{
	private Timer _myTimer;
	private int _myFPS;
	private boolean _myScheduleAtFixedRate;
	
	private CCListenerManager<CCUpdateListener> _myEvents = CCListenerManager.create(CCUpdateListener.class);

	/** 
	 * Creates an FPSAnimator with a given target frames-per-second
	 *  value. Equivalent to <code>FPSAnimator(null, fps)</code>. 
	 **/
	public CCUpdateAnimator(int fps) {
		this(null, fps);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value and a flag indicating whether to use fixed-rate
	    scheduling. Equivalent to <code>FPSAnimator(null, fps,
	    scheduleAtFixedRate)</code>. */
	public CCUpdateAnimator(int fps, boolean scheduleAtFixedRate) {
		this(null, fps, scheduleAtFixedRate);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value and an initial drawable to animate. Equivalent to
	    <code>FPSAnimator(null, fps, false)</code>. */
	public CCUpdateAnimator(CCUpdateListener theUpdateListener, int fps) {
		this(theUpdateListener, fps, false);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value, an initial drawable to animate, and a flag indicating
	    whether to use fixed-rate scheduling. */
	public CCUpdateAnimator(CCUpdateListener theUpdateListener, int fps, boolean scheduleAtFixedRate) {
		_myFPS = fps;
		if (theUpdateListener != null) {
			_myEvents.add(theUpdateListener);
		}
		_myScheduleAtFixedRate = scheduleAtFixedRate;
	}

	/** Starts this FPSAnimator. */
	public synchronized void start() {
		if (_myTimer != null) {
			CCLog.info("Already started");
		}
		_myTimer = new Timer();
		long delay = (long) (1000.0f / (float) _myFPS);
		TimerTask task = new TimerTask() {
			public void run() {
				_myEvents.proxy().update(1f / _myFPS);
			}
		};
		if (_myScheduleAtFixedRate) {
			_myTimer.scheduleAtFixedRate(task, 0, delay);
		} else {
			_myTimer.schedule(task, 0, delay);
		}
	}

	/** Indicates whether this FPSAnimator is currently running. This
	    should only be used as a heuristic to applications because in
	    some circumstances the FPSAnimator may be in the process of
	    shutting down and this method will still return true. */
	public synchronized boolean isAnimating() {
		return (_myTimer != null);
	}

	/** Stops this FPSAnimator. Due to the implementation of the
	    FPSAnimator it is not guaranteed that the FPSAnimator will be
	    completely stopped by the time this method returns. */
	public synchronized void stop() {
		if (_myTimer == null) {
			CCLog.info("Already stopped");
		}
		_myTimer.cancel();
		_myTimer = null;
	}
}
