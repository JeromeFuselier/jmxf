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

public class Common {
	

	public static final int MKTAG(int a, int b, int c, int d) {
		return (a | (b << 8) | (c << 16) | (d << 24));
	}
	
	
	public static final int MKTAG(char a, char b, char c, char d) {
		return MKTAG((int)a, (int)b, (int)c, (int)d);
	}
	
	public static final int MKTAG(String s) {
		if (s.length() != 4) {
			return -1;
		} else {
			return MKTAG((int)s.charAt(0), (int)s.charAt(1), (int)s.charAt(2), 
						 (int)s.charAt(3));
		}
	}

	public static final int MKTAG(String s, int i) {
		if (s.length() != 3) {
			return -1;
		} else {
			return MKTAG((int)s.charAt(0), (int)s.charAt(1), (int)s.charAt(2), i);
		}
	}

	public static final int MKTAG(int i, String s) {
		if (s.length() != 3) {
			return -1;
		} else {
			return MKTAG(i, (int)s.charAt(0), (int)s.charAt(1), (int)s.charAt(2));
		}
	}


	public static final int MKTAG(String s, int i, int j) {
		if (s.length() != 2) {
			return -1;
		} else {
			return MKTAG((int)s.charAt(0), (int)s.charAt(1), i, j);
		}
	}


	public static final int MKTAG(int i, int j, String s) {
		if (s.length() != 2) {
			return -1;
		} else {
			return MKTAG(i, j, (int)s.charAt(0), (int)s.charAt(1));
		}
	}
	

	public static void main(String[] args) {
		System.out.println(MKTAG((int)'r', (int)'a', (int)'w', (int)' '));
		System.out.println(MKTAG('r', 'a', 'w', ' '));
		System.out.println(MKTAG("raw "));
		System.out.println(MKTAG((int)'W', (int)'R', (int)'A', (int)'W'));
		System.out.println(MKTAG('W', 'R', 'A', 'W'));
		System.out.println(MKTAG("WRAW"));
		System.out.println(MKTAG("WRA", 16));
	}


	public static int av_log2(int v) {
	    int n = 0;
	    if ( (v & 0xffff0000) != 0 ) {
	        v >>= 16;
	        n += 16;
	    }
	    if ( (v & 0xff00) != 0 ) {
	        v >>= 8;
	        n += 8;
	    }
	    n += Mathematics.ff_log2_tab[v];

	    return n;
	}


	public static int av_clip(int a, int amin, int amax) {
		if (a < amin) 
			return amin;
		else if (a > amax)
			return amax;
		else
			return a;
	}


	public static double av_clip(double a, double amin, double amax) {
		if (a < amin) 
			return amin;
		else if (a > amax)
			return amax;
		else
			return a;
	}


	public static void FFSWAP(long[] buf, int i, int j) {
		long tmp = buf[i];
		buf[i] = buf[j];
		buf[j] = tmp;
	}


	public static int av_clip_uint8(int a) {
		return av_clip_uint8_c(a);
	}


	/**
	 * Clip a signed integer value into the 0-255 range.
	 * @param a value to clip
	 * @return clipped value
	 */
	private static int av_clip_uint8_c(int a) {
	    if ( (a&(~0xFF)) != 0) 
	    	return (-a)>>31;
	    else 
	    	return a;
	}


	public static int FFALIGN(int x, int a) {
		return ( (x + a - 1) & ~(a -1) );
	}


	public static long ROUNDED_DIV(long a, long b) {
		return ( (a > 0 ? a + (b>>1) : a - (b>>1)) / b );
	}


	public static long FFUDIV(long a, long b) {
		return ((a > 0 ? a : a - b + 1) / b);
	}
	
	
}
