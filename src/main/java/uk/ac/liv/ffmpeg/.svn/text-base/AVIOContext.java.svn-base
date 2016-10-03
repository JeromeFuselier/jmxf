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

package uk.ac.liv.ffmpeg;

import java.io.FileOutputStream;

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.AVInputFormat;
import uk.ac.liv.ffmpeg.libavformat.AVProbeData;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;


public class AVIOContext extends AVContext {
	
	/** size of probe buffer, for guessing file type from file contents */
	public static int PROBE_BUF_MIN = 2048;
	public static int PROBE_BUF_MAX = (1<<20);
	
	ByteReader reader;
	
	FileOutputStream fos;
	
	
	public AVIOContext(ByteReader reader) {
		super();
		this.reader = reader;
	}

	public AVIOContext() {
	}

	public ByteReader get_reader() {
		return reader;
	}

	public void set_reader(ByteReader reader) {
		this.reader = reader;
	}
	
	

	public AVInputFormat av_probe_input_buffer(AVFormatContext ctx, AVProbeData pd, int offset, int max_probe_size) {			
		return pd.av_probe_input_format(true);
	}

	public long tell() {
		return this.reader.position();
	}

	public long get_size() {
		return this.reader.size();
	}

	public void set_fos(FileOutputStream fos) {
		this.fos = fos;		
	}

	public FileOutputStream get_fos() {
		return this.fos;
	}
	
	
	
	
}
