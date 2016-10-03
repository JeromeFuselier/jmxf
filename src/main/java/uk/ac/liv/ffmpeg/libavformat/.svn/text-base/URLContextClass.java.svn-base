package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class URLContextClass extends AVClass {

	public URLContextClass() {
		super("URLContext", AVUtil.LIBAVUTIL_VERSION_INT());
	}
	

	// A function which returns the name of a context
    //instance ctx associated with the class.
	public String item_name(Object obj) {
		URLContext h = (URLContext) obj;
		if (h.get_prot() != null)
			return h.get_prot().get_name();
		else
			return "NULL";
    }

}
