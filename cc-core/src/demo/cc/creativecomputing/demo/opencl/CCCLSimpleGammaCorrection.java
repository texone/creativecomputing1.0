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

import java.io.IOException;
import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.opencl.CCOpenCL;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLProgram.CompilerOptions;

/**
 * Computes the classical gamma correction for a given image. http://en.wikipedia.org/wiki/Gamma_correction
 * 
 * @author Michael Bien
 */
public class CCCLSimpleGammaCorrection extends CCApp{
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	private CCTexture2D _myTexture3;
	
	private void gammaCorrection(float gamma, CLCommandQueue queue, CLKernel kernel, CLBuffer<FloatBuffer> buffer, int localWorkSize, int globalWorkSize) {

		float scaleFactor = (float) Math.pow(255, 1.0f - gamma);

		// setup kernel
		kernel.putArg(buffer).putArg(gamma).putArg(scaleFactor).putArg(buffer.getNIOSize()).rewind();

		// CLEventList list = new CLEventList(1);

		queue.putWriteBuffer(buffer, false); // upload image
		queue.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize/* , list */); // execute program
		queue.putReadBuffer(buffer, true); // read results back (blocking read)

	}
	
	@Override
	public void setup() {
		// find a CL implementation
		CCOpenCL myOpenCl = new CCOpenCL();
		CLContext myContext = myOpenCl.context();

		// load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");

		// allocate a OpenCL buffer using the direct fb as working copy
		CLBuffer<FloatBuffer> buffer = myOpenCl.createCLFloatBuffer(myImage, CLBuffer.Mem.READ_WRITE);
		
		// create a command queue with benchmarking flag set
		CLCommandQueue queue = myContext.getDevices()[0].createCommandQueue(Mode.PROFILING_MODE);

		// Local work size dimensions
//		int localWorkSize = queue.getDevice().getMaxWorkGroupSize();
		
		// rounded up to the nearest multiple of the localWorkSize
//		int globalWorkSize = CCOpenCLUtil.roundUp(localWorkSize, buffer.getBuffer().capacity()); 
																
		
		// load and compile program for the chosen device
		CLProgram program = myOpenCl.createProgram(CCCLSimpleGammaCorrectionImage2d.class, "Gamma.cl");
		program.build(CompilerOptions.FAST_RELAXED_MATH);
		
		// create kernel and set function parameters
		CLKernel kernel = program.createCLKernel("gamma");

		// a few gamma corrected versions
		gammaCorrection(0.5f, queue, kernel, buffer, 1, 1);
		_myTexture1 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		gammaCorrection(1.5f, queue, kernel, buffer, 1, 1);
		_myTexture2 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		gammaCorrection(2.0f, queue, kernel, buffer, 1, 1);
		_myTexture3 = new CCTexture2D(myOpenCl.createTextureData(buffer, myImage.width(), myImage.height()));

		myContext.release();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#draw()
	 */
	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture1, -width/2, -height/2);
		g.image(_myTexture2, -width/2 + 512, -height/2);
		g.image(_myTexture3, -width/2 + 1024, -height/2);
	}

	public static void main(String[] args) throws IOException {
		CCApplicationManager myManager = new CCApplicationManager(CCCLSimpleGammaCorrection.class);
		myManager.settings().size(512 * 3, 512);
		myManager.start();
	}

}
