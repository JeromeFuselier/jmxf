package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class AVFilterClass extends AVClass {
	

	public AVFilterClass() {
		super();	
		class_name = "AVFilter";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();	
			
	}

	public String item_name(Object obj) {
	    AVFilterContext filter = (AVFilterContext)obj;
	    return filter.get_filter().get_name();
	}
	

}
