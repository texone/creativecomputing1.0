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
package cc.creativecomputing.protocol.proxymatrix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

/**
 * A client connects to a server and sends data back and forth. If anything goes wrong with the connection, for example
 * the host is not there or is listening on a different port, an exception is thrown.
 * 
 * @brief The client class is used to create client Objects which connect to a server to exchange data.
 * @instanceName client any variable of type Client
 * @usage Application
 */
public class CCTCPClient implements Runnable {

	private Thread _myThread;
	private Socket _mySocket;
	private int _myPort;
	private String _myHost;

	private InputStream _myInput;
	private OutputStream _myOutput;

	private byte _myBuffer[] = new byte[32768];
	private int _myBufferIndex;
	private int _myBufferLast;

	/**
	 * 
	 * @param parent typically use "this"
	 * @param theHost address of the server
	 * @param thePort port to read/write from on the server
	 */
	public CCTCPClient(final String theHost, final int thePort) {
		_myHost = theHost;
		_myPort = thePort;

		try {
			_mySocket = new Socket(_myHost, _myPort);
			_myInput = _mySocket.getInputStream();
			_myOutput = _mySocket.getOutputStream();

			_myThread = new Thread(this);
			_myThread.start();

		} catch (ConnectException ce) {
			ce.printStackTrace();
			dispose();

		} catch (IOException e) {
			e.printStackTrace();
			dispose();
		}
	}

	/**
	 * Disconnects from the server. Use to shut the connection when you're finished with the Client. =advanced
	 * Disconnect from the server and calls disconnectEvent(Client c) in the host PApplet.
	 * <P/>
	 * Use this to shut the connection if you're finished with it while your applet is still running. Otherwise, it will
	 * be automatically be shut down by the host PApplet (using dispose, which is identical)
	 * 
	 * @brief Disconnects from the server
	 */
	public void stop() {
		dispose();
	}

	/**
	 * Disconnect from the server: internal use only.
	 * <P>
	 * This should only be called by the internal functions in PApplet, use stop() instead from within your own applets.
	 */
	public void dispose() {
		_myThread = null;
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
			if (_mySocket != null)
				_mySocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		_mySocket = null;
	}

