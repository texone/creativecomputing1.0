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

import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.media.opengl.GLCapabilities;
import javax.swing.WindowConstants;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.nio.CCNIOUtil;
import cc.creativecomputing.util.CCCommandLineParser;
import cc.creativecomputing.util.CCCommandLineParser.CCCommandlineOption;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;


/**
 * This class is used to setup your applications parameters.
 * @author texone
 * @see CCApplicationManager
 * @see CCApp
 */
public class CCApplicationSettings {
	
	private static class CCApplicationProperty{
		
		private static Map<String, CCApplicationProperty> valueMap = new HashMap<>();
		
		private String _myName;
		private boolean _myIsFlagOption;
		private String _myDescription;
		private String _myValue;
		
		private CCApplicationProperty(String theName, String theValue, String theDescription, boolean theIsFlagOption){
			_myName = theName;
			_myIsFlagOption = theIsFlagOption;
			_myDescription = theDescription;
			_myValue = theValue;
			valueMap.put(theName, this);
		}
		
		private CCApplicationProperty(String theName, String theValue, String theDescription){
			this(theName, theValue, theDescription, false);
		}
	}
	
	public static final CCApplicationProperty WIDTH = new CCApplicationProperty("width", 400 + "", "application width");
	public static final CCApplicationProperty HEIGHT = new CCApplicationProperty("height", 400 + "", "application height");
	public static final CCApplicationProperty X = new CCApplicationProperty("x", -1 + "", "x position of the application window");
	public static final CCApplicationProperty Y = new CCApplicationProperty("y", -1 + "", "y position of the application window");
	public static final CCApplicationProperty TITLE = new CCApplicationProperty("title","Creative Computing Application", "title of the application");
	public static final CCApplicationProperty DISPLAY_MODE = new CCApplicationProperty("display_mode", CCDisplayMode.WINDOW.name(), "display mode for the application");
	public static final CCApplicationProperty DISPLAY = new CCApplicationProperty("display", 0 + "", "display for the application");
	public static final CCApplicationProperty CONFIGURATION = new CCApplicationProperty("configuration", 0 + "", "configuration for the application");
	
	public static final CCApplicationProperty UI_FILE = new CCApplicationProperty("ui_file", "","file path to load and save ui settings");
	public static final CCApplicationProperty UI_X = new CCApplicationProperty("ui_x", 0 + "", "x offset for the ui");
	public static final CCApplicationProperty UI_Y = new CCApplicationProperty("ui_y", 0 + "", "y offset for the ui");
	

	public static final CCApplicationProperty APP_SETTINGS = new CCApplicationProperty("app_settings", "","file path to load app settings");
	
	public static final CCApplicationProperty FRAMERATE = new CCApplicationProperty("frameRate", -1+"","framerate of the application -1 for as fast as possible");
	
	public static final CCApplicationProperty ASSET_PATHS = new CCApplicationProperty("asset_paths", "","optional path for loading assets");
	
	public static final CCApplicationProperty UNDECORATED = new CCApplicationProperty("undecorated", "false", "flag to make app undecorated", true);
	
	public static final CCApplicationProperty ALWAYS_ON_TOP = new CCApplicationProperty("always_on_top", "false", "flag to make app always on top", true);
	
	public static final CCApplicationProperty INVSYNC = new CCApplicationProperty("invsync", "true", "flag to let the app run in vsync", true);
	
	private static final CCApplicationProperty[] properties = new CCApplicationProperty[]{
		WIDTH, HEIGHT, X, Y, TITLE, DISPLAY_MODE, UI_FILE, UI_X, UI_Y, FRAMERATE, ASSET_PATHS, UNDECORATED, INVSYNC
		
	};
	
	private static CCCommandLineParser createParser(){
		CCCommandLineParser myParser = new CCCommandLineParser("CC Settings");
		for(CCApplicationProperty myProperty:properties){
			myParser.addParameter(myProperty._myName, !myProperty._myIsFlagOption, myProperty._myDescription);
		}
		myParser.addParameter(APP_SETTINGS._myName, !APP_SETTINGS._myIsFlagOption, APP_SETTINGS._myDescription);
		return myParser;
	}
	
