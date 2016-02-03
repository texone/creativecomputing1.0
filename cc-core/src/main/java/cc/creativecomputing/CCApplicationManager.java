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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import cc.creativecomputing.CCApplicationSettings.CCDisplayMode;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.xml.CCXMLElement;



/**
 * <p>The Application Manager handles the setup and start off an application.
 * Here you can make all settings like Window size, window position etc.
 * To define the settings you can use various methods of the CCSettings class.</p>
 * @author texone
 *
 * @param <CCAppType>
 * @see CCApp
 * @see CCApplicationSettings
 * @example basics.CCAppExample
 */
public class CCApplicationManager{
	
	private final Map<String, GraphicsDevice> _myGraphicsDeviceMap = new HashMap<String, GraphicsDevice>();
	private String[] _myGraphicDeviceNames;
	private GraphicsDevice[] _myGraphicDevices;
	
	private final Map<String, GraphicsConfiguration> _myGraphicsConfigurationMap = new HashMap<String, GraphicsConfiguration>();
	private String[] _myGraphicConfigurationNames;
	private GraphicsConfiguration[] _myGraphicConfigurations;
	
	private GraphicsDevice _myGraphicsDevice;
	private GraphicsConfiguration _myGraphicsConfiguration;
	
	private void initGraphicDeviceSettings(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_myGraphicsDevice = ge.getDefaultScreenDevice();
		
		_myGraphicDevices = ge.getScreenDevices();
		_myGraphicDeviceNames = new String[_myGraphicDevices.length];
		
		for (int i = 0; i < _myGraphicDevices.length;i++) {
			GraphicsDevice myDevice = _myGraphicDevices[i];
			_myGraphicDeviceNames[i] = myDevice.getIDstring();
			_myGraphicsDeviceMap.put(myDevice.getIDstring(), myDevice);
		}
		updateConfigurations();
	}

	/**
	 * Update the available graphic configurations based on the current graphics device
	 */
	private void updateConfigurations() {
		_myGraphicsConfigurationMap.clear();
		_myGraphicsConfiguration = _myGraphicsDevice.getDefaultConfiguration();
		_myGraphicConfigurations = _myGraphicsDevice.getConfigurations();
		_myGraphicConfigurationNames = new String[_myGraphicConfigurations.length];
		
		for (int i = 0; i < _myGraphicConfigurations.length;i++) {
			GraphicsConfiguration myConfig = _myGraphicConfigurations[i];
			String myID = myConfig.getBounds().width + " x " + myConfig.getBounds().height;
			_myGraphicConfigurationNames[i] = myID;
			_myGraphicsConfigurationMap.put(myID, myConfig);
		}
	}
	
	/**
	 * Returns an array with the available Graphic devices.
	 * @return array with the available Graphic devices
	 */
	public String[] deviceNames() {
		return _myGraphicDeviceNames;
	}
	
	/**
	 * Returns an array with the available configurations.
	 * Be aware that this will return the configurations for the current device.
	 * So to get the right configurations set the display first.
	 * @return array with the available configurations
	 */
	public String[] configurationNames() {
		return _myGraphicConfigurationNames;
	}
	
	/**
	 * Returns the graphics device that should be used for Graphic output.
	 * @return the display of the application
	 */
	public GraphicsDevice display() {
		return _myGraphicsDevice;
	}

	/**
	 * Returns the display configuration that should be used for graphic output.
	 * @return the display configuration of the application
	 */
	public GraphicsConfiguration displayConfiguration() {
		return _myGraphicsConfiguration;
	}
	
