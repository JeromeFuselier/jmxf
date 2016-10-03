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

import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseTuple implements Comparable {   
	// Ordered collection of elements.   
	ArrayList elements = new ArrayList();  
	// Strings used to display the tuple.   
	String open;   
	String separator;    
	String close;  
	
	// Initialize the strings for this tuple type.   
	protected BaseTuple(String open, String separator, String close) {  
		this.open = open;       
		this.separator = separator;  
		this.close = close;    }  
	
	// Add elements to the tuple. Supports dot-chaining. 
	protected BaseTuple addElement(Object o) {   
		elements.add(o);        
		return this;   
	}    

	BaseTuple addElement(int i) {    
		return addElement(new Integer(i));  
	}     

	protected Object get(int i) {    
		return elements.get(i);  
	}   
	
	// Compare two tuples. All elements must be equal. 	
	public boolean equals(Object obj) {  
		if (obj == null)            
			return false;      
		if (!(obj instanceof BaseTuple))        
			return false;        
		BaseTuple that = (BaseTuple) obj;       
		if (that.elements.size() != this.elements.size())   
			return false;        
		for (int i = 0; i < elements.size(); ++i) {       
			if (!that.elements.get(i).equals(this.elements.get(i)))      
				return false;       
		}       
		return true;   
	}  
	
	// Calculate a hash code based on the hash of each element.   
	public int hashCode() {      
		int result = 0;      
		Iterator it = elements.iterator();   
		while (it.hasNext()) {          
			result = result * 37 + it.next().hashCode();   
		}       
		return result;    
	}  
	
	// Display the tuple using the open, separator, and close   
	// specified in the constructor.   
	public String toString() {        
		StringBuffer result = new StringBuffer(open);       
		Iterator it = elements.iterator();       
		while (it.hasNext()) {           
			result.append(it.next());      
			if (it.hasNext())              
				result.append(separator);      
		}        
		return result.append(close).toString();    
	}   
	// Order by the most significant element first.   
	// The tuples must agree in size and type.   
	public int compareTo(Object o) {        
		BaseTuple that = (BaseTuple) o;    
		for (int i = 0; i < elements.size(); ++i) {    
			int compare = ((Comparable) this.elements.get(i)).compareTo((Comparable) that.elements.get(i)); 
			if (compare != 0)
				return compare;
		}
		return 0; 
	}

}
