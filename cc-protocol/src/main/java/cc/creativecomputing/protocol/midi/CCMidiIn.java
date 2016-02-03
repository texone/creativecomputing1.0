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
package cc.creativecomputing.protocol.midi;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the incoming MIDI data defined by an an MIDI device and
 * an MIDI channel
 * @author tex
 *
 */
class CCMidiIn{
	/**
	 * The MIDIchannel of the MIDIout
	 */
	private final int _myMidiChannel;
	
	/**
	 * List of plugs to handle MIDI events
	 */
	private final List<CCPlug> _myPlugEventList;
	
	/**
	 * List of plugs handling incoming notes
	 */
	private final List<CCPlug> _myNotePlugs;
	
	/**
	 * List of plugs handling incoming controller
	 */
	private final List<CCPlug> _myControllerPlugs;
	
	/**
	 * List of plugs handling incoming program changes
	 */
	private final List<CCPlug> _myProgramChangePlugs;
	
	/**
	 * Initializes a new MidiOutput.
	 * @param theMidiChannel the midiChannel of the MIDI out
	 * @param i_midiInDevice the MIDI port of the MIDI out
	 */
	CCMidiIn(final int theMidiChannel){
		_myMidiChannel = theMidiChannel;
		
		_myPlugEventList = new ArrayList<CCPlug>();
		_myNotePlugs = new ArrayList<CCPlug>();
		_myControllerPlugs = new ArrayList<CCPlug>();
		_myProgramChangePlugs = new ArrayList<CCPlug>();
	}

	/**
	 * Looks if two MidiOuts are equal. This is the case if they have
	 * the same midiChannel and port.
	 * @return true, if the given object is equal to the MidiOut
	 */
	@Override
	public boolean equals(final Object theObject){
		if(!(theObject instanceof CCMidiOutDevice))return false;
		final CCMidiIn midiOut = (CCMidiIn)theObject;
		if(_myMidiChannel != midiOut._myMidiChannel) return false;
		return true;
	}	
	
	/**
	 * plugs a method with the given name of the given object
	 * @param theObject
	 * @param theMethodName
	 */
	void plug(
		final Object theObject, 
		final String theMethodName,
		final int theValue
	){
		List<CCPlug> plugList;
		CCPlug plug = new CCPlug(theObject,theMethodName,theValue);
		switch(plug.plugType()){
			case MIDIEVENT:
				plugList = _myPlugEventList;
				break;
			case NOTE:
				plugList = _myNotePlugs;
				break;	
			case CONTROLLER:
				plugList = _myControllerPlugs;
				break;
			case PROGRAMCHANGE:
				plugList = _myProgramChangePlugs;
				break;
			default:
				throw new RuntimeException("Error on plug "+theMethodName+" check the given event type");
		}
		
		plugList.add(plug);
	}

	/**
	 * Use this method to send a control change to the MIDI output. You can send 
	 * control changes to change the sound on MIDI sound sources for example.
	 * @param theController the controller you want to send.
	 * @param theDeviceNumber 
	 */
	void sendController(
		final CCController theController,
		final int theDeviceNumber,
		final int theMidiChannel
	){
		for(CCPlug myPlug:_myControllerPlugs){
			myPlug.callPlug(theController);
		}
	}

	/**
	 * With this method you can send a note on to your MIDI output. You can send note on commands
	 * to trigger MIDI sound sources. Be aware that you have to take care to send note off commands
	 * to release the notes otherwise you get MIDI hang ons.
	 * @param theNote Note, the note you want to send the note on for
	 */
	void sendNoteOn(
		final CCNoteOn theNote,
		final int theDeviceNumber,
		final int theMidiChannel
	){
		for(CCPlug myPlug: _myNotePlugs){
			myPlug.callPlug(theNote);
		}
	}

	/**
	 * Use this method to send a note off command to your MIDI output. You have to send note off commands 
	 * to release send note on commands.
	 * @param theNote Note, the note you want to send the note off for
	 */
	void sendNoteOff(
		final CCNoteOff theNote,
		final int theDeviceNumber,
		final int theMidiChannel
	){
		for(CCPlug myPlug:_myNotePlugs){
			myPlug.callPlug(theNote);
		}
	}

	/**
	 * With this method you can send program changes to your MIDI output. Program changes are used 
	 * to change the preset on a MIDI sound source.
	 */
	void sendProgramChange(
		final CCProgramChange theProgramChange,
		final int theDeviceNumber,
		final int theMidiChannel
	){
		for(CCPlug myPlug : _myProgramChangePlugs){
			myPlug.callPlug(theProgramChange);
		}
	}
}
