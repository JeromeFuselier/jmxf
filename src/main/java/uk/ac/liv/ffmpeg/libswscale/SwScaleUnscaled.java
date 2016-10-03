package uk.ac.liv.ffmpeg.libswscale;

import uk.ac.liv.ffmpeg.libavutil.AVPixFmtDescriptor;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class SwScaleUnscaled {
	
	public static int RGB2YUV_SHIFT = 15;
	public static int BY = ( (int)(0.114*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int BV = (-(int)(0.081*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int BU = ( (int)(0.500*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int GY = ( (int)(0.587*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int GV = (-(int)(0.419*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int GU = (-(int)(0.331*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int RY = ( (int)(0.299*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int RV = ( (int)(0.500*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static int RU = (-(int)(0.169*224/255*(1<<RGB2YUV_SHIFT)+0.5));

	public static int check_image_pointers(short[][] in, PixelFormat pix_fmt, int[] linesizes) {
		AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);
		int i;

	    for (i = 0 ; i < desc.get_nb_components() ; i++) {
	        int plane = desc.get_comp(i).get_plane();
	        if ( (in[plane] == null) || (linesizes[plane] == 0) )
	            return 0;
	    }

	    return 1;
	}

	public static void reset_ptr(short[][] src2, PixelFormat format) {
	    if (!SwScaleInternal.isALPHA(format))
	        src2[3] = null;
	    if (!SwScaleInternal.isPlanarYUV(format)) {
	        src2[3] = src2[2] = null;

	        if (!SwScaleInternal.usePal(format))
	            src2[1] = null;
	    }		
	}

}
