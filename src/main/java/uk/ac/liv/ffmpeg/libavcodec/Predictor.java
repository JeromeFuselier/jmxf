package uk.ac.liv.ffmpeg.libavcodec;

public class Predictor {

    double coeff;
    double count;
    double decay;
    
	public double get_coeff() {
		return coeff;
	}
	
	public void set_coeff(double coeff) {
		this.coeff = coeff;
	}
	
	public double get_count() {
		return count;
	}
	
	public void set_count(double count) {
		this.count = count;
	}
	
	public double get_decay() {
		return decay;
	}
	
	public void set_decay(double decay) {
		this.decay = decay;
	}
    
    
    
}