	private static CCXMLElement createDefaults(){
		CCXMLElement mySettingsXML = new CCXMLElement("settings");
		for(CCApplicationProperty myProperty:properties){
			mySettingsXML.createChild(myProperty._myName, myProperty._myValue);
		}
		return mySettingsXML;
	}
	
	public static CCXMLElement DEFAULT_SETTINGS = createDefaults();
	
	private CCAppContext _myAppContext;
	
	private CCXMLElement _mySettings;
	
	public void append(CCXMLElement theSettings){
		for(CCXMLElement myPropertyXML:theSettings){
			setProperty(myPropertyXML.name(), myPropertyXML.content());
		}
	}
	
	public void addParameter(String theOption, boolean theHasArg, String theDescription){
		_myParser.addParameter(theOption, theHasArg, theDescription);
	}
	
	public void append(String[] theArgs){
		_myParser.parse(theArgs);
		if(_myParser.hasOption(APP_SETTINGS._myName)){
			String myPath = _myParser.optionValue(APP_SETTINGS._myName);
			append(CCXMLIO.createXMLElement(myPath));
			return;
		}
		
		for(CCCommandlineOption myOption:_myParser.customOptions()){
			setProperty(myOption.name(), myOption.value());	
		}

		for(CCApplicationProperty myProperty:properties){
			if(_myParser.hasOption(myProperty._myName)){
				setProperty(myProperty, myProperty._myIsFlagOption ? _myParser.hasOption(myProperty._myName) + "" : 
					                                                 _myParser.optionValue(myProperty._myName));
			}
		}
	}
	
	private CCCommandLineParser _myParser;
	
	private CCApplicationSettings(CCXMLElement theSettings) {
		_myParser = createParser();
		_mySettings = new CCXMLElement("settings");
		append(theSettings);
	}
	
	public CCApplicationSettings(){
		this(DEFAULT_SETTINGS);
	}
	
	public void setProperty(String theProperty, String theValue){
		CCXMLElement myChild = _mySettings.child(theProperty);
		if(myChild == null){
			_mySettings.createChild(theProperty, theValue);
		}else{
			myChild.setContent(theValue);
		}
	}
	
	private void setProperty(CCApplicationProperty theProperty, String theValue){
		setProperty(theProperty._myName, theValue);
	}
	
	public String getProperty(String theProperty){
		CCXMLElement myChild = _mySettings.child(theProperty);
		if(myChild == null){
			myChild = DEFAULT_SETTINGS.child(theProperty);
			if(myChild == null)return null;
			return myChild.content();
		}
		return myChild.content();
	}
	
	public String getProperty(String theProperty, String theDefault){
		String myProperty = getProperty(theProperty);
		if(myProperty == null)return theDefault;
		return myProperty;
	}
	
	public int getIntProperty(String theProperty, int theDefault){
		String myProperty = getProperty(theProperty);
		if(myProperty == null)return theDefault;
		return Integer.parseInt(myProperty);
	}
	
	public boolean hasProperty(String theProperty){
		return _mySettings.child(theProperty) != null;
	}
	
	private String getProperty(CCApplicationProperty theProperty){
		return getProperty(theProperty._myName);
	}
	
	/*
	 * DISPLAY MODE
	 */
	/**
	 * Display modes of the application, there are 4 different display modes, ranging from
	 * update only to fullscreen
	 */
	public static enum CCDisplayMode{
		/**
		 * Use this mode to run an application without creating an opengl context.
		 * This is useful if you have an application that has debug visuals to setup
		 * but can than run without a visual as plain window less application
		 */
		UPDATE_ONLY,
		/**
		 * Use this mode to run an application with offscreen rendering, this will
		 * allow you to create an application to export renderings or just use opengl
		 * for computation
		 */
		OFFSCREEN,
		/**
		 * This is the default mode. Here an opengl context is created on base of 
		 * window. The implementation of the window is dependent of the container. You can 
		 * also set the size and location of the window using the according methods
		 * @see CCApplicationSettings#container(CCGLContainer)
		 * @see CCApplicationSettings#size(int, int)
		 * @see CCApplicationSettings#location(int, int)
		 */
		WINDOW,
		/**
		 * Use this mode to let your application run in fullscreen mode size and location
		 * settings of your application are ignored in this case
		 */
		FULLSCREEN
	}
	
	private Window _myDialogOwner;
	
