package net.sf.opengroove.projects.filleditor.bezier;
import java.lang.String;
import java.lang.StringBuffer;

/* Copyright 1997 Bill Casselman
   University of British Columbia
   cass@math.ubc.ca */

class RealPoint {
	double x, y;

	RealPoint(double a, double b) {
		x = a;
		y = b;
	}

	public String toString() {
		return("(" + x + ", " + y + ")");
	}
	
	// [c -s | s c] [x y] 
	
	public RealPoint rotated(double theta) {
		double c, s;
		c = Math.cos(theta);
		s = Math.sin(theta);
		RealPoint Q = new RealPoint(c*x - s*y, s*x + c*y);
		return(Q);
	}
	public RealPoint interp(double t, RealPoint P) {
		RealPoint Q = new RealPoint((1.0 - t)*x + t*P.x, (1.0 - t)*y + t*P.y);
		return(Q);
	}
}

