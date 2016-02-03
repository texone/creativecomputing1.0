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
package cc.creativecomputing.demo.input;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.input.CCInputButtonListener;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;
import cc.creativecomputing.input.CCInputStick;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCInputControlCamera extends CCApp {
	
	private class Node{
		CCVector3f vertex;
		float scale;
		
		public Node(final CCVector3f theVertex, final float theScale) {
			vertex = theVertex;
			scale = theScale;
		}
	}
	
	private List<Node> _myVertices = new ArrayList<Node>();
	
	private CCInputStick _myStick1;
	private CCInputStick _myStick2;

	public void setup() {
		
		for(int i = 0; i < 240;i++) {
			buildTree(new Node(new CCVector3f(CCMath.random(-800, 800),-100,CCMath.random(-800, 800)),CCMath.random(1,4)));
		}
		
		// init CCInput
		CCInputIO myInputIO = new CCInputIO(this);

		// print out devices to get your device name and see if the
		// devices are proper installed
		myInputIO.printDevices();

		// get the input device
		// you can do that by name or by id
		CCInputDevice myJoypad = myInputIO.device("Logitech RumblePad 2 USB");
		
		myJoypad.printDeviceInfo();

		// add a listener to the second button
		// to change color
		myJoypad.button(1).addListener(new CCInputButtonListener() {

			public void onRelease() {
				g.color(255);
			}

			public void onPress() {
				g.color(255, 0, 0);
			}
		});

		// get the first stick of the joypad for rotation
		_myStick1 = myJoypad.stick(0);
		_myStick1.multiplier(0.01f);
		_myStick1.power(2);

		// get the second stick for translation
		_myStick2 = myJoypad.stick(1);
		_myStick2.multiplier(5f);
		_myStick1.power(2);
	}
	
	private void buildTree(Node theNode) {
		int numberOfBranches = (int)CCMath.random(1,5);
		for(int i = 0; i < numberOfBranches;i++) {
			_myVertices.add(theNode);
			Node myNextNode = new Node(new CCVector3f(
					theNode.vertex.x + CCMath.random(-30, 40),
					theNode.vertex.y + CCMath.random(15,50),
					theNode.vertex.z + CCMath.random(-30, 40)), theNode.scale - 1
			) ;
			_myVertices.add(myNextNode);
			if(theNode.scale > 1)
				buildTree(myNextNode);
		}
	}
	
	float angle = 0.001f;
	
	public void update(final float theDeltaTime) {
//		angle += theDeltaTime;
	}

	public void draw() {
		g.clear();
		g.beginShape(CCDrawMode.QUADS);
		g.camera().rotateY(_myStick1.x());
		g.camera().rotateX(_myStick1.y());

		g.camera().moveX(_myStick2.x());
		g.camera().moveZ(_myStick2.y());
		
		CCVector3f myCameraDirection = CCVecMath.subtract(g.camera().target(), g.camera().position());
		myCameraDirection.normalize();
		
		CCVector3f side = myCameraDirection.cross(new CCVector3f(0,1,0));
		
		side.scale(2);
		
		for(int i = 0; i < _myVertices.size();i+=2) {
			Node node1 = _myVertices.get(i);
			CCVector3f vertex1 = node1.vertex.clone();
			vertex1.add(side);
			g.vertex(vertex1);
			vertex1.subtract(side.x * node1.scale, side.y * node1.scale, side.z * node1.scale);
			g.vertex(vertex1);

			Node node2 = _myVertices.get(i+1);
			CCVector3f vertex2 = node2.vertex.clone();
			vertex2.add(side);
			g.vertex(vertex2);
			vertex2.subtract(side.x * node2.scale, side.y * node2.scale, side.z * node2.scale);
			g.vertex(vertex2);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCInputControlCamera.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
