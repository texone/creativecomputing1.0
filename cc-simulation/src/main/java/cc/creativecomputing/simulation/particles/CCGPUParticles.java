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
package cc.creativecomputing.simulation.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.impulses.CCGPUImpulse;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUParticleRenderer;
import cc.creativecomputing.util.logging.CCLog;

/**
 * This particle system renders particles as points. You can add different forces
 * and constraints to change the behavior of the particles.
 * 
 * The data of the particles is stored in textures. Implementation wise the data is written into a framebuffer
 * with 4 attachments. Holding the information in the following layout:
 * <ul>
 * <li>attachment0: positions as xyz</li>
 * <li>attachment1: infos as age / lifetime / state</li>
 * <li>attachment2: velocities as xyz</li>
 * <li>attachment3: colors as rgba</li>
 * </ul>
 * You can use this data and overwrite them.
 * @author info
 * @demo cc.creativecomputing.gpu.particles.demo.CCParticlesNoiseFlowFieldTest
 * @see CCGPUQuadParticles
 */
public class CCGPUParticles{
	
	protected Map<Integer, CCVector3f> _myPositionUpdates = new HashMap<Integer, CCVector3f>();
	protected List<CCGPUParticle> _myLifetimeUpdates = new ArrayList<CCGPUParticle>();
	
	private List<CCGPUParticleEmitter> _myEmitter = new ArrayList<CCGPUParticleEmitter>();
	
	protected CCGraphics _myGraphics;
	protected List<CCGPUForce> _myForces;
	protected List<CCGPUConstraint> _myConstraints;
	protected List<CCGPUImpulse> _myImpulses;
	
	protected final int _myWidth;
	protected final int _myHeight;
	
	protected CCGPUUpdateShader _myUpdateShader;
	
	protected CCCGShader _myInitValue01Shader;
	protected CCCGShader _myInitValue0Shader;
	
	protected CCShaderBuffer _myCurrentDataTexture;
	protected CCShaderBuffer _myDestinationDataTexture;
	
	protected double _myCurrentTime = 0;
	
	protected FloatBuffer _myPositionBuffer;
	protected FloatBuffer _myVelocityBuffer;
	
	private CCGPUParticleRenderer _myParticleRender;
	
	/**
	 * <p>
	 * Creates a new particle system. To create a new particle system you have to
	 * pass the CCGraphics instance and a list with forces. You can also pass
	 * a list of constraints that act as boundary so that the particles bounce at
	 * collision.
	 * </p>
	 * <p>
	 * The number of particles you can create depends on the size of the texture
	 * that holds the particle data on the gpu. You can define this size by passing
	 * a width and height value. The number of particles you can allocate is 
	 * width * height.
	 * </p>
	 * <p>
	 * How the particles are drawn is defined by a shader. You can pass a custom
	 * shader to the particle system to define how the particles are drawn. To
	 * create your own shader you need to extend the CCGPUDisplayShader and write your
	 * own cg shader.
	 * </p>
	 * 
	 * @param g graphics object used to initialize shaders and meshes for drawing
	 * @param theDisplayShader custom shader for displaying the particles
	 * @param theForces list with the forces applied to the particles
	 * @param theConstraints list with constraints applied to the particles
	 * @param theWidth width of particle system texture
	 * @param theHeight height of the particle system texture
	 */
	public CCGPUParticles(
		final CCGraphics g, final CCGPUParticleRenderer theRender,
		final List<CCGPUForce> theForces, 
		final List<CCGPUConstraint> theConstraints, 
		final List<CCGPUImpulse> theImpulse, 
		final int theWidth, final int theHeight
	){
		_myGraphics = g;
		_myForces = theForces;
		_myConstraints = theConstraints;
		_myImpulses = theImpulse;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		for(CCGPUForce myForce:theForces) {
			myForce.setSize(g, theWidth, theHeight);
		}
		
		_myInitValue01Shader = new CCCGShader(null,CCIOUtil.classPath(this, "shader/initvalue01.fp"));
		_myInitValue01Shader.load();
		
		_myInitValue0Shader = new CCCGShader(null,CCIOUtil.classPath(this, "shader/initvalue.fp"));
		_myInitValue0Shader.load();
		
		_myCurrentDataTexture = new CCShaderBuffer(32,4,4,_myWidth,_myHeight);
		g.clearColor(0,0,0,0);
		_myCurrentDataTexture.beginDraw(0);
		g.clear();
		_myCurrentDataTexture.endDraw();
		_myCurrentDataTexture.beginDraw(1);
		g.clear();
		_myCurrentDataTexture.endDraw();
		g.clearColor(0);
		
		_myDestinationDataTexture = new CCShaderBuffer(32,4,4,_myWidth,_myHeight);
		
		_myParticleRender = theRender;
		_myParticleRender.setup(this);
		_myUpdateShader = new CCGPUUpdateShader(this, g,theForces, theConstraints, theImpulse,_myWidth,_myHeight);
		
		reset();
	}
	
