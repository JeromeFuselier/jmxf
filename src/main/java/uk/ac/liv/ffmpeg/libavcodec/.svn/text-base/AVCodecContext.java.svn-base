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

package uk.ac.liv.ffmpeg.libavcodec;

import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.ChannelLayout;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVAudioServiceType;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVChromaLocation;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVColorPrimaries;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVColorRange;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVColorSpace;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVColorTransferCharacteristic;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.Motion_Est_ID;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;
import uk.ac.liv.ffmpeg.libavformat.AVDictionary;
import uk.ac.liv.ffmpeg.libavformat.UtilsFormat;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.AudioConvert;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOII;


public class AVCodecContext extends AVContext {
	

	public static AVCodecContext avcodec_alloc_context2(AVMediaType media_type) {
    	AVCodecContext codecContext = new AVCodecContext();
    	codecContext.avcodec_get_context_defaults2(media_type);		
    	return codecContext;
	}	

	public static AVCodecContext allocContext3(AVCodec codec){
	    AVCodecContext avctx = new AVCodecContext();
	    
	    if (avctx.avcodec_get_context_defaults3(codec) < 0) {
	        return null;
	    }

	    return avctx;
	}

	
	public static AVCodecContext avcodec_alloc_context3(AVCodec codec) {
		AVCodecContext avctx = new AVCodecContext();
		
		if (avcodec_get_context_defaults3(avctx, codec) < 0)
			return null;
		
		return avctx;
	}

	private static int avcodec_get_context_defaults3(AVCodecContext avctx,
			AVCodec codec) {
		return avctx.avcodec_get_context_defaults3(codec);
	}

	public static AVCodecContext avcodec_alloc_context() {
	    return avcodec_alloc_context2(AVMediaType.AVMEDIA_TYPE_UNKNOWN);
	}
	
	

	
	// the average bitrate
    //   - encoding: Set by user; unused for constant quantizer encoding.
    //   - decoding: Set by libavcodec. 0 or some bitrate if this info is available in the stream.
    int bit_rate; 
    
    // number of bits the bitstream is allowed to diverge from the reference.
    //           the reference can be CBR (for CBR pass1) or VBR (for pass2)
    // - encoding: Set by user; unused for constant quantizer encoding.
    // - decoding: unused
    int bit_rate_tolerance;
   
    // CODEC_FLAG_*.
    // - encoding: Set by user.
    // - decoding: Set by user.
    int flags;
  
    // Some codecs need additional format info. It is stored here.
    // If any muxer uses this then ALL demuxers/parsers AND encoders for the
    // specific codec MUST set it correctly otherwise stream copy breaks.
    // In general use of this field by muxers is not recommanded.
    // - encoding: Set by libavcodec.
    // - decoding: Set by libavcodec. 
    int sub_id;
  
    // Motion estimation algorithm used for video coding.
    // 1 (zero), 2 (full), 3 (log), 4 (phods), 5 (epzs), 6 (x1), 7 (hex),
    // 8 (umh), 9 (iter), 10 (tesa) [7, 8, 10 are x264 specific, 9 is snow specific]
    // - encoding: MUST be set by user.
    // - decoding: unused
    //int me_method;
    Motion_Est_ID me_method = Motion_Est_ID.ME_EPZS;
    
    // some codecs need / can use extradata like Huffman tables.
    // mjpeg: Huffman tables
    // rv10: additional flags
    // mpeg4: global headers (they can be in the bitstream or here)
    // The allocated memory should be FF_INPUT_BUFFER_PADDING_SIZE bytes larger
    // than extradata_size to avoid prolems if it is read with the bitstream reader.
    // The bytewise contents of extradata must not depend on the architecture or CPU endianness.
    // - encoding: Set/allocated/freed by libavcodec.
    // - decoding: Set/allocated/freed by user.    
    byte [] extradata = new byte[0];
    
    // This is the fundamental unit of time (in seconds) in terms
    // of which frame timestamps are represented. For fixed-fps content,
    // timebase should be 1/framerate and timestamp increments should be
    // identically 1.
    // - encoding: MUST be set by user.
    // - decoding: Set by libavcodec.
    AVRational time_base = new AVRational();
    
    // video only
    
    // picture width / height.
    // - encoding: MUST be set by user.
    // - decoding: Set by libavcodec.
    // Note: For compatibility it is possible to set this instead of
    // coded_width/height before decoding.
    int width, height;
    
    // the number of pictures in a group of pictures, or 0 for intra_only
    // - encoding: Set by user.
    // - decoding: unused
    int gop_size;
   
    // Pixel format, see PIX_FMT_xxx.
    // May be set by the demuxer if known from headers.
    // May be overriden by the decoder if it knows better.
    // - encoding: Set by user.
    // - decoding: Set by user if known, overridden by libavcodec if known
    PixelFormat pix_fmt = PixelFormat.PIX_FMT_NONE;
   
    // audio only

    int sample_rate; // samples per second
    int channels;    // number of audio channels
  
    // audio sample format
    // - encoding: Set by user.
    // - decoding: Set by libavcodec.
    AVSampleFormat sample_fmt = AVSampleFormat.AV_SAMPLE_FMT_NONE;  // sample format
    
    // The following data should not be initialised.
    //Samples per packet, initialised when calling 'init'.
    int frame_size;
    int frame_number;   // audio or video frame number
   
    // Number of frames the decoded output will be delayed relative to
    // the encoded input.
    // - encoding: Set by libavcodec.
    // - decoding: unused
    int delay;
    
    // - encoding parameters
    float qcompress;  // amount of qscale change between easy & hard scenes (0.0-1.0)
    float qblur;      // amount of qscale smoothing over time (0.0-1.0)
   
    // minimum quantizer
    // - encoding: Set by user.
    // - decoding: unused
    int qmin;

    // maximum quantizer
    // - encoding: Set by user.
    // - decoding: unused
    int qmax;

    // maximum quantizer difference between frames
    // - encoding: Set by user.
    // - decoding: unused
    int max_qdiff;
   
    // maximum number of B-frames between non-B-frames
    // Note: The output will be delayed by max_b_frames+1 relative to the input.
    // - encoding: Set by user.
    // - decoding: unused
    int max_b_frames;

	// qscale factor between IP and B-frames
    //If > 0 then the last P-frame quantizer will be used (q= lastp_q*factor+offset).
    // If < 0 then normal ratecontrol will be done (q= -normal_q*factor+offset).
    // - encoding: Set by user.
    // - decoding: unused
    float b_quant_factor;

    int b_frame_strategy;
    
	AVCodec codec;
    
    // The size of the RTP payload: the coder will 
    // do its best to deliver a chunk with size    
    // below rtp_payload_size, the chunk will start
    // with a start code on some codecs like H.263.
    // This doesn't take account of any particular 
    // headers inside the transmitted RTP payload.
    int rtp_payload_size;

    // statistics, used for 2-pass encoding
    int mv_bits;
    int header_bits;
    int i_tex_bits;
    int p_tex_bits;
    int i_count;
    int p_count;
    int skip_count;
    int misc_bits;
    
    // number of bits used for the previously encoded frame
    // - encoding: Set by libavcodec.
    // - decoding: unused
    int frame_bits;
   
    // Private data of the user, can be used to carry app specific stuff.
    // - encoding: Set by user.
    // - decoding: Set by user.
    Object opaque;
   
	String codec_name;    
    AVMediaType codec_type;
	CodecID codec_id;
	
	// fourcc (LSB first, so "ABCD" -> ('D'<<24) + ('C'<<16) + ('B'<<8) + 'A').
    // This is used to work around some encoder bugs.
    // A demuxer should set this to what is stored in the field used to identify the codec.
    // If there are multiple such fields in a container then the demuxer should choose the one
    // which maximizes the information about the used codec.
    // If the codec tag field in a container is larger then 32 bits then the demuxer should
    // remap the longer ID to 32 bits with a table or other structure. Alternatively a new
    // extra_codec_tag + size could be added but for this a clear advantage must be demonstrated
    // first.
    // - encoding: Set by user, if not then the default based on codec_id will be used.
    // - decoding: Set by user, will be converted to uppercase by libavcodec during init.
    int codec_tag;
    
    // Work around bugs in encoders which sometimes cannot be detected automatically.
    // - encoding: Set by user
    // - decoding: Set by user
    int workaround_bugs;
    
    // luma single coefficient elimination threshold
    // - encoding: Set by user.
    // - decoding: unused
    int luma_elim_threshold;

    // chroma single coeff elimination threshold
    // - encoding: Set by user.
    // - decoding: unused
    int chroma_elim_threshold;
    
    // strictly follow the standard (MPEG4, ...).
    // - encoding: Set by user.
    // - decoding: Set by user.
    // Setting this to STRICT or higher means the encoder and decoder will
    // generally do stupid things, whereas setting it to unofficial or lower
    // will mean the encoder might produce output that is not supported by all
    // spec-compliant decoders. Decoders don't differentiate between normal,
    // unofficial and experimental (that is, they always try to decode things
    // when they can) unless they are explicitly asked to behave stupidly
    // (=strictly conform to the specs)
    int strict_std_compliance;
    
    // qscale offset between IP and B-frames
    // - encoding: Set by user.
    // - decoding: unused
    float b_quant_offset;
    
    // Error recognization; higher values will detect more errors but may
    // misdetect some more or less valid parts as errors.
    // - encoding: unused
    // - decoding: Set by user.
    int error_recognition;
    
    //Size of the frame reordering buffer in the decoder.
    // For MPEG-2 it is 1 IPB or 0 low delay IP.
    // - encoding: Set by libavcodec.
    // - decoding: Set by libavcodec.
    int has_b_frames;
    
    // number of bytes per packet if constant and known or 0
    // Used by some WAV based audio codecs.
    int block_align;
    
    // decoding only: If true, only parsing is done (function 
    // avcodec_parse_frame()). The frame data is returned. Only MPEG codecs 
    // support this now.
    int parse_only;
    
    // 0-> h263 quant 1-> mpeg quant
    // - encoding: Set by user.
    // - decoding: unused
	int mpeg_quant;

    // pass1 encoding statistics output buffer
    // - encoding: Set by libavcodec.
    // - decoding: unused
	String stats_out;

    // pass2 encoding statistics input buffer
    // Concatenated stuff from stats_out of pass1 should be placed here.
    // - encoding: Allocated/set/freed by user.
    // - decoding: unused
    String stats_in;
    
    
    // ratecontrol qmin qmax limiting method
    // 0-> clipping, 1-> use a nice continous function to limit qscale wthin qmin/qmax.
    // - encoding: Set by user.
    // - decoding: unused
    float rc_qsquish;

    float rc_qmod_amp;
    int rc_qmod_freq;
    
    // ratecontrol override, see RcOverride
    // - encoding: Allocated/set/freed by user.
    // - decoding: unused
    RcOverride [] rc_override = new RcOverride[0];
    
    // rate control equation
    // - encoding: Set by user
    // - decoding: unused
    String rc_eq;

    // maximum bitrate
    // - encoding: Set by user.
    // - decoding: unused
    int rc_max_rate;

    // minimum bitrate
    // - encoding: Set by user.
    // - decoding: unused
    int rc_min_rate;
    
    // decoder bitstream buffer size
    // - encoding: Set by user.
    // - decoding: unused
    int rc_buffer_size;
    float rc_buffer_aggressivity;
    
    // qscale factor between P and I-frames
    // If > 0 then the last p frame quantizer will be used (q= lastp_q*factor+offset).
    // If < 0 then normal ratecontrol will be done (q= -normal_q*factor+offset).
    // - encoding: Set by user.
    // - decoding: unused
    float i_quant_factor;

    // qscale offset between P and I-frames
    // - encoding: Set by user.
    // - decoding: unused
    float i_quant_offset;
    
    // initial complexity for pass1 ratecontrol
    // - encoding: Set by user.
    // - decoding: unused
    float rc_initial_cplx;
    
    // DCT algorithm, see FF_DCT_* below
    // - encoding: Set by user.
    // - decoding: unused
    int dct_algo;
    
    // luminance masking (0-> disabled)
    // - encoding: Set by user.
    // - decoding: unused
    float lumi_masking;

    // temporary complexity masking (0-> disabled)
    // - encoding: Set by user.
    // - decoding: unused
    float temporal_cplx_masking;

    // spatial complexity masking (0-> disabled)
    // - encoding: Set by user.
    // - decoding: unused
    float spatial_cplx_masking;
    
    // p block masking (0-> disabled)
    // - encoding: Set by user.
    // - decoding: unused
    float p_masking;

    // darkness masking (0-> disabled)
    // encoding: Set by user.
    // decoding: unused
    float dark_masking;

