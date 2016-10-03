package uk.ac.liv.ffmpeg.libavutil;

import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;

public class Log {
	
	public static final int AV_LOG_QUIET = -8;

	/**
	 * Something went really wrong and we will crash now.
	 */
	public static final int AV_LOG_PANIC = 0;

	/**
	 * Something went wrong and recovery is not possible.
	 * For example, no header was found for a format which depends
	 * on headers or an illegal combination of parameters is used.
	 */
	public static final int AV_LOG_FATAL = 8;

	/**
	 * Something went wrong and cannot losslessly be recovered.
	 * However, not all future data is affected.
	 */
	public static final int AV_LOG_ERROR = 16;

	/**
	 * Something somehow does not look correct. This may or may not
	 * lead to problems. An example would be the use of '-vstrict -2'.
	 */
	public static final int AV_LOG_WARNING = 24;

	public static final int AV_LOG_INFO    = 32;
	public static final int AV_LOG_VERBOSE = 40;

	/**
	 * Stuff which is only useful for libav* developers.
	 */
	public static final int AV_LOG_DEBUG   = 48;
	

	public static int av_log_level = AV_LOG_INFO;
	public static int flags;
	
	

	public static void av_log(String obj, int level, String fmt, Object ... args) {
		av_vlog(obj, level, fmt, args);
	}
	
	public static void av_vlog(String obj, int level, String fmt, Object ... args) {
		av_log_callback(obj, level, fmt, args);
	}
	
	public static void av_log_callback(String obj, int level, String fmt, Object ... args) {
	    if (level > av_log_level)
	        return;
	    
		if ( (obj != null) && !obj.equals("") ) {
			System.out.print(String.format("[%s] ", obj));
		}
		
		System.out.print(String.format(fmt, args));
		
	}
	
	public static void av_dlog(String obj, String fmt, Object ... args) {
		av_vlog(obj, AV_LOG_DEBUG, fmt, args);
	}

	public static int av_log_get_level() {
	    return av_log_level;
	}
	
	
}
