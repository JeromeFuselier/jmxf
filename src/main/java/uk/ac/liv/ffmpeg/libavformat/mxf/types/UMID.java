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



public class UMID {

	private UID ul1;
	private UID ul2;
	
	
	
	public UMID(UID ul1, UID ul2) {
		super();
		this.ul1 = ul1;
		this.ul2 = ul2;
	}
	
	
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append(ul1 + "-" + ul2);
        return sb.toString();
   }   


   public UID getUl1() {
	return ul1;
   }


	public UID getUl2() {
		return ul2;
	}


	public boolean equals(Object obj) {
	   if (obj instanceof String) {
		   return toString().equals(obj.toString());
	   }
		
	   if (! (obj instanceof UMID))
		   return false;
	   
	   UMID umid = (UMID) obj;
	   	   
	   return ( (this.getUl1().equals(umid.getUl1())) &&
			    (this.getUl2().equals(umid.getUl2())) );
	}
   

    
}
