package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.AVString;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOO;
import uk.ac.liv.util.OutOOI;
import uk.ac.liv.util.OutOOOI;
import uk.ac.liv.util.UtilsString;

public class AVFilterGraph {

	public static AVFilterGraph avfilter_graph_alloc() {
		return new AVFilterGraph();
	}
	
	public static OutOI avfilter_graph_create_filter(
			AVFilter filt, String name, String args,
			Object opaque, AVFilterGraph graph_ctx) {
		int ret;
		AVFilterContext filt_ctx;
		
		OutOI ret_obj = AVFilter.avfilter_open(filt, name);
	    ret = ret_obj.get_ret();
	    filt_ctx = (AVFilterContext) ret_obj.get_obj();	    
	    if (ret < 0)
	    	return new OutOI(null, ret);
	    
	    ret = AVFilter.avfilter_init_filter(filt_ctx, args, opaque);
	    if (ret < 0)
	    	return new OutOI(null, ret);
	    
	    ret = avfilter_graph_add_filter(graph_ctx, filt_ctx);
	    if (ret < 0)
	    	return new OutOI(null, ret);
	    
	    
		return new OutOI(filt_ctx, 0);
	}

	private static int avfilter_graph_add_filter(AVFilterGraph graph_ctx,
			AVFilterContext filter) {
		graph_ctx.avfilter_graph_add_filter(filter);
		return 0;
	}

	
	
    int filter_count;
    ArrayList<AVFilterContext> filters = new ArrayList<AVFilterContext>();

    String scale_sws_opts; ///< sws options to use for the auto-inserted scale filters

	public int get_filter_count() {
		return filter_count;
	}

	public void set_filter_count(int filter_count) {
		this.filter_count = filter_count;
	}
	
	public AVFilterContext get_filter(int i) {
		return filters.get(i);
	}

	public void avfilter_graph_add_filter(AVFilterContext item) {
		this.filters.add(item);
		this.filter_count++;
	}

	public String get_scale_sws_opts() {
		return scale_sws_opts;
	}

	public void set_scale_sws_opts(String scale_sws_opts) {
		this.scale_sws_opts = scale_sws_opts;
	}

