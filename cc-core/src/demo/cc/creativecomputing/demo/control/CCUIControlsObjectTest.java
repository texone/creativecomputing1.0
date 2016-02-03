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
package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;

public class CCUIControlsObjectTest extends CCApp {
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private class Shape{
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2)
		protected float _cX = 0;
		
		protected float _cY = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2)
		protected void value(float theValue) {
			_cY = theValue;
		}
	}
	
	private class Rect extends Shape{
		
		@CCControl(name = "width", min = 1, max = 50)
		private float _cWidth = 0;
		
		@CCControl(name = "height", min = 1, max = 50)
		private float _cHeight = 0;
		
		public void draw(CCGraphics g) {
			g.rect(_cX, _cY, _cWidth, _cHeight);
		}
	}
	
	private class Circle extends Shape{
		
		@CCControl(name = "radius", min = 1, max = 50)
		private float _cRadius = 0;
		
		public void draw(CCGraphics g) {
			g.ellipse(_cX, _cY, _cRadius);
		}
	}
	
	@CCControl(name = "rect")
	private Rect _myRect;
	
	@CCControl(name = "circle")
	private Circle _myCircle;

	@Override
	public void setup() {
		
		_myRect = new Rect();
		_myCircle = new Circle();
		
		addControls("shapes", "shapes", 0,this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myRect.draw(g);
		_myCircle.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIControlsObjectTest.class);
		myManager.settings().size(APP_WIDTH, APP_HEIGHT);
		myManager.start();
	}
}

