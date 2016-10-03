package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.Motion_Est_ID;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MotionEstContext;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegVideo;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;

public class MotionEst {

	public static int ff_init_me(MpegEncContext s) {
	    MotionEstContext c = s.get_me();
	    int cache_size = (int) Mathematics.FFMIN(MpegVideo.ME_MAP_SIZE>>MpegVideo.ME_MAP_SHIFT, 1<<MpegVideo.ME_MAP_SHIFT);
	    int dia_size = (int) Mathematics.FFMAX(Mathematics.FFABS(s.get_avctx().get_dia_size())&255, 
	    							   Mathematics.FFABS(s.get_avctx().get_pre_dia_size())&255);

	    if (Mathematics.FFMIN(s.get_avctx().get_dia_size(), s.get_avctx().get_pre_dia_size()) < -MpegVideo.ME_MAP_SIZE){
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "ME_MAP size is too small for SAB diamond\n");
	        return -1;
	    }
	    //special case of snow is needed because snow uses its own iterative ME code
	    if (s.get_me_method() != Motion_Est_ID.ME_ZERO && 
	     	s.get_me_method() != Motion_Est_ID.ME_EPZS && 
	     	s.get_me_method() != Motion_Est_ID.ME_X1 && 
	     	s.get_avctx().get_codec_id() != CodecID.CODEC_ID_SNOW){
	    	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "me_method is only allowed to be set to zero and epzs; for hex,umh,full and others see dia_size\n");
	        return -1;
	    }

	    c.set_avctx(s.get_avctx());

	    if (cache_size < 2*dia_size && c.get_stride() == 0) {
	    	Log.av_log("AVCodecContext", Log.AV_LOG_INFO, "ME_MAP size may be a little small for the selected diamond size\n");
	    }
/*
	    DspUtil.ff_set_cmp(s.get_dsp(), s.get_dsp().get_me_pre_cmp(), c.get_avctx.get_me_pre_cmp);
	    DspUtil.ff_set_cmp(s.get_dsp(), s.get_dsp().get_me_cmp(), c.get_avctx.get_me_cmp);
	    DspUtil.ff_set_cmp(s.get_dsp(), s.get_dsp().get_me_sub_cmp, c.get_avctx.get_me_sub_cmp);
	    DspUtil.ff_set_cmp(s.get_dsp(), s.get_dsp().get_mb_cmp, c.get_avctx.get_mb_cmp);

	    c.get_flags    = get_flags(c, 0, c.get_avctx.get_me_cmp    &FF_CMP_CHROMA);
	    c.get_sub_flags= get_flags(c, 0, c.get_avctx.get_me_sub_cmp&FF_CMP_CHROMA);
	    c.get_mb_flags = get_flags(c, 0, c.get_avctx.get_mb_cmp    &FF_CMP_CHROMA);

	    if(s.get_flags&CODEC_FLAG_QPEL){
	        c.get_sub_motion_search= qpel_motion_search;
	        c.get_qpel_avg= s.get_dsp.avg_qpel_pixels_tab;
	        if(s.get_no_rounding) c.get_qpel_put= s.get_dsp.put_no_rnd_qpel_pixels_tab;
	        else               c.get_qpel_put= s.get_dsp.put_qpel_pixels_tab;
	    }else{
	        if(c.get_avctx.get_me_sub_cmp&FF_CMP_CHROMA)
	            c.get_sub_motion_search= hpel_motion_search;
	        else if(   c.get_avctx.get_me_sub_cmp == FF_CMP_SAD
	                && c.get_avctx.get_    me_cmp == FF_CMP_SAD
	                && c.get_avctx.get_    mb_cmp == FF_CMP_SAD)
	            c.get_sub_motion_search= sad_hpel_motion_search; // 2050 vs. 2450 cycles
	        else
	            c.get_sub_motion_search= hpel_motion_search;
	    }
	    c.get_hpel_avg= s.get_dsp.avg_pixels_tab;
	    if(s.get_no_rounding) c.get_hpel_put= s.get_dsp.put_no_rnd_pixels_tab;
	    else               c.get_hpel_put= s.get_dsp.put_pixels_tab;

	    if(s.get_linesize){
	        c.get_stride  = s.get_linesize;
	        c.get_uvstride= s.get_uvlinesize;
	    }else{
	        c.get_stride  = 16*s.get_mb_width + 32;
	        c.get_uvstride=  8*s.get_mb_width + 16;
	    }*/

	    /* 8x8 fullpel search would need a 4x4 chroma compare, which we do
	     * not have yet, and even if we had, the motion estimation code
	     * does not expect it. */
	  /*  if(s.get_codec_id != CODEC_ID_SNOW){
	        if((c.get_avctx.get_me_cmp&FF_CMP_CHROMA)){
	            s.get_dsp.me_cmp[2]= zero_cmp;
	        }
	        if((c.get_avctx.get_me_sub_cmp&FF_CMP_CHROMA) && !s.get_dsp.me_sub_cmp[2]){
	            s.get_dsp.me_sub_cmp[2]= zero_cmp;
	        }
	        c.get_hpel_put[2][0]= c.get_hpel_put[2][1]=
	        c.get_hpel_put[2][2]= c.get_hpel_put[2][3]= zero_hpel;
	    }

	    if(s.get_codec_id == CODEC_ID_H261){
	        c.get_sub_motion_search= no_sub_motion_search;
	    }
*/
	    return 0;
	}

}
