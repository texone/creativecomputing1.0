package cc.creativecomputing.math.signal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCControl;

public class CCMixSignal extends CCSignal{
        
        @CCControl(name = "saw", min = 0, max = 1)
        private float _cSaw = 0;
        @CCControl(name = "simplex", min = 0, max = 1)
        private float _cSimplex = 0;
        @CCControl(name = "sine", min = 0, max = 1)
        private float _cSine = 0;
        @CCControl(name = "square", min = 0, max = 1)
        private float _cSquare = 0;
        @CCControl(name = "tri", min = 0, max = 1)
        private float _cTri = 0;
        @CCControl(name = "sloped tri", min = 0, max = 1)
        private float _cSlopedTri = 0;
        @CCControl(name = "amp", min = 0, max = 1)
        private float _cAmp = 0;
        
        
        private CCSawSignal _mySaw;
        private CCSimplexNoise _mySimplex;
        private CCSinSignal _mySine;
        private CCSquareSignal _mySquare;
        private CCTriSignal _myTri;
        private CCSlopedTriSignal _mySlopedTri;
        
        private List<CCSignal> _mySignals = new ArrayList<>();
        
        public CCMixSignal(){
                _mySignals.add(_mySaw = new CCSawSignal());
                _mySignals.add(_mySimplex = new CCSimplexNoise());
                _mySignals.add(_mySine = new CCSinSignal());
                _mySignals.add(_mySquare = new CCSquareSignal());
                _mySignals.add(_myTri = new CCTriSignal());
                _mySignals.add(_mySlopedTri = new CCSlopedTriSignal());
        }
        
        @Override
        protected void scaleImplementation(float theNoiseScale) {
                super.scaleImplementation(theNoiseScale);
                for(CCSignal mySignal:_mySignals){
                        mySignal.scale(theNoiseScale);
                }
        }
        
        @Override
        protected void bandsImplementation(float theBands) {
        	super.bandsImplementation(theBands);
        	for(CCSignal mySignal:_mySignals){
        		mySignal.bands(theBands);
        	}
        }
        
        @Override
        protected void gainImplementation(float theGain) {
        	super.gainImplementation(theGain);
        	for(CCSignal mySignal:_mySignals){
        		mySignal.gain(theGain);
               }
        }
        
        @Override
        protected void lacunarityImplementation(float theLacunarity) {
        	super.lacunarityImplementation(theLacunarity);
        	for(CCSignal mySignal:_mySignals){
        		mySignal.lacunarity(theLacunarity);
            }
        }
        
        private float mixSignal(float[] theSaw, float[] theSimplex, float[] theSine, float[] theSquare, float[] theTri, float[] theSlopedTri){
        	float myMaxAmount = _cSaw + _cSimplex + _cSine + _cSquare + _cTri + _cSlopedTri;
        	return (
        		theSaw[0] * _cSaw + 
        		theSimplex[0] * _cSimplex + 
        		theSine[0] * _cSine +
        		theSquare[0] * _cSquare + 
        		theTri[0] * _cTri + 
        		theSlopedTri[0] * _cSlopedTri
        	) / myMaxAmount * _cAmp;
        }

        @Override
        public float[] signalImpl(float theX, float theY, float theZ) {
        	return new float[]{mixSignal(
        		_mySaw.signalImpl(theX, theY, theZ),
        		_mySimplex.signalImpl(theX, theY, theZ),
        		_mySine.signalImpl(theX, theY, theZ),
        		_mySquare.signalImpl(theX, theY, theZ),
        		_myTri.signalImpl(theX, theY, theZ),
        		_mySlopedTri.signalImpl(theX, theY, theZ)
        	)};
        }
        
        @Override
        public float[] signalImpl(float theX, float theY) {
        	return new float[]{mixSignal(
        		_mySaw.signalImpl(theX, theY),
        		_mySimplex.signalImpl(theX, theY),
        		_mySine.signalImpl(theX, theY),
        		_mySquare.signalImpl(theX, theY),
        		_myTri.signalImpl(theX, theY),
        		_mySlopedTri.signalImpl(theX, theY)
        	)};
        }
        
        @Override
        public float[] signalImpl(float theX) {
        	return new float[]{mixSignal(
        		_mySaw.signalImpl(theX),
        		_mySimplex.signalImpl(theX),
        		_mySine.signalImpl(theX),
        		_mySquare.signalImpl(theX),
        		_myTri.signalImpl(theX),
        		_mySlopedTri.signalImpl(theX)
        	)};
        }

}