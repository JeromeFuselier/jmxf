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

import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class AVFormatParameters {
	
    AVRational time_base;
    int sample_rate;
    int channels;
    int width;
    int height;
    PixelFormat pix_fmt;
    int channel;
    String standard;
    int mpeg2ts_raw;
    int mpeg2ts_compute_pcr;
    int initial_pause;
    int prealloced_context;
    
	public AVFormatParameters() {
		super();
		time_base = new AVRational();
		pix_fmt = PixelFormat.PIX_FMT_NONE;
		standard = "";
	}

	public AVRational get_time_base() {
		return time_base;
	}

	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
	}

	public int get_sample_rate() {
		return sample_rate;
	}

	public void set_sample_rate(int sample_rate) {
		this.sample_rate = sample_rate;
	}

	public int get_channels() {
		return channels;
	}

	public void set_channels(int channels) {
		this.channels = channels;
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
	
	public int get_channel() {
		return channel;
	}
	
	public void set_channel(int channel) {
		this.channel = channel;
	}

	public String get_standard() {
		return standard;
	}

	public void set_standard(String standard) {
		this.standard = standard;
	}

	public int get_mpeg2ts_raw() {
		return mpeg2ts_raw;
	}

	public void set_mpeg2ts_raw(int mpeg2ts_raw) {
		this.mpeg2ts_raw = mpeg2ts_raw;
	}

	public int get_mpeg2ts_compute_pcr() {
		return mpeg2ts_compute_pcr;
	}

	public void set_mpeg2ts_compute_pcr(int mpeg2ts_compute_pcr) {
		this.mpeg2ts_compute_pcr = mpeg2ts_compute_pcr;
	}
	
	public int get_initial_pause() {
		return initial_pause;
	}
	
	public void set_initial_pause(int initial_pause) {
		this.initial_pause = initial_pause;
	}

	public int get_prealloced_context() {
		return prealloced_context;
	}

	public void set_prealloced_context(int prealloced_context) {
		this.prealloced_context = prealloced_context;
	}

	public int av_set_parameters(AVFormatContext s) {
		
		// TODO Impl
		return -1;
	}


}
