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
package cc.creativecomputing.demo.graphics.shader.fx;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.fx.refraction.CCGLSLRefractionShader;
import cc.creativecomputing.graphics.texture.CCTextureCubeMap;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.util.CCArcball;

public class CCRefractionDemo extends CCApp {
	
	private CCTextureData _myTextureData;
	private CCTextureCubeMap _myCubeMap;
	private CCArcball _myArcball;
	private CCGLSLRefractionShader _myRefractionShader;

	@Override
	public void setup() {
		_myTextureData = CCTextureIO.loadCubeMapData(
			"demo/textures/cubemap/cube_posx.png",
			"demo/textures/cubemap/cube_negx.png",
			"demo/textures/cubemap/cube_posy.png",
			"demo/textures/cubemap/cube_negy.png",
			"demo/textures/cubemap/cube_posz.png",
			"demo/textures/cubemap/cube_negz.png"	
		);
		_myCubeMap = new CCTextureCubeMap(_myTextureData);
			
		_myArcball = new CCArcball(this);
		
		_myRefractionShader = new CCGLSLRefractionShader(g, _myCubeMap);
		
		addControls("app", "refraction", _myRefractionShader);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myRefractionShader.start();
		g.box(200);
		_myRefractionShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRefractionDemo.class);
		myManager.settings().size(900, 900);
		myManager.start();
	}
}

