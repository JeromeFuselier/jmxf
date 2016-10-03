package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.Config;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.DSPContext;
import uk.ac.liv.ffmpeg.libavcodec.ImgConvert;
import uk.ac.liv.ffmpeg.libavcodec.MotionEst;
import uk.ac.liv.ffmpeg.libavcodec.PutBitContext;
import uk.ac.liv.ffmpeg.libavcodec.PutBits;
import uk.ac.liv.ffmpeg.libavcodec.RateControl;
import uk.ac.liv.ffmpeg.libavcodec.RateControlContext;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.h261.H261Enc;
import uk.ac.liv.ffmpeg.libavcodec.h263.H263;
import uk.ac.liv.ffmpeg.libavcodec.h263.H263Data;
import uk.ac.liv.ffmpeg.libavcodec.h263.Ituh263Enc;
import uk.ac.liv.ffmpeg.libavcodec.mjpeg.MjpegEnc;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegVideo.OutputFormat;
import uk.ac.liv.ffmpeg.libavcodec.mpeg4.MPEG4Data;
import uk.ac.liv.ffmpeg.libavcodec.mpeg4.Mpeg4VideoEnc;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOI;

public class MpegVideoEnc {

	public static void ff_convert_matrix(DSPContext dsp, int[] qmat, 
			int[][] qmat16, int[] quant_matrix, int bias, int qmin,
			int qmax, int intra) {
	/*    int qscale;
	    int shift=0;

	    for (qscale = qmin ; qscale <= qmax ; qscale++){
	        int i;
	        if (dsp.get_fdct == ff_jpeg_fdct_islow
	#ifdef FAAN_POSTSCALE
	            || dsp.get_fdct == ff_faandct
	#endif
	            ) {
	            for(i=0;i<64;i++) {
	                const int j= dsp.get_idct_permutation[i];
	                /* 16 <= qscale * quant_matrix[i] <= 7905 
	                /* 19952             <= ff_aanscales[i] * qscale * quant_matrix[i]               <= 249205026 
	                /* (1 << 36) / 19952 >= (1 << 36) / (ff_aanscales[i] * qscale * quant_matrix[i]) >= (1 << 36) / 249205026 
	                /* 3444240           >= (1 << 36) / (ff_aanscales[i] * qscale * quant_matrix[i]) >= 275 

	                qmat[qscale][i] = (int)((UINT64_C(1) << QMAT_SHIFT) /
	                                (qscale * quant_matrix[j]));
	            }
	        } else if (dsp.get_fdct == fdct_ifast
	#ifndef FAAN_POSTSCALE
	                   || dsp.get_fdct == ff_faandct
	#endif
	                   ) {
	            for(i=0;i<64;i++) {
	                const int j= dsp.get_idct_permutation[i];
	                /* 16 <= qscale * quant_matrix[i] <= 7905 */
	                /* 19952             <= ff_aanscales[i] * qscale * quant_matrix[i]               <= 249205026 
	                /* (1 << 36) / 19952 >= (1 << 36) / (ff_aanscales[i] * qscale * quant_matrix[i]) >= (1<<36)/249205026 
	                /* 3444240           >= (1 << 36) / (ff_aanscales[i] * qscale * quant_matrix[i]) >= 275 

	                qmat[qscale][i] = (int)((UINT64_C(1) << (QMAT_SHIFT + 14)) /
	                                (ff_aanscales[i] * qscale * quant_matrix[j]));
	            }
	        } else {
	            for(i=0;i<64;i++) {
	                const int j= dsp.get_idct_permutation[i];
	                /* We can safely suppose that 16 <= quant_matrix[i] <= 255
	                   So 16           <= qscale * quant_matrix[i]             <= 7905
	                   so (1<<19) / 16 >= (1<<19) / (qscale * quant_matrix[i]) >= (1<<19) / 7905
	                   so 32768        >= (1<<19) / (qscale * quant_matrix[i]) >= 67
	                
	                qmat[qscale][i] = (int)((UINT64_C(1) << QMAT_SHIFT) / (qscale * quant_matrix[j]));
//	                qmat  [qscale][i] = (1 << QMAT_SHIFT_MMX) / (qscale * quant_matrix[i]);
	                qmat16[qscale][0][i] = (1 << QMAT_SHIFT_MMX) / (qscale * quant_matrix[j]);

	                if(qmat16[qscale][0][i]==0 || qmat16[qscale][0][i]==128*256) qmat16[qscale][0][i]=128*256-1;
	                qmat16[qscale][1][i]= ROUNDED_DIV(bias<<(16-QUANT_BIAS_SHIFT), qmat16[qscale][0][i]);
	            }
	        }

	        for(i=intra; i<64; i++){
	            int64_t max= 8191;
	            if (dsp.get_fdct == fdct_ifast
	#ifndef FAAN_POSTSCALE
	                   || dsp.get_fdct == ff_faandct
	#endif
	                   ) {
	                max = (8191LL*ff_aanscales[i]) >> 14;
	            }
	            while(((max * qmat[qscale][i]) >> shift) > INT_MAX){
	                shift++;
	            }
	        }
	    }
	    if(shift){
	        av_log(null, AV_LOG_INFO, "Warning, QMAT_SHIFT is larger than %d, overflows possible\n", QMAT_SHIFT - shift);
	    }
		*/
	}

