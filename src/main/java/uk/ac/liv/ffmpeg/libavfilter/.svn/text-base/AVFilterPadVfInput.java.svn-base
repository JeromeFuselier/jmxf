package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;

public class AVFilterPadVfInput extends AVFilterPad {

	
	public AVFilterPadVfInput(String name, AVMediaType type, int min_perms) {
		super(name, type, min_perms);
	}

	
	public AVFilterPadVfInput(String name, AVMediaType type) {
		super(name, type);
	}

	
	public AVFilterPadVfInput(String name) {
		super(name);
	}
	

    int start_frame(AVFilterLink link, AVFilterBufferRef picref) {
        ScaleContext scale = (ScaleContext) link.get_dst().get_priv();
        AVFilterLink outlink = link.get_dst().get_output(0);
        AVFilterBufferRef outpicref;

        scale.set_hsub(PixDesc.av_pix_fmt_descriptors.get(link.get_format()).get_log2_chroma_w());
        scale.set_vsub(PixDesc.av_pix_fmt_descriptors.get(link.get_format()).get_log2_chroma_h());

        outpicref = AVFilter.avfilter_get_video_buffer(outlink, AVFilter.AV_PERM_WRITE, outlink.get_w(), outlink.get_h());
        AVFilter.avfilter_copy_buffer_ref_props(outpicref, picref);
        outpicref.get_video().set_w(outlink.get_w());
        outpicref.get_video().set_h(outlink.get_h());

        outlink.set_out_buf(outpicref);

        outpicref.get_video().set_sample_aspect_ratio(AVRational.av_reduce(
        		picref.get_video().get_sample_aspect_ratio().get_num() * outlink.get_h() * link.get_w(),
        		picref.get_video().get_sample_aspect_ratio().get_den() * outlink.get_w() * link.get_h(),
        		Integer.MAX_VALUE));

        scale.set_slice_y(0);
        AVFilter.avfilter_start_frame(outlink, AVFilter.avfilter_ref_buffer(outpicref, ~0));
        return 0;
    }
    

    int draw_slice(AVFilterLink link, int y, int h, int slice_dir) {
        ScaleContext scale = (ScaleContext) link.get_dst().get_priv();
        int out_h;

        if ( (scale.get_slice_y() == 0) && (slice_dir == -1) )
            scale.set_slice_y(link.get_dst().get_output(0).get_h());

        if ( (scale.get_interlaced() > 0) || 
        	 ( (scale.get_interlaced() < 0) && (link.get_cur_buf().get_video().get_interlaced() != 0) ) ) {
            out_h = VfScale.scale_slice(link, scale.get_isws(0), y, (h+1)/2, 2, 0);
            out_h+= VfScale.scale_slice(link, scale.get_isws(1), y,  h   /2, 2, 1);
        }else{
            out_h = VfScale.scale_slice(link, scale.get_sws(), y, h, 1, 0);
        }

        if (slice_dir == -1)
            scale.set_slice_y(scale.get_slice_y() - out_h);
        AVFilter.avfilter_draw_slice(link.get_dst().get_output(0), scale.get_slice_y(), out_h, slice_dir);
        if (slice_dir == 1)
            scale.set_slice_y(scale.get_slice_y() + out_h);

        return 0;
    }

}
