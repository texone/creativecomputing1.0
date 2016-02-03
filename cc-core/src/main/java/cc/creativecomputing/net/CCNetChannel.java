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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.net.codec.CCNetPacketCodec;

/**
 * A collection of common constants and methods that apply to all kinds of network communicators.
 * 
 * @author Christian Riekoff
 */

public abstract class CCNetChannel<ChannelType extends SelectableChannel, MessageType> {

	

	/**
	 * The default buffer size (in bytes) and maximum OSC packet size (8K at the moment).
	 */
	public static final int DEFAULTBUFSIZE = 8192;
	
	protected ChannelType _myChannel;

	protected CCNetPacketCodec<MessageType> _myCodec;
	protected final InetSocketAddress _myLocalAddress;
	
	protected boolean _myAllocateBuffer = true;
	protected int _myBufferSize = DEFAULTBUFSIZE;
	protected ByteBuffer _myByteBuffer = null;
	protected final Object _myBufferSyncObject = new Object(); // buffer (re)allocation

	protected CCNetChannel(CCNetPacketCodec<MessageType> theCodec, InetSocketAddress theLocalAddress) {
		_myCodec = theCodec;
		_myLocalAddress = theLocalAddress;
	}

	/**
	 * Queries the codec used in packet coding and decoding.
	 * 
	 * @return the current codec of this channel
	 */
	public CCNetPacketCodec<MessageType> codec() {
		return _myCodec;
	}

	/**
	 * Queries the communicator's local socket address. You can determine the host and port from the returned address by
	 * calling <code>getHostName()</code> (or for the IP <code>getAddress().getHostAddress()</code>) and
	 * <code>getPort()</code>.
	 * 
	 * @return the address of the communicator's local socket.
	 */
	public InetSocketAddress localAddress() {
		return _myLocalAddress;
	}

	/**
	 * Adjusts the buffer size for messages. This is the maximum size a message can grow to.
	 * 
	 * @param theSize the new size in bytes.
	 * 
	 * @see #bufferSize()
	 */
	public void bufferSize(int theSize) {
		synchronized (_myBufferSyncObject) {
			if (_myBufferSize != theSize) {
				_myBufferSize = theSize;
				_myAllocateBuffer = true;
			}
		}
	}

	/**
	 * Queries the buffer size used for coding or decoding messages. 
	 * This is the maximum size a message can grow to.
	 * 
	 * @return the buffer size in bytes.
	 * 
	 * @see #bufferSize(int )
	 */
	public int bufferSize() {
		synchronized (_myBufferSyncObject) {
			return _myBufferSize;
		}
	}
	
	protected void checkBuffer() {
		synchronized (_myBufferSyncObject) {
			if (_myAllocateBuffer) {
				_myByteBuffer = ByteBuffer.allocateDirect(_myBufferSize);
				_myAllocateBuffer = false;
			}
		}
	}

	/**
	 * Disposes the resources associated with the net communicator. The object should not be used any more after calling
	 * this method.
	 */
	public abstract void dispose();
	
	/**
	 * Establishes connection for transports requiring connectivity (e.g. TCP). For transports that do not require
	 * connectivity (e.g. UDP), this ensures the communication channel is created and bound.
	 * <P>
	 * Having a connected channel without actually listening to incoming messages is usually not making sense. You can
	 * call {@linkplain CCNetIn#startListening()} without explicit prior call to {@linkplain #connect()}, because
	 * {@linkplain CCNetIn#startListening()} will establish the connection if necessary.
	 * <P>
	 * When a <B>UDP</B> channel is created, calling {@linkplain #connect()} will actually create and
	 * bind a {@linkplain DatagramChannel}. However, for {@linkplain CCTCPIn}, this may throw an {@linkplain IOException} if
	 * the receiver was already connected, therefore be sure to check {@linkplain #isConnected()} before.
	 * 
	 * @see #isConnected()
	 * @see CCNetIn#startListening()
	 */
	public abstract void connect();

	/**
	 * Queries the connection state of the channel.
	 * 
	 * @return <code>true</code> if the channel is connected, <code>false</code> otherwise. For transports that do
	 *         not use connectivity (e.g. UDP) this returns <code>false</code>, if the underlying
	 *         <code>DatagramChannel</code> has not yet been created.
	 * 
	 * @see #connect()
	 */
	public abstract boolean isConnected();
}
