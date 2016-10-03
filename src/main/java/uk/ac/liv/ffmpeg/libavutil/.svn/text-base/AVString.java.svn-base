package uk.ac.liv.ffmpeg.libavutil;

import uk.ac.liv.ffmpeg.libavfilter.GraphParser;
import uk.ac.liv.util.UtilsString;

public class AVString {

	public static boolean av_strstart(String str, String pfx) {
		return str.startsWith(pfx);
	}

	public static String av_get_token(String buf, String term) {
		String out = "";
	    	    
	    buf = UtilsString.remove_leading_chars(buf, GraphParser.WHITESPACES);

	    while( (buf != null) && (UtilsString.strspn(buf, term) == 0) ) {
	        char c = buf.charAt(0);
	        buf = buf.substring(1);
	        if ( (c == '\\') && (buf != null) ){
	        	out += buf.charAt(0);
		        buf = buf.substring(1);	        	
	        } else if (c == '\'') {
	            while ( (buf != null) && (buf.charAt(0) != '\'') ) {
	        		out += buf.charAt(0);
			        buf = buf.substring(1);	 
	            }
	            if (buf != null){
			        buf = buf.substring(1);
	            }
	        } else {
	            out += c;
	        }
	    }

	    out = UtilsString.remove_trailing_chars(buf, GraphParser.WHITESPACES);

	    return out;
	}

}
