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
package cc.creativecomputing.graphics.texture.filter;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCConversion;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCBuffer;


/**
 * <p>Use this filter to detect the difference of pixels between two frames in a movie.
 * The difference of a pixel is defined by the difference of its rgb values. This
 * filter is useful to detect the amount of motion in a movie and to detect cuts inside 
 * the movie.</p>
 * @author texone
 *
 */
public class CCPixelDifference extends CCTextureFilter{
	
	public static interface CCPixelDifferenceCutListener {
		public void onCut();
	}

	private long _myAbsoluteDifference = 0;
	private float _myRelativeDifference = 0;
	
	CCBuffer<Float> _myDifferenceBuffer = CCBuffer.floatBuffer(10);
	int counter = 0;
	
	int[] theOldReds;
	int[] theOldGreens;
	int[] theOldBluess;
	
	private int _myCheckPixel;
	
	private final List<CCPixelDifferenceCutListener> _myCutListeners = new ArrayList<CCPixelDifferenceCutListener>();
	
	float _myMax = 0;
	
	public CCPixelDifference(final int theCheckPixel){
		_myCheckPixel = theCheckPixel;
	}
	
	public CCPixelDifference(){
		this(1);
	}
	
	public void addCutListener(final CCPixelDifferenceCutListener theListener){
		_myCutListeners.add(theListener);
	}
	
	@Override
	public void apply(final byte[] thePixels){
		
		int myNumberOfPixels = thePixels.length / _myCheckPixel;
		if(theOldReds == null || theOldReds.length != myNumberOfPixels){
			theOldReds = new int[myNumberOfPixels];
			theOldGreens = new int[myNumberOfPixels];
			theOldBluess = new int[myNumberOfPixels];
		}
		_myAbsoluteDifference = 0;
		
		int myRed, myGreen, myBlue;
		int myIndex = 0;
		for(int i = 0; i < thePixels.length;i+=_myCheckPixel){
			myRed = CCConversion.extractByte(thePixels[i], 2);
			myGreen = CCConversion.extractByte(thePixels[i], 1);
			myBlue = CCConversion.extractByte(thePixels[i], 0);
//		
			_myAbsoluteDifference += CCMath.abs(myRed - theOldReds[myIndex]);
			_myAbsoluteDifference += CCMath.abs(myGreen - theOldGreens[myIndex]);
			_myAbsoluteDifference += CCMath.abs(myBlue - theOldBluess[myIndex]);
			
			theOldReds[myIndex] = myRed;
			theOldGreens[myIndex] = myGreen;
			theOldBluess[myIndex] = myBlue;
			myIndex++;
		}
		_myRelativeDifference = _myAbsoluteDifference/(float)myNumberOfPixels/3/255;
		_myDifferenceBuffer.update(_myRelativeDifference);
		
		if(_myRelativeDifference > _myMax){
			_myMax = _myRelativeDifference;
		}else{
			_myMax *= 0.9f;
		}
		
		if(_myRelativeDifference > _myDifferenceBuffer.value() * 3){
			for(CCPixelDifferenceCutListener myListener:_myCutListeners){
				myListener.onCut();
			}
		}
	};
	
	public long absoluteDifference(){
		return _myAbsoluteDifference;
	}
	
	public float relativeDifference(){
		return _myRelativeDifference;
	}
	
	public float bufferedDifference(){
		return _myDifferenceBuffer.value();
	}
	
	public float maxDifference(){
		return _myMax;
	}
}
