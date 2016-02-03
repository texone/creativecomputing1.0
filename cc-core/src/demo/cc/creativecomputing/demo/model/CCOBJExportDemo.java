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
package cc.creativecomputing.demo.model;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.obj.CCOBJExporter;

public class CCOBJExportDemo extends CCApp {
	
	private CCVector3f[][] _myPoints;
	
	private int _myXres = 100;
	private int _myYres = 100;
	
	@CCControl(name = "scale", min = 1, max = 500)
	private float _cScale = 1;
	
	@CCControl(name = "x blend", min = 0, max = 1)
	private float _cXblend = 1;
	
	@CCControl(name = "y blend", min = 0, max = 1)
	private float _cYblend = 1;
	
	@CCControl(name = "z blend", min = 0, max = 1)
	private float _cZblend = 1;

	private CCSimplexNoise _myNoise;
	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		_myPoints = new CCVector3f[_myXres][_myYres];
		
		_myNoise = new CCSimplexNoise();
		addControls("app", "noise", _myNoise);
		addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		
		for(int x = 0;  x < _myXres; x++){
			for(int y = 0;  y < _myYres; y++){
				float[] myNoises = _myNoise.values(x / (float)_myXres * 10, y / (float)_myYres * 10);
				
				float myX = CCMath.map(x, 0, _myXres-1, -500, 500);
				float myY = CCMath.map(y, 0, _myYres-1, -500, 500);
				float myZ = 0;
				_myPoints[x][y] = new CCVector3f(
					CCMath.blend((myNoises[0] - 0.5) * _cScale * 2, myX, _cXblend),
					CCMath.blend((myNoises[1] - 0.5) * _cScale * 2, myY, _cYblend),
					CCMath.blend((myNoises[2] - 0.5) * _cScale * 2, myZ, _cZblend)
				);
//				System.out.println(_myPoints[x][y] );
			}
		}
		
	}
	
	private void export(){
		String myPath = CCIOUtil.selectOutput("save obj", CCIOUtil.dataPath(""));
		if(myPath == null)return;
		CCOBJExporter myExporter = new CCOBJExporter(myPath);
		myExporter.beginGroup("noise_mesh");
		myExporter.beginShape(CCDrawMode.TRIANGLES);
		for(int x = 0;  x < _myXres-1; x++){
			for(int y = 0;  y < _myYres-1; y++){
				myExporter.vertex(_myPoints[x][y]);
				myExporter.vertex(_myPoints[x][y + 1]);
				myExporter.vertex(_myPoints[x + 1][y]);
				myExporter.vertex(_myPoints[x][y + 1]);
				myExporter.vertex(_myPoints[x+1][y+1]);
				myExporter.vertex(_myPoints[x + 1][y]);
			}
		}
		myExporter.endShape();
		myExporter.endGroup();
	}

	@Override
	public void draw() {
		g.clear();
		
		_myArcball.draw(g);
		g.polygonMode(CCPolygonMode.LINE);
		g.beginShape(CCDrawMode.TRIANGLES);
		for(int x = 0;  x < _myXres-1; x++){
			for(int y = 0;  y < _myYres-1; y++){
				g.vertex(_myPoints[x][y]);
				g.vertex(_myPoints[x][y + 1]);
				g.vertex(_myPoints[x + 1][y]);
				g.vertex(_myPoints[x][y + 1]);
				g.vertex(_myPoints[x+1][y+1]);
				g.vertex(_myPoints[x + 1][y]);
			}
		}
		g.endShape();
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_E:
			export();
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOBJExportDemo.class);
		myManager.settings().size(1500, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

