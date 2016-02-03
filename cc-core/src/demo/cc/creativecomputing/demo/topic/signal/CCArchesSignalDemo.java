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
package cc.creativecomputing.demo.topic.signal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;

public class CCArchesSignalDemo extends CCApp {
	
	@CCControl(name = "low stretch", min = 0, max = 10, external=true)
	private float _cLowStretch = 0f;
	
	@CCControl(name = "up stretch", min = 0, max = 10, external=true)
	private float _cUpStretch = 0f;
	
	@CCControl(name = "stretch random", min = 0, max = 1, external=true)
	private float _cStretchRandom = 0;
	
	@CCControl(name = "amp", min = 0, max = 1, external=true)
	private float _cAmp = 0;
	
	@CCControl(name = "amp change", min = -1, max = 1, external=true)
	private float _cAmpChange = 0;
	
	@CCControl(name = "amp random", min = 0, max = 1, external=true)
	private float _cAmpRandom = 0;
	
	@CCControl(name = "freq", min = 0, max = 10, external=true)
	private float _cFreq = 0;
	
	@CCControl(name = "freq change", min = -1, max = 1, external=true)
	private float _cFreqChange = 0;
	
	@CCControl(name = "phase", min = 0, max = 10, external=true)
	private float _cPhase = 0;
	
	@CCControl(name = "max cycles", min = 1, max = 10, external=true)
	private float _cMaxCycles = 0;
	
	@CCControl(name = "end amp", min = 0, max = 10, external=true)
	private float _cEndAmp = 0;
	
	
	private class CCArch{
		private float[] _myRandom1;
		private float[] _myRandom2;
		
		private CCArch(){
			
			_myRandom1 = randoms(10);
			_myRandom2 = randoms(11);
		}
		
		private float[] randoms(int theValues){
			float[] myResult = new float[theValues];
			for(int i = 0; i < myResult.length;i++){
				myResult[i] = CCMath.random();
			}
			return myResult;
		}
		
		private float random1(int theCycle){
			return _myRandom1[theCycle % _myRandom1.length];
		}
		
		private float random2(int theCycle){
			return _myRandom1[theCycle % _myRandom2.length];
		}
	}
	
	private List<CCArch> _myArches = new ArrayList<CCArch>();

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		for(int i = 0; i < 16;i++){
			_myArches.add(new CCArch());
		}
	}

	@Override
	public void update(final float theDeltaTime) {
	}
	
	private float archLength(float theMod) {
		return _cUpStretch * theMod + _cLowStretch / theMod + CCMath.TWO_PI;
	}
	
	private float archSig(float theValue, CCArch theArch) {
		theValue += _cPhase;
		float myFreqModulation = 1 + theValue * _cFreqChange;
		theValue *= _cFreq * myFreqModulation;

		float myStretchMod = 1 + CCMath.blend(-1, 1, theArch.random1(0)) * _cStretchRandom;
		int myCycle = (int)(theValue / archLength(myStretchMod));
		
		if(myCycle > _cMaxCycles)return _cEndAmp;
		
		float myAmpModulation = 1 + myCycle *  _cAmpChange;
		myAmpModulation *= 1 + CCMath.blend(-1, 1, theArch.random2(myCycle)) * _cAmpRandom;
		
		theValue %= archLength(myStretchMod);
		theValue /= archLength(myStretchMod);
		theValue *= archLength(1);
				
		if(theValue < _cUpStretch) {
			return 0;
		}
		theValue -= _cUpStretch;
		
		if(myCycle > _cMaxCycles - 1){
			if(theValue < CCMath.PI) {
				return (CCMath.cos(CCMath.PI+theValue) + 1) / 2 * _cEndAmp;
			}
			return _cEndAmp;
		}
		if(theValue < CCMath.PI) {
			return (CCMath.cos(CCMath.PI+theValue) + 1) / 2 * myAmpModulation;
		}
		theValue -= CCMath.PI;
		if(theValue < _cLowStretch) {
			return myAmpModulation;
		}
		theValue -= _cLowStretch;
		if(theValue < CCMath.PI) {
			return (CCMath.cos(theValue) + 1) / 2 * myAmpModulation;
		}
		return 0;
	}

	@Override
	public void draw() {
		g.clear();
		g.color();
		g.scale(1,-1);
		g.translate(-width/2, -height/2);
		for(CCArch myArch:_myArches){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(float x = 0; x <=width;x++){
				float angle = x / width * archLength(1);
				float myY = archSig(angle, myArch) * height * _cAmp;
				g.vertex(x, myY);
			}
			g.endShape();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCArchesSignalDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
