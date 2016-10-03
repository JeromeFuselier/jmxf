package uk.ac.liv.ffmpeg.libswscale;

import java.util.Arrays;

public class SwsVector {
	
	double [] coeff;              ///< pointer to the list of coefficients
	int length;                 ///< number of coefficients in the vector
	
    public double[] get_coeff() {
		return coeff;
	}
    
	public void set_coeff(double[] coeff) {
		this.coeff = Arrays.copyOf(coeff, coeff.length);
	}
	
	public int get_length() {
		return length;
	}
	
	public void set_length(int length) {
		this.length = length;
	}

	public double get_coeff(int k) {
		return coeff[k];
	}
    
    
}
