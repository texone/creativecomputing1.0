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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;

public class CCGLSL01Demo extends CCApp {
	
	private CCGLSLShader _myShader;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this,"vertex.glsl"),
			CCIOUtil.classPath(this,"fragment01.glsl")
		);
		_myShader.load();
		
		CCGraphics.debug();
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

		g.clear();
		g.color(0,255,0);
		_myShader.start();
		g.rect(0,0,100,100);
		_myShader.end();
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_R:
			_myShader.reload();
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGLSL01Demo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

