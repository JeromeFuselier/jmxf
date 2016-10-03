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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;


/**
 * Describe the class of an AVClass context structure. That is an
 * arbitrary struct of which the first field is a pointer to an
 * AVClass struct (e.g. AVCodecContext, AVFormatContext etc.).
 */

public class AVClass {
	
	// The name of the class; usually it is the same name as the
    // context structure type to which the AVClass is associated.
    protected String class_name;

    // A pointer to the first option specified in the class if any or NULL
	protected Map<String,AVOption> option = new HashMap<String,AVOption>();
    
    // LIBAVUTIL_VERSION with which this structure was created.
    // This is used to allow fields to be added without requiring major
    // version bumps everywhere.
	protected int version;

    // Offset in the structure where log_level_offset is stored.
    // 0 means there is no such variable
	protected String log_level_offset_offset;

    // Offset in the structure where a pointer to the parent context for loging is stored.
    // for example a decoder that uses eval.c could pass its AVCodecContext to eval as such
    // parent context. And a av_log() implementation could then display the parent context
    // can be NULL of course
	protected String parent_log_context_offset;
    

    public AVClass(String class_name, int version) {
		super();
		this.class_name = class_name;
		this.version = version;
	}


	public AVClass() {
	}
		
	public AVClass(AVClass cls) {
		super();
		set_class_name(cls.get_class_name());
		set_version(cls.get_version());
		set_log_level_offset_offset(cls.get_log_level_offset_offset());
		set_parent_log_context_offset(cls.get_parent_log_context_offset());
		
		for (String key : cls.get_options().keySet()) {
			option.put(key, cls.get_option(key));
		}
	    	   
	}


	public String get_class_name() {
		return class_name;
	}

	public void set_class_name(String class_name) {
		this.class_name = class_name;
	}

	public Map<String,AVOption> get_options() {
		return option;
	}

	public AVOption get_option(String name) {
		if (option.containsKey(name))
			return option.get(name);
		else
			return null;
	}

	public int get_version() {
		return version;
	}

	public void set_version(int version) {
		this.version = version;
	}

	public String get_log_level_offset_offset() {
		return log_level_offset_offset;
	}

	public void set_log_level_offset_offset(String log_level_offset_offset) {
		this.log_level_offset_offset = log_level_offset_offset;
	}

	public String get_parent_log_context_offset() {
		return parent_log_context_offset;
	}

	public void set_parent_log_context_offset(String parent_log_context_offset) {
		this.parent_log_context_offset = parent_log_context_offset;
	}


	// A function which returns the name of a context
    //instance ctx associated with the class.
	public String item_name(Object obj) {
		return class_name;
    }


	public void add_option(AVOption opt) {	
		this.option.put(opt.get_name(), opt);		
	}
	
	
	public void print_options() {
		for (AVOption opt : option.values()) {
			System.out.println(opt);
		}
	}
	

	protected static AVOption opt_find(Object obj, String name, String unit, int opt_flags, int search_flags)
	{
	    return null;
	}


	public void av_opt_set_defaults() {
	    av_opt_set_defaults2(0, 0);
	}
	
	protected void av_opt_set_defaults2(int mask, int flags) {

		for (AVOption opt: this.get_options().values()) {

			if ((opt.get_flags() & mask) != flags)
	            continue;
			
			switch (opt.get_type()) {
			case FF_OPT_TYPE_CONST:
                /* Nothing to be done here */
            break;
			case FF_OPT_TYPE_ENUM: {
                Object val = opt.get_default_val().get_obj();
                av_set_enum(opt.get_name(), val);
			}
            break;
            case FF_OPT_TYPE_FLAGS:
            case FF_OPT_TYPE_INT: {
                int val;
                val = opt.get_default_val().get_int();
                av_set_int(opt.get_name(), val);
            }
            break;
            case FF_OPT_TYPE_INT64: {
            	long val = opt.get_default_val().get_long();

               // if ((double)(opt.get_default_val().get_dbl()+0.6) == opt.get_default_val().get_dbl())
                   // LogFactory.log(Level.WARNING, "loss of precision in default of " + opt.get_name());
                av_set_int(opt.get_name(), opt.get_default_val().get_long());
            }
            break;
            case FF_OPT_TYPE_DOUBLE: {
                double val;
                val = opt.get_default_val().get_dbl();
                av_set_double(opt.get_name(), val);
            }
            break;
            case FF_OPT_TYPE_FLOAT: {
                float val;
                val = opt.get_default_val().get_flt();
                av_set_float(opt.get_name(), val);
            }
            break;
            case FF_OPT_TYPE_RATIONAL: {
                AVRational val;
                val = AVRational.av_d2q(opt.get_default_val().get_dbl(), Integer.MAX_VALUE);
                av_set_q(opt.get_name(), val);
            }
            break;
            case FF_OPT_TYPE_STRING:
                av_set_string3(opt.get_name(), opt.get_default_val().get_str(), 1, null);
                break;
            case FF_OPT_TYPE_BINARY:
                /* Cannot set default for binary */
            break;
            default:
            	Log.av_log("avclass", Log.AV_LOG_DEBUG, "AVOption type %d of option %s not implemented yet\n",
            			opt.get_type(), opt.get_name());
			}
			
		}
		
		
	}
	

