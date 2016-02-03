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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCMath;

/**
 * Parametric color scheme inspired by the fabulous book generative gestaltung
 * 
 * @author christianriekoff
 * 
 */
public class CCColorScheme extends CCApp {

	private final static int MAX_TILE_COUNT_X = 50;
	private final static int MAX_TILE_COUNT_Y = 10;

	@CCControl(name = "x tiles", min = 1, max = MAX_TILE_COUNT_X)
	private int _cTileCountX = 10;
	@CCControl(name = "y tiles", min = 1, max = MAX_TILE_COUNT_Y)
	private int _cTileCountY = 5;

	private CCColor[] _myColors;
	
	@CCControl(name = "max colors", min = 1, max = 50)
	private int _cMaxColors = 50; 
	
	@CCControl(name = "random seed", min = 1, max = 50)
	private int _cRandomSeed = 50; 
	
	@CCControl(name = "hue min", min = 0, max = 1)
	private float _cHueMin = 0; 
	@CCControl(name = "hue max", min = 0, max = 1)
	private float _cHueMax = 1; 
	
	@CCControl(name = "saturation min", min = 0, max = 1)
	private float _cSaturationMin = 0; 
	@CCControl(name = "saturation max", min = 0, max = 1)
	private float _cSaturationMax = 1;
	
	@CCControl(name = "brightness min", min = 0, max = 1)
	private float _cBrightnesMin = 0;
	@CCControl(name = "brightness max", min = 0, max = 1)
	private float _cBrightnesMax = 1;

	@Override
	public void setup() {
		addControls("app", "app", this);
		g.clearColor(255);

		defineColors();
	}

	private void defineColors() {
		_myColors = new CCColor[CCMath.max(1,_cMaxColors)];

		// init with random values
		for (int i = 0; i < _myColors.length; i++) {
			_myColors[i] = CCColor.createFromHSB(
				CCMath.random(_cHueMin, _cHueMax), 
				CCMath.random(_cSaturationMin, _cSaturationMax), 
				CCMath.random(_cBrightnesMin, _cBrightnesMax)
			);
		}
	}

	@Override
	public void update(final float theDeltaTime) {
		CCMath.randomSeed(_cRandomSeed);
		defineColors();
	}

	@Override
	public void draw() {
		// white back
		g.clear();
		g.translate(-width/2, - height/2);

		// count every tile
		int counter = 0;

		float tileWidth = width / (float) _cTileCountX;
		float tileHeight = height / (float) _cTileCountY;

		for (int gridY = 0; gridY < _cTileCountY; gridY++) {
			for (int gridX = 0; gridX < _cTileCountX; gridX++) {
				float posX = tileWidth * gridX;
				float posY = tileHeight * gridY;
				int index = counter % _myColors.length;

				// get component color values
				g.color(_myColors[index]);
				g.rect(posX, posY, tileWidth, tileHeight);
				counter++;
			}
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorScheme.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
