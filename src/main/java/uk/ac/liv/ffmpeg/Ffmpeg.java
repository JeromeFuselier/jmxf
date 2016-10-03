package uk.ac.liv.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import uk.ac.liv.ffmpeg.libavcodec.AVBitStreamFilterContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVPicture;
import uk.ac.liv.ffmpeg.libavcodec.AVSubtitle;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.ResampleContext;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavfilter.AVFilter;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterBufferRef;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterContext;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterGraph;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterInOut;
import uk.ac.liv.ffmpeg.libavfilter.GraphParser;
import uk.ac.liv.ffmpeg.libavfilter.VSinkBuffer;
import uk.ac.liv.ffmpeg.libavformat.AVChapter;
import uk.ac.liv.ffmpeg.libavformat.AVDictionary;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormatParameters;
import uk.ac.liv.ffmpeg.libavformat.AVIO;
import uk.ac.liv.ffmpeg.libavformat.AVIOBuf;
import uk.ac.liv.ffmpeg.libavformat.AVInputFormat;
import uk.ac.liv.ffmpeg.libavformat.AVOutputFormat;
import uk.ac.liv.ffmpeg.libavformat.AVProgram;
import uk.ac.liv.ffmpeg.libavformat.UtilsFormat;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVFifoBuffer;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVString;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AudioConvert;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Eval;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutBBBB;
import uk.ac.liv.util.OutDS;
import uk.ac.liv.util.OutIS;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOL;
import uk.ac.liv.util.UtilsArrays;

public class Ffmpeg {
	
	public static int img_num = 0;
	
	public static final String program_name    = "jffmpeg";
	public static final String JFFMPEG_VERSION = "0.1";

	public static final int MAX_FILES   = 100;
	public static final int MAX_STREAMS = 1024;    /* arbitrary sanity check value */	
	public static final int QSCALE_NONE = -99999;
	
	public static int bit_buffer_size = 1024*256;
	public static short [] bit_buffer = null;
	
	public static final String DEFAULT_PASS_LOGFILENAME_PREFIX = "ffmpeg2pass";
	
	public static String last_asked_format                = "";
//	public static final long [] input_files_ts_offset   = new long[MAX_FILES];
//	public static final double [] input_files_ts_scale  = new double[MAX_FILES];
	public static ArrayList<AVCodec> input_codecs 	       = new ArrayList<AVCodec>();
//	static int nb_input_files_ts_scale[MAX_FILES]         = {0};

	public static ArrayList<AVFormatContext> output_files = new ArrayList<AVFormatContext>();
	
	public static ArrayList<AVStreamMap> stream_maps = new ArrayList<AVStreamMap>();
	
	/* first item specifies output metadata, second is input */
	public static AVMetaDataMap[][] meta_data_maps = new AVMetaDataMap[0][0];
    public static int metadata_global_autocopy            = 1;
	public static int metadata_streams_autocopy           = 1;
	public static int metadata_chapters_autocopy          = 1;

	public static ArrayList<AVChapterMap> chapter_maps = new ArrayList<AVChapterMap>();
	
	/* indexed by output file stream index */
	public static ArrayList<Integer> streamid_map = new ArrayList<Integer>();
	
	public static int frame_width           = 0;
	public static int frame_height          = 0;
	public static float frame_aspect_ratio = 0;
	public static PixelFormat frame_pix_fmt = PixelFormat.PIX_FMT_NONE;
	public static int frame_bits_per_raw_sample = 0;
	public static AVSampleFormat audio_sample_fmt = AVSampleFormat.AV_SAMPLE_FMT_NONE;
	public static int [] max_frames = {Integer.MAX_VALUE, Integer.MAX_VALUE, 
										 Integer.MAX_VALUE, Integer.MAX_VALUE};
	public static AVRational frame_rate          = new AVRational();
	public static float video_qscale             = 0;
	public static int [] intra_matrix            = null;
	public static int [] inter_matrix            = null;
	public static String video_rc_override_string = "";
	public static int video_disable               = 0;
	public static AVDiscard video_discard         = AVDiscard.AVDISCARD_NONE;
	public static String video_codec_name    = "";
	public static int video_codec_tag = 0;
	public static String video_language = "";	
	public static int same_quality = 0;
	public static int do_deinterlace = 0;
	public static int top_field_first = -1;
	public static int me_threshold = 0;
	public static int intra_dc_precision = 8;
	public static int loop_input = 0;
	public static int loop_output = AVFormat.AVFMT_NOOUTPUTLOOP;
	public static int qp_hist = 0;
	public static  String vfilters = "";
	
	public static int intra_only = 0;
	public static int audio_sample_rate = 0;
	public static long channel_layout = 0;
	public static float audio_qscale = QSCALE_NONE;
	public static int audio_disable = 0;
	public static int audio_channels = 0;
	public static String audio_codec_name = "";
	public static int audio_codec_tag = 0;
	public static String audio_language = "";	

	public static int subtitle_disable = 0;
	public static String subtitle_codec_name = "";
	public static String subtitle_language = "";
	public static int subtitle_codec_tag = 0;
	
	public static int data_disable = 0;
	public static String data_codec_name = "";
	public static int data_codec_tag = 0;

	public static float mux_preload = 0.5f;
	public static float mux_max_delay = 0.7f;

	public static long recording_time = Long.MAX_VALUE;
	public static long start_time = 0;
	public static long recording_timestamp = 0;
	public static long input_ts_offset = 0;
	public static int file_overwrite = 0;
	public static AVDictionary metadata = new AVDictionary();
	public static int do_benchmark = 1;//0;
	public static int do_hex_dump = 0;
	public static int do_pkt_dump = 0;
	public static int do_psnr = 0;
	public static int do_pass = 0;
	public static String pass_logfilename_prefix;
	public static int audio_stream_copy = 0;
	public static int video_stream_copy = 0;
	public static int subtitle_stream_copy = 0;
	public static int data_stream_copy = 0;
	public static int video_sync_method= -1;
	public static int audio_sync_method= 0;
	public static float audio_drift_threshold = 0.1f;
	public static int copy_ts= 0;
	public static int copy_tb= 0;
	public static int opt_shortest = 0;
	public static String vstats_filename;
	public static File vstats_file;
	public static int opt_programid = 0;
	public static int copy_initial_nonkeyframes = 0;

	public static int rate_emu = 0;

	public static int video_channel = 0;
	public static String video_standard = "";

	public static int audio_volume = 256;

	public static int exit_on_error = 0;
	public static int using_stdin = 0;
	public static int verbose = 1;
	public static int run_as_daemon  = 0;
	public static int thread_count= 1;
	public static int q_pressed = 0;
	public static long video_size = 0;
	public static long audio_size = 0;
	public static long extra_size = 0;
	public static int nb_frames_dup = 0;
	public static int nb_frames_drop = 0;
	public static int input_sync;
	public static long limit_filesize = 0;
	public static int force_fps = 0;
	public static String forced_key_frames = null;

	public static float dts_delta_threshold = 10;

	public static long timer_start;

	public static short [] audio_buf;
	public static short[] audio_out;
	public static int allocated_audio_out_size;
	public static int allocated_audio_buf_size;

	public static short [] samples;

	public static AVBitStreamFilterContext video_bitstream_filters    = null;
	public static AVBitStreamFilterContext audio_bitstream_filters    = null;
	public static AVBitStreamFilterContext subtitle_bitstream_filters = null;	

	public static ArrayList<ArrayList<AVOutputStream>> output_streams_for_file = new ArrayList<ArrayList<AVOutputStream>>();

	public static ArrayList<AVInputStream> input_streams = new ArrayList<AVInputStream>();
	public static ArrayList<AVInputFile> input_files 	 = new ArrayList<AVInputFile>();
	
	public static Map<String,OptionDef> options;
	

	public static int received_sigterm = 0;
	
	
	
	static {
		options = new HashMap<String, OptionDef>();
		
		// Common options
		options_put(new OptionDef("L", OptionDef.OPT_EXIT, "opt_license", "show_license"));
		options_put(new OptionDef("h", OptionDef.OPT_EXIT, "opt_help", "show help"));
		options_put(new OptionDef("?", OptionDef.OPT_EXIT, "opt_help", "show help"));
		options_put(new OptionDef("help", OptionDef.OPT_EXIT, "opt_help", "show help"));
		options_put(new OptionDef("-help", OptionDef.OPT_EXIT, "opt_help", "show help"));
		options_put(new OptionDef("version", OptionDef.OPT_EXIT, "opt_version", "show version"));
		options_put(new OptionDef("formats", OptionDef.OPT_EXIT, "opt_formats", "show available formats"));
		options_put(new OptionDef("codecs", OptionDef.OPT_EXIT, "opt_codecs", "show available codecs"));
//		options_put(new OptionDef("bsfs", OptionDef.OPT_EXIT, "opt_bsfs", "show available bit stream filters"));
//		options_put(new OptionDef("protocols", OptionDef.OPT_EXIT, "opt_protocols", "show available protocols"));
//		options_put(new OptionDef("filters", OptionDef.OPT_EXIT, "opt_filters", "show available filters"));
		options_put(new OptionDef("pix_fmts", OptionDef.OPT_EXIT, "opt_pix_fmts", "show available pixel formats"));
//		options_put(new OptionDef("loglevel", OptionDef.HAS_ARG, "opt_loglevel", "set libav* logging level", "loglevel"));
		

    	// Main options 
//		options_put(new OptionDef("f", OptionDef.HAS_ARG, "opt_format", "force format", "fmt"));
		options_put(new OptionDef("i", OptionDef.HAS_ARG, "opt_input_file", "input file name", "filename"));
//		options_put(new OptionDef("y", OptionDef.HAS_BOOL, "file_overwrite", "overwrite output files"));
//		options_put(new OptionDef("map", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_map", "set input stream mapping", "file.stream[:syncfile.syncstream]"));
//	    options_put(new OptionDef("map_metadata", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_map_metadata", "set metadata information of outfile from infile", "outfile[,metadata]:infile[,metadata]"));
//	    options_put(new OptionDef("map_chapters",  OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_map_chapters",  "set chapters mapping", "outfile:infile"));
//	    options_put(new OptionDef("t", OptionDef.HAS_ARG, "opt_recording_time", "record or transcode \"duration\" seconds of audio/video", "duration"));
//	    options_put(new OptionDef("fs", OptionDef.HAS_ARG | OptionDef.OPT_INT64, "limit_filesize", "set the limit file size in bytes", "limit_size")); 
//	    options_put(new OptionDef("ss", OptionDef.HAS_ARG, "opt_start_time", "set the start time offset", "time_off"));
//	    options_put(new OptionDef("itsoffset", OptionDef.HAS_ARG, "opt_input_ts_offset", "set the input ts offset", "time_off"));
//	    options_put(new OptionDef("itsscale", OptionDef.HAS_ARG, "opt_input_ts_scale", "set the input ts scale", "stream:scale"));
//	    options_put(new OptionDef("timestamp", OptionDef.HAS_ARG, "opt_recording_timestamp", "set the recording timestamp ('now' to set the current time)", "time"));
//	    options_put(new OptionDef("metadata", OptionDef.HAS_ARG, "opt_metadata", "add metadata", "string=string"));
//	    options_put(new OptionDef("dframes", OptionDef.OPT_INT | OptionDef.HAS_ARG, "max_frames", "set the number of data frames to record", "number"));
	    options_put(new OptionDef("benchmark", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "do_benchmark", "add timings for benchmarking"));
//	    options_put(new OptionDef("timelimit", OptionDef.HAS_ARG, "opt_timelimit", "set max runtime in seconds", "limit"));
//	    options_put(new OptionDef("dump", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "do_pkt_dump", "dump each input packet"));
//	    options_put(new OptionDef("hex", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "do_hex_dump", "when dumping packets, also dump the payload"));
//	    options_put(new OptionDef("re", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "rate_emu", "read input at native frame rate", ""));
//	    options_put(new OptionDef("loop_input", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "loop_input", "deprecated, use -loop"));
//	    options_put(new OptionDef("loop_output", OptionDef.HAS_ARG | OptionDef.OPT_INT | OptionDef.OPT_EXPERT, "loop_output", "deprecated, use -loop", ""));
//	    options_put(new OptionDef("v", OptionDef.HAS_ARG, "opt_verbose", "set ffmpeg verbosity level", "number"));
//	    options_put(new OptionDef("target", OptionDef.HAS_ARG, "opt_target", "specify target file type (\"vcd\", \"svcd\", \"dvd\", \"dv\", \"dv50\", \"pal-vcd\", \"ntsc-svcd\", ...)", "type"));
//	    options_put(new OptionDef("threads",  OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_thread_count", "thread count", "count"));
//	    options_put(new OptionDef("vsync", OptionDef.HAS_ARG | OptionDef.OPT_INT | OptionDef.OPT_EXPERT, "video_sync_method", "video sync method", ""));
//	    options_put(new OptionDef("async", OptionDef.HAS_ARG | OptionDef.OPT_INT | OptionDef.OPT_EXPERT, "audio_sync_method", "audio sync method", ""));
//	    options_put(new OptionDef("adrift_threshold", OptionDef.HAS_ARG | OptionDef.OPT_FLOAT | OptionDef.OPT_EXPERT, "audio_drift_threshold", "audio drift threshold", "threshold"));
//	    options_put(new OptionDef("copyts", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "copy_ts", "copy timestamps"));
//	    options_put(new OptionDef("copytb", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "copy_tb", "copy input stream time base when stream copying"));
//	    options_put(new OptionDef("shortest", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "opt_shortest", "finish encoding within shortest input")); 
//	    options_put(new OptionDef("dts_delta_threshold", OptionDef.HAS_ARG | OptionDef.OPT_FLOAT | OptionDef.OPT_EXPERT, "dts_delta_threshold", "timestamp discontinuity delta threshold", "threshold"));
//	    options_put(new OptionDef("programid", OptionDef.HAS_ARG | OptionDef.OPT_INT | OptionDef.OPT_EXPERT, "opt_programid", "desired program number", ""));
//	    options_put(new OptionDef("xerror", OptionDef.OPT_BOOL, "exit_on_error", "exit on error", "error"));
//	    options_put(new OptionDef("copyinkf", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT, "copy_initial_nonkeyframes", "copy initial non-keyframes"));

	    // Video options 
//	    options_put(new OptionDef("b", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_bitrate", "set bitrate (in bits/s)", "bitrate"));
//	    options_put(new OptionDef("vb", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_bitrate", "set bitrate (in bits/s)", "bitrate"));
//	    options_put(new OptionDef("vframes", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "max_frames[AVMEDIA_TYPE_VIDEO]", "set the number of video frames to record", "number"));
//	    options_put(new OptionDef("r", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_rate", "set frame rate (Hz value, fraction or abbreviation)", "rate"));
//	    options_put(new OptionDef("s", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_size", "set frame size (WxH or abbreviation)", "size"));
//	    options_put(new OptionDef("aspect", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_aspect_ratio", "set aspect ratio (4:3, 16:9 or 1.3333, 1.7777)", "aspect"));
//	    options_put(new OptionDef("pix_fmt", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_frame_pix_fmt", "set pixel format, 'list' as argument shows all the pixel formats supported", "format"));
//	    options_put(new OptionDef("bits_per_raw_sample", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "frame_bits_per_raw_sample", "set the number of bits per raw sample", "number"));
//	    options_put(new OptionDef("croptop",  OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_crop", "Removed, use the crop filter instead", "size"));
//	    options_put(new OptionDef("cropbottom", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_crop", "Removed, use the crop filter instead", "size"));
//	    options_put(new OptionDef("cropleft", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_crop", "Removed, use the crop filter instead", "size"));
//	    options_put(new OptionDef("cropright", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_frame_crop", "Removed, use the crop filter instead", "size"));
//	    options_put(new OptionDef("padtop", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pad", "Removed, use the pad filter instead", "size"));
//	    options_put(new OptionDef("padbottom", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pad", "Removed, use the pad filter instead", "size"));
//	    options_put(new OptionDef("padleft", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pad", "Removed, use the pad filter instead", "size"));
//	    options_put(new OptionDef("padright", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pad", "Removed, use the pad filter instead", "size"));
//	    options_put(new OptionDef("padcolor", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pad", "Removed, use the pad filter instead", "color"));
//	    options_put(new OptionDef("intra", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "intra_only", "use only intra frames"));
//	    options_put(new OptionDef("vn", OptionDef.OPT_BOOL | OptionDef.OPT_VIDEO, "video_disable", "disable video"));
//	    options_put(new OptionDef("vdt", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "video_discard", "discard threshold", "n"));
//	    options_put(new OptionDef("qscale", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_qscale", "use fixed video quantizer scale (VBR)", "q"));
//	    options_put(new OptionDef("rc_override", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_video_rc_override_string", "rate control override for specific intervals", "override"));
//	    options_put(new OptionDef("vcodec", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_codec", "force video codec ('copy' to copy stream)", "codec"));
//	    options_put(new OptionDef("me_threshold", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_me_threshold", "motion estimaton threshold",  "threshold"));
//	    options_put(new OptionDef("sameq", OptionDef.OPT_BOOL | OptionDef.OPT_VIDEO, "same_quality", "use same quantizer as source (implies VBR)"));
//	    options_put(new OptionDef("pass", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_pass", "select the pass number (1 or 2)", "n"));
//	    options_put(new OptionDef("passlogfile", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO, "opt_passlogfile", "select two pass log file name prefix", "prefix"));
//	    options_put(new OptionDef("deinterlace", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "do_deinterlace", "deinterlace pictures"));
//	    options_put(new OptionDef("psnr", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "do_psnr", "calculate PSNR of compressed frames"));
//	    options_put(new OptionDef("vstats", OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_vstats", "dump video coding statistics to file"));
//	    options_put(new OptionDef("vstats_file", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_vstats_file", "dump video coding statistics to file", "file"));
//	#if CONFIG_AVFILTER
//	    options_put(new OptionDef("vf", OptionDef.OPT_STRING | OptionDef.HAS_ARG, "vfilters", "video filters", "filter list"));
//	#endif
//	    options_put(new OptionDef("intra_matrix", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_intra_matrix", "specify intra matrix coeffs", "matrix"));
//	    options_put(new OptionDef("inter_matrix", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_inter_matrix", "specify inter matrix coeffs", "matrix"));
//	    options_put(new OptionDef("top", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_top_field_first", "top=1/bottom=0/auto=-1 field first", ""));
//	    options_put(new OptionDef("dc", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "intra_dc_precision", "intra_dc_precision", "precision"));
//	    options_put(new OptionDef("vtag", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "opt_codec_tag", "force video tag/fourcc", "fourcc/tag"));
//	    options_put(new OptionDef("newvideo", OptionDef.OPT_VIDEO, "opt_new_stream", "add a new video stream to the current output stream"));
//	    options_put(new OptionDef("vlang", OptionDef.HAS_ARG | OptionDef.OPT_STRING | OptionDef.OPT_VIDEO, "video_language", "set the ISO 639 language code (3 letters) of the current video stream" , "code"));
//	    options_put(new OptionDef("qphist", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "qp_hist", "show QP histogram"));
//	    options_put(new OptionDef("force_fps", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "force_fps", "force the selected framerate, disable the best supported framerate selection"));
//	    options_put(new OptionDef("streamid", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_streamid", "set the value of an outfile streamid", "streamIndex:value"));
//	    options_put(new OptionDef("force_key_frames", OptionDef.OPT_STRING | OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO, "forced_key_frames", "force key frames at specified timestamps", "timestamps"));

	    // audio options 
//	    options_put(new OptionDef("ab", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "opt_bitrate", "set bitrate (in bits/s)", "bitrate"));
//	    options_put(new OptionDef("aframes", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "max_frames[AVMEDIA_TYPE_AUDIO]", "set the number of audio frames to record", "number"));
//	    options_put(new OptionDef("aq", OptionDef.OPT_FLOAT | OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "audio_qscale", "set audio quality (codec-specific)", "quality"));
//	    options_put(new OptionDef("ar", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "opt_audio_rate", "set audio sampling rate (in Hz)", "rate"));
//	    options_put(new OptionDef("ac", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "opt_audio_channels", "set number of audio channels", "channels"));
//	    options_put(new OptionDef("an", OptionDef.OPT_BOOL | OptionDef.OPT_AUDIO, "&audio_disable", "disable audio"));
//	    options_put(new OptionDef("acodec", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "opt_codec", "force audio codec ('copy' to copy stream)", "codec"));
//	    options_put(new OptionDef("atag", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_AUDIO, "opt_codec_tag", "force audio tag/fourcc", "fourcc/tag"));
//	    options_put(new OptionDef("vol", OptionDef.OPT_INT | OptionDef.HAS_ARG | OptionDef.OPT_AUDIO, "audio_volume", "change audio volume (256=normal)" , "volume")); 
//	    options_put(new OptionDef("newaudio", OptionDef.OPT_AUDIO, "opt_new_stream", "add a new audio stream to the current output stream"));
//	    options_put(new OptionDef("alang", OptionDef.HAS_ARG | OptionDef.OPT_STRING | OptionDef.OPT_AUDIO, "audio_language", "set the ISO 639 language code (3 letters) of the current audio stream" , "code"));
//	    options_put(new OptionDef("sample_fmt", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_AUDIO, "opt_audio_sample_fmt", "set sample format, 'list' as argument shows all the sample formats supported", "format"));

	    // subtitle options 
//	    options_put(new OptionDef("sn", OptionDef.OPT_BOOL | OptionDef.OPT_SUBTITLE, "subtitle_disable", "disable subtitle"));
//	    options_put(new OptionDef("scodec", OptionDef.HAS_ARG | OptionDef.OPT_SUBTITLE, "opt_codec", "force subtitle codec ('copy' to copy stream)", "codec"));
//	    options_put(new OptionDef("newsubtitle", OptionDef.OPT_SUBTITLE, "opt_new_stream", "add a new subtitle stream to the current output stream"));
//	    options_put(new OptionDef("slang", OptionDef.HAS_ARG | OptionDef.OPT_STRING | OptionDef.OPT_SUBTITLE, "subtitle_language", "set the ISO 639 language code (3 letters) of the current subtitle stream" , "code"));
//	    options_put(new OptionDef("stag", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_SUBTITLE, "opt_codec_tag", "force subtitle tag/fourcc", "fourcc/tag"));

	    // grab options 
//	    options_put(new OptionDef("vc", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO | OptionDef.OPT_GRAB, "ot_video_channel", "set video grab channel (DV1394 only)", "channel"));
//	    options_put(new OptionDef("tvstd", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT | OptionDef.OPT_VIDEO | OptionDef.OPT_GRAB, "opt_video_standard", "set television standard (NTSC, PAL (SECAM))", "standard"));
//	    options_put(new OptionDef("isync", OptionDef.OPT_BOOL | OptionDef.OPT_EXPERT | OptionDef.OPT_GRAB, "input_sync", "sync read on input", ""));

	    // muxer options 
//	    options_put(new OptionDef("muxdelay", OptionDef.OPT_FLOAT | OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "mux_max_delay", "set the maximum demux-decode delay", "seconds"));
//	    options_put(new OptionDef("muxpreload", OptionDef.OPT_FLOAT | OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "mux_preload", "set the initial demux-decode delay", "seconds"));
//	    options_put(new OptionDef("absf", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO | OptionDef.OPT_EXPERT, "opt_bsf", "", "bitstream_filter"));
//	    options_put(new OptionDef("vbsf", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO | OptionDef.OPT_EXPERT, "opt_bsf", "", "bitstream_filter"));
//	    options_put(new OptionDef("sbsf", OptionDef.HAS_ARG | OptionDef.OPT_SUBTITLE | OptionDef.OPT_EXPERT, "opt_bsf", "", "bitstream_filter"));
//	    options_put(new OptionDef("apre", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO | OptionDef.OPT_EXPERT, "opt_preset", "set the audio options to the indicated preset", "preset"));
//	    options_put(new OptionDef("vpre", OptionDef.HAS_ARG | OptionDef.OPT_VIDEO | OptionDef.OPT_EXPERT, "opt_preset", "set the video options to the indicated preset", "preset"));
//	    options_put(new OptionDef("spre", OptionDef.HAS_ARG | OptionDef.OPT_SUBTITLE | OptionDef.OPT_EXPERT, "opt_preset", "set the subtitle options to the indicated preset", "preset"));
//	    options_put(new OptionDef("fpre", OptionDef.HAS_ARG | OptionDef.OPT_EXPERT, "opt_preset", "set options from indicated preset file", "filename"));
	    
	    // data codec support 
//	    options_put(new OptionDef("dcodec", OptionDef.HAS_ARG | OptionDef.OPT_DATA, "opt_codec", "force data codec ('copy' to copy stream)", "codec"));
//	    options_put(new OptionDef("default", OptionDef.HAS_ARG | OptionDef.OPT_AUDIO | OptionDef.OPT_VIDEO | OptionDef.OPT_EXPERT, "opt_default", "generic catch all option", ""));

	};
	
	

