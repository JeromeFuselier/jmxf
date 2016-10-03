package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavutil.AVClass;

public class AVResampleContext {
	
    AVClass av_class;
    int filter_bank;
    int filter_length;
    int ideal_dst_incr;
    int dst_incr;
    int index;
    int frac;
    int src_incr;
    int compensation_distance;
    int phase_shift;
    int phase_mask;
    int linear;
    
	public AVClass get_av_class() {
		return av_class;
	}
	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}
	public int get_filter_bank() {
		return filter_bank;
	}
	public void set_filter_bank(int filter_bank) {
		this.filter_bank = filter_bank;
	}
	public int get_filter_length() {
		return filter_length;
	}
	public void set_filter_length(int filter_length) {
		this.filter_length = filter_length;
	}
	public int get_ideal_dst_incr() {
		return ideal_dst_incr;
	}
	public void set_ideal_dst_incr(int ideal_dst_incr) {
		this.ideal_dst_incr = ideal_dst_incr;
	}
	public int get_dst_incr() {
		return dst_incr;
	}
	public void set_dst_incr(int dst_incr) {
		this.dst_incr = dst_incr;
	}
	public int get_index() {
		return index;
	}
	public void set_index(int index) {
		this.index = index;
	}
	public int get_frac() {
		return frac;
	}
	public void set_frac(int frac) {
		this.frac = frac;
	}
	public int get_src_incr() {
		return src_incr;
	}
	public void set_src_incr(int src_incr) {
		this.src_incr = src_incr;
	}
	public int get_compensation_distance() {
		return compensation_distance;
	}
	public void set_compensation_distance(int compensation_distance) {
		this.compensation_distance = compensation_distance;
	}
	public int get_phase_shift() {
		return phase_shift;
	}
	public void set_phase_shift(int phase_shift) {
		this.phase_shift = phase_shift;
	}
	public int get_phase_mask() {
		return phase_mask;
	}
	public void set_phase_mask(int phase_mask) {
		this.phase_mask = phase_mask;
	}
	public int get_linear() {
		return linear;
	}
	public void set_linear(int linear) {
		this.linear = linear;
	}
	public void av_resample_close() {
		
	}

	public void av_resample_compensate(int sample_delta, int compensation_distance) {
//		    sample_delta += (c->ideal_dst_incr - c->dst_incr)*(int64_t)c->compensation_distance / c->ideal_dst_incr;
	    set_compensation_distance(compensation_distance);
	    set_dst_incr((int)(get_ideal_dst_incr() - get_ideal_dst_incr() * (long)sample_delta / compensation_distance));		
	}
	
    
	

}
