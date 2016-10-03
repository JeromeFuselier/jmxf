package uk.ac.liv.ffmpeg.libswscale;

public class SwsFilter {
	
	SwsVector lumH;
    SwsVector lumV;
    SwsVector chrH;
    SwsVector chrV;
    
	public SwsVector get_lumH() {
		return lumH;
	}
	
	public void set_lumH(SwsVector lumH) {
		this.lumH = lumH;
	}
	
	public SwsVector get_lumV() {
		return lumV;
	}
	
	public void set_lumV(SwsVector lumV) {
		this.lumV = lumV;
	}
	
	public SwsVector get_chrH() {
		return chrH;
	}
	
	public void set_chrH(SwsVector chrH) {
		this.chrH = chrH;
	}
	
	public SwsVector get_chrV() {
		return chrV;
	}
	
	public void set_chrV(SwsVector chrV) {
		this.chrV = chrV;
	}
    
    
}
