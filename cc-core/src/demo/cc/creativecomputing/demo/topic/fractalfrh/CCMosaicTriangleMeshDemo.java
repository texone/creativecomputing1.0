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
package cc.creativecomputing.demo.topic.fractalfrh;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.d.CCTriangle2d;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

public class CCMosaicTriangleMeshDemo extends CCApp {
	
	

	private CCArcball _myArcball;
	@CCControl(name = "mosaic")
	private CCMosaicTriangleMesh _myParticleTriangleMesh;
	
	@CCControl (name = "texture", min = 0, max = 0)
	private int _cTextureIndex = 0;
	
	@CCControl(name = "scroll", min = 0, max = 1)
	private float _cScroll = 0;
	
	private List<CCTexture2D> _myTextures = new ArrayList<CCTexture2D>();
	
	private static int TEX_WIDTH = 4400;
	private static int TEX_HEIGHT = 380;
	
	@CCControl(name = "view all")
	private boolean _cViewAll = true;

	@Override
	public void setup() {

		_myUI.drawBackground(false);
		
		_myArcball = new CCArcball(this);
		g.clearColor(0.2f, 0.2f, 0.2f);
		
		int myColumns = 10;
		
		float myEdgelength = TEX_WIDTH / (float)myColumns;
		float myEdgeScale = myEdgelength / CCMath.SQRT3;
		float myTriangleHeight = myEdgelength / 2 * CCMath.SQRT3;
	
		float _myShortCenter = myEdgelength / 2 * CCMath.tan(CCMath.radians(30));
		float myLongCenter = myTriangleHeight - _myShortCenter;
		
		List<CCTriangle2d> myTriangles = new ArrayList<CCTriangle2d>();
		
		int myRows = 1;//CCMath.ceil(TEX_HEIGHT / myTriangleHeight);
		
		for(int myColumn = -1; myColumn < 10; myColumn++) {
			for(int myRow = 0; myRow < myRows;myRow++) {
				CCVector2d myOrigin0;
				CCVector2d myOrigin1;
				CCVector2d myOrigin2;

				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians( -30)), CCMath.sin(CCMath.radians( -30))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-150)), CCMath.sin(CCMath.radians(-150))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians(-270)), CCMath.sin(CCMath.radians(-270))).scale(myEdgeScale);
					
				CCVector2d myTranslation = new CCVector2d(
					(myColumn + 0.5f) * myEdgelength,
					myRow * myTriangleHeight + _myShortCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
				
				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians(-210)), CCMath.sin(CCMath.radians(-210))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-330)), CCMath.sin(CCMath.radians(-330))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians( -90)), CCMath.sin(CCMath.radians( -90))).scale(myEdgeScale);
					
				myTranslation = new CCVector2d(
					(myColumn + 1) * myEdgelength,
					myRow * myTriangleHeight + myLongCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
			}
		}
		
		_myParticleTriangleMesh = new CCMosaicTriangleMesh(g, myTriangles, 6);
		_myParticleTriangleMesh.textureSize(TEX_WIDTH, TEX_HEIGHT);
//		for(CCTextureData myData:CCTextureIO.newTextureDatas("demo/textures/")){
//			_myTextures.add(new CCTexture2D(myData));
//		}
			
		addControls("mosaic", "demo",0, this);
		
		_myParticleTriangleMesh.texture0(new CCTexture2D(CCTextureIO.newTextureData(CCIOUtil.classPath(this, "storyboard/07.jpg"))));
		_myParticleTriangleMesh.texture1(new CCTexture2D(CCTextureIO.newTextureData(CCIOUtil.classPath(this, "storyboard/08.jpg"))));
//		_myParticleTriangleMesh.texture1(_myTextures.get(_cTextureIndex + 1));
		
		fixUpdateTime(1/30f);
	}

	@Override
	public void update(final float theDeltaTime) {
//		if(_cTextureIndex < _myTextures.size() - 1){
//			_myParticleTriangleMesh.texture0(_myTextures.get(_cTextureIndex));
//			_myParticleTriangleMesh.texture1(_myTextures.get(_cTextureIndex + 1));
//		}
		_myParticleTriangleMesh.update(theDeltaTime);
	}
	

	int i = 0;
	int x = 0;
	
	@Override
	public void draw() {
		g.clearColor(40, 40, 40);
		g.clear();
		
		if(_cCapture){
			CCLog.info(x + ":" + width+ ":" + TEX_WIDTH);
			g.clear();
			g.pushMatrix();
			g.translate(-width/2 - x, -height / 2);
//			g.rect(0,0,TEX_WIDTH,TEX_HEIGHT );
			g.blend();
			_myParticleTriangleMesh.draw(g);
			g.popMatrix();
			CCScreenCapture.capture("export/masken/trixel_" + _myTrixel+"_" + i++ + "_" + x + ".png", width, TEX_HEIGHT);
			
			if(x > TEX_WIDTH){
				_cCapture = false;
				x = 0;
				i = 0;
				_myTrixel++;
			}else{
				i++;
				x+=width;
			}
			return;
		}
		
		
//		_myArcball.draw(g);
				
		g.color(255,0,0);
			
	
		if(_cViewAll){
			g.pushMatrix();
			g.scale(width/(float)TEX_WIDTH);
			g.translate(-TEX_WIDTH/2, -TEX_HEIGHT/2);
//			g.rect(0,0,TEX_WIDTH,TEX_HEIGHT );
			g.blend();
			_myParticleTriangleMesh.draw(g);
			g.popMatrix();
		}else{
			g.pushMatrix();
			g.translate(-width/2 - (TEX_WIDTH - width) * _cScroll, -TEX_HEIGHT/2);
//			g.rect(0,0,TEX_WIDTH,TEX_HEIGHT );
			g.blend();
			_myParticleTriangleMesh.draw(g);
			g.popMatrix();
		}
		
		
		
		g.color(255);
//		g.image(_myTriangleManager.forceField().texture(), -_myVisual.heightMap().width()/2,-_myVisual.heightMap().height()/2);
//		g.image(_myParticleTriangleMesh.forceBlendTexture(),0,0);
		g.blend();
//		CCScreenCapture.capture("export/disney/fract"+CCFormatUtil.nf(frameCount, 4)+".png",width,height);
//		g.image(_myTextureVisual.renderTexture(), 0,0);
	}
	
	private boolean _cCapture = false;
	
	int _myTrixel = 0;
	
	public void keyPressed(CCKeyEvent theKeyEvent){
		super.keyPressed(theKeyEvent);
		
		switch(theKeyEvent.keyCode()){
		case VK_R:
			_cCapture = true;
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMosaicTriangleMeshDemo.class);
		myManager.settings().size(1920, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().fov(20);
		myManager.settings().alwaysOnTop(true);
		myManager.start();
	}
}