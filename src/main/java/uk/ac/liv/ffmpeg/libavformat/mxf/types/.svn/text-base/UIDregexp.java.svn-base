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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UIDregexp {
	
	private Pattern pattern;
	
	public UIDregexp(String key) {
		super();
		key = key.replace("{", "").replace("}", "").replace("X", "\\d");
		this.pattern = Pattern.compile(key);
	}
	
	public boolean equals(UID key){
		// subtring to remove the trailing "{", "}"
		String s = key.toString();
		String keyStr = s.substring(1, s.length()-1);

		Matcher matcher = pattern.matcher(keyStr);
		return matcher.find();
	}
	  
	
	

}
