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

package uk.ac.liv.ffmpeg.libavutil;

public class AVComponentDescriptor {
	int plane;            ///< which of the 4 planes contains the component

	/**
	 * Number of elements between 2 horizontally consecutive pixels minus 1.
	 * Elements are bits for bitstream formats, bytes otherwise.
	 */
	int step_minus1;
	
	/**
	 * Number of elements before the component of the first pixel plus 1.
	 * Elements are bits for bitstream formats, bytes otherwise.
	 */
	int offset_plus1;
	int shift       ;            ///< number of least significant bits that must be shifted away to get the value
	int depth_minus1;            ///< number of bits in the component minus 1
	
	
	
	public AVComponentDescriptor(int plane, int step_minus1, int offset_plus1,
			int shift, int depth_minus1) {
		super();
		this.plane = plane;
		this.step_minus1 = step_minus1;
		this.offset_plus1 = offset_plus1;
		this.shift = shift;
		this.depth_minus1 = depth_minus1;
	}

	public int get_plane() {
		return plane;
	}
	
	public void set_plane(int plane) {
		this.plane = plane;
	}
	
	public int get_step_minus1() {
		return step_minus1;
	}
	
	public void set_step_minus1(int step_minus1) {
		this.step_minus1 = step_minus1;
	}
	
	public int get_offset_plus1() {
		return offset_plus1;
	}
	
	public void set_offset_plus1(int offset_plus1) {
		this.offset_plus1 = offset_plus1;
	}
	
	public int get_shift() {
		return shift;
	}
	
	public void set_shift(int shift) {
		this.shift = shift;
	}
	
	public int get_depth_minus1() {
		return depth_minus1;
	}
	
	public void set_depth_minus1(int depth_minus1) {
		this.depth_minus1 = depth_minus1;
	}
	
	
}
