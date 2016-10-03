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
import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;

public class Sequence extends MetadataReadTableEntry {
	
	long duration;
	UID dataDefinitionUL;
	UID [] structuralComponentsRefs;
	
	
	public Sequence(MetadataSetType type) {
		super(type);
	}

	
	
	

	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		switch (tag) {		
		case 0x0202:
			duration = reader.read_UInt64().toInt();
			break;

		case 0x0201:
			dataDefinitionUL = reader.read_uid();
			break;

		case 0x1001:
			structuralComponentsRefs = reader.read_UIDBatch();
			break;

		default:
			reader.read_bytes(size);
		}
	}
	

	public int getStructuralComponentsCount() {
		return structuralComponentsRefs.length;
	}
	

	public UID getStructuralComponentsRef(int i) {
		return structuralComponentsRefs[i];
	}
	

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public UID getDataDefinitionUL() {
		return dataDefinitionUL;
	}

	public void setDataDefinitionUL(UID dataDefinitionUL) {
		this.dataDefinitionUL = dataDefinitionUL;
	}

	public UID[] getStructuralComponentsRefs() {
		return structuralComponentsRefs;
	}

	public void setStructuralComponentsRefs(UID[] structuralComponentsRefs) {
		this.structuralComponentsRefs = structuralComponentsRefs;
	}

	@Override
	public String toString() {
		return "Sequence {" + uid + "} [duration=" + duration + ", dataDefinition="
				+ dataDefinitionUL + ", structuralComponentsRefs="
				+ Arrays.toString(structuralComponentsRefs) + "]";
	}
	
}
