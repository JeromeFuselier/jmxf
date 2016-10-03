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

package uk.ac.liv.ffmpeg.libavutil;

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;


public class SampleFmt {

	
	public enum AVSampleFormat {
	    AV_SAMPLE_FMT_NONE,
	    AV_SAMPLE_FMT_U8,          ///< unsigned 8 bits
	    AV_SAMPLE_FMT_S16,         ///< signed 16 bits
	    AV_SAMPLE_FMT_S32,         ///< signed 32 bits
	    AV_SAMPLE_FMT_FLT,         ///< float
	    AV_SAMPLE_FMT_DBL,         ///< double
	    AV_SAMPLE_FMT_NB           ///< Number of sample formats. DO NOT USE if linking dynamically
	}
	

	public static Map<AVSampleFormat, SampleFmtInfo> sample_fmt_info;
	

	static {
		sample_fmt_info = new HashMap<AVSampleFormat, SampleFmtInfo>();
		
		sample_fmt_info.put(AVSampleFormat.AV_SAMPLE_FMT_U8, 
							new SampleFmtInfo("u8", 8));
		sample_fmt_info.put(AVSampleFormat.AV_SAMPLE_FMT_S16, 
						    new SampleFmtInfo("s16", 16));
		sample_fmt_info.put(AVSampleFormat.AV_SAMPLE_FMT_S32, 
							new SampleFmtInfo("s32", 32));
		sample_fmt_info.put(AVSampleFormat.AV_SAMPLE_FMT_FLT, 
							new SampleFmtInfo("flt", 32));
		sample_fmt_info.put(AVSampleFormat.AV_SAMPLE_FMT_DBL, 
							new SampleFmtInfo("dbl", 64));		
		
	}

	public static String av_get_sample_fmt_name(AVSampleFormat sample_fmt) {
		if (sample_fmt == AVSampleFormat.AV_SAMPLE_FMT_NONE)
			return "";
		return sample_fmt_info.get(sample_fmt).get_name();
	};
	
	public static AVSampleFormat av_get_sample_fmt(String name) {
		for (AVSampleFormat fmt : AVSampleFormat.values()) {
			String tmp_name = av_get_sample_fmt_name(fmt);
			
			if (tmp_name != null) {
				if (tmp_name.equals(name))
					return fmt;
			}
			
		}
		return AVSampleFormat.AV_SAMPLE_FMT_NONE;
	}

	public static int av_get_bytes_per_sample(AVSampleFormat sample_fmt) {
		if (!sample_fmt_info.containsKey(sample_fmt))
			return 0;
		else
			return sample_fmt_info.get(sample_fmt).get_bits() >> 3;
	};
	
	

}
