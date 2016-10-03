package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Error;


public class AVFilterPadVsrc extends AVFilterPad {


	public AVFilterPadVsrc(String name) {
		super(name);
	}


	public AVFilterPadVsrc(String name, AVMediaType type) {
		super(name, type);
	}


	int request_frame(AVFilterLink link) {
        BufferSourceContext c = (BufferSourceContext) link.get_src().get_priv();

        if (c.get_picref() == null) {
            Log.av_log("AVFilterContext", Log.AV_LOG_WARNING,
                   "request_frame() called with no available frame!\n");
            return Error.AVERROR(Error.EINVAL);
        }

        AVFilter.avfilter_start_frame(link, AVFilter.avfilter_ref_buffer(c.get_picref(), ~0));
        AVFilter.avfilter_draw_slice(link, 0, link.get_h(), 1);
        AVFilter.avfilter_end_frame(link);
        AVFilter.avfilter_unref_buffer(c.get_picref());
        c.set_picref(null);

        return 0;
    }
    

    int config_props(AVFilterLink link) {
        BufferSourceContext c = (BufferSourceContext) link.get_src().get_priv();
        
        link.set_w(c.get_w());
        link.set_h(c.get_h());
        link.set_sample_aspect_ratio(c.get_sample_aspect_ratio());
        link.set_time_base(c.get_time_base());

        return 0;
    }
    
    
    int poll_frame(AVFilterLink link) {
        BufferSourceContext c = (BufferSourceContext) link.get_src().get_priv();
        return (c.get_picref() != null) ? 1 : 0;
    }
}
