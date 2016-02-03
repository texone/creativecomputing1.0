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

public class CCTurbulence2SignalDemo extends CCApp {
	
	@CCControl(name = "in pow", min = 0.5f, max = 5)
	private float _cInPow;
	
	@CCControl(name = "amp", min = 0, max = 1)
	private float _cAmp;
	
	@CCControl(name = "amp mod", min = -1, max = 1)
	private float _cAmpMod;
	
	@CCControl(name = "freq", min = 0, max = 100)
	private float _cFreq;
	
	@CCControl(name = "freq mod", min = -1, max = 1)
	private float _cFreqMod;
	
	@CCControl(name = "freq random", min = 0, max = 1)
	private float _cFreqRandom;
	
	@CCControl(name = "phase", min = 0, max = 100)
	private float _cPhase;
	
	@CCControl(name = "phase2", min = 0, max = 100)
	private float _cPhase2;
	
	@CCControl(name = "phase random", min = 0, max = 1)
	private float _cPhaseRandom;
	
	@CCControl(name = "max cycles", min = 0, max = 10)
	private float _cMaxCycles;
	
	private class CCArch{
		private float[] _myRandom1;
		
		private int _myID;
		
		private CCArch(int theID){
			_myRandom1 = randoms(10);
			
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
		int myCycles = (int)((theValue * _cFreq) / CCMath.PI);
		
//		theValue += _cPhase + _cPhaseRandom * theArch.random2(0) * CCMath.TWO_PI;
		float myStartAmpMod = CCMath.pow(myCycles * _cAmpMod, _cInPow);
		float myEndAmpMod = CCMath.pow((myCycles + 1) * _cAmpMod, _cInPow);
		
		float myFreqMod = 1 + myCycles * _cFreqMod;
		myFreqMod *= 1 + CCMath.blend(-1, 1, theArch.random1(0)) * _cFreqRandom;
		
		float myAngle = theValue * _cFreq * myFreqMod;
		float myBlend = (myAngle % CCMath.PI) / CCMath.PI;
		float myOut = CCMath.cos(myAngle) * CCMath.blend(myStartAmpMod, myEndAmpMod, myBlend);
		
		
		int mySign = theArch._myID % 2 == 0 ? 1 : -1;
		
//		if(myCycles > _cMaxCycles){
//			return _cAmp * myAmpMod * mySign;
//		}
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
				float myY = turbulence(angle, myArch) * height * _cAmp;
				g.vertex(x, myY);
			}
			g.endShape();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTurbulence2SignalDemo.class);
		myManager.settings().size(1500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
