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

package uk.ac.liv.ffmpeg.libavformat;

public class AVPanScan {
	
     /**
      * id
      * - encoding: Set by user.
      * - decoding: Set by libavcodec.
      */
     int id;

     /**
      * width and height in 1/16 pel
      * - encoding: Set by user.
      * - decoding: Set by libavcodec.
      */
     int width;
     int height;

     /**
      * position of the top left corner in 1/16 pel for up to 3 fields/frames
      * - encoding: Set by user.
      * - decoding: Set by libavcodec.
      */
     int [][] position = new int[3][2];

	public int get_id() {
		return id;
	}

	public void set_id(int id) {
		this.id = id;
	}

	public int get_width() {
		return width;
	}

	public void set_width(int width) {
		this.width = width;
	}

	public int get_height() {
		return height;
	}

	public void set_height(int height) {
		this.height = height;
	}

	public int[][] get_position() {
		return position;
	}

	public void set_position(int[][] position) {
		this.position = position;
	}

	     
}
