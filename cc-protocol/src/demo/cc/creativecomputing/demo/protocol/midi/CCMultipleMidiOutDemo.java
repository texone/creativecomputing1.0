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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.midi.CCController;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCMidiOut;
import cc.creativecomputing.protocol.midi.CCNoteOn;
import cc.creativecomputing.protocol.midi.CCProgramChange;


public class CCMultipleMidiOutDemo extends CCApp{

	private CCMidiIO midiIO;

	private CCMidiOut midiOut;

	private Bowl[] bowl;

	@Override
	public void setup(){

		// get an instance of MidiIO
		midiIO = CCMidiIO.getInstance();
		midiIO.printDevices();

		// print a list with all available devices
		midiIO.printDevices();

		// open an midiout using the first device and the first channel
		midiOut = midiIO.midiOut(0, 1);

		bowl = new Bowl[10];
		for (int i = 0; i < bowl.length; i++){
			bowl[i] = new Bowl(i);
		}
	}

	@Override
	public void draw(){
		g.clear();
		for (int i = 0; i < bowl.length; i++){
			bowl[i].move();
			bowl[i].paint();
		}
	}

	class Bowl{

		float xSpeed;

		float ySpeed;

		float xPos;

		float yPos;

		CCNoteOn note;

		int myNumber;

		Bowl(int number){
			xSpeed = CCMath.random(10);
			ySpeed = CCMath.random(10);
			note = new CCNoteOn(0, 0, 0);
			myNumber = number;
		}

		public void move(){
			xPos += xSpeed;
			yPos += ySpeed;
			midiOut.sendController(new CCController(myNumber, (int)(xPos / 6) + 2));

			if (xPos > width || xPos < 0){
				xSpeed = -xSpeed;
				xPos += xSpeed;

				playNote();
			}
			if (yPos > width || yPos < 0){
				ySpeed = -ySpeed;
				yPos += ySpeed;
				playNote();
				midiOut.sendProgramChange(new CCProgramChange(myNumber));
			}
		}

		public void playNote(){
			note = new CCNoteOn((int)(xPos / 5f), (int)(yPos / 10f) + 60, (int) CCMath.random(200));
			midiOut.sendNoteOn(note);
		}

		public void paint(){
			g.ellipse(xPos, yPos, 20, 20);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMultipleMidiOutDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
