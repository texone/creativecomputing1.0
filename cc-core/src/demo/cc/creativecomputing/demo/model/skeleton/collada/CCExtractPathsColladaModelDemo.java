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
package cc.creativecomputing.demo.model.skeleton.collada;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaControllers;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaScenes;
import cc.creativecomputing.model.collada.CCColladaSkeletonProvider;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonSkin;

public class CCExtractPathsColladaModelDemo extends CCApp {

	private class CCTriangleStructure {
		private int[][] _myTriangleIndices;
		private int[][] _myLookUpStructure;

		public CCTriangleStructure(int theNumberOfIndices, IntBuffer theIndices) {
			_myTriangleIndices = new int[theIndices.capacity() / 3][3];
			_myLookUpStructure = new int[theNumberOfIndices][25];
			theIndices.rewind();

			int myIndex = 0;

			while (theIndices.hasRemaining()) {
				addIndex(theIndices.get(), 0, myIndex);
				addIndex(theIndices.get(), 1, myIndex);
				addIndex(theIndices.get(), 2, myIndex);
				myIndex++;
			}
			theIndices.rewind();
		}

		private void addIndex(int theIndex, int theSubIndex, int theTriangle) {
			_myLookUpStructure[theIndex][0]++;
			_myLookUpStructure[theIndex][_myLookUpStructure[theIndex][0]] = theTriangle;

			_myTriangleIndices[theTriangle][theSubIndex] = theIndex;
		}

		public int[] createPath(int theLength) {
			int[] myIndices = new int[theLength];
			int myTriangle = (int) CCMath.random(_myTriangleIndices.length);
			int mySubIndex = (int) CCMath.random(3);
			myIndices[0] = _myTriangleIndices[myTriangle][mySubIndex];

			for (int i = 1; i < theLength; i++) {
				int myIndex = myIndices[i - 1];
				int myNeighbors = _myLookUpStructure[myIndex][0];
				int myNeighbor = (int) CCMath.random(myNeighbors) + 1;
				int myTriangleIndex = _myLookUpStructure[myIndex][myNeighbor];
				for (int j = 0; j < 3; j++) {
					if (_myTriangleIndices[myTriangleIndex][j] != myIndex) {
						myIndices[i] = _myTriangleIndices[myTriangleIndex][j];
						break;
					}
				}
			}
			return myIndices;
		}
	}

	@CCControl(name = "time", min = 0, max = 2)
	private float _cTime = 0;

	private CCArcball _myArcball;

	private CCColladaSkeletonProvider _myColladaSkeletonProvider;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeleton _mySkeleton;

	private CCTriangleStructure _myTriangleStructure;

	private CCSkeletonSkin _myPathMesh;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		CCColladaLoader myColladaLoader = new CCColladaLoader("demo/model/collada/humanoid.dae");

		CCColladaScenes myScenes = myColladaLoader.scenes();
		CCColladaScene myScene = myScenes.element(0);
		CCColladaControllers myControllers = myColladaLoader.controllers();

		CCColladaSkinController mySkinController = myControllers.element(0).skin();

		_myColladaSkeletonProvider = new CCColladaSkeletonProvider(myColladaLoader.animations().animations(), mySkinController, myScene.node("bvh_import/Hips"));
		_mySkeleton = _myColladaSkeletonProvider.skeleton();

		_mySkeletonSkin = new CCColladaSkeletonSkin(mySkinController);

		_myTriangleStructure = new CCTriangleStructure(_mySkeletonSkin.mesh().vertices().capacity() / 3, _mySkeletonSkin.mesh().indices());
		_myPathMesh = buildMesh(20, 150);
	}

	public CCSkeletonSkin buildMesh(int thePaths, int thePathLength) {
		int theValues = thePaths * (2 * thePathLength - 2);
		FloatBuffer myPositions = FloatBuffer.allocate(theValues * 3);
		FloatBuffer myWeights = FloatBuffer.allocate(theValues * 4);
		FloatBuffer myWeightIndices = FloatBuffer.allocate(theValues * 4);

		for (int i = 0; i < thePaths; i++) {
			int[] myPath = _myTriangleStructure.createPath(thePathLength);
			for (int j = 1; j < myPath.length; j++) {
				int myIndex1 = myPath[j - 1];
				int myIndex2 = myPath[j];
				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex1 * 3 + 0));
				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex1 * 3 + 1));
				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex1 * 3 + 2));

				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex2 * 3 + 0));
				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex2 * 3 + 1));
				myPositions.put(_mySkeletonSkin.mesh().vertices().get(myIndex2 * 3 + 2));

				myWeights.put(_mySkeletonSkin.weights().get(myIndex1 * 4 + 0));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex1 * 4 + 1));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex1 * 4 + 2));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex1 * 4 + 3));

				myWeights.put(_mySkeletonSkin.weights().get(myIndex2 * 4 + 0));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex2 * 4 + 1));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex2 * 4 + 2));
				myWeights.put(_mySkeletonSkin.weights().get(myIndex2 * 4 + 3));

				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex1 * 4 + 0));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex1 * 4 + 1));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex1 * 4 + 2));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex1 * 4 + 3));

				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex2 * 4 + 0));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex2 * 4 + 1));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex2 * 4 + 2));
				myWeightIndices.put(_mySkeletonSkin.weightIndices().get(myIndex2 * 4 + 3));
			}
		}

		myPositions.rewind();
		myWeights.rewind();
		myWeightIndices.rewind();

		CCSkeletonSkin mySkin = new CCSkeletonSkin(CCDrawMode.LINES);
		mySkin.mesh().vertices(myPositions);
		mySkin.weights(myWeights);
		mySkin.weightIndices(myWeightIndices);
		return mySkin;
	}

	@Override
	public void update(float theDeltaTime) {
		_myColladaSkeletonProvider.time(_cTime);
	}

	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();

		_myArcball.draw(g);

		g.color(125, 0, 0);
		g.strokeWeight(1);
		g.line(0, 0, 0, width, 0, 0);
		g.color(0, 125, 0);
		g.line(0, 0, 0, 0, 0, -width);
		g.color(0, 0, 125);
		g.line(0, 0, 0, 0, -height, 0);

		g.color(255);
		g.polygonMode(CCPolygonMode.LINE);
		_myPathMesh.draw(g, _mySkeleton);
		g.polygonMode(CCPolygonMode.FILL);
		g.clearDepthBuffer();
		g.color(255, 0, 0);
		_mySkeleton.draw(g);
		_mySkeleton.drawOrientations(g, 20);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCExtractPathsColladaModelDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
