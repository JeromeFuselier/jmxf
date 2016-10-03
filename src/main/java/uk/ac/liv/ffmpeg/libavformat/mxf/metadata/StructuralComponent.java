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
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UMID;


public class StructuralComponent extends MetadataReadTableEntry {


	UID sourcePackageUID;
	long duration;
	long startPosition;
	int sourceTrackID;

	
	public StructuralComponent(MetadataSetType type) {
		super(type);
	}


	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		
		if (type == MetadataSetType.SourceClip) {
			readChildSourceClip(mxf, reader, tag, size);
		} else {
			reader.read_bytes(size);
		}
	}
	

	public void readChildSourceClip(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		switch (tag) {		
		case 0x0202:
			duration = reader.read_UInt64().toInt();
			break;

		case 0x1201:
			startPosition = reader.read_position().toInt();
			break;

		case 0x1101:
			// UMID, only get last 16 bytes 
			UMID umid = reader.read_UMID();
			sourcePackageUID = umid.getUl2();
			break;

		case 0x1102:
			sourceTrackID = (int)reader.read_UInt32().toInt();
			break;

		default:
			reader.read_bytes(size);
		}
		
	}
	
	
	

	public long getDuration() {
		return duration;
	}


	public void setDuration(long duration) {
		this.duration = duration;
	}


	public long getStartPosition() {
		return startPosition;
	}


	public void setStartPosition(long startPosition) {
		this.startPosition = startPosition;
	}


	public UID getSourcePackageUID() {
		return sourcePackageUID;
	}


	public void setSourcePackageUID(UID sourcePackageUID) {
		this.sourcePackageUID = sourcePackageUID;
	}


	public int getSourceTrackID() {
		return sourceTrackID;
	}


	public void setSourceTrackID(int trackID) {
		this.sourceTrackID = trackID;
	}


	public String toString() {

		if (type == MetadataSetType.SourceClip) {
			return toStringSourceClip();
		}
		return "Structural Component";
		
	}
	
	public String toStringSourceClip() {
		return type + " {" + uid + "} [duration=" + duration + ", startPosition="
		+ startPosition + ", sourcePackageUID=" + sourcePackageUID
		+ ", trackID=" + sourceTrackID + "]";
	}

	
}
