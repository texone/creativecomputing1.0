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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCMipMapDistanceField extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCGLSLShader _myDistanceShader;
	
	private CCArcball _myArcball;
	
	private CCTexture2D _myHeightMap;
	
	private CCVBOMesh _myMesh;
	
	@CCControl(name = "lod", min = 0, max = 10)
	private int _cLod = 1;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;
	
	@CCControl(name = "alpha grid", min = 0, max = 1)
	private float _cAlphaGrid = 1;
	
	private int _myMaxLod = 0;

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/mipmapfrag_vp.glsl"),
			CCIOUtil.classPath(this, "shader/mipmapfrag_fp.glsl")
		);
		_myShader.load();
		

		_myDistanceShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/mipmap_dist_vp.glsl"),
			CCIOUtil.classPath(this, "shader/mipmap_dist_fp.glsl")
		);
		_myDistanceShader.load();
		
		_myHeightMap = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/dice.jpg"));
		_myHeightMap.generateMipmaps(true);
		_myHeightMap.textureFilter(CCTextureFilter.LINEAR);
		_myHeightMap.textureMipmapFilter(CCTextureMipmapFilter.NEAREST);
		_myHeightMap.wrap(CCTextureWrap.CLAMP_TO_BORDER);
		_myHeightMap.textureBorderColor(CCColor.BLACK);
		
		_myMaxLod = (int)CCMath.log2(_myHeightMap.width());
		System.out.println("myLodLevel:" + _myMaxLod);
		int res = 80;
		_myMesh = new CCVBOMesh(CCDrawMode.LINES, res * res * 2);
		for(int x = 0; x < res;x++) {
			for(int y = 0; y < res;y++) {
				float myX = CCMath.map(x, 0, res - 1, -256, 256);
				float myY = CCMath.map(y, 0, res - 1, -256, 256);
				_myMesh.addVertex(myX, myY);
				_myMesh.addTextureCoords(0, (x + 0.5F) / res, (y + 0.5F) / res, 0);
				_myMesh.addVertex(myX, myY);
				_myMesh.addTextureCoords(0, (x + 0.5F) / res, (y + 0.5F) / res, 1);
			}
		}
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {}
	
	private void drawSubdivisions(int theLOD){
		float mySize = CCMath.pow(2, theLOD);
		for(float x = -_myHeightMap.width() / 2; x <= _myHeightMap.width() / 2;x+= mySize){
			g.line(x, -_myHeightMap.height() / 2,x,_myHeightMap.height() / 2);
		}
		
		for(float y = -_myHeightMap.height() / 2; y <= _myHeightMap.height() / 2;y+= mySize){
			g.line(-_myHeightMap.width() / 2, y, _myHeightMap.width() / 2, y);
		}
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		_myArcball.draw(g);
		g.noDepthTest();
		_myShader.start();
		g.texture(0, _myHeightMap);
		_myShader.uniform1i("texture", 0);
		_myShader.uniform1f("lod", _cLod);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0f,0f);
		g.vertex(-256,-256);
		g.textureCoords(0, 1f,0f);
		g.vertex( 256,-256);
		g.textureCoords(0, 1f,1f);
		g.vertex( 256, 256);
		g.textureCoords(0, 0f,1f);
		g.vertex(-256, 256);
		g.endShape();
		g.noTexture();
		_myShader.end();
		
		g.color(1f,_cAlphaGrid);
		drawSubdivisions(_cLod);
		
		g.blend(CCBlendMode.ADD);
		_myDistanceShader.start();
		g.texture(0, _myHeightMap);
		_myDistanceShader.uniform1i("texture", 0);
		_myDistanceShader.uniform1i("lod", _cLod);
		_myDistanceShader.uniform1f("texSize", _myHeightMap.width());
		_myDistanceShader.uniform1f("alpha", _cAlpha);
		_myMesh.draw(g);
//		float x = mouseX;
//		float y = mouseY;
//		float texX = mouseX / (float)width;
//		float texY = mouseY / (float)height;
//		g.beginShape(CCDrawMode.LINES);
//		g.vertex(x, y);
//		g.textureCoords(0, texX, texY, 0);
//		g.vertex(x, y);
//		g.textureCoords(0, texX, texY, 1);
//		g.endShape();
		g.noTexture();
		_myDistanceShader.end();
		g.blend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMipMapDistanceField.class);
		myManager.settings().size(900, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

