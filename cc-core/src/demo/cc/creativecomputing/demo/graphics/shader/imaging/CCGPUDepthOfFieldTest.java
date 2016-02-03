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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.shader.imaging.CCGPUDepthOfField;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

/**
 * @author christianriekoff
 * 
 */
public class CCGPUDepthOfFieldTest extends CCApp {

	

	public static float DEFAULT_DISTANCE = 9.0f;
	public static float DEFAULT_RANGE = 6.0f;

	int i;
	int interval = 1;
	float _myMoveZ = 0.0f, _myRotateY = 0.0f;
	boolean _myIsMoving = false, _myIsRotating = true, bg = true;
	float focalDistance = DEFAULT_DISTANCE;
	float focalRange = DEFAULT_RANGE;
	
	@CCControl(name = "focalDistance", min = 0, max = 1000)
	private static float _cFocalDistance = 0;
	@CCControl(name = "focalRange", min = -0, max = 500)
	private static float _cFocalRange = 0;

	private CCTexture2D _myTexture1;
	
	private CCGPUDepthOfField _myDepthOfField;
	
	private CCArcball _myArcball;
	
	private CCMesh _myMesh;
	

	public void setup() {
		addControls("app", "dof", this);
		_myUI.drawBackground(false);

		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/tex1.png"));
		
		_myDepthOfField = new CCGPUDepthOfField(g, width, height);
		
		_myArcball = new CCArcball(this);
		
		_myMesh = new CCMesh(CCDrawMode.POINTS);
		
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for(int i = 0; i < 1000;i++) {
			CCVector3f myVector = CCVecMath.random3f(CCMath.random(200));
//			myVector.z(-2000 + 4f * i);
			myVertices.add(myVector);
		}
		_myMesh.vertices(myVertices);
		g.pointSize(25);
	}
	
	public void update(final float theDeltaTime) {
		if (_myIsMoving)
			_myMoveZ += theDeltaTime * interval;
		if (_myIsRotating)
			_myRotateY += interval * theDeltaTime * 10;
	}

	public void draw() {
		_myDepthOfField.begin();
		_myDepthOfField.focalDistance(_cFocalDistance);
		_myDepthOfField.focalRange(_cFocalRange);
		
		g.clear();
		g.clearColor(0);
		_myArcball.draw(g);
		g.translate(0, -80, 100);
		g.rotateX(15);
		g.noBlend();

		
		
		/* Brick room */
//		g.texture(_myTexture0);
//		if (bg) {
//			DrawCube(640, true);
//		}
//		g.noTexture();

		/* Rotating cubes */
		g.texture(_myTexture1);
		for (i = 0; i < 6; i++) {
			g.pushMatrix();
			g.translate(0.0f, 0.0f, 130.0f * CCMath.sin(_myMoveZ)+150);
			g.translate(-200.0f, 0.0f, 0.0f);
			g.translate(i * 100.0f, 0.0f, i * (-100.0f));
			g.rotate(_myRotateY, 0.0f, 1.0f, 0.0f);
			DrawCube(100, false);
//			_myMesh.draw(g);
//			g.rect(0,0,100,100);
			g.popMatrix();
		}
		g.noTexture();
		

		_myDepthOfField.end();
	}

	void DrawCube(final float theSize, boolean open) {
		g.beginShape(CCDrawMode.QUADS);
		if (!open) {
			/* Front Face */
			g.textureCoords(1.0f, 1.0f);
			g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 1.0f);
			g.vertex(theSize / 2, -theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 0.0f);
			g.vertex(theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(1.0f, 0.0f);
			g.vertex(-theSize / 2, theSize / 2, theSize / 2);
		}
		/* Back Face */
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		if (!open) {
			/* Top Face */
			g.textureCoords(1.0f, 0.0f);
			g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
			g.textureCoords(1.0f, 1.0f);
			g.vertex(-theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 1.0f);
			g.vertex(theSize / 2, theSize / 2, theSize / 2);
			g.textureCoords(0.0f, 0.0f);
			g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		}
		/* Bottom Face */
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, theSize / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
		// Right face
		g.textureCoords(0.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, -theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theSize / 2, theSize / 2, theSize / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theSize / 2, -theSize / 2, theSize / 2);
		// Left Face
		g.textureCoords(1.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, -theSize / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-theSize / 2, -theSize / 2, theSize / 2);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, theSize / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(-theSize / 2, theSize / 2, -theSize / 2);
		g.endShape();
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_P:
			_myIsMoving = !_myIsMoving;
			break;
		case VK_R:
			_myIsRotating = !_myIsRotating;
			break;
		case VK_B:
			bg = !bg;
			break;
		case VK_F:
			System.out.printf("focalDistance = %f\n", focalDistance);
			System.out.printf("focalRange = %f\n", focalRange);
			break;
		case VK_Y:
			focalDistance -= 5f;
			break;
		case VK_Q:
			focalDistance = DEFAULT_DISTANCE;
			break;
		case VK_A:
			focalDistance += 5f;
			break;
		case VK_X:
			focalRange -= 5f;
			break;
		case VK_W:
			focalRange = DEFAULT_RANGE;
			break;
		case VK_S:
			focalRange += 5f;
			break;
		case VK_E:
			focalDistance = DEFAULT_DISTANCE;
			focalRange = DEFAULT_RANGE;
			_myMoveZ = 0.0f;
			_myRotateY = 0.0f;
			break;
		case VK_1:
			focalDistance = 9.0f;
			break;
		case VK_2:
			focalDistance = 12.5f;
			break;
		case VK_3:
			focalDistance = 17.5f;
			break;
		case VK_4:
			focalDistance = 21.0f;
			break;
		case VK_5:
			focalDistance = 25.0f;
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUDepthOfFieldTest.class);
		myManager.settings().size(640, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
