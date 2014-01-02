package net.sf.opengroove.projects.filleditor.bezier;
class Real {
	static int defaultprec = 4;
	final static String Plus = "";
	final static String Minus = "-";

	public static String toString(double x, int prec) {
		String S = "";
		// char s[] = new char[20];
		long m, n;
		int i;
		// char c;
		double p = 1.0;
		double y;
		String sign = Plus;

		if (x < 0) {
			sign = Minus;
			x = -x;
		}
		// find 10^prec and round off x 
		for (i=0;i<prec;i++) {
			p = p*10.0;
		}
		x = x*p;
		n = (long) x;
		y = x - (double) n;
		if (y >= 0.5) {
			x += 1.0;			
		} 
		x = x/p;

		n = (long) x;
		x = x - n;
		for (i=0;i<prec;i++) {
			x = 10.0*x;
			m = (int) x;
			x = x - m;
			// c = (char) m;
			S = S + m;
		}
		// s[i] = (char) 0;
		if (prec > 0) {
			return(sign + String.valueOf(n) + "." + S);
		}
		else {
			return(sign + String.valueOf(n));
		}
	}

	static int strlen(String s) {
		int i = 0;
		while (i < s.length() && s.charAt(i) != '\0') {
			i++;
		}
		return(i);
	}

	public static String toString(double x, int prec, int wd) {
		StringBuffer t;
		String s = toString(x, prec);
		int len = strlen(s);

		// s.length doesn't work!

//System.out.println(len + ", " + wd);
//System.out.println("<" + s + ">");

		if (wd > len) {
			t = new StringBuffer(wd);
			for (int i=len;i<wd;i++) {
				t.append(" ");
			}
			t.append(s);
			s = t.toString();
//System.out.println("(" + s + ")");
			return(s);
		}
		else {
			return(s);
		}
	}

	public static String toString(double x) {
		return(toString(x, defaultprec));
	}

	public static double toDegrees(double radians) {
		return(radians*180.0/Math.PI);
	}

	public static double toRadians(double degrees) {
		return(degrees*Math.PI/180.0);
	}
} 
