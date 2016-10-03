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

import java.util.Arrays;

import uk.ac.liv.util.Format;


public class UID {

	private byte [] bytes = { 0, 0, 0, 0, 0, 0, 0, 0,
			                  0, 0, 0, 0, 0, 0, 0, 0 };
	
	
	public UID(byte [] b) {
		super();
		this.bytes = b;
	}

	public UID() {
	}

	public static UID fromString(String s) {
		byte [] b = new byte[16];
		
		if (s.length() != 38)
			throw new IllegalArgumentException();
		
		if ( (s.charAt(0) != '{') || (s.charAt(37) != '}') )
			throw new IllegalArgumentException();		
		
		b[0] = Byte.parseByte(s.substring(1,3), 16);
		b[1] = Byte.parseByte(s.substring(3,5), 16);
		b[2] = Byte.parseByte(s.substring(5,7), 16);
		b[3] = Byte.parseByte(s.substring(7,9), 16);

		b[4] = Byte.parseByte(s.substring(10,12), 16);
		b[5] = Byte.parseByte(s.substring(12,14), 16);

		b[6] = Byte.parseByte(s.substring(15,17), 16);
		b[7] = Byte.parseByte(s.substring(17,19), 16);

		b[8] = Byte.parseByte(s.substring(20,22), 16);
		b[9] = Byte.parseByte(s.substring(22,24), 16);

		b[10] = Byte.parseByte(s.substring(25,27), 16);
		b[11] = Byte.parseByte(s.substring(27,29), 16);
		b[12] = Byte.parseByte(s.substring(29,31), 16);
		b[13] = Byte.parseByte(s.substring(31,33), 16);
		b[14] = Byte.parseByte(s.substring(33,35), 16);
		b[15] = Byte.parseByte(s.substring(35,37), 16);
		
		return new UID(b);
	}
	
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("{" + Format.toHex(bytes[0])
                	 + Format.toHex(bytes[1])
                	 + Format.toHex(bytes[2])
                	 + Format.toHex(bytes[3]) + "-"
                	 
                	 + Format.toHex(bytes[4])
                	 + Format.toHex(bytes[5]) + "-"
                	 
                	 + Format.toHex(bytes[6])
                	 + Format.toHex(bytes[7]) + "-"
                	 
                	 + Format.toHex(bytes[8])
                	 + Format.toHex(bytes[9]) + "-"
                	 
                	 + Format.toHex(bytes[10])
                	 + Format.toHex(bytes[11])
                	 + Format.toHex(bytes[12])
                	 + Format.toHex(bytes[13])
                	 + Format.toHex(bytes[14])
                	 + Format.toHex(bytes[15]) + "}");

        return sb.toString();
   }
   
   // The key array may be shorter than 16 bytes
   public boolean equals(byte [] key) {
	   byte [] cp = new byte[key.length];
	   System.arraycopy(bytes, 0, cp, 0, key.length);
	   return Arrays.equals(cp, key);
   }
   

   public boolean equals(Object obj) {
	   if (obj instanceof String) {
		   try {
			   obj = fromString((String)obj);
		   } catch (Exception e) {
			   return false;
		   };
	   }
		   
		
	   if (! (obj instanceof UID))
		   return false;
	   
	   UID ul = (UID) obj;
	   return ( ul.get(0) == bytes[0] &&
			    ul.get(1) == bytes[1] &&
			    ul.get(2) == bytes[2] &&
			    ul.get(3) == bytes[3] &&
			    ul.get(4) == bytes[4] &&
			    ul.get(5) == bytes[5] &&
			    ul.get(6) == bytes[6] &&
			    ul.get(7) == bytes[7] &&
			    ul.get(8) == bytes[8] &&
			    ul.get(9) == bytes[9] &&
			    ul.get(10) == bytes[10] &&
			    ul.get(11) == bytes[11] &&
			    ul.get(12) == bytes[12] &&
			    ul.get(13) == bytes[13] &&
			    ul.get(14) == bytes[14] &&
			    ul.get(15) == bytes[15] );
   }

   public int hashCode() {
	   int sum = 0;
	   for (int i = 0 ; i < bytes.length ; i++)
		   sum += bytes[i];
	   return sum;
   }
	
	public int get(int n) { return bytes[n]; }
	

	public boolean equals(UIDregexp key){
		return key.equals(this);		
	}

	public boolean match(UID uid, int len) {
		if (uid == null) {
			return false;
		}
		
		for (int i = 0 ; i < len; i++) {
			if ( (i != 7) && (bytes[i] != uid.get(i)) )
				return false;
		}
		return true;
	}
	
	
	// Get the last 4 bytes as an int (MSB first). This is used to match the
	// track number which is the last 4 bytes of the key
	// SMPTE 379M 7.3 
	public int get_track_number() {
		return (bytes[12] << 24) + (bytes[13] << 16) + (bytes[14] << 8) + bytes[15];
		
	}
    
    
}
