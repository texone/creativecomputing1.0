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
package cc.creativecomputing.graphics;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.animation.CCBlendModifier;
import cc.creativecomputing.animation.CCBlendable;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;

/**
 * CCColor represents a color with a red a green a blue and an
 * alpha value. RGBA Colors as we know them have a range from
 * 0 to 255 for red, green, blue and alpha. However with graphic 
 * programming this is often changed to a value between 0.0 and
 * 1.0.
 * So when you work with the int methods you get values between
 * 0 and 255, the float methods work with value between 0.0 and 1.0.
 * If values are going over these ranges the are set to the next
 * valid value.
 * @author tex
 *
 */
@CCPropertyObject(name="color")
public class CCColor implements Cloneable, CCBlendable<CCColor>{


	/**
	 * Reads the color from a given String. Color can be parsed as comma separated RGB or RGBA
	 * values in the form <code>color="255,0,0,255"</code> or in hexadecimal form <code>color="#FF0000"</code>
	 * @param theColor string representation of the color
	 * @return the parsed color
	 */
	static public CCColor createFromString(String theColor) {
		
		if(theColor.contains(",")){
			CCColor myResult = new CCColor();
			String[] myValues = theColor.split(",");
			if(myValues.length >= 3){
				myResult.red(Integer.parseInt(myValues[0].trim()));
				myResult.green(Integer.parseInt(myValues[1].trim()));
				myResult.blue(Integer.parseInt(myValues[2].trim()));
			}
			if(myValues.length >= 4){
				myResult.alpha(Integer.parseInt(myValues[3].trim()));
			}
			return myResult;
		}
		if (theColor.charAt(0) == '#')
			theColor = theColor.substring(1);
		return createFromInteger(Integer.parseInt(theColor, 16));
	}

	static public CCColor createFromInteger(final int theColor) {
		return new CCColor().parseFromInteger(theColor);
	}
	
	static public CCColor createFromIntegerBGRA(final int theColor) {
		return new CCColor().parseFromIntegerBGRA(theColor);
	}
	
	static public CCColor createFromIntegerRGBA(final int theColor) {
		return new CCColor().parseFromIntegerRGBA(theColor);
	}
	
	static public CCColor fromArray(final float[] theArray){
		return new CCColor().set(theArray);
	}
	
	static public CCColor createFromHSB(float theHue, float theSaturation, float theBrightness) {
		return new CCColor().setHSB(theHue, theSaturation, theBrightness);
	}
	
	static public CCColor createFromHSB(float theHue, float theSaturation, float theBrightness, float theAlpha) {
		CCColor myResult = new CCColor();
		myResult.setHSB(theHue, theSaturation, theBrightness);
		myResult.a = theAlpha;
		return myResult;
	}
	
	public static CCColor random(){
		CCColor myColor = new CCColor();
		myColor.setHSB(CCMath.random(), 1, 1);
		return myColor;
	}

