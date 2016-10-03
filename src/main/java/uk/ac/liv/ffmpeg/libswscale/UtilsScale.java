package uk.ac.liv.ffmpeg.libswscale;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOOOI;

public class UtilsScale {

	public static SwsContext sws_getContext(int srcW, int srcH, PixelFormat srcFormat,
            int dstW, int dstH, PixelFormat dstFormat, int flags,
            SwsFilter srcFilter, SwsFilter dstFilter, double [] param) {

	    SwsContext c = sws_alloc_context();	    

	    c.set_flags(flags);
	    c.set_srcW(srcW);
	    c.set_srcH(srcH);
	    c.set_dstW(dstW);
	    c.set_dstH(dstH);
	    
	    OutOI ret_obj = handle_jpeg(srcFormat);
	    srcFormat = (PixelFormat) ret_obj.get_obj();
	    int ret = ret_obj.get_ret();
	    c.set_srcRange(ret);

	    ret_obj = handle_jpeg(dstFormat);
	    dstFormat = (PixelFormat) ret_obj.get_obj();
	    ret = ret_obj.get_ret();
	    c.set_dstRange(ret);
	    
	    c.set_srcFormat(srcFormat);
	    c.set_dstFormat(dstFormat);

	    if (param != null) {
	        c.set_param(0, param[0]);
	        c.set_param(1, param[1]);
	    }
	    c.sws_setColorspaceDetails(Yuv2Rgb.ff_yuv2rgb_coeffs[SwScale.SWS_CS_DEFAULT], c.get_srcRange(), Yuv2Rgb.ff_yuv2rgb_coeffs[SwScale.SWS_CS_DEFAULT] /* FIXME*/, c.get_dstRange(), 0, 1<<16, 1<<16);

	    if(c.sws_init_context(srcFilter, dstFilter) < 0){
	        c.sws_freeContext();
	        return null;
	    }

	    return c;
	}
	
	
	private static SwsContext sws_alloc_context() {
	    SwsContext c = new SwsContext();

	    c.av_opt_set_defaults();

	    return c;
	}


	private static OutOI handle_jpeg(PixelFormat format) {
	    switch (format) {
	    case PIX_FMT_YUVJ420P: 
	    	return new OutOI(PixelFormat.PIX_FMT_YUV420P, 1);
	    case PIX_FMT_YUVJ422P: 
	    	return new OutOI(PixelFormat.PIX_FMT_YUV422P, 1);
	    case PIX_FMT_YUVJ444P:
	    	return new OutOI(PixelFormat.PIX_FMT_YUV444P, 1);
	    case PIX_FMT_YUVJ440P:
	    	return new OutOI(PixelFormat.PIX_FMT_YUV440P, 1);
	    default:                                          
	    	return new OutOI(format, 0);
	    }
	}


