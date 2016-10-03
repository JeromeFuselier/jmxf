package uk.ac.liv.ffmpeg.libavfilter;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOOI;

public class Defaults {

	public static AVFilterBufferRef avfilter_default_get_video_buffer(
			AVFilterLink link, int perms, int w, int h) {
	    int [] linesize = new int [4];
	    short [][] data;
	    int i;
	    AVFilterBufferRef picref = null;
	    AVFilterPool pool = link.get_pool();

	    if (pool != null) {
	        for (i = 0; i < AVFilterPool.POOL_SIZE; i++) {
	            picref = pool.get_pic()[i];
	            if ( (picref != null) && 
	            	 (picref.get_buf().get_format() == link.get_format()) && 
	            	 (picref.get_buf().get_w() == w) && 
	            	 (picref.get_buf().get_h() == h) ) {
	                AVFilterBuffer pic = picref.get_buf();
	                pool.get_pic()[i] = null;
	                pool.set_count(pool.get_count() - 1);
	                picref.get_video().set_w(w);
	                picref.get_video().set_h(h);
	                picref.set_perms(perms | AVFilter.AV_PERM_READ);
	                picref.set_format(link.get_format());
	                pic.set_refcount(1);
	                picref.set_data(pic.get_data());
	                picref.set_linesize(pic.get_linesize());
	                return picref;
	            }
	        }
	    } else {
	        pool = new AVFilterPool();
	    	link.set_pool(pool);
	    }

	    // align: +2 is needed for swscaler, +16 to be SIMD-friendly
	    OutOOI ret_obj = ImgUtils.av_image_alloc(w, h, link.get_format(), 16);
	    data = (short [][])ret_obj.get_obj1();
	    linesize = (int[])ret_obj.get_obj2();
	    i = ret_obj.get_ret();
	    if (i < 0)
	        return null;

	    picref = AVFilter.avfilter_get_video_buffer_ref_from_arrays(data, linesize,
	                                                       perms, w, h, link.get_format());
	    if (picref == null) {
	        return null;
	    }
	    
	    picref.set_data(0, new short[i]);
	    Arrays.fill(picref.get_data(0), (short) 128);

	    picref.get_buf().set_priv(pool);

	    return picref;
	}

	public static void avfilter_set_common_pixel_formats(AVFilterContext ctx,
			AVFilterFormats formats) {
		ctx.avfilter_set_common_pixel_formats(formats);
	}

	public static int avfilter_default_query_formats(AVFilterContext ctx) {
		ctx.avfilter_set_common_pixel_formats(Formats.avfilter_all_formats(AVMediaType.AVMEDIA_TYPE_VIDEO));
		ctx.avfilter_set_common_sample_formats(Formats.avfilter_all_formats(AVMediaType.AVMEDIA_TYPE_AUDIO));
		ctx.avfilter_set_common_channel_layouts(Formats.avfilter_all_channel_layouts());

	    return 0;		
	}

	
	/**
	 * default config_link() implementation for output video links to simplify
	 * the implementation of one input one output video filters */
	public static int avfilter_default_config_output_link(AVFilterLink link) {
	    if (link.get_src().get_input_count() != 0) {
	        if (link.get_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
	            link.set_w(link.get_src().get_input(0).get_w());
	            link.set_h(link.get_src().get_input(0).get_h());
	            link.set_time_base(link.get_src().get_input(0).get_time_base());
	        } else if (link.get_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
	            link.set_channel_layout(link.get_src().get_input(0).get_channel_layout());
	            link.set_sample_rate(link.get_src().get_input(0).get_sample_rate());
	        }
	    } else {
	        /* XXX: any non-simple filter which would cause this branch to be taken
	         * really should implement its own config_props() for this link. */
	        return -1;
	    }

	    return 0;
	}

	public static void avfilter_default_start_frame(AVFilterLink inlink,
			AVFilterBufferRef picref) {
	    AVFilterLink outlink = null;

	    if (inlink.get_dst().get_output_count() != 0)
	        outlink = inlink.get_dst().get_output(0);

	    if (outlink != null) {
	        outlink.set_out_buf(AVFilter.avfilter_get_video_buffer(outlink, AVFilter.AV_PERM_WRITE, outlink.get_w(), outlink.get_h()));
	        AVFilter.avfilter_copy_buffer_ref_props(outlink.get_out_buf(), picref);
	        AVFilter.avfilter_start_frame(outlink, AVFilter.avfilter_ref_buffer(outlink.get_out_buf(), ~0));
	    }
	}

	public static void avfilter_default_end_frame(AVFilterLink inlink) {
	    AVFilterLink outlink = null;

	    if (inlink.get_dst().get_output_count() != 0)
	        outlink = inlink.get_dst().get_output(0);

	    inlink.get_cur_buf().avfilter_unref_buffer();
	    inlink.set_cur_buf(null);

	    if (outlink != null) {
	        if (outlink.get_out_buf() != null) {
	        	outlink.get_out_buf().avfilter_unref_buffer();
	        	outlink.set_out_buf(null);
	        }
	        AVFilter.avfilter_end_frame(outlink);
	    }
	}

}
