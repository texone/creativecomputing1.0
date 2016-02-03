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
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCTurbulenceSignalDemo extends CCApp {
	
	@CCControl(name = "start amp", min = 0, max = 1)
	private float _cStartAmp;
	
	@CCControl(name = "end amp", min = 0, max = 1)
	private float _cEndAmp;
	
	@CCControl(name = "freq", min = 0, max = 100)
	private float _cFreq;
	
	@CCControl(name = "freq mod", min = -1, max = 1)
	private float _cFreqMod;
	
	@CCControl(name = "freq random", min = 0, max = 1)
	private float _cFreqRandom;
	
	@CCControl(name = "pow random", min = 0, max = 1)
	private float _cPowRandom;
	
	@CCControl(name = "phase", min = -10, max = 10)
	private float _cPhase;
	
	@CCControl(name = "phase random", min = 0, max = 1)
	private float _cPhaseRandom;
	
	@CCControl(name = "max cycles", min = 0, max = 10)
	private int _cMaxCycles;
	
	@CCControl(name = "ease mode")
	private CCEaseFormular _cEaseFormular = CCEaseFormular.LINEAR;
	
	private class CCArch{
		private float[] _myRandom1;
		private float[] _myRandom2;
		private float[] _myRandom3;
		
		private int _myID;
		
		private CCArch(int theID){
			_myRandom1 = randoms(10);
			_myRandom2 = randoms(11);
			_myRandom3 = randoms(12);
			
			_myID = theID;
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
			return _myRandom2[theCycle % _myRandom2.length];
		}
		
		private float random3(int theCycle){
			return _myRandom3[theCycle % _myRandom3.length];
		}
	}
	
	private List<CCArch> _myArches = new ArrayList<CCArch>();

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		for(int i = 0; i < 16;i++){
			_myArches.add(new CCArch(i));
		}
	}

	@Override
	public void update(final float theDeltaTime) {
	}
	
	
	private float turbulence(float theValue, CCArch theArch) {
		theValue += _cPhase + _cPhaseRandom * theArch.random2(0) * CCMath.TWO_PI;
		float myFreqMod = 1 + theValue * _cFreqMod;
		myFreqMod *= 1 + CCMath.blend(-1, 1, theArch.random1(0)) * _cFreqRandom;
		
		float myAngle = theValue * _cFreq * myFreqMod;
		float myCycles = (myAngle / CCMath.TWO_PI);
		float myBlend = CCMath.saturate(myCycles / _cMaxCycles);
		myBlend = CCMath.pow(myBlend, 1 + CCMath.blend(-1, 1, theArch.random3(0)) * _cPowRandom);
		myBlend = _cEaseFormular.easing().easeInOut(myBlend);
		
		float myOut = CCMath.cos(myAngle) * CCMath.blend(_cStartAmp, _cEndAmp, myBlend);
		
		int mySign = theArch._myID % 2 == 0 ? 1 : -1;
		
		if((int)myCycles >= _cMaxCycles){
			return _cEndAmp * mySign;
		}
		return myOut * mySign;
	}

	@Override
	public void draw() {
		g.clear();
		g.color();
		g.scale(1,-1);
		g.translate(-width/2, 0);
		for(CCArch myArch:_myArches){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(float x = 0; x <=width;x++){
				float angle = x / width * CCMath.TWO_PI;
				float myY = turbulence(angle, myArch) * height;
				g.vertex(x, myY);
			}
			g.endShape();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTurbulenceSignalDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
