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
/*
 * Created on Monday, December 13 2010 17:43
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
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLProgram.CompilerOptions;

/**
 * Computes the classical gamma correction for a given image. http://en.wikipedia.org/wiki/Gamma_correction
 * 
 * @author Michael Bien
 */
public class CCCLSimpleGammaCorrectionImage2d extends CCApp {

	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@Override
	public void setup() {
		// find a CL implementation
		CCOpenCL.printInfos();
		CCOpenCL myOpenCl = new CCOpenCL(g);
		CLContext myContext = myOpenCl.context();

		// load and compile program for the chosen device
		CLProgram program = myOpenCl.createProgram(CCCLSimpleGammaCorrectionImage2d.class, "gamma_image2d.cl");
		program.build(CompilerOptions.FAST_RELAXED_MATH);

		// load image
		CCTextureData myImage = CCTextureIO.newTextureData("demo/textures/lena.png");
		_myTexture1 = new CCTexture2D(myImage);

		// Create the memory object for the input- and output image
		CLImage2d<?> myInputImage = myOpenCl.createCLGLTexture(_myTexture1);
		CLImage2d<FloatBuffer> myOutputImage = myOpenCl.createCLImage(myImage.width(), myImage.height());

		// create kernel and set function parameters
		CLKernel myKernel = program.createCLKernel("gamma");

		// a few gamma corrected versions
		float gamma = 0.5f;
		float scaleFactor = (float) Math.pow(255, 1.0f - gamma);

		// setup kernel
		myKernel.setArg(0,myInputImage);
		myKernel.setArg(1,myOutputImage);
		myKernel.setArg(2,gamma);
		myKernel.setArg(3,scaleFactor);
		
		// create a command queue with benchmarking flag set
		CLCommandQueue queue = myContext.getDevices()[0].createCommandQueue(Mode.PROFILING_MODE);
		queue.putWriteImage(myInputImage, false); // upload image
		queue.put2DRangeKernel(myKernel, 0, 0, myImage.width(), myImage.height(), 0, 0); // execute program
		queue.putReadImage(myOutputImage, true);
		
		for(int i = 0; i < 512;i++) {
			CCLog.info(
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get() +"," +
				myOutputImage.getBuffer().get()
			);
		}
		myOutputImage.getBuffer().rewind();
		CCTextureData myOutput = myOpenCl.createTextureData(myOutputImage);
		_myTexture2 = new CCTexture2D(myOutput);

		myContext.release();
	}
	
	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture1, -512, -256);
		g.image(_myTexture2, 0, -256);
	}

	public static void main(String[] args) throws IOException {
		CCApplicationManager myManager = new CCApplicationManager(CCCLSimpleGammaCorrectionImage2d.class);
		myManager.settings().size(1024, 512);
		myManager.start();
	}
}
