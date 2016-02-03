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
import cc.creativecomputing.protocol.serial.CCISerialListener;
import cc.creativecomputing.protocol.serial.CCSerial;
import cc.creativecomputing.util.logging.CCLog;

public class CCSerialTest extends CCApp implements CCISerialListener{

	@SuppressWarnings("unused")
	private CCSerial _mySerial;
	
	public void setup(){
		for(String myPort:CCSerial.list()){
			CCLog.info(myPort);
		}
		
		_mySerial = new CCSerial(this, this, "COM1",57600);
	}
	
	public void draw(){
		
	}
	
	private StringBuffer _myMessageBuffer = new StringBuffer();

	public void onSerialEvent(CCSerial theSerial) {
		char myChar = theSerial.readChar();
		if(myChar == '\n'){
			CCLog.info(_myMessageBuffer.toString().trim());
			_myMessageBuffer = new StringBuffer();
			return;
		}
		_myMessageBuffer.append(myChar);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myApplicationManager = new CCApplicationManager(CCSerialTest.class);
		myApplicationManager.start();
	}
}
