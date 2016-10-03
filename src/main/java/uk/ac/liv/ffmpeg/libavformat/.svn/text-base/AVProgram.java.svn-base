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

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;

public class AVProgram {
	
	int	id;
    int flags;
    AVDiscard discard;        ///< selects which program to discard and which to feed to the caller
    int [] stream_index;
    AVDictionary metadata;

    int program_num;
    int pmt_pid;
    int pcr_pid;
    
	public int get_id() {
		return id;
	}
	
	public void set_id(int id) {
		this.id = id;
	}
	
	public int get_flags() {
		return flags;
	}
	
	public void set_flags(int flags) {
		this.flags = flags;
	}
	
	public AVDiscard get_discard() {
		return discard;
	}
	
	public void set_discard(AVDiscard discard) {
		this.discard = discard;
	}
	
	public int[] get_stream_index() {
		return stream_index;
	}

	public int get_stream_index(int i) {
		return stream_index[i];
	}
	
	public void set_stream_index(int[] stream_index) {
		this.stream_index = stream_index;
	}
	
	public int get_nb_stream_index() {
		if (stream_index == null)
			return 0;
		else
			return stream_index.length;
	}
	
	public AVDictionary get_metadata() {
		return metadata;
	}
	
	public void set_metadata(AVDictionary metadata) {
		this.metadata = metadata;
	}
	
	public int get_program_num() {
		return program_num;
	}
	
	public void set_program_num(int program_num) {
		this.program_num = program_num;
	}
	
	public int get_pmt_pid() {
		return pmt_pid;
	}
	
	public void set_pmt_pid(int pmt_pid) {
		this.pmt_pid = pmt_pid;
	}
	
	public int get_pcr_pid() {
		return pcr_pid;
	}
	
	public void set_pcr_pid(int pcr_pid) {
		this.pcr_pid = pcr_pid;
	}

    
    

}
