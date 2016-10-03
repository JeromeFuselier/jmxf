package uk.ac.liv.ffmpeg.libavcodec;

public class PutBitContext {
	
	int bit_buf;
    int bit_left;
    short [] buf;
    int size_in_bits;
    
    int buf_ptr, buf_end;

	public int get_bit_buf() {
		return bit_buf;
	}

	public void set_bit_buf(int bit_buf) {
		this.bit_buf = bit_buf;
	}

	public int get_bit_left() {
		return bit_left;
	}

	public void set_bit_left(int bit_left) {
		this.bit_left = bit_left;
	}

	public short[] get_buf() {
		return buf;
	}

	public void set_buf(short[] buf) {
		this.buf = buf;
	}

	public int get_size_in_bits() {
		return size_in_bits;
	}

	public void set_size_in_bits(int size_in_bits) {
		this.size_in_bits = size_in_bits;
	}

	public int get_buf_ptr() {
		return buf_ptr;
	}

	public void set_buf_ptr(int buf_ptr) {
		this.buf_ptr = buf_ptr;
	}

	public int get_buf_end() {
		return buf_end;
	}

	public void set_buf_end(int buf_end) {
		this.buf_end = buf_end;
	}

	public void set_buf(int i, byte val) {
		this.buf[i] = val;
	}
    
    
    
    
	

}
