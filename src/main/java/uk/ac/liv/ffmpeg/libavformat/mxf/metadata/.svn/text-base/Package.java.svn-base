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
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UMID;


public class Package extends MetadataReadTableEntry {

	UID [] tracksRefs;
	UID packageUID;
	UID descriptorRef;
	
	Descriptor descriptor;
	
	public Package(MetadataSetType type) {
		super(type);
	}
	
	
	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		switch (tag) {		
		case 0x4403:
			tracksRefs = reader.read_UIDBatch();
			break;

		case 0x4401:
			// UMID, only get last 16 bytes 
			UMID umid = reader.read_UMID();
			packageUID = umid.getUl2();
			break;

		case 0x4701:
			descriptorRef = reader.read_uid();
			break;

		default:
			reader.read_bytes(size);
		}
	}


	public int getTracksCount() {
		return tracksRefs.length;
	}
	

	public UID getTracksRef(int i) {
		return tracksRefs[i];
	}


	public UID getPackageUID() {
		return packageUID;
	}


	public void setPackageUID(UID packageUID) {
		this.packageUID = packageUID;
	}


	public UID getDescriptorRef() {
		return descriptorRef;
	}


	public void setDescriptorRef(UID descriptor) {
		this.descriptorRef = descriptor;
	}



	public Descriptor getDescriptor() {
		return descriptor;
	}


	public void setDescriptor(Descriptor descriptor) {
		this.descriptor = descriptor;
	}


	@Override
	public String toString() {
		return "Package {" + uid + "} [descriptor=" + descriptor + 
			  ", packageUID=" + packageUID
				+ ", tracksRefs=" + Arrays.toString(tracksRefs) + "]";
	}



}
