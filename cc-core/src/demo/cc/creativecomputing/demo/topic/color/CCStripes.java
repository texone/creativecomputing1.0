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
package cc.creativecomputing.demo.topic.color;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;

public class CCStripes extends CCApp {
	
	public class CCLight{
		private float _myX = 0;
		private float _mySpeedRandom = 0;
		private float _mySpeed;
		private float _myY;
		private float _myAlpha;
		
		public CCLight(float theY) {
			_myX = CCMath.random(width);
			_myY = theY;
			_mySpeedRandom = CCMath.random();
		}
		
		public void update(float theDeltaTime) {
			_mySpeed = CCMath.blend(_cMinSpeed, _cMaxSpeed, _mySpeedRandom);
			_mySpeed *= CCMath.blend(1,CCMath.noise(_myX * 0.01f *_cNoiseScale, _myY*20), _cSpeedVariation);
			_myX += theDeltaTime * _mySpeed;// * ;
			if(_myX - width() > width)_myX -= width + width() * 2; 
		}
		
		public float width() {
			return _cLengthScale * 500 * CCMath.blend(1f, _mySpeed / _cMaxSpeed ,_cSpeedOnLength);
		}
		
		public void draw(float theY) {
			float mySpace = width() / 100;
			g.beginShape(CCDrawMode.QUAD_STRIP);
			for(int i = 0; i < 100;i++) {
				float x = _myX - i * mySpace;
				float myAlphaBlend = 1 - CCMath.pow(i / 100f, _cAlphaPow);
				g.color(1f,0.2f,0.2f, _cAlpha * myAlphaBlend * theY * _myAlpha);
				g.vertex(x,_myY);
				g.vertex(x,_myY + 5);
			}
			g.endShape();
		}
	}
	
	public class CCStripe{
		private float _myY;
		private float _myAlpha = 0;
		
		private List<CCLight> _myLights = new ArrayList<CCLight>();
		
		public CCStripe(float theY) {
			_myY = theY;
			for(int i = 0; i < 10;i++) {
				_myLights.add(new CCLight(_myY));
			}
		}
		
		public void update(float theDeltaTime) {
			int i = 0;
			for(CCLight myLight:_myLights) {
				float findex = _cLightsBlend * _myLights.size();
				int index = (int)findex;
				if(i < index)myLight._myAlpha = 1;
				else if(i < index + 1)myLight._myAlpha = findex - index;
				else myLight._myAlpha = 0;
				myLight.update(theDeltaTime);
				i++;
			}
		}
		
		public void draw() {
			for(CCLight myLight:_myLights) {
				myLight.draw(_myAlpha);
			}
		}
	}
	
	private List<CCStripe> _myStripes = new ArrayList<CCStripe>();
	
	@CCControl(name = "length scale", min = 0, max = 1)
	private float _cLengthScale = 1;
	
	@CCControl(name = "min speed", min = 0, max = 200)
	private float _cMinSpeed = 0;
	@CCControl(name = "max speed", min = 0, max = 200)
	private float _cMaxSpeed = 0;
	@CCControl(name = "speed variation", min = 0, max = 1)
	private float _cSpeedVariation = 0;
	@CCControl(name = "speed on length", min = 0, max = 1)
	private float _cSpeedOnLength = 0;
	@CCControl(name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 0;
	@CCControl(name="lines", min = 0, max = 1)
	private float _cLineBlend = 0;
	@CCControl(name="lights", min = 0, max = 1)
	private float _cLightsBlend = 0;
	

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;
	
	@CCControl(name = "alpha pow", min = 0, max = 10)
	private float _cAlphaPow = 0;

	@Override
	public void setup() {
		List<Integer> myIds = new ArrayList<Integer>();
		for(int i = 0; i < 50;i++) {
			myIds.add(i);
		}
		for(int i = 0; i < 50;i++) {
			int myId = myIds.remove((int)CCMath.random(myIds.size()));
			_myStripes.add(new CCStripe(myId * 5));
		}
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		int i = 0;
		for(CCStripe myStripe:_myStripes) {
			float findex = _cLineBlend * _myStripes.size();
			int index = (int)findex;
			if(i < index)myStripe._myAlpha = 1;
			else if(i < index + 1)myStripe._myAlpha = findex - index;
			else myStripe._myAlpha = 0;
			myStripe.update(theDeltaTime);
			i++;
		}
	}

	@Override
	public void draw() {
		g.clear();
		g.translate(-width/2, -height/2);
		g.blend(CCBlendMode.ADD);
		for(CCStripe myStripe:_myStripes) {
			myStripe.draw();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCStripes.class);
		myManager.settings().size(1000, 250);
		myManager.start();
	}
}

