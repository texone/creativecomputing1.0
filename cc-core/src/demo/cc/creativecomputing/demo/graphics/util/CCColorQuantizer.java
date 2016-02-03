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
package cc.creativecomputing.demo.graphics.util;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * Java implementation of the NeuQuant Neural-Net Quantization Algorithm
 *
 * Copyright (c) 1994 Anthony Dekker
 *
 * NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994.
 * See "Kohonen neural networks for optimal color quantization"
 * in "Network: Computation in Neural Systems" Vol. 5 (1994) pp 351-367.
 * for a discussion of the algorithm.
 * 
 * @author christianriekoff
 *
 */
public class CCColorQuantizer {

	/**
	 * no. of learning cycles
	 */
	private int _myLearningCycles = 128;

	/**
	 * number of colors used
	 */
	private int _myNetSize;
	/**
	 * number of reserved colors used
	 */
	private int _mySpecials;
	private int cutnetsize;
	private int maxnetpos;

	private int initrad; // for 256 colors, radius starts at 32
	private int radiusbiasshift = 6;
	private int radiusbias = 1 << radiusbiasshift;
	private int initBiasRadius;
	private int radiusdec = 30; // factor of 1/30 each cycle

	private int alphabiasshift = 10; // alpha starts at 1
	private int initalpha = 1 << alphabiasshift; // biased by 10 bits

	private double gamma = 1024.0;
	private double beta = 1.0 / 1024.0;
	private double betagamma = beta * gamma;

	private double[][] network; // the network itself
	private int[][] colormap; // the network itself

	private int[] netindex = new int[256]; // for network lookup - really 256

	private double[] bias; // bias and freq arrays for learning
	private double[] freq;

	// four primes near 500 - assume no image has a length so large
	// that it is divisible by all four primes

	private int prime1 = 499;
	private int prime2 = 491;
	private int prime3 = 487;
	private int prime4 = 503;

	private int samplefac;

	public CCColorQuantizer(int colorCount, boolean blackWhite) {
		samplefac = 1;

		_myNetSize = colorCount; // number of colours used
		if (blackWhite)
			_mySpecials = 2; // number of reserved colours used
		else
			_mySpecials = 0; // number of reserved colours used
		cutnetsize = _myNetSize - _mySpecials;
		maxnetpos = _myNetSize - 1;

		initrad = _myNetSize / 8; // for 256 cols, radius starts at 32

		initBiasRadius = initrad * radiusbias;

		network = new double[_myNetSize][3]; // the network itself
		colormap = new int[_myNetSize][4]; // the network itself
		bias = new double[_myNetSize]; // bias and freq arrays for learning
		freq = new double[_myNetSize];

		setUpArrays();
	}

	public int colorCount() {
		return _myNetSize;
	}

	public CCColor[] colorMap() {
		CCColor[] exportMap = new CCColor[_myNetSize];
		for (int i = 0; i < _myNetSize; i++) {
			exportMap[i] = new CCColor(colormap[i][2], colormap[i][1], colormap[i][0]);
		}
		return exportMap;
	}

	private void setUpArrays() {
		network[0][0] = 0.0; // black
		network[0][1] = 0.0;
		network[0][2] = 0.0;
		
		freq[0] = 1.0 / _myNetSize;
		bias[0] = 0.0;

		network[1][0] = 1.0; // white
		network[1][1] = 1.0;
		network[1][2] = 1.0;
		
		freq[1] = 1.0 / _myNetSize;
		bias[1] = 0.0;

		// background
		// freq [2] = 1.0 / netsize;
		// bias [2] = 0.0;

		for (int i = _mySpecials; i < _myNetSize; i++) {
			double[] p = network[i];
			p[0] = (256.0 * (i - _mySpecials)) / cutnetsize;
			p[1] = (256.0 * (i - _mySpecials)) / cutnetsize;
			p[2] = (256.0 * (i - _mySpecials)) / cutnetsize;

			freq[i] = 1.0 / _myNetSize;
			bias[i] = 0.0;
		}
	}

	public void init(CCTextureData theData) {
		learn(theData);
		fix();
		inxbuild();
	}

	private void altersingle(double alpha, int i, double b, double g, double r) {
		// Move neuron i towards biased (b,g,r) by factor alpha
		double[] n = network[i]; // alter hit neuron
		n[0] -= (alpha * (n[0] - b));
		n[1] -= (alpha * (n[1] - g));
		n[2] -= (alpha * (n[2] - r));
	}

