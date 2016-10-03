package uk.ac.liv.ffmpeg.libavcodec;

import java.io.File;

import uk.ac.liv.ffmpeg.libavutil.AVExpr;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;

public class RateControlContext {
    File stats_file;
    int num_entries;              ///< number of RateControlEntries
    RateControlEntry [] entry;
    double buffer_index;          ///< amount of bits in the video/audio buffer
    Predictor [] pred = new Predictor[5];
    double short_term_qsum;       ///< sum of recent qscales
    double short_term_qcount;     ///< count of recent qscales
    double pass1_rc_eq_output_sum;///< sum of the output of the rc equation, this is used for normalization
    double pass1_wanted_bits;     ///< bits which should have been outputed by the pass1 code (including complexity init)
    double last_qscale;
    double [] last_qscale_for = new double[5];    ///< last qscale for a specific pict type, used for max_diff & ipb factor stuff
    int last_mc_mb_var_sum;
    int last_mb_var_sum;
    long [] i_cplx_sum = new long[5];
    long [] p_cplx_sum = new long[5];
    long [] mv_bits_sum = new long[5];
    long [] qscale_sum = new long[5];
    int [] frame_count = new int[5];
    AVPictureType last_non_b_pict_type = AVPictureType.AV_PICTURE_TYPE_NONE;

    Object non_lavc_opaque;        ///< context for non lavc rc code (for example xvid)
    float dry_run_qscale;         ///< for xvid rc
    int last_picture_number;      ///< for xvid rc
    AVExpr rc_eq_eval;
    
	public double get_buffer_index() {
		return buffer_index;
	}
	
	public void set_buffer_index(double buffer_index) {
		this.buffer_index = buffer_index;
	}

	public File get_stats_file() {
		return stats_file;
	}

	public void set_stats_file(File stats_file) {
		this.stats_file = stats_file;
	}

	public int get_num_entries() {
		return num_entries;
	}

	public void set_num_entries(int num_entries) {
		this.num_entries = num_entries;
	}

	public RateControlEntry [] get_entry() {
		return entry;
	}
	
	public RateControlEntry get_entry(int i) {
		return entry[i];
	}

	public void set_entry(RateControlEntry [] entry) {
		this.entry = entry;
	}

	public Predictor[] get_pred() {
		return pred;
	}

	public Predictor get_pred(int i) {
		return pred[i];
	}

	public void set_pred(Predictor[] pred) {
		this.pred = pred;
	}

	public void set_pred(int i, Predictor pred) {
		this.pred[i] = pred;
	}

	public double get_short_term_qsum() {
		return short_term_qsum;
	}

	public void set_short_term_qsum(double short_term_qsum) {
		this.short_term_qsum = short_term_qsum;
	}

	public double get_short_term_qcount() {
		return short_term_qcount;
	}

	public void set_short_term_qcount(double short_term_qcount) {
		this.short_term_qcount = short_term_qcount;
	}

	public double get_pass1_rc_eq_output_sum() {
		return pass1_rc_eq_output_sum;
	}

	public void set_pass1_rc_eq_output_sum(double pass1_rc_eq_output_sum) {
		this.pass1_rc_eq_output_sum = pass1_rc_eq_output_sum;
	}

	public double get_pass1_wanted_bits() {
		return pass1_wanted_bits;
	}

	public void set_pass1_wanted_bits(double pass1_wanted_bits) {
		this.pass1_wanted_bits = pass1_wanted_bits;
	}

	public double get_last_qscale() {
		return last_qscale;
	}

	public void set_last_qscale(double last_qscale) {
		this.last_qscale = last_qscale;
	}

	public double[] get_last_qscale_for() {
		return last_qscale_for;
	}

	public void set_last_qscale_for(double[] last_qscale_for) {
		this.last_qscale_for = last_qscale_for;
	}

	public void set_last_qscale_for(int i, double last_qscale_for) {
		this.last_qscale_for[i] = last_qscale_for;
	}

	public int get_last_mc_mb_var_sum() {
		return last_mc_mb_var_sum;
	}

	public void set_last_mc_mb_var_sum(int last_mc_mb_var_sum) {
		this.last_mc_mb_var_sum = last_mc_mb_var_sum;
	}

	public int get_last_mb_var_sum() {
		return last_mb_var_sum;
	}

	public void set_last_mb_var_sum(int last_mb_var_sum) {
		this.last_mb_var_sum = last_mb_var_sum;
	}

	public long[] get_i_cplx_sum() {
		return i_cplx_sum;
	}

	public void set_i_cplx_sum(long[] i_cplx_sum) {
		this.i_cplx_sum = i_cplx_sum;
	}

	public void set_i_cplx_sum(int i, long i_cplx_sum) {
		this.i_cplx_sum[i] = i_cplx_sum;
	}

	public long[] get_p_cplx_sum() {
		return p_cplx_sum;
	}

	public void set_p_cplx_sum(long[] p_cplx_sum) {
		this.p_cplx_sum = p_cplx_sum;
	}

	public void set_p_cplx_sum(int i, long p_cplx_sum) {
		this.p_cplx_sum[i] = p_cplx_sum;
	}

	public long[] get_mv_bits_sum() {
		return mv_bits_sum;
	}

	public void set_mv_bits_sum(long[] mv_bits_sum) {
		this.mv_bits_sum = mv_bits_sum;
	}

	public void set_mv_bits_sum(int i, long mv_bits_sum) {
		this.mv_bits_sum[i] = mv_bits_sum;
	}

	public long[] get_qscale_sum() {
		return qscale_sum;
	}

	public void set_qscale_sum(long[] qscale_sum) {
		this.qscale_sum = qscale_sum;
	}

	public void set_qscale_sum(int i, long qscale_sum) {
		this.qscale_sum[i] = qscale_sum;
	}

	public int[] get_frame_count() {
		return frame_count;
	}

	public void set_frame_count(int i, int frame_count) {
		this.frame_count[i] = frame_count;
	}

	public AVPictureType get_last_non_b_pict_type() {
		return last_non_b_pict_type;
	}

	public void set_last_non_b_pict_type(AVPictureType last_non_b_pict_type) {
		this.last_non_b_pict_type = last_non_b_pict_type;
	}

	public Object get_non_lavc_opaque() {
		return non_lavc_opaque;
	}

	public void set_non_lavc_opaque(Object non_lavc_opaque) {
		this.non_lavc_opaque = non_lavc_opaque;
	}

	public float get_dry_run_qscale() {
		return dry_run_qscale;
	}

	public void set_dry_run_qscale(float dry_run_qscale) {
		this.dry_run_qscale = dry_run_qscale;
	}

	public int get_last_picture_number() {
		return last_picture_number;
	}

	public void set_last_picture_number(int last_picture_number) {
		this.last_picture_number = last_picture_number;
	}

	public AVExpr get_rc_eq_eval() {
		return rc_eq_eval;
	}

	public void set_rc_eq_eval(AVExpr rc_eq_eval) {
		this.rc_eq_eval = rc_eq_eval;
	}

	
	

    
    
    
    
    
}
