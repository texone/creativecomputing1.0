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
//  Copyright 2009 Wayne Keenan
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
//  FingerState.java
//
//  Created by Wayne Keenan on 30/05/2009.
//
//  wayne.keenan@gmail.com
//
package cc.creativecomputing.input.touch;

public class CCFingerState {
	
	private final String name;
	
	private CCFingerState(String name) { this.name=name;}
	
	public String toString() { return name; }
	
	public static final CCFingerState PRESSED   = new CCFingerState("PRESSED");
	public static final CCFingerState RELEASED  = new CCFingerState("RELEASED");
	public static final CCFingerState HOVER     = new CCFingerState("HOVER");
	public static final CCFingerState PRESSING  = new CCFingerState("PRESSING");
	public static final CCFingerState RELEASING = new CCFingerState("RELEASING");
	public static final CCFingerState TAP		  = new CCFingerState("TAP");
	public static final CCFingerState UNKNOWN_1 = new CCFingerState("UNKNOWN_1");
	public static final CCFingerState UNKNOWN   = new CCFingerState("UNKNOWN_?");
	
	public static CCFingerState getStateFor(int stateId) {
		CCFingerState state;
		switch (stateId) {
			case 1:		state = CCFingerState.UNKNOWN_1;		break;
			case 2:		state = CCFingerState.HOVER;		break;
			case 3:		state = CCFingerState.TAP;		break;
			case 4:		state = CCFingerState.PRESSED;	break;
			case 5:		state = CCFingerState.PRESSING;	break;
			case 6:		state = CCFingerState.RELEASING;	break;
			case 7:		state = CCFingerState.RELEASED;	break;
			default:	state = CCFingerState.UNKNOWN;	break;
		}
		
		return state;
	}
}
