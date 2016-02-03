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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.net.CCUDPOut;
import cc.creativecomputing.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.net.codec.osc.CCOSCPacketCodec;

public class CCTextureDataOutDemo extends CCApp {
	
	private CCUDPOut<CCOSCPacket> _myOut;
	
	private CCTexture2D _myTexture;
	private CCTextureData _myData;

	@Override
	public void setup() {
		
		_myData = new CCTextureData(20, 20);
		for(int x = 0; x < _myData.width();x++){
			for(int y = 0; y < _myData.height();y++){
				_myData.setPixel(x, y, CCColor.random());
			}
		}
		_myTexture = new CCTexture2D(_myData);
	
		_myOut = new CCUDPOut<CCOSCPacket>(new CCOSCPacketCodec(), "127.0.0.1",9000);
		_myOut.connect();
	}

	@Override
	public void update(final float theDeltaTime) {
		for(int i = 0; i < 20;i++){
			CCOSCMessage myMessage = new CCOSCMessage("/image");
			for(int j = 0; j < 70;j++){
				float number = CCMath.random(500);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
				myMessage.add(number);
			}
			_myOut.send(myMessage);
		}
	}

	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture, -width/2, -height/2, width, height);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureDataOutDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}
