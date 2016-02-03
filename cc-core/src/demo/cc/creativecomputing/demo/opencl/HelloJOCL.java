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
package cc.creativecomputing.demo.opencl;

import static com.jogamp.opencl.CLMemory.Mem.READ_ONLY;
import static com.jogamp.opencl.CLMemory.Mem.READ_WRITE;

import java.io.IOException;
import java.nio.FloatBuffer;

import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;

/**
 * Hello Java OpenCL example. Adds all elements of buffer A to buffer B and stores the result in buffer C.<br/>
 * Sample was inspired by the Nvidia VectorAdd example written in C/C++ which is bundled in the Nvidia OpenCL SDK.
 * 
 * @author Michael Bien
 */
public class HelloJOCL {

	void run() throws IOException {
		// set up (uses default CLPlatform and creates context for all devices)
		CCOpenCL myOpenCL = new CCOpenCL();
		
		// load sources, create and build program
		CCCLProgram myProgram = new CCCLProgram(myOpenCL);
		myProgram.appendSource(HelloJOCL.class,"VectorAdd.cl");
		myProgram.build();
		
		CCCLKernel myKernel = myProgram.createKernel("hello_kernel");

		// create command queue on device.
		CLCommandQueue myCommandQueue = myOpenCL.createCommandQueue();
		
		int myArraySize = 100;

		// A, B are input buffers, C is for the result
		CLBuffer<FloatBuffer> myA = myOpenCL.createCLFloatBuffer(myArraySize, READ_ONLY);
		CLBuffer<FloatBuffer> myB = myOpenCL.createCLFloatBuffer(myArraySize, READ_ONLY);
		CLBuffer<FloatBuffer> myResult = myOpenCL.createCLFloatBuffer(myArraySize, READ_WRITE);

		

		// get a reference to the kernel function with the name 'VectorAdd'
		// and map the buffers to its input parameters.
		myKernel.argument(0, myResult);
		myKernel.argument(1, myA);
		myKernel.argument(2, myB);
		
		for (int i = 0; i < myArraySize; i++) {
			myA.getBuffer().put(i, i);
			myB.getBuffer().put(i, i * 2);
		}

		myCommandQueue.putWriteBuffer(myA, false);
		myCommandQueue.putWriteBuffer(myB, false);
		myCommandQueue.put1DRangeKernel(myKernel.clKernel(), 0, myArraySize, 1);
		myCommandQueue.putReadBuffer(myResult, true);
		
		
		


		// Output the result buffer 
        for (int i = 0; i < myArraySize; i++) {
        	CCLog.info(myResult.getBuffer().get(i));
        }
        myOpenCL.context().release();

	}

	public static void main(String[] args) throws IOException {
		new HelloJOCL().run();
	}

}
