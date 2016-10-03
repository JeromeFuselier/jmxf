package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class BufferSourceContext {

    AVFilterBufferRef picref;
    int               h, w;
    PixelFormat  	  pix_fmt = PixelFormat.PIX_FMT_YUV420P;
    AVRational        time_base = new AVRational();     ///< time_base to set in the output link
    AVRational        sample_aspect_ratio = new AVRational();
    String sws_param;
    
	public AVFilterBufferRef get_picref() {
		return picref;
	}
	
	public void set_picref(AVFilterBufferRef picref) {
		this.picref = picref;
	}
	
	public int get_h() {
		return h;
	}
	
	public void set_h(int h) {
		this.h = h;
	}
	
	public int get_w() {
		return w;
	}
	
	public void set_w(int w) {
		this.w = w;
	}
	
	public PixelFormat get_pix_fmt() {
		return pix_fmt;
	}
	
	public void set_pix_fmt(PixelFormat pix_fmt) {
		this.pix_fmt = pix_fmt;
	}
	
	public AVRational get_time_base() {
		return time_base;
	}
	
	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
	}
	
	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}
	
	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}
	
	public String get_sws_param() {
		return sws_param;
	}
	
	public void set_sws_param(String sws_param) {
		this.sws_param = sws_param;
	}
    
    
}
