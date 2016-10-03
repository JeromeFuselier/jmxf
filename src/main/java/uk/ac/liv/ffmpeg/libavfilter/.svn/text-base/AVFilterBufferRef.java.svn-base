package uk.ac.liv.ffmpeg.libavfilter;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class AVFilterBufferRef implements Cloneable {
	
	AVFilterBuffer buf;        ///< the buffer that this is a reference to
    short [][] data;	///< picture/audio data for each plane
    int [] linesize = new int[8];            ///< number of bytes per line
    PixelFormat format;                 ///< media format

    /**
     * presentation timestamp. The time unit may change during
     * filtering, as it is specified in the link and the filter code
     * may need to rescale the PTS accordingly.
     */
    long pts;
    long pos;                ///< byte position in stream, -1 if unknown

    int perms;                  ///< permissions, see the AV_PERM_* flags

    AVMediaType type;      ///< media type of buffer data
    AVFilterBufferRefVideoProps video; ///< video buffer specific properties
    AVFilterBufferRefAudioProps audio; ///< audio buffer specific properties
    
    public Object clone() {
    	AVFilterBufferRef buf = null;
    	try {
			buf = (AVFilterBufferRef) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    
	    buf.set_data((short [][]) data.clone());
	    buf.set_linesize((int []) linesize.clone());
	    
	    return buf;
	}
    

	public AVFilterBufferRefAudioProps get_audio() {
		return audio;
	}
	
	public void set_audio(AVFilterBufferRefAudioProps audio) {
		this.audio = audio;
	}

	public AVFilterBuffer get_buf() {
		return buf;
	}

	public void set_buf(AVFilterBuffer buf) {
		this.buf = buf;
	}

	public short [][] get_data() {
		return data;
	}
	
	public void set_data(short[][] data) {
		this.data = new short[data.length][];
		
		for (int i = 0 ; i < data.length ; i++)
			this.data[i] = Arrays.copyOf(data[i], data[i].length);
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

	public PixelFormat get_format() {
		return format;
	}

	public void set_format(PixelFormat format) {
		this.format = format;
	}

	public long get_pts() {
		return pts;
	}

	public void set_pts(long pts) {
		this.pts = pts;
	}

	public long get_pos() {
		return pos;
	}

	public void set_pos(long pos) {
		this.pos = pos;
	}

	public int get_perms() {
		return perms;
	}

	public void set_perms(int perms) {
		this.perms = perms;
	}

	public AVMediaType get_type() {
		return type;
	}

	public void set_type(AVMediaType type) {
		this.type = type;
	}

	public AVFilterBufferRefVideoProps get_video() {
		return video;
	}

	public void set_video(AVFilterBufferRefVideoProps video) {
		this.video = video;
	}

	public void avfilter_unref_buffer() {
		get_buf().free(get_buf());
		set_video(null);
		set_audio(null);		
	}

	public void avfilter_copy_buffer_ref_props(AVFilterBufferRef src) {
	    // copy common properties
	    set_pts(src.get_pts());
	    set_pos(src.get_pos());

	    switch (src.get_type()) {
	    case AVMEDIA_TYPE_VIDEO: 
	    	set_video(src.get_video()); 
	    	break;
	    case AVMEDIA_TYPE_AUDIO: 
	    	set_audio(src.get_audio()); 
	    	break;
	    default: 
	    	break;
	    }
	}

	public short[] get_data(int i) {
		return data[i];
	}


	public void set_data(int i, short[] a) {
		data[i] = a;
	}
	
    
    

}
