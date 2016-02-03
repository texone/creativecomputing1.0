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
package cc.creativecomputing.demo.graphics.shader.simulation.dla;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 * 
 */
public class CCGPUDLA {

	@CCControl(name = "particle replacement", min = 1, max = 20)
	private float _cParticleReplacement = 0;

	@CCControl(name = "particle speed", min = 10, max = 1000)
	private float _cParticleSpeed = 0;

	private CCGraphics g;

	/* USED FOR PARTICLES */

	private CCCGShader _myParticleShader;
	private CGparameter _myRandomTextureParameter;
	private CGparameter _myPositionTextureParameter;
	private CGparameter _myParticleCrystalTextureParameter;

	private CGparameter _myTextureOffsetParameter;
	private CGparameter _myBoundaryParameter;
	private CGparameter _mySpeedParameter;
	private CGparameter _myReplacementParameter;

	private CCTexture2D _myRandomTexture;

	private CCShaderBuffer _myInputBuffer;
	private CCShaderBuffer _myOutputBuffer;

	private CCCGShader _myInitValueShader;

	private int _myParticlesSizeX;
	private int _myParticlesSizeY;

	/* USED FOR CRYSTALIZATION */

	private CCCGShader _myCrystalShader;
	private CGparameter _myCrystalTextureParameter;
	private CGparameter _myParticleTextureParameter;

	private CCShaderBuffer _myParticlesBuffer;
	private CCShaderBuffer _myCrystalInputBuffer;
	private CCShaderBuffer _myCrystalOutputBuffer;

	private int _myWidth;
	private int _myHeight;

	private CCVBOMesh _myParticlesMesh;

	public CCGPUDLA(CCGraphics theGraphics, final int theParticlesSizeX, final int theParticlesSizeY, final int theWidth, final int theHeight) {
		g = theGraphics;
		_myParticlesSizeX = theParticlesSizeX;
		_myParticlesSizeY = theParticlesSizeY;

		_myWidth = theWidth;
		_myHeight = theHeight;

		_myParticleShader = new CCCGShader(null, CCIOUtil.classPath(this,"particles.fp"));
		_myRandomTextureParameter = _myParticleShader.fragmentParameter("randomTexture");
		_myPositionTextureParameter = _myParticleShader.fragmentParameter("positionTexture");
		_myParticleCrystalTextureParameter = _myParticleShader.fragmentParameter("crystalTexture");

		_myTextureOffsetParameter = _myParticleShader.fragmentParameter("texOffset");
		_myBoundaryParameter = _myParticleShader.fragmentParameter("boundary");
		_mySpeedParameter = _myParticleShader.fragmentParameter("speed");
		_myReplacementParameter = _myParticleShader.fragmentParameter("replacement");
		_myParticleShader.load();

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(this,"initvalue.fp"));
		_myInitValueShader.load();

