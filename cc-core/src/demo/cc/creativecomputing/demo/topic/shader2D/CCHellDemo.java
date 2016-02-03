package cc.creativecomputing.demo.topic.shader2D;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;

public class CCHellDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCGLSLShader _myValueShader;
	
	@CCControl(name = "origin x", min = 0, max = 5f)
	private float _cOriginX = 1f;
	@CCControl(name = "origin y", min = 0, max = 5f)
	private float _cOriginY = 1.5f;
	@CCControl(name = "origin z", min = 0, max = 5f)
	private float _cOriginZ = 0f;

	@CCControl(name = "origin distance", min = 0, max = 15f)
	private float _cOriginDistance = 4f;

	@CCControl(name = "cam rotation", min = 0, max = CCMath.TWO_PI)
	private float _cCamRotation = 4f;
	
	@CCControl(name = "noise octaves", min = 1, max = 10)
	private int _cNoiseOctaves = 4;
	@CCControl(name = "noise gain", min = 0, max = 1f)
	private float _cNoiseGain = 0.5f;
	@CCControl(name = "noise lacunarity", min = 1f, max = 10f)
	private float _cNoiseLacunarity = 0.5f;
	

	@CCControl(name = "alphaReduction", min = 0, max = 1f)
	private float _cAlphaReduction = 0.6f;
	@CCControl(name = "march step", min = 0, max = 1f)
	private float _cMarchStep = 0.05f;
	
	private CCShaderBuffer _myRandomTexture;
	
	private CCTexture2D _myRandom;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			null,
			CCIOUtil.classPath(this, "hell.glsl")
		);
		_myShader.load();
		
		_myValueShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "valueShader_vert.glsl"), 
			CCIOUtil.classPath(this, "valueShader_frag.glsl")
		);
		_myValueShader.load();
		
		_myRandomTexture = new CCShaderBuffer(256, 256, CCTextureTarget.TEXTURE_2D);
		
		_myRandomTexture.clear();
		
		_myRandomTexture.beginDraw();
		_myValueShader.start();
		g.noBlend();
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < _myRandomTexture.width(); x++){
			for(int y = 0; y < _myRandomTexture.height(); y++){
				
				g.textureCoords(
					0,
					CCMath.random(),
					CCMath.random(),
					CCMath.random()
				);
				g.vertex(x,y);
			}
		}
		
		g.endShape();
		_myValueShader.end();
		_myRandomTexture.endDraw();
		
		_myRandomTexture.attachment(0).wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myRandomTexture.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		
		_myRandom = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/tex_random_16.png"));
		_myRandom.wrap(CCTextureWrap.REPEAT);
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		
		_myShader.start();
		
		g.texture(0,_myRandomTexture.attachment(0));
		
		_myShader.uniform1i("iChannel0", 0);
		_myShader.uniform1f("iGlobalTime", frameCount / 100f);
		_myShader.uniform2f("iResolution", width, height);
		_myShader.uniform2f("iChannelResolution", _myRandomTexture.width(), _myRandomTexture.height());
		
		
		_myShader.uniform3f("origin", _cOriginX, _cOriginY, _cOriginZ);
		_myShader.uniform1f("originDistance", _cOriginDistance);
		_myShader.uniform1f("cr", _cCamRotation);

		_myShader.uniform1f("gain", _cNoiseGain);
		_myShader.uniform1f("lacunarity", _cNoiseLacunarity);
		_myShader.uniform1i("octaves", _cNoiseOctaves);
		
		_myShader.uniform1f("marchStep", _cMarchStep);
		_myShader.uniform1f("alphaReduction", _cAlphaReduction);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-width/2, -height/2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(width/2, -height/2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(width/2, height / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-width/2, height/2);
        g.endShape();
        
        g.noTexture();
        
        _myShader.end();
        
        CCLog.info(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCHellDemo.class);
		myManager.settings().size(1500, 500);
		myManager.start();
	}
}
