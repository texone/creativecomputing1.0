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
package cc.creativecomputing;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author christianriekoff
 *
 */
public abstract class CCEventQueue<EventType> {

	private Queue<EventType> _myEventQueue;
	
	public CCEventQueue() {
		_myEventQueue = new LinkedList<EventType>();
	}
	
	public void enqueueEvent(EventType theEvent) {
		synchronized (_myEventQueue){
			_myEventQueue.add(theEvent);
		}
	}
	
	public void dequeueEvents() {
		synchronized (_myEventQueue){
			while(!_myEventQueue.isEmpty()) {
				handleEvent(_myEventQueue.poll());
			}
		}
	}
	
	public abstract void handleEvent(EventType theEvent);
}
