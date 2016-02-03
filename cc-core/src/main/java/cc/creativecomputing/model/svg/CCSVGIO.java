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
package cc.creativecomputing.model.svg;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCStrokeCap;
import cc.creativecomputing.graphics.CCStrokeJoin;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeFamily;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeKind;
import cc.creativecomputing.util.CCArrayUtil;
import cc.creativecomputing.util.CCStringUtil;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCSVGIO {
	
	/**
	 * Parse a size that may have a suffix for its units. Ignoring cases where
	 * this could also be a percentage. The <A
	 * HREF="http://www.w3.org/TR/SVG/coords.html#Units">units</A> spec:
	 * <UL>
	 * <LI>"1pt" equals "1.25px" (and therefore 1.25 user units)
	 * <LI>"1pc" equals "15px" (and therefore 15 user units)
	 * <LI>"1mm" would be "3.543307px" (3.543307 user units)
	 * <LI>"1cm" equals "35.43307px" (and therefore 35.43307 user units)
	 * <LI>"1in" equals "90px" (and therefore 90 user units)
	 * </UL>
	 */
	static protected float parseUnitSize(String text) {
		int len = text.length() - 2;

		if (text.endsWith("pt")) {
			return Float.parseFloat(text.substring(0, len)) * 1.25f;
		} else if (text.endsWith("pc")) {
			return Float.parseFloat(text.substring(0, len)) * 15;
		} else if (text.endsWith("mm")) {
			return Float.parseFloat(text.substring(0, len)) * 3.543307f;
		} else if (text.endsWith("cm")) {
			return Float.parseFloat(text.substring(0, len)) * 35.43307f;
		} else if (text.endsWith("in")) {
			return Float.parseFloat(text.substring(0, len)) * 90;
		} else if (text.endsWith("px")) {
			return Float.parseFloat(text.substring(0, len));
		} else {
			return Float.parseFloat(text);
		}
	}
	
	
	/**
	 * Used in place of element.getFloatAttribute(a) because we can have a unit
	 * suffix (length or coordinate).
	 * 
	 * @param element
	 *            what to parse
	 * @param attribute
	 *            name of the attribute to get
	 * @return unit-parsed version of the data
	 */
	static protected float getFloatWithUnit(CCXMLElement element, String attribute) {
		String val = element.attribute(attribute);
		return (val == null) ? 0 : parseUnitSize(val);
	}
	
	static protected HashMap<String, String> parseStyleAttributes(String style) {
		HashMap<String, String> table = new HashMap<String, String>();
		String[] pieces = style.split(";");
		for (int i = 0; i < pieces.length; i++) {
			String[] parts = pieces[i].split(":");
			table.put(parts[0], parts[1]);
		}
		return table;
	}
	
	static protected CCMatrix32f parseSingleTransform(String matrixStr) {
		// String[] pieces = PApplet.match(matrixStr,
		// "^\\s*(\\w+)\\((.*)\\)\\s*$");
		String[] pieces = CCStringUtil.match(matrixStr, "[,\\s]*(\\w+)\\((.*)\\)");
		if (pieces == null) {
			System.err.println("Could not parse transform " + matrixStr);
			return null;
		}
		String[] m = CCStringUtil.splitTokens(pieces[2], ", ");
		if (pieces[1].equals("matrix")) {
			return new CCMatrix32f(
				Float.parseFloat(m[0]), 
				Float.parseFloat(m[2]), 
				Float.parseFloat(m[4]), 
				Float.parseFloat(m[1]), 
				Float.parseFloat(m[3]), 
				Float.parseFloat(m[5])
			);
		} else if (pieces[1].equals("translate")) {
			float tx = Float.parseFloat(m[0]);
			float ty = (m.length == 2) ? Float.parseFloat(m[1]) : Float.parseFloat(m[0]);
			// return new float[] { 1, 0, tx, 0, 1, ty };
			return new CCMatrix32f(1, 0, tx, 0, 1, ty);

		} else if (pieces[1].equals("scale")) {
			float sx = Float.parseFloat(m[0]);
			float sy = (m.length == 2) ? Float.parseFloat(m[1]) : Float.parseFloat(m[0]);
			// return new float[] { sx, 0, 0, 0, sy, 0 };
			return new CCMatrix32f(sx, 0, 0, 0, sy, 0);

		} else if (pieces[1].equals("rotate")) {
			float angle = Float.parseFloat(m[0]);

			if (m.length == 1) {
				float c = CCMath.cos(angle);
				float s = CCMath.sin(angle);
				// SVG version is cos(a) sin(a) -sin(a) cos(a) 0 0
				return new CCMatrix32f(c, -s, 0, s, c, 0);

			} else if (m.length == 3) {
				CCMatrix32f mat = new CCMatrix32f(0, 1, Float.parseFloat(m[1]), 1, 0, Float.parseFloat(m[2]));
				mat.rotate(Float.parseFloat(m[0]));
				mat.translate(-Float.parseFloat(m[1]), -Float.parseFloat(m[2]));
				return mat; // .get(null);
			}

		} else if (pieces[1].equals("skewX")) {
			return new CCMatrix32f(1, 0, 1, CCMath.tan(Float.parseFloat(m[0])), 0, 0);

		} else if (pieces[1].equals("skewY")) {
			return new CCMatrix32f(1, 0, 1, 0, CCMath.tan(Float.parseFloat(m[0])), 0);
		}
		return null;
	}

	/**
	 * Parse the specified SVG matrix into a CCMatrix32f. Note that CCMatrix32f is
	 * rotated relative to the SVG definition, so parameters are rearranged
	 * here. More about the transformation matrices in <a
	 * href="http://www.w3.org/TR/SVG/coords.html#TransformAttribute">this
	 * section</a> of the SVG documentation.
	 * 
	 * @param matrixStr
	 *            text of the matrix param.
	 * @return a good old-fashioned CCMatrix32f
	 */
	static protected CCMatrix32f parseTransform(String matrixStr) {
		matrixStr = matrixStr.trim();
		CCMatrix32f outgoing = null;
		int start = 0;
		int stop = -1;
		while ((stop = matrixStr.indexOf(')', start)) != -1) {
			CCMatrix32f m = parseSingleTransform(matrixStr.substring(start,
					stop + 1));
			if (outgoing == null) {
				outgoing = m;
			} else {
				outgoing.apply(m);
			}
			start = stop + 1;
		}
		return outgoing;
	}
	
	class CCSVGGradient extends CCSVGElement {
		AffineTransform transform;

		float[] offset;
		int[] color;
		int count;

		CCSVGGradient(CCSVGGroup parent, CCXMLElement theSVG) {
			super(parent);

			offset = new float[theSVG.countChildren()];
			color = new int[theSVG.countChildren()];
			
			count = 0;
			
			// <stop offset="0" style="stop-color:#967348"/>
			for (CCXMLElement elem:theSVG) {
				String name = elem.name();
				if (name.equals("stop")) {
					String offsetAttr = elem.attribute("offset");
					float div = 1.0f;
					if (offsetAttr.endsWith("%")) {
						div = 100.0f;
						offsetAttr = offsetAttr.substring(0, offsetAttr.length() - 1);
					}
					
					offset[count] = Float.parseFloat(offsetAttr) / div;
					String style = elem.attribute("style");
					HashMap<String, String> styles = parseStyleAttributes(style);

					String colorStr = styles.get("stop-color");
					if (colorStr == null)
						colorStr = "#000000";
					
					String opacityStr = styles.get("stop-opacity");
					
					if (opacityStr == null)
						opacityStr = "1";
					
					int tupacity = (int) (Float.parseFloat(opacityStr) * 255);
					color[count] = (tupacity << 24) | Integer.parseInt(colorStr.substring(1), 16);
					count++;
				}
			}

			offset = CCArrayUtil.subset(offset, 0, count);
			color = CCArrayUtil.subset(color, 0, count);
		}
		

		
		@Override
		public void drawImplementation(CCGraphics g) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public List<CCLinearSpline> contours() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class LinearGradient extends CCSVGGradient {
		float x1, y1, x2, y2;

		public LinearGradient(CCSVGGroup parent, CCXMLElement theSVG) {
			super(parent, theSVG);

			this.x1 = getFloatWithUnit(theSVG, "x1");
			this.y1 = getFloatWithUnit(theSVG, "y1");
			this.x2 = getFloatWithUnit(theSVG, "x2");
			this.y2 = getFloatWithUnit(theSVG, "y2");

			String transformStr = theSVG.attribute("gradientTransform");

			if (transformStr != null) {
				float t[] = parseTransform(transformStr).get(null);
				this.transform = new AffineTransform(t[0], t[3], t[1], t[4],
						t[2], t[5]);

				Point2D t1 = transform.transform(new Point2D.Float(x1, y1), null);
				Point2D t2 = transform.transform(new Point2D.Float(x2, y2), null);

				this.x1 = (float) t1.getX();
				this.y1 = (float) t1.getY();
				this.x2 = (float) t2.getX();
				this.y2 = (float) t2.getY();
			}
		}
	}
	
	class RadialGradient extends CCSVGGradient {
		float cx, cy, r;

		public RadialGradient(CCSVGGroup parent, CCXMLElement theSVG) {
			super(parent, theSVG);

			this.cx = getFloatWithUnit(theSVG, "cx");
			this.cy = getFloatWithUnit(theSVG, "cy");
			this.r = getFloatWithUnit(theSVG, "r");

			String transformStr = theSVG.attribute("gradientTransform");

			if (transformStr != null) {
				float t[] = parseTransform(transformStr).get(null);
				this.transform = new AffineTransform(t[0], t[3], t[1], t[4],
						t[2], t[5]);

				Point2D t1 = transform.transform(new Point2D.Float(cx, cy),
						null);
				Point2D t2 = transform.transform(new Point2D.Float(cx + r, cy),
						null);

				this.cx = (float) t1.getX();
				this.cy = (float) t1.getY();
				this.r = (float) (t2.getX() - t1.getX());
			}
		}
	}

	class LinearGradientPaint implements Paint {
		float x1, y1, x2, y2;
		float[] offset;
		int[] color;
		int count;
		float opacity;

		public LinearGradientPaint(float x1, float y1, float x2, float y2,
				float[] offset, int[] color, int count, float opacity) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.offset = offset;
			this.color = color;
			this.count = count;
			this.opacity = opacity;
		}

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			Point2D t1 = xform.transform(new Point2D.Float(x1, y1), null);
			Point2D t2 = xform.transform(new Point2D.Float(x2, y2), null);
			return new LinearGradientContext((float) t1.getX(),
					(float) t1.getY(), (float) t2.getX(), (float) t2.getY());
		}

		public int getTransparency() {
			return TRANSLUCENT; // why not.. rather than checking each color
		}

		public class LinearGradientContext implements PaintContext {
			int ACCURACY = 2;
			float tx1, ty1, tx2, ty2;

			public LinearGradientContext(float tx1, float ty1, float tx2, float ty2) {
				this.tx1 = tx1;
				this.ty1 = ty1;
				this.tx2 = tx2;
				this.ty2 = ty2;
			}

			public void dispose() {
			}

			public ColorModel getColorModel() {
				return ColorModel.getRGBdefault();
			}

			public Raster getRaster(int x, int y, int w, int h) {
				WritableRaster raster = getColorModel()
						.createCompatibleWritableRaster(w, h);

				int[] data = new int[w * h * 4];

				// make normalized version of base vector
				float nx = tx2 - tx1;
				float ny = ty2 - ty1;
				float len = (float) Math.sqrt(nx * nx + ny * ny);
				if (len != 0) {
					nx /= len;
					ny /= len;
				}

				int span = (int) CCMath.dist(tx1, ty1, tx2, ty2) * ACCURACY;
				if (span <= 0) {
					// System.err.println("span is too small");
					// annoying edge case where the gradient isn't legit
					int index = 0;
					for (int j = 0; j < h; j++) {
						for (int i = 0; i < w; i++) {
							data[index++] = 0;
							data[index++] = 0;
							data[index++] = 0;
							data[index++] = 255;
						}
					}

				} else {
					int[][] interp = new int[span][4];
					int prev = 0;
					for (int i = 1; i < count; i++) {
						int c0 = color[i - 1];
						int c1 = color[i];
						int last = (int) (offset[i] * (span - 1));
						// System.out.println("last is " + last);
						for (int j = prev; j <= last; j++) {
							float btwn = CCMath.norm(j, prev, last);
							interp[j][0] = (int) CCMath.blend((c0 >> 16) & 0xff, (c1 >> 16) & 0xff, btwn);
							interp[j][1] = (int) CCMath.blend((c0 >> 8) & 0xff, (c1 >> 8) & 0xff, btwn);
							interp[j][2] = (int) CCMath.blend(c0 & 0xff, c1 & 0xff, btwn);
							interp[j][3] = (int) (CCMath.blend((c0 >> 24) & 0xff, (c1 >> 24) & 0xff, btwn) * opacity);
							// System.out.println(j + " " + interp[j][0] + " " +
							// interp[j][1] + " " + interp[j][2]);
						}
						prev = last;
					}

					int index = 0;
					for (int j = 0; j < h; j++) {
						for (int i = 0; i < w; i++) {
							// float distance = 0; //PApplet.dist(cx, cy, x + i,
							// y + j);
							// int which = PApplet.min((int) (distance *
							// ACCURACY), interp.length-1);
							float px = (x + i) - tx1;
							float py = (y + j) - ty1;
							// distance up the line is the dot product of the
							// normalized
							// vector of the gradient start/stop by the point
							// being tested
							int which = (int) ((px * nx + py * ny) * ACCURACY);
							if (which < 0)
								which = 0;
							if (which > interp.length - 1)
								which = interp.length - 1;
							// if (which > 138) System.out.println("grabbing " +
							// which);

							data[index++] = interp[which][0];
							data[index++] = interp[which][1];
							data[index++] = interp[which][2];
							data[index++] = interp[which][3];
						}
					}
				}
				raster.setPixels(0, 0, w, h, data);

				return raster;
			}
		}
	}

	class RadialGradientPaint implements Paint {
		float cx, cy, radius;
		float[] offset;
		int[] color;
		int count;
		float opacity;

		public RadialGradientPaint(float cx, float cy, float radius, float[] offset, int[] color, int count, float opacity) {
			this.cx = cx;
			this.cy = cy;
			this.radius = radius;
			this.offset = offset;
			this.color = color;
			this.count = count;
			this.opacity = opacity;
		}

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			return new RadialGradientContext();
		}

		public int getTransparency() {
			return TRANSLUCENT;
		}

		public class RadialGradientContext implements PaintContext {
			int ACCURACY = 5;

			public void dispose() {
			}

			public ColorModel getColorModel() {
				return ColorModel.getRGBdefault();
			}

			public Raster getRaster(int x, int y, int w, int h) {
				WritableRaster raster = getColorModel()
						.createCompatibleWritableRaster(w, h);

				int span = (int) radius * ACCURACY;
				int[][] interp = new int[span][4];
				int prev = 0;
				for (int i = 1; i < count; i++) {
					int c0 = color[i - 1];
					int c1 = color[i];
					int last = (int) (offset[i] * (span - 1));
					for (int j = prev; j <= last; j++) {
						float btwn = CCMath.norm(j, prev, last);
						interp[j][0] = (int) CCMath.blend((c0 >> 16) & 0xff,(c1 >> 16) & 0xff, btwn);
						interp[j][1] = (int) CCMath.blend((c0 >> 8) & 0xff,
								(c1 >> 8) & 0xff, btwn);
						interp[j][2] = (int) CCMath.blend(c0 & 0xff, c1 & 0xff, btwn);
						interp[j][3] = (int) (CCMath.blend((c0 >> 24) & 0xff,(c1 >> 24) & 0xff, btwn) * opacity);
					}
					prev = last;
				}

				int[] data = new int[w * h * 4];
				int index = 0;
				for (int j = 0; j < h; j++) {
					for (int i = 0; i < w; i++) {
						float distance = CCMath.dist(cx, cy, x + i, y + j);
						int which = CCMath.min((int) (distance * ACCURACY), interp.length - 1);

						data[index++] = interp[which][0];
						data[index++] = interp[which][1];
						data[index++] = interp[which][2];
						data[index++] = interp[which][3];
					}
				}
				raster.setPixels(0, 0, w, h, data);

				return raster;
			}
		}
	}
	
	private CCSVGDocument _myDocument;

	private CCSVGDocument readSVG(CCXMLElement theSVG){
		if (!theSVG.name().equals("svg")) {
			throw new RuntimeException("root is not <svg>, it's <" + theSVG.name() + ">");
		}
		
		_myDocument = new CCSVGDocument();

		readDocumentSize(_myDocument, theSVG);
		readElement(_myDocument, theSVG);
		readChildren(_myDocument, theSVG);

		return _myDocument;
	}
	
	private void readDocumentSize(CCSVGDocument theDocument, CCXMLElement theSVG){
		// not proper parsing of the viewBox, but will cover us for cases where
		// the width and height of the object is not specified
		String viewBoxStr = theSVG.attribute("viewBox");
		if (viewBoxStr != null) {
			String[] viewBox = CCStringUtil.splitTokens(viewBoxStr);
			theDocument.width = Integer.parseInt(viewBox[2]);
			theDocument.height = Integer.parseInt(viewBox[3]);
		}

		// TODO if viewbox is not same as width/height, then use it to scale
		// the original objects. for now, viewbox only used when width/height
		// are empty values (which by the spec means w/h of "100%"
		String unitWidth = theSVG.attribute("width");
		String unitHeight = theSVG.attribute("height");
		if (unitWidth != null) {
			theDocument.width = parseUnitSize(unitWidth);
			theDocument.height = parseUnitSize(unitHeight);
		} else {
			if ((_myDocument.width == 0) || (_myDocument.height == 0)) {
				// throw new RuntimeException("width/height not specified");
				CCLog.warn("The width and/or height is not readable in the <svg> tag of this file.");
				// For the spec, the default is 100% and 100%. For purposes
				// here, insert a dummy value because this is prolly just a
				// font or something for which the w/h doesn't matter.
				theDocument.width = 1;
				theDocument.height = 1;
			}
		}
	}
	
	private void readName(CCSVGElement theElement, CCXMLElement theSVG){
		String myName = theSVG.attribute("id");
		// @#$(* adobe illustrator mangles names of objects when re-saving
		if (myName != null) {
			while (true) {
				String[] m = CCStringUtil.match(myName, "_x([A-Za-z0-9]{2})_");
				if (m == null)
					break;
				char repair = (char) Integer.parseInt(m[1],16);
				myName = myName.replace(m[0], "" + repair);
			}
		}
		theElement._myName = myName;
	}
	
	private void readOpacity(CCSVGElement theElement, CCXMLElement theSVG) {
		if (!theSVG.hasAttribute("opacity")) return;
		
		String opacityText = theSVG.attribute("opacity");
		theElement.opacity(Float.parseFloat(opacityText));
	}
	
	private CCColor readRGB(String theColorText) {
		int leftParen = theColorText.indexOf('(') + 1;
		int rightParen = theColorText.indexOf(')');
		String sub = theColorText.substring(leftParen, rightParen);
		String[] values = CCStringUtil.splitTokens(sub, ", ");
		return new CCColor(
			Integer.parseInt(values[0]),
			Integer.parseInt(values[1]),
			Integer.parseInt(values[2])
		);
	}
	
	private Paint calcGradientPaint(CCSVGGradient gradient, float theOpacity) {
		if (gradient instanceof LinearGradient) {
			LinearGradient grad = (LinearGradient) gradient;
			return new LinearGradientPaint(grad.x1, grad.y1, grad.x2, grad.y2, grad.offset, grad.color, grad.count, theOpacity);

		} else if (gradient instanceof RadialGradient) {
			RadialGradient grad = (RadialGradient) gradient;
			return new RadialGradientPaint(grad.cx, grad.cy, grad.r, grad.offset, grad.color, grad.count, theOpacity);
		}
		return null;
	}
	
	private void readColor(CCSVGElement theElement, String theColorText, boolean isFill) {
		float myAlpha = theElement.fillColor.a;
		boolean myIsColorVisible = true;
		CCColor myColor = new CCColor();
		String name = "";
		CCSVGGradient myGradient = null;
		Paint myPaint = null;
		
		if (theColorText.equals("none")) {
			myIsColorVisible = false;
		} else if (theColorText.equals("black")) {
			myColor = CCColor.BLACK.clone();
			myColor.a = myAlpha;
		} else if (theColorText.equals("white")) {
			myColor = CCColor.WHITE.clone();
			myColor.a = myAlpha;
		} else if (theColorText.startsWith("#")) {
			if (theColorText.length() == 4) {
				// Short form: #ABC, transform to long form #AABBCC
				theColorText = theColorText.replaceAll("^#(.)(.)(.)$", "#$1$1$2$2$3$3");
			}
			myColor = CCColor.createFromString(theColorText);
			myColor.a = myAlpha;
			// System.out.println("hex for fill is " + PApplet.hex(fillColor));
		} else if (theColorText.startsWith("rgb")) {
			myColor = readRGB(theColorText);
			myColor.a = myAlpha;
		} else if (theColorText.startsWith("url(#")) {
			name = theColorText.substring(5, theColorText.length() - 1);
			// PApplet.println("looking for " + name);
			CCSVGElement myElement = _myDocument.findChild(name);
			// PApplet.println("found " + fillObject);
			if (myElement instanceof CCSVGGradient) {
				myGradient = (CCSVGGradient) myElement;
				myPaint = calcGradientPaint(myGradient, myAlpha);
				// PApplet.println("got filla " + fillObject);
			} else {
				// visible = false;
				System.err.println("url " + name + " refers to unexpected data: " + myElement);
			}
		}
		if (isFill) {
			theElement.fill = myIsColorVisible;
			theElement.fillColor = myColor;
			theElement.fillName = name;
			theElement.fillGradient = myGradient;
			theElement.fillGradientPaint = myPaint;
		} else {
			theElement.stroke = myIsColorVisible;
			theElement.strokeColor = myColor;
			theElement.strokeName = name;
			theElement.strokeGradient = myGradient;
			theElement.strokeGradientPaint = myPaint;
		}
	}
	
	private void readStroke(CCSVGElement theElement, CCXMLElement theSVG) {
		if (!theSVG.hasAttribute("stroke")) return;
		
		String strokeText = theSVG.attribute("stroke");
		readColor(theElement, strokeText, false);
	}
	
	private void readStrokeOpacity(CCSVGElement theElement, CCXMLElement theSVG) {
		if (!theSVG.hasAttribute("stroke-opacity")) return;
		
		theElement.strokeOpacity = theSVG.floatAttribute("stroke-opacity");
		theElement.strokeColor.a = theElement.strokeOpacity;
	}
	
	private void readStrokeWeight(CCSVGElement theElement, CCXMLElement theSVG){
		if (!theSVG.hasAttribute("stroke-width")) return;
		theElement.strokeWeight = parseUnitSize(theSVG.attribute("stroke-width"));
	}
	
	private void setStrokeJoin(CCSVGElement theElement, String theStrokeJoin){
		if (theStrokeJoin.equals("inherit")) {
			// do nothing, will inherit automatically
		} else if (theStrokeJoin.equals("miter")) {
			theElement.strokeJoin = CCStrokeJoin.MITER;
		} else if (theStrokeJoin.equals("round")) {
			theElement.strokeJoin = CCStrokeJoin.ROUND;
		} else if (theStrokeJoin.equals("bevel")) {
			theElement.strokeJoin = CCStrokeJoin.BEVEL;
		}
	}
	
	private void readStrokeJoin(CCSVGElement theElement, CCXMLElement theSVG){
		if (!theSVG.hasAttribute("stroke-linejoin")) return;
			
		setStrokeJoin(theElement, theSVG.attribute("stroke-linejoin"));
	}
	
	private void setStrokeCap(CCSVGElement theElement, String theStrokeCap){
		if (theStrokeCap.equals("inherit")) {
			// do nothing, will inherit automatically
		} else if (theStrokeCap.equals("butt")) {
			theElement.strokeCap = CCStrokeCap.SQUARE;
		} else if (theStrokeCap.equals("round")) {
			theElement.strokeCap = CCStrokeCap.ROUND;
		} else if (theStrokeCap.equals("square")) {
			theElement.strokeCap = CCStrokeCap.PROJECT;
		}
	}
	
	private void readStrokeCap(CCSVGElement theElement, CCXMLElement theSVG){
		if (!theSVG.hasAttribute("stroke-linecap")) return;
			
		setStrokeCap(theElement, theSVG.attribute("stroke-linecap"));
	}
	
	private void readFill(CCSVGElement theElement, CCXMLElement theSVG) {
		if (!theSVG.hasAttribute("fill")) return;
		
		String myFillText = theSVG.attribute("fill");
		readColor(theElement, myFillText, true);
	}
	
	private void readFillOpacity(CCSVGElement theElement, CCXMLElement theSVG) {
		if (!theSVG.hasAttribute("fill-opacity")) return;
		
		theElement.fillOpacity(theSVG.floatAttribute("fill-opacity"));
	}
	
	private void readColors(CCSVGElement theElement, CCXMLElement theSVG) {
		readOpacity(theElement, theSVG);
		readStroke(theElement, theSVG);
		readStrokeOpacity(theElement, theSVG);
		readStrokeWeight(theElement, theSVG);
		readStrokeJoin(theElement, theSVG);
		readStrokeCap(theElement, theSVG);
		readFill(theElement, theSVG);
		readFillOpacity(theElement, theSVG);


		if (theSVG.hasAttribute("style")) {
			String styleText = theSVG.attribute("style");
			String[] styleTokens = CCStringUtil.splitTokens(styleText, ";");

			// PApplet.println(styleTokens);
			for (int i = 0; i < styleTokens.length; i++) {
				String[] tokens = CCStringUtil.splitTokens(styleTokens[i], ":");
				// PApplet.println(tokens);

				tokens[0] = CCStringUtil.trim(tokens[0]);

				if (tokens[0].equals("fill")) {
					readColor(theElement, tokens[1], true);

				} else if (tokens[0].equals("fill-opacity")) {
					theElement.fillOpacity(Float.parseFloat(tokens[1]));
				} else if (tokens[0].equals("stroke")) {
					readColor(theElement, tokens[1], false);

				} else if (tokens[0].equals("stroke-width")) {
					theElement.strokeWeight = parseUnitSize(tokens[1]);
				} else if (tokens[0].equals("stroke-linecap")) {
					setStrokeCap(theElement, tokens[1]);
				} else if (tokens[0].equals("stroke-linejoin")) {
					setStrokeJoin(theElement,tokens[1]);
				} else if (tokens[0].equals("stroke-opacity")) {
					theElement.strokeOpacity(Float.parseFloat(tokens[1]));
				} else if (tokens[0].equals("opacity")) {
					theElement.opacity(Float.parseFloat(tokens[1]));
				}
			}
		}
	}
	
	private void readVisible(CCSVGElement theElement, CCXMLElement theSVG){
		String displayStr = theSVG.attribute("display", "inline");
		theElement.visible = !displayStr.equals("none");
	}
	
	private void readTransform(CCSVGElement theElement, CCXMLElement theSVG){
		String transformStr = theSVG.attribute("transform");
		if (transformStr != null) {
			theElement.matrix = parseTransform(transformStr);
		}
	}
	
	private void readElement(CCSVGElement theElement, CCXMLElement theSVG){
		readName(theElement, theSVG);
		readColors(theElement, theSVG);
		readVisible(theElement, theSVG);
		readTransform(theElement, theSVG);
	}
	
	private void readGroup(CCSVGGroup theGroup, CCXMLElement theSVG){
		readElement(theGroup, theSVG);
		readChildren(theGroup, theSVG);
	}
	
	private void readDefs(CCSVGGroup theGroup, CCXMLElement theSVG){
		readChildren(theGroup, theSVG);
	}
	
	private void readLine(CCSVGLine theLine, CCXMLElement theSVG){
		readElement(theLine, theSVG);
		theLine._myKind = CCShapeKind.LINE;
		theLine.family = CCShapeFamily.PRIMITIVE;
		theLine.a().set(
			getFloatWithUnit(theSVG, "x1"),
			getFloatWithUnit(theSVG, "y1")
		);
		theLine.b().set(
			getFloatWithUnit(theSVG, "x2"),
			getFloatWithUnit(theSVG, "y2")
		);
	}
	
	private void readEllipse(CCSVGEllipse theEllipse, CCXMLElement theSVG, boolean theIsCircle){
		readElement(theEllipse, theSVG);
		theEllipse._myKind = CCShapeKind.ELLIPSE;
		theEllipse.family = CCShapeFamily.PRIMITIVE;

		theEllipse.center().set(
			getFloatWithUnit(theSVG, "cx"),
			getFloatWithUnit(theSVG, "cy")
		);

		float rx, ry;
		if (theIsCircle) {
			rx = ry = getFloatWithUnit(theSVG, "r");
		} else {
			rx = getFloatWithUnit(theSVG, "rx");
			ry = getFloatWithUnit(theSVG, "ry");
		}
		theEllipse.radius().set(rx, ry);
	}
	
	private void readRect(CCSVGRectangle theRectangle, CCXMLElement theSVG) {
		readElement(theRectangle, theSVG);
		theRectangle._myKind = CCShapeKind.RECT;
		theRectangle.family = CCShapeFamily.PRIMITIVE;
		
		theRectangle.center().set(
			getFloatWithUnit(theSVG, "x"),
			getFloatWithUnit(theSVG, "y")
		);
		theRectangle.dimension().set(
			getFloatWithUnit(theSVG, "width"),
			getFloatWithUnit(theSVG, "height")
		);
	}
	
	/**
	 * Parse a polyline or polygon from an SVG file.
	 * 
	 * @param close
	 *            true if shape is closed (polygon), false if not (polyline)
	 */
	private void readPoly(CCSVGPoly thePath, CCXMLElement theSVG) {
		readElement(thePath, theSVG);
		thePath._myKind = CCShapeKind.POLYGON;
		thePath.family = CCShapeFamily.PATH;

		String pointsAttr = theSVG.attribute("points");
		if (pointsAttr != null) {
			String[] pointsBuffer = CCStringUtil.splitTokens(pointsAttr);
			thePath.spline().beginEditSpline();
			for (int i = 0; i < pointsBuffer.length; i++) {
				String pb[] = CCStringUtil.split(pointsBuffer[i], ',');
				thePath.spline().addPoint(new CCVector3f(
					Float.valueOf(pb[0]),
					Float.valueOf(pb[1])
				));
			}
			thePath.spline().endEditSpline();
		}

	}
	
	private CCSVGPathReader _myPathReader = new CCSVGPathReader();
	
	private void readPath(CCSVGPath thePath, CCXMLElement theSVG){
		readElement(thePath, theSVG);
		thePath.family = CCShapeFamily.PATH;
		thePath._myKind = CCShapeKind.PATH;

		String pathData = theSVG.attribute("d").replaceAll("\\n", "");
		if (pathData == null || CCStringUtil.trim(pathData).length() == 0) {
			return;
		}
		
		thePath._myPath = _myPathReader.buildPath(pathData, GeneralPath.WIND_EVEN_ODD);
		System.out.println(pathData);
	}
	
	private void readChildren(CCSVGGroup theGroup, CCXMLElement theSVG) {
		theGroup._myChildren = new ArrayList<>();

		for (CCXMLElement mySVG : theSVG) {
			String myName = mySVG.name();
			
			if (myName == null) {
				// just some whitespace that can be ignored (hopefully)
			} else if (myName.equals("g")) {
				CCSVGGroup myGroup = new CCSVGGroup(theGroup);
				myGroup.kind(CCShapeKind.GROUP);
				readGroup(myGroup, mySVG);
				theGroup.addChild(myGroup);
			} else if (myName.equals("defs")) {
				// generally this will contain gradient info, so may
				// as well just throw it into a group element for parsing
				// return new BaseObject(this, elem);
				CCSVGGroup myGroup = new CCSVGGroup(theGroup);
				readDefs(myGroup, mySVG);
				myGroup.kind(CCShapeKind.DEF);
				theGroup.addChild(myGroup);
			} else if (myName.equals("line")) {
				CCSVGLine myLine = new CCSVGLine(theGroup);
				readLine(myLine, mySVG);
				theGroup.addChild(myLine);
			} else if (myName.equals("circle")) {
				CCSVGEllipse myEllipe = new CCSVGEllipse(theGroup);
				readEllipse(myEllipe, mySVG, true);
				theGroup.addChild(myEllipe);
			} else if (myName.equals("ellipse")) {
				CCSVGEllipse myEllipe = new CCSVGEllipse(theGroup);
				readEllipse(myEllipe, mySVG, false);
				theGroup.addChild(myEllipe);
			} else if (myName.equals("rect")) {
				CCSVGRectangle myRect = new CCSVGRectangle(theGroup);
				readRect(myRect, mySVG);
				theGroup.addChild(myRect);
			} else if (myName.equals("polygon")) {
				CCSVGPoly myPoly = new CCSVGPoly(theGroup, true);
				readPoly(myPoly, mySVG);
				theGroup.addChild(myPoly);
			} else if (myName.equals("polyline")) {
				CCSVGPoly myPoly = new CCSVGPoly(theGroup, false);
				readPoly(myPoly, mySVG);
				theGroup.addChild(myPoly);
			} else if (myName.equals("path")) {
				CCSVGPath myPath = new CCSVGPath(theGroup);
				readPath(myPath, mySVG);
				theGroup.addChild(myPath);
				// return new BaseObject(this, elem, PATH);
//				shape = new PShapeSVG(this, mySVG, true);
//				shape.parsePath();

			} else if (myName.equals("radialGradient")) {
				theGroup.addChild(new RadialGradient(theGroup, mySVG));
			} else if (myName.equals("linearGradient")) {
				theGroup.addChild(new LinearGradient(theGroup, mySVG));
			} else if (myName.equals("font")) {
//				return new Font(this, mySVG);

				// } else if (myName.equals("font-face")) {
				// return new FontFace(this, elem);

				// } else if (myName.equals("glyph") || myName.equals("missing-glyph"))
				// {
				// return new FontGlyph(this, elem);

			} else if (myName.equals("metadata")) {

			} else if (myName.equals("text")) { // || myName.equals("font")) {
				CCLog.warn("Text and fonts in SVG files "
						+ "are not currently supported, "
						+ "convert text to outlines instead.");

			} else if (myName.equals("filter")) {
				CCLog.warn("Filters are not supported.");
			} else if (myName.equals("mask")) {
				CCLog.warn("Masks are not supported.");
			} else if (myName.equals("pattern")) {
				CCLog.warn("Patterns are not supported.");
			} else if (myName.equals("stop")) {
				// stop tag is handled by gradient parser, so don't warn about it
			} else if (myName.equals("sodipodi:namedview")) {
				// these are always in Inkscape files, the warnings get tedious
			} else if (!myName.startsWith("#")) {
				CCLog.warn("Ignoring <" + myName + "> tag.");
			}
		}
	}
	
	private static CCSVGIO _myReader;
	
	public static CCSVGDocument newSVG(final String theSVG){
		if(_myReader == null){
			_myReader = new CCSVGIO();
		}
		return _myReader.readSVG(CCXMLIO.createXMLElement(theSVG));
	}
}
