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
package cc.creativecomputing.graphics.shader.imaging;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderException;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

public class CCGPUConvolutionShader extends CCCGShader{
	private CGparameter _myKernelValueParameter;
	private CGparameter _myOffsetParameter;
	
	private float _myPixelWidth;
	private float _myPixelHeight;
	
	protected int _myKernelWidth;
	protected int _myKernelHeight;
	protected int _myKernelSize;
	
	public CCGPUConvolutionShader() {
		super(null, CCIOUtil.classPath(CCGPUConvolutionShader.class,"convolution.fp"));
	}
	
	public CCGPUConvolutionShader(final CCGraphics theGraphics, final String theShader) {
		super(null, theShader);
	}
	
	public CCGPUConvolutionShader(final int theKernelWidth, final int theKernelHeight) {
		super(null,CCIOUtil.classPath(CCGPUConvolutionShader.class,"convolution.fp"));
		
		_myKernelWidth = theKernelWidth;
		_myKernelHeight = theKernelHeight;
		_myKernelSize = _myKernelWidth * _myKernelHeight;
	}
	
	public CCGPUConvolutionShader(final List<Float> theKernel, final int theKernelWidth, final int theKernelHeight) {
		this(theKernelWidth,theKernelHeight);
		initKernel(theKernel);
	}
	
	protected void setKernel(final List<Float> theKernel, final int theKernelWidth, final int theKernelHeight) {
		_myKernelWidth = theKernelWidth;
		_myKernelHeight = theKernelHeight;
		_myKernelSize = _myKernelWidth * _myKernelHeight;
		
		initKernel(theKernel);
	}
	
	protected void initKernel(final List<Float> theKernel) {
		if(_myKernelSize != theKernel.size()) {
			throw new CCShaderException("The given Kernel of the size "+theKernel.size()+" does not match the given width and height.");
		}
		_myKernelValueParameter = fragmentParameter("kernelValue");
		_myOffsetParameter = fragmentParameter("offset");
		CgGL.cgSetArraySize(_myKernelValueParameter, theKernel.size());
		CgGL.cgSetArraySize(_myOffsetParameter, theKernel.size());
		
		checkError("setup convolution parameters");
		
		load();

		checkError("apply convolution kernel");
		parameter1(_myKernelValueParameter, theKernel);
	}
	
	protected void updateKernel(final List<Float> theKernel){
		if(_myKernelSize != theKernel.size()) {
			throw new CCShaderException("The given Kernel of the size "+theKernel.size()+" does not match the given width and height.");
		}
		parameter1(_myKernelValueParameter, theKernel);
	}
	
	public void texture(final CCTexture2D theTexture) {
		_myPixelWidth = 1f/theTexture.width();
		_myPixelHeight = 1f/theTexture.height();
	
		updateOffsets();
	}
	
	public void flipKernel() {
		int temp = _myKernelWidth;
		_myKernelWidth = _myKernelHeight;
		_myKernelHeight = temp;
		
		updateOffsets();
	}
	
	public void updateOffsets() {
		List<CCVector2f> myOffsets = new ArrayList<CCVector2f>();
		
		int xStart = -_myKernelWidth / 2;
		int yStart = -_myKernelHeight / 2;
		
		for(int x = 0; x < _myKernelWidth;x++) {
			for(int y = 0; y < _myKernelHeight; y++) {
				myOffsets.add(new CCVector2f((xStart + x) * _myPixelWidth,(yStart + y) * _myPixelHeight));
			}
		}
		
		parameter2(_myOffsetParameter, myOffsets);
		checkError("apply texture offsets");
	}
}
