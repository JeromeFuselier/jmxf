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

import java.util.Arrays;

import uk.ac.liv.ffmpeg.Config;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.xvmc.MpegVideoXvmc;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.AVPicture;
import uk.ac.liv.ffmpeg.libavcodec.DspUtil;
import uk.ac.liv.ffmpeg.libavcodec.Pthread;
import uk.ac.liv.ffmpeg.libavformat.AVPanScan;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;

public class MpegVideo {
	
	public static final byte [] ff_mpeg1_dc_scale_table = {
		8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
		8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
		8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
		8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
	};
	

	public static final byte [] mpeg2_dc_scale_table1 = {
	    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
	};

	public static final byte [] mpeg2_dc_scale_table2 = {
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	};

	public static final byte [] mpeg2_dc_scale_table3 = {
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	};
	
	public static final byte [] ff_default_chroma_qscale_table = {
	     0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 
	    16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
	};
	
	public static enum OutputFormat {
	    FMT_MPEG1,
	    FMT_H261,
	    FMT_H263,
	    FMT_MJPEG,
	    FMT_H264,		
	};
	
	public static final int PICT_TOP_FIELD    = 1;
	public static final int PICT_BOTTOM_FIELD = 2;
	public static final int PICT_FRAME        = 3;
	

	public static final int MAX_PICTURE_COUNT = 32;
	
	
	public static final byte [][] ff_mpeg2_dc_scale_table = {
		ff_mpeg1_dc_scale_table,		
	    mpeg2_dc_scale_table1,
	    mpeg2_dc_scale_table2,
	    mpeg2_dc_scale_table3
	};
	

	public static final int PREV_PICT_TYPES_BUFFER_SIZE = 256;
	

	 public static final int MV_DIR_FORWARD  = 1;
	 public static final int MV_DIR_BACKWARD = 2;
	 public static final int MV_DIRECT       = 4; ///< bidirectional mode where the difference equals the MV of the last P/S/I-Frame (mpeg4)

	 public static final int MV_TYPE_16X16 = 0;   ///< 1 vector for the whole mb
	 public static final int MV_TYPE_8X8   = 1;   ///< 4 vectors (h263, mpeg4 4MV)
	 public static final int MV_TYPE_16X8  = 2;   ///< 2 vectors, one per 16x8 block
	 public static final int MV_TYPE_FIELD = 3;   ///< 2 vectors, one per field
	 public static final int MV_TYPE_DMV   = 4;   ///< 2 vectors, special mpeg2 Dual Prime Vectors
	 
	 public static final int MAX_MV = 2048;
	 
	 public static final int CANDIDATE_MB_TYPE_INTRA      = 0x01;
	 public static final int CANDIDATE_MB_TYPE_INTER      = 0x02;
	 public static final int CANDIDATE_MB_TYPE_INTER4V    = 0x04;
	 public static final int CANDIDATE_MB_TYPE_SKIPPED    = 0x08;
//	 public static final int MB_TYPE_GMC      		     = 0x10;

	 public static final int CANDIDATE_MB_TYPE_DIRECT     = 0x10;
	 public static final int CANDIDATE_MB_TYPE_FORWARD    = 0x20;
	 public static final int CANDIDATE_MB_TYPE_BACKWARD   = 0x40;
	 public static final int CANDIDATE_MB_TYPE_BIDIR      = 0x80;

	 public static final int CANDIDATE_MB_TYPE_INTER_I    = 0x100;
	 public static final int CANDIDATE_MB_TYPE_FORWARD_I  = 0x200;
	 public static final int CANDIDATE_MB_TYPE_BACKWARD_I = 0x400;
	 public static final int CANDIDATE_MB_TYPE_BIDIR_I    = 0x800;

	 public static final int CANDIDATE_MB_TYPE_DIRECT0    = 0x1000;
	 
	 public static final int QUANT_BIAS_SHIFT = 8;	 