	public int avfilter_graph_parse(String filters, AVFilterInOut open_inputs_ptr,
			AVFilterInOut open_outputs_ptr, String log_ctx) {
		int index = 0, ret = 0;
		int idx_chr = 0;
		char chr = 0;

	    AVFilterInOut curr_inputs = null;
	    AVFilterInOut open_inputs  = (open_inputs_ptr != null)  ? open_inputs_ptr  : null;
	    AVFilterInOut open_outputs = (open_outputs_ptr != null) ? open_outputs_ptr : null;

	    do {
	        AVFilterContext filter;
	        String filterchain = filters;
	        
	        filters = UtilsString.remove_trailing_chars(filters, GraphParser.WHITESPACES);

	        OutOOOI ret_obj = GraphParser.parse_inputs(filters, curr_inputs, open_outputs, log_ctx);
	        ret = ret_obj.get_ret();
	        filters = (String) ret_obj.get_obj1();
	        curr_inputs = (AVFilterInOut) ret_obj.get_obj2();
	        open_outputs = (AVFilterInOut) ret_obj.get_obj3();
	        
	        
	        if (ret < 0) {
	        	/* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	        }

	        OutOOI ret_obj2 = parse_filter(filters, index, log_ctx);
	        ret = ret_obj2.get_ret();
	        filter = (AVFilterContext) ret_obj2.get_obj1();
	        filters = (String) ret_obj.get_obj1();
	        if (ret < 0) {
	        	/* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	        }

	        if ( (filter.get_input_count() == 1) && (curr_inputs == null) && (index == 0) ) {
	            /* First input pad, assume it is "[in]" if not specified */
	            String tmp = "[in]";
	            
	            ret_obj = GraphParser.parse_inputs(tmp, curr_inputs, open_outputs, log_ctx);
		        ret = ret_obj.get_ret();
		        tmp = (String) ret_obj.get_obj1();
		        curr_inputs = (AVFilterInOut) ret_obj.get_obj2();
		        open_outputs = (AVFilterInOut) ret_obj.get_obj3();
		        
	            if (ret < 0) {
		        	/* clear open_in/outputs only if not passed as parameters TODO ?*/
		        	return ret;
		        }
	        }

	        if ((ret = link_filter_inouts(filter, curr_inputs, open_inputs, log_ctx)) < 0) {
	        	/* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	        }

	        if ((ret = parse_outputs(filters, curr_inputs, open_inputs, open_outputs,
	                                 log_ctx)) < 0) {
	        	/* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	        }

	        filters = UtilsString.remove_leading_chars(filters, GraphParser.WHITESPACES);
	        chr = filters.charAt(0);
	        filters = filters.substring(1);

	        if ( (chr == ';') && (curr_inputs != null) ) {
	            Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	                   "Invalid filterchain containing an unlabelled output pad: \"%s\"\n",
	                   filterchain);
	            ret = Error.AVERROR(Error.EINVAL);
	            /* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	        }
	        index++;
	    } while ( (chr == ',') || (chr == ';') );

	    if (chr != 0) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	               "Unable to parse graph description substring: \"%s\"\n",
	               filters);
	        ret = Error.AVERROR(Error.EINVAL);
            /* clear open_in/outputs only if not passed as parameters TODO ?*/
        	return ret;
	    }

	    if (curr_inputs != null) {
	        /* Last output pad, assume it is "[out]" if not specified */
	        String tmp = "[out]";
	        if ((ret = parse_outputs(tmp, curr_inputs, open_inputs, open_outputs,
	                                 log_ctx)) < 0)
	            /* clear open_in/outputs only if not passed as parameters TODO ?*/
	        	return ret;
	    }
	    
	    return 0;
	}

	private int parse_outputs(String buf, AVFilterInOut curr_inputs,
			AVFilterInOut open_inputs, AVFilterInOut open_outputs,
			String log_ctx) {
	    int ret, pad = 0;

	    while (buf.charAt(0) == '[') {
	        String name = GraphParser.parse_link_name(buf, log_ctx);
	        AVFilterInOut match;

	        AVFilterInOut input = curr_inputs;
	        if (input == null) {
	            Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	                   "No output pad can be associated to link label '%s'.\n",
	                   name);
	            return Error.AVERROR(Error.EINVAL);
	        }
	        curr_inputs = curr_inputs.get_next();

	        if (name == null)
	            return Error.AVERROR(Error.EINVAL);

	        /* First check if the label is not in the open_inputs list */
	        OutOO ret_obj = GraphParser.extract_inout(name, open_outputs);
	        match = (AVFilterInOut) ret_obj.get_obj1();
	        open_outputs = (AVFilterInOut) ret_obj.get_obj2();

	        if (match != null) {
	            if ((ret = link_filter(input.get_filter_ctx(), input.get_pad_idx(),
	                                   match.get_filter_ctx(), match.get_pad_idx(), log_ctx)) < 0)
	                return ret;
	        } else {
	            /* Not in the list, so add the first input as a open_output */
	            input.set_name(name);
	            GraphParser.insert_inout(open_outputs, input);
	        }
	        buf = UtilsString.remove_leading_chars(buf, GraphParser.WHITESPACES);
	        pad++;
	    }

	    return pad;
	}

	private int link_filter_inouts(AVFilterContext filt_ctx,
			AVFilterInOut curr_inputs, AVFilterInOut open_inputs, String log_ctx) {
	    int pad = filt_ctx.get_input_count();
	    int ret;

	    while (pad != 0) {
	        AVFilterInOut p = curr_inputs;
	        if (p == null) {
	            Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	                   "Not enough inputs specified for the \"%s\" filter.\n",
	                   filt_ctx.get_filter().get_name());
	            return Error.AVERROR(Error.EINVAL);
	        }

	        curr_inputs = curr_inputs.get_next();

	        if (p.get_filter_ctx() != null) {
	            if ((ret = link_filter(p.get_filter_ctx(), p.get_pad_idx(), filt_ctx, pad, log_ctx)) < 0)
	                return ret;
	        } else {
	            p.set_filter_ctx(filt_ctx);
	            p.set_pad_idx(pad);
	            GraphParser.insert_inout(open_inputs, p);
	        }
	        pad--;
	    }

