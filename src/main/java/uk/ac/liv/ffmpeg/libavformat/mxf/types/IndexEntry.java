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

import java.io.IOException;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavutil.AVRational;

public class IndexEntry {

	ByteReader reader;
	private int NSL = 0;
	private int NPE = 0;

	private Int8 temporalOffset;
	private Int8 keyframeOffset;
	private UInt8 flags;
	private UInt64 streamOffset;
	private UInt32 [] sliceOffset;
	private AVRational [] posTable;
	
	public IndexEntry(ByteReader reader, int NSL, int NPE) {
		super();
		this.reader = reader;
		this.NSL = NSL;
		this.NPE = NPE;
	}
	
	public void read() throws IOException {
		temporalOffset = reader.read_Int8();
		keyframeOffset = reader.read_Int8();
		flags = reader.read_UInt8();
		streamOffset = reader.read_UInt64();		
		for (int i = 0 ; i < NSL ; i++)
			sliceOffset[i] = reader.read_UInt32();
		for (int i = 0 ; i < NPE ; i++)
			posTable[i] = reader.read_AVRational();
	}
	
	public String toString() {
		return "(" + temporalOffset + ", " + 
					 keyframeOffset + ", ...)";
	}
	
	
}
