package uk.ac.liv.ffmpeg.libavcodec;

import java.util.ArrayList;

public class AVSubtitle {
    int format; /* 0 = graphics */
    int start_display_time; /* relative to packet pts, in ms */
    int end_display_time; /* relative to packet pts, in ms */
    ArrayList<AVSubtitleRect> rects = new ArrayList<AVSubtitleRect>();
    long pts;    ///< Same as packet pts, in AV_TIME_BASE
    
	public int get_end_display_time() {
		return end_display_time;
	}
	
	public void set_end_display_time(int end_display_time) {
		this.end_display_time = end_display_time;
	}

	public int get_format() {
		return format;
	}

	public void set_format(int format) {
		this.format = format;
	}

	public int get_start_display_time() {
		return start_display_time;
	}

	public void set_start_display_time(int start_display_time) {
		this.start_display_time = start_display_time;
	}

	public ArrayList<AVSubtitleRect> get_rects() {
		return rects;
	}

	public void set_rects(ArrayList<AVSubtitleRect> rects) {
		this.rects = rects;
	}

	public long get_pts() {
		return pts;
	}

	public void set_pts(long pts) {
		this.pts = pts;
	}
	
	

}