	public CCGPUParticles(
		CCGraphics theG, 
		CCGPUParticleRenderer theRender, 
		List<CCGPUForce> theForces, 
		List<CCGPUConstraint> theConstraints, 
		int theWidth, int theHeight
	) {
		this(theG, theRender, theForces, theConstraints, new ArrayList<CCGPUImpulse>(), theWidth, theHeight);
	}

	public CCGPUParticles(CCGraphics g, List<CCGPUForce> theForces, List<CCGPUConstraint> theConstraints, int theWidth, int theHeight) {
		this(g, new CCGPUParticlePointRenderer(), theForces, theConstraints, theWidth, theHeight);
	}

	public CCGPUParticles(CCGraphics g, List<CCGPUForce> theForces, List<CCGPUConstraint> theConstraints) {
		this(g,theForces, theConstraints,200,200);
	}

	public CCGPUParticles(CCGraphics g, List<CCGPUForce> theForces) {
		this(g, theForces, new ArrayList<CCGPUConstraint>());
	}
	
	public void addEmitter(CCGPUParticleEmitter theEmitter) {
		_myEmitter.add(theEmitter);
	}
	
	public CCCGShader initValueShader() {
		return _myInitValue01Shader;
	}
	
	public double currentTime() {
		return _myCurrentTime;
	}
	
	public void reset(){

		for(CCGPUParticleEmitter myEmitter:_myEmitter) {
			myEmitter.reset();
		}
		
		_myCurrentDataTexture.clear();
		
		_myCurrentDataTexture.beginDraw();
		_myInitValue01Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myWidth * _myHeight; i++){
			_myGraphics.textureCoords(0, Float.MAX_VALUE,Float.MAX_VALUE,Float.MIN_VALUE);
			_myGraphics.textureCoords(1, 1, 1, 1);
			_myGraphics.textureCoords(2, 0, 0, 0);
			_myGraphics.textureCoords(3, 1, 1, 1);
			_myGraphics.vertex(i % _myWidth,i / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue01Shader.end();
		_myCurrentDataTexture.endDraw();
		
		for(CCGPUForce myForce:_myForces) {
			myForce.reset();
		}
	}
	
	/**
	 * Returns the width of the texture containing the particle data
	 * @return width of the particle texture
	 */
	public int width() {
		return _myWidth;
	}
	
	/**
	 * Returns the height of the texture containing the particle data
	 * @return height of the particle texture
	 */
	public int height() {
		return _myHeight;
	}
	
	public int size() {
		return _myWidth * _myHeight;
	}
	
	/**
	 * Returns the texture with the current positions of the particles.
	 * @return texture containing the positions of the particles
	 */
	public CCShaderBuffer dataBuffer() {
		return _myCurrentDataTexture;
	}
	
	/**
	 * Returns the position of the particle. This is useful as particle data is stored on the gpu
	 * and there for not accessible on the cpu side. Be aware that is time consuming and should only
	 * be used for a couple of particles.
	 * @param theParticle the particle to query
	 * @return the position of the given particle
	 */
	public CCVector3f position(CCGPUParticle theParticle) {
		return position(theParticle, new CCVector3f());
	}
	
	/**
	 * 
	 * @param theParticle the particle t query
	 * @param theVector vector to store the position
	 * @return the position of the particle as vector
	 */
	public CCVector3f position(CCGPUParticle theParticle, CCVector3f theVector){
		FloatBuffer myResult = _myCurrentDataTexture.getData(theParticle.x(), theParticle.y(), 1, 1);
		theVector.x = myResult.get();
		theVector.y = myResult.get();
		theVector.z = myResult.get();
		return theVector;
	}

	public CCShaderBuffer destinationDataTexture() {
		return _myDestinationDataTexture;
	}
	
	/**
	 * Set the absolute position of the particle referenced by theIndex.
	 * @param theIndex index of the target particle
	 * @param thePosition target position of the particle
	 */
	public void setPosition(int theIndex, CCVector3f thePosition) {
		_myPositionUpdates.put(theIndex, thePosition);
	}
	
	private void updateManualPositionChanges() {
		
		if (_myPositionUpdates.size() == 0) {
			return;
		}
		
		// Render manually changed positions into the texture.
		_myCurrentDataTexture.beginDraw(0);
		_myInitValue0Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
	
		Iterator<Entry<Integer, CCVector3f>> it = _myPositionUpdates.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<Integer, CCVector3f> pairs = (Map.Entry<Integer, CCVector3f>)it.next();
	        
	        _myGraphics.textureCoords(0, pairs.getValue());
			_myGraphics.vertex(pairs.getKey() % _myWidth, 
					           pairs.getKey() / _myWidth);
	    }
	    
		_myGraphics.endShape();
		
		_myInitValue0Shader.end();
		_myCurrentDataTexture.endDraw();
		
		_myPositionUpdates.clear();
	}
	
