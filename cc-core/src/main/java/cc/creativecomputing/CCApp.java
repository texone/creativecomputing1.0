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

import java.awt.event.FocusListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import cc.creativecomputing.control.CCControlHandle;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCMouseWheelListener;
import cc.creativecomputing.events.CCSetupListener;
import cc.creativecomputing.graphics.CCGraphics;


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
public class CCApp extends CCAbstractGraphicsApp<CCGraphics> 
	implements 
		GLEventListener, 
		FocusListener, 
		CCSetupListener,
		CCMouseListener, 
		CCMouseWheelListener,
		CCMouseMotionListener,
		CCKeyListener{

	
	@Override
	public void makeSettings(CCApplicationSettings theSettings, CCAppContainer theContainer) {
		_myUI = new CCControlUI(this, theSettings.uiFile());
		_myUI.hide();
		_myUI.translation().set(theSettings.uiTranslation());
		
		super.makeSettings(theSettings, theContainer);
		
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCAbstractGraphicsApp#drawListenerClass()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class drawListenerClass() {
		// TODO Auto-generated method stub
		return CCDrawListener.class;
	}
	
	/**
	 * Add the controls to the static members of a class. In this case the 
	 * tab is not read from the {@link CCControlClass} annotation but the given 
	 * tab name.
	 * @param theTabName name of the tab for the controls
	 * @param theClass the class to be controlled
	 */
	public CCControlHandle addControls(final String theTabName, final Class<?> theClass) {
		return _myUI.addControls(theTabName, theClass);
	}
	

	public CCControlHandle addControls(final String theTabName, final int theColumn, final Class<?> theClass) {
		return _myUI.addControls(theTabName, theColumn, theClass);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public CCControlHandle addControls(final String theTabName, final String theObjectID, final Class<?> theClass) {
		return _myUI.addControls(theTabName, theObjectID, theClass);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public CCControlHandle addControls(final String theTabName, final String theObjectID, final int theColumn, final Class<?> theClass) {
		return _myUI.addControls(theTabName, theObjectID, theColumn, theClass);
	}
	
	/**
	 * Adds an object to be controlled to the user interface.
	 * @param theTabName the name of the tab for the controls 
	 * @param theObjectID the id for saving the object data
	 * @param theObject the object to be controlled
	 */
	public CCControlHandle addControls(final String theTabName, final String theObjectID, final Object theObject) {
		return _myUI.addControls(theTabName, theObjectID, theObject);
	}
	
	/**
	 * Adds an object to be controlled to the user interface.
	 * @param theTabName the name of the tab for the controls 
	 * @param theObjectID the id for saving the object data
	 * @param theColumn column in which to place the ui elements
	 * @param theObject the object to be controlled
	 */
	public CCControlHandle addControls(final String theTabName, final String theObjectID, final int theColumn, final Object theObject) {
		return _myUI.addControls(theTabName, theObjectID, theColumn,theObject);
	}
	
	/**
	 * hides the user interface
	 */
	public void hideControls() {
		_myUI.hide();
	}
	
	/**
	 * Shows the userinterface
	 */
	public void showControls() {
		_myUI.show();
	}
	
	/**
	 * Checks if the user interface is visible.
	 * @return <code>true</code> if the user interface is visible otherwise <code>false</code>
	 */
	public boolean areControlsVisible() {		//TODO fix typo! (areControlsVisible)
		return _myUI.isVisible();
	}

	
	
	@Override
	public void initGraphics(GLAutoDrawable glDrawable) {
		g = new CCGraphics(glDrawable.getGL().getGL2());
	}
	
}
