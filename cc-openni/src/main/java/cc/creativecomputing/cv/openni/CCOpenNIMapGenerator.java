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

import org.openni.CodecID;
import org.openni.MapGenerator;
import org.openni.MapOutputMode;
import org.openni.StatusException;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * @author christianriekoff
 *
 */
abstract class CCOpenNIMapGenerator<MapGeneratorType extends MapGenerator> extends CCOpenNIGenerator<MapGeneratorType>{

	protected MapOutputMode _myMapOutputMode;
	
	private CodecID _myCaptureCodec;
	
	protected int _myWidth;
	protected int _myHeight;
	protected int _myFPS;
	
	protected CCTextureData _myTextureData;
	protected CCTexture2D _myTexture;
	
	/**
	 * @param theGenerator
	 */
	CCOpenNIMapGenerator(CCOpenNI theOpenNI, CodecID theCaptureCodec) {
		super(theOpenNI);
		_myCaptureCodec = theCaptureCodec;
		
		try {
			_myMapOutputMode = _myGenerator.getMapOutputMode();
//			_myMapOutputMode.setXRes(320);
//			_myMapOutputMode.setYRes(240);
//			_myGenerator.setMapOutputMode(_myMapOutputMode);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
		_myWidth = _myMapOutputMode.getXRes();
		_myHeight = _myMapOutputMode.getYRes();
		_myFPS = _myMapOutputMode.getFPS();
	}
	
	public CodecID captureCodec() {
		return _myCaptureCodec;
	}
	
	public CCTexture2D texture() {
		return _myTexture;
	}

	public int width() {
		return _myWidth;
	}
	
	public int height() {
		return _myHeight;
	}
	
	public int fps() {
		return _myFPS;
	}
}
