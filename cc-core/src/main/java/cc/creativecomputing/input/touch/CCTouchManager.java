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
package cc.creativecomputing.input.touch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alderstone.multitouch.mac.touchpad.TouchpadObservable;

import cc.creativecomputing.events.CCListenerManager;

/**
 * @author christianriekoff
 *
 */
public class CCTouchManager implements Iterable<CCTouch>{
	
	public static enum CCTouchImplementations{
		OSX_TRACKPAD
	}
	
	public static CCTouchManager createTouchManager(CCTouchImplementations theImplementation) {
		switch(theImplementation) {
		case OSX_TRACKPAD:
		default:
			return new CCTouchManager(new TouchpadObservable());
		}
	}
	
	public static interface CCTouchListener{
		public void onTouchAdd(CCTouch theTouch);
		public void onTouchUpdate(CCTouch theTouch);
		public void onTouchRemove(CCTouch theTouch);
	}
	
	private Map<Integer, CCTouch> _myTouchMap = new HashMap<Integer, CCTouch>();
	private CCListenerManager<CCTouchListener> _myEvents = CCListenerManager.create(CCTouchListener.class);
	
	private CCTouchProvider _myProvider;
	
	private CCTouchManager(CCTouchProvider theTouchProvider) {
		_myProvider = theTouchProvider;
		_myProvider.setManager(this);
	}
	
	public CCTouch touch(int theID) {
		synchronized (_myTouchMap) {
			return _myTouchMap.get(theID);
		}
	}

	public void onTouchAdd(CCTouch theTouch) {
		synchronized (_myTouchMap) {
			_myTouchMap.put(theTouch.id(), theTouch);
		}
		_myEvents.proxy().onTouchAdd(theTouch);
	}
	
	public void onTouchUpdate(CCTouch theTouch) {
		_myEvents.proxy().onTouchUpdate(theTouch);
	}
	
	public void onTouchRemove(CCTouch theTouch) {
		_myEvents.proxy().onTouchRemove(theTouch);
		synchronized (_myTouchMap) {
			_myTouchMap.remove(theTouch.id());
		}
	}

	@Override
	public Iterator<CCTouch> iterator() {
		synchronized (_myTouchMap) {
			return new ArrayList<CCTouch>(_myTouchMap.values()).iterator();
		}
	}
}
