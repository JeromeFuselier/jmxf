package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavutil.AVPixFmtDescriptor;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOI;

public class ImgConvert {

	public static OutII avcodec_get_chroma_sub_sample(PixelFormat pix_fmt) {
		AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);
		int h_shift = desc.get_log2_chroma_w();
	    int v_shift = desc.get_log2_chroma_h();
				
		return new OutII(h_shift, v_shift);
	}

	public static int avpicture_get_size(PixelFormat pix_fmt, int width, int height) {
		AVPicture dummy_pict = new AVPicture();
	    if (ImgUtils.av_image_check_size(width, height, 0, null) != 0)
	        return -1;
	    switch (pix_fmt) {
	    	case PIX_FMT_RGB8:
		    case PIX_FMT_BGR8:
		    case PIX_FMT_RGB4_BYTE:
		    case PIX_FMT_BGR4_BYTE:
		    case PIX_FMT_GRAY8:
		        // do not include palette for these pseudo-paletted formats
		    	return width * height;
	    }
	    return avpicture_fill(dummy_pict, null, pix_fmt, width, height);
	}

	public static int avpicture_fill(AVPicture picture, short [] ptr,
			PixelFormat pix_fmt, int width, int height) {
		int ret;

	    if ((ret = ImgUtils.av_image_check_size(width, height, 0, null)) < 0)
	        return ret;
	    
	    OutOI ret_obj = ImgUtils.av_image_fill_linesizes(pix_fmt, width);
	    ret = ret_obj.get_ret();
	    picture.set_linesize((int[])ret_obj.get_obj());

	    if (ret < 0)
	        return ret;	    
	   
	    ret_obj = ImgUtils.av_image_fill_pointers(pix_fmt, height, ptr, picture.get_linesize());
	    ret = ret_obj.get_ret();
	    picture.set_data((short [][])ret_obj.get_obj());
	    return ret;
	}

	public static boolean ff_is_hwaccel_pix_fmt(PixelFormat pix_fmt) {
		return PixDesc.av_pix_fmt_descriptors.get(pix_fmt).has_flag(PixDesc.PIX_FMT_HWACCEL);
	}

}
