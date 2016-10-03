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
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Sequence;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt32;
import uk.ac.liv.ffmpeg.libavutil.AVRational;


public class Track extends MetadataReadTableEntry {
	
	Sequence sequence;
	
	UID sequence_ref;
	int track_id;
	int number;
	AVRational edit_rate;
	
	
	public Track(MetadataSetType type) {
		super(type);
	}



	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {		
		switch (tag) {		
		case 0x4801:
			track_id = (int) reader.read_UInt32().toInt();
			break;

		case 0x4804:
			number = (int) reader.read_UInt32().toInt();
			break;

		case 0x4B01:
			//edit_rate = reader.read_AVRational();
			UInt32 den = reader.read_UInt32();
	    	UInt32 num = reader.read_UInt32();
	    	edit_rate = new AVRational(num.toInt(), den.toInt());   
			break;

		case 0x4803:
			sequence_ref = reader.read_uid();
			break;

		default:
			reader.read_bytes(size);
		}
	}


	public int get_track_id() {
		return track_id;
	}


	public void set_id(int id) {
		this.track_id = id;
	}


	public int get_number() {
		return number;
	}


	public void set_number(int number) {
		this.number = number;
	}


	public AVRational get_edit_rate() {
		return edit_rate;
	}


	public void set_edit_rate(AVRational editRate) {
		this.edit_rate = editRate;
	}


	public UID get_sequence_ref() {
		return sequence_ref;
	}


	public void set_sequence_ref(UID sequence) {
		this.sequence_ref = sequence;
	}


	public Sequence get_sequence() {
		return sequence;
	}


	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}


	@Override
	public String toString() {
		return "Track {" + uid + "} [id=" + track_id + ", number=" + number + ", editRate="
				+ edit_rate + ", sequence=" + sequence + "]";
	}

	

}