	public static int MPV_encode_init(AVCodecContext avctx) {
		int chroma_h_shift, chroma_v_shift;
	    int i;
		MpegEncContext s = new MpegEncContext();
	    
		avctx.set_priv_data(s);
		
		s.MPV_common_defaults();
		
		switch (avctx.get_codec_id()) {
		case CODEC_ID_MPEG2VIDEO:
	        if ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV420P) && 
	        	 (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV422P) ) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "only YUV420 and YUV422 are supported\n");
	            return -1;
	        }
	        break;	
		case CODEC_ID_LJPEG:
	        if ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUVJ420P) && 
	        	  (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUVJ422P) && 
	        	  (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUVJ444P) && 
	        	  (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_BGRA) &&
	        	  ( ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV420P) && 
	        	      (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV422P) && 
	        	      (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV444P) ) || 
	        	    (avctx.get_strict_std_compliance() > AVCodec.FF_COMPLIANCE_UNOFFICIAL) ) ) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "colorspace not supported in LJPEG\n");
	            return -1;
	        }
	        break;		
		case CODEC_ID_MJPEG:
	        if ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUVJ420P) && 
	        	  (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUVJ422P) && 
	        	  ( ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV420P) && 
	        	      (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV422P) ) || 
	        	    (avctx.get_strict_std_compliance() > AVCodec.FF_COMPLIANCE_UNOFFICIAL) ) ) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "colorspace not supported in jpeg\n");
	            return -1;
	        }
	        break;		
      	default: 
      		if (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_YUV420P) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "only YUV420 is supported\n");
	            return -1;	
			}
		}
		
		switch (avctx.get_pix_fmt()) {
	    case PIX_FMT_YUVJ422P:
	    case PIX_FMT_YUV422P:
	        s.set_chroma_format(MpegVideo.CHROMA_422);
	        break;
	    case PIX_FMT_YUVJ420P:
	    case PIX_FMT_YUV420P:
	    default:
	        s.set_chroma_format(MpegVideo.CHROMA_420);
	        break;
		}
		
		s.set_bit_rate(avctx.get_bit_rate());
	    s.set_width(avctx.get_width());
	    s.set_height(avctx.get_height());
	    if( (avctx.get_gop_size() > 600) && (avctx.get_strict_std_compliance() > AVCodec.FF_COMPLIANCE_EXPERIMENTAL) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "Warning keyframe interval too large! reducing it ...\n");
	        avctx.set_gop_size(600);
	    }
	    s.set_gop_size(avctx.get_gop_size());
	    s.set_avctx(avctx);
	    s.set_flags(avctx.get_flags());
	    s.set_flags2(avctx.get_flags2());
	    s.set_max_b_frames(avctx.get_max_b_frames());
	    s.set_codec_id(avctx.get_codec().get_id());
	    s.set_luma_elim_threshold(avctx.get_luma_elim_threshold());
	    s.set_chroma_elim_threshold(avctx.get_chroma_elim_threshold());
	    s.set_strict_std_compliance(avctx.get_strict_std_compliance());
	    s.set_data_partitioning(avctx.get_flags() & AVCodec.CODEC_FLAG_PART);
	    s.set_quarter_sample((avctx.get_flags() & AVCodec.CODEC_FLAG_QPEL) != 0 ? 1 : 0);
	    s.set_mpeg_quant(avctx.get_mpeg_quant());
	    s.set_rtp_mode(avctx.get_rtp_payload_size() != 0 ? 1 : 0);
	    s.set_intra_dc_precision(avctx.get_intra_dc_precision());
	    s.set_user_specified_pts(AVUtil.AV_NOPTS_VALUE);

	    if (s.get_gop_size() <= 1) {
	        s.set_intra_only(1);
	        s.set_gop_size(12);
	    } else {
	        s.set_intra_only(0);
	    }

	    s.set_me_method(avctx.get_me_method());

	    /* Fixed QSCALE */
	    s.set_fixed_qscale( (avctx.get_flags() & AVCodec.CODEC_FLAG_QSCALE) != 0 ? 1 : 0 );

	    s.set_adaptive_quant(( (s.get_avctx().get_lumi_masking() != 0) || 
	    		               (s.get_avctx().get_dark_masking() != 0) ||
	    		               (s.get_avctx().get_temporal_cplx_masking() != 0) ||
	    		               (s.get_avctx().get_spatial_cplx_masking() != 0) ||
	    		               (s.get_avctx().get_p_masking() !=0) ||
	    		               (s.get_avctx().get_border_masking() != 0) ||
	    		               ((s.get_flags() & AVCodec.CODEC_FLAG_QP_RD) != 0) ) &&
	                         (s.get_fixed_qscale() == 0) ?1:0);

	    s.set_obmc((s.get_flags() & AVCodec.CODEC_FLAG_OBMC) != 0 ? 1 : 0);
	    s.set_loop_filter((s.get_flags() & AVCodec.CODEC_FLAG_LOOP_FILTER) != 0 ? 1 : 0);
	    s.set_alternate_scan((s.get_flags() & AVCodec.CODEC_FLAG_ALT_SCAN) != 0 ? 1 : 0);
	    s.set_intra_vlc_format((s.get_flags2() & AVCodec.CODEC_FLAG2_INTRA_VLC) != 0 ? 1 : 0);
	    s.set_q_scale_type((s.get_flags2() & AVCodec.CODEC_FLAG2_NON_LINEAR_QUANT) != 0 ? 1 : 0);

	    if ( (avctx.get_rc_max_rate() != 0) && (avctx.get_rc_buffer_size() == 0) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "a vbv buffer size is needed, for encoding with a maximum bitrate\n");
	        return -1;
	    }

	    if ( (avctx.get_rc_min_rate() != 0) && (avctx.get_rc_max_rate() != avctx.get_rc_min_rate()) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "Warning min_rate > 0 but min_rate != max_rate isn't recommended!\n");
	    }

	    if ( (avctx.get_rc_min_rate() != 0) && (avctx.get_rc_min_rate() > avctx.get_bit_rate()) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "bitrate below min bitrate\n");
	        return -1;
	    }

	    if ( (avctx.get_rc_max_rate() != 0) && (avctx.get_rc_max_rate() < avctx.get_bit_rate()) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "bitrate above max bitrate\n");
	        return -1;
	    }

	    if ( (avctx.get_rc_max_rate() != 0) && (avctx.get_rc_max_rate() == avctx.get_bit_rate()) &&
	    	 (avctx.get_rc_max_rate() != avctx.get_rc_min_rate()) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "impossible bitrate constraints, this will fail\n");
	    }

	    if ( (avctx.get_rc_buffer_size() != 0) && 
	    	 (avctx.get_bit_rate() * (long) avctx.get_time_base().get_num() > avctx.get_rc_buffer_size() * (long)avctx.get_time_base().get_den()) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "VBV buffer too small for bitrate\n");
	        return -1;
	    }

	    if ( (s.get_fixed_qscale() == 0) && (avctx.get_bit_rate() * avctx.get_time_base().av_q2d() > avctx.get_bit_rate_tolerance()) ){
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "bitrate tolerance too small for bitrate\n");
	        return -1;
	    }

	    if ( (s.get_avctx().get_rc_max_rate() != 0) && 
	    	 (s.get_avctx().get_rc_min_rate() == s.get_avctx().get_rc_max_rate()) &&
	         ( (s.get_codec_id() == CodecID.CODEC_ID_MPEG1VIDEO) || (s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO) ) &&
	         (90000 * (avctx.get_rc_buffer_size() - 1) > s.get_avctx().get_rc_max_rate() * 0xFFFF) ){
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "Warning vbv_delay will be set to 0xFFFF (=VBR) as the specified vbv buffer is too large for the given bitrate!\n");
	    }

	    if ( (s.has_flag(AVCodec.CODEC_FLAG_4MV)) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) &&
	    	 (s.get_codec_id() != CodecID.CODEC_ID_H263) &&
	    	 (s.get_codec_id() != CodecID.CODEC_ID_H263P) &&
	    	 (s.get_codec_id() != CodecID.CODEC_ID_FLV1) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "4MV not supported by codec");
	        return -1;
	    }

	    if ( (s.get_obmc() != 0) && 
	    	 (s.get_avctx().get_mb_decision() != AVCodec.FF_MB_DECISION_SIMPLE) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "OBMC is only supported with simple mb decision\n");
	        return -1;
	    }

	    if ( (s.get_obmc() != 0) && 
             (s.get_codec_id() != CodecID.CODEC_ID_H263) && 
             (s.get_codec_id() != CodecID.CODEC_ID_H263P) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "OBMC is only supported with H263(+)\n");
	        return -1;
	    }

	    if ( (s.get_quarter_sample() != 0) && 
	    	  (s.get_codec_id()  !=  CodecID.CODEC_ID_MPEG4)){
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "qpel not supported by codec\n");
	        return -1;
	    }

	    if ( (s.get_data_partitioning() != 0) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) ){
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "data partitioning not supported by codec\n");
	        return -1;
	    }

	    if ( (s.get_max_b_frames() != 0) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG1VIDEO) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG2VIDEO) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "b frames not supported by codec\n");
	        return -1;
	    }

	    if ( ( (s.get_codec_id() == CodecID.CODEC_ID_MPEG4) || 
	    	   (s.get_codec_id() == CodecID.CODEC_ID_H263) ||
	           (s.get_codec_id() == CodecID.CODEC_ID_H263P) ) &&
	         ( (avctx.get_sample_aspect_ratio().get_num() > 255) || 
	           (avctx.get_sample_aspect_ratio().get_den() > 255) ) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "Invalid pixel aspect ratio %i/%i, limit is 255/255\n",
	               avctx.get_sample_aspect_ratio().get_num(), 
	               avctx.get_sample_aspect_ratio().get_den());
	        return -1;
	    }

	    if ( s.has_flag(AVCodec.CODEC_FLAG_INTERLACED_DCT | AVCodec.CODEC_FLAG_INTERLACED_ME | AVCodec.CODEC_FLAG_ALT_SCAN) && 
	         (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) && 
	         (s.get_codec_id() != CodecID.CODEC_ID_MPEG2VIDEO) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "interlacing not supported by codec\n");
	        return -1;
	    }

	    if ( (s.get_mpeg_quant() != 0) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) ) { //FIXME mpeg2 uses that too
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "mpeg2 style quantization not supported by codec\n");
	        return -1;
	    }

	    if ( s.has_flag(AVCodec.CODEC_FLAG_CBP_RD) && (avctx.get_trellis() == 0) ){
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "CBP RD needs trellis quant\n");
	        return -1;
	    }

	    if ( s.has_flag(AVCodec.CODEC_FLAG_QP_RD) && 
	    	 (s.get_avctx().get_mb_decision() != AVCodec.FF_MB_DECISION_RD) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "QP RD needs mbd=2\n");
	        return -1;
	    }

	    if ( (s.get_avctx().get_scenechange_threshold() < 1000000000) && 
	    	 s.has_flag(AVCodec.CODEC_FLAG_CLOSED_GOP) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "closed gop with scene change detection are not supported yet, set threshold to 1000000000\n");
	        return -1;
	    }

	    if ( s.has_flag2(AVCodec.CODEC_FLAG2_INTRA_VLC) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG2VIDEO) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "intra vlc table not supported by codec\n");
	        return -1;
	    }

	    if (s.has_flag(AVCodec.CODEC_FLAG_LOW_DELAY)){
	        if (s.get_codec_id()  !=  CodecID.CODEC_ID_MPEG2VIDEO) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "low delay forcing is only available for mpeg2\n");
	            return -1;
	        }
	        if (s.get_max_b_frames() != 0){
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "b frames cannot be used with low delay\n");
	            return -1;
	        }
	    }

	    if (s.get_q_scale_type() == 1){
	        if (s.get_codec_id() != CodecID.CODEC_ID_MPEG2VIDEO) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "non linear quant is only available for mpeg2\n");
	            return -1;
	        }
	        if (avctx.get_qmax() > 12) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "non linear quant only supports qmax <= 12 currently\n");
	            return -1;
	        }
	    }

	    if ( (s.get_avctx().get_thread_count() > 1) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG4) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG1VIDEO) && 
	    	 (s.get_codec_id() != CodecID.CODEC_ID_MPEG2VIDEO) && 
	    	 ( (s.get_codec_id() != CodecID.CODEC_ID_H263P) || 
	    	   !s.has_flag(AVCodec.CODEC_FLAG_H263P_SLICE_STRUCT) ) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "multi threaded encoding not supported by codec\n");
	        return -1;
	    }

	    if (s.get_avctx().get_thread_count() < 1) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "automatic thread number detection not supported by codec, patch welcome\n");
	        return -1;
	    }

	    if (s.get_avctx().get_thread_count() > 1)
	        s.set_rtp_mode(1);

	    if ( (avctx.get_time_base().get_den() == 0) ||
	    	 (avctx.get_time_base().get_num() == 0) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "framerate not set\n");
	        return -1;
	    }

	    i = (Integer.MAX_VALUE / 2 + 128) >> 8;
	    if (avctx.get_me_threshold() >= i) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "me_threshold too large, max is %d\n",
            		i - 1);
	        return -1;
	    }
	    if (avctx.get_mb_threshold() >= i) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "me_threshold too large, max is %d\n",
            		i - 1);
	        return -1;
	    }

	    if ( (avctx.get_b_frame_strategy() != 0) && 
	    	 (avctx.has_flag(AVCodec.CODEC_FLAG_PASS2)) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "notice: b_frame_strategy only affects the first pass\n");
	        avctx.set_b_frame_strategy(0);
	    }

	    i = Mathematics.av_gcd(avctx.get_time_base().get_den(), avctx.get_time_base().get_num());
	    if (i > 1) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "removing common factors from framerate\n");
	        avctx.get_time_base().set_den(avctx.get_time_base().get_den() / i);
	        avctx.get_time_base().set_num(avctx.get_time_base().get_num() / i);
