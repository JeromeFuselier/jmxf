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

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.utils.InternalBuffer;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AudioConvert;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.UtilsArrays;


public class UtilsCodec {
	
	public static int INTERNAL_BUFFER_SIZE = (32+1);
	

	public static int av_get_bits_per_sample(CodecID codec_id){
	    switch(codec_id){
	    case CODEC_ID_ADPCM_SBPRO_2:
	        return 2;
	    case CODEC_ID_ADPCM_SBPRO_3:
	        return 3;
	    case CODEC_ID_ADPCM_SBPRO_4:
	    case CODEC_ID_ADPCM_CT:
	    case CODEC_ID_ADPCM_IMA_WAV:
	    case CODEC_ID_ADPCM_MS:
	    case CODEC_ID_ADPCM_YAMAHA:
	        return 4;
	    case CODEC_ID_ADPCM_G722:
	    case CODEC_ID_PCM_ALAW:
	    case CODEC_ID_PCM_MULAW:
	    case CODEC_ID_PCM_S8:
	    case CODEC_ID_PCM_U8:
	    case CODEC_ID_PCM_ZORK:
	        return 8;
	    case CODEC_ID_PCM_S16BE:
	    case CODEC_ID_PCM_S16LE:
	    case CODEC_ID_PCM_S16LE_PLANAR:
	    case CODEC_ID_PCM_U16BE:
	    case CODEC_ID_PCM_U16LE:
	        return 16;
	    case CODEC_ID_PCM_S24DAUD:
	    case CODEC_ID_PCM_S24BE:
	    case CODEC_ID_PCM_S24LE:
	    case CODEC_ID_PCM_U24BE:
	    case CODEC_ID_PCM_U24LE:
	        return 24;
	    case CODEC_ID_PCM_S32BE:
	    case CODEC_ID_PCM_S32LE:
	    case CODEC_ID_PCM_U32BE:
	    case CODEC_ID_PCM_U32LE:
	    case CODEC_ID_PCM_F32BE:
	    case CODEC_ID_PCM_F32LE:
	        return 32;
	    case CODEC_ID_PCM_F64BE:
	    case CODEC_ID_PCM_F64LE:
	        return 64;
	    default:
	        return 0;
	    }
	}
	
	public static boolean tb_unreliable(AVCodecContext c) {
	    if (  (c.get_time_base().get_den() >= 101L * c.get_time_base().get_num())
	       || (c.get_time_base().get_den() <    5L * c.get_time_base().get_num())
	       || (c.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO)
	       || (c.get_codec_id() == CodecID.CODEC_ID_H264) )
	        return true;
	    return false;
	}

	public static void avcodec_set_dimensions(AVCodecContext s,
			int width, int height) {
		s.avcodec_set_dimensions(width, height);	
	}

