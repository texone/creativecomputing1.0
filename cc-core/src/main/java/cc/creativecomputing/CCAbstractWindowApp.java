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

import java.util.LinkedList;
import java.util.Queue;

import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCMouseWheelEvent;
import cc.creativecomputing.events.CCMouseWheelListener;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCSizeListener;
import cc.creativecomputing.events.CCUpdateListener;

/**
 * @author info
 *
 */
public abstract class CCAbstractWindowApp extends CCAbstractApp{

	protected CCListenerManager<CCSizeListener> _mySizeListener;
	protected CCListenerManager<CCPostListener> _myPostListener;
	protected CCListenerManager<CCMouseListener> _myMouseListener;
	protected CCListenerManager<CCMouseMotionListener> _myMouseMotionListener;
	protected CCListenerManager<CCMouseWheelListener> _myMouseWheelListener;
	protected CCListenerManager<CCKeyListener> _myKeyListener;
	
	private Queue<CCMouseEvent> _myMouseEventQueue = new LinkedList<CCMouseEvent>();
	private Queue<CCMouseWheelEvent> _myMouseWheelEventQueue = new LinkedList<CCMouseWheelEvent>();
	private Queue<CCKeyEvent> _myKeyEventQueue = new LinkedList<CCKeyEvent>();

	/**
	 * System variable which stores the width of the display window. 
	 * This value is set by the first parameter of the <code>setSize</code> 
	 * function of the <code>CCApplicationManager</code>. For example, 
	 * the function call <code>setSize(320, 240)</code> sets the 
	 * <code>width</code> variable to the value 320. 
	 * @shortdesc System variable which stores the width of the display window. 
	 * @see #height
	 * @see CCApplicationSettings#size(int, int)
	 * @example basics.CCWidthHeight
	 */
	public int width;
	
	/**
	 * System variable which stores the height of the display window. 
	 * This value is set by the second parameter of the <code>setSize</code> 
	 * function of the <code>CCApplicationManager</code>. For example, 
	 * the function call <code>setSize(320, 240)</code> sets the 
	 * <code>width</code> variable to the value 240. 
	 * @shortdesc System variable which stores the height of the display window. 
	 * @see #width
	 * @see CCApplicationSettings#size(int, int)
	 * @example basics.CCWidthHeight
	 */
	public int height;
	