//	        return -1;
	    }

	    if ( (s.get_mpeg_quant() != 0) || 
	    	 (s.get_codec_id() == CodecID.CODEC_ID_MPEG1VIDEO) || 
	    	 (s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO) || 
	    	 (s.get_codec_id() == CodecID.CODEC_ID_MJPEG) ) {
	        s.set_intra_quant_bias(3 << (MpegVideo.QUANT_BIAS_SHIFT - 3)); //(a + x*3/8)/x
	        s.set_inter_quant_bias(0);
	    }else{
	        s.set_intra_quant_bias(0);
	        s.set_inter_quant_bias(-(1<<(MpegVideo.QUANT_BIAS_SHIFT-2))); //(a - x/4)/x
	    }

	    if (avctx.get_intra_quant_bias() != AVCodec.FF_DEFAULT_QUANT_BIAS) 
	        s.set_intra_quant_bias(avctx.get_intra_quant_bias());
	    if (avctx.get_inter_quant_bias() != AVCodec.FF_DEFAULT_QUANT_BIAS)
	        s.set_inter_quant_bias(avctx.get_inter_quant_bias());

	    OutII tmp = ImgConvert.avcodec_get_chroma_sub_sample(avctx.get_pix_fmt());
	    chroma_h_shift = tmp.get_val1();
	    chroma_v_shift = tmp.get_val2();

	    if ( (avctx.get_codec_id() == CodecID.CODEC_ID_MPEG4) && 
	    	 (s.get_avctx().get_time_base().get_den() > (1<<16)-1) ) {
            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "timebase %d/%d not supported by MPEG 4 standard, "+
	               "the maximum admitted value for the timebase denominator is %d",
	               s.get_avctx().get_time_base().get_num(), 
	               s.get_avctx().get_time_base().get_den(), 
	               (1<<16)-1);
	        return -1;
	    }
	    
	    
	    s.set_time_increment_bits(Common.av_log2(s.get_avctx().get_time_base().get_den() - 1) + 1);

	    switch(avctx.get_codec().get_id()) {
	    case CODEC_ID_MPEG1VIDEO:
	        s.set_out_format(OutputFormat.FMT_MPEG1);
	        s.set_low_delay(s.has_flag(AVCodec.CODEC_FLAG_LOW_DELAY)?1:0);
	        avctx.set_delay(s.get_low_delay()!=0 ? 0 : (s.get_max_b_frames() + 1));
	        break;
	    case CODEC_ID_MPEG2VIDEO:
	        s.set_out_format(OutputFormat.FMT_MPEG1);
	        s.set_low_delay(s.has_flag(AVCodec.CODEC_FLAG_LOW_DELAY)?1:0);
	        avctx.set_delay(s.get_low_delay()!=0 ? 0 : (s.get_max_b_frames() + 1));
	        s.set_rtp_mode(1);
	        break;
	    case CODEC_ID_LJPEG:
	    case CODEC_ID_MJPEG:
	        s.set_out_format(OutputFormat.FMT_MJPEG);
	        s.set_intra_only(1); // force intra only for jpeg 
	        if ( (avctx.get_codec().get_id() == CodecID.CODEC_ID_LJPEG) && 
	        	 (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_BGRA) ) {
	            s.get_mjpeg_vsample()[0] = 1;
	            s.get_mjpeg_hsample()[0] = 1;
	            s.get_mjpeg_vsample()[1] = 1;
	            s.get_mjpeg_hsample()[1] = 1;
	            s.get_mjpeg_vsample()[2] = 1;
	            s.get_mjpeg_hsample()[2] = 1;
	        }else{
	            s.get_mjpeg_vsample()[0] = 2;
	            s.get_mjpeg_vsample()[1] = 2 >> chroma_v_shift;
	            s.get_mjpeg_vsample()[2] = 2 >> chroma_v_shift;
	            s.get_mjpeg_hsample()[0] = 2;
	            s.get_mjpeg_hsample()[1] = 2 >> chroma_h_shift;
	            s.get_mjpeg_hsample()[2] = 2 >> chroma_h_shift;
	        }
	        /*if (!(CONFIG_MJPEG_ENCODER || CONFIG_LJPEG_ENCODER)
	            || ff_mjpeg_encode_init(s) < 0)
	            return -1;*/
	        if (s.ff_mjpeg_encode_init() < 0)
	        	return -1;	        
	        
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_H261:
	       // if (!CONFIG_H261_ENCODER)  return -1;
	        if (H261Enc.ff_h261_get_picture_format(s.get_width(), s.get_height()) < 0) {
            	Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "The specified picture size of %dx%d is not valid for the H.261 codec.\n" + 
            			   "Valid sizes are 176x144, 352x288\n", 
            				s.get_width(), s.get_height());
	            return -1;
	        }
	        s.set_out_format(OutputFormat.FMT_H261);	        
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_H263:
	       // if (!CONFIG_H263_ENCODER)  return -1;
	        if (UtilsCodec.ff_match_2uint16(H263Data.h263_format, s.get_width(), s.get_height()) == 8) {
	            Log.av_log("mpegVideoEnc", Log.AV_LOG_WARNING, "The specified picture size of %dx%d is not valid for the H.263 codec.\n" +
	            				"Valid sizes are 128x96, 176x144, 352x288, 704x576, and 1408x1152. Try H.263+.", 
	            				s.get_width(), s.get_height());
	            return -1;
	        }
	        s.set_out_format(OutputFormat.FMT_H263);	
	        s.set_obmc(avctx.has_flag(AVCodec.CODEC_FLAG_OBMC) ? 1:0);        
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_H263P:
	        s.set_out_format(OutputFormat.FMT_H263);	
	        s.set_h263_plus(1);
	        // Fx 
	        s.set_umvplus(avctx.has_flag(AVCodec.CODEC_FLAG_H263P_UMV) ? 1:0);
	        s.set_h263_aic(avctx.has_flag(AVCodec.CODEC_FLAG_AC_PRED) ? 1:0);
	        s.set_modified_quant(s.get_h263_aic());
	        s.set_alt_inter_vlc(avctx.has_flag(AVCodec.CODEC_FLAG_H263P_AIV) ? 1:0);
	        s.set_obmc(avctx.has_flag(AVCodec.CODEC_FLAG_OBMC) ? 1:0);
	        s.set_loop_filter(avctx.has_flag(AVCodec.CODEC_FLAG_LOOP_FILTER) ? 1:0);
	        s.set_unrestricted_mv(( (s.get_obmc() != 0) || 
	        					    (s.get_loop_filter() != 0) || 
	        					    (s.get_umvplus() != 0) ) ? 1:0);
	        s.set_h263_slice_structured(avctx.has_flag(AVCodec.CODEC_FLAG_H263P_SLICE_STRUCT) ? 1:0);

	        // /Fx 
	        // These are just to be sure      
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_FLV1:
	        s.set_out_format(OutputFormat.FMT_H263);	
	        s.set_h263_flv(2); // format = 1; 11-bit codes 
	        s.set_unrestricted_mv(1);
	        s.set_rtp_mode(0); // don't allow GOB 
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_RV10:
	        s.set_out_format(OutputFormat.FMT_H263);	   
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_RV20:
	        s.set_out_format(OutputFormat.FMT_H263);	
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        s.set_modified_quant(1);
	        s.set_h263_aic(1);
	        s.set_h263_plus(1);
	        s.set_loop_filter(1);
	        s.set_unrestricted_mv(0);
	        break;
	    case CODEC_ID_MPEG4:
	        s.set_out_format(OutputFormat.FMT_H263);	
	        s.set_h263_pred(1);
	        s.set_unrestricted_mv(1);
	        s.set_low_delay(s.get_max_b_frames() != 0 ? 0 : 1);
	        avctx.set_delay(s.get_low_delay() != 0 ? 0 : (s.get_max_b_frames() + 1));
	        break;
	    case CODEC_ID_MSMPEG4V2:
	        s.set_out_format(OutputFormat.FMT_H263);
	        s.set_h263_pred(1);
	        s.set_unrestricted_mv(1);
	        s.set_msmpeg4_version(2);
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_MSMPEG4V3:
	        s.set_out_format(OutputFormat.FMT_H263);	
	        s.set_h263_pred(1);
	        s.set_unrestricted_mv(1);
	        s.set_msmpeg4_version(3);
	        s.set_flipflop_rounding(1);   
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_WMV1:
	        s.set_out_format(OutputFormat.FMT_H263);
	        s.set_h263_pred(1);
	        s.set_unrestricted_mv(1);
	        s.set_msmpeg4_version(4);
	        s.set_flipflop_rounding(1);   
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    case CODEC_ID_WMV2:
	        s.set_out_format(OutputFormat.FMT_H263);
	        s.set_h263_pred(1);
	        s.set_unrestricted_mv(1);
	        s.set_msmpeg4_version(5);	
	        s.set_flipflop_rounding(1);   
	        avctx.set_delay(0);
	        s.set_low_delay(1);
	        break;
	    default:
	        return -1;
	    }

	    avctx.set_has_b_frames(s.get_low_delay() == 0 ? 1 : 0);

	    s.set_encoding(1);

	    int bool_tmp = (avctx.has_flag(AVCodec.CODEC_FLAG_INTERLACED_DCT|AVCodec.CODEC_FLAG_INTERLACED_ME|AVCodec.CODEC_FLAG_ALT_SCAN) ? 0 : 1);
	    s.set_progressive_frame(bool_tmp);
	    s.set_progressive_sequence(bool_tmp);

	    /* init */
	    if (s.MPV_common_init() < 0)
	        return -1;

	   /* if(avctx.get_trellis)
	        s.get_dct_quantize = dct_quantize_trellis_c;*/

	  //  if((CONFIG_H263P_ENCODER || CONFIG_RV20_ENCODER) && s.get_modified_quant)
	    if (s.get_modified_quant() != 0)
	        s.set_chroma_qscale_table(H263.ff_h263_chroma_qscale_table);

	    s.set_quant_precision(5);