	public static String avcodec_string(AVCodecContext enc, boolean encode) {
		AVCodec p;
		String codec_name;
		String profile = null;
	    AVRational display_aspect_ratio;
		
		if (encode)
			p = AVCodec.avcodec_find_encoder(enc.get_codec_id());
		else
			p = AVCodec.avcodec_find_decoder(enc.get_codec_id());
		
		if (p != null) {
			codec_name = p.get_name();
			profile = p.av_get_profile_name(enc.get_profile());
		} else if  (enc.get_codec_id() == CodecID.CODEC_ID_MPEG2TS) {
			codec_name = "mpeg2ts";			
		} else if (!enc.get_codec_name().equals("")) {
			codec_name = enc.get_codec_name();
		} else {
	        /* output avi tags */
	        String tag_buf = av_get_codec_tag_string(enc.get_codec_tag());
	        codec_name = String.format("%s / 0x%04X", tag_buf, enc.get_codec_tag());
		}
		
		String buf = "";
		switch (enc.get_codec_type()) {
		case AVMEDIA_TYPE_VIDEO:
			buf += String.format("Video: %s%s", codec_name, 
					enc.get_mb_threshold() !=0 ? "(hq)" : "");
			
			if (profile != null){ 
				buf += " (" + profile + ")";
			}
			
			if (enc.get_pix_fmt() != PixelFormat.PIX_FMT_NONE) {
				buf += ", " + PixDesc.av_get_pix_fmt_name(enc.get_pix_fmt());
			}
			
			if (enc.get_width() != 0) {
				buf += String.format(", %dx%d", enc.get_width(), enc.get_height());
				
				if (enc.get_sample_aspect_ratio().get_num() != 0) {
					display_aspect_ratio = AVRational.av_reduce(enc.get_width() * enc.get_sample_aspect_ratio().get_num(),
														        enc.get_height() * enc.get_sample_aspect_ratio().get_den(),
														        1024*1024);
					buf += String.format(" [PAR %d:%d DAR %d:%d]",
									    enc.get_sample_aspect_ratio().get_num(),
									    enc.get_sample_aspect_ratio().get_den(),
									    display_aspect_ratio.get_num(),
									    display_aspect_ratio.get_den());
				}
			}
			
			if (encode) {
				buf += String.format(", q=%d-%d", enc.get_qmin(), enc.get_qmax());  
			}
			break;
			
		case AVMEDIA_TYPE_AUDIO:
			buf += String.format("Audio: %s", codec_name);
			
			if (profile != null){ 
				buf += " (" + profile + ")";
			}
			
			if (enc.get_sample_rate() != 0) {
				buf += String.format(", %d Hz", enc.get_sample_rate());
			}
			buf += ", ";
			
			buf += AudioConvert.av_get_channel_layout_string(enc.get_channels(), enc.get_channel_layout());
			
			if (enc.get_sample_fmt() != AVSampleFormat.AV_SAMPLE_FMT_NONE) {
				buf += String.format(", %s", SampleFmt.av_get_sample_fmt_name(enc.get_sample_fmt()));
			}
			break;
		    case AVMEDIA_TYPE_DATA:
				buf += String.format("Data: %s", codec_name);
		        break;
		    case AVMEDIA_TYPE_SUBTITLE:
				buf += String.format("Subtitle: %s", codec_name);
		        break;
		    case AVMEDIA_TYPE_ATTACHMENT:
				buf += String.format("Attachment: %s", codec_name);
		        break;
		    default:
				buf += String.format("Invalid Codec type %d", enc.get_codec_type());
		        return buf;
		}
		
		if (encode) {
			if (enc.has_flag(AVCodec.CODEC_FLAG_PASS1)) 
				buf += ", pass 1";

			if (enc.has_flag(AVCodec.CODEC_FLAG_PASS2)) 
				buf += ", pass 2";
		}
		
		int bitrate = get_bit_rate(enc); 
		
		if (bitrate != 0) {
			buf += String.format(", %d kb/s", bitrate / 1000);
		}
		
		
		
		
		return buf;
	}

	public static int get_bit_rate(AVCodecContext ctx) {
	    int bit_rate;
	    int bits_per_sample;
	    
	    switch (ctx.get_codec_type()) {
	    case AVMEDIA_TYPE_VIDEO:
	    case AVMEDIA_TYPE_DATA:
	    case AVMEDIA_TYPE_SUBTITLE:
	    case AVMEDIA_TYPE_ATTACHMENT:
	        bit_rate = ctx.get_bit_rate();
	        break;
	    case AVMEDIA_TYPE_AUDIO:
	        bits_per_sample = av_get_bits_per_sample(ctx.get_codec_id());
	        if (bits_per_sample != 0)
	        	bit_rate = ctx.get_sample_rate() * ctx.get_channels() * bits_per_sample;
	        else
		        bit_rate = ctx.get_bit_rate();
	        break;
	    default:
	        bit_rate = 0;
	        break;
	    }
	    return bit_rate;
	}