	 public static final int CHROMA_420 = 1;
	 public static final int CHROMA_422 = 2;
	 public static final int CHROMA_444 = 3;	 

	 public static final int SLICE_OK    = 0;
	 public static final int SLICE_ERROR = -1;
	 public static final int SLICE_END   = -2; ///<end marker found
	 public static final int SLICE_NOEND = -3; ///<no end marker or error found but mb count exceeded
	 
	 public static final int VP_START = 1;    ///< current MB is the first after a resync marker
	 public static final int AC_ERROR = 2;
	 public static final int DC_ERROR = 4;
	 public static final int MV_ERROR = 8;
	 public static final int AC_END   = 16;
	 public static final int DC_END   = 32;
	 public static final int MV_END   = 64;
	 
	 public static final int MAX_THREADS = 16;


	public static final int INPLACE_OFFSET = 16;
	
	public static final int ME_MAP_SIZE = 64;
	public static final int ME_MAP_SHIFT = 3;
	public static final int ME_MAP_MV_BITS = 11;

	public static final int MAX_MB_BYTES = (30*16*16*3/8 + 120);
	 
	 
	 public static int UNI_AC_ENC_INDEX(int run, int level) {
		 return run * 128 + level;
	 }


	public static void init_rl(RLTable rl, byte[][] static_store) {
		byte [] max_level = new byte[RLTable.MAX_RUN+1];
		byte [] max_run = new byte[RLTable.MAX_LEVEL+1];
		byte [] index_run = new byte[RLTable.MAX_RUN+1];
    	int last, run, level, start, end, i;

	    /* If table is static, we can quit if rl.get_max_level[0] is not NULL */
	    if ( (static_store != null) && (rl.get_max_level()[0] != null) )
	        return;

	    /* compute max_level[], max_run[] and index_run[] */
	    for (last = 0 ; last < 2 ; last++) {
	        if (last == 0) {
	            start = 0;
	            end = rl.get_last();
	        } else {
	            start = rl.get_last();
	            end = rl.get_n();
	        }

	        for (i = start ; i < end ; i++) {
	            run = rl.get_table_run()[i];
	            level = rl.get_table_level()[i];
	            if (index_run[run] == rl.get_n())
	                index_run[run] = (byte)i;
	            if (level > max_level[run])
	                max_level[run] = (byte)level;
	            if (run > max_run[level])
	                max_run[level] = (byte)run;
	        }
	        /* TODO IMPLEM
	        if(static_store)
	            rl.get_max_level[last] = static_store[last];
	        else
	            rl.get_max_level[last] = av_malloc(MAX_RUN + 1);
	        memcpy(rl.get_max_level[last], max_level, MAX_RUN + 1);
	        if(static_store)
	            rl.get_max_run[last] = static_store[last] + MAX_RUN + 1;
	        else
	            rl.get_max_run[last] = av_malloc(MAX_LEVEL + 1);
	        memcpy(rl.get_max_run[last], max_run, MAX_LEVEL + 1);
	        if(static_store)
	            rl.get_index_run[last] = static_store[last] + MAX_RUN + MAX_LEVEL + 2;
	        else
	            rl.get_index_run[last] = av_malloc(MAX_RUN + 1);
	        memcpy(rl.get_index_run[last], index_run, MAX_RUN + 1);
	        */
	    }

	}


