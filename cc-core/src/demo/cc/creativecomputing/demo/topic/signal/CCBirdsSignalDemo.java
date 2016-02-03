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
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;

public class CCBirdsSignalDemo extends CCApp {
	
	@CCControl(name = "frequencyX", min = 0, max = 10f, external=true)
	private float _cFrequency = 0.005f;
	
	@CCControl(name = "frequencyx shift", min = -0.1f, max = 0.1f, external=true)
	private float _cFrequencyShift = 0;

	@CCControl(name = "amplitudeX", min = -1, max = 1f, external=true)
	private float _cAmplitude = 0.25f;
	
	@CCControl(name = "up length", min = 0, max = 1f, external=true)
	private float _cUpLength = 0.25f;
	
	
	@CCControl(name = "start phase", min= 0, max = CCMath.TWO_PI, external=true)
	private float _cStartPhase = 0f;
	
	@CCControl(name = "end phase", min= 0, max = CCMath.TWO_PI, external=true)
	private float _cEndPhase = 0f;
	
	@CCControl(name = "wing phase", min= 0, max = CCMath.TWO_PI, external=true)
	private float _cWingPhase = 0f;

	@CCControl(name = "offset random", min = 0, max = 1f, external=true )
	private float _cOffsetRandom = 0.25f;

	@CCControl(name = "center damp", min = 0, max = 1f, external=true )
	private float _cCenterDamp = 0.25f;
	
	@CCControl(name = "noise scale", min = 0, max = 1, external=true)
	private float _cScale = 0;
	
	@CCControl(name = "noise amplitude", min = 0, max = 3, external=true)
	private float _cNoiseAmplitude = 0;
	
	@CCControl(name = "noise random", min = 0, max = 1, external=true)
	private float _cNoiseRandom = 0;
		
	@CCControl(name = "noise move x", min = 0, max = 10, external=true)
	private float _cMoveX = 0;
	
	@CCControl(name = "noise move y", min = 0, max = 10, external=true)
	private float _cMoveY = 0;
		
	@CCControl(name = "noise move z", min = 0, max = 10, external=true)
	private float _cMoveZ = 0;
	
	@CCControl(name = "speed", min = 0, max = 10)
	private float _cSpeed = 0;
	
	private float _myPhase = 0;
	
	private class CCBird{
		
		private float _myRandom = 0;
		private double _myOffset = 0;
		
		private float _myStart;
		private float _myEnd;
		private float _myCenter;
		private float _myWidth;
		private float _myY;
		
		private CCBird(float theStart, float theEnd, float theY) {
			_myStart = theStart;
			_myEnd = theEnd;
			_myY = theY;
			_myWidth = (_myEnd - _myStart) / 2;
			_myCenter = (_myStart + _myEnd) / 2;
			_myRandom = CCMath.random();
			_myOffset = CCMath.random(CCMath.TWO_PI);
		}
		
		private double calcXSin(double theX, double theXOffset, double theY){
			double myFrequency = _cFrequency + theY * _cFrequencyShift * 0.01;
		
			return _cAmplitude * CCMath.cos(0.01 * myFrequency * (theX - 0.5) * CCMath.TWO_PI + theXOffset);
		}
		
		private double angle(double theValue) {
			theValue %= CCMath.TWO_PI;
			theValue /= CCMath.TWO_PI;
			if(theValue < _cUpLength) {
				return CCMath.map(theValue, 0, _cUpLength, 0, CCMath.PI);
			}else {
				return CCMath.map(theValue, _cUpLength, 1.0, CCMath.PI, CCMath.TWO_PI);
			}
		}
		
		public double calculateDistances(float theX) {
			double myRelX = CCMath.abs(_myCenter - theX) / _myWidth * CCMath.TWO_PI;
			
			double myBlend = (CCMath.cos(angle(_myPhase + _myOffset * _cOffsetRandom)) + 1) / 2;
			double myOffset = CCMath.blend(_cStartPhase, _cEndPhase, myBlend);
			
			double myWingMotion = calcXSin(myRelX, myOffset, _myY) - calcXSin(0,myOffset,_myY) * _cCenterDamp;// +(calcYSin(myRelY));
			
			double myNoiseOffset = _cMoveZ + _cNoiseRandom * 10 * _myRandom;
			double myNoiseMotion = noise(_myCenter, _myY, myNoiseOffset, _cScale);
			return myWingMotion + myNoiseMotion;
				
		}
		
		public void draw() {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(float x = _myStart; x <=_myEnd;x++){
				double myY = calculateDistances(x) * height;
				g.vertex(x, myY,_myY);
			}
			g.endShape();
		}
	}
	
	private CCSimplexNoise _myNoise = new CCSimplexNoise();
	
	private double noise(final double theX, final double theY, final double theZ, double theScale) {
		return (controlNoise(theX, theY, theZ + 1000, theScale) - 0.5) * _cNoiseAmplitude;
	}
	
	private double controlNoise(final double theX, final double theY, final double theZ, double theScale) {
		theScale /= 100;
		return _myNoise.value(
			theX * theScale + _cMoveX, 
			theY * theScale + _cMoveY, 
			theZ
		);
	}
	
	private List<CCBird> _myBirds = new ArrayList<CCBird>();
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		addControls("app", "app", this);
		for(int i = 0; i < 200;i++) {
			float myStart = CCMath.random(-500,width-150+500);
			float myEnd = myStart + CCMath.random(50,150);
			_myBirds.add(new CCBird(myStart,myEnd,CCMath.random(-600,600)));
		}
		
		_myArcball = new CCArcball(this);
	}
	
	@Override
	public void update(final float theDeltaTime) {
		_myPhase += _cSpeed * theDeltaTime;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.color();
		g.scale(1,-1);
		g.translate(-width/2, 0);
		for(CCBird myBird:_myBirds) {
			myBird.draw();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBirdsSignalDemo.class);
		myManager.settings().size(1200, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
