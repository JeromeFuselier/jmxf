package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVClass;

public class URLContext {
	
	AVClass av_class; ///< information for av_log(). Set by url_open().
	URLProtocol prot;
    int flags;
    int is_streamed;  /**< true if streamed (no seek possible), default = false */
    int max_packet_size;  /**< if non zero, the stream is packetized with this max packet size */
    Object priv_data;
    String filename; /**< specified URL */
    int is_connected;
    
	public AVClass get_av_class() {
		return av_class;
	}
	
	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}
	
	public URLProtocol get_prot() {
		return prot;
	}
	
	public void set_prot(URLProtocol prot) {
		this.prot = prot;
	}
	
	public int get_flags() {
		return flags;
	}
	
	public boolean has_flag(int flag) {
		return (flags & flag) != 0;
	}
	
	public void set_flags(int flags) {
		this.flags = flags;
	}
	
	public int get_is_streamed() {
		return is_streamed;
	}
	
	public void set_is_streamed(int is_streamed) {
		this.is_streamed = is_streamed;
	}
	
	public int get_max_packet_size() {
		return max_packet_size;
	}
	
	public void set_max_packet_size(int max_packet_size) {
		this.max_packet_size = max_packet_size;
	}
	
	public Object get_priv_data() {
		return priv_data;
	}
	
	public void set_priv_data(Object priv_data) {
		this.priv_data = priv_data;
	}
	
	public String get_filename() {
		return filename;
	}
	
	public void set_filename(String filename) {
		this.filename = filename;
	}
	
	public int get_is_connected() {
		return is_connected;
	}
	
	public void set_is_connected(int is_connected) {
		this.is_connected = is_connected;
	}

	public int ffurl_connect() {
		int err = prot.url_open(filename, flags);
		if (err != 0)
			return err;
		is_connected = 1;
	    //We must be careful here as ffurl_seek() could be slow, for example for http
		if ( has_flag(AVIO.AVIO_FLAG_WRITE) || (prot.get_name().equals("file")))
			if ( (is_streamed == 0) && (ffurl_seek(0, AVIO.SEEK_SET) < 0) )
				is_streamed = 1;
		return 0;
	}

	private long ffurl_seek(long pos, int whence) {
		long ret = prot.url_seek(pos, whence & ~AVIO.AVSEEK_FORCE);
		return ret;
	}

	public int ffurl_close() {
		int ret = 0;
		if (is_connected != 0)
			ret = prot.url_close();
		return ret;		
	}
    
    
    
    

}