	public static boolean isSupportedIn(PixelFormat x) {
		return ( (x == PixelFormat.PIX_FMT_YUV420P)     || 
				 (x == PixelFormat.PIX_FMT_YUVA420P)    || 
				 (x == PixelFormat.PIX_FMT_YUYV422)     || 
				 (x == PixelFormat.PIX_FMT_UYVY422)     || 
				 (x == PixelFormat.PIX_FMT_RGB48BE)     || 
				 (x == PixelFormat.PIX_FMT_RGB48LE)     || 
				 (x == PixFmt.PIX_FMT_RGB32  )          || 
				 (x == PixFmt.PIX_FMT_RGB32_1)          || 
				 (x == PixelFormat.PIX_FMT_BGR48BE)     || 
				 (x == PixelFormat.PIX_FMT_BGR48LE)     || 
				 (x == PixelFormat.PIX_FMT_BGR24  )     || 
				 (x == PixelFormat.PIX_FMT_BGR565LE)    || 
				 (x == PixelFormat.PIX_FMT_BGR565BE)    || 
				 (x == PixelFormat.PIX_FMT_BGR555LE)    || 
				 (x == PixelFormat.PIX_FMT_BGR555BE)    || 
				 (x == PixFmt.PIX_FMT_BGR32   )         || 
				 (x == PixFmt.PIX_FMT_BGR32_1 )         || 
				 (x == PixelFormat.PIX_FMT_RGB24   )    || 
				 (x == PixelFormat.PIX_FMT_RGB565LE)    || 
				 (x == PixelFormat.PIX_FMT_RGB565BE)    || 
				 (x == PixelFormat.PIX_FMT_RGB555LE)    || 
				 (x == PixelFormat.PIX_FMT_RGB555BE)    || 
				 (x == PixelFormat.PIX_FMT_GRAY8   )    || 
				 (x == PixelFormat.PIX_FMT_GRAY8A  )    || 
				 (x == PixelFormat.PIX_FMT_YUV410P )    || 
		         (x == PixelFormat.PIX_FMT_YUV440P )    || 
		         (x == PixelFormat.PIX_FMT_NV12    )    || 
		         (x == PixelFormat.PIX_FMT_NV21    )    || 
		         (x == PixelFormat.PIX_FMT_GRAY16BE)    || 
		         (x == PixelFormat.PIX_FMT_GRAY16LE)    || 
		         (x == PixelFormat.PIX_FMT_YUV444P )    || 
		         (x == PixelFormat.PIX_FMT_YUV422P )    || 
		         (x == PixelFormat.PIX_FMT_YUV411P )    || 
		         (x == PixelFormat.PIX_FMT_YUVJ420P)    || 
		         (x == PixelFormat.PIX_FMT_YUVJ422P)    || 
		         (x == PixelFormat.PIX_FMT_YUVJ440P)    || 
		         (x == PixelFormat.PIX_FMT_YUVJ444P)    || 
 		         (x == PixelFormat.PIX_FMT_PAL8    )    || 
 		         (x == PixelFormat.PIX_FMT_BGR8    )    || 
 		         (x == PixelFormat.PIX_FMT_RGB8    )    || 
 		         (x == PixelFormat.PIX_FMT_BGR4_BYTE)   || 
  		         (x == PixelFormat.PIX_FMT_RGB4_BYTE)   || 
  		         (x == PixelFormat.PIX_FMT_YUV440P )    || 
     	         (x == PixelFormat.PIX_FMT_MONOWHITE)   || 
		         (x == PixelFormat.PIX_FMT_MONOBLACK)   || 
		         (x == PixelFormat.PIX_FMT_YUV420P9LE)  || 
		         (x == PixelFormat.PIX_FMT_YUV444P9LE)  || 
		         (x == PixelFormat.PIX_FMT_YUV420P10LE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P10LE) || 
		         (x == PixelFormat.PIX_FMT_YUV444P10LE) || 
		         (x == PixelFormat.PIX_FMT_YUV420P16LE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P16LE) || 
		         (x == PixelFormat.PIX_FMT_YUV444P16LE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P10LE) || 
		         (x == PixelFormat.PIX_FMT_YUV420P9BE)  || 
		         (x == PixelFormat.PIX_FMT_YUV444P9BE)  || 
		         (x == PixelFormat.PIX_FMT_YUV420P10BE) || 
		         (x == PixelFormat.PIX_FMT_YUV444P10BE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P10BE) || 
		         (x == PixelFormat.PIX_FMT_YUV420P16BE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P16BE) || 
		         (x == PixelFormat.PIX_FMT_YUV444P16BE) || 
		         (x == PixelFormat.PIX_FMT_YUV422P10BE) );		
	}
	
	