		_myRandomTexture = new CCTexture2D(CCTextureIO.newTextureData(CCIOUtil.classPath(this,"random.png")), CCTextureTarget.TEXTURE_RECT);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);

		_myInputBuffer = new CCShaderBuffer(32, 3, _myParticlesSizeX, _myParticlesSizeY);
		_myOutputBuffer = new CCShaderBuffer(32, 3, _myParticlesSizeX, _myParticlesSizeY);

		_myParticlesMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticlesSizeX * _myParticlesSizeY);

		initializeParticles();

		_myCrystalShader = new CCCGShader(null, CCIOUtil.classPath(this,"crystal.fp"));
		_myCrystalTextureParameter = _myCrystalShader.fragmentParameter("crystalTexture");
		_myParticleTextureParameter = _myCrystalShader.fragmentParameter("particleTexture");
		_myCrystalShader.load();

		_myParticlesBuffer = new CCShaderBuffer(32, 3, _myWidth, _myHeight);
		_myCrystalInputBuffer = new CCShaderBuffer(32, 3, _myWidth, _myHeight);
		_myCrystalOutputBuffer = new CCShaderBuffer(32, 3, _myWidth, _myHeight);
		// initializeCrystal();
	}

	private void initializeParticles() {
		// Render velocity.
		_myInputBuffer.beginDraw();
		_myInitValueShader.start();

		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < _myParticlesSizeX; x++) {
			for (int y = 0; y < _myParticlesSizeY; y++) {
				g.textureCoords(0, new CCVector3f(CCMath.random(_myWidth), CCMath.random(_myHeight)));
				g.vertex(x, y);// ) + offsetY /* + offsetY shouldn't be */
			}
		}
		g.endShape();

		_myInitValueShader.end();
		_myInputBuffer.endDraw();
	}

	public void beginCrystal() {
		_myCrystalInputBuffer.beginDraw();
	}

	public void endCrystal() {
		_myCrystalInputBuffer.endDraw();
	}

	@SuppressWarnings("unused")
	private void initializeCrystal() {
		// Render velocity.
		_myCrystalInputBuffer.beginDraw();
		// _myInitValueShader.start();

		g.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < 10; i++) {
			g.textureCoords(0, new CCVector3f(1, 1, 1));
			g.vertex(CCMath.random(_myWidth), CCMath.random(_myHeight));// ) + offsetY /* + offsetY shouldn't be */
		}
		g.endShape();

		// _myInitValueShader.end();
		_myCrystalInputBuffer.endDraw();
	}

	public void reset() {
		_myCrystalInputBuffer.clear();
		// initializeCrystal();
		_myCrystalOutputBuffer.clear();
	}

	public void update(final float theDeltaTime) {
		/* UPDATE PARTICLES */
		_myParticleShader.texture(_myRandomTextureParameter, _myRandomTexture.id());
		_myParticleShader.texture(_myPositionTextureParameter, _myInputBuffer.attachment(0).id());
		_myParticleShader.texture(_myParticleCrystalTextureParameter, _myCrystalInputBuffer.attachment(0).id());
		_myParticleShader.start();
		_myParticleShader.parameter(_myTextureOffsetParameter, new CCVector2f((int) CCMath.random(500), (int) CCMath.random(500)));

		_myParticleShader.parameter(_mySpeedParameter, _cParticleSpeed * theDeltaTime);
		_myParticleShader.parameter(_myReplacementParameter, _cParticleReplacement * theDeltaTime);
		_myParticleShader.parameter(_myBoundaryParameter, new CCVector2f(_myWidth, _myHeight));

		_myOutputBuffer.draw();
		_myParticleShader.end();

		_myParticlesMesh.vertices(_myOutputBuffer);

		CCShaderBuffer myTemp = _myInputBuffer;
		_myInputBuffer = _myOutputBuffer;
		_myOutputBuffer = myTemp;

		/* UPDATE CRYSTAL */
		_myParticlesBuffer.beginDraw();
		g.clear();
		_myParticlesMesh.draw(g);
		_myParticlesBuffer.endDraw();

		_myCrystalShader.texture(_myParticleTextureParameter, _myParticlesBuffer.attachment(0).id());
		_myCrystalShader.texture(_myCrystalTextureParameter, _myCrystalInputBuffer.attachment(0).id());
		_myCrystalShader.start();

		_myCrystalOutputBuffer.draw();

		_myCrystalShader.end();

		myTemp = _myCrystalInputBuffer;
		_myCrystalInputBuffer = _myCrystalOutputBuffer;
		_myCrystalOutputBuffer = myTemp;
	}

	public void draw(CCGraphics g) {
		g.image(_myCrystalOutputBuffer.attachment(0), -_myWidth / 2, -_myHeight / 2);
		// _myParticlesMesh.draw(g);
	}

	public CCTexture2D dlaTexture() {
		return _myCrystalOutputBuffer.attachment(0);
	}
}
