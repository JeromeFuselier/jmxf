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

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;

public class CodecUL {

	UID uid;
	int matchingLen;
	AVMediaType type = null;
	CodecID id = null;
	

	public CodecUL(UID uid, int matchingLen, AVMediaType type) {
		super();
		this.uid = uid;
		this.matchingLen = matchingLen;
		this.type = type;
	}


	public CodecUL(UID uid, int matchingLen, CodecID id) {
		super();
		this.uid = uid;
		this.matchingLen = matchingLen;
		this.id = id;
	}


	public String toString() {
		if (id == null)
			return "MXFCodecUL [uid=" + uid + ", matchingLen=" + matchingLen
					+ ", type=" + type + "]";
		else
			return "MXFCodecUL [uid=" + uid + ", matchingLen=" + matchingLen
			+ ", id=" + id + "]";
	}


	public UID getUID() {
		return uid;
	}


	public int getMatchingLen() {
		return matchingLen;
	}


	public AVMediaType getType() {
		return type;
	}


	public CodecID getID() {
		return id;
	}
	
	
	
	
}
