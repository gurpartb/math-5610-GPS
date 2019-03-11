public class RadiansToDegrees {
	private int degrees;
	private int minutes;
	private double seconds;
	private double radians;

	public RadiansToDegrees(double radians) {
		this.radians = radians;
		setDegreesMinutesSeconds(this.radians);
	}
	
	public void setDegreesMinutesSeconds(double radians){
		this.degrees = (int)(radians*180/Math.PI);
		this.minutes = (int)(((radians*180/Math.PI)-degrees)*60);
		this.seconds = ((((radians*180/Math.PI)-degrees)*60)-minutes)*60;
	}
	
	public int getDegrees(){
		return this.degrees;
	}
	
	public int getMinutes(){
		return this.minutes;
	}
	
	public double getSeconds(){
		return this.seconds;
	}
}
