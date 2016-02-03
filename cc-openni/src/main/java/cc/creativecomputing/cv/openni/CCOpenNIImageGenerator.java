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

import java.nio.ByteBuffer;

import org.openni.CodecID;
import org.openni.GeneralException;
import org.openni.ImageGenerator;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIImageGenerator extends CCOpenNIMapGenerator<ImageGenerator>{
	
	@Override
	public ImageGenerator create(CCOpenNI theOpenNI) {
		try {
			if(theOpenNI.deviceQuery() == null)return ImageGenerator.create(theOpenNI.context());
			return ImageGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	CCOpenNIImageGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI, CodecID.Jpeg);
		
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelFormat.RGB, CCPixelType.UNSIGNED_BYTE);
		if(CCGraphics.currentGL() != null){
			_myTexture = new CCTexture2D(_myTextureData);
			_myTexture.mustFlipVertically(true);
		}
	}
	
	@Override
	public void update(float theDeltaTime) {
		ByteBuffer myData = _myGenerator.getMetaData().getData().createByteBuffer();
		_myTextureData.buffer(myData);
		if(_myTexture != null)_myTexture.updateData(_myTextureData);
	}
}
