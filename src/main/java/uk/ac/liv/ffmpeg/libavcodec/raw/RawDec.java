package uk.ac.liv.ffmpeg.libavcodec.raw;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.ImgConvert;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.UtilsArrays;
import uk.ac.liv.util.UtilsString;

public class RawDec extends AVCodec  {
	
	public static ArrayList<PixelFormatTag> pix_fmt_bps_mov = new ArrayList<PixelFormatTag>();
	public static ArrayList<PixelFormatTag> pix_fmt_bps_avi = new ArrayList<PixelFormatTag>();
	
	
	static {
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_MONOWHITE, 1));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      2));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      4));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      8));
	    // FIXME swscale does not support 16 bit in .mov, sample 16bit.mov
	    // http://developer.apple.com/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB555BE, 16));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_RGB24,    24));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_ARGB,     32));
		pix_fmt_bps_mov.add(new PixelFormatTag(PixelFormat.PIX_FMT_MONOWHITE,33));
		
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_MONOWHITE, 1));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      2));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      4));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_PAL8,      8));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixFmt.PIX_FMT_RGB444,        12));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixFmt.PIX_FMT_RGB555,        15));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixFmt.PIX_FMT_RGB555,        16));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGR24,    24));
		pix_fmt_bps_avi.add(new PixelFormatTag(PixelFormat.PIX_FMT_BGRA,     32));
	};
	
	
	
	AVCodecContext avctx;
	

	public RawDec() {
		super();
		
		this.name = "rawvideo";		
		this.long_name = "raw video";
		this.type = AVMediaType.AVMEDIA_TYPE_VIDEO;
		this.id = CodecID.CODEC_ID_RAWVIDEO;
		this.decode = true;				
	}
	
	
	public int init(AVCodecContext avctx) {
		this.avctx = avctx;
		RawVideoContext context = new RawVideoContext();
		avctx.set_priv_data(context);
			
	    if (avctx.get_codec_tag() == Common.MKTAG("raw  "))
	        avctx.set_pix_fmt(ff_find_pix_fmt(pix_fmt_bps_mov, avctx.get_bits_per_coded_sample()));
	    else if (avctx.get_codec_tag() == Common.MKTAG("WRAW"))
	        avctx.set_pix_fmt(ff_find_pix_fmt(pix_fmt_bps_avi, avctx.get_bits_per_coded_sample()));
	    else if (avctx.get_codec_tag() != 0)
	        avctx.set_pix_fmt(ff_find_pix_fmt(Raw.ff_raw_pix_fmt_tags, avctx.get_codec_tag()));
	    else if ( (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_NONE) && (avctx.get_bits_per_coded_sample() != 0) )
	        avctx.set_pix_fmt(ff_find_pix_fmt(pix_fmt_bps_avi, avctx.get_bits_per_coded_sample()));

	    if (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_NONE) {
	        Log.av_log("raw", Log.AV_LOG_ERROR, "Pixel format was not specified and cannot be detected");
	        return Error.AVERROR(Error.EINVAL);
	    }

	    ImgUtils.ff_set_systematic_pal2(context.get_palette(), avctx.get_pix_fmt());
	    if ( ( (avctx.get_bits_per_coded_sample() == 4) || (avctx.get_bits_per_coded_sample() == 2) ) &&
	         (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_PAL8) &&
	         ( (avctx.get_codec_tag() == 0) || (avctx.get_codec_tag() == Common.MKTAG("raw ")) ) ) {

	        context.set_length(ImgConvert.avpicture_get_size(avctx.get_pix_fmt(), 
	        								                 (avctx.get_width() + 3 ) & ~3, 
	        								                 avctx.get_height()));
	        if (context.get_buffer() == null)
	            return -1;
	    } else {
	        context.set_length(ImgConvert.avpicture_get_size(avctx.get_pix_fmt(), 
											                 avctx.get_width(), 
											                 avctx.get_height()));
	    }
	    context.get_pic().set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
	    context.get_pic().set_key_frame(1);

	    avctx.set_coded_frame(context.get_pic());

	    if ( ( (avctx.get_extradata().length >= 9) && 
	    	   (UtilsString.to_string(avctx.get_extradata()).endsWith("BottomUp")) ) ||
	         (avctx.get_codec_tag() == Common.MKTAG(3, 0, 0, 0)) || 
	         (avctx.get_codec_tag() == Common.MKTAG("WRAW")) )
	        context.set_flip(1);

		return 0;
	}
	
	public OutOI decode(AVCodecContext avctx, AVPacket avpkt) {
        AVFrame frame = UtilsCodec.avcodec_get_frame_defaults();
        
        short [] buf = avpkt.get_data();
	    int buf_size = avpkt.get_size();		
		RawVideoContext context = (RawVideoContext) avctx.get_priv_data();

		//AVFrame * frame = (AVFrame *) data;
		//AVPicture * picture = (AVPicture *) data;

		frame.set_pict_type(avctx.get_coded_frame().get_pict_type());
	    frame.set_interlaced_frame(avctx.get_coded_frame().get_interlaced_frame());
	    frame.set_top_field_first(avctx.get_coded_frame().get_top_field_first());
	    frame.set_reordered_opaque(avctx.get_reordered_opaque());
	    frame.set_pkt_pts(avctx.get_pkt().get_pts());
	    frame.set_pkt_pos(avctx.get_pkt().get_pos());

    	if (context.get_tff() >=0 ) {
	        frame.set_interlaced_frame(1);
	        frame.set_top_field_first(context.get_tff());
	    }

    	
	    //2bpp and 4bpp raw in avi and mov (yes this is ugly ...)
	    if (context.get_buffer() == null) {
	        int i;
	       /* uint8_t *dst = context->buffer;
	        buf_size = context->length - 256*4;
	        if (avctx->bits_per_coded_sample == 4){
	            for(i=0; 2*i+1 < buf_size; i++){
	                dst[2*i+0]= buf[i]>>4;
	                dst[2*i+1]= buf[i]&15;
	            }
	        } else
	            for(i=0; 4*i+3 < buf_size; i++){
	                dst[4*i+0]= buf[i]>>6;
	                dst[4*i+1]= buf[i]>>4&3;
	                dst[4*i+2]= buf[i]>>2&3;
	                dst[4*i+3]= buf[i]   &3;
	            }
	        buf= dst;*/
	    }

	    /*if ( (avctx.get_codec_tag() == Common.MKTAG("AV1x")) ||
	         (avctx.get_codec_tag() == Common.MKTAG("AVup")) )
	        buf += buf_size - context.get_length();*/

	    if (buf_size < context.get_length() - (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_PAL8 ? 256*4 : 0)) 
	        return new OutOI(null, -1);

	    ImgConvert.avpicture_fill(frame, buf, avctx.get_pix_fmt(), avctx.get_width(), avctx.get_height());
	    
	    if ( ( (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_PAL8) && (buf_size < context.get_length()) ) ||
	         ( (avctx.get_pix_fmt() != PixelFormat.PIX_FMT_PAL8) && ((PixDesc.av_pix_fmt_descriptors.get(avctx.get_pix_fmt()).get_flags() & PixDesc.PIX_FMT_PAL) != 0) ) ) {
	        frame.set_data(1, UtilsArrays.long_to_byte_le(context.get_palette()));
	    }
		    
	    if (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_PAL8) {
	    	short [] pal = AVPacket.av_packet_get_side_data(avpkt, AVPacketSideDataType.AV_PKT_DATA_PALETTE);
	        

	        if (pal != null) {
	            frame.set_data(1, pal);
	            frame.set_palette_has_changed(1);
	        }
	    }

	    if ( ( (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_BGR24)    ||
	           (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_GRAY8)    ||
	           (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_RGB555LE) ||
	           (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_RGB555BE) ||
	           (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_RGB565LE) ||
	           (avctx.get_pix_fmt() == PixelFormat.PIX_FMT_PAL8) ) &&
	           ( ((frame.get_linesize(0) + 3) & ~3) * avctx.get_height() <= buf_size ) )
	        frame.set_linesize(0, (frame.get_linesize(0) + 3 ) & ~3);
		    

	    if (context.get_flip() != 0)
	        flip(avctx, frame);
	    
	    if ( (avctx.get_codec_tag() == Common.MKTAG("YV12")) || 
	    	 (avctx.get_codec_tag() == Common.MKTAG("YV16")) || 
	    	 (avctx.get_codec_tag() == Common.MKTAG("YV24")) || 
	    	 (avctx.get_codec_tag() == Common.MKTAG("YVU9")) )
	    		frame.FFSWAP_DATA12();
 

	    if ( (avctx.get_codec_tag() == Common.MKTAG("yuv2")) &&
	         (avctx.get_pix_fmt()   == PixelFormat.PIX_FMT_YUYV422) ) {
	        int x, y;
	      /*  uint8_t *line = picture->data[0];
	        for(y = 0; y < avctx->height; y++) {
	            for(x = 0; x < avctx->width; x++)
	                line[2*x + 1] ^= 0x80;
	            line += picture->linesize[0];
	        }*/
	    }
	    
	    frame.set_width(avctx.get_width());
	    frame.set_height(avctx.get_height());

         BufferedImage bi = video_decode_frame(frame.get_data(0), avctx.get_width(),
 	    		avctx.get_height());
         
         frame.set_img(bi);
	 
			
		return new OutOI(frame, buf_size);
	}
	

	private static BufferedImage video_decode_frame(short [] buf, int w, int h) {

		BufferedImage im = new BufferedImage(w, h,
				BufferedImage.TYPE_3BYTE_BGR);
					
		int [] img = convertYUV2RGB(buf, w, h);
		
			int xx = 0;
			int yy = 0;
			for (int i = 0 ; i < img.length ; i++) {
				im.setRGB(xx, yy, img[i]);
	
				xx++;
	
				if (xx >= w) {
					xx = 0;
					yy++;
				}
			}
		
	
		return im;
		
	}
	

    public static int unsigned(byte signedByte) {
        return (int)signedByte & 0xFF;
    }
    
	public static int[] convertYUV2RGB(short[] src, int w, int h) {
        int[] dest = new int[w*h];
        int numPixels = w*h;
        int i = (numPixels << 1)-1;
        int j = numPixels - 1;
        int y0, y1, u, v;
        int r, g, b;
//        int a = 255;
// djs transparency? we don't need no stinking transparency.
        int a = 0;
        while (i > 0) {
            
            y1 = unsigned((byte) src[i--]);
            v  = unsigned((byte) src[i--]) - 128;
            y0 = unsigned((byte) src[i--]);
            u  = unsigned((byte) src[i--]) - 128;
            {
                r = y1 + ((v*1436) >> 10);
                g = y1 - ((u*352 + v*731) >> 10);
                b = y1 + ((u*1814) >> 10);
                r = r < 0 ? 0 : r;
                g = g < 0 ? 0 : g;
                b = b < 0 ? 0 : b;
                r = r > 255 ? 255 : r;
                g = g > 255 ? 255 : g;
                b = b > 255 ? 255 : b;
            }
            dest[j--] = (a << 24) | (r << 16) | (g << 8) | b;
            {
                r = y0 + ((v*1436) >> 10);
                g = y0 - ((u*352 + v*731) >> 10);
                b = y0 + ((u*1814) >> 10);
                r = r < 0 ? 0 : r;
                g = g < 0 ? 0 : g;
                b = b < 0 ? 0 : b;
                r = r > 255 ? 255 : r;
                g = g > 255 ? 255 : g;
                b = b > 255 ? 255 : b;
            }
            dest[j--] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        return dest;
    }
	



	private void flip(AVCodecContext avctx, AVFrame picture) {
		// TODO ??
		/*picture->data[0] += picture->linesize[0] * (avctx->height-1);
		picture->linesize[0] *= -1;*/
	}


	public static PixelFormat ff_find_pix_fmt(ArrayList<PixelFormatTag> tags, int fourcc) {
		for (PixelFormatTag tag : tags) {
			if (tag.get_fourcc() == fourcc) 
				return tag.get_pix_fmt();
		}
		return PixelFormat.PIX_FMT_YUV420P;
	}
	
}

