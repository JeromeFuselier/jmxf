package uk.ac.liv.ffmpeg;

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;

public class AVInputFile {
	

    AVFormatContext ctx;
    int eof_reached;      /* true if eof reached */
    int ist_index;        /* index of first stream in ist_table */
    int buffer_size;      /* current total buffer size */
    long ts_offset;
    
    
    
	public AVInputFile(AVFormatContext ctx, int ist_index) {
		super();
		this.ctx = ctx;
		this.ist_index = ist_index;
	}

	public AVFormatContext get_ctx() {
		return ctx;
	}
	
	public void set_ctx(AVFormatContext ctx) {
		this.ctx = ctx;
	}
	
	public int get_eof_reached() {
		return eof_reached;
	}
	
	public void set_eof_reached(int eof_reached) {
		this.eof_reached = eof_reached;
	}
	
	public int get_ist_index() {
		return ist_index;
	}
	
	public void set_ist_index(int ist_index) {
		this.ist_index = ist_index;
	}
	
	public int get_buffer_size() {
		return buffer_size;
	}
	
	public void set_buffer_size(int buffer_size) {
		this.buffer_size = buffer_size;
	}

	public long get_ts_offset() {
		return ts_offset;
	}

	public void set_ts_offset(long ts_offset) {
		this.ts_offset = ts_offset;
	}

    
    
    
}
