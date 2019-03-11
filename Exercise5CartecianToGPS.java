public class Exercise5CartecianToGPS {

	private double t_v; //Universal time in seconds
	private int psi_d; // range from 0 to 90 deg, (North or South Pole)
	private int psi_m; // 0 to 59 minutes of degree
	private double psi_s; // 0 to 59.9999 seconds of degree, 10^-2 accuracy
	private int NS; // +1 North of equator and -1 South of the equator
	private int lamda_d; // 0 deg (i.e., the meridian of Greenwich) to 180
				//(i.e., 180 deg east, or west, the date line
	private int lamda_m; // 0 to 59 minutes of degree
	private double lamda_s; // 0 to 59.9999 seconds of degree, 10^-2 accuracy
	private int EW; // +1 east of Greenwich and -1 west of Greenwich
	private double h; // altitude in meters, to an accuracy of 1cm/ //

	private double phi; //angle between z coordinate and the x,y plane (equator)
	private double theta; //angle between x and y axis.


	private double radiusEarth = 6367444.5;
	private double distanceFromOrigin; //

	private double x;
	private double y;
	private double z;

	public Exercise5CartecianToGPS(double x, double y, double z){

		if(z<0){
			z=Math.abs(z);
			NS = -1;
		} else
			NS = 1;

		if(y<0){
			EW = -1;
			y = Math.abs(y);
		} else {
			EW = 1;
		}

		distanceFromOrigin = magnitude(x, y, z);
		h = distanceFromOrigin-radiusEarth;

		this.x = radiusEarth/distanceFromOrigin*x;
		this.y = radiusEarth/distanceFromOrigin*y;
		this.z = radiusEarth/distanceFromOrigin*z;

		this.phi = Math.atan(z/Math.sqrt(this.x*this.x+this.y*this.y));
		this.theta = Math.atan(Math.abs(this.y/this.x));

		if(x<0)
			theta = theta + Math.PI/2;
		setLatitude(phi);
		setLongitude(theta);
	}

	private void setLongitude(double theta2) {
		RadiansToDegrees longitude = new RadiansToDegrees(theta2);
		lamda_d = longitude.getDegrees();
		lamda_m = longitude.getMinutes();
		lamda_s = longitude.getSeconds();
	}

	private void setLatitude(double phi2) {
		RadiansToDegrees latitude = new RadiansToDegrees(phi2);
		psi_d = latitude.getDegrees();
		psi_m = latitude.getMinutes();
		psi_s = latitude.getSeconds();
	}

	private double magnitude(double x, double y, double z){
		return Math.sqrt(x*x+ y*y+ z*z);
	}

	public String toString(){
		return psi_d+" "+psi_m+" "+psi_s+" "+NS+" "+lamda_d+" "+lamda_m+" "+lamda_s+" "+EW;
	}





}
