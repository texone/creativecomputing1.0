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
package cc.creativecomputing.demo.net.codec.osc;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.net.CCUDPOut;
import cc.creativecomputing.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.net.codec.osc.CCOSCPacketCodec;

public class CCOSCOutReaktorDemo extends CCApp {
	
	private CCUDPOut<CCOSCPacket> _myOut;
	
	@CCControl(name = "freq", min = 0, max = 10000)
	private float _cFrequency = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;

	@Override
	public void setup() {
		_myOut = new CCUDPOut<CCOSCPacket>(new CCOSCPacketCodec(), "10.211.55.3", 9000);
		_myOut.connect();
		
		addControls("app", "app", this);
	}

	float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		_myTime += _cNoiseSpeed * theDeltaTime;
			for(int j = 0; j < 10;j++){
				CCOSCMessage myMessage = new CCOSCMessage("/tunes");
				myMessage.add(j);
				myMessage.add(_cFrequency * CCMath.noise(_myTime, j * 200));
				_myOut.send(myMessage);
			}
	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCOutReaktorDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}
