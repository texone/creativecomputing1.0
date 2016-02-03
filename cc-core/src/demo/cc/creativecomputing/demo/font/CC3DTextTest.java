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
package cc.creativecomputing.demo.font;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CC3DFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CC3DTextTest extends CCApp {
	
	private class Number{
		private CCText _myText;
		private CCVector3f _myTranslation;
		private CCVector3f _myRotationAxis;
		private float _myAngle;
		private float _myScale;
		
		public Number() {
			_myText = new CCText(_myFont);
			_myText.text("CC");
			_myText.align(CCTextAlign.CENTER);
			
			_myTranslation = CCVecMath.random3f(CCMath.random(300));
			_myRotationAxis = CCVecMath.random3f();
			_myAngle = CCMath.random(360);
			_myScale = CCMath.random(1);
		}
		
		public void update(final float theDeltaTime) {
			_myAngle += theDeltaTime * 10;
		}
		
		public void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(_myTranslation);
			g.rotate(_myAngle, _myRotationAxis);
			g.scale(_myScale);
			_myText.draw(g);
			g.popMatrix();
		}
	}
	
	private CC3DFont _myFont;
	private CCArcball _myArcball;
    
    private List<Number> _myNumbers = new ArrayList<Number>();

	@Override
	public void setup() {
		_myFont = CCFontIO.create3DFont("Helvetica", 50, 10);
		
		_myArcball = new CCArcball(this);
		
		for(int i = 0; i < 500;i++) {
			_myNumbers.add(new Number());
		}
	}

	@Override
	public void update(final float theDeltaTime) {
		for(Number myNumber:_myNumbers) {
			myNumber.update(theDeltaTime);
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		for(Number myNumber:_myNumbers) {
			myNumber.draw(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CC3DTextTest.class);
		myManager.settings().size(1000, 700);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

