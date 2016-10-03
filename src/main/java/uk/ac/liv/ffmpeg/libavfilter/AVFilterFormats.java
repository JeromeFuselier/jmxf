package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;

public class AVFilterFormats {
	


	ArrayList<Long> formats = new ArrayList<Long>();           ///< list of media formats

    ArrayList<AVFilterFormats> refs = new ArrayList<AVFilterFormats>(); ///< references to this list

    public void set_formats(ArrayList<Long> formats) {
		this.formats = formats;
	}

    public int avfilter_add_format(PixelFormat fmt) {
    	formats.add((long)fmt.ordinal());
    	return 0;
    }
    
    public int avfilter_add_format(AVSampleFormat fmt) {
    	formats.add((long)fmt.ordinal());
    	return 0;
    }
    
    public int avfilter_add_format(Long fmt) {
    	formats.add((long)fmt);
    	return 0;
    }
    
    public long get_format(int i) {
    	return formats.get(i);
    }
    
    public int get_format_count() {
    	return formats.size();
    }
    
    public AVFilterFormats get_ref(int i) {
    	return refs.get(i);
    }
    
    public ArrayList<AVFilterFormats> get_refs() {
    	return refs;
    }

    public int get_refcount() {
    	return refs.size();
    }

    public ArrayList<Long> get_formats() {
    	return formats;
    }
    
    public void add_ref(AVFilterFormats i) {
    	refs.add(i);
    }


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AVFilterFormats other = (AVFilterFormats) obj;
		if (formats == null) {
			if (other.formats != null)
				return false;
		} else if (!formats.equals(other.formats))
			return false;
		if (refs == null) {
			if (other.refs != null)
				return false;
		} else if (!refs.equals(other.refs))
			return false;
		return true;
	}

	public void avfilter_formats_unref() {
		// TODO Jerome
		refs = new ArrayList<AVFilterFormats>();
	}


	public void avfilter_formats_ref(AVFilterFormats refs) {
		// TODO Jerome
		//refs.add_ref(this);		
	}
    
    
}
