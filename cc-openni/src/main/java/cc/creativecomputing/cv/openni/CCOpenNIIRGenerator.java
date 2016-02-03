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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.openni.CodecID;
import org.openni.GeneralException;
import org.openni.IRGenerator;
import org.openni.MapOutputMode;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIIRGenerator extends CCOpenNIMapGenerator<IRGenerator>{
	
	@Override
	public IRGenerator create(CCOpenNI theOpenNI) {
		try {
			MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   // xRes, yRes, FPS
			IRGenerator myGenerator;
			if(theOpenNI.deviceQuery() == null)myGenerator = IRGenerator.create(theOpenNI.context());
			else myGenerator = IRGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
			myGenerator.setMapOutputMode(mapMode); 
			return myGenerator;
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private ShortBuffer _myRawData;
	
	CCOpenNIIRGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI, CodecID.Uncompressed);
	      
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		_myTexture.mustFlipVertically(true);
	}
	
	private boolean _myNeedTextureUpdate = true;
	
	public void update() {
		_myNeedTextureUpdate = true;
		try {
			_myRawData = _myGenerator.getIRMap().createShortBuffer();
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	@Override
	public CCTexture2D texture() {
		if(_myNeedTextureUpdate) {
			_myNeedTextureUpdate = false;

			_myRawData.rewind();
			int minIR = _myRawData.get();
			int maxIR = minIR;

			while (_myRawData.remaining() > 0) {
				int irVal = _myRawData.get();
				if (irVal > maxIR)
					maxIR = irVal;
				if (irVal < minIR)
					minIR = irVal;
			}
		      
			FloatBuffer myFloatBuffer = (FloatBuffer)_myTextureData.buffer();
			myFloatBuffer.rewind();
			_myRawData.rewind();
			
			while(_myRawData.hasRemaining()) {
				int myValue = _myRawData.get();
				myFloatBuffer.put((myValue - minIR)/(float)maxIR);
			}
			myFloatBuffer.rewind();
			_myTexture.updateData(_myTextureData);
		}
		return _myTexture;
	}
}
