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
 * Parametric color gradients inspired by the fabulous book generative gestaltung
 * 
 * @author christianriekoff
 * 
 */
public class CCColorBlend extends CCApp {

	@CCControl(name = "tile count x", min = 2, max = 100)
	private int _cTileCountX = 2;
	
	@CCControl(name = "tile count Y", min = 2, max = 10)
	private int _cTileCountY = 2;
	
	@CCControl(name = "random seed", min = 1, max = 50)
	private int _cRandomSeed = 50; 
	
	private CCColor[] _myLeftColors;
	private CCColor[] _myRightColors;
	
	@Override
	public void setup() {
		addControls("app", "app", this);
	}

	private void defineColors() {
		_myLeftColors = new CCColor[_cTileCountY];
		_myRightColors = new CCColor[_cTileCountY];
		
		for (int i = 0; i < _cTileCountY; i++) {
			_myLeftColors[i] = CCColor.createFromHSB(
				CCMath.random(60f / 360f), 
				CCMath.random(1f), 
				1f
			);

			_myRightColors[i] = CCColor.createFromHSB(
				CCMath.random(160f / 360f, 190f / 360f), 
				1f,
				CCMath.random(1f)
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
		g.clear();
		g.translate(-width/2, -height/2);
		
		float tileWidth = width / (float) _cTileCountX;
		float tileHeight = height / (float)_cTileCountY;
		CCColor interCol;

		for (int gridY = 0; gridY < _cTileCountY; gridY++) {
			CCColor col1 = _myLeftColors[gridY];
			CCColor col2 = _myRightColors[gridY];

			for (int gridX = 0; gridX < _cTileCountX; gridX++) {
				float amount = CCMath.map(gridX, 0, _cTileCountX - 1, 0, 1);

//				if (interpolateShortest) {
//					// switch to rgb
//					colorMode(RGB, 255, 255, 255, 255);
//					interCol = lerpColor(col1, col2, amount);
//					// switch back
//					colorMode(HSB, 360, 100, 100, 100);
//				} else {
					interCol = CCColor.blend(col1, col2, amount);
//				}
				g.color(interCol);

				float posX = tileWidth * gridX;
				float posY = tileHeight * gridY;
				g.rect(posX, posY, tileWidth, tileHeight);
			}
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorBlend.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
