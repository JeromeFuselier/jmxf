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
 * Creation   : March 2012
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavutil;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOOI;


public class ImgUtils {
	
	

	public static int av_image_check_size(int w, int h, int log_offset,
			Object log_ctx) {

	    if ( (w > 0) && (h > 0) && ( ((w + 128) * (h + 128)) < Integer.MAX_VALUE / 8 ) ) 
	        return 0;
	    
	    return Error.AVERROR(Error.EINVAL);
		
	}

	public static OutOOI av_image_alloc( int w, int h,
			PixelFormat pix_fmt, int align) {
		short [][] pointers = new short[0][0];
		int [] linesizes = new int[4];
		int i, ret;
	    short [] buf;

	    if ((ret = av_image_check_size(w, h, 0, null)) < 0)
	        return new OutOOI(pointers, linesizes, ret);
	    OutOI ret_obj = av_image_fill_linesizes(pix_fmt, w);
	    ret = ret_obj.get_ret();
	    linesizes = (int []) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOOI(pointers, linesizes, ret);

	    for (i = 0; i < 4; i++)
	        linesizes[i] = Mathematics.FFALIGN(linesizes[i], align);

	    ret_obj = av_image_fill_pointers(pix_fmt, h, null, linesizes);
	    ret = ret_obj.get_ret();
	    pointers = (short [][]) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOOI(pointers, linesizes, ret);
	    
	    buf = new short[ret];
	    
	    ret_obj = av_image_fill_pointers(pix_fmt, h, null, linesizes);
	    ret = ret_obj.get_ret();
	    pointers = (short [][]) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOOI(pointers, linesizes, ret);

	    /*AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);
	    if  ( desc.has_flag(PixDesc.PIX_FMT_PAL) )
	    	;ff_set_systematic_pal2(pointers[1], pix_fmt);*/

	    return new OutOOI(pointers, linesizes, ret);
	}

	public static OutOI av_image_fill_pointers(PixelFormat pix_fmt, int height,
			short [] ptr, int[] linesizes) {
		short  [][] data = new short[4][0];
		int i, total_size;
		int [] size = new int[4];
		int [] has_plane = new int[4];

	    AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);

	    if  ( desc.has_flag(PixDesc.PIX_FMT_HWACCEL) )
	        return new OutOI(data, Error.AVERROR(Error.EINVAL));

	    
	    if (linesizes[0] > (Integer.MAX_VALUE - 1024) / height)
	        return new OutOI(data, Error.AVERROR(Error.EINVAL));

	    size[0] = linesizes[0] * height;

	    if  ( desc.has_flag(PixDesc.PIX_FMT_PAL) ) {
	        size[0] = (size[0] + 3) & ~3;
	        data[0] = new short[size[0]];
	        //data[1] = ptr + size[0]; /* palette is stored here as 256 32 bits words */
	        return new OutOI(data, size[0] + 256 * 4);
	    }

	    if (ptr != null)
	    	data[0] = Arrays.copyOf(ptr, ptr.length);
	    else
	    	data[0] = new short[size[0]];

	    for (i = 0; i < desc.get_nb_components(); i++)
	        has_plane[desc.get_comp(i).get_plane()] = 1;

	    total_size = size[0];
	    //data[0] = new short[size[0]];
	    for (i = 1 ; (has_plane[i] != 0) && (i < 4) ; i++) {
	        int h;
	        int s = ( (i == 1) || (i == 2) ) ? desc.get_log2_chroma_h() : 0;
	        //data[i] = data[i-1] + size[i-1];
	        h = (height + (1 << s) - 1) >> s;
	        size[i] = h * linesizes[i];
		    data[i] = new short[size[i]];	        

	        total_size += size[i];
	    }

