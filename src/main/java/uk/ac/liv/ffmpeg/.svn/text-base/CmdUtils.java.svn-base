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
 * Creation   : January 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavformat.AVDictionary;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libswscale.SwScale;
import uk.ac.liv.ffmpeg.libswscale.SwsContext;
import uk.ac.liv.ffmpeg.libswscale.UtilsScale;

public class CmdUtils {
	
	public static final int INDENT       = 1;
	public static final int SHOW_VERSION = 2;
	public static final int SHOW_CONFIG  = 4;

	public static Map<AVMediaType, AVCodecContext> avcodec_opts = new HashMap<AVMediaType, AVCodecContext>();
	
	public static AVFormatContext avformat_opts;
	
	public static ArrayList<String> opt_names = new ArrayList<String>();
	public static ArrayList<String> opt_values = new ArrayList<String>();

	public static AVDictionary format_opts;
	public static AVDictionary video_opts;
	public static AVDictionary audio_opts;
	public static AVDictionary sub_opts;
	public static SwsContext sws_opts = new SwsContext();
	
	
	public static void init_opts() {
		for(AVMediaType media_type : AVMediaType.values()) {
	    	avcodec_opts.put(media_type, AVCodecContext.avcodec_alloc_context3(null));
        }
 	    avformat_opts = AVFormatContext.avformat_alloc_context();

	    
	    sws_opts = UtilsScale.sws_getContext(16, 16, PixelFormat.values()[1], 
	    		16, 16, PixelFormat.values()[1], SwScale.SWS_BICUBIC, null, null,
	    		null);
	}


	public static void show_help_options(Map<String, OptionDef> options,
			String msg, int mask, int value) {
		boolean first = true;
		
		for (OptionDef po : options.values()) {
			String buf = "";
			if ( (po.get_flags() & mask) == value) {
				if (first) {
					System.out.println(msg);
					first = false;
				}
				buf += po.get_name();
				if (po.has_flag(OptionDef.HAS_ARG)) {
					buf += " " + po.get_argname();
				}
				System.out.println(String.format("-%-17s  %s", buf, po.get_help()));
			}
		}
		
	}


	public static void print_all_libs_info(int flags) {
		// TODO 
/*
	    PRINT_LIB_INFO(outstream, avutil,   AVUTIL,   flags);
	    PRINT_LIB_INFO(outstream, avcodec,  AVCODEC,  flags);
	    PRINT_LIB_INFO(outstream, avformat, AVFORMAT, flags);
	    PRINT_LIB_INFO(outstream, avdevice, AVDEVICE, flags);
	    PRINT_LIB_INFO(outstream, avfilter, AVFILTER, flags);
	    PRINT_LIB_INFO(outstream, swscale,  SWSCALE,  flags);
	    PRINT_LIB_INFO(outstream, postproc, POSTPROC, flags);*/
	}


	public static void set_context_opts(AVContext ctx,
			AVContext opts_ctx, int i, AVCodec codec) {
		Object priv_ctx = null;
		
		if (ctx.get_av_class().get_class_name().equals("AVCodecContext")) {
			AVCodecContext avctx = (AVCodecContext)ctx;
			if (codec != null) {
				if (codec.get_priv_class() != null) {
					if (avctx.get_priv_data() == null) {
						avctx.set_priv_data(Ffmpeg.alloc_priv_context(codec.get_priv_class()));
					}
					priv_ctx = avctx.get_priv_data();
				}
			}
		} else if (ctx.get_av_class().get_class_name().equals("AVFormatContext")) {
			AVFormatContext avctx = (AVFormatContext)ctx;
			if (avctx.get_oformat() != null) {
				if (avctx.get_oformat().get_priv_class() != null) 
					priv_ctx = avctx.get_priv_data();
			} else if (avctx.get_iformat() != null) {
				if (avctx.get_iformat().get_priv_class() != null) 				
					priv_ctx = avctx.get_priv_data();
			}				
		}
		/// TODO 
		/*for(i=0; i<opt_name_count; i++){
			 char buf[256];
		        const AVOption *opt;
		        const char *str;
		        if (priv_ctx) {
		            if (av_find_opt(priv_ctx, opt_names[i], NULL, flags, flags)) {
		                if (av_set_string3(priv_ctx, opt_names[i], opt_values[i], 1, NULL) < 0) {
		                    fprintf(stderr, "Invalid value '%s' for option '%s'\n",
		                            opt_values[i], opt_names[i]);
		                    exit(1);
		                }
		            } else
		                goto global;
		        } else {
		        global:
		            str = av_get_string(opts_ctx, opt_names[i], &opt, buf, sizeof(buf));
		            // if an option with name opt_names[i] is present in opts_ctx then str is non-NULL 
		            if (str && ((opt->flags & flags) == flags))
		                av_set_string3(ctx, opt_names[i], str, 1, NULL);
		        }
		    }*/
	}


	public static void uninit_opts() {
		int i;
		avcodec_opts = new HashMap<AVMediaType, AVCodecContext>();
		avformat_opts = null;
		
		opt_names = new ArrayList<String>();
		opt_values = new ArrayList<String>();
		
		format_opts = null;
		video_opts = null;
		audio_opts = null;
		sub_opts = null;
	}


	public static void print_error(String filename, int err) {
		String errbuf = Error.av_strerror(err);
		
		if (errbuf.equals("")) {
			errbuf = Integer.toString(err);
		}
		System.out.println(filename + ": " + errbuf);
		
	}


	public static boolean read_yesno() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String answer = null;
		try {
			answer = br.readLine();
       } catch (IOException ioe) {
	          return false;
       }
       return answer.toLowerCase().equals("y");
	}

}
