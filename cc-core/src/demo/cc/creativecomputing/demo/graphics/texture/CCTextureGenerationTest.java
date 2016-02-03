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
package cc.creativecomputing.demo.graphics.texture;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCTextureGenMode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCTextureGenerationTest extends CCApp {

	@CCControl(name = "gen mode")
	private CCTextureGenMode _cGenMode = CCTextureGenMode.OBJECT_LINEAR;

	@CCControl(name = "p1", min = 0, max = 0.01f)
	private float _cP1 = 0;
	@CCControl(name = "p2", min = 0, max = 0.01f)
	private float _cP2 = 0;
	@CCControl(name = "p3", min = 0, max = 0.01f)
	private float _cP3 = 0;
	@CCControl(name = "p4", min = 0, max = 10f)
	private float _P4 = 0;

	private class Box {
		private CCVector3f _myPosition;
		private float _myRadius;
		private float _myRotation;
		private float _myRotationSpeed;

		private Box() {
			_myPosition = CCVecMath.random3f(300);
			_myRadius = CCMath.random(100);
			_myRotationSpeed = CCMath.random(-10, 10);
		}

		public void update(final float theDeltaTime) {
			_myRotation += theDeltaTime * _myRotationSpeed;
		}

		public void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(_myPosition);
			g.rotateX(_myRotation);
			g.rotateY(_myRotation * 0.987f);
			g.box(_myRadius);
			g.popMatrix();
		}
	}

	private CCTexture2D _myTexture;
	private CCArcball _myArcball;

	private List<Box> _myBoxes = new ArrayList<Box>();

	@Override
	public void setup() {
		addControls("app", "app", this);

		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("textures/dice.jpg"));

		_myArcball = new CCArcball(this);

		for (int i = 0; i < 140; i++) {
			_myBoxes.add(new Box());
		}
	}

	float _myRotation = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myRotation += theDeltaTime * 10;
		for (Box myBox : _myBoxes) {
			myBox.update(theDeltaTime);
		}
	}

	@Override
	public void draw() {
		g.clear();

		_myArcball.draw(g);

		g.rotateX(_myRotation);
		g.rotateY(_myRotation * 0.987f);
		g.texGen(_cGenMode, _cP1, _cP2, _cP3, _P4);
		g.texture(_myTexture);
		for (Box myBox : _myBoxes) {
			myBox.draw(g);
		}
		g.noTexture();
		g.noTexGen();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureGenerationTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
