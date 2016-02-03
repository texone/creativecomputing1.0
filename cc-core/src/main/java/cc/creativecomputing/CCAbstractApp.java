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

import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCSetupListener;
import cc.creativecomputing.events.CCSizeListener;
import cc.creativecomputing.events.CCUpdateListener;

/**
 * @author info
 *
 */
public abstract class CCAbstractApp{

	protected CCListenerManager<CCSetupListener> _mySetupListener;
	protected CCListenerManager<CCUpdateListener> _myUpdateListener;
	protected CCListenerManager<CCDisposeListener> _myDisposeListener;
	protected CCListenerManager<CCKeyListener> _myKeyListener;
	
	protected CCAbstractApp() {
		_mySetupListener = CCListenerManager.create(CCSetupListener.class);
		_myUpdateListener = CCListenerManager.create(CCUpdateListener.class);
		_myDisposeListener = CCListenerManager.create(CCDisposeListener.class);
		_myKeyListener = CCListenerManager.create(CCKeyListener.class);
	}

	/**
	 * Returns the current x coordinate of the application window.
	 * @return x coordinate of the application window
	 */
	public abstract int windowX();

	/**
	 * Returns the current y coordinate of the application window.
	 * @return y coordinate of the application window
	 */
	public abstract int windowY();

	/**
	 * Returns the current width of the application window.
	 * @return width of the application window
	 */
	public abstract int windowWidth();

	/**
	 * Returns the current height of the application window.
	 * @return height of the application window
	 */
	public abstract int windowHeight();

	/**
	 * Adds a listener to the application, to react to the initialization of the app. 
	 * This can be used to forward the setup event to 
	 * different objects. To do so the class needs to implement
	 * the {@link CCSetupListener} interface and must be added
	 * as listener.
	 * @param theSizeListener the listener for size events
	 * @see #resize(int, int)
	 * @see #addUpdateListener(CCUpdateListener)
	 * @see CCSizeListener
	 */
	public void addSetupListener(final CCSetupListener theSetupListener) {
		_mySetupListener.add(theSetupListener);
	}
	
	public void removeSetupListener(final CCSetupListener theSetupListener) {
		_mySetupListener.remove(theSetupListener);
	}

	/**
	 * Adds a listener to the application, to call program updates. 
	 * This can be used to forward the update event to 
	 * different objects. To do so the class needs to implement
	 * the <code>CCUpdateListener</code> interface and must be added
	 * as listener. The update call happens before the draw call.
	 * So this can be used for physics or similar simulation processes.
	 * @shortdesc adds a listener reacting to update events
	 * @param theUpdateListener the listener for update events
	 * @see #update(float)
	 * @see CCUpdateListener
	 */
	public void addUpdateListener(final CCUpdateListener theUpdateListener) {
		_myUpdateListener.add(theUpdateListener);
	}
	
	public void removeUpdateListener(final CCUpdateListener theUpdateListener) {
		_myUpdateListener.remove(theUpdateListener);
	}
	
	public CCListenerManager<CCUpdateListener> updateEvents(){
		return _myUpdateListener;
	}

	/**
	 * Adds a listener to react to application stop. This should shut down
	 * any threads, disconnect from the net, unload memory, etc. 
	 * @shortdesc adds a listener reacting to application stop
	 * @param theDisposeListener the listener for dispose events
	 * @see #finish()
	 * @see CCDisposeListener
	 */
	public void addDisposeListener(final CCDisposeListener theDisposeListener) {
		_myDisposeListener.add(theDisposeListener);
	}
	
	public void removeDisposeListener(final CCDisposeListener theDisposeListener) {
		_myDisposeListener.remove(theDisposeListener);
	}
}
