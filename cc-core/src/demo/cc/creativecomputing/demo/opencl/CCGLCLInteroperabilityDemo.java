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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.opencl.CCOpenCL;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.gl.CLGLBuffer;

/**
 * JOCL - JOGL interoperability example.
 * 
 * @author Michael Bien
 */
public class CCGLCLInteroperabilityDemo extends CCApp {

	private final int MESH_SIZE = 1000;

	private CCVBOMesh _myMesh;
	private CCArcball _myArcball;
	private CCOpenCL _myOpenCL;

	private CLKernel _myKernel;
	private CLCommandQueue _myCommandQueue;
	private CLGLBuffer<?> _myBuffer;

	private float step = 0;

	public void setup() {
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, MESH_SIZE * MESH_SIZE, 4);
		_myArcball = new CCArcball(this);
		
		// create OpenCL context before creating any OpenGL objects you want to share with OpenCL
		_myOpenCL = new CCOpenCL("nvidia",0,g);

		_myCommandQueue = _myOpenCL.createCommandQueue();
		_myBuffer = _myOpenCL.createFromGLBuffer(_myMesh.vertexBuffer(),CLGLBuffer.Mem.WRITE_ONLY);

		CLProgram myProgram = _myOpenCL.createProgram(CCGLCLInteroperabilityDemo.class, "JoglInterop.cl");
		myProgram.build();
		
		_myKernel = myProgram.createCLKernel("sineWave");
		_myKernel.setArg(0, _myBuffer);
		_myKernel.setArg(1, MESH_SIZE);
		_myKernel.rewind();

	}
	
	@Override
	public void update(float theDeltaTime) {
		_myKernel.setArg(2, step += theDeltaTime * 0.1f);
		_myKernel.setArg(3, (float)width);
		_myKernel.setArg(4, (float)height);

		_myCommandQueue.putAcquireGLObject(_myBuffer);
		_myCommandQueue.put1DRangeKernel(_myKernel, 0, MESH_SIZE * MESH_SIZE, 1);
		_myCommandQueue.putReleaseGLObject(_myBuffer);
		_myCommandQueue.finish();
	}

	@Override
	public void draw() {
		g.clear();
		g.color(1f, 0.25f);
		_myArcball.draw(g);
		_myMesh.draw(g);
		g.gl.glFlush();

		g.text(frameRate, 0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGLCLInteroperabilityDemo.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}
