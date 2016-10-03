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

public class OutOOOI {
	
	Object obj1;
	Object obj2;
	Object obj3;
	int ret;
	
	public OutOOOI(Object obj1, Object obj2, Object obj3, int ret) {
		super();
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.obj3 = obj3;
		this.ret = ret;
	}

	public Object get_obj1() {
		return obj1;
	}

	public Object get_obj2() {
		return obj2;
	}

	public Object get_obj3() {
		return obj3;
	}

	public void set_obj2(Object obj) {
		this.obj2 = obj;
	}

	public void set_obj1(Object obj) {
		this.obj1 = obj;
	}
	
	public void set_obj3(Object obj) {
		this.obj3 = obj;
	}

	public int get_ret() {
		return ret;
	}

	public void set_ret(int ret) {
		this.ret = ret;
	}
	
	
	
	

}
