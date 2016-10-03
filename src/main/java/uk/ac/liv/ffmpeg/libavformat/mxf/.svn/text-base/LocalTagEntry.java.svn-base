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

package uk.ac.liv.ffmpeg.libavformat.mxf;

import java.io.IOException;

import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt16;
import uk.ac.liv.util.Format;


public class LocalTagEntry {
	
	ByteReader reader;
	
	private UInt16 localTag;
	private UID uid;
	
	
	public LocalTagEntry(ByteReader reader) {
		super();
		this.reader = reader;
	}
	
	
	public void read() throws IOException {
		this.localTag = reader.read_UInt16();
		this.uid = reader.read_uid();
	}
	
	public String toString() {
		return "Local tag: " + Format.toHex(localTag.toInt()) + " -> AUID: "+ uid;
	}
	
	
	

}
