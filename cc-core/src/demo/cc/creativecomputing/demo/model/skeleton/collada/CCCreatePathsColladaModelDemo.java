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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.geometry.CCTriangleMesh;
import cc.creativecomputing.geometry.CCTriangleMesh.CCTriangleMeshIntersectionData;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.spline.CCLinearSpline;
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

public class CCCreatePathsColladaModelDemo extends CCApp {

	private class CCSkeletonCurve {

		private CCLinearSpline _mySpline;

		private List<List<CCTriangleMeshIntersectionData>> _myIntersections = new ArrayList<List<CCTriangleMeshIntersectionData>>();

		private int _myNumberOfVertices;
		private List<Integer> _myIndices = new ArrayList<Integer>();

		private CCSkeletonCurve(String... theJoints) {
			_mySpline = new CCLinearSpline(false);

			if (theJoints.length > 2) {
				_mySpline.addControlPoints(CCVecMath.subtract(_mySkeleton.joint(theJoints[0]).position(), _mySkeleton.joint(theJoints[1]).position()).add(
						_mySkeleton.joint(theJoints[0]).position()));
			}

			for (String myJoint : theJoints) {
				_mySpline.addControlPoints(_mySkeleton.joint(myJoint).position());
			}

			if (theJoints.length > 2) {
				_mySpline.addControlPoints(
					CCVecMath.subtract(
						_mySkeleton.joint(theJoints[theJoints.length - 1]).position(), 
						_mySkeleton.joint(theJoints[theJoints.length - 2]).position()).scale(2).add(_mySkeleton.joint(theJoints[theJoints.length - 1]).position()));
			}

			_mySpline.endEditSpline();

			calcPoints();
		}

		private List<CCTriangleMeshIntersectionData> rekursiveCurve(double theStart, double theEnd, List<CCTriangleMeshIntersectionData> thePoints, float theFreq, float thePhase,
				int depth) {
			double myStep = (theEnd - theStart) / 200;
			for (double myBlend = theStart; myBlend <= theEnd; myBlend += myStep) {
				CCVector3f myPoint = _mySpline.interpolate((float) myBlend);
				CCVector3f myPoint2 = _mySpline.interpolate((float) (myBlend + myStep));
				CCVector3f myPoint3 = new CCVector3f(myPoint).add(0, 0, 1);

				CCVector3f myNormal = CCVecMath.normal(myPoint, myPoint2, myPoint3);
				CCVector3f myOuterPoint = myNormal.clone().scale(20);
				CCVector3f myDirection = CCVecMath.subtract(myPoint2, myPoint).normalize();

				CCMatrix4f myMatrix = new CCMatrix4f();
				myMatrix.rotate((float) myBlend * CCMath.TWO_PI * theFreq + thePhase, myDirection);

				myMatrix.transform(myOuterPoint);

				CCRay3f myRay = new CCRay3f(myPoint, myOuterPoint.normalize());
				List<CCTriangleMeshIntersectionData> myIntersections = _myTriangleMesh.intersect(myRay);

				if (myIntersections.size() <= 0)
					continue;

				CCTriangleMeshIntersectionData myIntersection = myIntersections.get(0);

				if (myIntersection.t > 30) {
					_myIntersections.add(thePoints);
					thePoints = new ArrayList<CCTriangleMeshIntersectionData>();
					continue;
				}

				if (thePoints.size() > 1) {
					_myIndices.add(_myNumberOfVertices - 1);
					_myIndices.add(_myNumberOfVertices);
				}
				_myNumberOfVertices++;
				thePoints.add(myIntersection);

			}
			return thePoints;
		}

		private void calcPoints() {

			for (int j = 0; j < 5; j++) {
				List<CCTriangleMeshIntersectionData> myPoints = new ArrayList<CCTriangleMeshIntersectionData>();
				float myFreq = CCMath.random(3, 5);
				float myPhase = CCMath.random(CCMath.TWO_PI);
				_myIntersections.add(rekursiveCurve(0, 1, myPoints, myFreq, myPhase, 1));
			}
		}

		public int numberOfVertices() {
			return _myNumberOfVertices;
		}

		public List<Integer> indices() {
			return _myIndices;
		}

		public List<List<CCTriangleMeshIntersectionData>> intersections() {
			return _myIntersections;
		}
	}

