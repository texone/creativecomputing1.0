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
package cc.creativecomputing.demo.graphics.shader.noise;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.util.CCFormatUtil;

public class CCGPUWorleyNoise3D extends CCApp {
	
	private CCGLSLShader _myWorleyShader;
	
	@CCControl(name = "jitter", min = 0, max = 1)
	private float _cJitter = 0;
	
	@CCControl(name = "scale", min = 0, max = 1)
	private float _cScale = 0;

	@Override
	public void setup() {
		_myWorleyShader = new CCGLSLShader(
			new String[] {
				CCIOUtil.classPath(this, "cellular_vertex.glsl")
			},
			new String[] {
				CCIOUtil.classPath(this, "worleynoise.glsl"),
				CCIOUtil.classPath(this, "cellular3D_fragment.glsl")
			}
		);
		_myWorleyShader.load();
		
		addControls("worley", "worley", this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myWorleyShader.start();
		_myWorleyShader.uniform1f("jitter", _cJitter);
		_myWorleyShader.uniform1f("scale", _cScale);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0f, 0f);
		g.vertex(-width / 2, -height / 2);
		g.textureCoords(0, 1f * _cScale, 0f);
		g.vertex( width / 2, -height / 2);
		g.textureCoords(0, 1f * _cScale, 1f * _cScale);
		g.vertex( width / 2,  height / 2);
		g.textureCoords(0, 0f, 1f * _cScale);
		g.vertex(-width / 2,  height / 2);
		g.endShape();
		_myWorleyShader.end();
	}
	
	int count = 0;
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch(theEvent.keyCode()) {
//		case VK_F:
//			_myContentManager.isActive(true);
//			_myNetworkVisual.isActive(true);
//			_myParticleLayer.isActive(true);
//			break;
//		case VK_V:
//			_myContentManager.isActive(false);
//			_myNetworkVisual.isActive(false);
//			_myParticleLayer.isActive(false);
//			break;
		case VK_R:
			CCScreenCapture.capture("export/worley3d/frame_"+CCFormatUtil.nf(count++, 5)+".png", width, height);
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUWorleyNoise3D.class);
		myManager.settings().size(1000, 1000);
		myManager.settings().append(args);
		myManager.start();
	}
}