	public static String av_get_codec_tag_string(int codec_tag) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0 ; i < 4 ; i++) {
			int c = codec_tag&0xFF;
			
			if (!Character.isISOControl(c))
				sb.append((char)c);
			else
				sb.append("[" + c + "]");
			codec_tag >>= 8;			
		}
		
		return sb.toString();
	}

	private static String av_get_profile_name(AVCodec p, int profile) {
		return p.av_get_profile_name(profile);
	}

	public static void avcodec_close(AVCodecContext avctx) {
		avctx.avcodec_close();		
	}

	public static AVFrame avcodec_get_frame_defaults() {
		AVFrame pic = new AVFrame();
		
		pic.set_pts(AVUtil.AV_NOPTS_VALUE);
		pic.set_best_effort_timestamp(AVUtil.AV_NOPTS_VALUE);
		pic.set_pkt_pos(-1);
		pic.set_key_frame(1);
		pic.set_sample_aspect_ratio(new AVRational(0, 1));
		pic.set_formatA(AVSampleFormat.AV_SAMPLE_FMT_NONE); 	/* unknown */
		pic.set_formatV(PixelFormat.PIX_FMT_NONE); 	/* unknown */
		
		return pic;
	}

	public static int ff_match_2uint16(int[][] tab, int a, int b) {
		for (int i = 0 ; i < tab.length ; i++)
			if ( (tab[i][0] == a) && (tab[i][1] == b) )
				return i;
		return tab.length;
	}

	public static int ff_toupper4(int x) {
	    return     toupper( x     &0xFF)
		        + (toupper((x>>8 )&0xFF)<<8 )
		        + (toupper((x>>16)&0xFF)<<16)
		        + (toupper((x>>24)&0xFF)<<24);
	}

	private static int toupper(int i) {
		Character c = (char) i;
		c = Character.toUpperCase(c);		
		return (int)c;
	}

	public static int avcodec_default_get_buffer(AVCodecContext s, AVFrame pic) {
	    int i;
	    int w = s.get_width();
	    int h = s.get_height();
	    InternalBuffer buf;
	    int picture_number;

	    if (pic.get_data(0) != null) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "pic->data[0]!=NULL in avcodec_default_get_buffer\n");
	        return -1;
	    }
	    
	    if (s.get_internal_buffer_count() >= INTERNAL_BUFFER_SIZE) {
	    	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "internal_buffer_count overflow (missing release_buffer?)\n");
	        return -1;
	    }

	    if (ImgUtils.av_image_check_size(w, h, 0, s) != 0)
	        return -1;

	    if (s.get_internal_buffer() == null) {
	        s.set_internal_buffer(new InternalBuffer[INTERNAL_BUFFER_SIZE+1]);
	        for (i = 0 ; i < INTERNAL_BUFFER_SIZE+1 ; i++)
	        	s.set_internal_buffer(i, new InternalBuffer());
	    }

	    buf = (InternalBuffer) s.get_internal_buffer(s.get_internal_buffer_count());
	    picture_number = ((InternalBuffer) s.get_internal_buffer(INTERNAL_BUFFER_SIZE)).get_last_pic_num(); //FIXME ugly hack
	    picture_number++;

	    if (buf.get_base(0) != null && 
	    	(buf.get_width() != w || buf.get_height() != h || buf.get_pix_fmt() != s.get_pix_fmt())) {
	        if ( (s.get_active_thread_type() & AVCodec.FF_THREAD_FRAME) != 0) {
	            av_log_missing_feature("AVCodecContext", "Width/height changing with frame threads is", 0);
	            return -1;
	        }

	        for (i = 0 ; i < 4 ; i++) {
	        	buf.set_base(i, null);
	            buf.set_data(i, null);
	        }
	    }

	    if (buf.get_base(0) != null) {
	        pic.set_age(picture_number - buf.get_last_pic_num());
	        buf.set_last_pic_num(picture_number);
	    } else {
	        int h_chroma_shift, v_chroma_shift;
	        int [] size = {0, 0, 0, 0};
	        int tmpsize;
	        int unaligned;
	        AVPicture picture = new AVPicture();
	        int [] stride_align = new int[4];
	        int pixel_size = PixDesc.av_pix_fmt_descriptors.get(s.get_pix_fmt()).get_comp(0).get_step_minus1() + 1;

	        OutII ret_obj = ImgConvert.avcodec_get_chroma_sub_sample(s.get_pix_fmt());
	        h_chroma_shift = ret_obj.get_val1();
	        v_chroma_shift = ret_obj.get_val2();

	        ret_obj = avcodec_align_dimensions2(s, w, h, stride_align);
	        w = ret_obj.get_val1();
	        h = ret_obj.get_val2();

	        if (!s.has_flag(AVCodec.CODEC_FLAG_EMU_EDGE)) {
	            w += DspUtil.EDGE_WIDTH * 2;
	            h += DspUtil.EDGE_WIDTH * 2;
	        }

	        do {
	            // NOTE: do not align linesizes individually, this breaks e.g. assumptions
	            // that linesize[0] == 2*linesize[1] in the MPEG-encoder for 4:2:2
	    	    OutOI ret_obj1 = ImgUtils.av_image_fill_linesizes(s.get_pix_fmt(), w);
	    	    picture.set_linesize((int []) ret_obj1.get_obj());
	    	    
	            // increase alignment of w for next try (rhs gives the lowest bit set in w)
	            w += w & ~(w-1);

	            unaligned = 0;
	            for (i = 0 ; i < 4 ; i++) {
	                unaligned |= picture.get_linesize(i) % stride_align[i];
	            }
	        } while (unaligned != 0);

	        
	        OutOI ret_obj1 = ImgUtils.av_image_fill_pointers(s.get_pix_fmt(), h, null, picture.get_linesize());
	        picture.set_data((short [][]) ret_obj1.get_obj());
	        tmpsize = ret_obj1.get_ret();
	        
	        if (tmpsize < 0)
	            return -1;

	        for (i = 0 ; i < 3 && picture.get_data(i+1).length != 0; i++)
	            size[i] = picture.data[i].length;//picture.get_data[i+1] - picture.data[i];
	        size[i] = picture.data[i].length;
	        //size[i] = tmpsize - (picture.data[i] - picture.data[0]);
	        /*if (i == 0)
	        	size[i] = tmpsize;
	        else if (i == 1)
	        	size[i] = tmpsize - picture.get_data(1).length;
        	else if (i == 2)
        		size[i] = tmpsize - (picture.get_data(2).length + picture.get_data(1).length);
        	else if (i == 3)
        		size[i] = tmpsize - (picture.get_data(3).length + picture.get_data(2).length + picture.get_data(1).length);
	        */

	        buf.set_last_pic_num(-256*256*256*64);
	        
	        for (int k = 0 ; k < 4 ; k++) {
	        	if (buf.get_base(k) != null)
	        		Arrays.fill(buf.get_base(k), (short) 0);
	        	if (buf.get_data(k) != null)
	        		Arrays.fill(buf.get_data(k), (short) 0);
	        }

	        for (i = 0 ; i < 4 && size[i] != 0; i++) {
	            int h_shift = i==0 ? 0 : h_chroma_shift;
	            int v_shift = i==0 ? 0 : v_chroma_shift;

	            buf.set_linesize(i, picture.get_linesize(i));

	            buf.set_base(i, new short[size[i]+16]); //FIXME 16
	            if (buf.get_base(i) == null) 
	            	return -1;
	            Arrays.fill(buf.get_base(i), (short)128);

	            // no edge if EDGE EMU or not planar YUV
	            if ( s.has_flag(AVCodec.CODEC_FLAG_EMU_EDGE) || size[2] == 0 )
	                buf.set_data(i, buf.get_base(i));
	            else
	                buf.set_data(i, Arrays.copyOfRange(buf.get_base(i), Common.FFALIGN((buf.get_linesize(i) * DspUtil.EDGE_WIDTH >> v_shift) + (pixel_size * DspUtil.EDGE_WIDTH>>h_shift), stride_align[i]),buf.get_base(i).length)) ;
	        }
	        if (size[1] != 0 && size[2] == 0) {
	        	long [] pal = UtilsArrays.short_to_long(buf.get_data(1));
	            ImgUtils.ff_set_systematic_pal2(pal, s.get_pix_fmt());
	            buf.set_data(1, UtilsArrays.long_to_short_le(pal));
	        }
	        buf.set_width(s.get_width());
	        buf.set_height(s.get_height());
	        buf.set_pix_fmt(s.get_pix_fmt());
	        pic.set_age(256*256*256*64);
	    }
	    pic.set_type(AVCodec.FF_BUFFER_TYPE_INTERNAL);

	    for (i = 0 ; i < 4 ; i++) {
	        pic.set_base(i, buf.get_base(i));
	        pic.set_data(i, buf.get_data(i));
	        pic.set_linesize(i, buf.get_linesize(i));
	    }
	    s.set_internal_buffer_count(s.get_internal_buffer_count() + 1);

	    if (s.get_pkt() != null) {
	        pic.set_pkt_pts(s.get_pkt().get_pts());
	        pic.set_pkt_pos(s.get_pkt().get_pos());
	    } else {
	        pic.set_pkt_pts(AVUtil.AV_NOPTS_VALUE);
	        pic.set_pkt_pos(-1);
	    }
	    pic.set_reordered_opaque(s.get_reordered_opaque());
	    pic.set_sample_aspect_ratio(s.get_sample_aspect_ratio());
	    pic.set_width(s.get_width());
	    pic.set_height(s.get_height());
	    pic.set_format(s.get_pix_fmt());

	    if ( (s.get_debug() & AVCodec.FF_DEBUG_BUFFERS) != 0 )
	    	Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "default_get_buffer called on pic %p, %d buffers used\n", pic, s.get_internal_buffer_count());

	    return 0;
	}

	private static OutII avcodec_align_dimensions2(AVCodecContext s, int width,
			int height, int[] linesize_align) {
	    int w_align= 1;
	    int h_align= 1;

	    switch(s.get_pix_fmt()){
	    case PIX_FMT_YUV420P:
	    case PIX_FMT_YUYV422:
	    case PIX_FMT_UYVY422:
	    case PIX_FMT_YUV422P:
	    case PIX_FMT_YUV440P:
	    case PIX_FMT_YUV444P:
	    case PIX_FMT_GRAY8:
	    case PIX_FMT_GRAY16BE:
	    case PIX_FMT_GRAY16LE:
	    case PIX_FMT_YUVJ420P:
	    case PIX_FMT_YUVJ422P:
	    case PIX_FMT_YUVJ440P:
	    case PIX_FMT_YUVJ444P:
	    case PIX_FMT_YUVA420P:
	    case PIX_FMT_YUV420P9LE:
	    case PIX_FMT_YUV420P9BE:
	    case PIX_FMT_YUV420P10LE:
	    case PIX_FMT_YUV420P10BE:
	    case PIX_FMT_YUV422P10LE:
	    case PIX_FMT_YUV422P10BE:
	        w_align = 16; //FIXME check for non mpeg style codecs and use less alignment
	        h_align = 16;
	        if (s.get_codec_id() == CodecID.CODEC_ID_MPEG2VIDEO || 
	        	s.get_codec_id() == CodecID.CODEC_ID_MJPEG || 
	        	s.get_codec_id() == CodecID.CODEC_ID_AMV || 
	        	s.get_codec_id() == CodecID.CODEC_ID_THP || 
	        	s.get_codec_id() == CodecID.CODEC_ID_H264)
	            h_align= 32; // interlaced is rounded up to 2 MBs
	        break;
	    case PIX_FMT_YUV411P:
	    case PIX_FMT_UYYVYY411:
	        w_align =32;
	        h_align =8;
	        break;
	    case PIX_FMT_YUV410P:
	        if (s.get_codec_id() == CodecID.CODEC_ID_SVQ1) {
	            w_align = 64;
	            h_align = 64;
	        }
	    case PIX_FMT_RGB555LE:
	        if (s.get_codec_id() == CodecID.CODEC_ID_RPZA) {
	            w_align = 4;
	            h_align = 4;
	        }
	    case PIX_FMT_PAL8:
	    case PIX_FMT_BGR8:
	    case PIX_FMT_RGB8:
	        if (s.get_codec_id() == CodecID.CODEC_ID_SMC) {
	            w_align = 4;
	            h_align = 4;
	        }
	        break;
	    case PIX_FMT_BGR24:
	        if (s.get_codec_id() == CodecID.CODEC_ID_MSZH ||
	        	s.get_codec_id() == CodecID.CODEC_ID_ZLIB) {
	            w_align = 4;
	            h_align = 4;
	        }
	        break;
	    default:
	        w_align = 1;
	        h_align = 1;
	        break;
	    }

	    width  = Common.FFALIGN(width , w_align);
	    height = Common.FFALIGN(height, h_align);
	    if (s.get_codec_id() == CodecID.CODEC_ID_H264 || s.get_lowres() != 0)
	        height += 2; // some of the optimized chroma MC reads one line too much
	                    // which is also done in mpeg decoders with lowres > 0

	    linesize_align[0] =
	    linesize_align[1] =
	    linesize_align[2] =
	    linesize_align[3] = DspUtil.STRIDE_ALIGN;
	//STRIDE_ALIGN is 8 for SSE* but this does not work for SVQ1 chroma planes
	//we could change STRIDE_ALIGN to 16 for x86/sse but it would increase the
	//picture size unneccessarily in some cases. The solution here is not
	//pretty and better ideas are welcome!
	    if (s.get_codec_id() == CodecID.CODEC_ID_SVQ1 || 
	    	s.get_codec_id() == CodecID.CODEC_ID_VP5 ||
	    	s.get_codec_id() == CodecID.CODEC_ID_VP6 || 
	    	s.get_codec_id() == CodecID.CODEC_ID_VP6F ||
	    	s.get_codec_id() == CodecID.CODEC_ID_VP6A) {
	        linesize_align[0] =
	        linesize_align[1] =
	        linesize_align[2] = 16;
	    }
	
		return new OutII(width, height);
	}

	private static void av_log_missing_feature(String log_ctx, String feature,
			int want_sample) {
	    Log.av_log(log_ctx, Log.AV_LOG_WARNING, "%s not implemented. Update your FFmpeg " +
	            "version to the newest one from Git. If the problem still " +
	            "occurs, it means that your file has a feature which has not " +
	            "been implemented.\n", feature);
	  /*  if (want_sample!= 0)
	        av_log_ask_for_sample(log_ctx, null);*/
	}

	public static void avcodec_default_release_buffer(AVCodecContext s, AVFrame pic) {
	    int i;
	    InternalBuffer buf, last;

	    if (s.get_internal_buffer() != null) {
		    buf = null; /* avoids warning */
		    for (i = 0 ; i < s.get_internal_buffer_count() ; i++) { //just 3-5 checks so is not worth to optimize
		        buf = (InternalBuffer) s.get_internal_buffer(i);
		        if (buf.get_data(0) == pic.get_data(0))
		            break;
		    }
		    s.set_internal_buffer_count(s.get_internal_buffer_count() - 1);
	        last = (InternalBuffer) s.get_internal_buffer(s.get_internal_buffer_count());
	        
	        InternalBuffer tmp = buf;
	        buf = last;
	        last = tmp;
	    }

	    for (i = 0 ; i < 4 ; i++) {
	        pic.set_data(i, null);
//	        pic->base[i]=NULL;
	    }
	//printf("R%X\n", pic->opaque);

	    if ( (s.get_debug() & AVCodec.FF_DEBUG_BUFFERS) != 0)
	        Log.av_log("AVCodecContext", Log.AV_LOG_DEBUG, "default_release_buffer called on pic %p, %d buffers used\n", pic, s.get_internal_buffer_count());
	}

	public static PixelFormat avcodec_default_get_format(AVCodecContext s, PixelFormat [] fmt) {
		int idx_fmt = 0;
		while (fmt[idx_fmt] != PixelFormat.PIX_FMT_NONE && ImgConvert.ff_is_hwaccel_pix_fmt(fmt[idx_fmt]))
		        idx_fmt++;
	    return fmt[idx_fmt];
	}

}
