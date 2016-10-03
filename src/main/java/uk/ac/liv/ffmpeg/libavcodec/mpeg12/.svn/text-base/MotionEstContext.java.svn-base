package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;

public class MotionEstContext {
	
	AVCodecContext avctx;
    int skip;                          ///< set if ME is skipped for the current MB
    int [][] co_located_mv = new int[4][2];           ///< mv from last P-frame for direct mode ME
    int [][] direct_basis_mv = new int[4][2];
    byte scratchpad;               ///< data area for the ME algo, so that the ME does not need to malloc/free
    byte best_mb;
    byte [] temp_mb = new byte[2];
    byte temp;
    int best_bits;
    long map;                     ///< map to avoid duplicate evaluations
    long score_map;               ///< map to store the scores
    int map_generation;
    int pre_penalty_factor;
    int penalty_factor;                /*!< an estimate of the bits required to
                                        code a given mv value, e.g. (1,0) takes
                                        more bits than (0,0). We have to
                                        estimate whether any reduction in
                                        residual is worth the extra bits. */
    int sub_penalty_factor;
    int mb_penalty_factor;
    int flags;
    int sub_flags;
    int mb_flags;
    int pre_pass;                      ///< = 1 for the pre pass
    int dia_size;
    int xmin;
    int xmax;
    int ymin;
    int ymax;
    int pred_x;
    int pred_y;
    byte [][] src = new byte[4][4];
    byte [][] ref = new byte[4][4];
    int stride;
    int uvstride;
    /* temp variables for picture complexity calculation */
    int mc_mb_var_sum_temp;
    int mb_var_sum_temp;
    int scene_change_score;
/*    cmp, chroma_cmp;*/
//    op_pixels_func (*hpel_put)[4];
//    op_pixels_func (*hpel_avg)[4];
//    qpel_mc_func (*qpel_put)[16];
//    qpel_mc_func (*qpel_avg)[16];
    byte [] mv_penalty = new byte[MpegVideo.MAX_MV*2+1];  ///< amount of bits needed to encode a MV
    byte current_mv_penalty;
        
    
    public AVCodecContext get_avctx() {
		return avctx;
	}

	public void set_avctx(AVCodecContext avctx) {
		this.avctx = avctx;
	}

	public int get_skip() {
		return skip;
	}

	public void set_skip(int skip) {
		this.skip = skip;
	}

	public int[][] get_co_located_mv() {
		return co_located_mv;
	}

	public void set_co_located_mv(int[][] co_located_mv) {
		this.co_located_mv = co_located_mv;
	}
	
	public int[][] get_direct_basis_mv() {
		return direct_basis_mv;
	}
	
	public void set_direct_basis_mv(int[][] direct_basis_mv) {
		this.direct_basis_mv = direct_basis_mv;
	}

	public byte get_scratchpad() {
		return scratchpad;
	}

	public void set_scratchpad(byte scratchpad) {
		this.scratchpad = scratchpad;
	}

	public byte get_best_mb() {
		return best_mb;
	}

	public void set_best_mb(byte best_mb) {
		this.best_mb = best_mb;
	}

	public byte[] get_temp_mb() {
		return temp_mb;
	}

	public void set_temp_mb(byte[] temp_mb) {
		this.temp_mb = temp_mb;
	}

	public byte get_temp() {
		return temp;
	}

	public void set_temp(byte temp) {
		this.temp = temp;
	}

	public int get_best_bits() {
		return best_bits;
	}

	public void set_best_bits(int best_bits) {
		this.best_bits = best_bits;
	}

	public long get_map() {
		return map;
	}
	
	public void set_map(long map) {
		this.map = map;
	}

	public long get_score_map() {
		return score_map;
	}
	
	public void set_score_map(long score_map) {
		this.score_map = score_map;
	}

	public int get_map_generation() {
		return map_generation;
	}

	public void set_map_generation(int map_generation) {
		this.map_generation = map_generation;
	}

	public int get_pre_penalty_factor() {
		return pre_penalty_factor;
	}

