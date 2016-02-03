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
package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.protocol.midi.CCController;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCMidiOut;

public class CCMidiSendTest extends CCApp {

	private CCMidiIO midiIO;
	private CCMidiOut midiOut;

	// add controller 11 with value 0
	private CCController res1 = new CCController(11, 0);

	@Override
	public void setup() {

		// get an instance of MidiIO
		midiIO = CCMidiIO.getInstance();
		midiIO.printDevices();
		// open an midiout, channel 0 on third device
		midiOut = midiIO.midiOut(0, 2);
	}

	@Override
	public void draw() {
		// transform movement to midi
		res1.value(mouseX);
		// send midi
		midiOut.sendController(res1);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMidiSendTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}

}
