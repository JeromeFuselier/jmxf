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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavformat.mxf.types.Int32;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavutil.Log;

import uk.ac.liv.util.UtilsString;


public class KLVPacket {
	

    long offset;
	

	protected long position;
	protected UID key;
	protected long length;
	protected byte [] value;
	protected Context mxf;
	
	protected String name;
	

	protected Map<String, Object> metadatas;
	
	
	public KLVPacket() {
		this.position = 0;
		this.key = null;
		this.length = 0;
		this.value = null;
		// Remove the package name
		this.name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf('.') + 1);
		this.metadatas = new HashMap<String, Object>();
	}
/*
	public KLVPacket(MXFFile mxfFile, long position, UID key, long length, byte[] value) {
		super();
		this.mxfFile = mxfFile;
		this.mxf = mxfFile.getMxf();
		this.position = position;
		this.key = key;
		this.length = length;
		this.value = value;
		this.name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf('.') + 1);
		this.metadatas = new HashMap<String, Object>();
	}*/
	
	public void getValuesFrom(KLVPacket klv) {
		this.position = klv.get_position();
		this.key =  klv.get_key();
		this.length =  klv.get_length();
		this.value =  klv.get_value();
		//this.mxfFile = klv.getMxfFile();
	//	this.mxf = this.mxfFile.getMxf();
	}
	
	public UID get_key() { return key; }
	public long get_position() { return position; }
	public long get_length() { return length; }
	public byte[] get_value() { return value; }
	public String get_name() { return name; }
	//public MXFFile getMxfFile() { return mxfFile; }	

	public void set_length(long length) {
		this.length = length;
	}

	public void set_key(UID key) {
		this.key = key;
	}

	public void parse() throws IOException  {
   		Log.av_log("mxf", Log.AV_LOG_WARNING, "Unimplemented %s : offset: %d - size: %d bytes\n",
   				name, offset, position, length);
	}
	
	public UID getUID() { 
		if (metadatas.containsKey("instanceUID"))
			return (UID)metadatas.get("instanceUID");
		else 
			return new UID();
	}
	
	public Object getMetadata(String key) { 
		return metadatas.get(key);
	}


	public long get_offset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void resolveRefs() {	
		
	}

	public void print() {
		System.out.println(name + " " + key);
		printMeta();
	}
	


	
	public void printMeta() {
		for (Entry<String, Object> entry : metadatas.entrySet()) {
			Object value = entry.getValue();
			
			if (value instanceof UID []) {
				UID [] values = (UID[]) value;
				System.out.println("  " + entry.getKey() + ": [" + UtilsString.join(values, ", ") + "]");
								
			} else if (value instanceof Int32 []) {
				Int32 [] values = (Int32[]) value;
				System.out.println("  " + entry.getKey() + ": [" + UtilsString.join(values, ", ") + "]");
									
			} else {
				System.out.println("  " + entry.getKey() + ": " + value );
			}
		}
	}
	
	
	public String toString() {
   		return name + " - " + key + ": offset: " + position + " - size: " + length + " bytes";
	}



	

}
