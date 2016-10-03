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

package uk.ac.liv.ffmpeg.libavcodec.pcm;

import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;


public class PCM_S16LE extends PCM {
	

	public PCM_S16LE() {
		super();
		this.name = "pcm_s16le";		
		this.long_name = "PCM signed 16-bit little-endian";
		this.id = CodecID.CODEC_ID_PCM_S16LE;
		
		this.sample_fmts.add(AVSampleFormat.AV_SAMPLE_FMT_S16);
		this.sample_fmts.add(AVSampleFormat.AV_SAMPLE_FMT_NONE);
	}
	
	
	

}
