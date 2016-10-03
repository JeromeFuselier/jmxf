package uk.ac.liv.ffmpeg;

import java.io.File;

import uk.ac.liv.ffmpeg.libavcodec.AVAudioConvert;
import uk.ac.liv.ffmpeg.libavcodec.AVBitStreamFilterContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.ResampleContext;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterBufferRef;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterContext;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterGraph;
import uk.ac.liv.ffmpeg.libavutil.AVFifoBuffer;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;

public class AVOutputStream {

	int file_index;          /* file index */
    int index;               /* stream index in the output file */
    int source_index;        /* Input_stream index */
    AVStream st;            /* stream in the output file */
    int encoding_needed;     /* true if encoding needed for this stream */
    int frame_number;
    /* input pts and corresponding output pts
       for A/V sync */
    //double sync_ipts;        /* dts from the AVPacket of the demuxer in second units */
    AVInputStream sync_ist; /* input stream to sync against */
    long sync_opts;       /* output frame counter, could be changed to some true timestamp */ //FIXME look at frame_number
    AVBitStreamFilterContext bitstream_filters;
    AVCodec enc;

    /* video only */
    int video_resample;
    AVFrame resample_frame;              /* temporary frame for image resampling */
  //  SwsContext img_resample_ctx; /* for image resampling */
    int resample_height;
    int resample_width;
    PixelFormat resample_pix_fmt;
    AVRational frame_rate;

    float frame_aspect_ratio;

    /* forced key frames */
    long [] forced_kf_pts;
    int forced_kf_count;
    int forced_kf_index;

    /* audio only */
    int audio_resample;
    ResampleContext resample; /* for audio resampling */
    AVSampleFormat resample_sample_fmt;
    int resample_channels;
    int resample_sample_rate;
    int reformat_pair;
    AVAudioConvert reformat_ctx;
    AVFifoBuffer fifo;     /* for compression: one audio fifo per codec */
    String logfile;

    AVFilterContext output_video_filter;
    AVFilterContext input_video_filter;
    AVFilterBufferRef picref;
    String avfilter;
    AVFilterGraph graph;

   int sws_flags;
   
   
	
	public AVFilterContext get_output_video_filter() {
		return output_video_filter;
	}
	
	public void set_output_video_filter(AVFilterContext output_video_filter) {
		this.output_video_filter = output_video_filter;
	}
	
	public AVFilterContext get_input_video_filter() {
		return input_video_filter;
	}
	
	public void set_input_video_filter(AVFilterContext input_video_filter) {
		this.input_video_filter = input_video_filter;
	}
	
	public AVFilterBufferRef get_picref() {
		return picref;
	}
	
	public void set_picref(AVFilterBufferRef picref) {
		this.picref = picref;
	}
	
	public String get_avfilter() {
		return avfilter;
	}
	
	public void set_avfilter(String avfilter) {
		this.avfilter = avfilter;
	}
	
	public AVFilterGraph get_graph() {
		return graph;
	}
	
	public void set_graph(AVFilterGraph graph) {
		this.graph = graph;
	}

	public int get_file_index() {
		return file_index;
	}
	
	public void set_file_index(int file_index) {
		this.file_index = file_index;
	}
	
	public int get_index() {
		return index;
	}
	
	public void set_index(int index) {
		this.index = index;
	}
	
	public int get_source_index() {
		return source_index;
	}
	
	public void set_source_index(int source_index) {
		this.source_index = source_index;
	}
	
	public AVStream get_st() {
		return st;
	}
	
	public void set_st(AVStream st) {
		this.st = st;
	}
	
	public int get_encoding_needed() {
		return encoding_needed;
	}
	
	public void set_encoding_needed(int encoding_needed) {
		this.encoding_needed = encoding_needed;
	}
	
	public int get_frame_number() {
		return frame_number;
	}
	
	public void set_frame_number(int frame_number) {
		this.frame_number = frame_number;
	}
	
	public AVInputStream get_sync_ist() {
		return sync_ist;
	}
	
	public void set_sync_ist(AVInputStream avInputStream) {
		this.sync_ist = avInputStream;
	}
	
	public long get_sync_opts() {
		return sync_opts;
	}
	
	public void set_sync_opts(long sync_opts) {
		this.sync_opts = sync_opts;
	}
	
