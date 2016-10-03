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

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.Mathematics.AVRounding;

public class AVRational {
	
	public static final double LOG2 = 0.69314718055994530941723212145817656807550013436025;
	
	public static AVRational av_d2q(double d, int max) {
	    if (((Double)d).isNaN())
	        return new AVRational(0,0);
	    if (((Double)d).isInfinite())
	        return new AVRational(d<0 ? -1:1, 0);

	    int exponent = Math.max((int)(Math.log(Math.abs(d) + 1e-20)/LOG2), 0);	    
	    long den = 1 << (61 - exponent);
	    AVRational a = new AVRational();
	    a.reduce((long)(d * den + 0.5), den, max);
	    
	    return a;
	}

	public static int av_cmp_q(AVRational a, AVRational b) {
		return a.av_cmp_q(b);
	}

	public static final AVRational av_reduce(long num, long den, long max) {
		AVRational n = new AVRational();
		n.reduce(num, den, max);
		return n;
	}


	int num;
	int den;
	
	
	public AVRational(int num, int den) {
		super();
		this.num = num;
		this.den = den;
	}
	
	
	public AVRational(long num, long den) {
		super();
		this.num = (int) num;
		this.den = (int) den;
	}


	public AVRational() {
		num = 0;
		den = 1;
	}


	public int get_num() {
		return num;
	}


	public int get_den() {
		return den;
	}
	
	public void set_num(int num) {
		this.num = num;
	}

	public void set_den(int den) {
		this.den = den;
	}

	public boolean reduce(long num, long den, long max){
	    AVRational a0 = new AVRational(0, 1);
	    AVRational a1 = new AVRational(1, 0);
	    
	    // sign is true if the the rational is > 0
	    boolean sign = false;
	    
	    if ( ((num >=0) && (den >= 0)) || ((num < 0) && (den < 0)) )
	    		sign = true;
	    
	    long gcd = Mathematics.av_gcd(Mathematics.FFABS(num), Mathematics.FFABS(den));
	    
	    if (gcd != 0) {
	        num = Mathematics.FFABS(num) / gcd;
	        den = Mathematics.FFABS(den) / gcd;
	    }
	    
	    if ( (num <= max) && (den <= max) ) {
	        a1 = new AVRational((int) num, (int) den);
	        den = 0;
	    }

	    while (den != 0) {
	        long x = num / den;
	        long next_den = num - den * x;
	        long a2n = x * a1.get_num() + a0.get_num();
	        long a2d = x * a1.get_den() + a0.get_den();

	        if ( (a2n > max) || (a2d > max) ) {
	            if (a1.get_num() != 0)
	            	x = (max - a0.get_num()) / a1.get_num();
	            if (a1.get_den() != 0) 
	            	x = Mathematics.FFMIN(x, (max - a0.get_den()) / a1.get_den());

	            if ( den * (2 * x * a1.get_den() + a0.get_den()) > num * a1.get_den() )
	                a1 = new AVRational(x * a1.get_num() + a0.get_num(), 
	                					x * a1.get_den() + a0.get_den());
	            break;
	        }

	        a0 = a1;
	        a1 = new AVRational(a2n, a2d);
	        num = den;
	        den = next_den;
	    }
	    
	    if (sign) 
	    	this.num = a1.get_num();
	    else
	    	this.num = -a1.get_num();
	    this.den = a1.get_den();

	    
	    return den == 0;
	}


	public String toString() {
		return "AVRational [" + num + "/" + den + "]";
	}



	public double av_q2d() {
		return to_double();
	}

	public double to_double() {
		return num / (double) den;
	}
	
	public boolean equals(Object obj) {
		if (! (obj instanceof AVRational))
			return false;
		
		AVRational rat1 = (AVRational) obj;
		return (rat1.get_num() == get_num() && rat1.get_den() == get_den());
	}

	public void av_reduce(int max) {
        AVRational r = AVRational.av_reduce(get_num(), get_den(), Integer.MAX_VALUE);
        set_num(r.get_num());
        set_den(r.get_den());		
	}

	public static int av_find_nearest_q_idx(AVRational q, ArrayList<AVRational> q_list) {
		int i, nearest_q_idx = 0;
		
	    for (i = 0 ; i< q_list.size() ; i++)
	        if (av_nearer_q(q, q_list.get(i), q_list.get(nearest_q_idx)) > 0)
	            nearest_q_idx = i;

	    return nearest_q_idx;
	}

	private static int av_nearer_q(AVRational q, AVRational q1, AVRational q2) {
	    /* n/d is q, a/b is the median between q1 and q2 */
	    long a = q1.get_num() * (long)q2.get_den() + q2.get_num() * (long)q1.get_den();
	    long b = 2 * (long)q1.get_den() * q2.get_den();

	    /* rnd_up(a*d/b) > n => a*d/b > n */
	    long x_up = Mathematics.av_rescale_rnd(a, q.get_den(), b, AVRounding.AV_ROUND_UP);

	    /* rnd_down(a*d/b) < n => a*d/b < n */
	    long x_down = Mathematics.av_rescale_rnd(a, q.get_den(), b, AVRounding.AV_ROUND_DOWN);
	    
	    int tmp1 = (x_up > q.get_num())?1:0;
	    int tmp2 = (x_down < q.get_num())?1:0;
	    

	    return (tmp1 - tmp2) * (int)AVRational.av_cmp_q(q2, q1);
	}


	/**
	 * Compare two rationals.
	 * @param b rational
	 * @return 0 if a==b, 1 if a>b, -1 if a<b, and INT_MIN if one of the
	 * values is of the form 0/0
	 */
	public int av_cmp_q(AVRational b){
		long tmp = get_num() * (long) b.get_den() - b.get_num() * (long) get_den();
	
		if (tmp != 0) 
			return ((int)(tmp ^ get_den() ^ b.get_den() )>>63)|1;
		else if ( (b.get_den() != 0) && (get_den() != 0) ) 
			return 0;
		else if ( (get_num() != 0) && (b.get_num() != 0) ) 
			return (get_num() >> 31) - (b.get_num() >> 31);
		else                    
			return Integer.MIN_VALUE;
	}
	
	

}