	// IDCT algorithm, see FF_IDCT_* below.
    // - encoding: Set by user.
    // - decoding: Set by user.
    int idct_algo;
    
    // slice count
    // - encoding: Set by libavcodec.
    // - decoding: Set by user (or 0).
    int slice_count;
   
    // slice offsets in the frame in bytes
    // - encoding: Set/allocated by libavcodec.
    // - decoding: Set/allocated by user (or NULL).
    int slice_offset;

    // error concealment flags
    // - encoding: unused
    // - decoding: Set by user.
    int error_concealment;
    
    // dsp_mask could be add used to disable unwanted CPU features
    // CPU features (i.e. MMX, SSE. ...)
    //
    // With the FORCE flag you may instead enable given CPU features.
    // (Dangerous: Usable in case of misdetection, improper usage however will
    // result into program crash.)
    int dsp_mask;

    // bits per sample/pixel from the demuxer (needed for huffyuv).
    // - encoding: Set by libavcodec.
    // - decoding: Set by user.
    int bits_per_coded_sample;

    // prediction method (needed for huffyuv)
    // - encoding: Set by user.
    // - decoding: unused
    int prediction_method;
    
    // sample aspect ratio (0 if unknown)
    // That is the width of a pixel divided by the height of the pixel.
    // Numerator and denominator must be relatively prime and smaller than 256 for some video standards.
    // - encoding: Set by user.
    // - decoding: Set by libavcodec.
    AVRational sample_aspect_ratio = new AVRational();
    
    // the picture in the bitstream
    // - encoding: Set by libavcodec.
    // - decoding: Set by libavcodec.
    AVFrame coded_frame;
    
    // debug
    // - encoding: Set by user.
    // - decoding: Set by user.
    int debug;
    
    // debug
    // - encoding: Set by user.
    // - decoding: Set by user.
    int debug_mv;
    
    // error
    // - encoding: Set by libavcodec if flags&CODEC_FLAG_PSNR.
    // - decoding: unused
    long [] error = new long[4];
    
    // motion estimation comparison function
    // - encoding: Set by user.
    // - decoding: unused
    int me_cmp;
   
    // subpixel motion estimation comparison function
    // - encoding: Set by user.
    // - decoding: unused
    int me_sub_cmp;
   
    // macroblock comparison function (not supported yet)
    // - encoding: Set by user.
    // - decoding: unused
    int mb_cmp;
    
    // interlaced DCT comparison function
    // - encoding: Set by user.
    // - decoding: unused
    int ildct_cmp;
    
    // ME diamond size & shape
    // - encoding: Set by user.
    // - decoding: unused
    int dia_size;

    // amount of previous MV predictors (2a+1 x 2a+1 square)
    // - encoding: Set by user.
    // - decoding: unused
    int last_predictor_count;

    // prepass for motion estimation
    // - encoding: Set by user.
    // - decoding: unused
    int pre_me;

    // motion estimation prepass comparison function
    // - encoding: Set by user.
    // - decoding: unused
    int me_pre_cmp;

    // ME prepass diamond size & shape
    // - encoding: Set by user.
    // - decoding: unused
    int pre_dia_size;

    // subpel ME quality
    // - encoding: Set by user.
    // - decoding: unused
    int me_subpel_quality;
    
    // DTG active format information (additional aspect ratio
    // information only used in DVB MPEG-2 transport streams)
    // 0 if not set.
    //
    // - encoding: unused
    // - decoding: Set by decoder.
    int dtg_active_format;
    
    // maximum motion estimation search range in subpel units
    // If 0 then no limit.
    // - encoding: Set by user.
    // - decoding: unused
    int me_range;

    // intra quantizer bias
    // - encoding: Set by user.
    // - decoding: unused
    int intra_quant_bias;

    // inter quantizer bias
    // - encoding: Set by user.
    // - decoding: unused
    int inter_quant_bias;
    
    int color_table_id;

    /**
     * internal_buffer count
     * Don't touch, used by libavcodec default_get_buffer().
     */
    int internal_buffer_count;

    /**
     * internal_buffers
     * Don't touch, used by libavcodec default_get_buffer().
     */
    Object [] internal_buffer;
    
    /**
     * Global quality for codecs which cannot change it per frame.
     * This should be proportional to MPEG-1/2/4 qscale.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int global_quality;
    
    /**
     * coder type
     * - encoding: Set by user.
     * - decoding: unused
     */
    int coder_type;

    /**
     * context model
     * - encoding: Set by user.
     * - decoding: unused
     */
    int context_model;
    
    /**
     * slice flags
     * - encoding: unused
     * - decoding: Set by user.
     */
    int slice_flags;
    
    /**
     * XVideo Motion Acceleration
     * - encoding: forbidden
     * - decoding: set by decoder
     */
    int xvmc_acceleration;

    /**
     * macroblock decision mode
     * - encoding: Set by user.
     * - decoding: unused
     */
    int mb_decision;
    
    /**
     * custom intra quantization matrix
     * - encoding: Set by user, can be NULL.
     * - decoding: Set by libavcodec.
     */
    int [] intra_matrix;

    /**
     * custom inter quantization matrix
     * - encoding: Set by user, can be NULL.
     * - decoding: Set by libavcodec.
     */
    int [] inter_matrix;

    /**
     * fourcc from the AVI stream header (LSB first, so "ABCD" -> ('D'<<24) + ('C'<<16) + ('B'<<8) + 'A').
     * This is used to work around some encoder bugs.
     * - encoding: unused
     * - decoding: Set by user, will be converted to uppercase by libavcodec during init.
     */
    int stream_codec_tag;

    /**
     * scene change detection threshold
     * 0 is default, larger means fewer detected scene changes.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int scenechange_threshold;
    
    /**
     * minimum Lagrange multipler
     * - encoding: Set by user.
     * - decoding: unused
     */
    int lmin;

    /**
     * maximum Lagrange multipler
     * - encoding: Set by user.
     * - decoding: unused
     */
    int lmax;
        
    /**
     * noise reduction strength
     * - encoding: Set by user.
     * - decoding: unused
     */
    int noise_reduction;
    
    /**
     * Number of bits which should be loaded into the rc buffer before decoding starts.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int rc_initial_buffer_occupancy;

    /**
     *
     * - encoding: Set by user.
     * - decoding: unused
     */
    int inter_threshold;

    /**
     * CODEC_FLAG2_*
     * - encoding: Set by user.
     * - decoding: Set by user.
     */
    int flags2;

    /**
     * Simulates errors in the bitstream to test error concealment.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int error_rate;
    
    /**
     * quantizer noise shaping
     * - encoding: Set by user.
     * - decoding: unused
     */
    int quantizer_noise_shaping;

    /**
     * thread count
     * is used to decide how many independent tasks should be passed to execute()
     * - encoding: Set by user.
     * - decoding: Set by user.
     */
    int thread_count;
    
    /**
     * thread opaque
     * Can be used by execute() to store some per AVCodecContext stuff.
     * - encoding: set by execute()
     * - decoding: set by execute()
     */
    Object thread_opaque;

    /**
     * Motion estimation threshold below which no motion estimation is
     * performed, but instead the user specified motion vectors are used.
     *
     * - encoding: Set by user.
     * - decoding: unused
     */
    int me_threshold;

    /**
     * Macroblock threshold below which the user specified macroblock types will be used.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int mb_threshold;

    /**
     * precision of the intra DC coefficient - 8
     * - encoding: Set by user.
     * - decoding: unused
     */
    int intra_dc_precision;

    /**
     * noise vs. sse weight for the nsse comparsion function
     * - encoding: Set by user.
     * - decoding: unused
     */
    int nsse_weight;

    /**
     * Number of macroblock rows at the top which are skipped.
     * - encoding: unused
     * - decoding: Set by user.
     */
    int skip_top;

    /**
     * Number of macroblock rows at the bottom which are skipped.
     * - encoding: unused
     * - decoding: Set by user.
     */
    int skip_bottom;

    /**
     * profile
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    int profile;
    
    /**
     * level
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    int level;
    
    /**
     * low resolution decoding, 1-> 1/2 size, 2->1/4 size
     * - encoding: unused
     * - decoding: Set by user.
     */
     int lowres;

    /**
     * Bitstream width / height, may be different from width/height if lowres
     * or other things are used.
     * - encoding: unused
     * - decoding: Set by user before init if known. Codec should override / dynamically change if needed.
     */
    int coded_width, coded_height;

    /**
     * frame skip threshold
     * - encoding: Set by user.
     * - decoding: unused
     */
    int frame_skip_threshold;

    /**
     * frame skip factor
     * - encoding: Set by user.
     * - decoding: unused
     */
    int frame_skip_factor;

    /**
     * frame skip exponent
     * - encoding: Set by user.
     * - decoding: unused
     */
    int frame_skip_exp;

    /**
     * frame skip comparison function
     * - encoding: Set by user.
     * - decoding: unused
     */
    int frame_skip_cmp;

    /**
     * Border processing masking, raises the quantizer for mbs on the borders
     * of the picture.
     * - encoding: Set by user.
     * - decoding: unused
     */
    float border_masking;

    /**
     * minimum MB lagrange multipler
     * - encoding: Set by user.
     * - decoding: unused
     */
    int mb_lmin;

    /**
     * maximum MB lagrange multipler
     * - encoding: Set by user.
     * - decoding: unused
     */
    int mb_lmax;

    /**
     *
     * - encoding: Set by user.
     * - decoding: unused
     */
    int me_penalty_compensation;
    
    /**
     *
     * - encoding: unused
     * - decoding: Set by user.
     */
    AVDiscard skip_loop_filter = AVDiscard.AVDISCARD_DEFAULT;
    
    /**
     *
     * - encoding: unused
     * - decoding: Set by user.
     */
    AVDiscard skip_idct = AVDiscard.AVDISCARD_DEFAULT;

    /**
     *
     * - encoding: unused
     * - decoding: Set by user.
     */
    AVDiscard skip_frame = AVDiscard.AVDISCARD_DEFAULT;

    /**
     *
     * - encoding: Set by user.
     * - decoding: unused
     */
    int bidir_refine;

    /**
     *
     * - encoding: Set by user.
     * - decoding: unused
     */
    int brd_scale;

    /**
     * constant rate factor - quality-based VBR - values ~correspond to qps
     * - encoding: Set by user.
     * - decoding: unused
     */
    float crf;

    /**
     * constant quantization parameter rate control method
     * - encoding: Set by user.
     * - decoding: unused
     */
    int cqp;

    /**
     * minimum GOP size
     * - encoding: Set by user.
     * - decoding: unused
     */
    int keyint_min;

    /**
     * number of reference frames
     * - encoding: Set by user.
     * - decoding: Set by lavc.
     */
    int refs;

    /**
     * chroma qp offset from luma
     * - encoding: Set by user.
     * - decoding: unused
     */
    int chromaoffset;

    /**
     * Influences how often B-frames are used.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int bframebias;

    /**
     * trellis RD quantization
     * - encoding: Set by user.
     * - decoding: unused
     */
    int trellis;

    /**
     * Reduce fluctuations in qp (before curve compression).
     * - encoding: Set by user.
     * - decoding: unused
     */
    float complexityblur;

    /**
     * in-loop deblocking filter alphac0 parameter
     * alpha is in the range -6...6
     * - encoding: Set by user.
     * - decoding: unused
     */
    int deblockalpha;

    /**
     * in-loop deblocking filter beta parameter
     * beta is in the range -6...6
     * - encoding: Set by user.
     * - decoding: unused
     */
    int deblockbeta;

    /**
     * macroblock subpartition sizes to consider - p8x8, p4x4, b8x8, i8x8, i4x4
     * - encoding: Set by user.
     * - decoding: unused
     */
    int partitions;
    

    /**
     * direct MV prediction mode - 0 (none), 1 (spatial), 2 (temporal), 3 (auto)
     * - encoding: Set by user.
     * - decoding: unused
     */
    int directpred;

    /**
     * Audio cutoff bandwidth (0 means "automatic")
     * - encoding: Set by user.
     * - decoding: unused
     */
    int cutoff;

    /**
     * Multiplied by qscale for each frame and added to scene_change_score.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int scenechange_factor;

    /**
     *
     * Note: Value depends upon the compare function used for fullpel ME.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int mv0_threshold;

    /**
     * Adjusts sensitivity of b_frame_strategy 1.
     * - encoding: Set by user.
     * - decoding: unused
     */
    int b_sensitivity;

    /**
     * - encoding: Set by user.
     * - decoding: unused
     */
    int compression_level;
    
    /**
     * - encoding: Set by user.
     * - decoding: unused
     */
    int min_prediction_order;

    /**
     * - encoding: Set by user.
     * - decoding: unused
     */
    int max_prediction_order;
    
