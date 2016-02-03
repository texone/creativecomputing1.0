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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;

public class CCGPUWorleyNoise2x2x2 extends CCApp {
	
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
				CCIOUtil.classPath(this, "cellular2x2x2_fragment.glsl")
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
		g.textureCoords(0, 1f, 0f);
		g.vertex( width / 2, -height / 2);
		g.textureCoords(0, 1f, 1f);
		g.vertex( width / 2,  height / 2);
		g.textureCoords(0, 0f, 1f);
		g.vertex(-width / 2,  height / 2);
		g.endShape();
		_myWorleyShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUWorleyNoise2x2x2.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

