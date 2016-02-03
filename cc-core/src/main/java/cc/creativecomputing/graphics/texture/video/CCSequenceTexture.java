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
package cc.creativecomputing.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;


/**
 * Use this class to play texture sequences. You can create a sequence texture
 * by passing an array of texture data objects, or files.
 * @author Christian Riekoff
 *
 */
public class CCSequenceTexture extends CCTexture2D implements CCMovie, CCUpdateListener{
	
	/**
	 * frame rate of the sequence
	 */
	private float _myFrameRate = 15;
	
	/**
	 * play back rate of the sequence
	 */
	private float _myRate = 1;
	
	/**
	 * Duration of the movie in seconds
	 */
	private float _myDuration = 0;
	
	/**
	 * Current time of the sequence in seconds
	 */
	private float _myTime = 0;
	
	/**
	 * true if the movie is looping otherwise false
	 */
	protected boolean _myIsLooping = false;
	
	/**
	 * true if the movie is running otherwise false
	 */
	private boolean _myIsRunning;

	/**
	 * Creates a new sequence texture from the given texture data objects.
	 * @param theApp reference to the active app needed for updating
	 * @param theTarget texture target can be <code>TEXTURE_RECT</code> or <code>TEXTURE_2D</code>
	 * @param theAttributes  attributes of the texture to be generated
	 * @param theTextureData array containing the texture data
	 */
	public CCSequenceTexture(CCApp theApp, CCTextureTarget theTarget, CCTextureAttributes theAttributes, CCTextureData[] theTextureData) {
		super(theTarget, theAttributes, theTextureData.length);
		
		for(CCTextureData myData:theTextureData) {
			data(myData);
			_myTextureID++;
		}
		_myTextureID = 0;
		_myIsRunning = false;
		
		_myDuration = _myTextureIDs.length / (_myFrameRate);
		
		theApp.addUpdateListener(this);
	}
	
	/**
	 * Creates a new sequence texture from the given texture data objects.
	 * @param theApp reference to the active app needed for updating
	 * @param theTarget texture target can be <code>TEXTURE_RECT</code> or <code>TEXTURE_2D</code>
	 * @param theAttributes  attributes of the texture to be generated
	 * @param theFiles array containing the image files
	 */
	public CCSequenceTexture(CCApp theApp, CCTextureTarget theTarget, CCTextureAttributes theAttributes, final String[] theFiles) {
		super(theTarget, theAttributes, theFiles.length);
		for(String file:theFiles){
			data(CCTextureIO.newTextureData(file));
			_myTextureID++;
		}
		_myTextureID = 0;
		_myIsRunning = false;
		
		_myDuration = _myTextureIDs.length / (_myFrameRate);
		
		if(theApp != null)theApp.addUpdateListener(this);
	}
	
	/**
	 * Updates the texture for playback
	 */
	public void update(float theDeltaTime) {
		if (_myIsRunning) {
			_myTime += theDeltaTime * _myRate;
			_myTextureID = (int)(_myTime / _myDuration * (_myTextureIDs.length - 1));
			
			if(_myTextureID >= _myTextureIDs.length){
				if(_myIsLooping){
					_myTextureID = 0;
					_myTime = 0;
				}else{
					_myIsRunning = false;
					_myTime = _myDuration;
				}
			}
			
			if(_myTextureID < 0){
				if(_myIsLooping){
					_myTextureID = (_myTextureIDs.length - 1);
					_myTime = _myDuration;
				}else{
					_myIsRunning = false;
					_myTime = 0;
				}
			}
		}
	}
	
	/**
	 * Use this to define the frame rate of you texture sequence
	 * @param theFrameRate frame rate of the texture sequence
	 */
	public void frameRate(final float theFrameRate) {
		_myFrameRate = theFrameRate;
		_myDuration = _myTextureIDs.length / (_myFrameRate);
	}
	
	public float frameRate() {
		return _myFrameRate;
	}

	public float duration() {
		return _myDuration;
	}

	public void goToBeginning() {
		time(0);
	}

	public boolean isRunning() {
		return _myIsRunning;
	}

	public void loop() {
		_myIsLooping = true;
		try {
			play();
			_myIsRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			_myIsRunning = false;
		}
	}

	public void loop(boolean theDoLoop) {
		_myIsLooping = theDoLoop;
	}

	public float progress() {
		return time() / (float) duration();
	}

	public void rate(float theRate) {
		_myRate = theRate;
	}

	public float rate() {
		return _myRate;
	}

	public void play() {
		play(false);
	}

	public void play(boolean theRestart) {
		if(theRestart) {
			_myTextureID = 0;
			_myTime = 0;
		}
		_myIsRunning = true;
	}

	public void stop() {
		pause();
		goToBeginning();
	}
	
	public void pause() {
		_myIsRunning = false;
	}

	public float time() {
		return _myTime;
	}

	public void time(float theNewtime) {
		_myTime = 0;
		_myTextureID = (int)(_myTime/_myDuration);
	}

	public float volume() {
		return 0;
	}

	public void volume(float theVolume) {
	}



}
