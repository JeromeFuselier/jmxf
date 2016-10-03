package uk.ac.liv.ffmpeg.libavcodec.raw;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class Raw {
	
	public static ArrayList<PixelFormatTag> ff_raw_pix_fmt_tags;
	
	static {
		ff_raw_pix_fmt_tags = new ArrayList<PixelFormatTag>();
		PixelFormatTag tmp;
		
		 /* Planar formats */	    
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV420P, Common.MKTAG("I420")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV420P, Common.MKTAG("IYUV")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV420P, Common.MKTAG("YV12")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV410P, Common.MKTAG("YUV9")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV410P, Common.MKTAG("YVU9")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV411P, Common.MKTAG("Y41B")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV422P, Common.MKTAG("Y42B")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV422P, Common.MKTAG("P422")));
		
	    /* yuvjXXX formats are deprecated hacks specific to libav*,
	       they are identical to yuvXXX  */
		 /* Planar formats */	
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ420P, Common.MKTAG("I420")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ420P, Common.MKTAG("IYUV")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ420P, Common.MKTAG("YV12")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ422P, Common.MKTAG("Y42B")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ422P, Common.MKTAG("P422")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY8,    Common.MKTAG("Y800")));
		ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY8,    Common.MKTAG("  Y8")));		


	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("YUY2"))); /* Packed formats */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("Y422")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("V422")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("VYUY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("YUNV")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("UYVY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("HDYC")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("UYNV")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("UYNY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("uyv1")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("2Vu1")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("AVRn"))); /* Avid AVI Codec 1:1 */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("AV1x"))); /* Avid 1:1x */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("AVup")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("VDTZ"))); /* SoftLab-NSK VideoTizer */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("auv2")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYYVYY411, Common.MKTAG("Y411")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY8,   Common.MKTAG("GREY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_NV12,    Common.MKTAG("NV12")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_NV21,    Common.MKTAG("NV21")));

	    /* nut */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB555LE, Common.MKTAG("RGB", 15)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR555LE, Common.MKTAG("BGR", 15)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB565LE, Common.MKTAG("RGB", 16)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR565LE, Common.MKTAG("BGR", 16)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB555BE, Common.MKTAG(15 , "BGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR555BE, Common.MKTAG(15 , "RGB")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB565BE, Common.MKTAG(16 , "BGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR565BE, Common.MKTAG(16 , "RGB")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB444LE, Common.MKTAG("RGB", 12)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR444LE, Common.MKTAG("BGR", 12)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB444BE, Common.MKTAG(12 , "BGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR444BE, Common.MKTAG(12 , "RGB")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGBA,     Common.MKTAG("RGBA")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGRA,     Common.MKTAG("BGRA")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_ABGR,     Common.MKTAG("ABGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_ARGB,     Common.MKTAG("ARGB")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB24,    Common.MKTAG("RGB", 24)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR24,    Common.MKTAG("BGR", 24)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV411P,  Common.MKTAG("411P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV422P,  Common.MKTAG("422P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ422P, Common.MKTAG("422P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV440P,  Common.MKTAG("440P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ440P, Common.MKTAG("440P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV444P,  Common.MKTAG("444P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVJ444P, Common.MKTAG("444P")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_MONOWHITE,Common.MKTAG("B1W0")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_MONOBLACK,Common.MKTAG("B0W1")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR8,     Common.MKTAG("BGR",  8)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB8,     Common.MKTAG("RGB",  8)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR4,     Common.MKTAG("BGR",  4)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB4,     Common.MKTAG("RGB",  4)));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB4_BYTE,Common.MKTAG("B4BY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR4_BYTE,Common.MKTAG("R4BY")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB48LE,  Common.MKTAG("RGB", 48 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB48BE,  Common.MKTAG( 48, "RGB")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR48LE,  Common.MKTAG("BGR", 48 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR48BE,  Common.MKTAG( 48, "BGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY16LE,    Common.MKTAG("Y1",  0 , 16 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY16BE,    Common.MKTAG(16 ,  0 , "1Y")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV420P16LE, Common.MKTAG("Y3", 11 , 16 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV420P16BE, Common.MKTAG(16 , 11 , "'3Y")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV422P16LE, Common.MKTAG("Y3", 10 , 16 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV422P16BE, Common.MKTAG(16 , 10, "3Y")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV444P16LE, Common.MKTAG("Y3",  0 , 16 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUV444P16BE, Common.MKTAG(16 ,  0 , "3Y")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUVA420P,    Common.MKTAG("Y4", 11 ,  8 )));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY8A,      Common.MKTAG("Y2",  0 ,  8 )));

	    /* quicktime */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("2vuy")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("2Vuy")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_UYVY422, Common.MKTAG("AVUI"))); /* FIXME merge both fields */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("yuv2")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("yuvs")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_YUYV422, Common.MKTAG("DVOO"))); /* Digital Voodoo SD 8 Bit */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB555LE,Common.MKTAG("L555")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB565LE,Common.MKTAG("L565")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB565BE,Common.MKTAG("B565")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR24,   Common.MKTAG("24BG")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGRA,    Common.MKTAG("BGRA")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGBA,    Common.MKTAG("RGBA")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_ABGR,    Common.MKTAG("ABGR")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_GRAY16BE,Common.MKTAG("b16g")));
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB48BE, Common.MKTAG("b48r")));

	    /* special */
	    ff_raw_pix_fmt_tags.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB565LE,Common.MKTAG( 3 ,  0 ,  0 ,  0 ))); /* flipped RGB565LE */
		
		
	}
	
	

	public static int avcodec_pix_fmt_to_codec_tag(PixelFormat fmt) {
		for (PixelFormatTag tag : ff_raw_pix_fmt_tags) {
			if (tag.get_pix_fmt() == fmt) 
				return tag.get_fourcc();
		}
		return 0;
	}

}
