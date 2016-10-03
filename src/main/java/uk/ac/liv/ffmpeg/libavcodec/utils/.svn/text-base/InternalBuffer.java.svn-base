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

package uk.ac.liv.ffmpeg.libavcodec.utils;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;


public class InternalBuffer {
	

	int last_pic_num;
	short [][] base = {null, null, null, null};
	short [][] data = {null, null, null, null};
	int [] linesize = new int[4];
	int width, height;
	PixelFormat pix_fmt;
	
	public int get_last_pic_num() {
		return last_pic_num;
	}
	
	public void set_last_pic_num(int last_pic_num) {
		this.last_pic_num = last_pic_num;
	}
	
	public short [][] get_base() {
		return base;
	}
	
	public short [] get_base(int i) {
		return base[i];
	}
	
	public void set_base(short[][] base) {
		this.base = base;
	}
	
	public void set_base(int i, short [] base) {
		this.base[i] = base;
	}
	
	public short [][] get_data() {
		return data;
	}
	
	public short [] get_data(int i) {
		return data[i];
	}
	
	public void set_data(short [][] data) {
		this.data = data;
	}
	
	public void set_data(int i, short[] data) {
		this.data[i] = data;
	}
	
	public int[] get_linesize() {
		return linesize;
	}
	
	public int get_linesize(int i) {
		return linesize[i];
	}
	
	public void set_linesize(int[] linesize) {
		this.linesize = linesize;
	}
	
	public void set_linesize(int i, int linesize) {
		this.linesize[i] = linesize;
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
	
	public PixelFormat get_pix_fmt() {
		return pix_fmt;
	}
	
	public void set_pix_fmt(PixelFormat pix_fmt) {
		this.pix_fmt = pix_fmt;
	}
	
	
	


}