	public static int ff_find_unused_picture(MpegEncContext s, int shared) {
	    int i;

	    if (shared != 0) {
	        for (i = s.get_picture_range_start() ; i < s.get_picture_range_end() ; i++) {
	            if ( (s.get_picture(i).get_data(0) == null) && (s.get_picture(i).get_type() == 0) )
	            	return i;
	        }
	    } else {
	        for (i = s.get_picture_range_start() ; i < s.get_picture_range_end() ; i++) {
	            if ( (s.get_picture(i).get_data(0) == null) && (s.get_picture(i).get_type() == 0) )
	            	return i;
	        }
	        
	        for (i = s.get_picture_range_start() ; i < s.get_picture_range_end() ; i++) {
	            if (s.get_picture(i).get_data(0) == null)
	            	return i;
	        }
	    }

	    Log.av_log("AVCodecContext", Log.AV_LOG_FATAL, "Internal error, picture buffer overflow\n");
	    /* We could return -1, but the codec would crash trying to draw into a
	     * non-existing frame anyway. This is safer than waiting for a random crash.
	     * Also the return of this is never useful, an encoder must only allocate
	     * as much as allowed in the specification. This has no relationship to how
	     * much libavcodec could allocate (and MAX_PICTURE_COUNT is always large
	     * enough for such valid streams).
	     * Plus, a decoder has to check stream validity and remove frames if too
	     * many reference frames are around. Waiting for "OOM" is not correct at
	     * all. Similarly, missing reference frames have to be replaced by
	     * interpolated/MC frames, anything else is a bug in the codec ...
	     */
	    return -1;
	}


	public static int ff_alloc_picture(MpegEncContext s, Picture pic, int shared) {
	    int big_mb_num = s.get_mb_stride() * (s.get_mb_height() + 1) + 1; //the +1 is needed so memset(,,stride*height) does not sig11
	    int mb_array_size = s.get_mb_stride() * s.get_mb_height();
	    int b8_array_size = s.get_b8_stride() * s.get_mb_height() * 2;
	    int b4_array_size = s.get_b4_stride() * s.get_mb_height() * 4;
	    int i;
	    int r = -1;

	    if (shared != 0){
	        pic.set_type(AVCodec.FF_BUFFER_TYPE_SHARED);
	    }else{

	        if (alloc_frame_buffer(s, pic) < 0)
	            return -1;

	        s.set_linesize(pic.get_linesize(0));
	        s.set_uvlinesize(pic.get_linesize(1));
	    }

	    if (pic.get_qscale_table() == null){
	        if (s.get_encoding() != 0) {
	            pic.set_mb_var(new int[mb_array_size]);
	            pic.set_mc_mb_var(new int[mb_array_size]);
	            pic.set_mb_mean(new byte[mb_array_size]);
	        }

	        pic.set_mbskip_table(new byte[mb_array_size]);
	        pic.set_qscale_table(new byte[mb_array_size]);
	        pic.set_mb_type_base(new long[big_mb_num + s.get_mb_stride()]);
	        pic.set_mb_type(Arrays.copyOfRange(pic.get_mb_type_base(), 2*s.get_mb_stride()+1, pic.get_mb_type_base().length));
	        if (s.get_out_format() == OutputFormat.FMT_H264) {
	            for (i = 0 ; i < 2 ; i++) {
	                pic.set_motion_val_base(i, new int[2][b4_array_size+4]);
	                pic.set_motion_val(i, Arrays.copyOfRange(pic.get_motion_val_base(i), 4, pic.get_motion_val_base(i).length));
	                pic.set_ref_index(i, new byte[4*mb_array_size]);
	            }
	            pic.set_motion_subsample_log2((byte) 2);
	        } else if (s.get_out_format() == OutputFormat.FMT_H263 || 
	        		    s.get_encoding() != 0 || 
	        		    (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_MV) != 0 || 
	        		    (s.get_avctx().get_debug_mv() != 0) ) {
	            for (i = 0 ; i < 2 ; i++) {
	                pic.set_motion_val_base(i, new int[2][b8_array_size+4]);
	                // TODO Jerome
	                //pic.set_motion_val(i, Arrays.copyOfRange(pic.get_motion_val_base(i), 4, pic.get_motion_val_base(i).length));
	                pic.set_motion_val(pic.get_motion_val_base());
	                pic.set_ref_index(i, new byte[4*mb_array_size]);
	            }
	            pic.set_motion_subsample_log2((byte) 3);
	        }
	        if ( (s.get_avctx().get_debug() & AVCodec.FF_DEBUG_DCT_COEFF) != 0) {
	            pic.set_dct_coeff(new short[64 * mb_array_size]);
	        }
	        pic.set_qstride(s.get_mb_stride());
	        pic.set_pan_scan(new AVPanScan());
	    }

	    /* It might be nicer if the application would keep track of these
	     * but it would require an API change. */
	    s.set_prev_pict_types(Arrays.copyOfRange(s.get_prev_pict_types(), 1, s.get_prev_pict_types().length));
	    s.set_prev_pict_types(0, s.get_dropable() != 0 ? AVPictureType.AV_PICTURE_TYPE_B : s.get_pict_type());
	    if (pic.get_age() < PREV_PICT_TYPES_BUFFER_SIZE && 
    		s.get_prev_pict_types(pic.get_age()) == AVPictureType.AV_PICTURE_TYPE_B)
	        pic.set_age(Integer.MAX_VALUE); // Skipped MBs in B-frames are quite rare in MPEG-1/2 and it is a bit tricky to skip them anyway.
	    pic.set_owner2(null);

	    return 0;
	}


