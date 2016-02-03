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
package cc.creativecomputing.demo.graphics.shader.tutorial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.CCGLSLShader;

public class CCShaderTutorial01 extends CCApp {
	
	private CCGLSLShader _myShader;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			"demo/gpu/tutorial/tutorial01_vert.glsl",
			"demo/gpu/tutorial/tutorial01_frag.glsl"
		);
		_myShader.load();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myShader.start();
		g.beginShape();
		g.vertex(-50, -50);
		g.vertex( 50, -50);
		g.vertex( 50,  50);
		g.vertex(-50,  50);
		g.endShape();
		_myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderTutorial01.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

