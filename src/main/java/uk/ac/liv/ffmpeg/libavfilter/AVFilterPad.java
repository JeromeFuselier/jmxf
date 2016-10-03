package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;

public class AVFilterPad {
	
	 /**
     * Pad name. The name is unique among inputs and among outputs, but an
     * input may have the same name as an output. This may be NULL if this
     * pad has no need to ever be referenced by name.
     */
    String name;

    /**
     * AVFilterPad type. Only video supported now, hopefully someone will
     * add audio in the future.
     */
    AVMediaType type;

    /**
     * Minimum required permissions on incoming buffers. Any buffer with
     * insufficient permissions will be automatically copied by the filter
     * system to a new buffer which provides the needed access permissions.
     *
     * Input pads only.
     */
    int min_perms;

    /**
     * Permissions which are not accepted on incoming buffers. Any buffer
     * which has any of these permissions set will be automatically copied
     * by the filter system to a new buffer which does not have those
     * permissions. This can be used to easily disallow buffers with
     * AV_PERM_REUSE.
     *
     * Input pads only.
     */
    int rej_perms;
        
    
    public AVFilterPad(String name) {
    	this.name = name;
	}
    
    public AVFilterPad(String name, AVMediaType type) {
    	this.name = name;
    	this.type = type;
	}

    public AVFilterPad(String name, AVMediaType type, int min_perms) {
    	this.name = name;
    	this.type = type;
    	this.min_perms = min_perms;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public AVMediaType get_type() {
		return type;
	}

	public void set_type(AVMediaType type) {
		this.type = type;
	}

	public int get_min_perms() {
		return min_perms;
	}

	public void set_min_perms(int min_perms) {
		this.min_perms = min_perms;
	}

	public int get_rej_perms() {
		return rej_perms;
	}

	public void set_rej_perms(int rej_perms) {
		this.rej_perms = rej_perms;
	}

	/**
     * Callback called before passing the first slice of a new frame. If
     * NULL, the filter layer will default to storing a reference to the
     * picture inside the link structure.
     *
     * Input video pads only.
     */
    int start_frame(AVFilterLink link, AVFilterBufferRef picref) {
    	return -1;
    }

    /**
     * Callback function to get a video buffer. If NULL, the filter system will
     * use avfilter_default_get_video_buffer().
     *
     * Input video pads only.
     */
    AVFilterBufferRef get_video_buffer(AVFilterLink link, int perms, int w, int h) {
    	return null;
    }

    /**
     * Callback function to get an audio buffer. If NULL, the filter system will
     * use avfilter_default_get_audio_buffer().
     *
     * Input audio pads only.
     */
    AVFilterBufferRef get_audio_buffer(AVFilterLink link, int perms,
                                           AVSampleFormat sample_fmt, int nb_samples,
                                           long channel_layout, int planar) {
    	return null;
    }

    /**
     * Callback called after the slices of a frame are completely sent. If
     * NULL, the filter layer will default to releasing the reference stored
     * in the link structure during start_frame().
     *
     * Input video pads only.
     */
    int end_frame(AVFilterLink link) {
    	return -1;    	
    }

    /**
     * Slice drawing callback. This is where a filter receives video data
     * and should do its processing.
     *
     * Input video pads only.
     */
    int draw_slice(AVFilterLink link, int y, int height, int slice_dir) {
    	return -1;
    }

    /**
     * Samples filtering callback. This is where a filter receives audio data
     * and should do its processing.
     *
     * Input audio pads only.
     */
    int filter_samples(AVFilterLink link, AVFilterBufferRef samplesref) {
    	return -1;    	
    }

    /**
     * Frame poll callback. This returns the number of immediately available
     * samples. It should return a positive value if the next request_frame()
     * is guaranteed to return one frame (with no delay).
     *
     * Defaults to just calling the source poll_frame() method.
     *
     * Output video pads only.
     */
    int poll_frame(AVFilterLink link) {
    	return -1;    	
    }

    /**
     * Frame request callback. A call to this should result in at least one
     * frame being output over the given link. This should return zero on
     * success, and another value on error.
     *
     * Output video pads only.
     */
    int request_frame(AVFilterLink link) {
    	return -1;
    }

    /**
     * Link configuration callback.
     *
     * For output pads, this should set the link properties such as
     * width/height. This should NOT set the format property - that is
     * negotiated between filters by the filter system using the
     * query_formats() callback before this function is called.
     *
     * For input pads, this should check the properties of the link, and update
     * the filter's internal state as necessary.
     *
     * For both input and output filters, this should return zero on success,
     * and another value on error.
     */
    int config_props(AVFilterLink link) {
    	return -1;
    }

}