	    if (curr_inputs != null) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	               "Too many inputs specified for the \"%s\" filter.\n",
	               filt_ctx.get_filter().get_name());
	        return Error.AVERROR(Error.EINVAL);
	    }

	    pad = filt_ctx.get_output_count();
	    while (pad != 0) {
	        AVFilterInOut currlinkn = new AVFilterInOut();
	        currlinkn.set_filter_ctx(filt_ctx);
	        currlinkn.set_pad_idx(pad);
	        GraphParser.insert_inout(curr_inputs, currlinkn);
	        pad--;
	    }

	    return 0;
	}

	private int link_filter(AVFilterContext src, int srcpad,
			AVFilterContext dst, int dstpad, String log_ctx) {
	    int ret = AVFilter.avfilter_link(src, srcpad, dst, dstpad);
	    if (ret != 0) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	               "Cannot create the link %s:%d -> %s:%d\n",
	               src.get_filter().get_name(), srcpad, dst.get_filter().get_name(), dstpad);
	        return ret;
	    }

	    return 0;
	}

	private OutOOI parse_filter(String buf, int index, String log_ctx) {
	    String opts = null;
	    String name = AVString.av_get_token(buf, "=,;[\n");
	    int ret;
	    
	    if (buf.charAt(0) == '=') {
	        buf = buf.substring(1);
	        opts = AVString.av_get_token(buf, "[],;\n");
	    }

	    OutOI ret_obj = create_filter(index, name, opts, log_ctx);
	    AVFilterContext filt_ctx = (AVFilterContext) ret_obj.get_obj();
	    ret = ret_obj.get_ret(); 
	    return new OutOOI(filt_ctx, buf, ret);
	}

	private OutOI create_filter(int index, String filt_name,
			String args, String log_ctx) {
		AVFilterContext filt_ctx;
	    AVFilter filt;
	    String inst_name;
	    String tmp_args;
	    int ret;

	    inst_name = String.format("Parsed filter %d %s", index, filt_name);

	    filt = AVFilter.avfilter_get_by_name(filt_name);

	    if (filt == null) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR, "No such filter: '%s'\n", filt_name);
	        return new OutOI(null, Error.AVERROR(Error.EINVAL));
	    }
	    
	    OutOI ret_obj = AVFilter.avfilter_open(filt, inst_name);
        ret = ret_obj.get_ret();
        filt_ctx = (AVFilterContext)ret_obj.get_obj();
	    if (filt_ctx == null) {
	    	Log.av_log(log_ctx, Log.AV_LOG_ERROR, "Error creating filter '%s'\n", filt_name);
	        return new OutOI(null, ret);
	    }

	    if ((ret = avfilter_graph_add_filter(this, filt_ctx)) < 0) {
	        AVFilter.avfilter_free(filt_ctx);
	        return new OutOI(null, ret);
	    }

	    if ( (filt_name.equals("scale")) && (args != null) && (args.indexOf("flags") == -1) ) {
	        tmp_args = String.format("%s:%s", args, get_scale_sws_opts());
	        args = tmp_args;
	    }

	    if ((ret = AVFilter.avfilter_init_filter(filt_ctx, args, null)) < 0) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR, "Error initializing filter '%s' with args '%s'\n", filt_name, args);
	        return new OutOI(null, ret);
	    }

	    return new OutOI(filt_ctx, 0);
	}

	public int avfilter_graph_config(String log_ctx) {
	    int ret;

	    ret = ff_avfilter_graph_check_validity(log_ctx);
	    if (ret != 0)
	        return ret;
	        
        ret = ff_avfilter_graph_config_formats(log_ctx);
	    if (ret != 0)
	        return ret;
        
        ret = ff_avfilter_graph_config_links(log_ctx);
	    if (ret != 0)
	        return ret;

	    return 0;
	}

	private int ff_avfilter_graph_config_links(String log_ctx) {
	    int ret;

	    for (AVFilterContext filt : filters) {
	        if (filt.get_output_count() == 0) {
	        	ret = filt.avfilter_config_links();
	            if (ret != 0)
	                return ret;
	        }
	    }

	    return 0;
	}

	private int ff_avfilter_graph_config_formats(String log_ctx) {
	    int ret;

	    /* find supported formats from sub-filters, and merge along links */
	    ret = query_formats(log_ctx);
	    if (ret < 0)
	        return ret;

	    /* Once everything is merged, it's possible that we'll still have
	     * multiple valid media format choices. We pick the first one. */
	    pick_formats();

	    return 0;
	}

	private void pick_formats() {
	    int i, j;

	    for (i = 0 ; i < filter_count ; i++) {
	    	AVFilterContext filter = get_filter(i);
	    	
	        for (j = 0 ; j < filter.get_input_count() ; j++)
	            filter.get_input(j).pick_format();
	        for (j = 0 ; j < filter.get_output_count() ; j++)
	            filter.get_output(j).pick_format();
	    }
	}

	private int query_formats(String log_ctx) {
		int i, j, ret;
	    int scaler_count = 0;
	    String inst_name;

	    /* ask all the sub-filters for their supported media formats */
	    for (AVFilterContext filt_ctx : filters) {
	    	if (filt_ctx.get_filter().query_formats(filt_ctx) == -1)
	    		Defaults.avfilter_default_query_formats(filt_ctx);    	
	    }	    		

	    /* go through and merge as many format lists as possible */
	    for (i = 0 ; i < filter_count ; i++) {
	    	AVFilterContext filter = filters.get(i);
	    	
	    	
	        for (j = 0 ; j < filter.get_input_count() ; j++) {
	        	AVFilterLink link = filter.get_input(j);
	        	
	        	if ( (link != null) && (!link.get_in_formats().equals(link.get_out_formats())) ) {
	                if (avfilter_merge_formats(link.get_in_formats(),
	                                           link.get_out_formats()) == null) {
	                    AVFilterContext scale;
	                    String scale_args;
	                    /* couldn't merge format lists. auto-insert scale filter */
	                    inst_name = "auto-inserted scaler " + scaler_count++;
	                    scale_args =  String.format("0:0:%s", get_scale_sws_opts());
	                    OutOI ret_obj = avfilter_graph_create_filter(AVFilter.avfilter_get_by_name("scale"),
                                inst_name, scale_args, null, this); 
                        scale = (AVFilterContext) ret_obj.get_obj();
	                    ret = ret_obj.get_ret();
	                    if (ret  < 0)
	                        return ret;
	                    ret = AVFilter.avfilter_insert_filter(link, scale, 0, 0);
	                    if (ret < 0)
	                        return ret;

	                    scale.get_filter().query_formats(scale);
	                    
	                    link = scale.get_input(0);
	                    if ( (link != null) &&
	                         (avfilter_merge_formats(link.get_in_formats(), link.get_out_formats()) == null) ) {
	                    	Log.av_log(log_ctx, Log.AV_LOG_ERROR,
		                               "Impossible to convert between the formats supported by the filter " +
		                               "'%s' and the filter '%s'\n", link.get_src().get_name(), link.get_dst().get_name());
		                        return Error.AVERROR(Error.EINVAL);
	                    }
	                    
	                    link = scale.get_output(0);
	                    if ( (link != null) &&
	                         (avfilter_merge_formats(link.get_in_formats(), link.get_out_formats()) == null) ) {
	                    	Log.av_log(log_ctx, Log.AV_LOG_ERROR,
		                               "Impossible to convert between the formats supported by the filter " +
		                               "'%s' and the filter '%s'\n", link.get_src().get_name(), link.get_dst().get_name());
		                        return Error.AVERROR(Error.EINVAL);
	                    }
	                 
	                }
	            }
	        }
	    }

	    return 0;
	}

	private AVFilterFormats avfilter_merge_formats(AVFilterFormats a,
			AVFilterFormats b) {
	    AVFilterFormats ret = new AVFilterFormats();
	    int i, j, k = 0;

	    for (Long fmt1 : a.get_formats()) {
	    	for (Long fmt2 : b.get_formats()) {
	    		if (fmt1.equals(fmt2))
	    			ret.avfilter_add_format(fmt1);
	    	}
	    }

	    /* check that there was at least one common format */
	    if (ret.get_format_count() == 0)
	    	return null;
	    	
	    for (AVFilterFormats ref : a.get_refs())
	    	ret.add_ref(ref);
	    
	    for (AVFilterFormats ref : b.get_refs())
	    	ret.add_ref(ref);
	    
	    return ret;
	}

	private int ff_avfilter_graph_check_validity(String log_ctx) {
	    int j;

	    for (AVFilterContext filt : filters) {

	        for (j = 0; j < filt.get_input_count() ; j++) {
	            if ( (filt.get_input(j) == null) || (filt.get_input(j).get_src() == null) ) {
	                Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	                       "Input pad \"%s\" for the filter \"%s\" of type \"%s\" not connected to any source\n",
	                       filt.get_input_pad(j).name, filt.get_name(), filt.get_filter().get_name());
	                return Error.AVERROR(Error.EINVAL);
	            }
	        }

	        for (j = 0; j < filt.get_output_count(); j++) {
	            if ( (filt.get_output(j) == null) || (filt.get_output(j).get_dst() == null) ) {
	                Log.av_log(log_ctx, Log.AV_LOG_ERROR,
		                       "Input pad \"%s\" for the filter \"%s\" of type \"%s\" not connected to any source\n",
		                       filt.get_output_pad(j).name, filt.get_name(), filt.get_filter().get_name());
   	                return Error.AVERROR(Error.EINVAL);
	            }
	        }
	    }

	    return 0;
		
	}

    
    

}
