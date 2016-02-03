package cc.creativecomputing.demo.topic.shader2D;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCNoiseRaymarchDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCShaderBuffer _myRandomTexture;
	
	@CCControl(name = "noise scale", min = 0, max = 20)
	private float _cNoiseScale = 1f;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			null,
			CCIOUtil.classPath(this, "noiseraymarch.glsl")
		);
		_myShader.load();
		
		CCGLSLShader _myValueShader = new CCGLSLShader(
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

		_myShader.uniform1f("noiseScale", _cNoiseScale);
		
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
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseRaymarchDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
