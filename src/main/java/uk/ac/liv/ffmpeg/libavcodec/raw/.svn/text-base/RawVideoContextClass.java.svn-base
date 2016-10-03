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

package uk.ac.liv.ffmpeg.libavcodec.raw;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVOptionValue;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVOption.AVOptionType;

public class RawVideoContextClass extends AVClass {

	public RawVideoContextClass() {
		super();	
		class_name = "rawdec";		
		version = AVUtil.LIBAVUTIL_VERSION_INT();				
		add_option(new AVOption("top", "top field first", "tff", 
				AVOptionType.FF_OPT_TYPE_INT, new AVOptionValue(-1), -1, 1, 
				AVOption.AV_OPT_FLAG_DECODING_PARAM|AVOption.AV_OPT_FLAG_VIDEO_PARAM));
	}
		


}
