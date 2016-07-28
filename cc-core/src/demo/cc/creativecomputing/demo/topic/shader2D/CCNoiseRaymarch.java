package cc.creativecomputing.demo.topic.shader2D;

import java.io.File;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.random.CCRandom;
import cc.creativecomputing.util.logging.CCLog;

public class CCNoiseRaymarch extends CCApp{

	private CCGLSLShader _myShader;
	
	private CCRandom _myRandom = new CCRandom();
	
	private class CCNoiseControl{
		@CCControl(name = "octaves", min = 1, max = 10)
		private int octaves = 4; 
		@CCControl(name = "gain", min = 0, max = 1)
		private float gain = 0.5f; 
		@CCControl(name = "lacunarity", min = 0, max = 10)
		private float lacunarity = 2f; 
		
		@CCControl(name = "speed x", min = -1, max = 1)
		private float speedX = 0f; 
		@CCControl(name = "speed y", min = -1, max = 1)
		private float speedY = 1.0f; 
		@CCControl(name = "speed z", min = -1, max = 1)
		private float speedZ = 0f; 
		@CCControl(name = "speed gain", min = 0, max = 2)
		private float speedGain = 0.5f; 
	}
	
	@CCControl(name = "noise", column = 1)
	private CCNoiseControl _cNoiseControl = new CCNoiseControl();

	@CCControl(name = "map density start", min = -5, max = 5)
	private float _cMapDensityStart = 0.2f; 
	@CCControl(name = "map density noise amp", min = 0, max = 10)
	private float _cMapDensityNoiseAmp = 4f; 
	@CCControl(name = "map density sinus color mod", min = 0, max = 1)
	private float _cMapDensitySinusMod = 4f; 
	@CCControl(name = "map density scale", min = 0, max = 1)
	private float _cMapDensityScale = 0.6f; 
	
	private class CCRGBControl{
		@CCControl(name = "r", min = 0, max = 1)
		private float _cR = 0;
		@CCControl(name = "g", min = 0, max = 1)
		private float _cG = 0;
		@CCControl(name = "b", min = 0, max = 1)
		private float _cB = 0;
		@CCControl(name = "a", min = 0, max = 1)
		private float _cA = 0;
		@CCControl(name = "amp", min = 0, max = 5)
		private float _cAmp = 0;
	}
	
	@CCControl(name = "density0", column = 2)
	private CCRGBControl _cDensity0 = new CCRGBControl();
	@CCControl(name = "density1", column = 2)
	private CCRGBControl _cDensity1 = new CCRGBControl();
	
	@CCControl(name = "back", column = 2)
	private CCRGBControl _cBack = new CCRGBControl();
	
	@CCControl(name = "back color", column = 3)
	private CCRGBControl _cBackColor = new CCRGBControl();
	
	@CCControl(name = "march step size", min = 0, max = 1)
	private float _cMarchStepSize = 0.05f; 
	@CCControl(name = "march steps", min = 0, max = 200)
	private int _cMarchSteps = 100; 
	
	@CCControl(name = "density shape", min = 0, max = 10)
	private float _cDensityShape = 1f; 
	
	

	
	private long myLastFileTime = 0;
	
	@Override
	public void setup(){
		_myShader = new CCGLSLShader(
			null,
			CCIOUtil.classPath(this, "noise_raymarch.glsl")
		);
		_myShader.load();
		
		myLastFileTime = new File(CCIOUtil.classPath(this, "noise_raymarch.glsl")).lastModified();
		
		CCColor[][] myBaseColorMap = new CCColor[256][256];
		
		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				myBaseColorMap[x][y] = new CCColor(_myRandom.random(),0,0,0);
			}
		}

		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				int x2 = (x + 37) % 256;
				int y2 = (y + 17) % 256;
				myBaseColorMap[x2][y2].g = myBaseColorMap[x][y].r;
			}
		}
		
		CCTextureData myData = new CCTextureData(256,256);
		for(int x = 0; x < myData.width(); x++){
			for(int y = 0; y < myData.height(); y++){
				myData.setPixel(x, y, myBaseColorMap[x][y]);
			}
		}
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(float theDeltaTime) {
		_myTime += theDeltaTime;
		File myFile = new File(CCIOUtil.classPath(this, "noise_raymarch.glsl"));
		if(myFile.lastModified() > myLastFileTime){
			myLastFileTime = myFile.lastModified();
			_myShader.reload();
		}
	}
	
	@Override
	public void draw() {
		g.clearColor(_cBack._cR, _cBack._cG, _cBack._cB);
		g.clear();
		
		_myShader.start();
		_myShader.uniform1f("time", _myTime);
		_myShader.uniform2f("resolution", width, height);
		
		_myShader.uniform1i("octaves", _cNoiseControl.octaves);
		_myShader.uniform1f("gain", _cNoiseControl.gain);
		_myShader.uniform1f("lacunarity", _cNoiseControl.lacunarity);
		
		_myShader.uniform3f("noiseMovement", _cNoiseControl.speedX, _cNoiseControl.speedY, _cNoiseControl.speedZ);
		_myShader.uniform1f("speedGain", _cNoiseControl.speedGain);

		_myShader.uniform1f("densityStart", _cMapDensityStart);
		_myShader.uniform1f("densityNoiseAmp", _cMapDensityNoiseAmp);
		_myShader.uniform1f("densitySinusColorMod", _cMapDensitySinusMod);
		_myShader.uniform1f("densityScale", _cMapDensityScale);
		
		
		_myShader.uniform4f("densityColor0", new CCColor(_cDensity0._cR, _cDensity0._cG, _cDensity0._cB, _cDensity0._cA)); //vec3(1.0,0.9,0.8), 
		_myShader.uniform1f("densityColor0Amp", _cDensity0._cAmp);
		_myShader.uniform4f("densityColor1", new CCColor(_cDensity1._cR, _cDensity1._cG, _cDensity1._cB, _cDensity1._cA)); //vec3(0.4,0.15,0.1),
		_myShader.uniform1f("densityColor1Amp", _cDensity1._cAmp);
		
		_myShader.uniform4f("backColor", new CCColor(_cBackColor._cR, _cBackColor._cG, _cBackColor._cB)); //
		
		_myShader.uniform1f("marchStepSize", _cMarchStepSize);
		_myShader.uniform1i("marchSteps", _cMarchSteps);
		

		_myShader.uniform1f("densityShape", _cDensityShape);

		

		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-width / 2, -height / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex( width / 2, -height / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex( width / 2,  height / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-width / 2,  height / 2);
        g.endShape();
        
        
        _myShader.end();
        
//        CCLog.info(frameRate);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_R:
			_myShader.reload();
			break;

		default:
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseRaymarch.class);
		myManager.settings().size(960, 540);
		myManager.settings().vsync(true);
		myManager.settings().alwaysOnTop(true);
		myManager.start();
	}
}
