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

package uk.ac.liv.ffmpeg.libavformat.mxf.metadata;


import java.io.IOException;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavutil.AVRational;


public class IndexTableSegment extends MetadataReadTableEntry {

	
	int editUnitByteCount;
	int indexSID;
	int bodySID;
	AVRational indexEditRate;
	long indexStartPosition;
	long indexDuration;

	public IndexTableSegment(MetadataSetType type) {
		super(type);
	}

	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		switch (tag) {	
		
		case 0x3F05:
			editUnitByteCount = (int) reader.read_UInt32().toInt();
			break;	
			
		case 0x3F06:
			indexSID = (int) reader.read_UInt32().toInt();
			break;	
			
		case 0x3F07:
			bodySID = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x3F0B:
			indexEditRate = reader.read_AVRational();
			break;
			
		case 0x3F0C:
			indexStartPosition = reader.read_UInt64().toInt();
			break;
			
		case 0x3F0D:
			indexDuration = reader.read_UInt64().toInt();
			break;

		default:
			reader.read_bytes(size);
		}
	}

	public int getEditUnitByteCount() {
		return editUnitByteCount;
	}

	public AVRational getIndexEditRate() {
		return indexEditRate;
	}

	@Override
	public String toString() {
		return type + " {" + uid + "} [editUnitByteCount=" + editUnitByteCount
				+ ", indexSID=" + indexSID + ", bodySID=" + bodySID
				+ ", indexEditRate=" + indexEditRate + ", indexStartPosition="
				+ indexStartPosition + ", indexDuration=" + indexDuration + "]" ;
	}
	
	


}
