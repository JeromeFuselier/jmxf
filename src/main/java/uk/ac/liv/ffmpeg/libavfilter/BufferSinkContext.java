package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class BufferSinkContext {
	
    AVFilterBufferRef picref;   ///< cached picref
    ArrayList<PixelFormat> pix_fmts = new ArrayList<PixelFormat>();  ///< accepted pixel formats, must be terminated with -1
    
	public AVFilterBufferRef get_picref() {
		return picref;
	}
	
	public void set_picref(AVFilterBufferRef picref) {
		this.picref = picref;
	}
	
	public  ArrayList<PixelFormat> get_pix_fmts() {
		return pix_fmts;
	}
	
	public  PixelFormat get_pix_fmt(int idx) {
		return pix_fmts.get(idx);
	}
	
	public void set_pix_fmts( ArrayList<PixelFormat> pix_fmts) {
		this.pix_fmts = pix_fmts;
	}
    
    
    

}
