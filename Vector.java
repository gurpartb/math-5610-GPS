public class Vector {
	
	public static double multiply(double [] u, double [] v) {
		if(v.length!=u.length) {
			System.out.println(" Vector Mulitply: vector mismatch");
			return 0;
		}
		double scalar =0;
		for (int i=0; i<u.length;i++ )
			scalar = scalar+v[i]*u[i];
		return scalar;
	}
	
	public static double normTwo(double [] v) {
		return Math.sqrt(multiply(v, v));
	}
	
	public static double[] scale(double [] v, double scalar) {
		for(int i=0; i<v.length; i++)
			v[i]=v[i]*scalar;
		return v;
	}
	
	public static double[] subtract(double [] u, double [] v) {
		double [] vector = new double [u.length];
		for (int i=0; i<u.length;i++ )
			vector[i]=u[i]-v[i];
		return vector;
	}

}
