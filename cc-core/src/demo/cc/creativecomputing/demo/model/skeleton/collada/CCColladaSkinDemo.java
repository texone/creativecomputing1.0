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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaGeometry;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.model.collada.CCColladaTriangles;
import cc.creativecomputing.util.logging.CCLog;

public class CCColladaSkinDemo extends CCApp {

	@CCControl(name = "axis length", min = 0, max = 100)
	private float _cAxisLength = 0;

	@CCControl(name = "time", min = 0, max = 2)
	private float _cTime = 0;

	private CCArcball _myArcball;
	
	private CCVBOMesh _myMesh;
	
	private CCGLSLShader _myShader;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		CCColladaLoader myColladaLoader = new CCColladaLoader("demo/model/collada/humanoid.dae"); //demo/model/collada/adenovirus_lowpoly.dae
		CCColladaGeometry myGeometry = myColladaLoader.geometries().element(0);
		CCColladaTriangles myTriangles = myGeometry.triangles().get(0);
		
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		CCLog.info(myTriangles.hasNormals());

		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, myTriangles.numberOfVertices());
		_myMesh.vertices(myTriangles.positions());
		_myMesh.normals(myTriangles.normals());
		_myMesh.textureCoords(0,myTriangles.texCoords());
		_myMesh.textureCoords(1,mySkinController.weights(), 4);
		_myMesh.textureCoords(1,mySkinController.indices(), 4);
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "collada_vertex.glsl"), 
			CCIOUtil.classPath(this, "collada_fragment.glsl")
		);
		_myShader.load();
	}

	@Override
	public void update(float theDeltaTime) {
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
		_myShader.start();
		_myMesh.draw(g);
		_myShader.end();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColladaSkinDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}