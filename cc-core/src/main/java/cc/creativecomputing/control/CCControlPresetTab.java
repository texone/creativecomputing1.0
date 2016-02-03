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
import java.util.List;

import cc.creativecomputing.control.ui.CCUIButton;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;
import cc.creativecomputing.control.ui.CCUITab;
import cc.creativecomputing.control.ui.CCUITextBox;
import cc.creativecomputing.control.ui.layout.CCUIColumnLayoutManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCControlPresetTab extends CCUITab{
	
	protected class CCUIPresetControl extends CCUIComponent{
		private List<String> _myPresetNames = new ArrayList<String>();
		private int _myChoosenPreset = 0;
		private CCUIButton _myPreviousePresetButton;
		private CCUIButton _myNextPresetButton;
		private CCUIButton _myAddPresetButton;
		private CCUIButton _myDeletePresetButton;
		private CCUITextBox _myTextBox;
		
		private CCControlPresetTab _myTab;
		
		private float _mySliderOffset = 0;
		
		public CCUIPresetControl(final CCControlPresetTab theTab){
			super("presets", 0, 0, 0, 0);
			_myDrawLabel = false;
			_myTab = theTab;
			_myTextBox = new CCUITextBox("Preset:0",19 ,_mySliderOffset,50,14);
			_myTextBox.isSerialized(false);
			add(_myTextBox);
			
			_myPreviousePresetButton = new CCUIButton("-",false,0,_mySliderOffset,14,14);
			_myPreviousePresetButton.isSerialized(false);
			_myPreviousePresetButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					if(_myChoosenPreset > 0){
						changePreset(_myChoosenPreset - 1);
					}
				}
				
			});
			add(_myPreviousePresetButton);
			
			_myNextPresetButton = new CCUIButton("+",false,74 ,_mySliderOffset,14,14);
			_myNextPresetButton.isSerialized(false);
			_myNextPresetButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					if(_myChoosenPreset < _myPresetNames.size()-1){
						changePreset(_myChoosenPreset + 1);
					}
				}
			});
			add(_myNextPresetButton);

			_mySliderOffset += _myUI.space() + _myUI.defaultHeight();
			_myAddPresetButton = new CCUIButton("add preset",false,0 ,_mySliderOffset,70,14);
			_myAddPresetButton.isSerialized(false);
			_myAddPresetButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					_myChoosenPreset = _myPresetNames.size();
					_myPresetNames.add("Preset:"+_myChoosenPreset);
					_myTextBox.label(_myPresetNames.get(_myChoosenPreset));
					_myTab.createPreset();
					onAddPreset();
				}
				
			});
			add(_myAddPresetButton);
			
			_myDeletePresetButton = new CCUIButton("delete preset",false,75 ,_mySliderOffset,75,14);
			_myDeletePresetButton.isSerialized(false);
			_myDeletePresetButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					if(_myPresetNames.size() > 1){
						int myPresetToDelete = _myChoosenPreset;
						_myPresetNames.remove(_myChoosenPreset);
						if(_myChoosenPreset != 0)_myChoosenPreset--;
						_myTextBox.label(_myPresetNames.get(_myChoosenPreset));
						_myTab.deletePreset();
						onDeletePreset(myPresetToDelete);
					}
				}
				
			});
			add(_myDeletePresetButton);
			_mySliderOffset += _myUI.space() + _myUI.defaultHeight();
		}
		
		public void changePreset(final int thePreset){
			_myChoosenPreset = thePreset;
			_myTextBox.label(_myPresetNames.get(_myChoosenPreset));
			CCControlPresetTab.super.preset(_myChoosenPreset);
		}
		
		public int numberOfPresets(){
			return _myPresetNames.size();
		}
		
		public List<String> presetNames(){
			return _myPresetNames;
		}
	}
	
	protected CCUIPresetControl _myPresetControl;

	/**
	 * @param theLabel
	 * @param thePosition
	 * @param theDimension
	 */
	public CCControlPresetTab(final CCXMLElement theXML, final CCControlUI theUI, String theLabel, CCVector2f thePosition, CCVector2f theDimension, float theButtonOffset) {
		super(theUI, theLabel, thePosition, theDimension, theButtonOffset);
		
		_myLayoutManager = new CCUIColumnLayoutManager(this, theUI.defaultWidth() + 100, theUI.space() + 20, 10, 30);
		
		_myPresetControl = new CCUIPresetControl(this);
		add(_myPresetControl);
		((CCUIColumnLayoutManager)_myLayoutManager).yOffset(100);
		
		if(theXML != null){
			CCXMLElement myPresetNamesXML = theXML.child("presetnames");
			for(CCXMLElement myPresetNameXML:myPresetNamesXML.children()){
				_myPresetControl._myPresetNames.add(myPresetNameXML.attribute("name"));
			}
			_myXML = theXML;
		}else{
			_myPresetControl._myPresetNames.add("Preset:0");
		}
		
	}

	/**
	 * @param theLabel
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCControlPresetTab(final CCControlUI theUI, String theLabel, float theX, float theY, float theWidth, float theHeight, final float theButtonOffset) {
		super(theUI, theLabel, 0, 0, theWidth, theHeight, theButtonOffset);
		_myLayoutManager = new CCUIColumnLayoutManager(this, theUI.defaultWidth() + 100, theUI.space() + 20, 10, 30);
		_myPresetControl = new CCUIPresetControl(this);
		((CCUIColumnLayoutManager)_myLayoutManager).yOffset(100);
	}
	

	public CCControlPresetTab(final CCXMLElement theXML, final CCControlUI theUI, String theLabel, float theX, float theY, float theWidth, float theHeight, float theButtonOffset) {
		this(theXML,theUI, theLabel, new CCVector2f(theX,theY), new CCVector2f(theWidth, theHeight),theButtonOffset);
	}
	
	public void yOffset(float theYOffset){
		((CCUIColumnLayoutManager)_myLayoutManager).yOffset(theYOffset);
		_myLayoutManager.reset();
		
		for(CCUIElement myElement:_myUIElements){
			_myLayoutManager.layout(myElement);
		}
	}

	@Override
	public void draw(CCGraphics g){
		super.draw(g);
	}
	
	public void preset(final int thePreset){
		_myPresetControl.preset(thePreset);
	}
	
	public int preset(){
		return _myPresetControl._myChoosenPreset;
	}
	
	public int numberOfPresets(){
		return _myPresetControl.numberOfPresets();
	}
	
	@Override
	public CCXMLElement toXML(){
		CCXMLElement myResult = super.toXML();
		
		myResult.addAttribute("selectedpreset", _myPresetControl._myChoosenPreset);
		myResult.addAttribute("numberofpresets", _myPresetControl.numberOfPresets());
		
		CCXMLElement myPresetNamesXML = new CCXMLElement("presetnames");
		for(String myPresetName:_myPresetControl._myPresetNames){
			CCXMLElement myPresetNameXML = new CCXMLElement("presetname");
			myPresetNameXML.addAttribute("name", myPresetName);
			myPresetNamesXML.addChild(myPresetNameXML);
		}
		myResult.addChild(myPresetNamesXML);
		
		return myResult;
	}
	
	protected void onAddPreset(){
		
	}
	
	protected void onDeletePreset(int thePresetDelete){
		
	}
}
