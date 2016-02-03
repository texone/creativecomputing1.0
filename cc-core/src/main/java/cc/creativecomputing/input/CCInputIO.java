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
package cc.creativecomputing.input;

import java.util.ArrayList;
import java.util.List;

import jogamp.common.os.PlatformPropsImpl;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.CCSystem;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.util.CCNativeLibUtil;
import cc.creativecomputing.util.logging.CCLog;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectInputEnvironmentPlugin;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.OSXEnvironmentPlugin;


/**
 * <p>
 * CCInputIO is the base class for using controllers in creative computing.
 * It provides methods to retrieve information about the connected 
 * devices and get the input data from them.<br>
 * On start you should use the printDevices() function to see if all controllers
 * are connected and found.
 * </p>
 * @see CCInputDevice
 */
public class CCInputIO implements CCDisposeListener, CCUpdateListener{
	/**
	 * @invisible
	 */
	public static final int ON_PRESS = 0;
	/**
	 * @invisible
	 */
	public static final int ON_RELEASE = 1;
	/**
	 * @invisible
	 */
	public static final int WHILE_PRESS = 2;

	/**
	 * Holds the environment of JInput
	 */
	private final ControllerEnvironment _myEnvironment;

	/**
	 * Instance to the parent application
	 */
	private final CCAbstractWindowApp _myApp;

	/**
	 * List of the available _myDevices
	 */
	private final List<CCInputDevice> _myDevices = new ArrayList<CCInputDevice>();
	
	/**
	 * Gives back the Environment fitting for your OS
	 * @return
	 */
	public static ControllerEnvironment getEnvironment() {
		// setLibPath();
		switch (CCSystem.os) {
		case WINDOWS:
			return new DirectInputEnvironmentPlugin();
		case LINUX:
			return new LinuxEnvironmentPlugin();
		case MACOSX:
			CCNativeLibUtil.prepareLibraryForLoading (ControllerEnvironment.class, "jinput-osx");
			return new OSXEnvironmentPlugin();
		default:
			throw new RuntimeException("Your operating system is not supported");
		}
	}

	/**
	 * Initialize the CCInputIO instance
	 * @param theApp
	 */
	public CCInputIO(final CCAbstractWindowApp theApp){
		CCLog.info(PlatformPropsImpl.ARCH);
		CCLog.info(PlatformPropsImpl.OS);
		CCLog.info(PlatformPropsImpl.os_and_arch);
		_myEnvironment = getEnvironment();
		_myApp = theApp;
		_myApp.addDisposeListener(this);
		_myApp.addUpdateListener(this);
		setupDevices();
	}
	
	/**
	 * Call this method to initialize CCInput. This typically happens one time
	 * in the setup() method of your app. 
	 * <pre>
	 * public void setup(){
	 * 	CCInputIO.init(this);
	 * }
	 * </pre>
	 * 
	 * @shortdesc Initializes CCInputIO
	 * @param theApp reference to the parent app
	 */
//	public static void init(final CCAbstractWindowApp theApp) {
//		if (instance == null){
//			instance = new CCInputIO(theApp);
//		}
//	}

	/**
	 * Puts the available devices into the device list
	 */
	private void setupDevices(){
		final Controller[] controllers = _myEnvironment.getControllers();
		for (int i = 0; i < controllers.length; i++){
			_myDevices.add(new CCInputDevice(controllers[i]));
		}
	}

	/**
	 * dispose method called by the app after closing. The update thread is stopped here
	 * @invisible
	 */
	public void dispose(){
	}
	
	/**
	 * Lists the available devices in the console window. This method
	 * is useful at start to see if all devices are properly connected
	 * and get the name of the desired device.
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #device(int)
	 */
	public void printDevices() {
		System.out.println("\n<<< available input devices: >>>\n");
		for (int i = 0; i < _myDevices.size(); i++){
			System.out.print("     "+i+": ");
			System.out.println(_myDevices.get(i).name());
		}
		System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
	
	/**
	 * Returns the number of available devices
	 * @return the number of available devices
	 * @see CCInputDevice
	 * @see #device(int)
	 */
	public int numberOfDevices() {
		return _myDevices.size();
	}
	
	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceId number of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	public CCInputDevice device(final int theDeviceId) {
		if (theDeviceId >= numberOfDevices()){
			throw new RuntimeException("There is no device with the number " + theDeviceId + ".");
		}
		CCInputDevice result = _myDevices.get(theDeviceId);
		result.open();
		return result;
	}

	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceName String, name of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	public CCInputDevice device(final String theDeviceName) {

		for (int i = 0; i < numberOfDevices(); i++){
			CCInputDevice device = _myDevices.get(i);
			if (device.name().equals(theDeviceName)){
				device.open();
				return device;
			}
		}
		throw new RuntimeException("There is no device with the name " + theDeviceName + ".");
	}

	/**
	 * Updates the _myDevices, to get the actual data before a new
	 * frame is drawn
	 * @invisible
	 */
	public void update(float theDeltaTime){
		for (int i = 0; i < _myDevices.size(); i++){
			_myDevices.get(i).update(theDeltaTime);
		}
	}
}
