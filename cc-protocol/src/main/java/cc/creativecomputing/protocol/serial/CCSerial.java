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
package cc.creativecomputing.protocol.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.util.CCNativeLibUtil;
import cc.creativecomputing.util.logging.CCLog;

/**
 * <p>
 * The cc serial library allows for easily reading and writing data to and from external machines. 
 * It allows two computers to send and receive data and gives you the flexibility to communicate 
 * with custom microcontroller devices, using them as the input or output to Processing programs.
 * </p>
 * <p>
 * This class is based on the <a href="http://processing.org/reference/libraries/serial/index.html">serial</a> 
 * library of processing written by ben fry and tom igoe.
 * </p>
 * @shortdesc Supports sending data between Processing and external hardware via serial communication (RS-232).
 * @author Christian Riekoff
 *
 */
public class CCSerial implements SerialPortEventListener, CCDisposeListener {
	
	static{
		CCNativeLibUtil.prepareLibraryForLoading(SerialPort.class, "rxtxSerial");
		CommPortIdentifier.getPortIdentifiers();
	}
	
	public static enum CCSerialParity{
		EVEN(SerialPort.PARITY_EVEN),
		MARK(SerialPort.PARITY_MARK),
		NONE(SerialPort.PARITY_NONE),
		ODD(SerialPort.PARITY_ODD),
		SPACCE(SerialPort.PARITY_SPACE);
		
		int id;
		
		CCSerialParity(int theID){
			id = theID;
		}
	}
	
	public static enum CCSerialDataBit{
		DATABITS_5(SerialPort.DATABITS_5),
		DATABITS_6(SerialPort.DATABITS_6),
		DATABITS_7(SerialPort.DATABITS_7),
		DATABITS_8(SerialPort.DATABITS_8);
		
		int id;
		
		CCSerialDataBit(int theID){
			id = theID;
		}
	}
	public static enum CCSerialStopBit{
		STOPBITS_1(SerialPort.STOPBITS_1),
		STOPBITS_1_5(SerialPort.STOPBITS_1_5),
		STOPBITS_2(SerialPort.STOPBITS_2);
		
		int id;
		
		CCSerialStopBit(int theID){
			id = theID;
		}
	}

	// properties can be passed in for default values
	// otherwise defaults to 9600 N81

	// these could be made static, which might be a solution
	// for the classloading problem.. because if code ran again,
	// the static class would have an object that could be closed

	private SerialPort _myPort;

	private int _myRate;
	private CCSerialParity _myParity;
	private CCSerialDataBit _myDataBits;
	private CCSerialStopBit _myStopBits;

	// read buffer and streams

	protected InputStream _myInput;
	private OutputStream _myOutput;

	private byte _myBuffer[] = new byte[32768];
	private int _myBufferIndex;
	private int _myBufferLast;

	// boolean bufferUntil = false;
	private int _myBufferSize = 1; // how big before reset or event firing
	private boolean _myDoBufferUntil;
	private int _myBufferUntilByte;

	// defaults
	private static String DEFAULT_NAME = "COM1";
	public static int DEFAULT_RATE = 9600;
	private static CCSerialParity DEFAULT_PARITY = CCSerialParity.NONE;
	private static CCSerialDataBit DEFAULT_DATABIT = CCSerialDataBit.DATABITS_8;
	private static CCSerialStopBit DEFAULT_STOPBIT = CCSerialStopBit.STOPBITS_1;
	
	private CCISerialListener _mySerialListener;

	public CCSerial(CCAbstractWindowApp theAbstractApp, CCISerialListener theListener) {
		this(theAbstractApp, theListener, DEFAULT_NAME, DEFAULT_RATE, DEFAULT_PARITY, DEFAULT_DATABIT, DEFAULT_STOPBIT);
	}

	public CCSerial(CCAbstractWindowApp theAbstractApp, CCISerialListener theListener, int theRate) {
		this(theAbstractApp, theListener, DEFAULT_NAME, theRate, DEFAULT_PARITY, DEFAULT_DATABIT, DEFAULT_STOPBIT);
	}

	public CCSerial(CCAbstractWindowApp theAbstractApp, CCISerialListener theListener, String theName, int theRate) {
		this(theAbstractApp, theListener, theName, theRate, DEFAULT_PARITY, DEFAULT_DATABIT, DEFAULT_STOPBIT);
	}