	/**
	 * Use this method to set the display mode of your application. 
	 * @see CCDisplayMode
	 * @param theDisplayMode 
	 */
	public void displayMode(final CCDisplayMode theDisplayMode){
		setProperty(DISPLAY_MODE, theDisplayMode.name());
	}
	
	public void dialog(final Window theOwner){
		container(CCGLContainer.DIALOG);
		_myDialogOwner = theOwner;
	}
	
	public Window dialogOwner() {
		return _myDialogOwner;
	}
	
	public CCDisplayMode displayMode(){
		return CCDisplayMode.valueOf(getProperty(DISPLAY_MODE));
	}
	
	/*
	 * CLOSE OPERATION
	 */
	
	/**
	 * Represents the operations that will happen by default when the user initiates a 
	 * "close" on the application.
	 */
	public static enum CCCloseOperation{
		/**
		 * Don't do anything; require the program to handle the operation in the 
		 * windowClosing method of a registered WindowListener object.
		 */
		DO_NOTHING_ON_CLOSE(WindowConstants.DO_NOTHING_ON_CLOSE),
		
		/**
		 * Automatically hide the frame after invoking any registered WindowListener objects.
		 */
		HIDE_ON_CLOSE(WindowConstants.HIDE_ON_CLOSE),
		
		/**
		 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.
		 */
		DISPOSE_ON_CLOSE(WindowConstants.DISPOSE_ON_CLOSE),
		
		/**
		 * Exit the application using the System exit method. Use this only in applications.
		 */
		EXIT_ON_CLOSE(WindowConstants.EXIT_ON_CLOSE);
		
		private int _myID;
		
		private CCCloseOperation(final int theID) {
			_myID = theID;
		}
		
		public int id() {
			return _myID;
		}
	}
	
	private CCCloseOperation _myCloseOperation = CCCloseOperation.EXIT_ON_CLOSE;
	
	/**
	 * Sets the operation that will happen by default when the user initiates a 
	 * "close" on the application. You must specify one of the following choices:
	 * <ul>
	 * <li><code>DO_NOTHING_ON_CLOSE</code> 
	 * Don't do anything; require the program to handle the operation in the 
	 * windowClosing method of a registered WindowListener object.</li>
	 * <li><code>HIDE_ON_CLOSE</code> 
	 * Automatically hide the frame after invoking any registered WindowListener objects.</li>
	 * <li><code>DISPOSE_ON_CLOSE</code> 
	 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.</li> 
	 * <li><code>EXIT_ON_CLOSE </code> Exit the application using the System exit method. Use this only in applications.</li>
	 * </ul>
	 * The value is set to <code>EXIT_ON_CLOSE</code> by default. Changes to the value of 
	 * this property cause the firing of a property change event, with property name "defaultCloseOperation".
	 * @param theCloseOperation
	 */
	public void closeOperation(final CCCloseOperation theCloseOperation) {
		_myCloseOperation = theCloseOperation;
	}
	
	/**
	 * Returns the operation that will happen by default when the user initiates a 
	 * "close" on the application. Specified is one of the following choices:
	 * <ul>
	 * <li><code>DO_NOTHING_ON_CLOSE</code> 
	 * Don't do anything; require the program to handle the operation in the 
	 * windowClosing method of a registered WindowListener object.</li>
	 * <li><code>HIDE_ON_CLOSE</code> 
	 * Automatically hide the frame after invoking any registered WindowListener objects.</li>
	 * <li><code>DISPOSE_ON_CLOSE</code> 
	 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.</li> 
	 * <li><code>EXIT_ON_CLOSE </code> Exit the application using the System exit method. Use this only in applications.</li>
	 * </ul>
	 * The value is set to <code>EXIT_ON_CLOSE</code> by default. Changes to the value of 
	 * this property cause the firing of a property change event, with property name "defaultCloseOperation".
	 * @param theCloseOperation
	 */
	public CCCloseOperation closeOperation() {
		return _myCloseOperation;
	}
	
	/**
	 * flag to define if the app is visible on start or not
	 */
	private boolean _myShowAppOnStart = true;
	
	/**
	 * Defines if the app is visible on start or not
	 * @param theShowAppOnStart
	 */
	public void showAppOnStart(final boolean theShowAppOnStart) {
		_myShowAppOnStart = theShowAppOnStart;
	}
	
