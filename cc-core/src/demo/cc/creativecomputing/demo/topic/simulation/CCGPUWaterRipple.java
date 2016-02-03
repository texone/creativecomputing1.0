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
package cc.creativecomputing.demo.topic.simulation;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 *
 */
public class CCGPUWaterRipple{
	
	
	private static class CCGPUSineShader extends CCCGShader{
		
		private final CGparameter _myAmplitudeParameter;
		
		public CCGPUSineShader(final CCGraphics theG) {
			super(null, CCIOUtil.classPath(CCGPUWaterRipple.class, "sine.fp"));
			_myAmplitudeParameter = fragmentParameter("amplitude");
			amplitude(1f);
			load();
		}
		
		public void amplitude(final float theAmplitude){
			parameter(_myAmplitudeParameter, theAmplitude);
		}
	}
	
	private static class CCGPUWaterRippleSimulationShader extends CCCGShader{
		
		private CGparameter _myPreviousCellLocationsParameter;
		private CGparameter _myCurrentCellLocationsParameter;
		
		private CGparameter _myWaveInnerEdgesParameter;
		
		private CGparameter _myDampingParameter;
		private CGparameter _myNormalHeightParameter;
		private CGparameter _myInnerEdgesStrengthParameter;

		public CCGPUWaterRippleSimulationShader(final CCGraphics theG) {
			super(null, CCIOUtil.classPath(CCGPUWaterRipple.class, "water_simulation.fp"));
			
			_myPreviousCellLocationsParameter = fragmentParameter("previous_cells");
			_myCurrentCellLocationsParameter = fragmentParameter("current_cells");

			_myWaveInnerEdgesParameter = fragmentParameter("wave_break_inner_edges");
			
			_myDampingParameter = fragmentParameter("damping");
			_myNormalHeightParameter = fragmentParameter("normalHeightScale");
			_myInnerEdgesStrengthParameter = fragmentParameter("waveInnerEdgesStrength");
			
			load();
		}
		
		public void previousCellLocations(final CCTexture2D theTexture){
			texture(_myPreviousCellLocationsParameter, theTexture.id());
		}
		
		public void currentCellLocations(final CCTexture2D theTexture){
			texture(_myCurrentCellLocationsParameter, theTexture.id());
		}
		
		public void waveInnerEdges(final CCTexture2D theTexture){
			texture(_myWaveInnerEdgesParameter, theTexture.id());
		}
		
		public void damping(final float theDamping){
			parameter(_myDampingParameter, theDamping);
		}
		
		public void normalHeightScale(final float theNormalHeightScale) {
			parameter(_myNormalHeightParameter, theNormalHeightScale);
		}
		
		public void innerEdgesStrength(float theStrength){
			parameter(_myInnerEdgesStrengthParameter, theStrength);
		}
	}
	
	private static class CCGPUWaterDrawShader extends CCCGShader{
		
		private final CGparameter _myBackgroundTextureParameter;
		private final CGparameter _myHeightMapTextureParameter;
		private final CGparameter _myNormalMapTextureParameter;
		
		private final CGparameter _myRefractionParameter;

		public CCGPUWaterDrawShader(final CCGraphics theG) {
			super(null,  CCIOUtil.classPath(CCGPUWaterRipple.class, "water_draw.fp"));
			_myBackgroundTextureParameter = fragmentParameter("backgroundTexture");
			_myHeightMapTextureParameter = fragmentParameter("heightMap");
			_myNormalMapTextureParameter = fragmentParameter("normalMap");
			_myRefractionParameter = fragmentParameter("refraction");
			load();
		}
		
		public void heightMap(final int theID){
			texture(_myHeightMapTextureParameter, theID);
		}
		
		public void normalMap(final int theID){
			texture(_myNormalMapTextureParameter, theID);
		}
		
		public void backgroundTexture(final CCTexture2D theTexture){
			texture(_myBackgroundTextureParameter, theTexture.id());
		}
		
		public void refraction(final float theRefraction) {
			parameter(_myRefractionParameter, theRefraction);
		}
	}
	
	private static class CCGPUWaterSplash{
		
		private float _myX;
		private float _myY;
		
		private float _myRadius;
		
		private float _myAmplitude;
		
		public CCGPUWaterSplash(float theX, float theY, float theRadius, float theAmplitude){
			_myX = theX;
			_myY = theY;
			_myRadius = theRadius;
			_myAmplitude = theAmplitude;
		}
	}
	
	private int _myWidth;
	private int _myHeight;
	
	private CCGPUSineShader _mySineShader;
	private CCGPUWaterRippleSimulationShader _mySimShader;
	private CCGPUWaterDrawShader _myDrawShader;
	
	private List<CCGPUWaterSplash> _mySplashes;
	
	private CCShaderBuffer _myCurrentCellLocationsTexture;
	private CCShaderBuffer _myPreviousCellLocationsTexture;
	private CCShaderBuffer _myTargetCellLocationsTexture;
	
	// input textures
	private CCShaderBuffer _myWaveInnerEdgeTexture;
	private CCTexture2D _myBackgroundTexture;
	
	private float _myDamping = 0.99f;
	private float _myNormalHeightScale = 200;
	private float _myWaveInnerEdgesStrength = 1;
	private float _myRefraction = 200;
	
	private CCGraphics _myGraphics;
	
	public CCGPUWaterRipple(final CCGraphics g, final CCTexture2D theBackgroundTexture, final int theWidth, final int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myBackgroundTexture = theBackgroundTexture;
		_myWaveInnerEdgeTexture = new CCShaderBuffer(theWidth, theHeight);
		
		_mySineShader = new CCGPUSineShader(g);
		_mySimShader = new CCGPUWaterRippleSimulationShader(g);
		_myDrawShader = new CCGPUWaterDrawShader(g);
	
		_myCurrentCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myCurrentCellLocationsTexture.clear();
		_myPreviousCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myPreviousCellLocationsTexture.clear();
		_myTargetCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myTargetCellLocationsTexture.clear();
		
		_mySplashes = new ArrayList<CCGPUWaterSplash>();
	}
	
