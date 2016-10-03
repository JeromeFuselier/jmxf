package uk.ac.liv.ffmpeg.libavcodec;

public class PutBits {

	public static void init_put_bits(PutBitContext s, short[] buffer) {
		int buffer_size = buffer.length;
	    if (buffer_size < 0) {
	        buffer_size = 0;
	        buffer = null;
	    }

	    s.set_size_in_bits(8 * buffer_size);
	    s.set_buf(buffer);
	    s.set_buf_end(buffer_size);

	    s.set_buf_ptr(0);
	    s.set_bit_left(32);
	    s.set_bit_buf(0);
	}

	public static int put_bits_count(PutBitContext s) {
		return (s.get_buf_ptr() - 0/*s->buf*/) * 8 + 32 - s.get_bit_left();
	}

	public static void put_bits(PutBitContext s, int n, int value) {
		int bit_buf;
	    int bit_left;

	    bit_buf = s.get_bit_buf();
	    bit_left = s.get_bit_left();
	    
	    if (n < bit_left) {
	        bit_buf = (bit_buf<<n) | value;
	        bit_left -= n;
	    } else {
	        bit_buf <<= bit_left;
	        bit_buf |= value >> (n - bit_left);
		
			//TODO Jerome *(uint32_t *)s->buf_ptr = av_be2ne32(bit_buf);		 
		    s.set_buf(s.get_buf_ptr()    , (byte) (bit_buf >> 24));
	        s.set_buf(s.get_buf_ptr() + 1, (byte) (bit_buf >> 16));
	        s.set_buf(s.get_buf_ptr() + 2, (byte) (bit_buf >> 8));
	        s.set_buf(s.get_buf_ptr() + 3, (byte) bit_buf);
		    
		    s.set_buf_ptr(s.get_buf_ptr() + 4);
		    bit_left += 32 - n;
		    bit_buf = value;
	    }
	    
	    s.set_bit_buf(bit_buf);
	    s.set_bit_left(bit_left);
	}

	public static void flush_put_bits(PutBitContext s) {
		s.set_bit_buf(s.get_bit_buf() << s.get_bit_left());
		while (s.get_bit_left() < 32) {
		    s.set_buf_ptr(s.get_buf_ptr() + 1);
			s.set_buf(s.get_buf_ptr(), (byte) (s.get_bit_buf() >> 24));
	        s.set_bit_buf(s.get_bit_buf() << 8);
		    s.set_bit_left(s.get_bit_left() + 8);
    	}
	    s.set_bit_left(32);
		s.set_bit_buf(0);
	}

	public static void skip_put_bytes(PutBitContext s, int n) {
        s.set_buf_ptr(s.get_buf_ptr() + n);
	}

}
