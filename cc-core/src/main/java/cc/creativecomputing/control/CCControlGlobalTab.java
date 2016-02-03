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
package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.xml.CCXMLElement;


public class CCControlGlobalTab extends CCControlPresetTab{
	
	private Map<String, List<Integer>> _myTabPresetMap = new HashMap<String, List<Integer>>();
	private Map<String, CCControlPresetTab> _myTabMap = new HashMap<String, CCControlPresetTab>();

	public CCControlGlobalTab(CCControlUI theUI, String theLabel, float theX, float theY, float theWidth, float theHeight, float theButtonOffset) {
		super(theUI, theLabel, theX, theY, theWidth, theHeight, theButtonOffset);
	}

	@Override
	protected void onAddPreset() {
//		for(String myTabName:_myUI.tabMap().keySet()){
//			
//		}
	}

	@Override
	protected void onDeletePreset(int thePreset) {
//		for(List<Integer> myPresets:_myTabPresetMap.values()){
//			
//		}
	}

	@Override
	public void preset(int thePreset) {
		thePreset = CCMath.constrain(thePreset, 0, numberOfPresets());
		super.preset(thePreset);
		
	}

	public void addTab(final String theTabName, final CCControlPresetTab theTab){
		_myTabPresetMap.put(theTabName, new ArrayList<Integer>());
		_myTabMap.put(theTabName,theTab);
	}
	
	public void setPresets(final CCXMLElement theElement){
		
	}
	
	@Override
	public CCXMLElement toXML(){
		CCXMLElement myResult = new CCXMLElement("globalsettings");
		
		myResult.addAttribute("selectedpreset", preset());
		myResult.addAttribute("numberofpresets", numberOfPresets());
		myResult.addAttribute("label", "global");
		
		CCXMLElement myPresetNamesXML = new CCXMLElement("presetnames");
		for(String myPresetName:_myPresetControl.presetNames()){
			CCXMLElement myPresetNameXML = new CCXMLElement("presetname");
			myPresetNameXML.addAttribute("name", myPresetName);
			myPresetNamesXML.addChild(myPresetNameXML);
		}
		myResult.addChild(myPresetNamesXML);
		
		CCXMLElement myPresetsXML = new CCXMLElement("presets");
		for(String myTabName:_myTabPresetMap.keySet()){
			CCXMLElement myPresetXML = new CCXMLElement("preset");
			myPresetXML.addAttribute("name", myTabName);
			
			StringBuilder myStringBuilder = new StringBuilder();
			for(int myValue:_myTabPresetMap.get(myTabName)){
				myStringBuilder.append(myValue);
				myStringBuilder.append(" ");
			}
			myStringBuilder.deleteCharAt(myStringBuilder.length()-1);
			myPresetXML.addContent(myStringBuilder.toString());
			
			myPresetsXML.addChild(myPresetXML);
		}
		
		return myResult;
	} 
}