	public CCGPUWaterRipple(final CCGraphics g, final int theWidth, final int theHeight) {
		this(g,null, theWidth, theHeight);
	}
	
	private void applySplashes(CCGraphics g) {
		_myCurrentCellLocationsTexture.beginDraw();
		g.colorMask(true, false, false, false);
		g.clearColor();

		_mySineShader.start();
		g.beginShape(CCDrawMode.QUADS);
		for (int i = 0; i < _mySplashes.size(); i++) {
			CCGPUWaterSplash mySplash = _mySplashes.get(i);
			float x = mySplash._myX;
			float y = mySplash._myY;
			float r = mySplash._myRadius;

			_mySineShader.amplitude(mySplash._myAmplitude);
			g.vertex(x - r, y + r,0, 1);
			g.vertex(x - r, y - r,0, 0);
			g.vertex(x + r, y - r,1, 0);
			g.vertex(x + r, y + r,1, 1);
		}
		g.endShape();
		_mySineShader.end();

		g.noColorMask();
		_myCurrentCellLocationsTexture.endDraw();

		_mySplashes.clear();
	}
	
	public void beginDrawMask() {
		_myCurrentCellLocationsTexture.beginDraw();
	}
	
	public void endDrawMask() {
		_myCurrentCellLocationsTexture.endDraw();
	}
	
	public void beginDrawActiveArea() {
		_myCurrentCellLocationsTexture.beginDraw();
		_myGraphics.colorMask(true, false, false, false);
	}
	
	public void endDrawActiveArea() {
		_myGraphics.noColorMask();
		_myCurrentCellLocationsTexture.endDraw();
	}
	
	float _myAngle = 0;
	
	private void simulate(CCGraphics g){
	    
	    _mySimShader.currentCellLocations(_myCurrentCellLocationsTexture.attachment(0));
	    _mySimShader.previousCellLocations(_myPreviousCellLocationsTexture.attachment(0));
	    
	    _mySimShader.waveInnerEdges(_myWaveInnerEdgeTexture.attachment(0));
		
	    _mySimShader.start();

	    _mySimShader.damping(_myDamping);
	    _mySimShader.normalHeightScale(_myNormalHeightScale);
		_mySimShader.innerEdgesStrength(_myWaveInnerEdgesStrength); // 0.01f
	    
	    _myTargetCellLocationsTexture.clear();
	    _myTargetCellLocationsTexture.draw();
        
	    _mySimShader.end();
	    
	    // swap textures
	    CCShaderBuffer mySwap = _myPreviousCellLocationsTexture;
	    _myPreviousCellLocationsTexture = _myCurrentCellLocationsTexture;
	    _myCurrentCellLocationsTexture = _myTargetCellLocationsTexture;
	    _myTargetCellLocationsTexture = mySwap;
	}
	
	public void update(final float theDeltaTime) {
		applySplashes(_myGraphics);
		simulate(_myGraphics);
	}
	
	public void draw(CCGraphics g) {
		
		//pass water simulation texture
		_myDrawShader.heightMap(_myCurrentCellLocationsTexture.attachment(0).id());
		_myDrawShader.normalMap(_myCurrentCellLocationsTexture.attachment(1).id());
		if(_myBackgroundTexture != null)_myDrawShader.backgroundTexture(_myBackgroundTexture);
		
		_myDrawShader.start();
		
		_myDrawShader.refraction(_myRefraction);
		
		g.beginOrtho2D();
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, 0, 0, _myHeight);
		g.vertex(_myWidth, 0, _myWidth, _myHeight);
		g.vertex(_myWidth, _myHeight, _myWidth, 0);
		g.vertex(0, _myHeight, 0, 0);
		g.endShape();
		g.endOrtho2D();
		
        _myDrawShader.end();
	}

	public void addSplash(final float theX, final float theY, final float theRadius, final float theAmplitude) {
		_mySplashes.add(new CCGPUWaterSplash(theX, theY, theRadius, theAmplitude));
	}
	
	public void backgroundTexture(final CCTexture2D theBackgroundTexture) {
		_myBackgroundTexture = theBackgroundTexture;
	}

	/**
	 * @return the waveInnerEdgeTexture
	 */
	public float waveInnerEdgeStrength() {
		return _myWaveInnerEdgesStrength;
	}

	/**
	 * @param theWaveInnerEdgeTexture the waveInnerEdgeTexture to set
	 */
	public void waveInnerEdgeStrength(float theWaveInnerEdgeStrength) {
		_myWaveInnerEdgesStrength = theWaveInnerEdgeStrength;
	}

	/**
	 * @return the damping
	 */
	public float damping() {
		return _myDamping;
	}

	/**
	 * @param theDamping the damping to set
	 */
	public void damping(float theDamping) {
		_myDamping = theDamping;
	}

	/**
	 * @return the normalHeightScale
	 */
	public float normalHeightScale() {
		return _myNormalHeightScale;
	}

	/**
	 * @param theNormalHeightScale the normalHeightScale to set
	 */
	public void normalHeightScale(float theNormalHeightScale) {
		_myNormalHeightScale = theNormalHeightScale;
	}

	/**
	 * @return the refraction
	 */
	public float refraction() {
		return _myRefraction;
	}

	/**
	 * @param theRefraction the refraction to set
	 */
	public void refraction(float theRefraction) {
		_myRefraction = theRefraction;
	}
	
}