	public CCSerial(CCAbstractWindowApp theAbstractApp, CCISerialListener theListener, String theName) {
		this(theAbstractApp, theListener, theName, DEFAULT_RATE, DEFAULT_PARITY, DEFAULT_DATABIT, DEFAULT_STOPBIT);
	}

	/**
	 * 
	 * @param theAbstractApp reference to the application that holds the serial
	 * @param theListener listener to react on incoming serial events
	 * @param theName name of the serial port to open default is COM1
	 * @param theRate rate of the serial communication default is 9600 
	 * @param theParity parity bit for communication default is NONE
	 * @param theDataBits databits for communication default is DATABITS_8
	 * @param theStopBits stopbits for communication default is STOPBITS_1
	 */
	public CCSerial(
		CCAbstractWindowApp theAbstractApp, CCISerialListener theListener, 
		String theName, int theRate, 
		CCSerialParity theParity, CCSerialDataBit theDataBits, CCSerialStopBit theStopBits
	) {
		// if (port != null) port.close();
		// parent.attach(this);
		_mySerialListener = theListener;
		theAbstractApp.addDisposeListener(this);

		_myRate = theRate;
		_myParity = theParity;
		_myDataBits = theDataBits;
		_myStopBits = theStopBits;

		try {
			Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(theName)) {
						_myPort = (SerialPort) portId.open("serial madness", 2000);
						_myInput = _myPort.getInputStream();
						_myOutput = _myPort.getOutputStream();
						_myPort.setSerialPortParams(_myRate, _myDataBits.id, _myStopBits.id, _myParity.id);
						_myPort.addEventListener(this);
						_myPort.notifyOnDataAvailable(true);
					}
				}
			}

		} catch (Throwable e) {
			_myPort = null;
			_myInput = null;
			_myOutput = null;
		}
	}

	/**
	 * Stops data communication on this port. Use to shut the connection when you're finished with the Serial.
	 * Basically just a user-accessible version of dispose().
	 * @shortdesc  	Stops communicating
	 */
	public void stop() {
		dispose();
	}

	/**
	 * Used by CCAbstract to shut things down.
	 * @invisible
	 */
	public void dispose() {
		try {
			// do io streams need to be closed first?
			if (_myInput != null)
				_myInput.close();
			if (_myOutput != null)
				_myOutput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		_myInput = null;
		_myOutput = null;

		try {
			if (_myPort != null)
				_myPort.close(); // close the port

		} catch (Exception e) {
			e.printStackTrace();
		}
		_myPort = null;
	}

	/**
	 * 
	 */
	public void setDTR(boolean state) {
		_myPort.setDTR(state);
	}

	synchronized public void serialEvent(SerialPortEvent serialEvent) {
		if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				while (_myInput.available() > 0) {
					synchronized (_myBuffer) {
						if (_myBufferLast == _myBuffer.length) {
							byte temp[] = new byte[_myBufferLast << 1];
							System.arraycopy(_myBuffer, 0, temp, 0, _myBufferLast);
							_myBuffer = temp;
						}
						_myBuffer[_myBufferLast++] = (byte) _myInput.read();

						//						
						if (
							(_myDoBufferUntil && (_myBuffer[_myBufferLast - 1] == _myBufferUntilByte)) || 
							(!_myDoBufferUntil && ((_myBufferLast - _myBufferIndex) >= _myBufferSize))
						) {
							if(_mySerialListener != null)_mySerialListener.onSerialEvent(this);

						}
						//						
					}
				}

			} catch (IOException e) {
				errorMessage("serialEvent", e);
			}
		}
	}

	/**
	 * Sets the number of bytes to buffer before calling onSerialEvent()
	 * @shortdesc Sets the number of bytes to buffer before calling onSerialEvent
	 * @param theBufferSize number of bytes to buffer
	 */
	public void buffer(final int theBufferSize) {
		_myDoBufferUntil = false;
		_myBufferSize = theBufferSize;
	}

	/**
	 * Sets a specific byte to buffer until before calling onSerialEvent().
	 * @shortdesc Sets a specific byte to buffer to before calling onSerialEvent
	 * @param theBufferUntilByte the value to buffer until
	 */
	public void bufferUntil(final int theBufferUntilByte) {
		_myDoBufferUntil = true;
		_myBufferUntilByte = theBufferUntilByte;
	}

	/**
	 * Returns the number of bytes that have been read from serial and are waiting to be dealt with by the user.
	 * @shortdesc Returns the number of bytes available.
	 * @return the number of bytes available
	 */
	public int available() {
		return (_myBufferLast - _myBufferIndex);
	}

	/**
	 * Ignore all the bytes read so far and empty the buffer.
	 * @shortdesc clear the buffer
	 */
	public void clear() {
		_myBufferLast = 0;
		_myBufferIndex = 0;
	}

	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in the buffer. 
	 * Returns -1 if there is no byte, although this should be avoided by first checking 
	 * available() to see if data is available.
	 * @shortdesc Returns the next byte waiting in the buffer.
	 * @return the next byte waiting in the buffer
	 */
	public int read() {
		if (_myBufferIndex == _myBufferLast)
			return -1;

		synchronized (_myBuffer) {
			int outgoing = _myBuffer[_myBufferIndex++] & 0xff;
			if (_myBufferIndex == _myBufferLast) { // rewind
				_myBufferIndex = 0;
				_myBufferLast = 0;
			}
			return outgoing;
		}
	}

	/**
	 * Same as read() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @shortdesc Returns the last byte received.
	 * @return the last byte received
	 */
	public int last() {
		if (_myBufferIndex == _myBufferLast)
			return -1;
		synchronized (_myBuffer) {
			int outgoing = _myBuffer[_myBufferLast - 1];
			_myBufferIndex = 0;
			_myBufferLast = 0;
			return outgoing;
		}
	}
	
	/**
	 * Returns the next byte in the buffer as a char. Returns -1 or 0xffff if nothing is there.
	 * @shortdesc Returns the next byte in the buffer as a char
	 * @return
	 */
	public char readChar() {
		if (_myBufferIndex == _myBufferLast)
			return (char) (-1);
		return (char) read();
	}

	/**
	 * Same as readChar() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @shortdesc Returns the last byte received as a char
	 * @return the last byte received as a char
	 */
	public char lastChar() {
		if (_myBufferIndex == _myBufferLast)
			return (char) (-1);
		return (char) last();
	}
	
	/**
	 * <p>
	 * Reads a group of bytes from the buffer. The version with no parameters returns a byte array 
	 * of all data in the buffer. This is not efficient, but is easy to use. The version with the 
	 * byteBuffer parameter is more memory and time efficient. It grabs the data in the buffer and 
	 * puts it into the byte array passed in and returns an int value for the number of bytes read. 
	 * If more bytes are available than can fit into the byteBuffer, only those that fit are read.
	 * </p>
	 * @shortdesc Reads everything in the buffer
	 * @return a byte array of anything that's in the serial buffer
	 */
	public byte[] readBytes() {
		if (_myBufferIndex == _myBufferLast)
			return null;

		synchronized (_myBuffer) {
			int length = _myBufferLast - _myBufferIndex;
			byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, outgoing, 0, length);

			_myBufferIndex = 0; // rewind
			_myBufferLast = 0;
			return outgoing;
		}
	}

	/**
	 * @param outgoing passed in byte array to be altered
	 * @return a byte array of anything that's in the serial buffer
	 */
	public int readBytes(byte[] theBytes) {
		if (_myBufferIndex == _myBufferLast)
			return 0;

		synchronized (_myBuffer) {
			int length = _myBufferLast - _myBufferIndex;
			if (length > theBytes.length)
				length = theBytes.length;
			System.arraycopy(_myBuffer, _myBufferIndex, theBytes, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
		}
	}

	/**
	 * Reads from the port into a buffer of bytes up to and including a particular character. 
	 * If the character isn't in the buffer, 'null' is returned. The version with without the 
	 * byteBuffer parameter returns a byte array of all data up to and including the interesting 
	 * byte. This is not efficient, but is easy to use. The version with the byteBuffer parameter 
	 * is more memory and time efficient. It grabs the data in the buffer and puts it into the 
	 * byte array passed in and returns an int value for the number of bytes read. If the byte 
	 * buffer is not large enough, -1 is returned and an error is printed to the message area. 
	 * If nothing is in the buffer, 0 is returned.
	 * 
	 * @shortdesc Reads from the buffer of bytes up to and including a particular character
	 * @param theLookUpByte character designated to mark the end of the data
	 * @return the bytes 
	 */
	public byte[] readBytesUntil(int theLookUpByte) {
		if (_myBufferIndex == _myBufferLast)
			return null;
		byte what = (byte) theLookUpByte;

		synchronized (_myBuffer) {
			int found = -1;
			for (int k = _myBufferIndex; k < _myBufferLast; k++) {
				if (_myBuffer[k] == what) {
					found = k;
					break;
				}
			}
			if (found == -1)
				return null;

			int length = found - _myBufferIndex + 1;
			byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, outgoing, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return outgoing;
		}
	}

	/**
	 * @param theBytes passed in byte array to be altered
	 * @return
	 */
	public int readBytesUntil(int theLookUpByte, byte theBytes[]) {
		if (_myBufferIndex == _myBufferLast)
			return 0;
		byte what = (byte) theLookUpByte;

		synchronized (_myBuffer) {
			int found = -1;
			for (int k = _myBufferIndex; k < _myBufferLast; k++) {
				if (_myBuffer[k] == what) {
					found = k;
					break;
				}
			}
			if (found == -1)
				return 0;

			int length = found - _myBufferIndex + 1;
			if (length > theBytes.length) {
				CCLog.error("readBytesUntil() byte buffer is" + " too small for the " + length + " bytes up to and including char " + theLookUpByte);
				return -1;
			}
			// byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, theBytes, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
		}
	}

	/**
	 * Returns all the data from the buffer as a String. This method assumes the incoming characters are ASCII. 
	 * If you want to transfer Unicode data, first convert the String to a byte stream in the representation of 
	 * your choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * @shortdesc Returns the buffer as a String
	 * @return all the data from the buffer as a String
	 */
	public String readString() {
		if (_myBufferIndex == _myBufferLast)
			return null;
		return new String(readBytes());
	}

	/**
	 * Combination of readBytesUntil and readString. See caveats in each function. 
	 * Returns null if it still hasn't found what you're looking for.
	 * If you want to move Unicode data, you can first convert the String to a byte stream 
	 * in the representation of your choice (i.e. UTF8 or two-byte Unicode data), and send 
	 * it as a byte array.
	 * @shortdesc Returns the buffer as a String up to and including a particular character
	 * @param theLookUpByte character designated to mark the end of the data
	 * @return all the data from the buffer as a String
	 */
	public String readStringUntil(int theLookUpByte) {
		byte b[] = readBytesUntil(theLookUpByte);
		if (b == null)
			return null;
		return new String(b);
	}

	/**
	 * In case you write a String note that this doesn't account for Unicode (two bytes per char), nor will it send UTF8
	 * characters.. It assumes that you mean to send a byte buffer (most often the case for networking and serial i/o) and will
	 * only use the bottom 8 bits of each char in the string. (Meaning that internally it uses String.getBytes)
	 * If you want to move Unicode data, you can first convert the String to a byte stream in the representation of your choice
	 * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * @shortdesc Writes bytes, chars, ints, bytes[], Strings
	 * @param theValue int or char to write
	 */
	public void write(int theValue) { // will also cover char
		try {
			_myOutput.write(theValue & 0xff); // for good measure do the &
			_myOutput.flush(); // hmm, not sure if a good idea

		} catch (Exception e) { // null pointer or serial port dead
			errorMessage("write", e);
		}
	}

	/**
	 * 
	 * @param theBytes bytes to write to the output
	 */
	public void write(byte[] theBytes) {
		try {
			_myOutput.write(theBytes);
			_myOutput.flush(); // hmm, not sure if a good idea

		} catch (Exception e) { // null pointer or serial port dead
			// errorMessage("write", e);
//			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param theString string to write to output
	 */
	public void write(String theString) {
		write(theString.getBytes());
	}

	/**
	 * Gets a list of all available serial ports.
	 * @shortdesc Returns the available ports 
	 * @return list of all available serial ports.
	 */
	static public List<String> list() {
		List<String> myResult = new ArrayList<String>();
		try {
			Enumeration<?> myPorts = CommPortIdentifier.getPortIdentifiers();
			while (myPorts.hasMoreElements()) {
				CommPortIdentifier myportId = (CommPortIdentifier) myPorts.nextElement();
				if (myportId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					String name = myportId.getName();
					myResult.add(name);
				}
			}

		} catch (UnsatisfiedLinkError e) {
			errorMessage("ports", e);

		} catch (Exception e) {
			errorMessage("ports", e);
		}
		return myResult;
	}
	
	public static void printPorts() {
		for(String myPort:list()) {
			System.out.println(myPort);
		}
	}

	/**
	 * General error reporting, all corraled here just in case 
	 * I think of something slightly more intelligent to do.
	 */
	static private void errorMessage(String where, Throwable e) {
		e.printStackTrace();
		throw new RuntimeException("Error inside Serial." + where + "()");
	}
}