    /**
     * GOP timecode frame start number, in non drop frame format
     * - encoding: Set by user.
     * - decoding: unused
     */
    long timecode_frame_start;
    /**
     * Percentage of dynamic range compression to be applied by the decoder.
     * The default value is 1.0, corresponding to full compression.
     * - encoding: unused
     * - decoding: Set by user.
     */
    float drc_scale;
    
    /**
     * opaque 64bit number (generally a PTS) that will be reordered and
     * output in AVFrame.reordered_opaque
     * @deprecated in favor of pkt_pts
     * - encoding: unused
     * - decoding: Set by user.
     */
    long reordered_opaque;

    /**
     * Bits per sample/pixel of internal libavcodec pixel/sample format.
     * This field is applicable only when sample_fmt is AV_SAMPLE_FMT_S32.
     * - encoding: set by user.
     * - decoding: set by libavcodec.
     */
    int bits_per_raw_sample;

    /**
     * Audio channel layout.
     * - encoding: set by user.
     * - decoding: set by user, may be overwritten by libavcodec.
     */
    long channel_layout;

    /**
     * Request decoder to use this channel layout if it can (0 for default)
     * - encoding: unused
     * - decoding: Set by user.
     */
    long request_channel_layout;

    /**
     * Ratecontrol attempt to use, at maximum, <value> of what can be used without an underflow.
     * - encoding: Set by user.
     * - decoding: unused.
     */
    float rc_max_available_vbv_use;

    /**
     * Ratecontrol attempt to use, at least, <value> times the amount needed to prevent a vbv overflow.
     * - encoding: Set by user.
     * - decoding: unused.
     */
    float rc_min_vbv_overflow_use;

    /**
     * Hardware accelerator in use
     * - encoding: unused.
     * - decoding: Set by libavcodec
     */
    AVHWAccel hwaccel;


    /**
     * For some codecs, the time base is closer to the field rate than the frame rate.
     * Most notably, H.264 and MPEG-2 specify time_base as half of frame duration
     * if no telecine is used ...
     *
     * Set to time_base ticks per frame. Default 1, e.g., H.264/MPEG-2 set it to 2.
     */
    int ticks_per_frame;

    /**
     * Hardware accelerator context.
     * For some hardware accelerators, a global context needs to be
     * provided by the user. In that case, this holds display-dependent
     * data FFmpeg cannot instantiate itself. Please refer to the
     * FFmpeg HW accelerator documentation to know how to fill this
     * is. e.g. for VA API, this is a struct vaapi_context.
     * - encoding: unused
     * - decoding: Set by user
     */
    Object hwaccel_context;    

    /**
     * Chromaticity coordinates of the source primaries.
     * - encoding: Set by user
     * - decoding: Set by libavcodec
     */
    AVColorPrimaries color_primaries = AVColorPrimaries.AVCOL_PRI_UNSPECIFIED;

    /**
     * Color Transfer Characteristic.
     * - encoding: Set by user
     * - decoding: Set by libavcodec
     */
    AVColorTransferCharacteristic color_trc = AVColorTransferCharacteristic.AVCOL_TRC_UNSPECIFIED;

    /**
     * YUV colorspace type.
     * - encoding: Set by user
     * - decoding: Set by libavcodec
     */
    AVColorSpace colorspace = AVColorSpace.AVCOL_SPC_UNSPECIFIED;

    /**
     * MPEG vs JPEG YUV range.
     * - encoding: Set by user
     * - decoding: Set by libavcodec
     */
    AVColorRange color_range = AVColorRange.AVCOL_RANGE_UNSPECIFIED;

    /**
     * This defines the location of chroma samples.
     * - encoding: Set by user
     * - decoding: Set by libavcodec
     */
    AVChromaLocation chroma_sample_location = AVChromaLocation.AVCHROMA_LOC_UNSPECIFIED;    

    /**
     * explicit P-frame weighted prediction analysis method
     * 0: off
     * 1: fast blind weighting (one reference duplicate with -1 offset)
     * 2: smart weighting (full fade detection analysis)
     * - encoding: Set by user.
     * - decoding: unused
     */
    int weighted_p_pred;

    /**
     * AQ mode
     * 0: Disabled
     * 1: Variance AQ (complexity mask)
     * 2: Auto-variance AQ (experimental)
     * - encoding: Set by user
     * - decoding: unused
     */
    int aq_mode;

    /**
     * AQ strength
     * Reduces blocking and blurring in flat and textured areas.
     * - encoding: Set by user
     * - decoding: unused
     */
    float aq_strength;

    /**
     * PSY RD
     * Strength of psychovisual optimization
     * - encoding: Set by user
     * - decoding: unused
     */
    float psy_rd;

    /**
     * PSY trellis
     * Strength of psychovisual optimization
     * - encoding: Set by user
     * - decoding: unused
     */
    float psy_trellis;

    /**
     * RC lookahead
     * Number of frames for frametype and ratecontrol lookahead
     * - encoding: Set by user
     * - decoding: unused
     */
    int rc_lookahead;

    /**
     * Constant rate factor maximum
     * With CRF encoding mode and VBV restrictions enabled, prevents quality from being worse
     * than crf_max, even if doing so would violate VBV restrictions.
     * - encoding: Set by user.
     * - decoding: unused
     */
    float crf_max;

    int log_level_offset;

    /**
     * Number of slices.
     * Indicates number of picture subdivisions. Used for parallelized
     * decoding.
     * - encoding: Set by user
     * - decoding: unused
     */
    int slices;

    /**
     * Header containing style information for text subtitles.
     * For SUBTITLE_ASS subtitle type, it should contain the whole ASS
     * [Script Info] and [V4+ Styles] section, plus the [Events] line and
     * the Format line following. It shouldn't include any Dialogue line.
     * - encoding: Set/allocated/freed by user (before avcodec_open())
     * - decoding: Set/allocated/freed by libavcodec (by avcodec_open())
     */
    byte [] subtitle_header;

    /**
     * Current packet as passed into the decoder, to avoid having
     * to pass the packet into every function. Currently only valid
     * inside lavc and get/release_buffer callbacks.
     * - decoding: set by avcodec_decode_*, read by get_buffer() for setting pkt_pts
     * - encoding: unused
     */
    AVPacket pkt;    

    /**
     * Whether this is a copy of the context which had init() called on it.
     * This is used by multithreading - shared tables and picture pointers
     * should be freed from the original context only.
     * - encoding: Set by libavcodec.
     * - decoding: Set by libavcodec.
     */
    int is_copy;

    /**
     * Which multithreading methods to use.
     * Use of FF_THREAD_FRAME will increase decoding delay by one frame per thread,
     * so clients which cannot provide future frames should not use it.
     *
     * - encoding: Set by user, otherwise the default is used.
     * - decoding: Set by user, otherwise the default is used.
     */
    int thread_type;
    
    /**
     * Which multithreading methods are in use by the codec.
     * - encoding: Set by libavcodec.
     * - decoding: Set by libavcodec.
     */
    int active_thread_type;

    /**
     * Set by the client if its custom get_buffer() callback can be called
     * from another thread, which allows faster multithreaded decoding.
     * draw_horiz_band() will be called from other threads regardless of this setting.
     * Ignored if the default get_buffer() is used.
     * - encoding: Set by user.
     * - decoding: Set by user.
     */
    int thread_safe_callbacks;

    /**
     * VBV delay coded in the last frame (in periods of a 27 MHz clock).
     * Used for compliant TS muxing.
     * - encoding: Set by libavcodec.
     * - decoding: unused.
     */
    long vbv_delay;

    /**
     * Type of service that the audio stream conveys.
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    AVAudioServiceType audio_service_type = AVAudioServiceType.AV_AUDIO_SERVICE_TYPE_MAIN;

    /**
     * desired sample format
     * - encoding: Not used.
     * - decoding: Set by user.
     * Decoder will decode to this format if it can.
     */
    AVSampleFormat request_sample_fmt = AVSampleFormat.AV_SAMPLE_FMT_NONE;

    /**
     * Current statistics for PTS correction.
     * - decoding: maintained and used by libavcodec, not intended to be used by user apps
     * - encoding: unused
     */
    long pts_correction_num_faulty_pts; /// Number of incorrect PTS values so far
    long pts_correction_num_faulty_dts; /// Number of incorrect DTS values so far
    long pts_correction_last_pts;       /// PTS of the last frame
    long pts_correction_last_dts;       /// DTS of the last frame


    String get_buffer = "";
    String release_buffer = "";
    String get_format = "";
    String execute = "";
    String execute2 = "";    

	public String get_get_buffer() {
		return get_buffer;
	}

	public void set_get_buffer(String get_buffer) {
		this.get_buffer = get_buffer;
	}

	public String get_release_buffer() {
		return release_buffer;
	}

	public void set_release_buffer(String release_buffer) {
		this.release_buffer = release_buffer;
	}

	public String get_get_format() {
		return get_format;
	}

	public void set_get_format(String get_format) {
		this.get_format = get_format;
	}

	public String get_execute() {
		return execute;
	}

	public void set_execute(String execute) {
		this.execute = execute;
	}

	public String get_execute2() {
		return execute2;
	}

	public void set_execute2(String execute2) {
		this.execute2 = execute2;
	}

	public void set_bit_rate(int bit_rate) {
		this.bit_rate = bit_rate;
	}


	public int get_bit_rate_tolerance() {
		return bit_rate_tolerance;
	}


	public void set_bit_rate_tolerance(int bit_rate_tolerance) {
		this.bit_rate_tolerance = bit_rate_tolerance;
	}

	
	public boolean has_flag(int flag) {
		return (flags & flag) == flag;
	}

	public int get_flags() {
		return flags;
	}


	public void set_flags(int flags) {
		this.flags = flags;
	}


	public int get_sub_id() {
		return sub_id;
	}


	public void set_sub_id(int sub_id) {
		this.sub_id = sub_id;
	}


	public Motion_Est_ID get_me_method() {
		return me_method;
	}


	public void set_me_method(Motion_Est_ID me_method) {
		this.me_method = me_method;
	}


	public byte[] get_extradata() {
		return extradata;
	}


	public void set_extradata(byte[] extradata) {
		this.extradata = Arrays.copyOf(extradata, extradata.length);
	}
	

	public int get_extradata_size() {
		if (extradata != null)
			return extradata.length;
		else
			return 0;
	}


	public AVRational get_time_base() {
		return time_base;
	}


	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
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


	public PixelFormat get_pix_fmt() {
		return pix_fmt;
	}


	public void set_pix_fmt(PixelFormat pix_fmt) {
		this.pix_fmt = pix_fmt;
	}


	public int get_sample_rate() {
		return sample_rate;
	}


	public void set_sample_rate(int sample_rate) {
		this.sample_rate = sample_rate;
	}


	public int get_channels() {
		return channels;
	}


	public void set_channels(int channels) {
		this.channels = channels;
	}


	public AVSampleFormat get_sample_fmt() {
		return sample_fmt;
	}


	public void set_sample_fmt(AVSampleFormat sample_fmt) {
		this.sample_fmt = sample_fmt;
	}


	public int get_frame_size() {
		return frame_size;
	}


	public void set_frame_size(int frame_size) {
		this.frame_size = frame_size;
	}


	public int get_frame_number() {
		return frame_number;
	}


	public void set_frame_number(int frame_number) {
		this.frame_number = frame_number;
	}


	public int get_delay() {
		return delay;
	}


	public void set_delay(int delay) {
		this.delay = delay;
	}


	public float get_qcompress() {
		return qcompress;
	}


	public void set_qcompress(float qcompress) {
		this.qcompress = qcompress;
	}


	public float get_qblur() {
		return qblur;
	}


	public void set_qblur(float qblur) {
		this.qblur = qblur;
	}


	public int get_qmin() {
		return qmin;
	}


	public void set_qmin(int qmin) {
		this.qmin = qmin;
	}


	public int get_qmax() {
		return qmax;
	}


	public void set_qmax(int qmax) {
		this.qmax = qmax;
	}


	public int get_max_qdiff() {
		return max_qdiff;
	}


	public void set_max_qdiff(int max_qdiff) {
		this.max_qdiff = max_qdiff;
	}


	public int get_max_b_frames() {
		return max_b_frames;
	}


	public void set_max_b_frames(int max_b_frames) {
		this.max_b_frames = max_b_frames;
	}


	public float get_b_quant_factor() {
		return b_quant_factor;
	}


	public void set_b_quant_factor(float b_quant_factor) {
		this.b_quant_factor = b_quant_factor;
	}


	public int get_b_frame_strategy() {
		return b_frame_strategy;
	}


	public void set_b_frame_strategy(int b_frame_strategy) {
		this.b_frame_strategy = b_frame_strategy;
	}


