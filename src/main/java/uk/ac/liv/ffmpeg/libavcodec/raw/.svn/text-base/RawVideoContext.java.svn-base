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

package uk.ac.liv.ffmpeg.libavcodec.raw;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavutil.AVClass;

public class RawVideoContext extends AVContext {
	
    AVClass av_class;
    long [] palette = new long[AVCodec.AVPALETTE_COUNT];
    byte [] buffer;  /* block of memory for holding one frame */
    
    int flip;
    AVFrame pic = new AVFrame();             ///< AVCodecContext.coded_frame
    int tff = -1;
       
    
    
    public RawVideoContext() {
		super();
		set_av_class(new RawVideoContextClass());
	}

	public AVClass get_av_class() {
		return av_class;
	}

	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}
	
	public long[] get_palette() {
		return palette;
	}

	public void set_palette(long[] palette) {
		this.palette = palette;
	}

	public byte[] get_buffer() {
		return buffer;
	}

	public void set_buffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int get_flip() {
		return flip;
	}

	public void set_flip(int flip) {
		this.flip = flip;
	}

	public AVFrame get_pic() {
		return pic;
	}

	public void set_pic(AVFrame pic) {
		this.pic = pic;
	}
	
	public int get_tff() {
		return tff;
	}
	
	public void set_tff(int tff) {
		this.tff = tff;
	}
	
	public void set_length(int l) {
		buffer = new byte[l];
	}

	public int get_length() {
    	return buffer.length;
    }

}
