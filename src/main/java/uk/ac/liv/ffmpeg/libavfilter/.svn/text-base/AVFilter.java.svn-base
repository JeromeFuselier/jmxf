package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOO;

public class AVFilter {

	public static boolean initialized = false;

	public static final int AV_PERM_READ          = 0x01; ///< can read from the buffer
	public static final int AV_PERM_WRITE         = 0x02; ///< can write to the buffer
	public static final int AV_PERM_PRESERVE      = 0x04; ///< nobody else can overwrite the buffer
	public static final int AV_PERM_REUSE         = 0x08; ///< can output the buffer multiple times, with the same contents each time
	public static final int AV_PERM_REUSE2        = 0x10; ///< can output the buffer multiple times, modified each time
	public static final int AV_PERM_NEG_LINESIZES = 0x20; ///< the buffer requested can have negative linesizes
	
	public static Map<String, AVFilter> registered_avfilters = new HashMap<String, AVFilter>();
	


	public static void avfilter_register_all() {
		
		if (initialized)
			return;
		initialized = true;	
		
		add_filter(new VsrcBuffer());
		add_filter(new VSinkBuffer());
		add_filter(new VfScale());
		
	}

	private static void add_filter(AVFilter filter) {
		registered_avfilters.put(filter.get_name(), filter);		
	}
	

    String name;         ///< filter name

    int priv_size;      ///< size of private data to allocate for the filter
    

    ArrayList<AVFilterPad> inputs = new ArrayList<AVFilterPad>();  ///< NULL terminated list of inputs. NULL if none
    ArrayList<AVFilterPad> outputs = new ArrayList<AVFilterPad>(); ///< NULL terminated list of outputs. NULL if none

    /**
     * A description for the filter. You should use the
     * NULL_IF_CONFIG_SMALL() macro to define it.
     */
    String description;
    
    
    public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public int get_priv_size() {
		return priv_size;
	}

	public void set_priv_size(int priv_size) {
		this.priv_size = priv_size;
	}

	public AVFilterPad get_input(int i) {
		return inputs.get(i);
	}

	public void add_input(AVFilterPad item) {
		this.inputs.add(item);
	}

	public AVFilterPad get_output(int i) {
		return outputs.get(i);
	}

	public void set_output(AVFilterPad item) {
		this.outputs.add(item);
	}

	public String get_description() {
		return description;
	}

	public void set_description(String description) {
		this.description = description;
	}


	/**
     * Filter initialization function. Args contains the user-supplied
     * parameters. FIXME: maybe an AVOption-based system would be better?
     * opaque is data provided by the code requesting creation of the filter,
     * and is used to pass data to the filter.
     */
    int init(AVFilterContext ctx, String args, Object opaque) {
    	return -1;
    }
    

    /**
     * Filter uninitialization function. Should deallocate any memory held
     * by the filter, release any buffer references, etc. This does not need
     * to deallocate the AVFilterContext->priv memory itself.
     */
    int uninit(AVFilterContext ctx) {
    	return -1;
    }

    /**
     * Queries formats/layouts supported by the filter and its pads, and sets
     * the in_formats/in_chlayouts for links connected to its output pads,
     * and out_formats/out_chlayouts for links connected to its input pads.
     *
     * @return zero on success, a negative value corresponding to an
     * AVERROR code otherwise
     */
    int query_formats(AVFilterContext ctx) {
    	return -1;
    }

	public static AVFilterBufferRef avfilter_get_video_buffer_ref_from_arrays(
			short [][] data, int[] linesize, int perms, int w,
			int h, PixelFormat format) {
		AVFilterBuffer pic = new AVFilterBuffer();
	    AVFilterBufferRef picref = new AVFilterBufferRef();

	    picref.set_buf(pic);
	    picref.set_video(new AVFilterBufferRefVideoProps());
	    pic.set_w(w);
	    pic.set_h(h);
	    picref.get_video().set_w(w);
	    picref.get_video().set_h(h);

	    /* make sure the buffer gets read permission or it's useless for output */
	    picref.set_perms(perms | AVFilter.AV_PERM_READ);

	    pic.set_refcount(1);
	    picref.set_type(AVMediaType.AVMEDIA_TYPE_VIDEO);
	    pic.set_format(format);
	    picref.set_format(format);

	    pic.set_data(data);
	    pic.set_linesize(linesize);
	    picref.set_data(data);
	    picref.set_linesize(linesize);

	    return picref;
		
	}

	public static AVFilter avfilter_get_by_name(String name) {
		return registered_avfilters.get(name);
	}