	protected CCAbstractWindowApp() {
		super();
		_mySizeListener = CCListenerManager.create(CCSizeListener.class);
		_myPostListener = CCListenerManager.create(CCPostListener.class);
		_myMouseListener = CCListenerManager.create(CCMouseListener.class);
		_myMouseMotionListener = CCListenerManager.create(CCMouseMotionListener.class);
		_myMouseWheelListener = CCListenerManager.create(CCMouseWheelListener.class);
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
	 * Adds a listener to the application, to react to changes on the
	 * window size. This can be used to forward the size event to 
	 * different objects. To do so the class needs to implement
	 * the <code>CCSizeListener</code> interface and must be added
	 * as listener.
	 * @param theSizeListener the listener for size events
	 * @see #resize(int, int)
	 * @see #addUpdateListener(CCUpdateListener)
	 * @see CCSizeListener
	 */
	public void addSizeListener(final CCSizeListener theSizeListener) {
		_mySizeListener.add(theSizeListener);
	}

	/**
	 * Add a post listener to execute expressions after the end of draw
	 * @shortdesc adds a listener reacting to post events
	 * @param thePostListener the listener for post events
	 * @see CCPostListener
	 */
	public void addPostListener(final CCPostListener thePostListener) {
		_myPostListener.add(thePostListener);
	}
	
	/**
	 * The system variable <code>pmouseX</code> always contains the previous 
	 * horizontal coordinate of the mouse. This is the horizontal 
	 * position of the mouse in the frame previous to the current frame.
	 * @shortdesc horizontal position of the mouse in the previous frame
	 * @see #mouseX
	 * @see #mouseY
	 * @see #pMouseY
	 * @see #mousePressed
	 * @example basics.CCMousePosition
	 */
	public int pMouseX = 0;
	
	/**
	 * The system variable <code>pmouseY</code> always contains the previous 
	 * vertical coordinate of the mouse. This is the vertical position of 
	 * the mouse in the frame previous to the current frame.
	 * @shortdesc vertical position of the mouse in the previous frame
	 * @see #mouseX
	 * @see #mouseY
	 * @see #pMouseX
	 * @see #mousePressed
	 * @example basics.CCMousePosition
	 */
	public int pMouseY = 0;
	
	/**
	 * The system variable <code>mouseX</code> always contains 
	 * the current horizontal coordinate of the mouse.
	 * @shortdesc current horizontal coordinate of the mouse
	 * @see #mouseY
	 * @see #pMouseX
	 * @see #pMouseY
	 * @see #mousePressed
	 * @example basics.CCMousePosition
	 */
	public int mouseX = 0;
	
	/**
	 * The system variable <code>mouseY</code> always contains 
	 * the current vertical coordinate of the mouse.
	 * @shortdesc current vertical coordinate of the mouse
	 * @see #mouseX
	 * @see #pMouseX
	 * @see #pMouseY
	 * @see #mousePressed
	 * @example basics.CCMousePosition
	 */
	public int mouseY = 0;
	
	/**
	 * Variable storing if a mouse button is pressed. The value 
	 * of the system variable mousePressed is true if a mouse 
	 * button is pressed and false if a button is not pressed.
	 * @shortdesc Variable storing if a mouse button is pressed
	 * @see #mouseX
	 * @see #mouseY
	 * @see #pMouseX
	 * @see #pMouseY
	 * @example basics.CCMousePosition
	 */
	public boolean mousePressed = false;
	
	protected boolean _myIsFirstMouse = true;

	/**
	 * Add a listener to react to mouse events.
	 * @param theMouseListener the listener for mouse events
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see CCMouseListener
	 */
	public void addMouseListener(final CCMouseListener theMouseListener) {
		_myMouseListener.add(theMouseListener);
	}
	
	public void removeMouseListener(final CCMouseListener theMouseListener){
		_myMouseListener.remove(theMouseListener);
	}

	/**
	 * Add a listener to react to mouse motion events.
	 * @param theMouseMotionListener the listener for mouse motion events
	 * @see #mouseDragged(CCMouseEvent)
	 * @see #mouseMoved(CCMouseEvent)
	 * @see CCMouseMotionListener
	 */
	public void addMouseMotionListener(final CCMouseMotionListener theMouseMotionListener) {
		_myMouseMotionListener.add(theMouseMotionListener);
	}
	
	public void removeMouseMotionListener(final CCMouseMotionListener theMouseMotionListener) {
		_myMouseMotionListener.remove(theMouseMotionListener);
	}
	
	//////////////////////////////////////////////////////////////
	//
	// KEY HANDLING
	//
	//////////////////////////////////////////////////////////////
	
	/**
	 * The key variable always contains the value of the most recent 
	 * key on the keyboard that was used (either pressed or released).
	 * For non-ASCII keys, use the keyCode variable. The keys included in the 
	 * ASCII specification (BACKSPACE, TAB, ENTER, RETURN, ESC, and DELETE) do 
	 * not require checking to see if they key is coded, and you should simply 
	 * use the key variable instead of keyCode If you're making cross-platform 
	 * projects, note that the ENTER key is commonly used on PCs and Unix and 
	 * the RETURN key is used instead on Macintosh. Check for both ENTER and 
	 * RETURN to make sure your program will work for all platforms.
	 * @shortdesc most recent ASCII key on the keyboard that was used
	 * @see #keyCode
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 * @see CCKeyEvent
	 */
	public char key;

	/**
	 * The variable keyCode is used to detect special keys such as the UP, DOWN, 
	 * LEFT, RIGHT arrow keys and ALT, CONTROL, SHIFT.
	 * 
	 * The keys included in the ASCII specification (BACKSPACE, TAB, ENTER, RETURN, ESC, and DELETE) 
	 * do not require checking to see if they key is coded, and you should 
	 * simply use the key variable instead of keyCode If you're making 
	 * cross-platform projects, note that the ENTER key is commonly used on 
	 * PCs and Unix and the RETURN key is used instead on Macintosh. Check for 
	 * both ENTER and RETURN to make sure your program will work for all platforms.
	 * @shortdesc key code of the most recent key on the keyboard that was used
	 * @see #key
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * 
	 */
	public CCKeyCode keyCode;

	/**
	 * The boolean variable keyPressed is true if any key is pressed and false if no keys are pressed.
	 * @shortdesc variable to see if a key is pressed
	 * @see #key
	 * @see #keyCode
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * 
	 */
	public boolean keyPressed;

	/**
	 * Add a listener to react to key events.
	 * @shortdesc adds a listener reacting to key events
	 * @param theKeyListener the listener for key events 
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void addKeyListener(final CCKeyListener theKeyListener) {
		_myKeyListener.add(theKeyListener);
	}
	
	public void removeKeyListener(final CCKeyListener theKeyListener) {
		_myKeyListener.remove(theKeyListener);
	}

	public void enqueueMouseEvent(CCMouseEvent theEvent) {
		synchronized (_myMouseEventQueue){
			_myMouseEventQueue.add(theEvent);
		}
	}

	protected void dequeueMouseEvents() {
		synchronized (_myMouseEventQueue){
			while(!_myMouseEventQueue.isEmpty()) {
				handleMouseEvent(_myMouseEventQueue.poll());
			}
		}
	}

	public void enqueueMouseWheelEvent(CCMouseWheelEvent theEvent) {
		synchronized (_myMouseWheelEventQueue){
			_myMouseWheelEventQueue.add(theEvent);
		}
	}

	protected void dequeueMouseWheelEvents() {
		synchronized (_myMouseWheelEventQueue){
			while(!_myMouseWheelEventQueue.isEmpty()) {
				_myMouseWheelListener.proxy().mouseWheelMoved(_myMouseWheelEventQueue.poll());
			}
		}
	}

	private void handleMouseEvent(CCMouseEvent theEvent) {
	
		switch (theEvent.eventType()){
			case MOUSE_PRESSED:
				mousePressed = true;
				_myMouseListener.proxy().mousePressed(theEvent);
				break;
			case MOUSE_RELEASED:
				mousePressed = false;
				_myMouseListener.proxy().mouseReleased(theEvent);
				break;
			case MOUSE_ENTERED:
				_myMouseListener.proxy().mouseEntered(theEvent);
				break;
			case MOUSE_EXITED:
				_myMouseListener.proxy().mouseExited(theEvent);
				break;
			case MOUSE_DRAGGED:
				pMouseX = mouseX;
				pMouseY = mouseY;
				_myMouseMotionListener.proxy().mouseDragged(theEvent);
				break;
			case MOUSE_MOVED:
				if(_myIsFirstMouse){
					pMouseX = theEvent.x();
					pMouseY = theEvent.y();
					_myIsFirstMouse = false;
				}else{
					pMouseX = mouseX;
					pMouseY = mouseY;
				}
				
				_myMouseMotionListener.proxy().mouseMoved(theEvent);
				break;
			case MOUSE_CLICKED:
				_myMouseListener.proxy().mouseClicked(theEvent);
				break;
			case MOUSE_WHEEL:
				break;
		}
	
		mouseX = theEvent.x();
		mouseY = theEvent.y();
	}

	public void enqueueKeyEvent(CCKeyEvent theEvent) {
		synchronized (_myKeyEventQueue){
			_myKeyEventQueue.add(theEvent);
		}
	}

	protected void dequeueKeyEvents() {
		synchronized (_myKeyEventQueue){
			while(!_myKeyEventQueue.isEmpty()) {
				handleKeyEvent(_myKeyEventQueue.poll());
			}
		}
	}

	private void handleKeyEvent(CCKeyEvent theEvent) {
		key = theEvent.keyChar();
		keyCode = theEvent.keyCode();
	
		switch (theEvent.type()){
			case PRESSED:
				keyPressed = true;
				_myKeyListener.proxy().keyPressed(theEvent);
				break;
			case RELEASED:
				keyPressed = false;
				_myKeyListener.proxy().keyReleased(theEvent);
				break;
			case TYPED:
				_myKeyListener.proxy().keyTyped(theEvent);
				break;
		}
	}
}
