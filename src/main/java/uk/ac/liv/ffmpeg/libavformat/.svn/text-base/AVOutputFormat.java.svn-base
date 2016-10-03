/******************************************************************************
 *  
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author     : Jerome Fuselier
 * Creation   : June 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavformat;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.util.OutOI;

public class AVOutputFormat {

	
	// A comma separated list of short names for the format.
	protected String name;
    /**
     * Descriptive name for the format, meant to be more human-readable
     * than name. You should use the NULL_IF_CONFIG_SMALL() macro
     * to define it.
     */
	protected String long_name;
	protected String mime_type;
	protected ArrayList<String> extensions = new ArrayList<String>();
	
    /* output support */
	protected CodecID audio_codec = CodecID.CODEC_ID_NONE; /**< default audio codec */
	protected CodecID video_codec = CodecID.CODEC_ID_NONE; /**< default video codec */
    

	protected int flags;

	protected Object dummy;                 

     /**
      * List of supported codec_id-codec_tag pairs, ordered by "better
      * choice first". The arrays are all terminated by CODEC_ID_NONE.
      */
	protected AVCodecTag [] codec_tag;

	protected CodecID subtitle_codec; /**< default subtitle codec */    
	
	protected AVClass priv_class; ///< AVClass for the private context

	public AVOutputFormat() {
		super();
	}
	

	public AVOutputFormat(String name) {
		super();
		this.name = name;
	}
	
	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public String get_long_name() {
		return long_name;
	}

	public void set_long_name(String long_name) {
		this.long_name = long_name;
	}

	public String get_mime_type() {
		return mime_type;
	}

	public void set_mime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	public ArrayList<String> get_extensions() {
		return extensions;
	}

	public void add_extension(String ext) {
		this.extensions.add(ext);
	}

	public CodecID get_audio_codec() {
		return audio_codec;
	}

	public void set_audio_codec(CodecID audio_codec) {
		this.audio_codec = audio_codec;
	}

	public CodecID get_video_codec() {
		return video_codec;
	}

	public void set_video_codec(CodecID video_codec) {
		this.video_codec = video_codec;
	}

	public int get_flags() {
		return flags;
	}
	
	public boolean has_flag(int flag) {
		return (flags & flag) != 0;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public Object get_dummy() {
		return dummy;
	}

	public void set_dummy(Object dummy) {
		this.dummy = dummy;
	}

	public AVCodecTag[] get_codec_tag() {
		return codec_tag;
	}

	public AVCodecTag get_codec_tag(int i) {
		return codec_tag[i];
	}

	public int get_nb_codec_tag() {
		return codec_tag.length;
	}

	public void set_codec_tag(AVCodecTag[] codec_tag) {
		this.codec_tag = codec_tag;
	}

	public CodecID get_subtitle_codec() {
		return subtitle_codec;
	}

	public void set_subtitle_codec(CodecID subtitle_codec) {
		this.subtitle_codec = subtitle_codec;
	}

	public AVClass get_priv_class() {
		return priv_class;
	}

	public void set_priv_class(AVClass priv_class) {
		this.priv_class = priv_class;
	}

	public int write_header(AVFormatContext s) {
		return -1;
	}
	
	protected int write_packet(AVFormatContext s, AVPacket pkt) {
		return -1;
	}
	
    int write_trailer(AVFormatContext s) {
		return -1;
	}

    OutOI interleave_packet(AVFormatContext s, AVPacket in, int flush){
		return new OutOI(null, -1);
	}

}
