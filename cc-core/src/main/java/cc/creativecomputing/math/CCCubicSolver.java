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

/**
 * @author christianriekoff
 * 
 */
public class CCCubicSolver {
	// cubic equation solver example using Cardano's method

	private static double THIRD = 0.333333333333333;
	private static double ROOTTHREE = 1.73205080756888;

	// this function returns the cube root if x were a negative number as well
	
	/**
	 * Returns the cube root of the given value, also for negative x values
	 */
	public static double cubeRoot(double theX) {
		if (theX < 0)
			return -Math.pow(-theX, THIRD);
		else
			return Math.pow(theX, THIRD);
	}

	public static double[] solveCubic(double a, double b, double c, double d) {
		double[] myResult = new double[3];
		
		// find the discriminant
		double f = (3 * c / a - Math.pow(b, 2) / Math.pow(a, 2)) / 3;
		double g = (2 * Math.pow(b, 3) / Math.pow(a, 3) - 9 * b * c / Math.pow(a, 2) + 27 * d / a) / 27;
		double h = Math.pow(g, 2) / 4 + Math.pow(f, 3) / 27;

		// evaluate discriminant
		if (f == 0 && g == 0 && h == 0) {
			// 3 equal roots
			// when f, g, and h all equal 0 the roots can be found by the following line
			double x = -cubeRoot(d / a);
			myResult[0] = x;
			myResult[1] = x;
			myResult[2] = x;
			return myResult;
		} 
		
		if (h <= 0) {
			// 3 real roots
			// complicated math making use of the method
			double i = Math.pow(Math.pow(g, 2) / 4 - h, 0.5);
			double j = cubeRoot(i);
			double k = Math.acos(-(g / (2 * i)));
			double m = Math.cos(k / 3);
			double n = ROOTTHREE * Math.sin(k / 3);
			double p = -(b / (3 * a));
			
			myResult[0] = 2 * j * m + p;
			myResult[1] = -j * (m + n) + p;
			myResult[2] = -j * (m - n) + p;
			return myResult;
		}
		
		if (h > 0) {
			// 1 real root and 2 complex roots
			double r, s, t, u, p;
			// complicated maths making use of the method
			r = -(g / 2) + Math.pow(h, 0.5);
			s = cubeRoot(r);
			t = -(g / 2) - Math.pow(h, 0.5);
			u = cubeRoot(t);
			p = -(b / (3 * a));
			
			myResult[0] = s + u + p;
			myResult[1] = -(s + u) / 2 + p;
			myResult[2] = -(s + u) / 2 + p;
		}
		
		return myResult;
	}

	public static void main(String[] args) {

		double[] myResult = solveCubic(45, 24, -7, -2);
		// print solutions
		System.out.println("x = ");
		System.out.println(" " + myResult[0]);
		System.out.println(" " + myResult[1]);
		System.out.println(" " + myResult[2]);
	}

}