	private static int alloc_frame_buffer(MpegEncContext s, Picture pic) {
	    int r = 0;

	    // TODO Jerome
	    /*if (s.get_avctx().get_hwaccel() != null) {
	        if (s.get_avctx().get_hwaccel().get_priv_data_size() != 0) {
	            pic.set_hwaccel_picture_private(null);
	            if (!pic.get_hwaccel_picture_private) {
	                av_log(s.get_avctx, AV_LOG_ERROR, "alloc_frame_buffer() failed (hwaccel private data allocation)\n");
	                return -1;
	            }
	        }
	    }*/

	    r = Pthread.ff_thread_get_buffer(s.get_avctx(), (AVFrame) pic);

	    if ( r < 0 || pic.get_age() == 0 || pic.get_type() == 0 || pic.get_data(0) == null ) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "get_buffer() failed (%d %d %d)\n", 
	        		r, pic.get_age(), pic.get_type());
	        pic.set_hwaccel_picture_private(null);
	        return -1;
	    }

	    if (s.get_linesize() != 0 && 
	    	(s.get_linesize() != pic.get_linesize(0) || s.get_uvlinesize() != pic.get_linesize(1))) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "get_buffer() failed (stride changed)\n");
	        free_frame_buffer(s, pic);
	        return -1;
	    }

	    if (pic.get_linesize(1) != pic.get_linesize(2)) {
	    	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "get_buffer() failed (uv stride mismatch)\n");
	        free_frame_buffer(s, pic);
	        return -1;
	    }

	    return 0;
	}


	public static Picture ff_copy_picture(Picture src) {
	    Picture dst = (Picture) src.clone();
	    dst.set_type(AVCodec.FF_BUFFER_TYPE_COPY);
	    return dst;
	}

	/**
	 * generic function for encode/decode called after coding/decoding the header and before a frame is coded/decoded
	 */
	public static int MPV_frame_start(MpegEncContext s, AVCodecContext avctx) {
	    int i;
	    Picture pic;
	    s.set_mb_skipped(0);

	    /* mark&release old frames */
	    if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B && 
	    	s.get_last_picture_ptr() != null && 
	    	!s.get_last_picture_ptr().equals(s.get_next_picture_ptr()) && 
	    	s.get_last_picture_ptr().get_data(0) != null) {
	    	
	      if (s.get_out_format() != OutputFormat.FMT_H264 || s.get_codec_id() == CodecID.CODEC_ID_SVQ3) {
	          free_frame_buffer(s, s.get_last_picture_ptr());

	        /* release forgotten pictures */
	        /* if(mpeg124/h263) */
	        if (s.get_encoding() == 0) {
	            for (i = 0 ; i < s.get_picture_count() ; i++) {
	                if (s.get_picture(i).get_data(0) != null && 
	                	!s.get_picture(i).equals(s.get_next_picture_ptr()) && 
	                	s.get_picture(i).get_reference() != 0) {
	                    Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "releasing zombie picture\n");
	                    free_frame_buffer(s, s.get_picture(i));
	                }
	            }
	        }
	      }
	    }

	    if (s.get_encoding() == 0) {
	        ff_release_unused_pictures(s, 1);
	        
	        if (s.get_current_picture_ptr() != null && 
	        	s.get_current_picture_ptr().get_data(0) == null)
	            pic = s.get_current_picture_ptr(); //we already have a unused image (maybe it was set before reading the header)
	        else {
	            i = ff_find_unused_picture(s, 0);
	            pic = s.get_picture(i);
	        }

	        pic.set_reference(0);
	        if (s.get_dropable() == 0){
	            if (s.get_codec_id() == CodecID.CODEC_ID_H264)
	                pic.set_reference(s.get_picture_structure());
	            else if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B)
	                pic.set_reference(3);
	        }

	        s.set_coded_picture_number(s.get_coded_picture_number() + 1);
	        pic.set_coded_picture_number(s.get_coded_picture_number());

	        if(ff_alloc_picture(s, pic, 0) < 0)
	            return -1;

	        s.set_current_picture_ptr(pic);
	        //FIXME use only the vars from current_pic
	        s.get_current_picture_ptr().set_top_field_first(s.get_top_field_first());
	        if (s.get_codec_id() == CodecID.CODEC_ID_MPEG1VIDEO || s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO) {
	            if(s.get_picture_structure() != MpegVideo.PICT_FRAME)
	                s.get_current_picture_ptr().set_top_field_first((s.get_picture_structure() == MpegVideo.PICT_TOP_FIELD ? 1 : 0) == s.get_first_field() ? 1 : 0);
	        }
	        s.get_current_picture_ptr().set_interlaced_frame(s.get_progressive_frame() == 0 && s.get_progressive_sequence() == 0 ? 1 : 0);
	        s.get_current_picture_ptr().set_field_picture(s.get_picture_structure() != MpegVideo.PICT_FRAME ? 1 : 0);
	    }

	    s.get_current_picture_ptr().set_pict_type(s.get_pict_type());
