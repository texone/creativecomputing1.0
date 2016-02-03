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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.net.CCNetListener;
import cc.creativecomputing.net.CCUDPIn;
import cc.creativecomputing.net.CCUDPOut;
import cc.creativecomputing.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.net.codec.osc.CCOSCPacketCodec;

public class CCOSCByteStreamDemo extends CCApp {
	
	private CCUDPOut<CCOSCPacket> _myOut;
	private CCUDPIn<CCOSCPacket> _myIn;
	
	@CCControl(name = "out", min = 0, max = 1)
	private float _cOut = 0;
	
	private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
	
	float myColor2 = 0;

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myOut = new CCUDPOut<CCOSCPacket>(new CCOSCPacketCodec(),"127.0.0.1",9000);
		_myOut.connect();
		
		_myIn = new CCUDPIn<CCOSCPacket>(new CCOSCPacketCodec(), 9000);
		_myIn.startListening();
		_myIn.addListener(new CCNetListener<CCOSCPacket>() {
			
			@Override
			public void messageReceived(CCOSCPacket theMessage, SocketAddress theSender, long theTime) {
				byte[] myBytes = ((CCOSCMessage)theMessage).blobArgument(0);
				for(byte myByte:myBytes) {
					myColor2 = unsignedByteToInt(myByte) / 255f;
				}
			}
		});
	}
	
	

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 20;i++){
			CCOSCMessage myMessage = new CCOSCMessage("/test");
			byte[] myByte = new byte[70];
			for(int j = 0; j < 70;j++){
				myByte[j] = (byte)(_cOut * 255);
			}
			myMessage.add(myByte);
			_myOut.send(myMessage);
		}
	}

	@Override
	public void draw() {
		g.clear();
		g.color(_cOut);
		g.rect(0,0,100,100);
		g.color(myColor2);
		g.rect(100,0,100,100);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOSCByteStreamDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