	/**
	 * Update the lifetime of the given particle to what is specified in 
	 * the particle instance.
	 * @param theParticle particle instance containing new lifetime data
	 */
	public void updateLifecyle(CCGPUParticle theParticle) {
		_myLifetimeUpdates.add(theParticle);
	}
	
	private void initializeNewParticles(){
		// Render velocity.
		
		// Render current position into texture.
		for(CCGPUParticleEmitter myEmitter:_myEmitter) {
			myEmitter.setData(_myGraphics);
		}
		
	}
	
	private void changeStates() {
//		_myCurrentDataTexture.beginDraw(1);
//		_myInitValue0Shader.start();
//		_myGraphics.beginShape(CCDrawMode.POINTS);
//		for(CCGPUParticleEmitter myEmitter:_myEmitter) {
//			for (CCGPUParticle myChangedParticle:myEmitter.stateChangedParticles()){
//				_myGraphics.textureCoords(0, myChangedParticle.age(), myChangedParticle.lifeTime(), myChangedParticle.isPermanent() ? 1 : 0, myChangedParticle.step());
//				_myGraphics.vertex(myChangedParticle.x(),myChangedParticle.y());
//			}
//			myEmitter.stateChangedParticles().clear();
//		}
//		_myGraphics.endShape();
//		
//		_myInitValue0Shader.end();
//		_myCurrentDataTexture.endDraw();
	}
	
	protected void beforeUpdate() {
		initializeNewParticles();
		changeStates();
	}
	
	private void cleanUpParticles() {
//		if(_myActiveParticles.size() <= 0)
//			return;
//		
//		_myCurrentPositionTexture.beginDraw(1);
//		_myInitValue1Shader.start();
//		_myGraphics.beginShape(CCDrawMode.POINTS);
//				
//		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
//			CCGPUParticle myParticle = _myActiveParticles.poll();
//			if(myParticle.index == -1) continue;
//			_myAvailableIndices.add(myParticle.index);
//			_myActiveParticlesArray[myParticle.index].index = -1;
//			
//			_myGraphics.textureCoords(0, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
//			_myGraphics.textureCoords(1, 0, 0, 1, 0);
//			_myGraphics.vertex(myParticle.x() + 0.5f, myParticle.y() + 0.5f);
//		}
//		
//		_myGraphics.endShape();
//		_myInitValue1Shader.end();
//		_myCurrentPositionTexture.endDraw();
	}
	
	protected void afterUpdate(){
		updateManualPositionChanges();
//		updateManualLifetimeReset();
		cleanUpParticles();
	}
	
	public void update(final float theDeltaTime){
		if(theDeltaTime <= 0)return;
		
		for(CCGPUParticleEmitter myEmitter:_myEmitter) {
			myEmitter.update(theDeltaTime);
		}
		
		_myGraphics.pushAttribute();
		_myGraphics.noBlend();
		beforeUpdate();
		
		for(CCGPUForce myForce:_myForces) {
			myForce.update(theDeltaTime);
		}
		
		for(CCGPUConstraint myConstraint:_myConstraints) {
			myConstraint.update(theDeltaTime);
		}
		
		for(CCGPUImpulse myImpulse:_myImpulses) {
			myImpulse.update(theDeltaTime);
		}

		_myUpdateShader.data(_myCurrentDataTexture);
		_myUpdateShader.deltaTime(theDeltaTime);
		_myUpdateShader.start();
		_myDestinationDataTexture.draw();
		_myUpdateShader.end();
		
		swapDataTextures();
		
		afterUpdate();
		_myCurrentTime += theDeltaTime;
		_myParticleRender.update(theDeltaTime);
		_myGraphics.popAttribute();
	}
	
	public void swapDataTextures(){
		CCShaderBuffer myTemp = _myDestinationDataTexture;
		_myDestinationDataTexture = _myCurrentDataTexture;
		_myCurrentDataTexture = myTemp;
	}
	
	public void draw() {
		_myParticleRender.draw(_myGraphics);
	}
	
	public void staticPositions(CCTexture2D theStaticPositions){
		_myUpdateShader.staticPositions(theStaticPositions);
	}
	
	public void staticPositionBlend(float theBlend){
		_myUpdateShader.staticPositionBlend(theBlend);
	}
	
	public CCGPUParticleRenderer renderer() {
		return _myParticleRender;
	}
}