//	    if(s.get_flags && CODEC_FLAG_QSCALE)
	  //      s.get_current_picture_ptr.get_quality= s.get_new_picture_ptr.get_quality;
	    s.get_current_picture_ptr().set_key_frame(s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I ? 1 : 0);

	    s.set_current_picture(ff_copy_picture(s.get_current_picture_ptr()));

	    if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B) {
	        s.set_last_picture_ptr(s.get_next_picture_ptr());
	        if (s.get_dropable() == 0)
	            s.set_next_picture_ptr(s.get_current_picture_ptr());
	    }
	/*    av_log(s.get_avctx, AV_LOG_DEBUG, "L%p N%p C%p L%p N%p C%p type:%d drop:%d\n", s.get_last_picture_ptr, s.get_next_picture_ptr,s.get_current_picture_ptr,
	        s.get_last_picture_ptr    ? s.get_last_picture_ptr.get_get_data(0) : NULL,
	        s.get_next_picture_ptr    ? s.get_next_picture_ptr.get_get_data(0) : NULL,
	        s.get_current_picture_ptr ? s.get_current_picture_ptr.get_get_data(0) : NULL,
	        s.get_pict_type, s.get_dropable);*/

	    if (s.get_codec_id() != CodecID.CODEC_ID_H264) {
	        if ( (s.get_last_picture_ptr() == null || s.get_last_picture_ptr().get_data(0) == null) &&
	             (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_I || s.get_picture_structure() != MpegVideo.PICT_FRAME) ) {
	            if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_I)
	                Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "warning: first frame is no keyframe\n");
	            else if (s.get_picture_structure() != MpegVideo.PICT_FRAME)
	                Log.av_log("AVCodecContext", Log.AV_LOG_INFO, "allocate dummy last picture for field based first keyframe\n");

	            /* Allocate a dummy frame */
	            i = ff_find_unused_picture(s, 0);
	            s.set_last_picture_ptr(s.get_picture(i));
	            if (ff_alloc_picture(s, s.get_last_picture_ptr(), 0) < 0)
	                return -1;
	            // TODO Jerome
	            // PThread.ff_thread_report_progress((AVFrame) s.get_last_picture_ptr(), Integer.MAX_VALUE, 0);
	            // PThread.ff_thread_report_progress((AVFrame) s.get_last_picture_ptr(), Integer.MAX_VALUE, 1);
	        }
	        if ((s.get_next_picture_ptr() == null || s.get_next_picture_ptr().get_data(0) == null) && 
	        	s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) {
	            /* Allocate a dummy frame */
	            i= ff_find_unused_picture(s, 0);
	            s.set_next_picture_ptr(s.get_picture(i));
	            if (ff_alloc_picture(s, s.get_next_picture_ptr(), 0) < 0)
	                return -1;
	            // TODO Jerome
	            // PThread.ff_thread_report_progress((AVFrame) s.get_next_picture_ptr(), Integer.MAX_VALUE, 0);
	            // PThread.ff_thread_report_progress((AVFrame) s.get_next_picture_ptr(), Integer.MAX_VALUE, 1);
	        }
	    }

	    if (s.get_last_picture_ptr() != null) 
	    	s.set_last_picture(ff_copy_picture(s.get_last_picture_ptr()));
	    if (s.get_next_picture_ptr() != null) 
	    	s.set_next_picture(ff_copy_picture(s.get_next_picture_ptr()));

	    if (s.get_picture_structure() != MpegVideo.PICT_FRAME && 
	    	s.get_out_format() != OutputFormat.FMT_H264) {
	        for (i = 0 ; i < 4 ; i++) {
	            if (s.get_picture_structure() == PICT_BOTTOM_FIELD){
	                 s.get_current_picture().set_data(i, Arrays.copyOfRange(s.get_current_picture().get_data(i),
	                		 												s.get_current_picture().get_linesize(i),
	                		 												s.get_current_picture().get_data(i).length));
	            }
	            s.get_current_picture().set_linesize(i, s.get_current_picture().get_linesize(i) * 2);
	            s.get_last_picture().set_linesize(i, s.get_last_picture().get_linesize(i) * 2);
	            s.get_next_picture().set_linesize(i, s.get_next_picture().get_linesize(i) * 2);
	        }
	    }

	    s.set_error_recognition(avctx.get_error_recognition());

	    /* set dequantizer, we can't do it during init as it might change for mpeg4
	       and we can't do it in the header decode as init is not called for mpeg4 there yet */
	    if (s.get_mpeg_quant() != 0 || s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO) {
	        s.set_dct_unquantize_intra("dct_unquantize_mpeg2_intra");
	        s.set_dct_unquantize_inter("dct_unquantize_mpeg2_inter");
	    } else if (s.get_out_format() == OutputFormat.FMT_H263 || s.get_out_format() == OutputFormat.FMT_H261) {
	        s.set_dct_unquantize_intra("dct_unquantize_h263_intra");
	        s.set_dct_unquantize_inter("dct_unquantize_h263_inter");
	    } else {
	        s.set_dct_unquantize_intra("dct_unquantize_mpeg1_intra");
	        s.set_dct_unquantize_inter("dct_unquantize_mpeg1_inter");
	    }

	    // TODO Jerome
	    /*if (s.get_dct_error_sum() != null) {
	        update_noise_reduction(s);
	    }*/

	    // TODO Jerome
	    /*if(CONFIG_MPEG_XVMC_DECODER && s.get_avctx.get_xvmc_acceleration)
	        return ff_xvmc_field_start(s, avctx);*/

	    return 0;
		
	}


	private static void ff_release_unused_pictures(MpegEncContext s, int remove_current) {
	    int i;

	    /* release non reference frames */
	    for (i = 0 ; i < s.get_picture_count() ; i++) {
	        if (s.get_picture(i).get_data(0) != null && 
	        	s.get_picture(i).get_reference() == 0 &&
	        	(s.get_picture(i).get_owner2() == null || s.get_picture(i).get_owner2() == s) &&
	           (remove_current != 0 || s.get_picture(i) != s.get_current_picture_ptr())
	           /*&& s.get_picture[i].type!=FF_BUFFER_TYPE_SHARED*/){
	            free_frame_buffer(s, s.get_picture(i));
	        }
	    }
	}


	private static void update_noise_reduction(MpegEncContext s) {
	    int intra, i;

	    for (intra = 0 ; intra < 2 ; intra++) {
	        if (s.get_dct_count(intra) > (1<<16)) {
	            for (i = 0 ; i < 64 ; i++) {
	            	s.get_dct_error_sum()[intra][i] >>=1;
	            }
	            s.get_dct_count()[intra] >>= 1;
	        }

	        for (i = 0 ; i < 64 ; i++) {
	            s.get_dct_offset()[intra][i] = (s.get_avctx().get_noise_reduction() * s.get_dct_count(intra) + 
	            		s.get_dct_error_sum()[intra][i] / 2) / (s.get_dct_error_sum()[intra][i]+1);
	        }
	    }
	}


	private static void free_frame_buffer(MpegEncContext s, Picture pic) {
	    //TODO Jerome
		//ff_thread_release_buffer(s.get_avctx(), (AVFrame) pic);
	    pic.set_hwaccel_picture_private(null);
	}


	public static void MPV_frame_end(MpegEncContext s) {
	    int i;
	    /* redraw edges for the frame if decoding didn't complete */
	    //just to make sure that all data is rendered.
	    if (Config.CONFIG_MPEG_XVMC_DECODER && 
	    	 s.get_avctx().get_xvmc_acceleration() != 0){
	        MpegVideoXvmc.ff_xvmc_field_end(s);
	   } else if ( (s.get_error_count() != 0 || s.get_encoding() != 0 || 
			         (s.get_avctx().get_codec().get_capabilities() & AVCodec.CODEC_CAP_DRAW_HORIZ_BAND) == 0) &&
	                s.get_avctx().get_hwaccel() == null &&
	                (s.get_avctx().get_codec().get_capabilities() & AVCodec.CODEC_CAP_HWACCEL_VDPAU) == 0 &&
	                s.get_unrestricted_mv() != 0 &&
	                s.get_current_picture().get_reference() != 0 &&
	                s.get_intra_only() == 0 &&
	                !s.has_flag(AVCodec.CODEC_FLAG_EMU_EDGE) ) {
	            int hshift = PixDesc.av_pix_fmt_descriptors.get(s.get_avctx().get_pix_fmt()).get_log2_chroma_w();
	            int vshift = PixDesc.av_pix_fmt_descriptors.get(s.get_avctx().get_pix_fmt()).get_log2_chroma_h();
	            s.get_dsp().draw_edges(s.get_current_picture().get_data(0), 
	            				       s.get_linesize(),
	            				       s.get_h_edge_pos(), 
	            				       s.get_v_edge_pos(),
	            				       DspUtil.EDGE_WIDTH, 
	            				       DspUtil.EDGE_WIDTH, 
	            				       DspUtil.EDGE_TOP | DspUtil.EDGE_BOTTOM);
	            s.get_dsp().draw_edges(s.get_current_picture().get_data(1), 
				 				       s.get_uvlinesize(),
								       s.get_h_edge_pos() >> hshift, 
								       s.get_v_edge_pos() >> vshift,
								       DspUtil.EDGE_WIDTH >> hshift, 
								       DspUtil.EDGE_WIDTH >> vshift, 
								       DspUtil.EDGE_TOP | DspUtil.EDGE_BOTTOM);
	            s.get_dsp().draw_edges(s.get_current_picture().get_data(2), 
				 				       s.get_uvlinesize(),
								       s.get_h_edge_pos() >> hshift, 
								       s.get_v_edge_pos() >> vshift,
								       DspUtil.EDGE_WIDTH >> hshift, 
								       DspUtil.EDGE_WIDTH >> vshift, 
								       DspUtil.EDGE_TOP | DspUtil.EDGE_BOTTOM);
	    }

	    s.set_last_pict_type(s.get_pict_type());
	    s.get_last_lambda_for()[s.get_pict_type().ordinal()] = s.get_current_picture_ptr().get_quality();
	    if (s.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B) {
	        s.set_last_non_b_pict_type(s.get_pict_type());
	    }

	    if (s.get_encoding() != 0) {
	        /* release non-reference frames */
	        for (i = 0 ; i < s.get_picture_count() ; i++) {
	            if (s.get_picture(i).get_data(0) != null && 
	            	s.get_picture(i).get_reference() == 0 /*&& s.get_picture[i].type!=FF_BUFFER_TYPE_SHARED*/) {
	                free_frame_buffer(s, s.get_picture(i));
	            }
	        }
	    }
	  
	    s.get_avctx().set_coded_frame((AVFrame) s.get_current_picture_ptr());

	    if (s.get_codec_id() != CodecID.CODEC_ID_H264 && 
	    	s.get_current_picture().get_reference() != 0) {
	        //Pthread.ff_thread_report_progress((AVFrame) s.get_current_picture_ptr(), s.get_mb_height() - 1, 0);
	    }
	}


	public static void ff_update_duplicate_context(MpegEncContext dst, MpegEncContext src) {
	    MpegEncContext bak = new MpegEncContext();
	    int i;
	    MpegVideo.backup_duplicate_context(bak, dst);
	    dst = (MpegEncContext) src.clone();
	    MpegVideo.backup_duplicate_context(dst, bak);
	    
	    /* TODO Jerome
	    for(i=0;i<12;i++){
	        dst.set_pblocks(i, dst.get_block()[i]);
	    }*/
	}


	private static void backup_duplicate_context(MpegEncContext bak,
			MpegEncContext src) {
		bak.set_allocated_edge_emu_buffer(src.get_allocated_edge_emu_buffer());
	    bak.set_edge_emu_buffer(src.get_edge_emu_buffer());
	    bak.get_me().set_scratchpad(src.get_me().get_scratchpad());
	    bak.get_me().set_temp(src.get_me().get_temp());
	    bak.set_rd_scratchpad(src.get_rd_scratchpad());
	    bak.set_b_scratchpad(src.get_b_scratchpad());
	    bak.set_obmc_scratchpad(src.get_obmc_scratchpad());
	    bak.get_me().set_map(src.get_me().get_map());
	    bak.get_me().set_score_map(src.get_me().get_score_map());
	    bak.set_blocks(src.get_blocks());
	    bak.set_block(src.get_block());
	    bak.set_start_mb_y(src.get_start_mb_y());
	    bak.set_end_mb_y(src.get_end_mb_y());
	    bak.get_me().set_map_generation(src.get_me().get_map_generation());
	    bak.set_pb(src.get_pb());
	    bak.set_dct_error_sum(src.get_dct_error_sum());
	    bak.get_dct_count()[0] = src.get_dct_count()[0];
	    bak.get_dct_count()[1] = src.get_dct_count()[1];
	    bak.set_ac_val_base(src.get_ac_val_base());
	    bak.get_ac_val()[0] = src.get_ac_val()[0];
	    bak.get_ac_val()[1] = src.get_ac_val()[1];
	    bak.get_ac_val()[2] = src.get_ac_val()[2];
		
	}

}