	/**
	 * Sets the display for the application. This is useful for multi
	 * screen setups. If the display does not exist, the application will start
	 * at the first available one.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	private void display(String theDisplayName) {
		try{
			int myDisplay = Integer.parseInt(theDisplayName);
			if(myDisplay >= _myGraphicDevices.length)return;
			_myGraphicsDevice = _myGraphicDevices[myDisplay];
		}catch(Exception e){
			if(!_myGraphicsDeviceMap.containsKey(theDisplayName))return;
			_myGraphicsDevice = _myGraphicsDeviceMap.get(theDisplayName);
		}
		updateConfigurations();
	}

	
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theConfiguration display for the application
	 */
	public void displayConfiguration(int theConfiguration) {
		_myGraphicsConfiguration = _myGraphicConfigurations[theConfiguration];
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theDisplay display for the application
	 */
	public void displayConfiguration(String theConfigurationName) {
		try{
			int myConfig = Integer.parseInt(theConfigurationName);
			if(myConfig >= _myGraphicConfigurations.length)return;
			_myGraphicsConfiguration = _myGraphicConfigurations[myConfig];
		}catch(Exception e){
			if(!_myGraphicsConfigurationMap.containsKey(theConfigurationName))return;
			_myGraphicsConfiguration = _myGraphicsConfigurationMap.get(theConfigurationName);
		}
	}

	
	/**
	 * Class name of the Application to start
	 */
	private final Class<?> _myClass;
	
	/**
	 * Application that is controlled by the manager
	 */
	private CCAbstractGraphicsApp<?> _myApplication;
	
	private CCAppContainer _myContainer;
	
	private CCApplicationSettings _mySettings;
	
	/**
	 * Creates a new manager from the class object of your application
	 * @param theClass class object of your application
	 * @example basics.CCAppExample
	 */
	public CCApplicationManager(final Class<?> theClass){
		_myClass = theClass;
		
		_mySettings = new CCApplicationSettings();
		_mySettings.title(_myClass.getSimpleName());
		
		initGraphicDeviceSettings();
	}
	
	/**
	 * Gives you access to the internal settings object for more
	 * advanced settings of your application.
	 * @return settings object for more detailed control
	 * @example basics.CCAppManagerUndecorated
	 */
	public CCApplicationSettings settings(){
		return _mySettings;
	}
	
	/**
	 * Makes the settings based on the given application arguments.
	 * Be aware that your settings are completely overridden by this.
	 * @param theArgs
	 */
	public void settings(String[] theArgs){
		_mySettings.append(theArgs);
	}
	
	/**
	 * Makes the settings based on the given application arguments.
	 * Be aware that your settings are completely overridden by this.
	 * @param theArgs
	 */
	public void settings(CCXMLElement theXML){
		_mySettings.append(theXML);
	}
	
	private void startUpdate(){
		try{
			final Class<?> myArguments[] = new Class[]{};
			final Constructor<?> myContructor = _myClass.getConstructor( myArguments );
			
			_myApplication = (CCAbstractGraphicsApp<?>)myContructor.newInstance();
			_myContainer = new CCUpdateContainer();
			
			CCUpdateAnimator myUpdateAnimator = new CCUpdateAnimator(new CCUpdateListener() {
				
				@Override
				public void update(float theDeltaTime) {
					_myApplication.updateEvents().proxy().update(theDeltaTime);
					_myApplication.update(theDeltaTime);
				}
			}, 30);
			_mySettings.appContext(new CCAppContextUpdate(myUpdateAnimator));
			_myApplication.makeSettings(_mySettings, _myContainer);
			_myApplication.setup();
			myUpdateAnimator.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts your Application with the settings you made in the manager.
	 */
	public void start(){
		try {
			CCIOUtil.addAssetPaths(_mySettings.assetPaths());
			
			if(_mySettings.displayMode() == CCDisplayMode.UPDATE_ONLY){
				startUpdate();
				return;
			}
			
			
			final Class<?> myArguments[] = new Class[]{};
			final Constructor<?> myContructor = _myClass.getConstructor( myArguments );
			
			_myApplication = (CCAbstractGraphicsApp<?>)myContructor.newInstance();
			
			GLProfile myProfile;
			
			display(_mySettings.display());
			displayConfiguration(_mySettings.displayConfiguration());
			
			if(_myApplication instanceof CCApp){
				myProfile = GLProfile.getDefault();
			}else{
				myProfile = GLProfile.getMaximum(true);
			}
			
			final GLCapabilities myCapabilities = new GLCapabilities(myProfile);
			myCapabilities.setSampleBuffers(_mySettings.isAntialiasing());
			myCapabilities.setNumSamples(_mySettings.antialiasing());
//			if(_mySettings.stencilBits() >= 0)myCapabilities.setStencilBits(_mySettings.stencilBits());
			_mySettings.glCapabilities = myCapabilities;
			
			GLAutoDrawable myAutoDrawable = null;
			Component myComponent = null;
			
			if(_mySettings.displayMode() == CCDisplayMode.OFFSCREEN){
				_myContainer = new CCGLOffsreenContainer(_myApplication, this);
			}else{
				switch(_mySettings.container()){
				case FRAME:
				case DIALOG:
					GLCanvas myGLCanvas = new GLCanvas(_mySettings.glCapabilities);
					if(_mySettings.appContext().isShared())myGLCanvas.setSharedContext(_mySettings.appContext().glContext());
					myAutoDrawable = myGLCanvas;
					myComponent = myGLCanvas;
					_myContainer = new CCJavaAppContainer(_myApplication, this, myAutoDrawable, myComponent);
					break;
				case NEWT:
					_myContainer = new CCGLWindowContainer(_myApplication, this);
					break;
				}
			}
			
			CCCamera.DEFAULT_FOV = _mySettings.fov();

			_myApplication.makeSettings(_mySettings, _myContainer);
			_myContainer.setVisible(_mySettings.showAppOnStart());
		} catch (Exception e) {
			throw new RuntimeException("COULD NOT START APPLICATION:",e);
		}
	}
	
	/**
	 * Ends the application that is currently running.
	 */
	public void end() {
		_myContainer.dispose();
		_myApplication.dispose();
	}
	
	/**
	 * Use this method to get a reference to the running application. Is only working after
	 * you have started the application. This can be useful if you start your application
	 * from inside another program and need access to it.
	 * @shortdesc Returns a reference to the running application.
	 * @return a reference to your application
	 */
	public CCAbstractGraphicsApp<?> app(){
		return _myApplication;
	}
	
	
}
