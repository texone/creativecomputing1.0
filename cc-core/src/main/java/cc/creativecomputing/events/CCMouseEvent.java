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

import cc.creativecomputing.math.CCVector2f;


/**
 * An event which indicates that a mouse action occurred.
 * A mouse action is considered to occur in a an application if and only
 * if the mouse cursor is over the unobscured part of the application window
 * when the action happens. This event is used both for mouse events 
 * (press, release, click, enter, exit) and mouse motion events (moves and drags). 
 * </p>
 * <p>
 * A mouse event object is passed to every mouse listener object which is 
 * registered to receive the mouse events using the application's 
 * <code>addMouseListener</code> method. Each such listener object 
 * gets a mouse event object containing the mouse event.
 * </p>
 * <p>
 * When a mouse button is clicked, events are generated and sent to the
 * registered mouse listeners.
 * The button which has changed state is returned by <code>button()</code>.
 * When multiple mouse buttons are pressed, each press, release, and click
 * results in a separate event. 
 * </p>
 * <p>
 * Be aware that window coordinates of the mouse and the coordinate system for
 * drawing do not match so that you need to perform calculation.
 * </p>
 *   
 * @example basics.CCMouseEventTest
 * @see CCMouseListener
 * @see CCMouseMotionListener
 */

public class CCMouseEvent extends CCEvent{
	
	public static final String MOUSE_EVENT = "MOUSE_EVENT";
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static enum CCMouseButton{
		/**
		 * constant indicating that the left mouse button is pressed
		 */
		LEFT,
		
		/**
		 * constant indicating that the middle mouse button is pressed
		 */
		CENTER,
		
		/**
		 * constant indicating that the right mouse button is pressed
		 */
		RIGHT;
		
		public static CCMouseButton valueOf(final int theAwtID) {
			switch(theAwtID){
			case java.awt.event.MouseEvent.BUTTON1:
				return LEFT;
			case java.awt.event.MouseEvent.BUTTON2:
				return CENTER;
			case java.awt.event.MouseEvent.BUTTON3:
				return  RIGHT;
			default:
				return LEFT;
			}
		}
	}
	
	public static enum CCMouseEventType{
		/**
	     * The "mouse clicked" event. This <code>MouseEvent</code>
	     * occurs when a mouse button is pressed and released.
	     */
		MOUSE_CLICKED,

	    /**
	     * The "mouse pressed" event. This <code>MouseEvent</code>
	     * occurs when a mouse button is pushed down.
	     */
	    MOUSE_PRESSED,

	    /**
	     * The "mouse released" event. This <code>MouseEvent</code>
	     * occurs when a mouse button is let up.
	     */
	    MOUSE_RELEASED,

	    /**
	     * The "mouse moved" event. This <code>MouseEvent</code>
	     * occurs when the mouse position changes.
	     */
	    MOUSE_MOVED,

	    /**
	     * The "mouse entered" event. This <code>MouseEvent</code>
	     * occurs when the mouse cursor enters the unobscured part of component's
	     * geometry. 
	     */
	    MOUSE_ENTERED,

	    /**
	     * The "mouse exited" event. This <code>MouseEvent</code>
	     * occurs when the mouse cursor exits the unobscured part of component's
	     * geometry.
	     */
	    MOUSE_EXITED,

	    /**
	     * The "mouse dragged" event. This <code>MouseEvent</code>
	     * occurs when the mouse position changes while a mouse button is pressed.
	     */
	    MOUSE_DRAGGED,

