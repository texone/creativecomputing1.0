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
package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.signal.CCFarbrauschNoise;
import cc.creativecomputing.math.signal.CCPerlinNoise;
import cc.creativecomputing.math.signal.CCSawSignal;
import cc.creativecomputing.math.signal.CCSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.signal.CCSinSignal;
import cc.creativecomputing.math.signal.CCSquareSignal;
import cc.creativecomputing.math.signal.CCWorleyNoise;

public class CCSignalTest extends CCApp {


	public static enum CCSignalType {
		FARBRAUSCH(new CCFarbrauschNoise()), 
		PERLIN(new CCPerlinNoise()), 
		SIMPLEX(new CCSimplexNoise()), 
		WORLEY(new CCWorleyNoise(new CCWorleyNoise.CCWorleyF1Formular())), 
		SINUS(new CCSinSignal()), 
		SQUARE(new CCSquareSignal()), 
		SAW(new CCSawSignal());

		private CCSignal _mySignal;

		private CCSignalType(CCSignal theSignal) {
			_mySignal = theSignal;
		}

		public CCSignal signal() {
			return _mySignal;
		}

	}
	
	public static enum CCSignalDrawMode{
		LINE,
		GRADIENT
	}

	@CCControl(name = "signal")
	private CCSignalType _cSignalType = CCSignalType.PERLIN;
	
	@CCControl(name = "draw type")
	private CCSignalDrawMode _cDrawType = CCSignalDrawMode.LINE;
	
	@CCControl(name = "scale", min = 0f, max = 1)
	private float _cScale = 0.1f;
	@CCControl(name = "sub scale", min = 0f, max = 1)
	private float _cSubScale = 0.1f;
	
	@CCControl(name = "offset", min = 0, max = 100)
	private float _cOffset = 0f;
	
	@CCControl(name = "bands", min = 1, max = 10)
	private float _cBands = 1f;
	
	@CCControl(name = "lacunarity", min = 1, max = 10)
	private float _cLacunarity= 1f;
	
	@CCControl(name = "gain", min = 0, max = 1)
	private float _cGain = 1f;

	public void setup() {

		g.textFont(CCFontIO.createTextureMapFont("arial", 12));

		addControls("app", "app", this);
	}

	public void draw() {
		g.clear();

		float i = 0;
		
		if(_cSignalType == null)return;
		if(_cDrawType == null)return;
		
		_cSignalType.signal().scale(_cScale * _cSubScale);
		_cSignalType.signal().offset(_cOffset, 0, 0);
		_cSignalType.signal().bands(_cBands);
		_cSignalType.signal().lacunarity(_cLacunarity);
		_cSignalType.signal().gain(_cGain);

		g.color(1f);
		switch(_cDrawType) {
		case LINE:
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (float x = -width / 2; x <= width / 2; x++) {
				float y = (_cSignalType.signal().value((x + width / 2)) - 0.5f) * height;
				g.vertex(x, y);
			}
			g.endShape();
			break;
		case GRADIENT:
			g.beginShape(CCDrawMode.LINES);
			for (float x = -width / 2; x <= width / 2; x++) {
				g.color(_cSignalType.signal().value((x + width / 2)));
				g.vertex(x, -height/2);
				g.vertex(x, height/2);
			}
			g.endShape();
			break;
		}
		
		g.text(_cSignalType.name(), -width / 2 + 20, i * 20 - height / 2 + 20);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCSignalTest.class);
		myManager.settings().size(800, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
