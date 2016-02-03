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
package cc.creativecomputing.io;


public class CCConversion{
	//////////////////////////////////////////////////////////////

	// CASTING FUNCTIONS, INSERTED BY PREPROC

	static final public int extractByte(int theInt, final int theByteIndex){
		final int theOffset = theByteIndex * 8;
		return ((theInt >> theOffset) & 0xff);
	}

	static final public byte parseByte(char what){
		return (byte) what;
	}

	static final public byte parseByte(int what){
		return (byte) what;
	}

	/*
	 static final public byte parseByte(float what) {  // nonsensical
	 return (byte) what;
	 }

	 static final public byte[] parseByte(String what) {  // note: array[]
	 return what.getBytes();
	 }
	 */

	//
	/*
	 static final public byte[] parseByte(boolean what[]) {
	 byte outgoing[] = new byte[what.length];
	 for (int i = 0; i < what.length; i++) {
	 outgoing[i] = what[i] ? (byte)1 : 0;
	 }
	 return outgoing;
	 }
	 */

	static final public byte[] parseByte(char what[]){
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++){
			outgoing[i] = (byte) what[i];
		}
		return outgoing;
	}

	static final public byte[] parseByte(int what[]){
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++){
			outgoing[i] = (byte) what[i];
		}
		return outgoing;
	}

	

	static final public char parseChar(byte what){
		return (char) (what & 0xff);
	}

	static final public char parseChar(int what){
		return (char) what;
	}

	

	static final public char[] parseChar(int what[]){
		char outgoing[] = new char[what.length];
		for (int i = 0; i < what.length; i++){
			outgoing[i] = (char) what[i];
		}
		return outgoing;
	}

	static final public char[] parseChar(byte what[]){
		char outgoing[] = new char[what.length];
		for (int i = 0; i < what.length; i++){
			outgoing[i] = (char) (what[i] & 0xff);
		}
		return outgoing;
	}

	

	/**
	 * Note that parseInt() will un-sign a signed byte value.
	 */
	static final public int parseInt(byte what){
		return what & 0xff;
	}

	/**
	 * Note that parseInt('5') is unlike String in the sense that it
	 * won't return 5, but the ascii value. This is because ((int) someChar)
	 * returns the ascii value, and parseInt() is just longhand for the cast.
	 */
	static final public int parseInt(char what){
		return what;
	}

	/**
	 * Same as floor(), or an (int) cast.
	 */
	static final public int parseInt(float what){
		return (int) what;
	}

	/**
	 * Parse a String into an int value. Returns 0 if the value is bad.
	 */
	static final public int parseInt(String what){
		return parseInt(what, 0);
	}

	/**
	 * Parse a String to an int, and provide an alternate value that
	 * should be used when the number is invalid.
	 */
	static final public int parseInt(String what, int otherwise){
		try{
			int offset = what.indexOf('.');
			if (offset == -1){
				return Integer.parseInt(what);
			}else{
				return Integer.parseInt(what.substring(0, offset));
			}
		}catch (NumberFormatException e){
		}
		return otherwise;
	}

	static final public int[] parseInt(byte what[]){ // note this unsigns
		int list[] = new int[what.length];
		for (int i = 0; i < what.length; i++){
			list[i] = (what[i] & 0xff);
		}
		return list;
	}

	static final public int[] parseInt(char what[]){
		int list[] = new int[what.length];
		for (int i = 0; i < what.length; i++){
			list[i] = what[i];
		}
		return list;
	}

	static public int[] parseInt(float what[]){
		int inties[] = new int[what.length];
		for (int i = 0; i < what.length; i++){
			inties[i] = (int) what[i];
		}
		return inties;
	}

	/**
	 * Make an array of int elements from an array of String objects.
	 * If the String can't be parsed as a number, it will be set to zero.
	 *
	 * String s[] = { "1", "300", "44" };
	 * int numbers[] = parseInt(s);
	 *
	 * numbers will contain { 1, 300, 44 }
	 */
	static public int[] parseInt(String what[]){
		return parseInt(what, 0);
	}

	/**
	 * Make an array of int elements from an array of String objects.
	 * If the String can't be parsed as a number, its entry in the
	 * array will be set to the value of the "missing" parameter.
	 *
	 * String s[] = { "1", "300", "apple", "44" };
	 * int numbers[] = parseInt(s, 9999);
	 *
	 * numbers will contain { 1, 300, 9999, 44 }
	 */
	static public int[] parseInt(String what[], int missing){
		int output[] = new int[what.length];
		for (int i = 0; i < what.length; i++){
			try{
				output[i] = Integer.parseInt(what[i]);
			}catch (NumberFormatException e){
				output[i] = missing;
			}
		}
		return output;
	}

	

	static final public float parseFloat(int what){
		return (float) what;
	}

	static final public float parseFloat(String what){
		return parseFloat(what, Float.NaN);
	}

	static final public float parseFloat(String what, float otherwise){
		try{
			return new Float(what).floatValue();
		}catch (NumberFormatException e){
		}

		return otherwise;
	}

	

	static final public float[] parseFloat(int what[]){
		float floaties[] = new float[what.length];
		for (int i = 0; i < what.length; i++){
			floaties[i] = what[i];
		}
		return floaties;
	}

	static final public float[] parseFloat(String what[]){
		return parseFloat(what, 0);
	}

	static final public float[] parseFloat(String what[], float missing){
		float output[] = new float[what.length];
		for (int i = 0; i < what.length; i++){
			try{
				output[i] = new Float(what[i]).floatValue();
			}catch (NumberFormatException e){
				output[i] = missing;
			}
		}
		return output;
	}

	//

	static final public String str(boolean x){
		return String.valueOf(x);
	}

	static final public String str(byte x){
		return String.valueOf(x);
	}

	static final public String str(char x){
		return String.valueOf(x);
	}

	static final public String str(short x){
		return String.valueOf(x);
	}

	static final public String str(int x){
		return String.valueOf(x);
	}

	static final public String str(float x){
		return String.valueOf(x);
	}

	static final public String str(long x){
		return String.valueOf(x);
	}

	static final public String str(double x){
		return String.valueOf(x);
	}

	//

	static final public String[] str(boolean x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(byte x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(char x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(short x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(int x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(float x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(long x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	static final public String[] str(double x[]){
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++)
			s[i] = String.valueOf(x);
		return s;
	}

	

	//////////////////////////////////////////////////////////////

	// HEX/BINARY CONVERSION

	static final public String hex(byte what){
		return hex(what, 2);
	}

	static final public String hex(char what){
		return hex(what, 4);
	}

	static final public String hex(int what){
		return hex(what, 8);
	}

	static final public String hex(int what, int digits){
		String stuff = Integer.toHexString(what).toUpperCase();

		int length = stuff.length();
		if (length > digits){
			return stuff.substring(length - digits);

		}else if (length < digits){
			return "00000000".substring(8 - (digits - length)) + stuff;
		}
		return stuff;
	}

	static final public int unhex(String what){
		// has to parse as a Long so that it'll work for numbers bigger than 2^31
		return (int) (Long.parseLong(what, 16));
	}

	//

	/**
	 * Returns a String that contains the binary value of a byte.
	 * The returned value will always have 8 digits.
	 */
	static final public String binary(byte what){
		return binary(what, 8);
	}

	/**
	 * Returns a String that contains the binary value of a char.
	 * The returned value will always have 16 digits because chars
	 * are two bytes long.
	 */
	static final public String binary(char what){
		return binary(what, 16);
	}

	/**
	 * Returns a String that contains the binary value of an int.
	 * The length depends on the size of the number itself.
	 * An int can be up to 32 binary digits, but that seems like
	 * overkill for almost any situation, so this function just
	 * auto-size. If you want a specific number of digits (like all 32)
	 * use binary(int what, int digits) to specify how many digits.
	 */
	static final public String binary(int what){
		return Integer.toBinaryString(what);
		//return binary(what, 32);
	}

	/**
	 * Returns a String that contains the binary value of an int.
	 * The digits parameter determines how many digits will be used.
	 */
	static final public String binary(int what, int digits){
		String stuff = Integer.toBinaryString(what);

		int length = stuff.length();
		if (length > digits){
			return stuff.substring(length - digits);

		}else if (length < digits){
			int offset = 32 - (digits - length);
			return "00000000000000000000000000000000".substring(offset) + stuff;
		}
		return stuff;
	}

	/**
	 * Converts a String representation of a binary number to its equivalent integer value. For example,
	 * unbinary("00001000") will return 8.
	 * 
	 * ( end auto-generated )
	 * 
	 * @param what String to convert to an integer
	 * @see #hex(int,int)
	 * @see #binary(byte)
	 */
	static final public int unbinary(String what) {
		return Integer.parseInt(what, 2);
	}
}