	/**
	 * Returns if the app is visible on start or not
	 * @return
	 */
	public boolean showAppOnStart() {
		return _myShowAppOnStart;
	}
	
	public static enum CCGLContainer{
		FRAME,
		DIALOG,
		NEWT
	}
	 
	private CCGLContainer _myContainer = CCGLContainer.FRAME;
	
	/**
	 * Sets the implementation type of the jogl drawable this can be canvas or jpanel,
	 * So far canvas is the default value. 
	 * @param theContainer
	 */
	public void container(CCGLContainer theContainer) {
		_myContainer = theContainer;
	}
	
	public CCGLContainer container() {
		return _myContainer;
	}
	
	
	/**
	 * Sets the display for the application.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(int theDisplay) {
		setProperty(DISPLAY, theDisplay + "");
	}
	
	/**
	 * Sets the display for the application. This is useful for multi
	 * screen setups. If the display does not exist, the application will start
	 * at the first available one.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(String theDisplayName) {
		setProperty(DISPLAY, theDisplayName + "");
	}

	/**
	 * Returns the graphics device that should be used for Graphic output.
	 * @return the display of the application
	 */
	public String display() {
		return getProperty(DISPLAY);
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theConfiguration display for the application
	 */
	public void displayConfiguration(int theConfiguration) {
		setProperty(CONFIGURATION, theConfiguration + "");
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theDisplay display for the application
	 */
	public void displayConfiguration(String theConfigurationName) {
		setProperty(CONFIGURATION, theConfigurationName + "");
	}

	/**
	 * Returns the graphics device that should be used for Graphic output.
	 * @return the display of the application
	 */
	public String displayConfiguration() {
		return getProperty(CONFIGURATION);
	}
	
	/**
	 * Provides a platform-independent way to sync the application to vertical screen refreshes. 
	 * <code>false</code> disables sync-to-vertical-refresh completely, while <code>true</code> 
	 * causes the application to wait until the next vertical refresh until swapping buffers. 
	 * The default, which is <code>true</code>. This function is not guaranteed to have an effect, 
	 * and in particular only affects heavy weight on screen components.
	 * @param theIsInVsync
	 */
	public void vsync(boolean theIsInVsync){
		setProperty(INVSYNC, theIsInVsync+"");
	}
	
	/**
	 * Returns if the app is running in vsync
	 * @return <code>true</code> if the app runs in vsync otherwise <code>false</code>
	 */
	public boolean vsync(){
		return Boolean.parseBoolean(getProperty(INVSYNC));
	}
	
	/**
	 * Specifies the number of frames to be displayed every second. If the processor is not 
	 * fast enough to maintain the specified rate, it will not be achieved. For example, 
	 * the function call frameRate(30) will attempt to refresh 30 times a second. It is 
	 * recommended to set the frame rate within setup(). Per default the application runs
	 * as fast as possible.
	 * @param theFrameRate number of frames per second
	 */
	public void frameRate(int theFrameRate) {
		setProperty(FRAMERATE, theFrameRate + "");
	}
	
	public int frameRate() {
		return Integer.parseInt(getProperty(FRAMERATE));
	}
	
	/**
	 * Sets the app context. This allows sharing of the opengl context across multiple applications
	 * this way you can share resources like vertex buffer and textures. The default context is only 
	 * handling each separately without sharing the gl context.
	 * @param theContext
	 */
	public void appContext(CCAppContext theContext) {
		_myAppContext = theContext;
	}
	
	public CCAppContext appContext() {
		if(_myAppContext == null) {
			_myAppContext = new CCAppContextSimple(frameRate());
		}
		return _myAppContext;
	}
	
	private int _myStencilBits = 8;
	
	/**
	 * Sets the number of stencilbits to be used by opengl by default 8
	 * @param theStencilBits
	 */
	public void stencilBits(int theStencilBits){
		_myStencilBits = theStencilBits;
	}
	
	/**
	 * Returns the number of stencilbits
	 * @return
	 */
	public int stencilBits(){
		return _myStencilBits;
	}
	
	/**
	 * Sets or returns the title of the application
	 * @param theTitle title for the application
	 * @example basics.CCAppManager
	 */
	public void title(final String theTitle) {
		setProperty(TITLE, theTitle);
	}

	/**
	 * @return title of the application
	 */
	public String title() {
		return getProperty(TITLE);
	}
	
	/*
	 * FRAME DECORATION
	 */
	private boolean _myIsResizable = true;
	
	/**
	 * Defines if the application window is resizable.
	 * @param theIsResizable true if window should be resizable otherwise false
	 */
	public void isResizable(boolean theIsResizable) {
		_myIsResizable = theIsResizable;
	}

	/**
	 * @return true if the frame is resizable otherwise false
	 */
	public boolean isResizable() {
		return _myIsResizable;
	}
	
	/**
	 * Defines if the application window will have decoration. If set true
	 * the window will show borders and a head. Otherwise the window appears
	 * without borders and head. The version with a parameter returns true
	 * if the application runs undecorated otherwise false;
	 * @param theIsUndecorated true if window should be decorated otherwise false
	 * @example basics.CCAppManagerUndecorated
	 */
	public void undecorated(boolean theIsUndecorated) {
		setProperty(UNDECORATED, theIsUndecorated + "");
	}

	/**
	 * @return true if the frame is undecorated otherwise false
	 */
	public boolean undecorated() {
		return Boolean.parseBoolean(getProperty(UNDECORATED));
	}
	
	public void alwaysOnTop(boolean theIsAlwaysOnTop) {
		setProperty(ALWAYS_ON_TOP, theIsAlwaysOnTop + "");
	}

	/**
	 * @return true if the frame is undecorated otherwise false
	 */
	public boolean alwaysOnTop() {
		return Boolean.parseBoolean(getProperty(ALWAYS_ON_TOP));
	}

	/*
	 * ANTIALIASING
	 */
	private boolean _myIsAntialiasing = false;
	private int _myAntialiasingLevel = 0;
	
	/**
	 * @invisible
	 * @return true if antialiasing is active otherwise false
	 */
	public boolean isAntialiasing() {
		return _myIsAntialiasing;
	}

	/**
	 * Sets and returns the antialising level of the application.
	 * @param theAntialiasingLevel the level of antialiasing
	 * @example basics.CCAppManagerUndecorated
	 */
	public void antialiasing(final int theAntialiasingLevel) {
		_myAntialiasingLevel = theAntialiasingLevel;
		_myIsAntialiasing = _myAntialiasingLevel > 0;
	}

	/**
	 * 
	 * @return the antialiasing level of the application
	 */
	public int antialiasing() {
		return _myAntialiasingLevel;
	}
	
	/*
	 * FRAME SIZE
	 */
	/**
	 * Returns the width for the application.
	 * @return the width for the application
	 */
	public int width() {
		return Integer.parseInt(getProperty(WIDTH));
	}

	/**
	 * Returns the height for the application.
	 * @return the height for the application
	 */
	public int height() {
		return Integer.parseInt(getProperty(HEIGHT));
	}
	
	/**
	 * Sets the size of your application window. Calling size with the values
	 * 320, 240 will open a window of the size 320 x 240 px.
	 * The default size of the window is 400 x 400 pixels
	 * @param theWidth the width of the application window
	 * @param theHeight the height of the application window
	 * @see #location(int, int)
	 * @example basics.CCAppManager
	 */
	public void size(final int theWidth, final int theHeight){
		setProperty(WIDTH, theWidth + "");
		setProperty(HEIGHT, theHeight + "");
	}
	
	public void size(final float theWidth, final float theHeight){
		size((int)theWidth,(int)theHeight);
	}
	
	private CCColor _myBackground = new CCColor(200);
	
	public CCColor background(){
		return _myBackground;
	}
	
	public void background(final CCColor theBackground){
		_myBackground = theBackground;
	}
	
	private float _myFov = 60;
	
	/**
	 * Sets the initial field of view to be used from the camera in degrees,
	 * default value is 60.
	 * @param theFov
	 */
	public void fov(final float theFov) {
		_myFov = theFov;
	}
	
	public float fov() {
		return _myFov;
	}
	
	/*
	 * FRAME POSITION
	 */
	
	/**
	 * Sets the location of the application window. Call setLocation
	 * with the values 100,200 will open the position at the screen
	 * position 100, 200. By default the window will be placed in the 
	 * center of the screen.
	 * @param theX the x location of the application window
	 * @param theY the y location of the application window
	 * @see #size(int, int)
	 * @example basics.CCAppManager
	 */
	public void location(final int theX, final int theY){
		setProperty(X, theX + "");
		setProperty(Y, theY + "");
	}

	/**
	 * Returns the y position of the application window. By default
	 * this value will be -1 and the window will appear in the center
	 * of the screen.
	 * @return y position of the application window
	 * @see #location(int, int)
	 * @see #x()
	 */
	public int y() {
		return Integer.parseInt(getProperty(Y));
	}


	/**
	 * Returns the x position of the application window. By default
	 * this value will be -1 and the window will appear in the center
	 * of the screen.
	 * @return x position of the application window
	 * @see #location(int, int)
	 * @see #y()
	 */
	public int x() {
		return Integer.parseInt(getProperty(X));
	}
	
	/**
	 * @invisible
	 */
	public static enum CCAppModes{
		NORMAL;
	}
	
	Color background = Color.black;
	
	GLCapabilities glCapabilities = null;
	
	////////////////////////////////////////////////////////
	//
	// SETUP LOGGING
	//
	////////////////////////////////////////////////////////
	
	/**
	 * @invisible
	 */
	public static enum CCLoggingFormat{
		SIMPLE,XML;
	}
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static enum CCLoggingHandler{
		CONSOLE,FILE,MEMORY,SOCKET,STREAM;
	}
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static class CCLoggingSettings{
		
		private CCLoggingFormat _myFormater = CCLoggingFormat.SIMPLE;
		private CCLoggingHandler _myHandler = CCLoggingHandler.FILE;
		private Level _myLevel = Level.WARNING;
		
		public Logger logger(final CCAbstractWindowApp theApp){
			final Logger myLogger = Logger.getLogger(theApp.getClass().getName());
			try {
				Handler myHandler;
				
				/* check handler to use for logging */
				switch(_myHandler){
				case FILE:
					final String myPath = "log/"+theApp.getClass().getSimpleName()+".txt";
					CCNIOUtil.createPath(myPath);
					myHandler = new FileHandler(myPath);
					break;
				default:
					myHandler = new ConsoleHandler();
				break;
				}
				
				/* check formatter to use for logging*/
				switch(_myFormater){
				case SIMPLE:
					myHandler.setFormatter(new SimpleFormatter());
					break;
				case XML:
					myHandler.setFormatter(new XMLFormatter());
					break;
				}
				
				myHandler.setLevel(_myLevel);
				myLogger.addHandler(myHandler);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return myLogger;
		}
	}
	
	private CCLoggingSettings _myLogginSettings = new CCLoggingSettings();
	
	public CCLoggingSettings logingSettings(){
		return _myLogginSettings;
	}
	
	/**
	 * Returns a translation vector to move the ui from the upper left corner of the window.
	 * @return translation vector to move the ui 
	 */
	public CCVector2f uiTranslation(){
		return new CCVector2f(Float.parseFloat(getProperty(UI_X)), Float.parseFloat(getProperty(UI_Y)));
	}
	
	/**
	 * Sets the position of the ui. By default the ui is placed at the upper left
	 * corner of the window. To move the ui 100 px down and 200 px right you would call
	 * <code>uiTranslation(200,100)</code>
	 * @param theX horizontal translation in pixel
	 * @param theY vertical translation in pixel
	 */
	public void uiTranslation(final float theX, final float theY){
		setProperty(UI_X, theX + "");
		setProperty(UI_Y, theY + "");
	}
	
	public String uiFile(){
		return getProperty(UI_FILE);
	}
	
	public void uiFile(String theUIFile){
		setProperty(UI_FILE, theUIFile);
	}
	
	public void assetPaths(String theAssetPaths){
		setProperty(ASSET_PATHS, theAssetPaths);
	}
	
	public List<String> assetPaths(){
		String myPaths = getProperty(ASSET_PATHS);
		List<String> myResult = new ArrayList<>();
		if(myPaths == null)return myResult;
		String[] myPathsSeparated = myPaths.split(";");
		for(String myPath:myPathsSeparated){
			myResult.add(myPath);
		}
		return myResult;
	}
}
