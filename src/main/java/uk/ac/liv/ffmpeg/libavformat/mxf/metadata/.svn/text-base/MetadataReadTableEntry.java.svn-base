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

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavformat.mxf.KLVPacket;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;

public class MetadataReadTableEntry {
	
	MetadataSetType type;
	UID uid;
	
	
	public MetadataReadTableEntry(MetadataSetType type) {
		super();
		this.type = type;
	}


	public MetadataSetType getType() {
		return type;
	}


	public void setType(MetadataSetType type) {
		this.type = type;
	}


	public UID getUID() {
		return uid;
	}
	

	public void setUID(UID uid) {
		this.uid = uid;
	}


	public void read(AVFormatContext s, Context mxf, KLVPacket klv) throws IOException {
		ByteReader reader = s.get_pb().get_reader();
		
		reader.read_bytes(klv.get_length());	
	}
	


	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		reader.read_bytes(size);	
		
	}

}
