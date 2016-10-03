package uk.ac.liv.ffmpeg.libavcodec;

import java.util.Arrays;

public class AVPicture {
	
	protected short  [][] data = {null, null, null, null};;
	protected int [] linesize = new int[4];       ///< number of bytes per line
    
    public short [][] get_data() {
		return data;
	}

	public void set_data(short [][] data) {
		this.data = new short  [data.length][];
		for (int i = 0 ; i < data.length ; i++)
			this.data[i] = Arrays.copyOf(data[i], data[i].length);
	}

	public void set_data(int i, short  [] data) {
		if (data != null)
			this.data[i] = Arrays.copyOf(data, data.length);
		else
			this.data[i] = null;
	}

	public short[] get_data(int i) {
		return data[i];
	}
	
	public int[] get_linesize() {
		return linesize;
	}
	
	public int get_linesize(int i) {
		return linesize[i];
	}

	public void set_linesize(int[] linesize) {
		this.linesize = Arrays.copyOf(linesize, linesize.length);
	}

	public void set_linesize(int i, int nb) {
		this.linesize[i] = nb;
	}
    
      
    

}