	public AVBitStreamFilterContext get_bitstream_filters() {
		return bitstream_filters;
	}
	
	public void set_bitstream_filters(AVBitStreamFilterContext bitstream_filters) {
		this.bitstream_filters = bitstream_filters;
	}
	
	public AVCodec get_enc() {
		return enc;
	}
	
	public void set_enc(AVCodec enc) {
		this.enc = enc;
	}
	
	public int get_video_resample() {
		return video_resample;
	}
	
	public void set_video_resample(int video_resample) {
		this.video_resample = video_resample;
	}
	
	public AVFrame get_resample_frame() {
		return resample_frame;
	}
	
	public void set_resample_frame(AVFrame resample_frame) {
		this.resample_frame = resample_frame;
	}
	
	public int get_resample_height() {
		return resample_height;
	}
	
	public void set_resample_height(int resample_height) {
		this.resample_height = resample_height;
	}
	
	public int get_resample_width() {
		return resample_width;
	}
	
	public void set_resample_width(int resample_width) {
		this.resample_width = resample_width;
	}
	
	public PixelFormat get_resample_pix_fmt() {
		return resample_pix_fmt;
	}
	
	public void set_resample_pix_fmt(PixelFormat resample_pix_fmt) {
		this.resample_pix_fmt = resample_pix_fmt;
	}
	
	public AVRational get_frame_rate() {
		return frame_rate;
	}
	
	public void set_frame_rate(AVRational frame_rate) {
		this.frame_rate = frame_rate;
	}
	
	public float get_frame_aspect_ratio() {
		return frame_aspect_ratio;
	}
	
	public void set_frame_aspect_ratio(float frame_aspect_ratio) {
		this.frame_aspect_ratio = frame_aspect_ratio;
	}
	
	public long [] get_forced_kf_pts() {
		return forced_kf_pts;
	}
	
	public long get_forced_kf_pts(int i) {
		return forced_kf_pts[i];
	}
	
	public void set_forced_kf_pts(long [] forced_kf_pts) {
		this.forced_kf_pts = forced_kf_pts;
	}
	
	public int get_forced_kf_count() {
		return forced_kf_count;
	}
	
	public void set_forced_kf_count(int forced_kf_count) {
		this.forced_kf_count = forced_kf_count;
	}
	
	public int get_forced_kf_index() {
		return forced_kf_index;
	}
	
	public void set_forced_kf_index(int forced_kf_index) {
		this.forced_kf_index = forced_kf_index;
	}
	
	public int get_audio_resample() {
		return audio_resample;
	}
	
	public void set_audio_resample(int audio_resample) {
		this.audio_resample = audio_resample;
	}
	
	public ResampleContext get_resample() {
		return resample;
	}
	
	public void set_resample(ResampleContext resample) {
		this.resample = resample;
	}
	
	public AVSampleFormat get_resample_sample_fmt() {
		return resample_sample_fmt;
	}
	
	public void set_resample_sample_fmt(AVSampleFormat resample_sample_fmt) {
		this.resample_sample_fmt = resample_sample_fmt;
	}
	
	public int get_resample_channels() {
		return resample_channels;
	}
	
	public void set_resample_channels(int resample_channels) {
		this.resample_channels = resample_channels;
	}
	
	public int get_resample_sample_rate() {
		return resample_sample_rate;
	}
	
	public void set_resample_sample_rate(int resample_sample_rate) {
		this.resample_sample_rate = resample_sample_rate;
	}
	
	public int get_reformat_pair() {
		return reformat_pair;
	}
	
	public void set_reformat_pair(int reformat_pair) {
		this.reformat_pair = reformat_pair;
	}
	
	public AVAudioConvert get_reformat_ctx() {
		return reformat_ctx;
	}
	
	public void set_reformat_ctx(AVAudioConvert reformat_ctx) {
		this.reformat_ctx = reformat_ctx;
	}
	
	public AVFifoBuffer get_fifo() {
		return fifo;
	}
	
	public void set_fifo(AVFifoBuffer fifo) {
		this.fifo = fifo;
	}
	
	public String get_logfile() {
		return logfile;
	}
	
	public void set_logfile(String logfile) {
		this.logfile = logfile;
	}
	
	public int get_sws_flags() {
		return sws_flags;
	}
	
	public void set_sws_flags(int sws_flags) {
		this.sws_flags = sws_flags;
	}

	   
	   
}
