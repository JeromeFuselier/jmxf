package uk.ac.liv.ffmpeg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVInputFormat;
import uk.ac.liv.ffmpeg.libavformat.AVOutputFormat;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVPixFmtDescriptor;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;

public class OptionDef {
	

	public static final int HAS_ARG = 0x0001;
	public static final int OPT_BOOL = 0x0002;
	public static final int OPT_EXPERT = 0x0004;
	public static final int OPT_STRING = 0x0008;
	public static final int OPT_VIDEO = 0x0010;
	public static final int OPT_AUDIO = 0x0020;
	public static final int OPT_GRAB = 0x0040;
	public static final int OPT_INT = 0x0080;
	public static final int OPT_FLOAT = 0x0100;
	public static final int OPT_SUBTITLE = 0x0200;
	public static final int OPT_INT64 = 0x0400;
	public static final int OPT_EXIT = 0x0800;
	public static final int OPT_DATA = 0x1000;
	
    String name;
    int flags;
    OptionValue u;    
    String help;
    String argname;
    
    
	public OptionDef(String name, int flags, String func_arg, String help) {
		this.name = name;
		this.flags = flags;
		this.u = new OptionValue(func_arg);
		this.help = help;
	}	
	
	public OptionDef(String name, int flags, String func_arg, String help,
			String argname) {
		this.name = name;
		this.flags = flags;
		this.u = new OptionValue(func_arg);
		this.help = help;
		this.argname = argname;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public OptionValue get_u() {
		return u;
	}
	
	public void set_u(OptionValue u) {
		this.u = u;
	}
	
	public String get_help() {
		return help;
	}
	
	public void set_help(String help) {
		this.help = help;
	}

	public String get_argname() {
		return argname;
	}
	
	public void set_argname(String argname) {
		this.argname = argname;
	}


	public int opt_formats(String opt, String arg) {
		 
		System.out.println("File formats:");
		System.out.println(" D. = Demuxing supported");
		System.out.println(" .E = Muxing supported");
		System.out.println(" --");
		 
		Set<String> keys = new HashSet<String>();
		
		for (String s: AVFormat.inputFormats.keySet()){
			keys.add(s);
		}
		for (String s: AVFormat.outputFormats.keySet()){
			keys.add(s);
		}
		

		Iterator<String> itr = keys.iterator();
		 
		while(itr.hasNext()) {
			String key = (String)itr.next();
			AVInputFormat input = AVFormat.inputFormats.get(key);
			AVOutputFormat output = AVFormat.outputFormats.get(key);
			
			String name = input == null ? output.get_name(): input.get_name();
			String longName = input == null ? output.get_long_name(): input.get_long_name();			

			System.out.print(String.format(" %s%s %-15s %s,",
										   (input == null ? " " : "D"),
										   (output == null ? " " : "E"),
										   name,
										   longName));	
		}		
			 
		 
		return 0;
	}
	

	public int opt_codecs(String opt, String arg) {
		System.out.println("Codecs:");
		System.out.println(" D..... = Decoding supported");
		System.out.println(" .E.... = Encoding supported");
		System.out.println(" ..V... = Video codec");
		System.out.println(" ..A... = Audio codec");
		System.out.println(" ..S... = Subtitle codec");
		System.out.println(" ...S.. = Supports draw_horiz_band");
		System.out.println(" ....D. = Supports direct rendering method 1");
		System.out.println(" .....T = Supports weird frame truncation");
		System.out.println(" ------");
		System.out.println();
		
		for (AVCodec c: AVCodec.codecs.values()) {
			String typeStr;
			
			switch (c.get_type()) {
				case AVMEDIA_TYPE_VIDEO:
					typeStr = "V";
		            break;
		        case AVMEDIA_TYPE_AUDIO:
		        	typeStr = "A";
		            break;
		        case AVMEDIA_TYPE_SUBTITLE:
		        	typeStr = "S";
		            break;
		        default:
		        	typeStr = "?";
		            break;
		    }
			
			System.out.println(String.format(" %s%s%s%s%s%s %-15s %s",
							   				 (c.is_decode() ? "D" : " "),
							   				 (c.is_encode() ? "E" : " "),
							   				 typeStr,
							   				 ((c.get_capabilities() & AVCodec.CODEC_CAP_DRAW_HORIZ_BAND) == AVCodec.CODEC_CAP_DRAW_HORIZ_BAND ? "S":" "),
							   				 ((c.get_capabilities() & AVCodec.CODEC_CAP_DR1) == AVCodec.CODEC_CAP_DR1 ? "D":" "),
							   				 ((c.get_capabilities() & AVCodec.CODEC_CAP_TRUNCATED) == AVCodec.CODEC_CAP_TRUNCATED ? "T":" "),
							   				 c.get_name(),
							   				 c.get_long_name()));
		}
		System.out.println();
		
		System.out.println("Note, the names of encoders and decoders do not always match, so there are\n" +
						   "several cases where the above table shows encoder only or decoder only entries\n" +
						   "even though both encoding and decoding are supported. For example, the h263\n" +
						   "decoder corresponds to the h263 and h263p encoders, for file formats it is even\n" +
						   "worse.");
		return 0;
	}
	
	public int opt_bsfs(String opt, String arg) {
		return -1;
	}
	
	public int opt_protocols(String opt, String arg) {
		return -1;
	}
	
	public int opt_filters(String opt, String arg) {
		return -1;
	}
	
	public int opt_pix_fmts(String opt, String arg) {
		System.out.println("Pixel formats:");
		System.out.println("I.... = Supported Input  format for conversion");
		System.out.println(".O... = Supported Output format for conversion");
		System.out.println("..H.. = Hardware accelerated format");
		System.out.println("...P. = Paletted format");
		System.out.println("....B = Bitstream format");
		System.out.println("FLAGS NAME            NB_COMPONENTS BITS_PER_PIXEL");
		System.out.println("-----");
		
		
		for (AVPixFmtDescriptor pix_desc : PixDesc.av_pix_fmt_descriptors.values()) {
			System.out.println(String.format("%c%c%c%c%c %-16s       %d            %2d",
							   '.',  // sws_isSupportedInput
							   '.',  // sws_isSupportedOutput
							   ( ((pix_desc.get_flags() & AVPixFmtDescriptor.PIX_FMT_HWACCEL) != 0) ? 'H' : '.'),
							   ( ((pix_desc.get_flags() & AVPixFmtDescriptor.PIX_FMT_PAL) != 0) ? 'P' : '.'),
							   ( ((pix_desc.get_flags() & AVPixFmtDescriptor.PIX_FMT_BITSTREAM) != 0) ? 'B' : '.'),
							   pix_desc.get_name(),
							   pix_desc.get_nb_components(),
							   pix_desc.av_get_bits_per_pixel()));			
		}
		  
		return 0;
	}
	
	public int opt_loglevel(String opt, String arg) {
		return -1;
	}
	
	

	public int opt_license(String opt, String arg) {
		StringBuilder sb = new StringBuilder();
		String prg = Ffmpeg.program_name;
		
		sb.append(prg + " is free software; you can redistribute it and/or modify\n");
		sb.append("it under the terms of the GNU General Public License as published by\n");
		sb.append("the Free Software Foundation; either version 3 of the License, or\n");
		sb.append("(at your option) any later version.\n");
		sb.append("\n");
		sb.append(prg + " is distributed in the hope that it will be useful,\n");
		sb.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
		sb.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
		sb.append("GNU General Public License for more details.\n");
		sb.append("\n");
		sb.append("You should have received a copy of the GNU General Public License\n");
		sb.append("along with " + prg + ".  If not, see <http://www.gnu.org/licenses/>.\n");
	  
	    return 0;
	}
	
	
	public int opt_version(String opt, String arg) {
	    System.out.println(Ffmpeg.program_name + " " + Ffmpeg.JFFMPEG_VERSION);
	    CmdUtils.print_all_libs_info(CmdUtils.SHOW_VERSION);
	    return 0;
	}
	
	
	public int opt_format(String opt, String arg) {
	    return -1;
	}
	
	public int opt_input_file(String opt, String arg) {
	    return Ffmpeg.opt_input_file(opt, arg);
	}
	
	public int file_overwrite(String opt, String arg) {
	    return -1;
	}
	
	public int opt_map(String opt, String arg) {
	    return -1;
	}
	

	public int opt_map_metadata(String opt, String arg) {
	    return -1;
	}
	
	public int opt_map_chapters(String opt, String arg) {
	    return -1;
	}
	
	public int opt_recording_time(String opt, String arg) {
	    return -1;
	}	

	public int limit_filesize(String opt, String arg) {
	    return -1;
	}
	
    public int opt_start_time(String opt, String arg) {
	    return -1;
	}
	
    public int opt_input_ts_offset(String opt, String arg) {
	    return -1;
	}
	
    public int opt_input_ts_scale(String opt, String arg) {
	    return -1;
	}
	
    public int opt_recording_timestamp(String opt, String arg) {
	    return -1;
	}
	
    public int opt_metadata(String opt, String arg) {
	    return -1;
	}
	
    public int max_frames(String opt, String arg) {
	    return -1;
	}
	
    public int do_benchmark(String opt, String arg) {
	    return -1;
	}
	
    public int opt_timelimit(String opt, String arg) {
	    return -1;
	}
	
    public int do_pkt_dump(String opt, String arg) {
	    return -1;
	}
	
    public int do_hex_dump(String opt, String arg) {
	    return -1;
	}
	
    public int rate_emu(String opt, String arg) {
	    return -1;
	}
	
    public int loop_input(String opt, String arg) {
	    return -1;
	}
	
    public int loop_output(String opt, String arg) {
	    return -1;
	}
	
    public int opt_verbose(String opt, String arg) {
	    return -1;
	}
	
    public int opt_target(String opt, String arg) {
	    return -1;
	}
	
    public int opt_thread_count(String opt, String arg) {
	    return -1;
	}
	
    public int video_sync_method(String opt, String arg) {
	    return -1;
	}
	
    public int audio_sync_method(String opt, String arg) {
	    return -1;
	}
	
    public int audio_drift_threshold(String opt, String arg) {
	    return -1;
	}
	
    public int copy_ts(String opt, String arg) {
	    return -1;
	}
	
    public int copy_tb(String opt, String arg) {
	    return -1;
	}
	
    public int opt_shortest(String opt, String arg) {
	    return -1;
	}
	
    public int dts_delta_threshold(String opt, String arg) {
	    return -1;
	}
	
    public int opt_programid(String opt, String arg) {
	    return -1;
	}
	
    public int exit_on_error(String opt, String arg) {
	    return -1;
	}
	
    public int copy_initial_nonkeyframes(String opt, String arg) {
	    return -1;
	}
    
    public int opt_bitrate(String opt, String arg) {
	    return -1;
	}
    
    public int opt_frame_rate(String opt, String arg) {
	    return -1;
	}
    
    public int opt_frame_size(String opt, String arg) {
	    return -1;
	}
    
    public int opt_frame_aspect_ratio(String opt, String arg) {
	    return -1;
	}
    
    public int opt_frame_pix_fmt(String opt, String arg) {
	    return -1;
	}
    
    public int frame_bits_per_raw_sample(String opt, String arg) {
	    return -1;
	}
    
    public int opt_frame_crop(String opt, String arg) {
	    return -1;
	}
    
    public int opt_pad(String opt, String arg) {
	    return -1;
	}
    
    public int intra_only(String opt, String arg) {
	    return -1;
	}
    
    public int video_disable(String opt, String arg) {
	    return -1;
	}
    
    public int video_discard(String opt, String arg) {
	    return -1;
	}
    
    public int opt_qscale(String opt, String arg) {
	    return -1;
	}
    
    public int opt_video_rc_override_string(String opt, String arg) {
	    return -1;
	}
    
    public int opt_codec(String opt, String arg) {
	    return -1;
	}
    
    public int opt_me_threshold(String opt, String arg) {
	    return -1;
	}
    
    public int same_quality(String opt, String arg) {
	    return -1;
	}
    
    public int opt_pass(String opt, String arg) {
	    return -1;
	}
    
    public int opt_passlogfile(String opt, String arg) {
	    return -1;
	}
    
    public int do_deinterlace(String opt, String arg) {
	    return -1;
	}
    
    public int do_psnr(String opt, String arg) {
	    return -1;
	}
    
    public int opt_vstats(String opt, String arg) {
	    return -1;
	}
    
    public int opt_vstats_file(String opt, String arg) {
	    return -1;
	}
    
    public int vfilters(String opt, String arg) {
	    return -1;
	}

    public int opt_intra_matrix(String opt, String arg) {
	    return -1;
	}
    
    public int opt_inter_matrix(String opt, String arg) {
	    return -1;
	}
    
    public int opt_top_field_first(String opt, String arg) {
	    return -1;
	}
    
    public int intra_dc_precision(String opt, String arg) {
	    return -1;
	}
    
    public int opt_codec_tag(String opt, String arg) {
	    return -1;
	}
    
    public int opt_new_stream(String opt, String arg) {
	    return -1;
	}
    
    public int video_language(String opt, String arg) {
	    return -1;
	}
    
    public int qp_hist(String opt, String arg) {
	    return -1;
	}
    
    public int force_fps(String opt, String arg) {
	    return -1;
	}
    
    public int opt_streamid(String opt, String arg) {
	    return -1;
	}
    
    public int forced_key_frames(String opt, String arg) {
	    return -1;
	}
    
    public int audio_qscale(String opt, String arg) {
	    return -1;
	}
    
    public int opt_audio_rate(String opt, String arg) {
	    return -1;
	}
    
    public int opt_audio_channels(String opt, String arg) {
	    return -1;
	}
    
    public int audio_disable(String opt, String arg) {
	    return -1;
	}
    
    public int audio_volume(String opt, String arg) {
	    return -1;
	}
    
    public int audio_language(String opt, String arg) {
	    return -1;
	}
    
    public int opt_audio_sample_fmt(String opt, String arg) {
	    return -1;
	}
    
    public int subtitle_disable(String opt, String arg) {
	    return -1;
	}
    
    public int subtitle_language(String opt, String arg) {
	    return -1;
	}
    
    public int opt_video_channel(String opt, String arg) {
	    return -1;
	}
    
    public int opt_video_standard(String opt, String arg) {
	    return -1;
	}
    
    public int input_sync(String opt, String arg) {
	    return -1;
	}
    
    public int mux_max_delay(String opt, String arg) {
	    return -1;
	}
    
    public int mux_preload(String opt, String arg) {
	    return -1;
	}
    
    public int opt_preset(String opt, String arg) {
	    return -1;
	}
    
    public int opt_default(String opt, String arg) {
	    return -1;
	}
    


    
    
	

	public int opt_help(String opt, String arg) {
	    Ffmpeg.show_usage();
	    CmdUtils.show_help_options(Ffmpeg.options, "Main options:\n",
	                      		   OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_SUBTITLE | OPT_GRAB, 0);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nAdvanced options:\n",
	                      OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_SUBTITLE | OPT_GRAB,
	                      OPT_EXPERT);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nVideo options:\n",
	                      OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_GRAB,
	                      OPT_VIDEO);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nAdvanced Video options:\n",
	                      OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_GRAB,
	                      OPT_VIDEO | OPT_EXPERT);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nAudio options:\n",
	                      OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_GRAB,
	                      OPT_AUDIO);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nAdvanced Audio options:\n",
	                      OPT_EXPERT | OPT_AUDIO | OPT_VIDEO | OPT_GRAB,
	                      OPT_AUDIO | OPT_EXPERT);
	    CmdUtils. show_help_options(Ffmpeg.options, "\nSubtitle options:\n",
	                      OPT_SUBTITLE | OPT_GRAB,
	                      OPT_SUBTITLE);
	    CmdUtils.show_help_options(Ffmpeg.options, "\nAudio/Video grab options:\n",
	                      OPT_GRAB,
	                      OPT_GRAB);
	    System.out.println();
	    AVOption.av_opt_show2(CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO).get_av_class(), 
	    					  AVOption.AV_OPT_FLAG_ENCODING_PARAM|AVOption.AV_OPT_FLAG_DECODING_PARAM, 0);
	    System.out.println();

	    /* individual codec options */
	    for (AVCodec c: AVCodec.codecs.values()) {
	    	if (c.get_priv_class() != null) {
	    		AVOption.av_opt_show2(c.get_priv_class(), 
	    							  AVOption.AV_OPT_FLAG_ENCODING_PARAM|AVOption.AV_OPT_FLAG_DECODING_PARAM, 0);
	    	    System.out.println();
	    	}
	    }

	    AVOption.av_opt_show2(CmdUtils.avformat_opts.get_av_class(), 
	    					  AVOption.AV_OPT_FLAG_ENCODING_PARAM|AVOption.AV_OPT_FLAG_DECODING_PARAM, 0);
	    System.out.println();
	    
	    /* individual muxer options */
	    for (AVOutputFormat oformat: AVFormat.outputFormats.values()) {
	    	if (oformat.get_priv_class() != null) {
	    		AVOption.av_opt_show2(oformat.get_priv_class(),
	    				              AVOption.AV_OPT_FLAG_ENCODING_PARAM, 0);
	    	    System.out.println();
	    	}
	    }

	    //av_opt_show2(sws_opts, NULL, AV_OPT_FLAG_ENCODING_PARAM|AV_OPT_FLAG_DECODING_PARAM, 0);
	    return 0;
	}

	public boolean has_flag(int flag) {
		return (flags & flag) == flag;
	}

	public static OptionDef find_options(Map<String, OptionDef> po, String name) {
		return po.get(name);
	}

	public int func_arg(String opt, String arg) {

		try {
			Method m = this.getClass().getMethod(this.get_u().get_func_arg(), 
												 String.class, String.class);			
			return (Integer) m.invoke(this, opt, arg);
			
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