	public static OutOI avfilter_open(AVFilter filter,
			String inst_name) { 
		AVFilterContext ret = new AVFilterContext();

	    if (filter == null)
	        return new OutOI(null, Error.AVERROR(Error.EINVAL));

	    ret.set_av_class(new AVFilterClass());
	    ret.set_filter(filter);
	    ret.set_name(inst_name);


	    ret.set_input_count(AVFilter.pad_count(filter.get_inputs()));	    
	    if (ret.get_input_count() != 0) {
	        ret.set_input_pads(filter.get_inputs());
	        for (int i = 0 ; i < ret.get_input_count() ; i++)
	        	//ret.get_inputs().add(new AVFilterLink());
	        	ret.get_inputs().add(null);
	    }

	    ret.set_output_count(AVFilter.pad_count(filter.get_outputs()));
	    if (ret.get_output_count() != 0) {
	        ret.set_output_pads(filter.get_outputs());
	        for (int i = 0 ; i < ret.get_output_count() ; i++)
	        	//ret.get_outputs().add(new AVFilterLink());
        		ret.get_outputs().add(null);
	    }

	    return new OutOI(ret, 0);

	}
	
	private static int pad_count(ArrayList<AVFilterPad> pads) {
	    int count = 0;

	    for (AVFilterPad pad : pads) {
	    	if (pad.get_name() != null)
	    		count++;
	    }
	    
    	return count;
	    
	}

	private int get_inputs_size() {
		return inputs.size();
	}

	private ArrayList<AVFilterPad> get_inputs() {
		return inputs;
	}


	private ArrayList<AVFilterPad> get_outputs() {
		return outputs;
	}

	public static int avfilter_init_filter(AVFilterContext filter,
			String args, Object opaque) {
	    return filter.get_filter().init(filter, args, opaque);
	}

	public static void avfilter_free(AVFilterContext filter) { 
		int i;
	    AVFilterLink link;
	
	    filter.get_filter().uninit(filter);
	/*
	    for (i = 0; i < filter->input_count; i++) {
	        if ((link = filter->inputs[i])) {
	            if (link->src)
	                link->src->outputs[link->srcpad - link->src->output_pads] = NULL;
	            avfilter_formats_unref(&link->in_formats);
	            avfilter_formats_unref(&link->out_formats);
	        }
	        avfilter_link_free(&link);
	    }
	    for (i = 0; i < filter->output_count; i++) {
	        if ((link = filter->outputs[i])) {
	            if (link->dst)
	                link->dst->inputs[link->dstpad - link->dst->input_pads] = NULL;
	            avfilter_formats_unref(&link->in_formats);
	            avfilter_formats_unref(&link->out_formats);
	        }
	        avfilter_link_free(&link);
	    }
	
	    av_freep(&filter->name);
	    av_freep(&filter->input_pads);
	    av_freep(&filter->output_pads);
	    av_freep(&filter->inputs);
	    av_freep(&filter->outputs);
	    av_freep(&filter->priv);
	    av_free(filter);*/
		
	}

	public static int avfilter_insert_filter(AVFilterLink link,
			AVFilterContext filt, int filt_srcpad_idx, int filt_dstpad_idx) {
	   int ret;
	  // int dstpad_idx = link.get_dstpad() - link.get_dst().get_input_pads();
	   int dstpad_idx = 0; // TODO Jerome
	   
	   Log.av_log("buffersink", Log.AV_LOG_INFO, "auto-inserting filter '%s' " +
	           "between the filter '%s' and the filter '%s'\n",
	           filt.get_name(), link.get_src().get_name(), link.get_dst().get_name());

	    link.get_dst().set_input(dstpad_idx, null);
	    
	    if ((ret = avfilter_link(filt, filt_dstpad_idx, link.get_dst(), dstpad_idx)) < 0) {
	        // failed to link output filter to new filter 
		    link.get_dst().set_input(dstpad_idx, link);
	        return ret;
	    }

	    // re-hookup the link to the new destination filter we inserted 
	    link.set_dst(filt);
	    link.set_dstpad(filt.get_input_pad(filt_srcpad_idx));
	    filt.set_input(filt_srcpad_idx, link);

	    // if any information on supported media formats already exists on the
	    // link, we need to preserve that 
	    if (link.get_out_formats() != null) {
	    	OutOO ret_obj = avfilter_formats_changeref(link.get_out_formats(), filt.get_output(filt_dstpad_idx).get_out_formats());
	    	link.set_out_formats((AVFilterFormats) ret_obj.get_obj1());
	    	filt.get_output(filt_dstpad_idx).set_out_formats((AVFilterFormats) ret_obj.get_obj2());
	    }
	   
	    if (link.get_out_chlayouts() != null) {
	    	OutOO ret_obj = avfilter_formats_changeref(link.get_out_chlayouts(), filt.get_output(filt_dstpad_idx).get_out_chlayouts());
	    	link.set_out_chlayouts((AVFilterFormats) ret_obj.get_obj1());
	    	filt.get_output(filt_dstpad_idx).set_out_chlayouts((AVFilterFormats) ret_obj.get_obj2());	    	
	    }
	   
	    return 0;
	}

