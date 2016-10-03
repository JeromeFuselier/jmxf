package uk.ac.liv.ffmpeg.libavfilter;

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavfilter.VfScale.var_name;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Eval;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libswscale.UtilsScale;
import uk.ac.liv.util.OutOI;

public class AVFilterPadVfOutput extends AVFilterPad {

	public AVFilterPadVfOutput(String name, AVMediaType type, int min_perms) {
		super(name, type, min_perms);
	}

	public AVFilterPadVfOutput(String name, AVMediaType type) {
		super(name, type);
	}

	public AVFilterPadVfOutput(String name) {
		super(name);
	}
	
    int config_props(AVFilterLink outlink) {
        AVFilterContext ctx = outlink.get_src();
        AVFilterLink inlink = outlink.get_src().get_input(0);
        ScaleContext scale = (ScaleContext) ctx.get_priv();
        long w, h;
        double [] var_values = new double[var_name.values().length];
        double res;
        String expr;
        int ret;

        var_values[var_name.VAR_PI.ordinal()]    = Math.PI;
        var_values[var_name.VAR_PHI.ordinal()]   = Mathematics.M_PHI;
        var_values[var_name.VAR_E.ordinal()]     = Math.E;
        var_values[var_name.VAR_IN_W.ordinal()]  = (double) inlink.get_w();
		var_values[var_name.VAR_IW.ordinal()]    = (double) inlink.get_w();
        var_values[var_name.VAR_IN_H.ordinal()]  = (double) inlink.get_h();
		var_values[var_name.VAR_IH.ordinal()]    = (double) inlink.get_h();
        var_values[var_name.VAR_OUT_W.ordinal()] = Double.NaN; 
		var_values[var_name.VAR_OW.ordinal()]    = Double.NaN;
        var_values[var_name.VAR_OUT_H.ordinal()] = Double.NaN;
		var_values[var_name.VAR_OH.ordinal()]    = Double.NaN;
        var_values[var_name.VAR_A.ordinal()]     = (double) inlink.get_w() / inlink.get_h();
        var_values[var_name.VAR_HSUB.ordinal()]  = (double) (1 << PixDesc.av_pix_fmt_descriptors.get(inlink.get_format()).get_log2_chroma_w());
        var_values[var_name.VAR_VSUB.ordinal()]  = (double) (1 << PixDesc.av_pix_fmt_descriptors.get(inlink.get_format()).get_log2_chroma_h());

        /* evaluate width and height */
        expr = scale.get_w_expr();
        OutOI ret_obj = Eval.av_expr_parse_and_eval(expr, VfScale.var_names, var_values,
                                          		 null, null, null, null, null, 0, "AVFilterContext");
        res = (Double) ret_obj.get_obj();
        scale.set_w((int)res);
        var_values[var_name.VAR_OUT_W.ordinal()] = res;
        var_values[var_name.VAR_OW.ordinal()] = res;

        expr = scale.get_h_expr();
        ret_obj = Eval.av_expr_parse_and_eval(expr, VfScale.var_names, var_values,
         		 						     null, null, null, null, null, 0, "AVFilterContext");
        res = (Double) ret_obj.get_obj();
        ret = ret_obj.get_ret();
        if (ret < 0) {
        	Log.av_log("", Log.AV_LOG_ERROR, "Error when evaluating the expression '%s'\n", expr);
        	return ret;
        }
        scale.set_h((int)res);
        var_values[var_name.VAR_OUT_H.ordinal()] = res;
        var_values[var_name.VAR_OH.ordinal()] = res;
        
        /* evaluate again the width, as it may depend on the output height */
        expr = scale.get_w_expr();
        ret_obj = Eval.av_expr_parse_and_eval(expr, VfScale.var_names, var_values,
                                          		 null, null, null, null, null, 0, "AVFilterContext");
        if (ret < 0) {
        	Log.av_log("", Log.AV_LOG_ERROR, "Error when evaluating the expression '%s'\n", expr);
        	return ret;
        }
        scale.set_w((int)res);
        

        w = scale.get_w();
        h = scale.get_h();

        /* sanity check params */
        if ( (w <  -1) || (h <  -1) ) {
        	Log.av_log("AVFilterContext", Log.AV_LOG_ERROR, "Size values less than -1 are not acceptable.\n");
            return Error.AVERROR(Error.EINVAL);
        }
        if ( (w == -1) && (h == -1) ) {
            scale.set_w(0);
            scale.set_h(0);
        }
        
        if (scale.get_w() == 0)
            w = inlink.get_w();
        else
        	w = scale.get_w();
        
        if (scale.get_h() == 0)
            h = inlink.get_h();
        else
        	h = scale.get_h();
            
        if (w == -1)
            w = Mathematics.av_rescale(h, inlink.get_w(), inlink.get_h());
        if (h == -1)
            h = Mathematics.av_rescale(w, inlink.get_h(), inlink.get_w());

        if ( (w > Integer.MAX_VALUE) || 
        	 (h > Integer.MAX_VALUE) ||
             ( (h * inlink.get_w()) > Integer.MAX_VALUE )  ||
             ( (w * inlink.get_h()) > Integer.MAX_VALUE ) ) 
        	Log.av_log("AVFilterContext", Log.AV_LOG_ERROR, "Rescaled value for width or height is too big.\n");

        outlink.set_w((int) w);
        outlink.set_h((int) h);

        /* TODO: make algorithm configurable */
        Log.av_log("AVFilterContext", Log.AV_LOG_INFO, "w:%d h:%d fmt:%s -> w:%d h:%d fmt:%s flags:0x%x\n",
               inlink.get_w(), inlink.get_h(), PixDesc.av_pix_fmt_descriptors.get(inlink.get_format()).get_name(),
               outlink.get_w(), outlink.get_h(), PixDesc.av_pix_fmt_descriptors.get(outlink.get_format()).get_name(),
               scale.get_flags());

        scale.set_input_is_pal(PixDesc.av_pix_fmt_descriptors.get(inlink.get_format()).get_flags() & PixDesc.PIX_FMT_PAL);

        if (scale.get_sws() != null)
        	scale.get_sws().sws_freeContext();
        scale.set_sws(UtilsScale.sws_getContext(inlink.get_w(), inlink.get_h(), inlink.get_format(),
                                     outlink.get_w(), outlink.get_h(), outlink.get_format(),
                                     scale.get_flags(), null, null, null));
        
        if (scale.get_isws(0) != null)
        	scale.get_isws(0).sws_freeContext();
        scale.set_isws(0, UtilsScale.sws_getContext(inlink.get_w(), inlink.get_h()/2, inlink.get_format(),
                                        outlink.get_w(), outlink.get_h()/2, outlink.get_format(),
                                        scale.get_flags(), null, null, null));
        
        if (scale.get_isws(1) != null)
        	scale.get_isws(1).sws_freeContext();
        scale.set_isws(1,UtilsScale.sws_getContext(inlink .get_w(), inlink .get_h()/2, inlink .get_format(),
                                        outlink.get_w(), outlink.get_h()/2, outlink.get_format(),
                                        scale.get_flags(), null, null, null));
        if (scale.get_sws() == null)
            return Error.AVERROR(Error.EINVAL);

        return 0;

    }
	
	
	

}
