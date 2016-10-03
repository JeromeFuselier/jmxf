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
 * Creation   : March 2012
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavformat;

public class AVIndexEntry {
	
	public static int AVINDEX_KEYFRAME = 0x0001;
	
	long pos;
	long timestamp;
	
    int flags;
	int size; //Yeah, trying to keep the size of this small to reduce memory requirements (it is 24 vs. 32 bytes due to possible 8-byte alignment).
    int min_distance;         /**< Minimum distance between this and the previous keyframe, used to avoid unneeded searching. */
	
    public long get_pos() {
		return pos;
	}
	
    public void set_pos(long pos) {
		this.pos = pos;
	}
	
    public long get_timestamp() {
		return timestamp;
	}
	
    public void set_timestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
    public int get_flags() {
		return flags;
	}
	
    public void set_flags(int flags) {
		this.flags = flags;
	}
	
    public int getSize() {
		return size;
	}
	
    public void set_size(int size) {
		this.size = size;
	}
	
    public int get_min_distance() {
		return min_distance;
	}
	
    public void set_min_distance(int min_distance) {
		this.min_distance = min_distance;
	}
    
    
    
    
}
