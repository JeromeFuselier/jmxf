package uk.ac.liv.ffmpeg.libavfilter;

import uk.ac.liv.ffmpeg.libswscale.SwsContext;

public class ScaleContext {
	
    SwsContext sws;     ///< software scaler context
    SwsContext [] isws = new SwsContext[2]; ///< software scaler context for interlaced material

    /**
     * New dimensions. Special values are:
     *   0 = original width/height
     *  -1 = keep original aspect
     */
    int w, h;
    int flags;         ///sws flags

    int hsub, vsub;             ///< chroma subsampling
    int slice_y;                ///< top of current output slice
    int input_is_pal;           ///< set to 1 if the input format is paletted
    int interlaced;

    String w_expr;           ///< width  expression string
    String h_expr;           ///< height expression string
    
	public SwsContext get_sws() {
		return sws;
	}
	
	public void set_sws(SwsContext sws) {
		this.sws = sws;
	}
	
	public SwsContext[] get_isws() {
		return isws;
	}
	
	public SwsContext get_isws(int i) {
		return isws[i];
	}
	
	public void set_isws(SwsContext[] isws) {
		this.isws = isws;
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
	
	public int get_flags() {
		return flags;
	}
	
	public void set_flags(int flags) {
		this.flags = flags;
	}
	
	public int get_hsub() {
		return hsub;
	}
	
	public void set_hsub(int hsub) {
		this.hsub = hsub;
	}
	
	public int get_vsub() {
		return vsub;
	}
	
	public void set_vsub(int vsub) {
		this.vsub = vsub;
	}
	
	public int get_slice_y() {
		return slice_y;
	}
	
	public void set_slice_y(int slice_y) {
		this.slice_y = slice_y;
	}
	
	public int get_input_is_pal() {
		return input_is_pal;
	}
	
	public void set_input_is_pal(int input_is_pal) {
		this.input_is_pal = input_is_pal;
	}
	
	public int get_interlaced() {
		return interlaced;
	}
	
	public void set_interlaced(int interlaced) {
		this.interlaced = interlaced;
	}
	
	public String get_w_expr() {
		return w_expr;
	}
	
	public void set_w_expr(String w_expr) {
		this.w_expr = w_expr;
	}
	
	public String get_h_expr() {
		return h_expr;
	}
	
	public void set_h_expr(String h_expr) {
		this.h_expr = h_expr;
	}

	public void set_isws(int i, SwsContext sws) {
		this.isws[i] = sws;	
	}
    
    
    
    

}
