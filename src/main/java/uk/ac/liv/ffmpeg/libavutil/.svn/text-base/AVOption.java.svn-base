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

import java.lang.reflect.Field;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.util.OutOIDIL;
import uk.ac.liv.util.OutOL;


public class AVOption {	
	
	
	public enum AVOptionType {
		FF_OPT_TYPE_FLAGS,
	 	FF_OPT_TYPE_INT,
	 	FF_OPT_TYPE_INT64,
	 	FF_OPT_TYPE_DOUBLE,
	 	FF_OPT_TYPE_FLOAT,
	 	FF_OPT_TYPE_STRING,
	 	FF_OPT_TYPE_ENUM,
	 	FF_OPT_TYPE_RATIONAL,
	 	FF_OPT_TYPE_BINARY,  // offset must point to a pointer immediately followed by an int for the length
	 	FF_OPT_TYPE_CONST 
	}
	
	
	public static final int AV_OPT_FLAG_ENCODING_PARAM = 1; // a generic parameter which can be set by the user for muxing or encoding
	public static final int AV_OPT_FLAG_DECODING_PARAM = 2; // a generic parameter which can be set by the user for demuxing or decoding
	public static final int AV_OPT_FLAG_METADATA       = 4; // some data extracted or inserted into the file like title, comment, ...
	public static final int AV_OPT_FLAG_AUDIO_PARAM    = 8;
	public static final int AV_OPT_FLAG_VIDEO_PARAM    = 16;
	public static final int AV_OPT_FLAG_SUBTITLE_PARAM = 32;

	public static final int AV_OPT_SEARCH_CHILDREN =0x0001; /**< Search in possible children of the
	                                             			given object first. */

	
	public static AVOption av_opt_find(Object obj, String name, String unit, int opt_flags, 
				int search_flags) {
		AVClass c = (AVClass) obj;
	    
		if ( (search_flags & AV_OPT_SEARCH_CHILDREN) == AV_OPT_SEARCH_CHILDREN) {
			AVOption o = c.opt_find(obj, name, unit, opt_flags, search_flags);
			if (o != null) {
				return o;
			}
		}
				
		for (AVOption o: c.get_options().values()) {
			if ( (o.get_flags() & opt_flags) == opt_flags) {			
				if (unit == null) {
					if (o.get_name().equals(name))
						return o;
				} else {
					if (unit.equals(o.get_unit()))
						return o;
				}
			}			
		}
		
		return null;
	}
	


	public static int av_opt_show2(AVClass cls, int req_flags, int rej_flags) {
		if (cls == null)
			return -1;
		
		System.out.println(cls.get_class_name() + " AVOptions:");
		opt_list(cls, null, req_flags, rej_flags);
		return 0;
	}


	private static void opt_list(AVClass cls, String unit, int req_flags, int rej_flags) {
		for (AVOption opt: cls.get_options().values()) {
			if ( ((opt.get_flags() & req_flags) == 0)  || ((opt.get_flags() & rej_flags) != 0) ) 
				continue;

	        /* Don't print CONST's on level one.
	         * Don't print anything but CONST's on level two.
	         * Only print items from the requested unit.
	         */
			if ( (unit == null) && (opt.get_type() == AVOptionType.FF_OPT_TYPE_CONST) )
				continue;
			else if ( (unit != null) && (opt.get_type() != AVOptionType.FF_OPT_TYPE_CONST) )
				continue;
			else if ( (unit != null) && (opt.get_type() != AVOptionType.FF_OPT_TYPE_CONST) && (!unit.equals(opt.get_unit())) )
				continue;
			else if ( (unit != null) && (opt.get_type() == AVOptionType.FF_OPT_TYPE_CONST) )
				System.out.print(String.format("   %-15s ", opt.get_name()));
			else
				System.out.print(String.format("   %-17s ", opt.get_name()));
			
			switch (opt.get_type()) {
			case FF_OPT_TYPE_FLAGS:
				System.out.print(String.format("%-7s ", "<flags>"));
				break;
			case FF_OPT_TYPE_INT:
				System.out.print(String.format("%-7s ", "<int>"));
				break;
			case FF_OPT_TYPE_INT64:
				System.out.print(String.format("%-7s ", "<int64>"));
				break;
			case FF_OPT_TYPE_DOUBLE:
				System.out.print(String.format("%-7s ", "<double>"));
				break;
			case FF_OPT_TYPE_FLOAT:
				System.out.print(String.format("%-7s ", "<float>"));
				break;
			case FF_OPT_TYPE_STRING:
				System.out.print(String.format("%-7s ", "<string>"));
				break;
			case FF_OPT_TYPE_RATIONAL:
				System.out.print(String.format("%-7s ", "<rational>"));
				break;
			case FF_OPT_TYPE_BINARY:
				System.out.print(String.format("%-7s ", "<binary>"));
				break;
			case FF_OPT_TYPE_CONST:
			default:
				System.out.print(String.format("%-7s ", ""));
				break;
			}
			System.out.print(opt.has_flag(AV_OPT_FLAG_ENCODING_PARAM) ? 'E' : '.');
			System.out.print(opt.has_flag(AV_OPT_FLAG_DECODING_PARAM) ? 'D' : '.');
			System.out.print(opt.has_flag(AV_OPT_FLAG_VIDEO_PARAM) ? 'V' : '.');
			System.out.print(opt.has_flag(AV_OPT_FLAG_AUDIO_PARAM) ? 'A' : '.');
			System.out.print(opt.has_flag(AV_OPT_FLAG_SUBTITLE_PARAM) ? 'S' : '.');
			
			if (opt.get_help() != null)
				System.out.print(opt.get_help());
			System.out.print("\n");
			if ( (opt.get_unit() != null) && (opt.get_type() != AVOptionType.FF_OPT_TYPE_CONST) ) {
				opt_list(cls, opt.get_unit(), req_flags, rej_flags);
			}
			
		}
		
	}
		 
	
	  
