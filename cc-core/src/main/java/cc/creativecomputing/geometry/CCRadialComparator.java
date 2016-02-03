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
package cc.creativecomputing.geometry;

import java.util.Comparator;

import cc.creativecomputing.math.CCVector2f;

public class CCRadialComparator implements Comparator<CCVector2f> {

	private CCVector2f origin;

	public static final int CounterClockwise = 1;

	public static final int Clockwise = -1;

	public static final int Collinear = 0;

	// / <summary>
	// / Computes the orientation of a point q to the directed line segment p1-p2.
	// / The orientation of a point relative to a directed line segment indicates
	// / which way you turn to get to q after travelling from p1 to p2.
	// / </summary>
	// / <param name="p1"></param>
	// / <param name="p2"></param>
	// / <param name="q"></param>
	// / <returns>
	// / 1 if q is counter-clockwise from p1-p2,
	// / -1 if q is clockwise from p1-p2,
	// / 0 if q is collinear with p1-p2-
	// / </returns>
	public static int computeOrientation(CCVector2f p1, CCVector2f p2, CCVector2f q) {
		return orientationIndex(p1, p2, q);
	}

	// / <summary>
	// /
	// / </summary>
	// / <param name="x1"></param>
	// / <param name="y1"></param>
	// / <param name="x2"></param>
	// / <param name="y2"></param>
	// / <returns>
	// / returns -1 if the determinant is negative,
	// / returns 1 if the determinant is positive,
	// / retunrs 0 if the determinant is null.
	// / </returns>
	public static int signOfDet2x2(double x1, double y1, double x2, double y2) {
		// returns -1 if the determinant is negative,
		// returns 1 if the determinant is positive,
		// returns 0 if the determinant is null.

		int sign;
		double swap;
		double k;
		long count = 0;

		sign = 1;

		/*
		 * testing null entries
		 */
		if ((x1 == 0.0) || (y2 == 0.0)) {
			if ((y1 == 0.0) || (x2 == 0.0)) {
				return 0;
			} else if (y1 > 0) {
				if (x2 > 0) {
					return -sign;
				} else {
					return sign;
				}
			} else {
				if (x2 > 0) {
					return sign;
				} else {
					return -sign;
				}
			}
		}
		if ((y1 == 0.0) || (x2 == 0.0)) {
			if (y2 > 0) {
				if (x1 > 0) {
					return sign;
				} else {
					return -sign;
				}
			} else {
				if (x1 > 0) {
					return -sign;
				} else {
					return sign;
				}
			}
		}

		/*
		 * making y coordinates positive and permuting the entries
		 */
		/*
		 * so that y2 is the biggest one
		 */
		if (0.0 < y1) {
			if (0.0 < y2) {
				if (y1 <= y2) {
					;
				} else {
					sign = -sign;
					swap = x1;
					x1 = x2;
					x2 = swap;
					swap = y1;
					y1 = y2;
					y2 = swap;
				}
			} else {
				if (y1 <= -y2) {
					sign = -sign;
					x2 = -x2;
					y2 = -y2;
				} else {
					swap = x1;
					x1 = -x2;
					x2 = swap;
					swap = y1;
					y1 = -y2;
					y2 = swap;
				}
			}
		} else {
			if (0.0 < y2) {
				if (-y1 <= y2) {
					sign = -sign;
					x1 = -x1;
					y1 = -y1;
				} else {
					swap = -x1;
					x1 = x2;
					x2 = swap;
					swap = -y1;
					y1 = y2;
					y2 = swap;
				}
			} else {
				if (y1 >= y2) {
					x1 = -x1;
					y1 = -y1;
					x2 = -x2;
					y2 = -y2;
					;
				} else {
					sign = -sign;
					swap = -x1;
					x1 = -x2;
					x2 = swap;
					swap = -y1;
					y1 = -y2;
					y2 = swap;
				}
			}
		}

		/*
		 * making x coordinates positive
		 */
		/*
		 * if |x2| < |x1| one can conclude
		 */
		if (0.0 < x1) {
			if (0.0 < x2) {
				if (x1 <= x2) {
					;
				} else {
					return sign;
				}
			} else {
				return sign;
			}
		} else {
			if (0.0 < x2) {
				return -sign;
			} else {
				if (x1 >= x2) {
					sign = -sign;
					x1 = -x1;
					x2 = -x2;
					;
				} else {
					return -sign;
				}
			}
		}

		/*
		 * all entries strictly positive x1 <= x2 and y1 <= y2
		 */
		while (true) {
			count = count + 1;
			k = Math.floor(x2 / x1);
			x2 = x2 - k * x1;
			y2 = y2 - k * y1;

			/*
			 * testing if R (new U2) is in U1 rectangle
			 */
			if (y2 < 0.0) {
				return -sign;
			}
			if (y2 > y1) {
				return sign;
			}

			/*
			 * finding R'
			 */
			if (x1 > x2 + x2) {
				if (y1 < y2 + y2) {
					return sign;
				}
			} else {
				if (y1 > y2 + y2) {
					return -sign;
				} else {
					x2 = x1 - x2;
					y2 = y1 - y2;
					sign = -sign;
				}
			}
			if (y2 == 0.0) {
				if (x2 == 0.0) {
					return 0;
				} else {
					return -sign;
				}
			}
			if (x2 == 0.0) {
				return sign;
			}

			/*
			 * exchange 1 and 2 role.
			 */
			k = Math.floor(x1 / x2);
			x1 = x1 - k * x2;
			y1 = y1 - k * y2;

			/*
			 * testing if R (new U1) is in U2 rectangle
			 */
			if (y1 < 0.0) {
				return sign;
			}
			if (y1 > y2) {
				return -sign;
			}

			/*
			 * finding R'
			 */
			if (x2 > x1 + x1) {
				if (y2 < y1 + y1) {
					return -sign;
				}
			} else {
				if (y2 > y1 + y1) {
					return sign;
				} else {
					x1 = x2 - x1;
					y1 = y2 - y1;
					sign = -sign;
				}
			}
			if (y1 == 0.0) {
				if (x1 == 0.0) {
					return 0;
				} else {
					return sign;
				}
			}
			if (x1 == 0.0) {
				return -sign;
			}
		}

	}

