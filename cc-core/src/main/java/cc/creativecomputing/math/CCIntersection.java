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
package cc.creativecomputing.math;

import java.util.ArrayList;
import java.util.List;

/**
 * @author info
 *
 */
public class CCIntersection {
	
	public static enum CCLineIntersectResult { 
		PARALLEL, 
		COINCIDENT, 
		NOT_INTERESECTING, 
		INTERESECTING 
	};

	
	public static CCLineIntersectResult intersectLineLine(final CCLine2f theLine1, final CCLine2f theLine2, final CCVector2f theIntersection) {
		float myDenominator = 
			(theLine1.end().y - theLine1.start().y) * (theLine2.end().x - theLine2.start().x) - 
			(theLine1.end().x - theLine1.start().x) * (theLine2.end().y - theLine2.start().y);

		float ua = 
			(theLine1.end().x - theLine1.start().x) * (theLine2.start().y - theLine1.start().y) - 
			(theLine1.end().y - theLine1.start().y) * (theLine2.start().x - theLine1.start().x);

		float ub = 
			(theLine2.end().x - theLine2.start().x) * (theLine2.start().y - theLine1.start().y) - 
			(theLine2.end().y - theLine2.start().y) * (theLine2.start().x - theLine1.start().x);


		
		
		if (CCMath.abs(myDenominator) <= 0.1f) {
			if (ua == 0.0f && ub == 0.0f) {
				return CCLineIntersectResult.COINCIDENT;
			}
			return CCLineIntersectResult.PARALLEL;
		}

		ua = ua / myDenominator;
		ub = ub / myDenominator;

		if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
			// Get the intersection point.
			theIntersection.set(
				theLine2.start().x + ua * (theLine2.end().x - theLine2.start().x), 
				theLine2.start().y + ua * (theLine2.end().y - theLine2.start().y)
			);

			return CCLineIntersectResult.INTERESECTING;
		}

		return CCLineIntersectResult.NOT_INTERESECTING;
	}

	
	public static CCVector2f intersectSegmentLine(
		final CCLine2f theSegment, 
		final CCLine2f theLine
	) {
		CCVector2f myResult = null;
		float myDenominator = 
			(theLine.end().y - theLine.start().y) * (theSegment.end().x - theSegment.start().x) - 
			(theLine.end().x - theLine.start().x) * (theSegment.end().y - theSegment.start().y);
		if (myDenominator == 0.0f)
			return myResult;
		
		float ua = (
			(theLine.end().x - theLine.start().x) * (theSegment.start().y - theLine.start().y) - 
			(theLine.end().y - theLine.start().y) * (theSegment.start().x - theLine.start().x)
		) / myDenominator;
		
		if ((ua > 1.0f) || (ua < 0.0f))
			return myResult;
		
		myResult = new CCVector2f(
			theSegment.start().x + ua * (theSegment.end().x - theSegment.start().x), 
			theSegment.start().y + ua * (theSegment.end().y - theSegment.start().y)
		);
		return myResult;
	}

	public static CCVector2f intersectSegmentSegment(final CCLine2f theSegment1, final CCLine2f theSegment2) {
		CCVector2f myResult = null;
		float myDenominator = 
			(theSegment2.end().y - theSegment2.start().y) * (theSegment1.end().x - theSegment1.start().x) - 
			(theSegment2.end().x - theSegment2.start().x) * (theSegment1.end().y - theSegment1.start().y);
		
		float ua = (
			(theSegment2.end().x - theSegment2.start().x) * (theSegment1.start().y - theSegment2.start().y) - 
			(theSegment2.end().y - theSegment2.start().y) * (theSegment1.start().x - theSegment2.start().x)
		) / myDenominator;
		
		float ub = (
			(theSegment1.end().x - theSegment1.start().x) * (theSegment1.start().y - theSegment2.start().y) - 
			(theSegment1.end().y - theSegment1.start().y) * (theSegment1.start().x - theSegment2.start().x)
		) / myDenominator;
		
		if (myDenominator == 0.0f) {
			if(ua == 0 && ub == 0)return new CCVector2f();
			return myResult;
		}
		
		if ((ua > 1.0f) || (ua < 0.0f) || (ub > 1.0f) || (ub < 0.0f))
			return myResult;
		myResult = new CCVector2f(
			theSegment1.start().x + ua * (theSegment1.end().x - theSegment1.start().x), 
			theSegment1.start().y + ua * (theSegment1.end().y - theSegment1.start().y)
		);
		return myResult;
	}
	
	public static List<CCVector2f> intersectLineCircle(final CCLine2f theSegment, final CCVector2f theCenter, final float theRadius) {

		CCVector2f dp = new CCVector2f(theSegment.end().x - theSegment.start().x, theSegment.end().y - theSegment.start().y);

		float a = dp.x * dp.x + dp.y * dp.y;
		float b = 2 * (dp.x * (theSegment.start().x - theCenter.x) + dp.y * (theSegment.start().y - theCenter.y));
		float c = theCenter.x * theCenter.x + theCenter.y * theCenter.y;
		c += theSegment.start().x * theSegment.start().x + theSegment.start().y * theSegment.start().y;
		c -= 2 * (theCenter.x * theSegment.start().x + theCenter.y * theSegment.start().y);
		c -= theRadius * theRadius;

		float bb4ac = b * b - 4 * a * c;

		if (CCMath.abs(a) < 0.01f || bb4ac < 0) {
			return null;
		}

		float mu1 = (-b + CCMath.sqrt(bb4ac)) / (2 * a);
		float mu2 = (-b - CCMath.sqrt(bb4ac)) / (2 * a);

		List<CCVector2f> myResult = new ArrayList<CCVector2f>();
		myResult.add(new CCVector2f(theSegment.start().x + mu1 * dp.x, theSegment.start().y + mu1 * dp.y));
		myResult.add(new CCVector2f(theSegment.start().x + mu2 * dp.x, theSegment.start().y + mu2 * dp.y));

		return myResult;

	}
}
