package uk.ac.liv.ffmpeg.libavfilter;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class AVFilterBuffer {

	short [][] data;           ///< buffer data for each plane/channel
    int [] linesize = new int[8];            ///< number of bytes per line

    int refcount;          ///< number of references to this buffer

    /** private data to be used by a custom free function */
    Object priv; 

    PixelFormat format;                 ///< media format
    int w, h;                   ///< width and height of the allocated buffer
        
    public short[][] get_data() {
		return data;
	}

	public void set_data(short[][] data) {
		if (data != null) {
			this.data = new short[data.length][];
			
			for (int i = 0 ; i < data.length ; i++)
				this.data[i] = Arrays.copyOf(data[i], data[i].length);
		} else {
			this.data = null;
		}
	}

	public void set_data(int i, short [] data) {
		this.data[i] = data;
	}

	public int[] get_linesize() {
		return linesize;
	}

	public void set_linesize(int[] linesize) {
		this.linesize = Arrays.copyOf(linesize, linesize.length);
	}

	public int get_refcount() {
		return refcount;
	}

	public void set_refcount(int refcount) {
		this.refcount = refcount;
	}

	public Object get_priv() {
		return priv;
	}

	public void set_priv(Object priv) {
		this.priv = priv;
	}

	public PixelFormat get_format() {
		return format;
	}

	public void set_format(PixelFormat format) {
		this.format = format;
	}

	public int get_w() {
		return w;
	}

	public void set_w(int w) {
		this.w = w;
	}

	public int get_h() {
		return h;
	}

	public void set_h(int h) {
		this.h = h;
	}



	/**
     * A pointer to the function to deallocate this buffer if the default
     * function is not sufficient. This could, for example, add the memory
     * back into a memory pool to be reused later without the overhead of
     * reallocating it from scratch.
     */
    void free(AVFilterBuffer buf) {
    	data = null;    	
    }
}
