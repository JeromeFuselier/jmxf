package uk.ac.liv.ffmpeg.libavcodec.mjpeg;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.Bitstream;
import uk.ac.liv.ffmpeg.libavcodec.PutBitContext;
import uk.ac.liv.ffmpeg.libavcodec.PutBits;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegVideoEnc;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavcodec.Version;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;

public class MjpegEnc extends AVCodec  {
		

	public MjpegEnc() {
		super();
		
		this.name = "mjpeg";	
		this.long_name = "MJPEG (Motion JPEG)";
		this.type = AVMediaType.AVMEDIA_TYPE_VIDEO;
		this.id = CodecID.CODEC_ID_MJPEG;
		this.encode = true;
		this.has_priv_data = true;
		
		this.pix_fmts.add(PixelFormat.PIX_FMT_YUVJ420P);
		this.pix_fmts.add(PixelFormat.PIX_FMT_YUVJ422P);
		
	}
	
	/* MPV_encode_init */
	public int init(AVCodecContext avctx) {
		return MpegVideoEnc.MPV_encode_init(avctx);
	}
	

    public OutOI encode(AVCodecContext avctx, short[] buf, int buf_size, 
    		Object data) {
		return MpegVideoEnc.MPV_encode_picture(avctx, buf, buf_size, data);
    }
    
    
    public int close(AVCodecContext avctx) {
		return MpegVideoEnc.MPV_encode_end(avctx);
    }

	public static void ff_mjpeg_encode_picture_trailer(MpegEncContext s) {
	    ff_mjpeg_encode_stuffing(s.get_pb());
	    PutBits.flush_put_bits(s.get_pb());

	    escape_FF(s, s.get_header_bits() >> 3);

	    Mjpeg.put_marker(s.get_pb(), Mjpeg.EOI);		
	}

	private static void escape_FF(MpegEncContext s, int start) {
	    int size = PutBits.put_bits_count(s.get_pb()) - start * 8;
	    int i, ff_count;
	    short [] buf = Arrays.copyOfRange(s.get_pb().get_buf(), start, s.get_pb().get_buf().length);
	    //int align = (-(size_t)(buf))&3;

	    size >>= 3;

	    ff_count=0;
	    for (i = 0 ; i < size/* && i<align*/ ; i++) {
	        if (buf[i] == 0xFF) 
	        	ff_count++;
	    }
	    
	    for(; i < size-15 ; i += 16) {
	        int acc, v;

	        v = buf[i] << 24 + buf[i+1] << 16 + buf[i+2] << 8 + buf[i+3];
	        acc = (((v & (v>>4))&0x0F0F0F0F)+0x01010101)&0x10101010;
	        v = buf[i+4] << 24 + buf[i+5] << 16 + buf[i+6] << 8 + buf[i+7];
	        acc+=(((v & (v>>4))&0x0F0F0F0F)+0x01010101)&0x10101010;
	        v = buf[i+8] << 24 + buf[i+9] << 16 + buf[i+10] << 8 + buf[i+11];
	        acc+=(((v & (v>>4))&0x0F0F0F0F)+0x01010101)&0x10101010;
	        v = buf[i+12] << 24 + buf[i+13] << 16 + buf[i+14] << 8 + buf[i+15];
	        acc+=(((v & (v>>4))&0x0F0F0F0F)+0x01010101)&0x10101010;

	        acc >>= 4;
	        acc += (acc>>16);
	        acc += (acc>>8);
	        ff_count += acc&0xFF;
	    }
	    for(; i<size; i++){
	        if(buf[i]==0xFF) ff_count++;
	    }

	    if(ff_count==0) return;

	    PutBits.flush_put_bits(s.get_pb());
	    PutBits.skip_put_bytes(s.get_pb(), ff_count);

	    for(i=size-1; ff_count != 0; i--){
	        int v = buf[i];

	        if(v==0xFF){
	            buf[i+ff_count]= 0;
	            ff_count--;
	        }

	        buf[i+ff_count]= (short) v;
	    }
	}

	private static void ff_mjpeg_encode_stuffing(PutBitContext pbc) {
	    int length = (-PutBits.put_bits_count(pbc)) & 7;
	    if (length != 0) 
	    	PutBits.put_bits(pbc, length, (1<<length)-1);
	}

