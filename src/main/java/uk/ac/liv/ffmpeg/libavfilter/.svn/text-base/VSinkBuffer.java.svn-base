package uk.ac.liv.ffmpeg.libavfilter;


import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;


public class VSinkBuffer extends AVFilter {
	
	public static final int AV_VSINK_BUF_FLAG_PEEK = 1;

	public static OutOI av_vsink_buffer_get_video_buffer_ref(AVFilterContext ctx,
			int flags) {
		AVFilterBufferRef picref = null;
		BufferSinkContext buf = (BufferSinkContext) ctx.get_priv();
	    AVFilterLink inlink = ctx.get_input(0);
	    int ret;

	    /* no picref available, fetch it from the filterchain */
	    if (buf.get_picref() == null) {
	        if ((ret = AVFilter.avfilter_request_frame(inlink)) < 0)
	            return new OutOI(picref, ret);
	    }

	    if (buf.get_picref() == null)
	        return new OutOI(picref, Error.AVERROR(Error.EINVAL));

	    picref = buf.get_picref();
	    if ((flags & AV_VSINK_BUF_FLAG_PEEK) == 0)
	        buf.set_picref(null);

	    return new OutOI(picref, 0);
	}


	
	public VSinkBuffer() {
		super();
		this.name = "buffersink";
		
		this.inputs.add(new AVFilterPadVSink("default", AVMediaType.AVMEDIA_TYPE_VIDEO, AVFilter.AV_PERM_READ));	
		this.inputs.add(new AVFilterPad(null));
		
		this.outputs.add(new AVFilterPad(null));
	}

    
    int init(AVFilterContext ctx, String args, Object opaque) {
        BufferSinkContext buf = new BufferSinkContext();
        ctx.set_priv(buf);
        
        if (opaque == null) {
            Log.av_log("buffersink", Log.AV_LOG_ERROR, "No opaque field provided, which is required.\n");
            return Error.AVERROR(Error.EINVAL);
        }

        //TODO Jerome
        buf.set_pix_fmts((ArrayList<PixelFormat>)opaque);
        return 0;    	
    }
    
    
    int uninit(AVFilterContext ctx) {
    	BufferSinkContext buf = (BufferSinkContext) ctx.get_priv();

        if (buf.get_picref() != null)
        	buf.get_picref().avfilter_unref_buffer();
        buf.set_picref(null);
        return 0;
    }


    int query_formats(AVFilterContext ctx) {
    	BufferSinkContext buf = (BufferSinkContext) ctx.get_priv();

    	ArrayList<Long> tmp = new ArrayList<Long>();
    	for (PixelFormat fmt : buf.get_pix_fmts())
    		tmp.add((long) fmt.ordinal());
    	ctx.avfilter_set_common_pixel_formats(Formats.avfilter_make_format_list(tmp));
    	
    	return 0;
    }
	
	
	
}
