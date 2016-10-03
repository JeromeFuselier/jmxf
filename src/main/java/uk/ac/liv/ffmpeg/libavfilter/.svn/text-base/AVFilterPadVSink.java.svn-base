package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;

public class AVFilterPadVSink extends AVFilterPad {
	

    public AVFilterPadVSink(String name, AVMediaType type, int min_perms) {
		super(name, type, min_perms);
	}

	int end_frame(AVFilterLink link) {
    	BufferSinkContext buf = (BufferSinkContext) link.get_dst().get_priv();
    	
	    if (buf.get_picref() != null)            /* drop the last cached frame */
	        buf.get_picref().avfilter_unref_buffer();
	    buf.set_picref(link.get_cur_buf());
	    return 0;
    }
    

}