	public AVCodec get_codec() {
		return codec;
	}


	public void set_codec(AVCodec codec) {
		this.codec = codec;
	}



	public int get_rtp_payload_size() {
		return rtp_payload_size;
	}


	public void set_rtp_payload_size(int rtp_payload_size) {
		this.rtp_payload_size = rtp_payload_size;
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


	public int get_p_count() {
		return p_count;
	}


	public void set_p_count(int p_count) {
		this.p_count = p_count;
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


	public int get_frame_bits() {
		return frame_bits;
	}


	public void set_frame_bits(int frame_bits) {
		this.frame_bits = frame_bits;
	}


	public Object get_opaque() {
		return opaque;
	}


	public void set_opaque(Object opaque) {
		this.opaque = opaque;
	}


	public String get_codec_name() {
		return codec_name;
	}


	public void set_codec_name(String codec_name) {
		this.codec_name = codec_name;
	}


	public AVMediaType get_codec_type() {
		return codec_type;
	}


	public void set_codec_type(AVMediaType codec_type) {
		this.codec_type = codec_type;
	}


	public CodecID get_codec_id() {
		return codec_id;
	}


	public void set_codec_id(CodecID codec_id) {
		this.codec_id = codec_id;
	}


	public int get_codec_tag() {
		return codec_tag;
	}


	public void set_codec_tag(int codec_tag) {
		this.codec_tag = codec_tag;
	}


	public int get_workaround_bugs() {
		return workaround_bugs;
	}


	public void set_workaround_bugs(int workaround_bugs) {
		this.workaround_bugs = workaround_bugs;
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


	public float get_b_quant_offset() {
		return b_quant_offset;
	}


	public void set_b_quant_offset(float b_quant_offset) {
		this.b_quant_offset = b_quant_offset;
	}


	public int get_error_recognition() {
		return error_recognition;
	}


	public void set_error_recognition(int error_recognition) {
		this.error_recognition = error_recognition;
	}


	public int get_has_b_frames() {
		return has_b_frames;
	}


	public void set_has_b_frames(int has_b_frames) {
		this.has_b_frames = has_b_frames;
	}


	public int get_block_align() {
		return block_align;
	}


	public void set_block_align(int block_align) {
		this.block_align = block_align;
	}


	public int get_parse_only() {
		return parse_only;
	}


	public void set_parse_only(int parse_only) {
		this.parse_only = parse_only;
	}


	public int get_mpeg_quant() {
		return mpeg_quant;
	}


	public void set_mpeg_quant(int mpeg_quant) {
		this.mpeg_quant = mpeg_quant;
	}


	public String get_stats_out() {
		return stats_out;
	}


	public void set_stats_out(String stats_out) {
		this.stats_out = stats_out;
	}


	public String get_stats_in() {
		return stats_in;
	}


	public void set_stats_in(String stats_in) {
		this.stats_in = stats_in;
	}


	public float get_rc_qsquish() {
		return rc_qsquish;
	}


	public void set_rc_qsquish(float rc_qsquish) {
		this.rc_qsquish = rc_qsquish;
	}


	public float get_rc_qmod_amp() {
		return rc_qmod_amp;
	}


	public void set_rc_qmod_amp(float rc_qmod_amp) {
		this.rc_qmod_amp = rc_qmod_amp;
	}

	public int get_rc_qmod_freq() {
		return rc_qmod_freq;
	}

	public void set_rc_qmod_freq(int rc_qmod_freq) {
		this.rc_qmod_freq = rc_qmod_freq;
	}

	public RcOverride[] get_rc_override() {
		return rc_override;
	}

	public int get_rc_override_count() {
		return rc_override.length;
	}

	public void set_rc_override(RcOverride[] rc_override) {
		this.rc_override = rc_override;
	}


	public String get_rc_eq() {
		return rc_eq;
	}


	public void set_rc_eq(String rc_eq) {
		this.rc_eq = rc_eq;
	}

	public int get_rc_max_rate() {
		return rc_max_rate;
	}


	public void set_rc_max_rate(int rc_max_rate) {
		this.rc_max_rate = rc_max_rate;
	}


	public int get_rc_min_rate() {
		return rc_min_rate;
	}


	public void set_rc_min_rate(int rc_min_rate) {
		this.rc_min_rate = rc_min_rate;
	}


	public int get_rc_buffer_size() {
		return rc_buffer_size;
	}


	public void set_rc_buffer_size(int rc_buffer_size) {
		this.rc_buffer_size = rc_buffer_size;
	}


	public float get_rc_buffer_aggressivity() {
		return rc_buffer_aggressivity;
	}


	public void set_rc_buffer_aggressivity(float rc_buffer_aggressivity) {
		this.rc_buffer_aggressivity = rc_buffer_aggressivity;
	}


	public float get_i_quant_factor() {
		return i_quant_factor;
	}


	public void set_i_quant_factor(float i_quant_factor) {
		this.i_quant_factor = i_quant_factor;
	}


	public float get_i_quant_offset() {
		return i_quant_offset;
	}


	public void set_i_quant_offset(float i_quant_offset) {
		this.i_quant_offset = i_quant_offset;
	}


	public float get_rc_initial_cplx() {
		return rc_initial_cplx;
	}


	public void set_rc_initial_cplx(float rc_initial_cplx) {
		this.rc_initial_cplx = rc_initial_cplx;
	}


	public int get_dct_algo() {
		return dct_algo;
	}


	public void set_dct_algo(int dct_algo) {
		this.dct_algo = dct_algo;
	}


	public float get_lumi_masking() {
		return lumi_masking;
	}


	public void set_lumi_masking(float lumi_masking) {
		this.lumi_masking = lumi_masking;
	}


	public float get_temporal_cplx_masking() {
		return temporal_cplx_masking;
	}


	public void set_temporal_cplx_masking(float temporal_cplx_masking) {
		this.temporal_cplx_masking = temporal_cplx_masking;
	}


	public float get_spatial_cplx_masking() {
		return spatial_cplx_masking;
	}


	public void set_spatial_cplx_masking(float spatial_cplx_masking) {
		this.spatial_cplx_masking = spatial_cplx_masking;
	}


	public float get_p_masking() {
		return p_masking;
	}


	public void set_p_masking(float p_masking) {
		this.p_masking = p_masking;
	}


	public float get_dark_masking() {
		return dark_masking;
	}


	public void set_dark_masking(float dark_masking) {
		this.dark_masking = dark_masking;
	}


	public int get_idct_algo() {
		return idct_algo;
	}


	public void set_idct_algo(int idct_algo) {
		this.idct_algo = idct_algo;
	}


	public int get_slice_count() {
		return slice_count;
	}


	public void set_slice_count(int slice_count) {
		this.slice_count = slice_count;
	}


	public int get_slice_offset() {
		return slice_offset;
	}


	public void set_slice_offset(int slice_offset) {
		this.slice_offset = slice_offset;
	}


	public int get_error_concealment() {
		return error_concealment;
	}


	public void set_error_concealment(int error_concealment) {
		this.error_concealment = error_concealment;
	}


	public int get_dsp_mask() {
		return dsp_mask;
	}


	public void set_dsp_mask(int dsp_mask) {
		this.dsp_mask = dsp_mask;
	}


	public int get_bits_per_coded_sample() {
		return bits_per_coded_sample;
	}


	public void set_bits_per_coded_sample(int bits_per_coded_sample) {
		this.bits_per_coded_sample = bits_per_coded_sample;
	}


	public int get_prediction_method() {
		return prediction_method;
	}


	public void set_prediction_method(int prediction_method) {
		this.prediction_method = prediction_method;
	}


	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}


	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}


	public AVFrame get_coded_frame() {
		return coded_frame;
	}


	public void set_coded_frame(AVFrame coded_frame) {
		this.coded_frame = coded_frame;
	}


	public int get_debug() {
		return debug;
	}


	public void set_debug(int debug) {
		this.debug = debug;
	}


	public int get_debug_mv() {
		return debug_mv;
	}


	public void set_debug_mv(int debug_mv) {
		this.debug_mv = debug_mv;
	}

	public long get_error(int i) {
		return error[i];
	}


	public long[] get_error() {
		return error;
	}


	public void set_error(long[] error) {
		this.error = error;
	}
	
	public void set_error(int i, int err) {
		this.error[i] = err;
	}


	public int get_me_cmp() {
		return me_cmp;
	}


	public void set_me_cmp(int me_cmp) {
		this.me_cmp = me_cmp;
	}


	public int get_me_sub_cmp() {
		return me_sub_cmp;
	}


	public void set_me_sub_cmp(int me_sub_cmp) {
		this.me_sub_cmp = me_sub_cmp;
	}


	public int get_mb_cmp() {
		return mb_cmp;
	}


	public void set_mb_cmp(int mb_cmp) {
		this.mb_cmp = mb_cmp;
	}


	public int get_ildct_cmp() {
		return ildct_cmp;
	}


	public void set_ildct_cmp(int ildct_cmp) {
		this.ildct_cmp = ildct_cmp;
	}


	public int get_dia_size() {
		return dia_size;
	}


	public void set_dia_size(int dia_size) {
		this.dia_size = dia_size;
	}


	public int get_last_predictor_count() {
		return last_predictor_count;
	}


	public void set_last_predictor_count(int last_predictor_count) {
		this.last_predictor_count = last_predictor_count;
	}


	public int get_pre_me() {
		return pre_me;
	}


	public void set_pre_me(int pre_me) {
		this.pre_me = pre_me;
	}


	public int get_me_pre_cmp() {
		return me_pre_cmp;
	}


	public void set_me_pre_cmp(int me_pre_cmp) {
		this.me_pre_cmp = me_pre_cmp;
	}


	public int get_pre_dia_size() {
		return pre_dia_size;
	}


	public void set_pre_dia_size(int pre_dia_size) {
		this.pre_dia_size = pre_dia_size;
	}


	public int get_me_subpel_quality() {
		return me_subpel_quality;
	}


	public void set_me_subpel_quality(int me_subpel_quality) {
		this.me_subpel_quality = me_subpel_quality;
	}


	public int get_dtg_active_format() {
		return dtg_active_format;
	}


	public void set_dtg_active_format(int dtg_active_format) {
		this.dtg_active_format = dtg_active_format;
	}


	public int get_me_range() {
		return me_range;
	}


	public void set_me_range(int me_range) {
		this.me_range = me_range;
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


	public int get_color_table_id() {
		return color_table_id;
	}


	public void set_color_table_id(int color_table_id) {
		this.color_table_id = color_table_id;
	}


	public int get_internal_buffer_count() {
		return internal_buffer_count;
	}


	public void set_internal_buffer_count(int internal_buffer_count) {
		this.internal_buffer_count = internal_buffer_count;
	}


	public Object[] get_internal_buffer() {
		return internal_buffer;
	}
	
	public Object get_internal_buffer(int i) {
		return internal_buffer[i];
	}

	public void set_internal_buffer(Object[] internal_buffer) {
		this.internal_buffer = internal_buffer;
	}

	public void set_internal_buffer(int i, Object internal_buffer) {
		this.internal_buffer[i] = internal_buffer;
	}


	public int get_global_quality() {
		return global_quality;
	}


	public void set_global_quality(int global_quality) {
		this.global_quality = global_quality;
	}


	public int get_coder_type() {
		return coder_type;
	}


	public void set_coder_type(int coder_type) {
		this.coder_type = coder_type;
	}


	public int get_context_model() {
		return context_model;
	}


	public void set_context_model(int context_model) {
		this.context_model = context_model;
	}


	public int get_slice_flags() {
		return slice_flags;
	}


	public void set_slice_flags(int slice_flags) {
		this.slice_flags = slice_flags;
	}


	public int get_xvmc_acceleration() {
		return xvmc_acceleration;
	}


	public void set_xvmc_acceleration(int xvmc_acceleration) {
		this.xvmc_acceleration = xvmc_acceleration;
	}


	public int get_mb_decision() {
		return mb_decision;
	}


	public void set_mb_decision(int mb_decision) {
		this.mb_decision = mb_decision;
	}


	public int[] get_intra_matrix() {
		return intra_matrix;
	}


	public void set_intra_matrix(int[] intra_matrix) {
		this.intra_matrix = intra_matrix;
	}


	public int[] get_inter_matrix() {
		return inter_matrix;
	}


	public void set_inter_matrix(int[] inter_matrix) {
		this.inter_matrix = inter_matrix;
	}


	public int get_stream_codec_tag() {
		return stream_codec_tag;
	}


	public void set_stream_codec_tag(int stream_codec_tag) {
		this.stream_codec_tag = stream_codec_tag;
	}


	public int get_scenechange_threshold() {
		return scenechange_threshold;
	}


	public void set_scenechange_threshold(int scenechange_threshold) {
		this.scenechange_threshold = scenechange_threshold;
	}


	public int get_lmin() {
		return lmin;
	}


	public void set_lmin(int lmin) {
		this.lmin = lmin;
	}


	public int get_lmax() {
		return lmax;
	}


	public void set_lmax(int lmax) {
		this.lmax = lmax;
	}


	public int getNoise_reduction() {
		return noise_reduction;
	}


	public void setNoise_reduction(int noise_reduction) {
		this.noise_reduction = noise_reduction;
	}


	public int get_rc_initial_buffer_occupancy() {
		return rc_initial_buffer_occupancy;
	}


	public void set_rc_initial_buffer_occupancy(int rc_initial_buffer_occupancy) {
		this.rc_initial_buffer_occupancy = rc_initial_buffer_occupancy;
	}


	public int get_inter_threshold() {
		return inter_threshold;
	}


	public void set_inter_threshold(int inter_threshold) {
		this.inter_threshold = inter_threshold;
	}


	public int get_flags2() {
		return flags2;
	}


	public void set_flags2(int flags2) {
		this.flags2 = flags2;
	}


	public int get_error_rate() {
		return error_rate;
	}


	public void set_error_rate(int error_rate) {
		this.error_rate = error_rate;
	}


	public int get_quantizer_noise_shaping() {
		return quantizer_noise_shaping;
	}


	public void set_quantizer_noise_shaping(int quantizer_noise_shaping) {
		this.quantizer_noise_shaping = quantizer_noise_shaping;
	}


	public int get_thread_count() {
		return thread_count;
	}


	public void set_thread_count(int thread_count) {
		this.thread_count = thread_count;
	}


	public Object get_thread_opaque() {
		return thread_opaque;
	}


	public void set_thread_opaque(Object thread_opaque) {
		this.thread_opaque = thread_opaque;
	}


	public int get_me_threshold() {
		return me_threshold;
	}


	public void set_me_threshold(int me_threshold) {
		this.me_threshold = me_threshold;
	}


	public int get_mb_threshold() {
		return mb_threshold;
	}


	public void set_mb_threshold(int mb_threshold) {
		this.mb_threshold = mb_threshold;
	}


	public int get_intra_dc_precision() {
		return intra_dc_precision;
	}


	public void set_intra_dc_precision(int intra_dc_precision) {
		this.intra_dc_precision = intra_dc_precision;
	}


	public int get_noise_reduction() {
		return noise_reduction;
	}


	public int get_nsse_weight() {
		return nsse_weight;
	}


	public void set_nsse_weight(int nsse_weight) {
		this.nsse_weight = nsse_weight;
	}


	public int get_skip_top() {
		return skip_top;
	}


	public void set_skip_top(int skip_top) {
		this.skip_top = skip_top;
	}


	public int get_skip_bottom() {
		return skip_bottom;
	}


	public void set_skip_bottom(int skip_bottom) {
		this.skip_bottom = skip_bottom;
	}


	public int get_profile() {
		return profile;
	}


	public void set_profile(int profile) {
		this.profile = profile;
	}


	public int get_level() {
		return level;
	}


	public void set_level(int level) {
		this.level = level;
	}


	public int get_lowres() {
		return lowres;
	}


	public void set_lowres(int lowres) {
		this.lowres = lowres;
	}


	public int get_coded_width() {
		return coded_width;
	}


	public void set_coded_width(int coded_width) {
		this.coded_width = coded_width;
	}


	public int get_coded_height() {
		return coded_height;
	}


	public void set_coded_height(int coded_height) {
		this.coded_height = coded_height;
	}


	public int get_frame_skip_threshold() {
		return frame_skip_threshold;
	}


	public void set_frame_skip_threshold(int frame_skip_threshold) {
		this.frame_skip_threshold = frame_skip_threshold;
	}


	public int get_frame_skip_factor() {
		return frame_skip_factor;
	}


	public void set_frame_skip_factor(int frame_skip_factor) {
		this.frame_skip_factor = frame_skip_factor;
	}


	public int get_frame_skip_exp() {
		return frame_skip_exp;
	}


	public void set_frame_skip_exp(int frame_skip_exp) {
		this.frame_skip_exp = frame_skip_exp;
	}


	public int get_frame_skip_cmp() {
		return frame_skip_cmp;
	}


	public void set_frame_skip_cmp(int frame_skip_cmp) {
		this.frame_skip_cmp = frame_skip_cmp;
	}


	public float get_border_masking() {
		return border_masking;
	}


	public void set_border_masking(float border_masking) {
		this.border_masking = border_masking;
	}


	public int get_mb_lmin() {
		return mb_lmin;
	}


	public void set_mb_lmin(int mb_lmin) {
		this.mb_lmin = mb_lmin;
	}


	public int get_mb_lmax() {
		return mb_lmax;
	}


	public void set_mb_lmax(int mb_lmax) {
		this.mb_lmax = mb_lmax;
	}


	public int get_me_penalty_compensation() {
		return me_penalty_compensation;
	}


	public void set_me_penalty_compensation(int me_penalty_compensation) {
		this.me_penalty_compensation = me_penalty_compensation;
	}


	public AVDiscard get_skip_loop_filter() {
		return skip_loop_filter;
	}


	public void set_skip_loop_filter(AVDiscard skip_loop_filter) {
		this.skip_loop_filter = skip_loop_filter;
	}


	public AVDiscard get_skip_idct() {
		return skip_idct;
	}


	public void set_skip_idct(AVDiscard skip_idct) {
		this.skip_idct = skip_idct;
	}


	public AVDiscard get_skip_frame() {
		return skip_frame;
	}


	public void set_skip_frame(AVDiscard skip_frame) {
		this.skip_frame = skip_frame;
	}


	public int get_bidir_refine() {
		return bidir_refine;
	}


	public void set_bidir_refine(int bidir_refine) {
		this.bidir_refine = bidir_refine;
	}


	public int get_brd_scale() {
		return brd_scale;
	}


	public void set_brd_scale(int brd_scale) {
		this.brd_scale = brd_scale;
	}


	public float get_crf() {
		return crf;
	}


	public void set_crf(float crf) {
		this.crf = crf;
	}


	public int get_cqp() {
		return cqp;
	}


	public void set_cqp(int cqp) {
		this.cqp = cqp;
	}


	public int get_keyint_min() {
		return keyint_min;
	}


	public void set_keyint_min(int keyint_min) {
		this.keyint_min = keyint_min;
	}


	public int get_refs() {
		return refs;
	}


	public void set_refs(int refs) {
		this.refs = refs;
	}


	public int get_chromaoffset() {
		return chromaoffset;
	}


	public void set_chromaoffset(int chromaoffset) {
		this.chromaoffset = chromaoffset;
	}


	public int get_bframebias() {
		return bframebias;
	}


	public void set_bframebias(int bframebias) {
		this.bframebias = bframebias;
	}


	public int get_trellis() {
		return trellis;
	}


	public void set_trellis(int trellis) {
		this.trellis = trellis;
	}


	public float get_complexityblur() {
		return complexityblur;
	}


	public void set_complexityblur(float complexityblur) {
		this.complexityblur = complexityblur;
	}


	public int get_deblockalpha() {
		return deblockalpha;
	}


	public void set_deblockalpha(int deblockalpha) {
		this.deblockalpha = deblockalpha;
	}


	public int get_deblockbeta() {
		return deblockbeta;
	}


	public void set_deblockbeta(int deblockbeta) {
		this.deblockbeta = deblockbeta;
	}


	public int get_partitions() {
		return partitions;
	}


	public void set_partitions(int partitions) {
		this.partitions = partitions;
	}


	public int get_directpred() {
		return directpred;
	}


	public void set_directpred(int directpred) {
		this.directpred = directpred;
	}


	public int get_cutoff() {
		return cutoff;
	}


	public void set_cutoff(int cutoff) {
		this.cutoff = cutoff;
	}


	public int get_scenechange_factor() {
		return scenechange_factor;
	}


	public void set_scenechange_factor(int scenechange_factor) {
		this.scenechange_factor = scenechange_factor;
	}


	public int get_mv0_threshold() {
		return mv0_threshold;
	}


	public void set_mv0_threshold(int mv0_threshold) {
		this.mv0_threshold = mv0_threshold;
	}


	public int get_b_sensitivity() {
		return b_sensitivity;
	}


	public void set_b_sensitivity(int b_sensitivity) {
		this.b_sensitivity = b_sensitivity;
	}


	public int get_compression_level() {
		return compression_level;
	}


	public void set_compression_level(int compression_level) {
		this.compression_level = compression_level;
	}


	public int get_min_prediction_order() {
		return min_prediction_order;
	}


	public void set_min_prediction_order(int min_prediction_order) {
		this.min_prediction_order = min_prediction_order;
	}


	public int get_max_prediction_order() {
		return max_prediction_order;
	}


	public void set_max_prediction_order(int max_prediction_order) {
		this.max_prediction_order = max_prediction_order;
	}


	public long get_timecode_frame_start() {
		return timecode_frame_start;
	}


	public void set_timecode_frame_start(long timecode_frame_start) {
		this.timecode_frame_start = timecode_frame_start;
	}


	public float get_drc_scale() {
		return drc_scale;
	}


	public void set_drc_scale(float drc_scale) {
		this.drc_scale = drc_scale;
	}


	public long get_reordered_opaque() {
		return reordered_opaque;
	}


	public void set_reordered_opaque(long reordered_opaque) {
		this.reordered_opaque = reordered_opaque;
	}


	public int get_bits_per_raw_sample() {
		return bits_per_raw_sample;
	}


	public void set_bits_per_raw_sample(int bits_per_raw_sample) {
		this.bits_per_raw_sample = bits_per_raw_sample;
	}


	public long get_channel_layout() {
		return channel_layout;
	}


	public void set_channel_layout(long channel_layout) {
		this.channel_layout = channel_layout;
	}


	public long get_request_channel_layout() {
		return request_channel_layout;
	}


	public void set_request_channel_layout(long request_channel_layout) {
		this.request_channel_layout = request_channel_layout;
	}


	public float get_rc_max_available_vbv_use() {
		return rc_max_available_vbv_use;
	}


	public void set_rc_max_available_vbv_use(float rc_max_available_vbv_use) {
		this.rc_max_available_vbv_use = rc_max_available_vbv_use;
	}


	public float get_rc_min_vbv_overflow_use() {
		return rc_min_vbv_overflow_use;
	}


	public void set_rc_min_vbv_overflow_use(float rc_min_vbv_overflow_use) {
		this.rc_min_vbv_overflow_use = rc_min_vbv_overflow_use;
	}


	public AVHWAccel get_hwaccel() {
		return hwaccel;
	}


	public void set_hwaccel(AVHWAccel hwaccel) {
		this.hwaccel = hwaccel;
	}


	public int get_ticks_per_frame() {
		return ticks_per_frame;
	}


	public void set_ticks_per_frame(int ticks_per_frame) {
		this.ticks_per_frame = ticks_per_frame;
	}


	public Object get_hwaccel_context() {
		return hwaccel_context;
	}


	public void set_hwaccel_context(Object hwaccel_context) {
		this.hwaccel_context = hwaccel_context;
	}


	public AVColorPrimaries get_color_primaries() {
		return color_primaries;
	}


	public void set_color_primaries(AVColorPrimaries color_primaries) {
		this.color_primaries = color_primaries;
	}


	public AVColorTransferCharacteristic get_color_trc() {
		return color_trc;
	}


	public void set_color_trc(AVColorTransferCharacteristic color_trc) {
		this.color_trc = color_trc;
	}


	public AVColorSpace get_colorspace() {
		return colorspace;
	}


	public void set_colorspace(AVColorSpace colorspace) {
		this.colorspace = colorspace;
	}


	public AVColorRange get_color_range() {
		return color_range;
	}


	public void set_color_range(AVColorRange color_range) {
		this.color_range = color_range;
	}


	public AVChromaLocation get_chroma_sample_location() {
		return chroma_sample_location;
	}


	public void set_chroma_sample_location(AVChromaLocation chroma_sample_location) {
		this.chroma_sample_location = chroma_sample_location;
	}


	public int get_weighted_p_pred() {
		return weighted_p_pred;
	}


	public void set_weighted_p_pred(int weighted_p_pred) {
		this.weighted_p_pred = weighted_p_pred;
	}


	public int get_aq_mode() {
		return aq_mode;
	}


	public void set_aq_mode(int aq_mode) {
		this.aq_mode = aq_mode;
	}


	public float get_aq_strength() {
		return aq_strength;
	}


	public void set_aq_strength(float aq_strength) {
		this.aq_strength = aq_strength;
	}


	public float get_psy_rd() {
		return psy_rd;
	}


	public void set_psy_rd(float psy_rd) {
		this.psy_rd = psy_rd;
	}


	public float get_psy_trellis() {
		return psy_trellis;
	}


	public void set_psy_trellis(float psy_trellis) {
		this.psy_trellis = psy_trellis;
	}


	public int get_rc_lookahead() {
		return rc_lookahead;
	}


	public void set_rc_lookahead(int rc_lookahead) {
		this.rc_lookahead = rc_lookahead;
	}


	public float get_crf_max() {
		return crf_max;
	}


	public void set_crf_max(float crf_max) {
		this.crf_max = crf_max;
	}


	public int get_log_level_offset() {
		return log_level_offset;
	}


	public void set_log_level_offset(int log_level_offset) {
		this.log_level_offset = log_level_offset;
	}


	public int get_slices() {
		return slices;
	}


	public void set_slices(int slices) {
		this.slices = slices;
	}


	public byte[] get_subtitle_header() {
		return subtitle_header;
	}


	public void set_subtitle_header(byte[] subtitle_header) {
		this.subtitle_header = Arrays.copyOf(subtitle_header, subtitle_header.length);
	}


	public AVPacket get_pkt() {
		return pkt;
	}


	public void set_pkt(AVPacket pkt) {
		this.pkt = pkt;
	}


	public int get_is_copy() {
		return is_copy;
	}


	public void set_is_copy(int is_copy) {
		this.is_copy = is_copy;
	}


	public int get_thread_type() {
		return thread_type;
	}


	public void set_thread_type(int thread_type) {
		this.thread_type = thread_type;
	}


	public int get_active_thread_type() {
		return active_thread_type;
	}


	public void set_active_thread_type(int active_thread_type) {
		this.active_thread_type = active_thread_type;
	}


	public int get_thread_safe_callbacks() {
		return thread_safe_callbacks;
	}


	public void set_thread_safe_callbacks(int thread_safe_callbacks) {
		this.thread_safe_callbacks = thread_safe_callbacks;
	}


	public long get_vbv_delay() {
		return vbv_delay;
	}


	public void set_vbv_delay(long vbv_delay) {
		this.vbv_delay = vbv_delay;
	}


	public AVAudioServiceType get_audio_service_type() {
		return audio_service_type;
	}


	public void set_audio_service_type(AVAudioServiceType audio_service_type) {
		this.audio_service_type = audio_service_type;
	}


	public AVSampleFormat get_request_sample_fmt() {
		return request_sample_fmt;
	}


	public void set_request_sample_fmt(AVSampleFormat request_sample_fmt) {
		this.request_sample_fmt = request_sample_fmt;
	}


	public long get_pts_correction_num_faulty_pts() {
		return pts_correction_num_faulty_pts;
	}


	public void set_pts_correction_num_faulty_pts(long pts_correction_num_faulty_pts) {
		this.pts_correction_num_faulty_pts = pts_correction_num_faulty_pts;
	}


	public long get_pts_correction_num_faulty_dts() {
		return pts_correction_num_faulty_dts;
	}


	public void set_pts_correction_num_faulty_dts(long pts_correction_num_faulty_dts) {
		this.pts_correction_num_faulty_dts = pts_correction_num_faulty_dts;
	}


	public long get_pts_correction_last_pts() {
		return pts_correction_last_pts;
	}


	public void set_pts_correction_last_pts(long pts_correction_last_pts) {
		this.pts_correction_last_pts = pts_correction_last_pts;
	}


	public long get_pts_correction_last_dts() {
		return pts_correction_last_dts;
	}


	public void set_pts_correction_last_dts(long pts_correction_last_dts) {
		this.pts_correction_last_dts = pts_correction_last_dts;
	}
	

	public int get_bit_rate() {
		
		 //  int bit_rate;
		   // int bits_per_sample;

		switch (codec_type) {
		    case AVMEDIA_TYPE_VIDEO:
		    case AVMEDIA_TYPE_DATA:
		    case AVMEDIA_TYPE_SUBTITLE:
		    case AVMEDIA_TYPE_ATTACHMENT:
		        return bit_rate;
		    case AVMEDIA_TYPE_AUDIO:
		        int bits_per_sample = get_bits_per_sample();
		        return (bits_per_sample != 0) ? sample_rate * channels * bits_per_sample : bit_rate;
		    default:
		    	return 0;
	    }
	}

	private int get_bits_per_sample() {
	    switch(codec_id){
//		    case CODEC_ID_ADPCM_SBPRO_2:
//		        return 2;
//		    case CODEC_ID_ADPCM_SBPRO_3:
//		        return 3;
//		    case CODEC_ID_ADPCM_SBPRO_4:
//		    case CODEC_ID_ADPCM_CT:
//		    case CODEC_ID_ADPCM_IMA_WAV:
//		    case CODEC_ID_ADPCM_MS:
//		    case CODEC_ID_ADPCM_YAMAHA:
//		        return 4;
//		    case CODEC_ID_ADPCM_G722:
		    case CODEC_ID_PCM_ALAW:
//		    case CODEC_ID_PCM_MULAW:
//		    case CODEC_ID_PCM_S8:
//		    case CODEC_ID_PCM_U8:
//		    case CODEC_ID_PCM_ZORK:
		        return 8;
		    case CODEC_ID_PCM_S16BE:
		    case CODEC_ID_PCM_S16LE:
//		    case CODEC_ID_PCM_S16LE_PLANAR:
//		    case CODEC_ID_PCM_U16BE:
//		    case CODEC_ID_PCM_U16LE:
		        return 16;
//		    case CODEC_ID_PCM_S24DAUD:
		    case CODEC_ID_PCM_S24BE:
		    case CODEC_ID_PCM_S24LE:
//		    case CODEC_ID_PCM_U24BE:
//		    case CODEC_ID_PCM_U24LE:
		        return 24;
		    case CODEC_ID_PCM_S32BE:
		    case CODEC_ID_PCM_S32LE:
//		    case CODEC_ID_PCM_U32BE:
//		    case CODEC_ID_PCM_U32LE:
//		    case CODEC_ID_PCM_F32BE:
//		    case CODEC_ID_PCM_F32LE:
		        return 32;
//		    case CODEC_ID_PCM_F64BE:
//		    case CODEC_ID_PCM_F64LE:
//		        return 64;
		    default:
		        return 0;
	    }
	}

	public int avcodec_get_context_defaults3(AVCodec codec) {
		if (codec != null) {
			avcodec_get_context_defaults2(codec.get_type());
			
			//TODO: Get codec priv_data, priv_class, defaults
		} else
			avcodec_get_context_defaults2(AVMediaType.AVMEDIA_TYPE_UNKNOWN);
		
		
		return 0;
	}
	

	private void avcodec_get_context_defaults2(AVMediaType codec_type) {
		int flags = 0;
		set_av_class(new AVCodecContextClass());

	    this.codec_type = codec_type;
	    if(codec_type == AVMediaType.AVMEDIA_TYPE_AUDIO)
	        flags = AVOption.AV_OPT_FLAG_AUDIO_PARAM;
	    else if(codec_type == AVMediaType.AVMEDIA_TYPE_VIDEO)
	        flags = AVOption.AV_OPT_FLAG_VIDEO_PARAM;
	    else if(codec_type == AVMediaType.AVMEDIA_TYPE_SUBTITLE)
	        flags = AVOption.AV_OPT_FLAG_SUBTITLE_PARAM;
	    
	    av_opt_set_defaults2(flags, flags);
	    

	    this.set_time_base(new AVRational(0,1));
	    set_get_buffer("avcodec_default_get_buffer");
	    set_release_buffer("avcodec_default_release_buffer");
	    set_get_format("avcodec_default_get_format");
	    /*set_execute("avcodec_default_execute");--
	    set_execute2("avcodec_default_execute2");--*/
	    
	    this.set_sample_aspect_ratio(new AVRational(0,1));
	    this.set_pix_fmt(PixelFormat.PIX_FMT_NONE);
	    this.set_sample_fmt(AVSampleFormat.AV_SAMPLE_FMT_NONE);

	    this.set_reordered_opaque(AVUtil.AV_NOPTS_VALUE);
	}
	
	
    /**
     * If non NULL, 'draw_horiz_band' is called by the libavcodec
     * decoder to draw a horizontal band. It improves cache usage. Not
     * all codecs can do that. You must check the codec capabilities
     * beforehand.
     * When multithreading is used, it may be called from multiple threads
     * at the same time; threads might draw different parts of the same AVFrame,
     * or multiple AVFrames, and there is no guarantee that slices will be drawn
     * in order.
     * The function is also used by hardware acceleration APIs.
     * It is called at least once during frame decoding to pass
     * the data needed for hardware render.
     * In that mode instead of pixel data, AVFrame points to
     * a structure specific to the acceleration API. The application
     * reads the structure and can change some fields to indicate progress
     * or mark state.
     * - encoding: unused
     * - decoding: Set by user.
     * @param height the height of the slice
     * @param y the y position of the slice
     * @param type 1->top field, 2->bottom field, 3->frame
     * @param offset offset into the AVFrame.data from which the slice should be read
     */
    void draw_horiz_band(AVCodecContext s, AVFrame src, int [] offset,
                            int y, int type, int height) {
    }
    
    // The RTP callback: This function is called    
    // every time the encoder has a packet to send. 
    // It depends on the encoder if the data starts 
    // with a Start Code (it should). H.263 does.   
    // mb_nb contains the number of macroblocks     
    // encoded in the RTP payload.                  
    void rtp_callback(AVCodecContext avctx, Object data, int size, int mb_nb) {
    	
    }
    
    /**
     * Called at the beginning of each frame to get a buffer for it.
     * If pic.reference is set then the frame will be read later by libavcodec.
     * avcodec_align_dimensions2() should be used to find the required width and
     * height, as they normally need to be rounded up to the next multiple of 16.
     * if CODEC_CAP_DR1 is not set then get_buffer() must call
     * avcodec_default_get_buffer() instead of providing buffers allocated by
     * some other means.
     * If frame multithreading is used and thread_safe_callbacks is set,
     * it may be called from a different thread, but not from more than one at once.
     * Does not need to be reentrant.
     * - encoding: unused
     * - decoding: Set by libavcodec, user can override.
     */
    int get_buffer(AVFrame pic) {
    	if (get_buffer.equals("avcodec_default_get_buffer"))
    		return UtilsCodec.avcodec_default_get_buffer(this, pic);
    	
    	return 0;
    }

    /**
     * Called to release buffers which were allocated with get_buffer.
     * A released buffer can be reused in get_buffer().
     * pic.data[*] must be set to NULL.
     * May be called from a different thread if frame multithreading is used,
     * but not by more than one thread at once, so does not need to be reentrant.
     * - encoding: unused
     * - decoding: Set by libavcodec, user can override.
     */
    public void release_buffer(AVFrame pic) {
    	if (release_buffer.equals("avcodec_default_release_buffer"))
    		UtilsCodec.avcodec_default_release_buffer(this, pic);
    }

    
    /**
     * callback to negotiate the pixelFormat
     * @param fmt is the list of formats which are supported by the codec,
     * it is terminated by -1 as 0 is a valid format, the formats are ordered by quality.
     * The first is always the native one.
     * @return the chosen format
     * - encoding: unused
     * - decoding: Set by user, if not set the native format will be chosen.
     */
    public PixelFormat get_format(PixelFormat [] fmt) {
    	if (get_format.equals("avcodec_default_get_format"))
    		UtilsCodec.avcodec_default_get_format(this, fmt); 	
    	
    	return null;
    }
    
    /**
     * Called at the beginning of a frame to get cr buffer for it.
     * Buffer type (size, hints) must be the same. libavcodec won't check it.
     * libavcodec will pass previous buffer in pic, function should return
     * same buffer or new buffer with old frame "painted" into it.
     * If pic.data[0] == NULL must behave like get_buffer().
     * if CODEC_CAP_DR1 is not set then reget_buffer() must call
     * avcodec_default_reget_buffer() instead of providing buffers allocated by
     * some other means.
     * - encoding: unused
     * - decoding: Set by libavcodec, user can override.
     */
    int reget_buffer(AVCodecContext c, AVFrame pic) {
    	return 0;
    }
    
    /**
     * The codec may call this to execute several independent things.
     * It will return only after finishing all tasks.
     * The user may replace this with some multithreaded implementation,
     * the default implementation will execute the parts serially.
     * @param count the number of things to execute
     * - encoding: Set by libavcodec, user can override.
     * - decoding: Set by libavcodec, user can override.
     */
    //int execute(AVCodecContext c, int (*func)(struct AVCodecContext *c2, void *arg), void *arg2, int *ret, int count, int size);

    
    
    /**
     * The codec may call this to execute several independent things.
     * It will return only after finishing all tasks.
     * The user may replace this with some multithreaded implementation,
     * the default implementation will execute the parts serially.
     * Also see avcodec_thread_init and e.g. the --enable-pthread configure option.
     * @param c context passed also to func
     * @param count the number of things to execute
     * @param arg2 argument passed unchanged to func
     * @param val2 return values of executed functions, must have space for "count" values. May be NULL.
     * @param func function that will be called count times, with jobnr from 0 to count-1.
     *             threadnr will be in the range 0 to c->thread_count-1 < MAX_THREADS and so that no
     *             two instances of func executing at the same time will have the same threadnr.
     * @return always 0 currently, but code should handle a future improvement where when any call to func
     *         returns < 0 no further calls to func may be done and < 0 is returned.
     * - encoding: Set by libavcodec, user can override.
     * - decoding: Set by libavcodec, user can override.
     */
   // int (*execute2)(struct AVCodecContext *c, int (*func)(struct AVCodecContext *c2, void *arg, int jobnr, int threadnr), void *arg2, int *ret, int count);


	@Override
	public String toString() {
		return "AVCodecContext [codecType=" + codec_type + ", codecID="
				+ codec_id + ", width=" + width + ", height=" + height
				+ ", channels=" + channels + ", bitsPerCodedSample="
				+ bits_per_coded_sample + ", sampleRate=" + sample_rate 
				+ ", timeBase=" + time_base + "]";
	}


	public int avcodec_open2(AVCodec codec) {
		return avcodec_open2(codec, null);
	}
	
	
	public int avcodec_open2(AVCodec codec, AVDictionary options) {

		int ret = 0;
		AVDictionary tmp = null;
		
		if (options != null)
			tmp = options.av_dict_copy();

	    if ( (get_codec() != null) || (codec == null) )
	        return Error.AVERROR(Error.EINVAL);
	    
	    
	    if (codec.has_priv_data()) {
	    	if (get_priv_data() == null) {
	    		set_priv_data(codec.get_priv_class());
	    	}
	    } else {
	    	set_priv_data(null);
	    }
	    
	    ret = av_opt_set_dict(tmp);
	    if (ret < 0)
	    	return ret;
	    

	    if ( (get_coded_width() != 0) && (get_coded_height() != 0) ) {
	        avcodec_set_dimensions(get_coded_width(), get_coded_height());
	    } else if ( (get_width() != 0) && (get_height() != 0) ) {
	    	avcodec_set_dimensions(get_width(), get_height());
	    }

	    if ( ( (get_coded_width() == 0) || (get_coded_height() == 0) || 
	    	   (get_width() == 0) || (get_height() == 0) ) && 
	    	 ( (ImgUtils.av_image_check_size(get_coded_width(), get_coded_height(), 0, this) < 0) ||
	    	   (ImgUtils.av_image_check_size(get_width(), get_height(), 0, this) < 0) ) ) {
	    	avcodec_set_dimensions(0, 0);	    	
	    }

	    if (get_channels() > UtilsFormat.SANE_NB_CHANNELS)
	        return Error.AVERROR(Error.EINVAL);
	    
	    set_codec(codec);
	    
	    if ( ( (get_codec_type() == AVMediaType.AVMEDIA_TYPE_UNKNOWN) || (get_codec_type() == codec.get_type()) ) &&
	           (get_codec_id() == CodecID.CODEC_ID_NONE) ) {
            set_codec_type(codec.get_type());
            set_codec_id(codec.get_id());
        }

	    if ( (get_codec_id() != codec.get_id()) || 
	         ( (get_codec_type() != codec.get_type()) && 
	           (get_codec_type() != AVMediaType.AVMEDIA_TYPE_ATTACHMENT) ) ) {
	    	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "codec type or id mismatches\n");
	        return Error.AVERROR(Error.EINVAL);
	    }
	    
	    set_frame_number(0);
	    
	    if ( (get_codec().get_max_lowres() < get_lowres()) || (get_lowres() < 0) ) {
	        Log.av_log("codecCtx", Log.AV_LOG_ERROR, "The maximum value for lowres supported by the decoder is %d\n", 
	        		get_codec().get_max_lowres());
	        return Error.AVERROR(Error.EINVAL);
	    	
	    }

	    if (get_codec().is_encode()) {
	        int i;
	        if (get_codec().get_sample_fmts().size() != 0) {
	        	for (i = 0 ; i < get_codec().get_sample_fmts().size() ; i++)
	                if (get_sample_fmt() == get_codec().get_sample_fmt(i))
	                    break;
	            if (i == get_codec().get_sample_fmts().size()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified sample_fmt is not supported.\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                set_codec(null);
	                return ret;
	            }
	        }
	        if (get_codec().get_supported_samplerates().size() != 0) {
	        	for (i = 0 ; i < get_codec().get_supported_samplerates().size() ; i++)
	                if (get_sample_rate() == get_codec().get_supported_samplerate(i))
	                    break;
	            if (i == get_codec().get_supported_samplerates().size()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified sample_fmt is not supported.\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                set_codec(null);
	                return ret;
	            }
	        }
	        if (get_codec().get_channel_layouts().size() != 0) {
	            if (get_channel_layout() == 0) {
	            	Log.av_log("codecCtx", Log.AV_LOG_WARNING, "channel_layout not specified\n");
	            } else {
		        	for (i = 0 ; i < get_codec().get_channel_layouts().size() ; i++)
		                if (get_channel_layout() == get_codec().get_channel_layout(i))
		                    break;
		            if (i == get_codec().get_channel_layouts().size()) {
		            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified channel_layout is not supported\n");
		                ret = Error.AVERROR(Error.EINVAL);
		                set_codec(null);
		                return ret;
		            }
	            }
	        }
	        if ( (get_channel_layout() != 0) && (get_channels() != 0) ) {
	            if (AudioConvert.av_get_channel_layout_nb_channels(get_channel_layout()) != get_channels()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "channel layout does not match number of channels\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                set_codec(null);
	                return ret;
	            }
	        } else if (channel_layout != 0) {
	            set_channels(AudioConvert.av_get_channel_layout_nb_channels(get_channel_layout()));
	        }
	    }

	    set_pts_correction_num_faulty_pts(0);
    	set_pts_correction_num_faulty_dts(0);
        set_pts_correction_last_pts(Long.MIN_VALUE);
        set_pts_correction_last_dts(Long.MIN_VALUE);
	    
        ret = get_codec().init(this);
	    
		return ret;
	}
	

	void avcodec_set_dimensions(int width,
			int height) {
		set_coded_width(width);
		set_coded_height(height);
		set_width(-((-width )>>get_lowres()));
		set_height(-((-height )>>get_lowres()));		
	}

	public OutOI avcodec_decode_video2(AVPacket avpkt) { 
		//AVFrame picture = codec.decode(avpkt);
		AVFrame picture = null;
		int ret;
		int got_picture_ptr = 0;
		
		if ( ( (get_coded_width() != 0) || (get_coded_height() != 0) ) &&
		     (av_image_check_size(0) != 0) )
		     return new OutOI(null, -1);
		if (get_codec().has_capabilities(AVCodec.CODEC_CAP_DELAY) ||
		    (avpkt.get_size() != 0) /*|| (get_active_thread_type() & FF_THREAD_FRAME != 0) */ ) {
			
			//TODO
			//avpkt.av_packet_split_side_data();
			set_pkt(avpkt);
			OutOI ret_obj = codec.decode(this, avpkt);
			ret = ret_obj.get_ret();
			picture = (AVFrame) ret_obj.get_obj();
			got_picture_ptr = picture != null ? 1 : 0;
			
			picture.set_pkt_dts(avpkt.get_dts());
			
			if (has_b_frames == 0)
				picture.set_pkt_pos(avpkt.get_pos());
			
	
			//FIXME these should be under if(!avctx->has_b_frames)
			if (picture.get_sample_aspect_ratio().get_num() == 0)
				picture.set_sample_aspect_ratio(get_sample_aspect_ratio());
			
			if (picture.get_width() == 0)
				picture.set_width(width);
			if (picture.get_height() == 0)
				picture.set_height(height);
	
			if (picture.get_formatV() == PixelFormat.PIX_FMT_NONE)
				picture.set_formatV(pix_fmt);
			
	
			if (got_picture_ptr != 0){
				set_frame_number(get_frame_number() + 1);
				picture.set_best_effort_timestamp(guess_correct_pts(picture.get_pkt_pts(),
															        picture.get_pkt_dts()));
			}

		} else {
			ret = 0;
		}
		return new OutOI(picture, ret);
		
	}

	private long guess_correct_pts(long reordered_pts, long dts) {
		long pts = AVUtil.AV_NOPTS_VALUE;
		
		if (dts != AVUtil.AV_NOPTS_VALUE) {
			if (dts <= get_pts_correction_last_dts())
				set_pts_correction_num_faulty_dts(get_pts_correction_num_faulty_dts()+1);
			set_pts_correction_last_dts(dts);
		}
		
		if (reordered_pts != AVUtil.AV_NOPTS_VALUE) {
			if (reordered_pts <= get_pts_correction_last_pts())
				set_pts_correction_num_faulty_pts(get_pts_correction_num_faulty_pts()+1);
			set_pts_correction_last_pts(reordered_pts);
		}
		
		if ( ( (get_pts_correction_num_faulty_pts() <= get_pts_correction_num_faulty_dts()) ||
			   (dts == AVUtil.AV_NOPTS_VALUE) ) &&
			 (reordered_pts != AVUtil.AV_NOPTS_VALUE)  )
			pts = reordered_pts;
		else
			pts = dts;
		
		return pts;
	}

	private int av_image_check_size(int i) {
		int w = get_coded_width();
		int h = get_coded_height();

	    if ( (w > 0)  && (h > 0) && ( (w+128)*(h+128) < Integer.MAX_VALUE / 8) )
	        return 0;

	    Log.av_log("ImgUtils", Log.AV_LOG_ERROR, "Picture size %ux%u is invalid\n", w, h);
	    return Error.AVERROR(Error.EINVAL);
	}

	public OutOI avcodec_decode_audio(AVPacket pkt) { 
		return codec.decodeAudio(this, pkt);
	}

	public String get_codec_tag_string() {    
		
	    return "";
	}


	private int get_channel_layout_nb_channels() {
		int count;
		long x = channel_layout;
		for (count = 0 ; x != 0 ; count++)
			x &= x-1; // unset lowest set bit
		return count;
		
		
	}

	/** Set the values of the AVCodecContext or AVFormatContext structure.
	 * They are set to the defaults specified in the according AVOption options
	 * array default_val field.
	 * TODO
	 */
	private void opt_set_defaults2(int mask, int flags) {		
	}
	
	public boolean has_codec_parameters() {
	    boolean val;
	    switch (codec_type) {
	    case AVMEDIA_TYPE_AUDIO:      
	        val = ( (sample_rate != 0) && (channels != 0) && 
	        		(sample_fmt != AVSampleFormat.AV_SAMPLE_FMT_NONE) );
	        
	        if ( (frame_size == 0) &&
	             ( (codec_id == CodecID.CODEC_ID_VORBIS) ||
    		       (codec_id == CodecID.CODEC_ID_AAC) ||
    		       (codec_id == CodecID.CODEC_ID_MP1) ||
    		       (codec_id == CodecID.CODEC_ID_MP2) ||
    		       (codec_id == CodecID.CODEC_ID_MP3) ||
    		       (codec_id == CodecID.CODEC_ID_SPEEX) ||
    		       (codec_id == CodecID.CODEC_ID_CELT) ) )
	            return false;
	        break;
	    case AVMEDIA_TYPE_VIDEO:
	        val = ( (width != 0) && (pix_fmt != PixelFormat.PIX_FMT_NONE) );
	        break;
	    default:
	        val = true;
	        break;
	    }
	    return ( (codec_id !=  CodecID.CODEC_ID_NONE) && (val == true) );
	}
	


	public int reget_buffer(AVFrame pic){
//	    AVFrame temp_pic;
//	    int i;
	    /*

	    // If no picture return a new buffer 
	    if(pic->data[0] == NULL) {
	        // We will copy from buffer, so must be readable 
	        pic->buffer_hints |= FF_BUFFER_HINTS_READABLE;
	        return s->get_buffer(s, pic);
	    }

	    // If internal buffer type return the same buffer 
	    if(pic->type == FF_BUFFER_TYPE_INTERNAL) {
	        if(s->pkt) pic->pkt_pts= s->pkt->pts;
	        else       pic->pkt_pts= AV_NOPTS_VALUE;
	        pic->reordered_opaque= s->reordered_opaque;
	        return 0;
	    }

	    //
	    // Not internal type and reget_buffer not overridden, emulate cr buffer
	    //
	    temp_pic = *pic;
	    for(i = 0; i < 4; i++)
	        pic->data[i] = pic->base[i] = NULL;
	    pic->opaque = NULL;
	    // Allocate new frame 
	    if (s->get_buffer(s, pic))
	        return -1;
	    // Copy image data from old buffer to new buffer 
	    av_picture_copy((AVPicture*)pic, (AVPicture*)&temp_pic, s->pix_fmt, s->width,
	             s->height);
	    s->release_buffer(s, &temp_pic); // Release old frame
	    */
	    return 0;
	}

	

	/*
	 * Is the time base unreliable.
	 * This is a heuristic to balance between quick acceptance of the values in
	 * the headers vs. some extra checks.
	 * Old DivX and Xvid often have nonsense timebases like 1fps or 2fps.
	 * MPEG-2 commonly misuses field repeat flags to store different framerates.
	 * And there are "variable" fps files this needs to detect as well.
	 */
	public int tb_unreliable() {
		
		if (   (get_time_base().get_den() >= 101 * get_time_base().get_num())
	        || (get_time_base().get_den() <    5 * get_time_base().get_num())
	        || (get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO)
	        || (get_codec_id() == CodecID.CODEC_ID_H264) 
	       )
			return 1;		
		return 0;
	}

	public void avcodec_close() {
		this.coded_frame = null;
		this.codec = null;
		this.active_thread_type = 0;
		
	}

	public void add_flag(int flag) {
		this.flags = this.flags & flag;
		
	}

	public OutOI avcodec_decode_audio3(AVPacket avpkt) {
		int ret;
	    set_pkt(avpkt);

	    if ( ( (get_codec().get_capabilities() & AVCodec.CODEC_CAP_DELAY) != 0) || 
	    	   (avpkt.get_size() != 0) ) {
	        //FIXME remove the check below _after_ ensuring that all audio check that the available space is enough
	        /*if (frame_size_ptr < AVCODEC_MAX_AUDIO_FRAME_SIZE){
	            av_log(avctx, AV_LOG_ERROR, "buffer smaller than AVCODEC_MAX_AUDIO_FRAME_SIZE\n");
	            return -1;
	        }
	        if(*frame_size_ptr < FF_MIN_BUFFER_SIZE ||
	        *frame_size_ptr < avctx->channels * avctx->frame_size * sizeof(int16_t)){
	            av_log(avctx, AV_LOG_ERROR, "buffer %d too small\n", *frame_size_ptr);
	            return -1;
	        }*/

	        OutOI ret_obj = get_codec().decode(this, avpkt);
	        set_frame_number(get_frame_number() + 1);
	        return ret_obj;
        } else {
        	return new OutOI(new short[0], 0);
	    }
	    

	}

	public OutOI avcodec_decode_subtitle2(AVPacket avpkt) {
	    set_pkt(avpkt);
	   
	    // Integrate this somehow...
	    //sub = avcodec_get_subtitle_defaults();
	    
	    OutOI ret_obj = get_codec().decode(this, avpkt);
	    AVSubtitle sub = (AVSubtitle)ret_obj.get_obj();
	    
	    if (sub != null)
	        this.frame_number++;
	    return ret_obj;
	}
	
	/**
	 * Get the number of samples of an audio frame. Return -1 on error.
	 */
	public int get_audio_frame_size(int size) {
	    int frame_size;

	    if (get_codec_id() == CodecID.CODEC_ID_VORBIS)
	        return -1;

	    if (get_frame_size() <= 1) {
	        int bits_per_sample = UtilsCodec.av_get_bits_per_sample(get_codec_id());

	        if (bits_per_sample != 0) {
	            if (get_channels() == 0)
	                return -1;
	            frame_size = (size << 3) / (bits_per_sample * get_channels());
	        } else {
	            /* used for example by ADPCM codecs */
	            if (get_bit_rate() == 0)
	                return -1;
	            frame_size = (int)((long)size * 8 * get_sample_rate()) / get_bit_rate();
	        }
	    } else {
	        frame_size = get_frame_size();
	    }
	    return frame_size;
	}

	public int is_intra_only() {
	    if (codec_type == AVMediaType.AVMEDIA_TYPE_AUDIO){
	        return 1;
	    } else if (codec_type == AVMediaType.AVMEDIA_TYPE_VIDEO) {
	        switch (codec_id) {
	        case CODEC_ID_MJPEG:
	        case CODEC_ID_MJPEGB:
	        case CODEC_ID_LJPEG:
	        case CODEC_ID_RAWVIDEO:
	        case CODEC_ID_DVVIDEO:
	        case CODEC_ID_HUFFYUV:
	        case CODEC_ID_FFVHUFF:
	        case CODEC_ID_ASV1:
	        case CODEC_ID_ASV2:
	        case CODEC_ID_VCR1:
	        case CODEC_ID_DNXHD:
	        case CODEC_ID_JPEG2000:
	            return 1;
	        default: break;
	        }
	    }
	    return 0;
	}


	public OutOI avcodec_encode_audio(short [] buf, int buf_size, short [] samples) { 
	  	    
		if ( ((get_codec().get_capabilities() & AVCodec.CODEC_CAP_DELAY) != 0) || 
		     (samples != null) ){
			OutOI ret_obj = get_codec().encode(this, buf, buf_size, samples); 
			this.frame_number++;
	        return ret_obj;
	    } else
	        return new OutOI(new short[0], 0);
		
	}

	public OutOI avcodec_encode_video(short [] buf, int buf_size, AVFrame pict) {

	    if (ImgUtils.av_image_check_size(get_width(), get_height(), 0, this) != 0)
	        return  new OutOI(new short[0], -1);;
	    
	    if ( ((get_codec().get_capabilities() & AVCodec.CODEC_CAP_DELAY) != 0) || 
	    	 (pict != null) ){
	    	OutOI ret_obj = get_codec().encode(this, buf, buf_size, pict);
	        set_frame_number(get_frame_number() + 1);
	        return ret_obj;
	    } else
	        return new OutOI(new short[0], 0);
	}

	public int avcodec_open(AVCodec codec) {
	    Log.av_log("", Log.AV_LOG_INFO, "avcodec_open: name: %s\n", codec.get_name());
	    int ret = 0;

	    /* If there is a user-supplied mutex locking routine, call it. */
	  /*  if (ff_lockmgr_cb) {
	        if ((*ff_lockmgr_cb)(&codec_mutex, AV_LOCK_OBTAIN))
	            return -1;
	    }*/

	   /* entangled_thread_counter++;
	    if(entangled_thread_counter != 1){
	        av_log(avctx, AV_LOG_ERROR, "insufficient thread locking around avcodec_open/close()\n");
	        ret = -1;
	        goto end;
	    }*/

	    if ( (this.get_codec() == null) || (codec == null) ) {
	        return Error.AVERROR(Error.EINVAL);
	    }

	    
	    if (codec.has_priv_data()) {
	    	if (get_priv_data() == null) {
	    		set_priv_data(codec.get_priv_class());
	    	}
	    	/*
	    	 if(codec->priv_class){ //this can be droped once all user apps use   avcodec_get_context_defaults3()
		        	*(AVClass**)avctx->priv_data= codec->priv_class;
		            av_opt_set_defaults(avctx->priv_data);
		        }*/
	    } else {
	    	set_priv_data(null);
	    }
	  
	     
	    if (coded_width != 0 && coded_height != 0)
	        avcodec_set_dimensions(coded_width, coded_height);
	    else if(width != 0 && height != 0)
	        avcodec_set_dimensions(width, height);

	    if ( ( (get_coded_width() == 0) || (get_coded_height() == 0) || 
	    	   (get_width() == 0) || (get_height() == 0) ) && 
	    	 ( (ImgUtils.av_image_check_size(get_coded_width(), get_coded_height(), 0, this) < 0) ||
	    	   (ImgUtils.av_image_check_size(get_width(), get_height(), 0, this) < 0) ) ) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_WARNING, "ignoring invalid width/height values\n");
	    	avcodec_set_dimensions(0, 0);	    	
	    }


