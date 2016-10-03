package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class AVFilterLink {
	
	public static enum AVFilterLinkState {
		AVLINK_UNINIT,      ///< not started
        AVLINK_STARTINIT,       ///< started, but incomplete
        AVLINK_INIT  
	};
	
	

    AVFilterContext src;       ///< source filter
    AVFilterPad srcpad;        ///< output pad on the source filter

    AVFilterContext dst;       ///< dest filter
    AVFilterPad dstpad;        ///< input pad on the dest filter

    /** stage of the initialization of the link properties (dimensions, etc) */
    AVFilterLinkState init_state = AVFilterLinkState.AVLINK_UNINIT;

    AVMediaType type;      ///< filter media type

    /* These parameters apply only to video */
    int w;                      ///< agreed upon image width
    int h;                      ///< agreed upon image height
    AVRational sample_aspect_ratio = new AVRational(0, 0); ///< agreed upon sample aspect ratio
    /* These two parameters apply only to audio */
    long channel_layout;     ///< channel layout of current buffer (see libavutil/audioconvert.h)
    long sample_rate;        ///< samples per second

    PixelFormat format;                 ///< agreed upon media format

    /**
     * Lists of formats and channel layouts supported by the input and output
     * filters respectively. These lists are used for negotiating the format
     * to actually be used, which will be loaded into the format and
     * channel_layout members, above, when chosen.
     *
     */
    AVFilterFormats in_formats;
    AVFilterFormats out_formats;

    AVFilterFormats in_chlayouts;
    AVFilterFormats out_chlayouts;
    
    /**
     * The buffer reference currently being sent across the link by the source
     * filter. This is used internally by the filter system to allow
     * automatic copying of buffers which do not have sufficient permissions
     * for the destination. This should not be accessed directly by the
     * filters.
     */
    AVFilterBufferRef src_buf;

    AVFilterBufferRef cur_buf;
    AVFilterBufferRef out_buf;

    /**
     * Define the time base used by the PTS of the frames/samples
     * which will pass through this link.
     * During the configuration stage, each filter is supposed to
     * change only the output timebase, while the timebase of the
     * input link is assumed to be an unchangeable property.
     */
    AVRational time_base = new AVRational(0, 0);

    AVFilterPool pool;

	public long get_channel_layout() {
		return channel_layout;
	}

	public void set_channel_layout(long channel_layout) {
		this.channel_layout = channel_layout;
	}

	public AVFilterContext get_src() {
		return src;
	}

	public void set_src(AVFilterContext src) {
		this.src = src;
	}

	public AVFilterPad get_srcpad() {
		return srcpad;
	}

	public void set_srcpad(AVFilterPad srcpad) {
		this.srcpad = srcpad;
	}

	public AVFilterContext get_dst() {
		return dst;
	}

	public void set_dst(AVFilterContext dst) {
		this.dst = dst;
	}

	public AVFilterPad get_dstpad() {
		return dstpad;
	}

	public void set_dstpad(AVFilterPad dstpad) {
		this.dstpad = dstpad;
	}

	public AVFilterLinkState get_init_state() {
		return init_state;
	}

	public void set_init_state(AVFilterLinkState init_state) {
		this.init_state = init_state;
	}

	public AVMediaType get_type() {
		return type;
	}

	public void set_type(AVMediaType type) {
		this.type = type;
	}

	public int get_w() {
		return w;
	}

	public void set_w(int w) {
		this.w = w;
	}

	public int get_h() {
		return h;
	}

	public void set_h(int h) {
		this.h = h;
	}

	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}

	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}

	public long get_sample_rate() {
		return sample_rate;
	}

	public void set_sample_rate(long sample_rate) {
		this.sample_rate = sample_rate;
	}

	public PixelFormat get_format() {
		return format;
	}

	public void set_format(PixelFormat pixelFormat) {
		this.format = pixelFormat;
	}

	public AVFilterFormats get_in_formats() {
		return in_formats;
	}

	public void set_in_formats(AVFilterFormats in_formats) {
		this.in_formats = in_formats;
	}

	public AVFilterFormats get_out_formats() {
		return out_formats;
	}

	public void set_out_formats(AVFilterFormats out_formats) {
		this.out_formats = out_formats;
	}

	public AVFilterFormats get_in_chlayouts() {
		return in_chlayouts;
	}

	public void set_in_chlayouts(AVFilterFormats in_chlayouts) {
		this.in_chlayouts = in_chlayouts;
	}

	public AVFilterFormats get_out_chlayouts() {
		return out_chlayouts;
	}

	public void set_out_chlayouts(AVFilterFormats out_chlayouts) {
		this.out_chlayouts = out_chlayouts;
	}

	public AVFilterBufferRef get_src_buf() {
		return src_buf;
	}

	public void set_src_buf(AVFilterBufferRef src_buf) {
		this.src_buf = src_buf;
	}

	public AVFilterBufferRef get_cur_buf() {
		return cur_buf;
	}

	public void set_cur_buf(AVFilterBufferRef cur_buf) {
		this.cur_buf = cur_buf;
	}

	public AVFilterBufferRef get_out_buf() {
		return out_buf;
	}

	public void set_out_buf(AVFilterBufferRef out_buf) {
		this.out_buf = out_buf;
	}

	public AVRational get_time_base() {
		return time_base;
	}

	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
	}

	public AVFilterPool get_pool() {
		return pool;
	}

	public void set_pool(AVFilterPool pool) {
		this.pool = pool;
	}

	public void pick_format() {
	    if (in_formats == null)
	        return;

	    format = PixelFormat.values()[(int)in_formats.get_format(0)];
	    in_formats.set_formats(new ArrayList<Long>());
	    in_formats.avfilter_add_format(format);

	    in_formats.avfilter_formats_unref();
	    out_formats.avfilter_formats_unref();

	    if (type == AVMediaType.AVMEDIA_TYPE_AUDIO) {
	        channel_layout = in_chlayouts.get_format(0);
	        in_chlayouts.set_formats(new ArrayList<Long>());
	        in_chlayouts.avfilter_add_format(channel_layout);
	        in_chlayouts.avfilter_formats_unref();
	        out_chlayouts.avfilter_formats_unref();
	    }
		
	}

    

}
