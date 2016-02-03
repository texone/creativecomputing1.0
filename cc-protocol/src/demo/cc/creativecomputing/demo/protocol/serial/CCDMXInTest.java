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
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;
import cc.creativecomputing.protocol.serial.dmx.CCDMXListener;
import cc.creativecomputing.protocol.serial.dmx.CCDMXMessage;

public class CCDMXInTest extends CCApp implements CCDMXListener {

	private CCDMX _myDMX;
	
	private CCTextureMapFont _myFont;

	public void setup() {
		
		_myFont = CCFontIO.createTextureMapFont("Arial", 10);
		g.textFont(_myFont);

		_myDMX = new CCDMX(this, "/dev/cu.usbserial-ENQWD1SM");
		_myDMX.getInterfaceData();
		_myDMX.getSerial();
		_myDMX.addDMXListener(this);
	}

	int[] theData = new int[0];

	public void draw() {
		g.clear();
		int y = -200;
		int x = -200;
		for (int i = 0; i < theData.length; i++) {

			g.color(theData[i]);
			g.rect(x,y-22, 19, 21);
			g.color(255,0,0);
			g.text(theData[i],x,y);
			x+=20;
			if(i%27==0){
				x = -200;
				y +=22;
			}
		}
		// CCLog.info();
		// CCLog.info(_mySerial.firmwareVersion);
		// CCLog.info(_mySerial.breakTime);
		// CCLog.info(_mySerial.markAfterBreakTime);
		// CCLog.info(_mySerial.refreshRate);
		// CCLog.info(_mySerial.serial);
		//		
		// _mySerial.setDMXChannel(0, mouseX);
		// _mySerial.send();
	}

	public void onDMXIn(CCDMXMessage theMessage) {
		theData = theMessage.data();
		for (int i = 0; i < theMessage.data().length; i++) {

		}
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCDMXInTest.class);
		myManager.settings().size(800, 500);
		myManager.start();
	}
}
