package cc.creativecomputing.demo.topic.shader2D;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;

public class CCFireDemo extends CCApp {
	
	private CCGLSLShader _myShader;

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			null,
			CCIOUtil.classPath(this, "fire.glsl")
		);
		_myShader.load();
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		
		_myShader.start();
		_myShader.uniform1f("iGlobalTime", frameCount / 100f);
		_myShader.uniform2f("iResolution", width, height);
		
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
        
        _myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFireDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
