package uk.ac.liv.ffmpeg.libavutil;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class ParserClass extends AVClass {
	

	public ParserClass() {
		super();	
		class_name = "Eval";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();	
		log_level_offset_offset = "log_offset";
		parent_log_context_offset = "log_ctx";
			
	}
	
}
