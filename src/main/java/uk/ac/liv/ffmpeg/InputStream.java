package uk.ac.liv.ffmpeg;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;

public class InputStream {
	int file_index;
    AVStream st;
    int discard;             /* true if stream data should be discarded */
    int decoding_needed;     /* true if the packet_s must be decoded in 'raw_fifo' */
    AVCodec dec;

    long start;     /* t_ime when read started */
	long next_pts;  /* synthet_ic pt_s for cases where pkt.pt_s
	                                is not defined */
	long pts;       /* current pt_s */
    double ts_scale;
    int is_start;            /* is 1 at the start and after a discont_inuity */
    int showed_multi_packet_warning;
    int is_past_recording_time;
    
	public int get_file_index() {
		return file_index;
	}
	
	public void set_file_index(int file_index) {
		this.file_index = file_index;
	}
	
	public AVStream get_st() {
		return st;
	}
	public void set_st(AVStream st) {
		this.st = st;
	}
	public int get_discard() {
		return discard;
	}
	public void set_discard(int discard) {
		this.discard = discard;
	}
	public int get_decoding_needed() {
		return decoding_needed;
	}
	public void set_decoding_needed(int decoding_needed) {
		this.decoding_needed = decoding_needed;
	}
	public AVCodec get_dec() {
		return dec;
	}
	public void set_dec(AVCodec dec) {
		this.dec = dec;
	}
	public long get_start() {
		return start;
	}
	public void set_start(long start) {
		this.start = start;
	}
	public long get_next_pts() {
		return next_pts;
	}
	public void set_next_pts(long next_pts) {
		this.next_pts = next_pts;
	}
	public long get_pts() {
		return pts;
	}
	public void set_pts(long pts) {
		this.pts = pts;
	}
	public double get_ts_scale() {
		return ts_scale;
	}
	public void set_ts_scale(double ts_scale) {
		this.ts_scale = ts_scale;
	}
	public int get_is_start() {
		return is_start;
	}
	public void set_is_start(int is_start) {
		this.is_start = is_start;
	}
	public int get_showed_multi_packet_warning() {
		return showed_multi_packet_warning;
	}
	public void set_showed_multi_packet_warning(int showed_multi_packet_warning) {
		this.showed_multi_packet_warning = showed_multi_packet_warning;
	}
	public int get_is_past_recording_time() {
		return is_past_recording_time;
	}
	public void set_is_past_recording_time(int is_past_recording_time) {
		this.is_past_recording_time = is_past_recording_time;
	}
    
    
    
  

} 
