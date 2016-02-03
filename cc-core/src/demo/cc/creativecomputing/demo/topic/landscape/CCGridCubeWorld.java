package cc.creativecomputing.demo.topic.landscape;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCGridCubeWorld extends CCApp {
	
	@CCControl(name = "grid space", min = 0, max = 50)
	private float _cGridSpace = 15;
	
	private class CCGridCube{
		CCVector3f _myCubeCorner1;
		CCVector3f _myCubeCorner2;
		
		CCGridCube(
			CCVector3f theCubeCorner1,
			CCVector3f theCubeCorner2
		){
			_myCubeCorner1 = theCubeCorner1;
			_myCubeCorner2 = theCubeCorner2;
		}
		
		public void draw(CCGraphics g){
			for(float x = _myCubeCorner1.x; x < _myCubeCorner2.x; x += _cGridSpace){
				g.line(x, _myCubeCorner1.y, _myCubeCorner1.z, x, _myCubeCorner2.y, _myCubeCorner1.z);
				g.line(x, _myCubeCorner1.y, _myCubeCorner2.z, x, _myCubeCorner2.y, _myCubeCorner2.z);
			}
			for(float y = _myCubeCorner1.y; y < _myCubeCorner2.y; y += _cGridSpace){
				g.line(_myCubeCorner1.x, y, _myCubeCorner1.z, _myCubeCorner2.x, y, _myCubeCorner1.z);
				g.line(_myCubeCorner1.x, y, _myCubeCorner2.z, _myCubeCorner2.x, y, _myCubeCorner2.z);
			}
			for(float z = _myCubeCorner1.z; z < _myCubeCorner2.z; z += _cGridSpace){
				g.line(_myCubeCorner1.x, _myCubeCorner1.y, z, _myCubeCorner2.x, _myCubeCorner1.y, z);
				g.line(_myCubeCorner1.x, _myCubeCorner2.y, z, _myCubeCorner2.x, _myCubeCorner2.y, z);
			}
		}
	}
	
	private List<CCGridCube> _myCubes = new ArrayList<>();
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}
	
	private void createWorld(){
		_myCubes.clear();
		
		for(int i = 0; i < 500;i++){
			CCVector3f myCubeCorner1 = new CCVector3f(
				((int)CCMath.random(-1000 / _cGridSpace, 1000 / _cGridSpace)) * _cGridSpace,
				((int)CCMath.random(-1000 / _cGridSpace, 1000 / _cGridSpace)) * _cGridSpace,
				((int)CCMath.random(-1000 / _cGridSpace, 1000 / _cGridSpace)) * _cGridSpace
			);
			CCVector3f myCubeCorner2 = myCubeCorner1.clone();
			myCubeCorner2.add(
				((int)CCMath.random(300 / _cGridSpace)) * _cGridSpace,
				((int)CCMath.random(300 / _cGridSpace)) * _cGridSpace,
				((int)CCMath.random(300 / _cGridSpace)) * _cGridSpace
			);
			
			_myCubes.add(new CCGridCube(myCubeCorner1, myCubeCorner2));
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		for(CCGridCube myCube:_myCubes){
			myCube.draw(g);
		}
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_C:
			createWorld();
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGridCubeWorld.class);
		myManager.settings().size(1000, 1000);
		myManager.start();
	}
}

