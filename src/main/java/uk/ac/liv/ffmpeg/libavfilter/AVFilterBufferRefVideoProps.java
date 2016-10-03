package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;

public class AVFilterBufferRefVideoProps implements Cloneable {
	
    int w;                      ///< image width
    int h;                      ///< image height
    AVRational sample_aspect_ratio = new AVRational(0, 0); ///< sample aspect ratio
    int interlaced;             ///< is frame interlaced
    int top_field_first;        ///< field order
    AVPictureType pict_type = AVPictureType.AV_PICTURE_TYPE_NONE; ///< picture type of the frame
    int key_frame;              ///< 1 -> keyframe, 0-> not
    
    public Object clone() {
    	AVFilterBufferRefVideoProps buf = null;
    	try {
			buf = (AVFilterBufferRefVideoProps) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    
	    return buf;
	}
    
	public int get_h() {
		return h;
	}
	
	public void set_h(int h) {
		this.h = h;
	}

	public int get_w() {
		return w;
	}

	public void set_w(int w) {
		this.w = w;
	}

	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}

	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}

	public int get_interlaced() {
		return interlaced;
	}

	public void set_interlaced(int interlaced) {
		this.interlaced = interlaced;
	}

	public int get_top_field_first() {
		return top_field_first;
	}

	public void set_top_field_first(int top_field_first) {
		this.top_field_first = top_field_first;
	}

	public AVPictureType get_pict_type() {
		return pict_type;
	}

	public void set_pict_type(AVPictureType pict_type) {
		this.pict_type = pict_type;
	}

	public int get_key_frame() {
		return key_frame;
	}

	public void set_key_frame(int key_frame) {
		this.key_frame = key_frame;
	}
    
    

}
