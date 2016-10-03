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

package uk.ac.liv.ffmpeg.libavformat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVPacketList;
import uk.ac.liv.ffmpeg.AVStream;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecParserContext;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavformat.img2.Img2;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.Tuple;
import uk.ac.liv.util.UtilsArrays;
import uk.ac.liv.util.UtilsString;

public class UtilsFormat {
	
	public static int SANE_NB_CHANNELS = 128;
	


	public static AVInputFormat avProbeInputFormat(AVProbeData pd, boolean isOpened) {
		return avProbeInputFormat2(pd, isOpened, 0);
	}


	private static AVInputFormat avProbeInputFormat2(AVProbeData pd,
			boolean isOpened, int scoreMax) {
		Tuple t = avProbeInputFormat3(pd, isOpened);
		AVInputFormat fmt = (AVInputFormat) t.first();
		int scoreRet = (Integer) t.second();
		
		if (scoreRet > scoreMax){
			scoreMax = scoreRet;  // Return score ?
			return fmt;
		} else
			return null;
	}
	
	
	private static Tuple avProbeInputFormat3(AVProbeData pd, boolean isOpened) {		
		AVInputFormat fmt = null;
		int score = 0;
		int scoreMax = 0;
		
		for (AVInputFormat fmt1: AVFormat.inputFormats.values()) {
			score = fmt1.read_probe(pd);
			
			if (score > scoreMax) {
				scoreMax = score;
				fmt = fmt1;
			} else if (score == scoreMax) 
				fmt = null;			
		}
		
		return new Tuple(fmt, scoreMax);
	}
	