	private static OutOO avfilter_formats_changeref(
			AVFilterFormats oldref, AVFilterFormats newref) {
		// TODO Jerome: Improve ref management
		
		newref = oldref;
		oldref = null;
		
		return new OutOO(oldref, newref);
	}

	public static AVFilterBufferRef avfilter_get_video_buffer(
			AVFilterLink link, int perms, int w, int h) {

	    AVFilterBufferRef ret = null;

	    String buf;
	    
	    ret = link.get_dstpad().get_video_buffer(link, perms, w, h);

	    if (ret == null)
	        ret = Defaults.avfilter_default_get_video_buffer(link, perms, w, h);

	    if (ret != null)
	        ret.set_type(AVMediaType.AVMEDIA_TYPE_VIDEO);

	    return ret;
	}

	public static int avfilter_poll_frame(AVFilterLink link) {
	    int i;
	    int min = Integer.MAX_VALUE;

	    int ret = link.get_srcpad().poll_frame(link);
	    if (ret != -1)
	    	return ret;

	    for (i = 0 ; i < link.get_src().get_input_count() ; i++) {
	        int val;
	        if (link.get_src().get_input(i) == null)
	            return -1;
	        val = avfilter_poll_frame(link.get_src().get_input(i));
	        min = (int)Mathematics.FFMIN(min, val);
	    }

	    return min;
	}

	public static int avfilter_request_frame(AVFilterLink link) {
		int ret = link.get_srcpad().request_frame(link);
		if (ret != -1)
	        return ret;
	    else if (link.get_src().get_input(0) != null)
	        return avfilter_request_frame(link.get_src().get_input(0));
	    else return -1;
	}

	
	public static int avfilter_link(AVFilterContext src, int srcpad,
			AVFilterContext dst, int dstpad) {
		AVFilterLink link = new AVFilterLink();

		
	    if ( (src.get_output_count() <= srcpad) || 
	    	 (dst.get_input_count() <= dstpad)  ||
	    	 (src.get_output(srcpad) != null)   || 
	    	 (dst.get_input(dstpad) != null) )
	        return -1;

	    if (src.get_output_pad(srcpad).get_type() != dst.get_input_pad(dstpad).get_type()) {
	        Log.av_log("AVFilterContext", Log.AV_LOG_ERROR,
	               "Media type mismatch between the '%s' filter output pad %d and the '%s' filter input pad %d\n",
	               src.get_name(), srcpad, dst.get_name(), dstpad);
	        return Error.AVERROR(Error.EINVAL);
	    }
	    

	    src.set_output(srcpad, link);
	    dst.set_input(dstpad, link);

	    link.set_src(src);
	    link.set_dst(dst);
	    link.set_srcpad(src.get_output_pad(srcpad));
	    link.set_dstpad(dst.get_input_pad(dstpad));
	    link.set_type(src.get_output_pad(srcpad).get_type());
	    link.set_format(PixelFormat.PIX_FMT_NONE);

	    return 0;
	}

	public static AVFilterBufferRef avfilter_ref_buffer(AVFilterBufferRef ref, int pmask) {	

	    AVFilterBufferRef ret = new AVFilterBufferRef();
	    
	    ret = (AVFilterBufferRef) ref.clone();

	    if (ref.get_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
	        ret.set_video((AVFilterBufferRefVideoProps) ref.get_video().clone());
	    } else if (ref.get_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
	        ret.set_audio((AVFilterBufferRefAudioProps) ref.get_audio().clone());
	    }
		
		ret.set_perms(ret.get_perms() & pmask);
		ret.get_buf().set_refcount(ret.get_buf().get_refcount() + 1);
	  
	    return ret;
	}