	private AVOption av_set_enum(String name, Object val) {
		AVOption o = AVOption.av_opt_find(this, name, null, 0, 0);		

		if ( (o == null) || (o.get_offset() == null) )
			return null;		
	    
	    try {
	    	Field f = this.getClass().getDeclaredField(o.get_offset());	
	    	f.setAccessible(true);
	    	f.set(this, val);
		} catch (SecurityException e) {
			System.out.println(o.get_offset() + " SecurityException");
		} catch (NoSuchFieldException e) {
			System.out.println(o.get_offset() + " NoSuchFieldException");
		} catch (IllegalArgumentException e) {
			System.out.println(o.get_offset() + " IllegalArgumentException");
		} catch (IllegalAccessException e) {
			System.out.println(o.get_offset() + " IllegalAccessException");
		}
		
		return o;
	}
	

	private AVOption av_set_int(String name, double n) {
	    return av_set_number(name, 1, 1, n);
		
	}

	private AVOption av_set_number(String name, double num, int den, 
					double intnum) {
		return av_set_number2(name, num, den, intnum);
	}

	
	private AVOption av_set_number2(String name, double num, int den,
			double intnum) {
		AVOption o = AVOption.av_opt_find(this, name, null, 0, 0);		
		
		if ( (o == null) || (o.get_offset() == null) )
			return null;
		
	    if ( (o.get_max() * den < num * intnum) || (o.get_min()*den > num*intnum) ) {
	        Log.av_log("avclass", Log.AV_LOG_ERROR, "Value %lf for parameter '%s' out of range\n", 
	        		num, name);
	        return null;
	    }
	    
	    try {
	    	Field f = this.getClass().getDeclaredField(o.get_offset());	
	    	f.setAccessible(true);
			
	    	switch (o.get_type()) {
	    	case FF_OPT_TYPE_FLAGS:
	    	case FF_OPT_TYPE_INT:
	    		f.setInt(this, (int)(Math.rint(num/den)*intnum));
	    		break;
	        case FF_OPT_TYPE_INT64: 
	        	f.setLong(this, (long)(Math.rint(num/den)*intnum)); 
	        	break;
	        case FF_OPT_TYPE_FLOAT: 
	        	f.setFloat(this, (float) (num * intnum / den));
	        	break;
	        case FF_OPT_TYPE_DOUBLE:
	        	f.setDouble(this, num * intnum / den);
	        	break;
	        case FF_OPT_TYPE_RATIONAL:
	            if ((int)num == num) 
	            	f.set(this, new AVRational((int)(num * intnum), den));
	            else
	            	f.set(this, AVRational.av_d2q(num*intnum/den, 1<<24)) ;
	            break;
	    	}
		} catch (SecurityException e) {
			System.out.println(o.get_offset() + " SecurityException");
		} catch (NoSuchFieldException e) {
			System.out.println(o.get_offset() + " NoSuchFieldException");
		} catch (IllegalArgumentException e) {
			System.out.println(o.get_offset() + " IllegalArgumentException");
		} catch (IllegalAccessException e) {
			System.out.println(o.get_offset() + " IllegalAccessException");
		}
		return o;
	}

	private AVOption av_set_q(String name, AVRational n) {
		return av_set_number(name, n.get_num(), n.get_den(), 1);
		
	}

	private AVOption av_set_double(String name, double n) {
		return av_set_number(name, n, 1, 1);
		
	}

	private AVOption av_set_float(String name, float n) {
		return av_set_number(name, n, 1, 1);
		
	}

	protected int av_set_string3(String name, String get_str, int i,
			Object object) {
		// TODO
		AVOption o = AVOption.av_opt_find(this, name, null, 0, 0);		
		
		if (o == null)
			return 0;
		return 0;
	}
	

}
