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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * MidiIO is the base class for managing the available MIDI ports. 
 * It provides you with methods to get information on your ports and 
 * to open them. There are various changes on the new proMIDI version
 * in handling inputs and outputs. Instead of opening a complete port
 * you can now open inputs and outputs with a channel number and a 
 * port name or number. To start use the printDevices method to get
 * all devices available on your system.
 */
public class CCMidiIO{

	/**
	 * Stores all available MIDI input devices
	 */
	final private List<CCMidiInDevice> _myMidiInputDevices = new ArrayList<CCMidiInDevice>();

	/**
	 * Stores all available MIDI output devices
	 */
	final private List<CCMidiOutDevice> _myMidiOutDevices = new ArrayList<CCMidiOutDevice>();

	/**
	 * Contains all open MIDI outs
	 */
	final private Map<String,CCMidiOut> _myOpenMidiOutMap = new HashMap<String,CCMidiOut>();

	/**
	 * Stores the MidiIO instance;
	 */
	private static CCMidiIO instance = new CCMidiIO();

	private CCMidiIO(){
		getAvailablePorts();
	}

	/**
	 * Use this method to get instance of MidiIO. It makes sure that only one 
	 * instance of MidiIO is initialized. 
	 * @return MidiIO, an instance of MidiIO for MIDI communication
	 */
	public static CCMidiIO getInstance(){
		if (instance == null){
			instance = new CCMidiIO();
		}
		return instance;
	}

	/**
	 * Throws an exception if an invalid midiChannel number is put in
	 * @param theMidiChannel
	 */
	private static void checkMidiChannel(final int theMidiChannel){
		if (theMidiChannel < 0 || theMidiChannel > 15){
			throw new RuntimeException("Invalid midiChannel make sure you have a channel number between 0 and 15.");
		}
	}

	/**
	 * The dispose method to close all opened ports.
	 */
	public void dispose(){
		closePorts();
	}
	
	private javax.sound.midi.MidiDevice.Info[] getDeviceInfo(){
		return MidiSystem.getMidiDeviceInfo();
	}
	
	private javax.sound.midi.MidiDevice getDevice(final javax.sound.midi.MidiDevice.Info theInfo) throws MidiUnavailableException{
		return MidiSystem.getMidiDevice(theInfo);
	}

