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

import uk.ac.liv.ffmpeg.ChannelLayout;
import uk.ac.liv.ffmpeg.libavcodec.AVAudioConvert;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;

public class AudioConvert {
	
	
	/* Audio channel masks */
	public static final int AV_CH_FRONT_LEFT            = 0x00000001;
	public static final int AV_CH_FRONT_RIGHT           = 0x00000002;
	public static final int AV_CH_FRONT_CENTER          = 0x00000004;
	public static final int AV_CH_LOW_FREQUENCY         = 0x00000008;
	public static final int AV_CH_BACK_LEFT             = 0x00000010;
	public static final int AV_CH_BACK_RIGHT            = 0x00000020;
	public static final int AV_CH_FRONT_LEFT_OF_CENTER  = 0x00000040;
	public static final int AV_CH_FRONT_RIGHT_OF_CENTER = 0x00000080;
	public static final int AV_CH_BACK_CENTER           = 0x00000100;
	public static final int AV_CH_SIDE_LEFT             = 0x00000200;
	public static final int AV_CH_SIDE_RIGHT            = 0x00000400;
	public static final int AV_CH_TOP_CENTER            = 0x00000800;
	public static final int AV_CH_TOP_FRONT_LEFT        = 0x00001000;
	public static final int AV_CH_TOP_FRONT_CENTER      = 0x00002000;
	public static final int AV_CH_TOP_FRONT_RIGHT       = 0x00004000;
	public static final int AV_CH_TOP_BACK_LEFT         = 0x00008000;
	public static final int AV_CH_TOP_BACK_CENTER       = 0x00010000;
	public static final int AV_CH_TOP_BACK_RIGHT        = 0x00020000;
	public static final int AV_CH_STEREO_LEFT           = 0x20000000; // Stereo downmix.
	public static final int AV_CH_STEREO_RIGHT          = 0x40000000; // See AV_CH_STEREO_LEFT.
	
	public static final int AV_CH_LAYOUT_MONO           = (AV_CH_FRONT_CENTER);
	public static final int AV_CH_LAYOUT_STEREO         = (AV_CH_FRONT_LEFT|AV_CH_FRONT_RIGHT);
	public static final int AV_CH_LAYOUT_2_1            = (AV_CH_LAYOUT_STEREO|AV_CH_BACK_CENTER);
	public static final int AV_CH_LAYOUT_SURROUND       = (AV_CH_LAYOUT_STEREO|AV_CH_FRONT_CENTER);
	public static final int AV_CH_LAYOUT_4POINT0        = (AV_CH_LAYOUT_SURROUND|AV_CH_BACK_CENTER);
	public static final int AV_CH_LAYOUT_2_2            = (AV_CH_LAYOUT_STEREO|AV_CH_SIDE_LEFT|AV_CH_SIDE_RIGHT);
	public static final int AV_CH_LAYOUT_QUAD           = (AV_CH_LAYOUT_STEREO|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT);
	public static final int AV_CH_LAYOUT_5POINT0        = (AV_CH_LAYOUT_SURROUND|AV_CH_SIDE_LEFT|AV_CH_SIDE_RIGHT);
	public static final int AV_CH_LAYOUT_5POINT1        = (AV_CH_LAYOUT_5POINT0|AV_CH_LOW_FREQUENCY);
	public static final int AV_CH_LAYOUT_5POINT0_BACK   = (AV_CH_LAYOUT_SURROUND|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT);
	public static final int AV_CH_LAYOUT_5POINT1_BACK   = (AV_CH_LAYOUT_5POINT0_BACK|AV_CH_LOW_FREQUENCY);
	public static final int AV_CH_LAYOUT_7POINT0        = (AV_CH_LAYOUT_5POINT0|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT);
	public static final int AV_CH_LAYOUT_7POINT1        = (AV_CH_LAYOUT_5POINT1|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT);
	public static final int AV_CH_LAYOUT_7POINT1_WIDE   = (AV_CH_LAYOUT_5POINT1_BACK|AV_CH_FRONT_LEFT_OF_CENTER|AV_CH_FRONT_RIGHT_OF_CENTER);
	public static final int AV_CH_LAYOUT_STEREO_DOWNMIX = (AV_CH_STEREO_LEFT|AV_CH_STEREO_RIGHT);
	
	

