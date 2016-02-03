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
package cc.creativecomputing.opencl;

/**
 * @author christianriekoff
 *
 */
public class CCOpenCLUtil {
	
	/**
	 * Round the given global size value up to a multiple of the 
	 * given group size value. 
	 * @param theGroupSize maximum possible size of a work group
	 * @param theGlobalSize global number of work items
	 * @return the nearest multiple of the localWorkSize
	 */
	public static int roundUp(int theGroupSize, int theGlobalSize) {
		int r = theGlobalSize % theGroupSize;
		if (r == 0) {
			return theGlobalSize;
		} else {
			return theGlobalSize + theGroupSize - r;
		}
	}
}
