package uk.ac.liv.ffmpeg.libavformat.img2;

import java.util.ArrayList;


import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;

public class Img2 {

	public static ArrayList<IdStrMap> img_tags = new ArrayList<IdStrMap>();
	
	
	static {
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_MJPEG     , "jpeg"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_MJPEG     , "jpg"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_LJPEG     , "ljpg"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_JPEGLS    , "jls"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_PNG       , "png"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_PNG       , "mng"));
		img_tags.add(new IdStrMap(CodecID.CODEC_ID_PPM       , "ppm"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PPM       , "pnm"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PGM       , "pgm"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PGMYUV    , "pgmyuv"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PBM       , "pbm"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PAM       , "pam"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_MPEG1VIDEO, "mpg1-img"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_MPEG2VIDEO, "mpg2-img"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_MPEG4     , "mpg4-img"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_FFV1      , "ffv1-img"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_RAWVIDEO  , "y"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_RAWVIDEO  , "raw"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_BMP       , "bmp"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_GIF       , "gif"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_TARGA     , "tga"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_TIFF      , "tiff"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_TIFF      , "tif"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SGI       , "sgi"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PTX       , "ptx"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PCX       , "pcx"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "sun"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "ras"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "rs"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "im1"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "im8"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "im24"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_SUNRAST   , "sunras"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_JPEG2000  , "j2k"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_JPEG2000  , "jp2"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_JPEG2000  , "jpc"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_DPX       , "dpx"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_PICTOR    , "pic"));
	    img_tags.add(new IdStrMap(CodecID.CODEC_ID_NONE      , ""));
	}
	

	public static CodecID ff_guess_image2_codec(String filename) {
	    return av_str2id(img_tags, filename);
	}


	static CodecID av_str2id(ArrayList<IdStrMap> tags, String str) {
		int last_point = str.lastIndexOf('.');
		
		if (last_point == -1)
			return CodecID.CODEC_ID_NONE;
		
		str = str.substring(last_point+1, str.length());
		  
		for (IdStrMap tag : tags) {
			if (tag.get_str().toLowerCase().equals(str.toLowerCase()))
				return tag.get_id();
		}
		return CodecID.CODEC_ID_NONE;

	}

}
