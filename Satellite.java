import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;

public class Satellite {
	// field pi, speed of light, radius of Earth, s lenght of siderial day
	//private double pi, c_speed, rEarth, sDay;
	private static double pi;
	private static double c_speed;
	private static double rEarth;
	private static double sDay;


	public static void main(String [] args) {

		File dataFile = new File("data.dat");
		Scanner dataFileScanner = null;
		try {
			dataFileScanner = new Scanner(dataFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pi = dataFileScanner.nextDouble(); dataFileScanner.nextLine();
		c_speed = dataFileScanner.nextDouble(); dataFileScanner.nextLine();
		rEarth = dataFileScanner.nextDouble(); dataFileScanner.nextLine();
		//System.out.println(rEarth);
		sDay = dataFileScanner.nextDouble(); dataFileScanner.nextLine();
		//System.out.println(pi+" "+ c_speed+" "+rEarth+" "+sDay);

		double[][] satU = new double [24][3];
		double[][] satV = new double [24][3];
		double[][] satAtrib = new double [24][3];
		// i0 periodicity , i1 altitude, i2 phase

		for(int i =0; i<24; i++) {
			satU[i][0]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satU[i][1]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satU[i][2]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satV[i][0]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satV[i][1]=dataFileScanner.nextDouble(); dataFileScanner.nextLine();
			satV[i][2]=dataFileScanner.nextDouble(); dataFileScanner.nextLine();
			satAtrib[i][0]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satAtrib[i][1]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
			satAtrib[i][2]=dataFileScanner.nextDouble();dataFileScanner.nextLine();
		}
		dataFileScanner.close();

		InputStreamReader testReader = new InputStreamReader(System.in);
		BufferedReader br=null;
		//try{
		    br = new BufferedReader(testReader);
		    String screenGrab = "G";

		    try{
			while((screenGrab=br.readLine())!=null){
			    Scanner in = new Scanner(screenGrab);
			    double tV = in.nextDouble();
			    double [] xV = vehiclePosInitial(tV, in);
			    in.close();
			    double [][] satCoord=satPos(tV, satU, satV, satAtrib);
			     satsAboveHorizon(tV, satCoord, satU, satV, satAtrib, xV);
			}

			// currently reciever position is B12
			//String s = "12600 40 45 55.0 1 111 50 58.0 -1 1372";
			   
			} catch (Exception e){
			    e.printStackTrace();
			    
			}
		    /*}finally{
		    br.close();
		    }*/
		
		
	}

	public static void  satsAboveHorizon(double tV, double [][] satCoord, double [][] satU, double [][] satV,
			double [][] satAtrib, double [] Xv){
		int satCount = 0;
		double [][] satsAboveHorizon = new double [24][5];

		// check each satellite, if above horizon store satellite number
		for(int i = 0; i<satCoord.length;i++) {
			if(isAboveHorizon(Xv[0], Xv[1], Xv[2], satCoord[i][0], satCoord[i][1], satCoord[i][2])) {
				satsAboveHorizon[satCount][0]=i;
				// calculate sat time and plug in the second column
				double tknot = satelliteSignalTimeSimple(satU, satV, satAtrib, i,tV, Xv,  tV );
				satsAboveHorizon[satCount][1]=tknot; //satelliteTime(satU, satV, satAtrib, i, Xv,  tknot);
				double [] xS = satPos(tknot,satU,satV, satAtrib, i);
				satsAboveHorizon[satCount][2]=xS[0];
				satsAboveHorizon[satCount][3]= xS[1];
				satsAboveHorizon[satCount][4]= xS[2];
				satCount++;
			}
		}
		PrintStream print =null;
		for(int row =0; row<satCount ; row++) {
			String satOutput= (int)satsAboveHorizon[row][0]+" "+satsAboveHorizon[row][1]+" "+
					satsAboveHorizon[row][2]+" "+satsAboveHorizon[row][3]+" "+satsAboveHorizon[row][4];
			System.out.println(satOutput);
			try{
			    FileOutputStream fos = new FileOutputStream("satellite.log");
			    print = new PrintStream(fos);
			    print.println(satOutput);
			}catch(FileNotFoundException e){
			    e.printStackTrace();
			}
		}
		print.close();
	}

	public static boolean isAboveHorizon(double Xrec, double Yrec, double Zrec, double Xsat, double Ysat, double Zsat) {
		// build vector from receiver/ vehicle to satellite
		double XrecToSat = Xsat-Xrec; double YrecToSat = Ysat-Yrec; double ZrecToSat = Zsat-Zrec;
		// if dotProduct > 0 return true
		return (XrecToSat*Xrec+YrecToSat*Yrec+ZrecToSat*Zrec)>0;
	}

	public static double satelliteSignalTimeSimple( double[][] satU, double [][] satV, double [][] satAtrib,
			int sat,double tV, double [] Xv, double tKnot) {
		double [] satPos=satPos(tKnot, satU, satV, satAtrib, sat);
		satPos[0]=satPos[0]-Xv[0];satPos[1]=satPos[1]-Xv[1];satPos[2]=satPos[2]-Xv[2];
		double timeKnotPlusOne = tV-Vector.normTwo(satPos)/c_speed;
		if(Math.abs(timeKnotPlusOne-tKnot)<.01/c_speed)
		return timeKnotPlusOne;
		return satelliteSignalTimeSimple( satU, satV, satAtrib, sat,tV, Xv, timeKnotPlusOne);
	}

	public static double [] satPos(double time, double[][] satU, double [][] satV, double [][] satAtrib, int sat) {
		double [] satPos = new double[3]; int i = sat;
		satPos[0]=(rEarth+satAtrib[i][1])*(satU[i][0]*Math.cos(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2])+
				satV[i][0]*Math.sin(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2]));
		satPos[1]=(rEarth+satAtrib[i][1])*(satU[i][1]*Math.cos(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2])+
				satV[i][1]*Math.sin(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2]));
		satPos[2]=(rEarth+satAtrib[i][1])*(satU[i][2]*Math.cos(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2])+
				satV[i][2]*Math.sin(2*Math.PI*time/satAtrib[i][0]+satAtrib[i][2]));
		return satPos;
	}
	// circular orbit of a sattelite
	public static double[][] satPos(double timeVehicleInitial, double[][] satU,
			double[][] satV, double [][] satAtrib ){
		double[][] satCoord = new double [satU.length][3];
		for(int i =0; i<24; i++) {
			satCoord[i][0]=(rEarth+satAtrib[i][1])*(satU[i][0]*Math.cos(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2])+
					satV[i][0]*Math.sin(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2]));
			satCoord[i][1]=(rEarth+satAtrib[i][1])*(satU[i][1]*Math.cos(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2])+
					satV[i][1]*Math.sin(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2]));
			satCoord[i][2]=(rEarth+satAtrib[i][1])*(satU[i][2]*Math.cos(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2])+
					satV[i][2]*Math.sin(2*Math.PI*timeVehicleInitial/satAtrib[i][0]+satAtrib[i][2]));
			//satCoord[i][3]=timeVehicleInitial;
		}
		return satCoord;
	}

	public static double [] vehiclePosInitial(double timeVehicleInitial, Scanner in){
		double time, latSec, longSec, alt, latRad, lonRad;
		int latDeg, latMin, lonDeg, lonMin, NS, EW;

		time = timeVehicleInitial;
		latDeg = in.nextInt(); latMin = in.nextInt(); latSec = in.nextDouble();
		NS = in.nextInt();
		lonDeg = in.nextInt(); lonMin = in.nextInt(); longSec = in.nextDouble();
		EW = in.nextInt(); alt = in.nextDouble();

		latRad = Angles.rad(latDeg, latMin, latSec, NS);
		//System.out.println(latRad);
		lonRad = Angles.rad(lonDeg, lonMin, longSec, !(time<43200));
		//System.out.println(lonRad);

		double [] Xv = new double [3];
		Xv[0]=GPStoCartecian.xCoordTime(time, rEarth, alt, latRad, lonRad);
		Xv[1] = GPStoCartecian.yCoordTime(time, rEarth, alt, latRad, lonRad);
		Xv[2]= GPStoCartecian.zCoord(rEarth, alt, latRad);
		return Xv;
	}
}
