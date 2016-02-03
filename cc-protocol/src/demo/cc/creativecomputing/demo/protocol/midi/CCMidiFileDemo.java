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
import cc.creativecomputing.protocol.midi.CCMidiFile;
import cc.creativecomputing.protocol.midi.CCMidiIO;
import cc.creativecomputing.protocol.midi.CCMidiTrack;
import cc.creativecomputing.protocol.midi.CCNoteOff;
import cc.creativecomputing.protocol.midi.CCNoteOn;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCMidiFileDemo extends CCApp {
	
	private CCMidiFile _myFile;

	@Override
	public void setup() {
		
		// Create a new MIDI file with 30 ticks and 30 frames timing
		_myFile = new CCMidiFile(1);

		loadData();
		
		CCMidiIO.writeFile(_myFile, "130411_notes.mid");

	}
	
	private void loadData(){
		CCXMLElement myParticleGrid = CCXMLIO.createXMLElement("ackugelkinetik714_20100806.xml");
		for(CCXMLElement myMotionData:myParticleGrid){
			CCXMLElement vectorofvector2f = myMotionData.child(0);
			String myContent = vectorofvector2f.content();
			float[] myValues = readData(myContent);
			if(myValues.length <= 10 || myValues[10] == 2){
				continue;
			}
			writeMidiData(myMotionData.attribute("gridpos"), myValues);
			System.out.println(myMotionData.attribute("gridpos"));
//			System.out.println(myContent);
		}
	}
	
	private void writeMidiData(String theTrackName, float[] theData){
		// Obtain a MIDI track from the file
		CCMidiTrack myTrack = _myFile.createTrack();
		myTrack.trackName(theTrackName);
		myTrack.addEvent(new CCNoteOn(60, 100), 0);
		myTrack.addEvent(new CCNoteOff(60), 1);
		float myLastValue = 0;
		float myLastSpeed = 0;
		for (int i = 0; i < theData.length; i++) {
			float myValue = CCMath.map(theData[i], 0, 192, 0, 127);
			if(i > 0){
				float mySpeed = myValue - myLastValue;
				if(CCMath.sign(mySpeed) != CCMath.sign(myLastSpeed)){
					myTrack.addEvent(new CCNoteOn((int)myValue, 100), i);
					myTrack.addEvent(new CCNoteOff((int)myValue), i + 1);
				}
				myLastSpeed = mySpeed;
			}
			myLastValue = myValue;
			myTrack.addEvent(new CCController(0x0A, (int)myValue), i);
		}
		myTrack.addEndOfTrack(theData.length);
	}
	
	private float[] readData(String theContent){
		theContent = theContent.replaceAll("\\[\\[", "");
		theContent = theContent.replaceAll("\\]\\]", "");
		String[] theData = theContent.split("\\],\\[");
		
		float[] myValues = new float[theData.length];
		for(int i = 0; i < theData.length;i++){
			String[] myVector = theData[i].split(",");
			try{
				myValues[i] = Float.parseFloat(myVector[0]);
			}catch(Exception e){
				System.out.println(myVector[0]);
			}
		}
		return myValues;
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMidiFileDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
