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

package uk.ac.liv.util;


public class Format {

    /* Returns a string representation of the integer argument as an
    * unsigned integer in base16 with trailing zeros. */
	public static String toHex(int i, int width) {
		return toHex((long) i & 0xFF, 2);		
	}

    /* Returns a string representation of the long argument as an
    * unsigned integer in base16 with trailing zeros. */
	public static String toHex(long i, int width) {
		String s = Long.toHexString(i).toUpperCase();
		
		int nb = width - s.length();
		if (nb > 0) {
			s = UtilsString.repeat("0", nb) + s;
		}
		return s;		
	}
	
	public static String toHex(int i) {
		return toHex(i, 2);
	}
	
	public static String toHex(long i) {
		return toHex(i, 4);
	}
	
}
