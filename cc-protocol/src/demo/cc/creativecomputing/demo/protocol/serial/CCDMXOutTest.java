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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.protocol.serial.CCSerial;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;
import cc.creativecomputing.util.logging.CCLog;

public class CCDMXOutTest extends CCApp {

	private CCDMX _myDMX;
	
	private CCTextureMapFont _myFont;
	
	private class DMFaderSet{
		@CCControl(name = "fader 01", min = 0, max = 255)
		private int _cFader1 = 0;

		@CCControl(name = "fader 02", min = 0, max = 255)
		private int _cFader2 = 0;

		@CCControl(name = "fader 03", min = 0, max = 255)
		private int _cFader3 = 0;

		@CCControl(name = "fader 04", min = 0, max = 255)
		private int _cFader4 = 0;

		@CCControl(name = "fader 05", min = 0, max = 255)
		private int _cFader5 = 0;

		@CCControl(name = "fader 06", min = 0, max = 255)
		private int _cFader6 = 0;

		@CCControl(name = "fader 07", min = 0, max = 255)
		private int _cFader7 = 0;

		@CCControl(name = "fader 08", min = 0, max = 255)
		private int _cFader8 = 0;

		@CCControl(name = "fader 09", min = 0, max = 255)
		private int _cFader9 = 0;

		@CCControl(name = "fader 10", min = 0, max = 255)
		private int _cFader10 = 0;

		@CCControl(name = "fader 11", min = 0, max = 255)
		private int _cFader11 = 0;

		@CCControl(name = "fader 12", min = 0, max = 255)
		private int _cFader12 = 0;
	}
	
	private DMFaderSet _mySet1 = new DMFaderSet();
	private DMFaderSet _mySet2 = new DMFaderSet();
	private DMFaderSet _mySet3 = new DMFaderSet();
	private DMFaderSet _mySet4 = new DMFaderSet();
	
	@CCControl(name = "port 0", min = 0, max = 511)
	private int _cPort0 = 0;
	@CCControl(name = "port 1", min = 0, max = 512)
	private int _cPort1 = 512;
	@CCControl(name = "value", min = 0, max = 255)
	private int _cValue = 0;
	
	private DMFaderSet[] _mySets = new DMFaderSet[]{_mySet1,_mySet2,_mySet3,_mySet4};
	
	private static String DMX_PORT = "/dev/cu.usbserial-EN119017";

	public void setup() {
		
		_myFont = CCFontIO.createTextureMapFont("Arial", 10);
		g.textFont(_myFont);

		_myDMX = new CCDMX(this, DMX_PORT, 512);
		_myDMX.getInterfaceData();
		_myDMX.getSerial();
		
		addControls("app", "global", this);
		addControls("dmx","01-12",0,_mySet1);
		addControls("dmx","13-24",1,_mySet2);
		addControls("dmx","25-36",2,_mySet3);
		addControls("dmx","37-48",3,_mySet4);
		
	}
	
	@Override
	public void update(float theDeltaTime) {
		super.update(theDeltaTime);
	}

	public void draw() {
		g.clear();
		
		for(int i = 0; i < _mySets.length;i++){
			int myChannel = i * 12;
			_myDMX.setDMXChannel(myChannel + 0, _mySets[i]._cFader1);
			_myDMX.setDMXChannel(myChannel + 1, _mySets[i]._cFader2);
			_myDMX.setDMXChannel(myChannel + 2, _mySets[i]._cFader3);
			_myDMX.setDMXChannel(myChannel + 3, _mySets[i]._cFader4);
			_myDMX.setDMXChannel(myChannel + 4, _mySets[i]._cFader5);
			_myDMX.setDMXChannel(myChannel + 5, _mySets[i]._cFader6);
			_myDMX.setDMXChannel(myChannel + 6, _mySets[i]._cFader7);
			_myDMX.setDMXChannel(myChannel + 7, _mySets[i]._cFader8);
			_myDMX.setDMXChannel(myChannel + 8, _mySets[i]._cFader9);
			_myDMX.setDMXChannel(myChannel + 9, _mySets[i]._cFader10);
			_myDMX.setDMXChannel(myChannel + 10, _mySets[i]._cFader11);
			_myDMX.setDMXChannel(myChannel + 11, _mySets[i]._cFader12);
			
			g.color(_mySets[i]._cFader1);
			g.rect((myChannel + 0) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader2);
			g.rect((myChannel + 1) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader3);
			g.rect((myChannel + 2) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader4);
			g.rect((myChannel + 3) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader5);
			g.rect((myChannel + 4) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader6);
			g.rect((myChannel + 5) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader7);
			g.rect((myChannel + 6) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader8);
			g.rect((myChannel + 7) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader9);
			g.rect((myChannel + 8) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader10);
			g.rect((myChannel + 9) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader11);
			g.rect((myChannel + 10) * 10 - width/2,-height/2,10,height);
			g.color(_mySets[i]._cFader12);
			g.rect((myChannel + 11) * 10 - width/2,-height/2,10,height);
		}
//		for(int i = _cPort0; i < _cPort1;i++){
//			_myDMX.setDMXChannel(i, _cValue);
//		}
		_myDMX.send();
	}

	@Override
	public void mousePressed(CCMouseEvent theEvent) {

	}

	public static void main(String[] args) {
		CCSerial.printPorts();
		
		if(args.length == 0){
			CCLog.info("You have to specify a serial connection for dmx output");
		}else{
			DMX_PORT = args[0];
		}
		
		final CCApplicationManager myManager = new CCApplicationManager(CCDMXOutTest.class);
		myManager.settings().size(800, 500);
		myManager.start();
	}
}
