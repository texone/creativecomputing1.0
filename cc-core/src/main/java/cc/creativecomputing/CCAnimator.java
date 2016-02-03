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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * <P>
 * An Animator can be attached to one or more {@linkGLAutoDrawable}s to drive
 * their display() methods in a loop.
 * </P>
 * <P>
 * The Animator class creates a background thread in which the calls to
 * <code>display()</code> are performed. After each drawable has been redrawn, a
 * brief pause is performed to avoid swamping the CPU, unless
 * {@link #setRunAsFastAsPossible} has been called.
 * </P>
 * 
 * @invisible
 */

public class CCAnimator {

	private volatile ArrayList<GLAutoDrawable> _myDrawables = new ArrayList<GLAutoDrawable>();
	private Thread _myMainLoop;
	private volatile boolean _myShouldStop;
	protected boolean ignoreExceptions;
	protected boolean _myPrintExceptions;
	private boolean runAsFastAsPossible;

	// For efficient rendering of Swing components, in particular when
	// they overlap one another
	private List<JComponent> _myLightweights = new ArrayList<JComponent>();
	private Map<RepaintManager, RepaintManager> repaintManagers = new IdentityHashMap<RepaintManager, RepaintManager>();
	private Map<JComponent, Rectangle> dirtyRegions = new IdentityHashMap<JComponent, Rectangle>();

	/** Creates a new, empty Animator. */
	public CCAnimator() {
	}

	/** Creates a new Animator for a particular drawable. */
	public CCAnimator(GLAutoDrawable drawable) {
		add(drawable);
	}

	/** Adds a drawable to the list managed by this Animator. */
	public synchronized void add(GLAutoDrawable drawable) {
		@SuppressWarnings("unchecked")
		ArrayList<GLAutoDrawable> myNewList = (ArrayList<GLAutoDrawable>) _myDrawables.clone();
		myNewList.add(drawable);
		_myDrawables = myNewList;
		notifyAll();
	}

	/** Removes a drawable from the list managed by this Animator. */
	public synchronized void remove(GLAutoDrawable theDrawable) {
		@SuppressWarnings("unchecked")
		ArrayList<GLAutoDrawable> myNewList = (ArrayList<GLAutoDrawable>) _myDrawables.clone();
		myNewList.remove(theDrawable);
		_myDrawables = myNewList;
	}

	/**
	 * Returns an iterator over the drawables managed by this Animator.
	 */
	public List<GLAutoDrawable> drawables() {
		return _myDrawables;
	}

	/**
	 * Sets a flag causing this Animator to ignore exceptions produced while
	 * redrawing the drawables. By default this flag is set to false, causing
	 * any exception thrown to halt the Animator.
	 */
	public void setIgnoreExceptions(boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}

	/**
	 * Sets a flag indicating that when exceptions are being ignored by this
	 * Animator (see {@link #setIgnoreExceptions}), to print the exceptions'
	 * stack traces for diagnostic information. Defaults to false.
	 */
	public void setPrintExceptions(boolean printExceptions) {
		_myPrintExceptions = printExceptions;
	}

	/**
	 * Sets a flag in this Animator indicating that it is to run as fast as
	 * possible. By default there is a brief pause in the animation loop which
	 * prevents the CPU from getting swamped. This method may not have an effect
	 * on subclasses.
	 */
	public final void setRunAsFastAsPossible(boolean runFast) {
		runAsFastAsPossible = runFast;
	}

	/**
	 * Called every frame to cause redrawing of all of the GLAutoDrawables this
	 * Animator manages. Subclasses should call this to get the most optimized
	 * painting behavior for the set of components this Animator manages, in
	 * particular when multiple lightweight widgets are continually being
	 * redrawn.
	 */
	protected void display() {
		for (int i = _myDrawables.size() - 1; i >= 0; i--) {
			GLAutoDrawable myDrawable = _myDrawables.get(i);
			if (myDrawable instanceof JComponent) {
				// Lightweight components need a more efficient drawing
				// scheme than simply forcing repainting of each one in
				// turn since drawing one can force another one to be
				// drawn in turn
				_myLightweights.add((JComponent) myDrawable);
			} else {
				try {
					myDrawable.display();
				} catch (Exception e) {
					e.printStackTrace();
					if (ignoreExceptions) {
						if (_myPrintExceptions) {
							e.printStackTrace();
						}
					} else {
						throw (e);
					}
				}
			}
		}
		if (_myLightweights.size() > 0) {
			try {
				SwingUtilities.invokeAndWait(drawWithRepaintManagerRunnable);
			} catch (Exception e) {
				e.printStackTrace();
			}
			_myLightweights.clear();
		}
	}

	private class MainLoop extends Thread {
		public void run() {
			try {
				while (!_myShouldStop) {
					try {
						// Don't consume CPU unless there is work to be done
						if (_myDrawables.size() == 0) {
							synchronized (CCAnimator.this) {
								while (_myDrawables.size() == 0 && !_myShouldStop) {
									try {
										CCAnimator.this.wait();
									} catch (InterruptedException e) {
									}
								}
							}
						}
						display();
						if (!runAsFastAsPossible) Thread.yield();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} finally {
				_myShouldStop = false;
				synchronized (CCAnimator.this) {
					_myMainLoop = null;
					CCAnimator.this.notify();
				}
			}
		}
	}

	/** 
	 * Starts this animator. 
	 **/
	public synchronized void start() {
		if (_myMainLoop == null) {
			_myMainLoop = new MainLoop();
		}
		if (!_myMainLoop.isAlive()) {
			_myMainLoop.start();
		}
	}

	/**
	 * Indicates whether this animator is currently running. This should only be
	 * used as a heuristic to applications because in some circumstances the
	 * Animator may be in the process of shutting down and this method will
	 * still return true.
	 */
	public synchronized boolean isAnimating() {
		return (_myMainLoop != null);
	}

	/**
	 * Stops this animator. In most situations this method blocks until
	 * completion, except when called from the animation thread itself or in
	 * some cases from an implementation-internal thread like the AWT event
	 * queue thread.
	 */
	public synchronized void stop() {
		_myShouldStop = true;
		notifyAll();
		// It's hard to tell whether the thread which calls stop() has
		// dependencies on the Animator's internal thread. Currently we
		// use a couple of heuristics to determine whether we should do
		// the blocking wait().
		if ((Thread.currentThread() == _myMainLoop) || EventQueue.isDispatchThread()) {
			return;
		}
		while (_myShouldStop && _myMainLoop != null) {
			try {
				wait();
			} catch (InterruptedException ie) {
			}
		}
	}

	// Uses RepaintManager APIs to implement more efficient redrawing of
	// the Swing widgets we're animating
	private Runnable drawWithRepaintManagerRunnable = new Runnable() {
		public void run() {
			for (JComponent comp : _myLightweights) {
				RepaintManager rm = RepaintManager.currentManager(comp);
				rm.markCompletelyDirty(comp);
				repaintManagers.put(rm, rm);

				// RepaintManagers don't currently optimize the case of
				// overlapping sibling components. If we have two
				// JInternalFrames in a JDesktopPane, the redraw of the
				// bottom one will cause the top one to be redrawn as
				// well. The top one will then be redrawn separately. In
				// order to optimize this case we need to compute the union
				// of all of the dirty regions on a particular JComponent if
				// optimized drawing isn't enabled for it.

				// Walk up the hierarchy trying to find a non-optimizable
				// ancestor
				Rectangle visible = comp.getVisibleRect();
				int x = visible.x;
				int y = visible.y;
				while (comp != null) {
					x += comp.getX();
					y += comp.getY();
					Component c = comp.getParent();
					if ((c == null) || (!(c instanceof JComponent))) {
						comp = null;
					} else {
						comp = (JComponent) c;
						if (!comp.isOptimizedDrawingEnabled()) {
							rm = RepaintManager.currentManager(comp);
							repaintManagers.put(rm, rm);
							// Need to dirty this region
							Rectangle dirty = (Rectangle) dirtyRegions.get(comp);
							if (dirty == null) {
								dirty = new Rectangle(x, y, visible.width, visible.height);
								dirtyRegions.put(comp, dirty);
							} else {
								// Compute union with already dirty region
								// Note we could compute multiple
								// non-overlapping
								// regions: might want to do that in the future
								// (prob. need more complex algorithm -- dynamic
								// programming?)
								dirty.add(new Rectangle(x, y, visible.width, visible.height));
							}
						}
					}
				}
			}

			// Dirty any needed regions on non-optimizable components
			for (JComponent comp : dirtyRegions.keySet()) {
				Rectangle rect = (Rectangle) dirtyRegions.get(comp);
				RepaintManager rm = RepaintManager.currentManager(comp);
				rm.addDirtyRegion(comp, rect.x, rect.y, rect.width, rect.height);
			}

			// Draw all dirty regions
			for (RepaintManager rm : repaintManagers.keySet()) {
				rm.paintDirtyRegions();
			}
			dirtyRegions.clear();
			repaintManagers.clear();
		}
	};
}
