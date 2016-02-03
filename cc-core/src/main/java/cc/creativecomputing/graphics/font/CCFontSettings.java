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
package cc.creativecomputing.graphics.font;

import java.awt.Font;

import cc.creativecomputing.io.CCIOUtil;

public class CCFontSettings{
	private String _myName;
	
	private float _mySize;
	
	private boolean _myIsSmooth;
	
	private CCCharSet _myCharSet;
	
	private float _myBlurRadius;
	
	private Font _myJavaFont;
	
	private CCKerningTable _myKerning;
	
	private int _myDetail;
	
	private float _myDepth;
	
	public CCFontSettings(String theName, float theSize,  boolean theIsSmooth, final CCCharSet theCharset) {
		_myName = theName;
		_mySize = theSize;
		_myIsSmooth = theIsSmooth;
		_myCharSet = theCharset;
		_myBlurRadius = 0;
		createFont();
	}
	
	public CCFontSettings(String theName, float theSize) {
		this(theName, theSize, true, CCCharSet.EXTENDED_CHARSET);
	}
	
	private void createFont(){
		final String lowerName = _myName.toLowerCase();
		try{
			if (lowerName.endsWith(".otf") || lowerName.endsWith(".ttf")){
				_myJavaFont = Font.createFont(Font.TRUETYPE_FONT, CCIOUtil.openStream(_myName)).deriveFont(_mySize);
				_myKerning = new CCKerningTable(CCIOUtil.openStream(_myName));
			}else{
				_myJavaFont = new Font(_myName, Font.PLAIN, 1).deriveFont(_mySize);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + _myName);
		}
	}
	
	public Font font() {
		return _myJavaFont;
	}
	
	public float size() {
		return _mySize;
	}
	
	public void size(float theSize) {
		_mySize = theSize;
		createFont();
	}
	
	public CCKerningTable kerningTable() {
		return _myKerning;
	}
	
	public CCCharSet charset() {
		return _myCharSet;
	}
	
	public boolean isSmooth() {
		return _myIsSmooth;
	}
	
	public float blurRadius() {
		return _myBlurRadius;
	}
	
	public void blurRadius(float theBlurRadius) {
		_myBlurRadius = theBlurRadius;
	}
	
	public int detail() {
		return _myDetail;
	}
	
	public void detail(int theDetail) {
		_myDetail = theDetail;
	}
	
	public float depth() {
		return _myDepth;
	}
	
	public void depth(float theDepth) {
		_myDepth = theDepth;
	}
}
