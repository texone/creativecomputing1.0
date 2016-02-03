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

import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCMouseWheelEvent;
import cc.creativecomputing.events.CCMouseWheelListener;
import cc.creativecomputing.events.CCSetupListener;
import cc.creativecomputing.events.CCSizeListener;
import cc.creativecomputing.graphics.CCAbstractGraphics;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.util.logging.CCLog;


/**
 * <p>This is the base class for your applications. Everything starts with an
 * extended class off CCApp. To create your own application you must first
 * define a constructor that passes the given settings to its super class.</p>
 * <blockquote><pre>public CCAppExample(CCApplicationSettings theSettings) {
 *    super(theSettings);
 *}</pre></blockquote>
 * <p>This always needs to be included in your application so far there is no
 * other way to initialize it. If your are using eclipse you can load a template
 * file that comes with the download to create a first stub.</p>
 * <p>To start your application you have to define a main method where you
 * create an instance of <code>CCApplicationManager</code> to setup your application
 * and start it. For more information for setting up and starting your application
 * have a look at <code>CCApplicationManager</code> and <code>CCApplicationSettings</code>
 * </p>
 * <blockquote><pre>public static void main(String[] args) {
 *    final CCApplicationManager _myManager = new CCApplicationManager(CCAppExample.class);
 *    _myManager.setSize(400, 400);
 *    _myManager.start();
 * }</pre></blockquote>
 * <p>To define the behavior of your program you have to override the <code>setup</code>
 * and <code>draw</code> function. <code>setup</code> is executed once when your program
 * has started. <code>draw</code> is continuously called after setup for every frame.</p>
 * 
 * @see CCApplicationManager
 * @see CCApplicationSettings
 * @example basics.CCAppExample
 * @author texone
 * @nosuperclasses
 */
