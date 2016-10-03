package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;

public class FmtType {
	
	String name;
	CodecID id;
	AVMediaType type;	
	
	
	public FmtType(String name, CodecID id, AVMediaType type) {
		super();
		this.name = name;
		this.id = id;
		this.type = type;
	}
	
	public String get_name() {
		return name;
	}
	
	public void set_name(String name) {
		this.name = name;
	}
	
	public CodecID get_id() {
		return id;
	}
	
	public void set_id(CodecID id) {
		this.id = id;
	}
	
	public AVMediaType get_type() {
		return type;
	}
	
	public void set_type(AVMediaType type) {
		this.type = type;
	}

	
	
}
