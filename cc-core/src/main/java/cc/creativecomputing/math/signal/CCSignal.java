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
package cc.creativecomputing.math.signal;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.d.CCVector3d;

/**
 * This is the base signal class that handles the basic setup, like scale and offset.
 * Here you can also set how fractal values should be calculated. Values are always in the 
 * range 0 to 1.
 * @author christianriekoff
 *
 */
public abstract class CCSignal {
	protected float _myScale = 1;
        
	protected float _myOffsetX = 0;
	protected float _myOffsetY = 0;
	protected float _myOffsetZ = 0;
        
	protected float _myOctaves = 1;
	protected float _myGain = 0.5f;
	protected float _myLacunarity = 2;
        
	/**
	 * Override this method to define how the 3d is calculated
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return the calculated value
	 */
	public abstract float[] signalImpl(final float theX, final float theY, final float theZ);
        
	public float[] signalImpl(final float theX, final float theY) {
		return signalImpl(theX,theY,0);
	}
        
	public float[] signalImpl(final float theX) {
		return signalImpl(theX,0);
	}
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @return multiple values
	 */
	public final float[] values(final float theX){
		float myScale = _myScale;
		float myFallOff = _myGain;
		
		int myOctaves = CCMath.floor(_myOctaves);
		float[] myResult = null;
		float myAmp = 0;
                
		for(int i = 0; i < myOctaves;i++){
			float[] myValues = signalImpl(theX * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		float myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			float[] myValues = signalImpl(theX * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		for(int j = 0; j < myResult.length;j++) {
			myResult[j] /= myAmp;
		}
		return myResult;
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @return the value for the given coordinates.
	 */
	public float value(final float theX) {
		return values(theX)[0];
	}
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @return multiple values
	 */
	public final float[] values(final float theX, final float theY){
		float myScale = _myScale;
		float myFallOff = _myGain;
                
		int myOctaves = CCMath.floor(_myOctaves);
		float[] myResult = null;
		float myAmp = 0;
                
		for(int i = 0; i < myOctaves;i++){
			float[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		float myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			float[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		for(int j = 0; j < myResult.length;j++) {
			myResult[j] /= myAmp;
		}
		return myResult;
    }
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theVector coordinates for the noise
	 * @return multiple values
	 */
	public float[] values(final CCVector2f theVector) {
		return values(theVector.x, theVector.y);
	}
        
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @return the value for the given coordinates.
	 */
	public float value(final float theX, final float theY) {
		return values(theX, theY)[0];
    }
        
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theVector coordinates for the noise
	 * @return the value for the given coordinates.
	 */
	public float value(final CCVector2f theVector) {
		return values(theVector)[0];
    }
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @param theZ z coord for the noise
	 * @return multiple values
	 */
	public final float[] values(final float theX, final float theY, final float theZ) {
		float myScale = _myScale;
		float myFallOff = _myGain;
                
		int myOctaves = CCMath.floor(_myOctaves);
		float[] myResult = null;
		float myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			float[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		float myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			float[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
			if(myResult == null)myResult = new float[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		for(int j = 0; j < myResult.length;j++) {
			myResult[j] /= myAmp;
		}
		return myResult;
   }
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theVector coordinates for the noise
	 * @return multiple values
	 */
	public float[] values(final CCVector3f theVector) {
		return values(theVector.x, theVector.y, theVector.z);
    }
        
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @param theZ z coord for the noise
	 * @return the value for the given coordinates.
	 */
	public float value(final float theX, final float theY, final float theZ) {
		return values(theX, theY, theZ)[0];
    }
        
    /**
     * Returns the value for the given coordinates. By default only one band is calculated.
     * @param theVector coordinates for the noise
     * @return the value for the given coordinates.
     */
	public float value(final CCVector3f theVector) {
		return values(theVector)[0];
    }
        
	/**
	 * Override this method to define how the 3d is calculated
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return the calculated value
	 */
	public double[] signalImpl(final double theX, final double theY, final double theZ) {
		float[] myValues = signalImpl((float)theX, (float)theY, (float)theZ);
		double[] myResult = new double[myValues.length];
		for(int i = 0; i < myResult.length;i++) {
			myResult[i] = myValues[i];
		}
		return myResult;
    }
        
	public double[] signalImpl(final double theX, final double theY) {
		return signalImpl(theX,theY,0);
    }
        
	public double[] noiseImpl(final double theX) {
		return signalImpl(theX,0);
    }
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @return multiple values
	 */
	public final double[] values(final double theX){
		float myScale = _myScale;
		float myFallOff = _myGain;
		
		int myOctaves = CCMath.floor(_myOctaves);
		double[] myResult = null;
		double myAmp = 0;
                
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = noiseImpl(theX * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		float myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			double[] myValues = noiseImpl(theX * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		for(int j = 0; j < myResult.length;j++) {
			myResult[j] /= myAmp;
		}
		return myResult;
    }
        
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final double theX) {
		return values(theX)[0];
    }
        
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @return multiple values
	 */
	public final double[] values(final double theX, final double theY){
		double myScale = _myScale;
		double myFallOff = _myGain;
                
		int myOctaves = CCMath.floor(_myOctaves);
		double[] myResult = null;
		double myAmp = 0;
                
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		float myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			double[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		for(int j = 0; j < myResult.length;j++) {
			myResult[j] /= myAmp;
		}
		return myResult;
    }
        
        /**
         * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
         * multiple output values like in case of the worley noise
         * @param theVector coordinates for the noise
         * @return multiple values
         */
        public double[] values(final CCVector2d theVector) {
                return values(theVector.x, theVector.y);
        }
        
        /**
         * Returns the value for the given coordinates. By default only one band is calculated.
         * @param theX x coord for the noise
         * @param theY y coord for the noise
         * @return the value for the given coordinates.
         */
        public double value(final double theX, final double theY) {
                return values(theX, theY)[0];
        }
        
        /**
         * Returns the value for the given coordinates. By default only one band is calculated.
         * @param theVector coordinates for the noise
         * @return the value for the given coordinates.
         */
        public double value(final CCVector2d theVector) {
                return values(theVector)[0];
        }
        
        /**
         * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
         * multiple output values like in case of the worley noise
         * @param theX x coord for the noise
         * @param theY y coord for the noise
         * @param theZ z coord for the noise
         * @return multiple values
         */
        public final double[] values(final double theX, final double theY, final double theZ) {
                double myScale = _myScale;
                double myFallOff = _myGain;
                
                int myOctaves = CCMath.floor(_myOctaves);
                double[] myResult = null;
                double myAmp = 0;
                
                for(int i = 0; i < myOctaves;i++){
                        double[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
                        if(myResult == null)myResult = new double[myValues.length];
                        for(int j = 0; j < myResult.length;j++) {
                                myResult[j] += myValues[j] * myFallOff;
                        }
                        myAmp += myFallOff;
                        myFallOff *= _myGain;
                        myScale *= _myLacunarity;
                }
                float myBlend = _myOctaves - myOctaves;
                if(myBlend > 0) {
                        double[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
                        if(myResult == null)myResult = new double[myValues.length];
                        for(int j = 0; j < myResult.length;j++) {
                                myResult[j] += myValues[j] * myFallOff * myBlend;
                        }
                        myAmp += myFallOff * myBlend;
                }
                for(int j = 0; j < myResult.length;j++) {
                        myResult[j] /= myAmp;
                }
                return myResult;
        }
        
        /**
         * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
         * multiple output values like in case of the worley noise
         * @param theVector coordinates for the noise
         * @return multiple values
         */
        public double[] values(final CCVector3d theVector) {
                return values(theVector.x, theVector.y, theVector.z);
        }
        
        /**
         * Returns the value for the given coordinates. By default only one band is calculated.
         * @param theX x coord for the noise
         * @param theY y coord for the noise
         * @param theZ z coord for the noise
         * @return the value for the given coordinates.
         */
        public double value(final double theX, final double theY, final double theZ) {
                return values(theX, theY, theZ)[0];
        }
        
        /**
         * Returns the value for the given coordinates. By default only one band is calculated.
         * @param theVector coordinates for the noise
         * @return the value for the given coordinates.
         */
        public double value(final CCVector3d theVector) {
                return values(theVector)[0];
        }
        
        @CCControl(name = "scale", min = 0, max = 20)
        public void scale(final float theNoiseScale){
                scaleImplementation(theNoiseScale);
        }
        
        protected void scaleImplementation(final float theNoiseScale){
                _myScale = theNoiseScale;
        }
        
        public void offset(final float theX, final float theY, final float theZ){
                _myOffsetX = theX;
                _myOffsetY = theY;
                _myOffsetZ = theZ;
        }
        
        public float scale(){
                return _myScale;
        }
        
        /**
         * The minimum value is one. You can also set floating numbers to blend
         * between the result of 2 or three bands.
         * @param theBands
         */
        @CCControl(name = "octaves", min = 1, max = 10)
        public void bands(final float theBands) {
                bandsImplementation(theBands);
        }
        
        protected void bandsImplementation(final float theBands){
                _myOctaves = CCMath.max(1.0f,theBands);
        }
        
        public float bands() {
                return _myOctaves;
        }
        
        /**
         * Controls amplitude change between each band. The default gain
         * is 0.5 meaning that the influence of every higher band is half as
         * high as the one from the previous.
         * @param theGain amplitude change between each band
         */
        @CCControl(name = "gain", min = 0, max = 1)
        public void gain(final float theGain) {
                gainImplementation(theGain);
        }
        
        protected void gainImplementation(final float theGain) {
                _myGain = theGain;
        }
        
        /**
         * Returns the amplitude change between each band
         * @return the amplitude change between each band
         */
        public float gain() {
                return _myGain;
        }
        
        /**
         * Lacunarity controls frequency change between each band. The default value
         * is 2.0 meaning the frequency of every band is twice as high as the previous
         * @param theLacunarity frequency change between each band
         */
        @CCControl(name = "lacunarity", min = 0, max = 10)
        public void lacunarity(final float theLacunarity) {
                lacunarityImplementation(theLacunarity);
        }
        
        protected void lacunarityImplementation(final float theLacunarity) {
                _myLacunarity = theLacunarity;
        }
        
        /**
         * Returns the frequency change between each band
         * @return the frequency change between each band
         */
        public float lacunarity() {
                return _myLacunarity;
        }
}