	public void run() {
		while (Thread.currentThread() == _myThread) {
			try {
				while ((_myInput != null) && (_myInput.available() > 0)) { // this will block
					synchronized (_myBuffer) {
						if (_myBufferLast == _myBuffer.length) {
							byte temp[] = new byte[_myBufferLast << 1];
							System.arraycopy(_myBuffer, 0, temp, 0, _myBufferLast);
							_myBuffer = temp;
						}
						_myBuffer[_myBufferLast++] = (byte) _myInput.read();
					}
				}

				try {
					// uhh.. not sure what's best here.. since blocking,
					// do we need to worry about sleeping much? or is this
					// gonna try to slurp cpu away from the main applet?
					Thread.sleep(10);
				} catch (InterruptedException ex) {
				}

			} catch (IOException e) {
				// errorMessage("run", e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return true if this client is still active and hasn't run into any trouble.
	 */
	public boolean active() {
		return (_myThread != null);
	}

	/**
	 * Returns the IP address of the computer to which the Client is attached.
	 * 
	 * @brief Returns the IP address of the machine as a String
	 */
	public String ip() {
		return _mySocket.getInetAddress().getHostAddress();
	}

	/**
	 * Returns the number of bytes available. When any client has bytes available from the server, it returns the number
	 * of bytes.
	 * 
	 * @brief Returns the number of bytes in the buffer waiting to be read
	 */
	public int available() {
		return (_myBufferLast - _myBufferIndex);
	}

	/**
	 * Empty the buffer, removes all the data stored there.
	 * 
	 * @brief Clears the buffer
	 */
	public void clear() {
		_myBufferLast = 0;
		_myBufferIndex = 0;
	}

	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in the buffer. Returns -1 if there is no
	 * byte, although this should be avoided by first checking <b>available()</b> to see if any data is available.
	 * 
	 * @brief Returns a value from the buffer
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
	 * Returns the next byte in the buffer as a char. Returns -1, or 0xffff, if nothing is there.
	 * 
	 * @brief Returns the next byte in the buffer as a char
	 */
	public char readChar() {
		if (_myBufferIndex == _myBufferLast)
			return (char) (-1);
		return (char) read();
	}

	/**
	 * Reads a group of bytes from the buffer. The version with no parameters returns a byte array of all data in the
	 * buffer. This is not efficient, but is easy to use. The version with the <b>byteBuffer</b> parameter is more
	 * memory and time efficient. It grabs the data in the buffer and puts it into the byte array passed in and returns
	 * an int value for the number of bytes read. If more bytes are available than can fit into the <b>byteBuffer</b>,
	 * only those that fit are read. =advanced Return a byte array of anything that's in the serial buffer. Not
	 * particularly memory/speed efficient, because it creates a byte array on each read, but it's easier to use than
	 * readBytes(byte b[]) (see below).
	 * 
	 * @brief Reads everything in the buffer
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
	 * Grab whatever is in the serial buffer, and stuff it into a byte buffer passed in by the user. This is more
	 * memory/time efficient than readBytes() returning a byte[] array.
	 * 
	 * Returns an int for how many bytes were read. If more bytes are available than can fit into the byte array, only
	 * those that will fit are read.
	 * 
	 * @param theBytebuffer passed in byte array to be altered
	 */
	public int readBytes(final byte[] theBytebuffer) {
		if (_myBufferIndex == _myBufferLast)
			return 0;

		synchronized (_myBuffer) {
			int length = _myBufferLast - _myBufferIndex;
			if (length > theBytebuffer.length)
				length = theBytebuffer.length;
			System.arraycopy(_myBuffer, _myBufferIndex, theBytebuffer, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
		}
	}

	/**
	 * Reads from the port into a buffer of bytes up to and including a particular character. If the character isn't in
	 * the buffer, 'null' is returned. The version with no <b>byteBuffer</b> parameter returns a byte array of all data
	 * up to and including the <b>interesting</b> byte. This is not efficient, but is easy to use. The version with the
	 * <b>byteBuffer</b> parameter is more memory and time efficient. It grabs the data in the buffer and puts it into
	 * the byte array passed in and returns an int value for the number of bytes read. If the byte buffer is not large
	 * enough, -1 is returned and an error is printed to the message area. If nothing is in the buffer, 0 is returned.
	 * 
	 * @brief Reads from the buffer of bytes up to and including a particular character
	 * @param theInteresting character designated to mark the end of the data
	 */
	public byte[] readBytesUntil(final int theInteresting) {
		if (_myBufferIndex == _myBufferLast)
			return null;
		byte what = (byte) theInteresting;

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
	 * Reads from the serial port into a buffer of bytes until a particular character. If the character isn't in the
	 * serial buffer, then 'null' is returned.
	 * 
	 * If outgoing[] is not big enough, then -1 is returned, and an error message is printed on the console. If nothing
	 * is in the buffer, zero is returned. If 'interesting' byte is not in the buffer, then 0 is returned.
	 * 
	 * @param theByteBuffer passed in byte array to be altered
	 */
	public int readBytesUntil(final int theInteresting, final byte theByteBuffer[]) {
		if (_myBufferIndex == _myBufferLast)
			return 0;
		byte what = (byte) theInteresting;

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
			if (length > theByteBuffer.length) {
				System.err.println("readBytesUntil() byte buffer is" + " too small for the " + length + " bytes up to and including char " + theInteresting);
				return -1;
			}
			// byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, theByteBuffer, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
		}
	}

	/**
	 * Returns the all the data from the buffer as a String. This method assumes the incoming characters are ASCII. If
	 * you want to transfer Unicode data, first convert the String to a byte stream in the representation of your choice
	 * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * 
	 * @brief Returns the buffer as a String
	 */
	public String readString() {
		if (_myBufferIndex == _myBufferLast)
			return null;
		return new String(readBytes());
	}

	/**
	 * Combination of <b>readBytesUntil()</b> and <b>readString()</b>. Returns <b>null</b> if it doesn't find what
	 * you're looking for. =advanced
	 * <p/>
	 * If you want to move Unicode data, you can first convert the String to a byte stream in the representation of your
	 * choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * 
	 * @brief Returns the buffer as a String up to and including a particular character
	 * @param theInteresting character designated to mark the end of the data
	 */

	public String readStringUntil(final int theInteresting) {
		byte b[] = readBytesUntil(theInteresting);
		if (b == null)
			return null;
		return new String(b);
	}

	/**
	 * Writes data to a server specified when constructing the client.
	 * 
	 * @brief Writes bytes, chars, ints, bytes[], Strings
	 * @param theData data to write
	 */
	public void write(final int theData) { // will also cover char
		try {
			_myOutput.write(theData & 0xff); // for good measure do the &
			_myOutput.flush(); // hmm, not sure if a good idea
		} catch (Exception e) { // null pointer or serial port dead
			e.printStackTrace();
			stop();
		}
	}

	public void write(final byte theData[]) {
		try {
			_myOutput.write(theData);
			_myOutput.flush(); // hmm, not sure if a good idea
		} catch (Exception e) { // null pointer or serial port dead
			e.printStackTrace();
			stop();
		}
	}

	/**
	 * Write a String to the output. Note that this doesn't account for Unicode (two bytes per char), nor will it send
	 * UTF8 characters.. It assumes that you mean to send a byte buffer (most often the case for networking and serial
	 * i/o) and will only use the bottom 8 bits of each char in the string. (Meaning that internally it uses
	 * String.getBytes)
	 * 
	 * If you want to move Unicode data, you can first convert the String to a byte stream in the representation of your
	 * choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 */
	public void write(final String theData) {
		write(theData.getBytes());
	}

}
