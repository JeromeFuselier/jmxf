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


public class Tuple extends BaseTuple {  
	// Display a coma-separated list of elements.   

	public Tuple() {       
		super("(", ", ", ")");  
	}  
	
	public Tuple(Object o1, Object o2) {
		super("(", ", ", ")"); 
		this.add(o1).add(o2);
	}
	
	// Add generic elements to the tuple.  
	// Supports dot-chaining.   
	public Tuple add(Object o){   
		super.addElement(o);  
		return this;
	}  
	
	public Tuple add(int i) {  
		super.addElement(i);
		return this;
	}
	

	public Object first() {    
		return get(0);  
	}
	
	
	public Object second() {    
		return get(1);  
	}   
	
}