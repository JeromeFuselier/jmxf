/******************************************************************************
 *  
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author     : Jerome Fuselier
 * Creation   : January 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavutil;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;

public class AVUtil {
	
	public static enum AVMediaType {	
		AVMEDIA_TYPE_UNKNOWN,
		AVMEDIA_TYPE_VIDEO,
		AVMEDIA_TYPE_AUDIO,
		AVMEDIA_TYPE_DATA,
		AVMEDIA_TYPE_SUBTITLE,
		AVMEDIA_TYPE_ATTACHMENT,
		AVMEDIA_TYPE_NB 
	};
	

	public enum AVPictureType {
	    AV_PICTURE_TYPE_NONE, ///< Undefined
	    AV_PICTURE_TYPE_I, 		  ///< Intra
	    AV_PICTURE_TYPE_P,     ///< Predicted
	    AV_PICTURE_TYPE_B,     ///< Bi-dir predicted
	    AV_PICTURE_TYPE_S,     ///< S(GMC)-VOP MPEG4
	    AV_PICTURE_TYPE_SI,    ///< Switching Intra
	    AV_PICTURE_TYPE_SP,    ///< Switching Predicted
	    AV_PICTURE_TYPE_BI,    ///< BI type
	};
	
	
	public static final int LIBAVUTIL_VERSION_MAJOR = 51;
	public static final int LIBAVUTIL_VERSION_MINOR = 9;
	public static final int LIBAVUTIL_VERSION_MICRO = 1;
	
	public static final int FF_LAMBDA_SHIFT = 7;
	public static final int FF_LAMBDA_SCALE = (1<<FF_LAMBDA_SHIFT);
	public static final int FF_QP2LAMBDA = 118; ///< factor to convert from H.263 QP to lambda
	public static final int FF_LAMBDA_MAX = (256*128-1);

	
	public static int AV_VERSION_INT(int a, int b, int c) {
		return (a<<16 | b<<8 | c);
	}
	
	public static int LIBAVUTIL_VERSION_INT() {
		return AV_VERSION_INT(LIBAVUTIL_VERSION_MAJOR, LIBAVUTIL_VERSION_MINOR, 
							  LIBAVUTIL_VERSION_MICRO);
	}	

	public static final long AV_NOPTS_VALUE       = Long.MAX_VALUE;
	public static final int AV_TIME_BASE          = 1000000;
	public static final AVRational AV_TIME_BASE_Q = new AVRational(1, AV_TIME_BASE);


	public static String AV_STRINGIFY(int i) {
		return Integer.toString(i);
	}

	public static String AV_VERSION(int a, int b, int c) {
		return AV_VERSION_DOT(a, b, c);
	}

	public static String AV_VERSION_DOT(int a, int b, int c) {
		return AV_STRINGIFY(a) + "." + AV_STRINGIFY(b) + "." + AV_STRINGIFY(c);
	}

	public static char av_get_picture_type_char(AVPictureType pict_type) {
	    switch (pict_type) {
	    case AV_PICTURE_TYPE_I:  return 'I';
	    case AV_PICTURE_TYPE_P:  return 'P';
	    case AV_PICTURE_TYPE_B:  return 'B';
	    case AV_PICTURE_TYPE_S:  return 'S';
	    case AV_PICTURE_TYPE_SI: return 'i';
	    case AV_PICTURE_TYPE_SP: return 'p';
	    case AV_PICTURE_TYPE_BI: return 'b';
	    default:                 return '?';
	    }
	}



}
