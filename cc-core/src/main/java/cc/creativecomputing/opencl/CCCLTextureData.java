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

import java.nio.Buffer;

import com.jogamp.opencl.CLImage2d;

import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * @author christianriekoff
 *
 */
public class CCCLTextureData extends CCTextureData{

	private CLImage2d<Buffer> _myCLImage;
	private CCCLPixelFormat _myFormat;
	private CCCLPixelType _myType;
	
	CCCLTextureData(CLImage2d<Buffer> theImage2d, CCCLPixelFormat thePixelFormat, CCCLPixelType thePixelType){
		_myCLImage = theImage2d;
		_myFormat = thePixelFormat;
		_myType = thePixelType;
		
		_myWidth = theImage2d.width;
		_myHeight = theImage2d.height;
		_myBorder = 0;
		
		_myPixelInternalFormat = _myFormat.internalFormat();
		pixelFormat(_myFormat.format());
		_myPixelType = _myType.pixelType();
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = false;
		
		_myBuffer = new Buffer[] {theImage2d.getBuffer()};
		
		_myFlusher = null;
		_myPixelStorageModes.alignment(4);
		_myEstimatedMemorySize = estimatedMemorySize(_myBuffer[0]);
	}
	
	public CLImage2d<Buffer> clImage(){
		return _myCLImage;
	}
}
