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

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVOptionValue;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVOption.AVOptionType;

public class Options {
	
	

	public static void add_default_options(AVClass cls) {

		int D = AVOption.AV_OPT_FLAG_DECODING_PARAM;
		int E = AVOption.AV_OPT_FLAG_ENCODING_PARAM;
		
		cls.add_option(new AVOption("probesize", "set probing size", "probesize", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(5000000), 32, Integer.MAX_VALUE, D));
		cls.add_option(new AVOption("muxrate", "set mux rate", "mux_rate", AVOptionType.FF_OPT_TYPE_INT,  new AVOptionValue(0), 0, Integer.MAX_VALUE, E));
		cls.add_option(new AVOption("packetsize", "set packet size", "packet_size", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(0), 0, Integer.MAX_VALUE, E));
		cls.add_option(new AVOption("fflags", "", "flags", AVOptionType.FF_OPT_TYPE_FLAGS, new AVOptionValue(0), Integer.MIN_VALUE, Integer.MAX_VALUE, D|E, "fflags"));
		cls.add_option(new AVOption("ignidx", "ignore index", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_IGNIDX), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("genpts", "generate pts", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_GENPTS), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("nofillin", "do not fill in missing values that can be exactly calculated", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_NOFILLIN), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("noparse", "disable AVParsers, this needs nofillin too", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_NOPARSE), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("igndts", "ignore dts", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_IGNDTS), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("sortdts", "try to interleave outputted packets by dts", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_SORT_DTS), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("keepside", "dont merge side data", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_KEEP_SIDE_DATA), Integer.MIN_VALUE, Integer.MAX_VALUE, D, "fflags"));
		cls.add_option(new AVOption("latm", "enable RTP MP4A-LATM payload", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.AVFMT_FLAG_MP4A_LATM), Integer.MIN_VALUE, Integer.MAX_VALUE, E, "fflags"));
		cls.add_option(new AVOption("analyzeduration", "how many microseconds are analyzed to estimate duration", "max_analyze_duration", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(5*AVUtil.AV_TIME_BASE), 0, Integer.MAX_VALUE, D));
		cls.add_option(new AVOption("cryptokey", "decryption key", "key", AVOptionType.FF_OPT_TYPE_BINARY, new AVOptionValue(0), 0, 0, D));
		cls.add_option(new AVOption("indexmem", "max memory used for timestamp index (per stream)", "max_index_size", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(1<<20), 0, Integer.MAX_VALUE, D));
		cls.add_option(new AVOption("rtbufsize", "max memory used for buffering real-time frames", "max_picture_buffer", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(3041280), 0, Integer.MAX_VALUE, D)); /* defaults to 1s of 15fps 352x288 YUYV422 video */
		cls.add_option(new AVOption("fdebug", "print specific debug info", "debug", AVOptionType.FF_OPT_TYPE_FLAGS, new AVOptionValue(0), 0, Integer.MAX_VALUE, E|D, "fdebug"));
		cls.add_option(new AVOption("ts", "", null, AVOptionType.FF_OPT_TYPE_CONST, new AVOptionValue(AVFormat.FF_FDEBUG_TS), Integer.MIN_VALUE, Integer.MAX_VALUE, E|D, "fdebug"));
		cls.add_option(new AVOption("max_delay", "maximum muxing or demuxing delay in microseconds", "max_delay", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(0), 0, Integer.MAX_VALUE, E|D));
		cls.add_option(new AVOption("fpsprobesize", "number of frames used to probe fps", "fps_probe_size", AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(-1), -1, Integer.MAX_VALUE-1, D));

	}
	
}
