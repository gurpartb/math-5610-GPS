import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.PrintStream;



public class Receiver {

	private static double c_speed;
	private static double r_earth;

	public static void main(String [] args) {
		//new Receiver();

		// read the first part of the data.dat file, initialize variables
		File dataFile = new File("data.dat");
		Scanner dataFileScanner = null;
		try {
			dataFileScanner = new Scanner(dataFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double pi = dataFileScanner.nextDouble();
		dataFileScanner.nextLine();
		c_speed = dataFileScanner.nextDouble();
		dataFileScanner.nextLine();
		r_earth = dataFileScanner.nextDouble();
		dataFileScanner.nextLine();
		double sDay = dataFileScanner.nextDouble();
		dataFileScanner.nextLine();
		dataFileScanner.close();

		double [] Xv = {0,0,0};
		 PrintStream print = null;
                try {
                    FileOutputStream fos = new FileOutputStream("receiver.log");
                    print = new PrintStream(fos);
                        } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String screenData="G";

		try{
		    int sat =-1;
		    ArrayList<String> satList= new ArrayList<String>();
		    while((screenData=br.readLine())!=null){
			String satNumber = screenData.substring(0, screenData.indexOf(" "));
			int nextSat= Integer.parseInt(satNumber);
			if(nextSat>sat){
			    sat=nextSat;
			    satList.add(screenData);
			}else if(satList.size()>=4){
			    sat = nextSat;
			    double [][] satMatrix = sat(satList);
			    satList = new ArrayList<String>();
			    satList.add(screenData);
			    double [] Xvnext= vehiclePos(satMatrix, Xv);
			    double time = vehicleTime(satMatrix, Xvnext);
			    int t = (int)(time*100);
			    time = t*.01;satList.add(screenData);
			    int  mag =(int) Vector.normTwo(Xvnext);
			     if(mag>100000){
			    Exercise5CartecianToGPS obj = new Exercise5CartecianToGPS(Xvnext[0],Xvnext[1],Xvnext[2]);
			    System.out.println(time +" "+obj+" "+(mag-r_earth));
			    print.println(time+"  "+obj+" "+(mag-r_earth));}
			}
			//System.out.println(sat);
		    }
		    if(((screenData=br.readLine())==null)&& satList.size()>4)
			   {
				double [][] satMatrix = sat(satList);
				double [] Xvnext= vehiclePos(satMatrix, Xv);
				double time =vehicleTime(satMatrix, Xvnext);
				int t = (int)(time*100);
				time = t*.01;satList.add(screenData);
				int  mag = (int) Vector.normTwo(Xvnext);
				if(mag>100000){
				Exercise5CartecianToGPS obj = new Exercise5CartecianToGPS(Xvnext[0],(Xvnext[1]),Xvnext[2]);
				System.out.println(time +" "+obj+" "+(mag-r_earth));
				print.println(time+"  "+obj+" "+(mag-r_earth));}
			   }
		}catch (Exception e){
		    e.printStackTrace();
		}
		print.close();
	}

	public static double[] rectangleToPolarCoordinates(double[] xyz, double t) {
		double[][] matrix = new double[3][3];

		matrix[0][0] = Math.cos(2*Math.PI*t/86164.09);
		matrix[0][1] = -Math.sin(2*Math.PI*t/86164.09);
		matrix[0][2] = 0;

		matrix[1][0] = Math.sin(2*Math.PI*t/86164.09);
		matrix[1][1] = Math.cos(2*Math.PI*t/86164.09);
		matrix[1][2] = 0;

		matrix[2][0] = 0;
		matrix[2][1] = 0;
		matrix[2][2] = 1;

		double[][] inversedMatrix = Inverse.invert(matrix);

		return Matrix.matrixTimesVector(inversedMatrix, xyz);
	}

	@SuppressWarnings("resource")
	public static double [][] sat(ArrayList<String> list )
	{
	    
	    double [][] satMatrix= new double [list.size()][4];
		for(int row=0; row<satMatrix.length;row++) {
		    Scanner in = new Scanner(list.get(row));
			in.nextInt(); satMatrix[row][3]=in.nextDouble(); satMatrix[row][0]=in.nextDouble();
			satMatrix[row][1]=in.nextDouble();satMatrix[row][2]=in.nextDouble();
			//System.out.println(satMatrix[row][0]+" "+satMatrix[row][1]+" "+satMatrix[row][2]+" "+satMatrix[row][3]);
			in.close();
		}
		return satMatrix;
	}

	public static double[] gradient(double [][] satCoordWithTime, double [] Xv) {
		double [] gradient=new double[3];
		for(int row = 0; row<satCoordWithTime.length-1; row++) {
			double [] temp2 = new double [3]; double []temp1 = new double [3];
			temp2[0]=satCoordWithTime[row+1][0]-Xv[0];temp1[0]=satCoordWithTime[row][0]-Xv[0];
			temp2[1]=satCoordWithTime[row+1][1]-Xv[1];temp1[1]=satCoordWithTime[row][1]-Xv[1];
			temp2[2]=satCoordWithTime[row+1][2]-Xv[2];temp1[2]=satCoordWithTime[row][2]-Xv[2];
			//double timeRow = satCoordWithTime[row][3]; double timeRow2 =satCoordWithTime[row+1][3];
			double magOne=Vector.normTwo(temp1); double magTwo=Vector.normTwo(temp2);
			//System.out.println(magOne+" "+magTwo);
			// mag2-mag1-c(t1-t2)
			double aai= magTwo-magOne-c_speed*(satCoordWithTime[row][3]-satCoordWithTime[row+1][3]);
			//System.out.println(aai+" aai");
			gradient[0]=gradient[0]+(-temp2[0]/magTwo+temp1[0]/magOne)*aai*2;
			gradient[1]=gradient[1]+(-temp2[1]/magTwo+temp1[1]/magOne)*aai*2;
			gradient[2]=gradient[2]+(-temp2[2]/magTwo+temp1[2]/magOne)*aai*2;
			//System.out.println(gradient[0]+" "+gradient[1]+" "+gradient[2]+" grad");
		}
	        
		return gradient;
	}

	public static double [][] jacobian(double [][] sat, double [] Xv){
		/*for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++)
				System.out.print(sat[i][j]+" ");
			System.out.println();}*/
		
		double [][] j = new double[3][3];
		for(int row = 0; row<sat.length-1; row++) {
			//System.out.println((Xv[0])+" "+(Xv[1])+" "+(Xv[2]));
			double [] temp2 = new double [3]; double []temp1 = new double [3];
			temp2[0]=sat[row+1][0]-Xv[0];temp1[0]=sat[row][0]-Xv[0];
			temp2[1]=sat[row+1][1]-Xv[1];temp1[1]=sat[row][1]-Xv[1];
			temp2[2]=sat[row+1][2]-Xv[2];temp1[2]=sat[row][2]-Xv[2];
			
		        
			double magOne=Vector.normTwo(temp1); 
			double magTwo=Vector.normTwo(temp2);
			double ai= magTwo-magOne-c_speed*(sat[row][3]-sat[row+1][3]);
			double xi=-temp2[0]/magTwo+temp1[0]/magOne;
			double yi=-temp2[1]/magTwo+temp1[1]/magOne;
			double zi=-temp2[2]/magTwo+temp1[2]/magOne;
		        
			j[0][0]=j[0][0]+2*xi*xi+2*ai*(1/magTwo-temp2[0]*temp2[0]/(magTwo*magTwo*magTwo)
					-1/magOne+temp1[0]*temp1[0]/(magOne*magOne*magOne));
		        
			j[1][1]=j[1][1]+2*yi*yi+2*ai*((magTwo*magTwo-temp2[1]*temp2[1])/(magTwo*magTwo*magTwo)
					-(magOne*magOne-temp1[1]*temp1[1])/(magOne*magOne*magOne));
			j[2][2]=j[2][2]+2*zi*zi+2*ai*((magTwo*magTwo-temp2[2]*temp2[2])/(magTwo*magTwo*magTwo)
					-(magOne*magOne-temp1[2]*temp1[2])/(magOne*magOne*magOne));
			j[0][1]=j[0][1]+2*xi*yi+2*ai*(-temp2[1]*temp2[0]/(magTwo*magTwo*magTwo)
					+temp1[1]*temp1[0]/(magOne*magOne*magOne));
			j[0][2]=j[0][2]+2*xi*zi+2*ai*(-temp2[2]*temp2[0]/(magTwo*magTwo*magTwo)
					+temp1[2]*temp1[0]/(magOne*magOne*magOne));
			j[1][2]=j[1][2]+2*zi*yi+2*ai*(-temp2[2]*temp2[1]/(magTwo*magTwo*magTwo)
					+temp1[2]*temp1[1]/(magOne*magOne*magOne));
		}
		j[1][0]=j[0][1];
		j[2][0]=j[0][2];
		j[2][1]=j[1][2];
        
		return j;
	}

	private static double iterationCount=0;
	public static double [] vehiclePos(double [][] satCoordWithTime, double [] Xv) {
		iterationCount++;
		double [][] jacInv= Inverse.invert(jacobian(satCoordWithTime, Xv));
		double [] gradient = gradient(satCoordWithTime, Xv);
		double [] XvNext = Vector.subtract(Xv, Matrix.matrixTimesVector(jacInv, gradient));
		if(Math.abs(Vector.normTwo(XvNext)-Vector.normTwo(Xv))<.1)
			return XvNext;
		if(iterationCount==100){
		    iterationCount=0;
		    return XvNext;}
		return vehiclePos(satCoordWithTime, XvNext);
	}

	public static double vehicleTime(double [][] satMatrix, double [] Xv) {
		double [] Xs = new double [3]; double tsat= satMatrix[0][3];
		Xs[0]=satMatrix[0][0];Xs[1]=satMatrix[0][1];Xs[2]=satMatrix[0][2];
		return Vector.normTwo(Vector.subtract(Xs, Xv))/c_speed+tsat;
	}
}
