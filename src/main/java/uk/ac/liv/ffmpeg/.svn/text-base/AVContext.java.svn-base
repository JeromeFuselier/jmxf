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

package uk.ac.liv.ffmpeg;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavformat.AVDictionary;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.Log;

public class AVContext {

	protected AVClass av_class = new AVClass();
	protected Object priv_data;

	public AVClass get_av_class() {
		return av_class;
	}


	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}
	
	
	public Object get_priv_data() {
		return priv_data;
	}


	public void set_priv_data(Object priv_data) {
		this.priv_data = priv_data;
	}
	

	public void av_opt_set_defaults() {
		av_opt_set_defaults2(0,0);
	}
	

	/** Set the values of the AVCodecContext or AVFormatContext structure.
	 * They are set to the defaults specified in the according AVOption options
	 * array default_val field.
	 */
	protected void av_opt_set_defaults2(int mask, int flags) {

		for (AVOption opt: this.av_class.get_options().values()) {

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
                av_set_int(opt.get_name(), val);
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
                Log.av_log("ctx", Log.AV_LOG_DEBUG, "AVOption type %d of option %s not implemented yet\n", 
                		opt.get_type(), opt.get_name());
			}
			
		}
		
		
	}
	

	private AVOption av_set_enum(String name, Object val) {
		AVOption o = AVOption.av_opt_find(this.av_class, name, null, 0, 0);		

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


	private AVOption av_set_q(String name, AVRational n) {
		return av_set_number(name, n.get_num(), n.get_den(), 1);
		
	}

	private AVOption av_set_double(String name, double n) {
		return av_set_number(name, n, 1, 1);
		
	}

	private AVOption av_set_float(String name, float n) {
		return av_set_number(name, n, 1, 1);
		
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
		AVOption o = AVOption.av_opt_find(this.av_class, name, null, 0, 0);		
		
		if ( (o == null) || (o.get_offset() == null) )
			return null;
		
	    if ( (o.get_max() * den < num * intnum) || (o.get_min()*den > num*intnum) ) {
	        Log.av_log("ctx", Log.AV_LOG_ERROR, "Value %lf for parameter '%s' out of range\n", num, name);
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
	


	public int av_opt_set_dict(AVDictionary options) {
		int ret = 0;
		if (options == null)
			return ret;
		
		Map<String, String> opts = options.get_values();
		for (String key : opts.keySet()) {
			String value = opts.get(key);
			ret = av_set_string3(key, value, 1, null);	
			
			/*
			if (ret == Error.AVERROR_OPTION_NOT_FOUND) {
				// TODO
			} */
			
			if (ret < 0) {
	            Log.av_log("ctx", Log.AV_LOG_ERROR, "Error setting option %s to value %s.\n", 
	            		key, value);
				break;
			}
			ret = 0;
		}		
		return ret;
	}

	protected int av_set_string3(String name, String get_str, int i,
			Object object) {
		// TODO
		AVOption o = AVOption.av_opt_find(this.av_class, name, null, 0, 0);		
		
		if (o == null)
			return 0;
		return 0;
	}

}
