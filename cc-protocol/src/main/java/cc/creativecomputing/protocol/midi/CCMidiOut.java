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

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * The MidiOut class is for sending MIDI events. An MidiOut is
 * defined by a port and a MIDI channel. To get a MidiOut you have to use 
 * the midiOut() method of the MidiIO class.
 */
public class CCMidiOut{
	/**
	 * The MIDI channel of the MIDI out
	 */
	private final int _myMidiChannel;

	/**
	 * The MIDI output port of the MIDI out
	 */
	final CCMidiOutDevice _myMidiOutDevice;
	
	static private CCNoteBufferThread noteBuffer;
	
	/**
	 * Initializes a new MidiOutput
	 * @param theMidiChannel the midiChannel of the MIDI out
	 * @param theMidiOutDevice the MIDI port of the MIDI out
	 */
	CCMidiOut(final int theMidiChannel, final CCMidiOutDevice theMidiOutDevice){
		if(noteBuffer == null){
			noteBuffer = new CCNoteBufferThread();
		}
		_myMidiChannel = theMidiChannel;
		_myMidiOutDevice = theMidiOutDevice;
	}

	/**
	 * Looks if two MidiOuts are equal. This is the case if they have the same midiChannel and port.
	 * @return true, if the given object is equal to the MidiOut
	 */
	@Override
	public boolean equals(final Object theObject){
		if(!(theObject instanceof CCMidiOutDevice))return false;
		final CCMidiOut midiOut = (CCMidiOut)theObject;
		if(_myMidiChannel != midiOut._myMidiChannel) return false;
		if(!(_myMidiOutDevice.equals(midiOut._myMidiOutDevice))) return false;
		return true;
	}
	
	/**
	 * @param theEvent
	 * @throws CCMidiException
	 */
	private void sendEvent(final CCMidiMessage theEvent){
		if (theEvent.getChannel() > 15 || theEvent.getChannel() < 0){
			throw new CCMidiException("You tried to send to MIDI channel" + theEvent.getChannel() + ". With MIDI you only have the channels 0 - 15 available.");
		}
		theEvent.channel(_myMidiChannel);
		_myMidiOutDevice.sendEvent(theEvent);
	}
	
	/**
	 * Packs the given data to a MIDI event and sends it to the tracks MIDI out.
	 * @param theCommand
	 * @param theData1
	 * @param theData2
	 */
	@SuppressWarnings("unused")
	private void sendEvent(final int theCommand, final int theData1, final int theData2){
		final CCMidiMessage event = new CCMidiMessage(theCommand, theData1, theData2);
		sendEvent(event);
	}

	/**
	 * Use this method to send a control change to the MIDI output. You can send 
	 * control changes to change the sound on MIDI sound sources for example.
	 * @param theController Controller, the controller you want to send.
	 */
	public void sendController(CCController theController){
		try{
			sendEvent(theController);
		}catch (CCMidiException e){
			if (theController.number() > 127 || theController.number() < 0){
				throw new RuntimeException("You tried to send the controller number " + theController.number() + ". With MIDI you only have the controller numbers 0 - 127 available.");
			}
			if (theController.value() > 127 || theController.value() < 0){
				throw new RuntimeException("You tried to send the controller value " + theController.value() + ". With MIDI you only have the controller values 0 - 127 available.");
			}
		}
	}
	
	public void sendController(int theID, int theValue) {
		sendController(new CCController(theID, theValue));
	}

	/**
	 * With this method you can send a note on to your MIDI output. You can send note on commands
	 * to trigger MIDI sound sources. Be aware that you have to take care to send note off commands
	 * to release the notes otherwise you get MIDI hang ons.
	 * @param theNote Note, the note you want to send the note on for
	 */
	public void sendNote(final CCNoteOn theNote){
		try{
			sendEvent(theNote);
			noteBuffer.addNote(this,theNote);
		}catch (CCMidiException e){
			if (theNote.pitch() > 127 || theNote.pitch() < 0){
				throw new RuntimeException("You tried to send a note with the pitch " + theNote.pitch() + ". With MIDI you only have pitch values from 0 - 127 available.");
			}
			if (theNote.velocity() > 127 || theNote.velocity() < 0){
				throw new RuntimeException("You tried to send a note with the velocity " + theNote.velocity() + ". With MIDI you only have velocities values from 0 - 127 available.");
			}
		}
	}
	
