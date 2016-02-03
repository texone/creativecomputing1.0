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
package cc.creativecomputing.cv.openni;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNIRenderer {

	private CCVBOMesh _myMesh;
	
	private CCGLSLShader _myDepthmapShader;
	private CCGLSLShader _mySceneDepthmapShader;
	
	@CCControl(name = "depth lod", min = 0, max = 10)
	private float _cDepthLod = 0;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCOpenNIRenderer(int theWidth, int theHeight) {
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, theWidth * theHeight);
		for (float x = 0; x < _myWidth; x ++) {
			for (float y = 0; y < _myHeight; y ++) {
				_myMesh.addVertex((x + 0.5f) / theWidth,(y + 0.5f) / theHeight);	
			}
		}
		
		_myDepthmapShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/drawDepthMap_vert.glsl"),
			CCIOUtil.classPath(this, "shader/drawDepthMap_frag.glsl")
		);
		_myDepthmapShader.load();
		
		_mySceneDepthmapShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/drawDepthMap_vert.glsl"),
			CCIOUtil.classPath(this, "shader/drawSceneMap_frag.glsl")
		);
		_mySceneDepthmapShader.load();
	}
	
	public void drawDepthMesh(CCGraphics g, CCTexture2D theDepthTexture) {
		g.texture(0, theDepthTexture);
		_myDepthmapShader.start();
		_myDepthmapShader.uniform1i("depthData", 0);
		_myDepthmapShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_myDepthmapShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_myDepthmapShader.uniform2f("depthDimension", _myWidth, _myHeight);
		_myDepthmapShader.uniform1f("res", 640f / _myWidth);
		_myDepthmapShader.uniform1f("depthLod", _cDepthLod);
		_myMesh.draw(g);
		_myDepthmapShader.end();
		g.noTexture();
	}
	
	public void drawSceneMesh(CCGraphics g, CCTexture2D theDepthTexture, CCTexture2D theSceneTexture) {
		g.texture(0, theDepthTexture);
		g.texture(1, theSceneTexture);
		_mySceneDepthmapShader.start();
		_mySceneDepthmapShader.uniform1i("depthData", 0);
		_mySceneDepthmapShader.uniform1i("sceneData", 1);
		_mySceneDepthmapShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_mySceneDepthmapShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_mySceneDepthmapShader.uniform2f("depthDimension", _myWidth, _myHeight);
		_mySceneDepthmapShader.uniform1f("res", 640f / _myWidth);
		_mySceneDepthmapShader.uniform1f("depthLod", _cDepthLod);
		_myMesh.draw(g);
		_mySceneDepthmapShader.end();
		g.noTexture();
	}
}
