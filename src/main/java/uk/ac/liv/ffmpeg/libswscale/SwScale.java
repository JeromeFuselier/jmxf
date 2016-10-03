package uk.ac.liv.ffmpeg.libswscale;

import uk.ac.liv.ffmpeg.libavutil.BSwap;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class SwScale {
	

	/* values for the flags, the stuff on the command line is different */
	public static final int SWS_FAST_BILINEAR =     1;
	public static final int SWS_BILINEAR      =     2;
	public static final int SWS_BICUBIC       =     4;
	public static final int SWS_X             =     8;
	public static final int SWS_POINT         =  0x10;
	public static final int SWS_AREA          =  0x20;
	public static final int SWS_BICUBLIN      =  0x40;
	public static final int SWS_GAUSS         =  0x80;
	public static final int SWS_SINC          = 0x100;
	public static final int SWS_LANCZOS       = 0x200;
	public static final int SWS_SPLINE        = 0x400;

	public static final int SWS_SRC_V_CHR_DROP_MASK  = 0x30000;
	public static final int SWS_SRC_V_CHR_DROP_SHIFT =      16;

	public static final int SWS_PARAM_DEFAULT = 123456;

	public static final int SWS_PRINT_INFO = 0x1000;

	//the following 3 flags are not completely implemented
	//internal chrominace subsampling info
	public static final int SWS_FULL_CHR_H_INT =  0x2000;
	//input subsampling info
	public static final int SWS_FULL_CHR_H_INP =  0x4000;
	public static final int SWS_DIRECT_BGR     =  0x8000;
	public static final int SWS_ACCURATE_RND   = 0x40000;
	public static final int SWS_BITEXACT       = 0x80000;


	public static final float SWS_MAX_REDUCE_CUTOFF = 0.002f;

	public static final int SWS_CS_ITU709    = 1;
	public static final int SWS_CS_FCC       = 4;
	public static final int SWS_CS_ITU601    = 5;
	public static final int SWS_CS_ITU624    = 5;
	public static final int SWS_CS_SMPTE170M = 5;
	public static final int SWS_CS_SMPTE240M = 7;
	public static final int SWS_CS_DEFAULT   = 5;
	
	public static final int RGB2YUV_SHIFT = 15;
	public static final int BY = ( (int)(0.114*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int BV = (-(int)(0.081*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int BU = ( (int)(0.500*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int GY = ( (int)(0.587*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int GV = (-(int)(0.419*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int GU = (-(int)(0.331*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int RY = ( (int)(0.299*219/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int RV = ( (int)(0.500*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	public static final int RU = (-(int)(0.169*224/255*(1<<RGB2YUV_SHIFT)+0.5));
	
	
	public static byte[] ff_sws_pb_64 = {64, 64, 64, 64, 64, 64, 64, 64 };
	
	public static byte[][] dither_8x8_128 = {
			{  36, 68, 60, 92, 34, 66, 58, 90,},
			{ 100,  4,124, 28, 98,  2,122, 26,},
			{  52, 84, 44, 76, 50, 82, 42, 74,},
			{ 116, 20,108, 12,114, 18,106, 10,},
			{  32, 64, 56, 88, 38, 70, 62, 94,},
			{  96,  0,120, 24,102,  6,126, 30,},
			{  48, 80, 40, 72, 54, 86, 46, 78,},
			{ 112, 16,104,  8,118, 22,110, 14,},
			};
	
	public static byte [] flat64 = {64, 64, 64, 64, 64, 64, 64, 64};
	
	public static byte [][][] dithers = {
			{
			  {   0,  1,  0,  1,  0,  1,  0,  1,},
			  {   1,  0,  1,  0,  1,  0,  1,  0,},
			  {   0,  1,  0,  1,  0,  1,  0,  1,},
			  {   1,  0,  1,  0,  1,  0,  1,  0,},
			  {   0,  1,  0,  1,  0,  1,  0,  1,},
			  {   1,  0,  1,  0,  1,  0,  1,  0,},
			  {   0,  1,  0,  1,  0,  1,  0,  1,},
			  {   1,  0,  1,  0,  1,  0,  1,  0,},
			},{
			  {   1,  2,  1,  2,  1,  2,  1,  2,},
			  {   3,  0,  3,  0,  3,  0,  3,  0,},
			  {   1,  2,  1,  2,  1,  2,  1,  2,},
			  {   3,  0,  3,  0,  3,  0,  3,  0,},
			  {   1,  2,  1,  2,  1,  2,  1,  2,},
			  {   3,  0,  3,  0,  3,  0,  3,  0,},
			  {   1,  2,  1,  2,  1,  2,  1,  2,},
			  {   3,  0,  3,  0,  3,  0,  3,  0,},
			},{
			  {   2,  4,  3,  5,  2,  4,  3,  5,},
			  {   6,  0,  7,  1,  6,  0,  7,  1,},
			  {   3,  5,  2,  4,  3,  5,  2,  4,},
			  {   7,  1,  6,  0,  7,  1,  6,  0,},
			  {   2,  4,  3,  5,  2,  4,  3,  5,},
			  {   6,  0,  7,  1,  6,  0,  7,  1,},
			  {   3,  5,  2,  4,  3,  5,  2,  4,},
			  {   7,  1,  6,  0,  7,  1,  6,  0,},
			},{
			  {   4,  8,  7, 11,  4,  8,  7, 11,},
			  {  12,  0, 15,  3, 12,  0, 15,  3,},
			  {   6, 10,  5,  9,  6, 10,  5,  9,},
			  {  14,  2, 13,  1, 14,  2, 13,  1,},
			  {   4,  8,  7, 11,  4,  8,  7, 11,},
			  {  12,  0, 15,  3, 12,  0, 15,  3,},
			  {   6, 10,  5,  9,  6, 10,  5,  9,},
			  {  14,  2, 13,  1, 14,  2, 13,  1,},
			},{
			  {   9, 17, 15, 23,  8, 16, 14, 22,},
			  {  25,  1, 31,  7, 24,  0, 30,  6,},
			  {  13, 21, 11, 19, 12, 20, 10, 18,},
			  {  29,  5, 27,  3, 28,  4, 26,  2,},
			  {   8, 16, 14, 22,  9, 17, 15, 23,},
			  {  24,  0, 30,  6, 25,  1, 31,  7,},
			  {  12, 20, 10, 18, 13, 21, 11, 19,},
			  {  28,  4, 26,  2, 29,  5, 27,  3,},
			},{
			  {  18, 34, 30, 46, 17, 33, 29, 45,},
			  {  50,  2, 62, 14, 49,  1, 61, 13,},
			  {  26, 42, 22, 38, 25, 41, 21, 37,},
			  {  58, 10, 54,  6, 57,  9, 53,  5,},
			  {  16, 32, 28, 44, 19, 35, 31, 47,},
			  {  48,  0, 60, 12, 51,  3, 63, 15,},
			  {  24, 40, 20, 36, 27, 43, 23, 39,},
			  {  56,  8, 52,  4, 59, 11, 55,  7,},
			},{
			  {  18, 34, 30, 46, 17, 33, 29, 45,},
			  {  50,  2, 62, 14, 49,  1, 61, 13,},
			  {  26, 42, 22, 38, 25, 41, 21, 37,},
			  {  58, 10, 54,  6, 57,  9, 53,  5,},
			  {  16, 32, 28, 44, 19, 35, 31, 47,},
			  {  48,  0, 60, 12, 51,  3, 63, 15,},
			  {  24, 40, 20, 36, 27, 43, 23, 39,},
			  {  56,  8, 52,  4, 59, 11, 55,  7,},
			},{
			  {  36, 68, 60, 92, 34, 66, 58, 90,},
			  { 100,  4,124, 28, 98,  2,122, 26,},
			  {  52, 84, 44, 76, 50, 82, 42, 74,},
			  { 116, 20,108, 12,114, 18,106, 10,},
			  {  32, 64, 56, 88, 38, 70, 62, 94,},
			  {  96,  0,120, 24,102,  6,126, 30,},
			  {  48, 80, 40, 72, 54, 86, 46, 78,},
			  { 112, 16,104,  8,118, 22,110, 14,},
			}};

	
	public static void yuy2ToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] pal) {
		for (int i = 0 ; i < width ; i++) {
			dstU[i]= src1[4*i + 1];
	        dstV[i]= src1[4*i + 3];
	    }
	}


	public static void uyvyToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] unused) {
		for (int i = 0 ; i < width ; i++) {
			dstU[i]= src1[4*i + 0];
	        dstV[i]= src1[4*i + 2];
	    }		
	}


	public static void nv12ToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] unused) {
	    nvXXtoUV_c(dstU, dstV, src1, width);		
	}


	private static void nvXXtoUV_c(short [] dstU, short [] dstV, short [] src1,
			int width) {
		for (int i = 0 ; i < width ; i++) {
			dstU[i]= src1[4*i + 0];
	        dstV[i]= src1[4*i + 1];
	    }				
	}

	public static void nv21ToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] unused) {
	    nvXXtoUV_c(dstV, dstU, src1, width);
		
	}


	public static void palToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] pal) {
	    
		for (int i = 0 ; i < width ; i++) {
	        int p = pal[src1[i]];

	        dstU[i] = (byte) ((p >> 8) << 6);
	        dstV[i] = (byte) ((p >> 16) << 6);
	    }
	}


	public static void hScale16NX_c(int [] dst, int dstW, int [] src, int srcW,
			int xInc, int [] filter, int [] filterPos, long filterSize, int shift) {
	    for (int i = 0 ; i < dstW ; i++) {
	        int srcPos = filterPos[i];
	        int val = 0;
	        for (int j = 0 ; j < filterSize ; j++) {
	            val += ((int)BSwap.av_bswap16(src[srcPos + j]))*filter[(int) (filterSize*i + j)];
	        }
	        dst[i] = (int) Mathematics.FFMIN(val>>shift, (1<<15)-1); // the cubic equation does overflow ...
	    }
	}


	public static void hScale16N_c(int[] dst, int dstW, int[] src, int srcW,
			int xInc, int[] filter, int[] filterPos, long filterSize, int shift) {
	    for (int i = 0 ; i < dstW ; i++) {
	        int srcPos = filterPos[i];
	        int val = 0;
	        for (int j = 0 ; j < filterSize ; j++) {
	            val += ((int)src[srcPos + j])*filter[(int) (filterSize*i + j)];
	        }
	        dst[i] = (int) Mathematics.FFMIN(val>>shift, (1<<15)-1); // the cubic equation does overflow ...
	    }
		
	}


	public static void bswap16UV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		
		for (int i = 0 ; i < width ; i++) {
			dstU[i] = (byte) BSwap.av_bswap16(src1[i]);
			dstV[i] = (byte) BSwap.av_bswap16(src2[i]);
		}
	}
	

	public static void rgb48BEToUV_half_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int [] pal) {
		rgb48ToUV_half_c_template(dstU, dstV, src1, src2, width, 
				PixelFormat.PIX_FMT_RGB48BE);
	}

	public static void rgb48LEToUV_half_c(short [] dstU, short [] dstV,
			short [] src1, short [] src2, int width, int[] pal) {
		rgb48ToUV_half_c_template(dstU, dstV, src1, src2, width, 
				PixelFormat.PIX_FMT_RGB48LE);		
	}

	public static void bgr48BEToUV_half_c(short [] dstU, short [] dstV,
			short [] src1, short [] src2, int width, int[] pal) {
		rgb48ToUV_half_c_template(dstU, dstV, src1, src2, width, 
				PixelFormat.PIX_FMT_BGR48BE);		
	}

	public static void bgr48LEToUV_half_c(short [] dstU, short [] dstV,
			short [] src1, short [] src2, int width, int[] pal) {
		rgb48ToUV_half_c_template(dstU, dstV, src1, src2, width, 
				PixelFormat.PIX_FMT_BGR48LE);		
	}
	
	private static void rgb48ToUV_half_c_template(short [] dstU, short [] dstV,
			short [] src1, short [] src2, int width, PixelFormat origin) {
		
	    for (int i = 0; i < width; i++) {
	        int r_b = (input_pixel(origin, src1[6 * i + 0]) + input_pixel(origin, src1[6 * i + 3]) + 1) >> 1;
	        int   g = (input_pixel(origin, src1[6 * i + 1]) + input_pixel(origin, src1[6 * i + 4]) + 1) >> 1;
	        int b_r = (input_pixel(origin, src1[6 * i + 2]) + input_pixel(origin, src1[6 * i + 5]) + 1) >> 1;
	        
	        int r = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? b_r : r_b;
	        int b = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? r_b : b_r;
	        
	        

	        dstU[i]= (byte) ((RU*r + GU*g + BU*b + (0x10001<<(RGB2YUV_SHIFT-1))) >> RGB2YUV_SHIFT);
	        dstV[i]= (byte) ((RV*r + GV*g + BV*b + (0x10001<<(RGB2YUV_SHIFT-1))) >> RGB2YUV_SHIFT);
	    }
		
	}


	private static int input_pixel(PixelFormat origin, int pos) {
		if (SwScaleInternal.isBE(origin)) {			
			return BSwap.av_bswap16((short) pos);
		} else {
			return pos;
		}			
	}


	public static void bgr32ToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGBA, 
				16, 0, 0, 0,0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr321ToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_ARGB, 
				16, 0,  0, 8, 0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void rgb32ToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGRA, 
				0, 0, 16, 0,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void rgb321ToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_ABGR, 
				0, 0, 16, 8,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr16leToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR565LE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr15leToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR555LE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);	
	}
	
	public static void rgb16leToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB565LE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15leToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB555LE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}
	
	public static void bgr16beToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR565BE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr15beToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR555BE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);
	}
	
	public static void rgb16beToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB565BE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15beToUV_half_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_half_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB555BE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}


	private static void rgb16_32ToUV_half_c_template(short [] dstU, short [] dstV,
			short [] src, int width, PixelFormat origin, int shr, int shg,
			int shb, int shp, int maskr, int maskg, int maskb, int rsh, int gsh,
			int bsh, int S) {
	    int ru = RU << rsh;
	    int gu = GU << gsh;
	    int bu = BU << bsh;
        int rv = RV << rsh;
        int gv = GV << gsh;
        int bv = BV << bsh;
        int rnd = (256 << S) + (1 << (S-6) );
        int maskgx = ~(maskr | maskb);
		
		maskr |= maskr << 1; 
		maskb |= maskb << 1; 
		maskg |= maskg << 1;
		
		for (int i = 0; i < width; i++) {
		  int px0 = input_pixel(origin, 2 * i + 0) >> shp;
		  int px1 = input_pixel(origin, 2 * i + 1) >> shp;
		  int b, r, g = (px0 & maskgx) + (px1 & maskgx);
		  int rb = px0 + px1 - g;
		
		  b = (rb & maskb) >> shb;
		  if ( (shp != 0) || 
			   (origin == PixelFormat.PIX_FMT_BGR565LE) || 
			   (origin == PixelFormat.PIX_FMT_BGR565BE) ||
		       (origin == PixelFormat.PIX_FMT_RGB565LE) || 
		       (origin == PixelFormat.PIX_FMT_RGB565BE) ) {
		      g >>= shg;
		  } else {
		      g = (g  & maskg) >> shg;
		  }
		  r = (rb & maskr) >> shr;
		
		  dstU[i] = (byte) ((ru * r + gu * g + bu * b + rnd) >> ((S)-6+1));
		  dstV[i] = (byte) ((rv * r + gv * g + bv * b + rnd) >> ((S)-6+1));
		}
		
	}


	public static void bgr24ToUV_half_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        int b= src1[6*i + 0] + src1[6*i + 3];
	        int g= src1[6*i + 1] + src1[6*i + 4];
	        int r= src1[6*i + 2] + src1[6*i + 5];

	        dstU[i]= (byte) ((RU*r + GU*g + BU*b + (256<<RGB2YUV_SHIFT) + (1<<(RGB2YUV_SHIFT-6)))>>(RGB2YUV_SHIFT-5));
	        dstV[i]= (byte) ((RV*r + GV*g + BV*b + (256<<RGB2YUV_SHIFT) + (1<<(RGB2YUV_SHIFT-6)))>>(RGB2YUV_SHIFT-5));
	    }
		
	}


	public static void rgb24ToUV_half_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) { 
		for (int i = 0 ; i < width ; i++) {
	        int r= src1[6*i + 0] + src1[6*i + 3];
	        int g= src1[6*i + 1] + src1[6*i + 4];
	        int b= src1[6*i + 2] + src1[6*i + 5];

	        dstU[i]= (byte) ((RU*r + GU*g + BU*b + (256<<RGB2YUV_SHIFT) + (1<<(RGB2YUV_SHIFT-6)))>>(RGB2YUV_SHIFT-5));
	        dstV[i]= (byte) ((RV*r + GV*g + BV*b + (256<<RGB2YUV_SHIFT) + (1<<(RGB2YUV_SHIFT-6)))>>(RGB2YUV_SHIFT-5));
	    }		
	}


	public static void rgb48BEToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		rgb48ToUV_c_template(dstU, dstV, src1, src2, width, PixelFormat.PIX_FMT_RGB48BE);		
	}
	
	public static void rgb48LEToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		rgb48ToUV_c_template(dstU, dstV, src1, src2, width, PixelFormat.PIX_FMT_RGB48LE);		
	}

	public static void bgr48BEToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		rgb48ToUV_c_template(dstU, dstV, src1, src2, width, PixelFormat.PIX_FMT_BGR48BE);		
	}
	
	public static void bgr48LEToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		rgb48ToUV_c_template(dstU, dstV, src1, src2, width, PixelFormat.PIX_FMT_BGR48LE);		
	}


	private static void rgb48ToUV_c_template(short [] dstU, short [] dstV,
			short [] src1, short [] src2, int width, PixelFormat origin) {
	    for (int i = 0 ; i < width ; i++) {
	        int r_b = input_pixel(origin, src1[i*3+0]);
	        int   g = input_pixel(origin, src1[i*3+1]);
	        int b_r = input_pixel(origin, src1[i*3+2]);	        

	        int r = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? b_r : r_b;
	        int b = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? r_b : b_r;

	        dstU[i] = (byte) ((RU*r + GU*g + BU*b + (0x10001<<(RGB2YUV_SHIFT-1))) >> RGB2YUV_SHIFT);
	        dstV[i] = (byte) ((RV*r + GV*g + BV*b + (0x10001<<(RGB2YUV_SHIFT-1))) >> RGB2YUV_SHIFT);
	    }		
	}




	public static void bgr32ToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGBA, 
				16, 0, 0, 0,0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr321ToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_ARGB, 
				16, 0,  0, 8, 0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void rgb32ToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGRA, 
				0, 0, 16, 0,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void rgb321ToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_ABGR, 
				0, 0, 16, 8,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr16leToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR565LE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);		
	}
	
	public static void bgr15leToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR555LE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);	
	}
	
	public static void rgb16leToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB565LE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15leToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB555LE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}
	
	public static void bgr16beToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR565BE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr15beToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_BGR555BE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);
	}
	
	public static void rgb16beToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB565BE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15beToUV_c(short [] dstU, short [] dstV, short [] src,
			short [] dummy, int width, int[] pal) {
		rgb16_32ToUV_c_template(dstU, dstV, src, width, PixelFormat.PIX_FMT_RGB555BE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}


	private static void rgb16_32ToUV_c_template(short [] dstU, short [] dstV,
			short [] src, int width, PixelFormat origin, int shr, int shg,
			int shb, int shp, int maskr, int maskg, int maskb, int rsh, int gsh,
			int bsh, int S) {
	   	int ru = RU << rsh;
	   	int gu = GU << gsh;
	   	int bu = BU << bsh;
	   	int rv = RV << rsh;
	   	int gv = GV << gsh;
	   	int bv = BV << bsh;
	   	int rnd = (256<<((S)-1)) + (1<<(S-7));

	   	for (int i = 0 ; i < width ; i++) {
	   		int px = input_pixel(origin, i) >> shp;
	   		int b = (px & maskb) >> shb;
	   		int g = (px & maskg) >> shg;
	   		int r = (px & maskr) >> shr;
			
	   		dstU[i] = (short) ((ru * r + gu * g + bu * b + rnd) >> ((S)-6));
	   		dstV[i] = (short) ((rv * r + gv * g + bv * b + rnd) >> ((S)-6));
	   	}
		
	}


	public static void bgr24ToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
		for (int i = 0 ; i < width ; i++) {
		        int b= src1[3*i + 0];
		        int g= src1[3*i + 1];
		        int r= src1[3*i + 2];

		        dstU[i]= (short) ((RU*r + GU*g + BU*b + (256<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
		        dstV[i]= (short) ((RV*r + GV*g + BV*b + (256<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
		    }
	}


	public static void rgb24ToUV_c(short [] dstU, short [] dstV, short [] src1,
			short [] src2, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        int r= src1[3*i + 0];
	        int g= src1[3*i + 1];
	        int b= src1[3*i + 2];

	        dstU[i]= (short) ((RU*r + GU*g + BU*b + (256<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
	        dstV[i]= (short) ((RV*r + GV*g + BV*b + (256<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
	    }
	}

	public static void bswap16Y_c(short [] dst, short  [] src, int width, int[] pal) {
		for (int i = 0 ; i < width ; i++) {
	        dst[i] = (short) BSwap.av_bswap16(src[i]);
	    }
		
	}

	public static void yuy2ToY_c(short [] dst, short [] src, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++)
	        dst[i] = src[2*i];
		
	}


	public static void uyvyToY_c(short [] dst, short [] src, int width, int[] pal) {
		for (int i = 0 ; i < width ; i++)
			dst[i] = src[2*i+1];
	}


	public static void bgr24ToY_c(short [] dst, short [] src, int width, int[] pal) { 
		for (int i = 0 ; i < width ; i++) {
	        int b = src[i*3+0];
	        int g = src[i*3+1];
	        int r = src[i*3+2];
	
	        dst[i]= (short) ((RY*r + GY*g + BY*b + (32<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
		}
	}
	  
	public static void bgr32ToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGBA, 
				16, 0,  0, 0, 0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr321ToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_ARGB, 
				16, 0,  0, 8, 0xFF0000, 0xFF00,   0x00FF,  8, 0,  8, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb32ToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGRA, 
			0, 0, 16, 0,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb321ToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_ABGR, 
				0, 0, 16, 8,   0x00FF, 0xFF00, 0xFF0000,  8, 0,  8, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr16leToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR565LE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr15leToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR555LE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);
	}
	
	public static void rgb16leToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB565LE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15leToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB555LE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}
	
	public static void bgr16beToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR565BE, 
				0, 0,  0, 0,   0x001F, 0x07E0,   0xF800, 11, 5,  0, RGB2YUV_SHIFT+8);
	}
	
	public static void bgr15beToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR555BE, 
				0, 0,  0, 0,   0x001F, 0x03E0,   0x7C00, 10, 5,  0, RGB2YUV_SHIFT+7);
	}
	
	public static void rgb16beToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB565BE, 
				0, 0,  0, 0,   0xF800, 0x07E0,   0x001F,  0, 5, 11, RGB2YUV_SHIFT+8);
	}
	
	public static void rgb15beToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb16_32ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB555BE, 
				0, 0,  0, 0,   0x7C00, 0x03E0,   0x001F,  0, 5, 10, RGB2YUV_SHIFT+7);
	}


	private static void rgb16_32ToY_c_template(short [] dst, short [] src,
			int width, PixelFormat origin, int shr, int shg, int shb, int shp,
			int maskr, int maskg, int maskb, int rsh, int gsh, int bsh, int S) {
		int ry = RY << rsh;
		int gy = GY << gsh;
		int by = BY << bsh;
		int rnd = (32<<((S)-1)) + (1<<(S-7));
		for (int i = 0 ; i < width ; i++) {
	        int px = input_pixel(origin, i) >> shp;
	        int b = (px & maskb) >> shb;
	        int g = (px & maskg) >> shg;
	        int r = (px & maskr) >> shr;

	        dst[i] = (byte) ((ry * r + gy * g + by * b + rnd) >> ((S)-6));
	    }		
	}


	public static void rgb24ToY_c(short [] dst, short [] src, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        int r= src[i*3+0];
	        int g= src[i*3+1];
	        int b= src[i*3+2];

	        dst[i]= (byte) ((RY*r + GY*g + BY*b + (32<<(RGB2YUV_SHIFT-1)) + (1<<(RGB2YUV_SHIFT-7)))>>(RGB2YUV_SHIFT-6));
	    }
		
	}


	public static void palToY_c(short [] dst, short [] src, int width, int[] pal) {
	    for (int i=0; i<width; i++) {
	        int d= src[i];

	        dst[i]= (byte) ((pal[d] & 0xFF)<<6);
	    }
	}


	public static void monoblack2Y_c(short [] dst, short [] src, int width,
			int[] pal) {
		int i, j;
	    for (i = 0 ; i < width / 8 ; i++) {
	        int d= src[i];
	        for(j = 0 ; j < 8 ; j++)
	            dst[8*i+j]= (byte) (((d>>(7-j))&1)*16383);
	    }
	    if ( (width & 7) != 0) {
	        int d= src[i];
	        for (j = 0 ; j < (width&7) ; j++)
	            dst[8*i+j]= (byte) (((d>>(7-j))&1)*16383);
	    }
		
	}


	public static void monowhite2Y_c(short [] dst, short [] src, int width,
			int[] pal) {
	    int i, j;
	    for (i=0; i<width/8; i++) {
	        int d= ~src[i];
	        for(j=0; j<8; j++)
	            dst[8*i+j]= (byte) (((d>>(7-j))&1)*16383);
	    }
	    if ((width&7)!= 0) {
	        int d= ~src[i];
	        for(j=0; j<(width&7); j++)
	            dst[8*i+j]= (byte) (((d>>(7-j))&1)*16383);
	    }
		
	}


	public static void rgb48BEToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb48ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB48BE);
	}
	
	public static void rgb48LEToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb48ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_RGB48LE);
	}

	public static void bgr48BEToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb48ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR48BE);
	}
	
	public static void bgr48LEToY_c(short [] dst, short [] src, int width, int[] pal) {
		rgb48ToY_c_template(dst, src, width, PixelFormat.PIX_FMT_BGR48LE);
	}


	private static void rgb48ToY_c_template(short [] dst, short [] src, int width,
			PixelFormat origin) {
	    for (int i = 0 ; i < width ; i++) {
	        int r_b = input_pixel(origin, src[i*3+0]);
	        int   g = input_pixel(origin, src[i*3+1]);
	        int b_r = input_pixel(origin, src[i*3+2]);        

	        int r = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? b_r : r_b;
	        int b = ( (origin == PixelFormat.PIX_FMT_BGR48BE) || 
	        		  (origin == PixelFormat.PIX_FMT_BGR48LE) ) ? r_b : b_r;

	        dst[i] = (short) ((RY*r + GY*g + BY*b + (0x2001<<(RGB2YUV_SHIFT-1))) >> RGB2YUV_SHIFT);
	    }
	}


	public static void rgbaToA_c(short[] dst, short[] src, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        dst[i]= (byte) (src[4*i+3]<<6);
	    }		
	}


	public static void abgrToA_c(short[] dst, short[] src, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        dst[i]= (byte) (src[4*i]<<6);
	    }
	}


	public static void palToA_c(short[] dst, short[] src1, int width, int[] pal) {
	    for (int i = 0 ; i < width ; i++) {
	        int d= src1[i];

	        dst[i]= (byte) ((pal[d] >> 24)<<6);
	    }
	}


	public static void hScale_c(int[] dst, int dstW, int[] src, int srcW,
			int xInc, int[] filter, int[] filterPos, int filterSize) {
	    for (int i = 0 ; i < dstW; i++) {
	        int srcPos = filterPos[i];
	        int val = 0;
	        for (int j=0; j<filterSize; j++) {
	            val += ((int)src[srcPos + j])*filter[filterSize*i + j];
	        }
	        //filter += hFilterSize;
	        dst[i] = (int) Mathematics.FFMIN(val>>7, (1<<15)-1); // the cubic equation does overflow ...
	        //dst[i] = val>>7;
	    }
	}


	public static void hyscale_fast_c(int[] dst, int dstWidth, int[] src, int srcW,
			int xInc) {
	    int i;
	    int xpos=0;
	    for (i=0;i<dstWidth;i++) {
	        int xx=xpos>>16;
	        int xalpha=(xpos&0xFFFF)>>9;
	        dst[i]= (src[xx]<<7) + (src[xx+1] - src[xx])*xalpha;
	        xpos+=xInc;
	    }
	    for (i=dstWidth-1; (i*xInc)>>16 >=srcW-1; i--)
	        dst[i] = src[srcW-1]*128;
		
	}


	public static void hcscale_fast_c(int[] dst1, int[] dst2, int dstWidth,
			short [] src1, short [] src2, int srcW, int xInc) {
	    int i;
	    int xpos=0;
	    for (i=0;i<dstWidth;i++) {
	        int xx=xpos>>16;
	        int xalpha=(xpos&0xFFFF)>>9;
	        dst1[i]=(src1[xx]*(xalpha^127)+src1[xx+1]*xalpha);
	        dst2[i]=(src2[xx]*(xalpha^127)+src2[xx+1]*xalpha);
	        xpos+=xInc;
	    }
	    for (i=dstWidth-1; (i*xInc)>>16 >=srcW-1; i--) {
	        dst1[i] = src1[srcW-1]*128;
	        dst2[i] = src2[srcW-1]*128;
	    }
	}


	public static void lumRangeFromJpeg_c(int [] dst, int width) {
	    int i;
	    for (i = 0; i < width; i++)
	        dst[i] = (dst[i]*14071 + 33561947)>>14;		
	}


	public static void lumRangeToJpeg_c(int[] dst, int width) {
	    int i;
	    for (i = 0; i < width; i++)
	        dst[i] = (int) ((Mathematics.FFMIN(dst[i],30189)*19077 - 39057361)>>14);
		
	}


	public static void chrRangeFromJpeg_c(int[] dstU, int[] dstV, int width) {
	    int i;
	    for (i = 0; i < width; i++) {
	        dstU[i] = (dstU[i]*1799 + 4081085)>>11; //1469
	        dstV[i] = (dstV[i]*1799 + 4081085)>>11; //1469
	    }
	}


	public static void chrRangeToJpeg_c(int[] dstU, int[] dstV, int width) {
	    int i;
	    for (i = 0; i < width; i++) {
	        dstU[i] = (int) ((Mathematics.FFMIN(dstU[i],30775)*4663 - 9289992)>>12); //-264
	        dstV[i] = (int) ((Mathematics.FFMIN(dstV[i],30775)*4663 - 9289992)>>12); //-264
	    }		
	}


	public static void hScale16_c(SwsContext c, int[] dst, int dstW, int[] src, int srcW,
			int xInc, int[] filter, int[] filterPos, int filterSize) {
	    int i;
	    int bits = PixDesc.av_pix_fmt_descriptors.get(c.get_srcFormat()).get_comp(0).get_depth_minus1();
	    int sh = (bits <= 7) ? 11 : (bits - 4);

	    if( ( SwScaleInternal.isAnyRGB(c.get_srcFormat()) || c.get_srcFormat() == PixelFormat.PIX_FMT_PAL8) && 
	     	PixDesc.av_pix_fmt_descriptors.get(c.get_srcFormat()).get_comp(0).get_depth_minus1() < 15 )
	        sh= 9;

	    for (i = 0; i < dstW; i++) {
	        int j;
	        int srcPos = filterPos[i];
	        int val = 0;

	        for (j = 0; j < filterSize; j++) {
	            val += src[srcPos + j] * filter[filterSize * i + j];
	        }
	        // filter=14 bit, input=16 bit, output=30 bit, >> 11 makes 19 bit
	        dst[i] = (int) Mathematics.FFMIN(val >> sh, (1 << 19) - 1);
	    }
	}


	public static void scale8To16Rv_c(short[] dst, short[] src1, int len) {
	    int i;
	    for (i = len - 1; i >= 0; i--) {
	        dst[i * 2] = dst[i * 2 + 1] = src1[i];
	    }
		
	}


	public static void scale19To15Fw_c(short[] dst, int[] src, int len) {
	    int i;
	    for (i = 0; i < len; i++) {
	        dst[i] = (short) (src[i] >> 4);
	    }
	}


	public static void lumRangeFromJpeg16_c(int[] dst, int width) {
	    int i;
	    for (i = 0; i < width; i++)
	        dst[i] = (dst[i]*(14071/4) + (33561947<<4)/4)>>12;
	}


	public static void lumRangeToJpeg16_c(int[] dst, int width) {
	    int i;
	    for (i = 0; i < width; i++)
	        dst[i] = (int) ((Mathematics.FFMIN(dst[i],30189<<4)*4769 - (39057361<<2))>>12);
	}


	public static void chrRangeFromJpeg16_c(int[] dstU, int[] dstV, int width) {
	    int i;
	    for (i = 0; i < width; i++) {
	        dstU[i] = (dstU[i]*1799 + (4081085<<4))>>11; //1469
	        dstV[i] = (dstV[i]*1799 + (4081085<<4))>>11; //1469
	    }
	}


	public static void chrRangeToJpeg16_c(int[] dstU, int[] dstV, int width) {
	    int i;
	    for (i = 0; i < width; i++) {
	        dstU[i] = (int) ((Mathematics.FFMIN(dstU[i],30775<<4)*4663 - (9289992<<4))>>12); //-264
	        dstV[i] = (int) ((Mathematics.FFMIN(dstV[i],30775<<4)*4663 - (9289992<<4))>>12); //-264
	    }
	}
	
	
	
}
