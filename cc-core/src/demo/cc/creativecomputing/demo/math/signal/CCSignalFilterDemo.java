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
import cc.creativecomputing.math.signal.filter.CCDoubleExponentialSmoothingFilter;
import cc.creativecomputing.math.signal.filter.CCDoubleMovingAverageFilter;
import cc.creativecomputing.math.signal.filter.CCExponentialSmoothingFilter;
import cc.creativecomputing.math.signal.filter.CCFilter;
import cc.creativecomputing.math.signal.filter.CCMedianFilter;
import cc.creativecomputing.math.signal.filter.CCOneEuroFilter;
import cc.creativecomputing.math.signal.filter.CCSimpleAverageFilter;

public class CCSignalFilterDemo extends CCApp {


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
	
	public static enum CCSignalFilterType{
		SIMPLE_MOVING_AVERAGE,
		DOUBLE_MOVING_AVERAGE,
		EXPONENTIAL_SMOOTHING,
		DOUBLE_EXPONENTIAL_SMOOTHING,
		MEDIAN,
		ONE_EURO
	}

	@CCControl(name = "signal")
	private CCSignalType _cSignalType = CCSignalType.PERLIN;
	
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
	
	private class CCFilterSettings{
		@CCControl(name = "filter")
		private CCFilter<?> _myFilter;
		
		@CCControl(name = "active")
		private boolean _cIsActive;
	}
	

	private CCSimpleAverageFilter _mySimpleAverageFilter;
	
	private CCDoubleMovingAverageFilter _myDoubleMovingAverageFilter;
	
	private CCExponentialSmoothingFilter _myExponentialSmoothingFilter;
	
	private CCDoubleExponentialSmoothingFilter _myDoubleExponentialSmoothingFilter;
	
	private CCMedianFilter _myMedianFilter;
	
	@CCControl(name = "oneEuroFilter")
	private CCOneEuroFilter _myOneEuroFilter = new CCOneEuroFilter();
	

	public void setup() {

		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		

		addControls("app", "app", this);
	}

	public void draw() {
		g.clear();

		float i = 0;
		
		if(_cSignalType == null)return;
		
		_cSignalType.signal().scale(_cScale * _cSubScale);
		_cSignalType.signal().offset(_cOffset, 0, 0);
		_cSignalType.signal().bands(_cBands);
		_cSignalType.signal().lacunarity(_cLacunarity);
		_cSignalType.signal().gain(_cGain);

		g.color(1f,0.5f);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (float x = -width / 2; x <= width / 2; x++) {
			float y = (_cSignalType.signal().value((x + width / 2)) - 0.5f) * (height - 10);
			g.vertex(x, y);
		}
		g.endShape();

		
		g.color(1f, 0f, 0);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (float x = -width / 2; x <= width / 2; x++) {
			float mySignal = (_cSignalType.signal().value((x + width / 2)) - 0.5f) * (height - 10);
			_myOneEuroFilter.update(mySignal, 0.01f);
			g.vertex(x, _myOneEuroFilter.value());
		}
		g.endShape();
		
		_myOneEuroFilter.reset();
		

		g.text(_cSignalType.name(), -width / 2 + 20, i * 20 - height / 2 + 20);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCSignalFilterDemo.class);
		myManager.settings().size(1800, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
