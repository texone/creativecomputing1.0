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


import java.net.SocketAddress;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.net.CCNetListener;
import cc.creativecomputing.net.CCUDPIn;
import cc.creativecomputing.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.net.codec.osc.CCOSCPacketCodec;
import cc.creativecomputing.util.logging.CCLog;

public class CCOSCInDemo extends CCApp {
	
	private CCUDPIn<CCOSCPacket> _myIn;

	@Override
	public void setup() {
		_myIn = new CCUDPIn<CCOSCPacket>(new CCOSCPacketCodec(), 9000);
		_myIn.startListening();
		_myIn.addListener(new CCNetListener<CCOSCPacket>() {
			
			@Override
			public void messageReceived(CCOSCPacket theMessage, SocketAddress theSender, long theTime) {
				CCLog.info("RECEIVE:"+theMessage);
				CCLog.info(theMessage.size());
				
			}
		});
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCInDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}
