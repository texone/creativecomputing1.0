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
package cc.creativecomputing.demo.topic.fractalfrh;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;

public class CCQuadSubdivision extends CCApp {
	
	@CCControl(name = "start random", min = 0, max = 1)
	private float _cStartRandom = 0;
	
	@CCControl(name = "end random", min = 0, max = 1)
	private float _cEndRandom = 1;
	
	@CCControl(name = "levels", min = 2, max = 10)
	private int _cLevels = 2;
	
	
	
	private class Quad{
		float _myX1, _myY1;
		float _myX2, _myY2;
		float _myX3, _myY3;
		float _myX4, _myY4;
		
		public Quad(float theX1, float theY1, float theX2, float theY2, float theX3, float theY3, float theX4, float theY4){
			_myX1 = theX1; _myY1 = theY1;
			_myX2 = theX2; _myY2 = theY2;
			_myX3 = theX3; _myY3 = theY3;
			_myX4 = theX4; _myY4 = theY4;
		}
		
		public void draw(CCGraphics g){
			g.beginShape(CCDrawMode.LINE_LOOP);
			g.vertex(_myX1, _myY1);
			g.vertex(_myX2, _myY2);
			g.vertex(_myX3, _myY3);
			g.vertex(_myX4, _myY4);
			g.endShape();
		}
	}
	
	Quad _myStartQuad;
	List<Quad> _myQuads;

	@Override
	public void setup() {
		_myStartQuad = new Quad(-200, -200, -100, 200, 0, 200, -100, -200);
		_myQuads = subdivide(5, _myStartQuad);
		
		addControls("app", "app", this);
	}
	
	private List<Quad> subdivide(int theLevels, Quad theQuad){
		return subdivide(new ArrayList<Quad>(),theLevels, theQuad);
	}
	
	private List<Quad> subdivide(List<Quad> theQuads, int theLevel, Quad theQuad){
		if(theLevel > 0){
			List<Float> _myScales = new ArrayList<Float>();
			
			float _myValue = 0;
			
			while(_myValue < 1){
				_myScales.add(_myValue);
				_myValue += CCMath.random(_cStartRandom,_cEndRandom);
			}
			_myScales.add(1f);
			
			if(theLevel % 3 != 1){
				float myX1add = theQuad._myX4 - theQuad._myX1;
				float myY1add = theQuad._myY4 - theQuad._myY1;
				float myX2add = theQuad._myX3 - theQuad._myX2;
				float myY2add = theQuad._myY3 - theQuad._myY2;
				
				for(int i = 0; i < _myScales.size() - 1;i++){
					float myScale1 = _myScales.get(i);
					float myScale2 = _myScales.get(i + 1);
					subdivide(
						theQuads,
						theLevel - 1,
						new Quad(
							theQuad._myX1 + myX1add * myScale1, theQuad._myY1 + myY1add * myScale1,
							theQuad._myX2 + myX2add * myScale1, theQuad._myY2 + myY2add * myScale1, 
							theQuad._myX2 + myX2add * myScale2, theQuad._myY2 + myY2add * myScale2, 
							theQuad._myX1 + myX1add * myScale2, theQuad._myY1 + myY1add * myScale2
						)
					);
				}
			}else{
				float myX1add = theQuad._myX2 - theQuad._myX1;
				float myY1add = theQuad._myY2 - theQuad._myY1;
				float myX4add = theQuad._myX3 - theQuad._myX4;
				float myY4add = theQuad._myY3 - theQuad._myY4;
				
				for(int i = 0; i < _myScales.size() - 1;i++){
					float myScale1 = _myScales.get(i);
					float myScale2 = _myScales.get(i + 1);
					subdivide(
						theQuads,
						theLevel - 1,
						new Quad(
							theQuad._myX1 + myX1add * myScale1, theQuad._myY1 + myY1add * myScale1,
							theQuad._myX1 + myX1add * myScale2, theQuad._myY1 + myY1add * myScale2, 
							theQuad._myX4 + myX4add * myScale2, theQuad._myY4 + myY4add * myScale2, 
							theQuad._myX4 + myX4add * myScale1, theQuad._myY4 + myY4add * myScale1
						)
					);
				}
			}
		}else{
			theQuads.add(theQuad);
		}
		return theQuads;
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myStartQuad.draw(g);
		
		for(Quad myQuad:_myQuads){
			myQuad.draw(g);
		}
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_R:
			_myQuads = subdivide(_cLevels, _myStartQuad);
			break;

		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCQuadSubdivision.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