	public static boolean sws_isSupportedInput(PixelFormat pix_fmt) {
		return isSupportedIn(pix_fmt);		
	}
	

	
	public static boolean isSupportedOut(PixelFormat x) {
		return ( (x == PixelFormat.PIX_FMT_YUV420P)     || 
				 (x == PixelFormat.PIX_FMT_YUVA420P)    || 
				 (x == PixelFormat.PIX_FMT_YUYV422)     || 
				 (x == PixelFormat.PIX_FMT_UYVY422)     || 
			     (x == PixelFormat.PIX_FMT_YUV444P)     || 
				 (x == PixelFormat.PIX_FMT_YUV422P)     || 
				 (x == PixelFormat.PIX_FMT_YUV411P)     || 
				 (x == PixelFormat.PIX_FMT_YUVJ420P)    || 
				 (x == PixelFormat.PIX_FMT_YUVJ422P)    || 
				 (x == PixelFormat.PIX_FMT_YUVJ440P)    || 
				 (x == PixelFormat.PIX_FMT_YUVJ444P)    ||
			     (SwScaleInternal.isRGBinBytes(x))     ||
			     (SwScaleInternal.isBGRinBytes(x))     ||
			     (x == PixelFormat.PIX_FMT_RGB565LE)    ||
		         (x == PixelFormat.PIX_FMT_RGB565BE)    ||
		         (x == PixelFormat.PIX_FMT_RGB555LE)    ||
		         (x == PixelFormat.PIX_FMT_RGB555BE)    ||
		         (x == PixelFormat.PIX_FMT_RGB444LE)    ||
		         (x == PixelFormat.PIX_FMT_RGB444BE)    ||
		         (x == PixelFormat.PIX_FMT_BGR565LE)    ||
		         (x == PixelFormat.PIX_FMT_BGR565BE)    ||
		         (x == PixelFormat.PIX_FMT_BGR555LE)    ||
		         (x == PixelFormat.PIX_FMT_BGR555BE)    ||
		         (x == PixelFormat.PIX_FMT_BGR444LE)    ||
		         (x == PixelFormat.PIX_FMT_BGR444BE)    ||
		         (x == PixelFormat.PIX_FMT_RGB8)        ||
		         (x == PixelFormat.PIX_FMT_BGR8)        ||
		         (x == PixelFormat.PIX_FMT_RGB4_BYTE)   ||
		         (x == PixelFormat.PIX_FMT_BGR4_BYTE)   ||
		         (x == PixelFormat.PIX_FMT_RGB4)        ||
		         (x == PixelFormat.PIX_FMT_BGR4)        ||
		         (x == PixelFormat.PIX_FMT_MONOBLACK)   ||
		         (x == PixelFormat.PIX_FMT_MONOWHITE)   ||
		         (x == PixelFormat.PIX_FMT_NV12)        ||
		         (x == PixelFormat.PIX_FMT_NV21)        ||
		         (x == PixelFormat.PIX_FMT_GRAY16BE)    ||
		         (x == PixelFormat.PIX_FMT_GRAY16LE)    ||
		         (x == PixelFormat.PIX_FMT_GRAY8)       ||
		         (x == PixelFormat.PIX_FMT_YUV410P)     ||
		         (x == PixelFormat.PIX_FMT_YUV440P)     ||
		         (x == PixelFormat.PIX_FMT_YUV422P10LE) ||
		         (x == PixelFormat.PIX_FMT_YUV420P9LE)  ||
		         (x == PixelFormat.PIX_FMT_YUV420P10LE) ||
		         (x == PixelFormat.PIX_FMT_YUV420P16LE) ||
		         (x == PixelFormat.PIX_FMT_YUV422P16LE) ||
		         (x == PixelFormat.PIX_FMT_YUV444P16LE) ||
		         (x == PixelFormat.PIX_FMT_YUV422P10BE) ||
		         (x == PixelFormat.PIX_FMT_YUV420P9BE)  ||
		         (x == PixelFormat.PIX_FMT_YUV420P10BE) ||
		         (x == PixelFormat.PIX_FMT_YUV420P16BE) ||
		         (x == PixelFormat.PIX_FMT_YUV422P16BE) ||
		         (x == PixelFormat.PIX_FMT_YUV444P16BE) );
				 	
	}



	public static boolean sws_isSupportedOutput(PixelFormat pix_fmt) {
		return isSupportedOut(pix_fmt);
	}


