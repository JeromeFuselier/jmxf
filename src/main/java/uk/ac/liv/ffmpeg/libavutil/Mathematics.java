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
 * Creation   : June 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavutil;


public class Mathematics {
	
	public static double M_PHI = 1.61803398874989484820;
	

	public static final byte [] ff_log2_tab = {
	        0,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
	        5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
	        6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
	        6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
	        7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
	        7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
	        7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
	        7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
	};
	

	public static enum AVRounding {
	    AV_ROUND_ZERO,	  // Round toward zero.
	    AV_ROUND_INF,     // Round away from zero.
	    AV_ROUND_DOWN,    // Round toward -infinity.
	    AV_ROUND_UP,      // Round toward +infinity.
	    AV_ROUND_NEAR_INF // Round to nearest and halfway cases away from zero.
	};
	
	public static int av_gcd(int a, int b) {	
		return (int) av_gcd((long) a, (long) b);
	}
	
	
	public static long av_gcd(long a, long b) {		
	    if (b != 0) {
	    	return av_gcd(b, a%b);
	    } else {  
	    	return a;
	    }
	}

	public static long FFABS(long a) {
		if (a >= 0)
			return a;
		else
			return -a;
	}

	public static float FFABS(float a) {
		if (a >= 0)
			return a;
		else
			return -a;
	}

	static int FFABS(int a) {
		if (a >= 0)
			return a;
		else
			return -a;
	}

	static int FFMIN(int a, int b) {
		if (a > b)
			return b;
		else
			return a;
	}

	public static long FFMIN(long a, long b) {
		if (a > b)
			return b;
		else
			return a;
	}

	public static long FFMAX(long a, long b) {
		if (a > b)
			return a;
		else
			return b;
	}

	public static long av_rescale(long a, long b, long c) {
	    return av_rescale_rnd(a, b, c, AVRounding.AV_ROUND_NEAR_INF);
	}

	public static double av_q2d(AVRational a) {
		return a.to_double();
	}

	public static long av_rescale_rnd(long a, long b, long c,
			AVRounding rnd) {
		 long r=0;
		 
		 if (a < 0) {
			 if (rnd == AVRounding.AV_ROUND_DOWN)
				 rnd =  AVRounding.AV_ROUND_UP;
			 else if  (rnd == AVRounding.AV_ROUND_UP)
				 rnd =  AVRounding.AV_ROUND_DOWN;
			 return -av_rescale_rnd(-a, b, c, rnd);
		 }
		 
		 if (rnd == AVRounding.AV_ROUND_NEAR_INF) 
			 r = c / 2;
		 else if ( (rnd == AVRounding.AV_ROUND_INF) ||
				   (rnd == AVRounding.AV_ROUND_UP) ||
				   (rnd == AVRounding.AV_ROUND_NEAR_INF) )
			 r = c-1;

	    if ( (b <= Integer.MAX_VALUE) && (c <= Integer.MAX_VALUE) ) {
	        if (a <= Integer.MAX_VALUE)
	            return (a * b + r) / c;
	        else
	            return a / c * b + (a % c * b + r) / c;
	    } else {
	        long a0= a&0xFFFFFFFF;
	        long a1= a>>32;
	        long b0= b&0xFFFFFFFF;
	        long b1= b>>32;
	        long t1= a0*b1 + a1*b0;
	        long t1a= t1<<32;
	        int i;

	        a0 = a0 * b0 + t1a;
	        a1 = a1 * b1 + (t1>>32) + (a0<t1a?1:0);
	        a0 += r;
	        a1 += a0<r?1:0;

	        for(i=63; i>=0; i--){
	            a1 += a1 + ((a0>>i)&1);
	            t1 += t1;
	            if(/*o || */c <= a1){
	                a1 -= c;
	                t1++;
	            }
	        }
	        return t1;
		}
	}


	public static long lrintf(double x) {
		return (int) Math.round(x);
	}


	public static int lrintf(float x) {
		return (int) Math.round(x);
	}


	public static long av_rescale_q(long a, AVRational bq, AVRational cq) {
	    long b = bq.get_num() * (long) cq.get_den();
	    long c = cq.get_num() * (long) bq.get_den();
	    return av_rescale_rnd(a, b, c, AVRounding.AV_ROUND_NEAR_INF);
	}


	public static long av_compare_mod(long a, long b, long mod) {
		long c = (a - b) & (mod - 1);
		if (c > (mod >> 1))
			c = c - mod;
		return c;
	}


	public static int av_compare_ts(long ts_a, AVRational tb_a,
			long ts_b, AVRational tb_b) {
	    long a = tb_a.get_num() * (long)tb_b.get_den();
	    long b = tb_b.get_num() * (long)tb_a.get_den();
	    if ( ( FFABS(ts_a) | a | FFABS(ts_b) | b ) <= Integer.MAX_VALUE )
	        return ( (ts_a * a > ts_b * b) ? 1 : 0) - ( (ts_a * a < ts_b * b) ? 1 : 0);
	    if (av_rescale_rnd(ts_a, a, b, AVRounding.AV_ROUND_DOWN) < ts_b) return -1;
	    if (av_rescale_rnd(ts_b, b, a, AVRounding.AV_ROUND_DOWN) < ts_a) return  1;
	    return 0;
	}


	public static int FFALIGN(int x, int a) {
		return  (x+a-1) & ~(a-1);
	}


	public static double trunc(double a) {
	    return Math.floor(a);
	}
}