	    /**
	     * The "mouse wheel" event.  This is the only <code>MouseWheelEvent</code>.
	     * It occurs when a mouse equipped with a wheel has its wheel rotated.
	     */
	    MOUSE_WHEEL;
	    
	    
	    public static CCMouseEventType valueOf(final int theAWTID) {
	    	switch(theAWTID) {
	    	case java.awt.event.MouseEvent.MOUSE_CLICKED : return MOUSE_CLICKED;
	    	case java.awt.event.MouseEvent.MOUSE_PRESSED : return MOUSE_PRESSED;
	    	case java.awt.event.MouseEvent.MOUSE_RELEASED : return MOUSE_RELEASED;
	    	case java.awt.event.MouseEvent.MOUSE_MOVED : return MOUSE_MOVED;
	    	case java.awt.event.MouseEvent.MOUSE_ENTERED : return MOUSE_ENTERED;
	    	case java.awt.event.MouseEvent.MOUSE_EXITED : return MOUSE_ENTERED;
	    	case java.awt.event.MouseEvent.MOUSE_DRAGGED : return MOUSE_DRAGGED;
	    	case java.awt.event.MouseEvent.MOUSE_WHEEL : return MOUSE_WHEEL;
	    	}
	    	throw new RuntimeException("Unrecognized mouse eventtype");
	    }
	}
	
	/**
	 * @invisible
	 */
	public static final CCMouseButton LEFT = CCMouseButton.LEFT;
	/**
	 * @invisible
	 */
	public static final CCMouseButton CENTER = CCMouseButton.CENTER;
	/**
	 * @invisible
	 */
	public static final CCMouseButton RIGHT = CCMouseButton.RIGHT;	
	
	private final CCMouseButton _myButton;
	private final CCMouseEventType _myEventType;
	
	private final CCVector2f _myPosition;
	private final CCVector2f _myPreviousPosition;
	
	private int _myClickCount;
	
	private boolean _myIsAltDown;
	private boolean _myIsAltGraphDown;
	private boolean _myIsShiftDown;
	private boolean _myIsCtrlDown;
	private boolean _myIsMetaDown;
	
	/**
	 * @invisible
	 * @param theMouseEvent
	 * @param theX
	 * @param theY
	 * @param thePX
	 * @param thePY
	 */
	public CCMouseEvent(
		final java.awt.event.MouseEvent theMouseEvent,
		final int thePX, final int thePY
	){
		this(theMouseEvent, thePX, thePY, theMouseEvent.getButton());
	}
	
	public CCMouseEvent(
		final java.awt.event.MouseEvent theMouseEvent,
		final int thePX, final int thePY,
		final int theButton
	){
		super(MOUSE_EVENT);
		_myPosition = new CCVector2f(theMouseEvent.getX(),theMouseEvent.getY());
		_myPreviousPosition = new CCVector2f(thePX, thePY);

		_myEventType = CCMouseEventType.valueOf(theMouseEvent.getID());
		_myButton = CCMouseButton.valueOf(theButton);
		
		_myClickCount = theMouseEvent.getClickCount();
		
		_myIsAltDown = theMouseEvent.isAltDown();
		_myIsAltGraphDown = theMouseEvent.isAltGraphDown();
		_myIsCtrlDown = theMouseEvent.isControlDown();
		_myIsShiftDown = theMouseEvent.isShiftDown();
		_myIsMetaDown = theMouseEvent.isMetaDown();
	}
	
	/**
	 * @invisible
	 * @param theMouseEvent
	 * @param theX
	 * @param theY
	 * @param thePX
	 * @param thePY
	 */
	public CCMouseEvent(
		final com.jogamp.newt.event.MouseEvent theMouseEvent,
		final int thePX, final int thePY,
		CCMouseEventType theType
	){
		this(theMouseEvent, thePX, thePY, theMouseEvent.getButton(),theType);
	}
	
	public CCMouseEvent(
		final com.jogamp.newt.event.MouseEvent theMouseEvent,
		final int thePX, final int thePY,
		final int theButton,
		CCMouseEventType theType
	){
		super(MOUSE_EVENT);
		_myPosition = new CCVector2f(theMouseEvent.getX(),theMouseEvent.getY());
		_myPreviousPosition = new CCVector2f(thePX, thePY);

		_myEventType = theType;
		_myButton = CCMouseButton.valueOf(theButton);
		
		_myClickCount = theMouseEvent.getClickCount();
		
		_myIsAltDown = theMouseEvent.isAltDown();
		_myIsAltGraphDown = theMouseEvent.isAltGraphDown();
		_myIsCtrlDown = theMouseEvent.isControlDown();
		_myIsShiftDown = theMouseEvent.isShiftDown();
		_myIsMetaDown = theMouseEvent.isMetaDown();
	}
	