	public static void avfilter_start_frame(AVFilterLink link,
			AVFilterBufferRef picref) { 
	    AVFilterPad dst = link.get_dstpad();
	    int perms = picref.get_perms();

	    if (picref.get_linesize(0) < 0)
	        perms |= AV_PERM_NEG_LINESIZES;
	    /* prepare to copy the picture if it has insufficient permissions */
	    if ( ( (dst.get_min_perms() & perms) != dst.get_min_perms() ) || 
	    	 ( (dst.get_rej_perms() & perms) != 0) ) {
	        Log.av_log("AVFilterContext", Log.AV_LOG_DEBUG,
	                "frame copy needed (have perms %x, need %x, reject %x)\n",
	                picref.get_perms(),
	                link.get_dstpad().get_min_perms(), link.get_dstpad().get_rej_perms());

	        link.set_cur_buf(avfilter_get_video_buffer(link, dst.get_min_perms(), link.get_w(), link.get_h()));
	        link.set_src_buf(picref);
	        AVFilter.avfilter_copy_buffer_ref_props(link.get_cur_buf(), link.get_src_buf());
	    }
	    else
	        link.set_cur_buf(picref);

	    if (dst.start_frame(link, link.get_cur_buf()) == -1)
	    	Defaults.avfilter_default_start_frame(link, link.get_cur_buf());		
	}

	public static void avfilter_copy_buffer_ref_props(AVFilterBufferRef dst, AVFilterBufferRef src) {
	    // copy common properties
	    dst.set_pts(src.get_pts());
	    dst.set_pos(src.get_pos());

	    switch (src.get_type()) {
	    case AVMEDIA_TYPE_VIDEO: dst.set_video(src.get_video()); break;
	    case AVMEDIA_TYPE_AUDIO: dst.set_audio(src.get_audio()); break;
	    }
	}

	public static void avfilter_draw_slice(AVFilterLink link, int y, int h,
			int slice_dir) {
		short [] src = new short[4];
		short [] dst = new short[4];
	    int i, j, vsub;

	    /* copy the slice if needed for permission reasons */
	    if (link.get_src_buf() != null) {
	        vsub = PixDesc.av_pix_fmt_descriptors.get(link.get_format()).get_log2_chroma_h();

	        for (i = 0; i < 4; i++) {
	            if (link.get_src_buf().get_data(i) != null) {
	                src[i] = link.get_src_buf().get_data()[i][(y >> (i==1 || i==2 ? vsub : 0)) * link.get_src_buf().get_linesize(i)];
	                dst[i] = link.get_cur_buf().get_data()[i][(y >> (i==1 || i==2 ? vsub : 0)) * link.get_cur_buf().get_linesize(i)];
	            } else
	                src[i] = dst[i] = 0;
	        }

	        for (i = 0; i < 4; i++) {
	            int planew =
	                ImgUtils.av_image_get_linesize(link.get_format(), link.get_cur_buf().get_video().get_w(), i);

	            if (src[i] == 0) continue;

	            /*for (j = 0; j < h >> (i==1 || i==2 ? vsub : 0); j++) {
	                dst[i] , src[i], planew);
	                src[i] += link->src_buf->linesize[i];
	                dst[i] += link->cur_buf->linesize[i];
	            }*/
	        }
	    }

	    if (link.get_dstpad().draw_slice(link, y, h, slice_dir) == -1)
	    	avfilter_default_draw_slice(link, y, h, slice_dir);
	}

	private static void avfilter_default_draw_slice(AVFilterLink inlink, int y, int h,
			int slice_dir) {
		AVFilterLink outlink = null;
		
		if (inlink.get_dst().get_output_count() != 0)
			outlink = inlink.get_dst().get_output(0);
		
		if (outlink != null)
			avfilter_draw_slice(outlink, y, h, slice_dir);
	}

	public static void avfilter_end_frame(AVFilterLink link) {

		if (link.get_dstpad().end_frame(link) == -1)
			Defaults.avfilter_default_end_frame(link);
		
	    /* unreference the source picture if we're feeding the destination filter
	     * a copied version dues to permission issues */
	    if (link.get_src_buf() != null) {
	    	link.get_src_buf().avfilter_unref_buffer();
	        link.set_src_buf(null);
	    }	
	}

	public static void avfilter_unref_buffer(AVFilterBufferRef get_picref) {
	/*    if (!ref)
	        return;
	    if (!(--ref->buf->refcount)) {
	        if (!ref->buf->free) {
	            store_in_pool(ref);
	            return;
	        }
	        ref->buf->free(ref->buf);
	    }
	    av_freep(&ref->video);
	    av_freep(&ref->audio);
	    av_free(ref);*/
	}

}
