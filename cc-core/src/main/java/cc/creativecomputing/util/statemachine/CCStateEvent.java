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
package cc.creativecomputing.util.statemachine;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.events.CCEvent;

public class CCStateEvent extends CCEvent{
	
	private Map<String, Object> _myParameterMap = new HashMap<>();
	
	private String _myId;
	
	public CCStateEvent(String theId){
		_myId = theId;
	}
	
	public Object parameter(String theParameter){
		return _myParameterMap.get(theParameter);
	}
	
	public CCStateEvent addParameter(String theKey, Object theParameter){
		_myParameterMap.put(theKey, theParameter);
		return this;
	}
	
	public float floatParameter(String theKey){
		return(Float)parameter(theKey);
	}
	
	public String id(){
		return _myId;
	}
}
