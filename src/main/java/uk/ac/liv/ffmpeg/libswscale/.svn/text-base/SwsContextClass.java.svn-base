package uk.ac.liv.ffmpeg.libswscale;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVOptionValue;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVOption.AVOptionType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class SwsContextClass extends AVClass {
	


	public SwsContextClass() {
		super();	
		class_name = "SWScaler";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();
		
		int DEFAULT = 0;
		int VE = AVOption.AV_OPT_FLAG_VIDEO_PARAM | AVOption.AV_OPT_FLAG_ENCODING_PARAM;
		
		add_option(new AVOption("sws_flags", "scaler/cpu flags", "flags", AVOptionType.FF_OPT_TYPE_FLAGS, new AVOptionValue(DEFAULT), 0, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("fast_bilinear", "fast bilinear", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_FAST_BILINEAR), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("bilinear", "bilinear", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_BILINEAR), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("bicubic", "bicubic", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_BICUBIC), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("experimental", "experimental", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_X), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("neighbor", "nearest neighbor", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_POINT), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("area", "averaging area", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_AREA), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("bicublin", "luma bicubic, chroma bilinear", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_BICUBLIN), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("gauss", "gaussian", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_GAUSS), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("sinc", "sinc", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_SINC), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("lanczos", "lanczos", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_LANCZOS), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("spline", "natural bicubic spline", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_SPLINE), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("print_info", "print info", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_PRINT_INFO), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("accurate_rnd", "accurate rounding", 0, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_ACCURATE_RND), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("full_chroma_int", "full chroma interpolation", 0 , AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_FULL_CHR_H_INT), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("full_chroma_inp", "full chroma input", 0 , AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_FULL_CHR_H_INP), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));
		add_option(new AVOption("bitexact", "", 0 , AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(SwScale.SWS_BITEXACT), Integer.MIN_VALUE, Integer.MAX_VALUE, VE, "sws_flags"));

		add_option(new AVOption("srcw", "source width"      , "srcW", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(16), 1, Integer.MAX_VALUE, VE));
		add_option(new AVOption("srch", "source height"     , "srcH", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(16), 1, Integer.MAX_VALUE, VE));
		add_option(new AVOption("dstw", "destination width" , "dstW", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(16), 1, Integer.MAX_VALUE, VE));
		add_option(new AVOption("dsth", "destination height", "dstH", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(16), 1, Integer.MAX_VALUE, VE));
		add_option(new AVOption("src_format", "source format"     , "srcFormat", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(DEFAULT), 0, PixelFormat.values()[PixelFormat.values().length-1].ordinal(), VE));
		add_option(new AVOption("dst_format", "destination format", "dstFormat", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(DEFAULT), 0, PixelFormat.values()[PixelFormat.values().length-1].ordinal(), VE));
		add_option(new AVOption("src_range" , "source range"      , "srcRange" , AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(DEFAULT), 0, 1, VE));
		add_option(new AVOption("dst_range" , "destination range" , "dstRange" , AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(DEFAULT), 0, 1, VE));
		add_option(new AVOption("param0" , "scaler param 0" , "param[0]" , AVOptionType.FF_OPT_TYPE_DOUBLE, new AVOptionValue(SwScale.SWS_PARAM_DEFAULT), Integer.MIN_VALUE, Integer.MAX_VALUE, VE));
		add_option(new AVOption("param1" , "scaler param 1" , "param[1]" , AVOptionType.FF_OPT_TYPE_DOUBLE, new AVOptionValue(SwScale.SWS_PARAM_DEFAULT), Integer.MIN_VALUE, Integer.MAX_VALUE, VE));

	}

	
	public String item_name(Object obj) {
		return "swscaler";
	}
	
	

}
