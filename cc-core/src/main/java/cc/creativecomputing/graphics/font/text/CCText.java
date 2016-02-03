package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.graphics.font.CCFont;


public class CCText extends CCMultiFontText{
	
	public CCText(String theTex, CCFont<?> theFont, int theFontSize){
		super();
		addText(theTex, theFont, theFontSize);
		breakText();
	}
	
	public CCText(CCFont<?> theFont){
		super();
		addText("", theFont);
	}
	
	public CCText(){
		super();
	}
	
	public String text() {
		return _myTextParts.get(0).text();
	}
	
	/**
	 * Set the text to display
	 * 
	 * @param theText
	 */
	public void text(final String theText) {
		_myTextParts.get(0).text(theText);
		breakText();
	}
	
	public void text(Number theNumber) {
		text(theNumber.toString());
	}
	
	public void text(final int theText) {
		text(Integer.toString(theText));
	}
	
	public void text(final char theChar) {
		text(Character.toString(theChar));
	}
	
	public void text(final float theText) {
		text(Float.toString(theText));
	}
	
	public void text(final double theText) {
		text(Double.toString(theText));
	}
	
	/**
	 */
	public float size() {
		return _myTextParts.get(0).size();
	}

	public void size(float theSize) {
		_myTextParts.get(0).size(theSize);
	}

	/**
	 */
	public CCFont<?> font() {
		return _myTextParts.get(0).font();
	}

	public void font(CCFont<?> theFont) {
		_myTextParts.get(0).font(theFont);
	}

	public void font(CCFont<?> theFont, float theSize) {
		_myTextParts.get(0).font(theFont, theSize);
	}

	public void leading(float theTextLeading) {
		_myTextParts.get(0).leading(theTextLeading);
	}

	public float ascent() {
		return _myTextParts.get(0).ascent();
	}

	public float descent() {
		return _myTextParts.get(0).descent();
	}

	public void spacing(float theSpacing) {
		_myTextParts.get(0).spacing(theSpacing);
	}
	
}