package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVClass;

public class URLProtocol {

    String name;
    int priv_data_size;
    AVClass priv_data_class;
    int flags;   
    
    
    public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public int get_priv_data_size() {
		return priv_data_size;
	}

	public void set_priv_data_size(int priv_data_size) {
		this.priv_data_size = priv_data_size;
	}

	public AVClass get_priv_data_class() {
		return priv_data_class;
	}

	public void set_priv_data_class(AVClass priv_data_class) {
		this.priv_data_class = priv_data_class;
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

	int url_open(String url, int flags) {
    	return -1;
    }
    
    String url_read() {
    	return "";
    }
    
    int url_write(String buf) {
    	return -1;
    }
    
    long url_seek(long pos, int whence) {
    	return -1;
    }
    
    int url_close() {
    	return -1;
    }

    int url_read_pause(int pause) {
    	return -1;
    }
    
    long url_read_seek(int stream_index, long timestamp, int flags) {
    	return -1;
    }
    
    int url_get_file_handle() {
    	return -1;
    }
    
    int url_check(int mask) {
    	return -1;
    }
    
    
    
    
}



