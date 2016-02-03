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

/**
 * @author maxgoettner
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class CCUDPClient implements Runnable {
	
	public static final int FRAMESIZE = 4096;
	public static final int PROXY_HDR = 92;
	public static final int UDP_HDR   = 8;
	public static final int TOUCHSIZE = 160;
	
	public float[] _myRecentPacket = new float[UDP_HDR+PROXY_HDR+TOUCHSIZE+FRAMESIZE];
	private Thread _myThread;
	private int _myPort;
	private DatagramSocket _mySocket;
	private InputStream _myInput;
	private OutputStream _myOutput;
	
	public CCUDPClient(int thePort){
		_myPort = thePort;

		try {
			_mySocket = new DatagramSocket(_myPort);
			//_mySocket.connect(new InetSocketAddress(_myIP, _myPort));

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
	
	public byte read(){
		return 0;
	}
	
	public void run(){
		byte[] rcvData = new byte[UDP_HDR+PROXY_HDR+TOUCHSIZE+FRAMESIZE];
		DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
	
		try{
			while(true){
				_mySocket.receive(rcvPkt);
				synchronized (_myRecentPacket){
					for (int i=0; i<rcvData.length; i++){
						_myRecentPacket[i] = (0xFF)&rcvData[i];
					}
				}
			}
		}
		catch (IOException e){}
	}

	public void getFrame(float[] readPacket){
		synchronized(_myRecentPacket){
			for (int i=0; i<readPacket.length; i++){
				readPacket[i] = _myRecentPacket[UDP_HDR+PROXY_HDR+TOUCHSIZE+i];
			}
		}
	}
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

	public byte[] getTouchData () {
		byte[] touchData = new byte[TOUCHSIZE];
		for (int i=0; i<TOUCHSIZE; i++) {
			touchData[i] = (byte)_myRecentPacket[i+UDP_HDR+PROXY_HDR];
		}
		return touchData;
	}
	
	public byte[] getHeader() {
		byte[] hdr = new byte[UDP_HDR+PROXY_HDR];
		
		synchronized(_myRecentPacket){
			for (int i=0; i<UDP_HDR+PROXY_HDR; i++){
				hdr[i] = (byte) _myRecentPacket[i];
			}
		}
		return hdr;
	}
}
