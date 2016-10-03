package uk.ac.liv.ffmpeg.libavformat.img2;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;

public class IdStrMap {
	
	CodecID id;
	String str;
		
	public IdStrMap(CodecID id, String str) {
		super();
		this.id = id;
		this.str = str;
	}

	public CodecID get_id() {
		return id;
	}
	
	public void set_id(CodecID id) {
		this.id = id;
	}
	
	public String get_str() {
		return str;
	}
	
	public void set_str(String str) {
		this.str = str;
	}
	
	

}
