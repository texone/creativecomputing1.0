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
package cc.creativecomputing.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cc.creativecomputing.util.logging.CCLog;

public class CCStatusServer extends Thread {

	static final int DEFAULT_PORT = 9876; 
    static final String DEFAULT_NAME = "CreativeComputing App"; 

	private ServerSocket _myServerSocket = null;
 	private long   _myStartTime = 0;
    private float _myFramerate = 0.0f;
    
    private final int _myPort;
    private final String _myAppName;

	public CCStatusServer() {
		this(DEFAULT_PORT, DEFAULT_NAME);
	}
	 
	public CCStatusServer(final int thePort, String theAppName) {
		_myPort = thePort;
		_myAppName = theAppName;
		_myStartTime = System.currentTimeMillis();

		recreateSocket();
        this.start();
    }

	public synchronized void recreateSocket()  {
		try {
			if (_myServerSocket != null) {
				_myServerSocket.close();
				_myServerSocket = null;
			}
			
			_myServerSocket = new ServerSocket(_myPort);

		} catch (IOException ex) {
			CCLog.error("Error creating server socket: " + ex);
		}	
	}
	
	public void run() {
		while(true) {
			Socket myConnection = null;
			try {
				recreateSocket();
				myConnection = _myServerSocket.accept();

				synchronized(this) {
					// don't care what is sent. you write out the status message.
					DataOutputStream myOutputStream = new DataOutputStream(myConnection.getOutputStream());
					myOutputStream.writeBytes(this.getStatusMessage());
					myConnection.close();
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				try {
					myConnection.close();
				} catch(Exception ex2) {
					ex.printStackTrace();
				}
			}
			
			myConnection = null;
		}
	}
	
	public synchronized String getStatusMessage() {
		return "\u0002 " + _myAppName + ":" + (int)_myFramerate + ":" + ((System.currentTimeMillis() - _myStartTime) / 1000 )+ " \u0003";
	}
	
    public synchronized void setFramerate(float theCurrentFramerate) {
        _myFramerate = theCurrentFramerate;
    }
    
	// test main
	public static void main(String[] args) {
		
		CCStatusServer myServer = new CCStatusServer(2348, args[0]);
		myServer.setFramerate(60.0f);
		
		CCLog.info("Starting Status Server\n");
		
		while (true) {
			try {
				Thread.sleep(1000);
				CCLog.info("main running...");
            } catch (Exception ex) {
            	CCLog.error(ex);
			}
		}
	}
}