	public void set_pre_penalty_factor(int pre_penalty_factor) {
		this.pre_penalty_factor = pre_penalty_factor;
	}

	public int get_penalty_factor() {
		return penalty_factor;
	}

	public void set_penalty_factor(int penalty_factor) {
		this.penalty_factor = penalty_factor;
	}

	public int get_sub_penalty_factor() {
		return sub_penalty_factor;
	}

	public void set_sub_penalty_factor(int sub_penalty_factor) {
		this.sub_penalty_factor = sub_penalty_factor;
	}

	public int get_mb_penalty_factor() {
		return mb_penalty_factor;
	}

	public void set_mb_penalty_factor(int mb_penalty_factor) {
		this.mb_penalty_factor = mb_penalty_factor;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public int get_sub_flags() {
		return sub_flags;
	}

	public void set_sub_flags(int sub_flags) {
		this.sub_flags = sub_flags;
	}

	public int get_mb_flags() {
		return mb_flags;
	}

	public void set_mb_flags(int mb_flags) {
		this.mb_flags = mb_flags;
	}

	public int get_pre_pass() {
		return pre_pass;
	}

	public void set_pre_pass(int pre_pass) {
		this.pre_pass = pre_pass;
	}

	public int get_dia_size() {
		return dia_size;
	}

	public void set_dia_size(int dia_size) {
		this.dia_size = dia_size;
	}

	public int get_xmin() {
		return xmin;
	}

	public void set_xmin(int xmin) {
		this.xmin = xmin;
	}

	public int get_xmax() {
		return xmax;
	}

	public void set_xmax(int xmax) {
		this.xmax = xmax;
	}

	public int get_ymin() {
		return ymin;
	}

	public void set_ymin(int ymin) {
		this.ymin = ymin;
	}
	
	public int get_ymax() {
		return ymax;
	}

	public void set_ymax(int ymax) {
		this.ymax = ymax;
	}

	public int get_pred_x() {
		return pred_x;
	}

	public void set_pred_x(int pred_x) {
		this.pred_x = pred_x;
	}

	public int get_pred_y() {
		return pred_y;
	}

	public void set_pred_y(int pred_y) {
		this.pred_y = pred_y;
	}

	public byte[][] get_src() {
		return src;
	}

	public void set_src(byte[][] src) {
		this.src = src;
	}

	public byte[][] get_ref() {
		return ref;
	}

	public void set_ref(byte[][] ref) {
		this.ref = ref;
	}

	public int get_stride() {
		return stride;
	}

	public void set_stride(int stride) {
		this.stride = stride;
	}

	public int get_uvstride() {
		return uvstride;
	}

	public void set_uvstride(int uvstride) {
		this.uvstride = uvstride;
	}

	public int get_mc_mb_var_sum_temp() {
		return mc_mb_var_sum_temp;
	}

	public void set_mc_mb_var_sum_temp(int mc_mb_var_sum_temp) {
		this.mc_mb_var_sum_temp = mc_mb_var_sum_temp;
	}

	public int get_mb_var_sum_temp() {
		return mb_var_sum_temp;
	}

	public void set_mb_var_sum_temp(int mb_var_sum_temp) {
		this.mb_var_sum_temp = mb_var_sum_temp;
	}

	public int get_scene_change_score() {
		return scene_change_score;
	}

	public void set_scene_change_score(int scene_change_score) {
		this.scene_change_score = scene_change_score;
	}

	public byte[] get_mv_penalty() {
		return mv_penalty;
	}

	public void set_mv_penalty(byte[] mv_penalty) {
		this.mv_penalty = mv_penalty;
	}

	public byte get_current_mv_penalty() {
		return current_mv_penalty;
	}

	public void set_current_mv_penalty(byte current_mv_penalty) {
		this.current_mv_penalty = current_mv_penalty;
	}

	int sub_motion_search(MpegEncContext s, int mx_ptr, int my_ptr, int dmin,
                                  int src_index, int ref_index,
                                  int size, int h) {
    	return -1;
    }

}