	String name;

	// short English help text
	String help;

	// The name of the field in the class
	String offset;
	AVOptionType type;
	
	//  the default value for scalar options
	AVOptionValue default_val;
	
	double min;                 // minimum valid value for the option
	double max;                 // maximum valid value for the option

	int flags;
	
	// The logical unit to which the option belongs. Non-constant
	// options and corresponding named constants share the same
	// unit. May be NULL.
	String unit;
	
	public AVOption(String name, String help, String offset, AVOptionType type,
			AVOptionValue default_val, double min, double max) {
		this.name = name;
		this.help = help;
		this.offset = offset;
		this.type = type;
		this.default_val = default_val;
		this.min = min;
		this.max = max;
		this.flags = 0;
	}
	

	public AVOption(String name, String help, String offset, AVOptionType type,
			AVOptionValue default_val, double min, double max, int flags) {
		this.name = name;
		this.help = help;
		this.offset = offset;
		this.type = type;
		this.default_val = default_val;
		this.min = min;
		this.max = max;
		this.flags = flags;
	}

	public AVOption(String name, String help, String offset, AVOptionType type,
			AVOptionValue default_val, double min, double max, int flags,
			String unit) {
		this.name = name;
		this.help = help;
		this.offset = offset;
		this.type = type;
		this.default_val = default_val;
		this.min = min;
		this.max = max;
		this.flags = flags;
		this.unit = unit;
	}

	public AVOption(String name, String help, int offset, AVOptionType type,
			AVOptionValue default_val, double min, double max, int flags,
			String unit) {
		this.name = name;
		this.help = help;
		this.offset = null;
		this.type = type;
		this.default_val = default_val;
		this.min = min;
		this.max = max;
		this.flags = flags;
		this.unit = unit;
	}



	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public String get_help() {
		return help;
	}

	public void set_help(String help) {
		this.help = help;
	}

	public String get_offset() {
		return offset;
	}

	public void set_offset(String offset) {
		this.offset = offset;
	}

	public AVOptionType get_type() {
		return type;
	}

	public void set_type(AVOptionType type) {
		this.type = type;
	}

	public AVOptionValue get_default_val() {
		return default_val;
	}

	public void set_default_val(AVOptionValue default_val) {
		this.default_val = default_val;
	}

	public double get_min() {
		return min;
	}

	public void set_min(double min) {
		this.min = min;
	}

	public double get_max() {
		return max;
	}

	public void set_max(double max) {
		this.max = max;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public String get_unit() {
		return unit;
	}

	public void set_unit(String unit) {
		this.unit = unit;
	}


	public String toString() {
		return name + "= " + default_val;
	}



	private boolean has_flag(int flag) {
		return (this.flags & flag) == flag;
	}
	
	
	

	public static OutOL av_get_int(Object obj, AVClass cls, String name, boolean get_option) {
		AVOption o_out = null;
		long intnum = 1;
	    double num = 1;
	    int den = 1;
	    
		
	    OutOIDIL tmp = av_get_number(obj, cls, name, get_option, num, den, intnum);
	    if (get_option)
	    	o_out = (AVOption) tmp.get_obj();
	    int ret = tmp.get_val1();
	    num = tmp.get_val2();
	    den = tmp.get_val3();
	    intnum = tmp.get_val4();
	    if (ret < 0)
	        return new OutOL(o_out, -1);
	    return new OutOL(o_out, (int)num * intnum / den);
	}

    		
	private static OutOIDIL av_get_number(Object obj, AVClass cls, String name, boolean get_option, double num, int den, long intnum) {
		//AVOption o = AVOption.av_opt_find(obj, name, null, 0, 0);
		AVOption o = AVOption.av_opt_find(cls, name, null, 0, 0);
		AVOption o_out = null;
		
		if ( (o == null) || (o.get_offset() == null) )
			return new OutOIDIL(null, -1, 0, 0, 0);
		
		if (get_option)
			o_out = o;
		
    	Field f;
		try {
			f = obj.getClass().getDeclaredField(o.get_offset());
		
			
		switch (o.get_type()) {
		case FF_OPT_TYPE_FLAGS:
		case FF_OPT_TYPE_INT:
			intnum = f.getInt(obj);			
			break;
		case FF_OPT_TYPE_INT64:
			intnum = f.getLong(obj);
			break;
		case FF_OPT_TYPE_FLOAT:
			num = f.getDouble(obj);
			break;
		case FF_OPT_TYPE_RATIONAL:
			AVRational r = (AVRational)f.get(obj);
			intnum = r.get_num();
			den = r.get_den();
			break;
		case FF_OPT_TYPE_CONST:
			intnum = (long)o.get_default_val().get_dbl();
			break;			
		
		}

		} catch (SecurityException e1) {
			return new OutOIDIL(null, -1, 0, 0, 0);		
		} catch (NoSuchFieldException e1) {
			return new OutOIDIL(null, -1, 0, 0, 0);		
		} catch (IllegalArgumentException e) {
			return new OutOIDIL(null, -1, 0, 0, 0);			
		} catch (IllegalAccessException e) {
			return new OutOIDIL(null, -1, 0, 0, 0);
			
		}
		
		return new OutOIDIL(o_out, 0, num, den, intnum);
	}
	

}
