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
package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.signal.CCFarbrauschNoise;
import cc.creativecomputing.math.signal.CCPerlinNoise;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.util.CCStopWatch;

public class CCNoisePerformanceTest extends CCApp {
	
	private CCFarbrauschNoise _myPerlinNoise;
	private CCPerlinNoise _myPerlinNoise2;
	private CCSimplexNoise _mySimplexNoise;
	
	private CCStopWatch _myStopWatch;
	
	public void setup() {
		g.clearColor(0);
		
		_myPerlinNoise = new CCFarbrauschNoise();
		_mySimplexNoise = new CCSimplexNoise();
		_myPerlinNoise2 = new CCPerlinNoise();
		
		_myStopWatch = CCStopWatch.instance();
		
		addControls("app", "app", CCStopWatch.instance());
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myStopWatch.update(theDeltaTime);
	}

	public void draw() {
		CCStopWatch.instance().startWatch("PERLIN");
		for(int i = 0; i < 100000;i++){
			_myPerlinNoise2.value(i, i, i);
		}
		CCStopWatch.instance().endWatch("PERLIN");
		CCStopWatch.instance().startWatch("SIMPLEX");
		for(int i = 0; i < 100000;i++){
			_mySimplexNoise.value(i, i, i);
		}
		CCStopWatch.instance().endWatch("SIMPLEX");
		CCStopWatch.instance().startWatch("FARBRAUSCH");
		for(int i = 0; i < 100000;i++){
			_myPerlinNoise.value(i, i, i);
		}
		CCStopWatch.instance().endWatch("FARBRAUSCH");
		g.clear();
		_myStopWatch.draw(g);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCNoisePerformanceTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}
