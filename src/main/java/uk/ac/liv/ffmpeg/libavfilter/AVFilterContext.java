package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterLink.AVFilterLinkState;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.util.OutOI;

public class AVFilterContext {
	
	
	/**
	 * Tell av_vsrc_buffer_add_video_buffer_ref() to overwrite the already
	 * cached video buffer with the new added one, otherwise the function
	 * will complain and exit.
	 */
	public static int AV_VSRC_BUF_FLAG_OVERWRITE = 1;
	

    AVClass av_class;              ///< needed for av_log()

    AVFilter filter;               ///< the AVFilter of which this is an instance

    String name;                     ///< name of this filter instance

    int input_count;           ///< number of input pads
    ArrayList<AVFilterPad>  input_pads = new ArrayList<AVFilterPad>();      ///< array of input pads
    ArrayList<AVFilterLink> inputs = new ArrayList<AVFilterLink>();          ///< array of pointers to input links

    int output_count;          ///< number of output pads
    ArrayList<AVFilterPad> output_pads = new ArrayList<AVFilterPad>();     ///< array of output pads
    ArrayList<AVFilterLink> outputs = new ArrayList<AVFilterLink>();         ///< array of pointers to output links

    Object priv;                     ///< private data for use by the filter

	public AVClass get_av_class() {
		return av_class;
	}

	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}

	public AVFilter get_filter() {
		return filter;
	}

	public void set_filter(AVFilter filter) {
		this.filter = filter;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public int get_input_count() {
		return this.input_count;
	}

	public void set_input_count(int input_count) {
		this.input_count = input_count;
	}

	public void set_output_count(int output_count) {
		this.output_count = output_count;
	}

	public AVFilterPad get_input_pad(int i) {
		return input_pads.get(i);
	}

	public void add_input_pad(AVFilterPad item) {
		this.input_pads.add(item);
	}

	public AVFilterLink get_input(int i) {
		return inputs.get(i);
	}	

	public ArrayList<AVFilterLink> get_inputs() {
		return this.inputs;
	}

	public void add_input(AVFilterLink item) {
		this.inputs.add(item);
	}

	public void set_input(ArrayList<AVFilterLink> inputs) {
		this.inputs = inputs;
	}
	
	public void set_input(int idx, AVFilterLink element) {
		this.inputs.add(idx, element);
	}

	public void set_input_pads(ArrayList<AVFilterPad> inputs) {
		this.input_pads = inputs;
	}

	public void set_output_pads(ArrayList<AVFilterPad> outputs) {
		this.output_pads = outputs;
	}

	public int get_output_count() {
		return this.output_count;
	}

	public AVFilterPad get_output_pad(int i) {
		return output_pads.get(i);
	}

	public void add_output_pad(AVFilterPad item) {
		this.output_pads.add(item);
	}

	public AVFilterLink get_output(int i) {
		return outputs.get(i);
	}

	public ArrayList<AVFilterLink> get_outputs() {
		return this.outputs;
	}
	
	public void set_output(int idx, AVFilterLink element) {
		this.outputs.add(idx, element);
	}

	public void add_output(AVFilterLink item) {
		this.outputs.add(item);
	}

	public Object get_priv() {
		return priv;
	}

	public void set_priv(Object priv) {
		this.priv = priv;
	}

	public int av_vsrc_buffer_add_frame(AVFrame frame, int flags) {
	    int ret;
	    AVFilterBufferRef picref = AVCodec.avfilter_get_video_buffer_ref_from_frame(frame, AVFilter.AV_PERM_WRITE);
	    if (picref == null)
	        return Error.AVERROR(Error.ENOMEM);
	    ret = av_vsrc_buffer_add_video_buffer_ref(picref, flags);
	    picref.get_buf().set_data(0, null);
	    picref.avfilter_unref_buffer();

	    return ret;
		
	}

	private int av_vsrc_buffer_add_video_buffer_ref(AVFilterBufferRef picref,
			int flags) {
		BufferSourceContext c = (BufferSourceContext)get_priv();
	    AVFilterLink outlink = get_output(0);
	    int ret;

	    if (c.get_picref() != null) {
	        if ( (flags & AV_VSRC_BUF_FLAG_OVERWRITE) != 0) {
	            c.get_picref().avfilter_unref_buffer();
	            c.set_picref(null);
	        } else {
	            Log.av_log("filterCtx", Log.AV_LOG_ERROR,
	                   "Buffering several frames is not supported. "+
	                   "Please consume all available frames before adding a new one.");
	            return Error.AVERROR(Error.EINVAL);
	        }
	    }

	    if ( (picref.get_video().get_w() != c.get_w()) ||
	    	 (picref.get_video().get_h() != c.get_h()) ||
	    	 (picref.get_format() != c.get_pix_fmt()) ) {
	        AVFilterContext scale = null;
	        if (get_output_count() > 0)
	        	scale = get_output(0).get_dst();
	        AVFilterLink link;
	        String scale_param = "";

	        Log.av_log("filterCtx", Log.AV_LOG_INFO,
	               String.format("Buffer video input changed from size:%dx%d fmt:%s to size:%dx%d fmt:%s",
	               c.get_w(), c.get_h(), PixDesc.av_pix_fmt_descriptors.get(c.get_pix_fmt()).get_name(),
	               picref.get_video().get_w(), picref.get_video().get_h(), 
	               PixDesc.av_pix_fmt_descriptors.get(picref.get_format()).get_name()));

	        if ( (scale == null) || (scale.get_filter().get_name().equals("scale")) ) {
	            AVFilter f = AVFilter.avfilter_get_by_name("scale");

	            Log.av_log("filterCtx", Log.AV_LOG_INFO, "Inserting scaler filter");
	            OutOI ret_obj = AVFilter.avfilter_open(f, "Input equalizer");
	            ret = ret_obj.get_ret();
	            scale = (AVFilterContext)ret_obj.get_obj();
	            get_output(0).set_dst(scale);
	            
	            if (ret < 0)
	                return ret;

	            scale_param += String.format("%d:%d:%s", c.get_w(), c.get_h(), c.get_sws_param());
	            ret = AVFilter.avfilter_init_filter(scale, scale_param, null);	            
	            if (ret < 0) {
	                AVFilter.avfilter_free(scale);
	                return ret;
	            }

	            if ((ret = AVFilter.avfilter_insert_filter(get_output(0), scale, 0, 0)) < 0) {
	            	AVFilter.avfilter_free(scale);
	                return ret;
	            }
	            scale.get_output(0).set_time_base(scale.get_input(0).get_time_base());
	            scale.get_output(0).set_format(c.get_pix_fmt());

	        } else if ( !scale.get_filter().get_name().equals("scale") ) {
	            scale_param += String.format("%d:%d:%s", scale.get_output(0).get_w(), 
	            		scale.get_output(0).get_h(), c.get_sws_param());
	        	scale.get_filter().init(scale, scale_param, null);
	        }

	        c.set_pix_fmt(picref.get_format());
	        scale.get_input(0).set_format(picref.get_format());
	        c.set_w(picref.get_video().get_w());
	        scale.get_input(0).set_w(picref.get_video().get_w());
	        c.set_h(picref.get_video().get_h());
	        scale.get_input(0).set_h(picref.get_video().get_h());

	        link = scale.get_output(0);
	        if ( (ret = link.get_srcpad().config_props(link)) < 0)
	            return ret;
	    }

	    c.set_picref(AVFilter.avfilter_get_video_buffer(outlink, AVFilter.AV_PERM_WRITE,
	                                          picref.get_video().get_w(), picref.get_video().get_h()));
	    ImgUtils.av_image_copy(c.get_picref().get_data(), c.get_picref().get_linesize(),
	                  picref.get_data(), picref.get_linesize(),
	                  picref.get_format(), picref.get_video().get_w(), picref.get_video().get_h());
	    c.get_picref().avfilter_copy_buffer_ref_props(picref);

	    return 0;
		
	}

	private void set_common_formats(AVFilterFormats fmts, AVMediaType type, String offin, String offout) {
		
		int i;
	    for (i = 0; i < input_count; i++)
	        if ( (inputs.get(i) != null) && (inputs.get(i).get_type() == type) ) {
	        	//TODO Jerome : Deal with references ?
	        	if (offout.equals("out_formats"))
	        		inputs.get(i).set_out_formats(fmts);
	        	else if  (offout.equals("out_chlayouts"))
	        		inputs.get(i).set_out_chlayouts(fmts);	        	
	        }
	    
	    for (i = 0; i < output_count; i++)
	        if ( (outputs.get(i) != null) && (outputs.get(i).get_type() == type) ) {
	        	//TODO Jerome : Deal with references ?
	        	if (offin.equals("in_formats"))
	        		outputs.get(i).set_in_formats(fmts);
	        	else if  (offin.equals("in_chlayouts"))
	        		outputs.get(i).set_in_chlayouts(fmts);	        	
	        }
	}

	public void avfilter_set_common_pixel_formats(AVFilterFormats formats) { 
		set_common_formats(formats, AVMediaType.AVMEDIA_TYPE_VIDEO, "in_formats", "out_formats");
	}

	public void avfilter_set_common_sample_formats(AVFilterFormats formats) {
	    set_common_formats(formats, AVMediaType.AVMEDIA_TYPE_AUDIO, "in_formats", "out_formats");
	}

	public void avfilter_set_common_channel_layouts(
			AVFilterFormats formats) {
	    set_common_formats(formats, AVMediaType.AVMEDIA_TYPE_AUDIO, "in_chlayouts", "out_chlayouts");		
	}

	public int avfilter_config_links() {
	    int ret;
	    int i;

	    for (i = 0 ; i < input_count ; i++) {
	    	AVFilterLink link = get_input(i);
	    	
	    	if (link == null)
	    		continue;
	    	
	        switch (link.get_init_state()) {
	        case AVLINK_INIT:
	            continue;
	        case AVLINK_STARTINIT:
	            Log.av_log("AVFilterContext", Log.AV_LOG_INFO, "circular filter chain detected\n");
	            return 0;
	        case AVLINK_UNINIT:
	            link.set_init_state(AVFilterLinkState.AVLINK_STARTINIT);

	            ret = link.get_src().avfilter_config_links();
	            if (ret < 0)
	                return ret;
	            
	            ret = link.get_srcpad().config_props(link);
	            if (ret == -1) // Undefined
	                ret = Defaults.avfilter_default_config_output_link(link);
	            if (ret < 0)
	                return ret;

	            if ( (link.get_time_base().get_num() == 0) && (link.get_time_base().get_den() == 0) )
	                link.set_time_base( ( (link.get_src() != null) && (link.get_src().get_input_count() != 0) ) ?
	                    link.get_src().get_input(0).get_time_base() : AVUtil.AV_TIME_BASE_Q);

	            if ( (link.get_sample_aspect_ratio().get_num() == 0) && (link.get_sample_aspect_ratio().get_den() == 0) )
	                link.set_sample_aspect_ratio(link.get_src().get_input_count() != 0 ?
	                		link.get_src().get_input(0).get_sample_aspect_ratio() : new AVRational(1,1));

	            if ( (link.get_sample_rate() == 0) && 
	            	 (link.get_src() != null) && 
	            	 (link.get_src().get_input_count() != 0) )
	                link.set_sample_rate(link.get_src().get_input(0).get_sample_rate());

	            if ( (link.get_channel_layout() == 0) && 
	            	 (link.get_src() != null) && 
	            	 (link.get_src().get_input_count() != 0) )
	                link.set_channel_layout(link.get_src().get_input(0).get_channel_layout());
	            
	            // TODO Jerome : probably not working 
	            ret = link.get_dstpad().config_props(link);
	            if (ret != -1) // Undefined
	            	return ret;	            

	            link.set_init_state(AVFilterLinkState.AVLINK_INIT);
	        }
	    }

	    return 0;
	}


	
    
    


}
