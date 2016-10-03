package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.Config;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegVideo;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.Picture;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Eval;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.util.OutII;

public class RateControl {

	public static int ff_vbv_update(MpegEncContext s, int frame_size) {
	    RateControlContext rcc = s.get_rc_context();
	    double fps = 1 / s.get_avctx().get_time_base().av_q2d();
	    int buffer_size = s.get_avctx().get_rc_buffer_size();
	    double min_rate = s.get_avctx().get_rc_min_rate() / fps;
	    double max_rate = s.get_avctx().get_rc_max_rate() / fps;

	//printf("%d %f %d %f %f\n", buffer_size, rcc.get_buffer_index, frame_size, min_rate, max_rate);
	    if (buffer_size != 0) {
	        int left;

	        rcc.set_buffer_index(rcc.get_buffer_index() - frame_size);
	        if (rcc.get_buffer_index() < 0) {
	            Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "rc buffer underflow\n");
	            rcc.set_buffer_index(0);
	        }

	        left = (int) (buffer_size - rcc.get_buffer_index() - 1);
	        rcc.set_buffer_index(rcc.get_buffer_index() + Common.av_clip(left, min_rate, max_rate));

	        if (rcc.get_buffer_index() > buffer_size) {
	            int stuffing = (int) Math.ceil((rcc.get_buffer_index() - buffer_size)/8);

	            if ( (stuffing < 4) && (s.get_codec_id() == CodecID.CODEC_ID_MPEG4) )
	                stuffing = 4;
	            rcc.set_buffer_index(rcc.get_buffer_index() - 8*stuffing);

	            if ( (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_RC) != 0)
	            	Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "stuffing %d bytes\n", stuffing);

	            return stuffing;
	        }
	    }
	    return 0;
		
	}

	public static float ff_rate_estimate_qscale(MpegEncContext s, int dry_run) {
	    float q;
	    int qmin, qmax;
	    float br_compensation;
	    double diff;
	    double short_term_q;
	    double fps;
	    int picture_number = s.get_picture_number();
	    long wanted_bits;
	    RateControlContext rcc = s.get_rc_context();
	    AVCodecContext a = s.get_avctx();
	    RateControlEntry local_rce = new RateControlEntry(), rce = new RateControlEntry();
	    double bits;
	    double rate_factor;
	    int var;
	    AVPictureType pict_type = s.get_pict_type();
	    Picture pic = s.get_current_picture();

	    OutII ret_obj = get_qminmax(s, pict_type);
	    qmin = ret_obj.get_val1();
	    qmax = ret_obj.get_val2();
	    
	    
	    fps = 1 / s.get_avctx().get_time_base().av_q2d();
	//printf("input_pic_num:%d pic_num:%d frame_rate:%d\n", s.get_input_picture_number, s.get_picture_number, s.get_frame_rate);
	        /* update predictors */
	    if (picture_number > 2 && dry_run == 0) {
	        int last_var = s.get_last_pict_type() == AVPictureType.AV_PICTURE_TYPE_I ? 
	        		          rcc.get_last_mb_var_sum() : 
	        		          rcc.get_last_mc_mb_var_sum();
	        update_predictor(rcc.get_pred()[s.get_last_pict_type().ordinal()], 
	        		         rcc.get_last_qscale(), 
	        		         Math.sqrt(last_var), 
	        		         s.get_frame_bits());
	    }

	    if (s.has_flag(AVCodec.CODEC_FLAG_PASS2)) {
	        rce = rcc.get_entry(picture_number);
	        wanted_bits = rce.get_expected_bits();
	    } else {
	        Picture dts_pic;
	        rce = local_rce;

	        //FIXME add a dts field to AVFrame and ensure its set and use it here instead of reordering
	        //but the reordering is simpler for now until h.264 b pyramid must be handeld
	        if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B || 
	        	s.get_low_delay() != 0)
	            dts_pic = s.get_current_picture_ptr();
	        else
	            dts_pic = s.get_last_picture_ptr();

	//if(dts_pic)
//	            av_log(NULL, AV_LOG_ERROR, "%Ld %Ld %Ld %d\n", s.get_current_picture_ptr.get_pts, s.get_user_specified_pts, dts_pic.get_pts, picture_number);

	        if (dts_pic == null || 
	        	dts_pic.get_pts() == AVUtil.AV_NOPTS_VALUE)
	            wanted_bits = (long) (s.get_bit_rate() * (double) picture_number/ fps);
	        else
	            wanted_bits= (long) (s.get_bit_rate() * (double) dts_pic.get_pts() / fps);
	    }

	    diff = s.get_total_bits() - wanted_bits;
	    br_compensation = (float) ((a.get_bit_rate_tolerance() - diff) / a.get_bit_rate_tolerance());
	    if (br_compensation <= 0.0) 
	    	br_compensation = (float) 0.001;

	    var = pict_type == AVPictureType.AV_PICTURE_TYPE_I ? pic.get_mb_var_sum() : pic.get_mc_mb_var_sum();

	    short_term_q = 0; /* avoid warning */
	    if (s.has_flag(AVCodec.CODEC_FLAG_PASS2)) {
	        
	        q = rce.get_new_qscale() / br_compensation;
	//printf("%f %f %f last:%d var:%d type:%d//\n", q, rce.get_new_qscale, br_compensation, s.get_frame_bits, var, pict_type);
	    } else {
	        rce.set_pict_type(pict_type);
	        rce.set_new_pict_type(pict_type);
	        rce.set_mc_mb_var_sum(pic.get_mc_mb_var_sum());
	        rce.set_mb_var_sum(pic.get_mb_var_sum());
	        rce.set_qscale(AVUtil.FF_QP2LAMBDA * 2);
	        rce.set_f_code(s.get_f_code());
	        rce.set_b_code(s.get_b_code());
	        rce.set_misc_bits(1);

	        bits = predict_size(rcc.get_pred()[pict_type.ordinal()], rce.get_qscale(), Math.sqrt(var));
	        if (pict_type == AVPictureType.AV_PICTURE_TYPE_I) {
	            rce.set_i_count(s.get_mb_num());
	            rce.set_i_tex_bits((int) bits);
	            rce.set_p_tex_bits(0);
	            rce.set_mv_bits(0);
	        }else{
	            rce.set_i_count(0); //FIXME we do know this approx
	            rce.set_i_tex_bits(0);
	            rce.set_p_tex_bits((int) (bits * 0.9));
	            rce.set_mv_bits((int) (bits * 0.1));
	        }
	        rcc.get_i_cplx_sum()[pict_type.ordinal()] += rce.get_i_tex_bits() * rce.get_qscale();
	        rcc.get_p_cplx_sum()[pict_type.ordinal()] += rce.get_p_tex_bits() * rce.get_qscale();
	        rcc.get_mv_bits_sum()[pict_type.ordinal()] += rce.get_mv_bits();
	        rcc.get_frame_count()[pict_type.ordinal()] ++;

	        bits = rce.get_i_tex_bits() + rce.get_p_tex_bits();
	        rate_factor = rcc.get_pass1_wanted_bits() / rcc.get_pass1_rc_eq_output_sum() * br_compensation;

	        q = (float) get_qscale(s, rce, rate_factor, picture_number);
	        if (q < 0)
	            return -1;

	        q = (float) get_diff_limited_q(s, rce, q);

	        if (pict_type == AVPictureType.AV_PICTURE_TYPE_P || 
	        	s.get_intra_only() != 0){ //FIXME type dependent blur like in 2-pass
	        	rcc.set_short_term_qsum(rcc.get_short_term_qsum() * a.get_qblur());
	        	rcc.set_short_term_qcount(rcc.get_short_term_qcount() * a.get_qblur());

	        	rcc.set_short_term_qsum(rcc.get_short_term_qsum() + q);
	        	rcc.set_short_term_qcount(rcc.get_short_term_qcount() + 1);

	            q = (float) (rcc.get_short_term_qsum() / rcc.get_short_term_qcount());
	            short_term_q = q;
	        }

	        q = (float) modify_qscale(s, rce, q, picture_number);

	        rcc.set_pass1_wanted_bits(rcc.get_pass1_wanted_bits() + s.get_bit_rate() / fps);
	    }

	    if ( (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_RC) != 0) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "%c qp:%d<%2.1f<%d %d want:%d total:%d comp:%f st_q:%2.2f size:%d var:%d/%d br:%d fps:%d\n",
	        AVUtil.av_get_picture_type_char(pict_type), qmin, q, qmax, picture_number, (int) wanted_bits/1000,
	        (int) s.get_total_bits() / 1000, br_compensation, short_term_q, 
	        s.get_frame_bits(), pic.get_mb_var_sum(), pic.get_mc_mb_var_sum(), 
	        s.get_bit_rate() / 1000, (int)fps);
	    }

	    if (q < qmin) 
	    	q = qmin;
	    else if (q > qmax) 
	    	q = qmax;

	    if (s.get_adaptive_quant() != 0)
	        adaptive_quantization(s, q);
	    else
	        q= (int)(q + 0.5);

	    if (dry_run == 0) {
	        rcc.set_last_qscale(q);
	        rcc.set_last_mc_mb_var_sum(pic.get_mc_mb_var_sum());
	        rcc.set_last_mb_var_sum(pic.get_mb_var_sum());
	    }
	    return q;
	}

	private static void adaptive_quantization(MpegEncContext s, double q) {
	    int i;
	    float lumi_masking = s.get_avctx().get_lumi_masking() / (128.0f * 128.0f);
	    float dark_masking = s.get_avctx().get_dark_masking() / (128.0f * 128.0f);
	    float temp_cplx_masking = s.get_avctx().get_temporal_cplx_masking();
	    float spatial_cplx_masking = s.get_avctx().get_spatial_cplx_masking();
	    float p_masking = s.get_avctx().get_p_masking();
	    float border_masking = s.get_avctx().get_border_masking();
	    float bits_sum = 0.0f;
	    float cplx_sum = 0.0f;
	    float [] cplx_tab = new float[s.get_mb_num()];
	    float [] bits_tab = new float[s.get_mb_num()];
	    int qmin = s.get_avctx().get_mb_lmin();
	    int qmax = s.get_avctx().get_mb_lmax();
	    Picture pic = s.get_current_picture();
	    int mb_width = s.get_mb_width();
	    int mb_height = s.get_mb_height();

	    for (i = 0 ; i < s.get_mb_num() ; i++) {
	        int mb_xy = s.get_mb_index2xy()[i];
	        float temp_cplx = (float) Math.sqrt(pic.get_mc_mb_var()[mb_xy]); 
	        float spat_cplx = (float) Math.sqrt(pic.get_mb_var()[mb_xy]);
	        int lumi = pic.get_mb_mean()[mb_xy];
	        float bits, cplx, factor;
	        int mb_x = mb_xy % s.get_mb_stride();
	        int mb_y = mb_xy / s.get_mb_stride();
	        int mb_distance;
	        float mb_factor = 0.0f;
	        if (spat_cplx < 4) spat_cplx = 4;
	        if (temp_cplx < 4) temp_cplx = 4;

	        if ( (s.get_mb_type()[mb_xy] & MpegVideo.CANDIDATE_MB_TYPE_INTRA) != 0) {
	            cplx = spat_cplx;
	            factor = (float) (1.0 + p_masking);
	        } else {
	            cplx = temp_cplx;
	            factor = (float) Math.pow(temp_cplx, - temp_cplx_masking);
	        }
	        factor *= Math.pow(spat_cplx, - spatial_cplx_masking);

	        if (lumi > 127)
	            factor *= (1.0 - (lumi - 128) * (lumi - 128) * lumi_masking);
	        else
	            factor *= (1.0 - (lumi - 128) * (lumi - 128) * dark_masking);

	        if (mb_x < mb_width / 5) {
	            mb_distance = mb_width/5 - mb_x;
	            mb_factor = (float)mb_distance / (float)(mb_width/5);
	        } else if(mb_x > 4*mb_width/5) {
	            mb_distance = mb_x - 4*mb_width/5;
	            mb_factor = (float)mb_distance / (float)(mb_width/5);
	        }
	        if(mb_y < mb_height/5) {
	            mb_distance = mb_height/5 - mb_y;
	            mb_factor = Mathematics.FFMAX((long) mb_factor, mb_distance / (mb_height / 5));
	        } else if (mb_y > 4 * mb_height / 5) {
	            mb_distance = mb_y - 4 * mb_height / 5;
	            mb_factor = Mathematics.FFMAX((long) mb_factor, mb_distance / (mb_height/5));
	        }

	        factor *= 1.0 - border_masking * mb_factor;

	        if (factor < 0.00001) 
	        	factor = 0.00001f;

	        bits = cplx * factor;
	        cplx_sum += cplx;
	        bits_sum += bits;
	        cplx_tab[i] = cplx;
	        bits_tab[i] = bits;
	    }

	    /* handle qmin/qmax clipping */
	    if (s.has_flag(AVCodec.CODEC_FLAG_NORMALIZE_AQP)) {
	        float factor = bits_sum / cplx_sum;
	        for (i = 0 ; i < s.get_mb_num() ; i++) {
	            float newq = (float) (q * cplx_tab[i] / bits_tab[i]);
	            newq *= factor;

	            if     (newq > qmax){
	                bits_sum -= bits_tab[i];
	                cplx_sum -= cplx_tab[i]*q/qmax;
	            }
	            else if(newq < qmin){
	                bits_sum -= bits_tab[i];
	                cplx_sum -= cplx_tab[i]*q/qmin;
	            }
	        }
	        if(bits_sum < 0.001) bits_sum= 0.001f;
	        if(cplx_sum < 0.001) cplx_sum= 0.001f;
	    }

	    for(i=0; i<s.get_mb_num(); i++){
	        int mb_xy= s.get_mb_index2xy()[i];
	        float newq= (float) (q*cplx_tab[i]/bits_tab[i]);
	        int intq;

	        if (s.has_flag(AVCodec.CODEC_FLAG_NORMALIZE_AQP)) {
	            newq*= bits_sum/cplx_sum;
	        }

	        intq= (int)(newq + 0.5);

	        if     (intq > qmax) intq= qmax;
	        else if(intq < qmin) intq= qmin;
	        s.get_lambda_table()[mb_xy]= intq;
	    }
	}

	private static double modify_qscale(MpegEncContext s, RateControlEntry rce,
			double q, int frame_num) {
	    RateControlContext rcc = s.get_rc_context();
	    int qmin, qmax;
	    AVPictureType pict_type = rce.get_new_pict_type();
	    double buffer_size = s.get_avctx().get_rc_buffer_size();
	    double fps = 1 / s.get_avctx().get_time_base().av_q2d();
	    double min_rate = s.get_avctx().get_rc_min_rate() / fps;
	    double max_rate = s.get_avctx().get_rc_max_rate() / fps;

	    OutII ret_obj = get_qminmax(s, pict_type);
	    qmin = ret_obj.get_val1();
	    qmax = ret_obj.get_val2();

	    /* modulation */
	    if (s.get_avctx().get_rc_qmod_freq() != 0 && 
	    	frame_num % s.get_avctx().get_rc_qmod_freq() == 0 && 
	    	pict_type == AVPictureType.AV_PICTURE_TYPE_P)
	        q *= s.get_avctx().get_rc_qmod_amp();

	    /* buffer overflow/underflow protection */
	    if (buffer_size != 0) {
	        double expected_size= rcc.get_buffer_index();
	        double q_limit;

	        if (min_rate != 0) {
	            double d = 2 * (buffer_size - expected_size) / buffer_size;
	            if (d > 1.0) 
	            	d = 1.0;
	            else if (d < 0.0001) 
	            	d = 0.0001;
	            q *= Math.pow(d, 1.0 / s.get_avctx().get_rc_buffer_aggressivity());

	            q_limit = bits2qp(rce, Mathematics.FFMAX((long) ((min_rate - buffer_size + rcc.get_buffer_index()) * s.get_avctx().get_rc_min_vbv_overflow_use()), 1));
	            if (q > q_limit) {
	                if ( (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_RC) != 0) {
	                    Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "limiting QP %f .get_ %f\n", q, q_limit);
	                }
	                q = q_limit;
	            }
	        }

	        if (max_rate != 0) {
	            double d = 2 * expected_size / buffer_size;
	            if (d > 1.0) 
	            	d = 1.0;
	            else if (d < 0.0001) 
	            	d = 0.0001;
	            q /= Math.pow(d, 1.0 / s.get_avctx().get_rc_buffer_aggressivity());

	            q_limit = bits2qp(rce, Mathematics.FFMAX((long) (rcc.get_buffer_index() * s.get_avctx().get_rc_max_available_vbv_use()), 1));
	            if (q < q_limit){
	                if ( (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_RC) != 0) {
	                    Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "limiting QP %f .get_ %f\n", q, q_limit);
	                }
	                q = q_limit;
	            }
	        }
	    }

	    if (s.get_avctx().get_rc_qsquish() == 0.0 || 
	    	qmin == qmax) {
	        if (q < qmin) 
	        	q = qmin;
	        else if (q > qmax) 
	        	q = qmax;
	    } else {
	        double min2 = Math.log(qmin);
	        double max2 = Math.log(qmax);

	        q = Math.log(q);
	        q = (q - min2) / (max2 - min2) - 0.5;
	        q *= -4.0;
	        q = 1.0/(1.0 + Math.exp(q));
	        q = q * (max2 - min2) + min2;

	        q = Math.exp(q);
	    }

	    return q;
	}

	private static double get_diff_limited_q(MpegEncContext s, RateControlEntry rce, double q) {
		RateControlContext rcc = s.get_rc_context();
	    AVCodecContext a = s.get_avctx();
	    AVPictureType pict_type = rce.get_new_pict_type();
	    double last_p_q    = rcc.get_last_qscale_for()[AVPictureType.AV_PICTURE_TYPE_P.ordinal()];
	    double last_non_b_q= rcc.get_last_qscale_for()[rcc.get_last_non_b_pict_type().ordinal()];

	    if (pict_type == AVPictureType.AV_PICTURE_TYPE_I && 
	    	(a.get_i_quant_factor() > 0.0 || rcc.get_last_non_b_pict_type() == AVPictureType.AV_PICTURE_TYPE_P))
	        q = last_p_q * Mathematics.FFABS(a.get_i_quant_factor()) + a.get_i_quant_offset();
	    else if (pict_type == AVPictureType.AV_PICTURE_TYPE_B && 
	    		  a.get_b_quant_factor() > 0.0)
	        q = last_non_b_q * a.get_b_quant_factor() + a.get_b_quant_offset();
	    if(q<1) q=1;

	    /* last qscale / qdiff stuff */
	    if (rcc.get_last_non_b_pict_type() == pict_type || 
	    	pict_type != AVPictureType.AV_PICTURE_TYPE_I) {
	        double last_q = rcc.get_last_qscale_for()[pict_type.ordinal()];
	        int maxdiff = AVUtil.FF_QP2LAMBDA * a.get_max_qdiff();

	        if (q > last_q + maxdiff) 
	        	q = last_q + maxdiff;
	        else if (q < last_q - maxdiff) 
	        	q = last_q - maxdiff;
	    }

	    rcc.get_last_qscale_for()[pict_type.ordinal()] = q; //Note we cannot do that after blurring

	    if (pict_type != AVPictureType.AV_PICTURE_TYPE_B)
	        rcc.set_last_non_b_pict_type(pict_type);

	    return q;
	}

	private static double get_qscale(MpegEncContext s, RateControlEntry rce,
			double rate_factor, int frame_num) {
	    RateControlContext rcc = s.get_rc_context();
	    AVCodecContext a = s.get_avctx();
	    double q, bits;
	    AVPictureType pict_type = rce.get_new_pict_type();
	    double mb_num = s.get_mb_num();
	    int i;

	    double const_values[] = {
	        Math.PI,
	        Math.E,
	        rce.get_i_tex_bits() * rce.get_qscale(),
	        rce.get_p_tex_bits() * rce.get_qscale(),
	        (rce.get_i_tex_bits() + rce.get_p_tex_bits()) * (double) rce.get_qscale(),
	        rce.get_mv_bits()/ mb_num,
	        rce.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B ? (rce.get_f_code() + rce.get_b_code())*0.5 : rce.get_f_code(),
	        rce.get_i_count() / mb_num,
	        rce.get_mc_mb_var_sum() / mb_num,
	        rce.get_mb_var_sum() / mb_num,
	        rce.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I ? 1 : 0,
	        rce.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_P ? 1 : 0,
	        rce.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B ? 1 : 0,
	        rcc.get_qscale_sum()[pict_type.ordinal()] / (double) rcc.get_frame_count()[pict_type.ordinal()],
	        a.get_qcompress(),
	/*        rcc.get_last_qscale_for[AV_PICTURE_TYPE_I],
	        rcc.get_last_qscale_for[AV_PICTURE_TYPE_P],
	        rcc.get_last_qscale_for[AV_PICTURE_TYPE_B],
	        rcc.get_next_non_b_qscale,*/
	        rcc.get_i_cplx_sum()[AVPictureType.AV_PICTURE_TYPE_I.ordinal()] / (double)rcc.get_frame_count()[AVPictureType.AV_PICTURE_TYPE_I.ordinal()],
	        rcc.get_i_cplx_sum()[AVPictureType.AV_PICTURE_TYPE_P.ordinal()] / (double)rcc.get_frame_count()[AVPictureType.AV_PICTURE_TYPE_P.ordinal()],
	        rcc.get_p_cplx_sum()[AVPictureType.AV_PICTURE_TYPE_P.ordinal()] / (double)rcc.get_frame_count()[AVPictureType.AV_PICTURE_TYPE_P.ordinal()],
	        rcc.get_p_cplx_sum()[AVPictureType.AV_PICTURE_TYPE_B.ordinal()] / (double)rcc.get_frame_count()[AVPictureType.AV_PICTURE_TYPE_B.ordinal()],
	        (rcc.get_i_cplx_sum()[pict_type.ordinal()] + rcc.get_p_cplx_sum()[pict_type.ordinal()]) / (double)rcc.get_frame_count()[pict_type.ordinal()],
	        0
	    };

	    bits = Eval.av_expr_eval(rcc.get_rc_eq_eval(), const_values, rce);
	    if (Double.isNaN(bits)) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "Error evaluating rc_eq \"%s\"\n", s.get_avctx().get_rc_eq());
	        return -1;
	    }

	    rcc.set_pass1_rc_eq_output_sum(rcc.get_pass1_rc_eq_output_sum() + bits);
	    bits *= rate_factor;
	    if (bits < 0.0) 
	    	bits=0.0;
	    bits += 1.0; //avoid 1/0 issues

	    /* user override */
	    for (i = 0 ; i < s.get_avctx().get_rc_override_count() ; i++) {
	        RcOverride [] rco = s.get_avctx().get_rc_override();
	        if (rco[i].get_start_frame() > frame_num) 
	        	continue;
	        if(rco[i].get_end_frame() < frame_num) 
	        	continue;

	        if (rco[i].get_qscale() != 0)
	            bits = qp2bits(rce, rco[i].get_qscale()); //FIXME move at end to really force it?
	        else
	            bits *= rco[i].get_quality_factor();
	    }

	    q = bits2qp(rce, bits);

	    /* I/B difference */
	    if (pict_type == AVPictureType.AV_PICTURE_TYPE_I && 
	    	s.get_avctx().get_i_quant_factor() < 0.0)
	        q = -q * s.get_avctx().get_i_quant_factor() + s.get_avctx().get_i_quant_offset();
	    else if (pict_type == AVPictureType.AV_PICTURE_TYPE_B && 
	    		  s.get_avctx().get_b_quant_factor() < 0.0)
	        q = -q * s.get_avctx().get_b_quant_factor() + s.get_avctx().get_b_quant_offset();
	    if (q <1) 
	    	q = 1;

	    return q;
	}

	private static double bits2qp(RateControlEntry rce, double bits) {
	    if (bits < 0.9) {
	        Log.av_log("", Log.AV_LOG_ERROR, "bits<0.9\n");
	    }
	    return rce.get_qscale() * (double)(rce.get_i_tex_bits() + rce.get_p_tex_bits() + 1) / bits;
	}

	private static double qp2bits(RateControlEntry rce, int qp) {
		if (qp <= 0.0) {
	        Log.av_log("", Log.AV_LOG_ERROR, "qp<=0.0\n");
	    }
	    return rce.get_qscale() * (double) (rce.get_i_tex_bits() + rce.get_p_tex_bits() + 1) / qp;
	}
	    

	private static double predict_size(Predictor p, float q, double var) {
		return p.get_coeff() * var / (q * p.get_count());
	}

	private static void update_predictor(Predictor p, double q, double var, double size) {
	    double new_coeff = size *q / (var + 1);
	    if (var < 10) 
	    	return;

	    p.set_count(p.get_count() * p.get_decay());
	    p.set_coeff(p.get_coeff() * p.get_decay());
	    p.set_count(p.get_count() + 1);
	    p.set_coeff(p.get_coeff() + new_coeff);
	}

	private static OutII get_qminmax(MpegEncContext s, AVPictureType pict_type) {
		int qmin = s.get_avctx().get_lmin();
	    int qmax = s.get_avctx().get_lmax();

	    if (pict_type == AVPictureType.AV_PICTURE_TYPE_B) {
	        qmin = (int)(qmin * Mathematics.FFABS(s.get_avctx().get_b_quant_factor()) + s.get_avctx().get_b_quant_offset() + 0.5);
	        qmax = (int)(qmax * Mathematics.FFABS(s.get_avctx().get_b_quant_factor()) + s.get_avctx().get_b_quant_offset() + 0.5);
	    } else if (pict_type == AVPictureType.AV_PICTURE_TYPE_I) {
	        qmin = (int)(qmin * Mathematics.FFABS(s.get_avctx().get_i_quant_factor()) + s.get_avctx().get_i_quant_offset() + 0.5);
	        qmax = (int)(qmax * Mathematics.FFABS(s.get_avctx().get_i_quant_factor()) + s.get_avctx().get_i_quant_offset() + 0.5);
	    }

	    qmin = Common.av_clip(qmin, 1, AVUtil.FF_LAMBDA_MAX);
	    qmax = Common.av_clip(qmax, 1, AVUtil.FF_LAMBDA_MAX);

	    if (qmax < qmin) 
	    	qmax= qmin;

	    return new OutII(qmin, qmax);
	}

	public static void ff_write_pass1_stats(MpegEncContext s) {
	    s.get_avctx().set_stats_out(String.format("in:%d out:%d type:%d q:%d itex:%d ptex:%d mv:%d misc:%d fcode:%d bcode:%d mc-var:%d var:%d icount:%d skipcount:%d hbits:%d;\n",
            s.get_current_picture_ptr().get_display_picture_number(), 
            s.get_current_picture_ptr().get_coded_picture_number(), s.get_pict_type(),
            s.get_current_picture().get_quality(), s.get_i_tex_bits(), 
            s.get_p_tex_bits(), s.get_mv_bits(), s.get_misc_bits(), s.get_f_code(), 
            s.get_b_code(), s.get_current_picture().get_mc_mb_var_sum(), 
            s.get_current_picture().get_mb_var_sum(), s.get_i_count(), 
            s.get_skip_count(), s.get_header_bits()));
	}

	public static void ff_get_2pass_fcode(MpegEncContext s) {
	    RateControlContext rcc = s.get_rc_context();
	    int picture_number = s.get_picture_number();
	    RateControlEntry rce;

	    rce = rcc.get_entry(picture_number);
	    s.set_f_code(rce.get_f_code());
	    s.set_b_code(rce.get_b_code());
	}

}
