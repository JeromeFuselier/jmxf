package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVString;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.util.OutOO;
import uk.ac.liv.util.OutOOOI;
import uk.ac.liv.util.UtilsString;

public class GraphParser {

	public static final String WHITESPACES = " \n\t";

	public static int avfilter_graph_parse(AVFilterGraph graph,
			String filters, AVFilterInOut inputs_ptr, AVFilterInOut outputs_ptr,
			String log_ctx) {
		return graph.avfilter_graph_parse(filters, inputs_ptr, outputs_ptr, log_ctx);
	}

	public static OutOOOI parse_inputs(String buf,
			AVFilterInOut curr_inputs, AVFilterInOut open_outputs,
			String log_ctx) {
		int idx_buf = 0;
		int pad = 0;

	    while (buf.charAt(idx_buf) == '[') {
	        String name = parse_link_name(buf, log_ctx);
	        AVFilterInOut match;

	        if (name == null)
	            return new OutOOOI(null, null, null, Error.AVERROR(Error.EINVAL));

	        /* First check if the label is not in the open_outputs list */
	        OutOO ret_obj = extract_inout(name, open_outputs);
	        match = (AVFilterInOut) ret_obj.get_obj1();
	        open_outputs = (AVFilterInOut) ret_obj.get_obj2();

	        if (match == null) {
	            /* Not in the list, so add it as an input */
	        	match = new AVFilterInOut();
	            match.set_name(name);
	            match.set_pad_idx(pad);
	        }

	        curr_inputs = insert_inout(curr_inputs, match);

	        idx_buf += UtilsString.strspn(buf, WHITESPACES);
	        pad++;
	    }

		return new OutOOOI(buf, curr_inputs, open_outputs, pad);
	}

	static AVFilterInOut insert_inout(AVFilterInOut inouts,
			AVFilterInOut element) {
		element.set_next(inouts);
		return element;
	}

	static OutOO extract_inout(String label, AVFilterInOut links) {
	    AVFilterInOut ret;

	    while ( (links != null) && (!links.get_name().equals(label)) )
	        links = links.get_next();

	    ret = links;

	    if (ret != null)
	        links = ret.get_next();

	    return new OutOO(ret, links);
	}

	static String parse_link_name(String buf, String log_ctx) {
	    String name;
	    buf = buf.substring(1);

	    name = AVString.av_get_token(buf, "]");

	    if (name == null) {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	               "Bad (empty?) label found in the following: \"%s\".\n", buf);
	    }

	    if (buf.charAt(1) != ']') {
	        Log.av_log(log_ctx, Log.AV_LOG_ERROR,
	               "Mismatched '[' found in the following: \"%s\".\n", buf);
	    }
	      
        return name;
	}

}
