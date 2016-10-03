package uk.ac.liv.ffmpeg.libswscale;

import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;

public class SwScaleInternal {

	public static final int MAX_FILTER_SIZE = 256;
	public static final int APCK_PTR2 = 4;
	public static final int APCK_COEF = 8;
	public static final int APCK_SIZE = 16;

	public static boolean usePal(PixelFormat x) {
		return ( (x == PixelFormat.PIX_FMT_GRAY8A) || ( (PixDesc.av_pix_fmt_descriptors.get(x).get_flags() & PixDesc.PIX_FMT_PAL) != 0 ) );
	}

	public static boolean isALPHA(PixelFormat x) {
		return  ( (x == PixFmt.PIX_FMT_BGR32) ||
			       (x == PixFmt.PIX_FMT_BGR32_1) ||
			       (x == PixFmt.PIX_FMT_RGB32) ||
			       (x == PixFmt.PIX_FMT_RGB32_1) ||
			       (x == PixelFormat.PIX_FMT_PAL8) ||
			       (x == PixelFormat.PIX_FMT_GRAY8A) ||
			       (x == PixelFormat.PIX_FMT_YUVA420P) );
	}

	public static boolean isPlanarYUV(PixelFormat x) {
		return  ( (isPlanar8YUV(x)) ||
                   (x == PixelFormat.PIX_FMT_YUV420P9LE) ||
                   (x == PixelFormat.PIX_FMT_YUV444P9LE ) ||
                   (x == PixelFormat.PIX_FMT_YUV420P10LE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P10LE) ||
                   (x == PixelFormat.PIX_FMT_YUV444P10LE) ||
                   (x == PixelFormat.PIX_FMT_YUV420P16LE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P10LE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P16LE) ||
                   (x == PixelFormat.PIX_FMT_YUV444P16LE) ||
                   (x == PixelFormat.PIX_FMT_YUV420P9BE) || 
                   (x == PixelFormat.PIX_FMT_YUV444P9BE) || 
                   (x == PixelFormat.PIX_FMT_YUV420P10BE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P10BE) ||
                   (x == PixelFormat.PIX_FMT_YUV444P10BE) ||
                   (x == PixelFormat.PIX_FMT_YUV420P16BE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P10BE) ||
                   (x == PixelFormat.PIX_FMT_YUV422P16BE) ||
                   (x == PixelFormat.PIX_FMT_YUV444P16BE) );    
	}

	private static boolean isPlanar8YUV(PixelFormat x) {
		return  ( (x == PixelFormat.PIX_FMT_YUV410P) ||
			       (x == PixelFormat.PIX_FMT_YUV420P) ||
			       (x == PixelFormat.PIX_FMT_YUVA420P) ||
			       (x == PixelFormat.PIX_FMT_YUV411P) ||
			       (x == PixelFormat.PIX_FMT_YUV422P) ||
			       (x == PixelFormat.PIX_FMT_YUV444P) ||
			       (x == PixelFormat.PIX_FMT_YUV440P) ||
			       (x == PixelFormat.PIX_FMT_NV12) );
	}
	

	static boolean isRGBinBytes(PixelFormat x) {
       return ( (x == PixelFormat.PIX_FMT_RGB48BE) ||
                (x == PixelFormat.PIX_FMT_RGB48LE) ||
                (x == PixelFormat.PIX_FMT_RGBA)    ||
                (x == PixelFormat.PIX_FMT_ARGB)    ||
                (x == PixelFormat.PIX_FMT_RGB24) );
	}

	public static boolean isBGRinBytes(PixelFormat x) {
       return ( (x == PixelFormat.PIX_FMT_BGR48BE) ||
                (x == PixelFormat.PIX_FMT_BGR48LE) ||
                (x == PixelFormat.PIX_FMT_BGRA)    ||
                (x == PixelFormat.PIX_FMT_ABGR)    ||
                (x == PixelFormat.PIX_FMT_BGR24) );
		
	}

	public static boolean isYUV(PixelFormat x) {   
       return ( (x == PixelFormat.PIX_FMT_UYVY422) ||
                (x == PixelFormat.PIX_FMT_YUYV422) ||
                (isPlanarYUV(x)) );      
	}

	public static boolean isGray(PixelFormat x) {
       return ( (x == PixelFormat.PIX_FMT_GRAY8) ||
                (x == PixelFormat.PIX_FMT_GRAY8A) ||
                (x == PixelFormat.PIX_FMT_GRAY16BE)    ||
                (x == PixelFormat.PIX_FMT_GRAY16LE) );
	}
	

