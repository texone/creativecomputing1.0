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
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.net.codec.CCNetPacketCodec;

/**
 * A class that sends messages to a target server. Each instance takes a network channel,
 * either explicitly specified by a {@linkplain DatagramChannel} (for UDP) or {@linkplain SocketChannel} (for TCP), or
 * internally opened when a protocol type is specified. Messages are send by invoking one of the {@linkplain #send(Object)}
 * methods.
 * 
 * @author christian riekoff
 * 
 * @see CCNetClient
 * @see CCNetServer
 * @see CCNetIn
 * 
 * @synchronization sending messages is thread safe
 */
public abstract class CCNetOut<ChannelType extends SelectableChannel, MessageType> extends CCNetChannel<ChannelType, MessageType> {

	protected SocketAddress _myTarget = null;

	protected CCNetOut(
		CCNetPacketCodec<MessageType> theCodec, 
		String theLocalAddress, int theLocalPort,
		String theTargetAddress, int theTargetPort
	) {
		super(theCodec,  new InetSocketAddress(theLocalAddress, theLocalPort));
		_myTarget = new InetSocketAddress(theTargetAddress, theTargetPort);
	}

	protected CCNetOut(
		CCNetPacketCodec<MessageType> theCodec, 
		InetSocketAddress theLocalAddress
	) {
		super(theCodec,  theLocalAddress);
	}
	
	protected CCNetOut(CCNetPacketCodec<MessageType> theCodec, String theTargetAddress, int theTargetPort) {
		this(theCodec, "0.0.0.0",0,theTargetAddress, theTargetPort);
	}

	/**
	 * Specifies the outputs target address, that is the address of the remote side to talk to. You should call
	 * this method only once and you must call it before starting to send messages using the shortcut call
	 * {@linkplain #send(Object)}.
	 * 
	 * @param theHost
	 * @param thePort
	 */
	public void target(String theHost, int thePort) {
		_myTarget = new InetSocketAddress(theHost, thePort);
	}
	
	void target(SocketAddress theTarget) {
		_myTarget = theTarget;
	}

	/**
	 * Sends a message to the given network address, using the current codec.
	 * 
	 * @param theMessage the packet to send
	 * @param theTarget the target address to send the packet to
	 */
	public abstract void send(MessageType theMessage, SocketAddress theTarget);

	/**
	 * Sends a message to the default network address, using the current codec. The default
	 * address is the one specified using the {@linkplain #target(String, int)} method. Therefore this will throw a
	 * {@linkplain NullPointerException} if no default address was specified.
	 * 
	 * @param theMessage the packet to send
	 * 
	 * @see #target(String, int)
	 */
	public void send(MessageType theMessage) {
		send(theMessage, _myTarget);
	}

	@Override
	public void dispose() {
		_myByteBuffer = null;
		if (_myChannel != null) {
			try {
				_myChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			_myChannel = null;
		}
	}

	protected ChannelType channel() {
		synchronized (_myBufferSyncObject) {
			return _myChannel;
		}
	}
}
