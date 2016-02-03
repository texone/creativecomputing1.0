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
import java.util.List;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCStrokeCap;
import cc.creativecomputing.graphics.CCStrokeJoin;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGIO.CCSVGGradient;


public abstract class CCSVGElement {

	public static enum CCShapeKind{
		LINE, ELLIPSE, RECT, POLYGON, PATH, GROUP, DEF
	}
	
	protected CCShapeKind _myKind;
	
	public static enum CCShapeFamily{
		GROUP, PRIMITIVE, PATH
	}

	/** The shape type, one of GROUP, PRIMITIVE, PATH, or GEOMETRY. */
	protected CCShapeFamily family;
	
	protected CCSVGGroup parent;
	
	protected String _myName;
	
	protected CCMatrix32f matrix;
	
	// set to false if the object is hidden in the layers palette
	protected boolean visible = true;
	
	protected boolean stroke;
	protected CCColor strokeColor;
	protected float strokeWeight; // default is 1
	protected CCStrokeCap strokeCap;
	protected CCStrokeJoin strokeJoin;
	CCSVGGradient strokeGradient;
	Paint strokeGradientPaint;
	String strokeName; // id of another object, gradients only?

	protected boolean fill;
	protected CCColor fillColor;
	CCSVGGradient fillGradient;
	Paint fillGradientPaint;
	String fillName; // id of another object
	
	/** True if this is a closed path. */
	protected boolean close;
	
	float opacity;
	float strokeOpacity;
	float fillOpacity;
	
	public CCSVGElement(CCSVGGroup theParent){
		parent = theParent;
		
		if (parent == null) {
			// set values to their defaults according to the SVG spec
			stroke = false;
			strokeColor = CCColor.BLACK.clone();
			strokeWeight = 1;
//			strokeCap = PConstants.SQUARE; // equivalent to BUTT in svg spec
//			strokeJoin = PConstants.MITER;
			strokeGradient = null;
			strokeGradientPaint = null;
			strokeName = null;

			fill = true;
			fillColor = CCColor.BLACK.clone();
			fillGradient = null;
			fillGradientPaint = null;
			fillName = null;

			// hasTransform = false;
			// transformation = null; //new float[] { 1, 0, 0, 1, 0, 0 };

			strokeOpacity = 1;
			fillOpacity = 1;
			opacity = 1;

		} else {
			stroke = parent.stroke;
			strokeColor = parent.strokeColor.clone();
			strokeWeight = parent.strokeWeight;
			strokeCap = parent.strokeCap;
			strokeJoin = parent.strokeJoin;
			strokeGradient = parent.strokeGradient;
			strokeGradientPaint = parent.strokeGradientPaint;
			strokeName = parent.strokeName;

			fill = parent.fill;
			fillColor = parent.fillColor.clone();
			fillGradient = parent.fillGradient;
			fillGradientPaint = parent.fillGradientPaint;
			fillName = parent.fillName;

			// hasTransform = parent.hasTransform;
			// transformation = parent.transformation;

			opacity = parent.opacity;
		}
	}
	
	public void kind(CCShapeKind theKind) {
		_myKind = theKind;
	}
	
	public CCShapeKind kind(){
		return _myKind;
	}

	public void name(String theName) {
		_myName = theName;
	}

	public String name() {
		return _myName;
	}
	
	public void opacity(float theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void fillOpacity(float theOpacity){
		opacity = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void strokeOpacity(float theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
	}
	
	public void pre(CCGraphics g){
		
	}
	
	public abstract void drawImplementation(CCGraphics g);
	
	public void draw(CCGraphics g){
		if(matrix != null){
			g.pushMatrix();
			g.applyMatrix(matrix);
		}
		g.pushAttribute();
		if (stroke) {
			g.color(strokeColor);
			g.strokeWeight(strokeWeight);
			g.polygonMode(CCPolygonMode.LINE);
//			g.strokeCap(strokeCap);
//			g.strokeJoin(strokeJoin);
			
			drawImplementation(g);
		} 

		if (fill) {
			// System.out.println("filling " + PApplet.hex(fillColor));
			g.color(fillColor);
			g.polygonMode(CCPolygonMode.FILL);
			drawImplementation(g);
		} 
		
		g.popAttribute();
		if(matrix != null){
			g.popMatrix();
		}
	}
	
	public abstract List<CCLinearSpline> contours();
}
