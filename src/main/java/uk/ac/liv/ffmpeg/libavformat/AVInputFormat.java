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
import java.util.ArrayList;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.util.OutOI;


public class AVInputFormat {
    /**
     * A comma separated list of short names for the format. New names
     * may be appended with a minor bump.
     */
	protected String name;

    /**
     * Descriptive name for the format, meant to be more human-readable
     * than name. You should use the NULL_IF_CONFIG_SMALL() macro
     * to define it.
     */
	protected String long_name;

    /**
     * Can use flags: AVFMT_NOFILE, AVFMT_NEEDNUMBER.
     */
	int flags;

    /**
     * If extensions are defined, then no probe is done. You should
     * usually not use extension format guessing because it is not
     * reliable enough
     */
   ArrayList<String> extensions = new ArrayList<String>();

   /**
    * General purpose read-only value that the format can use.
    */
   int value;
   

   AVCodecTag [] codec_tag;

   AVClass priv_class; ///< AVClass for the private context

	
	public AVInputFormat() {
		super();
	}
	
	
	public AVInputFormat(String name) {
		super();
		this.name = name;
	}
	
	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public String get_long_name() {
		return long_name;
	}

	public void set_long_name(String long_name) {
		this.long_name = long_name;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public ArrayList<String> get_extensions() {
		return extensions;
	}

	public void add_extension(String ext) {
		this.extensions.add(ext);
	}

	public int get_value() {
		return value;
	}

	public void set_value(int value) {
		this.value = value;
	}

	public AVCodecTag [] get_codec_tag() {
		return codec_tag;
	}

	public void set_codec_tag(AVCodecTag [] codec_tag) {
		this.codec_tag = codec_tag;
	}

	public AVClass get_priv_class() {
		return priv_class;
	}

	public void set_priv_class(AVClass priv_class) {
		this.priv_class = priv_class;
	}


	/**
	 *  Tell if a given file has a chance of being parsed as this format.
	 */
	public int read_probe(AVProbeData p) {		
		return -1;
	}
	
	/**
	 * Read the format header and initialize the AVFormatContext structure. 
	 * @throws IOException 
	 */
    public int read_header(AVFormatContext s, AVFormatParameters ap) {
    	return -1;
    }
    
    
    /**
     * Read one packet and put it in 'pkt'. 
     * @throws IOException 
     */
    public int read_packet(AVFormatContext ctxt, AVPacket pkt) {
    	return -1;
    }
    

    public int read_seek(AVFormatContext s, int stream_index, long timestamp, int flags) {
    	return -1;
    }
    

    /**
     * Seek to timestamp ts.
     * Seeking will be done so that the point from which all active streams
     * can be presented successfully will be closest to ts and within min/max_ts.
     * Active streams are all streams that have AVStream.discard < AVDISCARD_ALL.
     */
    public int read_seek2(AVFormatContext s, int stream_index, long min_ts, 
    		long ts, long max_ts, int flags) {
    	return -1;
    }

    public long read_timestamp(AVFormatContext s, int stream_index,
    							long pos, long pos_limit) {
    	return -1;
    }
    
    /**
     * Close the stream.
     */
    public  int read_close(AVFormatContext ctxt) {
    	return -1;
    }
    

    /**
     * Start/resume playing - only meaningful if using a network-based format
     * (RTSP).
     */
    int read_play(AVFormatContext s) {
    	return -1;
    }

    /**
     * Pause playing - only meaningful if using a network-based format
     * (RTSP).
     */
    int read_pause(AVFormatContext s) {
    	return -1;
    }


	public boolean has_flag(int flag) {
		// TODO Auto-generated method stub
		return (this.flags & flag) != 0;
	}


}