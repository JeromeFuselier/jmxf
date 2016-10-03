package uk.ac.liv.ffmpeg.libavformat;

import java.util.ArrayList;

import uk.ac.liv.util.UtilsString;

import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Error;

public class AVIO {
	
	public static String URL_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyz" +
    										  "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
    										  "0123456789+-.";

	public static int URL_PROTOCOL_FLAG_NESTED_SCHEME = 1;
	
	public static int AVIO_FLAG_READ  = 1;        /**< read-only */
	public static int AVIO_FLAG_WRITE = 2;        /**< write-only */
	public static int AVIO_FLAG_READ_WRITE = (AVIO_FLAG_READ|AVIO_FLAG_WRITE);  /**< read-write pseudo flag */
	
	public static int SEEK_SET = 0;	/* Seek from beginning of file.  */
	public static int SEEK_CUR = 1;	/* Seek from current position.  */
	public static int SEEK_END = 2;	/* Seek from end of file.  */

	/**
	 * Passing this as the "whence" parameter to a seek function causes it to
	 * return the filesize without seeking anywhere. Supporting this is optional.
	 * If it is not supported then the seek function will return <0.
	 */
	public static int AVSEEK_SIZE = 0x10000;

	/**
	 * Oring this flag as into the "whence" parameter to a seek function causes it to
	 * seek by any means (like reopening and linear reading) or other normally unreasonble
	 * means that can be extremely slow.
	 * This may be ignored by the seek code.
	 */
	public static int AVSEEK_FORCE = 0x20000;
	
	public static ArrayList<URLProtocol> protocols = new ArrayList<URLProtocol>();

	public static int avio_check(String url, int flags) {
		URLContext h = ffurl_alloc(url, flags);
		int ret;
		
		if (h == null)
			return Error.AVERROR(Error.ENOENT);
		
		ret = h.get_prot().url_check(flags);
		if (ret < 0) {
			ret = h.ffurl_connect();
			if (ret >= 0)
				ret = flags;
		}
		
		h.ffurl_close();
		return ret;
	}

	private static URLContext ffurl_alloc(String filename, int flags) {
		
		int proto_len = UtilsString.strspn(filename, URL_SCHEME_CHARS);
		String proto_str = "";
		String proto_nested = "";
		
		if ( (filename.charAt(proto_len) != ':') || (OSSupport.is_dos_path(filename)) )
			proto_str = "file";
		else
			proto_str = filename;
		proto_nested = proto_str;
		
		
		for (URLProtocol up: protocols) {
			if (proto_str.equals(up.get_name()))
				return url_alloc_for_protocol(up, filename, flags);
			
			if (up.has_flag(URL_PROTOCOL_FLAG_NESTED_SCHEME) &&
				proto_nested.equals(up.get_name()))
				return url_alloc_for_protocol(up, filename, flags);
		}
		
		return null;
	}

	private static URLContext url_alloc_for_protocol(URLProtocol up,
			String filename, int flags) {
		URLContext uc = new URLContext();
		
		uc.set_av_class(new URLContextClass());
		uc.set_filename(filename);
		uc.set_prot(up);
		uc.set_flags(flags);
		uc.set_is_streamed(0); /* default = not streamed */
		uc.set_max_packet_size(0); /* default: stream file */
		
		/*
		if (up->priv_data_size) {
        uc->priv_data = av_mallocz(up->priv_data_size);
        if (up->priv_data_class) {
            *(const AVClass**)uc->priv_data = up->priv_data_class;
            av_opt_set_defaults(uc->priv_data);
        }
  	 	}
  	 	*/
		
		return uc;
	}

	public static URLContext ffurl_open(String filename, int flags) {
	    URLContext puc = ffurl_alloc(filename, flags);
	    if (puc == null)
	        return null;
	    int ret = puc.ffurl_connect();
	    if (ret == 0)
	        return puc;
	    puc.ffurl_close();
	    return null;
	}

}