	@CCControl(name = "mesh alpha", min = 0, max = 1)
	private float _cMeshAlpha = 0;

	@CCControl(name = "curves alpha", min = 0, max = 1)
	private float _cCurvesAlpha = 0;

	@CCControl(name = "time", min = 0, max = 2)
	private float _cTime = 0;

	private List<CCSkeletonCurve> _myCurves = new ArrayList<CCSkeletonCurve>();

	private CCArcball _myArcball;

	private CCTriangleMesh _myTriangleMesh;

	private CCColladaSkeletonProvider _myColladaSkeletonProvider;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeleton _mySkeleton;

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

		_myTriangleMesh = new CCTriangleMesh(_mySkeletonSkin.mesh());

		// _myCurves.add(new CCSkeletonCurve("RightHand", "RightForeArm",
		// "RightArm", "RightShoulder", "Neck", "Head"));
		_myCurves.add(new CCSkeletonCurve("RightFoot", "RightLeg", "RightUpLeg", "Spine", "Neck", "Head"));
		// _myCurves.add(new CCSkeletonCurve("LeftHand", "LeftForeArm",
		// "LeftArm", "LeftShoulder", "Neck", "Head"));
		// _mySkeletonMesh.skeleton().joint(theJointID);

		_myPathMesh = buildSkin();
	}

	private class CCTriangleWeightInfo {
		private int weightIndex;
		private float weight0;
		private float weight1;
		private float weight2;

		private CCTriangleWeightInfo(int theWeightIndex) {
			weightIndex = theWeightIndex;
			weight0 = 0;
			weight1 = 0;
			weight2 = 0;
		}

		private void setWeight(int theIndex, float theWeight) {
			switch (theIndex) {
			case 0:
				weight0 = theWeight;
				break;
			case 1:
				weight1 = theWeight;
				break;
			case 2:
				weight2 = theWeight;
				break;
			}
		}

	}

	private class CCTriangleWeightIndexPair implements Comparable<CCTriangleWeightIndexPair> {

		private int weightIndex;
		private float weight;

		private CCTriangleWeightIndexPair(int theWeightIndex, float theWeight) {
			weightIndex = theWeightIndex;
			weight = theWeight;
		}

		@Override
		public int compareTo(CCTriangleWeightIndexPair theO) {
			if (weight < theO.weight)
				return -1;
			return 1;
		}
	}

	private class CCWeightMatcher {
		private Map<Integer, CCTriangleWeightInfo> _myWeightMap = new HashMap<Integer, CCTriangleWeightInfo>();

		private void add(CCVector4f theWeightIndices, CCVector4f theWeights, int thePointIndex) {
			add((int) theWeightIndices.x, theWeights.x, thePointIndex);
			add((int) theWeightIndices.y, theWeights.y, thePointIndex);
			add((int) theWeightIndices.z, theWeights.z, thePointIndex);
			add((int) theWeightIndices.w, theWeights.w, thePointIndex);
		}

		private void add(int theWeightIndex, float theWeight, int thePointIndex) {
			if (theWeight <= 0)
				return;
			if (!_myWeightMap.containsKey(theWeightIndex)) {
				_myWeightMap.put(theWeightIndex, new CCTriangleWeightInfo(theWeightIndex));
			}
			CCTriangleWeightInfo myInfo = _myWeightMap.get(theWeightIndex);
			myInfo.setWeight(thePointIndex, theWeight);
		}

		private CCVector4f[] match(CCTriangleMeshIntersectionData theIntersectionData) {
			CCVector4f myWeightIndexData = new CCVector4f();
			CCVector4f myWeightData = new CCVector4f();

			List<CCTriangleWeightIndexPair> myWeightIndexPairs = new ArrayList<CCTriangleWeightIndexPair>();

			for (CCTriangleWeightInfo myWeightInfo : _myWeightMap.values()) {
				myWeightIndexPairs.add(new CCTriangleWeightIndexPair(myWeightInfo.weightIndex, CCMath.blend(theIntersectionData.u, theIntersectionData.v, myWeightInfo.weight0,
						myWeightInfo.weight1, myWeightInfo.weight2)));
			}

			Collections.sort(myWeightIndexPairs);
			for (int i = 0; i < myWeightIndexPairs.size() && i < 4; i++) {
				CCTriangleWeightIndexPair myPair = myWeightIndexPairs.get(i);
				myWeightIndexData.set(i, myPair.weightIndex);
				myWeightData.set(i, myPair.weight);
			}

			return new CCVector4f[] { myWeightData, myWeightIndexData };
		}
	}

	private CCVector4f[] calcWeightInfos(CCTriangleMeshIntersectionData theIntersection) {
		CCVector4f myWeightIndex0 = _myTriangleMesh.texCoord4f(theIntersection.index0(), 2);
		CCVector4f myWeightIndex1 = _myTriangleMesh.texCoord4f(theIntersection.index1(), 2);
		CCVector4f myWeightIndex2 = _myTriangleMesh.texCoord4f(theIntersection.index2(), 2);

		CCVector4f myWeight0 = _myTriangleMesh.texCoord4f(theIntersection.index0(), 1);
		CCVector4f myWeight1 = _myTriangleMesh.texCoord4f(theIntersection.index1(), 1);
		CCVector4f myWeight2 = _myTriangleMesh.texCoord4f(theIntersection.index2(), 1);

		CCWeightMatcher myWeightMatcher = new CCWeightMatcher();
		myWeightMatcher.add(myWeightIndex0, myWeight0, 0);
		myWeightMatcher.add(myWeightIndex1, myWeight1, 1);
		myWeightMatcher.add(myWeightIndex2, myWeight2, 2);
		return myWeightMatcher.match(theIntersection);
	}

	public CCSkeletonSkin buildSkin() {

		int myNumberOfVertices = 0;
		List<Integer> myIndices = new ArrayList<Integer>();

		for (CCSkeletonCurve myCurve : _myCurves) {
			for (int myIndex : myCurve.indices()) {
				myIndices.add(myIndex + myNumberOfVertices);
			}
			myNumberOfVertices += myCurve.numberOfVertices();
		}

		FloatBuffer myPositions = FloatBuffer.allocate(myNumberOfVertices * 3);
		FloatBuffer myWeights = FloatBuffer.allocate(myNumberOfVertices * 4);
		FloatBuffer myWeightIndices = FloatBuffer.allocate(myNumberOfVertices * 4);

		for (CCSkeletonCurve myCurve : _myCurves) {
			for (List<CCTriangleMeshIntersectionData> myIntersections : myCurve.intersections()) {
				for (CCTriangleMeshIntersectionData myIntersection : myIntersections) {
					myPositions.put(myIntersection.position().x);
					myPositions.put(myIntersection.position().y);
					myPositions.put(myIntersection.position().z);

					CCVector4f[] myWeightInfos = calcWeightInfos(myIntersection);
					myWeights.put(myWeightInfos[0].x);
					myWeights.put(myWeightInfos[0].y);
					myWeights.put(myWeightInfos[0].z);
					myWeights.put(myWeightInfos[0].w);

					calcWeightInfos(myIntersection);
					myWeightIndices.put(myWeightInfos[1].x);
					myWeightIndices.put(myWeightInfos[1].y);
					myWeightIndices.put(myWeightInfos[1].z);
					myWeightIndices.put(myWeightInfos[1].w);
				}
			}
		}

		myPositions.rewind();
		myWeights.rewind();
		myWeightIndices.rewind();

		CCSkeletonSkin mySkeletonSkin = new CCSkeletonSkin(CCDrawMode.LINES);
		mySkeletonSkin.mesh().vertices(myPositions);
		mySkeletonSkin.mesh().indices(myIndices);
		mySkeletonSkin.mesh().textureCoords(1, myWeights, 4);
		mySkeletonSkin.mesh().textureCoords(2, myWeightIndices, 4);
		return mySkeletonSkin;
	}

	@Override
	public void update(float theDeltaTime) {
		_myColladaSkeletonProvider.time(_cTime);
	}

	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();

		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
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
		g.clearDepthBuffer();
		g.color(255, 0, 0);
		_mySkeleton.draw(g);

		g.color(1f, _cMeshAlpha);
		_mySkeletonSkin.draw(g, _mySkeleton);

		g.polygonMode(CCPolygonMode.FILL);

		g.color(1f, _cCurvesAlpha);
		g.blend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCreatePathsColladaModelDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}