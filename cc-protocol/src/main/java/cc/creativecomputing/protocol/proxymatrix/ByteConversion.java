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

import java.nio.ByteBuffer;
public class ByteConversion {
	
	// convert 4 byte array to signed int
	public static int toInt(byte[]in){
		ByteBuffer bb = ByteBuffer.wrap(in);
		return bb.getInt();
	}
	
	// convert reversed 4 byte array to signed int
	public static int toIntRev(byte[]in){
		byte inR[] = new byte[4];
		for (int i=0; i<4; i++)
			inR[i] = in[3-i];
		ByteBuffer bb = ByteBuffer.wrap(inR);
		return bb.getInt();
	}
	
	// todo: dangerous workaround
	public static String toString(byte[]in){
		//ByteBuffer bb = ByteBuffer.wrap(in);
		String ret = new String(in,0,16);
		return ret;
	}
	
	public static void main(String[] args){
		byte test[] = {100,101,102,102};
		System.out.println("testing\n"+ByteConversion.toString(test));
	}
}
