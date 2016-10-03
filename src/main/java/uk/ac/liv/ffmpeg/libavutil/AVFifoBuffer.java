package uk.ac.liv.ffmpeg.libavutil;

import java.util.Arrays;

public class AVFifoBuffer {
	   
	   
	public static AVFifoBuffer av_fifo_alloc(int i) {
		AVFifoBuffer f = new AVFifoBuffer();
		
		f.buffer = new byte[i];
		f.end = i;
		f.av_fifo_reset();
		return f;
	}
	
   byte [] buffer;
   int rptr;
   int wptr;
   int end;
   long rndx, wndx;


	private void av_fifo_reset() {
	   rptr = 0;
	   wptr = 0;
	   rndx = 0;
	   wndx = 0;
	   Arrays.fill(buffer, (byte)0);
	}


	public int av_fifo_size() {
		return (int)(wndx - rndx); 
	}


	public int av_fifo_realloc2(int i) {
		return -1;
	}


	public int av_fifo_generic_write(short[] buftmp, int size_out, Object object) {
		return -1;
	}


	public short [] av_fifo_generic_read(int frame_bytes, Object object) {
		return new short [0];
	}
   
}
