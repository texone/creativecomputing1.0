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
package cc.creativecomputing.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.net.codec.CCNetPacketCodec;

public class CCUDPOut<MessageType> extends CCNetOut<DatagramChannel, MessageType> {

	public CCUDPOut(CCNetPacketCodec<MessageType> theCodec, String theTargetAddress, int theTargetPort) {
		super(theCodec, theTargetAddress, theTargetPort);
	}
	
	public CCUDPOut(CCNetPacketCodec<MessageType> theCodec, int theTargetPort){
		this(theCodec, "0.0.0.0", theTargetPort);
	}

	protected CCUDPOut(
		CCNetPacketCodec<MessageType> theCodec, 
		String theLocalAddress, int theLocalPort, 
		String theTargetAddress, int theTargetPort
	) {
		super(theCodec, theLocalAddress, theLocalPort, theTargetAddress, theTargetPort);
	}

	@Override
	public void connect(){
		synchronized (_myBufferSyncObject) {
			if ((_myChannel != null) && !_myChannel.isOpen()) {
				_myChannel = null;
			}
			if (_myChannel == null) {
				try {
					_myChannel = DatagramChannel.open();
					_myChannel.socket().bind(_myLocalAddress);
				} catch (IOException e) {
					throw new CCNetException(e);
				}
			}
		}
	}

	@Override
	public boolean isConnected() {
		synchronized (_myBufferSyncObject) {
			return ((_myChannel != null) && _myChannel.isOpen());
		}
	}

	@Override
	public void send(MessageType theMessage, SocketAddress theTarget){
		try {
			synchronized (_myBufferSyncObject) {
				// if( _myChannel == null ) throw new NotYetConnectedException();
				if (_myChannel == null)
					throw new CCNetException("Channel not connected");

				checkBuffer();
				_myByteBuffer.clear();
				_myCodec.encode(theMessage, _myByteBuffer);
				_myByteBuffer.flip();
				_myChannel.send(_myByteBuffer, theTarget);
			}
		} catch (BufferOverflowException e1) {
			throw new CCNetException("Buffer Overflow or Underflow" + theMessage.getClass().getName());
		} catch (IOException e) {
			throw new CCNetException(e);
		}
	}
}