	public static ChannelLayout [] CHANNEL_LAYOUT_MAP = {
		new ChannelLayout("mono"       ,  1, AV_CH_LAYOUT_MONO),
		new ChannelLayout("stereo"     ,  1, AV_CH_LAYOUT_STEREO),
		new ChannelLayout("4.0"        ,  2, AV_CH_LAYOUT_4POINT0),
		new ChannelLayout("quad"       ,  4, AV_CH_LAYOUT_QUAD),
		new ChannelLayout("5.0"        ,  4, AV_CH_LAYOUT_5POINT0),
		new ChannelLayout("5.0"        ,  5, AV_CH_LAYOUT_5POINT0_BACK),
		new ChannelLayout("5.1"        ,  6, AV_CH_LAYOUT_5POINT1),
		new ChannelLayout("5.1"        ,  6, AV_CH_LAYOUT_5POINT1_BACK),
		new ChannelLayout("5.1+downmix",  8, AV_CH_LAYOUT_5POINT1|AV_CH_LAYOUT_STEREO_DOWNMIX),
		new ChannelLayout("7.1"        ,  8, AV_CH_LAYOUT_7POINT1),
		new ChannelLayout("7.1(wide)"  ,  8, AV_CH_LAYOUT_7POINT1_WIDE),
		new ChannelLayout("7.1+downmix", 10, AV_CH_LAYOUT_7POINT1|AV_CH_LAYOUT_STEREO_DOWNMIX),		
	};
	

	public static String [] channel_names = {
		"FL", "FR", "FC", "LFE", "BL", "BR", "FLC", "FRC",
		"BC", "SL", "SR", "TC", "TFL", "TFC", "TFR", "TBL",
		"TBC", "TBR", "", "", "", "", "", "", "", "", "", "", "", 
		"DL", "DR" };



	public static String av_get_channel_layout_string(int nb_channels,
			long channel_layout) {
		String buf = "";
		if (nb_channels <= 0 ) {
			nb_channels = av_get_channel_layout_nb_channels(channel_layout);
		}
		
		for (ChannelLayout c : CHANNEL_LAYOUT_MAP) {
			if ( (nb_channels == c.get_nb_channels()) &&
				 (channel_layout == c.get_layout()) ) {
				return c.get_name();
			}
		}
		buf += String.format("%d channels", nb_channels);
		
		if (channel_layout != 0) {
			buf += " (";
			int ch = 0;
			for (int i = 0 ; i < 64 ; i++) {
				if ( (channel_layout & (1 <<i)) != 0 ) {
					String name = get_channel_name(i);
					if (name != "") {
						if (ch > 0) {
							buf += "|";
						}
						buf += name;
					}
					ch++;
				}
			}				
			buf += ")";			
		}
		return buf;
	}
		
		
	private static String get_channel_name(int channel_id) {
		if ( (channel_id < 0) || (channel_id >= channel_names.length))
			return "";
		return channel_names[channel_id];
	}



	public static int av_get_channel_layout_nb_channels(long channel_layout) {
	    int count;
	    long x = channel_layout;
	    for (count = 0 ; x != 0 ; count++)
	        x &= x-1; // unset lowest set bit
	    return count;
	}


	public static AVAudioConvert av_audio_convert_alloc(AVSampleFormat out_fmt, 
			int out_channels, AVSampleFormat in_fmt, int in_channels, 
			float [] matrix, int flags) {
	    if (in_channels != out_channels)
	        return null;  /* FIXME: not supported */
	    AVAudioConvert ctx = new AVAudioConvert();
	    
	    ctx.set_in_channels(in_channels);
	    ctx.set_out_channels(out_channels);
	    ctx.set_fmt_pair(out_fmt.ordinal() + AVSampleFormat.values().length * in_fmt.ordinal());
	    return ctx;
	}

}
