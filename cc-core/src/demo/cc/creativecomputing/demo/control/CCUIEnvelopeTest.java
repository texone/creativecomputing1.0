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
import cc.creativecomputing.control.modulators.CCEnvelope;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.util.logging.CCLog;

public class CCUIEnvelopeTest extends CCApp {

	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private static class Circle{
		
		public Circle() {
		}
		
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2)
		private float _myX = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2)
		private float _myY = 0;
		
		@CCControl(name = "radius", min = 1, max = 50)
		private float _myRadius = 0;
		
		@CCControl(name = "envelope", min = 0, max = 100, length=4, numberOfEnvelopes=4)
		public CCEnvelope _myEnvelope = new CCEnvelope();
		
		public void draw(CCGraphics g) {
			CCLog.info(_myEnvelope.value());
			g.ellipse(_myX, _myY, _myEnvelope.value());
		}
	}
	
	private Circle _myCircle1;
	private Circle _myCircle2;

	@Override
	public void setup() {
		
		_myCircle1 = new Circle();
		_myCircle2 = new Circle();
		
		addControls("circles", "circle1", 0, _myCircle1);
		addControls("circles", "circle2", 1, _myCircle2);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myCircle1.draw(g);
		_myCircle2.draw(g);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	int index = 0;
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		_myCircle1._myEnvelope.play(index++);
		index%=4;
		_myCircle2._myEnvelope.play();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIEnvelopeTest.class);
		myManager.settings().size(APP_WIDTH, APP_HEIGHT);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

