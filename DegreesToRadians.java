public class DegreesToRadians {
	private int degrees;
	private int minutes;
	private double seconds;
	private double radians;

	public DegreesToRadians(int degrees, int minutes, double seconds) {
		this.degrees = degrees;
		this.minutes = minutes;
		this.seconds = seconds;
		this.setRadians(this.degrees, this.minutes, this.seconds);
	}
	
	public void setRadians(int degrees, int minutes, double seconds){
		this.radians = (Math.PI/180)*((double) degrees+ (double) minutes/60+seconds/3600);
	}
	
	public double getRadians(){
		return this.radians;
	}
	
}
