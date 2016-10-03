/******************************************************************************
 *  
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author     : Jerome Fuselier
 * Creation   : June 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.Motion_Est_ID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.DSPContext;
import uk.ac.liv.ffmpeg.libavcodec.DspUtil;
import uk.ac.liv.ffmpeg.libavcodec.GetBitContext;
import uk.ac.liv.ffmpeg.libavcodec.ImgConvert;
import uk.ac.liv.ffmpeg.libavcodec.Predictor;
import uk.ac.liv.ffmpeg.libavcodec.PutBitContext;
import uk.ac.liv.ffmpeg.libavcodec.RateControlContext;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.h261.H261;
import uk.ac.liv.ffmpeg.libavcodec.h261.H261Data;
import uk.ac.liv.ffmpeg.libavcodec.mjpeg.Mjpeg;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegVideo.OutputFormat;
import uk.ac.liv.ffmpeg.libavcodec.ParseContext;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterBufferRef;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.AVExpr;
import uk.ac.liv.ffmpeg.libavutil.Eval;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOI;

public class MpegEncContext implements Cloneable {
		
	static int done; 
	
    AVCodecContext avctx;
	
	 /* the following parameters must be initialized before encoding */
    int width, height;///< picture size. must be a multiple of 16
    int gop_size;
    int intra_only;   ///< if true, only intra pictures are generated
    int bit_rate;     ///< wanted bit rate
    OutputFormat out_format; ///< output format
    int h263_pred;    ///< use mpeg4/h263 ac/dc predictions
    int pb_frame;     ///< PB frame mode (0 = none, 1 = base, 2 = improved)

    /* the following codec id fields are deprecated in favor of codec_id */
    int h263_plus;    ///< h263 plus headers
    int h263_msmpeg4; ///< generate MSMPEG4 compatible stream (deprecated, use msmpeg4_version instead)
    int h263_flv;     ///< use flv h263 header

    CodecID codec_id; 
    int fixed_qscale; ///< fixed qscale if non zero
    int encoding;     ///< true if we are encoding (vs decoding)
    int flags;        ///< AVCodecContext.flags (HQ, MV4, ...)
    int flags2;       ///< AVCodecContext.flags2
    int max_b_frames; ///< max number of b-frames for encoding
    int luma_elim_threshold;
    int chroma_elim_threshold;
    int strict_std_compliance; ///< strictly follow the std (MPEG4, ...)
    int workaround_bugs;       ///< workaround bugs in encoders which cannot be detected automatically
    int codec_tag;             ///< internal codec_tag upper case converted from avctx codec_tag
    int stream_codec_tag;      ///< internal stream_codec_tag upper case converted from avctx stream_codec_tag
    
    /* the following fields are managed internally by the encoder */
    
    /* sequence parameters */
    int context_initialized;
    int input_picture_number;  ///< used to set pic->display_picture_number, should not be used for/by anything else
    int coded_picture_number;  ///< used to set pic->coded_picture_number, should not be used for/by anything else
    int picture_number;       //FIXME remove, unclear definition
    int picture_in_gop_number; ///< 0-> first pic in gop, ...
    int b_frames_since_non_b;  ///< used for encoding, relative to not yet reordered input
    long user_specified_pts;///< last non zero pts from AVFrame which was passed into avcodec_encode_video()
    int mb_width, mb_height;   ///< number of MBs horizontally & vertically
    int mb_stride;             ///< mb_width+1 used for some arrays to allow simple addressing of left & top MBs without sig11
    int b8_stride;             ///< 2*mb_width+1 used for some 8x8 block arrays to allow simple addressing
    int b4_stride;             ///< 4*mb_width+1 used for some 4x4 block arrays to allow simple addressing
    int h_edge_pos, v_edge_pos;///< horizontal / vertical position of the right/bottom edge (pixel replication)
    int mb_num;                ///< number of MBs of a picture
    int linesize;              ///< line size, in bytes, may be different from width
    int uvlinesize;            ///< line size, for chroma in bytes, may be different from width
    Picture [] picture;          ///< main picture buffer
    Picture [] input_picture;   ///< next pictures on display order for encoding
    Picture [] reordered_input_picture; ///< pointer to the next pictures in codedorder for encoding

    int y_dc_scale, c_dc_scale;
    int ac_pred;
    int [] block_last_index = new int[12];  ///< last non zero coefficient in block
    int h263_aic;              ///< Advanded INTRA Coding (AIC)

    /* scantables */
    ScanTable inter_scantable = new ScanTable(); ///< if inter == intra then intra should be used to reduce tha cache usage
    ScanTable intra_scantable = new ScanTable();
    ScanTable intra_h_scantable = new ScanTable();
    ScanTable intra_v_scantable = new ScanTable();
    
    /** bit output */
    PutBitContext pb = new PutBitContext();
    
    int start_mb_y;            ///< start mb_y of this thread (so current thread should process start_mb_y <= row < end_mb_y)
    int end_mb_y;              ///< end   mb_y of this thread (so current thread should process start_mb_y <= row < end_mb_y)
    ArrayList<MpegEncContext> thread_context;
    
    /**
     * copy of the previous picture structure.
     * note, linesize & data, might not match the previous picture (for field pictures)
     */
    Picture last_picture;
    Picture last_picture_ptr;

    /**
     * copy of the next picture structure.
     * note, linesize & data, might not match the next picture (for field pictures)
     */
    Picture next_picture;
    

    /**
     * copy of the source picture structure for encoding.
     * note, linesize & data, might not match the source picture (for field pictures)
     */
    Picture new_picture;

    /**
     * copy of the current picture structure.
     * note, linesize & data, might not match the current picture (for field pictures)
     */
    Picture current_picture;    ///< buffer to store the decompressed current picture
    
    int picture_count;             ///< number of allocated pictures (MAX_PICTURE_COUNT * avctx->thread_count)
    int picture_range_start, picture_range_end; ///< the part of picture that this context can allocate in
    byte [] visualization_buffer = new byte[3]; //< temporary buffer vor MV visualization
    int [] last_dc = new int[3];                ///< last DC values for MPEG1
    int [] dc_val_base;
    int [] dc_val = new int[3];            ///< used for mpeg4 DC prediction, all 3 arrays must be continuous
    int [] dc_cache = new int[4*5];
    byte [] y_dc_scale_table;     ///< qscale -> y_dc_scale table
    byte [] c_dc_scale_table;     ///< qscale -> c_dc_scale table
    byte [] chroma_qscale_table;  ///< qscale -> chroma_qscale (h263)
    byte [] coded_block_base;
    byte [] coded_block;          ///< used for coded block pattern prediction (msmpeg4v3, wmv1)
    int [] ac_val_base = new int[16];
    int [][] ac_val = new int[3][16];      ///< used for for mpeg4 AC prediction, all 3 arrays must be continuous
    AVPictureType [] prev_pict_types;     ///< previous picture types in bitstream order, used for mb skip
    int mb_skipped;                ///< MUST BE SET only during DECODING
    byte [] mbskip_table;        /**< used to avoid copy if macroblock skipped (for black regions for example)
                                   and used for b-frame encoding & decoding (contains skip table of next P Frame) */
    byte [] mbintra_table;       ///< used to avoid setting {ac, dc, cbp}-pred stuff to zero on inter MB decoding
    byte [] cbp_table;           ///< used to store cbp, ac_pred for partitioned decoding
    byte [] pred_dir_table;      ///< used to store pred_dir for partitioned decoding
    byte [] allocated_edge_emu_buffer;
    byte [] edge_emu_buffer;     ///< points into the middle of allocated_edge_emu_buffer
    byte [] rd_scratchpad;       ///< scratchpad for rate distortion mb decision
    byte [] obmc_scratchpad;
    byte [] b_scratchpad;        ///< scratchpad used for writing into write only buffers

    int qscale;                 ///< QP
    int chroma_qscale;          ///< chroma QP
    int lambda;        ///< lagrange multipler used in rate distortion
    int lambda2;       ///< (lambda*lambda) >> FF_LAMBDA_SHIFT
    int [] lambda_table;
    int adaptive_quant;         ///< use adaptive quantization
    int dquant;                 ///< qscale difference to prev qscale
    int closed_gop;             ///< MPEG1/2 GOP is closed
    AVPictureType pict_type = AVPictureType.AV_PICTURE_TYPE_NONE;              ///< AV_PICTURE_TYPE_I, AV_PICTURE_TYPE_P, AV_PICTURE_TYPE_B, ...
    AVPictureType last_pict_type = AVPictureType.AV_PICTURE_TYPE_NONE; 
    AVPictureType last_non_b_pict_type = AVPictureType.AV_PICTURE_TYPE_NONE;   ///< used for mpeg4 gmc b-frames & ratecontrol
    int dropable;
    int frame_rate_index;
    int [] last_lambda_for = new int[AVPictureType.values().length];     ///< last lambda for a specific pict type
    int skipdct;                ///< skip dct and code zero residual

    /* motion compensation */
    int unrestricted_mv;        ///< mv can point outside of the coded picture
    int h263_long_vectors;      ///< use horrible h263v1 long vector mode
    int decode;                 ///< if 0 then decoding will be skipped (for encoding b frames for example)
    
	
    DSPContext dsp= new DSPContext();             ///< pointers for accelerated dsp functions
    int f_code;                 ///< forward MV resolution
    int b_code;                 ///< backward MV resolution for B Frames (mpeg4)
    int [] p_mv_table_base = new int[2];
    int [] b_forw_mv_table_base = new int[2];
    int [] b_back_mv_table_base = new int[2];
    int [] b_bidir_forw_mv_table_base = new int[2];
    int [] b_bidir_back_mv_table_base = new int[2];
    int [] b_direct_mv_table_base = new int[2];
    int [][][] p_field_mv_table_base = new int[2][2][2];
    int [][][][] b_field_mv_table_base = new int[2][2][2][2];
    int [] p_mv_table = new int[2];            ///< MV table (1MV per MB) p-frame encoding
    int [] b_forw_mv_table = new int[2];       ///< MV table (1MV per MB) forward mode b-frame encoding
    int [] b_back_mv_table = new int[2];       ///< MV table (1MV per MB) backward mode b-frame encoding
    int [] b_bidir_forw_mv_table = new int[2]; ///< MV table (1MV per MB) bidir mode b-frame encoding
    int [] b_bidir_back_mv_table = new int[2]; ///< MV table (1MV per MB) bidir mode b-frame encoding
    int [] b_direct_mv_table = new int[2];     ///< MV table (1MV per MB) direct mode b-frame encoding
    int [][][] p_field_mv_table = new int[2][2][2];   ///< MV table (2MV per MB) interlaced p-frame encoding
    int [][][][] b_field_mv_table = new int[2][2][2][2];///< MV table (4MV per MB) interlaced b-frame encoding
    byte [][] p_field_select_table = new byte[2][2];
    byte [][][] b_field_select_table = new byte[2][2][2];
    Motion_Est_ID me_method;                       ///< ME algorithm
    int mv_dir;
    int mv_type; 
    
    /**motion vectors for a macroblock
    first coordinate : 0 = forward 1 = backward
    second "         : depend on type
    third  "         : 0 = x, 1 = y
	*/
	int [][][] mv = new int[2][4][2];
	int [][] field_select = new int[2][2];
	int [][][] last_mv = new int[2][2][2];             ///< last MV, used for MV prediction in MPEG1 & B-frame MPEG4
	byte [] fcode_tab;               ///< smallest fcode needed for each MV
	long [][] direct_scale_mv = new long[2][64];   ///< precomputed to avoid divisions in ff_mpeg4_set_direct_mv
	
	MotionEstContext me = new MotionEstContext();
	
	int no_rounding;  /**< apply no rounding to motion compensation (MPEG4, msmpeg4, ...)
	                     for b-frames rounding mode is always 0 */
	 
	 /* macroblock layer */
    int mb_x, mb_y;
    int mb_skip_run;
    int mb_intra;
    int [] mb_type;           ///< Table for candidate MB types for encoding

    int [] block_index = new int[6]; ///< index to current MB in block based arrays with edges
    int [] block_wrap = new int[6];
    byte [] dest = new byte[3];

    int [] mb_index2xy;        ///< mb_index -> mb_x + mb_y*mb_stride

    /** matrix transmitted in the bitstream */
    int [] intra_matrix = new int[64];
    int [] chroma_intra_matrix = new int[64];
    int [] inter_matrix = new int[64];
    int [] chroma_inter_matrix = new int[64];
    
    int intra_quant_bias;    ///< bias for the quantizer
    int inter_quant_bias;    ///< bias for the quantizer
    int min_qcoeff;          ///< minimum encodable coefficient
    int max_qcoeff;          ///< maximum encodable coefficient
    int ac_esc_length;       ///< num of bits needed to encode the longest esc
    byte intra_ac_vlc_length;
    byte intra_ac_vlc_last_length;
    byte inter_ac_vlc_length;
    byte inter_ac_vlc_last_length;
    byte luma_dc_vlc_length;
    byte chroma_dc_vlc_length;
    
    int [] coded_score = new int[8];

    /** precomputed matrix (combine qscale and DCT renorm) */
    int [] q_intra_matrix = new int[64];
    int [] q_inter_matrix = new int[64];
    /** identical to the above but for MMX & these are not permutated, second 64 entries are bias*/
    int [][] q_intra_matrix16 = new int[2][64];
    int [][] q_inter_matrix16 = new int[2][64];

    /* noise reduction */
    int [][] dct_error_sum = new int[64][];
    int [] dct_count = new int[2];
    int [][] dct_offset = new int[64][];

    Object opaque;              ///< private data for the user

    /* bit rate control */
    long wanted_bits;
    long total_bits;
    int frame_bits;                ///< bits used for the current frame
    int next_lambda;               ///< next lambda used for retrying to encode a frame
    RateControlContext rc_context = new RateControlContext(); ///< contains stuff only accessed in ratecontrol.c

    /* statistics, used for 2-pass encoding */
    int mv_bits;
    int header_bits;
    int i_tex_bits;
    int p_tex_bits;
    int i_count;
    int f_count;
    int b_count;
    int skip_count;
    int misc_bits; ///< cbp, mb_type
    int last_bits; ///< temp var used for calculating the above vars

    /* error concealment / resync */
    int error_count, error_occurred;
    byte [] error_status_table;       ///< table of the error status of each MB
    
    int resync_mb_x;                 ///< x position of last resync marker
    int resync_mb_y;                 ///< y position of last resync marker
    GetBitContext last_resync_gb;    ///< used to search for the next resync marker
    int mb_num_left;                 ///< number of MBs left in this video packet (for partitioned Slices only)
    int next_p_frame_damaged;        ///< set if the next p frame is damaged, to avoid showing trashed b frames
    int error_recognition;

    ParseContext parse_context = new ParseContext();

    /* H.263 specific */
    int gob_index;
    int obmc;                       ///< overlapped block motion compensation
    int showed_packed_warning;      ///< flag for having shown the warning about divxs invalid b frames

    /* H.263+ specific */
    int umvplus;                    ///< == H263+ && unrestricted_mv
    int h263_aic_dir;               ///< AIC direction: 0 = left, 1 = top
    int h263_slice_structured;
    int alt_inter_vlc;              ///< alternative inter vlc
    int modified_quant;
    int loop_filter;
    int custom_pcf;

    /* mpeg4 specific */
    int time_increment_bits;        ///< number of bits to represent the fractional part of time
    int last_time_base;
    int time_base;                  ///< time in seconds of last I,P,S Frame
    long time;                   ///< time of current frame
    long last_non_b_time;
    int pp_time;               ///< time distance between the last 2 p,s,i frames
    int pb_time;               ///< time distance between the last b and p,s,i frame
    int pp_field_time;
    int pb_field_time;         ///< like above, just for interlaced
    int shape;
    int vol_sprite_usage;
    int sprite_width;
    int sprite_height;
    int sprite_left;
    int sprite_top;
    int sprite_brightness_change;
    int num_sprite_warping_points;
    int real_sprite_warping_points;
    int [][] sprite_traj = new int[4][2];      ///< sprite trajectory points
    int [][] sprite_offset = new int[2][2];         ///< sprite offset[isChroma][isMVY]
    int [][] sprite_delta = new int[2][2];          ///< sprite_delta [isY][isMVY]
    int [] sprite_shift = new int[2];             ///< sprite shift [isChroma]
    int mcsel;
    int quant_precision;
    int quarter_sample;              ///< 1->qpel, 0->half pel ME/MC
    int scalability;
    int hierachy_type;
    int enhancement_type;
    int new_pred;
    int reduced_res_vop;
    int aspect_ratio_info; //FIXME remove
    int sprite_warping_accuracy;
    int low_latency_sprite;
    int data_partitioning;           ///< data partitioning flag from header
    int partitioned_frame;           ///< is current frame partitioned
    int rvlc;                        ///< reversible vlc
    int resync_marker;               ///< could this stream contain resync markers
    int low_delay;                   ///< no reordering needed / has no b-frames
    int vo_type;
    int vol_control_parameters;      ///< does the stream contain the low_delay flag, used to workaround buggy encoders
    int intra_dc_threshold;          ///< QP above whch the ac VLC should be used for intra dc
    int use_intra_dc_vlc;
    PutBitContext tex_pb;            ///< used for data partitioned VOPs
    PutBitContext pb2;               ///< used for data partitioned VOPs
    int mpeg_quant;
    int t_frame;                       ///< time distance of first I -> B, used for interlaced b frames
    int padding_bug_score;             ///< used to detect the VERY common padding bug in MPEG4
    int cplx_estimation_trash_i;
    int cplx_estimation_trash_p;
    int cplx_estimation_trash_b;

    /* divx specific, used to workaround (many) bugs in divx5 */
    int divx_version;
    int divx_build;
    int divx_packed;
    byte [] bitstream_buffer; //Divx 5.01 puts several frames in a single one, this is used to reorder them
    int bitstream_buffer_size;
    int allocated_bitstream_buffer_size;

    int xvid_build;

    /* lavc specific stuff, used to workaround bugs in libavcodec */
    int lavc_build;

    /* RV10 specific */
    int rv10_version; ///< RV10 version: 0 or 3
    int [] rv10_first_dc_coded = new int[3];
    int orig_width, orig_height;

    /* MJPEG specific */
    MJpegContext mjpeg_ctx;
    int [] mjpeg_vsample = new int[3];       ///< vertical sampling factors, default = {2, 1, 1}
    int [] mjpeg_hsample = new int[3];       ///< horizontal sampling factors, default = {2, 1, 1}

    /* MSMPEG4 specific */
    int mv_table_index;
    int rl_table_index;
    int rl_chroma_table_index;
    int dc_table_index;
    int use_skip_mb_code;
    int slice_height;      ///< in macroblocks
    int first_slice_line;  ///< used in mpeg4 too to handle resync markers
    int flipflop_rounding;
    int msmpeg4_version;   ///< 0=not msmpeg4, 1=mp41, 2=mp42, 3=mp43/divx3 4=wmv1/7 5=wmv2/8
    int per_mb_rl_table;
    int esc3_level_length;
    int esc3_run_length;
    /** [mb_intra][isChroma][level][run][last] */
    int [][][][][] ac_stats = new int[2][2][RLTable.MAX_LEVEL+1][RLTable.MAX_RUN+1][2];
    int inter_intra_pred;
    int mspel;

    /* decompression specific */
    GetBitContext gb;

    /* Mpeg1 specific */
    int gop_picture_number;  ///< index of the first picture of a GOP based on fake_pic_num & mpeg1 specific
    int last_mv_dir;         ///< last mv_dir, used for b frame encoding
    int broken_link;         ///< no_output_of_prior_pics_flag
    byte vbv_delay_ptr;  ///< pointer to vbv_delay in the bitstream

    /* MPEG-2-specific - I wished not to have to support this mess. */
    int progressive_sequence;
    int [][] mpeg_f_code = new int[2][2];
    int picture_structure;

    int intra_dc_precision;
    int frame_pred_frame_dct;
    int top_field_first;
    int concealment_motion_vectors;
    int q_scale_type;
    int intra_vlc_format;
    int alternate_scan;
    int repeat_first_field;
    int chroma_420_type;
    int chroma_format;
    int chroma_x_shift;//depend on pix_format, that depend on chroma_format
    int chroma_y_shift;

    int progressive_frame;
    int [] full_pel = new int[2];
    int interlaced_dct;
    int first_slice;
    int first_field;         ///< is 1 for the first field of a field picture 0 otherwise

    /* RTP specific */
    int rtp_mode;

    byte [] ptr_lastgob;
    int swap_uv;             //vcr2 codec is an MPEG-2 variant with U and V swapped
    short [][] pblocks = new short[12][64];

    short [] block = new short[64]; ///< points to one of the following blocks
    short [][] blocks = new short[8][64]; // for HQ mode we need to keep the best block
    
    String dct_unquantize_intra = "";
    String dct_unquantize_inter = "";
    

    public Object clone() {
    	MpegEncContext enc = null;
    	try {
    		enc = (MpegEncContext) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}	   	    
	    return enc;
	}
    
    
    public int get_ac_esc_length() {
		return ac_esc_length;
	}

	public void set_ac_esc_length(int ac_esc_length) {
		this.ac_esc_length = ac_esc_length;
	}
	
	public AVCodecContext get_avctx() {
		return avctx;
	}

	public void set_avctx(AVCodecContext avctx) {
		this.avctx = avctx;
	}

	public int get_width() {
		return width;
	}

	public void set_width(int width) {
		this.width = width;
	}

	public int get_height() {
		return height;
	}

	public void set_height(int height) {
		this.height = height;
	}

	public int get_gop_size() {
		return gop_size;
	}

	public void set_gop_size(int gop_size) {
		this.gop_size = gop_size;
	}

	public int get_intra_only() {
		return intra_only;
	}

	public void set_intra_only(int intra_only) {
		this.intra_only = intra_only;
	}

	public int get_bit_rate() {
		return bit_rate;
	}

	public void set_bit_rate(int bit_rate) {
		this.bit_rate = bit_rate;
	}

	public OutputFormat get_out_format() {
		return out_format;
	}

	public void set_out_format(OutputFormat out_format) {
		this.out_format = out_format;
	}

	public int get_h263_pred() {
		return h263_pred;
	}

	public void set_h263_pred(int h263_pred) {
		this.h263_pred = h263_pred;
	}

	public int get_pb_frame() {
		return pb_frame;
	}

	public void set_pb_frame(int pb_frame) {
		this.pb_frame = pb_frame;
	}	

	public int get_h263_plus() {
		return h263_plus;
	}

	public void set_h263_plus(int h263_plus) {
		this.h263_plus = h263_plus;
	}

	public int get_h263_msmpeg4() {
		return h263_msmpeg4;
	}

	public void set_h263_msmpeg4(int h263_msmpeg4) {
		this.h263_msmpeg4 = h263_msmpeg4;
	}

	public int get_h263_flv() {
		return h263_flv;
	}

	public void set_h263_flv(int h263_flv) {
		this.h263_flv = h263_flv;
	}

	public CodecID get_codec_id() {
		return codec_id;
	}

	public void set_codec_id(CodecID codec_id) {
		this.codec_id = codec_id;
	}

	public int get_fixed_qscale() {
		return fixed_qscale;
	}

	public void set_fixed_qscale(int fixed_qscale) {
		this.fixed_qscale = fixed_qscale;
	}

	public int get_encoding() {
		return encoding;
	}

	public void set_encoding(int encoding) {
		this.encoding = encoding;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}	

	public boolean has_flag(int flag) {
		return (this.flags & flag) != 0;
	}

	public int get_flags2() {
		return flags2;
	}

	public void set_flags2(int flags2) {
		this.flags2 = flags2;
	}
	
	public boolean has_flag2(int flag) {
		return (this.flags2 & flag) != 0;
	}

	public int get_max_b_frames() {
		return max_b_frames;
	}

	public void set_max_b_frames(int max_b_frames) {
		this.max_b_frames = max_b_frames;
	}

	public int get_luma_elim_threshold() {
		return luma_elim_threshold;
	}

	public void set_luma_elim_threshold(int luma_elim_threshold) {
		this.luma_elim_threshold = luma_elim_threshold;
	}

	public int get_chroma_elim_threshold() {
		return chroma_elim_threshold;
	}

	public void set_chroma_elim_threshold(int chroma_elim_threshold) {
		this.chroma_elim_threshold = chroma_elim_threshold;
	}

	public int get_strict_std_compliance() {
		return strict_std_compliance;
	}

	public void set_strict_std_compliance(int strict_std_compliance) {
		this.strict_std_compliance = strict_std_compliance;
	}

	public int get_workaround_bugs() {
		return workaround_bugs;
	}

	public void set_workaround_bugs(int workaround_bugs) {
		this.workaround_bugs = workaround_bugs;
	}

	public int get_codec_tag() {
		return codec_tag;
	}

	public void set_codec_tag(int codec_tag) {
		this.codec_tag = codec_tag;
	}

	public int get_stream_codec_tag() {
		return stream_codec_tag;
	}

	public void set_stream_codec_tag(int stream_codec_tag) {
		this.stream_codec_tag = stream_codec_tag;
	}

	public int get_context_initialized() {
		return context_initialized;
	}

	public void set_context_initialized(int context_initialized) {
		this.context_initialized = context_initialized;
	}

	public int get_input_picture_number() {
		return input_picture_number;
	}

	public void set_input_picture_number(int input_picture_number) {
		this.input_picture_number = input_picture_number;
	}

	public int get_coded_picture_number() {
		return coded_picture_number;
	}

	public void set_coded_picture_number(int coded_picture_number) {
		this.coded_picture_number = coded_picture_number;
	}

	public int get_picture_number() {
		return picture_number;
	}

	public void set_picture_number(int picture_number) {
		this.picture_number = picture_number;
	}

	public int get_picture_in_gop_number() {
		return picture_in_gop_number;
	}

	public void set_picture_in_gop_number(int picture_in_gop_number) {
		this.picture_in_gop_number = picture_in_gop_number;
	}

	public int get_b_frames_since_non_b() {
		return b_frames_since_non_b;
	}

	public void set_b_frames_since_non_b(int b_frames_since_non_b) {
		this.b_frames_since_non_b = b_frames_since_non_b;
	}

	public long get_user_specified_pts() {
		return user_specified_pts;
	}

	public void set_user_specified_pts(long user_specified_pts) {
		this.user_specified_pts = user_specified_pts;
	}

	public int get_mb_width() {
		return mb_width;
	}

	public void set_mb_width(int mb_width) {
		this.mb_width = mb_width;
	}

	public int get_mb_height() {
		return mb_height;
	}

	public void set_mb_height(int mb_height) {
		this.mb_height = mb_height;
	}

	public int get_mb_stride() {
		return mb_stride;
	}

	public void set_mb_stride(int mb_stride) {
		this.mb_stride = mb_stride;
	}

	public int get_b8_stride() {
		return b8_stride;
	}

	public void set_b8_stride(int b8_stride) {
		this.b8_stride = b8_stride;
	}

	public int get_b4_stride() {
		return b4_stride;
	}

	public void set_b4_stride(int b4_stride) {
		this.b4_stride = b4_stride;
	}

	public int get_h_edge_pos() {
		return h_edge_pos;
	}

	public void set_h_edge_pos(int h_edge_pos) {
		this.h_edge_pos = h_edge_pos;
	}

	public int get_v_edge_pos() {
		return v_edge_pos;
	}

	public void set_v_edge_pos(int v_edge_pos) {
		this.v_edge_pos = v_edge_pos;
	}

	public int get_mb_num() {
		return mb_num;
	}

	public void set_mb_num(int mb_num) {
		this.mb_num = mb_num;
	}

	public int get_linesize() {
		return linesize;
	}

	public void set_linesize(int linesize) {
		this.linesize = linesize;
	}

	public int get_uvlinesize() {
		return uvlinesize;
	}

	public void set_uvlinesize(int uvlinesize) {
		this.uvlinesize = uvlinesize;
	}

	public Picture [] get_picture() {
		return picture;
	}

	public void set_picture(Picture [] picture) {
		this.picture = picture;
	}

	public void set_picture(int i, Picture picture) {
		this.picture[i] = picture;
	}

	public Picture [] get_input_picture() {
		return input_picture;
	}

	public void set_input_picture(Picture [] input_picture) {
		this.input_picture = input_picture;
	}

	public  Picture [] get_reordered_input_picture() {
		return reordered_input_picture;
	}

	public Picture get_reordered_input_picture(int i) {
		return reordered_input_picture[i];
	}

	public void set_reordered_input_picture(int i, Picture pic) {
		reordered_input_picture[i] = pic;
	}

	public void set_reordered_input_picture(
			 Picture [] reordered_input_picture) {
		this.reordered_input_picture = reordered_input_picture;
	}

	public int get_y_dc_scale() {
		return y_dc_scale;
	}

	public void set_y_dc_scale(int y_dc_scale) {
		this.y_dc_scale = y_dc_scale;
	}

	public int get_c_dc_scale() {
		return c_dc_scale;
	}

	public void set_c_dc_scale(int c_dc_scale) {
		this.c_dc_scale = c_dc_scale;
	}

	public int get_ac_pred() {
		return ac_pred;
	}

	public void set_ac_pred(int ac_pred) {
		this.ac_pred = ac_pred;
	}

	public int[] get_block_last_index() {
		return block_last_index;
	}

	public void set_block_last_index(int[] block_last_index) {
		this.block_last_index = block_last_index;
	}

	public int get_h263_aic() {
		return h263_aic;
	}

	public void set_h263_aic(int h263_aic) {
		this.h263_aic = h263_aic;
	}

	public ScanTable get_inter_scantable() {
		return inter_scantable;
	}

	public void set_inter_scantable(ScanTable inter_scantable) {
		this.inter_scantable = inter_scantable;
	}

	public ScanTable get_intra_scantable() {
		return intra_scantable;
	}

	public void set_intra_scantable(ScanTable intra_scantable) {
		this.intra_scantable = intra_scantable;
	}

	public ScanTable get_intra_h_scantable() {
		return intra_h_scantable;
	}

	public void set_intra_h_scantable(ScanTable intra_h_scantable) {
		this.intra_h_scantable = intra_h_scantable;
	}

	public ScanTable get_intra_v_scantable() {
		return intra_v_scantable;
	}

	public void set_intra_v_scantable(ScanTable intra_v_scantable) {
		this.intra_v_scantable = intra_v_scantable;
	}

	public PutBitContext get_pb() {
		return pb;
	}

	public void set_pb(PutBitContext pb) {
		this.pb = pb;
	}

	public int get_start_mb_y() {
		return start_mb_y;
	}

	public void set_start_mb_y(int start_mb_y) {
		this.start_mb_y = start_mb_y;
	}

	public int get_end_mb_y() {
		return end_mb_y;
	}

	public void set_end_mb_y(int end_mb_y) {
		this.end_mb_y = end_mb_y;
	}

	public ArrayList<MpegEncContext> get_thread_context() {
		return thread_context;
	}

	public void set_thread_context(ArrayList<MpegEncContext> thread_context) {
		this.thread_context = thread_context;
	}

	public Picture get_last_picture() {
		return last_picture;
	}

	public Picture get_last_picture_ptr() {
		return last_picture;
	}

	public void set_last_picture(Picture last_picture) {
		this.last_picture = last_picture;
	}

	public void set_last_picture_ptr(Picture last_picture) {
		this.last_picture = last_picture;
	}

	public Picture get_next_picture() {
		return next_picture;
	}

	public Picture get_next_picture_ptr() {
		return next_picture;
	}

	public void set_next_picture(Picture next_picture) {
		this.next_picture = next_picture;
	}

	public void set_next_picture_ptr(Picture next_picture) {
		this.next_picture = next_picture;
	}

	public Picture get_new_picture() {
		return new_picture;
	}

	public Picture get_new_picture_ptr() {
		return new_picture;
	}

	public void set_new_picture(Picture new_picture) {
		this.new_picture = new_picture;
	}

	public Picture get_current_picture() {
		return current_picture;
	}

	public void set_current_picture(Picture current_picture) {
		this.current_picture = current_picture;
	}	

	public void set_current_picture_ptr(Picture pic) {
		this.current_picture = pic;	
	}

	public Picture get_current_picture_ptr() {
		return current_picture;
	}

	public int get_picture_count() {
		return picture_count;
	}

	public void set_picture_count(int picture_count) {
		this.picture_count = picture_count;
	}

	public int get_picture_range_start() {
		return picture_range_start;
	}

	public void set_picture_range_start(int picture_range_start) {
		this.picture_range_start = picture_range_start;
	}

	public int get_picture_range_end() {
		return picture_range_end;
	}

	public void set_picture_range_end(int picture_range_end) {
		this.picture_range_end = picture_range_end;
	}

	public byte[] get_visualization_buffer() {
		return visualization_buffer;
	}

	public void set_visualization_buffer(byte[] visualization_buffer) {
		this.visualization_buffer = visualization_buffer;
	}

	public int[] get_last_dc() {
		return last_dc;
	}

	public void set_last_dc(int[] last_dc) {
		this.last_dc = last_dc;
	}

	public int[] get_dc_val_base() {
		return dc_val_base;
	}

	public void set_dc_val_base(int[] dc_val_base) {
		this.dc_val_base = dc_val_base;
	}

	public int[] get_dc_val() {
		return dc_val;
	}

	public void set_dc_val(int[] dc_val) {
		this.dc_val = dc_val;
	}

	public int[] get_dc_cache() {
		return dc_cache;
	}

	public void set_dc_cache(int[] dc_cache) {
		this.dc_cache = dc_cache;
	}

	public byte[] get_y_dc_scale_table() {
		return y_dc_scale_table;
	}

	public void set_y_dc_scale_table(byte[] y_dc_scale_table) {
		this.y_dc_scale_table = Arrays.copyOf(y_dc_scale_table, y_dc_scale_table.length);
	}

	public byte[] get_c_dc_scale_table() {
		return c_dc_scale_table;
	}

	public void set_c_dc_scale_table(byte[] c_dc_scale_table) {
		this.c_dc_scale_table = Arrays.copyOf(c_dc_scale_table, c_dc_scale_table.length);
	}

	public byte[] get_chroma_qscale_table() {
		return chroma_qscale_table;
	}

	public void set_chroma_qscale_table(byte[] chroma_qscale_table) {
		this.chroma_qscale_table = Arrays.copyOf(chroma_qscale_table, chroma_qscale_table.length);
	}

	public byte[] get_coded_block_base() {
		return coded_block_base;
	}

	public void set_coded_block_base(byte[] coded_block_base) {
		this.coded_block_base = coded_block_base;
	}

	public byte[] get_coded_block() {
		return coded_block;
	}

	public void set_coded_block(byte[] coded_block) {
		this.coded_block = coded_block;
	}

	public int[] get_ac_val_base() {
		return ac_val_base;
	}

	public void set_ac_val_base(int[] ac_val_base) {
		this.ac_val_base = ac_val_base;
	}

	public int[][] get_ac_val() {
		return ac_val;
	}

	public void set_ac_val(int[][] ac_val) {
		this.ac_val = ac_val;
	}

	public AVPictureType[] get_prev_pict_types() {
		return prev_pict_types;
	}

	public AVPictureType get_prev_pict_types(int i) {
		return prev_pict_types[i];
	}

	public void set_prev_pict_types(AVPictureType[] prev_pict_types) {
		this.prev_pict_types = prev_pict_types;
	}

	public void set_prev_pict_types(int i, AVPictureType pict) {
		this.prev_pict_types[i] = pict;
	}

	public int get_mb_skipped() {
		return mb_skipped;
	}

	public void set_mb_skipped(int mb_skipped) {
		this.mb_skipped = mb_skipped;
	}

	public byte[] get_mbskip_table() {
		return mbskip_table;
	}

	public void set_mbskip_table(byte[] mbskip_table) {
		this.mbskip_table = mbskip_table;
	}

	public byte[] get_mbintra_table() {
		return mbintra_table;
	}

	public void set_mbintra_table(byte[] mbintra_table) {
		this.mbintra_table = mbintra_table;
	}

	public byte[] get_cbp_table() {
		return cbp_table;
	}

	public void set_cbp_table(byte[] cbp_table) {
		this.cbp_table = cbp_table;
	}

	public byte[] get_pred_dir_table() {
		return pred_dir_table;
	}

	public void set_pred_dir_table(byte[] pred_dir_table) {
		this.pred_dir_table = pred_dir_table;
	}

	public byte[] get_allocated_edge_emu_buffer() {
		return allocated_edge_emu_buffer;
	}

	public void set_allocated_edge_emu_buffer(byte[] allocated_edge_emu_buffer) {
		this.allocated_edge_emu_buffer = allocated_edge_emu_buffer;
	}

	public byte[] get_edge_emu_buffer() {
		return edge_emu_buffer;
	}

	public void set_edge_emu_buffer(byte[] edge_emu_buffer) {
		this.edge_emu_buffer = edge_emu_buffer;
	}

	public byte[] get_rd_scratchpad() {
		return rd_scratchpad;
	}

	public void set_rd_scratchpad(byte[] rd_scratchpad) {
		this.rd_scratchpad = rd_scratchpad;
	}

	public byte[] get_obmc_scratchpad() {
		return obmc_scratchpad;
	}

	public void set_obmc_scratchpad(byte[] obmc_scratchpad) {
		this.obmc_scratchpad = obmc_scratchpad;
	}

	public byte[] get_b_scratchpad() {
		return b_scratchpad;
	}

	public void set_b_scratchpad(byte[] b_scratchpad) {
		this.b_scratchpad = b_scratchpad;
	}

	public int get_qscale() {
		return qscale;
	}

	public void set_qscale(int qscale) {
		this.qscale = qscale;
	}

	public int get_chroma_qscale() {
		return chroma_qscale;
	}

	public void set_chroma_qscale(int chroma_qscale) {
		this.chroma_qscale = chroma_qscale;
	}

	public int get_lambda() {
		return lambda;
	}

	public void set_lambda(int lambda) {
		this.lambda = lambda;
	}

	public int get_lambda2() {
		return lambda2;
	}

	public void set_lambda2(int lambda2) {
		this.lambda2 = lambda2;
	}

	public int[] get_lambda_table() {
		return lambda_table;
	}

	public void set_lambda_table(int[] lambda_table) {
		this.lambda_table = lambda_table;
	}

	public int get_adaptive_quant() {
		return adaptive_quant;
	}

	public void set_adaptive_quant(int adaptive_quant) {
		this.adaptive_quant = adaptive_quant;
	}

	public int get_dquant() {
		return dquant;
	}

	public void set_dquant(int dquant) {
		this.dquant = dquant;
	}

	public int get_closed_gop() {
		return closed_gop;
	}

	public void set_closed_gop(int closed_gop) {
		this.closed_gop = closed_gop;
	}

	public AVPictureType get_pict_type() {
		return pict_type;
	}

	public void set_pict_type(AVPictureType pict_type) {
		this.pict_type = pict_type;
	}

	public AVPictureType get_last_pict_type() {
		return last_pict_type;
	}

	public void set_last_pict_type(AVPictureType last_pict_type) {
		this.last_pict_type = last_pict_type;
	}

	public AVPictureType get_last_non_b_pict_type() {
		return last_non_b_pict_type;
	}

	public void set_last_non_b_pict_type(AVPictureType last_non_b_pict_type) {
		this.last_non_b_pict_type = last_non_b_pict_type;
	}

	public int get_dropable() {
		return dropable;
	}

	public void set_dropable(int dropable) {
		this.dropable = dropable;
	}

	public int get_frame_rate_index() {
		return frame_rate_index;
	}

	public void set_frame_rate_index(int frame_rate_index) {
		this.frame_rate_index = frame_rate_index;
	}

	public int[] get_last_lambda_for() {
		return last_lambda_for;
	}

	public int get_last_lambda_for(int i) {
		return last_lambda_for[i];
	}

	public void set_last_lambda_for(int[] last_lambda_for) {
		this.last_lambda_for = last_lambda_for;
	}

	public int get_skipdct() {
		return skipdct;
	}

	public void set_skipdct(int skipdct) {
		this.skipdct = skipdct;
	}

	public int get_unrestricted_mv() {
		return unrestricted_mv;
	}

	public void set_unrestricted_mv(int unrestricted_mv) {
		this.unrestricted_mv = unrestricted_mv;
	}

	public int get_h263_long_vectors() {
		return h263_long_vectors;
	}

	public void set_h263_long_vectors(int h263_long_vectors) {
		this.h263_long_vectors = h263_long_vectors;
	}

	public int get_decode() {
		return decode;
	}

	public void set_decode(int decode) {
		this.decode = decode;
	}

	public DSPContext get_dsp() {
		return dsp;
	}

	public void set_dsp(DSPContext dsp) {
		this.dsp = dsp;
	}

	public int get_f_code() {
		return f_code;
	}

	public void set_f_code(int f_code) {
		this.f_code = f_code;
	}

	public int get_b_code() {
		return b_code;
	}

	public void set_b_code(int b_code) {
		this.b_code = b_code;
	}

	public int[] get_p_mv_table_base() {
		return p_mv_table_base;
	}

	public void set_p_mv_table_base(int[] p_mv_table_base) {
		this.p_mv_table_base = p_mv_table_base;
	}

	public int[] get_b_forw_mv_table_base() {
		return b_forw_mv_table_base;
	}

	public void set_b_forw_mv_table_base(int[] b_forw_mv_table_base) {
		this.b_forw_mv_table_base = b_forw_mv_table_base;
	}

	public int[] get_b_back_mv_table_base() {
		return b_back_mv_table_base;
	}

	public void set_b_back_mv_table_base(int[] b_back_mv_table_base) {
		this.b_back_mv_table_base = b_back_mv_table_base;
	}

	public int[] get_b_bidir_forw_mv_table_base() {
		return b_bidir_forw_mv_table_base;
	}

	public void set_b_bidir_forw_mv_table_base(int[] b_bidir_forw_mv_table_base) {
		this.b_bidir_forw_mv_table_base = b_bidir_forw_mv_table_base;
	}

	public int[] get_b_bidir_back_mv_table_base() {
		return b_bidir_back_mv_table_base;
	}

	public void set_b_bidir_back_mv_table_base(int[] b_bidir_back_mv_table_base) {
		this.b_bidir_back_mv_table_base = b_bidir_back_mv_table_base;
	}

	public int[] get_b_direct_mv_table_base() {
		return b_direct_mv_table_base;
	}

	public void set_b_direct_mv_table_base(int[] b_direct_mv_table_base) {
		this.b_direct_mv_table_base = b_direct_mv_table_base;
	}

	public int[][][] get_p_field_mv_table_base() {
		return p_field_mv_table_base;
	}

	public void set_p_field_mv_table_base(int[][][] p_field_mv_table_base) {
		this.p_field_mv_table_base = p_field_mv_table_base;
	}

	public int[][][][] get_b_field_mv_table_base() {
		return b_field_mv_table_base;
	}

	public void set_b_field_mv_table_base(int[][][][] b_field_mv_table_base) {
		this.b_field_mv_table_base = b_field_mv_table_base;
	}

	public int[] get_p_mv_table() {
		return p_mv_table;
	}

	public void set_p_mv_table(int[] p_mv_table) {
		this.p_mv_table = p_mv_table;
	}

	public int[] get_b_forw_mv_table() {
		return b_forw_mv_table;
	}

	public void set_b_forw_mv_table(int[] b_forw_mv_table) {
		this.b_forw_mv_table = b_forw_mv_table;
	}

	public int[] get_b_back_mv_table() {
		return b_back_mv_table;
	}

	public void set_b_back_mv_table(int[] b_back_mv_table) {
		this.b_back_mv_table = b_back_mv_table;
	}

	public int[] get_b_bidir_forw_mv_table() {
		return b_bidir_forw_mv_table;
	}

	public void set_b_bidir_forw_mv_table(int[] b_bidir_forw_mv_table) {
		this.b_bidir_forw_mv_table = b_bidir_forw_mv_table;
	}

	public int[] get_b_bidir_back_mv_table() {
		return b_bidir_back_mv_table;
	}

	public void set_b_bidir_back_mv_table(int[] b_bidir_back_mv_table) {
		this.b_bidir_back_mv_table = b_bidir_back_mv_table;
	}

	public int[] get_b_direct_mv_table() {
		return b_direct_mv_table;
	}

	public void set_b_direct_mv_table(int[] b_direct_mv_table) {
		this.b_direct_mv_table = b_direct_mv_table;
	}

	public int[][][] get_p_field_mv_table() {
		return p_field_mv_table;
	}

	public void set_p_field_mv_table(int[][][] p_field_mv_table) {
		this.p_field_mv_table = p_field_mv_table;
	}

	public int[][][][] get_b_field_mv_table() {
		return b_field_mv_table;
	}

	public void set_b_field_mv_table(int[][][][] b_field_mv_table) {
		this.b_field_mv_table = b_field_mv_table;
	}

	public byte[][] get_p_field_select_table() {
		return p_field_select_table;
	}

	public void set_p_field_select_table(byte[][] p_field_select_table) {
		this.p_field_select_table = p_field_select_table;
	}

	public byte[][][] get_b_field_select_table() {
		return b_field_select_table;
	}

	public void set_b_field_select_table(byte[][][] b_field_select_table) {
		this.b_field_select_table = b_field_select_table;
	}

	public Motion_Est_ID get_me_method() {
		return me_method;
	}

	public void set_me_method(Motion_Est_ID me_method) {
		this.me_method = me_method;
	}

	public int get_mv_dir() {
		return mv_dir;
	}

	public void set_mv_dir(int mv_dir) {
		this.mv_dir = mv_dir;
	}

	public int get_mv_type() {
		return mv_type;
	}

	public void set_mv_type(int mv_type) {
		this.mv_type = mv_type;
	}

	public int[][][] get_mv() {
		return mv;
	}

	public void set_mv(int[][][] mv) {
		this.mv = mv;
	}

	public int[][] get_field_select() {
		return field_select;
	}

	public void set_field_select(int[][] field_select) {
		this.field_select = field_select;
	}

	public int[][][] get_last_mv() {
		return last_mv;
	}

	public void set_last_mv(int[][][] last_mv) {
		this.last_mv = last_mv;
	}

	public byte[] get_fcode_tab() {
		return fcode_tab;
	}

	public void set_fcode_tab(byte[] fcode_tab) {
		this.fcode_tab = fcode_tab;
	}

	public long[][] get_direct_scale_mv() {
		return direct_scale_mv;
	}

	public void set_direct_scale_mv(long[][] direct_scale_mv) {
		this.direct_scale_mv = direct_scale_mv;
	}

	public MotionEstContext get_me() {
		return me;
	}

	public void set_me(MotionEstContext me) {
		this.me = me;
	}

	public int get_no_rounding() {
		return no_rounding;
	}

	public void set_no_rounding(int no_rounding) {
		this.no_rounding = no_rounding;
	}

	public int get_mb_x() {
		return mb_x;
	}

	public void set_mb_x(int mb_x) {
		this.mb_x = mb_x;
	}

	public int get_mb_y() {
		return mb_y;
	}

	public void set_mb_y(int mb_y) {
		this.mb_y = mb_y;
	}

	public int get_mb_skip_run() {
		return mb_skip_run;
	}

	public void set_mb_skip_run(int mb_skip_run) {
		this.mb_skip_run = mb_skip_run;
	}

	public int get_mb_intra() {
		return mb_intra;
	}

	public void set_mb_intra(int mb_intra) {
		this.mb_intra = mb_intra;
	}

	public int[] get_mb_type() {
		return mb_type;
	}

	public void set_mb_type(int[] mb_type) {
		this.mb_type = mb_type;
	}

	public void set_mb_type(int i, int mb_type) {
		this.mb_type[i] = mb_type;
	}

	public int[] get_block_index() {
		return block_index;
	}

	public void set_block_index(int[] block_index) {
		this.block_index = block_index;
	}

	public int[] get_block_wrap() {
		return block_wrap;
	}

	public void set_block_wrap(int[] block_wrap) {
		this.block_wrap = block_wrap;
	}

	public byte[] get_dest() {
		return dest;
	}

	public void set_dest(byte[] dest) {
		this.dest = dest;
	}

	public int [] get_mb_index2xy() {
		return mb_index2xy;
	}

	public void set_mb_index2xy(int [] mb_index2xy) {
		this.mb_index2xy = Arrays.copyOf(mb_index2xy, mb_index2xy.length);
	}

	public int[] get_intra_matrix() {
		return intra_matrix;
	}

	public int get_intra_matrix(int i) {
		return intra_matrix[i];
	}


	public void set_intra_matrix(int[] intra_matrix) {
		this.intra_matrix = Arrays.copyOf(intra_matrix, intra_matrix.length);
	}

	public void set_intra_matrix(int i, int intra_matrix) {
		this.intra_matrix[i] = intra_matrix;
	}

	public int[] get_chroma_intra_matrix() {
		return chroma_intra_matrix;
	}

	public void set_chroma_intra_matrix(int[] chroma_intra_matrix) {
		this.chroma_intra_matrix = Arrays.copyOf(chroma_intra_matrix, chroma_intra_matrix.length);
	}

	public int[] get_inter_matrix() {
		return inter_matrix;
	}

	public void set_inter_matrix(int[] inter_matrix) {
		this.inter_matrix = Arrays.copyOf(inter_matrix, inter_matrix.length);
	}

	public int[] get_chroma_inter_matrix() {
		return chroma_inter_matrix;
	}

	public void set_chroma_inter_matrix(int[] chroma_inter_matrix) {
		this.chroma_inter_matrix = Arrays.copyOf(chroma_inter_matrix, chroma_inter_matrix.length);
	}

	public int get_intra_quant_bias() {
		return intra_quant_bias;
	}

	public void set_intra_quant_bias(int intra_quant_bias) {
		this.intra_quant_bias = intra_quant_bias;
	}

	public int get_inter_quant_bias() {
		return inter_quant_bias;
	}

	public void set_inter_quant_bias(int inter_quant_bias) {
		this.inter_quant_bias = inter_quant_bias;
	}

	public int get_min_qcoeff() {
		return min_qcoeff;
	}

	public void set_min_qcoeff(int min_qcoeff) {
		this.min_qcoeff = min_qcoeff;
	}

	public int get_max_qcoeff() {
		return max_qcoeff;
	}

	public void set_max_qcoeff(int max_qcoeff) {
		this.max_qcoeff = max_qcoeff;
	}

	public byte get_intra_ac_vlc_length() {
		return intra_ac_vlc_length;
	}

	public void set_intra_ac_vlc_length(byte intra_ac_vlc_length) {
		this.intra_ac_vlc_length = intra_ac_vlc_length;
	}

	public byte get_intra_ac_vlc_last_length() {
		return intra_ac_vlc_last_length;
	}

	public void set_intra_ac_vlc_last_length(byte intra_ac_vlc_last_length) {
		this.intra_ac_vlc_last_length = intra_ac_vlc_last_length;
	}

	public byte get_inter_ac_vlc_length() {
		return inter_ac_vlc_length;
	}

	public void set_inter_ac_vlc_length(byte inter_ac_vlc_length) {
		this.inter_ac_vlc_length = inter_ac_vlc_length;
	}

	public byte get_inter_ac_vlc_last_length() {
		return inter_ac_vlc_last_length;
	}

	public void set_inter_ac_vlc_last_length(byte inter_ac_vlc_last_length) {
		this.inter_ac_vlc_last_length = inter_ac_vlc_last_length;
	}

	public byte get_luma_dc_vlc_length() {
		return luma_dc_vlc_length;
	}

	public void set_luma_dc_vlc_length(byte luma_dc_vlc_length) {
		this.luma_dc_vlc_length = luma_dc_vlc_length;
	}

	public byte get_chroma_dc_vlc_length() {
		return chroma_dc_vlc_length;
	}

	public void set_chroma_dc_vlc_length(byte chroma_dc_vlc_length) {
		this.chroma_dc_vlc_length = chroma_dc_vlc_length;
	}

	public int[] get_coded_score() {
		return coded_score;
	}

	public void set_coded_score(int[] coded_score) {
		this.coded_score = coded_score;
	}

	public int[] get_q_intra_matrix() {
		return q_intra_matrix;
	}

	public void set_q_intra_matrix(int[] q_intra_matrix) {
		this.q_intra_matrix = q_intra_matrix;
	}

	public int[] get_q_inter_matrix() {
		return q_inter_matrix;
	}

	public void set_q_inter_matrix(int[] q_inter_matrix) {
		this.q_inter_matrix = q_inter_matrix;
	}

	public int[][] get_q_intra_matrix16() {
		return q_intra_matrix16;
	}

	public void set_q_intra_matrix16(int[][] q_intra_matrix16) {
		this.q_intra_matrix16 = q_intra_matrix16;
	}

	public int[][] get_q_inter_matrix16() {
		return q_inter_matrix16;
	}

	public void set_q_inter_matrix16(int[][] q_inter_matrix16) {
		this.q_inter_matrix16 = q_inter_matrix16;
	}

	public int [][] get_dct_error_sum() {
		return dct_error_sum;
	}

	public void set_dct_error_sum(int[][] dct_error_sum) {
		this.dct_error_sum = dct_error_sum;
	}

	public int[] get_dct_count() {
		return dct_count;
	}

	public int get_dct_count(int i) {
		return dct_count[i];
	}

	public void set_dct_count(int[] dct_count) {
		this.dct_count = dct_count;
	}

	public int[][] get_dct_offset() {
		return dct_offset;
	}

	public void set_dct_offset(int[][] dct_offset) {
		this.dct_offset = dct_offset;
	}

	public Object get_opaque() {
		return opaque;
	}

	public void set_opaque(Object opaque) {
		this.opaque = opaque;
	}

	public long get_wanted_bits() {
		return wanted_bits;
	}

	public void set_wanted_bits(long wanted_bits) {
		this.wanted_bits = wanted_bits;
	}

	public long get_total_bits() {
		return total_bits;
	}

	public void set_total_bits(long total_bits) {
		this.total_bits = total_bits;
	}

	public int get_frame_bits() {
		return frame_bits;
	}

	public void set_frame_bits(int frame_bits) {
		this.frame_bits = frame_bits;
	}

	public int get_next_lambda() {
		return next_lambda;
	}

	public void set_next_lambda(int next_lambda) {
		this.next_lambda = next_lambda;
	}

	public RateControlContext get_rc_context() {
		return rc_context;
	}

	public void set_rc_context(RateControlContext rc_context) {
		this.rc_context = rc_context;
	}

	public int get_mv_bits() {
		return mv_bits;
	}

	public void set_mv_bits(int mv_bits) {
		this.mv_bits = mv_bits;
	}

	public int get_header_bits() {
		return header_bits;
	}

	public void set_header_bits(int header_bits) {
		this.header_bits = header_bits;
	}

	public int get_i_tex_bits() {
		return i_tex_bits;
	}

	public void set_i_tex_bits(int i_tex_bits) {
		this.i_tex_bits = i_tex_bits;
	}

	public int get_p_tex_bits() {
		return p_tex_bits;
	}

	public void set_p_tex_bits(int p_tex_bits) {
		this.p_tex_bits = p_tex_bits;
	}

	public int get_i_count() {
		return i_count;
	}

	public void set_i_count(int i_count) {
		this.i_count = i_count;
	}

	public int get_f_count() {
		return f_count;
	}

	public void set_f_count(int f_count) {
		this.f_count = f_count;
	}

	public int get_b_count() {
		return b_count;
	}

	public void set_b_count(int b_count) {
		this.b_count = b_count;
	}

	public int get_skip_count() {
		return skip_count;
	}

	public void set_skip_count(int skip_count) {
		this.skip_count = skip_count;
	}

	public int get_misc_bits() {
		return misc_bits;
	}

	public void set_misc_bits(int misc_bits) {
		this.misc_bits = misc_bits;
	}

	public int get_last_bits() {
		return last_bits;
	}

	public void set_last_bits(int last_bits) {
		this.last_bits = last_bits;
	}

	public int get_error_count() {
		return error_count;
	}

	public void set_error_count(int error_count) {
		this.error_count = error_count;
	}

	public int get_error_occurred() {
		return error_occurred;
	}

	public void set_error_occurred(int error_occurred) {
		this.error_occurred = error_occurred;
	}

	public byte[] get_error_status_table() {
		return error_status_table;
	}

	public void set_error_status_table(byte[] error_status_table) {
		this.error_status_table = error_status_table;
	}

	public int get_resync_mb_x() {
		return resync_mb_x;
	}

	public void set_resync_mb_x(int resync_mb_x) {
		this.resync_mb_x = resync_mb_x;
	}

	public int get_resync_mb_y() {
		return resync_mb_y;
	}

	public void set_resync_mb_y(int resync_mb_y) {
		this.resync_mb_y = resync_mb_y;
	}

	public GetBitContext get_last_resync_gb() {
		return last_resync_gb;
	}

	public void set_last_resync_gb(GetBitContext last_resync_gb) {
		this.last_resync_gb = last_resync_gb;
	}

	public int get_mb_num_left() {
		return mb_num_left;
	}

	public void set_mb_num_left(int mb_num_left) {
		this.mb_num_left = mb_num_left;
	}

	public int get_next_p_frame_damaged() {
		return next_p_frame_damaged;
	}

	public void set_next_p_frame_damaged(int next_p_frame_damaged) {
		this.next_p_frame_damaged = next_p_frame_damaged;
	}

	public int get_error_recognition() {
		return error_recognition;
	}

	public void set_error_recognition(int error_recognition) {
		this.error_recognition = error_recognition;
	}

	public ParseContext get_parse_context() {
		return parse_context;
	}

	public void set_parse_context(ParseContext parse_context) {
		this.parse_context = parse_context;
	}

	public int get_gob_index() {
		return gob_index;
	}

	public void set_gob_index(int gob_index) {
		this.gob_index = gob_index;
	}

	public int get_obmc() {
		return obmc;
	}

	public void set_obmc(int obmc) {
		this.obmc = obmc;
	}

	public int get_showed_packed_warning() {
		return showed_packed_warning;
	}

	public void set_showed_packed_warning(int showed_packed_warning) {
		this.showed_packed_warning = showed_packed_warning;
	}

	public int get_umvplus() {
		return umvplus;
	}

	public void set_umvplus(int umvplus) {
		this.umvplus = umvplus;
	}

	public int get_h263_aic_dir() {
		return h263_aic_dir;
	}

	public void set_h263_aic_dir(int h263_aic_dir) {
		this.h263_aic_dir = h263_aic_dir;
	}

	public int get_h263_slice_structured() {
		return h263_slice_structured;
	}

	public void set_h263_slice_structured(int h263_slice_structured) {
		this.h263_slice_structured = h263_slice_structured;
	}

	public int get_alt_inter_vlc() {
		return alt_inter_vlc;
	}

	public void set_alt_inter_vlc(int alt_inter_vlc) {
		this.alt_inter_vlc = alt_inter_vlc;
	}

	public int get_modified_quant() {
		return modified_quant;
	}

	public void set_modified_quant(int modified_quant) {
		this.modified_quant = modified_quant;
	}

	public int get_loop_filter() {
		return loop_filter;
	}

	public void set_loop_filter(int loop_filter) {
		this.loop_filter = loop_filter;
	}

	public int get_custom_pcf() {
		return custom_pcf;
	}

	public void set_custom_pcf(int custom_pcf) {
		this.custom_pcf = custom_pcf;
	}

	public int get_time_increment_bits() {
		return time_increment_bits;
	}

	public void set_time_increment_bits(int time_increment_bits) {
		this.time_increment_bits = time_increment_bits;
	}

	public int get_last_time_base() {
		return last_time_base;
	}

	public void set_last_time_base(int last_time_base) {
		this.last_time_base = last_time_base;
	}

	public int get_time_base() {
		return time_base;
	}

	public void set_time_base(int time_base) {
		this.time_base = time_base;
	}

	public long get_time() {
		return time;
	}

	public void set_time(long time) {
		this.time = time;
	}

	public long get_last_non_b_time() {
		return last_non_b_time;
	}

	public void set_last_non_b_time(long last_non_b_time) {
		this.last_non_b_time = last_non_b_time;
	}

	public int get_pp_time() {
		return pp_time;
	}

	public void set_pp_time(int pp_time) {
		this.pp_time = pp_time;
	}

	public int get_pb_time() {
		return pb_time;
	}

	public void set_pb_time(int pb_time) {
		this.pb_time = pb_time;
	}

	public int get_pp_field_time() {
		return pp_field_time;
	}

	public void set_pp_field_time(int pp_field_time) {
		this.pp_field_time = pp_field_time;
	}

	public int get_pb_field_time() {
		return pb_field_time;
	}

	public void set_pb_field_time(int pb_field_time) {
		this.pb_field_time = pb_field_time;
	}

	public int get_shape() {
		return shape;
	}

	public void set_shape(int shape) {
		this.shape = shape;
	}

	public int get_vol_sprite_usage() {
		return vol_sprite_usage;
	}

	public void set_vol_sprite_usage(int vol_sprite_usage) {
		this.vol_sprite_usage = vol_sprite_usage;
	}

	public int get_sprite_width() {
		return sprite_width;
	}

	public void set_sprite_width(int sprite_width) {
		this.sprite_width = sprite_width;
	}

	public int get_sprite_height() {
		return sprite_height;
	}

	public void set_sprite_height(int sprite_height) {
		this.sprite_height = sprite_height;
	}

	public int get_sprite_left() {
		return sprite_left;
	}

	public void set_sprite_left(int sprite_left) {
		this.sprite_left = sprite_left;
	}

	public int get_sprite_top() {
		return sprite_top;
	}

	public void set_sprite_top(int sprite_top) {
		this.sprite_top = sprite_top;
	}

	public int get_sprite_brightness_change() {
		return sprite_brightness_change;
	}

	public void set_sprite_brightness_change(int sprite_brightness_change) {
		this.sprite_brightness_change = sprite_brightness_change;
	}

	public int get_num_sprite_warping_points() {
		return num_sprite_warping_points;
	}

	public void set_num_sprite_warping_points(int num_sprite_warping_points) {
		this.num_sprite_warping_points = num_sprite_warping_points;
	}

	public int get_real_sprite_warping_points() {
		return real_sprite_warping_points;
	}

	public void set_real_sprite_warping_points(int real_sprite_warping_points) {
		this.real_sprite_warping_points = real_sprite_warping_points;
	}

	public int[][] get_sprite_traj() {
		return sprite_traj;
	}

	public void set_sprite_traj(int[][] sprite_traj) {
		this.sprite_traj = sprite_traj;
	}

	public int[][] get_sprite_offset() {
		return sprite_offset;
	}

	public void set_sprite_offset(int[][] sprite_offset) {
		this.sprite_offset = sprite_offset;
	}

	public int[][] get_sprite_delta() {
		return sprite_delta;
	}

	public void set_sprite_delta(int[][] sprite_delta) {
		this.sprite_delta = sprite_delta;
	}

	public int[] get_sprite_shift() {
		return sprite_shift;
	}

	public void set_sprite_shift(int[] sprite_shift) {
		this.sprite_shift = sprite_shift;
	}

	public int get_mcsel() {
		return mcsel;
	}

	public void set_mcsel(int mcsel) {
		this.mcsel = mcsel;
	}

	public int get_quant_precision() {
		return quant_precision;
	}

	public void set_quant_precision(int quant_precision) {
		this.quant_precision = quant_precision;
	}

	public int get_quarter_sample() {
		return quarter_sample;
	}

	public void set_quarter_sample(int quarter_sample) {
		this.quarter_sample = quarter_sample;
	}

	public int get_scalability() {
		return scalability;
	}

	public void set_scalability(int scalability) {
		this.scalability = scalability;
	}

	public int get_hierachy_type() {
		return hierachy_type;
	}

	public void set_hierachy_type(int hierachy_type) {
		this.hierachy_type = hierachy_type;
	}

	public int get_enhancement_type() {
		return enhancement_type;
	}

	public void set_enhancement_type(int enhancement_type) {
		this.enhancement_type = enhancement_type;
	}

	public int get_new_pred() {
		return new_pred;
	}

	public void set_new_pred(int new_pred) {
		this.new_pred = new_pred;
	}

	public int get_reduced_res_vop() {
		return reduced_res_vop;
	}

	public void set_reduced_res_vop(int reduced_res_vop) {
		this.reduced_res_vop = reduced_res_vop;
	}

	public int get_aspect_ratio_info() {
		return aspect_ratio_info;
	}

	public void set_aspect_ratio_info(int aspect_ratio_info) {
		this.aspect_ratio_info = aspect_ratio_info;
	}

	public int get_sprite_warping_accuracy() {
		return sprite_warping_accuracy;
	}

	public void set_sprite_warping_accuracy(int sprite_warping_accuracy) {
		this.sprite_warping_accuracy = sprite_warping_accuracy;
	}

	public int get_low_latency_sprite() {
		return low_latency_sprite;
	}

	public void set_low_latency_sprite(int low_latency_sprite) {
		this.low_latency_sprite = low_latency_sprite;
	}

	public int get_data_partitioning() {
		return data_partitioning;
	}

	public void set_data_partitioning(int data_partitioning) {
		this.data_partitioning = data_partitioning;
	}

	public int get_partitioned_frame() {
		return partitioned_frame;
	}

	public void set_partitioned_frame(int partitioned_frame) {
		this.partitioned_frame = partitioned_frame;
	}

	public int get_rvlc() {
		return rvlc;
	}

	public void set_rvlc(int rvlc) {
		this.rvlc = rvlc;
	}

	public int get_resync_marker() {
		return resync_marker;
	}

	public void set_resync_marker(int resync_marker) {
		this.resync_marker = resync_marker;
	}

	public int get_low_delay() {
		return low_delay;
	}

	public void set_low_delay(int low_delay) {
		this.low_delay = low_delay;
	}

	public int get_vo_type() {
		return vo_type;
	}

	public void set_vo_type(int vo_type) {
		this.vo_type = vo_type;
	}

	public int get_vol_control_parameters() {
		return vol_control_parameters;
	}

	public void set_vol_control_parameters(int vol_control_parameters) {
		this.vol_control_parameters = vol_control_parameters;
	}

	public int get_intra_dc_threshold() {
		return intra_dc_threshold;
	}

	public void set_intra_dc_threshold(int intra_dc_threshold) {
		this.intra_dc_threshold = intra_dc_threshold;
	}

	public int get_use_intra_dc_vlc() {
		return use_intra_dc_vlc;
	}

	public void set_use_intra_dc_vlc(int use_intra_dc_vlc) {
		this.use_intra_dc_vlc = use_intra_dc_vlc;
	}

	public PutBitContext get_tex_pb() {
		return tex_pb;
	}

	public void set_tex_pb(PutBitContext tex_pb) {
		this.tex_pb = tex_pb;
	}

	public PutBitContext get_pb2() {
		return pb2;
	}

	public void set_pb2(PutBitContext pb2) {
		this.pb2 = pb2;
	}

	public int get_mpeg_quant() {
		return mpeg_quant;
	}

	public void set_mpeg_quant(int mpeg_quant) {
		this.mpeg_quant = mpeg_quant;
	}

	public int get_t_frame() {
		return t_frame;
	}

	public void set_t_frame(int t_frame) {
		this.t_frame = t_frame;
	}

	public int get_padding_bug_score() {
		return padding_bug_score;
	}

	public void set_padding_bug_score(int padding_bug_score) {
		this.padding_bug_score = padding_bug_score;
	}

	public int get_cplx_estimation_trash_i() {
		return cplx_estimation_trash_i;
	}

	public void set_cplx_estimation_trash_i(int cplx_estimation_trash_i) {
		this.cplx_estimation_trash_i = cplx_estimation_trash_i;
	}

	public int get_cplx_estimation_trash_p() {
		return cplx_estimation_trash_p;
	}

	public void set_cplx_estimation_trash_p(int cplx_estimation_trash_p) {
		this.cplx_estimation_trash_p = cplx_estimation_trash_p;
	}

	public int get_cplx_estimation_trash_b() {
		return cplx_estimation_trash_b;
	}

	public void set_cplx_estimation_trash_b(int cplx_estimation_trash_b) {
		this.cplx_estimation_trash_b = cplx_estimation_trash_b;
	}

	public int get_divx_version() {
		return divx_version;
	}

	public void set_divx_version(int divx_version) {
		this.divx_version = divx_version;
	}

	public int get_divx_build() {
		return divx_build;
	}

	public void set_divx_build(int divx_build) {
		this.divx_build = divx_build;
	}

	public int get_divx_packed() {
		return divx_packed;
	}

	public void set_divx_packed(int divx_packed) {
		this.divx_packed = divx_packed;
	}

	public byte[] get_bitstream_buffer() {
		return bitstream_buffer;
	}

	public void set_bitstream_buffer(byte[] bitstream_buffer) {
		this.bitstream_buffer = bitstream_buffer;
	}

	public int get_bitstream_buffer_size() {
		return bitstream_buffer_size;
	}

	public void set_bitstream_buffer_size(int bitstream_buffer_size) {
		this.bitstream_buffer_size = bitstream_buffer_size;
	}

	public int get_allocated_bitstream_buffer_size() {
		return allocated_bitstream_buffer_size;
	}

	public void set_allocated_bitstream_buffer_size(
			int allocated_bitstream_buffer_size) {
		this.allocated_bitstream_buffer_size = allocated_bitstream_buffer_size;
	}

	public int get_xvid_build() {
		return xvid_build;
	}

	public void set_xvid_build(int xvid_build) {
		this.xvid_build = xvid_build;
	}

	public int get_lavc_build() {
		return lavc_build;
	}

	public void set_lavc_build(int lavc_build) {
		this.lavc_build = lavc_build;
	}

	public int get_rv10_version() {
		return rv10_version;
	}

	public void set_rv10_version(int rv10_version) {
		this.rv10_version = rv10_version;
	}

	public int[] get_rv10_first_dc_coded() {
		return rv10_first_dc_coded;
	}

	public void set_rv10_first_dc_coded(int[] rv10_first_dc_coded) {
		this.rv10_first_dc_coded = rv10_first_dc_coded;
	}

	public int get_orig_width() {
		return orig_width;
	}

	public void set_orig_width(int orig_width) {
		this.orig_width = orig_width;
	}

	public int get_orig_height() {
		return orig_height;
	}

	public void set_orig_height(int orig_height) {
		this.orig_height = orig_height;
	}

	public MJpegContext get_mjpeg_ctx() {
		return mjpeg_ctx;
	}

	public void set_mjpeg_ctx(MJpegContext mjpeg_ctx) {
		this.mjpeg_ctx = mjpeg_ctx;
	}

	public int [] get_mjpeg_vsample() {
		return mjpeg_vsample;
	}

	public int get_mjpeg_vsample(int i) {
		return mjpeg_vsample[i];
	}

	public void set_mjpeg_vsample(int[] mjpeg_vsample) {
		this.mjpeg_vsample = mjpeg_vsample;
	}

	public int[] get_mjpeg_hsample() {
		return mjpeg_hsample;
	}

	public int get_mjpeg_hsample(int i) {
		return mjpeg_hsample[i];
	}

	public void set_mjpeg_hsample(int[] mjpeg_hsample) {
		this.mjpeg_hsample = mjpeg_hsample;
	}

	public int get_mv_table_index() {
		return mv_table_index;
	}

	public void set_mv_table_index(int mv_table_index) {
		this.mv_table_index = mv_table_index;
	}

	public int get_rl_table_index() {
		return rl_table_index;
	}

	public void set_rl_table_index(int rl_table_index) {
		this.rl_table_index = rl_table_index;
	}

	public int get_rl_chroma_table_index() {
		return rl_chroma_table_index;
	}

	public void set_rl_chroma_table_index(int rl_chroma_table_index) {
		this.rl_chroma_table_index = rl_chroma_table_index;
	}

	public int get_dc_table_index() {
		return dc_table_index;
	}

	public void set_dc_table_index(int dc_table_index) {
		this.dc_table_index = dc_table_index;
	}

	public int get_use_skip_mb_code() {
		return use_skip_mb_code;
	}

	public void set_use_skip_mb_code(int use_skip_mb_code) {
		this.use_skip_mb_code = use_skip_mb_code;
	}

	public int get_slice_height() {
		return slice_height;
	}

	public void set_slice_height(int slice_height) {
		this.slice_height = slice_height;
	}

	public int get_first_slice_line() {
		return first_slice_line;
	}

	public void set_first_slice_line(int first_slice_line) {
		this.first_slice_line = first_slice_line;
	}

	public int get_flipflop_rounding() {
		return flipflop_rounding;
	}

	public void set_flipflop_rounding(int flipflop_rounding) {
		this.flipflop_rounding = flipflop_rounding;
	}

	public int get_msmpeg4_version() {
		return msmpeg4_version;
	}

	public void set_msmpeg4_version(int msmpeg4_version) {
		this.msmpeg4_version = msmpeg4_version;
	}

	public int get_per_mb_rl_table() {
		return per_mb_rl_table;
	}

	public void set_per_mb_rl_table(int per_mb_rl_table) {
		this.per_mb_rl_table = per_mb_rl_table;
	}

	public int get_esc3_level_length() {
		return esc3_level_length;
	}

	public void set_esc3_level_length(int esc3_level_length) {
		this.esc3_level_length = esc3_level_length;
	}

	public int get_esc3_run_length() {
		return esc3_run_length;
	}

	public void set_esc3_run_length(int esc3_run_length) {
		this.esc3_run_length = esc3_run_length;
	}

	public int[][][][][] get_ac_stats() {
		return ac_stats;
	}

	public void set_ac_stats(int[][][][][] ac_stats) {
		this.ac_stats = ac_stats;
	}

	public int get_inter_intra_pred() {
		return inter_intra_pred;
	}

	public void set_inter_intra_pred(int inter_intra_pred) {
		this.inter_intra_pred = inter_intra_pred;
	}

	public int get_mspel() {
		return mspel;
	}

	public void set_mspel(int mspel) {
		this.mspel = mspel;
	}

	public GetBitContext get_gb() {
		return gb;
	}

	public void set_gb(GetBitContext gb) {
		this.gb = gb;
	}

	public int get_gop_picture_number() {
		return gop_picture_number;
	}

	public void set_gop_picture_number(int gop_picture_number) {
		this.gop_picture_number = gop_picture_number;
	}

	public int get_last_mv_dir() {
		return last_mv_dir;
	}

	public void set_last_mv_dir(int last_mv_dir) {
		this.last_mv_dir = last_mv_dir;
	}

	public int get_broken_link() {
		return broken_link;
	}

	public void set_broken_link(int broken_link) {
		this.broken_link = broken_link;
	}

	public byte get_vbv_delay_ptr() {
		return vbv_delay_ptr;
	}

	public void set_vbv_delay_ptr(byte vbv_delay_ptr) {
		this.vbv_delay_ptr = vbv_delay_ptr;
	}

	public int get_progressive_sequence() {
		return progressive_sequence;
	}

	public void set_progressive_sequence(int progressive_sequence) {
		this.progressive_sequence = progressive_sequence;
	}

	public int[][] get_mpeg_f_code() {
		return mpeg_f_code;
	}

	public void set_mpeg_f_code(int[][] mpeg_f_code) {
		this.mpeg_f_code = mpeg_f_code;
	}

	public int get_picture_structure() {
		return picture_structure;
	}

	public void set_picture_structure(int picture_structure) {
		this.picture_structure = picture_structure;
	}

	public int get_intra_dc_precision() {
		return intra_dc_precision;
	}

	public void set_intra_dc_precision(int intra_dc_precision) {
		this.intra_dc_precision = intra_dc_precision;
	}

	public int get_frame_pred_frame_dct() {
		return frame_pred_frame_dct;
	}

	public void set_frame_pred_frame_dct(int frame_pred_frame_dct) {
		this.frame_pred_frame_dct = frame_pred_frame_dct;
	}

	public int get_top_field_first() {
		return top_field_first;
	}

	public void set_top_field_first(int top_field_first) {
		this.top_field_first = top_field_first;
	}

	public int get_concealment_motion_vectors() {
		return concealment_motion_vectors;
	}

	public void set_concealment_motion_vectors(int concealment_motion_vectors) {
		this.concealment_motion_vectors = concealment_motion_vectors;
	}

	public int get_q_scale_type() {
		return q_scale_type;
	}

	public void set_q_scale_type(int q_scale_type) {
		this.q_scale_type = q_scale_type;
	}

	public int get_intra_vlc_format() {
		return intra_vlc_format;
	}

	public void set_intra_vlc_format(int intra_vlc_format) {
		this.intra_vlc_format = intra_vlc_format;
	}

	public int get_alternate_scan() {
		return alternate_scan;
	}

	public void set_alternate_scan(int alternate_scan) {
		this.alternate_scan = alternate_scan;
	}

	public int get_repeat_first_field() {
		return repeat_first_field;
	}

	public void set_repeat_first_field(int repeat_first_field) {
		this.repeat_first_field = repeat_first_field;
	}

	public int get_chroma_420_type() {
		return chroma_420_type;
	}

	public void set_chroma_420_type(int chroma_420_type) {
		this.chroma_420_type = chroma_420_type;
	}

	public int get_chroma_format() {
		return chroma_format;
	}

	public void set_chroma_format(int chroma_format) {
		this.chroma_format = chroma_format;
	}

	public int get_chroma_x_shift() {
		return chroma_x_shift;
	}

	public void set_chroma_x_shift(int chroma_x_shift) {
		this.chroma_x_shift = chroma_x_shift;
	}

	public int get_chroma_y_shift() {
		return chroma_y_shift;
	}

	public void set_chroma_y_shift(int chroma_y_shift) {
		this.chroma_y_shift = chroma_y_shift;
	}

	public int get_progressive_frame() {
		return progressive_frame;
	}

	public void set_progressive_frame(int progressive_frame) {
		this.progressive_frame = progressive_frame;
	}

	public int[] get_full_pel() {
		return full_pel;
	}

	public void set_full_pel(int[] full_pel) {
		this.full_pel = full_pel;
	}

	public int get_interlaced_dct() {
		return interlaced_dct;
	}

	public void set_interlaced_dct(int interlaced_dct) {
		this.interlaced_dct = interlaced_dct;
	}

	public int get_first_slice() {
		return first_slice;
	}

	public void set_first_slice(int first_slice) {
		this.first_slice = first_slice;
	}

	public int get_first_field() {
		return first_field;
	}

	public void set_first_field(int first_field) {
		this.first_field = first_field;
	}

	public int get_rtp_mode() {
		return rtp_mode;
	}

	public void set_rtp_mode(int rtp_mode) {
		this.rtp_mode = rtp_mode;
	}

	public byte[] get_ptr_lastgob() {
		return ptr_lastgob;
	}

	public void set_ptr_lastgob(byte[] ptr_lastgob) {
		this.ptr_lastgob = ptr_lastgob;
	}

	public int get_swap_uv() {
		return swap_uv;
	}

	public void set_swap_uv(int swap_uv) {
		this.swap_uv = swap_uv;
	}

	public short[][] get_pblocks() {
		return pblocks;
	}

	public void set_pblocks(short[][] pblocks) {
		this.pblocks = pblocks;
	}

	public void set_pblocks(int i, short[] pblocks) {
		this.pblocks[i] = pblocks;
	}

	public short[] get_block() {
		return block;
	}

	public void set_block(short[] block) {
		this.block = block;
	}

	public short[][] get_blocks() {
		return blocks;
	}

	public void set_blocks(short[][] blocks) {
		this.blocks = blocks;
	}

	int decode_mb(short [][] block) { // used by some codecs to avoid a switch()
    	return -1;
    }

    void dct_unquantize_mpeg1_intra(short [] block/*align 16*/, int n, int qscale) { 
    	int i, level, nCoeffs;
	    int [] quant_matrix;
	
	    nCoeffs = get_block_last_index()[n];
	
	    if (n < 4)
	        block[0] = (short)(block[0] * get_y_dc_scale());
	    else
	        block[0] = (short)(block[0] * get_c_dc_scale());
	    /* XXX: only mpeg1 */
	    quant_matrix = get_intra_matrix();
	    for (i = 1 ; i <= nCoeffs ; i++) {
	        int j = get_intra_scantable().get_permutated()[i];
	        level = block[j];
	        if (level != 0) {
	            if (level < 0) {
	                level = -level;
	                level = (int)(level * qscale * quant_matrix[j]) >> 3;
	                level = (level - 1) | 1;
	                level = -level;
	            } else {
	                level = (int)(level * qscale * quant_matrix[j]) >> 3;
	                level = (level - 1) | 1;
	            }
	            block[j] = (short)level;
	        }
	    }    	
    }
    
    void dct_unquantize_mpeg1_inter(short [] block/*align 16*/, int n, int qscale) {
    	int i, level, nCoeffs;
	    int [] quant_matrix;

	    nCoeffs = get_block_last_index()[n];

	    quant_matrix = get_inter_matrix();
	    for (i = 0 ; i <= nCoeffs ; i++) {
	        int j = get_intra_scantable().get_permutated()[i];
	        level = block[j];
	        if (level != 0) {
	            if (level < 0) {
	                level = -level;
	                level = (((level << 1) + 1) * qscale *
	                         ((int) (quant_matrix[j]))) >> 4;
	                level = (level - 1) | 1;
	                level = -level;
	            } else {
	                level = (((level << 1) + 1) * qscale *
	                         ((int) (quant_matrix[j]))) >> 4;
	                level = (level - 1) | 1;
	            }
	            block[j] = (short)level;
	        }
	    }
    }
    
    void dct_unquantize_mpeg2_intra(short [] block/*align 16*/, int n, int qscale){
        int i, level, nCoeffs;
	    int [] quant_matrix;

        if (get_alternate_scan() != 0) nCoeffs= 63;
        else nCoeffs = get_block_last_index()[n];

        if (n < 4)
	        block[0] = (short)(block[0] * get_y_dc_scale());
	    else
	        block[0] = (short)(block[0] * get_c_dc_scale());
        
        quant_matrix = get_intra_matrix();
        for(i = 1 ; i <= nCoeffs ; i++) {
	        int j = get_intra_scantable().get_permutated()[i];
            level = block[j];
	        if (level != 0) {
                if (level < 0) {
                    level = -level;
                    level = (int)(level * qscale * quant_matrix[j]) >> 3;
                    level = -level;
                } else {
                    level = (int)(level * qscale * quant_matrix[j]) >> 3;
                }
	            block[j] = (short)level;
            }
        }    	
    }
    
    void dct_unquantize_mpeg2_inter(short [] block/*align 16*/, int n, int qscale) {
        int i, level, nCoeffs;
	    int [] quant_matrix;
        int sum =-1;

        if (get_alternate_scan() != 0) nCoeffs= 63;
        else nCoeffs = get_block_last_index()[n];

        quant_matrix = get_intra_matrix();
        for(i = 1 ; i <= nCoeffs ; i++) {
	        int j = get_intra_scantable().get_permutated()[i];
            level = block[j];
            if (level != 0) {
                if (level < 0) {
                    level = -level;
                    level = (((level << 1) + 1) * qscale *
                             ((int) (quant_matrix[j]))) >> 4;
                    level = -level;
                } else {
                    level = (((level << 1) + 1) * qscale *
                             ((int) (quant_matrix[j]))) >> 4;
                }
	            block[j] = (short)level;
                sum+=level;
            }
        }
        block[63] ^= sum&1;
    	
    }
    
    void dct_unquantize_h263_intra(short [] block/*align 16*/, int n, int qscale) {

        int i, level, qmul, qadd;
        int nCoeffs;

//        assert(s->block_last_index[n]>=0);

        qmul = qscale << 1;

        if (get_h263_aic() == 0) {
            if (n < 4)
                block[0] = (short)(block[0] * get_y_dc_scale());
            else
                block[0] = (short)(block[0] * get_c_dc_scale());
            qadd = (qscale - 1) | 1;
        }else{
            qadd = 0;
        }
        if (get_ac_pred() != 0)
            nCoeffs = 63;
        else
            nCoeffs = get_inter_scantable().get_raster_end()[get_block_last_index()[n] ];

        for (i = 1 ; i <= nCoeffs ; i++) {
            level = block[i];
            if (level != 0) {
                if (level < 0) {
                    level = level * qmul - qadd;
                } else {
                    level = level * qmul + qadd;
                }
                block[i] = (short)level;
            }
        }
    	
    }
    
    void dct_unquantize_h263_inter(short [] block/*align 16*/, int n, int qscale) {
    	int i, level, qmul, qadd;
	    int nCoeffs;
	
	   // assert(s->block_last_index[n]>=0);
	
	    qadd = (qscale - 1) | 1;
	    qmul = qscale << 1;
	
	    nCoeffs = get_inter_scantable().get_raster_end()[get_block_last_index()[n]];
	
	    for(i = 0 ; i <= nCoeffs ; i++) {
	        level = block[i];
	        if (level != 0) {
	            if (level < 0) {
	                level = level * qmul - qadd;
	            } else {
	                level = level * qmul + qadd;
	            }
	            block[i] = (short)level;
	        }
	    }
    	
    }
    
    void dct_unquantize_h261_intra(short [] block/*align 16*/, int n, int qscale) {
    	
    }
    
    void dct_unquantize_h261_inter(short [] block/*align 16*/, int n, int qscale) {
    	
    }
    
    
    int dct_quantize(short []block/*align 16*/, int n, int qscale, int overflow) {
    	return -1;
//    	 int i, j, level, last_non_zero, q, start_i;
//    	    const int *qmat;
//    	    const uint8_t *scantable= get_intra_scantable.scantable;
//    	    int bias;
//    	    int max=0;
//    	    unsigned int threshold1, threshold2;
//
//    	    get_dsp.fdct (block);
//
//    	    if(get_dct_error_sum)
//    	        get_denoise_dct(s, block);
//
//    	    if (get_mb_intra) {
//    	        if (!get_h263_aic) {
//    	            if (n < 4)
//    	                q = get_y_dc_scale;
//    	            else
//    	                q = get_c_dc_scale;
//    	            q = q << 3;
//    	        } else
//    	            /* For AIC we skip quant/dequant of INTRADC */
//    	            q = 1 << 3;
//
//    	        /* note: block[0] is assumed to be positive */
//    	        block[0] = (block[0] + (q >> 1)) / q;
//    	        start_i = 1;
//    	        last_non_zero = 0;
//    	        qmat = get_q_intra_matrix[qscale];
//    	        bias= get_intra_quant_bias<<(QMAT_SHIFT - QUANT_BIAS_SHIFT);
//    	    } else {
//    	        start_i = 0;
//    	        last_non_zero = -1;
//    	        qmat = get_q_inter_matrix[qscale];
//    	        bias= get_inter_quant_bias<<(QMAT_SHIFT - QUANT_BIAS_SHIFT);
//    	    }
//    	    threshold1= (1<<QMAT_SHIFT) - bias - 1;
//    	    threshold2= (threshold1<<1);
//    	    for(i=63;i>=start_i;i--) {
//    	        j = scantable[i];
//    	        level = block[j] * qmat[j];
//
//    	        if(((unsigned)(level+threshold1))>threshold2){
//    	            last_non_zero = i;
//    	            break;
//    	        }else{
//    	            block[j]=0;
//    	        }
//    	    }
//    	    for(i=start_i; i<=last_non_zero; i++) {
//    	        j = scantable[i];
//    	        level = block[j] * qmat[j];
//
////    	        if(   bias+level >= (1<<QMAT_SHIFT)
////    	           || bias-level >= (1<<QMAT_SHIFT)){
//    	        if(((unsigned)(level+threshold1))>threshold2){
//    	            if(level>0){
//    	                level= (bias + level)>>QMAT_SHIFT;
//    	                block[j]= level;
//    	            }else{
//    	                level= (bias - level)>>QMAT_SHIFT;
//    	                block[j]= -level;
//    	            }
//    	            max |=level;
//    	        }else{
//    	            block[j]=0;
//    	        }
//    	    }
//    	    *overflow= get_max_qcoeff < max; //overflow might have happened
//
//    	    /* we need this permutation so that we correct the IDCT, we only permute the !=0 elements */
//    	    if (get_dsp().get_idct_permutation()_type != FF_NO_IDCT_PERM)
//    	        ff_block_permute(block, get_dsp().get_idct_permutation(), scantable, last_non_zero);
//
//    	    return last_non_zero;

    }
    
    int fast_dct_quantize(short [] block/*align 16*/, int n, int qscale, int overflow)
    {
    	return dct_quantize(block, n, qscale, overflow);
    }
    
    void denoise_dct(short [] block) { 
//    	const int intra= get_mb_intra;
//	    int i;
//	
//	    get_dct_count[intra]++;
//	
//	    for(i=0; i<64; i++){
//	        int level= block[i];
//	
//	        if(level){
//	            if(level>0){
//	                get_dct_error_sum[intra][i] += level;
//	                level -= get_dct_offset[intra][i];
//	                if(level<0) level=0;
//	            }else{
//	                get_dct_error_sum[intra][i] -= level;
//	                level += get_dct_offset[intra][i];
//	                if(level>0) level=0;
//	            }
//	            block[i]= level;
//	        }
//	    }
	    
    }  
    
	

	public void MPV_common_defaults() {
		set_y_dc_scale(0);
	    set_y_dc_scale_table(MpegVideo.ff_mpeg1_dc_scale_table);
		set_c_dc_scale_table(MpegVideo.ff_mpeg1_dc_scale_table);
		set_chroma_qscale_table(MpegVideo.ff_default_chroma_qscale_table);
		set_progressive_frame(1);
		set_progressive_sequence(1);
		set_picture_structure(MpegVideo.PICT_FRAME);
		set_coded_picture_number(0);
		set_picture_number(0);
		set_input_picture_number(0);
		set_picture_in_gop_number(0);
		set_f_code(1);
		set_b_code(1);
		set_picture_range_start(0);
		set_picture_range_end(MpegVideo.MAX_PICTURE_COUNT);
	}
	

	void MPVDecodeDefaults(){
	    MPV_common_defaults();
	}

	public int ff_mjpeg_encode_init() {  
		MJpegContext m = new MJpegContext();
		
		set_min_qcoeff(-1023);
		set_max_qcoeff(1023);

	    /* build all the huffman tables */
	    Mjpeg.ff_mjpeg_build_huffman_codes(m.get_huff_size_dc_luminance(),
		                                   m.get_huff_code_dc_luminance(),
		                                   Mjpeg.ff_mjpeg_bits_dc_luminance,
		                                   Mjpeg.ff_mjpeg_val_dc);
	    Mjpeg.ff_mjpeg_build_huffman_codes(m.get_huff_size_dc_chrominance(),
	                                       m.get_huff_code_dc_chrominance(),
	                                       Mjpeg.ff_mjpeg_bits_dc_chrominance,
	                                       Mjpeg.ff_mjpeg_val_dc);
	    Mjpeg.ff_mjpeg_build_huffman_codes(m.get_huff_size_ac_luminance(),
			                               m.get_huff_code_ac_luminance(),
			                               Mjpeg.ff_mjpeg_bits_ac_luminance,
			                               Mjpeg.ff_mjpeg_val_ac_luminance);
	    Mjpeg.ff_mjpeg_build_huffman_codes(m.get_huff_size_ac_chrominance(),
	                                 	   m.get_huff_code_ac_chrominance(),
	                                 	   Mjpeg.ff_mjpeg_bits_ac_chrominance,
	                                 	   Mjpeg.ff_mjpeg_val_ac_chrominance);
	
	    set_mjpeg_ctx(m);
	    return 0;
	}
	

	/**
	 * init common structure for both encoder and decoder.
	 * this assumes that some variables like width/height are already set
	 */
	public int MPV_common_init() {
	   int y_size, c_size, yc_size, i, mb_array_size, mv_table_size, x, y,
	   threads = ( (get_encoding() != 0) ||
	               ( (get_avctx().get_active_thread_type() & AVCodec.FF_THREAD_SLICE) != 0 )) ?
	                  get_avctx().get_thread_count() : 1;

	    if ( (get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO) && 
	    	 (get_progressive_sequence() == 0) )
	        set_mb_height((get_height() + 31) / 32 * 2);
	    else if (get_codec_id() != CodecID.CODEC_ID_H264)
	    	set_mb_height((get_height() + 15) / 16);

	    if (get_avctx().get_pix_fmt() == PixelFormat.PIX_FMT_NONE){
	        Log.av_log("mpegEncCtx", Log.AV_LOG_WARNING, "decoding to PIX_FMT_NONE is not supported.\n");
	        return -1;	        
	    }

	    if ( ( (get_encoding() != 0) || ( (get_avctx().get_active_thread_type() & AVCodec.FF_THREAD_SLICE) != 0 ) ) &&
	         ( (get_avctx().get_thread_count() > MpegVideo.MAX_THREADS) || ((get_avctx().get_thread_count() > get_mb_height()) && (get_mb_height() != 0))) ) {
	        int max_threads = (int)Mathematics.FFMIN(MpegVideo.MAX_THREADS, get_mb_height());
	        Log.av_log("mpegEncCtx", Log.AV_LOG_WARNING, "too many threads (%d), reducing to %d",
    				get_avctx().get_thread_count(), max_threads);
	        threads = max_threads;
	    }

	    if ( ( (get_width() != 0) || (get_height() != 0) ) && 
	    	   (ImgUtils.av_image_check_size(get_width(), get_height(), 0, get_avctx()) != 0) )
	        return -1;

	    get_dsp().dsputil_init(get_avctx());
	    ff_dct_common_init();

	    set_flags(get_avctx().get_flags());
	    set_flags2(get_avctx().get_flags2());

	    set_mb_width((get_width()  + 15) / 16);
	    set_mb_stride(get_mb_width() + 1);
	    set_b8_stride(get_mb_width() * 2 + 1);
	    set_b4_stride(get_mb_width() * 4 + 1);
	    mb_array_size = get_mb_height() * get_mb_stride();
	    mv_table_size = (get_mb_height() + 2) * get_mb_stride() + 1;

	    /* set chroma shifts */
	    OutII tmp = ImgConvert.avcodec_get_chroma_sub_sample(get_avctx().get_pix_fmt());
	    set_chroma_x_shift(tmp.get_val1());
	    set_chroma_y_shift(tmp.get_val2());

	    /* set default edge pos, will be overriden in decode_header if needed */
	    set_h_edge_pos(get_mb_width() * 16);
	    set_v_edge_pos(get_mb_height() * 16);

	    set_mb_num(get_mb_width() * get_mb_height());

	    this.block_wrap[0] = get_b8_stride();
	    this.block_wrap[1] = get_b8_stride();
	    this.block_wrap[2] = get_b8_stride();
	    this.block_wrap[3] = get_b8_stride();
	    this.block_wrap[4] = get_mb_stride();
    	this.block_wrap[5] = get_mb_stride();

	    y_size = get_b8_stride() * (2 * get_mb_height() + 1);
	    c_size = get_mb_stride() * (get_mb_height() + 1);
	    yc_size = y_size + 2 * c_size;

	    /* convert fourcc to upper case */
	    set_codec_tag(UtilsCodec.ff_toupper4(get_avctx().get_codec_tag()));

	    set_stream_codec_tag(UtilsCodec.ff_toupper4(get_avctx().get_stream_codec_tag()));

	    get_avctx().set_coded_frame((AVFrame)get_current_picture());

	    this.mb_index2xy = new int[get_mb_num()+1];
	    for (y = 0 ; y < get_mb_height() ; y++) {
	        for (x = 0 ; x < get_mb_width() ; x++) {
	            this.mb_index2xy[x + y * get_mb_width() ] = x + y * get_mb_stride();
	        }
	    }
	    this.mb_index2xy[get_mb_height() * get_mb_width() ] = (get_mb_height() - 1) * get_mb_stride() + get_mb_width(); //FIXME really needed?

	    if (get_encoding() != 0) {
	        /* Allocate MV tables */
	        this.p_mv_table_base            = new int[mv_table_size];
	        this.b_forw_mv_table_base       = new int[mv_table_size];
	        this.b_back_mv_table_base       = new int[mv_table_size];
	        this.b_bidir_forw_mv_table_base = new int[mv_table_size];
	        this.b_bidir_back_mv_table_base = new int[mv_table_size];
	        this.b_direct_mv_table_base     = new int[mv_table_size];
	        this.p_mv_table                 = new int[mv_table_size];
	        this.b_forw_mv_table            = new int[mv_table_size];
	        this.b_back_mv_table            = new int[mv_table_size];
	        this.b_bidir_forw_mv_table      = new int[mv_table_size];
	        this.b_bidir_back_mv_table      = new int[mv_table_size];
	        this.b_direct_mv_table          = new int[mv_table_size];

	        if (get_msmpeg4_version() != 0){
	            this.ac_stats = new int[2][2][RLTable.MAX_LEVEL+1][(RLTable.MAX_RUN+1)][2];
	        }

	        avctx.set_stats_out("");
	        
	        /* Allocate MB type table */
	        this.mb_type = new int[mb_array_size];

	        this.lambda_table = new int[mb_array_size];

	        this.q_intra_matrix = new int[64*32];
	        this.q_inter_matrix  = new int[64*32];
	        this.q_intra_matrix16 = new int[2][64*32];
	        this.q_inter_matrix16 = new int[2][64*32];
	        this.input_picture = new Picture[MpegVideo.MAX_PICTURE_COUNT];
	        this.reordered_input_picture = new Picture[MpegVideo.MAX_PICTURE_COUNT];


	        if (get_avctx().get_noise_reduction() != 0){
	            this.dct_offset = new int[64][2];
	        }
	    }

	    set_picture_count(MpegVideo.MAX_PICTURE_COUNT * (int)Mathematics.FFMAX(1, get_avctx().get_thread_count()));
	    set_picture(new Picture[get_picture_count()]);
	    for(i = 0 ; i < get_picture_count() ; i++) {
	    	set_picture(i, Mpeg12.avcodec_get_frame_defaults());
	    }

	   this.error_status_table = new byte[mb_array_size];

	    if ( (get_codec_id() == CodecID.CODEC_ID_MPEG4) || 
	    	 (has_flag(AVCodec.CODEC_FLAG_INTERLACED_ME)) ) {
	        /* interlaced direct mode decoding tables */
	    	this.b_field_mv_table = new int [2][2][2][mv_table_size];
	    	this.b_field_mv_table_base = new int [2][2][2][mv_table_size];
	    	this.b_field_select_table = new byte[2][2][mb_array_size];
	    	this.p_field_mv_table_base = new int[2][2][mv_table_size];
	    	this.p_field_mv_table = new int[2][2][mv_table_size];
	    	this.p_field_select_table = new byte[2][mb_array_size];
	    }
	    	
	    if (get_out_format() == OutputFormat.FMT_H263) {
	        /* cbp values */
	        this.coded_block_base = new byte[y_size];
	        this.coded_block = new byte[y_size];

	        /* cbp, ac_pred, pred_dir */
	        this.cbp_table = new byte[mb_array_size];
	        this.pred_dir_table = new byte[mb_array_size];
	    }

	    if ( (get_h263_pred() != 0) || (get_h263_plus() != 0) || (get_encoding() == 0) ) {
	        /* dc values */
	        //MN: we need these for error resilience of intra-frames
	        this.dc_val_base = new int[yc_size];
	        /*get_dc_val[0] = get_dc_val_base + get_b8_stride + 1;
	        get_dc_val[1] = get_dc_val_base + y_size + get_mb_stride + 1;
	        get_dc_val[2] = get_dc_val[1] + c_size;*/
	        for (i = 0 ; i < yc_size ; i++)
	            this.dc_val_base[i] = 1024;
	    }

	    /* which mb is a intra block */
	    this.mbintra_table = new byte[mb_array_size];
        for (i = 0 ; i < mb_array_size ; i++)
            this.mbintra_table[i] = 1;

	    /* init macroblock skip table */
        this.mbskip_table = new byte[mb_array_size+2];
	    //Note the +1 is for a quicker mpeg4 slice_end detection
        this.prev_pict_types = new AVPictureType[MpegVideo.PREV_PICT_TYPES_BUFFER_SIZE];

	    get_parse_context().set_state(get_parse_context().get_state() -1);
	    /*if((get_avctx()->debug&(FF_DEBUG_VIS_QP|FF_DEBUG_VIS_MB_TYPE)) || (get_avctx()->debug_mv)){
	       get_visualization_buffer[0] = av_malloc((get_mb_width*16 + 2*EDGE_WIDTH) * get_mb_height*16 + 2*EDGE_WIDTH);
	       get_visualization_buffer[1] = av_malloc((get_mb_width*16 + 2*EDGE_WIDTH) * get_mb_height*16 + 2*EDGE_WIDTH);
	       get_visualization_buffer[2] = av_malloc((get_mb_width*16 + 2*EDGE_WIDTH) * get_mb_height*16 + 2*EDGE_WIDTH);
	    }*/

	    set_context_initialized(1);
	    this.thread_context = new ArrayList<MpegEncContext>();
	    this.thread_context.add(this);

	    if ( (get_encoding() != 0) || ( (get_avctx().get_active_thread_type() & AVCodec.FF_THREAD_SLICE) != 0 ) ) {
	        for (i = 1 ; i < threads ; i++){
	    	    this.thread_context.add(this); // TODO: make a copy ?
	        }

	        for (i = 0 ; i < threads ; i++){
	            /*if(init_duplicate_context(get_thread_context[i], s) < 0)
	                goto fail;*/
	            get_thread_context(i).set_start_mb_y( (get_mb_height() * (i  ) + get_avctx().get_thread_count() / 2) / get_avctx().get_thread_count());
	            get_thread_context(i).set_end_mb_y  ( (get_mb_height() * (i+1) + get_avctx().get_thread_count() / 2) / get_avctx().get_thread_count());
	        }
	    } else {
	        /*if(init_duplicate_context(s, s) < 0) goto fail;*/
	        set_start_mb_y(0);
	        set_end_mb_y(get_mb_height());

	    }

	    return 0;
	}

	private int ff_dct_common_init() { 
	    //if (has_flag(AVCodec.CODEC_FLAG_BITEXACT))
	    //    set_dct_unquantize_mpeg2_intra(dct_unquantize_mpeg2_intra_bitexact);
	
	    //MPV_common_init_mmx();
	
	    /* load & permutate scantables
	       note: only wmv uses different ones
	    */
	    if (get_alternate_scan() != 0){
	    	DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_inter_scantable(), DspUtil.ff_alternate_vertical_scan);
	    	DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_intra_scantable(), DspUtil.ff_alternate_vertical_scan);
	    }else{
	    	DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_inter_scantable(), DspUtil.ff_zigzag_direct);
	    	DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_intra_scantable(), DspUtil.ff_zigzag_direct);
	    }
	    DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_intra_h_scantable(), DspUtil.ff_alternate_horizontal_scan);
	    DspUtil.ff_init_scantable(get_dsp().get_idct_permutation(), get_intra_v_scantable(), DspUtil.ff_alternate_vertical_scan);
	
	    return 0;
		
	}

	public void ff_h261_encode_init() {
	    if (done == 0) {
	        done = 1;
	        MpegVideo.init_rl(H261Data.h261_rl_tcoeff, H261.ff_h261_rl_table_store);
	    }

	    set_min_qcoeff(-127);
	    set_max_qcoeff(127);
	    set_y_dc_scale_table(MpegVideo.ff_mpeg1_dc_scale_table);
	    set_c_dc_scale_table(MpegVideo.ff_mpeg1_dc_scale_table);
		
	}

	public void h263_encode_init() {
/*
	    static int done = 0;

	    if (!done) {
	        done = 1;

	        init_rl(&ff_h263_rl_inter, ff_h263_static_rl_table_store[0]);
	        init_rl(&rl_intra_aic, ff_h263_static_rl_table_store[1]);

	        init_uni_h263_rl_tab(&rl_intra_aic, NULL, uni_h263_intra_aic_rl_len);
	        init_uni_h263_rl_tab(&ff_h263_rl_inter    , NULL, uni_h263_inter_rl_len);

	        init_mv_penalty_and_fcode(s);
	    }
	    s->me.mv_penalty= mv_penalty; //FIXME exact table for msmpeg4 & h263p

	    s->intra_ac_vlc_length     =s->inter_ac_vlc_length     = uni_h263_inter_rl_len;
	    s->intra_ac_vlc_last_length=s->inter_ac_vlc_last_length= uni_h263_inter_rl_len + 128*64;
	    if(s->h263_aic){
	        s->intra_ac_vlc_length     = uni_h263_intra_aic_rl_len;
	        s->intra_ac_vlc_last_length= uni_h263_intra_aic_rl_len + 128*64;
	    }
	    s->ac_esc_length= 7+1+6+8;

	    // use fcodes >1 only for mpeg4 & h263 & h263p FIXME
	    switch(s->codec_id){
	    case CODEC_ID_MPEG4:
	        s->fcode_tab= fcode_tab;
	        break;
	    case CODEC_ID_H263P:
	        if(s->umvplus)
	            s->fcode_tab= umv_fcode_tab;
	        if(s->modified_quant){
	            s->min_qcoeff= -2047;
	            s->max_qcoeff=  2047;
	        }else{
	            s->min_qcoeff= -127;
	            s->max_qcoeff=  127;
	        }
	        break;
	        //Note for mpeg4 & h263 the dc-scale table will be set per frame as needed later
	    case CODEC_ID_FLV1:
	        if (s->h263_flv > 1) {
	            s->min_qcoeff= -1023;
	            s->max_qcoeff=  1023;
	        } else {
	            s->min_qcoeff= -127;
	            s->max_qcoeff=  127;
	        }
	        s->y_dc_scale_table=
	        s->c_dc_scale_table= ff_mpeg1_dc_scale_table;
	        break;
	    default: //nothing needed - default table already set in mpegvideo.c
	        s->min_qcoeff= -127;
	        s->max_qcoeff=  127;
	        s->y_dc_scale_table=
	        s->c_dc_scale_table= ff_mpeg1_dc_scale_table;
	    }*/
		
	}

	public void ff_msmpeg4_encode_init() {
		/* static int init_done=0;
		    int i;

		    common_init(s);
		    if(s->msmpeg4_version>=4){
		        s->min_qcoeff= -255;
		        s->max_qcoeff=  255;
		    }

		    if (!init_done) {
		        // init various encoding tables 
		        init_done = 1;
		        init_mv_table(&mv_tables[0]);
		        init_mv_table(&mv_tables[1]);
		        for(i=0;i<NB_RL_TABLES;i++)
		            init_rl(&rl_table[i], static_rl_table_store[i]);

		        for(i=0; i<NB_RL_TABLES; i++){
		            int level;
		            for(level=0; level<=MAX_LEVEL; level++){
		                int run;
		                for(run=0; run<=MAX_RUN; run++){
		                    int last;
		                    for(last=0; last<2; last++){
		                        rl_length[i][level][run][last]= get_size_of_code(s, &rl_table[  i], last, run, level, 0);
		                    }
		                }
		            }
		        }
		    }*/
		
	}

	public void ff_mpeg1_encode_init() {
		/* static int done=0;

		    ff_mpeg12_common_init(s);

		    if(!done){
		        int f_code;
		        int mv;
		        int i;

		        done=1;
		        init_rl(&ff_rl_mpeg1, ff_mpeg12_static_rl_table_store[0]);
		        init_rl(&ff_rl_mpeg2, ff_mpeg12_static_rl_table_store[1]);

		        for(i=0; i<64; i++)
		        {
		                mpeg1_max_level[0][i]= ff_rl_mpeg1.max_level[0][i];
		                mpeg1_index_run[0][i]= ff_rl_mpeg1.index_run[0][i];
		        }

		        init_uni_ac_vlc(&ff_rl_mpeg1, uni_mpeg1_ac_vlc_len);
		        if(s->intra_vlc_format)
		            init_uni_ac_vlc(&ff_rl_mpeg2, uni_mpeg2_ac_vlc_len);

		        /* build unified dc encoding tables 
		        for(i=-255; i<256; i++)
		        {
		                int adiff, index;
		                int bits, code;
		                int diff=i;

		                adiff = FFABS(diff);
		                if(diff<0) diff--;
		                index = av_log2(2*adiff);

		                bits= ff_mpeg12_vlc_dc_lum_bits[index] + index;
		                code= (ff_mpeg12_vlc_dc_lum_code[index]<<index) + (diff & ((1 << index) - 1));
		                mpeg1_lum_dc_uni[i+255]= bits + (code<<8);

		                bits= ff_mpeg12_vlc_dc_chroma_bits[index] + index;
		                code= (ff_mpeg12_vlc_dc_chroma_code[index]<<index) + (diff & ((1 << index) - 1));
		                mpeg1_chr_dc_uni[i+255]= bits + (code<<8);
		        }

		        for(f_code=1; f_code<=MAX_FCODE; f_code++){
		            for(mv=-MAX_MV; mv<=MAX_MV; mv++){
		                int len;

		                if(mv==0) len= ff_mpeg12_mbMotionVectorTable[0][1];
		                else{
		                    int val, bit_size, code;

		                    bit_size = f_code - 1;

		                    val=mv;
		                    if (val < 0)
		                        val = -val;
		                    val--;
		                    code = (val >> bit_size) + 1;
		                    if(code<17){
		                        len= ff_mpeg12_mbMotionVectorTable[code][1] + 1 + bit_size;
		                    }else{
		                        len= ff_mpeg12_mbMotionVectorTable[16][1] + 2 + bit_size;
		                    }
		                }

		                mv_penalty[f_code][mv+MAX_MV]= len;
		            }
		        }


		        for(f_code=MAX_FCODE; f_code>0; f_code--){
		            for(mv=-(8<<f_code); mv<(8<<f_code); mv++){
		                fcode_tab[mv+MAX_MV]= f_code;
		            }
		        }
		    }
		    s->me.mv_penalty= mv_penalty;
		    s->fcode_tab= fcode_tab;
		    if(s->codec_id == CODEC_ID_MPEG1VIDEO){
		        s->min_qcoeff=-255;
		        s->max_qcoeff= 255;
		    }else{
		        s->min_qcoeff=-2047;
		        s->max_qcoeff= 2047;
		    }
		    if (s->intra_vlc_format) {
		        s->intra_ac_vlc_length=
		        s->intra_ac_vlc_last_length= uni_mpeg2_ac_vlc_len;
		    } else {
		        s->intra_ac_vlc_length=
		        s->intra_ac_vlc_last_length= uni_mpeg1_ac_vlc_len;
		    }
		    s->inter_ac_vlc_length=
		    s->inter_ac_vlc_last_length= uni_mpeg1_ac_vlc_len;*/
	}

	public int ff_rate_control_init() {
		RateControlContext rcc = get_rc_context();
	    int i, res;
	    String[] const_names = {
	    	"PI", "E",
	        "iTex",
	        "pTex",
	        "tex",
	        "mv",
	        "fCode",
	        "iCount",
	        "mcVar",
	        "var",
	        "isI",
	        "isP",
	        "isB",
	        "avgQP",
	        "qComp",
	/*        "lastIQP",
	        "lastPQP",
	        "lastBQP",
	        "nextNonBQP",*/
	        "avgIITex",
	        "avgPITex",
	        "avgPPTex",
	        "avgBPTex",
	        "avgTex",
	    };
	  /*  static double (* const func1[])(void *, double)={
	        (void *)bits2qp,
	        (void *)qp2bits,
	        NULL
	    };*/
	    
	    String [] func1_names = {"bits2qp", "qp2bits"};

	    OutOI ret_obj = Eval.av_expr_parse(get_avctx().get_rc_eq() != null ? get_avctx().get_rc_eq(): "tex^qComp", 
	    		const_names, func1_names, null, null, null, 0, "AVCodecContext");
	    rcc.set_rc_eq_eval((AVExpr) ret_obj.get_obj());
		res = ret_obj.get_ret();
		
	    if (res < 0) {
	    	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "Error parsing rc_eq \"%s\"\n", get_avctx().get_rc_eq());
	        return res;
	    }

	    for (i = 0 ; i < 5 ; i++) {
	    	rcc.set_pred(i, new Predictor());
	        rcc.get_pred(i).set_coeff(AVUtil.FF_QP2LAMBDA * 7.0);
	        rcc.get_pred(i).set_count(1.0);

	        rcc.get_pred(i).set_decay(0.4);
	        rcc.set_i_cplx_sum(i, 1);
	        rcc.set_p_cplx_sum(i, 1);
	        rcc.set_mv_bits_sum(i, 1);
	        rcc.set_qscale_sum(i, 1);
	        rcc.set_frame_count(i, 1); // 1 is better because of 1/0 and such
	        rcc.set_last_qscale_for(i, AVUtil.FF_QP2LAMBDA * 5);
	    }
	    rcc.set_buffer_index(get_avctx().get_rc_initial_buffer_occupancy());

	   /* if (has_flag(AVCodec.CODEC_FLAG_PASS2)) {
	        char *p;

	        /* find number of pics 
	        p= s->avctx->stats_in;
	        for(i=-1; p; i++){
	            p= strchr(p+1, ';');
	        }
	        i+= s->max_b_frames;
	        if(i<=0 || i>=INT_MAX / sizeof(RateControlEntry))
	            return -1;
	        rcc->entry = av_mallocz(i*sizeof(RateControlEntry));
	        rcc->num_entries= i;

	        /* init all to skipped p frames (with b frames we might have a not encoded frame at the end FIXME) 
	        for(i=0; i<rcc->num_entries; i++){
	            RateControlEntry *rce= &rcc->entry[i];
	            rce->pict_type= rce->new_pict_type=AV_PICTURE_TYPE_P;
	            rce->qscale= rce->new_qscale=FF_QP2LAMBDA * 2;
	            rce->misc_bits= s->mb_num + 10;
	            rce->mb_var_sum= s->mb_num*100;
	        }

	        /* read stats 
	        p= s->avctx->stats_in;
	        for(i=0; i<rcc->num_entries - s->max_b_frames; i++){
	            RateControlEntry *rce;
	            int picture_number;
	            int e;
	            char *next;

	            next= strchr(p, ';');
	            if(next){
	                (*next)=0; //sscanf in unbelievably slow on looong strings //FIXME copy / do not write
	                next++;
	            }
	            e= sscanf(p, " in:%d ", &picture_number);

	            assert(picture_number >= 0);
	            assert(picture_number < rcc->num_entries);
	            rce= &rcc->entry[picture_number];

	            e+=sscanf(p, " in:%*d out:%*d type:%d q:%f itex:%d ptex:%d mv:%d misc:%d fcode:%d bcode:%d mc-var:%d var:%d icount:%d skipcount:%d hbits:%d",
	                   &rce->pict_type, &rce->qscale, &rce->i_tex_bits, &rce->p_tex_bits, &rce->mv_bits, &rce->misc_bits,
	                   &rce->f_code, &rce->b_code, &rce->mc_mb_var_sum, &rce->mb_var_sum, &rce->i_count, &rce->skip_count, &rce->header_bits);
	            if(e!=14){
	                av_log(s->avctx, AV_LOG_ERROR, "statistics are damaged at line %d, parser out=%d\n", i, e);
	                return -1;
	            }

	            p= next;
	        }

	        if(init_pass2(s) < 0) return -1;

	        //FIXME maybe move to end
	        if((s->flags&CODEC_FLAG_PASS2) && s->avctx->rc_strategy == FF_RC_STRATEGY_XVID) {
	#if CONFIG_LIBXVID
	            return ff_xvid_rate_control_init(s);
	#else
	            av_log(s->avctx, AV_LOG_ERROR, "Xvid ratecontrol requires libavcodec compiled with Xvid support.\n");
	            return -1;
	#endif
	        }
	    }

	    if(!(s->flags&CODEC_FLAG_PASS2)){

	        rcc->short_term_qsum=0.001;
	        rcc->short_term_qcount=0.001;

	        rcc->pass1_rc_eq_output_sum= 0.001;
	        rcc->pass1_wanted_bits=0.001;

	        if(s->avctx->qblur > 1.0){
	            av_log(s->avctx, AV_LOG_ERROR, "qblur too large\n");
	            return -1;
	        }
	        /* init stuff with the user specified complexity 
	        if(s->avctx->rc_initial_cplx){
	            for(i=0; i<60*30; i++){
	                double bits= s->avctx->rc_initial_cplx * (i/10000.0 + 1.0)*s->mb_num;
	                RateControlEntry rce;

	                if     (i%((s->gop_size+3)/4)==0) rce.pict_type= AV_PICTURE_TYPE_I;
	                else if(i%(s->max_b_frames+1))    rce.pict_type= AV_PICTURE_TYPE_B;
	                else                              rce.pict_type= AV_PICTURE_TYPE_P;

	                rce.new_pict_type= rce.pict_type;
	                rce.mc_mb_var_sum= bits*s->mb_num/100000;
	                rce.mb_var_sum   = s->mb_num;
	                rce.qscale   = FF_QP2LAMBDA * 2;
	                rce.f_code   = 2;
	                rce.b_code   = 1;
	                rce.misc_bits= 1;

	                if(s->pict_type== AV_PICTURE_TYPE_I){
	                    rce.i_count   = s->mb_num;
	                    rce.i_tex_bits= bits;
	                    rce.p_tex_bits= 0;
	                    rce.mv_bits= 0;
	                }else{
	                    rce.i_count   = 0; //FIXME we do know this approx
	                    rce.i_tex_bits= 0;
	                    rce.p_tex_bits= bits*0.9;
	                    rce.mv_bits= bits*0.1;
	                }
	                rcc->i_cplx_sum [rce.pict_type] += rce.i_tex_bits*rce.qscale;
	                rcc->p_cplx_sum [rce.pict_type] += rce.p_tex_bits*rce.qscale;
	                rcc->mv_bits_sum[rce.pict_type] += rce.mv_bits;
	                rcc->frame_count[rce.pict_type] ++;

	                get_qscale(s, &rce, rcc->pass1_wanted_bits/rcc->pass1_rc_eq_output_sum, i);
	                rcc->pass1_wanted_bits+= s->bit_rate/(1/av_q2d(s->avctx->time_base)); //FIXME misbehaves a little for variable fps
	            }
	        }

	    }

*/
	    return 0;
		
	}

	public void ff_rate_control_uninit() {
	    RateControlContext rcc = get_rc_context();

	    rcc.get_rc_eq_eval().av_expr_free();
	    rcc.set_entry(null);

	}

	public void MPV_common_end() {
	    int i, j, k;

	   /* if ( (encoding != 0) || 
	    	 (Config.HAVE_THREADS && get_avctx().get_active_thread_type() & AVCodec.FF_THREAD_SLICE)) {
	        for(i=0; i<s->avctx->thread_count; i++){
	            free_duplicate_context(s->thread_context[i]);
	        }
	        for(i=1; i<s->avctx->thread_count; i++){
	            av_freep(&s->thread_context[i]);
	        }
	    } else free_duplicate_context(s);*/

	    parse_context.set_buffer(null);

	    mb_type = null;
	    p_mv_table_base = null;
	    b_forw_mv_table_base = null;
	    b_back_mv_table_base = null;
	    b_bidir_forw_mv_table_base = null;
	    b_bidir_back_mv_table_base = null;
	    b_direct_mv_table_base = null;
	    p_mv_table = null;
	    b_forw_mv_table = null;
	    b_back_mv_table = null;
	    b_bidir_forw_mv_table = null;
	    b_bidir_back_mv_table = null;
	    b_direct_mv_table = null;
	    
	    for(i=0; i<2; i++){
	        for(j=0; j<2; j++){
	            for(k=0; k<2; k++){
	                b_field_mv_table[i][j][k] = null;
	            }
	            b_field_select_table[i][j] = null;
	            p_field_mv_table_base[i][j] = null;
	            p_field_mv_table[i][j] = null;
	        }
	        p_field_select_table[i] = null;
	    }

	    dc_val_base = null;
	    coded_block_base = null;
	    mbintra_table = null;
	    cbp_table = null;
	    pred_dir_table = null;

	    mbskip_table = null;
	    prev_pict_types = null;
	    bitstream_buffer = null;
	    allocated_bitstream_buffer_size = 0;

	    avctx.set_stats_out(null);
	    ac_stats = null;
	    error_status_table = null;
	    mb_index2xy = null;
	    lambda_table = null;
	    q_intra_matrix = null;
	    q_inter_matrix = null;
	    q_intra_matrix16 = null;
	    q_inter_matrix16 = null;
	    input_picture = null;
	    reordered_input_picture = null;
	    dct_offset = null;

	  /*  if ( (picture.size() != 0) && (avctx.get_is_copy() == 0) ){
	        for (i = 0; i<s->picture_count; i++){
	            free_picture(s, &s->picture[i]);
	        }
	    }*/
	    picture = null;
	    context_initialized = 0;
	   // last_picture_ptr = next_picture_ptr = current_picture_ptr = null;
	    linesize = uvlinesize = 0;

	  /*  for(i=0; i<3; i++)
	        av_freep(&s->visualization_buffer[i]);

	    if(!(s->avctx->active_thread_type&FF_THREAD_FRAME))
	        avcodec_default_free_buffers(s->avctx);		*/
	}

	public void ff_mjpeg_encode_close() {
		mjpeg_ctx = null;		
	}

	public MpegEncContext get_thread_context(int i) {
		return thread_context.get(i);
	}

	public Picture get_picture(int i) {
		return picture[i];
	}

	public Picture get_input_picture(int i) {
		return input_picture[i];
	}

	public void set_input_picture(int i, Picture pic) {
		input_picture[i] = pic;		
	}
    
	public void set_dct_unquantize_intra(String dct_unquantize_intra) {
    	this.dct_unquantize_intra = dct_unquantize_intra;
	}

	public void set_dct_unquantize_inter(String dct_unquantize_inter) {
    	this.dct_unquantize_inter = dct_unquantize_inter;
	}
	
	
    public void dct_unquantize_intra(short [] block/*align 16*/, int n, int qscale) {
    	if (dct_unquantize_intra.equals("dct_unquantize_mpeg2_intra"))
    		dct_unquantize_mpeg2_intra(block, n, qscale);
    	else if (dct_unquantize_intra.equals("dct_unquantize_h263_intra"))
        	dct_unquantize_h263_intra(block, n, qscale);
    	else if (dct_unquantize_intra.equals("dct_unquantize_mpeg1_intra"))
    		dct_unquantize_mpeg1_intra(block, n, qscale);
    }
	
    public void dct_unquantize_inter(short [] block/*align 16*/, int n, int qscale) {
    	if (dct_unquantize_intra.equals("dct_unquantize_mpeg2_inter"))
    		dct_unquantize_mpeg2_inter(block, n, qscale);
    	else if (dct_unquantize_intra.equals("dct_unquantize_h263_inter"))
        	dct_unquantize_h263_inter(block, n, qscale);
    	else if (dct_unquantize_intra.equals("dct_unquantize_mpeg1_inter"))
    		dct_unquantize_mpeg1_inter(block, n, qscale);
    }


	



}