	/**
	 * Converts the components of a color, as specified by the default RGB 
	 * model, to an equivalent set of values for hue, saturation, and 
	 * brightness that are the three components of the HSB model. 
	 * <p>
	 * If the <code>hsbvals</code> argument is <code>null</code>, then a 
	 * new array is allocated to return the result. Otherwise, the method 
	 * returns the array <code>hsbvals</code>, with the values put into 
	 * that array. 
	 * @param     theRed   the red component of the color
	 * @param     theGreen   the green component of the color
	 * @param     theBlue   the blue component of the color
	 * @param     theHSB  the array used to return the 
	 *                     three HSB values, or <code>null</code>
	 * @return    an array of three elements containing the hue, saturation, 
	 *                     and brightness (in that order), of the color with 
	 *                     the indicated red, green, and blue components.
	 */
	public static float[] RGBtoHSB(float theRed, float theGreen, float theBlue, float[] theHSB) {
		float hue;
		float saturation;
		float brightness;
		
		if (theHSB == null) {
			theHSB = new float[3];
		}
		float cmax = (theRed > theGreen) ? theRed : theGreen;
		if (theBlue > cmax)
			cmax = theBlue;
		float cmin = (theRed < theGreen) ? theRed : theGreen;
		if (theBlue < cmin)
			cmin = theBlue;

		brightness = cmax;
		if (cmax != 0)
			saturation = (cmax - cmin) / cmax;
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = (cmax - theRed) / (cmax - cmin);
			float greenc = (cmax - theGreen) / (cmax - cmin);
			float bluec = (cmax - theBlue) / (cmax - cmin);
			
			if (theRed == cmax)
				hue = bluec - greenc;
			else if (theGreen == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		theHSB[0] = hue;
		theHSB[1] = saturation;
		theHSB[2] = brightness;
		return theHSB;
	}
	
	/**
	 * Completely translucent color. It is like painting a wall with water :)
	 */
	public static final CCColor TRANSPARENT = new CCColor(0f, 0f, 0f, 0f);

	/**
	 * The color white.
	 */
	public static final CCColor WHITE = new CCColor(1f, 1f, 1f);

	/**
	 * The color black.
	 */
	public static final CCColor BLACK = new CCColor(0f, 0f, 0f);

	/**
	 * The color black with an alpha value of 0.5
	 */
	public static final CCColor BLACK_HALF_TRANSPARENT = new CCColor(0f, 0f, 0f, 0.5f);

	/**
	 * The color green.
	 */
	public static final CCColor GREEN = new CCColor(0, 128, 0);

	/**
	 * The color dark green.
	 */
	public static final CCColor DARK_GREEN = new CCColor(0, 100, 0);

	/**
	 * The color light green.
	 */
	public static final CCColor LIGHT_GREEN = new CCColor(144, 238, 144);

	/**
	 * The color red.
	 */
	public static final CCColor RED = new CCColor(1f, 0f, 0f);

	/**
	 * The color light red.
	 */
	public static final CCColor LIGHT_RED = new CCColor(1f, 0.5f, 0.5f);

	/**
	 * The color dark red.
	 */
	public static final CCColor DARK_RED = new CCColor(139, 0, 0);

	/**
	 * The color blue.
	 */
	public static final CCColor BLUE = new CCColor(0f, 0f, 1);

	/**
	 * The color light blue
	 */
	public static final CCColor LIGHT_BLUE = new CCColor(173, 216, 230);

	/**
	 * The color dark blue
	 */
	public static final CCColor DARK_BLUE = new CCColor(0, 0, 139);

	/**
	 * The color yellow
	 */
	public static final CCColor YELLOW = new CCColor(1f, 1f, 0f);

	/**
	 * The color light yellow
	 */
	public static final CCColor LIGHT_YELLOW = new CCColor(255, 255, 224);

	/**
	 * The color dark yellow
	 */
	public static final CCColor DARK_YELLOW = new CCColor(0.5f, 0.5f, 0f);

	/**
	 * The color magenta
	 */
	public static final CCColor MAGENTA = new CCColor(1f, 0f, 1f);

	/**
	 * The color light magenta
	 */
	public static final CCColor LIGHT_MAGENTA = new CCColor(1f, 0.5f, 1f);

	/**
	 * The color dark magenta
	 */
	public static final CCColor DARK_MAGENTA = new CCColor(139, 0, 139);

	/**
	 * The color cyuan
	 */
	public static final CCColor CYAN = new CCColor(0f, 1f, 1f);

	/**
	 * The color light cyuan
	 */
	public static final CCColor LIGHT_CYUAN = new CCColor(224, 255, 255);

	/**
	 * The color dark cyuan
	 */
	public static final CCColor DARK_CYUAN = new CCColor(0, 139, 139);

	/**
	 * The color gray
	 */
	public static final CCColor GRAY = new CCColor(0.5f, 0.5f, 0.5f);

	/**
	 * The color light gray
	 */
	public static final CCColor LIGHT_GRAY = new CCColor(211, 211, 211);

	/**
	 * The color dark gray
	 */
	public static final CCColor DARK_GRAY = new CCColor(169, 169, 169);

	/**
	 * The color white with an alpha value of 0.5
	 */
	public static final CCColor WHITE_HALF_TRANSPARENT = new CCColor(1f, 1f, 1f, 0.5f);
	
	public static Map<String, CCColor> colorMap = new HashMap<String, CCColor>();

	static {
		colorMap.put("blue", BLUE);
		colorMap.put("green", GREEN);
		colorMap.put("red", RED);
		colorMap.put("yellow", YELLOW);
		colorMap.put("magenta", MAGENTA);
		colorMap.put("white", WHITE);
		colorMap.put("black", BLACK);
		colorMap.put("gray", GRAY);
		colorMap.put("light blue", LIGHT_BLUE);
		colorMap.put("light yellow", LIGHT_YELLOW);
		colorMap.put("light magneta", LIGHT_MAGENTA);
		colorMap.put("light gray", LIGHT_GRAY);
		colorMap.put("light green", LIGHT_GREEN);
		colorMap.put("light cyuan", LIGHT_CYUAN);
		colorMap.put("light red", LIGHT_RED);
		colorMap.put("dark blue", DARK_BLUE);
		colorMap.put("dark yellow", DARK_YELLOW);
		colorMap.put("dark magneta", DARK_MAGENTA);
		colorMap.put("dark gray", DARK_GRAY);
		colorMap.put("dark green", DARK_GREEN);
		colorMap.put("dark cyuan", DARK_CYUAN);
		colorMap.put("dark red", DARK_RED);
	}
	
	/**
	 * Returns a new color created by blending the two given colors color1 and color2 with the factor
	 * blend. Values nearer to zero result in colors nearer to color1, values nearer to color2 result
	 * in values nearer to color2.
	 * @param theColor1
	 * @param theColor2
	 * @param theBlend
	 * @return
	 */
	public static CCColor blend(final CCColor theColor1, final CCColor theColor2, float theBlend){
		theBlend = CCMath.constrain(theBlend, 0, 1);
		return new CCColor(
			theColor1.r * (1 - theBlend) + theColor2.r * theBlend,
			theColor1.g * (1 - theBlend) + theColor2.g * theBlend,
			theColor1.b * (1 - theBlend) + theColor2.b * theBlend,
			theColor1.a * (1 - theBlend) + theColor2.a * theBlend
		);
	}
	
	@CCProperty(name="red", node = false)
	@CCControl(name="red", min = 0, max = 1)
	public float r = 0;
	@CCProperty(name="green", node = false)
	@CCControl(name="green", min = 0, max = 1)
	public float g = 0;
	@CCProperty(name="blue", node = false)
	@CCControl(name="blue", min = 0, max = 1)
	public float b = 0;
	@CCProperty(name="alpha", node = false)
	@CCControl(name="alpha", min = 0, max = 1)
	public float a = 0;

	/**
	 * Checks the color <code>float</code> components supplied for validity.
	 * Throws an <code>IllegalArgumentException</code> if the value is out of
	 * range.
	 * 
	 * @param theRed the Red component
	 * @param theGreen the Green component
	 * @param theBlue the Blue component
	 * @param theAlpha the Alpha component
	 */
	private static void testColorValueRange(final float theRed, final float theGreen, final float theBlue, final float theAlpha) {
		String badComponentString = "";

		if (theAlpha < 0 || theAlpha > 1) {
			badComponentString = badComponentString + " Alpha:" + theAlpha;
		}
		if (theRed < 0 || theRed > 1) {
			badComponentString = badComponentString + " Red:" + theRed;
		}
		if (theGreen < 0 || theGreen > 1) {
			badComponentString = badComponentString + " Green:" + theGreen;
		}
		if (theBlue < 0 || theBlue > 1) {
			badComponentString = badComponentString + " Blue:" + theBlue;
		}
//		if (rangeError == true) {
//			throw new RuntimeException("Color parameter outside of expected range:" + badComponentString);
//		}
	}
	
	/**
	 * Checks the color <code>int</code> components supplied for validity.
	 * Throws an <code>IllegalArgumentException</code> if the value is out of
	 * range.
	 * 
	 * @param theRed the Red component
	 * @param theGreen the Green component
	 * @param theBlue the Blue component
	 * @param theAlpha the Alpha component
	 */
	private static void testColorValueRange(final int theRed, final int theGreen, final int theBlue, final int theAlpha) {
		boolean rangeError = false;
		String badComponentString = "";

		if (theAlpha < 0 || theAlpha > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Alpha:" + theAlpha;
		}
		if (theRed < 0 || theRed > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Red:" + theRed;
		}
		if (theGreen < 0 || theGreen > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Green:" + theGreen;
		}
		if (theBlue < 0 || theBlue > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Blue:" + theBlue;
		}
		if (rangeError == true) {
			throw new RuntimeException("Color parameter outside of expected range:" + badComponentString);
		}
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue values in the range (0 - 255).  
	 * The actual color used in rendering depends on finding the best match given the color space available 
	 * for a given output device.  Alpha is defaulted to 255. If the RGBA values are outside of the range 
	 * 0 - 255 for int values or 0 - 1  for float values an exception is thrown.
	 * 
	 * @param theRed the red component
	 * @param theGreen the green component
	 * @param theBlue the blue component
	 * @param theAlpha the alpha component
	 */
	public CCColor(final int theRed, final int theGreen, final int theBlue, final int theAlpha) {
		r = (float) theRed / 255f;
		g = (float) theGreen / 255f;
		b = (float) theBlue / 255;
		a = (float) theAlpha / 255;
	}

	public CCColor(final int theRed, final int theGreen, final int theBlue) {
		this(theRed, theGreen, theBlue, 255);
	}
	
	/**
	 * @param theGrey int, grey value for the color
	 * @param theAlpha int, alpha value for the color
	 */
	public CCColor(final int theGrey) {
		this(theGrey, theGrey, theGrey, 255);
	}

	/**
	 * @param theGrey int, grey value for the color
	 * @param theAlpha int, alpha value for the color
	 */
	public CCColor(final int theGrey, final int theAlpha) {
		this(theGrey, theGrey, theGrey, theAlpha);
	}
	
	public CCColor(final int[] theValues){
		set(theValues);
	}
	
	public CCColor(final float[] theValues){
		set(theValues);
	}

	/**
	 * @param theRed float, the red component
	 * @param theGreen float, the green component
	 * @param theBlue float, the blue component
	 * @param theAlpha float, the alpha component
	 */
	public CCColor(final float theRed, final float theGreen, final float theBlue, final float theAlpha) {
		testColorValueRange(theRed, theGreen, theBlue, 1f);

		r = theRed;
		g = theGreen;
		b = theBlue;
		a = theAlpha;
	}

	public CCColor(final float theRed, final float theGreen, final float theBlue) {
		this(theRed, theGreen, theBlue, 1f);
	}

	public CCColor(final float theGrey) {
		this(theGrey, theGrey, theGrey);
	}

	public CCColor(final float theGrey, final float theAlpha) {
		this(theGrey, theGrey, theGrey, theAlpha);
	}

	public CCColor() {
		this(0);
	}
	
	public CCColor(final CCColor theColor){
		this();
		set(theColor);
	}
	
	public CCColor clone(){
		return new CCColor(r, g,b,a);
	}

	/**
	 * Returns the red part of the RGB color as an value between
	 * 0.0 and 1.0
	 * @return
	 */
	public float red() {
		return r;
	}

	/**
	 * Returns the green part of the RGB color as an value between
	 * 0 and 255
	 * @return
	 */
	public float green() {
		return g;
	}

	/**
	 * Returns the blue part of the RGB color as an value between
	 * 0 and 255
	 * @return
	 */
	public float blue() {
		return b;
	}

	/**
	 * Returns the alpha part of the RGB color as an value between
	 * 0 and 255
	 * @return
	 */
	public float alpha() {
		return a;
	}
	
	/**
	 * Sets a gray value by setting the red, green and blue color at once.
	 * 0.0 and 1.0
	 * @return
	 */
	public void gray(float theGray) {
		theGray = CCMath.constrain(theGray, 0, 1);
		r = theGray;
		g = theGray;
		b = theGray;
	}
	
	public void gray(int theGray){
		gray(theGray / 255f);
	}

	/**
	 * Sets the red part of the RGB color to the given value between
	 * 0.0 and 1.0
	 * @return
	 */
	public void red(final float theRed) {
		CCMath.constrain(theRed, 0, 1);
		r = theRed;
	}
	
	public void red(final int theRed){
		CCMath.constrain(theRed, 0, 255);
		r = theRed / 255.0f;
	}

	/**
	 * Sets the green part of the RGB color to the given value between
	 * 0.0 and 1.0
	 * @return
	 */
	public void green(final float theGreen) {
		CCMath.constrain(theGreen, 0, 1);
		g = theGreen;
	}
	
	public void green(final int theGreen) {
		CCMath.constrain(theGreen, 0, 255);
		g = theGreen/255f;
	}

	/**
	 * Sets the blue part of the RGB color to the given value between
	 * 0.0 and 1.0
	 * @return
	 */
	public void blue(final float theBlue) {
		CCMath.constrain(theBlue, 0, 1);
		b = theBlue;
	}
	
	public void blue(final int theBlue) {
		CCMath.constrain(theBlue, 0, 255);
		b = theBlue/255f;
	}

	/**
	 * Sets the alpha part of the RGB color to the given value between
	 * 0.0 and 1.0
	 * @return
	 */
	public void alpha(final float theAlpha) {
		CCMath.constrain(theAlpha, 0, 1);
		a = theAlpha;
	}

	public void alpha(final int theAlpha) {
		CCMath.constrain(theAlpha, 0, 255);
		a = theAlpha/255f;
	}
	
	public CCColor set(float[] theValues){
		switch(theValues.length) {
		case 4:
			a = theValues[3];
			b = theValues[2];
			g = theValues[1];
			r = theValues[0];
		case 3:
			b = theValues[2];
			g = theValues[1];
			r = theValues[0];
			a = 1f;
			break;
		case 2:
			a = theValues[1];
			r = g = b = theValues[0];
			break;
		case 1:
			r = g = b = theValues[0];
			a = 1f;
			break;
		}
		return this;
	}

	public CCColor set(final float theRed, final float theGreen, final float theBlue, final float theAlpha) {
		testColorValueRange(theRed, theGreen, theBlue, theAlpha);
		r = theRed;
		g = theGreen;
		b = theBlue;
		a = theAlpha;
		return this;
	}

	public CCColor set(final float theRed, final float theGreen, final float theBlue) {
		return set(theRed, theGreen, theBlue, 1f);
	}

	public CCColor set(final float theGray, final float theAlpha) {
		return set(theGray, theGray, theGray, theAlpha);
	}

	public CCColor set(final float theGray) {
		return set(theGray, 1f);
	}
	
	public CCColor set(int[] theValues){
		switch(theValues.length) {
		case 4:
			a = theValues[3] / 255f;
			b = theValues[2] / 255f;
			g = theValues[1] / 255f;
			r = theValues[0] / 255f;
		case 3:
			b = theValues[2] / 255f;
			g = theValues[1] / 255f;
			r = theValues[0] / 255f;
			a = 1f;
			break;
		case 2:
			a = theValues[1] / 255f;
			r = g = b = theValues[0] / 255f;
			break;
		case 1:
			r = g = b = theValues[0] / 255f;
			a = 1f;
			break;
		}
		return this;
	}

	public CCColor set(final int theRed, final int theGreen, final int theBlue, final int theAlpha) {
		testColorValueRange(theRed, theGreen, theBlue, theAlpha);
		r = theRed/255f;
		g = theGreen/255f;
		b = theBlue/255f;
		a = theAlpha/255f;
		return this;
	}

	public CCColor set(final int theRed, final int theGreen, final int theBlue) {
		return set(theRed, theGreen, theBlue, 255);
	}

	public CCColor set(final int theGray, final int theAlpha) {
		return set(theGray, theGray, theGray, theAlpha);
	}

	public CCColor set(final int theGray) {
		return set(theGray, 255);
	}
	
	public CCColor set(final CCColor theColor){
		return set(theColor.r,theColor.g,theColor.b,theColor.a);
	}
	
	public CCColor add(final float theRed, final float theGreen, final float theBlue){
		r += theRed;
		g += theGreen;
		b += theBlue;
		return this;
	}

	/**
	 * Sets the <code>Color</code> a <code>String</code> to an integer and
	 * returns the specified opaque <code>Color</code>. This method handles
	 * string formats that are used to represent octal and hexidecimal numbers.
	 * 
	 * @param nm
	 *           a <code>String</code> that represents an opaque color as a
	 *           24-bit integer
	 * @return the new <code>Color</code> object.
	 */
	public void set(String nm) {
		try {
			Integer intval = Integer.decode(nm);
			int i = intval.intValue();
			set(i);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid Color parameter");
		}
	}

	/**
	 * Sets the color using values specified by the HSB model. 
	 * <ul>
	 * <li>hue should be floating-point values between zero and one</li>
	 * <li>saturation should be floating-point values between zero and one</li>
	 * <li>brightness should be floating-point values between zero and one</li>
	 * </ul>
	 * The <code>saturation</code> and <code>brightness</code> components
	 * should be floating-point values between zero and one
	 * (numbers in the range 0.0-1.0).  The <code>hue</code> component
	 * can be any floating-point number.  The floor of this number is
	 * subtracted from it to create a fraction between 0 and 1.  This
	 * fractional number is then multiplied by 360 to produce the hue
	 * angle in the HSB color model.
	 * <p>
	 * The integer that is returned by <code>HSBtoRGB</code> encodes the 
	 * value of a color in bits 0-23 of an integer value that is the same 
	 * format used by the method {@link #getRGB() <code>getRGB</code>}.
	 * This integer can be supplied as an argument to the
	 * <code>Color</code> constructor that takes a single integer argument. 
	 * @param     theHue   the hue component of the color
	 * @param     theSaturation   the saturation of the color
	 * @param     theBrightness   the brightness of the color
	 * @return    the RGB value of the color with the indicated hue, 
	 *                            saturation, and brightness.
	 */
	public CCColor setHSB(final float theHue, final float theSaturation, final float theBrightness, final float theAlpha) {

		a = theAlpha;

		if (theSaturation == 0) {
			r = g = b = theBrightness;
		} else {
			float which = (theHue - (int) theHue) * 6.0f;
			float f = which - (int) which;
			float p = theBrightness * (1.0f - theSaturation);
			float q = theBrightness * (1.0f - theSaturation * f);
			float t = theBrightness * (1.0f - (theSaturation * (1.0f - f)));

			switch ((int) which) {
			case 0:
				r = theBrightness;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = theBrightness;
				b = p;
				break;
			case 2:
				r = p;
				g = theBrightness;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = theBrightness;
				break;
			case 4:
				r = t;
				g = p;
				b = theBrightness;
				break;
			case 5:
				r = theBrightness;
				g = p;
				b = q;
				break;
			}
		}
		return this;
	}
	
	public CCColor setHSB(final float theHue, final float theSaturation, final float theBrightness){
		return setHSB(theHue, theSaturation, theBrightness, 1);
	}
	
	public CCColor parseFromInteger(final int theColor) {
		return set(
			((theColor >> 16) & 0xff) / 255f,
			((theColor >> 8) & 0xff) / 255f, 
			(theColor & 0xff) / 255f
		);
	}
	
	public CCColor parseFromIntegerBGRA(final int theColor) {
		return set(
			((theColor >>  8) & 0xff) / 255f,
			((theColor >> 16) & 0xff) / 255f,
			((theColor >> 24) & 0xff) / 255f, 
			(theColor & 0xff) / 255f
		);
	}
	
	public CCColor parseFromIntegerRGBA(final int theColor) {
		return set(
			((theColor >> 24) & 0xff) / 255f,
			((theColor >> 16) & 0xff) / 255f,
			((theColor >>  8) & 0xff) / 255f, 
			(theColor & 0xff) / 255f
		);
	}

	/**
	 * Returns the maximum of the red, green and blue values.
	 * @return maximum of the red, green and blue values.
	 */
	public float maxChannelValue() {
		return CCMath.max(r, g, b);
	}
	
	/**
	 * Extracts the brightness value from a color.
	 * @return
	 */
	public float brightness() {
		return 0.3f * r + 0.59f * g + 0.11f * b;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.animation.CCBlendable#blend(float, java.lang.Object)
	 */
	public void blend(float theBlend, CCColor theStart, CCColor theTarget) {
		r = CCMath.blend(theStart.r, theTarget.r, theBlend);
		g = CCMath.blend(theStart.g, theTarget.g, theBlend);
		b = CCMath.blend(theStart.b, theTarget.b, theBlend);
		a = CCMath.blend(theStart.a, theTarget.a, theBlend);
	}
	
	private CCBlendModifier _myModifier;
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.animation.CCBlendable#modifier(cc.creativecomputing.newui.animation.CCBlendModifier)
	 */
	public void modifier(CCBlendModifier theModifier) {
		if(_myModifier != null)_myModifier.onReplace();
		_myModifier = theModifier;
	}

	/**
	 * Extracts the saturation value from a color.
	 * @return
	 */
	public float saturation() {
		float cmax = maxChannelValue();
		float cmin = Math.min(r, g);
		cmin = Math.min(cmin, b);

		if (cmax != 0)
			return ((cmax - cmin) / cmax);
		else
			return 0;
	}

	/**
	 * Extracts the hue value from a color.
	 * @return
	 */
	public float hue() {
		float hue;

		float cmax = maxChannelValue();

		float cmin = Math.min(r, g);
		cmin = Math.min(cmin, b);

		if (saturation() == 0)
			hue = 0;
		else {
			float redc = (cmax - r) / (cmax - cmin);
			float greenc = (cmax - g) / (cmax - cmin);
			float bluec = (cmax - b) / (cmax - cmin);

			if (r == cmax) {
				hue = bluec - greenc;
			} else if (g == cmax) {
				hue = 2.0f + redc - bluec;
			} else {
				hue = 4.0f + greenc - redc;
			}
			hue = hue / 6.0f;

			if (hue < 0) {
				hue = hue + 1.0f;
			}
		}
		return hue;
	}
	
	public float[] hsb() {
		return hsb(null);
	}

	/**
	 * Converts the components of a color, as specified by the default RGB model, to an equivalent set of values for
	 * hue, saturation, and brightness that are the three components of the HSB model.
	 * <p>
	 * If the <code>hsbvals</code> argument is <code>null</code>, then a new array is allocated to return the result.
	 * Otherwise, the method returns the array <code>hsbvals</code>, with the values put into that array.
	 * 
	 * @param hsbvals the array used to return the three HSB values, or <code>null</code>
	 * @return an array of three elements containing the hue, saturation, and brightness (in that order), of the color
	 *         with the indicated red, green, and blue components.
	 */
	public float[] hsb(float[] theResult) {
		float hue, saturation, brightness;

		float cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		float cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		brightness = cmax;
		if (cmax != 0)
			saturation = (cmax - cmin) / cmax;
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = (cmax - r) / (cmax - cmin);
			float greenc = (cmax - g) / (cmax - cmin);
			float bluec = (cmax - b) / (cmax - cmin);
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		if (theResult == null) {
			theResult = new float[3];
		}
		theResult[0] = hue;
		theResult[1] = saturation;
		theResult[2] = brightness;
		return theResult;
	}

	private static final float DEFAULT_SCALE = 0.7f;

	/**
	 * Creates a new <code>Colour</code> that is a brighter version of this
	 * <code>Colour</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB 
	 * components of this <code>Colour</code> to create a brighter version
	 * of this <code>Colour</code>. Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a
	 * series of invocations of these two methods might be inconsistent
	 * because of rounding errors. 
	 * @return a new <code>Colour</code> object that is a brighter version of this <code>Colour</code>.
	 */
	public CCColor brighter(final float theScale) {

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying
		 * brighter to blue will always return blue, brighter 3. non pure colour
		 * (non zero RGB) will eventually return white
		 */
		float i = (1.0f - theScale) / 2;

		if (r == 0 && g == 0 && b == 0) {
			return new CCColor(i, i, i);
		}

		return new CCColor(
			CCMath.min(r / theScale, 1), 
			CCMath.min(g / theScale, 1), 
			CCMath.min(b / theScale, 1),
			a
		);
	}

	public CCColor brighter() {
		return brighter(DEFAULT_SCALE);
	}

	/**
	 * Creates a new Color that is a darker version of this Color. This method
	 * applies an arbitrary scale factor to each of the three RGB components of
	 * this Color to create a darker version of this Color. Although brighter
	 * and darker are inverse operations, the results of a series of invocations
	 * of these two methods might be inconsistent because of rounding errors.
	 * 
	 * @return a new Color object that is a darker version of this Color.
	 */
	public CCColor darker(final float theScale) {
		return new CCColor(
			CCMath.constrain(r * theScale, 0, 1), 
			CCMath.constrain(g * theScale, 0, 1), 
			CCMath.constrain(b * theScale, 0, 1),
			a
		);
	}

	public CCColor darker() {
		return darker(DEFAULT_SCALE);
	}
	
	public float[]array(){
		return new float[] {r,g,b,a};
	}

	/**
	 * Returns a string representation of this <code>Color</code>. This
	 * method is intended to be used only for debugging purposes.  The 
	 * content and format of the returned string might vary between 
	 * implementations. The returned string might be empty but cannot 
	 * be <code>null</code>.
	 * 
	 * @return  a string representation of this <code>Color</code>.
	 */
	public String toFloatString() {
		return getClass().getName() + "[r=" + r + ",g=" + g + ",b=" + b + "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object theObj) {
		if(!(theObj instanceof CCColor))return false;
		CCColor myColor = (CCColor)theObj;
		return 
			myColor.r == r &&
			myColor.g == g &&
			myColor.b == b &&
			myColor.a == a;
	}
	
	public String toString(){
		int a = (int)(this.a*255);
		int r = (int)(this.r*255);
		int g = (int)(this.g*255);
		int b = (int)(this.b*255);
		String sa = ((Integer.toHexString(a)).length() == 1) ? "0" + Integer.toHexString(a) : Integer.toHexString(a);
		String sr = ((Integer.toHexString(r)).length() == 1) ? "0" + Integer.toHexString(r) : Integer.toHexString(r);
		String sg = ((Integer.toHexString(g)).length() == 1) ? "0" + Integer.toHexString(g) : Integer.toHexString(g);
		String sb = ((Integer.toHexString(b)).length() == 1) ? "0" + Integer.toHexString(b) : Integer.toHexString(b);
		return sa + sr + sg + sb;
	}

	
}
