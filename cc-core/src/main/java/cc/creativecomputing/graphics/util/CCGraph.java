package cc.creativecomputing.graphics.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;

public class CCGraph {
	
	@CCControl(name = "x", min = 0, max = 1)
	private float _cX;
	@CCControl(name = "y", min = 0, max = 1)
	private float _cY;

	@CCControl(name = "width", min = 0, max = 1)
	private float _cWidth;
	@CCControl(name = "height", min = 0, max = 1)
	private float _cHeight;

	@CCControl(name = "size", min = 1, max = 10000)
	private int _cHistorySize = 0;

	@CCControl(name = "skip", min = 1, max = 100)
	private int _cSkip = 0;
	
	@CCControl(name = "merge", min = 1, max = 100)
	private boolean _cMerge = true;
	
	private List<Float> _myHistory = new ArrayList<Float>(10000);

	public CCGraph(){
		
	}
	
	private int _myCounter = 0;
	
	private float _myMax = 0;
	private float _myMin = Float.MAX_VALUE;
	
	private float _mySum = 0;
	
	public void add(float theValue){
		_mySum += theValue;
		if(_myCounter % _cSkip != 0){
			_myCounter++;
			return;
		}
		_myCounter++;
		
		_myMax = CCMath.max(_myMax, theValue);
		_myMin = CCMath.min(_myMin, theValue);
		
		while(_myHistory.size() > _cHistorySize){
			_myHistory.remove(0);
		}
		while(_myHistory.size() < _cHistorySize){
			_myHistory.add(0f);
		}
		
		_myHistory.add(_cMerge ? _mySum / _cSkip : theValue);
		_mySum = 0;
	}
	
	public void draw(CCGraphics g){
		float x0 = _cX * g.width - g.width/2;
		float x1 = x0 + _cWidth * g.width;

		float y0 = _cY * g.height - g.height/2;
		float y1 = y0 + _cHeight * g.height;
		
		g.line(x0, y0, x1, y0);
		g.line(x0, y1, x1, y1);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < _myHistory.size();i++){
			float myValue = _myHistory.get(i);
			float myX = CCMath.map(i, 0, _myHistory.size() - 1, x0, x1);
			float myY = CCMath.map(myValue, _myMin, _myMax, y0, y1);
			g.vertex(myX, myY);
		}
		g.endShape();
	}
}
