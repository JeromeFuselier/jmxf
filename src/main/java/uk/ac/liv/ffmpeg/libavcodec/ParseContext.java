package uk.ac.liv.ffmpeg.libavcodec;

public class ParseContext {
	
	
    byte [] buffer;
    int index;
    int last_index;
    long state;             ///< contains the last few bytes in MSB order
    int frame_start_found;
    int overread;               ///< the number of bytes which where irreversibly read from the next frame
    int overread_index;         ///< the index into ParseContext.buffer of the overread bytes
    long state64;           ///< contains the last 8 bytes in MSB order
    
	public byte[] get_buffer() {
		return buffer;
	}
	
	public void set_buffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int get_index() {
		return index;
	}

	public void set_index(int index) {
		this.index = index;
	}

	public int get_last_index() {
		return last_index;
	}

	public void set_last_index(int last_index) {
		this.last_index = last_index;
	}

	public long get_state() {
		return state;
	}

	public void set_state(long state) {
		this.state = state;
	}

	public int get_frame_start_found() {
		return frame_start_found;
	}

	public void set_frame_start_found(int frame_start_found) {
		this.frame_start_found = frame_start_found;
	}

	public int get_overread() {
		return overread;
	}

	public void set_overread(int overread) {
		this.overread = overread;
	}

	public int get_overread_index() {
		return overread_index;
	}

	public void set_overread_index(int overread_index) {
		this.overread_index = overread_index;
	}

	public long get_state64() {
		return state64;
	}

	public void set_state64(long state64) {
		this.state64 = state64;
	}
	
    
    
	

}
