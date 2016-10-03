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
 * Creation   : March 2012
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class AVFormatContextClass extends AVClass {
	

	public AVFormatContextClass() {
		super();	
		class_name = "AVFormatContext";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();	
		Options.add_default_options(this);		
	}
	
	
	public String item_name(Object obj) {
		AVFormatContext ctx = (AVFormatContext) obj;
		if (ctx.get_iformat() != null)
			return ctx.get_iformat().get_name();
		else if (ctx.get_oformat() != null)
			return ctx.get_oformat().get_name();
		else
			return "NULL";
	}	

	public static AVOption opt_find(Object obj, String name, String unit, int opt_flags, int search_flags)
	{   
		AVFormatContext s = (AVFormatContext) obj;		
			    
		if (s.get_priv_data() != null) {
			if (s.get_iformat() != null) {
				if (s.get_iformat().get_priv_class() == null)
					return null;
			} else {
				return null;
			}
			if (s.get_oformat() != null) {
				if (s.get_oformat().get_priv_class() == null)
					return null;
			} else {
				return null;
			}
			return AVOption.av_opt_find(s.get_priv_data(), name, unit, opt_flags, search_flags);
		 }
		
		for (AVInputFormat ifmt: AVFormat.inputFormats.values()) {
			if (ifmt.get_priv_class() != null) {
				return AVOption.av_opt_find(ifmt.get_priv_class(), name, unit, opt_flags, search_flags);
			}
		}
	
		for (AVOutputFormat ofmt: AVFormat.outputFormats.values()) {
			if (ofmt.get_priv_class() != null) {
				return AVOption.av_opt_find(ofmt.get_priv_class(), name, unit, opt_flags, search_flags);
			}
		}
		
		return null;
	}

}
