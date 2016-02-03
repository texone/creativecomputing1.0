package cc.creativecomputing.demo.topic.color;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;

public class CCGradients extends CCApp {

	private class CCGradientBlock{
		
		public float _myX;
		public float _myWidth;
		
		private CCColor _myColor0 = new CCColor(255);
		private CCColor _myColor1 = new CCColor(255);
		
		
		public CCGradientBlock(float theX, float theWidth){
			_myX = theX;
			_myWidth = theWidth;
		}
		
		public void draw(CCGraphics g){
			g.color(_myColor0);
			g.vertex(_myX, -_cHeight / 2);
			g.vertex(_myX + _myWidth, -_cHeight / 2);
			g.color(_myColor1);
			g.vertex(_myX + _myWidth, +_cHeight / 2);
			g.vertex(_myX, + _cHeight / 2);
		}
	}
	@CCControl(name = "height", min = 10, max = 400)
	private float _cHeight;
	
	List<CCGradientBlock> _myGradientBlocks = new ArrayList<CCGradients.CCGradientBlock>();
	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		for(int i = 0; i < 50;i++){
			_myGradientBlocks.add(new CCGradientBlock(CCMath.map(i, 0, 50, -width/2, width/2), width / 51));
		}
		
		addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}

	@CCControl(name = "noiseSpeed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	@CCControl(name = "saturation", min = 0, max = 1)
	private float _cSaturation = 0;
	@CCControl(name = "bottom offset", min = 0, max = 1)
	private float _cBottomOffset = 0;
	@CCControl(name = "id offset", min = 0, max = 1)
	private float _cIDOffset = 0;
	@CCControl(name = "random offset", min = 0, max = 1)
	private float _cRandomOffset = 0;
	
	private float _noiseOffset = 0;
	
	@CCControl(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();
	
	
	
	private float[] _myRandoms = new float[50];
	{
		for(int i = 0; i < 50;i++){
			_myRandoms[i] = CCMath.random();
		}
	}
	
	@Override
	public void update(final float theDeltaTime) {
		_noiseOffset += theDeltaTime * _cNoiseSpeed;
		int i = 0;
		for(CCGradientBlock myBlock:_myGradientBlocks){
			float myIDOffset = i % 2 * _cIDOffset;
			float myRandomOffset = _myRandoms[i] * _cRandomOffset;
					i++;
			myBlock._myColor0.setHSB(_myNoise.value(myBlock._myX / width + myIDOffset + myRandomOffset, _noiseOffset), _cSaturation, 1.0f);
			myBlock._myColor1.setHSB(_myNoise.value(myBlock._myX / width + myIDOffset + myRandomOffset + _cBottomOffset, _noiseOffset), _cSaturation, 1.0f);
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.beginShape(CCDrawMode.QUADS);
		for(CCGradientBlock myBlock:_myGradientBlocks){
			myBlock.draw(g);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGradients.class);
		myManager.settings().size(1900, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

