package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libswscale.SwScale;
import uk.ac.liv.ffmpeg.libswscale.SwsContext;
import uk.ac.liv.ffmpeg.libswscale.UtilsScale;

public class VfScale extends AVFilter {
	
	public static enum var_name {
	    VAR_PI,
	    VAR_PHI,
	    VAR_E,
	    VAR_IN_W,   VAR_IW,
	    VAR_IN_H,   VAR_IH,
	    VAR_OUT_W,  VAR_OW,
	    VAR_OUT_H,  VAR_OH,
	    VAR_A,
	    VAR_HSUB,
	    VAR_VSUB	  
	};
	
	public static int VARS_NB = var_name.values().length;
	
	public static String [] var_names = {"PI", "PHI", "E", "in_w", "iw", "in_h",
		"ih", "out_w", "ow", "out_h", "oh", "a", "hsub", "vsub"};
	
	
	public VfScale() {
		super();
		this.name = "scale";
		this.description = "Scale the input video to width:height size and/or convert the image format.";
		

		this.inputs.add(new AVFilterPadVfInput("default", AVMediaType.AVMEDIA_TYPE_VIDEO, AVFilter.AV_PERM_READ));
		this.inputs.add(new AVFilterPad(null));
		
		this.outputs.add(new AVFilterPadVfOutput("default", AVMediaType.AVMEDIA_TYPE_VIDEO));
		this.outputs.add(new AVFilterPad(null));
		
	}

	
	public static int scale_slice(AVFilterLink link, SwsContext sws,
			int y, int h, int mul, int field) {
        ScaleContext scale = (ScaleContext) link.get_dst().get_priv();
	    AVFilterBufferRef cur_pic = link.get_cur_buf();
	    AVFilterBufferRef out_buf = link.get_dst().get_output(0).get_out_buf();
	    short [][] in = { null, null, null, null }; 
	    short [][] out = { null, null, null, null }; 
	    int [] in_stride = new int[4];
	    int [] out_stride = new int[4];
	    int i;

	    for (i = 0 ; i < 4 ; i++){
	        int vsub = ( ((i+1) & 2) != 0) ? scale.get_vsub() : 0;
	        in_stride[i] = cur_pic.get_linesize(i) * mul;
	        out_stride[i] = out_buf.get_linesize(i) * mul;
	        in[i] = Arrays.copyOfRange(cur_pic.get_data(i), ((y >> vsub) + field) * cur_pic.get_linesize(i), cur_pic.get_data(i).length);
	        out[i] = Arrays.copyOfRange(out_buf.get_data(i), field  * out_buf.get_linesize(i), out_buf.get_data(i).length);
	    }
	    if (scale.get_input_is_pal() != 0){
	         in[1] = Arrays.copyOfRange(cur_pic.get_data(1), 0, cur_pic.get_data(1).length);
	        out[1] = Arrays.copyOfRange(out_buf.get_data(1), 0, out_buf.get_data(1).length);
	    }

	    return sws.sws_scale(in, in_stride, y/mul, h, out, out_stride);
	}
	

    
    int init(AVFilterContext ctx, String args, Object opaque) {
    	ScaleContext scale = new ScaleContext();
    	ctx.set_priv(scale);
	     
    	String tmp;
	
    	scale.set_w_expr("iw");
    	scale.set_h_expr("ih");
    	
    	scale.set_flags(SwScale.SWS_BILINEAR);
	    if (args != null) {
	    	
	    	 
	    	Pattern p = Pattern.compile("([^:]{0,255}):([^:]{0,255}).*");
		    Matcher m = p.matcher(args);
		    if (m.matches()) {
		    	scale.set_w_expr(m.group(1));
		    	scale.set_h_expr(m.group(2));
		    	int idx = args.indexOf("flags=");
		    	if (idx != -1) {
		    		idx += 6; // Skip "flags="
		    		int radix = 10;
		    		boolean is_hexa = args.substring(idx).toLowerCase().startsWith("0x");
		    		if (is_hexa) {
		    			idx += 2;
		    			radix = 16;
		    		}
		    		int idx_end = idx;
		    		
		    		while ( (idx_end < args.length()) && Character.isDigit(args.charAt(idx_end)) )
	    				idx_end++;
		    		
		    		String arg_str = args.substring(idx, idx_end);
		    		if (arg_str != null)
		    			scale.set_flags(Integer.parseInt(arg_str, radix));
		    	}
		    	
		    	if (args.contains("interl=1"))
		    		scale.set_interlaced(1);
		    	else if (args.contains("interl=-1"))
		    		scale.set_interlaced(-1);		    		
		    	
		    } 
	    }

	    return 0;
    }
    
    
    int uninit(AVFilterContext ctx) {
        ScaleContext scale =  (ScaleContext) ctx.get_priv();
        scale.get_sws().sws_freeContext();
        scale.get_isws(0).sws_freeContext();
        scale.get_isws(1).sws_freeContext();
        scale.set_sws(null);
    	return 0;
    }
    


    int query_formats(AVFilterContext ctx) {
        int ret;

        if (ctx.get_input(0) != null) {
        	AVFilterFormats formats = new AVFilterFormats();
            for (PixelFormat pix_fmt : PixelFormat.values())
                if ( (UtilsScale.sws_isSupportedInput(pix_fmt)) &&
                     (ret = formats.avfilter_add_format(pix_fmt)) < 0) {
                	formats.avfilter_formats_unref();
                    return ret;
                }
            //formats.avfilter_formats_ref(ctx.get_input(0).get_out_formats());
            ctx.get_input(0).set_out_formats(formats);
        }

        if (ctx.get_output(0) != null) {
        	AVFilterFormats formats = new AVFilterFormats();
            for (PixelFormat pix_fmt : PixelFormat.values())
                if ( (UtilsScale.sws_isSupportedOutput(pix_fmt)) &&
                     (ret = formats.avfilter_add_format(pix_fmt)) < 0) {
                	formats.avfilter_formats_unref();
                    return ret;
                }
            //formats.avfilter_formats_ref(ctx.get_output(0).get_in_formats());
            ctx.get_output(0).set_in_formats(formats);
        }
        
        return 0;
    }
	
    
	
    
    

}
