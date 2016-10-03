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
 * Creation   : January 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVRational;

public class AVChapter {
	
	int id;                 ///< unique ID to identify the chapter
	AVRational time_base;   ///< time base in which the start/end timestamps are specified
	long start, end;     ///< chapter start/end time in time_base units
	AVDictionary metadata;
	
	public long get_end() {
		return end;
	}
	
	public void set_end(long end) {
		this.end = end;
	}

	public int get_id() {
		return id;
	}

	public void set_id(int id) {
		this.id = id;
	}

	public AVRational get_time_base() {
		return time_base;
	}

	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
	}

	public long get_start() {
		return start;
	}

	public void set_start(long start) {
		this.start = start;
	}

	public AVDictionary get_metadata() {
		return metadata;
	}

	public void set_metadata(AVDictionary metadata) {
		this.metadata = metadata;
	}
	
	
	
	
	
}
