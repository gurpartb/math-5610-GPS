public class Matrix {
	private static void SubtractOneRowFromAnother(double [][] A, int row, int subtractFrom) {
		for(int j=0; j<A[row].length;j++)
			A[subtractFrom][j]=A[subtractFrom][j]-A[row][j];
	}

	/*public static void scaleRow(double [][] A, double scalar, int row) {
		for(int j=0; j<A[row].length;j++)
			A[row][j]=A[row][j]*scalar;
	}*/

	public static double [][] transpose(double [][] A) {
		double [][] transpose = new double[A[0].length][A.length];
		for(int j = 0; j<A[0].length; j++)
			for(int i = 0; i<A.length;i++)
				transpose[j][i]=A[i][j];
		return transpose;
	}

	public static double [] matrixTimesVector(double [][] A, double []x) {
		double[] b = new double[A.length];
		double [] colA= new double [A[0].length];
		for(int i=0; i<A.length; i++) {
			for(int j = 0; j<A[0].length; j++)
				colA[j]=A[i][j];
			b[i]=Vector.multiply(colA, x);
		}
		return b;
	}

	public static double[][] combiningTwoRowVectors(double[] u, double [] v){
		double [][] A = new double [2][u.length];
		for(int j=0; j<A.length; j++) {
			A[0][j]=u[j];
			A[1][j]=u[j];
		}
		return A;
	}

	public static double[][] combiningMatrixAndColumnVector(double[][] A, double []v){
		double [][] B = new double [A.length][A[0].length+1];
		copyToBiggerMatrix(B, A);
		for(int i=0; i<B.length; i++)
			B[i][A[0].length]=v[i];
		return B;
	}
	
	public static void combiningMatrixAndColumnVector(double[][] A, double []v, int indTo){
		for(int i=0; i<A.length; i++)
			A[i][indTo]=v[i];
	}
	
	public static void copyRow(double [][] A, int rowA, double [][] B, int rowB) {
		for(int j=0; j<B[0].length;j++ )
			A[rowA][j]=B[rowB][j];
	}

	public static void copyToBiggerMatrix(double [][] Abig, double [][] Asmall, int indFrom) {
		for(int j = indFrom; j<indFrom+Asmall[0].length; j++)
			for(int i=0; i<Asmall.length; i++)
				Abig[i][j]=Asmall[i][j];
	}

	public static void copyToBiggerMatrix(double [][] Abig, double [][] Asmall) {
		copyToBiggerMatrix(Abig, Asmall, 0);
	}

	public static double [][] multiplication(double [][] A, double [][] B){
		double [][] sol = new double[A.length][B[0].length];
		double [] colB = new double[B.length];
		for(int j=0; j<B[0].length; j++) {
			for(int i =0; i<B.length; i++ ) {
				colB[i]=B[i][j];
			}
			double[] solV= matrixTimesVector(A, colB);
			combiningMatrixAndColumnVector(sol, solV, j);
		}
		return sol;
	}
}
