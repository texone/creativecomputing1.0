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
package cc.creativecomputing.demo.topic.interaction;

import java.util.Iterator;
import java.util.LinkedList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCMotionLimiter;
import cc.creativecomputing.util.CCFormatUtil;

public class CCLimitedMotionDemo extends CCApp {
	
	public class CCValueBuffer implements Iterable<Float>{
		
		private LinkedList<Float> _myValues;
		private float _myValue;
		
		private int _myBufferSize;
		
		private CCColor _myColor;
		private String _myName;
		private int _myID;
		
		public CCValueBuffer(int theBufferSize, CCColor theColor, String theName, int theID) {
			_myValues = new LinkedList<Float>();
			_myBufferSize = theBufferSize;
			_myColor = theColor;
			_myName = theName;
			_myID = theID;
		}
		
		public void update(float theValue) {
			_myValue = theValue;
			if(_myValues.size() >= _myBufferSize)_myValues.removeFirst();
			_myValues.add(theValue);
		}

		@Override
		public Iterator<Float> iterator() {
			return _myValues.iterator();
		}
		
		private void draw(float theScale) {
			g.color(_myColor);
			float myY = -height/2 + _myID * 20 + 10;
			g.rect(-width/2 + 10, myY, 10,10);
			g.text(CCFormatUtil.nd(_myValue,3,3) + " " + _myName, -width/2 + 25, myY);
			
			int i = -width / 2;
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(float myPosition:_myValues) {
				g.vertex(i++, myPosition * theScale);
			}
			g.endShape();
		}
	}

	@CCControl(name = "speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	
	private float _myNoiseOffset = 0;
	
	private CCSimplexNoise _myNoise;
	private CCMotionLimiter _myLimiter;
	
	private CCValueBuffer _myTargetBuffer;
	private CCValueBuffer _myPositionBuffer;
	private CCValueBuffer _myFuturePositionBuffer;
	private CCValueBuffer _myVelocityBuffer;
	private CCValueBuffer _myAccelerationBuffer;
	
	private int _myBufferSize;
	
	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myNoise = new CCSimplexNoise();
		
		_myBufferSize = (int)(width * 0.75f);
		_myTargetBuffer = new CCValueBuffer(_myBufferSize, CCColor.YELLOW, "target",0);
		_myPositionBuffer = new CCValueBuffer(_myBufferSize, CCColor.WHITE, "position",1);
		_myVelocityBuffer = new CCValueBuffer(_myBufferSize, CCColor.RED, "velocity", 2);
		_myAccelerationBuffer = new CCValueBuffer(_myBufferSize, CCColor.GREEN, "acceleration", 3);
		_myFuturePositionBuffer = new CCValueBuffer(_myBufferSize, CCColor.CYAN, "future position", 4);
		
		_myLimiter = new CCMotionLimiter();
		_myLimiter.range(-height/4, height/4);
		addControls("app", "limiter", _myLimiter);
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	
	private float target(float theTime){
		return (_myNoise.value(theTime) - 0.5f) * height;
	}
	
	private float myLimitedPosition;

	@Override
	public void update(final float theDeltaTime) {
		_myNoiseOffset += theDeltaTime * _cNoiseSpeed;
		
		float myTarget = target(_myNoiseOffset);
		myLimitedPosition = _myLimiter.limit(myTarget, theDeltaTime);

		_myTargetBuffer.update(myTarget);
		_myPositionBuffer.update(myLimitedPosition);
		_myFuturePositionBuffer.update((float)_myLimiter.futurePosition());
		_myVelocityBuffer.update((float)_myLimiter.velocity() / CCMotionLimiter._cMaxVelocity);
		_myAccelerationBuffer.update((float)_myLimiter.acceleration() / CCMotionLimiter._cMaxAcceleration);
	}
	
	@Override
	public void draw() {
		g.clear();
		
		_myVelocityBuffer.draw(height / 3);
		_myAccelerationBuffer.draw(height / 3);
		
		_myFuturePositionBuffer.draw(1);
		_myPositionBuffer.draw(1);
		
		_myTargetBuffer.draw(1);
		
		float myStay = _myLimiter.stay();
		g.color(255);
		g.line(-width/2, myLimitedPosition + myStay, myStay + width/2, myLimitedPosition + myStay);
		g.line(-width/2, myLimitedPosition - myStay, myStay + width/2, myLimitedPosition - myStay);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCLimitedMotionDemo.class);
		myManager.settings().size(1400, 500);
		myManager.settings().antialiasing(8);
		myManager.settings().vsync(true);
		myManager.start();
	}
}

