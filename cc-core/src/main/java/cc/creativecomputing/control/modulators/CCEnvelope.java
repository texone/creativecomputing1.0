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
package cc.creativecomputing.control.modulators;

import cc.creativecomputing.control.timeline.CCTimedData;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCEnvelope implements CCUpdateListener{

	private CCTimedData[] _myTimedDatas;
	
	private float _myTime = Float.MIN_VALUE;
	private float _myValue;
	
	private float _myMin = 0;
	private float _myMax = 1;
	
	private int _myIndex = 0;
	
	public CCEnvelope() {
		
	}

    public float min() {
        return _myMin;
    }

    public float max() {
        return _myMax;
    }
	
	public void min(final float theMin) {
		_myMin = theMin;
	}
	
	public void max(final float theMax) {
		_myMax = theMax;
	}

    public CCTimedData data(final int theIndex) {
        if (theIndex < _myTimedDatas.length) {
            return _myTimedDatas[theIndex];
        } else {
            return null;
        }
    }

	/* (non-Javadoc)
	 * @see cc.creativecomputing.events.CCUpdateListener#update(float)
	 */
	public void update(float theDeltaTime) {
		_myTime += theDeltaTime;
		if(_myTimedDatas == null){
			return;
		}
		if(_myIndex >= _myTimedDatas.length) {
			return;
		}
		_myTimedDatas[_myIndex].time(_myTime);
		_myValue = _myTimedDatas[_myIndex].value(_myTime);
	}
	
	public void play() {
		_myTime = 0;
	}
	
	public void play(final int theIndex) {
		_myIndex = theIndex;
		_myTime = 0;
	}
	
	public float value() {
		return CCMath.blend(_myMin, _myMax, _myValue);
	}
	
	public CCEnvelope instance(){
		CCEnvelope myResult = new CCEnvelope();
		myResult._myMin = _myMin;
		myResult._myMax = _myMax;
		myResult._myTimedDatas = _myTimedDatas;
		return myResult;
	}
}