	public void sendNoteOn(final CCNoteOn theNote){
		try{
			sendEvent(theNote);
		}catch (CCMidiException e){
			if (theNote.pitch() > 127 || theNote.pitch() < 0){
				throw new RuntimeException("You tried to send a note with the pitch " + theNote.pitch() + ". With MIDI you only have pitch values from 0 - 127 available.");
			}
			if (theNote.velocity() > 127 || theNote.velocity() < 0){
				throw new RuntimeException("You tried to send a note with the velocity " + theNote.velocity() + ". With MIDI you only have velocities values from 0 - 127 available.");
			}
		}
	}
	
	public void sendNoteOff(final CCNoteOff theNote){
		try{
			sendEvent(theNote);
		}catch (CCMidiException e){
			if (theNote.pitch() > 127 || theNote.pitch() < 0){
				throw new RuntimeException("You tried to send a note with the pitch " + theNote.pitch() + ". With MIDI you only have pitch values from 0 - 127 available.");
			}
			if (theNote.velocity() > 127 || theNote.velocity() < 0){
				throw new RuntimeException("You tried to send a note with the velocity " + theNote.velocity() + ". With MIDI you only have velocities values from 0 - 127 available.");
			}
		}
	}

	/**
	 * With this method you can send program changes to your MIDI output. Program changes are used 
	 * to change the preset on a MIDI sound source.
	 * @param theProgramChange ProgramChange, program change you want to send
	 */
	public void sendProgramChange(final CCProgramChange theProgramChange){
		try{
			sendEvent(theProgramChange);
		}catch (CCMidiException e){
			if (theProgramChange.number() > 127 || theProgramChange.number() < 0){
				throw new RuntimeException("You tried to send the program number " + theProgramChange.number() + ". With MIDI you only have the program numbers 0 - 127 available.");
			}
		}
	}
	
	/**
	 * A Comparator defining how CueNote objects have to be sorted. According
	 * to there length
	 * @author christianr
	 *
	 */
	private static class NoteComparator implements Comparator<CCCueNote>{
		public int compare(final CCCueNote theNote1, final CCCueNote theNote2){
			return (int)(theNote1._myOffTime - theNote2._myOffTime);
		}
	}
	
	/**
	 * Class for saving all necessary information for buffering and
	 * and sending a note off command corresponding to a send note on.
	 * @author christianr
	 *
	 */
	private static class CCCueNote extends CCMidiMessage{
		/**
		 * The MIDI out the note has to be send out
		 */
		private final CCMidiOut _myMidiOut;
		
		/**
		 * the time the note off event has to be send
		 */
		private final long _myOffTime;
		
		CCCueNote(final CCMidiOut i_midiOut,final CCNoteOff note, final long i_offTime){
			super(NOTE_OFF,note.pitch(),note.velocity());
			_myMidiOut = i_midiOut;
			_myOffTime =i_offTime;
		}
		
		/**
		 * triggers the note off
		 *
		 */
		void trigger(){
			try{
				_myMidiOut.sendEvent(this);
			}catch (CCMidiException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * NoteBuffer is a simultaneously running thread buffering all
	 * note off events. All events are events are buffered and send
	 * according to the note length.
	 * @author christianr
	 *
	 */
	private static class CCNoteBufferThread extends Thread{
		
		/**
		 * number of times the thread has been looped
		 */
		private long _myNumberOfLoops = 0;
		
		/**
		 * Set that automatically sort all incoming notes according
		 * there length
		 */
		private final Set<CCCueNote> _myNotes = new TreeSet<CCCueNote>(new NoteComparator());
		
		/**
		 * Initializes a new NoteBuffer by starting the thread
		 */
		CCNoteBufferThread(){
			this.start();
		}
		
		/**
		 * Here all current note off events are send and deleted afterwards
		 */
		public void run(){
			while (true){
				_myNumberOfLoops++;
				try{
					Thread.sleep(1);

					final CCCueNote[] cueNotes = _myNotes.toArray(new CCCueNote[0]);
					int counter = 0;

						while (
							counter < cueNotes.length && 
							cueNotes.length > 0 && 
							cueNotes[counter]._myOffTime <= _myNumberOfLoops
						){
							CCCueNote note = cueNotes[counter];
							note.trigger();
							_myNotes.remove(note);
							counter++;
						}
					
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Adds an note off event to the buffer
		 * @param theMidiOut
		 * @param theNote
		 */
		void addNote(final CCMidiOut theMidiOut, final CCNoteOn theNote){
			_myNotes.add(new CCCueNote(theMidiOut, new CCNoteOff(theNote.pitch()), theNote.length()+_myNumberOfLoops));
		}
	}
}
