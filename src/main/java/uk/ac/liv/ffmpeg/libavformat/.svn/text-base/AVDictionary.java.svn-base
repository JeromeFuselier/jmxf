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

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class AVDictionary {
	
	public static int AV_DICT_MATCH_CASE      = 1;
	public static int AV_DICT_IGNORE_SUFFIX   = 2;
	public static int AV_DICT_DONT_STRDUP_KEY = 4;   /**< Take ownership of a key that's been
	                                                  allocated with av_malloc() and children. */
	public static int AV_DICT_DONT_STRDUP_VAL = 8;   /**< Take ownership of a value that's been
	                                         allocated with av_malloc() and chilren. */
	public static int AV_DICT_DONT_OVERWRITE  = 16;   ///< Don't overwrite existing entries.
	public static int AV_DICT_APPEND          = 32;   /**< If the entry already exists, append to it.  Note that no
	                                      delimiter is added, the strings are simply concatenated. */
	
	
	
	Map<String, String> opts = new HashMap<String, String>();

	public AVDictionary() {
		super();
	}
	

	public AVDictionary(AVFormatParameters ap) {
		super();
		if (ap == null)
	        return;

		if (ap.get_time_base().get_num() != 0)
			opts.put("framerate", 
					 ap.get_time_base().get_num() + "/" + ap.get_time_base().get_den());
		
		if (ap.get_sample_rate() != 0)
			opts.put("sampleRate", 
					 Integer.toString(ap.get_sample_rate()));
		
		if (ap.get_channels() != 0)
			opts.put("channels", 
					 Integer.toString(ap.get_channels()));
		
		if (ap.get_width() != 0 || ap.get_height() != 0)
			opts.put("videoSize", 
					 ap.get_width() + "x" + ap.get_height());
	    
		if (ap.get_pix_fmt() != PixelFormat.PIX_FMT_NONE) 
			opts.put("pixelFormat", 
					 ap.get_pix_fmt().toString());
		
		if (ap.get_channels() != 0)
			opts.put("channel", 
					 Integer.toString(ap.get_channel()));
		
		if (!ap.get_standard().equals(""))
			opts.put("standard", 
					 ap.get_standard());
		
		if (ap.get_mpeg2ts_compute_pcr() != 0)
			opts.put("mpeg2tsComputePcr", 
					 Integer.toString(ap.get_mpeg2ts_compute_pcr()));
		
		if (ap.get_initial_pause() != 0)
			opts.put("initialPause", 
					 Integer.toString(ap.get_initial_pause()));
	}
	
	


	public AVDictionary(AVDictionary options) {
		super();
		for (String key : options.get_values().keySet()) {
			opts.put(key, options.get_values().get(key));
		}
	}


	public Map<String, String> get_values() {
		return opts;
	}


	public AVDictionary av_dict_copy() {
		AVDictionary dictCopy = new AVDictionary();
		
		for (Map.Entry<String, String> entry : opts.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    dictCopy.put(key, value);
		}
		
		return dictCopy;
	}


	public void put(String key, String value) {
		opts.put(key, value);
	}


	public void setOptions(Object obj) {
		
		Class<?> c = obj.getClass();
		
		for (Map.Entry<String, String> entry : opts.entrySet()) {
		    String varName = entry.getKey();
		    String value = entry.getValue();
		    
		  /*  try {
				Field field = c.getDeclaredField(varName);
				
				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}*/
		    
		}
				
	}
	
	private String get(String key) {
		return opts.get(key);
	}
	
	public int get_count() {
		return opts.size();
	}


	public void dump(String indent) {
		if (opts.size() == 0)
			return;
		
		System.out.println(indent + "Metadata:");

		for (Map.Entry<String, String> entry : opts.entrySet()) {
		    String key = entry.getKey();
		    String val = entry.getValue();
		    
		    System.out.println(indent + " " + key + " " + val);
			
		}
		
	}


	public void av_dict_set(String key, String value, int flags) {
		String old_val = opts.get(key);
		
		if (old_val != null) {
			if ( (flags & AV_DICT_DONT_OVERWRITE) != 0) 
				return;			
		}
		if (value != null) {
			if ( (flags & AV_DICT_APPEND) != 0)
				opts.put(key, old_val + value);
			else
				opts.put(key, value);
		}
		
	}


	public static void av_dict_copy(AVDictionary dst, AVDictionary src, 
			int flags) {
		
		for (Map.Entry<String, String> entry : src.get_values().entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();

			String old_val = dst.get(key);
			
			if (old_val != null) {
				if ( (flags & AV_DICT_DONT_OVERWRITE) != 0) 
					break;			

				if ( (flags & AV_DICT_APPEND) != 0)
					dst.put(key, old_val + value);
				else
					dst.put(key, value);
			} else {
				dst.put(key, value);
			}
		}
		
	}


	public String av_dict_get(String key, int flags) {
		return opts.get(key);
	}


	public void dump_metadata(String ctx, String indent) {
	    if ( (get_count() > 1) || (av_dict_get("language", 0) == null) ) {
	        Log.av_log(ctx, Log.AV_LOG_INFO, "%sMetadata:\n", indent);
	    	for (Map.Entry<String, String> entry : opts.entrySet()) {
	    		String key = entry.getKey();
    		    String value = entry.getValue();
    		    
    		    if (!key.equals("language")) {
    		    	value.replace((char)0xd, ' ');
	                Log.av_log(ctx, Log.AV_LOG_INFO, "%s  %-16s: %s\n", indent, key, value);
    		    }
	    	
	    	}
	    }
	}

}
