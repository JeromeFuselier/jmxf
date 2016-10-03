package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class VsrcBuffer extends AVFilter {

	
	public VsrcBuffer() {
		super();
		this.name = "buffer";
		this.description = "Buffer video frames, and make them accessible to the filterchain.";
		
		this.inputs.add(new AVFilterPad(null));
		
		this.outputs.add(new AVFilterPadVsrc("default", AVMediaType.AVMEDIA_TYPE_VIDEO));
		this.outputs.add(new AVFilterPad(null));
	}
	

    int query_formats(AVFilterContext ctx) {
        BufferSourceContext c = (BufferSourceContext) ctx.get_priv();
        ArrayList<Long> pix_fmts = new ArrayList<Long>();
        pix_fmts.add((long)c.get_pix_fmt().ordinal());

        Defaults.avfilter_set_common_pixel_formats(ctx, Formats.avfilter_make_format_list(pix_fmts));
        return 0;
    }
    
    int init(AVFilterContext ctx, String args, Object opaque) {
        BufferSourceContext c = new BufferSourceContext();
        ctx.set_priv(c);
	    String pix_fmt_str;
	    int n = 0;
	    c.set_sws_param("");
	    
	    Pattern p = Pattern.compile("(\\d+):(\\d+):([^:]{0,127}):(\\d+):(\\d+):(\\d+):(\\d+):?(.{0,255})");
	    Matcher m = p.matcher(args);
	    if (m.matches()) {
		    c.set_w(Integer.parseInt(m.group(1)));
		    c.set_h(Integer.parseInt(m.group(2)));
		    pix_fmt_str = m.group(3);
		    c.get_time_base().set_num(Integer.parseInt(m.group(4)));
		    c.get_time_base().set_den(Integer.parseInt(m.group(5)));
		    c.get_sample_aspect_ratio().set_num(Integer.parseInt(m.group(6)));
		    c.get_sample_aspect_ratio().set_den(Integer.parseInt(m.group(7)));
		    c.set_sws_param(m.group(8));
	    } else {
	    	 Log.av_log("buffer", Log.AV_LOG_ERROR, "Expected at least 7 arguments in " + args);
	         return Error.AVERROR(Error.EINVAL);
	    }
	    

	    c.set_pix_fmt(PixDesc.av_get_pix_fmt(pix_fmt_str));
	    
	    
	    if (c.get_pix_fmt() == PixelFormat.PIX_FMT_NONE) {
	    	try {
		    	c.set_pix_fmt(PixelFormat.values()[Integer.parseInt(pix_fmt_str)]);
			} catch (Exception e) {
				Log.av_log("buffer", Log.AV_LOG_ERROR, "Invalid pixel format string '" + pix_fmt_str + "'");
				return Error.AVERROR(Error.EINVAL);
			}
	    }

	    Log.av_log("buffer", Log.AV_LOG_INFO, String.format("w:%d h:%d pixfmt:%s tb:%d/%d sar:%d/%d sws_param:%s\n",
	           c.get_w(), c.get_h(), PixDesc.av_pix_fmt_descriptors.get(c.get_pix_fmt()).get_name(),
	           c.get_time_base().get_num(), c.get_time_base().get_den(),
	           c.get_sample_aspect_ratio().get_num(), c.get_sample_aspect_ratio().get_den(),
	           c.get_sws_param()));
	    return 0;
    }
    
	
    
    

}
