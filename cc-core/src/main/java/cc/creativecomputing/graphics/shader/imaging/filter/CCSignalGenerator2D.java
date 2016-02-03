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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;


public class CCSignalGenerator2D {

	private CCShaderBuffer _myOutputBuffer;
	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;
	
	@CCControl(name = "noise gain", min = 0.0f, max = 1f)
	private float _myNoiseGain = 1f;
	
	@CCControl(name = "noiseScale", min = 0, max = 1)
	private float _cNoiseScale = 1;
	
	@CCControl(name = "noiseOffsetX", min = 0, max = 20)
	private float _cNoiseOffsetX = 1;
	
	@CCControl(name = "noiseOffsetY", min = 0, max = 20)
	private float _cNoiseOffsetY = 1;
	
	@CCControl(name = "noiseOffsetZ", min = 0, max = 20)
	private float _cNoiseOffsetZ = 1;
	
	@CCControl(name = "randThresh", min = 0, max = 1f)
	private float _cRandThresh = 1;

	private CCSignalGeneratorBall   _myBall;
		
	public class CCSignalGeneratorRand {
		
		CCGLSLShader _myRandomShader;
		public CCSignalGeneratorRand() {
			_myRandomShader = new CCGLSLShader (CCIOUtil.classPath(this, "shader/random_vertex.glsl"), CCIOUtil.classPath(this, "shader/random_fragment.glsl"));
			_myRandomShader.load();
		}
		
		
		public void draw() {
			_myRandomShader.start();
			_myRandomShader.uniform3f("randAdd", CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
			_myRandomShader.uniform1f("thresh", _cRandThresh);
			_myGraphics.rect(0,0, _myWidth, _myHeight);
			_myRandomShader.end();
		}
	}
	
	public class CCSignalGeneratorNoise {
		CCCGShader _myNoiseShader;
		CGparameter _myNoiseScaleParameter;
		CGparameter _myNoiseOffsetParameter;
		CCVector3f _myOffset = new CCVector3f();
		
		public CCSignalGeneratorNoise() {
			_myNoiseShader = new CCCGShader (null, CCIOUtil.classPath(this, "simplexnoisefragment.fp"));
			_myNoiseScaleParameter  = _myNoiseShader.fragmentParameter("noiseScale");
			_myNoiseOffsetParameter = _myNoiseShader.fragmentParameter("noiseOffset");
			_myNoiseShader.load();
			CCGPUNoise.attachFragmentNoise(_myNoiseShader);
		}
		
		float time = 0;
		
		public void draw() {
			_myGraphics.pushAttribute();
			time+= 0.01f;
			_myOffset = new CCVector3f(_cNoiseOffsetX, _cNoiseOffsetY, _cNoiseOffsetZ + time);
			_myGraphics.clear();
			_myNoiseShader.start();
			_myGraphics.color(_myNoiseGain);
			_myNoiseShader.parameter(_myNoiseScaleParameter, _cNoiseScale);
			_myNoiseShader.parameter(_myNoiseOffsetParameter, _myOffset);
			_myGraphics.rect (0,0, _myWidth, _myHeight);
			_myNoiseShader.end();
			_myGraphics.popAttribute();
		}
	}
	
	public class CCSignalGeneratorBall {
		float _myFreq;
		float _mySize;
		float _myRadius;
		CCVector2f dir;
		CCVector2f pos;
		
		public CCSignalGeneratorBall (float theRadius) {
			_myRadius = theRadius;
			pos = new CCVector2f (CCMath.random(_myRadius, _myWidth-_myRadius), CCMath.random(_myRadius, _myHeight-_myRadius));
			dir = new CCVector2f (2,3);//new CCVector2f (CCMath.random(-1,1), CCMath.random(-1,1));
		}
		public void draw() {
			pos.x += dir.x;
			pos.y += dir.y;
			if (pos.x<_myRadius || pos.x > _myWidth-_myRadius) {
				dir.x *= -1;
			}
			if (pos.y<_myRadius || pos.y > _myHeight-_myRadius) {
				dir.y *= -1;
			}
			_myGraphics.pushAttribute();
			_myGraphics.color(1f);
			_myGraphics.ellipse(pos, _myRadius);
			//_myGraphics.rect(pos, new CCVector2f(30f,30f));
			_myGraphics.popAttribute();
		}
	}

	
	public CCSignalGenerator2D (CCGraphics theGraphics, int theWidth, int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myOutputBuffer = new CCShaderBuffer (theWidth, theHeight);
		_myGraphics = theGraphics;
		 
		// generator instances
		//_myNoise = new CCSignalGeneratorNoise();
		_myBall  = new CCSignalGeneratorBall(60);
	}
	
	public void enableMipMaps() {
		_myOutputBuffer.attachment(0).generateMipmaps(true);
		_myOutputBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myOutputBuffer.attachment(0).textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
	}
	
	public void update () {
		
		_myGraphics.pushAttribute();
		_myOutputBuffer.beginDraw();
		_myGraphics.clear();
				
		
		_myGraphics.blend(CCBlendMode.ADD);
		//_myNoise.draw();
		//_myRand.draw();
		_myBall.draw();
		
		_myGraphics.blend(CCBlendMode.BLEND);
		
		_myOutputBuffer.endDraw();
		_myGraphics.popAttribute();
	}
	
	
	public void drawRect() {
		_myGraphics.pushAttribute();
		_myOutputBuffer.beginDraw();
		_myGraphics.clear();
		_myGraphics.color(1f);
		_myGraphics.rect(_myWidth/2, _myHeight/2, 16, 16);
		_myOutputBuffer.endDraw();
		_myGraphics.popAttribute();
	}
	
	public void clear() {
		_myGraphics.pushAttribute();
		_myOutputBuffer.beginDraw();
		_myGraphics.clear();
		_myOutputBuffer.endDraw();
		_myGraphics.popAttribute();
	}
	
	public CCTexture2D output() {
		return _myOutputBuffer.attachment(0);
	}
}