        return new OutOI(data, total_size);
	}

	public static OutOI av_image_fill_linesizes(PixelFormat pix_fmt, int width) {
		int [] linesizes = new int[4];
	    int i, ret;
	    AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);
	    int [] max_step = new int[4];       /* max pixel step for each plane */
	    int [] max_step_comp = new int[4];  /* the component for each plane which has the max pixel step */

	    if  ( desc.has_flag(PixDesc.PIX_FMT_HWACCEL) )
	        return new OutOI(linesizes, Error.AVERROR(Error.EINVAL));

	    av_image_fill_max_pixsteps(max_step, max_step_comp, desc);
	    for (i = 0; i < 4; i++) {
	        if ((ret = image_get_linesize(width, i, max_step[i], max_step_comp[i], desc)) < 0)
	            return new OutOI(linesizes, ret);
	        linesizes[i] = ret;
	    }

	    return new OutOI(linesizes, 0);
		
	}

	private static int image_get_linesize(int width, int plane, int max_step, int max_step_comp,
			AVPixFmtDescriptor desc) {
	    int s, shifted_w, linesize;

	    if (width < 0)
	        return Error.AVERROR(Error.EINVAL);
	    s = ( (max_step_comp == 1) || (max_step_comp == 2) ) ? desc.get_log2_chroma_w() : 0;
	    shifted_w = ((width + (1 << s) - 1)) >> s;
	    if ( (shifted_w != 0) && (max_step > Integer.MAX_VALUE / shifted_w) )
	        return Error.AVERROR(Error.EINVAL);
	    linesize = max_step * shifted_w;
	    if (desc.has_flag(PixDesc.PIX_FMT_BITSTREAM))
	        linesize = (linesize + 7) >> 3;
	    return linesize;
	}

	private static void av_image_fill_max_pixsteps(int[] max_pixsteps,
			int[] max_pixstep_comps, AVPixFmtDescriptor pixdesc) {
	    for (int i = 0 ; i < pixdesc.get_nb_components() ; i++) {
	        AVComponentDescriptor comp = pixdesc.get_comp(i);
	        if ( (comp.get_step_minus1() + 1) > max_pixsteps[comp.get_plane()] ) {
	            max_pixsteps[comp.get_plane()] = comp.get_step_minus1() + 1;
	            if (max_pixstep_comps != null)
	                max_pixstep_comps[comp.get_plane()] = i;
	        }
	    }		
	}

	public static void av_image_copy(short[][] dst_data, int[] dst_linesizes,
			short[][] src_data, int[] src_linesizes, PixelFormat pix_fmt,
			int width, int height) {
	    AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);

	    if (desc.has_flag(PixDesc.PIX_FMT_HWACCEL))
	        return;

	    if (desc.has_flag(PixDesc.PIX_FMT_PAL)) {
	        av_image_copy_plane(dst_data[0], dst_linesizes[0],
	                            src_data[0], src_linesizes[0],
	                            width, height);
	        /* copy the palette */
	        dst_data[1] = Arrays.copyOf(src_data[1], src_data[1].length);
	    } else {
	        int i, planes_nb = 0;

	        for (i = 0; i < desc.get_nb_components() ; i++)
	            planes_nb = (int)Mathematics.FFMAX(planes_nb, desc.get_comp(i).get_plane() + 1);

	        for (i = 0; i < planes_nb; i++) {
	            int h = height;
	            int bwidth = av_image_get_linesize(pix_fmt, width, i);
	            if (i == 1 || i == 2) {
	                h= -((-height) >> desc.get_log2_chroma_h());
	            }
	            av_image_copy_plane(dst_data[i], dst_linesizes[i],
	                                src_data[i], src_linesizes[i],
	                                bwidth, h);
	        }
	    }
		
	}

	public static int av_image_get_linesize(PixelFormat pix_fmt, int width,
			int plane) {
	    AVPixFmtDescriptor desc = PixDesc.av_pix_fmt_descriptors.get(pix_fmt);
	    int [] max_step      = new int[4];       /* max pixel step for each plane */
	    int [] max_step_comp = new int[4];       /* the component for each plane which has the max pixel step */

	    if (desc.has_flag(PixDesc.PIX_FMT_HWACCEL))
	        return Error.AVERROR(Error.EINVAL);

	    av_image_fill_max_pixsteps(max_step, max_step_comp, desc);
	    return image_get_linesize(width, plane, max_step[plane], max_step_comp[plane], desc);
	}
	

	private static void av_image_copy_plane(short[] dst, int dst_linesize, short[] src,
			int src_linesize, int bytewidth, int height) {
	    if ( (dst == null) || (src == null) )
	        return;
	    
	    int idx_dst = 0;
	    int idx_src = 0;
	    for ( ; height > 0 ; height--) {
	    	System.arraycopy(src, idx_src, dst, idx_dst, bytewidth);
	    	idx_dst += dst_linesize;
	    	idx_src += src_linesize;
	    }

		
	}

	public static int ff_set_systematic_pal2(long[] pal, PixelFormat pix_fmt) {
	    for (int i = 0 ; i < pal.length ; i++) {
	        int r, g, b;

	        switch (pix_fmt) {
	        case PIX_FMT_RGB8:
	            r = (i>>5    )*36;
	            g = ((i>>2)&7)*36;
	            b = (i&3     )*85;
	            break;
	        case PIX_FMT_BGR8:
	            b = (i>>6    )*85;
	            g = ((i>>3)&7)*36;
	            r = (i&7     )*36;
	            break;
	        case PIX_FMT_RGB4_BYTE:
	            r = (i>>3    )*255;
	            g = ((i>>1)&3)*85;
	            b = (i&1     )*255;
	            break;
	        case PIX_FMT_BGR4_BYTE:
	            b = (i>>3    )*255;
	            g = ((i>>1)&3)*85;
	            r = (i&1     )*255;
	            break;
	        case PIX_FMT_GRAY8:
	            r = b = g = i;
	            break;
	        default:
	            return Error.AVERROR(Error.EINVAL);
	        }
	        pal[i] = b + (g<<8) + (r<<16);
	    }

	    return 0;
	}

}
