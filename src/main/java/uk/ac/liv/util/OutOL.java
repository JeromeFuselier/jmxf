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

public class OutOL {
	
	Object obj;
	long val;
	
	public OutOL(Object obj, long val) {
		super();
		this.obj = obj;
		this.val = val;
	}

	public Object get_obj() {
		return obj;
	}

	public void set_obj(Object obj) {
		this.obj = obj;
	}

	public long get_val() {
		return val;
	}

	public void set_val(long val) {
		this.val = val;
	}
	
	
	
	

}
