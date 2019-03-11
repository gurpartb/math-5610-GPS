public class GPStoCartecian {
	
	public static void main(String arg []) {
		double r = 6367444.50;
		double lat = 0.711488177;
		double lon = -1.9521410721;
		double xcord = xCoordTime(12600, r, 1372.0, lat, lon);
		double ycord = yCoordTime(12600, r, 1372.0, lat,  lon);
		System.out.println(xcord+" "+ycord);
	}

	public static double xCoord(double radius, double altitude, double latRad, double lonRad) {
		return (radius+altitude)*Math.cos(latRad)*Math.cos(lonRad);
	}

	public static double yCoord(double radius, double altitude, double latRad, double lonRad) {
		return (radius+altitude)*Math.cos(latRad)*Math.sin(lonRad);
	}

	public static double zCoord(double radius, double altitude, double latRad) {
		return (radius+altitude)*Math.sin(latRad);
	}
	
	public static double xCoordTime(double time, double radius, double altitude, double latRad, double lonRad) {
		
		return (GPStoCartecian.xCoord(radius, altitude, latRad, lonRad)*Math.cos(2*Math.PI*time/86164.09)
				-GPStoCartecian.yCoord(radius, altitude, latRad, lonRad)*Math.sin(2*Math.PI*time/86164.09));
	}

	public static double yCoordTime(double time, double radius, double altitude, double latRad, double lonRad) {
		return (GPStoCartecian.xCoord(radius, altitude, latRad, lonRad)*Math.sin(2*Math.PI*time/86164.09)
				+GPStoCartecian.yCoord(radius, altitude, latRad, lonRad)*Math.cos(2*Math.PI*time/86164.09));
	}

}