//	    s.get_dsp().ff_set_cmp(s.get_dsp().get_ildct_cmp(), s.get_avctx().get_ildct_cmp());
//	    s.get_dsp().ff_set_cmp(s.get_dsp().get_frame_skip_cmp(), s.get_avctx().get_frame_skip_cmp());

	    //if (CONFIG_H261_ENCODER && s.get_out_format == FMT_H261)
	    if (s.get_out_format() == OutputFormat.FMT_H261)
	        s.ff_h261_encode_init();
	    //if (CONFIG_H263_ENCODER && s.get_out_format == FMT_H263)
	    if (s.get_out_format() == OutputFormat.FMT_H263)
	        s.h263_encode_init();
	    //if (CONFIG_MSMPEG4_ENCODER && s.get_msmpeg4_version)
        if (s.get_msmpeg4_version() != 0)
	        s.ff_msmpeg4_encode_init();
	    //if ((CONFIG_MPEG1VIDEO_ENCODER || CONFIG_MPEG2VIDEO_ENCODER)
	    //    && s.get_out_format == FMT_MPEG1)
	    if (s.get_out_format() == OutputFormat.FMT_MPEG1)
	        s.ff_mpeg1_encode_init();

	    /* init q matrix */
	    for (i = 0 ; i < 64 ; i++) {
	        int j = s.get_dsp().get_idct_permutation()[i];
	        //if(CONFIG_MPEG4_ENCODER && s.get_codec_id==CODEC_ID_MPEG4 && s.get_mpeg_quant){
	        if ( (s.get_codec_id() == CodecID.CODEC_ID_MPEG4) && 
	        	 (s.get_mpeg_quant() != 0) ) {
	            s.get_intra_matrix()[j] = MPEG4Data.ff_mpeg4_default_intra_matrix[i];
	            s.get_inter_matrix()[j] = MPEG4Data.ff_mpeg4_default_non_intra_matrix[i];
	        } else if ( (s.get_out_format() == OutputFormat.FMT_H261) || 
	        		    (s.get_out_format() == OutputFormat.FMT_H263) ) {
	            s.get_intra_matrix()[j] = Mpeg12Data.ff_mpeg1_default_non_intra_matrix[i];
	            s.get_inter_matrix()[j] = Mpeg12Data.ff_mpeg1_default_non_intra_matrix[i];
	        } else { // mpeg1/2 
	            s.get_intra_matrix()[j] = Mpeg12Data.ff_mpeg1_default_intra_matrix[i];
	            s.get_inter_matrix()[j] = Mpeg12Data.ff_mpeg1_default_non_intra_matrix[i];
	        }
	        if (s.get_avctx().get_intra_matrix() != null)
	            s.get_intra_matrix()[j] = s.get_avctx().get_intra_matrix()[i];
	        if (s.get_avctx().get_inter_matrix() != null)
	            s.get_inter_matrix()[j] = s.get_avctx().get_inter_matrix()[i];
	       
	    }

	    /* precompute matrix */
	    /* for mjpeg, we do include qscale in the matrix */
	    if (s.get_out_format() != OutputFormat.FMT_MJPEG) {
	    	MpegVideoEnc.ff_convert_matrix(s.get_dsp(),
	    					  s.get_q_intra_matrix(), 
					          s.get_q_intra_matrix16(),
					          s.get_intra_matrix(), 
					          s.get_intra_quant_bias(), 
					          avctx.get_qmin(), 
					          31, 
					          1);
	    	MpegVideoEnc.ff_convert_matrix(s.get_dsp(),
	    					  s.get_q_inter_matrix(), 
					          s.get_q_inter_matrix16(),
					          s.get_inter_matrix(), 
					          s.get_inter_quant_bias(), 
					          avctx.get_qmin(), 
					          31, 
					          1);
	    }

	    if (s.ff_rate_control_init() < 0)
	        return -1;	    
	    return 0;
		
	}

	public static int MPV_encode_end(AVCodecContext avctx) {
		MpegEncContext s = (MpegEncContext) avctx.get_priv_data();
		 
		s.ff_rate_control_uninit();
		 
		s.MPV_common_end();
		 
	    if ( (Config.CONFIG_MJPEG_ENCODER || Config.CONFIG_LJPEG_ENCODER) && 
	    	 (s.get_out_format() == OutputFormat.FMT_MJPEG) )
	        s.ff_mjpeg_encode_close();

	    avctx.set_extradata(null);
		
	    return 0;
	}

	public static OutOI MPV_encode_picture(AVCodecContext avctx, short[] buf, 
			int buf_size, Object data) {
	    MpegEncContext s = (MpegEncContext) avctx.get_priv_data();
	    AVFrame pic_arg = (AVFrame) data;
	    int i, stuffing_count, context_count = avctx.get_thread_count();

	    for(i = 0 ; i < context_count ; i++){
	        int start_y = s.get_thread_context(i).get_start_mb_y();
	        int   end_y = s.get_thread_context(i).get_end_mb_y();
	        int h = s.get_mb_height();
	        int start = buf_size * start_y / h;
	        int end = buf_size * end_y / h;

	       PutBits.init_put_bits(s.get_thread_context(i).get_pb(), buf);
	    }

	    s.set_picture_in_gop_number(s.get_picture_in_gop_number() + 1);

	    if (load_input_picture(s, pic_arg) < 0)
	        return new OutOI(null, -1);

	    if (select_input_picture(s) < 0)
	        return new OutOI(null, -1);
	    

	    /* output? */
	    if (s.get_new_picture().get_data(0) != null) {
	        s.set_pict_type(s.get_new_picture().get_pict_type());
	//emms_c();
	//printf("qs:%f %f %d\n", s.get_new_picture.quality, s.get_current_picture.quality, s.get_qscale);
	        MpegVideo.MPV_frame_start(s, avctx);
	        
	        boolean vbv_retry = true;
		
	        while (vbv_retry) {
	        	vbv_retry = false;
		        if (encode_picture(s, s.get_picture_number()) < 0)
			        return new OutOI(null, -1);
	
		        avctx.set_header_bits(s.get_header_bits());
		        avctx.set_mv_bits(s.get_mv_bits());
		        avctx.set_misc_bits(s.get_misc_bits());
		        avctx.set_i_tex_bits(s.get_i_tex_bits());
		        avctx.set_p_tex_bits(s.get_p_tex_bits());
		        avctx.set_i_count(s.get_i_count());
		        avctx.set_p_count(s.get_mb_num() - s.get_i_count() - s.get_skip_count()); //FIXME f/b_count in avctx
		        avctx.set_skip_count(s.get_skip_count());
	
		        MpegVideo.MPV_frame_end(s);
	
		        if (Config.CONFIG_MJPEG_ENCODER && s.get_out_format() == OutputFormat.FMT_MJPEG)
		            MjpegEnc.ff_mjpeg_encode_picture_trailer(s);
	
		        if (avctx.get_rc_buffer_size() != 0) {
		            RateControlContext rcc = s.get_rc_context();
		            int max_size = (int) (rcc.get_buffer_index() * avctx.get_rc_max_available_vbv_use());
	
		            if (PutBits.put_bits_count(s.get_pb()) > max_size && 
		            	s.get_lambda() < s.get_avctx().get_lmax()) {
		                s.set_next_lambda((int) Mathematics.FFMAX(s.get_lambda() + 1, s.get_lambda() * (s.get_qscale() + 1) / s.get_qscale()));
		                if (s.get_adaptive_quant() != 0) {
		                    for (i = 0 ; i < s.get_mb_height() * s.get_mb_stride() ; i++)
		                        s.get_lambda_table()[i] = (int) Mathematics.FFMAX(s.get_lambda_table()[i]+1, 
		                        		s.get_lambda_table()[i] * (s.get_qscale() + 1) / s.get_qscale());
		                }
		                s.set_mb_skipped(0);        //done in MPV_frame_start()
		                if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_P) { //done in encode_picture() so we must undo it
		                    if (s.get_flipflop_rounding() != 0 || 
		                    	s.get_codec_id() == CodecID.CODEC_ID_H263P || 
		                    	s.get_codec_id() == CodecID.CODEC_ID_MPEG4)
		                        s.set_no_rounding(s.get_no_rounding() ^ 1);
		                }
		                if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B) {
		                    s.set_time_base(s.get_last_time_base());
		                    s.set_last_non_b_time(s.get_time() - s.get_pp_time());
		                }
	
		                for (i = 0 ; i < context_count ; i++) {
		                    PutBitContext pb = s.get_thread_context(i).get_pb();
		                    PutBits.init_put_bits(pb, pb.get_buf());
		                }
		                vbv_retry = true;
		            }
		        }
	        }

	        if (s.has_flag(AVCodec.CODEC_FLAG_PASS1))
	            RateControl.ff_write_pass1_stats(s);

	        for(i=0; i<4; i++){
	            s.get_current_picture_ptr().get_error()[i] = s.get_current_picture().get_error(i);
	            avctx.get_error()[i] += s.get_current_picture_ptr().get_error()[i];
	        }

	        PutBits.flush_put_bits(s.get_pb());
	        s.set_frame_bits(PutBits.put_bits_count(s.get_pb()));

	        stuffing_count = RateControl.ff_vbv_update(s, s.get_frame_bits());
	        if (stuffing_count != 0) {
	            if (s.get_pb().get_buf_end() - 0 /*s.get_pb.buf*/ - (PutBits.put_bits_count(s.get_pb()) >> 3) < stuffing_count + 50) {
	                Log.av_log("AVcodecContext", Log.AV_LOG_ERROR, "stuffing too large\n");
	    	        return new OutOI(null, -1);
	            }

	            switch (s.get_codec_id()) {
	            case CODEC_ID_MPEG1VIDEO:
	            case CODEC_ID_MPEG2VIDEO:
	                while (stuffing_count-- != 0){
	                	PutBits.put_bits(s.get_pb(), 8, 0);
	                }
	            break;
	            case CODEC_ID_MPEG4:
	            	PutBits.put_bits(s.get_pb(), 16, 0);
	            	PutBits.put_bits(s.get_pb(), 16, 0x1C3);
	                stuffing_count -= 4;
	                while(stuffing_count-- != 0){
	                	PutBits.put_bits(s.get_pb(), 8, 0xFF);
	                }
	            break;
	            default:
	            	Log.av_log("AVcodecContext", Log.AV_LOG_ERROR, "vbv buffer overflow\n");
	            }
	            PutBits.flush_put_bits(s.get_pb());
	            s.set_frame_bits(PutBits.put_bits_count(s.get_pb()));
	        }

	        /* update mpeg1/2 vbv_delay for CBR */
	        if (s.get_avctx().get_rc_max_rate() != 0 && 
	        	s.get_avctx().get_rc_min_rate() == s.get_avctx().get_rc_max_rate() && 
	        	s.get_out_format() == OutputFormat.FMT_MPEG1 && 
	        	90000L * (avctx.get_rc_buffer_size() - 1) <= s.get_avctx().get_rc_max_rate() * 0xFFFFL) {
	            int vbv_delay, min_delay;
	            double inbits  = s.get_avctx().get_rc_max_rate() * s.get_avctx().get_time_base().av_q2d();
	            int    minbits = s.get_frame_bits() - 8 * (s.get_vbv_delay_ptr() - 0/*s.get_pb.buf*/ - 1);
	            double bits    = s.get_rc_context().get_buffer_index() + minbits - inbits;

	            if (bits < 0)
	            	Log.av_log("AVcodecContext", Log.AV_LOG_ERROR, "Internal error, negative bits\n");

	            vbv_delay = (int) (bits * 90000 / s.get_avctx().get_rc_max_rate());
	            min_delay = (int) ((minbits * 90000L + s.get_avctx().get_rc_max_rate() - 1) / s.get_avctx().get_rc_max_rate());

	            vbv_delay = (int) Mathematics.FFMAX(vbv_delay, min_delay);

	            /* TODO Jerome
	            s.get_vbv_delay()[s.get_vbv_delay_ptr()+0] &= 0xF8;
	            s.get_vbv_delay()[s.get_vbv_delay_ptr()+0] |= vbv_delay>>13;
	            s.get_vbv_delay()[s.get_vbv_delay_ptr()+1]  = vbv_delay>>5;
	            s.get_vbv_delay()[s.get_vbv_delay_ptr()+2] &= 0x07;
	            s.get_vbv_delay()[s.get_vbv_delay_ptr()+2] |= vbv_delay<<3;
	            */
	            avctx.set_vbv_delay(vbv_delay * 300);
	        }
	        s.set_total_bits(s.get_total_bits() + s.get_frame_bits());
	        avctx.set_frame_bits(s.get_frame_bits());
	    } else {
	        s.set_frame_bits(0);
	    }

	    return new OutOI(buf, s.get_frame_bits() / 8);
	}

	private static int encode_picture(MpegEncContext s, int picture_number) {
	    int i;
	    int bits;
	    int context_count = s.get_avctx().get_thread_count();

	    s.set_picture_number(picture_number);

	    /* Reset the average MB variance */
	    s.get_me().set_mb_var_sum_temp(0);
	    s.get_me().set_mc_mb_var_sum_temp(0);

	    /* we need to initialize some time vars before we can encode b-frames */
	    // RAL: Condition added for MPEG1VIDEO
	    if (s.get_codec_id() == CodecID.CODEC_ID_MPEG1VIDEO || 
	    	s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO || 
	    	(s.get_h263_pred() != 0 && s.get_msmpeg4_version() == 0))
	        set_frame_distances(s);
	    if (Config.CONFIG_MPEG4_ENCODER && s.get_codec_id() == CodecID.CODEC_ID_MPEG4)
	        Mpeg4VideoEnc.ff_set_mpeg4_time(s);

	    s.get_me().set_scene_change_score(0);

//	    s->lambda= s->current_picture_ptr->quality; //FIXME qscale / ... stuff for ME rate distortion

	    if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I) {
	        if (s.get_msmpeg4_version() >= 3) 
	        	s.set_no_rounding(1);
	        else                   
	        	s.set_no_rounding(0);
	    } else if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B) {
	        if (s.get_flipflop_rounding() != 0 || 
	        	s.get_codec_id() == CodecID.CODEC_ID_H263P || 
	        	s.get_codec_id() == CodecID.CODEC_ID_MPEG4)
	        	s.set_no_rounding(s.get_no_rounding() ^ 1);
	    }

	    if (s.has_flag(AVCodec.CODEC_FLAG_PASS2)) {
	        if (estimate_qp(s,1) < 0)
	            return -1;
	        RateControl.ff_get_2pass_fcode(s);
	    } else if (!s.has_flag(AVCodec.CODEC_FLAG_QSCALE)) {
	        if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B)
	            s.set_lambda(s.get_last_lambda_for(s.get_pict_type().ordinal()));
	        else
	            s.set_lambda(s.get_last_lambda_for(s.get_last_non_b_pict_type().ordinal() ));
	        update_qscale(s);
	    }

	    s.set_mb_intra(0); //for the rate distortion & bit compare functions
	    for (i = 1 ; i < context_count ; i++) {
	        MpegVideo.ff_update_duplicate_context(s.get_thread_context(i), s);
	    }

	    if (MotionEst.ff_init_me(s) < 0)
	        return -1;

	    /* Estimate motion for every MB */
	    if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_I) {
	    	s.set_lambda((s.get_lambda() * s.get_avctx().get_me_penalty_compensation() + 128)>>8);
            s.set_lambda2((s.get_lambda2() * s.get_avctx().get_me_penalty_compensation() + 128)>>8);
	        if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B && s.get_avctx().get_me_threshold() == 0) {
	            if ((s.get_avctx().get_pre_me() != 0 && s.get_last_non_b_pict_type() == AVPictureType.AV_PICTURE_TYPE_I) || 
	            	 s.get_avctx().get_pre_me() == 2) {
	                s.get_avctx().execute(s.get_avctx(), "pre_estimate_motion_thread", s.get_thread_context(0), 
	                		null, context_count);
	            }
	        }

	        s.get_avctx().execute(s.get_avctx(), "estimate_motion_thread", s.get_thread_context(0), null, context_count);
	    } else { // if(s->pict_type == AV_PICTURE_TYPE_I) 
	        // I-Frame 
	        for (i = 0 ; i < s.get_mb_stride() * s.get_mb_height() ; i++)
	            s.set_mb_type(i, MpegVideo.CANDIDATE_MB_TYPE_INTRA);

	        if (s.get_fixed_qscale() == 0){
	            // finding spatial complexity for I-frame rate control 
		        //s.get_avctx().execute(s.get_avctx(), "mb_var_thread", s.get_thread_context(0), null, context_count);
	        	mb_var_thread(s.get_avctx(), s);
	        }
	    }
	    
	    /*for (i = 1 ; i < context_count ; i++) {
	        merge_context_after_me(s, s.get_thread_context(i));
	    }
	    s.get_current_picture().set_mc_mb_var_sum(s.get_me().get_mc_mb_var_sum_temp());
	    s.get_current_picture_ptr().set_mc_mb_var_sum(s.get_me().get_mc_mb_var_sum_temp());
	    s.get_current_picture().set_mb_var_sum(s.get_me().get_mb_var_sum_temp());
	    s.get_current_picture_ptr.set_mb_var_sum(s.get_me().get_mb_var_sum_temp());
*/
	    /*if (s.get_me().get_scene_change_score() > s.get_avctx().get_scenechange_threshold() && 
	    	s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_P) {
	        s.set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	        for (i = 0 ; i < s.get_mb_stride() * s.get_mb_height() ; i++)
	            s.set_mb_type(i, CANDIDATE_MB_TYPE_INTRA);
	    }*/

	   /* if (s.get_umvplus() == 0) {
	        if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_P || 
	        	s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_S) {
	            s.set_f_code(ff_get_best_fcode(s, s.get_p_mv_table(), CANDIDATE_MB_TYPE_INTER));

	            if (s.has_flag(AVCodec.CODEC_FLAG_INTERLACED_ME)) {
	                int a,b;
	                a = ff_get_best_fcode(s, s.get_p_field_mv_table()[0][0], CANDIDATE_MB_TYPE_INTER_I); //FIXME field_select
	                b = ff_get_best_fcode(s, s.get_p_field_mv_table()[1][1], CANDIDATE_MB_TYPE_INTER_I);
	                s.set_f_code(Mathematics.FFMAX3(s.get_f_code(), a, b));
	            }

	            ff_fix_long_p_mvs(s);
	            ff_fix_long_mvs(s, null, 0, s.get_p_mv_table(), s.get_f_code(), CANDIDATE_MB_TYPE_INTER, 0);
	            if (s.has_flag(AVCodec.CODEC_FLAG_INTERLACED_ME)) {
	                int j;
	                for (i = 0 ; i < 2 ; i++) {
	                    for (j = 0 ; j < 2 ; j++)
	                        ff_fix_long_mvs(s, s.get_p_field_select_table()[i], j,
	                                        s.get_p_field_mv_table()[i][j], s.get_f_code(), CANDIDATE_MB_TYPE_INTER_I, 0);
	                }
	            }
	        }

	        if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) {
	            int a, b;

	            a = ff_get_best_fcode(s, s.get_b_forw_mv_table(), CANDIDATE_MB_TYPE_FORWARD);
	            b = ff_get_best_fcode(s, s.get_b_bidir_forw_mv_table(), CANDIDATE_MB_TYPE_BIDIR);
	            s.set_f_code(Mathematics.FFMAX(a, b));

	            a = ff_get_best_fcode(s, s.get_b_back_mv_table(), CANDIDATE_MB_TYPE_BACKWARD);
	            b = ff_get_best_fcode(s, s.get_b_bidir_back_mv_table(0), CANDIDATE_MB_TYPE_BIDIR);
	            s.set_b_code(Mathematics.FFMAX(a, b));

	            ff_fix_long_mvs(s, null, 0, s.get_b_forw_mv_table(), s.get_f_code(), CANDIDATE_MB_TYPE_FORWARD, 1);
	            ff_fix_long_mvs(s, null, 0, s.get_b_back_mv_table(), s.get_b_code(), CANDIDATE_MB_TYPE_BACKWARD, 1);
	            ff_fix_long_mvs(s, null, 0, s.get_b_bidir_forw_mv_table(), s.get_f_code(), CANDIDATE_MB_TYPE_BIDIR, 1);
	            ff_fix_long_mvs(s, null, 0, s.get_b_bidir_back_mv_table(), s.get_b_code(), CANDIDATE_MB_TYPE_BIDIR, 1);
	            if (s.has_flag(AVCodec.CODEC_FLAG_INTERLACED_ME))  {
	                int dir, j;
	                for (dir = 0 ; dir < 2 ; dir++) {
	                    for (i = 0 ; i < 2 ; i++) {
	                        for (j = 0 ; j < 2 ; j++) {
	                            int type = dir != 0 ? (CANDIDATE_MB_TYPE_BACKWARD_I|CANDIDATE_MB_TYPE_BIDIR_I)
	                                          : (CANDIDATE_MB_TYPE_FORWARD_I |CANDIDATE_MB_TYPE_BIDIR_I);
	                            ff_fix_long_mvs(s, s.get_b_field_select_table()[dir][i], j,
	                                            s.get_b_field_mv_table()[dir][i][j], 
	                                            dir != 0 ? s.get_b_code() : s.get_f_code(), type, 1);
	                        }
	                    }
	                }
	            }
	        }
	    }*/

	    if (estimate_qp(s, 0) < 0)
	        return -1;

	    if (s.get_qscale() < 3 && 
	    	s.get_max_qcoeff() <= 128 && 
	    	s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I && 
	    	!s.has_flag(AVCodec.CODEC_FLAG_QSCALE))
	        s.set_qscale(3); //reduce clipping problems

	    if (s.get_out_format() == OutputFormat.FMT_MJPEG) {
	        // for mjpeg, we do include qscale in the matrix 
	        for (i = 1 ; i < 64 ; i++) {
	            int j = s.get_dsp().get_idct_permutation()[i];

	            s.set_intra_matrix(j, Common.av_clip_uint8((Mpeg12Data.ff_mpeg1_default_intra_matrix[i] * s.get_qscale()) >> 3));
	        }
	        s.set_y_dc_scale_table(MpegVideo.ff_mpeg2_dc_scale_table[s.get_intra_dc_precision()]);
	        s.set_c_dc_scale_table(MpegVideo.ff_mpeg2_dc_scale_table[s.get_intra_dc_precision()]);
	        s.set_intra_matrix(0, MpegVideo.ff_mpeg2_dc_scale_table[s.get_intra_dc_precision()][8]);
	        ff_convert_matrix(s.get_dsp(), s.get_q_intra_matrix(), s.get_q_intra_matrix16(),
	                       s.get_intra_matrix(), s.get_intra_quant_bias(), 8, 8, 1);
	        s.set_qscale(8);
	    }

	    //FIXME var duplication
	    s.get_current_picture_ptr().set_key_frame(s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I ? 1 : 0);
	    s.get_current_picture().set_key_frame(s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I ? 1 : 0); //FIXME pic_ptr
	    s.get_current_picture_ptr().set_pict_type(s.get_pict_type());
	    s.get_current_picture().set_pict_type(s.get_pict_type());

	    if (s.get_current_picture().get_key_frame() != 0)
	        s.set_picture_in_gop_number(0);

	    s.set_last_bits(PutBits.put_bits_count(s.get_pb()));
	    switch (s.get_out_format()) {
	    case FMT_MJPEG:
	        if (Config.CONFIG_MJPEG_ENCODER)
	            MjpegEnc.ff_mjpeg_encode_picture_header(s);
	        break;
	    /*case FMT_H261:
	        if (Config.CONFIG_H261_ENCODER)
	            ff_h261_encode_picture_header(s, picture_number);
	        break;
	    case FMT_H263:
	        if (Config.CONFIG_WMV2_ENCODER && s.get_codec_id() == CodecIC.CODEC_ID_WMV2)
	            ff_wmv2_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_MSMPEG4_ENCODER && s.get_msmpeg4_version() != 0)
	            msmpeg4_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_MPEG4_ENCODER && s.get_h263_pred() != 0)
	            mpeg4_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_RV10_ENCODER && s.get_codec_id() == CODEC_ID_RV10)
	            rv10_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_RV20_ENCODER && s.get_codec_id() == CODEC_ID_RV20)
	            rv20_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_FLV_ENCODER && s.get_codec_id() == CODEC_ID_FLV1)
	            ff_flv_encode_picture_header(s, picture_number);
	        else if (Config.CONFIG_H263_ENCODER)
	            h263_encode_picture_header(s, picture_number);
	        break;
	    case FMT_MPEG1:
	        if (Config.CONFIG_MPEG1VIDEO_ENCODER || Config.CONFIG_MPEG2VIDEO_ENCODER)
	            mpeg1_encode_picture_header(s, picture_number);
	        break;
	    case FMT_H264:
	        break;*/
	    }
	   /* bits = put_bits_count(s.get_pb());
	    s.set_header_bits(bits - s.get_last_bits());

	    for (i = 1 ; i < context_count ; i++) {
	        update_duplicate_context_after_me(s.get_thread_context(i), s);
	    }
	    s.get_avctx().execute(s.get_avctx(), encode_thread, s.get_thread_context(0), null, 
	    		context_count);
	    for (i = 1 ; i < context_count ; i++) {
	        merge_context_after_encode(s, s.get_thread_context(i));
	    }
*/
	    return 0;
	}

	private static int mb_var_thread(AVCodecContext c, MpegEncContext s) {
	    int mb_x, mb_y;

	    for (mb_y = s.get_start_mb_y() ; mb_y < s.get_end_mb_y(); mb_y++) {
	        for (mb_x = 0 ; mb_x < s.get_mb_width() ; mb_x++) {
	            int xx = mb_x * 16;
	            int yy = mb_y * 16;
	            short [] pix = Arrays.copyOfRange(s.get_new_picture().get_data(0),
	            								  (yy * s.get_linesize()) + xx,
	            								  s.get_new_picture().get_data(0).length);
	            int varc;
	            int sum = s.get_dsp().pix_sum(pix, s.get_linesize());

	            varc = (s.get_dsp().pix_norm1(pix, s.get_linesize()) - (((sum*sum))>>8) + 500 + 128)>>8;

	            s.get_current_picture().get_mb_var()[s.get_mb_stride() * mb_y + mb_x] = varc;
	            s.get_current_picture().get_mb_mean()[s.get_mb_stride() * mb_y + mb_x] = (byte) ((sum+128)>>8);
	            s.get_me().set_mb_var_sum_temp(s.get_me().get_mb_var_sum_temp() + varc);
	        }
	    }
	    return 0;
	    
		
	}

	private static int estimate_qp(MpegEncContext s, int dry_run) {
	    if (s.get_next_lambda() != 0) {
	        s.get_current_picture_ptr().set_quality(s.get_next_lambda());
	        s.get_current_picture().set_quality(s.get_next_lambda());
	        if (dry_run == 0) 
	        	s.set_next_lambda(0);
	    } else if (s.get_fixed_qscale() == 0) {
	        s.get_current_picture_ptr().set_quality((int) RateControl.ff_rate_estimate_qscale(s, dry_run));
	        s.get_current_picture().set_quality((int) RateControl.ff_rate_estimate_qscale(s, dry_run));
	        if (s.get_current_picture().get_quality() < 0)
	            return -1;
	    }

	    if (s.get_adaptive_quant() != 0) {
	        switch(s.get_codec_id()) {
	        case CODEC_ID_MPEG4:
	            if (Config.CONFIG_MPEG4_ENCODER)
	                Mpeg4VideoEnc.ff_clean_mpeg4_qscales(s);
	            break;
	        case CODEC_ID_H263:
	        case CODEC_ID_H263P:
	        case CODEC_ID_FLV1:
	            if (Config.CONFIG_H263_ENCODER)
	                Ituh263Enc.ff_clean_h263_qscales(s);
	            break;
	        default:
	            ff_init_qscale_tab(s);
	        }

	        s.set_lambda(s.get_lambda_table()[0]);
	        //FIXME broken
	    } else
	        s.set_lambda(s.get_current_picture().get_quality());
	    
	    update_qscale(s);
	    return 0;
	}

	private static void update_qscale(MpegEncContext s) {
	    s.set_qscale( (s.get_lambda() * 139 + AVUtil.FF_LAMBDA_SCALE * 64) >> (AVUtil.FF_LAMBDA_SHIFT + 7) );
	    s.set_qscale( Common.av_clip(s.get_qscale(), s.get_avctx().get_qmin(), s.get_avctx().get_qmax()) );

	    s.set_lambda2( (s.get_lambda() * s.get_lambda() + AVUtil.FF_LAMBDA_SCALE / 2) >> AVUtil.FF_LAMBDA_SHIFT );
	}

	private static void ff_init_qscale_tab(MpegEncContext s) {
	    byte [] qscale_table= s.get_current_picture().get_qscale_table();
	    int i;

	    for (i = 0 ; i < s.get_mb_num() ; i++) {
	        int lam = s.get_lambda_table()[ s.get_mb_index2xy()[i] ];
	        int qp = (lam*139 + AVUtil.FF_LAMBDA_SCALE * 64) >> (AVUtil.FF_LAMBDA_SHIFT + 7);
	        qscale_table[ s.get_mb_index2xy()[i] ] = (byte) Common.av_clip(qp, s.get_avctx().get_qmin(), s.get_avctx().get_qmax());
	    }
	}

	private static void set_frame_distances(MpegEncContext s) {
	    s.set_time(s.get_current_picture_ptr().get_pts() * s.get_avctx().get_time_base().get_num());

	    if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) {
	        s.set_pb_time((int) (s.get_pp_time() - (s.get_last_non_b_time() - s.get_time())));
	    } else {
	        s.set_pp_time((int) (s.get_time() - s.get_last_non_b_time()));
	        s.set_last_non_b_time(s.get_time());
	    }		
	}

	private static int select_input_picture(MpegEncContext s) {
	    int i;

	    for (i = 1 ; i < MpegVideo.MAX_PICTURE_COUNT ; i++)
	        s.set_reordered_input_picture(i-1, s.get_reordered_input_picture(i));
	    s.set_reordered_input_picture(MpegVideo.MAX_PICTURE_COUNT-1, null);

	    /* set next picture type & ordering */
	    if ( (s.get_reordered_input_picture(0) == null) && (s.get_input_picture(0) != null) ) {
	        if (/*s->picture_in_gop_number >= s->gop_size ||*/ 
	        	(s.get_next_picture_ptr() == null) || 
	        	(s.get_intra_only() != 0) ) {
	            s.set_reordered_input_picture(0, s.get_input_picture(0));
	            s.get_reordered_input_picture(0).set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	            s.set_coded_picture_number(s.get_coded_picture_number() + 1);
	            s.get_reordered_input_picture(0).set_coded_picture_number(s.get_coded_picture_number());
	        } else {
	            int b_frames;

	            if ( (s.get_avctx().get_frame_skip_threshold() != 0) || 
	            	 (s.get_avctx().get_frame_skip_factor() != 0) ) {
	                if ( (s.get_picture_in_gop_number() < s.get_gop_size()) && 
	                	 (skip_check(s, s.get_input_picture(0), s.get_next_picture_ptr()) != 0) ) {
	                //FIXME check that te gop check above is +-1 correct
	//av_log(null, AV_LOG_DEBUG, "skip %p %"PRId64"\n", s.get_input_picture(0)->data[0], s.get_input_picture(0)->pts);

	                    if (s.get_input_picture(0).get_type() == AVCodec.FF_BUFFER_TYPE_SHARED){
	                        for (i = 0 ; i < 4 ; i++)
	                            s.get_input_picture(0).set_data(i, null);
	                        s.get_input_picture(0).set_type(0);
	                    } else {
	                        s.get_avctx().release_buffer((AVFrame) s.get_input_picture(0));
	                    }
	                    RateControl.ff_vbv_update(s, 0);
	                    
	                    if (s.get_reordered_input_picture(0) != null) {
	                        s.get_reordered_input_picture(0).set_reference(s.get_reordered_input_picture(0).get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B ? 3 : 0);

	                        s.set_new_picture(MpegVideo.ff_copy_picture(s.get_reordered_input_picture(0)));

	                        if ( (s.get_reordered_input_picture(0).get_type() == AVCodec.FF_BUFFER_TYPE_SHARED) || 
	                        	 (s.get_avctx().get_rc_buffer_size() != 0) ){
	                            // input is a shared pix, so we can't modifiy it -> 
	                        	// alloc a new one & ensure that the shared one is reuseable

	                            i = MpegVideo.ff_find_unused_picture(s, 0);
	                            Picture pic = (Picture) s.get_picture(i);

	                            pic.set_reference(s.get_reordered_input_picture(0).get_reference());
	                            if (MpegVideo.ff_alloc_picture(s, pic, 0) < 0) {
	                                return -1;
	                            }

	                            /* mark us unused / free shared pic */
	                            if (s.get_reordered_input_picture(0).get_type() == AVCodec.FF_BUFFER_TYPE_INTERNAL)
	                                s.get_avctx().release_buffer((AVFrame)s.get_reordered_input_picture(0));
	                            for (i = 0 ; i < 4 ; i++)
	                                s.get_reordered_input_picture(0).set_data(i, null);
	                            s.get_reordered_input_picture(0).set_type(0);

	                            copy_picture_attributes(s, (AVFrame) pic, (AVFrame) s.get_reordered_input_picture(0));

	                            s.set_current_picture_ptr(pic);
	                        } else {
	                            // input is not a shared pix -> reuse buffer for current_pix

	                            s.set_current_picture_ptr(s.get_reordered_input_picture(0));
	                            for (i = 0 ; i < 4 ; i++){
	                            	if (s.get_new_picture().get_data(i) != null)
		                                s.get_new_picture().set_data(i, Arrays.copyOfRange(s.get_new_picture().get_data(i), 
		                                												   MpegVideo.INPLACE_OFFSET, 
		                                		                                           s.get_new_picture().get_data(i).length) );
	                            }
	                        }
	                        s.set_current_picture(MpegVideo.ff_copy_picture(s.get_current_picture_ptr()));

	                        s.set_picture_number(s.get_new_picture().get_display_picture_number());
	                //		printf("dpn:%d\n", s->picture_number);
	                    } else {
	                       s.set_new_picture(null);
	                    }
	                    return 0;
	                }
	            }

	            if (s.has_flag(AVCodec.CODEC_FLAG_PASS2)) {
	                for (i = 0 ; i < s.get_max_b_frames() + 1 ; i++){
	                    int pict_num = s.get_input_picture(0).get_display_picture_number() + i;

	                    if (pict_num >= s.get_rc_context().get_num_entries())
	                        break;
	                    if (s.get_input_picture(i) == null){
	                        s.get_rc_context().get_entry(pict_num-1).set_new_pict_type(AVPictureType.AV_PICTURE_TYPE_P);
	                        break;
	                    }

	                    s.get_input_picture(i).set_pict_type(
	                        s.get_rc_context().get_entry(pict_num).get_new_pict_type());
	                }
	            }

	            if (s.get_avctx().get_b_frame_strategy() == 0) {
	                b_frames = s.get_max_b_frames();
	                while ( (b_frames != 0) && (s.get_input_picture(b_frames) == null) ) 
	                	b_frames--;
	            } else if (s.get_avctx().get_b_frame_strategy() == 1) {
	                for (i = 1 ; i < s.get_max_b_frames() + 1 ; i++) {
	                    if ( (s.get_input_picture(i) != null) && 
	                    	 (s.get_input_picture(i).get_b_frame_score() == 0) ) {
	                        s.get_input_picture(i).set_b_frame_score(
	                            get_intra_count(s, s.get_input_picture(i  ).get_data(0),
	                                               s.get_input_picture(i-1).get_data(0), 
	                                               s.get_linesize()) + 1);
	                    }
	                }
	                for (i = 0 ; i < s.get_max_b_frames() + 1 ; i++) {
	                    if ( (s.get_input_picture(i) == null) || 
	                    	 (s.get_input_picture(i).get_b_frame_score() - 1 > s.get_mb_num() / s.get_avctx().get_b_sensitivity()) )
	                    	break;
	                }

	                b_frames = (int) Mathematics.FFMAX(0, i-1);

	                /* reset scores */
	                for (i = 0 ; i < b_frames + 1 ; i++) {
	                    s.get_input_picture(i).set_b_frame_score(0);
	                }
	            } else if (s.get_avctx().get_b_frame_strategy() == 2) {
	                b_frames = estimate_best_b_count(s);
	            } else {
	                Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "illegal b frame strategy\n");
	                b_frames = 0;
	            }

	            //static int b_count=0;
	//b_count+= b_frames;
	//av_log(s.get_avctx(), AV_LOG_DEBUG, "b_frames: %d\n", b_count);

	            for (i = b_frames - 1 ; i >= 0 ; i--) {
	                AVPictureType type = s.get_input_picture(i).get_pict_type();
	                if (type != AVPictureType.AV_PICTURE_TYPE_B)
	                    b_frames = i;
	            }
	            if ( (s.get_input_picture(b_frames).get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) && 
	            	  (b_frames == s.get_max_b_frames()) ) {
	                Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "warning, too many b frames in a row\n");
	            }

	            if (s.get_picture_in_gop_number() + b_frames >= s.get_gop_size()) {
	              if ( ((s.get_flags2() & AVCodec.CODEC_FLAG2_STRICT_GOP) != 0) && 
	            		(s.get_gop_size() > s.get_picture_in_gop_number()) ){
	                    b_frames = s.get_gop_size() - s.get_picture_in_gop_number() - 1;
	              } else {
	                if (s.has_flag(AVCodec.CODEC_FLAG_CLOSED_GOP))
	                    b_frames = 0;
	                s.get_input_picture(b_frames).set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	              }
	            }

	            if ( s.has_flag(AVCodec.CODEC_FLAG_CLOSED_GOP) &&
	                 (b_frames != 0) &&
	                 (s.get_input_picture(b_frames).get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I) )
	                b_frames--;

	            s.set_reordered_input_picture(0, s.get_input_picture(b_frames));
	            if (s.get_reordered_input_picture(0).get_pict_type() != AVPictureType.AV_PICTURE_TYPE_I)
	                s.get_reordered_input_picture(0).set_pict_type(AVPictureType.AV_PICTURE_TYPE_P);
	            
	            s.set_coded_picture_number(s.get_coded_picture_number() + 1);
	            s.get_reordered_input_picture(0).set_coded_picture_number(s.get_coded_picture_number());
	            
	            for (i = 0 ; i < b_frames ; i++) {
	                s.set_reordered_input_picture(i+1, s.get_input_picture(i));
	                s.get_reordered_input_picture(i+1).set_pict_type(AVPictureType.AV_PICTURE_TYPE_B);
		            s.set_coded_picture_number(s.get_coded_picture_number() + 1);
	                s.get_reordered_input_picture(i+1).set_coded_picture_number(s.get_coded_picture_number());
	            }
	        }
	    }

		if (s.get_reordered_input_picture(0) != null) {
            s.get_reordered_input_picture(0).set_reference(s.get_reordered_input_picture(0).get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B ? 3 : 0);

            s.set_new_picture(MpegVideo.ff_copy_picture(s.get_reordered_input_picture(0)));

            if ( (s.get_reordered_input_picture(0).get_type() == AVCodec.FF_BUFFER_TYPE_SHARED) || 
            	 (s.get_avctx().get_rc_buffer_size() != 0) ){
                // input is a shared pix, so we can't modifiy it -> 
            	// alloc a new one & ensure that the shared one is reuseable

                i = MpegVideo.ff_find_unused_picture(s, 0);
                Picture pic = (Picture) s.get_picture(i);

                pic.set_reference(s.get_reordered_input_picture(0).get_reference());
                if (MpegVideo.ff_alloc_picture(s, pic, 0) < 0) {
                    return -1;
                }

                /* mark us unused / free shared pic */
                if (s.get_reordered_input_picture(0).get_type() == AVCodec.FF_BUFFER_TYPE_INTERNAL)
                    s.get_avctx().release_buffer((AVFrame)s.get_reordered_input_picture(0));
                for (i = 0 ; i < 4 ; i++)
                    s.get_reordered_input_picture(0).set_data(i, null);
                s.get_reordered_input_picture(0).set_type(0);

                copy_picture_attributes(s, (AVFrame) pic, (AVFrame) s.get_reordered_input_picture(0));

                s.set_current_picture_ptr(pic);
            } else {
                // input is not a shared pix -> reuse buffer for current_pix

                s.set_current_picture_ptr(s.get_reordered_input_picture(0));
                for (i = 0 ; i < 4 ; i++){
                	if (s.get_new_picture().get_data(i) != null)
	                    s.get_new_picture().set_data(i, Arrays.copyOfRange(s.get_new_picture().get_data(i), 
	                    												   MpegVideo.INPLACE_OFFSET, 
	                    		                                           s.get_new_picture().get_data(i).length) );
                }
            }
            s.set_current_picture(MpegVideo.ff_copy_picture(s.get_current_picture_ptr()));

            s.set_picture_number(s.get_new_picture().get_display_picture_number());
    //		printf("dpn:%d\n", s->picture_number);
        } else {
           s.set_new_picture(new Picture());
        }
	    return 0;
	}

	private static int estimate_best_b_count(MpegEncContext s) {
	    AVCodec codec = AVCodec.avcodec_find_encoder(s.get_avctx().get_codec_id());
	    AVCodecContext c = AVCodecContext.avcodec_alloc_context();
	    AVFrame [] input = new AVFrame[AVCodec.FF_MAX_B_FRAMES+2];
	    int scale = s.get_avctx().get_brd_scale();
	    int i, j, out_size, p_lambda, b_lambda, lambda2;
	    int outbuf_size = s.get_width() * s.get_height(); //FIXME
	    short [] outbuf = new short[outbuf_size];
	    long best_rd = Long.MAX_VALUE;
	    int best_b_count = -1;

	    p_lambda = s.get_last_lambda_for(AVPictureType.AV_PICTURE_TYPE_P.ordinal()); //s->next_picture_ptr->quality;
	    b_lambda = s.get_last_lambda_for(AVPictureType.AV_PICTURE_TYPE_B.ordinal()); //p_lambda *FFABS(s.get_avctx()->b_quant_factor) + s.get_avctx()->b_quant_offset;
	    if (b_lambda == 0) 
	    	b_lambda = p_lambda; //FIXME we should do this somewhere else
	    lambda2= (b_lambda*b_lambda + (1 << AVUtil.FF_LAMBDA_SHIFT)/2 ) >> AVUtil.FF_LAMBDA_SHIFT;

	    c.set_width(s.get_width() >> scale);
	    c.set_height(s.get_height()>> scale);
	    c.set_flags(AVCodec.CODEC_FLAG_QSCALE | AVCodec.CODEC_FLAG_PSNR | AVCodec.CODEC_FLAG_INPUT_PRESERVED /*| AVCodec.CODEC_FLAG_EMU_EDGE*/);
	    c.set_flags(c.get_flags() | (s.get_avctx().get_flags() & AVCodec.CODEC_FLAG_QPEL));
	    c.set_mb_decision(s.get_avctx().get_mb_decision());
	    c.set_me_cmp(s.get_avctx().get_me_cmp());
	    c.set_mb_cmp(s.get_avctx().get_mb_cmp());
	    c.set_me_sub_cmp(s.get_avctx().get_me_sub_cmp());
	    c.set_pix_fmt(PixelFormat.PIX_FMT_YUV420P);
	    c.set_time_base(s.get_avctx().get_time_base());
	    c.set_max_b_frames(s.get_max_b_frames());

	    if (c.avcodec_open(codec) < 0)
	        return -1;

	    for (i = 0 ; i < s.get_max_b_frames() + 2 ; i++) {
	        int ysize = c.get_width() * c.get_height();
	        int csize = (c.get_width() / 2) * (c.get_height() / 2);
	        Picture pre_input, pre_input_ptr = (i != 0) ? s.get_input_picture(i-1) : s.get_next_picture_ptr();

	        input[i] = UtilsCodec.avcodec_get_frame_defaults();
	        input[i].set_data(0, new short[ysize + 2*csize]);
	        input[i].set_data(1, Arrays.copyOfRange(input[i].get_data(0), ysize, input[i].get_data(0).length));
	        input[i].set_data(2, Arrays.copyOfRange(input[i].get_data(1), csize, input[i].get_data(1).length));
	        input[i].set_linesize(0, c.get_width());
	        input[i].set_linesize(1, c.get_width() / 2);
	        input[i].set_linesize(2, c.get_width() / 2);

	        if ( (pre_input_ptr != null) && ( (i == 0) || (s.get_input_picture(i-1) != null) ) ) {
	            pre_input = pre_input_ptr;

	            if ( (pre_input.get_type() != AVCodec.FF_BUFFER_TYPE_SHARED) && (i != 0) ) {
	                pre_input.set_data(0, Arrays.copyOfRange(pre_input.get_data(0), MpegVideo.INPLACE_OFFSET, pre_input.get_data(0).length));
	                pre_input.set_data(1, Arrays.copyOfRange(pre_input.get_data(1), MpegVideo.INPLACE_OFFSET, pre_input.get_data(1).length));
	                pre_input.set_data(2, Arrays.copyOfRange(pre_input.get_data(2), MpegVideo.INPLACE_OFFSET, pre_input.get_data(2).length));
	            }

	            
	            s.get_dsp().shrink(scale, input[i].get_data(0), 
	            		 				  input[i].get_linesize(0), 
	            		 				  pre_input.get_data(0), 
	            		 				  pre_input.get_linesize(0), 
	            		 				  c.get_width(), c.get_height());
	            s.get_dsp().shrink(scale, input[i].get_data(1), 
						 				  input[i].get_linesize(1), 
						 				  pre_input.get_data(1), 
						 				  pre_input.get_linesize(1), 
						 				  c.get_width() >> 1, c.get_height() >> 1);
	            s.get_dsp().shrink(scale, input[i].get_data(2), 
						 				  input[i].get_linesize(2), 
						 				  pre_input.get_data(2), 
						 				  pre_input.get_linesize(2), 
						 				  c.get_width() >> 1, c.get_height() >> 1);
	        }
	    }

	    for (j = 0 ; j < s.get_max_b_frames() + 1 ; j++) {
	        long rd=0;

	        if (s.get_input_picture(j) == null)
	            break;

	        c.set_error(0, 0);
	        c.set_error(1, 0);
	        c.set_error(2, 0);

	        input[0].set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	        input[0].set_quality(1 * AVUtil.FF_QP2LAMBDA);
	        
	        OutOI ret_obj = c.avcodec_encode_video(outbuf, outbuf_size, input[0]);
	        out_size = ret_obj.get_ret();
	        outbuf = (short []) ret_obj.get_obj();
//	        rd += (out_size * lambda2) >> FF_LAMBDA_SHIFT;

	        for (i = 0 ; i < s.get_max_b_frames() + 1 ; i++) {
	            boolean is_p = i % (j+1) == j || i == s.get_max_b_frames();

	            input[i+1].set_pict_type(is_p ? AVPictureType.AV_PICTURE_TYPE_P : AVPictureType.AV_PICTURE_TYPE_B);
	            input[i+1].set_quality(is_p ? p_lambda : b_lambda);
	            ret_obj = c.avcodec_encode_video(outbuf,  outbuf_size, input[i+1]);
		        out_size = ret_obj.get_ret();
		        outbuf = (short []) ret_obj.get_obj();
	            rd += (out_size * lambda2) >> (AVUtil.FF_LAMBDA_SHIFT - 3);
	        }

	        /* get the delayed frames */
	        while (out_size != 0) {
	            ret_obj = c.avcodec_encode_video(outbuf, outbuf_size, null);
		        out_size = ret_obj.get_ret();
		        outbuf = (short []) ret_obj.get_obj();
	            rd += (out_size * lambda2) >> (AVUtil.FF_LAMBDA_SHIFT - 3);
	        }

	        rd += c.get_error(0) + c.get_error(1) + c.get_error(2);

	        if (rd < best_rd) {
	            best_rd = rd;
	            best_b_count = j;
	        }
	    }

	    outbuf = null;
	    c.avcodec_close();
	    c = null;

	    for (i = 0 ; i < s.get_max_b_frames() + 2 ; i++) {
	        input[i].set_data(0, null);
	    }

	    return best_b_count;
	}

	private static int get_intra_count(MpegEncContext s, short[] src,
			short[] ref, int stride) {
	    int x, y, w, h;
	    int acc = 0;

	    w = s.get_width() & ~15;
	    h = s.get_height() & ~15;

	    for (y = 0 ; y < h ; y+=16) {
	        for (x = 0 ; x < w ; x+=16) {
	            int offset = x + y*stride;
	            int sad  = s.get_dsp().sad_0(null, Arrays.copyOfRange(src, offset, src.length), Arrays.copyOfRange(ref, offset, ref.length), stride, 16);
	            int mean = (s.get_dsp().pix_sum(Arrays.copyOfRange(src, offset, src.length), stride) + 128)>>8;
	            int sae = get_sae(Arrays.copyOfRange(src, offset, src.length), mean, stride);

	            acc += sae + (500 < sad ? 1 : 0);
	        }
	    }
	    return acc;
	}

	private static int get_sae(short[] src, int ref, int stride) {
	    int x, y;
	    int acc = 0;

	    for (y = 0 ; y < 16 ; y++){
	        for (x = 0 ; x < 16 ; x++){
	            acc += Mathematics.FFABS(src[x+y*stride] - ref);
	        }
	    }

	    return acc;
	}

	private static int skip_check(MpegEncContext s, Picture p, Picture ref) {
	    int x, y, plane;
	    int score = 0;
	    long score64 = 0;

	    for (plane = 0 ; plane < 3 ; plane++) {
	        int stride= p.get_linesize(plane);
	        int bw = plane != 0 ? 1 : 2;
	        for (y = 0 ; y < s.get_mb_height() * bw ; y++) {
	            for (x = 0 ; x < s.get_mb_width() * bw ; x++) {
	                int off = p.get_type() == AVCodec.FF_BUFFER_TYPE_SHARED ? 0: 16;
	                int v = s.get_dsp().get_frame_skip_cmp_1(s, 
	                		Arrays.copyOfRange(p.get_data(plane), 8 * (x + y * stride) + off, p.get_data(plane).length), 
	                		Arrays.copyOfRange(ref.get_data(plane), 8*(x + y*stride), ref.get_data(plane).length), 
	                		stride, 8);

	                switch (s.get_avctx().get_frame_skip_exp()){
	                    case 0: score = (int) Mathematics.FFMAX(score, v); break;
	                    case 1: score += Mathematics.FFABS(v);break;
	                    case 2: score += v*v;break;
	                    case 3: score64 += Mathematics.FFABS(v*v*(long)v);break;
	                    case 4: score64 += v*v*(long)(v*v);break;
	                }
	            }
	        }
	    }

	    if (score!= 0) score64= score;

	    if (score64 < s.get_avctx().get_frame_skip_threshold())
	        return 1;
	    if (score64 < ((s.get_avctx().get_frame_skip_factor() * (long)s.get_lambda())>>8))
	        return 1;
	    return 0;
	}
	

	private static int load_input_picture(MpegEncContext s, AVFrame pic_arg) {
		AVFrame pic = null;
	    long pts = 0;
	    int i;
	    int encoding_delay = s.get_max_b_frames();
	    int direct = 1;

	    if (pic_arg != null){
	        pts = pic_arg.get_pts();
	        
	        s.set_input_picture_number(s.get_input_picture_number() + 1);
	        pic_arg.set_display_picture_number(s.get_input_picture_number());

	        if (pts != AVUtil.AV_NOPTS_VALUE) {
	            if (s.get_user_specified_pts() != AVUtil.AV_NOPTS_VALUE){
	                long time = pts;
	                long last = s.get_user_specified_pts();

	                if (time <= last){
	                    Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "Error, Invalid timestamp=%d, last=%d\n", pts, s.get_user_specified_pts());
	                    return -1;
	                }
	            }
	            s.set_user_specified_pts(pts);
	        } else {
	            if (s.get_user_specified_pts() != AVUtil.AV_NOPTS_VALUE){
	                s.set_user_specified_pts(s.get_user_specified_pts() + 1);
	                pts = s.get_user_specified_pts();
	                Log.av_log("AVCodecContext", Log.AV_LOG_INFO, "Warning: AVFrame.pts=? trying to guess (%d)\n", pts);
	            } else {
	                pts = pic_arg.get_display_picture_number();
	            }
	        }
	    }

	    if (pic_arg != null) {
		    if ( (encoding_delay != 0) && !s.has_flag(AVCodec.CODEC_FLAG_INPUT_PRESERVED) ) 
		    	direct = 0;
		    if (pic_arg.get_linesize(0) != s.get_linesize()) 
		    	direct = 0;
		    if (pic_arg.get_linesize(1) != s.get_uvlinesize()) 
		    	direct = 0;
		    if (pic_arg.get_linesize(2) != s.get_uvlinesize()) 
		    	direct = 0;
	
	//	    av_log(AV_LOG_DEBUG, "%d %d %d %d\n",pic_arg.get_linesize[0], pic_arg.get_linesize[1], s.get_linesize, s.get_uvlinesize);
	
		    if (direct != 0) {
		        i = MpegVideo.ff_find_unused_picture(s, 1);
	
		        pic = (AVFrame) s.get_picture(i);
		        pic.set_reference(3);
	
		        for (i = 0 ; i < 4 ; i++){
		            pic.set_data(i, pic_arg.get_data(i));
		            pic.set_linesize(i, pic_arg.get_linesize(i));
		        }
		        if (MpegVideo.ff_alloc_picture(s, (Picture) pic, 1) < 0){
		            return -1;
		        }
	    } else {
	        i = MpegVideo.ff_find_unused_picture(s, 0);

	        pic = (AVFrame) s.get_picture(i);
	        pic.set_reference(3);

	        if (MpegVideo.ff_alloc_picture(s, (Picture) pic, 0) < 0) {
	            return -1;
	        }

	        if (Arrays.copyOfRange(pic.get_data(0), MpegVideo.INPLACE_OFFSET, pic.get_data(0).length).equals(pic_arg.get_data(0)) &&
        	 	Arrays.copyOfRange(pic.get_data(0), MpegVideo.INPLACE_OFFSET, pic.get_data(0).length).equals(pic_arg.get_data(0)) &&
        	 	Arrays.copyOfRange(pic.get_data(0), MpegVideo.INPLACE_OFFSET, pic.get_data(0).length).equals(pic_arg.get_data(0)) ) {
	       // 	empty
	        } else {
	            int h_chroma_shift, v_chroma_shift;
	            
	    	    OutII tmp = ImgConvert.avcodec_get_chroma_sub_sample(s.get_avctx().get_pix_fmt());
	    	    h_chroma_shift = tmp.get_val1();
	    	    v_chroma_shift = tmp.get_val2();

	            for (i = 0 ; i < 3 ; i++){
	                int src_stride = pic_arg.get_linesize(i);
	                int dst_stride = i != 0 ? s.get_uvlinesize() : s.get_linesize();
	                int h_shift = i != 0 ? h_chroma_shift : 0;
	                int v_shift = i != 0 ? v_chroma_shift : 0;
	                int w = s.get_width() >> h_shift;
	                int h = s.get_height() >> v_shift;
	                short [] src = pic_arg.get_data(i);
	                short [] dst = pic.get_data(i);

	                if (s.get_avctx().get_rc_buffer_size() == 0)
	                    dst = Arrays.copyOfRange(dst, MpegVideo.INPLACE_OFFSET, dst.length);

	             
                    if(src_stride==dst_stride) {
                    	for (int j = 0 ; j < src_stride * h ; j++)
                    		dst[j] = src[j];
                    } else {
                        while (h-- != 0) {
                        	for (int j = 0 ; j < w ; j++)
                        		dst[j] = src[j];
                            dst = Arrays.copyOfRange(dst, dst_stride, dst.length);
                            src = Arrays.copyOfRange(src, src_stride, src.length);
                        }
                    }
	            }
	        }
	    }
		copy_picture_attributes(s, pic, pic_arg);
	    pic.set_pts(pts); //we set this here to avoid modifiying pic_arg
	  }

	    /* shift buffer entries */
	    for(i = 1 ; i < MpegVideo.MAX_PICTURE_COUNT /*s.get_encoding_delay+1*/; i++)
	        s.set_input_picture(i-1, s.get_input_picture(i));

	    s.set_input_picture(encoding_delay, (Picture)pic);

	    return 0;
	}

	private static void copy_picture_attributes(MpegEncContext s, AVFrame dst,
			AVFrame src) {
	    int i;

	    dst.set_pict_type(src.get_pict_type());
	    dst.set_quality(src.get_quality());
	    dst.set_coded_picture_number(src.get_coded_picture_number());
	    dst.set_display_picture_number(src.get_display_picture_number());
//	    dst.set_reference(src.get_reference());
	    dst.set_pts(src.get_pts());
	    dst.set_interlaced_frame(src.get_interlaced_frame());
	    dst.set_top_field_first(src.get_top_field_first());

	    if (s.get_avctx().get_me_threshold() != 0){
	        if (src.get_motion_val(0) == null)
	            Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "AVFrame.motion_val not set!\n");
	        if (src.get_mb_type() == null)
	        	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "AVFrame.mb_type not set!\n");
	        if (src.get_ref_index(0) == null)
	        	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "AVFrame.ref_index not set!\n");
	        if (src.get_motion_subsample_log2() != dst.get_motion_subsample_log2())
	        	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "AVFrame.motion_subsample_log2 doesn't match! (%d!=%d)\n",
    			src.get_motion_subsample_log2(), dst.get_motion_subsample_log2());

	        dst.set_mb_type(Arrays.copyOfRange(src.get_mb_type(), 0, s.get_mb_stride() * s.get_mb_height()));

	        for (i = 0 ; i < 2 ; i++) {
	            int stride = ((16 * s.get_mb_width()) >> src.get_motion_subsample_log2()) + 1;
	            int height = ((16 * s.get_mb_height()) >> src.get_motion_subsample_log2());

	            if (src.get_motion_val(i) != null && 
	            	src.get_motion_val(i) != dst.get_motion_val(i)) {
	                dst.set_motion_val(i, Arrays.copyOfRange(src.get_motion_val(i), 0 , 2 * stride * height));
	            }

	            if (src.get_ref_index(i) != null && 
	            	src.get_ref_index(i) != dst.get_ref_index(i)) {
	                dst.set_ref_index(i, Arrays.copyOfRange(src.get_ref_index(i), 0 , s.get_mb_stride() * 4 * s.get_mb_height()));
	            }
	        }
	    }
	}

}
