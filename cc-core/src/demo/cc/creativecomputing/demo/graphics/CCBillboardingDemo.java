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
package cc.creativecomputing.demo.graphics;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCBillboardingDemo extends CCApp {
	
	private class Node{
		CCVector3f vertex;
		float scale;
		
		public Node(final CCVector3f theVertex, final float theScale) {
			vertex = theVertex;
			scale = theScale;
		}
	}
	
	private List<Node> _myVertices = new ArrayList<Node>();

	public void setup() {
		buildTree(new Node(new CCVector3f(0,-200,0),7));
	}
	
	private void buildTree(Node theNode) {
		for(int i = 0; i < 3;i++) {
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
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		
		g.beginShape(CCDrawMode.QUADS);
		g.camera().rotateYaroundTarget(angle);
		CCVector3f myCameraDirection = CCVecMath.subtract(g.camera().target(), g.camera().position());
		myCameraDirection.normalize();
		
		CCVector3f side = myCameraDirection.cross(new CCVector3f(0,1,0));
		
		side.scale(1);
		
		for(int i = 0; i < _myVertices.size();i+=2) {
			Node node1 = _myVertices.get(i);
			CCVector3f vertex1 = node1.vertex.clone();
			vertex1.add(side);
			g.vertex(vertex1);
			vertex1.subtract(side.x * node1.scale, side.y * node1.scale, side.z * node1.scale);
			g.vertex(vertex1);

			Node node2 = _myVertices.get(i+1);
			CCVector3f vertex21 = node2.vertex.clone();
			vertex21.add(side);
			CCVector3f vertex22 = vertex21.clone();
			vertex22.subtract(side.x * node2.scale, side.y * node2.scale, side.z * node2.scale);
			g.vertex(vertex22);
			g.vertex(vertex21);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBillboardingDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
