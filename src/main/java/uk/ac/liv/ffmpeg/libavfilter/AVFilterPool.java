package uk.ac.liv.ffmpeg.libavfilter;

public class AVFilterPool {

	public static int POOL_SIZE = 32;
	
    AVFilterBufferRef [] pic = new AVFilterBufferRef[POOL_SIZE];
    int count;
    
	public AVFilterBufferRef[] get_pic() {
		return pic;
	}
	
	public void set_pic(AVFilterBufferRef[] pic) {
		this.pic = pic;
	}
	
	public int get_count() {
		return count;
	}
	
	public void set_count(int count) {
		this.count = count;
	}
    
    

}
