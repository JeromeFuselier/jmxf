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

package uk.ac.liv.ffmpeg.libavformat;

public class AVFrac {
	
	
	long val;
	long num;
	long den;
	
	public long get_val() {
		return val;
	}
	
	
	public void set_val(long val) {
		this.val = val;
	}
	
	
	public long get_num() {
		return num;
	}
	
	
	public void set_num(long num) {
		this.num = num;
	}
	
	
	public long get_den() {
		return den;
	}
	
	
	public void set_den(long den) {
		this.den = den;
	}

	
	public void av_frac_init(long val, long num, long den) {
	    num += (den >> 1);
	    if (num >= den) {
	        val += num / den;
	        num = num % den;
	    }
	    this.val = val;
	    this.num = num;
	    this.den = den;
	}

	/**
	 * Fractional addition to f: f = f + (incr / f->den).
	 *
	 * @param incr increment, can be positive or negative
	 */
	public void av_frac_add(long incr) {
	    long num, den;

	    num = this.num + incr;
	    den = this.den;
	    if (num < 0) {
	        this.val += num / den;
	        num = num % den;
	        if (num < 0) {
	            num += den;
	            this.val--;
	        }
	    } else if (num >= den) {
	        this.val += num / den;
	        num = num % den;
	    }
	    this.num = num;
		
	}
	
	

}
