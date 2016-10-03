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

package uk.ac.liv.util;

public class OutIS {
	
	int val1;
	String val2;
	

	public OutIS(int val1, String val2) {
		super();
		this.val1 = val1;
		this.val2 = val2;
	}
	public OutIS(String val2, int val1) {
		super();
		this.val1 = val1;
		this.val2 = val2;
	}

	public int get_int() {
		return val1;
	}

	public void set_int(int val) {
		this.val1 = val;
	}

	public String get_string() {
		return val2;
	}

	public void set_string(String val) {
		this.val2 = val;
	}
	
	
	
	

}
