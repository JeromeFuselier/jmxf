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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.libavformat.img2.Img2Out;
import uk.ac.liv.ffmpeg.libavformat.mxf.Demux;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class AVFormat {
	

	public static final int AV_DISPOSITION_DEFAULT          = 0x0001;
	public static final int AV_DISPOSITION_DUB              = 0x0002;
	public static final int AV_DISPOSITION_ORIGINAL         = 0x0004;
	public static final int AV_DISPOSITION_COMMENT          = 0x0008;
	public static final int AV_DISPOSITION_LYRICS           = 0x0010;
	public static final int AV_DISPOSITION_KARAOKE          = 0x0020;
	public static final int AV_DISPOSITION_FORCED           = 0x0040;
	public static final int AV_DISPOSITION_HEARING_IMPAIRED = 0x0080;
	public static final int AV_DISPOSITION_VISUAL_IMPAIRED  = 0x0100;
	public static final int AV_DISPOSITION_CLEAN_EFFECTS    = 0x0200;
	
	

	public static final int AVFMT_FLAG_GENPTS         = 0x0001; // Generate missing pts even if it requires parsing future frames.
	public static final int AVFMT_FLAG_IGNIDX         = 0x0002; // Ignore index.
	public static final int AVFMT_FLAG_NONBLOCK       = 0x0004; // Do not block when reading packets from input.
	public static final int AVFMT_FLAG_IGNDTS         = 0x0008; // Ignore DTS on frames that contain both DTS & PTS
	public static final int AVFMT_FLAG_NOFILLIN       = 0x0010; // Do not infer any values from other values, just return what is stored in the container
	public static final int AVFMT_FLAG_NOPARSE        = 0x0020; // Do not use AVParsers, you also must set AVFMT_FLAG_NOFILLIN as the fillin code works on frames and no parsing -> no frames. Also seeking to frames can not work if parsing to find frame boundaries has been disabled

	public static final int AVFMT_FLAG_CUSTOM_IO      = 0x0080; // The caller has supplied a custom AVIOContext, don't avio_close() it.
	public static final int AVFMT_FLAG_MP4A_LATM      = 0x8000; // Enable RTP MP4A-LATM payload
	public static final int AVFMT_FLAG_SORT_DTS       = 0x10000; // try to interleave outputted packets by dts (using this flag can slow demuxing down)
	public static final int AVFMT_FLAG_PRIV_OPT       = 0x20000; // Enable use of private options by delaying codec open (this could be made default once all code is converted)
	public static final int AVFMT_FLAG_KEEP_SIDE_DATA = 0x40000; // Dont merge side data but keep it seperate.AVFMT_FLAG_GENPTS       0x0001 ///< Generate missing pts even if it requires parsing future frames.
	
	public static final int AVFMT_NOOUTPUTLOOP = -1;
	public static final int AVFMT_INFINITEOUTPUTLOOP = 0;
	
	public static final int AVFMT_NOFILE        = 0x0001;
	public static final int AVFMT_NEEDNUMBER    = 0x0002; // Needs '%d' in filename.
	public static final int AVFMT_SHOW_IDS      = 0x0008; // Show format stream IDs numbers.
	public static final int AVFMT_RAWPICTURE    = 0x0020; // Format wants AVPicture structure for raw picture data.
	public static final int AVFMT_GLOBALHEADER  = 0x0040; // Format wants global header.
	public static final int AVFMT_NOTIMESTAMPS  = 0x0080; // Format does not need / have any timestamps.
	public static final int AVFMT_GENERIC_INDEX = 0x0100; // Use generic index building code.
	public static final int AVFMT_TS_DISCONT    = 0x0200; // Format allows timestamp discontinuities. Note, muxers always require valid (monotone) timestamps
	public static final int AVFMT_VARIABLE_FPS  = 0x0400; // Format allows variable fps.
	public static final int AVFMT_NODIMENSIONS  = 0x0800; // Format does not need width/height
	public static final int AVFMT_NOSTREAMS     = 0x1000; // Format does not require any streams
	public static final int AVFMT_NOBINSEARCH   = 0x2000; // Format does not allow to fallback to binary search via read_timestamp
	public static final int AVFMT_NOGENSEARCH   = 0x4000; // Format does not allow to fallback to generic search
	public static final int AVFMT_TS_NONSTRICT  = 0x8000; // Format does not require strictly increasing timestamps, but they must still be monotonic
	
	public static final int AVFMTCTX_NOHEADER   = 0x0001;

	public static final int AVPROBE_SCORE_MAX    = 100;            ///< maximum score, half of that is used for file-extension-based detection
	public static final int AVPROBE_PADDING_SIZE = 32;             ///< extra allocated bytes at the end of the probe buffer

	
	public static int AVSEEK_FLAG_BACKWARD = 1;
	public static int AVSEEK_FLAG_BYTE     = 2;
	public static int AVSEEK_FLAG_ANY      = 4;
	public static int AVSEEK_FLAG_FRAME    = 8;	
	
	public static final int FF_FDEBUG_TS = 0x0001;
	
	public static final int AVINDEX_KEYFRAME = 0x0001;

	public static int MAX_PROBE_PACKETS = 2500;
	public static int RAW_PACKET_BUFFER_SIZE = 2500000;
	
	public static boolean initialized = false;

	public static Map<String,AVInputFormat> inputFormats = new HashMap<String,AVInputFormat>();
	public static Map<String,AVOutputFormat> outputFormats = new HashMap<String,AVOutputFormat>();

	

	public static void av_register_all() {
		
		if (initialized)
			return;
		initialized = true;		
	    
	    // Register Formats
	    /* demuxers */
	    addInputFormat(new Demux());
	    /* muxers ? */
	    //addOutputFormat(new AVOutputFormat("mxf"));
	    addOutputFormat(new Img2Out());
		
	}
	



	private static void addInputFormat(AVInputFormat format) {
		inputFormats.put(format.get_name(), format);
	}

	
	private static void addOutputFormat(AVOutputFormat format) {
		outputFormats.put(format.get_name(), format);
	}


	public static void printFormats() {

		System.out.println("File formats:");
		System.out.println(" D. = Demuxing supported");
		System.out.println(" .E = Muxing supported");
		System.out.println(" --");
		
		Set<String> keys = new HashSet<String>();
		for (String s: inputFormats.keySet()){
			keys.add(s);
		}
		for (String s: outputFormats.keySet()){
			keys.add(s);
		}
		

		Iterator itr = keys.iterator();
		 
		while(itr.hasNext()) {
			String key = (String)itr.next();
			AVInputFormat input = inputFormats.get(key);
			AVOutputFormat output = outputFormats.get(key);
			
			String name = input == null ? output.get_name(): input.get_name();
			String longName = input == null ? output.get_long_name(): input.get_long_name();			

			System.out.print(" " + (input == null ? " " : "D"));
			System.out.print(output == null ? " " : "E");
			System.out.print("  " + name + "  ");
			System.out.print(longName);
			System.out.println();
			
		}		
	}




	public static AVFormatContext av_open_input_file(URI uri, AVInputFormat fmt,
			AVFormatParameters ap) {
		
		AVDictionary opts = new AVDictionary(ap);
		return avFormatOpenInput(uri, fmt, opts);
	}




	private static AVFormatContext avFormatOpenInput(URI uri,
			AVInputFormat fmt, AVDictionary options) {
		
		AVFormatContext s = new AVFormatContext();
		AVFormatParameters ap = new AVFormatParameters();
		AVDictionary tmp;
		
		//s.print_options();
		
		if (fmt != null)
			s.set_iformat(fmt);
		
		if (options != null)
			tmp = options.av_dict_copy();
		else
			tmp = new AVDictionary();
		
		s.av_opt_set_dict(tmp);
		
		initInput(s, uri);
		
		s.set_duration(AVUtil.AV_NOPTS_VALUE);
		s.set_start_time(AVUtil.AV_NOPTS_VALUE);
		s.set_uri(uri);
		
		s.get_iformat().read_header(s, ap);
		
		return s;
	}




	private static void initInput(AVFormatContext s, URI uri) {
		AVProbeData pd = new AVProbeData(uri, AVProbeData.PROBE_BUF_MIN);
		AVIOContext pb = new AVIOContext(pd.getReader());
		s.set_iformat(UtilsFormat.avProbeInputFormat(pd, false));	
		
		s.set_pb(pb);
	}




	

}