	private CCMouseEvent(CCMouseEvent theEvent){
		super(MOUSE_EVENT);
		_myPosition = theEvent.position().clone();
		_myPreviousPosition = theEvent.pPosition().clone();
		
		_myEventType = theEvent.eventType();
		_myButton = theEvent.button();
		
		_myIsAltDown = theEvent.isAltDown();
		_myIsAltGraphDown = theEvent.isAltGraphDown();
		_myIsCtrlDown = theEvent.isCtrlDown();
		_myIsShiftDown = theEvent.isShiftDown();
	}
	
	public int clickCount() {
		return _myClickCount;
	}
	
	/**
	 * Returns the horizontal coordinate of the mouse position.
	 * @return horizontal coordinate of the mouse
	 * @example events.CCMousePosition
	 * @see #y()
	 * @see #position()
	 */
	public int x(){
		return (int)_myPosition.x;
	}
	
	/**
	 * Returns the vertical coordinate of the mouse position
	 * @return vertical coordinate of the mouse position
	 * @example events.CCMousePosition
	 * @see #x()
	 * @see #position()
	 */
	public int y(){
		return (int)_myPosition.y;
	}
	
	/**
	 * Returns the previous horizontal coordinate of the mouse 
	 * @return previous horizontal coordinate of the mouse
	 * @example events.CCMousePosition
	 * @see #pPosition()
	 * @see #py()
	 */
	public int px(){
		return (int)_myPreviousPosition.x;
	}
	
	/**
	 * Returns the previous vertical coordinate of the mouse 
	 * @return previous vertical coordinate of the mouse
	 * @example events.CCMousePosition
	 * @see #pPosition()
	 * @see #px()
	 */
	public int py(){
		return (int)_myPreviousPosition.y;
	}
	
	/**
	 * Returns the current mouse position.
	 * @return current position of the mouse
	 * @example events.CCMousePositionV
	 * @see #x()
	 * @see #y()
	 */
	public CCVector2f position(){
		return _myPosition;
	}
	
	/**
	 * Returns the previous position of the mouse.
	 * @return previous position of the mouse
	 * @example events.CCMousePositionV
	 * @see #px()
	 * @see #py()
	 */
	public CCVector2f pPosition(){
		return _myPreviousPosition;
	}
	
	/**
	 * Returns the movement of the mouse since the last mouse motion  event as vector.
	 * This is interesting for mouse motion events like mouse move and
	 * mouse drag.
	 * @return the movement since the last mouse motion event
	 * @example events.CCMouseMovement
	 */
	public CCVector2f movement(){
		return new CCVector2f(
			_myPosition.x - _myPreviousPosition.x,
			_myPosition.y - _myPreviousPosition.y
		);
	}
	
	/**
	 * Returns the pressed button. Can be either LEFT, CENTER or RIGHT.
	 * @return the pressed button of the mouse
	 * @example events.CCMouseButtonTest
	 */
	public CCMouseButton button(){
		return _myButton;
	}
	
	/**
	 * @invisible
	 * @return
	 */
	public CCMouseEventType eventType() {
		return _myEventType;
	}
	
	public CCMouseEvent clone() {
		return new CCMouseEvent(this);
	}

	/**
	 * Returns whether or not the Alt modifier is down on this event.
	 * @return the isAltDown
	 */
	public boolean isAltDown() {
		return _myIsAltDown;
	}

	/**
	 * Returns whether or not the AltGraph modifier is down on this event.
	 * @return the isAltGraphDown
	 */
	public boolean isAltGraphDown() {
		return _myIsAltGraphDown;
	}

	/**
	 * Returns whether or not the Shift modifier is down on this event.
	 * @return the isShiftDown
	 */
	public boolean isShiftDown() {
		return _myIsShiftDown;
	}

	/**
	 * Returns whether or not the Control modifier is down on this event.
	 * @return the isCtrlDown
	 */
	public boolean isCtrlDown() {
		return _myIsCtrlDown;
	}

	/**
	 * Returns whether or not the Meta modifier is down on this event.
	 * @return the isCtrlDown
	 */
	public boolean isMetaDown() {
		return _myIsMetaDown;
	}
}