	/**
	 * Method to get all available MIDI ports and add them to the corresponding
	 * device list.
	 */
	private void getAvailablePorts(){
		javax.sound.midi.MidiDevice.Info[] infos = getDeviceInfo();
		for (int i = 0; i < infos.length; i++){
			try{
				javax.sound.midi.MidiDevice theDevice = getDevice (infos[i]);
				
				if (theDevice instanceof javax.sound.midi.Sequencer) {
					// Ignore this device as it's a sequencer
				}else if (theDevice.getMaxReceivers () != 0) {
					_myMidiOutDevices.add(new CCMidiOutDevice(theDevice, i));
				}else if (theDevice.getMaxTransmitters () != 0) {
					_myMidiInputDevices.add(new CCMidiInDevice(theDevice, i));
				}
			}catch (MidiUnavailableException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Use this method to get the number of available MIDI input devices.
	 * @return the number of available MIDI inputs
	 */
	public int numberOfInputDevices(){
		return _myMidiInputDevices.size();
	}

	/**
	 * Use this method to get the number of available MIDI output devices.
	 * @return the number of available MIDI output devices.
	 */
	public int numberOfOutputDevices(){
		return _myMidiOutDevices.size();
	}

	/**
	 * Use this method to get the name of an input device.
	 * @param theInput number of the input 
	 * @return String, the name of the input
	 */
	public String inputDeviceName(final int theInput){
		return _myMidiInputDevices.get(theInput).name();
	}

	/**
	 * Use this method to get the name of an output device.
	 * @param theOutput number of the output
	 * @return the name of the output
	 */
	public String outputDeviceName(final int theOutput){
		return _myMidiOutDevices.get(theOutput).name();
	}

	/**
	 * Use this method for a simple trace of all available MIDI input devices.
	 */
	public void printInputDevices(){
		System.out.println("<< inputs: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (int i = 0; i < numberOfInputDevices(); i++){
			System.out.println("input " + /*parent.nf(*/i/*,2)*/+ " : " + inputDeviceName(i));
		}
	}

	/**
	 * Use this method for a simple trace of all available MIDI output devices.
	 */
	public void printOutputDevices(){
		System.out.println("<< outputs: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (int i = 0; i < numberOfOutputDevices(); i++){
			System.out.println("output " + /*parent.nf(*/i/*,2)*/+ " : " + outputDeviceName(i));
		}
	}

	/**
	 * Use this method for a simple trace of all MIDI devices. Call
	 * this method before working with proMIDI to get the numbers and
	 * names of the installed devices
	 */
	public void printDevices(){
		printInputDevices();
		printOutputDevices();
		System.out.println("<<>>>>>>>>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	/**
	 * Use this method to open an input device. You can open an 
	 * input device with its number or with its name. Once a input device is opened 
	 * it is reserved for your program. All opened devices are closed 
	 * by CCMIDIIO when you close your application. You can also close opened devices 
	 * on your own.<br>
	 * Note that you open inputs with MIDI channels now, this makes you more
	 * flexible with handling incoming MIDI data. Instead of opening an input and
	 * analyzing the incoming events in noteOn, controllerIn, noteOff or programChange
	 * you could use the plug method to directly forward the incoming events to
	 * a method and object of your choice.<br>
	 * If you only pass an inputDevice to the method, you open all channels on that input.
	 * @param inputDeviceNumber number of the input device to open
	 * @param theMidiChannel the MIDI channel of the input to open
	 */
	public void openInput(
		final int theInputDeviceNumber, 
		final int theMidiChannel
	){
		checkMidiChannel(theMidiChannel);
		CCMidiInDevice midiInDevice = _myMidiInputDevices.get(theInputDeviceNumber);
		midiInDevice.open();
		midiInDevice.openMidiChannel(theMidiChannel);
	}
	
	public void openInput(
		final int theInputDeviceNumber
	){
		CCMidiInDevice midiInDevice = _myMidiInputDevices.get(theInputDeviceNumber);
		midiInDevice.open();
	}

	/**
	 * @param theInputDeviceName String, name of the input to open
	 */
	public void openInput(final String theInputDeviceName, final int theMidiChannel){
		checkMidiChannel(theMidiChannel);
		for (int i = 0; i < numberOfInputDevices(); i++){
			CCMidiInDevice midiInDevice = (CCMidiInDevice) _myMidiInputDevices.get(i);
			if (midiInDevice.name().equals(theInputDeviceName)){
				midiInDevice.open();
				midiInDevice.openMidiChannel(theMidiChannel);
			}
		}
		throw new RuntimeException("There is no input device with the name " + theInputDeviceName + ".");
	}

	/**
	 * Plug is a handy method to for incoming midiEvents. To create a plug
	 * you have to implement a method that gets a Note, a Controller or a ProgramChange
	 * as input parameter. Now you can plug these methods using this method to send
	 * the corresponding MIDI events to it.
	 * @param theObject the object with the method to plug
	 * @param theMethodName the name of the method to plug
	 * @param theIntputDeviceNumber the number of the device for the plug
	 * @param theMidiChannel  the MIDI channel for the plug
	 */
	public void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theIntputDeviceNumber, 
		final int theMidiChannel
	){
		CCMidiInDevice midiInDevice = _myMidiInputDevices.get(theIntputDeviceNumber);
		midiInDevice.plug(theObject,theMethodName,theMidiChannel);
	}
	
	public void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theIntputDeviceNumber
	){
		CCMidiInDevice midiInDevice = _myMidiInputDevices.get(theIntputDeviceNumber);
		midiInDevice.plug(theObject,theMethodName);
	}
	
	public void plug(
		final Object theObject, 
		final String theMethodName, 
		final int theInputDeviceNumber, 
		final int theMidiChannel,
		final int theValue
	){
		CCMidiInDevice midiInDevice = _myMidiInputDevices.get(theInputDeviceNumber);
		midiInDevice.plug(theObject,theMethodName,theMidiChannel,theValue);
	}
	/**
	 * Use this method to open an output. You can open an 
	 * output with its number or with its name. Once the output is opened 
	 * it is reserved for your program. All opened ports are closed 
	 * by MIDIO when you close your app. You can also close opened Ports 
	 * on your own.
	 * @param theOutDeviceNumber number of the output to open
	 */
	public CCMidiOut midiOut(final int theMidiChannel, final int theOutDeviceNumber){
		checkMidiChannel(theMidiChannel);
		try{
			final String key = theMidiChannel + "_" + theOutDeviceNumber;
			if (!_myOpenMidiOutMap.containsKey(key)){
				CCMidiOutDevice midiOutDevice = _myMidiOutDevices.get(theOutDeviceNumber);
				midiOutDevice.open();
				final CCMidiOut midiOut = new CCMidiOut(theMidiChannel, midiOutDevice);
				_myOpenMidiOutMap.put(key, midiOut);
			}
			return (CCMidiOut) _myOpenMidiOutMap.get(key);
		}catch (RuntimeException e){
			throw new CCMidiException("You wanted to open the unavailable output " + theOutDeviceNumber + ". The available outputs are 0 - " + (numberOfOutputDevices() - 1) + ".");
		}
	}

	/**
	 * @param theOutDeviceName String, name of the Output to open
	 */
	public CCMidiOut midiOut(final int theMidiChannel, final String theOutDeviceName){
		for (int i = 0; i < numberOfOutputDevices(); i++){
			CCMidiOutDevice midiOutDevice = _myMidiOutDevices.get(i);
			if (midiOutDevice.name().equals(theOutDeviceName)){
				return midiOut(theMidiChannel, i);
			}
		}
		throw new CCMidiException("There is no output with the name " + theOutDeviceName + ".");
	}

	/**
	 * Use this method to close an input. You can close it with its number or name. 
	 * There is no need of closing the ports, as midio closes them when the app 
	 * is closed.
	 * @param theInputNumber number of the input to close
	 */
	public void closeInput(int theInputNumber){
		try{
			CCMidiInDevice inDevice = _myMidiInputDevices.get(theInputNumber);
			inDevice.close();
		}catch (ArrayIndexOutOfBoundsException e){
			throw new CCMidiException("You wanted to close the unavailable input " + theInputNumber + ". The available inputs are 0 - " + (_myMidiInputDevices.size() - 1) + ".");
		}

	}

	/**
	 * @param outputName name of the Input to close
	 */
	public void closeInput(String theInputName){
		for (int i = 0; i < numberOfInputDevices(); i++){
			CCMidiInDevice inDevice = (CCMidiInDevice) _myMidiInputDevices.get(i);
			if (inDevice.name().equals(theInputName)){
				closeInput(i);
				return;
			}
		}
		throw new CCMidiException("There is no input with the name " + theInputName + ".");
	}

	/**
	 * Use this method to close an output. You can close it with its number or name. 
	 * There is no need of closing the ports, as MDIOIO closes them when the app 
	 * is closed.
	 * @param theOutputNumber number of the output to close
	 */
	public void closeOutput(int theOutputNumber){
		try{
			CCMidiOutDevice outDevice = (CCMidiOutDevice) _myMidiOutDevices.get(theOutputNumber);
			outDevice.close();
		}catch (ArrayIndexOutOfBoundsException e){
			throw new CCMidiException("You wanted to close the unavailable output " + theOutputNumber + ". The available outputs are 0 - " + (_myMidiOutDevices.size() - 1) + ".");
		}
	}

	/**
	 * @param theOutputName name of the Output to close
	 */
	public void closeOutput(String theOutputName){
		for (int i = 0; i < numberOfOutputDevices(); i++){
			CCMidiOutDevice outDevice = (CCMidiOutDevice) _myMidiOutDevices.get(i);
			if (outDevice.name().equals(theOutputName)){
				closeOutput(i);
				return;
			}
		}
		throw new CCMidiException("There is no output with the name " + theOutputName + ".");
	}

	/**
	 * Use this method to close all opened inputs. 
	 * There is no need of closing the ports, as MIDIO closes them when the app 
	 * is closed.
	 */
	public void closeInputs(){
		for (int i = 0; i < numberOfInputDevices(); i++){
			closeInput(i);
		}
	}

	/**
	 * Use this method to close all opened outputs. 
	 * There is no need of closing the ports, as MIDIIO closes them when the app 
	 * is closed.
	 */
	public void closeOutputs(){
		for (int i = 0; i < numberOfOutputDevices(); i++){
			closeOutput(i);
		}
	}

	/**
	 * Use this method to close all opened ports. 
	 * There is no need of closing the ports, as MIDIIO closes them when the app 
	 * is closed.
	 * @invisible
	 */
	public void closePorts(){
		closeInputs();
		closeOutputs();
	}
	
	public static void addNoteListener(final CCINoteListener theListener, final int theDevice){
		
	}
	
	public static void writeFile(CCMidiFile theFile, String thePath){
		try {
			MidiSystem.write(theFile._mySequence, 1, new File(thePath));
		} catch (IOException e) {
			throw new CCMidiException("Problem writing midi file! ", e);
		}
	}
}
