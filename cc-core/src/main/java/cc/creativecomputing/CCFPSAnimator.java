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

import java.util.*;
import javax.media.opengl.*;


/** 
 * An Animator subclass which attempts to achieve a target
 * frames-per-second rate to avoid using all CPU time. The target FPS
 * is only an estimate and is not guaranteed. 
 * @invisible
 **/
public class CCFPSAnimator extends CCAnimator {
	private Timer timer;
	private int fps;
	private boolean scheduleAtFixedRate;

	/** 
	 * Creates an FPSAnimator with a given target frames-per-second
	 *  value. Equivalent to <code>FPSAnimator(null, fps)</code>. 
	 **/
	public CCFPSAnimator(int fps) {
		this(null, fps);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value and a flag indicating whether to use fixed-rate
	    scheduling. Equivalent to <code>FPSAnimator(null, fps,
	    scheduleAtFixedRate)</code>. */
	public CCFPSAnimator(int fps, boolean scheduleAtFixedRate) {
		this(null, fps, scheduleAtFixedRate);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value and an initial drawable to animate. Equivalent to
	    <code>FPSAnimator(null, fps, false)</code>. */
	public CCFPSAnimator(GLAutoDrawable drawable, int fps) {
		this(drawable, fps, false);
	}

	/** Creates an FPSAnimator with a given target frames-per-second
	    value, an initial drawable to animate, and a flag indicating
	    whether to use fixed-rate scheduling. */
	public CCFPSAnimator(GLAutoDrawable drawable, int fps, boolean scheduleAtFixedRate) {
		this.fps = fps;
		if (drawable != null) {
			add(drawable);
		}
		this.scheduleAtFixedRate = scheduleAtFixedRate;
	}

	/** Starts this FPSAnimator. */
	public synchronized void start() {
		if (timer != null) {
			throw new GLException("Already started");
		}
		timer = new Timer();
		long delay = (long) (1000.0f / (float) fps);
		TimerTask task = new TimerTask() {
			public void run() {
				display();
			}
		};
		if (scheduleAtFixedRate) {
			timer.scheduleAtFixedRate(task, 0, delay);
		} else {
			timer.schedule(task, 0, delay);
		}
	}

	/** Indicates whether this FPSAnimator is currently running. This
	    should only be used as a heuristic to applications because in
	    some circumstances the FPSAnimator may be in the process of
	    shutting down and this method will still return true. */
	public synchronized boolean isAnimating() {
		return (timer != null);
	}

	/** Stops this FPSAnimator. Due to the implementation of the
	    FPSAnimator it is not guaranteed that the FPSAnimator will be
	    completely stopped by the time this method returns. */
	public synchronized void stop() {
		if (timer == null) {
			throw new GLException("Already stopped");
		}
		timer.cancel();
		timer = null;
	}
}
