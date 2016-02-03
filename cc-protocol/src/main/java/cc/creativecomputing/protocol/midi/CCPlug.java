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
package cc.creativecomputing.protocol.midi;

import java.lang.reflect.Method;

/**
 * A Plug is the invocation of a method to handle incoming MidiEvents.
 * These methods are plugged by reflection, so a plug needs the name 
 * of this method and the object where it is declared.
 * @author tex
 *
 */
class CCPlug{
	
	static enum CCPlugType{
		MIDIEVENT, NOTE, CONTROLLER, PROGRAMCHANGE
	}

	/**
	 * The plugged method
	 */
	private final Method method;

	/**
	 * Name of the method to plug
	 */
	private final String methodName;

	/**
	 * Object containg the method to plug
	 */
	private final Object object;
	
	/**
	 * Class of the object containing the method to plug
	 */
	private final Class<?> objectClass;
	
	/**
	 * Kind of Parameter that is handled by the plug can be
	 * NOTE, Controller, Program Change or a MidiEvent at general
	 */
	private CCPlugType _myPlugType;
	
	private int triggerValue = -1;

	/**
	 * Initializes a new Plug by a method name and the object 
	 * declaring the method.
	 * @param theObject
	 * @param theMethodName
	 */
	public CCPlug(
		final Object theObject,
		final String theMethodName,
		final int theValue
	){
		object = theObject;
		objectClass = object.getClass();
		methodName = theMethodName;
		method = initPlug();
		triggerValue = theValue;
	}
	
	CCPlugType plugType(){
		return _myPlugType;
	}
	
	/**
	 * @throws Exception 
	 * 
	 *
	 */
	private boolean checkParameter(final Class<?>[] theObjectMethodParams) throws Exception{
		if(theObjectMethodParams.length == 1){
			final Class<?> paramClass = theObjectMethodParams[0];
			if(paramClass == CCMidiMessage.class){
				_myPlugType = CCPlugType.MIDIEVENT;
				return true;
			}else if(paramClass == CCNote.class){
				_myPlugType = CCPlugType.NOTE;
				return true;
			}else if(paramClass == CCController.class){
				_myPlugType = CCPlugType.CONTROLLER;
				return true;
			}else if(paramClass == CCProgramChange.class){
				_myPlugType = CCPlugType.PROGRAMCHANGE;
				return true;
			}
		}
		throw new Exception();
	}

	/**
	 * Initializes the method that has been plugged.
	 * @return
	 */
	private Method initPlug(){		
		if (methodName != null && methodName.length() > 0){
			final Method[] objectMethods = objectClass.getDeclaredMethods();
			
			for (int i = 0; i < objectMethods.length; i++){
				objectMethods[i].setAccessible(true);
				
				if (objectMethods[i].getName().equals(methodName)){
					final Class<?>[] objectMethodParams = objectMethods[i].getParameterTypes();
					try{
						checkParameter(objectMethodParams);
						return objectClass.getDeclaredMethod(methodName, objectMethodParams);
					}catch (Exception e){
						break;
					}
				}
			}
		}
		throw new RuntimeException("Error on plug: >" +methodName +  "< You can only plug methods that have a MidiEvent, a Note, a Controller or a ProgramChange as Parameter");
	}
	
	/**
	 * Calls the plug by invoking the method given by the plug.
	 * @param theMidiEvent
	 */
	void callPlug(final CCMidiMessage theMidiEvent){
       try{
      	 if(theMidiEvent.data1() == triggerValue || triggerValue == -1)
			method.invoke(object,new Object[]{theMidiEvent});
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error on calling plug: " +methodName);
		}
   }
}
