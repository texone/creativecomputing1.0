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
package cc.creativecomputing.events;

import cc.creativecomputing.CCApp;

/**
 * <p>
 * Mouse events are generated for mouse presses releases and clicks
 * inside the application window. They also occur when the mouse
 * enters or exits the application window. Classes that are interested 
 * in processing these events implement this interface (and all the
 *  methods it contains).
 * </p>
 * <p>
 * The listener object created from that class is then registered 
 * using the application's <code>addMouseListener</code> 
 * method. A mouse event is generated when the mouse is pressed, released
 * clicked (pressed and released). A mouse event is also generated when
 * the mouse cursor enters or leaves a component. When a mouse event
 * occurs, the relevant method in the listener object is invoked, and 
 * the <code>CCMouseEvent</code> is passed to it.
 * </p>
 * @author texone
 * @see CCApp#addMouseListener(CCMouseListener)
 * @example events.CCMousePressReleaseClick
 */
public interface CCMouseListener{
	/**
	 * Invoked when a mouse button has been pressed on the application window.
	 * @param theEvent the related mouse event
	 * @example events.CCMousePressReleaseClick
	 * @see CCMouseEvent
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 */
	public void mousePressed(final CCMouseEvent theEvent);
	
	/**
	 * Invoked when a mouse button has been released on the application window.
	 * @param theEvent the related mouse event
	 * @example events.CCMousePressReleaseClick
	 * @see CCMouseEvent
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 */
	public void mouseReleased(final CCMouseEvent theEvent);
	
	/**
	 * Invoked when the mouse button has been clicked (pressed
     * and released) on the application window.
	 * @param theEvent the related mouse event
	 * @example events.CCMousePressReleaseClick
	 * @see CCMouseEvent
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 */
	public void mouseClicked(final CCMouseEvent theEvent);
	
	/**
	 * Invoked when the mouse enters the application window.
	 * @param theEvent the related mouse event
	 * @see CCMouseEvent
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @example events.CCMouseEnterExit
	 */
	public void mouseEntered(final CCMouseEvent theEvent);
	
	/**
     * Invoked when the mouse exits the application window.
	 * @param theEvent the related mouse event
	 * @see CCMouseEvent
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @example events.CCMouseEnterExit
	 */
	public void mouseExited(final CCMouseEvent theEvent);
}
