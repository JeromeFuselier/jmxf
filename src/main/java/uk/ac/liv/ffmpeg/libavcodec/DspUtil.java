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

package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavcodec.mpeg12.ScanTable;

public class DspUtil {
	
	/* pixel operations */
	public static final int MAX_NEG_CROP = 1024;
	public static final int STRIDE_ALIGN = 8;
	
	public static int EDGE_WIDTH  = 16;
	public static int EDGE_TOP    = 1;
	public static int EDGE_BOTTOM = 2;
	

	public static final int FF_NO_IDCT_PERM = 1;
	public static final int FF_LIBMPEG2_IDCT_PERM = 2;
	public static final int FF_SIMPLE_IDCT_PERM = 3;
	public static final int FF_TRANSPOSE_IDCT_PERM = 4;
	public static final int FF_PARTTRANS_IDCT_PERM = 5;
	public static final int FF_SSE2_IDCT_PERM = 6;
	
	/* not permutated inverse zigzag_direct + 1 for MMX quantizer */
	public static int [] inv_zigzag_direct16 = new int[64];
	
	/* Input permutation for the simple_idct_mmx */
	public static int [] simple_mmx_permutation = {
	        0x00, 0x08, 0x04, 0x09, 0x01, 0x0C, 0x05, 0x0D,
	        0x10, 0x18, 0x14, 0x19, 0x11, 0x1C, 0x15, 0x1D,
	        0x20, 0x28, 0x24, 0x29, 0x21, 0x2C, 0x25, 0x2D,
	        0x12, 0x1A, 0x16, 0x1B, 0x13, 0x1E, 0x17, 0x1F,
	        0x02, 0x0A, 0x06, 0x0B, 0x03, 0x0E, 0x07, 0x0F,
	        0x30, 0x38, 0x34, 0x39, 0x31, 0x3C, 0x35, 0x3D,
	        0x22, 0x2A, 0x26, 0x2B, 0x23, 0x2E, 0x27, 0x2F,
	        0x32, 0x3A, 0x36, 0x3B, 0x33, 0x3E, 0x37, 0x3F,
	};
	
	
	public static final byte [] ff_zigzag_direct = {
	    0,   1,  8, 16,  9,  2,  3, 10,
	    17, 24, 32, 25, 18, 11,  4,  5,
	    12, 19, 26, 33, 40, 48, 41, 34,
	    27, 20, 13,  6,  7, 14, 21, 28,
	    35, 42, 49, 56, 57, 50, 43, 36,
	    29, 22, 15, 23, 30, 37, 44, 51,
	    58, 59, 52, 45, 38, 31, 39, 46,
	    53, 60, 61, 54, 47, 55, 62, 63
	};
	
	public static final byte [] ff_alternate_horizontal_scan = {
		    0,  1,   2,  3,  8,  9, 16, 17,
		    10, 11,  4,  5,  6,  7, 15, 14,
		    13, 12, 19, 18, 24, 25, 32, 33,
		    26, 27, 20, 21, 22, 23, 28, 29,
		    30, 31, 34, 35, 40, 41, 48, 49,
		    42, 43, 36, 37, 38, 39, 44, 45,
		    46, 47, 50, 51, 56, 57, 58, 59,
		    52, 53, 54, 55, 60, 61, 62, 63,
		};

	public static final byte [] ff_alternate_vertical_scan = {
		    0,  8,  16, 24,  1,  9,  2, 10,
		    17, 25, 32, 40, 48, 56, 57, 49,
		    41, 33, 26, 18,  3, 11,  4, 12,
		    19, 27, 34, 42, 50, 58, 35, 43,
		    51, 59, 20, 28,  5, 13,  6, 14,
		    21, 29, 36, 44, 52, 60, 37, 45,
		    53, 61, 22, 30,  7, 15, 23, 31,
		    38, 46, 54, 62, 39, 47, 55, 63,
		};
	

	public static short [] ff_cropTbl = new short[256 + 2 * MAX_NEG_CROP];
	public static int [] ff_squareTbl = new int[512];
	
	{
		int i;
		
	    for (i = 0 ; i < 256 ; i++) 
	    	ff_cropTbl[i + MAX_NEG_CROP] = (short) i;
	    
	    for (i = 0 ; i < MAX_NEG_CROP ; i++) {
	        ff_cropTbl[i] = 0;
	        ff_cropTbl[i + MAX_NEG_CROP + 256] = 255;
	    }

	    for (i = 0 ; i < 512 ; i++) {
	        ff_squareTbl[i] = (i - 256) * (i - 256);
	    }

	    for (i = 0 ; i < 64 ; i++) 
	    	inv_zigzag_direct16[ff_zigzag_direct[i]]= i+1;
		
	}

	public static void ff_init_scantable(int[] permutation,
			ScanTable st, byte[] src_scantable) {
		int i;
	    int end;

	    st.set_scantable(src_scantable);

	    for (i = 0 ; i < 64 ; i++){
	        int j;
	        j = src_scantable[i];
	        st.get_permutated()[i] = permutation[j];
		}

	    end =-1;
	    for (i=0 ; i < 64 ; i++){
	        int j;
	        j = st.get_permutated()[i];
	        if (j > end) 
	        	end = j;
	        st.get_raster_end()[i]= end;
	    }
	
	}

}
