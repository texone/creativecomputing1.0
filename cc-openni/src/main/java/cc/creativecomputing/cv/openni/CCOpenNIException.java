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
package cc.creativecomputing.cv.openni;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNIException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 643884736459566564L;

	public CCOpenNIException() {
		super();
	}

	public CCOpenNIException(String theArg0, Throwable theArg1) {
		super(theArg0, theArg1);
	}

	public CCOpenNIException(String theArg0) {
		super(theArg0);
	}

	public CCOpenNIException(Throwable theArg0) {
		super(theArg0);
	}

}