	public static boolean isRGBinInt(PixelFormat x) {
		return  ( (x == PixelFormat.PIX_FMT_RGB48BE) ||
                  (x == PixelFormat.PIX_FMT_RGB48LE ) ||
                  (x == PixFmt.PIX_FMT_RGB32) ||
                  (x == PixFmt.PIX_FMT_RGB32_1) ||
                  (x == PixelFormat.PIX_FMT_RGB24) ||
                  (x == PixelFormat.PIX_FMT_RGB565BE) ||
                  (x == PixelFormat.PIX_FMT_RGB565LE) ||
                  (x == PixelFormat.PIX_FMT_RGB555BE) ||
                  (x == PixelFormat.PIX_FMT_RGB555LE) ||
                  (x == PixelFormat.PIX_FMT_RGB444BE) || 
                  (x == PixelFormat.PIX_FMT_RGB444LE) || 
                  (x == PixelFormat.PIX_FMT_RGB8) ||
                  (x == PixelFormat.PIX_FMT_RGB4) ||
                  (x == PixelFormat.PIX_FMT_RGB4_BYTE) ||
                  (x == PixelFormat.PIX_FMT_MONOBLACK) ||
                  (x == PixelFormat.PIX_FMT_MONOWHITE) );    
	}
	

	public static boolean isBGRinInt(PixelFormat x) {
		return  ( (x == PixelFormat.PIX_FMT_BGR48BE) ||
                  (x == PixelFormat.PIX_FMT_BGR48LE ) ||
                  (x == PixFmt.PIX_FMT_BGR32) ||
                  (x == PixFmt.PIX_FMT_BGR32_1) ||
                  (x == PixelFormat.PIX_FMT_BGR24) ||
                  (x == PixelFormat.PIX_FMT_BGR565BE) ||
                  (x == PixelFormat.PIX_FMT_BGR565LE) ||
                  (x == PixelFormat.PIX_FMT_BGR555BE) ||
                  (x == PixelFormat.PIX_FMT_BGR555LE) ||
                  (x == PixelFormat.PIX_FMT_BGR444BE) || 
                  (x == PixelFormat.PIX_FMT_BGR444LE) || 
                  (x == PixelFormat.PIX_FMT_BGR8) ||
                  (x == PixelFormat.PIX_FMT_BGR4) ||
                  (x == PixelFormat.PIX_FMT_BGR4_BYTE) ||
                  (x == PixelFormat.PIX_FMT_MONOBLACK) ||
                  (x == PixelFormat.PIX_FMT_MONOWHITE) );    
	}
	
	
	public static boolean isAnyRGB(PixelFormat x) {   
       return ( (isRGBinInt(x)) ||
                (isBGRinInt(x)) );      
	}

	public static boolean is16BPS(PixelFormat x) {
		return  ( (x == PixelFormat.PIX_FMT_GRAY16BE) ||
                (x == PixelFormat.PIX_FMT_GRAY16LE ) ||
                (x == PixelFormat.PIX_FMT_BGR48BE) ||
                (x == PixelFormat.PIX_FMT_BGR48LE) ||
                (x == PixelFormat.PIX_FMT_RGB48BE) ||
                (x == PixelFormat.PIX_FMT_RGB48LE) ||
                (x == PixelFormat.PIX_FMT_YUV420P16LE) ||
                (x == PixelFormat.PIX_FMT_YUV422P16LE) || 
                (x == PixelFormat.PIX_FMT_YUV444P16LE) || 
                (x == PixelFormat.PIX_FMT_YUV420P16BE) ||
                (x == PixelFormat.PIX_FMT_YUV422P16BE) ||
                (x == PixelFormat.PIX_FMT_YUV444P16BE) );   
	}

	public static boolean isNBPS(PixelFormat x) {
		return  ( (x == PixelFormat.PIX_FMT_YUV420P9LE) ||
                (x == PixelFormat.PIX_FMT_YUV420P9BE ) ||
                (x == PixelFormat.PIX_FMT_YUV444P9BE) ||
                (x == PixelFormat.PIX_FMT_YUV444P9LE) ||
                (x == PixelFormat.PIX_FMT_YUV422P10BE) ||
                (x == PixelFormat.PIX_FMT_YUV422P10LE) ||
                (x == PixelFormat.PIX_FMT_YUV444P10BE) ||
                (x == PixelFormat.PIX_FMT_YUV444P10LE) || 
                (x == PixelFormat.PIX_FMT_YUV420P10LE) || 
                (x == PixelFormat.PIX_FMT_YUV420P10BE) ||
                (x == PixelFormat.PIX_FMT_YUV422P10LE) ||
                (x == PixelFormat.PIX_FMT_YUV422P10BE) );   
	}

	public static boolean isPacked(PixelFormat x) {
	       return ( (x == PixelFormat.PIX_FMT_PAL8) ||
	                (x == PixelFormat.PIX_FMT_YUYV422) ||
	                (x == PixelFormat.PIX_FMT_UYVY422)    ||
	                (x == PixFmt.PIX_FMT_Y400A) ||
	                (isAnyRGB(x)) ); 
	}

	public static boolean isBE(PixelFormat x) {
		return (x.ordinal() & 1) != 0;
	}
	
}
