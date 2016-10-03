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

package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.libavformat.AVPanScan;
import uk.ac.liv.ffmpeg.libavutil.AVRational;

public class Mpeg1Context extends AVContext {

	MpegEncContext mpeg_enc_ctx;
	boolean mpeg_enc_ctx_allocated; 	// true if decoding context allocated
	boolean repeat_field; 			// true if we must repeat the field
	AVPanScan pan_scan;              // some temporary storage for the panscan
    int slice_count;
    int swap_uv; 					//indicate VCR2
    int save_aspect_info;
    int save_width;
    int save_height;
    int save_progressive_seq;
    AVRational frame_rate_ext;        // MPEG-2 specific framerate modificator
    int sync;                       // Did we reach a sync point like a GOP/SEQ/KEYFrame?
    
	public MpegEncContext get_mpeg_enc_ctx() {
		return mpeg_enc_ctx;
	}
	public void set_mpeg_enc_ctx(MpegEncContext mpeg_enc_ctx) {
		this.mpeg_enc_ctx = mpeg_enc_ctx;
	}
	public boolean is_mpeg_enc_ctx_allocated() {
		return mpeg_enc_ctx_allocated;
	}
	public void set_mpeg_enc_ctx_allocated(boolean mpeg_enc_ctx_allocated) {
		this.mpeg_enc_ctx_allocated = mpeg_enc_ctx_allocated;
	}
	public boolean is_repeat_field() {
		return repeat_field;
	}
	public void set_repeat_field(boolean repeat_field) {
		this.repeat_field = repeat_field;
	}
	public AVPanScan get_pan_scan() {
		return pan_scan;
	}
	public void set_pan_scan(AVPanScan pan_scan) {
		this.pan_scan = pan_scan;
	}
	public int get_slice_count() {
		return slice_count;
	}
	public void set_slice_count(int slice_count) {
		this.slice_count = slice_count;
	}
	public int get_swap_uv() {
		return swap_uv;
	}
	public void set_swap_uv(int swap_uv) {
		this.swap_uv = swap_uv;
	}
	public int get_save_aspect_info() {
		return save_aspect_info;
	}
	public void set_save_aspect_info(int save_aspect_info) {
		this.save_aspect_info = save_aspect_info;
	}
	public int get_save_width() {
		return save_width;
	}
	public void set_save_width(int save_width) {
		this.save_width = save_width;
	}
	public int get_save_height() {
		return save_height;
	}
	public void set_save_height(int save_height) {
		this.save_height = save_height;
	}
	public int get_save_progressive_seq() {
		return save_progressive_seq;
	}
	public void set_save_progressive_seq(int save_progressive_seq) {
		this.save_progressive_seq = save_progressive_seq;
	}
	public AVRational get_frame_rate_ext() {
		return frame_rate_ext;
	}
	public void set_frame_rate_ext(AVRational frame_rate_ext) {
		this.frame_rate_ext = frame_rate_ext;
	}
	public int get_sync() {
		return sync;
	}
	public void set_sync(int sync) {
		this.sync = sync;
	}

    
}
