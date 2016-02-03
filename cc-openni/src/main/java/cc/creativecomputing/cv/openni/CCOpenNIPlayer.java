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
package cc.creativecomputing.cv.openni;

import org.openni.Generator;
import org.openni.Player;
import org.openni.PlayerSeekOrigin;
import org.openni.StatusException;

/**
 * A Player node is a Production Node that allows playing of a recording. 
 * It supports all Production Node functions, and adds additional functions.
 * @author christianriekoff
 *
 */
public class CCOpenNIPlayer {

	private Player _myPlayer;
	private Generator _myGenerator = null;
	
	private int _myLoopStart = 0;
	private int _myLoopEnd = 0;
	private boolean _myDoRepeat = true;
	
	CCOpenNIPlayer(Player thePlayer) {
		_myPlayer = thePlayer;
		_myLoopEnd = numberOfFrames() - 1;
		repeat(true);
	}
	
	void generator(Generator theGenerator){
		_myGenerator = theGenerator;
	}
	
	/**
	 * Gets the playback speed.
	 * @see #playBackSpeed(double)
	 * @return speed ratio
	 */
	public double playBackSpeed() {
		return _myPlayer.getPlaybackSpeed();
	}
	
	/**
	 * Sets the playback speed, as a ratio of the time passed in the recording. 
	 * A value of 1.0 means the player will try to output frames in the rate they 
	 * were recorded (according to their timestamps). A value bigger than 1.0 means 
	 * fast-forward, and a value between 0.0 and 1.0 means slow-motion.
	 * @param theSpeed The speed ratio
	 */
	public void playBackSpeed(double theSpeed) {
		try {
			_myPlayer.setPlaybackSpeed(theSpeed);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Retrieves the number of frames of a specific generator played by a player.
	 * @param theGenerator handle to the generator
	 * @return the number of frames
	 */
	public int numberOfFrames() {
		if(_myGenerator == null)return 0;
		try {
			return _myPlayer.getNumberOfFrames(_myGenerator);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Returns the current frame of the player.
	 * @return the current frame of the player
	 */
	public int frame(){
		if(_myGenerator == null)return 0;
		try {
			return _myPlayer.tellFrame(_myGenerator);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Gets the name of the format supported by a player.
	 * @return
	 */
	public String format() {
		return _myPlayer.getFormat();
	}
	
	/**
	 * Gets the player's source, i.e where the played events come from.
	 * @return
	 */
	public String source() {
		try {
			return _myPlayer.getSource();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Determines whether the player will automatically rewind to the 
	 * beginning of the recording when reaching the end.
	 */
	public void repeat(boolean theRepeat) {
		_myDoRepeat = theRepeat;
	}
	
	/**
	 * Seeks the player to a specific frame of a specific played node, 
	 * so that playing will continue from that frame onwards.
	 * @param theGenerator A handle to the generator.
	 * @param theFrame The number of frames to move
	 */
	public void frame(int theFrame) {
		if(_myGenerator == null)return;
		try {
			_myPlayer.seekToFrame(_myGenerator, PlayerSeekOrigin.SET, theFrame);
		} catch (StatusException e) {
//			throw new CCOpenNIException(e);
		}
	}
	
	public void loop(int theStart, int theEnd){
		if(theStart > numberOfFrames() - 1)return;
		if(theEnd > numberOfFrames() - 1)return;
		if(theStart > theEnd)return;
		_myLoopStart = theStart;
		_myLoopEnd = theEnd;
	}
	
	public void update(final float theDeltaTime){
		if(_myLoopEnd < 0)_myLoopEnd = numberOfFrames() - 1;
		if(!_myDoRepeat)return;
		int myFrame = frame();
		if(myFrame < _myLoopStart || myFrame > _myLoopEnd){
			frame(_myLoopStart);
		}
	}
}