	private void alterneigh(double alpha, int rad, int i, double b, double g, double r) {

		int lo = i - rad;
		if (lo < _mySpecials - 1)
			lo = _mySpecials - 1;
		int hi = i + rad;
		if (hi > _myNetSize)
			hi = _myNetSize;

		int j = i + 1;
		int k = i - 1;
		int q = 0;
		while ((j < hi) || (k > lo)) {
			double a = (alpha * (rad * rad - q * q)) / (rad * rad);
			q++;
			if (j < hi) {
				double[] p = network[j];
				p[0] -= (a * (p[0] - b));
				p[1] -= (a * (p[1] - g));
				p[2] -= (a * (p[2] - r));
				j++;
			}
			if (k > lo) {
				double[] p = network[k];
				p[0] -= (a * (p[0] - b));
				p[1] -= (a * (p[1] - g));
				p[2] -= (a * (p[2] - r));
				k--;
			}
		}
	}

	private int contest(double b, double g, double r) { // Search for biased BGR values
		// finds closest neuron (min dist) and updates freq
		// finds best neuron (min dist-bias) and returns position
		// for frequently chosen neurons, freq[i] is high and bias[i] is negative
		// bias[i] = gamma*((1/netsize)-freq[i])

		double bestd = Float.MAX_VALUE;
		double bestbiasd = bestd;
		int bestpos = -1;
		int bestbiaspos = bestpos;

		for (int i = 0; i < _myNetSize; i++) {
			double[] n = network[i];
			double dist = n[0] - b;
			if (dist < 0)
				dist = -dist;
			double a = n[1] - g;
			if (a < 0)
				a = -a;
			dist += a;
			a = n[2] - r;
			if (a < 0)
				a = -a;
			dist += a;
			if (dist < bestd) {
				bestd = dist;
				bestpos = i;
			}
			double biasdist = dist - bias[i];
			if (biasdist < bestbiasd) {
				bestbiasd = biasdist;
				bestbiaspos = i;
			}
			freq[i] -= beta * freq[i];
			bias[i] += betagamma * freq[i];
		}
		freq[bestpos] += beta;
		bias[bestpos] -= betagamma;
		return bestbiaspos;
	}

	private int specialFind(double b, double g, double r) {
		for (int i = 0; i < _mySpecials; i++) {
			double[] n = network[i];
			if (n[0] == b && n[1] == g && n[2] == r)
				return i;
		}
		return -1;
	}

	private void learn(CCTextureData theData) {
		int biasRadius = initBiasRadius;
		int alphadec = 30 + ((samplefac - 1) / 3);
		int lengthcount = theData.width() * theData.height();
		int samplepixels = lengthcount / samplefac;
		int delta = samplepixels / _myLearningCycles;
		int alpha = initalpha;

		int i = 0;
		int rad = biasRadius >> radiusbiasshift;
		if (rad <= 1)
			rad = 0;

		int step = 0;
		int pos = 0;

		if ((lengthcount % prime1) != 0)
			step = prime1;
		else if ((lengthcount % prime2) != 0)
			step = prime2;
		else if ((lengthcount % prime3) != 0)
			step = prime3;
		else
			step = prime4;

		i = 0;
		for(int x = 0; x < theData.width();x+=samplefac) {
			for(int y = 0; y < theData.height();y+=samplefac) {
				CCColor theColor = theData.getPixel(x, y);
				
				int r = (int) (theColor.r * 255);
				int g = (int) (theColor.g * 255);
				int b = (int) (theColor.b * 255);
	
	
				/*
				 * if (i == 0) { // remember background colour network [2] [0] = b; network [2] [1] = g; network [2] [2] =
				 * r; }
				 */
				int j = specialFind(b, g, r);
				j = j < 0 ? contest(b, g, r) : j;
	
				if (j >= _mySpecials) { // don't learn for specials
					double a = (1.0 * alpha) / initalpha;
					altersingle(a, j, b, g, r);
					if (rad > 0)
						alterneigh(a, rad, j, b, g, r); // alter neighbours
				}
	
				pos += step;
				while (pos >= lengthcount)
					pos -= lengthcount;
	
				i++;
				if (i % delta == 0) {
					alpha -= alpha / alphadec;
					biasRadius -= biasRadius / radiusdec;
					rad = biasRadius >> radiusbiasshift;
					if (rad <= 1)
						rad = 0;
				}
			}
		}
	}