	public static OutOOOI initFilter(int xInc, int srcW, int dstW,
			int filterAlign, int one, int flags, int cpu_flags, SwsVector srcFilter,
			SwsVector dstFilter, double[] param) {
		int [] outFilter = null;
		int [] filterPos = null;
		int outFilterSize = 0;
		
		int ret = -1;
	    int i;
	    int filterSize;
	    int filter2Size;
	    int minFilterSize;
	    long [] filter = null;
	    long [] filter2 = null;
	    long fone = 1L << 54;

	    //emms_c(); //FIXME this should not be required but it IS (even for non-MMX versions)

	    // NOTE: the +1 is for the MMX scaler which reads over the end
	    filterPos = new int[dstW+1];

	    if (Mathematics.FFABS(xInc - 0x10000) <10) { // unscaled
	        filterSize = 1;
	        filter = new long[dstW*filterSize];

	        for (i = 0 ; i < dstW ; i++) {
	            filter[i*filterSize]= fone;
	            filterPos[i] = i;
	        }

	    } else if ( (flags & SwScale.SWS_POINT) != 0) { // lame looking point sampling mode
	        int xDstInSrc;
	        filterSize= 1;
	        filter = new long[dstW*filterSize];

	        xDstInSrc = xInc / 2 - 0x8000;
	        for (i = 0 ; i < dstW ; i++) {
	            int xx = (xDstInSrc - ((filterSize-1)<<15) + (1<<15))>>16;

	            filterPos[i] = xx;
	            filter[i] = fone;
	            xDstInSrc += xInc;
	        }
	    } else if ( ( (xInc <= (1<<16)) && ( (flags & SwScale.SWS_AREA) != 0 ) ) || 
	    		    ( (flags & SwScale.SWS_FAST_BILINEAR) != 0) ) { // bilinear upscale
	        int xDstInSrc;
	        filterSize = 2;
	        filter = new long[dstW*filterSize];

	        xDstInSrc = xInc/2 - 0x8000;
	        for (i = 0 ; i < dstW ; i++) {
	            int xx = (xDstInSrc - ((filterSize-1)<<15) + (1<<15))>>16;
	            int j;

	            filterPos[i]= xx;
	            //bilinear upscale / linear interpolate / area averaging
	            for (j = 0 ; j < filterSize ; j++) {
	                long coeff = fone - Mathematics.FFABS((xx<<16) - xDstInSrc)*(fone>>16);
	                if (coeff < 0) 
	                	coeff=0;
	                filter[i*filterSize + j]= coeff;
	                xx++;
	            }
	            xDstInSrc+= xInc;
	        }
	    } else {
	        int xDstInSrc;
	        int sizeFactor;

	        if      ( (flags & SwScale.SWS_BICUBIC) != 0)  sizeFactor =  4;
	        else if ( (flags & SwScale.SWS_X) != 0)        sizeFactor =  8;
	        else if ( (flags & SwScale.SWS_AREA) != 0)     sizeFactor =  1; //downscale only, for upscale it is bilinear
	        else if ( (flags & SwScale.SWS_GAUSS) != 0)    sizeFactor =  8;   // infinite ;)
	        else if ( (flags & SwScale.SWS_LANCZOS) != 0)  sizeFactor = (int) (param[0] != SwScale.SWS_PARAM_DEFAULT ? Math.ceil(2*param[0]) : 6);
	        else if ( (flags & SwScale.SWS_SINC) != 0)     sizeFactor = 20; // infinite ;)
	        else if ( (flags & SwScale.SWS_SPLINE) != 0)   sizeFactor = 20;  // infinite ;)
	        else if ( (flags & SwScale.SWS_BILINEAR) != 0) sizeFactor =  2;
	        else {
	            sizeFactor = 0; //GCC warning killer
	        }

	        if (xInc <= 1<<16)      filterSize= 1 + sizeFactor; // upscale
	        else                    filterSize= 1 + (sizeFactor*srcW + dstW - 1)/ dstW;

	        if (filterSize > srcW-2) filterSize=srcW-2;

	        filter = new long[dstW*filterSize];

	        xDstInSrc = xInc - 0x10000;
	        for (i = 0 ; i < dstW ; i++) {
	            int xx = (xDstInSrc - ((filterSize-2)<<16)) / (1<<17);
	            int j;
	            filterPos[i]= xx;
	            for (j=0; j<filterSize; j++) {
	                long d= (Mathematics.FFABS((xx<<17) - xDstInSrc))<<13;
	                double floatd;
	                long coeff;

	                if (xInc > 1<<16)
	                    d= d*dstW/srcW;
	                floatd= d * (1.0/(1<<30));

					if ( (flags & SwScale.SWS_BICUBIC) != 0 ) {
	                    long B = (long) ((param[0] != SwScale.SWS_PARAM_DEFAULT ? param[0] :   0) * (1<<24));
	                    long C = (long) ((param[1] != SwScale.SWS_PARAM_DEFAULT ? param[1] : 0.6) * (1<<24));
	                    long dd = ( d*d)>>30;
	                    long ddd = (dd*d)>>30;

	                    if      (d < 1L<<30)
	                        coeff = (12*(1<<24)-9*B-6*C)*ddd + (-18*(1<<24)+12*B+6*C)*dd + (6*(1<<24)-2*B)*(1<<30);
	                    else if (d < 1L<<31)
	                        coeff = (-B-6*C)*ddd + (6*B+30*C)*dd + (-12*B-48*C)*d + (8*B+24*C)*(1<<30);
	                    else
	                        coeff = 0;
	                    coeff *= fone>>(30+24);
	                }
	/*                else if (flags & SWS_X) {
	                    double p= param ? param*0.01 : 0.3;
	                    coeff = d ? sin(d*M_PI)/(d*M_PI) : 1.0;
	                    coeff*= pow(2.0, - p*d*d);
	                }*/
	                else if ( (flags & SwScale.SWS_X) != 0 ) {
	                    double A = param[0] != SwScale.SWS_PARAM_DEFAULT ? param[0] : 1.0;
	                    double c;

	                    if (floatd<1.0)
	                        c = Math.cos(floatd*Math.PI);
	                    else
	                        c=-1.0;
	                    if (c<0.0)      c= -Math.pow(-c, A);
	                    else            c=  Math.pow( c, A);
	                    coeff = (long) ((c*0.5 + 0.5)*fone);
	                    
	                } else if ( (flags & SwScale.SWS_AREA) != 0 ) {
	                    long d2= d - (1<<29);
	                    if      (d2*xInc < -(1L<<(29+16))) 
	                    	coeff= (long) (1.0 * (1L<<(30+16)));
	                    else if (d2*xInc <  (1L<<(29+16))) 
	                    	coeff= -d2*xInc + (1L<<(29+16));
	                    else coeff = 0;
	                    coeff *= fone>>(30+16);
	                    
	                } else if ( (flags & SwScale.SWS_GAUSS) != 0 ) { 
	                    double p = param[0] != SwScale.SWS_PARAM_DEFAULT ? param[0] : 3.0;
	                    coeff = (long) ((Math.pow(2.0, - p*floatd*floatd))*fone);
	                    
	                } else if ( (flags & SwScale.SWS_SINC) != 0 ) {
	                    coeff = (long) ((d != 0 ? Math.sin(floatd*Math.PI)/(floatd*Math.PI) : 1.0)*fone);
	                    
	                } else if ( (flags & SwScale.SWS_LANCZOS) != 0 ) {
	                    double p= param[0] != SwScale.SWS_PARAM_DEFAULT ? param[0] : 3.0;
	                    coeff = (long) ((d != 0 ? Math.sin(floatd*Math.PI)*Math.sin(floatd*Math.PI/p)/(floatd*floatd*Math.PI*Math.PI/p) : 1.0)*fone);
	                    if (floatd>p) coeff=0;
	                    
	                } else if ( (flags & SwScale.SWS_BILINEAR) != 0 ) {
	                    coeff= (1<<30) - d;
	                    if (coeff<0) coeff=0;
	                    coeff *= fone >> 30;
	                    
	                } else if ( (flags & SwScale.SWS_SPLINE) != 0 ) {
	                    double p=-2.196152422706632;
	                    coeff = (long) (getSplineCoeff(1.0, 0.0, p, -p-1.0, floatd) * fone);
	                } else {
	                    coeff= 0; //GCC warning killer
	                }

	                filter[i*filterSize + j]= coeff;
	                xx++;
	            }
	            xDstInSrc+= 2*xInc;
	        }
	    }

	    /* apply src & dst Filter to filter -> filter2
	       av_free(filter);
	    */

	    filter2Size = filterSize;
	    if (srcFilter != null) 
	    	filter2Size+= srcFilter.get_length() - 1;
	    if (dstFilter != null) 
	    	filter2Size+= dstFilter.get_length() - 1;

	    filter2 = new long[filter2Size * dstW];

	    for (i=0; i<dstW; i++) {
	        int j, k;

	        if (srcFilter != null) {
	            for (k = 0 ; k < srcFilter.get_length() ; k++) {
	                for (j = 0 ; j < filterSize ; j++)
	                    filter2[i*filter2Size + k + j] += srcFilter.get_coeff(k) * filter[i*filterSize + j];
	            }
	        } else {
	            for (j=0; j<filterSize; j++)
	                filter2[i*filter2Size + j]= filter[i*filterSize + j];
	        }
	        //FIXME dstFilter

	        filterPos[i] += (filterSize-1)/2 - (filter2Size-1)/2;
	    }
	    filter = null;

	    /* try to reduce the filter-size (step1 find size and shift left) */
	    // Assume it is near normalized (*0.5 or *2.0 is OK but * 0.001 is not).
	    minFilterSize= 0;
	    for (i=dstW-1; i>=0; i--) {
	        int min= filter2Size;
	        int j;
	        long cutOff = 0;

	        /* get rid of near zero elements on the left by shifting left */
	        for (j=0; j<filter2Size; j++) {
	            int k;
	            cutOff += Mathematics.FFABS(filter2[i*filter2Size]);

	            if (cutOff > SwScale.SWS_MAX_REDUCE_CUTOFF*fone) break;

	            /* preserve monotonicity because the core can't handle the filter otherwise */
	            if ( (i < dstW-1) && 
	            	 (filterPos[i] >= filterPos[i+1]) )
	            	break;

	            // move filter coefficients left
	            for (k=1; k<filter2Size; k++)
	                filter2[i*filter2Size + k - 1]= filter2[i*filter2Size + k];
	            filter2[i*filter2Size + k - 1]= 0;
	            filterPos[i]++;
	        }

	        cutOff=0;
	        /* count near zeros on the right */
	        for (j=filter2Size-1; j>0; j--) {
	            cutOff += Mathematics.FFABS(filter2[i*filter2Size + j]);

	            if (cutOff > SwScale.SWS_MAX_REDUCE_CUTOFF*fone) break;
	            min--;
	        }

	        if (min>minFilterSize) minFilterSize= min;
	    }
/*
	    if (HAVE_ALTIVEC && cpu_flags & AV_CPU_FLAG_ALTIVEC) {
	        // we can handle the special case 4,
	        // so we don't want to go to the full 8
	        if (minFilterSize < 5)
	            filterAlign = 4;

	        // We really don't want to waste our time
	        // doing useless computation, so fall back on
	        // the scalar C code for very small filters.
	        // Vectorizing is worth it only if you have a
	        // decent-sized vector.
	        if (minFilterSize < 3)
	            filterAlign = 1;
	    }*/
/*
	    if (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) {
	        // special case for unscaled vertical filtering
	        if (minFilterSize == 1 && filterAlign == 2)
	            filterAlign= 1;
	    }*/

	    filterSize= (minFilterSize +(filterAlign-1)) & (~(filterAlign-1));

	    filter = new long[filterSize*dstW];
	    
	    if ( filterSize >= SwScaleInternal.MAX_FILTER_SIZE * 16 / ( (flags & SwScale.SWS_ACCURATE_RND) != 0 ? SwScaleInternal.APCK_SIZE : 16) )
			return new OutOOOI(null, null, null, ret);

	    outFilterSize = filterSize;

	    if ( (flags & SwScale.SWS_PRINT_INFO) != 0)
	        Log.av_log("", Log.AV_LOG_VERBOSE, "SwScaler: reducing / aligning filtersize %d -> %d\n", filter2Size, filterSize);
	    
	    /* try to reduce the filter-size (step2 reduce it) */
	    for (i=0; i<dstW; i++) {
	        int j;

	        for (j=0; j<filterSize; j++) {
	            if (j>=filter2Size) filter[i*filterSize + j]= 0;
	            else               filter[i*filterSize + j]= filter2[i*filter2Size + j];
	            if( ((flags & SwScale.SWS_BITEXACT) != 0) && j>=minFilterSize)
	                filter[i*filterSize + j]= 0;
	        }
	    }

	    //FIXME try to align filterPos if possible

	    //fix borders
	    for (i=0; i<dstW; i++) {
	        int j;
	        if (filterPos[i] < 0) {
	            // move filter coefficients left to compensate for filterPos
	            for (j=1; j<filterSize; j++) {
	                int left= (int) Mathematics.FFMAX(j + filterPos[i], 0);
	                filter[i*filterSize + left] += filter[i*filterSize + j];
	                filter[i*filterSize + j]=0;
	            }
	            filterPos[i]= 0;
	        }

	        if (filterPos[i] + filterSize > srcW) {
	            int shift= filterPos[i] + filterSize - srcW;
	            // move filter coefficients right to compensate for filterPos
	            for (j=filterSize-2; j>=0; j--) {
	                int right= (int) Mathematics.FFMIN(j + shift, filterSize-1);
	                filter[i*filterSize +right] += filter[i*filterSize +j];
	                filter[i*filterSize +j]=0;
	            }
	            filterPos[i]= srcW - filterSize;
	        }
	    }

	    // Note the +1 is for the MMX scaler which reads over the end
	    /* align at 16 for AltiVec (needed by hScale_altivec_real) */
	    outFilter = new int[outFilterSize*(dstW+1)];

	    /* normalize & store in outFilter */
	    for (i=0; i<dstW; i++) {
	        int j;
	        long error=0;
	        long sum=0;

	        for (j=0; j<filterSize; j++) {
	            sum+= filter[i*filterSize + j];
	        }
	        sum= (sum + one/2)/ one;
	        for (j=0; j < outFilterSize; j++) {
	            long v= filter[i*filterSize + j] + error;
	            int intV= (int) Common.ROUNDED_DIV(v, sum);
	            outFilter[i*outFilterSize + j]= intV;
	            error= v - intV*sum;
	        }
	    }

	    filterPos[dstW]= filterPos[dstW-1]; // the MMX scaler will read over the end
	    for (i=0; i < outFilterSize; i++) {
	        int j= dstW* outFilterSize;
	        outFilter[j + i]= outFilter[j + i - outFilterSize];
	    }

	    ret = 0;
		return new OutOOOI(outFilter, filterPos, outFilterSize, ret);
	}


	private static double getSplineCoeff(double a, double b, double c, double d,
			double dist) {
	    if (dist<=1.0) return ((d*dist + c)*dist + b)*dist +a;
	    else           return getSplineCoeff(        0.0,
	                                          b+ 2.0*c + 3.0*d,
	                                                 c + 3.0*d,
	                                         -b- 3.0*c - 6.0*d,
	                                         dist-1.0);
	}

}
