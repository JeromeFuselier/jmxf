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

package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class AVCodecContextClass extends AVClass {

	public AVCodecContextClass() {
		super();	
		class_name = "AVCodecContext";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();		
		Options.add_default_options(this);		
	}
		

	// A function which returns the name of a context
    //instance ctx associated with the class.
	public String item_name(Object obj) {
		AVCodecContext avc = (AVCodecContext) obj;
		
		if (avc.get_codec() != null) {
			return avc.get_codec().get_name();
		}
		return "NULL";
    }
	
	public static AVOption opt_find(Object obj, String name, String unit, int opt_flags, int search_flags)
	{
		AVCodecContext s = (AVCodecContext) obj;

	    if (s.get_priv_data() != null) {
	        if (s.get_codec().get_priv_class() != null)
	            return AVOption.av_opt_find((AVClass)s.get_priv_data(), name, unit, opt_flags, search_flags);
	        return null;
	    }

		for (AVCodec c: AVCodec.codecs.values()) {
			if (c.get_priv_class() != null) {
				return AVOption.av_opt_find(c.get_priv_class(), name, unit, opt_flags, search_flags);				
			}
	    }
	    return null;
	}


}