	private static int orientationIndex(CCVector2f p1, CCVector2f p2, CCVector2f q) {
		// travelling along p1->p2, turn counter clockwise to get to q return 1,
		// travelling along p1->p2, turn clockwise to get to q return -1,
		// p1, p2 and q are colinear return 0.
		double dx1 = p2.x - p1.x;
		double dy1 = p2.y - p1.y;
		double dx2 = q.x - p2.x;
		double dy2 = q.y - p2.y;
		return signOfDet2x2(dx1, dy1, dx2, dy2);
	}

	public CCRadialComparator(CCVector2f origin) {
		assert origin != null;
		this.origin = origin;
	}

	public int compare(CCVector2f p1, CCVector2f p2) {
		return polarCompare(origin, p1, p2);
	}

	public void setOrigin(CCVector2f newO) {
		origin = newO;
	}

	private static int polarCompare(CCVector2f o, CCVector2f p, CCVector2f q) {
		double dxp = p.x - o.x;
		double dyp = p.y - o.y;
		double dxq = q.x - o.x;
		double dyq = q.y - o.y;

		int orient = computeOrientation(o, p, q);

		if (orient == CounterClockwise)
			return -1;
		if (orient == Clockwise)
			return 1;

		// points are collinear - check distance
		double op = dxp * dxp + dyp * dyp;
		double oq = dxq * dxq + dyq * dyq;
		if (op < oq)
			return 1;
		if (op > oq)
			return -1;

		return 0;
	}
}
