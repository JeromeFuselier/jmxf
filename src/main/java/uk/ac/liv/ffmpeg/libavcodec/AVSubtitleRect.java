package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVSubtitleType;

public class AVSubtitleRect {
    int x;         ///< top left corner  of pict, undefined when pict is not set
    int y;         ///< top left corner  of pict, undefined when pict is not set
    int w;         ///< width            of pict, undefined when pict is not set
    int h;         ///< height           of pict, undefined when pict is not set
    int nb_colors; ///< number of colors in pict, undefined when pict is not set

    /**
     * data+linesize for the bitmap of this subtitle.
     * can be set for text/ass as well once they where rendered
     */
    AVPicture pict;
    AVSubtitleType type;

    String text;                     ///< 0 terminated plain UTF-8 text

    /**
     * 0 terminated ASS/SSA compatible event line.
     * The pressentation of this is unaffected by the other values in this
     * struct.
     */
    String ass;

	public String get_ass() {
		return ass;
	}

	public void set_ass(String ass) {
		this.ass = ass;
	}

	public int get_x() {
		return x;
	}

	public void set_x(int x) {
		this.x = x;
	}

	public int get_y() {
		return y;
	}

	public void set_y(int y) {
		this.y = y;
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

	public int get_nb_colors() {
		return nb_colors;
	}

	public void set_nb_colors(int nb_colors) {
		this.nb_colors = nb_colors;
	}

	public AVPicture get_pict() {
		return pict;
	}

	public void set_pict(AVPicture pict) {
		this.pict = pict;
	}

	public AVSubtitleType get_type() {
		return type;
	}

	public void set_type(AVSubtitleType type) {
		this.type = type;
	}

	public String get_text() {
		return text;
	}

	public void set_text(String text) {
		this.text = text;
	}
    
    

}
