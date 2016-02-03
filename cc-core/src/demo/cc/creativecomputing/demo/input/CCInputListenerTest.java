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
package cc.creativecomputing.demo.input;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.input.CCInputButtonListener;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;
import cc.creativecomputing.input.CCInputStick;
import cc.creativecomputing.math.CCMath;

public class CCInputListenerTest extends CCApp{

	private CCInputDevice _myJoypad;
	private CCInputStick _myStick1;
	private CCInputStick _myStick2;

	public void setup(){
		
		CCInputIO myInputIO = new CCInputIO(this);
		
		_myJoypad = myInputIO.device(0);
		_myJoypad.button(1).addListener(new CCInputButtonListener() {
			
			public void onRelease() {
				g.color(255);
			}
			
			public void onPress() {
				g.color(255,0,0);
			}
		});
		
		_myStick1 = _myJoypad.stick(0);
		_myStick1.multiplier(CCMath.PI);
		
		_myStick2 = _myJoypad.stick(1);
		_myStick2.tolerance(0.06f);
		_myStick2.multiplier(0.05f);
	}
	
	public void handleMovement(final float i_x,final float i_y){
	}
	
	public void draw(){
		g.clear();
		g.rotateX(_myStick2.totalY());
		g.rotateY(_myStick2.totalX());
		g.box(200);
	}

	public static void main(String[] args){
		CCApplicationManager myManager = new CCApplicationManager(CCInputListenerTest.class);
		myManager.settings().size(600,600);
		myManager.start();
	}
}

