uniform vec4 splinePoints[20];
uniform float splineLengths[20];
uniform float splineLength;
uniform int splinePointsLength;

uniform vec4 linePoints[20];
uniform float lineLengths[20];
uniform float lineLength;
uniform int linePointsLength;

uniform float curveBlend;

uniform float time;
uniform float curveTension;
uniform float curveVariation;
uniform float curveLength;
uniform float curveAlpha;
uniform float curveAmount;
uniform float curveAmountBlend;

/**
 * Interpolate a spline between at least 4 control points following the Catmull-Rom equation.
 * here is the interpolation matrix
 * m = [ 0.0  1.0  0.0   0.0 ]
 *     [-T    0.0  T     0.0 ]
 *     [ 2T   T-3  3-2T  -T  ]
 *     [-T    2-T  T-2   T   ]
 * where T is the curve tension
 * the result is a value between p1 and p2, t=0 for p1, t=1 for p2
 * @param theU value from 0 to 1
 * @param theT The tension of the curve
 * @param theP0 control point 0
 * @param theP1 control point 1
 * @param theP2 control point 2
 * @param theP3 control point 3
 * @return catmull-Rom interpolation
 */
vec4 catmulRomPoint(vec4 theP0, vec4 theP1, vec4 theP2, vec4 theP3, float theU, float theT) {
	vec4 c1 = theP1;
	vec4 c2 = -1.0 * theT * theP0 + theT * theP2;
	vec4 c3 = 2.0 * theT * theP0 + (theT - 3.0) * theP1 + (3.0 - 2.0 * theT) * theP2 + -theT * theP3;
	vec4 c4 = -theT * theP0 + (2.0 - theT) * theP1 + (theT - 2.0) * theP2 + theT * theP3;

	return ((c4 * theU + c3) * theU + c2) * theU + c1;
}

vec4 interpolate(float value, int currentControlPoint, float theCurveTension) {
	return catmulRomPoint(
		splinePoints[currentControlPoint], 
		splinePoints[currentControlPoint + 1], 
		splinePoints[currentControlPoint + 2], 
		splinePoints[currentControlPoint + 3], 
		value, curveTension + theCurveTension
	);
}

/**
 * Interpolate a position on the spline
 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
 * @return the position
 */
vec4 interpolate (float theBlend, float theCurveTension){
	theBlend -= floor(theBlend);
	float myLength = splineLength * theBlend;//clamp(theBlend, 0.0, 1.0);
	float myReachedLength = 0.0;
	int myIndex = 0;
		
	while(myReachedLength + splineLengths[myIndex] < myLength){
		myReachedLength += splineLengths[myIndex];
		myIndex ++;
	}
		
	float myLocalLength = myLength - myReachedLength;
	float myLocalBlend = myLocalLength / splineLengths[myIndex];
	return interpolate(myLocalBlend, myIndex, theCurveTension);
}

void main(){	
	//Transform the vertex (ModelViewProj matrix)
	float myRandom = gl_Vertex.y;
	float myBlend = gl_Vertex.w;
	
	float myCurveRandom = myRandom - 0.5;
	float myCurveTension = myCurveRandom * curveVariation;
	
	float myCurveStart = gl_Vertex.x;
	float myCurveOffset = myBlend * curveLength;
	float myCurveSpeed = gl_Vertex.z;
	float myCurveMove = myCurveSpeed * time;
	float myCurvePos = myCurveStart + myCurveMove - myCurveOffset;
	
	
	float myAlpha = clamp(myRandom - 1.0 + curveAmountBlend * 3.0,0.0,1.0);
	
	vec4 myPoint = interpolate(myCurvePos, myCurveTension);
	myPoint.w = 1.0;
	gl_FrontColor = gl_Color;
	gl_FrontColor.a = curveAlpha * (1.0 - myBlend) * myAlpha;
	gl_Position = gl_ModelViewProjectionMatrix * myPoint;
}