	private void fix() {
		for (int i = 0; i < _myNetSize; i++) {
			for (int j = 0; j < 3; j++) {
				int x = (int) (0.5 + network[i][j]);
				if (x < 0)
					x = 0;
				if (x > 255)
					x = 255;
				colormap[i][j] = x;
			}
			colormap[i][3] = i;
		}
	}

	private void inxbuild() {
		// Insertion sort of network and building of netindex[0..255]

		int previouscol = 0;
		int startpos = 0;

		for (int i = 0; i < _myNetSize; i++) {
			int[] p = colormap[i];
			int[] q = null;
			int smallpos = i;
			int smallval = p[1]; // index on g
			// find smallest in i..netsize-1
			for (int j = i + 1; j < _myNetSize; j++) {
				q = colormap[j];
				if (q[1] < smallval) { // index on g
					smallpos = j;
					smallval = q[1]; // index on g
				}
			}
			q = colormap[smallpos];
			// swap p (i) and q (smallpos) entries
			if (i != smallpos) {
				int j = q[0];
				q[0] = p[0];
				p[0] = j;
				j = q[1];
				q[1] = p[1];
				p[1] = j;
				j = q[2];
				q[2] = p[2];
				p[2] = j;
				j = q[3];
				q[3] = p[3];
				p[3] = j;
			}
			// smallval entry is now in position i
			if (smallval != previouscol) {
				netindex[previouscol] = (startpos + i) >> 1;
				for (int j = previouscol + 1; j < smallval; j++)
					netindex[j] = i;
				previouscol = smallval;
				startpos = i;
			}
		}
		netindex[previouscol] = (startpos + maxnetpos) >> 1;
		for (int j = previouscol + 1; j < 256; j++)
			netindex[j] = maxnetpos; // really 256
	}

	public CCColor convert(CCColor theColor) {
		int r = (int) (theColor.r * 255);
		int g = (int) (theColor.g * 255);
		int b = (int) (theColor.b * 255);
		int a = (int) (theColor.a * 255);

		int i = inxsearch(b, g, r);

		b = colormap[i][0];
		g = colormap[i][1];
		r = colormap[i][2];

		return new CCColor(r, g, b, a);
	}

	public int lookup(CCColor theColor) {
		int r = (int) (theColor.r * 255);
		int g = (int) (theColor.g * 255);
		int b = (int) (theColor.b * 255);
		int i = inxsearch(b, g, r);
		return i;
	}

	public int lookup(boolean rgb, int x, int g, int y) {
		int i = rgb ? inxsearch(y, g, x) : inxsearch(x, g, y);
		return i;
	}

	private int inxsearch(int b, int g, int r) {
		// Search for BGR values 0..255 and return colour index
		int bestd = 1000; // biggest possible dist is 256*3
		int best = -1;
		int i = netindex[g]; // index on g
		int j = i - 1; // start at netindex[g] and work outwards

		while ((i < _myNetSize) || (j >= 0)) {
			if (i < _myNetSize) {
				int[] p = colormap[i];
				int dist = p[1] - g; // inx key
				if (dist >= bestd)
					i = _myNetSize; // stop iter
				else {
					if (dist < 0)
						dist = -dist;
					int a = p[0] - b;
					if (a < 0)
						a = -a;
					dist += a;
					if (dist < bestd) {
						a = p[2] - r;
						if (a < 0)
							a = -a;
						dist += a;
						if (dist < bestd) {
							bestd = dist;
							best = i;
						}
					}
					i++;
				}
			}
			if (j >= 0) {
				int[] p = colormap[j];
				int dist = g - p[1]; // inx key - reverse dif
				if (dist >= bestd)
					j = -1; // stop iter
				else {
					if (dist < 0)
						dist = -dist;
					int a = p[0] - b;
					if (a < 0)
						a = -a;
					dist += a;
					if (dist < bestd) {
						a = p[2] - r;
						if (a < 0)
							a = -a;
						dist += a;
						if (dist < bestd) {
							bestd = dist;
							best = j;
						}
					}
					j--;
				}
			}
		}

		return best;
	}

}