	    /* if the decoder init function was already called previously,
	       free the already allocated subtitle_header before overwriting it */
	    if (codec.is_decode())
	        subtitle_header = null;

	    if (this.channels > UtilsFormat.SANE_NB_CHANNELS) {
	        ret = Error.AVERROR(Error.EINVAL);
	        this.priv_data = null;
	        this.codec = null;
	        return ret;
	    }

	    this.codec = codec;
	    
	    if ( ( (get_codec_type() == AVMediaType.AVMEDIA_TYPE_UNKNOWN) || (get_codec_type() == codec.get_type()) ) &&
		           (get_codec_id() == CodecID.CODEC_ID_NONE) ) {
	            set_codec_type(codec.get_type());
	            set_codec_id(codec.get_id());
	        }


	    if ( (get_codec_id() != codec.get_id()) || 
	         ( (get_codec_type() != codec.get_type()) && 
	           (get_codec_type() != AVMediaType.AVMEDIA_TYPE_ATTACHMENT) ) ) {
	    	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "codec type or id mismatches\n");
	        priv_data = null;
	        codec = null;
	        return Error.AVERROR(Error.EINVAL);
	    }

	    set_frame_number(0);

	    /*if (HAVE_THREADS && !avctx->thread_opaque) {
	        ret = ff_thread_init(avctx);
	        if (ret < 0) {
	            priv_data = null;
	        codec = null;
	        return ret;
	        }
	    }*/
	    
	    if ( (get_codec().get_max_lowres() < get_lowres()) || (get_lowres() < 0) ) {
	        Log.av_log("codecCtx", Log.AV_LOG_ERROR, "The maximum value for lowres supported by the decoder is %d\n", 
	        		get_codec().get_max_lowres());
	        priv_data = null;
	        codec = null;
	        return Error.AVERROR(Error.EINVAL);
	    	
	    }

	    
	    if (get_codec().is_encode()) {
	        int i;
	        if (get_codec().get_sample_fmts().size() != 0) {
	        	for (i = 0 ; i < get_codec().get_sample_fmts().size() ; i++)
	                if (get_sample_fmt() == get_codec().get_sample_fmt(i))
	                    break;
	            if (i == get_codec().get_sample_fmts().size()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified sample_fmt is not supported.\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                this.priv_data = null;
	                set_codec(null);
	                return ret;
	            }
	        }
	        if (get_codec().get_supported_samplerates().size() != 0) {
	        	for (i = 0 ; i < get_codec().get_supported_samplerates().size() ; i++)
	                if (get_sample_rate() == get_codec().get_supported_samplerate(i))
	                    break;
	            if (i == get_codec().get_supported_samplerates().size()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified sample_fmt is not supported.\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                this.priv_data = null;
	                set_codec(null);
	                return ret;
	            }
	        }
	        if (get_codec().get_channel_layouts().size() != 0) {
	            if (get_channel_layout() == 0) {
	            	Log.av_log("codecCtx", Log.AV_LOG_WARNING, "channel_layout not specified\n");
	            } else {
		        	for (i = 0 ; i < get_codec().get_channel_layouts().size() ; i++)
		                if (get_channel_layout() == get_codec().get_channel_layout(i))
		                    break;
		            if (i == get_codec().get_channel_layouts().size()) {
		            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "Specified channel_layout is not supported\n");
		                ret = Error.AVERROR(Error.EINVAL);
		                this.priv_data = null;
		                set_codec(null);
		                return ret;
		            }
	            }
	        }
	        if ( (get_channel_layout() != 0) && (get_channels() != 0) ) {
	            if (AudioConvert.av_get_channel_layout_nb_channels(get_channel_layout()) != get_channels()) {
	            	Log.av_log("codecCtx", Log.AV_LOG_ERROR, "channel layout does not match number of channels\n");
	                ret = Error.AVERROR(Error.EINVAL);
	                priv_data = null;
	                set_codec(null);
	                return ret;
	            }
	        } else if (channel_layout != 0) {
	            set_channels(AudioConvert.av_get_channel_layout_nb_channels(get_channel_layout()));
	        }
	    }

	    set_pts_correction_num_faulty_pts(0);
    	set_pts_correction_num_faulty_dts(0);
        set_pts_correction_last_pts(Long.MIN_VALUE);
        set_pts_correction_last_dts(Long.MIN_VALUE);
	    
        ret = get_codec().init(this);
	    
		return ret;
	}

	public void execute(AVCodecContext c, String func,
			MpegEncContext get_thread_context, Object object, int count) {
		// TODO Jerome
		
	}

	
	

}

