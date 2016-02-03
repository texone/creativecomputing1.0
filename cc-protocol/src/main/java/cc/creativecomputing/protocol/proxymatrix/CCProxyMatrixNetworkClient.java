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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.creativecomputing.math.CCVector4f;


/**
 * @author christianriekoff
 *
 */
public class CCProxyMatrixNetworkClient implements Runnable{
	
	/**
	 * stores if the connection to the proxymatrix is established
	 */
	private boolean _myIsConnected = false;
	
	private CCPixelRaster _myRawRaster;
	private List<CCVector4f> _myTouches;
	
	private int _myMatrixWidth;
	private int _myMatrixHeight;
	
	/**
	 * this is in ms, how long we sleep till we talk to the box again
	 */
	private int _myRefreshRate; 

	/**
	 * network client
	 */
	//private CCTCPClient _myClient = null;
	private CCUDPClient _myClient = null;
	private Thread _myThread;
	
	private List<CCProxyMatrixClientListener> _myListener = new ArrayList<CCProxyMatrixClientListener>();
	
	private String _myIP;
	private int _myPort;
	
	public CCProxyMatrixNetworkClient(
		final CCProxyMatrixClientListener theListener,
		final String theIP, final int thePort,
		final int theMatrixWidth, final int theMatrixHeight
	) {
		_myListener.add(theListener);
		
		updateMatrixSize(theMatrixWidth, theMatrixHeight);
		_myTouches = new ArrayList<CCVector4f>();
		
		_myRefreshRate = 10;
//		pixels = matrixAsPixelArray();
		
		_myIP = theIP;
		_myPort = thePort;
	}
	
	public void connect() {

		openConnection(_myIP, _myPort);

		_myThread = new Thread(this);
		_myThread.start();
	}
	
	/**
	 * Open connection to the proxy matrix
	 * @param theIP
	 * @param thePort
	 */
	private void openConnection(final String theIP, final int thePort) {

		_myClient = new CCUDPClient(thePort);
		delay(500);
		_myIsConnected = true;
	}
	
	public void run() {
		while (true) {
			update();
			// System.out.println( "update" );
			delay(_myRefreshRate); // refreshRate in ms
		}
	}
	
	private void delay(int milis) {
		if (milis > 0) {
			try {
				Thread.sleep(milis);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void updateMatrixSize(final int theNewMatrixWidth, final int theNewMatrixHeight) {
		if (theNewMatrixHeight != _myMatrixHeight || _myMatrixWidth != theNewMatrixWidth) {
			_myMatrixHeight = theNewMatrixHeight;
			_myMatrixWidth = theNewMatrixWidth;
			_myRawRaster = new CCPixelRaster("RAW CLIENT",_myMatrixWidth, _myMatrixHeight);
			
			for(CCProxyMatrixClientListener myListener:_myListener) {
				myListener.onChangeMatrixSize(_myMatrixWidth, _myMatrixHeight);
			}
		}
	}
	

	private void update() {
		byte[] header = _myClient.getHeader();
		byte[] touchData = _myClient.getTouchData();
		
		
		@SuppressWarnings("unused")
		String serial = ByteConversion.toString(Arrays.copyOfRange(header, CCUDPClient.UDP_HDR+14*4, CCUDPClient.UDP_HDR+14*4+32));

		// read the frame and update in proximatrix
		float[] frameTmp = new float[4096];
		if (_myIsConnected && _myClient != null){
			_myClient.getFrame(frameTmp);
			for (int i=0; i<_myRawRaster.width(); i++){
				for (int j=0; j<_myRawRaster.height(); j++){
					
					float val = frameTmp[i*_myRawRaster.width()+j];
					_myRawRaster.set(j, i, val);
					
					/*
					if (serial.compareTo(SERIAL_BROMBEERE)==0)
						_myRawRaster.set(i, j, frameTmp[i*_myRawRaster.width()+j]);
					else if (serial.compareTo(SERIAL_HIMBEERE)==0)
						_myRawRaster.set(64-i, 64-j, frameTmp[i*_myRawRaster.width()+j]);
					else
						_myRawRaster.set(i, j, frameTmp[i*_myRawRaster.width()+j]);
					*/
				}
			}
			for(CCProxyMatrixClientListener myListener:_myListener) {
				myListener.onUpdateRaster(_myRawRaster);
			}
			
			
			// read the touch data and update in proximatrix
			int[] touches = new int[40];
			for (int i=0; i<10; i++) {
				for (int j=0; j<4; j++) {
					touches[4*i+j] = ByteConversion.toIntRev(Arrays.copyOfRange(touchData,i*16+j*4,i*16+j*4+4));
				}
			}
			_myTouches.clear();
			for (int i=0; i<10; i++) {
				if (touches[4*i] != -1) {
					_myTouches.add(new CCVector4f(touches[4*i],touches[4*i+1],touches[4*i+2],touches[4*i+3]));
				}
			}
				
			for(CCProxyMatrixClientListener myListener:_myListener) {
				myListener.onUpdateTouches (_myTouches);
			}
		}
	}

	
	public void setFrameRate(float fps) {
		_myRefreshRate = (int)(1000 / fps);
	}
}