	public static void ff_mjpeg_encode_picture_header(MpegEncContext s) {
	    boolean lossless = s.get_avctx().get_codec_id() != CodecID.CODEC_ID_MJPEG;

	    Mjpeg.put_marker(s.get_pb(), Mjpeg.SOI);

	    jpeg_put_comments(s);

	    jpeg_table_header(s);

	    switch (s.get_avctx().get_codec_id()) {
	    case CODEC_ID_MJPEG:  
	    	Mjpeg.put_marker(s.get_pb(), Mjpeg.SOF0 ); 
	    	break;
	    case CODEC_ID_LJPEG: 
	    	Mjpeg.put_marker(s.get_pb(), Mjpeg.SOF3 );
	    	break;
	    }

	    PutBits.put_bits(s.get_pb(), 16, 17);
	    if (lossless && s.get_avctx().get_pix_fmt() == PixelFormat.PIX_FMT_BGRA)
	    	PutBits.put_bits(s.get_pb(), 8, 9); /* 9 bits/component RCT */
	    else
	    	PutBits.put_bits(s.get_pb(), 8, 8); /* 8 bits/component */
	    PutBits.put_bits(s.get_pb(), 16, s.get_height());
	    PutBits.put_bits(s.get_pb(), 16, s.get_width());
	    PutBits.put_bits(s.get_pb(), 8, 3); /* 3 components */

	    /* Y component */
	    PutBits.put_bits(s.get_pb(), 8, 1); /* component number */
	    PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_hsample(0)); /* H factor */
	    PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_vsample(0)); /* V factor */
	    PutBits.put_bits(s.get_pb(), 8, 0); /* select matrix */

	    /* Cb component */
	    PutBits.put_bits(s.get_pb(), 8, 2); /* component number */
	    PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_hsample(1)); /* H factor */
	    PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_vsample(1)); /* V factor */
	
	    PutBits.put_bits(s.get_pb(), 8, 0); /* select matrix */

	    /* Cr component */
		PutBits.put_bits(s.get_pb(), 8, 3); /* component number */
		PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_hsample(2)); /* H factor */
		PutBits.put_bits(s.get_pb(), 4, s.get_mjpeg_vsample(2)); /* V factor */

		PutBits.put_bits(s.get_pb(), 8, 0); /* select matrix */
	

	    /* scan header */
		Mjpeg.put_marker(s.get_pb(), Mjpeg.SOS);
		PutBits.put_bits(s.get_pb(), 16, 12); /* length */
		PutBits.put_bits(s.get_pb(), 8, 3); /* 3 components */

	    /* Y component */
		PutBits.put_bits(s.get_pb(), 8, 1); /* index */
		PutBits.put_bits(s.get_pb(), 4, 0); /* DC huffman table index */
		PutBits.put_bits(s.get_pb(), 4, 0); /* AC huffman table index */

	    /* Cb component */
		PutBits.put_bits(s.get_pb(), 8, 2); /* index */
		PutBits.put_bits(s.get_pb(), 4, 1); /* DC huffman table index */
		PutBits.put_bits(s.get_pb(), 4, lossless ? 0 : 1); /* AC huffman table index */

	    /* Cr component */
		PutBits.put_bits(s.get_pb(), 8, 3); /* index */
		PutBits.put_bits(s.get_pb(), 4, 1); /* DC huffman table index */
		PutBits.put_bits(s.get_pb(), 4, lossless ? 0 : 1); /* AC huffman table index */

		PutBits.put_bits(s.get_pb(), 8, lossless ? s.get_avctx().get_prediction_method()+1 : 0); /* Ss (not used) */

	    switch (s.get_avctx().get_codec_id()) {
	    case CODEC_ID_MJPEG:  
	    	PutBits.put_bits(s.get_pb(), 8, 63); 
	    	break; /* Se (not used) */
	    case CODEC_ID_LJPEG:  
	    	PutBits.put_bits(s.get_pb(), 8,  0);
	    	break; /* not used */
	    }

	    PutBits.put_bits(s.get_pb(), 8, 0); /* Ah/Al (not used) */
	}

	private static void jpeg_table_header(MpegEncContext s) {
	    PutBitContext p = s.get_pb();
	    int i, j, size;
	    //uint8_t *ptr;

	    /* quant matrixes */
	    Mjpeg.put_marker(p, Mjpeg.DQT);
	    PutBits.put_bits(p, 16, 2 + 1 * (1 + 64));
	    PutBits.put_bits(p, 4, 0); /* 8 bit precision */
	    PutBits.put_bits(p, 4, 0); /* table 0 */
	    for (i = 0 ; i < 64 ; i++) {
	        j = s.get_intra_scantable().get_permutated(i);
	        PutBits.put_bits(p, 8, s.get_intra_matrix(j));
	    }

	    /* huffman table */
	    Mjpeg.put_marker(p, Mjpeg.DHT);
	    PutBits.flush_put_bits(p);
	    //ptr = put_bits_ptr(p);
	    PutBits.put_bits(p, 16, 0); /* patched later */
	    size = 2;
	    size += put_huffman_table(s, 0, 0, Mjpeg.ff_mjpeg_bits_dc_luminance,
	    		Mjpeg.ff_mjpeg_val_dc);
	    size += put_huffman_table(s, 0, 1, Mjpeg.ff_mjpeg_bits_dc_chrominance,
	    		Mjpeg.ff_mjpeg_val_dc);

	    size += put_huffman_table(s, 1, 0, Mjpeg.ff_mjpeg_bits_ac_luminance,
	    		Mjpeg.ff_mjpeg_val_ac_luminance);
	    size += put_huffman_table(s, 1, 1, Mjpeg.ff_mjpeg_bits_ac_chrominance,
	    		Mjpeg.ff_mjpeg_val_ac_chrominance);
	   // AV_WB16(ptr, size);
	}

	/* table_class: 0 = DC coef, 1 = AC coefs */
	private static int put_huffman_table(MpegEncContext s, int table_class, int table_id,
			int[] bits_table, int[] value_table) {
	    PutBitContext p = s.get_pb();
	    int n, i;

	    PutBits.put_bits(p, 4, table_class);
	    PutBits.put_bits(p, 4, table_id);

	    n = 0;
	    for (i = 1 ; i <= 16 ; i++) {
	        n += bits_table[i];
	        PutBits.put_bits(p, 8, bits_table[i]);
	    }

	    for (i = 0 ; i < n ; i++)
	    	PutBits.put_bits(p, 8, value_table[i]);

	    return n + 17;
	}

	private static void jpeg_put_comments(MpegEncContext s) {
	    PutBitContext p = s.get_pb();
	    int size;
	    //uint8_t *ptr;

	    if (s.get_avctx().get_sample_aspect_ratio().get_num() != 0 /* && !lossless */) {
	    /* JFIF header */
		    Mjpeg.put_marker(p, Mjpeg.APP0);
		    PutBits.put_bits(p, 16, 16);
		    Bitstream.ff_put_string(p, "JFIF", 1); /* this puts the trailing zero-byte too */
		    PutBits.put_bits(p, 16, 0x0102); /* v 1.02 */
		    PutBits.put_bits(p, 8, 0); /* units type: 0 - aspect ratio */
		    PutBits.put_bits(p, 16, s.get_avctx().get_sample_aspect_ratio().get_num());
		    PutBits.put_bits(p, 16, s.get_avctx().get_sample_aspect_ratio().get_den());
		    PutBits.put_bits(p, 8, 0); /* thumbnail width */
		    PutBits.put_bits(p, 8, 0); /* thumbnail height */
	    }

	    /* comment */
	    if (!s.has_flag(AVCodec.CODEC_FLAG_BITEXACT)) {
	        Mjpeg.put_marker(p, Mjpeg.COM);
	        PutBits.flush_put_bits(p);
	        
	        PutBits.put_bits(p, 16, 0); /* patched later */
	        Bitstream.ff_put_string(p, Version.LIBAVCODEC_IDENT, 1);
	        //size = strlen(LIBAVCODEC_IDENT)+3;
	       // AV_WB16(ptr, size);
	    }

	    if ( s.get_avctx().get_pix_fmt() == PixelFormat.PIX_FMT_YUV420P ||
	    	 s.get_avctx().get_pix_fmt() == PixelFormat.PIX_FMT_YUV422P ||
	         s.get_avctx().get_pix_fmt() == PixelFormat.PIX_FMT_YUV444P) {
	        Mjpeg.put_marker(p, Mjpeg.COM);
	        PutBits.flush_put_bits(p);
	        //ptr = put_bits_ptr(p);
	        PutBits.put_bits(p, 16, 0); /* patched later */
	        Bitstream.ff_put_string(p, "CS=ITU601", 1);
	        //size = strlen("CS=ITU601")+3;
	        //AV_WB16(ptr, size);
	    }
	}
	

	
}

