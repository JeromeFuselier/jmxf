package uk.ac.liv.ffmpeg.libavcodec;

import java.util.Arrays;

import uk.ac.liv.util.OutOI;

public class AVBitStreamFilterContext {
	Object priv_data;
	AVBitStreamFilter filter;
	AVCodecParserContext parser;
	AVBitStreamFilterContext next;
	
	public Object get_priv_data() {
		return priv_data;
	}
	
	public void set_priv_data(Object priv_data) {
		this.priv_data = priv_data;
	}
	
	public AVBitStreamFilter get_filter() {
		return filter;
	}
	
	public void set_filter(AVBitStreamFilter filter) {
		this.filter = filter;
	}
	
	public AVCodecParserContext get_parser() {
		return parser;
	}
	
	public void set_parser(AVCodecParserContext parser) {
		this.parser = parser;
	}
	
	public AVBitStreamFilterContext get_next() {
		return next;
	}
	
	public void set_next(AVBitStreamFilterContext next) {
		this.next = next;
	}

	public OutOI av_bitstream_filter_filter(AVCodecContext avctx,
			String args, short[] s, int keyframe) {
		short [] poutbuf = Arrays.copyOf(s, s.length);
		int ret = get_filter().filter(this, avctx, args, poutbuf, s, keyframe);
		return new OutOI(poutbuf, ret);
	}	
	
}
