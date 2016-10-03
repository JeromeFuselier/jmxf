package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavcodec.AVFrame;

public class AVCodec {
	

	public static AVFilterBufferRef avfilter_get_video_buffer_ref_from_frame(
			AVFrame frame, int perms) {
	    AVFilterBufferRef picref =
	        AVFilter.avfilter_get_video_buffer_ref_from_arrays(frame.get_data(), 
			        										   frame.get_linesize(), 
			        										   perms,
			                                                   frame.get_width(), 
			                                                   frame.get_height(),
			                                                   frame.get_formatV());
	    if (picref == null)
	        return null;
	    avfilter_copy_frame_props(picref, frame);
	    return picref;
	}

	private static void avfilter_copy_frame_props(AVFilterBufferRef dst,
			AVFrame src) {
	    dst.set_pts(src.get_pts());
	    dst.set_pos(src.get_pkt_pos());
	    dst.set_format(src.get_formatV());

	    switch (dst.get_type()) {
	    case AVMEDIA_TYPE_VIDEO:
	        dst.get_video().set_w(src.get_width());
	        dst.get_video().set_h(src.get_height());
	        dst.get_video().set_sample_aspect_ratio(src.get_sample_aspect_ratio());
	        dst.get_video().set_interlaced(src.get_interlaced_frame());
	        dst.get_video().set_top_field_first(src.get_top_field_first());
	        dst.get_video().set_key_frame(src.get_key_frame());
	        dst.get_video().set_pict_type(src.get_pict_type());
	    }		
	}

}
