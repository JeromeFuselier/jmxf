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

public class AVOptionValue {
	// A union structure in C => 2 possible constructors, no setters
	
	Object obj;
	
	
	public double get_dbl() {
		return ((Double) obj).doubleValue();
	}
	
	public float get_flt() {
		return ((Float) obj).floatValue();
	}
	
	public int get_int() {
		return ((Integer) obj).intValue();
	}
	
	public String get_str() {
		if (obj == null) {
			return null;
		} else if (obj instanceof String) {
			return (String) obj;
		} else {
			return obj.toString();
		}
	}
	
	public Object get_obj() {
		return obj;
	}
	

	public AVOptionValue(Object obj) {
		super();
		this.obj = obj;
	}

	public long get_long() {
		return ((Long) obj).longValue();
	}
	
	
	
	
}
