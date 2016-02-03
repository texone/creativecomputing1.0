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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

class CCMidiInDevice extends CCMidiDevice implements Receiver{

	private final Transmitter _myInputTransmitter;
	
	/**
	 * Contains the states of the 16 midi channels for a device.
	 * true if open otherwise false.
	 */
	private final CCMidiIn[] _myMidiIns = new CCMidiIn [16];

	/**
	 * Initializes a new MidiIn.
	 * @param theMidiIO
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	CCMidiInDevice(
		final MidiDevice theMidiDevice, 
		final int theDeviceNumber
	){
		super(theMidiDevice, theDeviceNumber);
		
		try{
			_myInputTransmitter = theMidiDevice.getTransmitter();
		}catch (MidiUnavailableException e){
			throw new RuntimeException();
		}
	}
	
	String name(){
		return _myMidiDevice.getDeviceInfo().getName();
	}
	
	void open(){
		super.open();
		_myInputTransmitter.setReceiver(this);
	}
	
	void openMidiChannel(final int theMidiChannel){
		if(_myMidiIns[theMidiChannel]==null)
			_myMidiIns[theMidiChannel] = new CCMidiIn(theMidiChannel);
	}
	
	void closeMidiChannel(final int theMidiChannel){
		_myMidiIns[theMidiChannel]=null;
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theMidiChannel
	){
		open();
		openMidiChannel(theMidiChannel);
		_myMidiIns[theMidiChannel].plug(theObject,theMethodName,-1);
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName
	){
		open();
		for(int i = 0; i < 16;i++){
			openMidiChannel(i);
			_myMidiIns[i].plug(theObject,theMethodName,-1);
		}
	}
	
	void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theMidiChannel,
		final int theValue
	){
		open();
		openMidiChannel(theMidiChannel);
		_myMidiIns[theMidiChannel].plug(theObject,theMethodName,theValue);
	}

	/**
	 * Sorts the incoming MidiIO data in the different Arrays.
	 * @invisible
	 * @param theMessage MidiMessage
	 * @param theDeltaTime long
	 */
	public void send(final MidiMessage theMessage, final long theDeltaTime){
		final ShortMessage shortMessage = (ShortMessage) theMessage;

		// get messageInfos
		final int midiChannel = shortMessage.getChannel();

		if (_myMidiIns[midiChannel] == null)
			return;

		final int midiCommand = shortMessage.getCommand();
		final int midiData1 = shortMessage.getData1();
		final int midiData2 = shortMessage.getData2();

		if (midiCommand == CCMidiMessage.NOTE_ON && midiData2 > 0){
			final CCNoteOn note = new CCNoteOn(midiData1, midiData2);
			_myMidiIns[midiChannel].sendNoteOn(note,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiMessage.NOTE_OFF || midiData2 == 0){
			final CCNoteOff note = new CCNoteOff(midiData1);
			_myMidiIns[midiChannel].sendNoteOff(note,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiMessage.CONTROL_CHANGE){
			final CCController controller = new CCController(midiData1, midiData2);
			_myMidiIns[midiChannel].sendController(controller,_myDeviceNumber,midiChannel);
		}else if (midiCommand == CCMidiMessage.PROGRAM_CHANGE){
			final CCProgramChange programChange = new CCProgramChange(midiData1);
			_myMidiIns[midiChannel].sendProgramChange(programChange,_myDeviceNumber,midiChannel);
		}
	}
}
