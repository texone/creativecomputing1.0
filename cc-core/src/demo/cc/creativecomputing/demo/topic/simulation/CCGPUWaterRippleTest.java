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
package cc.creativecomputing.demo.topic.simulation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCGPUWaterRippleTest extends CCApp {

	@CCControl(name = "damping", min = 0.9f, max = 0.998f)
	public float _cSimulationDamping;

	@CCControl(name = "splash radius", min = 0, max = 20)
	public float _cSplashRadius;

	@CCControl(name = "splash amplitude", min = 0, max = 0.2f)
	public float _cSplashAmplitude;

	@CCControl(name = "inner edges strength", min = 0, max = 1f)
	public float _cWaveInnerEdgesStrength;

	@CCControl(name = "normal height scale", min = 0, max = 500f)
	public float _cNormalHeightScale;

	@CCControl(name = "refraction", min = 0, max = 500f)
	public float _cRefraction;

	@CCControl(name = "blend", min = 0, max = 0.05f)
	public float _cBlend;

	@CCControl(name = "blend2", min = 0, max = 0.05f)
	public float _cBlend2;

	private CCGPUWaterRipple _myWaterRipple;

	// private CCTexture _myRippleTexone;

	public void setup() {
		addControls("app", "app", this);
		hideControls();

		// _myRippleTexone = CCTextureIO.newTexture("effects/ripple_tex.png");
		_myWaterRipple = new CCGPUWaterRipple(g, null, width, height);
		_myWaterRipple.backgroundTexture(new CCTexture2D(CCTextureIO.newTextureData("demo/textures/kristall-7.png"), CCTextureTarget.TEXTURE_RECT));
		g.imageMode(CCShapeMode.CENTER);
	}

	float _myAngle = 0;

	public void update(final float theDeltaTime) {
		if (mousePressed)
			_myWaterRipple.addSplash(mouseX, mouseY, 2, 10f);

		// define areas where water animation will be masked
		_myWaterRipple.beginDrawMask();
		g.color(0);
		float x = width / 2;
		float y = height / 2;
		float r = 300;

		// g.line(x-r, y-r, x+r, y-r);
		// g.line(x+r, y-r, x+r, y+r);
		// g.line(x+r, y+r, x-r, y+r);
		// g.line(x-r, y+r, x-r, y-r);

		_myWaterRipple.endDrawMask();

		// define areas that cause water ripple
		_myWaterRipple.beginDrawActiveArea();
		g.clearColor(0);
		g.clear();
		g.pushMatrix();
		g.translate(width / 2 + 50, height / 2 + 50);
		g.rotate(_myAngle);
		_myAngle += theDeltaTime * 30;
		g.color(_cBlend);
		x = 0;// _myWidth/2 + 50;
		y = 0;// _myHeight/2 + 50;
		r = 100;

		g.line(x - r, y - r, x + r, y - r);
		g.line(x + r, y - r, x + r, y + r);
		g.line(x + r, y + r, x - r, y + r);
		g.line(x - r, y + r, x - r, y - r);
		g.triangle(200, 200, 250, 300, 300, 150);

		g.popMatrix();
		// g.clearColor(0);
		// g.clear();
		g.color(_cBlend2);
		g.rect(200, 200, 300, 300);
		_myWaterRipple.endDrawActiveArea();
		g.clearColor(0);
		_myWaterRipple.damping(_cSimulationDamping);
		_myWaterRipple.normalHeightScale(_cNormalHeightScale);
		_myWaterRipple.refraction(_cRefraction);
		_myWaterRipple.waveInnerEdgeStrength(_cWaveInnerEdgesStrength);
		_myWaterRipple.update(theDeltaTime);
	}

	public void draw() {
		g.clear();
		_myWaterRipple.draw(g);
	}

	@Override
	public void mousePressed(final CCMouseEvent theMouseEvent) {
		// _myWaterRipple.addSplash(mouseX, mouseY, 2, 0.25f);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUWaterRippleTest.class);
		myManager.settings().size(1024, 1024);
		myManager.start();
	}
}
