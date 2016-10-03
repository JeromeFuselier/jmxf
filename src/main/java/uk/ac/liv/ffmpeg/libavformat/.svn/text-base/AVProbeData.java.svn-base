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

import java.io.IOException;
import java.net.URI;

import com.sun.org.apache.xpath.internal.operations.Bool;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.util.OutOI;

public class AVProbeData {	
	
	public static final int AVPROBE_SCORE_MAX = 100;    
	
	/** size of probe buffer, for guessing file type from file contents */
	public static final int PROBE_BUF_MIN = 2048;
	static final int PROBE_BUF_MAX = (1<<20);
	
	String filename;
	ByteReader reader;
	

	public AVProbeData(URI uri, int size) {
		try {
			reader = new ByteReader(uri, size);
			filename = uri.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public AVProbeData(URI uri) {
		try {
			reader = new ByteReader(uri);
			filename = uri.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AVProbeData(ByteReader reader, URI uri) {
		this.reader = reader;
		filename = uri.getPath();
	}


	public ByteReader getReader() {
		return reader;
	}
	

	public AVInputFormat av_probe_input_format(boolean is_opened) {
		int score = 0;
		OutOI ret_obj = av_probe_input_format2(is_opened, score);
		return (AVInputFormat)ret_obj.get_obj();
	}

	private OutOI av_probe_input_format2(boolean is_opened, int score_max) {
		OutOI ret_obj = av_probe_input_format3(is_opened);
	    
		if (ret_obj.get_ret() > score_max){
		    return ret_obj;
	   	} else {
	   		return new OutOI(null, 0);
	   	}		
	}

	OutOI av_probe_input_format3(boolean is_opened) {
		AVInputFormat fmt = null;
		int score;
		int score_max = 0;
		
		for (AVInputFormat fmt1 : AVFormat.inputFormats.values()) {
			if (is_opened == fmt1.has_flag(AVFormat.AVFMT_NOFILE))
				continue;
            score = fmt1.read_probe(this);
         
            if ( (score == 0) && (UtilsFormat.av_match_ext(filename, fmt1.get_extensions())) ) {
            	score = 1;
            }
            
            if (score > score_max) {
            	 score_max = score;
                 fmt = fmt1;
            } else if (score == score_max)
                fmt = null;
            
		}		

		return new OutOI(fmt, score_max);
	}

	public void add_bytes(short[] s) {
		reader.add_bytes(s);
	}

	public int get_buf_size() {
		return (int)reader.size();
	}

	public void av_freep() {
		reader.av_freep();		
	}
	
	

}
