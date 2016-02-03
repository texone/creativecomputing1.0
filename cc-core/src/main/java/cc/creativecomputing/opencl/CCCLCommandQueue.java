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

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.gl.CLGLObject;

public class CCCLCommandQueue {

	private CCOpenCL _myCL;
	private CLCommandQueue _myCommandQueue;
	
	public CCCLCommandQueue(CCOpenCL theCL){
		_myCL = theCL;
		_myCommandQueue = _myCL.createCommandQueue();
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkOffset, int theGlobalWorkSize, int theLocalWorkSize){
		_myCommandQueue.put1DRangeKernel(theKernel.clKernel(), theGlobalWorkOffset, theGlobalWorkSize, theLocalWorkSize);
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkOffset, int theGlobalWorkSize){
		put1DKernel(theKernel, theGlobalWorkOffset, theGlobalWorkSize, 0);
	}
	
	public void put1DKernel(CCCLKernel theKernel, int theGlobalWorkSize){
		put1DKernel(theKernel, 0, theGlobalWorkSize, 0);
	}

	public void putReadBuffer(CLBuffer<?> theBuffer, boolean theIsBlocking) {
		_myCommandQueue.putReadBuffer(theBuffer, theIsBlocking);
	}

	public void putWriteBuffer(CLBuffer<?> theBuffer, boolean theIsBlocking) {
		_myCommandQueue.putWriteBuffer(theBuffer, theIsBlocking);
	}

	public void putWriteImage(CLImage2d<?> theImage, boolean theIsBlocking) {
		_myCommandQueue.putWriteImage(theImage, theIsBlocking);
	}

	public void putAcquireGLObject(CLGLObject theObject) {
		_myCommandQueue.putAcquireGLObject(theObject);
	}

	public void putReleaseGLObject(CLGLObject theObject) {
		_myCommandQueue.putReleaseGLObject(theObject);
	}

	public void finish() {
		_myCommandQueue.finish();
	}
}