	public static int opt_input_file(String opt, String arg) {
		AVFormatContext ic;
		AVFormatParameters ap = new AVFormatParameters();
	    AVInputFormat file_iformat = null;
	    URI uri = new File(arg).toURI();
	    int rfps, rfps_base;
	    long timestamp;
				
		ic = AVFormatContext.avformat_alloc_context();
		
		ap.set_prealloced_context(1);
		ap.set_sample_rate(audio_sample_rate);
		ap.set_channels(audio_channels);
		ap.set_time_base(frame_rate);
		ap.set_width(frame_width);
		ap.set_width(frame_height);
		ap.set_pix_fmt(frame_pix_fmt);
		ap.set_channel(video_channel);
		ap.set_standard(video_standard);
		
		set_context_opts(ic, CmdUtils.avformat_opts, AVOption.AV_OPT_FLAG_DECODING_PARAM, null);
		
		ic.set_video_codec_id(find_codec_or_die(video_codec_name, AVMediaType.AVMEDIA_TYPE_VIDEO, false,
							  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO).get_strict_std_compliance()));
		
		ic.set_audio_codec_id(find_codec_or_die(audio_codec_name, AVMediaType.AVMEDIA_TYPE_AUDIO, false,
							  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO).get_strict_std_compliance()));
		
		ic.set_audio_codec_id(find_codec_or_die(subtitle_codec_name, AVMediaType.AVMEDIA_TYPE_SUBTITLE, false,
							  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_SUBTITLE).get_strict_std_compliance()));
		
		ic.add_flag(AVFormat.AVFMT_FLAG_NONBLOCK);
		ic.add_flag(AVFormat.AVFMT_FLAG_PRIV_OPT);		
		
		
		// open the input file with generic libav function
	    int err = UtilsFormat.av_open_input_file(ic, uri, file_iformat, 0, ap);
		
	    if (err >= 0) {
	    	//set_context_opts(ic, avformat_opts, AV_OPT_FLAG_DECODING_PARAM, NULL);
	        err = UtilsFormat.av_demuxer_open(ic, ap);
	    }
	    
	    if (err < 0) {
	    	System.exit(1);
	    }
	    
	    ic.set_loop_input(0);
	    
	    int ret = ic.avformat_find_stream_info(null);
	    

	    if ( (ret < 0) && (verbose >= 0) ) {
	        System.out.println(uri + "could not find codec parameters");
	        ic.av_close_input_file();
	        ffmpeg_exit(1);
	    }
	    

	    timestamp = start_time;
	    
	    /* add the stream start time */
	    if (ic.get_start_time() != AVUtil.AV_NOPTS_VALUE) {
	        timestamp += ic.get_start_time();
	    }

	    /* if seeking requested, we execute it */
	    if (start_time != 0) {
	        ret = UtilsFormat.av_seek_frame(ic, -1, timestamp, AVFormat.AVSEEK_FLAG_BACKWARD);
	        if (ret < 0) {
	            System.out.println(uri + ": could not seek to position " + 
	            		(double)timestamp / AVUtil.AV_TIME_BASE);
	        }
	        /* reset seek info */
	        start_time = 0;
	    }
	    
	    for (int i = 0 ; i < ic.get_nb_streams() ; i++) {
	    	AVStream st = ic.get_stream(i);
	    	AVCodecContext dec = st.get_codec();
	    	AVInputStream ist = new AVInputStream();
	    	input_streams.add(ist);
	    	ist.set_st(st);
	    	ist.set_file_index(input_streams.indexOf(ist));
	    	ist.set_discard(1);
	    	
	    	switch (dec.get_codec_type()) {
	    	case AVMEDIA_TYPE_AUDIO: {
	    		AVCodec codec = AVCodec.avcodec_find_decoder_by_name(audio_codec_name);
	    		if (codec == null)
	    			codec = AVCodec.avcodec_find_decoder(dec.get_codec_id());
	    		input_codecs.add(codec);
	    		CmdUtils.set_context_opts(dec, 
	    				CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO), 
	    				AVOption.AV_OPT_FLAG_AUDIO_PARAM | AVOption.AV_OPT_FLAG_DECODING_PARAM, 
	    				codec);
	    		if (audio_disable != 0)
	    			st.set_discard(AVDiscard.AVDISCARD_ALL);
	    		break;
	    	}	
	    	case AVMEDIA_TYPE_VIDEO: {
	    		AVCodec codec = AVCodec.avcodec_find_decoder_by_name(video_codec_name);
	    		if (codec == null)
	    			codec = AVCodec.avcodec_find_decoder(dec.get_codec_id());
	    		input_codecs.add(codec);
	    		CmdUtils.set_context_opts(dec, 
	    				CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO), 
	    				AVOption.AV_OPT_FLAG_VIDEO_PARAM | AVOption.AV_OPT_FLAG_DECODING_PARAM, 
	    				codec);
	    		rfps = st.get_r_frame_rate().get_num();
	    		rfps_base = st.get_r_frame_rate().get_den();
	    		if (dec.get_lowres() != 0) {
	    			dec.set_flags(dec.get_flags() | AVCodec.CODEC_FLAG_EMU_EDGE);
	    			dec.set_height(dec.get_height() >> dec.get_lowres());
	    			dec.set_width(dec.get_width() >> dec.get_lowres());	    			
	    		}
	    		if (me_threshold != 0)
	    			dec.set_debug(dec.get_debug() | AVCodec.FF_DEBUG_MV);
	    		
	    		/*if ( (dec.get_time_base().get_den() != rfps * dec.get_ticks_per_frame()) ||
	    			 (dec.get_time_base().get_num() != rfps_base) ) {
	    			if (verbose >= 0) {
	    				// fprintf(stderr,"\nSeems stream %d codec frame rate differs from container frame rate: %2.2f (%d/%d) .get_ %2.2f (%d/%d)\n",
                            i, (float)dec.get_time_base().get_den() / dec.get_time_base().get_num(), dec.get_time_base().get_den(), dec.get_time_base().get_num(),

                    (float)rfps / rfps_base, rfps, rfps_base);
	    			}
	    		}*/
	    		
	    		if (video_disable != 0) 
	    			st.set_discard(AVDiscard.AVDISCARD_ALL);
	    		else if (video_discard != AVDiscard.AVDISCARD_NONE)
	    			st.set_discard(video_discard);
	    		break;
	    	}
	        case AVMEDIA_TYPE_DATA:
	            break;
	        case AVMEDIA_TYPE_SUBTITLE:
	    		AVCodec codec = AVCodec.avcodec_find_decoder_by_name(subtitle_codec_name);
	    		if (codec == null)
	    			codec = AVCodec.avcodec_find_decoder(dec.get_codec_id());
	    		input_codecs.add(codec);
	            if (subtitle_disable!= 0)
	    			st.set_discard(AVDiscard.AVDISCARD_ALL);
	            break;
	        case AVMEDIA_TYPE_ATTACHMENT:
	        case AVMEDIA_TYPE_UNKNOWN:
	            break;
	    		
	    	}	    	
	    	
	    }

	    /* dump the file content */
	    if (verbose >= 0)
	        UtilsFormat.av_dump_format(ic, input_files.size(), uri.getPath(), false);
	    
	    input_files.add(new AVInputFile(ic, input_streams.size() - ic.get_nb_streams()));
	    

	    top_field_first = -1;
	    video_channel = 0;
	    frame_rate    = new AVRational(0, 0);
	    frame_pix_fmt = PixelFormat.PIX_FMT_NONE;
	    frame_height = 0;
	    frame_width  = 0;
	    audio_sample_rate = 0;
	    audio_channels    = 0;
	    
	    video_codec_name = "";
	    audio_codec_name = "";
	    subtitle_codec_name = "";
	    
		CmdUtils.uninit_opts();
		CmdUtils.init_opts();

		
	    return ret;
	}


	private static void options_put(OptionDef opt) {
		options.put(opt.get_name(), opt);		
	}


	public static CodecID find_codec_or_die(String name, AVMediaType type, 
			boolean encoder, int strict) {
		String codec_string = encoder ? "encoder" : "decoder";
		AVCodec codec = new AVCodec();
		
		if (name.equals("")) {
			return CodecID.CODEC_ID_NONE;
		}
		
		codec = encoder ?
		        AVCodec.avcodec_find_encoder_by_name(name) :
			    AVCodec.avcodec_find_decoder_by_name(name);
		        
		if (codec == null) {
			System.err.print(String.format("Unknown %s '%s'\n", codec_string, name));
			ffmpeg_exit(1);
		}
		
		if (codec.get_type() != type) {
			System.err.print(String.format("Invalid %s type '%s'\n", codec_string, name));
			ffmpeg_exit(1);			
		}
		
		// TODO experimental codecs?		
		
		return codec.get_id();
	}
	

	private static void ffmpeg_exit(int ret) {
		System.exit(ret);		
	}


	public static Object alloc_priv_context(AVClass cls) {
		AVClass p = new AVClass(cls);
		
		p.av_opt_set_defaults();
		return p;
	}
	
	public static void set_context_opts(Object ctx, Object opts_ctx, int flags, AVCodec codec) {
		Object priv_ctx = null;
		
		if (ctx instanceof AVCodecContext) {
			AVCodecContext avctx = (AVCodecContext)ctx;
			if (codec != null) {
				if (codec.get_priv_class() != null) {
					if (avctx.get_priv_data() == null) {
						avctx.set_priv_data(alloc_priv_context(codec.get_priv_class()));
					}
					priv_ctx = avctx.get_priv_data();
				}
			}
			
		} else if (ctx instanceof AVFormatContext) {
			AVFormatContext avctx = (AVFormatContext)ctx;
			boolean found = true;
			if (avctx.get_oformat() != null) {
				if (avctx.get_oformat().get_priv_class() != null) {
					priv_ctx = avctx.get_priv_data();
				} else {
					found = false;
				}
			} else {
				found = false;
			}
			
			if (!found) {
				if (avctx.get_iformat() != null) {
					if (avctx.get_iformat().get_priv_class() != null) {
						priv_ctx = avctx.get_priv_data();
					}
				}
			}
		} 
		
		// TODO: Add the loop on some options (See cmdutils.c - opt_default()) 
		
	}


	public static void parse_options(String[] argv) {
		//parse_arg_function = opt_output_file
		// options		
		int optindex = 0;
		String opt, arg;
		boolean handleoptions = true;
		OptionDef po;
		
		prepare_app_arguments(argv);
		
		
		while (optindex < argv.length) {
			opt = argv[optindex++];
			
			if ( (handleoptions) && (opt.charAt(0) == '-') && (opt.length() > 1) ) {
				boolean bool_val = true;
				if (opt.equals("--")) {
					handleoptions = false;
					continue;
				}
				opt = opt.substring(1);
				po = OptionDef.find_options(options, opt);
				if ( (po.get_name() == null) && (opt.startsWith("no")) ) {
	                /* handle 'no' bool option */
					po = OptionDef.find_options(options, opt.substring(2));
					if (!( (po.get_name()!= null) && (po.has_flag(OptionDef.OPT_BOOL)) )) {
						System.err.print(String.format("%s: unrecognized option '%s'\n", 
								Ffmpeg.program_name, opt));
						System.exit(-1);
					}
					bool_val = false;
				}
				if (po.get_name() == null)
					po = OptionDef.find_options(options, "default");
				if (po.get_name() == null) {
					System.err.print(String.format("%s: unrecognized option '%s'\n", 
							Ffmpeg.program_name, opt));
					System.exit(-1);
				}
				arg = null;
				if (po.has_flag(OptionDef.HAS_ARG)) {
					arg = argv[optindex++];
					if (arg == null){
						System.err.print(String.format("%s: missing argument for option '%s'\n", 
								Ffmpeg.program_name, opt));
						System.exit(-1);
					}					
				}
				if (po.has_flag(OptionDef.OPT_STRING)) {
					po.get_u().set_str_arg(arg);
				} else if (po.has_flag(OptionDef.OPT_BOOL)) {
					po.get_u().set_bool_arg(bool_val);
				} else if (po.has_flag(OptionDef.OPT_INT)) {
					po.get_u().set_int_arg((int)parse_number_or_die(opt, arg, OptionDef.OPT_INT64, Integer.MIN_VALUE, Integer.MAX_VALUE));
				} else if (po.has_flag(OptionDef.OPT_INT64)) {
					po.get_u().set_int64_arg((long)parse_number_or_die(opt, arg, OptionDef.OPT_INT64, Long.MIN_VALUE, Long.MAX_VALUE));
				} else if (po.has_flag(OptionDef.OPT_FLOAT)) {
					po.get_u().set_float_arg((float)parse_number_or_die(opt, arg, OptionDef.OPT_FLOAT, Float.MIN_VALUE, Float.MAX_VALUE));
				} else if (po.get_u().get_func_arg() != null) {
					if (po.func_arg(opt, arg) < 0) {
						System.err.print(String.format("%s: failed to set value '%s' for option '%s'\n",
								Ffmpeg.program_name, ((arg != null) ? arg : "[null]"), opt));
						System.exit(-1);						
					}
				}
				if (po.has_flag(OptionDef.OPT_EXIT))
					System.exit(0);
				
			} else {
				if (opt_output_file(null, opt) < 0)
					System.exit(-1);
			}
			
		}
		
		
	}




	private static double parse_number_or_die(String context, String numstr,
			int type, double min, double max) {
		OutDS tmp = Eval.av_strtod(numstr);
		double d = tmp.get_double();
		String tail = tmp.get_string();
		String error;
		
		if (tail != null)
	        error= "Expected number for %s but found: %s";
		else if ( (d < min) || (d > max) )
	        error= "The value for %s was %s which is not within %f - %f";
		else if ( (type == OptionDef.OPT_INT64) && ((long)d != d) )
	        error= "Expected int64 for %s but found %s\n";
		else if ( (type == OptionDef.OPT_INT) && ((int)d != d) )
	        error= "Expected int for %s but found %s\n";
		else
			return d;
		System.err.print(String.format(error, context, numstr, min, max));
		System.exit(1);
			
		
		
		return 0;
	}


	private static int opt_output_file(Object opt, String filename) {
		AVFormatContext oc;
		int err;
	    AVOutputFormat file_oformat;
	    boolean use_video, use_audio, use_subtitle, use_data; 
	    boolean input_has_video, input_has_audio, input_has_subtitle, input_has_data;
	    AVFormatParameters ap;

		
	    if (output_files.size() >= MAX_FILES){
	        System.err.print("Too many output files\n");
	        ffmpeg_exit(1);
	    }
	    output_streams_for_file.add(new ArrayList<AVOutputStream>());
	    
	    
	    /*if (!strcmp(filename, "-"))
	        filename = "pipe:";*/
	    
	    OutOI tmp = UtilsFormat.avformat_alloc_output_context2(null, last_asked_format, filename);
	    oc = (AVFormatContext) tmp.get_obj();
	    err = tmp.get_ret(); 
	    last_asked_format = "";
	    if (oc == null) {
	    	CmdUtils.print_error(filename, err);
	    	ffmpeg_exit(1);
	    }
	    file_oformat = oc.get_oformat();
	    
	    /*
	    if (!strcmp(file_oformat.get_name, "ffm") &&
	            av_strstart(filename, "http:", NULL)) {
	            // special case for files sent to ffserver: we get the stream
	            //   parameters from ffserver 
	            int err = read_ffserver_streams(oc, filename);
	            if (err < 0) {
	                print_error(filename, err);
	                ffmpeg_exit(1);
	            }
	    } else {*/
	    
	    use_video = (file_oformat.get_video_codec() != CodecID.CODEC_ID_NONE) || (video_stream_copy != 0) || (video_codec_name != "");
        use_audio = (file_oformat.get_audio_codec() != CodecID.CODEC_ID_NONE) || (audio_stream_copy != 0) || (audio_codec_name != "");
        use_subtitle = (file_oformat.get_subtitle_codec() != CodecID.CODEC_ID_NONE) || (subtitle_stream_copy != 0) || (subtitle_codec_name != "");
	    use_data = (data_stream_copy != 0) || (data_codec_name != "");
        
	    OutBBBB tmpB = check_inputs();
	    input_has_video = tmpB.get_b1();
        input_has_audio = tmpB.get_b2();
        input_has_subtitle = tmpB.get_b3();
        input_has_data = tmpB.get_b4();

        if (!input_has_video)
            use_video = false;
        if (!input_has_audio)
            use_audio = false;
        if (!input_has_subtitle)
            use_subtitle = false;
        if (!input_has_data)
            use_data = false;
        
        if (use_video)    
        	new_video_stream(oc, output_files.size());
        if (use_audio)    
        	new_audio_stream(oc, output_files.size());
        if (use_subtitle) 
        	new_subtitle_stream(oc, output_files.size());
        if (use_data)     
        	new_data_stream(oc, output_files.size());

        oc.set_timestamp(recording_timestamp);

        oc.set_metadata(metadata.av_dict_copy());
        
        /*}*/
        
        output_files.add(oc);

        /* check filename in case of an image number is expected */
        if (oc.get_oformat().has_flag(AVFormat.AVFMT_NEEDNUMBER)) {
            if (UtilsFormat.av_filename_number_test(oc.get_filename())) {
                print_error(oc.get_filename(), Error.AVERROR(Error.EINVAL));
                ffmpeg_exit(1);
            }
        }
        
        
        if (!oc.get_oformat().has_flag(AVFormat.AVFMT_NOFILE)) {
        	// TODO: not tested
            /* test if it already exists to avoid loosing precious files */
        	if ( (file_overwrite != 0) &&
        		 ( (!filename.contains(":")) ||
        		   (filename.charAt(1) == ':') ||
        		   (AVString.av_strstart(filename, "file:")) ) ) {
        		if (AVIO.avio_check(filename, 0) == 0) {
        			if (using_stdin == 0) {
        				System.out.println("File " + filename + " already exists. Overwrite ? [y/N] ");
        				if (!CmdUtils.read_yesno()) {
        					System.err.print("Not overwriting - exiting\n");
		                    ffmpeg_exit(1);
        				}        					
        			} else {
        				System.err.print("File " + filename + " already exists. Exiting.");
        				ffmpeg_exit(1);        				
        			}
        		}
        	}        	

            /* open the file */        	
        	err = AVIOBuf.avio_open(oc.get_pb(), filename, AVIO.AVIO_FLAG_WRITE);
            if (err < 0) {
                print_error(filename, err);
                ffmpeg_exit(1);
            }
        	
        }
        
        /*ap = new AVFormatParameters();
        if (ap.av_set_parameters(oc) < 0) {
        	System.out.println(oc.get_filename() + ": Invalid encoding parameters");
        	ffmpeg_exit(1);
        }
        */
        
        oc.set_preload((int)mux_preload * AVUtil.AV_TIME_BASE);
        oc.set_max_delay((int)mux_max_delay * AVUtil.AV_TIME_BASE);
        oc.set_loop_output(loop_output);
        
        CmdUtils.set_context_opts(oc, CmdUtils.avformat_opts, AVOption.AV_OPT_FLAG_ENCODING_PARAM, null);
        
        frame_rate = new AVRational(0, 0);
        frame_width = 0;
        frame_height  = 0;
        audio_sample_rate = 0;
        audio_channels    = 0;
        forced_key_frames = "";
        CmdUtils.uninit_opts();
        CmdUtils.init_opts();
        
		return 0;
	}


	public static void print_error(String filename, int err) {
		String errbuf = Error.av_strerror(err);
		System.err.print(String.format("%s: %s\n", filename, errbuf));
	}


	private static void new_data_stream(AVFormatContext oc, int file_idx) {
	    AVStream st;
	    AVCodec codec = null;
	    AVCodecContext data_enc;
	    
	    st = oc.av_new_stream((oc.get_nb_streams() < streamid_map.size()) ? streamid_map.get(oc.get_nb_streams()) : 0);
	    if (st == null) {
	        Log.av_log(null, Log.AV_LOG_ERROR, "Could not alloc stream.\n");
	    	ffmpeg_exit(1);
	    }
		new_output_stream(oc, file_idx);	    

		data_enc = st.get_codec();
		

		if (data_stream_copy == 0) {
			System.err.print("Data stream encoding not supported yet (only streamcopy)\n");
	        ffmpeg_exit(1);
		}

		st.get_codec().avcodec_get_context_defaults3(codec);
		data_enc.set_codec_type(AVMediaType.AVMEDIA_TYPE_DATA);
		

		if (data_codec_tag != 0)
			data_enc.set_codec_tag(data_codec_tag);

	    if ( (oc.get_oformat().get_flags() & AVFormat.AVFMT_GLOBALHEADER) != 0) {
	    	data_enc.add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	        CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_DATA).add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	    }
	    
	    if (data_stream_copy != 0) {
			st.set_stream_copy(true);
		} 

	    /* reset some key parameters */
	    data_disable = 0;
	    data_codec_name = null;
	    data_stream_copy = 0;		
		
	}


	private static void new_subtitle_stream(AVFormatContext oc, int file_idx) {
	    AVStream st;
	    AVOutputStream ost;
	    AVCodec codec = null;
	    CodecID codec_id = CodecID.CODEC_ID_NONE;
	    AVCodecContext subtitle_enc;

	    st = oc.av_new_stream((oc.get_nb_streams() < streamid_map.size()) ? streamid_map.get(oc.get_nb_streams()) : 0);
	    if (st == null) {
	    	System.err.print("Could not alloc stream.\n");
	    	ffmpeg_exit(1);
	    }
		ost = new_output_stream(oc, file_idx);

		subtitle_enc = st.get_codec();

		if (subtitle_stream_copy == 0) {
			if (subtitle_codec_name != "") {
				codec_id = find_codec_or_die(subtitle_codec_name, 
						AVMediaType.AVMEDIA_TYPE_SUBTITLE, 
						true, 
						CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_SUBTITLE).get_strict_std_compliance());
				codec = AVCodec.avcodec_find_encoder_by_name(subtitle_codec_name);
				ost.set_enc(codec);
			} else {
				codec_id = UtilsFormat.av_guess_codec(oc.get_oformat(), "", 
						oc.get_filename(), "", AVMediaType.AVMEDIA_TYPE_SUBTITLE);
				codec = AVCodec.avcodec_find_encoder(codec_id);
			}
		}
		
		st.get_codec().avcodec_get_context_defaults3(codec);
		ost.set_bitstream_filters(subtitle_bitstream_filters);
		subtitle_bitstream_filters = null;
		
		subtitle_enc.set_codec_type(AVMediaType.AVMEDIA_TYPE_SUBTITLE);
		

		if (subtitle_codec_tag != 0)
			subtitle_enc.set_codec_tag(subtitle_codec_tag);

	    if ( (oc.get_oformat().get_flags() & AVFormat.AVFMT_GLOBALHEADER) != 0) {
	    	subtitle_enc.add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	        CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_SUBTITLE).add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	    }
	    
	    if (subtitle_stream_copy != 0) {
			st.set_stream_copy(true);
		} else {
			subtitle_enc.set_codec_id(codec_id);
			set_context_opts(subtitle_enc, CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_SUBTITLE), 
					AVOption.AV_OPT_FLAG_SUBTITLE_PARAM |AVOption.AV_OPT_FLAG_ENCODING_PARAM, codec);
		}
	    

		if (subtitle_language != "")
			st.get_metadata().av_dict_set("language", subtitle_language, 0);
		

	    /* reset some key parameters */
	    subtitle_disable = 0;
	    subtitle_codec_name = null;
	    subtitle_stream_copy = 0;		
			
	}


	private static void new_audio_stream(AVFormatContext oc, int file_idx) {
	    AVStream st;
	    AVOutputStream ost;
	    AVCodec codec = null;
	    CodecID codec_id = CodecID.CODEC_ID_NONE;
	    AVCodecContext audio_enc;

	    st = oc.av_new_stream((oc.get_nb_streams() < streamid_map.size()) ? streamid_map.get(oc.get_nb_streams()) : 0);
	    if (st == null) {
	    	System.err.print("Could not alloc stream.\n");
	    	ffmpeg_exit(1);
	    }
		ost = new_output_stream(oc, file_idx);		

		if (audio_stream_copy == 0) {
			if (audio_codec_name != "") {
				codec_id = find_codec_or_die(audio_codec_name, 
						AVMediaType.AVMEDIA_TYPE_AUDIO, 
						true, 
						CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO).get_strict_std_compliance());
				codec = AVCodec.avcodec_find_encoder_by_name(audio_codec_name);
			} else {
				codec_id = UtilsFormat.av_guess_codec(oc.get_oformat(), "", 
						oc.get_filename(), "", AVMediaType.AVMEDIA_TYPE_AUDIO);
				codec = AVCodec.avcodec_find_encoder(codec_id);
			}
		}
		
		st.get_codec().avcodec_get_context_defaults3(codec);
		ost.set_bitstream_filters(audio_bitstream_filters);
		audio_bitstream_filters = null;
		
		st.get_codec().set_thread_count(thread_count);
		
		audio_enc = st.get_codec();
	    audio_enc.set_codec_type(AVMediaType.AVMEDIA_TYPE_AUDIO);

		if (audio_codec_tag != 0)
			audio_enc.set_codec_tag(audio_codec_tag);

	    if ( (oc.get_oformat().get_flags() & AVFormat.AVFMT_GLOBALHEADER) != 0) {
	    	audio_enc.add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	        CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO).add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	    }

		if (audio_stream_copy != 0) {
			st.set_stream_copy(true);
		} else {
			audio_enc.set_codec_id(codec_id);
			set_context_opts(audio_enc, CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO), 
					AVOption.AV_OPT_FLAG_AUDIO_PARAM |AVOption.AV_OPT_FLAG_ENCODING_PARAM, codec);
			
			
			if (audio_qscale > QSCALE_NONE) {
				audio_enc.add_flag(AVCodec.CODEC_FLAG_QSCALE);
				audio_enc.set_global_quality(AVUtil.FF_QP2LAMBDA * (int)audio_qscale);
			}

	        if (audio_channels != 0)
	            audio_enc.set_channels(audio_channels);
	        audio_enc.set_sample_fmt(audio_sample_fmt);
	        if (audio_sample_rate != 0)
	            audio_enc.set_sample_rate(audio_sample_rate);
	        audio_enc.set_channel_layout(channel_layout);
	        choose_sample_fmt(st, codec);
		}

		if (audio_language != "")
			st.get_metadata().av_dict_set("language", audio_language, 0);
		

	    /* reset some key parameters */
	    audio_disable = 0;
	    audio_codec_name = null;
	    audio_stream_copy = 0;		
	}


	private static void choose_sample_fmt(AVStream st, AVCodec codec) {

	    if (codec != null) {
	    	if (codec.get_sample_fmts() != null){
	    		boolean found = false;
	    		for (AVSampleFormat p : codec.get_sample_fmts()) {
	    			if (p == st.get_codec().get_sample_fmt()) {
	    				found = true;
	    				break;
	    			}
	    		}
	    		if (!found) {
	    			String codec_fmt = SampleFmt.av_get_sample_fmt_name(st.get_codec().get_sample_fmt());
	    			String first_fmt = SampleFmt.av_get_sample_fmt_name(codec.get_sample_fmt(0));
	    			
	    			if ( ((codec.get_capabilities() & AVCodec.CODEC_CAP_LOSSLESS) != 0) && (codec_fmt.compareTo(first_fmt) < 0) ) 
	    				Log.av_log(null, Log.AV_LOG_ERROR, "Convertion will not be lossless'\n");
	    			Log.av_log(null, Log.AV_LOG_WARNING,
	    	               "Incompatible sample format '%s' for codec '%s', auto-selecting format '%s'\n",
    						codec_fmt, codec.get_name(), first_fmt);
    				st.get_codec().set_sample_fmt(codec.get_sample_fmt(0));	
	    		}
	    	}
	    }
	}


	private static void new_video_stream(AVFormatContext oc, int file_idx) {
	    CodecID codec_id = CodecID.CODEC_ID_NONE;
	    AVCodec codec = null;
	    AVOutputStream ost;
	    AVStream st;
	    AVCodecContext video_enc;
	    
	    st = oc.av_new_stream((oc.get_nb_streams() < streamid_map.size()) ? streamid_map.get(oc.get_nb_streams()) : 0);
	    
	    if (st == null) {
	    	System.err.print("Could not alloc stream.\n");
	    	ffmpeg_exit(1);
	    }

		ost = new_output_stream(oc, file_idx);
		
		if (video_stream_copy == 0) {
			if (video_codec_name != "") {
				codec_id = find_codec_or_die(video_codec_name, 
						AVMediaType.AVMEDIA_TYPE_VIDEO, 
						true, 
						CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO).get_strict_std_compliance());
				codec = AVCodec.avcodec_find_encoder_by_name(video_codec_name);
			} else {
				codec_id = UtilsFormat.av_guess_codec(oc.get_oformat(), "", 
						oc.get_filename(), "", AVMediaType.AVMEDIA_TYPE_VIDEO);
				codec = AVCodec.avcodec_find_encoder(codec_id);
			}
			ost.set_frame_aspect_ratio(frame_aspect_ratio);
			frame_aspect_ratio = 0;			
		}
		
		st.get_codec().avcodec_get_context_defaults3(codec);
		ost.set_bitstream_filters(video_bitstream_filters);
		video_bitstream_filters = null;
		
		st.get_codec().set_thread_count(thread_count);
		
		video_enc = st.get_codec();
		
		if (video_codec_tag != 0)
			video_enc.set_codec_tag(video_codec_tag);

	    if ( (oc.get_oformat().get_flags() & AVFormat.AVFMT_GLOBALHEADER) != 0) {
	    	video_enc.add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	        CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO).add_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER);
	    }

		if (video_stream_copy != 0) {
			st.set_stream_copy(true);
			video_enc.set_codec_type(AVMediaType.AVMEDIA_TYPE_VIDEO);
			AVRational rat = AVRational.av_d2q(frame_aspect_ratio*frame_height/frame_width, 255);
			video_enc.set_sample_aspect_ratio(rat);
			st.set_sample_aspect_ratio(rat);
		} else {
			
			if (frame_rate.get_num() != 0)
				ost.set_frame_rate(frame_rate);
			video_enc.set_codec_id(codec_id);
			set_context_opts(video_enc, CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO), 
					AVOption.AV_OPT_FLAG_VIDEO_PARAM |AVOption.AV_OPT_FLAG_ENCODING_PARAM, codec);
			
			video_enc.set_width(frame_width);
			video_enc.set_height(frame_height);
			video_enc.set_pix_fmt(frame_pix_fmt);
			video_enc.set_bits_per_raw_sample(frame_bits_per_raw_sample);
			st.set_sample_aspect_ratio(video_enc.get_sample_aspect_ratio());
			
			if (intra_only != 0)
				video_enc.set_gop_size(0);
			
			if ( (video_qscale != 0) || (same_quality != 0) ) {
				video_enc.add_flag(AVCodec.CODEC_FLAG_QSCALE);
				video_enc.set_global_quality(AVUtil.FF_QP2LAMBDA * (int)video_qscale);
			}
			
			if (intra_matrix != null)
				video_enc.set_intra_matrix(intra_matrix);
			if (inter_matrix != null)
				video_enc.set_inter_matrix(inter_matrix);
			
			
			/*p= video_rc_override_string;
	        for(i=0; p; i++){
	            int start, end, q;
	            int e=sscanf(p, "%d,%d,%d", &start, &end, &q);
	            if(e!=3){
System.err.println("error parsing rc_override\n");
	                ffmpeg_exit(1);
	            }
	            video_enc.get_rc_override=
	                av_realloc(video_enc.get_rc_override,
	                           sizeof(RcOverride)*(i+1));
	            video_enc.get_rc_override[i].start_frame= start;
	            video_enc.get_rc_override[i].end_frame  = end;
	            if(q>0){
	                video_enc.get_rc_override[i].qscale= q;
	                video_enc.get_rc_override[i].quality_factor= 1.0;
	            }
	            else{
	                video_enc.get_rc_override[i].qscale= 0;
	                video_enc.get_rc_override[i].quality_factor= -q/100.0;
	            }
	            p= strchr(p, '/');
	            if(p) p++;
	        }
	        
			
			video_enc.set_rc_override(i);*/
			
			if (video_enc.get_rc_initial_buffer_occupancy() == 0)
				video_enc.set_rc_initial_buffer_occupancy(video_enc.get_rc_buffer_size() * 3 / 4);
			video_enc.set_me_threshold(me_threshold);
			video_enc.set_intra_dc_precision(intra_dc_precision - 8);
			
			if (do_psnr != 0)
				video_enc.add_flag(AVCodec.CODEC_FLAG_PSNR);
			
			/* two pass mode */
			if (do_pass != 0) {
				if (do_pass == 1)
					video_enc.add_flag(AVCodec.CODEC_FLAG_PASS1);
				else
					video_enc.add_flag(AVCodec.CODEC_FLAG_PASS2);				
			}
			
			if (forced_key_frames != "")
				parse_forced_key_frames(forced_key_frames, ost, video_enc);
				
		}
		
		if (video_language != "")
			st.get_metadata().av_dict_set("language", video_language, 0);
		

	    /* reset some key parameters */
	    video_disable = 0;
	    video_codec_name = null;
	    forced_key_frames = null;
	    video_stream_copy = 0;
	    frame_pix_fmt = PixelFormat.PIX_FMT_NONE;	    
	}

	static void parse_forced_key_frames(String kf, AVOutputStream ost,
	                                    AVCodecContext avctx)
	{
/*		TODO IMPL
	    char *p;
	    int n = 1, i;
	    long t;

	    for (p = kf; *p; p++)
	        if (*p == ',')
	            n++;
	    ost.get_forced_kf_count = n;
	    ost.get_forced_kf_pts = av_malloc(sizeof(*ost.get_forced_kf_pts) * n);
	    if (!ost.get_forced_kf_pts) {
	        av_log(NULL, AV_LOG_FATAL, "Could not allocate forced key frames array.\n");
	        ffmpeg_exit(1);
	    }
	    for (i = 0; i < n; i++) {
	        p = i ? strchr(p, ',') + 1 : kf;
	        t = parse_time_or_die("force_key_frames", p, 1);
	        ost.get_forced_kf_pts[i] = av_rescale_q(t, AV_TIME_BASE_Q, avctx.get_time_base);
	    }*/
	}



	private static AVOutputStream new_output_stream(AVFormatContext oc, int file_idx) {
	    AVOutputStream ost = new AVOutputStream();
	    int idx = oc.get_nb_streams() - 1;    
	    
	    output_streams_for_file.get(file_idx).add(ost);
	    
	    ost.set_file_index(file_idx);
	    ost.set_index(output_streams_for_file.get(file_idx).size()-1);
	    	    
	    OutOL tmp = AVOption.av_get_int(CmdUtils.sws_opts, CmdUtils.sws_opts.get_av_class(), "sws_flags", false);
	    long val = tmp.get_val();
	    ost.set_sws_flags((int)val);
	    
		return ost;
	}


	private static OutBBBB check_inputs() {
	    boolean has_video = false;
	    boolean has_audio = false;
	    boolean has_subtitle = false;
	    boolean has_data = false;
	    
	    for (AVInputFile ifil: input_files) {
	    	AVFormatContext ic = ifil.get_ctx();
	    	
	    	for (AVStream st: ic.get_streams()) {
	    		AVCodecContext enc = st.get_codec();
	    		switch (enc.get_codec_type()) {
	    		case AVMEDIA_TYPE_AUDIO:
	    			has_audio = true;
	    			break;
	    		case AVMEDIA_TYPE_VIDEO:
	    			has_video = true;
	    			break;
	    		case AVMEDIA_TYPE_SUBTITLE:
	    			has_subtitle = true;
	    			break;
	    		case AVMEDIA_TYPE_ATTACHMENT:
	    		case AVMEDIA_TYPE_DATA:
	    		case AVMEDIA_TYPE_UNKNOWN:
	    			has_data = true;
	    			break;
    			default:
    				System.exit(-1);
	    		}
	    	}
	    }
	    
		return new OutBBBB(has_video, has_audio, has_subtitle, has_data);
	}


	private static void prepare_app_arguments(String[] argv) {
		// TODO: windows case
		/* Nothing to do otherwise */
	}


	public static void show_usage() {
	    System.out.println("Java Audio and Video encoder");
	    System.out.println("usage: jffmpeg [options] [[infile options] -i infile]... {[outfile options] outfile}...");
		
	}


	public static void main(String[] args) {
	    long ti;
		// Register Codecs
	    AVCodec.avcodec_register_all();
	    
	    
	    // avdevice_register_all();
	    AVFilter.avfilter_register_all();
	    
		// Register all formats
		AVFormat.av_register_all();
		
		CmdUtils.init_opts();
		
		parse_options(args);
		

	    if( (output_files.size() <= 0) && (input_files.size() == 0) ) {
	        show_usage();
	        System.out.println("Use -h to get full help");
	        ffmpeg_exit(1);
	    }

	    /* file converter / grab */
	    if (output_files.size() <= 0) {
	        System.out.println("At least one output file must be specified");
	        ffmpeg_exit(1);
	    }

	    if (input_files.size() == 0) {
	        System.out.println("At least one input file must be specified");
	        ffmpeg_exit(1);
	    }

	    ti = getutime();
	    
	    if (transcode(output_files, input_files, stream_maps) < 0)
	    	ffmpeg_exit(1);

	    ti = getutime() - ti;
	    
	    
	    if (do_benchmark != 0) {
	        System.out.println(String.format("bench: utime=%.3fs\n", ti / 1000000.0));
	    }

		
		System.out.println("end main");
		
	}


	private static int transcode(ArrayList<AVFormatContext> output_files,
			ArrayList<AVInputFile> input_files,
			ArrayList<AVStreamMap> stream_maps) {

	    int ret = 0, i, j, k, n, nb_ostreams = 0, step;

	    AVFormatContext is, os;
	    AVCodecContext codec, icodec;
		AVOutputStream [] ost_table;
		AVOutputStream ost;
		AVInputStream ist;
	    String error = "";
	    int key;
	    int want_sdp = 1;
	    byte [] no_packet = new byte[MAX_FILES];
	    int no_packet_count = 0;
		EnumMap<AVMediaType, Integer> nb_streams = new EnumMap<AVMediaType, Integer>(AVMediaType.class);
		EnumMap<AVMediaType, Integer> nb_frame_threshold = new EnumMap<AVMediaType, Integer>(AVMediaType.class);
		
		for (AVMediaType m: AVMediaType.values()) {
			nb_streams.put(m, 0);
			nb_frame_threshold.put(m, 0);
		}

	    if (rate_emu != 0)
	        for (i = 0 ; i < input_streams.size(); i++)
	            input_streams.get(i).set_start(UtilsFormat.av_gettime());
	    
	    /* output stream init */
	    nb_ostreams = 0;
	    for (i = 0 ; i < output_files.size(); i++) {
	    	os = output_files.get(i);
	    	if ( (os.get_nb_streams() == 0) && !os.get_oformat().has_flag(AVFormat.AVFMT_NOSTREAMS) ) {
	    		UtilsFormat.av_dump_format(os, i, os.get_filename(), true);
	            System.out.println("Output file #" + i + " does not contain any stream");
	            ret = Error.AVERROR(Error.EINVAL);
	            //goto fail;
	            return ret;
	        }
	    	i++;
	        nb_ostreams += os.get_nb_streams();
	    }
	    if ( (stream_maps.size() > 0) && (stream_maps.size() != nb_ostreams) ) {
	    	System.out.println("Number of stream maps must match number of output streams");
            ret = Error.AVERROR(Error.EINVAL);
            //goto fail;
            return ret;
	    }

	    /* Sanity check the mapping args -- do the input files & streams exist? */
	    for(i = 0 ; i < stream_maps.size() ; i++) {
	        int fi = stream_maps.get(i).get_file_index();
	        int si = stream_maps.get(i).get_stream_index();

	        if ( (fi < 0) || (fi > input_files.size() - 1) ||
	             (si < 0) || (si > input_files.get(i).get_ctx().get_nb_streams() - 1) ) {
	        	System.out.println("Could not find input stream #" + fi + "." + si);
	            ret = Error.AVERROR(Error.EINVAL);
	            //goto fail;
	            return ret;
	        }
	    }
	    
	    ost_table = new AVOutputStream[nb_ostreams];

	    for (k = 0 ; k < output_files.size(); k++) {	
	    	os = output_files.get(k);
			for (i = 0 ; i < os.get_nb_streams() ; i++) {
				AVMediaType st_type = os.get_stream(i).get_codec().get_codec_type();
				nb_streams.put(st_type, nb_streams.get(st_type) + 1);
			}	    	
	    }

	    for (step = 1<<30 ; step != 0 ; step >>= 1) {

			EnumMap<AVMediaType, Integer> found_streams = new EnumMap<AVMediaType, Integer>(AVMediaType.class);
			for (AVMediaType m: AVMediaType.values()) {
				found_streams.put(m, 0);
	            nb_frame_threshold.put(m, nb_frame_threshold.get(m) + step);
			}

	        for (j = 0 ; j < input_streams.size(); j++) {
	            int skip=0;
	            ist = input_streams.get(j);
	            if (opt_programid != 0) {
	                int pi,si;
	                AVFormatContext f= input_files.get(ist.get_file_index()).get_ctx();
	                skip = 1;
	                for (pi = 0 ; pi < f.get_nb_programs() ; pi++) {
	                	AVProgram p = f.get_program(pi);
	                    if (p.get_id() == opt_programid)
	                    	for (int s_idx : p.get_stream_index()) {
	                    		if (f.get_stream(s_idx).equals(ist.get_st()));
	                    		skip = 0;
	                    	}
	                }
	            }
	            
	            if ( (ist.get_discard() != 0) && 
	            	 (ist.get_st().get_discard() != AVDiscard.AVDISCARD_ALL) && 
	            	 (skip == 0) && 
	            	 (nb_frame_threshold.get(ist.get_st().get_codec().get_codec_type()) <= ist.get_st().get_codec_info_nb_frames()) ) {
	                found_streams.put(ist.get_st().get_codec().get_codec_type(), 
	                		found_streams.get(ist.get_st().get_codec().get_codec_type())+1);
	            }
	        }
	        for (AVMediaType m: AVMediaType.values()) {
	        	if (found_streams.get(m) < nb_streams.get(m))
	        		nb_frame_threshold.put(m, nb_frame_threshold.get(m)-step);
			}
	    }
	    

	    n = 0;
	    for(k = 0 ; k < output_files.size(); k++) {
	        os = output_files.get(k);
	        for (i = 0 ; i < os.get_nb_streams() ; i++,n++) {
	            int found;
	            ost = output_streams_for_file.get(k).get(i);
	            ost_table[n] = ost;
	            ost.set_st(os.get_stream(i));
	            
	            if (stream_maps.size() > 0) {
	                ost.set_source_index(input_files.get(stream_maps.get(n).get_file_index()).get_ist_index() +
	                    stream_maps.get(n).get_stream_index());

	                /* Sanity check that the stream types match */
	                if (input_streams.get(ost.get_source_index()).get_st().get_codec().get_codec_type() != ost.get_st().get_codec().get_codec_type()) {
	                    int l = ost.get_file_index();
	                    UtilsFormat.av_dump_format(output_files.get(l), l, output_files.get(l).get_filename(), true);

	                    System.err.print(String.format("Codec type mismatch for mapping #%d.%d -> #%d.%d\n",
	                       stream_maps.get(n).get_file_index(), stream_maps.get(n).get_stream_index(),
	                       ost.get_file_index(), ost.get_index()));
	                    ffmpeg_exit(1);
	                }

	            } else {
	                /* get corresponding input stream index : we select the first one with the right type */
	                found = 0;
	                for (j = 0; j < input_streams.size() ; j++) {
	                    int skip = 0;
	                    ist = input_streams.get(j);
	                    if (opt_programid != 0){
	                        int pi,si;
	                        AVFormatContext f = input_files.get(ist.get_file_index()).get_ctx();
	                        skip = 1;
	                        for (pi = 0 ; pi < f.get_nb_programs(); pi++){
	                            AVProgram p= f.get_program(pi);
	                            if (p.get_id() == opt_programid)
	                                for (si = 0 ; si < p.get_nb_stream_index() ; si++){
	                                    if (f.get_stream(p.get_stream_index(si)).equals(ist.get_st()))
	                                        skip=0;
	                                }
	                        }
	                    }
	                    if ( (ist.get_discard() != 0) && 
	                    	 (ist.get_st().get_discard() != AVDiscard.AVDISCARD_ALL) && 
	                    	 (skip == 0) &&
	                         (ist.get_st().get_codec().get_codec_type() == ost.get_st().get_codec().get_codec_type()) &&
	                         (nb_frame_threshold.get(ist.get_st().get_codec().get_codec_type()) <= ist.get_st().get_codec_info_nb_frames()) ) {
	                            ost.set_source_index(j);
	                            found = 1;
	                            break;
	                    }
	                }

	                if (found == 0) {
	                    if (opt_programid == 0) {
	                        /* try again and reuse existing stream */
	                        for (j = 0 ; j < input_streams.size() ; j++) {
	                        	ist = input_streams.get(j);
	                            if ( (ist.get_st().get_codec().get_codec_type() == ost.get_st().get_codec().get_codec_type()) &&
	                                 (ist.get_st().get_discard() != AVDiscard.AVDISCARD_ALL) ) {
	                                ost.set_source_index(j);
	                                found = 1;
	                            }
	                        }
	                    }
	                    if (found == 0) {
	                        i= ost.get_file_index();
	                        UtilsFormat.av_dump_format(output_files.get(i), i, output_files.get(i).get_filename(), true);
	                        System.err.print(String.format("Could not find input stream matching output stream #%d.%d\n",
		                    				ost.get_file_index(), 
		                    				ost.get_index()));
	                        ffmpeg_exit(1);
	                    }
	                }
	            }
	            ist = input_streams.get(ost.get_source_index());
	            ist.set_discard(0);
	            ost.set_sync_ist( (stream_maps.size() > 0) ?
	                input_streams.get(input_files.get(stream_maps.get(n).get_sync_file_index()).get_ist_index() + stream_maps.get(n).get_sync_stream_index()) : ist);
	        }
	    }
	    

	    /* for each output stream, we compute the right encoding parameters */
	    for (i = 0 ; i < nb_ostreams ; i++) {
	        ost = ost_table[i];
	        os = output_files.get(ost.get_file_index());
	        ist = input_streams.get(ost.get_source_index());

	        codec = ost.get_st().get_codec();
	        icodec = ist.get_st().get_codec();

	        if (metadata_streams_autocopy != 0)
	            AVDictionary.av_dict_copy(ost.get_st().get_metadata(), ist.get_st().get_metadata(),
	            		AVDictionary.AV_DICT_DONT_OVERWRITE);

	        ost.get_st().set_disposition(ist.get_st().get_disposition());
	        codec.set_bits_per_raw_sample(icodec.get_bits_per_raw_sample());
	        codec.set_chroma_sample_location(icodec.get_chroma_sample_location());

	        if (ost.get_st().get_stream_copy()) {
	            long extra_size = icodec.get_extradata_size() + AVCodec.FF_INPUT_BUFFER_PADDING_SIZE;

	            if (extra_size > Integer.MAX_VALUE) {
	            	//gtoto fail
	                return ret;
	            }

	            /* if stream_copy is selected, no need to decode or encode */
	            codec.set_codec_id(icodec.get_codec_id());
	            codec.set_codec_type(icodec.get_codec_type());

	            if (codec.get_codec_tag() == 0) {
	                if ( (os.get_oformat().get_codec_tag() == null) ||
	                	 (UtilsFormat.av_codec_get_id(os.get_oformat().get_codec_tag(), icodec.get_codec_tag()) == codec.get_codec_id()) ||
	                	 (UtilsFormat.av_codec_get_tag(os.get_oformat().get_codec_tag(), icodec.get_codec_id()) <= 0) )
	                    codec.set_codec_tag(icodec.get_codec_tag());

	            }

	            codec.set_bit_rate(icodec.get_bit_rate());
	            codec.set_rc_max_rate(icodec.get_rc_max_rate());
	            codec.set_rc_buffer_size(icodec.get_rc_buffer_size());
	            codec.set_extradata(icodec.get_extradata());
	            //codec.set_extradata_size(icodec.get_extradata_size());
	            
	            if ( (copy_tb == 0) && 
	            	 (icodec.get_time_base().av_q2d() * icodec.get_ticks_per_frame() > ist.get_st().get_time_base().av_q2d()) && 
	            	 (ist.get_st().get_time_base().av_q2d() < 1.0 / 500) ){
	                codec.set_time_base(icodec.get_time_base());
	                codec.get_time_base().set_num(codec.get_time_base().get_num() * icodec.get_ticks_per_frame());
	                codec.get_time_base().av_reduce(Integer.MAX_VALUE);

	            } else
	                codec.set_time_base(ist.get_st().get_time_base());
	            
	           switch(codec.get_codec_type()) {
	            case AVMEDIA_TYPE_AUDIO:
	                if (audio_volume != 256) {
	                    System.err.print("-acodec copy and -vol are incompatible (frames are not decoded)\n");
	                    ffmpeg_exit(1);
	                }
	                codec.set_channel_layout(icodec.get_channel_layout());
	                codec.set_sample_rate(icodec.get_sample_rate());
	                codec.set_channels(icodec.get_channels());
	                codec.set_frame_size(icodec.get_frame_size());
	                codec.set_audio_service_type(icodec.get_audio_service_type());
	                codec.set_block_align(icodec.get_block_align());
	                if ( (codec.get_block_align() == 1) && (codec.get_codec_id() == CodecID.CODEC_ID_MP3) )
	                    codec.set_block_align(0);
	                if(codec.get_codec_id() == CodecID.CODEC_ID_AC3)
	                    codec.set_block_align(0);
	                break;
	            case AVMEDIA_TYPE_VIDEO:
	                codec.set_pix_fmt(icodec.get_pix_fmt());
	                codec.set_width(icodec.get_width());
	                codec.set_height(icodec.get_height());
	                codec.set_has_b_frames(icodec.get_has_b_frames());
	                if (codec.get_sample_aspect_ratio().get_num() == 0) {
	                	AVRational ratio;	                	
	                	if (ist.get_st().get_sample_aspect_ratio().get_num() != 0)
	                		ratio = ist.get_st().get_sample_aspect_ratio();
	                	else if (ist.get_st().get_codec().get_sample_aspect_ratio().get_num() != 0)
	                		ratio = ist.get_st().get_codec().get_sample_aspect_ratio();
                		else
                			ratio = new AVRational(0, 1);
	                    codec.set_sample_aspect_ratio(ratio);
	                    ost.get_st().set_sample_aspect_ratio(ratio);     
	                }
	                break;
	            case AVMEDIA_TYPE_SUBTITLE:
	                codec.set_width(icodec.get_width());
	                codec.set_height(icodec.get_height());
	                break;
	            case AVMEDIA_TYPE_DATA:
	                break;
	            default:
	                ffmpeg_exit(0);
	            }
	        } else {
	            if (ost.get_enc( ) == null)
	                ost.set_enc(AVCodec.avcodec_find_encoder(ost.get_st().get_codec().get_codec_id()));
	            switch (codec.get_codec_type()) {
	            case AVMEDIA_TYPE_AUDIO:
	                ost.set_fifo(AVFifoBuffer.av_fifo_alloc(1024));
	                ost.set_reformat_pair(MAKE_SFMT_PAIR(AVSampleFormat.AV_SAMPLE_FMT_NONE, AVSampleFormat.AV_SAMPLE_FMT_NONE));
	                if (codec.get_sample_rate() == 0) {
	                    codec.set_sample_rate(icodec.get_sample_rate());
	                    if (icodec.get_lowres() != 0)
	                        codec.set_sample_rate(codec.get_sample_rate() >> icodec.get_lowres());
	                }
	                choose_sample_rate(ost.get_st(), ost.get_enc());
	                codec.set_time_base(new AVRational(1, codec.get_sample_rate()));
	                if (codec.get_channels() == 0)
	                    codec.set_channels(icodec.get_channels());
	                if (AudioConvert.av_get_channel_layout_nb_channels(codec.get_channel_layout()) != codec.get_channels())
	                    codec.set_channel_layout(0);
	                ost.set_audio_resample( (codec.get_sample_rate() != icodec.get_sample_rate() || audio_sync_method > 1) ?1:0);
	                //icodec.set_request_channels(codec.get_channels());
	                ist.set_decoding_needed(1);
	                ost.set_encoding_needed(1);
	                ost.set_resample_sample_fmt(icodec.get_sample_fmt());
	                ost.set_resample_sample_rate(icodec.get_sample_rate());
	                ost.set_resample_channels(icodec.get_channels());
	                break;
	            case AVMEDIA_TYPE_VIDEO:
	                if (codec.get_pix_fmt() == PixelFormat.PIX_FMT_NONE)
	                    codec.set_pix_fmt(icodec.get_pix_fmt());
	                choose_pixel_fmt(ost.get_st(), ost.get_enc());

	                if (ost.get_st().get_codec().get_pix_fmt() == PixelFormat.PIX_FMT_NONE) {
	                   System.err.print("Video pixel format is unknown, stream cannot be encoded\n");
	                    ffmpeg_exit(1);
	                }
	                ost.set_video_resample(( (codec.get_width()   != icodec.get_width()) ||
	                                         (codec.get_height()  != icodec.get_height()) ||
	                                         (codec.get_pix_fmt() != icodec.get_pix_fmt()) ) ? 1 : 0 );
	                if (ost.get_video_resample() != 0) {
	                    codec.set_bits_per_raw_sample(frame_bits_per_raw_sample);
	                }
	                if ( (codec.get_width() == 0) || (codec.get_height() == 0) ) {
	                    codec.set_width(icodec.get_width());
	                    codec.set_height(icodec.get_height());
	                }
	                ost.set_resample_height(icodec.get_height());
	                ost.set_resample_width(icodec.get_width());
	                ost.set_resample_pix_fmt(icodec.get_pix_fmt());
	                ost.set_encoding_needed(1);
	                ist.set_decoding_needed(1);

	                if ( (ost.get_frame_rate() == null) || (ost.get_frame_rate().get_num() == 0) ) {
	                	if (ist.get_st().get_r_frame_rate().get_num() != 0)
	                		ost.set_frame_rate(ist.get_st().get_r_frame_rate());
                		else
                			ost.set_frame_rate(new AVRational(25, 1));
	                }
	                
	                if ( (ost.get_enc() != null) && 
	                	 (ost.get_enc().get_supported_framerates() != null) && 
	                	 (ost.get_enc().get_supported_framerates().size() != 0) && 	                	 
	                	 (force_fps == 0) ) {
	                    int idx = AVRational.av_find_nearest_q_idx(ost.get_frame_rate(), ost.get_enc().get_supported_framerates());
	                    ost.set_frame_rate(ost.get_enc().get_supported_framerate(idx));
	                }
	                codec.set_time_base(new AVRational(ost.get_frame_rate().get_den(), ost.get_frame_rate().get_num()));
	                if ( (codec.get_time_base().av_q2d() < 0.001) && 
	                     (video_sync_method != 0) &&  
	                     ( (video_sync_method == 1) || ( (video_sync_method < 0) && (!os.get_oformat().has_flag(AVFormat.AVFMT_VARIABLE_FPS)))) ){
	                	Log.av_log("formatCtx", Log.AV_LOG_WARNING, "Frame rate very high for a muxer not effciciently supporting it.\n" +
	                                               "Please consider specifiying a lower framerate, a different muxer or -vsync 2\n");
	                }

	                if (configure_video_filters(ist, ost) != 0) {
	                    System.err.println("Error opening filters!");
	                    ffmpeg_exit(1);
	                }
                break;
	            case AVMEDIA_TYPE_SUBTITLE:
	                ost.set_encoding_needed(1);
	                ist.set_decoding_needed(1);
	                break;
	            default:
	                ffmpeg_exit(-1);
	                break;
	            }
	            /* two pass mode */
	            if ( (ost.get_encoding_needed() != 0) && 
	            	 (codec.get_codec_id() != CodecID.CODEC_ID_H264) &&
	                 (codec.has_flag(AVCodec.CODEC_FLAG_PASS1 | AVCodec.CODEC_FLAG_PASS2)) ) {
	            	String logfilename = String.format("%s-%d.log",
	            			pass_logfilename_prefix != null ? pass_logfilename_prefix : DEFAULT_PASS_LOGFILENAME_PREFIX,
	            			i);

	                if (codec.has_flag(AVCodec.CODEC_FLAG_PASS1)) {
	                   /* f = fopen(logfilename, "wb");
	                    if (!f) {
	                        fprintf(stderr, "Cannot write log file '%s' for pass-1 encoding: %s\n", logfilename, strerror(errno));
	                        ffmpeg_exit(1);
	                    }*/
	                    ost.set_logfile(logfilename);
	                } else {
	                   /* char  *logbuffer;
	                    size_t logbuffer_size;
	                    if (read_file(logfilename, &logbuffer, &logbuffer_size) < 0) {
	                        fprintf(stderr, "Error reading log file '%s' for pass-2 encoding\n", logfilename);
	                        ffmpeg_exit(1);
	                    }
	                    codec.set_stats_in(logbuffer);*/
	                }
	            }
	        }
	        if (codec.get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO){
	            /* maximum video buffer size is 6-bytes per pixel, plus DPX header size */
	            int size = codec.get_width() * codec.get_height();
	            bit_buffer_size = (int)Mathematics.FFMAX(bit_buffer_size, 6 * size + 1664);
	        }
	    }

	    if (bit_buffer == null)
	        bit_buffer = new short[bit_buffer_size];
	    
	    /* open each encoder */
	    for(i = 0 ; i < nb_ostreams ; i++) {
	        ost = ost_table[i];
	        if (ost.get_encoding_needed() != 0) {
	            AVCodec codec2 = ost.get_enc();
	            AVCodecContext dec = input_streams.get(ost.get_source_index()).get_st().get_codec();
	            if (codec2 == null) {
	                error = String.format("Encoder (codec id %d) not found for output stream #%d.%d",
	                				ost.get_st().get_codec().get_codec_id(), 
	                				ost.get_file_index(), ost.get_index());
	                ret = Error.AVERROR(Error.EINVAL);
	                ffmpeg_exit(0);
	                //goto dump_format;
	            }
	            if (dec.get_subtitle_header() != null) {
	                ost.get_st().get_codec().set_subtitle_header(dec.get_subtitle_header());
	            }
	            if (ost.get_st().get_codec().avcodec_open2(codec2) < 0) {
	            	error = String.format("Error while opening encoder for output stream #%d.%d - maybe "+
	                				"incorrect parameters such as bit_rate, rate, width or height",
	                				ost.get_file_index(), 
	                				ost.get_index());
	                ret = Error.AVERROR(Error.EINVAL);
	                ffmpeg_exit(0);
	                //goto dump_format;
	            }
	            extra_size += ost.get_st().get_codec().get_extradata_size();
	        }
	    }
	    

	    /* open each decoder */
	    for (i = 0; i < input_streams.size(); i++) {
	        ist = input_streams.get(i);
	        if (ist.get_decoding_needed() != 0) {
	            AVCodec codec2 = i < input_codecs.size() ? input_codecs.get(i) : null;
	            if (codec2 == null)
	                codec2 = AVCodec.avcodec_find_decoder(ist.get_st().get_codec().get_codec_id());
	            if (codec2 == null) {
	            	error = String.format("Decoder (codec id %d) not found for input stream #%d.%d",
	                				ist.get_st().get_codec().get_codec_id(), 
	                				ist.get_file_index(), 
	                				ist.get_st().get_index());
	                ret = Error.AVERROR(Error.EINVAL);
	                ffmpeg_exit(0);
	                //goto dump_format;
	            }
	            if (ist.get_st().get_codec().avcodec_open2(codec2) < 0) {
	            	error = String.format("Error while opening decoder for input stream #%d.%d",
	                				ist.get_file_index(), 
	                				ist.get_st().get_index());
	                ret = Error.AVERROR(Error.EINVAL);
	                ffmpeg_exit(0);
	                //goto dump_format;
	            }
	            //if (ist.get_st().get_codec().get_codec_type == AVMEDIA_TYPE_VIDEO)
	            //    ist.get_st().get_codec().get_flags |= CODEC_FLAG_REPEAT_FIELD;
	        }
	    }

	    /* init pts */
	    for (i = 0; i < input_streams.size(); i++) {
	        ist = input_streams.get(i);
	        AVStream st;
	        st = ist.get_st();
	        ist.set_pts(st.get_avg_frame_rate().get_num() != 0 ? (long)(- st.get_codec().get_has_b_frames() * AVUtil.AV_TIME_BASE / st.get_avg_frame_rate().av_q2d()) : 0);
	        ist.set_next_pts(AVUtil.AV_NOPTS_VALUE);
	        ist.set_is_start(1);
	    }
	    

	    /* set meta data information from input file if required */
	    for (i = 0 ; i < meta_data_maps.length ; i++) {
	        AVFormatContext [] files = new AVFormatContext[2];
	        AVDictionary [] meta = new AVDictionary[2];


	        int out_file_index = meta_data_maps[i][0].get_file();
	        int in_file_index = meta_data_maps[i][1].get_file();
	        if (in_file_index < 0 || out_file_index < 0)
	            continue;
	        OutIS tmp = METADATA_CHECK_INDEX(out_file_index, output_files.size(), "output file");
	        if (tmp != null) {
	        	ret = tmp.get_int();
	        	error = tmp.get_string();
                ffmpeg_exit(0);
                //goto dump_format;
	        	
	        }

	        tmp = METADATA_CHECK_INDEX(in_file_index, input_files.size(), "input file");
	        if (tmp != null) {
	        	ret = tmp.get_int();
	        	error = tmp.get_string();
                ffmpeg_exit(0);
                //goto dump_format;
	        	
	        }

	        files[0] = output_files.get(out_file_index);
	        files[1] = input_files.get(in_file_index).get_ctx();

	        for (int l = 0; l < 2; l++) {
	            AVMetaDataMap map = meta_data_maps[i][l];

	            switch (map.get_type()) {
	            case 'g':
	                meta[l] = files[l].get_metadata();
	                break;
	            case 's':
	    	        tmp = METADATA_CHECK_INDEX(map.get_index(), files[l].get_nb_streams(), "stream");
	    	        if (tmp != null) {
	    	        	ret = tmp.get_int();
	    	        	error = tmp.get_string();
	                    ffmpeg_exit(0);
	                    //goto dump_format;	    	        	
	    	        }
	                meta[l] = files[l].get_stream(map.get_index()).get_metadata();
	                break;
	            case 'c':
	    	        tmp = METADATA_CHECK_INDEX(map.get_index(), files[l].get_nb_chapters(), "chapter");
	    	        if (tmp != null) {
	    	        	ret = tmp.get_int();
	    	        	error = tmp.get_string();
	                    ffmpeg_exit(0);
	                    //goto dump_format;	    	        	
	    	        }
	                meta[l] = files[l].get_chapter(map.get_index()).get_metadata();
	                break;
	            case 'p':
	    	        tmp = METADATA_CHECK_INDEX(map.get_index(), files[l].get_nb_programs(), "program");
	    	        if (tmp != null) {
	    	        	ret = tmp.get_int();
	    	        	error = tmp.get_string();
	                    ffmpeg_exit(0);
	                    //goto dump_format;	    	        	
	    	        }
	                meta[l] = files[l].get_program(map.get_index()).get_metadata();
	                break;
	            }
	        }

	       AVDictionary.av_dict_copy(meta[0], meta[1], AVDictionary.AV_DICT_DONT_OVERWRITE);
	    }

	    /* copy global metadata by default */
	    if (metadata_global_autocopy != 0) {
	        for (i = 0 ; i < output_files.size() ; i++)
	        	AVDictionary.av_dict_copy(output_files.get(i).get_metadata(), 
	            			 			  input_files.get(0).get_ctx().get_metadata(),
	            			 			  AVDictionary.AV_DICT_DONT_OVERWRITE);
	    }
	    
	    /* copy chapters according to chapter maps */
	    for (i = 0 ; i < chapter_maps.size() ; i++) {
	        int infile  = chapter_maps.get(i).in_file;
	        int outfile = chapter_maps.get(i).out_file;

	        if (infile < 0 || outfile < 0)
	            continue;
	        if (infile >= input_files.size()) {
	            error = "Invalid input file index " + infile + " in chapter mapping.";
	            ret = Error.AVERROR(Error.EINVAL);
	            //goto dump_format;
	            ffmpeg_exit(ret);
	        }
	        if (outfile >= output_files.size()) {
	            error = "Invalid output file index " + outfile + " in chapter mapping.";
	            ret = Error.AVERROR(Error.EINVAL);
	            //goto dump_format;
	            ffmpeg_exit(ret);
	        }
	        copy_chapters(infile, outfile);
	    }
	    
	    /* copy chapters from the first input file that has them*/
	    if (chapter_maps.size() == 0)
	        for (i = 0 ; i < input_files.size() ; i++) {
	            if (input_files.get(i).get_ctx().get_nb_chapters() == 0)
	                continue;

	            for (j = 0; j < output_files.size() ; j++)
	            	copy_chapters(i, j);
	            break;
	        }


	    /* open files and write file headers */
	    for (i = 0 ; i < output_files.size() ; i++) {
	        os = output_files.get(i);
	        if (os.av_write_header() < 0) {
	            error = "Could not write header for output file #" + i + " (incorrect codec parameters ?)";
	            ret = Error.AVERROR(Error.EINVAL);
	            //goto dump_format;
	            continue;
	        }
	        if (os.get_oformat().get_name().equals("rtp")) {
	            want_sdp = 0;
	        }
	    }

	    //dump_format:
	      
	    /* dump the file output parameters - cannot be done before in case
	         of stream copy */
       for (i = 0 ; i < output_files.size() ; i++) {
           UtilsFormat.av_dump_format(output_files.get(i), i, output_files.get(i).get_filename(), true);
       }
       
       /* dump the stream mapping */
       if (verbose >= 0) {
           System.err.println("Stream mapping:");
           for (i = 0 ; i < nb_ostreams ; i++) {
               ost = ost_table[i];
               System.err.println(String.format("  Stream #%d.%d .get_ #%d.%d",
                       input_streams.get(ost.get_source_index()).get_file_index(),
                       input_streams.get(ost.get_source_index()).get_st().get_index(),
                       ost.get_file_index(),
                       ost.get_index()));
               if (ost.get_sync_ist() != input_streams.get(ost.get_source_index()))
                   System.err.println(String.format(" [sync #%d.%d]",
                           ost.get_sync_ist().get_file_index(),
                           ost.get_sync_ist().get_st().get_index()));               
           }
       }

       if (ret != 0) {
           System.err.println(error);
           ffmpeg_exit(ret);
       }

       /*if (want_sdp != 0) {
           print_sdp(output_files, output_files.size());
       }*/
	    

       if (using_stdin == 0) {
           if(verbose >= 0)
               System.err.println("Press [q] to stop, [?] for help");
           //avio_set_interrupt_cb(decode_interrupt_cb);
       }
       //term_init();
       
       timer_start = UtilsFormat.av_gettime();
       

       for(; received_sigterm == 0;) {
           int file_index, ist_index;
           AVPacket pkt;
           double ipts_min;
           double opts_min;

     //  redo:
           ipts_min= 1e100;
           opts_min= 1e100;
           /* if 'q' pressed, exits */
           if (using_stdin == 0) {
               if (q_pressed != 0)
                   break;
               /* read_key() returns 0 on EOF */
              /* key = read_key();
               if (key == 'q')
                   break;
               if (key == '+') verbose++;
               if (key == '-') verbose--;
               if (key == 's') qp_hist     ^= 1;
               if (key == 'h'){
                   if (do_hex_dump){
                       do_hex_dump = do_pkt_dump = 0;
                   } else if(do_pkt_dump){
                       do_hex_dump = 1;
                   } else
                       do_pkt_dump = 1;
                   av_log_set_level(AV_LOG_DEBUG);
               }
               if (key == 'd' || key == 'D'){
                   int debug=0;
                   if(key == 'D') {
                       debug = input_streams[0].st.get_codec().get_debug<<1;
                       if(!debug) debug = 1;
                       while(debug & (FF_DEBUG_DCT_COEFF|FF_DEBUG_VIS_QP|FF_DEBUG_VIS_MB_TYPE)) //unsupported, would just crash
                           debug += debug;
                   }else
                       scanf("%d", &debug);
                   for(i=0;i<nb_input_streams;i++) {
                       input_streams[i].st.get_codec().get_debug = debug;
                   }
                   for(i=0;i<nb_ostreams;i++) {
                       ost = ost_table[i];
                       ost.get_st().get_codec().get_debug = debug;
                   }
                   if(debug) av_log_set_level(AV_LOG_DEBUG);
                   fprintf(stderr,"debug=%d\n", debug);
               }
               if (key == '?'){
                   fprintf(stderr, "key    function\n"
                                   "?      show this help\n"
                                   "+      increase verbosity\n"
                                   "-      decrease verbosity\n"
                                   "D      cycle through available debug modes\n"
                                   "h      dump packets/hex press to cycle through the 3 states\n"
                                   "q      quit\n"
                                   "s      Show QP histogram\n"
                   );
               }*/
           }

           /* select the stream that we must read now by looking at the
              smallest output pts */
           file_index = -1;
           for (i = 0 ; i < nb_ostreams ; i++) {
               double ipts, opts;
               ost = ost_table[i];
               os = output_files.get(ost.get_file_index());
               ist = input_streams.get(ost.get_source_index());
               if ( (ist.get_is_past_recording_time() != 0) || 
            		(no_packet[ist.get_file_index()] != 0) )
                   continue;
               opts = ost.get_st().get_pts().get_val() * ost.get_st().get_time_base().av_q2d();
               ipts = (double)ist.get_pts();
               if (input_files.get(ist.get_file_index()).get_eof_reached() == 0){
                   if (ipts < ipts_min) {
                       ipts_min = ipts;
                       if (input_sync != 0) 
                    	   file_index = ist.get_file_index();
                   }
                   if (opts < opts_min) {
                       opts_min = opts;
                       if (input_sync == 0) 
                    	   file_index = ist.get_file_index();
                   }
               }
               if (ost.get_frame_number() >= max_frames[ost.get_st().get_codec().get_coder_type()]){
                   file_index= -1;
                   break;
               }
           }
           /* if none, if is finished */
           if (file_index < 0) {
        	   if (no_packet_count != 0) {
        		   no_packet_count = 0;
                   no_packet = new byte[no_packet.length];
                   try {
                	   Thread.sleep(10);
                   } catch (InterruptedException e) {
                	   e.printStackTrace();
                   }
                   continue;
               }
               break;
           }

           /* finish if limit size exhausted */
           if ( (limit_filesize != 0) && 
        		(limit_filesize <= output_files.get(0).get_pb().tell()) )
               break;

           /* read a frame from it and output it in the fifo */
           is = input_files.get(file_index).get_ctx();
           OutOI tmp = UtilsFormat.av_read_frame(is);
           ret = tmp.get_ret();
           pkt = (AVPacket) tmp.get_obj();
           
           if (ret == Error.AVERROR(Error.EAGAIN)){
               no_packet[file_index]=1;
               no_packet_count++;
               continue;
           }
           if (ret < 0) {
               input_files.get(file_index).set_eof_reached(1);
               if (opt_shortest != 0)
                   break;
               else
                   continue;
           }
           
           no_packet_count = 0;
           no_packet = new byte[no_packet.length];

           if (do_pkt_dump != 0) {
               UtilsFormat.av_pkt_dump_log2(null, Log.AV_LOG_DEBUG, pkt, do_hex_dump,
                                is.get_stream(pkt.get_stream_index()));
           }
           /* the following test is needed in case new streams appear
              dynamically in stream : we ignore them */
           if (pkt.get_stream_index() >= input_files.get(file_index).get_ctx().get_nb_streams()) {
               //goto discard_packet;
               /* dump report by using the output first video and audio streams */
               print_report(output_files, ost_table, nb_ostreams, 0);
               continue;
           }
           ist_index = input_files.get(file_index).get_ist_index() + pkt.get_stream_index();
           ist = input_streams.get(ist_index);
           if (ist.get_discard() != 0) {
               print_report(output_files, ost_table, nb_ostreams, 0);
               continue;
               //goto discard_packet;
           }

           if (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE)
        	   pkt.set_dts(pkt.get_dts() + Mathematics.av_rescale_q(input_files.get(ist.get_file_index()).get_ts_offset(), 
        			   									            AVUtil.AV_TIME_BASE_Q, 
        			   									            ist.get_st().get_time_base()));
           if (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE)
        	   pkt.set_pts(pkt.get_pts() + Mathematics.av_rescale_q(input_files.get(ist.get_file_index()).get_ts_offset(), 
        			   											    AVUtil.AV_TIME_BASE_Q, 
        			   											    ist.get_st().get_time_base()));

           if (ist.get_ts_scale() != 0) {
               if(pkt.get_pts() != AVUtil.AV_NOPTS_VALUE)
                   pkt.set_pts(Math.round(pkt.get_pts() * ist.get_ts_scale()));
               if(pkt.get_dts() != AVUtil.AV_NOPTS_VALUE)
                   pkt.set_dts(Math.round(pkt.get_dts() * ist.get_ts_scale()));
           }
           
           
//           fprintf(stderr, "next:%"PRId64" dts:%"PRId64" off:%"PRId64" %d\n", ist.get_next_pts, pkt.get_dts(), input_files_ts_offset[ist.get_file_index], ist.get_st().get_codec().get_codec_type);
           if ( (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && 
        		(ist.get_next_pts() != AVUtil.AV_NOPTS_VALUE) && 
        		(is.get_iformat().has_flag(AVFormat.AVFMT_TS_DISCONT)) ) {
               long pkt_dts = Mathematics.av_rescale_q(pkt.get_dts(), 
            		   							       ist.get_st().get_time_base(), 
            		   							       AVUtil.AV_TIME_BASE_Q);
               long delta = pkt_dts - ist.get_next_pts();
               if ( ( (Mathematics.FFABS(delta) > dts_delta_threshold * AVUtil.AV_TIME_BASE) || 
            		  (pkt_dts + 1 < ist.get_pts()) ) && (copy_ts == 0) ) {
            	   AVInputFile tmp_input = input_files.get(ist.get_file_index());
            	   tmp_input.set_ts_offset(tmp_input.get_ts_offset() - delta);
                   if (verbose > 2)
                       System.err.println(String.format("timestamp discontinuity %d, new offset= %d", delta, tmp_input.get_ts_offset()));
                   pkt.set_dts(pkt.get_dts() - Mathematics.av_rescale_q(delta, AVUtil.AV_TIME_BASE_Q, ist.get_st().get_time_base()));
                   if(pkt.get_pts() != AVUtil.AV_NOPTS_VALUE)
                       pkt.set_pts(pkt.get_pts() - Mathematics.av_rescale_q(delta, AVUtil.AV_TIME_BASE_Q, ist.get_st().get_time_base()));
               }
           }

           /* finish if recording time exhausted */
           if ( (recording_time != Long.MAX_VALUE) &&
                ((pkt.get_pts() != AVUtil.AV_NOPTS_VALUE ?
                		Mathematics.av_compare_ts(pkt.get_pts(), ist.get_st().get_time_base(), recording_time + start_time, new AVRational(1, 1000000))
                       :
                    	Mathematics.av_compare_ts(ist.get_pts(), AVUtil.AV_TIME_BASE_Q, recording_time + start_time, new AVRational(1, 1000000))
               ) >= 0 ) ) {
               ist.set_is_past_recording_time(1);
               print_report(output_files, ost_table, nb_ostreams, 0);
               continue;
               //goto discard_packet;
           }

           //fprintf(stderr,"read #%d.%d size=%d\n", ist.get_file_index, ist.get_st().get_index, pkt.size);
           if (output_packet(ist, ist_index, ost_table, nb_ostreams, pkt) < 0) {

               if (verbose >= 0)
                   System.err.println(String.format("Error while decoding stream #%d.%d\n",
                           ist.get_file_index(), ist.get_st().get_index()));
               if (exit_on_error != 0)
                   ffmpeg_exit(1);
           } 

       }
       
		
		return 0;
	}

	private static int configure_video_filters(AVInputStream ist,
			AVOutputStream ost) {
		AVFilterContext last_filter, filter;
	    /** filter graph containing all filters including input & output */
		AVCodecContext codec = ost.get_st().get_codec();
	    AVCodecContext icodec = ist.get_st().get_codec();
	    ArrayList<PixelFormat> pix_fmts = new ArrayList<PixelFormat>();
	    pix_fmts.add(codec.get_pix_fmt());
	    /* TODO Jerome: Useful ?
	     *  pix_fmts.add(PixelFormat.PIX_FMT_NONE); */
	    AVRational sample_aspect_ratio;
	    String args;
	    int ret;
	    OutOI ret_obj;
	    
	    ost.set_graph(AVFilterGraph.avfilter_graph_alloc());

	    if (ist.get_st().get_sample_aspect_ratio().get_num() != 0)
	        sample_aspect_ratio = ist.get_st().get_sample_aspect_ratio();
	    else
	        sample_aspect_ratio = ist.get_st().get_codec().get_sample_aspect_ratio();

	    args = String.format("%d:%d:%d:%d:%d:%d:%d",
	    			ist.get_st().get_codec().get_width(),
	             	ist.get_st().get_codec().get_height(), 
	             	ist.get_st().get_codec().get_pix_fmt().ordinal(), 
	             	1, 
	             	AVUtil.AV_TIME_BASE,
	             	sample_aspect_ratio.get_num(), 
	             	sample_aspect_ratio.get_den());

	    ret_obj = AVFilterGraph.avfilter_graph_create_filter(AVFilter.avfilter_get_by_name("buffer"),
                "src", args, null, ost.get_graph());
	    ret = ret_obj.get_ret();
	    ost.set_input_video_filter((AVFilterContext) ret_obj.get_obj());
	    
	  	if (ret < 0)
	  		return ret;
		 
	  	ret_obj = AVFilterGraph.avfilter_graph_create_filter(AVFilter.avfilter_get_by_name("buffersink"),
	  			"out", null, pix_fmts, ost.get_graph());
	    ret = ret_obj.get_ret();
	    ost.set_output_video_filter((AVFilterContext) ret_obj.get_obj());
	  	
	    if (ret < 0)
	        return ret;

	    last_filter = ost.get_input_video_filter();

	    if ( (codec.get_width()  != icodec.get_width()) || 
	    	 (codec.get_height() != icodec.get_height()) ) {
	        args = String.format("%d:%d:flags=0x%X", codec.get_width(),
	                 	codec.get_height(), ost.get_sws_flags());
	        
	        ret_obj = AVFilterGraph.avfilter_graph_create_filter(AVFilter.avfilter_get_by_name("scale"),
	                            null, args, null, ost.get_graph());
    	    ret = ret_obj.get_ret();
    	    filter = (AVFilterContext) ret_obj.get_obj();
	        if (ret < 0)
	            return ret;
	        
	        ret = AVFilter.avfilter_link(last_filter, 0, filter, 0);
	        if (ret < 0)
	            return ret;
	        last_filter = filter;
	    }

	    args = String.format("flags=0x%X", ost.get_sws_flags());
	    ost.get_graph().set_scale_sws_opts(args);

	    if (ost.get_avfilter() != null) {
	        AVFilterInOut outputs = AVFilterInOut.avfilter_inout_alloc();
	        AVFilterInOut inputs  = AVFilterInOut.avfilter_inout_alloc();

	        outputs.set_name("in");
	        outputs.set_filter_ctx(last_filter);
	        outputs.set_pad_idx(0);
	        outputs.set_next(null);

	        inputs.set_name("out");
	        inputs.set_filter_ctx(ost.get_output_video_filter());
	        inputs.set_pad_idx(0);
	        inputs.set_next(null);

	        if ((ret = GraphParser.avfilter_graph_parse(ost.get_graph(), 
	        		ost.get_avfilter(), inputs, outputs, null)) < 0)
	            return ret;
	    } else {
	        if ((ret = AVFilter.avfilter_link(last_filter, 0, ost.get_output_video_filter(), 0)) < 0)
	            return ret;
	    }

	    if ((ret = ost.get_graph().avfilter_graph_config(null)) < 0)
	        return ret;

	    codec.set_width(ost.get_output_video_filter().get_input(0).get_w());
	    codec.set_height(ost.get_output_video_filter().get_input(0).get_h());
	    
	    codec.set_sample_aspect_ratio(ost.get_frame_aspect_ratio() != 0 ?
	    		AVRational.av_d2q(ost.get_frame_aspect_ratio() * codec.get_height() / codec.get_width(), 255) :
    			ost.get_output_video_filter().get_input(0).get_sample_aspect_ratio());
	    ost.get_st().set_sample_aspect_ratio(codec.get_sample_aspect_ratio());

	    return 0;
	}

	static int samples_size = 0;
	
    private static int output_packet(AVInputStream ist, int ist_index,
			AVOutputStream[] ost_table, int nb_ostreams, AVPacket pkt) {
    	AVFormatContext os;
    	AVOutputStream ost;
    	int ret, i;
    	int got_output = 0;
    	AVFrame picture = null;
    	byte [] buffer_to_free = null;
    	
    	AVSubtitle subtitle = null;
    	long pkt_pts = AVUtil.AV_NOPTS_VALUE;
    	int frame_available;
    	float quality;

	    AVPacket avpkt;
	    int bps = SampleFmt.av_get_bytes_per_sample(ist.get_st().get_codec().get_sample_fmt());

	    if(ist.get_next_pts() == AVUtil.AV_NOPTS_VALUE)
	        ist.set_next_pts(ist.get_pts());

	    if (pkt == null) {
	        /* EOF handling */
	    	avpkt = new AVPacket();
	    	avpkt.av_init_packet();
	        avpkt.set_data(null);
	        //goto handle_eof;
	        // TODO EOF ?? pkt == null
	        return -1;
	    } else {
	        avpkt = pkt;
	    }

	    if (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) {
	    	long pts = Mathematics.av_rescale_q(pkt.get_dts(), ist.get_st().get_time_base(), AVUtil.AV_TIME_BASE_Q);
	        ist.set_next_pts(pts);
	        ist.set_pts(pts);
	    }
	    if (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) 
	        pkt_pts = Mathematics.av_rescale_q(pkt.get_pts(), ist.get_st().get_time_base(), AVUtil.AV_TIME_BASE_Q);

	    //while we have more to decode or while the decoder did output something on EOF
	    while ( (avpkt.get_size() > 0) || 
	    		( (pkt == null) && (got_output != 0) ) ) {
	    	short [] data_buf;
	    	short [] decoded_data_buf;
	        int data_size, decoded_data_size;
	        ist.set_pts(ist.get_next_pts());

	        if ( (avpkt.get_size()!= 0) && 
	        	 (avpkt.get_size() != pkt.get_size()) &&
	             ( ( (ist.get_showed_multi_packet_warning() == 0) && (verbose > 0) ) || (verbose > 1) ) ) {
	            System.err.println("Multiple frames in a packet from stream " + pkt.get_stream_index());
	            ist.set_showed_multi_packet_warning(1);
	        }

	        /* decode the packet if needed */
	        decoded_data_buf = null; /* fail safe */
	        decoded_data_size= 0;
	        data_buf  = avpkt.data;
	        data_size = avpkt.size;
	        System.out.println(ist.get_decoding_needed());
	        if (ist.get_decoding_needed() != 0) {
	            switch(ist.get_st().get_codec().get_codec_type()) {
	            case AVMEDIA_TYPE_AUDIO: {
	            	/* *2 in FFMAX is because samples is short [] => 2 bytes (replace sizeof(samples)) */
	                if ( (pkt != null) && 
	                	 (samples_size < Mathematics.FFMAX(pkt.get_size() * 2 * samples.length, AVCodec.AVCODEC_MAX_AUDIO_FRAME_SIZE)) ) {
	                    samples_size = (int)Mathematics.FFMAX(pkt.get_size() * 2 * samples.length, AVCodec.AVCODEC_MAX_AUDIO_FRAME_SIZE);
	                    samples = new short[samples_size / 2];
	                }
	                decoded_data_size = samples_size;
                    /* XXX: could avoid copy if PCM 16 bits with same
                       endianness as CPU */
	                OutOI ret_obj = ist.get_st().get_codec().avcodec_decode_audio3(avpkt);
	                samples = (short[]) ret_obj.get_obj();
	                ret = ret_obj.get_ret();
	                if (ret < 0)
	                    return ret;
	                //avpkt.data += ret;
	                //avpkt.size -= ret;
	                //data_size   = ret;
	                // decoded_data_size ?? comes from decode_audio
	                got_output = decoded_data_size > 0 ? 1 : 0;
	                /* Some bug in mpeg audio decoder gives */
	                /* decoded_data_size < 0, it seems they are overflows */
	                if (got_output == 0) {
	                    /* no audio frame */
	                    continue;
	                }
	                decoded_data_buf = UtilsArrays.short_to_byte_be(samples);
	                ist.set_next_pts(ist.get_next_pts() +
                		((long)AVUtil.AV_TIME_BASE / bps * decoded_data_size) /
	                     (ist.get_st().get_codec().get_sample_rate() * ist.get_st().get_codec().get_channels()));
	                break;
	            }
	            case AVMEDIA_TYPE_VIDEO: {
	                    decoded_data_size = (ist.get_st().get_codec().get_width() * ist.get_st().get_codec().get_height() * 3) / 2;
	                    /* XXX: allocate picture correctly */
	                    avpkt.pts = pkt_pts;
	                    avpkt.dts = ist.get_pts();
	                    pkt_pts = AVUtil.AV_NOPTS_VALUE;

	                    OutOI ret_obj = ist.get_st().get_codec().avcodec_decode_video2(avpkt);
	                    ret = ret_obj.get_ret();
	                    picture = (AVFrame) ret_obj.get_obj();
	                    got_output = (picture != null) ? 1 : 0;
	                                       
	            		File outputfile = new File( "toto2_" + img_num + ".jpg");
	            		img_num ++;
	            	    try {
	            			ImageIO.write(picture.get_img(), "jpg", outputfile);
	            		} catch (IOException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		}
	                    
	                    quality = (same_quality != 0) ? picture.get_quality() : 0;
	                    if (ret < 0)
	                        return ret;
	                    if (got_output == 0) {
	                        /* no picture yet */
	                        //goto discard_packet;
	                    	return 0;
	                    }
	                    ist.set_next_pts(picture.get_best_effort_timestamp());
	                    ist.set_pts(picture.get_best_effort_timestamp());
	                    if (ist.get_st().get_codec().get_time_base().get_num() != 0) {
	                        int ticks = (ist.get_st().get_parser() != null) ? 
	                        		ist.get_st().get_parser().get_repeat_pict() + 1 
	                        		: 
	                        		ist.get_st().get_codec().get_ticks_per_frame();
	                        ist.set_next_pts(ist.get_next_pts() +
	                        		((long)AVUtil.AV_TIME_BASE *
	                        		 ist.get_st().get_codec().get_time_base().get_num() * ticks) /
	                        		ist.get_st().get_codec().get_time_base().get_den());
	                    }
	                    avpkt.set_size(0);
	                    buffer_to_free = pre_process_video_frame(ist, (AVPicture)picture);
	                    break;
	            }
	            case AVMEDIA_TYPE_SUBTITLE: {
	            	OutOI ret_obj = ist.get_st().get_codec().avcodec_decode_subtitle2(avpkt);
	            	subtitle = (AVSubtitle)ret_obj.get_obj();
	            	ret = ret_obj.get_ret();
	            	got_output = (subtitle != null) ? 1 : 0; 
	                if (ret < 0)
	                    return ret;
	                if (got_output == 0) {
	                    //goto discard_packet;
	                	return 0;
	                }
	                avpkt.set_size(0);
	                break;
	            }
	            default:
	                return -1;
	            }
	        } else {
	            switch(ist.get_st().get_codec().get_codec_type()) {
	            case AVMEDIA_TYPE_AUDIO:
	                ist.set_next_pts(ist.get_next_pts() + 
	                		((long)AVUtil.AV_TIME_BASE * ist.get_st().get_codec().get_frame_size()) /
	                		ist.get_st().get_codec().get_sample_rate());
	                break;
	            case AVMEDIA_TYPE_VIDEO:
	                if (ist.get_st().get_codec().get_time_base().get_num() != 0) {
	                    int ticks = (ist.get_st().get_parser() != null) ? 
	                    		ist.get_st().get_parser().get_repeat_pict() + 1 
	                    		: 
	                    		ist.get_st().get_codec().get_ticks_per_frame();
	                    ist.set_next_pts(ist.get_next_pts() + 
	                    		((long)AVUtil.AV_TIME_BASE *
	                    		 ist.get_st().get_codec().get_time_base().get_num() * ticks) /
	                    		 ist.get_st().get_codec().get_time_base().get_den());
	                }
	                break;
	            }
	            ret = avpkt.size;
	            avpkt.size = 0;
	        }
	        

	        /******** TODO Jerome
	        if (ist.get_st().get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO)
    	        if ( (start_time == 0) || (ist.get_pts() >= start_time) ) {
    	            for (i = 0 ; i < nb_ostreams ; i++) {
    	                ost = ost_table[i];
    	                if ( (ost.get_input_video_filter() != null) && 
    	                	 (ost.get_source_index() == ist_index) ) {
    	                    if (picture.get_sample_aspect_ratio().get_num() == 0)
    	                        picture.set_sample_aspect_ratio(ist.get_st().get_sample_aspect_ratio());
    	                    picture.set_pts(ist.get_pts());

    	                    ost.get_input_video_filter().av_vsrc_buffer_add_frame(picture, AVFilterContext.AV_VSRC_BUF_FLAG_OVERWRITE);
    	                }
    	            }
    	        }

    	        // preprocess audio (volume)
    	        if (ist.get_st().get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
    	            if (audio_volume != 256) {
    	                short [] volp;
    	                volp = samples;
    	                for (i = 0 ; i < (decoded_data_size / 2) ; i++) {  // 2 => sizeof(short)
    	                    int v = (volp[i] * audio_volume + 128) >> 8;
    	                    if (v < -32768) v = -32768;
    	                    if (v >  32767) v = 32767;
    	                    volp[i] = (short)v;
    	                }
    	            }
    	        }

    	        // frame rate emulation 
    	        if (rate_emu != 0) {
    	            long pts = Mathematics.av_rescale(ist.get_pts(), 1000000, AVUtil.AV_TIME_BASE);
    	            long now = UtilsFormat.av_gettime() - ist.get_start();
    	            if (pts > now)
    	                usleep(pts - now);
    	        }
    	        // if output time reached then transcode raw format,
    	        // encode packets and output them 
    	        if ( (start_time == 0) || (ist.get_pts() >= start_time) )
    	            for (i = 0 ; i < nb_ostreams ; i++) {
    	                int frame_size = 0;

    	                ost = ost_table[i];
    	                if (ost.get_source_index() == ist_index) {
    	                
    	                	frame_available = ( ( (ist.get_st().get_codec().get_codec_type() != AVMediaType.AVMEDIA_TYPE_VIDEO) ||
    	                    				      (ost.get_output_video_filter() == null) || 
    	                    				      (AVFilter.avfilter_poll_frame(ost.get_output_video_filter().get_input(0)) != 0) ) ? 1 : 0);
	    	                while (frame_available != 0) {
	    	                    if ( (ist.get_st().get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) && 
	    	                         (ost.get_output_video_filter() != null) ) {
	    	                        AVRational ist_pts_tb = ost.get_output_video_filter().get_input(0).get_time_base();
	    	                        OutOI ret_obj = VSinkBuffer.av_vsink_buffer_get_video_buffer_ref(ost.get_output_video_filter(), 0);
	    	                        ret = ret_obj.get_ret();
	    	                        ost.set_picref((AVFilterBufferRef)ret_obj.get_obj()); 	                        
	    	                        if (ret < 0) {
	    	                        	// goto cont
	    	    	                	frame_available = ( ( (ist.get_st().get_codec().get_codec_type() != AVMediaType.AVMEDIA_TYPE_VIDEO) ||
	    	    	                    				      (ost.get_output_video_filter() != null) || 
	    	    	                    				      (AVFilter.avfilter_poll_frame(ost.get_output_video_filter().get_input(0)) != 0) ) ? 1 : 0);
	    	                            ost.get_picref().avfilter_unref_buffer();
	    	                        	
	    	                        }
	    	                        if (ost.get_picref() != null) {
	    	                        	picture.avfilter_fill_frame_from_video_buffer_ref(ost.get_picref());
	    	                            ist.set_pts(Mathematics.av_rescale_q(ost.get_picref().get_pts(), ist_pts_tb, AVUtil.AV_TIME_BASE_Q));
	    	                        }
	    	                    }
    	                	
	    	                    os = output_files.get(ost.get_file_index());
	
	    	                    // set the input output pts pairs 
	    	                    //ost.get_sync_ipts = (double)(ist.get_pts + input_files[ist.get_file_index].ts_offset - start_time)/ AVUtil.AV_TIME_BASE;
	
	    	                    if (ost.get_encoding_needed() != 0) {
	    	                        switch (ost.get_st().get_codec().get_codec_type()) {
	    	                        case AVMEDIA_TYPE_AUDIO:
	    	                            do_audio_out(os, ost, ist, decoded_data_buf, decoded_data_size);
	    	                            break;
	    	                        case AVMEDIA_TYPE_VIDEO:
	    	                        	
	    	                            if ( (ost.get_picref().get_video() != null) && 
	    	                            	 (ost.get_frame_aspect_ratio() == 0) )
	    	                                ost.get_st().get_codec().set_sample_aspect_ratio(ost.get_picref().get_video().get_sample_aspect_ratio());
	    	                            
	    	                            frame_size = do_video_out(os, ost, ist, picture, frame_size);
	    	                           
	    	                            if ( (vstats_filename != null) && 
	    	                            	 (frame_size != 0) )
	    	                                do_video_stats(os, ost, frame_size);
	    	                            break;
	    	                        case AVMEDIA_TYPE_SUBTITLE:
	    	                            do_subtitle_out(os, ost, ist, subtitle, pkt.get_pts());
	    	                            break;
	    	                        default:
	    	                            ffmpeg_exit(-1);
	    	                        }
	    	                    } //else {
	//    	                        AVFrame avframe; //FIXME/XXX remove this
	//    	                        AVPicture pict;
	//    	                        AVPacket opkt;
	//    	                        int64_t ost_tb_start_time= av_rescale_q(start_time, AVUtil.AV_TIME_BASE_Q, ost.get_st().get_time_base);
	//
	//    	                        av_init_packet(&opkt);
	//
	//    	                        if ((!ost.get_frame_number && !(pkt.get_flags & AV_PKT_FLAG_KEY)) && !copy_initial_nonkeyframes)
	//    	#if !CONFIG_AVFILTER
	//    	                            continue;
	//    	#else
	//    	                            goto cont;
	//    	#endif
	//
	//    	                        // no reencoding needed : output the packet directly 
	//    	                        // force the input stream PTS 
	//
	//    	                        avcodec_get_frame_defaults(&avframe);
	//    	                        ost.get_st().get_codec().get_coded_frame= &avframe;
	//    	                        avframe.key_frame = pkt.get_flags & AV_PKT_FLAG_KEY;
	//
	//    	                        if(ost.get_st().get_codec().get_codec_type == AVMEDIA_TYPE_AUDIO)
	//    	                            audio_size += data_size;
	//    	                        else if (ost.get_st().get_codec().get_codec_type == AVMEDIA_TYPE_VIDEO) {
	//    	                            video_size += data_size;
	//    	                            ost.get_sync_opts++;
	//    	                        }
	//
	//    	                        opkt.stream_index= ost.get_index;
	//    	                        if(pkt.get_pts != AVUtil.AV_NOPTS_VALUE)
	//    	                            opkt.pts= av_rescale_q(pkt.get_pts, ist.get_st().get_time_base, ost.get_st().get_time_base) - ost_tb_start_time;
	//    	                        else
	//    	                            opkt.pts= AVUtil.AV_NOPTS_VALUE;
	//
	//    	                        if (pkt.get_dts == AVUtil.AV_NOPTS_VALUE)
	//    	                            opkt.dts = av_rescale_q(ist.get_pts, AVUtil.AV_TIME_BASE_Q, ost.get_st().get_time_base);
	//    	                        else
	//    	                            opkt.dts = av_rescale_q(pkt.get_dts, ist.get_st().get_time_base, ost.get_st().get_time_base);
	//    	                        opkt.dts -= ost_tb_start_time;
	//
	//    	                        opkt.duration = av_rescale_q(pkt.get_duration, ist.get_st().get_time_base, ost.get_st().get_time_base);
	//    	                        opkt.flags= pkt.get_flags;
	//
	//    	                        //FIXME remove the following 2 lines they shall be replaced by the bitstream filters
	//    	                        if(   ost.get_st().get_codec().get_codec_id != CODEC_ID_H264
	//    	                           && ost.get_st().get_codec().get_codec_id != CODEC_ID_MPEG1VIDEO
	//    	                           && ost.get_st().get_codec().get_codec_id != CODEC_ID_MPEG2VIDEO
	//    	                           ) {
	//    	                            if(av_parser_change(ist.get_st().get_parser, ost.get_st().get_codec, &opkt.data, &opkt.size, data_buf, data_size, pkt.get_flags & AV_PKT_FLAG_KEY))
	//    	                                opkt.destruct= av_destruct_packet;
	//    	                        } else {
	//    	                            opkt.data = data_buf;
	//    	                            opkt.size = data_size;
	//    	                        }
	//
	//    	                        if (os.get_oformat.get_flags & AVFMT_RAWPICTURE) {
	//    	                            // store AVPicture in AVPacket, as expected by the output format 
	//    	                            avpicture_fill(&pict, opkt.data, ost.get_st().get_codec().get_pix_fmt, ost.get_st().get_codec().get_width, ost.get_st().get_codec().get_height);
	//    	                            opkt.data = (uint8_t *)&pict;
	//    	                            opkt.size = sizeof(AVPicture);
	//    	                            opkt.flags |= AV_PKT_FLAG_KEY;
	//    	                        }
	//    	                        write_frame(os, &opkt, ost.get_st().get_codec, ost.get_bitstream_filters);
	//    	                        ost.get_st().get_codec().get_frame_number++;
	//    	                        ost.get_frame_number++;
	//    	                        av_free_packet(&opkt);
	//    	                    }
	    	                    
	    	                    frame_available = ( (ist.get_st().get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) &&
	    	                                        (ost.get_output_video_filter() != null) && 
	    	                                        (AVFilter.avfilter_poll_frame(ost.get_output_video_filter().get_input(0)) != 0) ) ? 1 : 0;
	    	                    ost.get_picref().avfilter_unref_buffer();
	    	                }
    	                }*/
    	            }

	    
    

	    return 0;
	}
    



	private static void do_subtitle_out(AVFormatContext os, AVOutputStream ost,
			AVInputStream ist, AVSubtitle subtitle, long get_pts) {
		// TODO Jerome
		
	}


	private static void do_video_stats(AVFormatContext os, AVOutputStream ost,
			int frame_size) {
		// TODO Jerome
		
	}


	private static int do_video_out(AVFormatContext s, AVOutputStream ost,
			AVInputStream ist, AVFrame in_picture, int frame_size) {
	    int nb_frames, i, ret, resample_changed;
	    AVFrame final_picture, formatted_picture;
	    AVCodecContext enc, dec;
	    double sync_ipts;
	    
	    enc = ost.get_st().get_codec();
	    dec = ist.get_st().get_codec();    

	    sync_ipts = get_sync_ipts(ost) / enc.get_time_base().av_q2d();
	    
	    /* by default, we output a single frame */
	    nb_frames = 1;

	    frame_size = 0;	    

	    if (video_sync_method != 0) {
	        double vdelta = sync_ipts - ost.get_sync_opts();
	        //FIXME set to 0.5 after we fix some dts/pts bugs like in avidec.c
	        if (vdelta < -1.1)
	            nb_frames = 0;
	        else if ( (video_sync_method == 2) || 
	        		   ( (video_sync_method < 0) && s.get_oformat().has_flag(AVFormat.AVFMT_VARIABLE_FPS) ) ) {
	            if (vdelta <= -0.6){
	                nb_frames = 0;
	            } else if (vdelta > 0.6)
	                ost.set_sync_opts(Mathematics.lrintf(sync_ipts));
	        } else if (vdelta > 1.1)
	            nb_frames = (int) Mathematics.lrintf(vdelta);
	//fprintf(stderr, "vdelta:%f, ost.get_sync_opts():%"PRId64", ost->sync_ipts:%f nb_frames:%d\n", vdelta, ost.get_sync_opts(), get_sync_ipts(ost), nb_frames);
	        if (nb_frames == 0) {
	            ++nb_frames_drop;
	            if (verbose > 2)
	                System.err.println("*** drop!");
	        }else if (nb_frames > 1) {
	            nb_frames_dup += nb_frames - 1;
	            if (verbose > 2)
	            	System.err.println(String.format("*** %d dup!", nb_frames-1));
	        }
	    } else
	        ost.set_sync_opts(Mathematics.lrintf(sync_ipts));

	    nb_frames = (int) Mathematics.FFMIN(nb_frames, 
	    						      max_frames[AVMediaType.AVMEDIA_TYPE_VIDEO.ordinal()] - ost.get_frame_number());
	    if (nb_frames <= 0)
	        return frame_size;
	    
	    formatted_picture = in_picture;
	    final_picture = formatted_picture;	    

	    /* duplicates frame if needed */
	    for (i = 0 ; i < nb_frames ; i++) {
	        AVPacket pkt = new AVPacket();
	        pkt.av_init_packet();
	        pkt.set_stream_index(ost.get_index());

	        if (s.get_oformat().has_flag(AVFormat.AVFMT_RAWPICTURE)) {
	            /* raw pictures are written as AVPicture structure to
	               avoid any copies. We support temorarily the older
	               method. */
	            AVFrame old_frame = enc.get_coded_frame();
	            enc.set_coded_frame(dec.get_coded_frame()); //FIXME/XXX remove this hack
	            /* TODO Jerome
	            pkt.data = (uint8_t *)final_picture;
	            pkt.size =  sizeof(AVPicture);*/
	            pkt.set_pts(Mathematics.av_rescale_q(ost.get_sync_opts(), 
	            									 enc.get_time_base(), 
	            									 ost.get_st().get_time_base()));
	            pkt.add_flag(AVCodec.AV_PKT_FLAG_KEY);

	            write_frame(s, pkt, ost.get_st().get_codec(), ost.get_bitstream_filters());
	            enc.set_coded_frame(old_frame);
	        } else {
	            AVFrame big_picture;

	            big_picture = final_picture;
	            /* better than nothing: use input picture interlaced
	               settings */
	            big_picture.set_interlaced_frame(in_picture.get_interlaced_frame());
	            if ( (ost.get_st().get_codec().get_flags() & (AVCodec.CODEC_FLAG_INTERLACED_DCT|AVCodec.CODEC_FLAG_INTERLACED_ME)) != 0) {
	                if (top_field_first == -1)
	                    big_picture.set_top_field_first(in_picture.get_top_field_first());
	                else
	                    big_picture.set_top_field_first(top_field_first);
	            }

	            /* handles sameq here. This is not correct because it may
	               not be a global option */
	            float quality = same_quality != 0 ? ist.get_st().get_quality() : ost.get_st().get_quality();
	            big_picture.set_quality(Math.round(quality));
	            if (me_threshold == 0)
	                big_picture.set_pict_type(AVPictureType.AV_PICTURE_TYPE_P);
//	            big_picture.pts = AV_NOPTS_VALUE;
	            big_picture.set_pts(ost.get_sync_opts());
//	            big_picture.pts= av_rescale(ost->sync_opts, AV_TIME_BASE*(int64_t)enc->time_base.num, enc->time_base.den);
	//av_log(NULL, AV_LOG_DEBUG, "%"PRId64" -> encoder\n", ost->sync_opts);
	            if ( (ost.get_forced_kf_index() < ost.get_forced_kf_count()) &&
	                 (big_picture.get_pts() >= ost.get_forced_kf_pts(ost.get_forced_kf_index())) ) {
	                big_picture.set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	                ost.set_forced_kf_index(ost.get_forced_kf_index()+1);
	            }
	            
	            OutOI ret_obj = AVCodec.avcodec_encode_video(enc, bit_buffer, 
	            		bit_buffer_size, big_picture);
	            ret = ret_obj.get_ret();
	            bit_buffer = (short []) ret_obj.get_obj();
	            
	            if (ret < 0) {
	                System.err.println("Video encoding failed");
	                ffmpeg_exit(1);
	            }

	            if(ret > 0){
	                pkt.set_data(bit_buffer);
	                pkt.set_size(ret);
	                if (enc.get_coded_frame().get_pts() != AVUtil.AV_NOPTS_VALUE)
	                    pkt.set_pts(Mathematics.av_rescale_q(enc.get_coded_frame().get_pts(), 
	                    		                             enc.get_time_base(), 
	                    		                             ost.get_st().get_time_base()));
	/*av_log(NULL, AV_LOG_DEBUG, "encoder -> %"PRId64"/%"PRId64"\n",
	   pkt.pts != AV_NOPTS_VALUE ? av_rescale(pkt.pts, enc->time_base.den, AV_TIME_BASE*(int64_t)enc->time_base.num) : -1,
	   pkt.dts != AV_NOPTS_VALUE ? av_rescale(pkt.dts, enc->time_base.den, AV_TIME_BASE*(int64_t)enc->time_base.num) : -1);*/

	                if (enc.get_coded_frame().get_key_frame() != 0)
	                    pkt.add_flag(AVCodec.AV_PKT_FLAG_KEY);
	                write_frame(s, pkt, ost.get_st().get_codec(), ost.get_bitstream_filters());
	                frame_size = ret;
	                video_size += ret;
	                //fprintf(stderr,"\nFrame: %3d size: %5d type: %d",
	                //        enc->frame_number-1, ret, enc->pict_type);
	                /* if two pass, output log */
	                if ( (ost.get_logfile() != null) && (enc.get_stats_out() != null) ) {
	                	try {
	                		FileWriter writer = new FileWriter(ost.get_logfile());
	                	    writer.write(enc.get_stats_out(), 0, enc.get_stats_out().length());
	                	} catch (IOException x) {
	                	    x.printStackTrace();
	                	}
	                }
	            }
	        }
	        ost.set_sync_opts(ost.get_sync_opts()+1);
	        ost.set_frame_number(ost.get_frame_number()+1);
	    }
	    
	    
	    return frame_size;
	}


	private static void do_audio_out(AVFormatContext s, AVOutputStream ost,
			AVInputStream ist, short [] buf, int size) {
		short [] buftmp;
	    int audio_out_size, audio_buf_size;
	    long allocated_for_size = size;

	    int size_out, frame_bytes, ret, resample_changed;
	    AVCodecContext enc= ost.get_st().get_codec();
	    AVCodecContext dec= ist.get_st().get_codec();
	    int osize = SampleFmt.av_get_bytes_per_sample(enc.get_sample_fmt());
	    int isize = SampleFmt.av_get_bytes_per_sample(dec.get_sample_fmt());
	    int coded_bps = UtilsCodec.av_get_bits_per_sample(enc.get_codec().get_id());

//	need_realloc:
	    audio_buf_size = (int) ((allocated_for_size + isize * dec.get_channels() - 1) / (isize * dec.get_channels()));
	    audio_buf_size = (audio_buf_size * enc.get_sample_rate() + dec.get_sample_rate()) / dec.get_sample_rate();
	    audio_buf_size = audio_buf_size*2 + 10000; //safety factors for the deprecated resampling API
	    audio_buf_size = (int) Mathematics.FFMAX(audio_buf_size, enc.get_frame_size());
	    audio_buf_size *= osize * enc.get_channels();

	    audio_out_size = (int) Mathematics.FFMAX(audio_buf_size, enc.get_frame_size() * osize * enc.get_channels());
	    if (coded_bps > 8 * osize)
	        audio_out_size = audio_out_size * coded_bps / (8 * osize);
	    audio_out_size += AVCodec.FF_MIN_BUFFER_SIZE;

	    if ( (audio_out_size > Integer.MAX_VALUE) || (audio_buf_size > Integer.MAX_VALUE) ){
	        System.err.println("Buffer sizes too large");
	        ffmpeg_exit(1);
	    }

	    audio_buf = new short[(int)audio_buf_size];
	    allocated_audio_buf_size = (int)audio_buf_size;
	    audio_out = new short[(int)audio_out_size];
	    allocated_audio_out_size = (int)audio_out_size;
	    
//	    if (!audio_buf || !audio_out){
//	        fprintf(stderr, "Out of memory in do_audio_out\n");
//	        ffmpeg_exit(1);
//	    }

	    if (enc.get_channels() != dec.get_channels())
	        ost.set_audio_resample(1);

	    resample_changed = ( (ost.get_resample_sample_fmt()  != dec.get_sample_fmt()) ||
	                       	 (ost.get_resample_channels()    != dec.get_channels())   ||
	                         (ost.get_resample_sample_rate() != dec.get_sample_rate()) ) ? 1 : 0;

	    if ( ( (ost.get_audio_resample() != 0) && (ost.get_resample() == null) ) || (resample_changed!= 0) ) {
	        if (resample_changed != 0) {
	            Log.av_log(null, Log.AV_LOG_INFO, String.format("Input stream #%d.%d frame changed from rate:%d fmt:%s ch:%d to rate:%d fmt:%s ch:%d",
	                   ist.get_file_index(), ist.get_st().get_index(),
	                   ost.get_resample_sample_rate(), SampleFmt.av_get_sample_fmt_name(ost.get_resample_sample_fmt()), ost.get_resample_channels(),
	                   dec.get_sample_rate(), SampleFmt.av_get_sample_fmt_name(dec.get_sample_fmt()), dec.get_channels()));
	            ost.set_resample_sample_fmt(dec.get_sample_fmt());
	            ost.set_resample_channels(dec.get_channels());
	            ost.set_resample_sample_rate(dec.get_sample_rate());
	            if (ost.get_resample() != null) {
	            	ost.get_resample().audio_resample_close();
	            	ost.set_resample(null);
	            }
	            
	        }
	        /* if audio_sync_method is >1 the resampler is needed for audio drift compensation */
	        if ( (audio_sync_method <= 1) &&
	             (ost.get_resample_sample_fmt()  == enc.get_sample_fmt()) &&
	             (ost.get_resample_channels()    == enc.get_channels())   &&
	             (ost.get_resample_sample_rate() == enc.get_sample_rate()) ) {
	        	ost.set_resample(null);
             	ost.set_audio_resample(0);
	        } else {
	            if (dec.get_sample_fmt() != AVSampleFormat.AV_SAMPLE_FMT_S16)
	                System.err.println("Warning, using s16 intermediate sample format for resampling");
	            ost.set_resample(ResampleContext.av_audio_resample_init(enc.get_channels(), dec.get_channels(),
                                       enc.get_sample_rate(), dec.get_sample_rate(),
                                       enc.get_sample_fmt(), dec.get_sample_fmt(),
                                       16, 10, 0, 0.8));
	            if (ost.get_resample() == null) {
	                System.err.println(String.format("Can not resample %d channels @ %d Hz to %d channels @ %d Hz",
	                        dec.get_channels(), dec.get_sample_rate(),
	                        enc.get_channels(), enc.get_sample_rate()));
	                ffmpeg_exit(1);
	            }
	        }
	    }

	    if ( (ost.get_audio_resample() == 0) && 
	    	 (dec.get_sample_fmt() != enc.get_sample_fmt()) &&
	         (MAKE_SFMT_PAIR(enc.get_sample_fmt(), dec.get_sample_fmt()) != ost.get_reformat_pair()) ) {
	        if (ost.get_reformat_ctx() != null)
	        	ost.get_reformat_ctx().av_audio_convert_free();
	        ost.set_reformat_ctx(AudioConvert.av_audio_convert_alloc(enc.get_sample_fmt(), 1,
							dec.get_sample_fmt(), 1, null, 0));
	        if (ost.get_reformat_ctx() == null) {
	            System.err.println(String.format("Cannot convert %s sample format to %s sample format",
	                SampleFmt.av_get_sample_fmt_name(dec.get_sample_fmt()),
	                SampleFmt.av_get_sample_fmt_name(enc.get_sample_fmt())));
	            ffmpeg_exit(1);
	        }
	        ost.set_reformat_pair(MAKE_SFMT_PAIR(enc.get_sample_fmt(), dec.get_sample_fmt()));
	    }

	    if (audio_sync_method != 0){
	        double delta = get_sync_ipts(ost) * enc.get_sample_rate() - ost.get_sync_opts()
	                - ost.get_fifo().av_fifo_size() / (enc.get_channels() * 2);
	        double idelta = delta * dec.get_sample_rate() / enc.get_sample_rate();
	        int byte_delta = ((int)idelta) * 2 * dec.get_channels();

	        //FIXME resample delay
	        if (Math.abs(delta) > 50){
	            if( (ist.get_is_start() != 0) || 
	            	(Math.abs(delta) > audio_drift_threshold * enc.get_sample_rate()) ) {
	                if (byte_delta < 0){
	                    byte_delta = (int)Mathematics.FFMAX(byte_delta, -size);
	                    size += byte_delta;
	                   // buf  -= byte_delta;
	                    if(verbose > 2)
	                        System.err.println("discarding " + (int)-delta + " audio samples");
	                    if (size == 0)
	                        return;
	                    ist.set_is_start(0);
	                } else {
	                	short [] input_tmp = new short[byte_delta + size];

	                    /*if (byte_delta > allocated_for_size - size){
	                        allocated_for_size= byte_delta + (int64_t)size;
	                        goto need_realloc;
	                    }*/
	                    ist.set_is_start(0);

	                    buf = input_tmp;
	                    size += byte_delta;
	                    if(verbose > 2)
	                    	System.err.println("adding " + (int)delta + " audio samples of silence\n");
	                }
	            } else if (audio_sync_method > 1){
	                int comp = Common.av_clip((int)delta, -audio_sync_method, audio_sync_method);
	                if(verbose > 2)
	                    System.err.println(String.format("compensating audio timestamp drift:%f compensation:%d in:%d\n", 
	                    		delta, comp, enc.get_sample_rate()));
//System.err.println("drift:%f len:%d opts:%"PRId64" ipts:%"PRId64" fifo:%d\n", delta, -1, ost.get_sync_opts, (int64_t)(get_sync_ipts(ost) * enc.get_sample_rate), av_fifo_size(ost.get_fifo)/(ost.get_st().get_codec().get_channels * 2));
	                ost.get_resample().get_resample_context().av_resample_compensate(comp, enc.get_sample_rate());
	            }
	        }
	    } else
	        ost.set_sync_opts(Mathematics.lrintf(get_sync_ipts(ost) * enc.get_sample_rate())
	        - ost.get_fifo().av_fifo_size() / (enc.get_channels() * 2)); //FIXME wrong

	    if (ost.get_audio_resample() != 0) {
	        buftmp = audio_buf;
	        OutOI ret_obj =  ost.get_resample().audio_resample(UtilsArrays.byte_to_short_be(buf),
                    size / (dec.get_channels() * isize));
	        buftmp = UtilsArrays.short_to_byte_be((short[])ret_obj.get_obj());
	        size_out = ret_obj.get_ret();
	        
	        size_out = size_out * enc.get_channels() * osize;
	    } else {
	        buftmp = buf;
	        size_out = size;
	    }

	    if ( (ost.get_audio_resample() ==0) && 
	    	 (dec.get_sample_fmt() != enc.get_sample_fmt()) ) {
	    	short [][] ibuf = {buftmp};
	    	short [][] obuf = {audio_buf};
	        int [] istride = {isize};
	        int [] ostride = {osize};
	        int len = size_out / istride[0];
	        if (ost.get_reformat_ctx().av_audio_convert(UtilsArrays.byte_to_short_be(obuf), 
	        		ostride, UtilsArrays.byte_to_short_be(ibuf), istride, len)<0) {
	            System.err.println("av_audio_convert() failed");
	            if (exit_on_error != 0)
	                ffmpeg_exit(1);
	            return;
	        }
	        buftmp = audio_buf;
	        size_out = len * osize;
	    }

	    /* now encode as many frames as possible */
	    if (enc.get_frame_size() > 1) {
	        /* output resampled raw samples */
	        if (ost.get_fifo().av_fifo_realloc2(ost.get_fifo().av_fifo_size() + size_out) < 0) {
	            System.err.println("av_fifo_realloc2() failed");
	            ffmpeg_exit(1);
	        }
	        ost.get_fifo().av_fifo_generic_write(buftmp, size_out, null);

	        frame_bytes = enc.get_frame_size() * osize * enc.get_channels();

	        while (ost.get_fifo().av_fifo_size() >= frame_bytes) {
	        	AVPacket pkt = new AVPacket();
		    	pkt.av_init_packet();
		    	

	            audio_buf = ost.get_fifo().av_fifo_generic_read(frame_bytes, null);

	            //FIXME pass ost.get_sync_opts as AVFrame.pts in avcodec_encode_audio()

	            OutOI ret_obj = enc.avcodec_encode_audio(audio_out, audio_out_size, UtilsArrays.byte_to_short_be(audio_buf));
	            ret = ret_obj.get_ret();
	            audio_out = (short[])ret_obj.get_obj();
	            audio_out_size = audio_out.length;
	            if (ret < 0) {
	            	System.err.println("Audio encoding failed");
	                ffmpeg_exit(1);
	            }
	            audio_size += ret;
	            pkt.set_stream_index(ost.get_index());
	            pkt.set_data(audio_out);
	            pkt.set_size(ret);
	            if ( (enc.get_coded_frame() != null) && 
	            	 (enc.get_coded_frame().get_pts() != AVUtil.AV_NOPTS_VALUE) )
	                pkt.set_pts(Mathematics.av_rescale_q(enc.get_coded_frame().get_pts(), 
	                		enc.get_time_base(), ost.get_st().get_time_base()));
	            pkt.add_flag(AVCodec.AV_PKT_FLAG_KEY);
	            write_frame(s, pkt, enc, ost.get_bitstream_filters());

	            ost.set_sync_opts(ost.get_sync_opts() + enc.get_frame_size());
	        }
	    } //else {
//	        AVPacket pkt;
//	        av_init_packet(&pkt);
//
//	        ost.get_sync_opts += size_out / (osize * enc.get_channels);
//
//	        /* output a pcm frame */
//	        /* determine the size of the coded buffer */
//	        size_out /= osize;
//	        if (coded_bps)
//	            size_out = size_out*coded_bps/8;
//
//	        if(size_out > audio_out_size){
//	            fprintf(stderr, "Internal error, buffer size too small\n");
//	            ffmpeg_exit(1);
//	        }
//
//	        //FIXME pass ost.get_sync_opts as AVFrame.pts in avcodec_encode_audio()
//	        ret = avcodec_encode_audio(enc, audio_out, size_out,
//	                                   (short *)buftmp);
//	        if (ret < 0) {
//	            fprintf(stderr, "Audio encoding failed\n");
//	            ffmpeg_exit(1);
//	        }
//	        audio_size += ret;
//	        pkt.stream_index= ost.get_index;
//	        pkt.data= audio_out;
//	        pkt.size= ret;
//	        if(enc.get_coded_frame && enc.get_coded_frame.get_pts != AV_NOPTS_VALUE)
//	            pkt.pts= av_rescale_q(enc.get_coded_frame.get_pts, enc.get_time_base, ost.get_st().get_time_base);
//	        pkt.flags |= AV_PKT_FLAG_KEY;
//	        write_frame(s, &pkt, enc, ost.get_bitstream_filters);
//	    }
		
	}


	private static void write_frame(AVFormatContext s, AVPacket pkt,
			AVCodecContext avctx, AVBitStreamFilterContext bsfc) {
		    int ret;

		    while (bsfc != null){
		        AVPacket new_pkt = new AVPacket(pkt);
		        OutOI ret_obj = bsfc.av_bitstream_filter_filter(avctx, "",
	                    pkt.get_data(), pkt.get_flags() & AVCodec.AV_PKT_FLAG_KEY); 
		        int a = ret_obj.get_ret();
		        new_pkt.set_data((short[]) ret_obj.get_obj());
		        if (a > 0) {
		            pkt.av_free_packet();
		        } else if (a < 0){
		            System.err.println(String.format("%s failed for stream %d, codec %s",
		                    bsfc.get_filter().get_name(), pkt.get_stream_index(),
		                    (avctx.get_codec() != null)  ? avctx.get_codec().get_name() : "copy"));
		            print_error("", a);
		            if (exit_on_error != 0)
		                ffmpeg_exit(1);
		        }
		        pkt = new_pkt;

		        bsfc = bsfc.get_next();
		    }

		    ret = s.av_interleaved_write_frame(pkt);
		    if(ret < 0) {
		        print_error("av_interleaved_write_frame()", ret);
		        ffmpeg_exit(1);
		    }
		}



	private static double get_sync_ipts(AVOutputStream ost) {
		AVInputStream ist = ost.get_sync_ist();
	    return (double)(ist.get_pts() - start_time) / AVUtil.AV_TIME_BASE;
	}


	private static void usleep(long l) {
		try {
	 	   Thread.sleep(l / 1000);
	    } catch (InterruptedException e) {
	 	   e.printStackTrace();
	    }
	}


	private static byte[] pre_process_video_frame(AVInputStream ist,
			AVPicture picture) {
		// TODO Auto-generated method stub
		return null;
	}

	static long last_time = -1;
    static int [] qp_histogram = null;
    
	private static void print_report(ArrayList<AVFormatContext> output_files,
			AVOutputStream[] ost_table, int nb_ostreams, int is_last_report) {
		String buf = "";
	    AVOutputStream ost;
	    AVFormatContext oc;
	    long total_size;
	    AVCodecContext enc;
	    int frame_number, vid, i;
	    double bitrate;
	    long pts = Long.MAX_VALUE;
	    if (qp_histogram == null)
	    	qp_histogram = new int[52];
	    
	    if (is_last_report == 0) {
	        long cur_time;
	        /* display the report every 0.5 seconds */
	        cur_time = UtilsFormat.av_gettime();
	        if (last_time == -1) {
	            last_time = cur_time;
	            return;
	        }
	        if ((cur_time - last_time) < 500000)
	            return;
	        last_time = cur_time;
	    }


	    oc = output_files.get(0);

	    total_size = oc.get_pb().get_size();
	    if (total_size < 0) // FIXME improve avio_size() so it works with non seekable output too
	        total_size = oc.get_pb().tell();

	    vid = 0;
	    for (i = 0 ; i < nb_ostreams ; i++) {
	        float q = -1;
	        ost = ost_table[i];
	        enc = ost.get_st().get_codec();
	        if ( (!ost.get_st().get_stream_copy()) && (enc.get_coded_frame() != null) )
	            q = enc.get_coded_frame().get_quality() / (float)AVUtil.FF_QP2LAMBDA;
	        if ( (vid != 0) && (enc.get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) ) {
	            buf += String.format("q=%2.1f ", q);
	        }
	        if ( (vid == 0) && (enc.get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) ) {
	            float t = (UtilsFormat.av_gettime() - timer_start) / 1000000.0f;

	            frame_number = ost.get_frame_number();
	            buf += String.format("frame=%5d fps=%3d q=%3.1f ",
                     frame_number, (t>1)?(int)(frame_number / t + 0.5) : 0, q);
	            if (is_last_report != 0)
	                buf += "L";
	            if (qp_hist != 0) {
	                int j;
	                int qp = Mathematics.lrintf(q);
	                if( (qp >= 0) && (qp < qp_histogram.length) )
	                    qp_histogram[qp]++;
	                for (j = 0 ; j < 32 ; j++)
	                    buf += String.format("%X", (int)Mathematics.lrintf(Math.log(qp_histogram[j]+1)/Math.log(2)));
	            }
	            if (enc.has_flag(AVCodec.CODEC_FLAG_PSNR)) {
	                int j;
	                double error, error_sum=0;
	                double scale, scale_sum=0;
	                char [] type= {'Y','U','V'};
	                buf += "PSNR=";
	                for (j = 0 ; j < 3 ; j++){
	                    if (is_last_report != 0) {
	                        error = enc.get_error(j);
	                        scale = enc.get_width() * enc.get_height() * 255.0 * 255.0 * frame_number;
	                    } else {
	                        error = enc.get_coded_frame().get_error(j);
	                        scale = enc.get_width() * enc.get_height() * 255.0 * 255.0;
	                    }
	                    if (j!= 0)
	                    	scale /= 4;
	                    error_sum += error;
	                    scale_sum += scale;
	                    buf += String.format("%c:%2.2f ", type[j], psnr(error/scale));
	                }
	                buf += String.format("*:%2.2f ", psnr(error_sum / scale_sum));
	            }
	            vid = 1;
	        }
	        /* compute min output value */
	        pts = Mathematics.FFMIN(pts, 
	        						Mathematics.av_rescale_q(ost.get_st().get_pts().get_val(),
	        											     ost.get_st().get_time_base(), 
	        											     AVUtil.AV_TIME_BASE_Q));
	    }

	    if ( (verbose > 0) || (is_last_report != 0) ) {
	        int hours, mins, secs, us;
	        secs = (int) pts / AVUtil.AV_TIME_BASE;
	        us = (int) pts % AVUtil.AV_TIME_BASE;
	        mins = secs / 60;
	        secs %= 60;
	        hours = mins / 60;
	        mins %= 60;

	        bitrate = (pts != 0) ? total_size * 8 / (pts / 1000.0) : 0;

	        buf += String.format("size=%8.0fkB time=", total_size / 1024.0);
	        buf += String.format("%02d:%02d:%02d.%02d ", hours, mins, secs, (100 * us) / AVUtil.AV_TIME_BASE);
	        buf += String.format("bitrate=%6.1fkbits/s", bitrate);

	        if ( (nb_frames_dup != 0)|| (nb_frames_drop != 0) )
	        	buf += String.format(" dup=%d drop=%d", nb_frames_dup, nb_frames_drop);

	        if (verbose >= 0)
	            System.err.println(String.format("%s    \r", buf));

	    }

	    if ( (is_last_report != 0) && (verbose >= 0) ) {
	        long raw = audio_size + video_size + extra_size;
	        System.err.println("\n");
	        System.err.println(String.format("video:%1.0fkB audio:%1.0fkB global headers:%1.0fkB muxing overhead %f%%\n",
	                video_size/1024.0,
	                audio_size/1024.0,
	                extra_size/1024.0,
	                100.0*(total_size - raw)/raw
	        ));
	    }		
	}


	private static Object psnr(double d) {
	    return -10.0 * Math.log(d) / Math.log(10.0);
		
	}


	private static void copy_chapters(int infile, int outfile) {
	    AVFormatContext is = input_files.get(infile).get_ctx();
	    AVFormatContext os = output_files.get(outfile);
	    int i;

	    for (i = 0; i < is.get_nb_chapters() ; i++) {
	        AVChapter in_ch = is.get_chapter(i);
	        AVChapter out_ch;
	        long ts_off   = Mathematics.av_rescale_q(start_time - input_files.get(infile).get_ts_offset(),
	                                      		     AVUtil.AV_TIME_BASE_Q, in_ch.get_time_base());
	        long rt       = (recording_time == Long.MAX_VALUE) ? Long.MAX_VALUE :
	        	Mathematics.av_rescale_q(recording_time, AVUtil.AV_TIME_BASE_Q, in_ch.get_time_base());


	        if (in_ch.get_end() < ts_off)
	            continue;
	        if ( (rt != Long.MAX_VALUE) && 
	        	 (in_ch.get_start() > rt + ts_off) )
	            break;

	        out_ch = new AVChapter();

	        out_ch.set_id(in_ch.get_id());
	        out_ch.set_time_base(in_ch.get_time_base());
	        out_ch.set_start(Mathematics.FFMAX(0,  in_ch.get_start() - ts_off));
	        out_ch.set_end(Mathematics.FFMIN(rt, in_ch.get_end()   - ts_off));

	        if (metadata_chapters_autocopy != 0)
	            AVDictionary.av_dict_copy(out_ch.get_metadata(), in_ch.get_metadata(), 0);

	        os.add_chapter(out_ch);
	    }
	}


	private static OutIS METADATA_CHECK_INDEX(int index, int nb_elems, String desc) {
		int ret = 0;
		if ( (index < 0) || (index >= nb_elems) ) {
			return new OutIS(Error.AVERROR(Error.EINVAL),
					          String.format("Invalid %s index %d while processing metadata maps", desc, index));
		}
		return null;
	}
	


	private static void choose_pixel_fmt(AVStream st, AVCodec codec) {
	    if ( (codec != null) && 
	    	 (codec.get_pix_fmts() != null) ) {
	        ArrayList<PixelFormat> p = codec.get_pix_fmts();
	        if (st.get_codec().get_strict_std_compliance() <= AVCodec.FF_COMPLIANCE_UNOFFICIAL) {
	            if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_MJPEG) {
	                p = new ArrayList<PixelFormat>();
	                p.add(PixelFormat.PIX_FMT_YUVJ420P);
	                p.add(PixelFormat.PIX_FMT_YUVJ422P);
	                p.add(PixelFormat.PIX_FMT_YUV420P);
	                p.add(PixelFormat.PIX_FMT_YUV422P);
	                p.add(PixelFormat.PIX_FMT_NONE);
	            } else if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_LJPEG) {
	                p = new ArrayList<PixelFormat>();
	                p.add(PixelFormat.PIX_FMT_YUVJ420P);
	                p.add(PixelFormat.PIX_FMT_YUVJ422P);
	                p.add(PixelFormat.PIX_FMT_YUVJ444P);
	                p.add(PixelFormat.PIX_FMT_YUV420P);
	                p.add(PixelFormat.PIX_FMT_YUV422P);
	                p.add(PixelFormat.PIX_FMT_BGRA);
	                p.add(PixelFormat.PIX_FMT_NONE);	            	
	            }
	        }
	        int i = 0;
	        for (i = 0 ; i < p.size() ; i++) {
	        	if (p.get(i) == st.get_codec().get_pix_fmt())
	        		break;
	        }
	        if (i == p.size()) { // not found
	        	if (st.get_codec().get_pix_fmt() != PixelFormat.PIX_FMT_NONE)
	                Log.av_log(null, Log.AV_LOG_WARNING,
	                		"Incompatible pixel format '%s' for codec '%s', auto-selecting format '%s'\n",
	                				PixDesc.av_pix_fmt_descriptors.get(st.get_codec().get_pix_fmt()).get_name(),
	                				codec.get_name(),
	                				PixDesc.av_pix_fmt_descriptors.get(codec.get_pix_fmt(0)).get_name());
	            st.get_codec().set_pix_fmt(codec.get_pix_fmt(0));
	        }
	    }		
	}


	private static void choose_sample_rate(AVStream st, AVCodec codec) {
	    if ( (codec != null) && 
	    	 (codec.get_supported_samplerates() != null) ){
	        int best = 0;
	        int best_dist = Integer.MAX_VALUE;
	        for (int p : codec.get_supported_samplerates()) {
	            int dist= Math.abs(st.get_codec().get_sample_rate() - p);
	            if(dist < best_dist){
	                best_dist = dist;
	                best = p;
	            }
	        }
	        if (best_dist != 0) {
	            Log.av_log("codecCtx", Log.AV_LOG_WARNING, "Requested sampling rate unsupported using closest supported (%d)\n", best);
	        }
	        st.get_codec().set_sample_rate(best);
	    }
	}


	private static int MAKE_SFMT_PAIR(AVSampleFormat a, AVSampleFormat b) {
		return a.ordinal() + AVSampleFormat.values().length * b.ordinal();
	}


	private static long getutime() {
		return UtilsFormat.av_gettime();
	}
	

	

}
