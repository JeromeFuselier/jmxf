package uk.ac.liv.ffmpeg.libavfilter;


/**
 * A linked-list of the inputs/outputs of the filter chain.
 *
 * This is mainly useful for avfilter_graph_parse(), since this
 * function may accept a description of a graph with not connected
 * input/output pads. This struct specifies, per each not connected
 * pad contained in the graph, the filter context and the pad index
 * required for establishing a link.
 */
public class AVFilterInOut {
	

	public static AVFilterInOut avfilter_inout_alloc() {
		return new AVFilterInOut();
	}
	
	
    /** unique name for this input/output in the list */
    String name;

    /** filter context associated to this input/output */
    AVFilterContext filter_ctx;

    /** index of the filt_ctx pad to use for linking */
    int pad_idx;

    /** next input/input in the list, null if this is the last */
    AVFilterInOut next;

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public AVFilterContext get_filter_ctx() {
		return filter_ctx;
	}

	public void set_filter_ctx(AVFilterContext filter_ctx) {
		this.filter_ctx = filter_ctx;
	}

	public int get_pad_idx() {
		return pad_idx;
	}

	public void set_pad_idx(int pad_idx) {
		this.pad_idx = pad_idx;
	}

	public AVFilterInOut get_next() {
		return next;
	}

	public void set_next(AVFilterInOut next) {
		this.next = next;
	}


	
    
    

}
