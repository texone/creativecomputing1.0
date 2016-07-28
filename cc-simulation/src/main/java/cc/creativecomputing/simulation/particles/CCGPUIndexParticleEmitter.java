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
import java.util.LinkedList;
import java.util.List;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public class CCGPUIndexParticleEmitter implements CCGPUParticleEmitter{
	
	public class CCParticleWaitingList {
		
		private float _myTimeStep;
		private int _myOffset = 0;
		private List<CCGPUParticle>[] _myWaitLists;

		@SuppressWarnings("unchecked")
		public CCParticleWaitingList(float theTimeStep) {
			_myTimeStep = theTimeStep;
			
			// asume a default max lifetime of 120 s
			int myNumberOfSteps = (int)(120 / theTimeStep);
			_myWaitLists = new ArrayList[myNumberOfSteps];
		}
		
		public void add(CCGPUParticle theParticle) {
			_myPendingParticles.remove(theParticle);
			int myStep = (int)(theParticle.lifeTime() / _myTimeStep);
			myStep += _myOffset;
			myStep %= _myWaitLists.length;
			
			if(_myWaitLists[myStep] == null)_myWaitLists[myStep] = new ArrayList<CCGPUParticle>();
			_myWaitLists[myStep].add(theParticle);
		}
		
		private float _myStepTime = 0;
		private int _myCurrentWorkedIndex = 0;
		private List<CCGPUParticle> _myCurrentWaitList = null;
		
		private void handleCurrentWaitList(float theDeltaTime){
			if(_myCurrentWaitList == null)return;

			float myFramesPerStep = _myTimeStep / theDeltaTime;
			int myChecksPerFrame = CCMath.ceil(_myCurrentWaitList.size() / myFramesPerStep);
			
			for(int i = 0; i < myChecksPerFrame && _myCurrentWorkedIndex < _myCurrentWaitList.size(); i++, _myCurrentWorkedIndex++){
				CCGPUParticle myParticle = _myCurrentWaitList.get(_myCurrentWorkedIndex);
				
				if(myParticle.isPermanent()) {
					_myPendingParticles.add(myParticle);
				}else {
					_myFreeIndices.add(myParticle.index());
				}
			}
		}
		
		public void update(float theDeltaTime) {
			_myStepTime += theDeltaTime;
			
			handleCurrentWaitList(theDeltaTime);
			
			if(_myStepTime > _myTimeStep) {
				_myStepTime -= _myTimeStep;
				if(_myCurrentWaitList != null){
					for(;_myCurrentWorkedIndex < _myCurrentWaitList.size(); _myCurrentWorkedIndex++){
						CCGPUParticle myParticle = _myCurrentWaitList.get(_myCurrentWorkedIndex);
						
						if(myParticle.isPermanent()) {
							_myPendingParticles.add(myParticle);
						}else {
							_myFreeIndices.add(myParticle.index());
						}
					}
					_myCurrentWorkedIndex = 0;
					_myCurrentWaitList.clear();
				}
				
				if(_myWaitLists[_myOffset] != null) {
					_myCurrentWaitList = _myWaitLists[_myOffset];
				}
				_myOffset++;
				_myOffset %= _myWaitLists.length;
				
				int mySize = 0;
				for(List<?> myList:_myWaitLists){
					if(myList == null)continue;
					mySize += myList.size();
				}
			}
		}
		
		public void reset() {
			for(int i = 0; i < _myWaitLists.length; i++) {
				if(_myWaitLists[i] != null)_myWaitLists[i].clear();
			}
		}
	}
	
	private int _myNumberOfParticles;
	
	protected CCGPUParticles _myParticles;
	
	private List<CCGPUParticle> _myAllocatedParticles = new ArrayList<CCGPUParticle>();
	private List<CCGPUParticle> _myPendingParticles = new LinkedList<CCGPUParticle>();
	private List<CCGPUParticle> _myStateChanges = new ArrayList<CCGPUParticle>();
	private List<Integer> _myFreeIndices;
	
	private CCParticleWaitingList _myParticleWaitingList;
	protected final CCGPUParticle[] _myActiveParticlesArray;
	
	protected CCMesh _myEmitMesh;
	private FloatBuffer _myVertexBuffer;
	private FloatBuffer _myColorBuffer;
	private FloatBuffer _myPositionBuffer;
	private FloatBuffer _myInfoBuffer;
	private FloatBuffer _myVelocityBuffer;
	protected float[] _myFillArray;
	
	private int _myStart;
	
	protected double _myCurrentTime = 0;
	
	public CCGPUIndexParticleEmitter(CCGPUParticles theParticles, int theStart, int theNumberParticles) {
		_myParticles = theParticles;
		
		_myStart = theStart;
		
		_myNumberOfParticles = theNumberParticles;
		_myFreeIndices = new ArrayList<Integer>(_myNumberOfParticles);
		
		_myParticleWaitingList = new CCParticleWaitingList(0.5f);
		_myActiveParticlesArray = new CCGPUParticle[_myNumberOfParticles];
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			int myIndex = _myStart + i;
			_myActiveParticlesArray[i] = new CCGPUParticle(_myParticles, myIndex);
			_myFreeIndices.add(myIndex);
		}
		
		_myEmitMesh = new CCMesh(CCDrawMode.POINTS);
		_myVertexBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 3);
		_myColorBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 4);
		_myPositionBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 3);
		_myInfoBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 4);
		_myVelocityBuffer = CCBufferUtil.newDirectFloatBuffer(1000 * 3);
		_myFillArray = new float[1000 * 4];
	}
	
	public CCGPUIndexParticleEmitter(CCGPUParticles theParticles) {
		this(theParticles, 0, theParticles.size());
	}
	
	public List<CCGPUParticle> pendingParticles(){
		return _myPendingParticles;
	}
	
	/**
	 * Returns the number of currently active particles
	 * @return
	 */
	public int particlesInUse(){
		return size() - _myFreeIndices.size();
	}
	
	/**
	 * Returns the number of particles that can still be allocated from this particle system
	 * @return
	 */
	public int freeParticles() {
		return _myFreeIndices.size();
	}
	
	public int start() {
		return _myStart;
	}
	
	public int numberOfParticles() {
		return _myNumberOfParticles;
	}
	
	public int nextFreeId() {
		if (_myFreeIndices.isEmpty())
			return -1;

		return _myFreeIndices.get(_myFreeIndices.size() - 1);
	}
	
	public CCGPUParticle nextParticle(){
		if (_myFreeIndices.isEmpty())
			return null;
		
		int myNextFreeID = nextFreeId();
		int myIndex = myNextFreeID - _myStart;
		return _myActiveParticlesArray[myIndex];
	}
	
	public int xforIndex(int theIndex) {
		return (_myStart + theIndex) % _myParticles.width();
	}
	
	public int yforIndex(int theIndex) {
		return (_myStart + theIndex) / _myParticles.width();
	}
	
	public void changeParticle(CCGPUParticle theParticle) {
		_myParticleWaitingList.add(theParticle);
		_myStateChanges.add(theParticle);
	}
	
	public void kill(){
		for(CCGPUParticle myParticle:new ArrayList<>(pendingParticles())){
			myParticle.isPermanent(false);
			changeParticle(myParticle);
		}
	}
	
	public void kill(int theIndex){
		if(theIndex >= _myActiveParticlesArray.length)return;
		changeParticle(_myActiveParticlesArray[theIndex]);
	}

	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle emit(
		final CCColor theColor,
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		final boolean theIsPermanent
	){
		if(_myFreeIndices.isEmpty())return null;

		int myFreeIndex = _myFreeIndices.remove(_myFreeIndices.size() - 1);
//		
		return emit(myFreeIndex, theColor, thePosition, theVelocity, theLifeTime, theIsPermanent);
	}
	
	public CCGPUParticle emit(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		final boolean theIsPermanent
	){	
		return emit(CCColor.WHITE, thePosition, theVelocity, theLifeTime, theIsPermanent);
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle emit(
		final int theIndex,
		final CCColor theColor,
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		final boolean theIsPermanent
	){
//		_myAvailableIndices.remove(theIndex);
		int myIndex = theIndex - _myStart;
		CCGPUParticle myActiveParticle = _myActiveParticlesArray[myIndex];
		myActiveParticle.color().set(theColor);
		myActiveParticle.position().set(thePosition);
		myActiveParticle.velocity().set(theVelocity);
		myActiveParticle.timeOfDeath(_myCurrentTime + theLifeTime);
		myActiveParticle.lifeTime(theLifeTime);
		myActiveParticle.isPermanent(theIsPermanent);
		myActiveParticle.step(0);

		_myAllocatedParticles.add(myActiveParticle);
		_myParticleWaitingList.add(myActiveParticle);
		
		return myActiveParticle;
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * The number of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle emit(
		final CCColor theColor,
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime
	) {
		return emit(theColor, thePosition, theVelocity, theLifeTime, false);
	}
	
	public CCGPUParticle emit(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime
	) {
		return emit(CCColor.WHITE, thePosition, theVelocity, theLifeTime);
	}
	
	public void update(final float theDeltaTime) {
		_myParticleWaitingList.update(theDeltaTime);
	}
	
	@Override
	public void reset() {
		_myFreeIndices.clear();
		
		_myParticleWaitingList = new CCParticleWaitingList(0.5f);
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			int myIndex = _myStart + i;
			_myFreeIndices.add(myIndex);
		}
	}
	
	public int size() {
		return _myNumberOfParticles;
	}
	
	public CCGPUParticle particle(final int theID) {
		return _myActiveParticlesArray[theID - _myStart];
	}
	
	public void fillColorData(FloatBuffer theBuffer, List<CCGPUParticle> theParticles){
		int i = 0;
		for (CCGPUParticle myParticle:theParticles){
			_myFillArray[i * 4 + 0] = myParticle.color().r;
			_myFillArray[i * 4 + 1] = myParticle.color().g;
			_myFillArray[i * 4 + 2] = myParticle.color().b;
			_myFillArray[i * 4 + 3] = myParticle.color().a;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 4);
	}
	
	public void fillPositionData(FloatBuffer theBuffer, List<CCGPUParticle> theParticles){
		int i = 0;
		for (CCGPUParticle myParticle:theParticles){
			_myFillArray[i * 3 + 0] = myParticle.position().x;
			_myFillArray[i * 3 + 1] = myParticle.position().y;
			_myFillArray[i * 3 + 2] = myParticle.position().z;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 3);
	}
	
	public void fillInfoData(FloatBuffer theBuffer, List<CCGPUParticle> theParticles){
		int i = 0;
		for (CCGPUParticle myParticle:theParticles){
			_myFillArray[i * 4 + 0] = myParticle.age();
			_myFillArray[i * 4 + 1] = myParticle.lifeTime();
			_myFillArray[i * 4 + 2] = myParticle.isPermanent() ? 1 : 0;//, myParticle.step();
			_myFillArray[i * 4 + 3] = myParticle.step();
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 4);
	}
	
	public void fillVelocityData(FloatBuffer theBuffer, List<CCGPUParticle> theParticles){
		int i = 0;
		for (CCGPUParticle myParticle:theParticles){
			_myFillArray[i * 3 + 0] = myParticle.velocity().x;
			_myFillArray[i * 3 + 1] = myParticle.velocity().y;
			_myFillArray[i * 3 + 2] = myParticle.velocity().z;
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 3);
	}
	
	private void prepareBuffer(int theSize){
		if(theSize > _myEmitMesh.numberOfVertices()){
			_myVertexBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 3);
			_myColorBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 4);
			_myPositionBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 3);
			_myInfoBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 4);
			_myVelocityBuffer = CCBufferUtil.newDirectFloatBuffer(theSize * 3);
			_myFillArray = new float[theSize * 4];
		}else{
			_myVertexBuffer.limit(theSize * 3);
			_myColorBuffer.limit(theSize * 4);
			_myPositionBuffer.limit(theSize * 3);
			_myInfoBuffer.limit(theSize * 4);
			_myVelocityBuffer.limit(theSize * 3);
		}
		
		_myVertexBuffer.rewind();
		_myColorBuffer.rewind();
		_myPositionBuffer.rewind();
		_myInfoBuffer.rewind();
		_myVelocityBuffer.rewind();
	}
	
	public void transferEmitData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw();
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);
		
		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw();
	}
	
	private void transferEmits(CCGraphics theGraphics){
		if(_myAllocatedParticles.size() == 0)return;
		
		prepareBuffer(_myAllocatedParticles.size());
		
		for (CCGPUParticle myParticle:_myAllocatedParticles){
			_myVertexBuffer.put(myParticle.x() + 0.5f);
			_myVertexBuffer.put(myParticle.y() + 0.5f);
			_myVertexBuffer.put(0);
		}
		
		fillColorData(_myColorBuffer, _myAllocatedParticles);
		fillPositionData(_myPositionBuffer, _myAllocatedParticles);
		fillInfoData(_myInfoBuffer, _myAllocatedParticles);
		fillVelocityData(_myVelocityBuffer, _myAllocatedParticles);
		
		_myVertexBuffer.rewind();
		_myColorBuffer.rewind();
		_myPositionBuffer.rewind();
		_myInfoBuffer.rewind();
		_myVelocityBuffer.rewind();
		
		_myEmitMesh.clearAll();
		_myEmitMesh.vertices(_myVertexBuffer);
		_myEmitMesh.textureCoords(0, _myPositionBuffer, 3);
		_myEmitMesh.textureCoords(1, _myInfoBuffer, 4);
		_myEmitMesh.textureCoords(2, _myVelocityBuffer, 3);
		_myEmitMesh.textureCoords(3, _myColorBuffer, 4);
		
		transferEmitData(theGraphics);
		
		_myAllocatedParticles.clear();
	}
	
	public void transferInfoData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw(1);
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);

		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw();
	}
	
	private void transferColorData(CCGraphics g){
		_myParticles.dataBuffer().beginDraw(3);
		_myParticles.initValueShader().start();
		
		_myEmitMesh.draw(g);

		_myParticles.initValueShader().end();
		_myParticles.dataBuffer().endDraw();
	}
	
	private void transferChanges(CCGraphics theGraphics){
		if(_myStateChanges.size() == 0)return;
		
		prepareBuffer(_myStateChanges.size());
		
		for (CCGPUParticle myParticle:_myStateChanges){
			_myVertexBuffer.put(myParticle.x() + 0.5f);
			_myVertexBuffer.put(myParticle.y() + 0.5f);
			_myVertexBuffer.put(0);
		}
		
		fillInfoData(_myInfoBuffer, _myStateChanges);
		fillColorData(_myColorBuffer, _myStateChanges);
		
		_myVertexBuffer.rewind();
		_myInfoBuffer.rewind();
		
		_myEmitMesh.clearAll();
		_myEmitMesh.vertices(_myVertexBuffer);
		_myEmitMesh.textureCoords(1, _myInfoBuffer, 4);
		_myEmitMesh.textureCoords(3, _myColorBuffer, 4);
		
		transferInfoData(theGraphics);
		transferColorData(theGraphics);
		
		_myStateChanges.clear();
	}
	
	public void setData(CCGraphics theGraphics) {
		transferEmits(theGraphics);
		transferChanges(theGraphics);
	}
}
