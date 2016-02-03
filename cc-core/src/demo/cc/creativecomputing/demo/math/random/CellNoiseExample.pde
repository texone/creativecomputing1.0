/*
* Created: by Carl-Johan Rosén as a part of the 
* course Procedural Methods for Images.
* At: Linköping Univeristy, Sweden.
* Date: 20060302
* Contact: cj(dot)rosen(at)gmail(dot)com
* Based on the work and research by Steven Worley.
*/

/* Importing the library. */
import CellNoise.*;

/*
* One instance of the main class is needed to get access to the 
* noise functions.
* The main function is the noise() function, which is used to 
* collect a set of values depending on the position in space or 
* plane. It takes an instance of the CellDataStruct class as its
* only argument. (See below for more info on CellDataStruct)
* The second public function is the constructor, which is used 
* to initialize the noise. It takes an instance of PApplet as
* its only argument.
*/
CellNoise cn;

/*
* One instance of the data structure is needed to send data to 
* and from the noise() function. It is through this class the 
* cell noise is controled.
* The user will use the only available function (the constructor)
* to set the properties of the noise. The properties are listed 
* below, as the are expected by the function CellDataStruct():
* - controler applet (PApplet parent).
* - order of feature points to measure distances to (int max_order).
* - position in space or plane (int[] at)
* - type of distance measure (int dist_type)
*   [EUCLIDEAN | CITYBLOCK | MANHATTAN | QUADRATIC]
*
* After calling the noise() function with a specific CellDataStruct,
* this instance contains the information connected to the position
* defined in this instance before the function call. The available 
* return data is listed below:
* - F[0 .. max_order] is the distance to the closest feature points, 
*   calculated using the distance measure specified.
* - ID[0 .. max_order] is a unique ID number for each feature point.
* - delta[0 .. max_order][0 .. dimensions] is an array of the vectors
*   pointing from the sample position towards each of the first 
*   feature points.
*/
CellDataStruct cd;
double[] at = {0, 0};
double t = 0;
PImage im;
int x, y, i;

void setup() {
  size(600, 600);
  cn = new CellNoise(this);
  cd = new CellDataStruct(this, 2, at, cn.EUCLIDEAN);
}

void draw() {
  loadPixels();
  
  t = 0;
  for (x = 0; x < width; x++) {
    at[0] = 0.01 * ( x + 20);
    for (y = 0; y < height; y++) {
      at[1] = 0.01 * ( y + 700 );
      
      
      /*
      * The CellDataStruct's (cd) position vector is set to current
      * position. In this case in 2D, adding a translation in
      * each call to draw().
      */
      cd.at = at;
      /*
      * The main work is done here. The result of the operation 
      * will be available in the CellDataStruct, cd, after the call.
      */
      cn.noise(cd);
      
      
      
      /*
      * Example 1: Point spread
      */
       pixels[x + y*width] = color(
         255,
         (float)cd.F[0]*2560,
         (float)cd.F[0]*2560
       );
      
      
      /*
      * Example 2: First order distance
      */
      // pixels[x + y*width] = color((float)cd.F[0] * 350);
      
      
      /*
      * Example 3: Second order distance
      */
      //pixels[x + y*width] = color((float)cd.F[1] * 120);
      
      
      /*
      * Example 4: First and second order distance difference
      */
      // pixels[x + y*width] = color((float)(cd.F[1]-cd.F[0]) * 120);
      
      
      /*
      * Example 5: Fractal combination
      */
      /* 
      double sum = 1;
      for (int i = 0; i < 4; i++) {
        at[0] = 0.01*(i*2+1) * (x + 20);
        at[1] = 0.01*(i*2+1) * (y + 700);
        cd.at = at;
        cn.noise(cd);
        sum *= (cd.F[0]);
      }
      pixels[x + y*width] = color((float)(sum)*255);
      */
      
      
      /*
      * Example 6: Cityblock distance
      */
      // pixels[x + y*width] = color((float)cd.F[0] * 150);
      
      
      /*
      * Example 7: ID number coloring
      */
      // pixels[x + y*width] = color((cd.ID[0] % 255), (cd.ID[0] % 155), (cd.ID[0] % 100));
      
      
      /*
      * Example 8: Noisy noise
      */
      /*
      at[0] = 0.01*cd.F[0] * (x + 20);
      at[1] = 0.01*cd.F[0] * (y + 700);
      cd.at = at;
      cn.noise(cd);
      pixels[x + y*width] = color((float)(cd.F[1])*150, (float)(cd.F[0]+cd.F[1])*50, (float)(cd.F[0])*10);
      */
    }
  }
  updatePixels();
}