	public static int av_get_packet(AVIOContext s, AVPacket pkt, int size) {
		pkt.set_pos(s.get_reader().position());
		pkt.set_size(size);

		
	    try {
	    	pkt.set_data(UtilsArrays.byte_to_short(s.get_reader().read_bytes(size)));
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	    
	    return 0;
	}


	public static int av_open_input_file(AVFormatContext ic, URI uri,
			AVInputFormat fmt, int buf_size, AVFormatParameters ap) {
		int err;
		AVDictionary opts = new AVDictionary(ap);
		
		if (ap == null) {
			ic = null;
		} else if (ap.get_prealloced_context() == 0) {
			ic = null;
		}
		err = avformat_open_input(ic, uri, fmt, opts);
		return err;
	}
	
	public static boolean av_match_ext(String filename, ArrayList<String> extensions) {
		if (filename == null) {
			return false;
		}
		
		String ext = filename.substring(filename.lastIndexOf(".")+1, filename.length());
		
		if (ext != null) {			
			for (String ext1 : extensions) {
				if (ext1.toLowerCase().equals(ext.toLowerCase())) {
					return true;
				}
			}
		}
		
		return false;
		
	}


	public static int avformat_open_input(AVFormatContext ps, URI uri,
						AVInputFormat fmt, AVDictionary options) {
		AVFormatContext s = ps;
		int ret;
		AVFormatParameters a = new AVFormatParameters();
		AVDictionary tmp = null;
		
		if (s == null) {
			s = AVFormatContext.avformat_alloc_context();
		}
		
		if (fmt != null) {
			s.set_iformat(fmt);
		}
		
		if (options != null) {
			tmp = new AVDictionary(options);
		}
		
		s.av_opt_set_dict(tmp);
		
		ret = s.init_input(uri);
		if (ret < 0) {
			return ret;
		}
		
		s.set_duration(AVUtil.AV_NOPTS_VALUE);
		s.set_start_time(AVUtil.AV_NOPTS_VALUE);
		
		s.set_raw_packet_buffer_remaining_size(AVFormat.RAW_PACKET_BUFFER_SIZE);
		
			
		

		return 0;
	}


	public static int av_demuxer_open(AVFormatContext ic, AVFormatParameters ap) {
		int err;
		
		err = ic.get_iformat().read_header(ic, ap);

		if (err < 0)
			return err;
				
		if ( (ic.get_pb() != null) && (ic.get_data_offset() == 0) )
			ic.set_data_offset(ic.get_pb().tell());
		
		return 0;
	}


	public static int avcodec_open(AVCodecContext avctx, AVCodec codec) {
		return avcodec_open2(avctx, codec, null);
	}
	

	public static int avcodec_open2(AVCodecContext avctx, AVCodec codec, AVDictionary options) {
		return avctx.avcodec_open2(codec, null);
	}
	
	
	public static int tb_unreliable(AVCodecContext c) {
		return c.tb_unreliable();
	}


	public static OutOI av_read_frame_internal(AVFormatContext s) {
		return s.av_read_frame_internal();		
	}


	public static int get_std_framerate(int i) {
	    if (i < 60 * 12) 
	    	return i*1001;
	    else {
	    	int [] tab = {24, 30, 60, 12, 15};
	    	return tab[i - 60 * 12] * 1000 * 12;
	    }
	}


	public static void av_close_input_file(AVFormatContext ic) {
		ic.av_close_input_file();
	}


	public static int av_seek_frame(AVFormatContext s, int stream_index, long timestamp,
			int flags) {
		return s.av_seek_frame(stream_index, timestamp, flags);
	}


	public static OutOI avformat_alloc_output_context2(AVOutputFormat oformat,
			String format, String filename) {
		AVFormatContext s = AVFormatContext.avformat_alloc_context();
		int ret = 0;
		
		if (oformat == null) {
			if (!format.equals("")) {
				oformat = UtilsFormat.av_guess_format(format, null, null);
				if (oformat == null) {
	                Log.av_log("formatCtx", Log.AV_LOG_ERROR, 
	                		"Requested output format '%s' is not a suitable output format\n", format);
					ret = Error.AVERROR(Error.EINVAL);
					UtilsFormat.avformat_free_context(s);
				    return new OutOI(null, ret);
				}				
			} else {
				oformat = UtilsFormat.av_guess_format(null, filename, null);
				if (oformat == null) {
					Log.av_log("formatCtx", Log.AV_LOG_ERROR, "Unable to find a suitable output format for '%s'\n",
	                        filename);
					ret = Error.AVERROR(Error.EINVAL);
					UtilsFormat.avformat_free_context(s);
				    return new OutOI(null, ret);
				}
			}
		}
		
		s.set_oformat(oformat);
		
		/*
		if (s.get_oformat.get_priv_data_size > 0) {
	        s.get_priv_data = av_mallocz(s.get_oformat.get_priv_data_size);
	        if (!s.get_priv_data)
	            goto nomem;
	        if (s.get_oformat.get_priv_class) {
	            *(const AVClass**)s.get_priv_data= s.get_oformat.get_priv_class;
	            av_opt_set_defaults(s.get_priv_data);
	        }
	    } else
	        s.get_priv_data = NULL;
	        */
		
		 if (filename != null) {
			s.set_filename_int(filename);
		}
		 
		
		return new OutOI(s, 0);
	}


	private static void avformat_free_context(AVFormatContext s) {
		s.avformat_free_context();
	}


	private static AVOutputFormat av_guess_format(String short_name, 
			String filename, String mime_type) {
		AVOutputFormat fmt_found = null;
		int score_max = 0;
		int score;

		for (AVOutputFormat fmt: AVFormat.outputFormats.values()) {
			score = 0;
		
			if (fmt.get_name() != null)
				if (fmt.get_name().equals(short_name))
					score += 100;
		
			if (fmt.get_mime_type() != null)
				if (fmt.get_mime_type().equals(mime_type))
					score += 10;
			
			if ( (filename != null) && (fmt.get_extensions() != null) )
				if (av_match_ext(filename, fmt.get_extensions()))
						score += 5;
				
			if (score > score_max) {
				score_max = score;
				fmt_found = fmt;
			}
		}
		
		return fmt_found;
	}


	public static CodecID av_guess_codec(AVOutputFormat fmt,
			String short_name, String filename, String mime_type, 
			AVMediaType type) {
		if (type == AVMediaType.AVMEDIA_TYPE_VIDEO) {
			CodecID codec_id = CodecID.CODEC_ID_NONE;
			
			if ( fmt.get_name().equals("image2") || fmt.get_name().equals("image2pipe") ) {
				codec_id = Img2.ff_guess_image2_codec(filename);
			}
			if (codec_id == CodecID.CODEC_ID_NONE)
				codec_id = fmt.get_video_codec();			
			
			return codec_id;
			
		} else if (type == AVMediaType.AVMEDIA_TYPE_AUDIO) {
			return fmt.get_audio_codec();
		} else if (type == AVMediaType.AVMEDIA_TYPE_SUBTITLE) {
			return fmt.get_subtitle_codec();
		} else {
			return CodecID.CODEC_ID_NONE;
		}
	}


	public static AVStream av_new_stream(AVFormatContext s, int id) {
	    return s.av_new_stream(id);
	}


	private static void av_set_pts_info(AVStream st, int i, int j, int k) {
	    AVRational new_tb;
	    
	}


	public static boolean av_filename_number_test(String filename) {
		OutOI ret_obj = av_get_frame_filename(filename, 1);
		String buf = (String) ret_obj.get_obj();
	    return (!filename.equals("")) && (!buf.equals(""));
	}


	public static OutOI av_get_frame_filename(String path, int number) {
		String buf = "";
	    int p;
	    char c;
	    int nd, len;
	    boolean percentd_found = false;

	    p = 0;
	    for (;;) {
	    	if (p >= path.length())
	    		c = '\0';
	    	else
	    		c = path.charAt(p++);
	        
	        if (c == '\0')
	            break;
	        if (c == '%') {
	            do {
	                nd = 0;
	                while (Character.isDigit(path.charAt(p))) {
	                    nd = nd * 10 + path.charAt(p++) - '0';
	                }
	    	        c = path.charAt(p++);
	            } while (Character.isDigit(c));

	            switch(c) {
	            case '%':
		            buf += c;
	            case 'd':
	                if (percentd_found)
	            	    return new OutOI("", -1);
	                percentd_found = true;
	                String num = String.format("%d", number);
	                if (nd > 0) {
		                int nb_0 = nd - num.length();
		                buf += UtilsString.repeat("0", nb_0);
	                }
	                buf += num;
	                break;
	            default:
	        	    return new OutOI("", -1);
	            }
	        } else {
	        	buf += c;
	        }
	    }
	    if (!percentd_found)
		    return new OutOI("", -1);
	    
	    return new OutOI(buf, 0);
	}


	public static long av_gettime() {
		return System.currentTimeMillis();
	}


	public static void av_dump_format(AVFormatContext ic, int index, String url, 
			boolean is_output) {
		ic.av_dump_format(index, url, is_output);
	}


	public static CodecID av_codec_get_id(AVCodecTag[] tags, int tag) {
		int i;
	    for (i = 0 ; (tags != null) && (tags[i] != null) ; i++){
	        CodecID id = ff_codec_get_id(Arrays.copyOfRange(tags, i, tags.length), tag);
	        if (id != CodecID.CODEC_ID_NONE) 
	        	return id;
	    }
	    return CodecID.CODEC_ID_NONE;   
	}


	private static CodecID ff_codec_get_id(AVCodecTag [] tags, int tag) {
		for (int i = 0 ; tags[i].get_id() != CodecID.CODEC_ID_NONE ; i++) {
	        if (tag == tags[i].tag)
	            return tags[i].id;
	    }
	    for (int i = 0; tags[i].get_id() != CodecID.CODEC_ID_NONE ; i++) {
	        if (UtilsCodec.ff_toupper4(tag) == UtilsCodec.ff_toupper4(tags[i].get_tag()))
	            return tags[i].get_id();
	    }
	    return CodecID.CODEC_ID_NONE;
	}


	public static int av_codec_get_tag(AVCodecTag[] tags, CodecID id) {
	    for (int i = 0 ; (tags != null) && (tags[i] != null) ; i++){
	        int tag = ff_codec_get_tag(Arrays.copyOfRange(tags, i, tags.length), id);
	        if (tag != 0) 
	        	return tag;
	    }
	    
		return 0;
	}

	private static int ff_codec_get_tag(AVCodecTag[] tags, CodecID id) {
		for (AVCodecTag ctag : tags) {
			if (ctag.get_id() == id)
				return ctag.get_tag();
		}
		return 0;
	}


	public static int validate_codec_tag(AVFormatContext s,	AVStream st) {
	    int n;
	    CodecID id = CodecID.CODEC_ID_NONE;
	    int tag = 0;

	    /**
	     * Check that tag + id is in the table
	     * If neither is in the table .get_ OK
	     * If tag is in the table with another id .get_ FAIL
	     * If id is in the table with another tag .get_ FAIL unless strict < normal
	     */
	    for (AVCodecTag avctag: s.get_oformat().get_codec_tag()) {
	    	if (UtilsCodec.ff_toupper4(avctag.get_tag()) == UtilsCodec.ff_toupper4(st.get_codec().get_codec_tag())) {
	    		id = avctag.get_id();
	    		if (id == st.get_codec().get_codec_id())
	    			return 1;
	    	}
	    	if (avctag.get_id() == st.get_codec().get_codec_id())
	    		tag = avctag.get_tag();
	    	
	    }
	    
	    if (id != CodecID.CODEC_ID_NONE)
	        return 0;
	    if ( (tag != 0) && (st.get_codec().get_strict_std_compliance() >= AVCodec.FF_COMPLIANCE_NORMAL))
	        return 0;
	    return 1;
	}


	public static OutOI av_read_frame(AVFormatContext s) {
		return s.av_read_frame();
	}


	public static void av_pkt_dump_log2(Object avcl, int level,
			AVPacket pkt, int dump_payload, AVStream st) {
	    pkt_dump_internal(avcl, null, level, pkt, dump_payload, st.get_time_base());
		
	}


	private static void pkt_dump_internal(Object avcl, File f,
			int level, AVPacket pkt, int dump_payload, AVRational time_base) {
		Log.av_log("formatCtx", level, String.format("stream #%d:", pkt.get_stream_index()));
		Log.av_log("formatCtx", level, String.format("  keyframe=%d\n", pkt.has_flag(AVCodec.AV_PKT_FLAG_KEY)));
		Log.av_log("formatCtx", level, String.format("  duration=%0.3f\n", pkt.get_duration() * time_base.av_q2d()));
	    /* DTS is _always_ valid after av_read_frame() */
		Log.av_log("formatCtx", level, "  dts=");
	    if (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE)
	    	Log.av_log("formatCtx", level, "N/A");
	    else
	    	Log.av_log("formatCtx", level, String.format("%0.3f", pkt.get_dts() * time_base.av_q2d()));
	    /* PTS may not be known if B-frames are present. */
    	Log.av_log("formatCtx", level, "  pts=");
	    if (pkt.get_pts() == AVUtil.AV_NOPTS_VALUE)
	    	Log.av_log("formatCtx", level, "N/A");
	    else
	    	Log.av_log("formatCtx", level, String.format("%0.3f", pkt.get_pts() * time_base.av_q2d()));
    	Log.av_log("formatCtx", level, String.format("  size=%d\n", pkt.get_size()));

	    if (dump_payload != 0)
	        av_hex_dump(f, pkt.get_data());
		
	}


	private static void av_hex_dump(File f, short [] buf) {
	    hex_dump_internal(null, f, 0, buf);
	}


	private static void hex_dump_internal(Object avcl, File f, int level,
			short [] buf) {
	    int len, i, j, c;

	    for (i = 0 ; i < buf.length ; i += 16) {
	        len = buf.length - i;
	        if (len > 16)
	            len = 16;
	        Log.av_log("formatCtx", level, String.format("%08x ", i));
	        for (j = 0 ; j < 16 ; j++) {
	            if (j < len)
	            	Log.av_log("formatCtx", level, String.format(" %02x", buf[i+j]));
	            else
	            	Log.av_log("formatCtx", level, "   ");
	        }
	        Log.av_log("formatCtx", level, " ");
	        for(j=0;j<len;j++) {
	            c = buf[i+j];
	            if (c < ' ' || c > '~')
	                c = '.';
	            Log.av_log("formatCtx", level, String.format("%c", c));
	        }
	        Log.av_log("formatCtx", level, "\n");
	    }		
	}


	public static AVRational compute_frame_duration(AVStream st,
			AVCodecParserContext pc, AVPacket pkt) {
		int frame_size;

	    int pnum = 0;
	    int pden = 0;
	    
	    switch(st.get_codec().get_codec_type()) {
	    case AVMEDIA_TYPE_VIDEO:
	        if (st.get_time_base().get_num() * 1000 > st.get_time_base().get_den()) {
	            pnum = st.get_time_base().get_num();
	            pden = st.get_time_base().get_den();
	        } else if (st.get_codec().get_time_base().get_num() * 1000 > st.get_codec().get_time_base().get_den()) {
	            pnum = st.get_codec().get_time_base().get_num();
	            pden = st.get_codec().get_time_base().get_den();
	            if ( (pc != null) && (pc.get_repeat_pict() != 0) ) {
	                pnum = pnum * (1 + pc.get_repeat_pict());
	            }
	            //If this codec can be interlaced or progressive then we need a parser to compute duration of a packet
	            //Thus if we have no parser in such case leave duration undefined.
	            if ( (st.get_codec().get_ticks_per_frame() > 1) && 
	            	 (pc == null) ) {
	                pnum = 0;
	                pden = 0;
	            }
	        }
	        break;
	    case AVMEDIA_TYPE_AUDIO:
	        frame_size = get_audio_frame_size(st.get_codec(), pkt.get_size());
	        if ( (frame_size <= 0) || (st.get_codec().get_sample_rate() <= 0) )
	            break;
	        pnum = frame_size;
	        pden = st.get_codec().get_sample_rate();
	        break;
	    default:
	        break;
	    }
	    return new AVRational(pnum, pden);
	}


	/**
	 * Get the number of samples of an audio frame. Return -1 on error.
	 */
	private static int get_audio_frame_size(AVCodecContext enc,
			int size) { 
		int frame_size;

	    if (enc.get_codec_id() == CodecID.CODEC_ID_VORBIS)
	        return -1;

	    if (enc.get_frame_size() <= 1) {
	        int bits_per_sample = UtilsCodec.av_get_bits_per_sample(enc.get_codec_id());

	        if (bits_per_sample != 0) {
	            if (enc.get_channels() == 0)
	                return -1;
	            frame_size = (size << 3) / (bits_per_sample * enc.get_channels());
	        } else {
	            /* used for example by ADPCM codecs */
	            if (enc.get_bit_rate() == 0)
	                return -1;
	            frame_size = (size * 8 * enc.get_sample_rate()) / enc.get_bit_rate();
	        }
	    } else {
	        frame_size = enc.get_frame_size();
	    }
	    return frame_size;
	}
}
