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
package cc.creativecomputing.demo.protocol.serial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.protocol.serial.CCSerial;
import cc.creativecomputing.protocol.serial.sensors.CCISerialSensorListener;
import cc.creativecomputing.protocol.serial.sensors.CCSerialSensors;
import cc.creativecomputing.util.logging.CCLog;

public class CCSensorsTest extends CCApp implements CCISerialSensorListener{

	private CCSerialSensors _mySensors;
	
	public void setup(){
		CCSerial.printPorts();
		_mySensors = new CCSerialSensors(this, "/dev/tty.usbserial",57600);
		_mySensors.addListener(this);
		_mySensors.loadSensors("sensors.xml");
		_mySensors.mouseScale(0.5f);
		_mySensors.drawSensors(true);
	}
	
	public void draw(){
		g.clear();
		g.scale(0.5f);
		_mySensors.draw(g);
	}

	public void onPressSensor(int theID, final int theCategory) {
		CCLog.info(theID+":"+theCategory);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myApplicationManager = new CCApplicationManager(CCSensorsTest.class);
		myApplicationManager.settings().size(1000, 700);
		myApplicationManager.start();
	}
}