public abstract class CCAbstractGraphicsApp<GraphicsType extends CCAbstractGraphics<?>> extends CCAbstractWindowApp 
	implements 
		GLEventListener, 
		FocusListener, 
		CCSetupListener,
		CCMouseListener, 
		CCMouseWheelListener,
		CCMouseMotionListener,
		CCKeyListener{

	/**
	 * Used to animate the scene
	 * specify if the display must be called in loop
	 */
//	protected CCAnimator _myAnimator;
	
	/**
	 * Contains the number of frames since your application started.
	 * Inside <code>setup()</code>, <code>frameCount</code> is 0. 
	 * For every call of <code>draw</code> <code>frameCount</code> is increased by
	 * one.
	 * @see #frameRate
	 * @example basics.CCFrameRate
	 */
	public int frameCount = 0;
	
	/**
	 * Contains the current value of frames per second.
	 * The initial value will be 10 fps, and will be updated with each
	 * frame thereafter. The value is not instantaneous (since that
	 * wouldn't be very useful since it would jump around so much),
	 * but is instead averaged (integrated) over several frames.
	 * As such, this value won't be valid until after 5-10 frames.
	 * @see #frameCount
	 * @see #frameRate(int)
	 * @example basics.CCFrameRate
	 */
	public float frameRate = 10;
	public float frameTime = 0;

	protected long _myFrameRateLastNanos = 0;
	protected long _myMillisSinceStart = 0;
	
	protected GLAutoDrawable _myAutoDrawable;
	
	/**
	 * The graphics object is used for all the drawing inside your application.
	 * @shortdesc graphics object for all drawing
	 * @example basics.CCAppExample
	 * @see CCGraphics
	 */
	public GraphicsType g;
	
	protected CCListenerManager<CCDrawListener<GraphicsType>> _myDrawListener;
	
	/**
	 * The setting field contains all properties of the active application.
	 * @shortdesc settings of the application
	 * @see CCApplicationSettings
	 */
	public CCApplicationSettings settings;
	protected CCAppContainer _myContainer;
	
	private CCAppContext _myAppContext;
	protected CCControlUI _myUI;
	
	public void makeSettings(CCApplicationSettings theSettings, CCAppContainer theContainer) {
		_myContainer = theContainer;
		_myAppContext = theSettings.appContext();
		_myAutoDrawable = theContainer.glAutoDrawable();
		if(_myAutoDrawable != null)_myAutoDrawable.addGLEventListener(this);
		
		settings = theSettings;
		
		// TODO check this
		width = theSettings.width();
		height = theSettings.height();
		
		_myMouseListener.add(this);
		_myMouseMotionListener.add(this);
		_myMouseWheelListener.add(this);
		_myKeyListener.add(this);
		_mySetupListener.add(this);
		
		_myDrawListener = CCListenerManager.create(drawListenerClass());
		
		_myAppContext.onFirstDisplay(this);
		frameSetup();
	}
	
	/**
	 * Get UI Object
	 */
	public CCControlUI ui() {
		return _myUI;
	}
	
	public abstract Class<CCDrawListener<GraphicsType>> drawListenerClass();
	
	GLAutoDrawable autoDrawable() {
		return _myAutoDrawable;
	}
	
	public void menuBar(JMenuBar theMenuBar) {
		if(_myContainer instanceof JFrame) {
			((JFrame) _myContainer).setJMenuBar(theMenuBar);
		}else{
			throw new RuntimeException("Setting menu bars is only allowed for applications based on a Jframe");
		}
	}
	
	
	
	/**
	 * Adds a listener to the application, to draw different objects. 
	 * This can be used to forward the draw event to 
	 * different objects. To do so the class needs to implement
	 * the <code>CCDrawListener</code> interface and must be added
	 * as listener. 
	 * @shortdesc adds a listener reacting to draw events
	 * @param theDrawListener the listener for draw events
	 * @see #draw()
	 * @see CCDrawListener
	 */
	public void addDrawListener(final CCDrawListener<GraphicsType> theDrawListener) {
		_myDrawListener.add(theDrawListener);
	}
	
	public void removeDrawListener(final CCDrawListener<GraphicsType> theDrawListener) {
		_myDrawListener.remove(theDrawListener);
	}
	
	/**
	 * Returns the current x coordinate of the application window.
	 * @return x coordinate of the application window
	 */
	public int windowX() {
		return _myContainer.x();
	}

	/**
	 * Returns the current y coordinate of the application window.
	 * @return y coordinate of the application window
	 */
	public int windowY() {
		return _myContainer.y();
	}

	/**
	 * Returns the current width of the application window.
	 * @return width of the application window
	 */
	public int windowWidth() {
		return _myContainer.width();
	}

	/**
	 * Returns the current height of the application window.
	 * @return height of the application window
	 */
	public int windowHeight() {
		return _myContainer.height();
	}
	
	/**
	 * Method to check if the application is visible
	 * @return <code>true</code> in case the application is visible, <code>false</code> otherwise
	 */
	public boolean isVisible() {
		return _myContainer.isVisible();
	}
	
	/**
	 * Hide this Window, its subcomponents, and all of its owned children. 
	 * The Window and its subcomponents can be made visible again with a call to show.
	 */
	public void hide() {
		_myContainer.setVisible(false);
	}
	
	/**
	 * Makes the Window visible. If the Window and/or its owner are not yet displayable, 
	 * both are made displayable. The Window will be validated prior to being made visible. 
	 * If the Window is already visible, this will bring the Window to the front.
	 */
	public void show() {
		_myContainer.setVisible(true);
	}
	
	/**
	 * Call by the GLDrawable just after the Gl-Context is 
	 * initialized.    
	 * @invisible
	 **/
	public void init(GLAutoDrawable glDrawable) {
		final GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		
		if(settings.vsync()){
			gl.setSwapInterval(1);
		}else{
			gl.setSwapInterval(0);
		}
		
		_myAppContext.onInit(this);
		
		initGraphics(glDrawable);
	}
	
	public abstract void initGraphics(GLAutoDrawable glDrawable);
	
	/**
	 * Called once when the program is started. Used to define initial environment properties such as clear color, 
	 * loading images, etc. before the draw() begins executing. Simply overwrite this function for initialization
	 * steps. 
	 * @shortdesc called on application start
	 * @see #draw()
	 * @see #finish()
	 * @example basics.CCSetupDraw
	 * @order 1
	 */
	public void setup(){
		
	}
	
	public void frameSetup() {
		
	}
	
	public void dispose(GLAutoDrawable theAutoDrawable){
		
	}
	
	/**
	 *   
	 * @invisible
	 */
	public void dispose(){
		try{
			finish();
			_myDisposeListener.proxy().dispose();
		}catch(RuntimeException e){
			CCLog.error(e);
		}
	}
	
	/**
	 * Called once when the program is closed. Can be used to store relevant data to a file
	 * before the application ends.
	 * @shortdesc called on application end
	 * @order 3
	 * @see #draw()
	 * @see #setup()
	 * @example basics.CCFinish
	 */
	public void finish(){
		
	}

	/**
	 * Called when the display mode has been changed : 
	 * CURRENTLY UNIMPLEMENTED IN JOGL  
	 * @invisible
	 **/
	public void displayChanged(GLAutoDrawable glDrawable, boolean modeChanged, boolean deviceChanged) {
	}
	
	private boolean _myHasCalledSetup = false;

	/**
	 * Called when the application window is resized. You can overwrite this function 
	 * to change settings that are dependent on the window size.
	 * @param theWidth the new width of the application window
	 * @param theHeight the new height of the application window
	 * @shortdesc Called when the application window is resized.
	 * @see #addSizeListener(CCSizeListener)
	 * @see CCSizeListener
	 * @example basics.CCResize
	 */
	public void resize(int theWidth, int theHeight){
		
	}

	/**
	 * This method is called by the GLDrawable at each resize event of the
	 * OpenGl component. It resize the view port of the scene and set the Viewing Volume  
	 * @invisible
	 */
	public void reshape(GLAutoDrawable glDrawable, int theX, int theY, int theWidth, int theHeight) {
		// let the appmode handle all resizing
		g.resize(theWidth, theHeight);
		
		g.reshape(theX, theY, theWidth, theHeight);
		
		resize(width, height);
		
		// first call resize listener so that screen modes can change size before call of setup
		_mySizeListener.proxy().size(width,height);
		
		
	}
    
	private void updateFrameRate(){
		if (_myFrameRateLastNanos != 0){
			long myNanos = System.nanoTime();
			frameTime = myNanos - _myFrameRateLastNanos;
			_myMillisSinceStart += myNanos - _myFrameRateLastNanos;
			if (frameTime != 0){
				//frameRate = (frameRate * 0.9f) + ((1.0f / (frameTime / 1000.0f)) * 0.1f);
				frameRate = (float)(1.0 / (frameTime / 1e9));
			}
			_myFrameRateLastNanos = myNanos;
		}else {
			_myFrameRateLastNanos = System.nanoTime();
		}
	}
	
	public String appTime(){
		long myTime = _myMillisSinceStart;
		int millis = (int)(myTime % 1000);
		
		myTime /= 1000;
		int seconds = (int)myTime;
		seconds %= 60;
		
		myTime /= 60;
		int minutes = (int)myTime;
		minutes %= 60;
		
		myTime /= 60;
		int hours = (int)myTime;
		
		return
			"HOURS:" + CCFormatUtil.nf(hours,2) +
			" MINUTES:"+CCFormatUtil.nf(minutes, 2)+
			" SECONDS:"+CCFormatUtil.nf(seconds, 2)+
			" MILLIS:"+CCFormatUtil.nf(millis, 4);
	}
	
	private boolean _myIsFixedUpdateTime = false;
	private float _myFixedUpdateTime = 1;
	
	public void fixUpdateTime(final float theTime){
		_myIsFixedUpdateTime = true;
		_myFixedUpdateTime = theTime;
	}
	
	public void freeUpdateTime(){
		_myIsFixedUpdateTime = false;
	}
	
	/**
	 * Here we place all the code related to the drawing of the scene.
	 * This method is called by the drawing loop (the display method)  
	 * @invisible
	 **/
	public void display(GLAutoDrawable theDrawable) {
		g.updateGL(theDrawable);
		
		// call setup for the window size is set for the first time so that the values are set
		if(!_myHasCalledSetup){
			try{
				g.beginDraw();
				// set default font to enable traces to screen
//				g.textFont(CCFontIO.createGlutFont(CCGlutFontType.BITMAP_HELVETICA_10));
				_mySetupListener.proxy().setup();
				g.endDraw();
			}catch(RuntimeException e){
				CCLog.error(e);
				System.exit(0);
			}catch(Exception e){
				CCLog.error(e);
				throw new RuntimeException(e);
			}
			
//			g.clear();
//			g.noTexture();
			_myHasCalledSetup = true;
			return;
		}
		
		processEvents(theDrawable);
		updateFrameRate();

		
		g.beginDraw();

		try{
			
			dequeueKeyEvents();
			dequeueMouseEvents();
			dequeueMouseWheelEvents();

			float myUpdateTime;
			if(_myIsFixedUpdateTime){
				myUpdateTime = _myFixedUpdateTime;
			}else{
				myUpdateTime = 1/frameRate;
			}
			g.gl.glFinish();
			update(myUpdateTime);
			_myUpdateListener.proxy().update(myUpdateTime);
		
			draw();
			
			_myDrawListener.proxy().draw(g);
		
			_myPostListener.proxy().post();
		}catch(RuntimeException e){
			CCLog.error(e);
		}catch(Exception e){
			CCLog.error(e);
			throw new RuntimeException(e);
		}
		g.endDraw();

		frameCount++;
	}
	
	/**
	 * This method is called before the draw method on every frame. Override this
	 * method to define updates in your applications logic. The update function
	 * receives the time since the last frame as float value in seconds. You can
	 * use this parameter to calculate you updates dependent on the frame rate. 
	 * @shortdesc automatically called before draw for application updates
	 * @param theDeltaTime time since the last frame in seconds
	 * @see #setup()
	 * @see #draw()
	 * @example basics.CCUpdate
	 */
	public void update(float theDeltaTime){
		
	}
	
	/**
	 * Called directly after <code>setup()</code> and continuously executes the lines of code contained 
	 * inside its block until the program is stopped. The <code>draw()</code> function is called automatically 
	 * and should never be called explicitly. The number of times <code>draw()</code> executes in each second may be 
	 * controlled with the frameRate() function.
	 * @shortdesc automatically called on every frame for drawing
	 * @order 2
	 * @see #setup()
	 * @see #finish()
	 * @see #update(float)
	 * @see #addDrawListener(CCDrawListener)
	 * @example basics.CCSetupDraw
	 */
	public void draw(){
	}

	/*The events*/

	/**
	 * Put here all the events related to a key or a mouse pressed.  
	 * @invisible
	 **/
	public void processEvents(GLDrawable glDrawable) {
		/*
		 * Put here all related OpenGl effects of the events
		 *
		 * How put the events here:
		 * Just because if you would call an OpenGl method in on of
		 * the key or mouse method, it do not work !
		 *
		 * Ex: in the Tutorial 04, the call: gl.glShadeModel(...)
		 * could not be placed on an event method.
		 */
	}
	
	//////////////////////////////////////////////////////////////
	//
	// CURSOR
	// 
	//////////////////////////////////////////////////////////////
	
	/**
	 * Simply maps the java awt cursor types for better convenience  
	 */
	public static enum CCCursor{
		/**
	     * The default cursor type (gets set if no cursor is defined).
	     */
	    DEFAULT_CURSOR(Cursor.DEFAULT_CURSOR),

	    /**
	     * The crosshair cursor type.
	     */
	    CROSSHAIR_CURSOR(Cursor.CROSSHAIR_CURSOR),

	    /**
	     * The text cursor type.
	     */
	    TEXT_CURSOR(Cursor.TEXT_CURSOR),

	    /**
	     * The wait cursor type.
	     */
	    WAIT_CURSOR(Cursor.WAIT_CURSOR),

	    /**
	     * The south-west-resize cursor type.
	     */
	    SW_RESIZE_CURSOR(Cursor.SW_RESIZE_CURSOR),

	    /**
	     * The south-east-resize cursor type.
	     */
	    SE_RESIZE_CURSOR(Cursor.SE_RESIZE_CURSOR),

	    /**
	     * The north-west-resize cursor type.
	     */
	    NW_RESIZE_CURSOR(Cursor.NW_RESIZE_CURSOR),

	    /**
	     * The north-east-resize cursor type.
	     */
	    NE_RESIZE_CURSOR(Cursor.NE_RESIZE_CURSOR),

	    /**
	     * The north-resize cursor type.
	     */
	    N_RESIZE_CURSOR(Cursor.N_RESIZE_CURSOR),

	    /**
	     * The south-resize cursor type.
	     */
	    S_RESIZE_CURSOR(Cursor.S_RESIZE_CURSOR),

	    /**
	     * The west-resize cursor type.
	     */
	    W_RESIZE_CURSOR(Cursor.W_RESIZE_CURSOR),

	    /**
	     * The east-resize cursor type.
	     */
	    E_RESIZE_CURSOR(Cursor.E_RESIZE_CURSOR),

	    /**
	     * The hand cursor type.
	     */
	    HAND_CURSOR(Cursor.HAND_CURSOR),

	    /**
	     * The move cursor type.
	     */
	    MOVE_CURSOR(Cursor.DEFAULT_CURSOR);
	    
	    private Cursor _myJavaCursor;
	    
	    private CCCursor(final int theJavaID) {
	    	_myJavaCursor = new Cursor(theJavaID);
	    }
	    
	    public Cursor javaCursor(){
	    	return _myJavaCursor;
	    }
	}
	
	/**
	 * Use this method to hide the mouse cursor.
	 */
	public void noCursor(){
		_myContainer.noCursor();
	}
	
	
	public void cursor(final CCCursor theCursor){
		_myContainer.cursor(theCursor);
	}
	
	/**
	 * Use this method to activate the mouse cursor
	 */
	public void cursor() {
		cursor(CCCursor.DEFAULT_CURSOR);
	}
	

	//////////////////////////////////////////////////////////////
	//
	// ADD LISTENERS
	//
	//////////////////////////////////////////////////////////////
	
	/**
	 * Called every time the mouse button has been pressed on the application window.
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 * 
	 */
	public void mousePressed(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called every time the mouse button has been released on the application window.
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseReleased(final CCMouseEvent theMouseEvent){};
	/**
	 * Called every time the mouse button has been clicked (pressed and released) on the application window.
	 * @shortdesc called on mouse click
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseClicked(final CCMouseEvent theMouseEvent){};
	/**
	 * Called every time the mouse has entered the application window.
	 * @shortdesc called on mouse enter
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseExited(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseEntered(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called every time the mouse has left the application window.
	 * @shortdesc called on mouse exit
	 * @param theMouseEvent related mouse event
	 * @example basics.CCMouseEventTest
	 * @see #mousePressed(CCMouseEvent)
	 * @see #mouseReleased(CCMouseEvent)
	 * @see #mouseClicked(CCMouseEvent)
	 * @see #mouseEntered(CCMouseEvent)
	 * @see CCMouseEvent
	 * @see CCMouseListener
	 */
	public void mouseExited(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called when a mouse button is pressed on the application window and then 
     * dragged. Mouse drag events will continue to be delivered to the application
     * until the mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the application window).
     * @shortdesc called when the mouse is dragged over the application window
	 * @param theMouseEvent the related mouse event
	 * @example basics.CCMouseMoveTest
	 * @see CCMouseEvent
	 * @see #mouseDragged(CCMouseEvent)
	 */
	public void mouseMoved(final CCMouseEvent theMouseEvent){};
	
	/**
	 * Called when the mouse wheel is moved or you scroll on a trackpad
	 * @param theMouseEvent the related mouse wheel event
	 * @see CCMouseWheelEvent
	 * @see #mouseDragged(CCMouseEvent)
	 */
	public void mouseWheelMoved(final CCMouseWheelEvent theMouseEvent){};
	
	/**
	 * Called when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     * @shortdesc called when the mouse has moved over the application window
	 * @param theMouseEventn the related mouse event
	 * @example basics.CCMouseDragTest
	 * @see CCMouseEvent
	 * @see #mouseMoved(CCMouseEvent)
	 */
	public void mouseDragged(final CCMouseEvent theMouseEvent){};
	
	//////////////////////////////////////////////////////////////
	//
	// KEY HANDLING
	//
	//////////////////////////////////////////////////////////////
	
	/**
	 * The keyPressed() function is called once every time a key is pressed. 
	 * The key that was pressed is passed as key event. Because of how operating 
	 * systems handle key repeats, holding down a key may cause multiple calls to 
	 * keyPressed() (and keyReleased() as well). The rate of repeat is set by the 
	 * operating system and how each computer is configured.
	 * @shortdesc The keyPressed() function is called once every time a key is pressed. 
	 * @param theKeyEvent event object with all information on the pressed key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyReleased(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyPressed(final CCKeyEvent theKeyEvent){
	}
	
	/**
	 * The keyReleased() function is called once every time a key is released. 
	 * The key that was released is passed as key event. See key and 
	 * keyReleased for more information.
	 * @shortdesc The keyReleased() function is called once every time a key is released.
	 * @param theKeyEvent event object with all information on the released key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyTyped(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyReleased(final CCKeyEvent theKeyEvent){
	}
	
	/**
	 * The keyTyped() function is called once every time a key is pressed, 
	 * but action keys such as CTRL, SHIFT, and ALT are ignored. Because of
	 * how operating systems handle key repeats, holding down a key will cause 
	 * multiple calls to keyTyped(), the rate is set by the operating system 
	 * and how each computer is configured.
	 * @shortdesc The keyTyped() function is called once every time a  ASCII key is pressed, 
	 * @param theKeyEvent event object with all information on the typed key
	 * @see CCKeyEvent
	 * @see #keyPressed
	 * @see #keyPressed(CCKeyEvent)
	 * @see #keyReleased(CCKeyEvent)
	 * @see CCKeyListener
	 */
	public void keyTyped(final CCKeyEvent theKeyEvent){
	}

	//////////////////////////////////////////////////////////////

	/**
	 * @invisible
	 */
	public void focusGained(final FocusEvent theFocusEvent){
		focusGained();
	}
	
	/**
	 * This method will be called every time the application window
	 * is focused.
	 */
	public void focusGained(){
		
	}

	/**
	 * @invisible
	 */
	public void focusLost(final FocusEvent theFocusEvent){
		focusLost();
	}
	
	/**
	 * This method will be called every time the application window
	 * loosed the focus.
	 */
	public void focusLost(){
		
	}
}
