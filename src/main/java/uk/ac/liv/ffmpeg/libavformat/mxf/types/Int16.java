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

package uk.ac.liv.ffmpeg.libavformat.mxf.types;


public class Int16 {

	int n;
	
	public Int16(int n) {
		super();
		this.n = n;
	}
	

	public int toInt() {
		return (int)n;
	}
	

	public String toString() {
		return Integer.toString(n);
	}
    
	public boolean equals(Object obj) {
		if (! (obj instanceof Int16))
			return false;
	   
		Int16 u = (Int16) obj;
		return ( u.toInt() == toInt() );
	}

	public int hashCode() {
		return (int) (toInt());
	}
    
}
