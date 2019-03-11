public class Angles {
	double radians, seconds;
	int degrees, minutes;
	boolean plus;

	final static double pi = 2*java.lang.Math.acos(0.0);
	final static double twopi = 2.0*pi;

	public Angles (double rad) {
		radians = rad;
		if(radians<-pi|| radians>pi) {
			int n = (int) ((radians+pi)/twopi);
			if(radians<0.0)
				n--;
			radians = radians - n*twopi;
		}
		double theta = radians;
		if(theta >+ 0.0)
			plus=true;
		else {
			plus = false;
			theta = -theta;
		}
		double degs = theta*360/twopi;
		degrees = (int) degs;
		degs = degs-degrees;
		degs = 60*degs;
		minutes = (int) degs;
		degs = degs-minutes;
		seconds = 60*degs;

		if(java.lang.Math.abs(60-seconds)<1.0E-3){
			seconds = 0;
			minutes++;
		}
		if(minutes == 60) {
			minutes = 0;
			degrees++;
		}
		check();
	}

	public Angles( int deg, int min, int sec, boolean pm) {
		degrees = deg;
		minutes = min;
		seconds = sec;
		plus = pm;
		radians = rad(deg, min, sec, pm);
		check();
	}

	static double rad(int deg, int min, double sec, int NS) {
		if(NS==1)
			return rad(deg, min, sec, true);
		else if(NS==-1)
			return rad(deg, min, sec, false);
		else
			return 0;
	}

	static double rad( int deg, int min, double sec, boolean pm) {
		double Deg = (double) deg;
		double Min = (double) min;
		double result = twopi/360*(Deg+Min/60+sec/60/60);
		if(!pm)
			result = -result;
		return result;
	}

	void report(String note) {
		String s = note+radians+" radians = ";
		if(!plus)
			s = s+" - ";
		s = s + degrees +" "+minutes+" "+seconds;
		System.out.println(s);
	}

	public static void main (String [] args) {
		boolean correct = true;
		boolean pm = false;
		for(int k=0; k<2; k++)
			if (k==0)
				pm=true;
			else 
				pm = false;

		for (int deg = 0; deg<180; deg++)
			for(int min =0; min <60; min++)
				for(int sec = 0; sec<60; sec++) {
					Angles a = new Angles(deg, min, sec, pm);
					Angles b = new Angles(a.radians);
					if(b.degrees !=deg || b.minutes != min
							|| java.lang.Math.abs(((double) sec)-b.seconds)>1.0E-6
							|| (b.plus !=pm && b.radians !=0.0)){
						correct = false;
						System.out.println("Angles error: ");
						a.report("input: ");
						b.report("Output: ");
					}
				}
		if(correct)
			System.out.println("angles passed self test");
	}
	
	void check() {
		if(degrees<0|| degrees>180
				|| minutes <0 || minutes >59
				|| seconds <0.0 || seconds >= 60.0
				|| radians < -pi || radians > pi)
			report("angles error ");
	}
}
