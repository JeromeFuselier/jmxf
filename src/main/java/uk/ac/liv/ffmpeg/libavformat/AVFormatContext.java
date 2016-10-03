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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVPacketList;
import uk.ac.liv.ffmpeg.AVStream;
import uk.ac.liv.ffmpeg.Ffmpeg;
import uk.ac.liv.ffmpeg.libavcodec.AVBitStreamFilterContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecParserContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.raw.Raw;
import uk.ac.liv.ffmpeg.libavcodec.raw.RawDec;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.AVStreamParseType;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.AudioConvert;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Mathematics.AVRounding;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutOI;

public class AVFormatContext extends AVContext  {
	

	public static AVFormatContext avformat_alloc_context() {	
		AVFormatContext ic = new AVFormatContext();
	    avformat_get_context_defaults(ic);
		return ic;
	}	
	
	
    private static void avformat_get_context_defaults(AVFormatContext ic) {
		ic.avformat_get_context_defaults();		
	}





	/* Can only be iformat or oformat, not both at the same time. */	
	AVInputFormat iformat = null;
    AVOutputFormat oformat = null;    

    AVIOContext pb = null;
    ArrayList<AVStream> streams;
    URI filename; /**< input or output filename */
    String filename_int;
    /* stream info */
    long timestamp;

    int ctx_flags; /**< Format-specific flags, see AVFMTCTX_xx */
    
    /* private data for pts handling (do not modify directly). */
    /**
     * This buffer is only needed when packets were already buffered but
     * not decoded, for example to get the codec parameters in MPEG
     * streams.
     */
    AVPacketList packet_buffer = null;
    
    /**
     * Decoding: position of the first frame of the component, in
     * AV_TIME_BASE fractional seconds. NEVER set this value directly:
     * It is deduced from the AVStream values.
     */
    long start_time;

    /**
     * Decoding: duration of the stream, in AV_TIME_BASE fractional
     * seconds. Only set this value if you know none of the individual stream
     * durations and also dont set any of them. This is deduced from the
     * AVStream values if not set.
     */
    long duration;
    
    /**
     * decoding: total file size, 0 if unknown
     */
    long file_size;

    /**
     * Decoding: total stream bitrate in bit/s, 0 if not
     * available. Never set it directly if the file_size and the
     * duration are known as FFmpeg can compute it automatically.
     */
    long bit_rate;
    
    /* av_read_frame() support */
    AVStream cur_st = null;
    
    /* av_seek_frame() support */
    long data_offset; /**< offset of the first packet */

    int mux_rate;
    int packet_size;
    int preload;
    int max_delay;
    
    /**
     * number of times to loop output in formats that support it
     */
    int loop_output; 
    
    int flags = 0;
    
    int loop_input;

    /**
     * decoding: size of data to probe; encoding: unused.
     */
    int probesize;
    
    
    /**
     * Maximum time (in AV_TIME_BASE units) during which the input should
     * be analyzed in av_find_stream_info().
     */
    int max_analyze_duration;
    
    byte [] key;

    AVProgram [] programs;

    /**
     * Forced video codec_id.
     * Demuxing: Set by user.
     */    
    CodecID video_codec_id = CodecID.CODEC_ID_NONE;
    
    /**
     * Forced audio codec_id.
     * Demuxing: Set by user.
     */
    CodecID audio_codec_id = CodecID.CODEC_ID_NONE;

    /**
     * Forced subtitle codec_id.
     * Demuxing: Set by user.
     */
    CodecID subtitle_codec_id = CodecID.CODEC_ID_NONE;
    
    /**
     * Maximum amount of memory in bytes to use for the index of each stream.
     * If the index exceeds this size, entries will be discarded as
     * needed to maintain a smaller size. This can lead to slower or less
     * accurate seeking (depends on demuxer).
     * Demuxers for which a full in-memory index is mandatory will ignore
     * this.
     * muxing  : unused
     * demuxing: set by user
     */
    int max_index_size;    

    /**
     * Maximum amount of memory in bytes to use for buffering frames
     * obtained from realtime capture devices.
     */
    int max_picture_buffer;

    ArrayList<AVChapter> chapters = new ArrayList<AVChapter>();
    
    /**
     * Flags to enable debugging.
     */
    int debug;
    
    /**
     * Raw packets from the demuxer, prior to parsing and decoding.
     * This buffer is used for buffering packets until the codec can
     * be identified, as parsing cannot be done without knowing the
     * codec.
     */
    AVPacketList raw_packet_buffer = null;
    AVPacketList raw_packet_buffer_end = null;
    
    AVPacketList packet_buffer_end = null;
    
    AVDictionary metadata = new AVDictionary();
    
    /**
     * Remaining size available for raw_packet_buffer, in bytes.
     * NOT PART OF PUBLIC API
     */
    int raw_packet_buffer_remaining_size;
    
    /**
     * Start time of the stream in real world time, in microseconds
     * since the unix epoch (00:00 1st January 1970). That is, pts=0
     * in the stream was captured at this real world time.
     * - encoding: Set by user.
     * - decoding: Unused.
     */
    long start_time_realtime;

    /**
     * decoding: number of frames used to probe fps
     */
    int fps_probe_size;

    /**
     * Transport stream id.
     * This will be moved into demuxer private options. Thus no API/ABI compatibility
     */
    int ts_id;
    
    
    int width;
    int height;
	long pos_first_frame;

    
    

	public AVFormatContext() {
		super();		
		this.streams = new ArrayList<AVStream>();
		probesize = 5000000;
	}





	public int get_keylen() {
		if (key == null)
			return 0;
		else
			return key.length;
	}

	public int get_nb_programs() {
		if (programs == null)
			return 0;
		else
			return programs.length;
	}

	public int get_nb_chapters() {
		return chapters.size();
	}
	
	public int get_nb_streams() {
		return streams.size();
	}	
		
	public long get_pos_first_frame() {
		return pos_first_frame;
	}

	public void set_pos_first_frame(long pos_first_frame) {
		this.pos_first_frame = pos_first_frame;
	}

	public int get_width() {
		return width;
	}

	public void set_width(int width) {
		this.width = width;
	}
	
	public int get_height() {
		return height;
	}
	
	public void set_height(int height) {
		this.height = height;
	}

	public AVInputFormat get_iformat() {
		return iformat;
	}

	public void set_iformat(AVInputFormat iformat) {
		this.iformat = iformat;
	}

	public AVOutputFormat get_oformat() {
		return oformat;
	}

	public void set_oformat(AVOutputFormat oformat) {
		this.oformat = oformat;
	}

	public AVIOContext get_pb() {
		return pb;
	}

	public void set_pb(AVIOContext pb) {
		this.pb = pb;
	}

	public ArrayList<AVStream> get_streams() {
		return streams;
	}

	public void set_streams(ArrayList<AVStream> streams) {
		this.streams = streams;
	}

	public URI get_uri() {
		return filename;
	}
	
	public String get_filename() {
		if (filename != null)
			return filename.getPath();
		else
			return filename_int;
	}
	
	public String get_filename_int() {
		return filename_int;
	}

	public void set_uri(URI uri) {
		this.filename = uri;
	}

	public long get_timestamp() {
		return timestamp;
	}

	public void set_timestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int get_ctx_flags() {
		return ctx_flags;
	}

	public void set_ctx_flags(int ctx_flags) {
		this.ctx_flags = ctx_flags;
	}

	public AVPacketList get_packet_buffer() {
		return packet_buffer;
	}

	public void set_packet_buffer(AVPacketList packet_buffer) {
		this.packet_buffer = packet_buffer;
	}

	public long get_start_time() {
		return start_time;
	}

	public void set_start_time(long start_time) {
		this.start_time = start_time;
	}

	public long get_duration() {
		return duration;
	}

	public void set_duration(long duration) {
		this.duration = duration;
	}

	public long get_file_size() {
		return file_size;
	}

	public void set_file_size(long file_size) {
		this.file_size = file_size;
	}

	public long get_bit_rate() {
		return bit_rate;
	}

	public void set_bit_rate(long bit_rate) {
		this.bit_rate = bit_rate;
	}

	public AVStream get_cur_st() {
		return cur_st;
	}

	public void set_cur_st(AVStream cur_st) {
		this.cur_st = cur_st;
	}

	public long get_data_offset() {
		return data_offset;
	}

	public void set_data_offset(long data_offset) {
		this.data_offset = data_offset;
	}

	public int get_mux_rate() {
		return mux_rate;
	}

	public void set_mux_rate(int mux_rate) {
		this.mux_rate = mux_rate;
	}

	public int get_packet_size() {
		return packet_size;
	}

	public void set_packet_size(int packet_size) {
		this.packet_size = packet_size;
	}

	public int get_preload() {
		return preload;
	}

	public void set_preload(int preload) {
		this.preload = preload;
	}

	public int get_max_delay() {
		return max_delay;
	}

	public void set_max_delay(int max_delay) {
		this.max_delay = max_delay;
	}

	public int get_loop_output() {
		return loop_output;
	}

	public void set_loop_output(int loop_output) {
		this.loop_output = loop_output;
	}

	public int get_flags() {
		return flags;
	}

	public boolean has_flag(int flag) {
		return (this.flags & flag) != 0;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public int get_loop_input() {
		return loop_input;
	}

	public void set_loop_input(int loop_input) {
		this.loop_input = loop_input;
	}

	public int get_probesize() {
		return probesize;
	}

	public void set_probesize(int probesize) {
		this.probesize = probesize;
	}

	public int get_max_analyze_duration() {
		return max_analyze_duration;
	}

	public void set_max_analyze_duration(int max_analyze_duration) {
		this.max_analyze_duration = max_analyze_duration;
	}

	public byte[] get_key() {
		return key;
	}

	public void set_key(byte[] key) {
		this.key = key;
	}

	public AVProgram[] get_programs() {
		return programs;
	}

	public AVProgram get_program(int i) {
		return programs[i];
	}

	public void set_programs(AVProgram[] programs) {
		this.programs = programs;
	}

	public CodecID get_video_codec_id() {
		return video_codec_id;
	}

	public void set_video_codec_id(CodecID video_codec_id) {
		this.video_codec_id = video_codec_id;
	}

	public CodecID get_audio_codec_id() {
		return audio_codec_id;
	}

	public void set_audio_codec_id(CodecID audio_codec_id) {
		this.audio_codec_id = audio_codec_id;
	}

	public CodecID get_subtitle_codec_id() {
		return subtitle_codec_id;
	}

	public void set_subtitle_codec_id(CodecID subtitle_codec_id) {
		this.subtitle_codec_id = subtitle_codec_id;
	}

	public int get_max_index_size() {
		return max_index_size;
	}

	public void set_max_index_size(int max_index_size) {
		this.max_index_size = max_index_size;
	}

	public int get_max_picture_buffer() {
		return max_picture_buffer;
	}

	public void set_max_picture_buffer(int max_picture_buffer) {
		this.max_picture_buffer = max_picture_buffer;
	}

	public void add_chapter(AVChapter ch) {
		chapters.add(ch);		
	}

	public ArrayList<AVChapter> get_chapters() {
		return chapters;
	}
	
	public AVChapter get_chapter(int i) {
		return chapters.get(i);
	}

	public int get_debug() {
		return debug;
	}

	public void set_debug(int debug) {
		this.debug = debug;
	}

	public AVPacketList get_raw_packet_buffer() {
		return raw_packet_buffer;
	}

	public void set_raw_packet_buffer(AVPacketList raw_packet_buffer) {
		this.raw_packet_buffer = raw_packet_buffer;
	}

	public AVPacketList get_raw_packet_buffer_end() {
		return raw_packet_buffer_end;
	}

	public void set_raw_packet_buffer_end(AVPacketList raw_packet_buffer_end) {
		this.raw_packet_buffer_end = raw_packet_buffer_end;
	}

	public AVPacketList get_packet_buffer_end() {
		return packet_buffer_end;
	}

	public void set_packet_buffer_end(AVPacketList packet_buffer_end) {
		this.packet_buffer_end = packet_buffer_end;
	}

	public AVDictionary get_metadata() {
		return metadata;
	}

	public void set_metadata(AVDictionary metadata) {
		this.metadata = metadata;
	}

	public int get_raw_packet_buffer_remaining_size() {
		return raw_packet_buffer_remaining_size;
	}

	public void set_raw_packet_buffer_remaining_size(
			int raw_packet_buffer_remaining_size) {
		this.raw_packet_buffer_remaining_size = raw_packet_buffer_remaining_size;
	}

	public long get_start_time_realtime() {
		return start_time_realtime;
	}

	public void set_start_time_realtime(long start_time_realtime) {
		this.start_time_realtime = start_time_realtime;
	}

	public int get_fps_probe_size() {
		return fps_probe_size;
	}

	public void set_fps_probe_size(int fps_probe_size) {
		this.fps_probe_size = fps_probe_size;
	}

	public int get_ts_id() {
		return ts_id;
	}

	public void set_ts_id(int ts_id) {
		this.ts_id = ts_id;
	}

	public AVStream av_new_stream(int id) {
		AVStream st = new AVStream(streams.size(), id);		
		
		st.set_codec(AVCodecContext.avcodec_alloc_context3(null));
				
		if (iformat != null) {
	        // no default bitrate if decoding 
			st.get_codec().set_bit_rate(0);
		}
		
		st.set_start_time(AVUtil.AV_NOPTS_VALUE);
		st.set_duration(AVUtil.AV_NOPTS_VALUE);
        /* we set the current DTS to 0 so that formats without any timestamps
        but durations get some timestamps, formats with some unknown
        timestamps have their first few packets buffered and the
        timestamps corrected before they are returned to the user */
		st.set_cur_dts(0);
		st.set_first_dts(AVUtil.AV_NOPTS_VALUE);
		st.set_probe_packets(AVFormat.MAX_PROBE_PACKETS);
	    /* default pts setting is MPEG-like */
		st.av_set_pts_info(33, 1, 90000);
		
		st.set_last_IP_pts(AVUtil.AV_NOPTS_VALUE);
	    for (int i=0 ; i < AVStream.MAX_REORDER_DELAY+1 ; i++)
	        st.get_pts_buffer()[i] = AVUtil.AV_NOPTS_VALUE;
		st.set_reference_dts(AVUtil.AV_NOPTS_VALUE);
		st.set_sample_aspect_ratio(new AVRational());

		streams.add(st);
		
		return st;
	}

	
	public AVStream get_stream(int index) {
		return streams.get(index);
	}

	public OutOI av_read_frame() {
		AVPacket pkt;
		AVPacketList pktl;
	    int eof=0;
	    int genpts = get_flags() & AVFormat.AVFMT_FLAG_GENPTS;

	    for (;;) {
	        pktl = get_packet_buffer();
	        if (pktl != null) {
	        	AVPacket next_pkt = pktl.get_pkt();

	           if ( genpts != 0 && next_pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) {
	            	int wrap_bits = get_stream(next_pkt.get_stream_index()).get_pts_wrap_bits();	            	
	            	
	            	while ( pktl != null && next_pkt.get_pts() == AVUtil.AV_NOPTS_VALUE ) {
	            		if ( (pktl.get_pkt().get_stream_index() == next_pkt.get_stream_index()) &&
	            			 (0 > Mathematics.av_compare_mod(next_pkt.get_dts(),pktl.get_pkt().get_dts(), 2 << (wrap_bits - 1))) &&
	            			 (Mathematics.av_compare_mod(pktl.get_pkt().get_pts(), pktl.get_pkt().get_dts(), 2 << (wrap_bits - 1)) != 0) ) { //not b frame
	            			next_pkt.set_pts(pktl.get_pkt().get_dts());
	            		}
	            		pktl = pktl.get_next();	            		
	            	}	            	
	            }

	            if ( (next_pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) || 
	            	 (next_pkt.get_dts() == AVUtil.AV_NOPTS_VALUE) ||
	            	 (genpts == 0) || 
	            	 (eof != 0) ) {
	                /* read packet from packet buffer, if there is data */
	            	pkt = next_pkt;
	                set_packet_buffer(pktl.get_next());
	                return new OutOI(pkt, 0);
	            }
	        }
	        if (genpts != 0) {
	            OutOI tmp = av_read_frame_internal();
	            int ret = tmp.get_ret();
	            pkt = (AVPacket) tmp.get_obj(); 
	            if (ret < 0) {
	                if ( pktl != null && ret != Error.AVERROR(Error.EAGAIN) ) {
	                    eof = 1;
	                    continue;
	                } else
	                    return new OutOI(pkt, ret);
	            }
	            
	            add_to_pktbuf(pkt);
	            
	        } else {
	            return av_read_frame_internal();
	        }
	    }
	}

	private void add_to_pktbuf(AVPacket pkt) {
		AVPacketList pktl = new AVPacketList();
		
		if (packet_buffer != null)
			packet_buffer_end.set_next(pktl);
		else
			packet_buffer = pktl;
		
		packet_buffer_end = pktl;
		pktl.set_pkt(pkt);		
	}


	private OutOI av_read_packet() {
		int ret, i;
	    AVStream st;
	    AVPacket pkt;

	    for(;;) {
	        AVPacketList pktl = get_raw_packet_buffer();

	        if (pktl != null) {
	        	
	            pkt = pktl.get_pkt();
	            if (get_stream(pkt.get_stream_index()).get_request_probe() <= 0) {
	                raw_packet_buffer = pktl.get_next();
	                raw_packet_buffer_remaining_size += pkt.get_size();
	                return new OutOI(pkt, 0);
	            }
	        }

	        pkt = new AVPacket();
		    pkt.av_init_packet();;
	        ret = get_iformat().read_packet(this, pkt);
	        if (ret < 0) {
	            if ( pktl != null || ret == Error.AVERROR(Error.EAGAIN) )
	                return new OutOI(pkt, ret);
	            for (i = 0 ; i < get_nb_streams() ; i++)
	                if (get_stream(i).get_request_probe() > 0)
	                	get_stream(i).set_request_probe(-1);
	            continue;
	        }


	        if (!has_flag(AVFormat.AVFMT_FLAG_KEEP_SIDE_DATA))
	            pkt.av_packet_merge_side_data();
	        st = get_stream(pkt.get_stream_index());

	        switch (st.get_codec().get_codec_type()){
	        case AVMEDIA_TYPE_VIDEO:
	            if (get_video_codec_id() != CodecID.CODEC_ID_NONE) 
	            	st.get_codec().set_codec_id(get_video_codec_id());
	            break;
	        case AVMEDIA_TYPE_AUDIO:
	            if (get_audio_codec_id() != CodecID.CODEC_ID_NONE) 
	            	st.get_codec().set_codec_id(get_audio_codec_id());
	            break;
	        case AVMEDIA_TYPE_SUBTITLE:
	            if (get_subtitle_codec_id() != CodecID.CODEC_ID_NONE) 
	            	st.get_codec().set_codec_id(get_subtitle_codec_id());
	            break;
	        }

	        if ( pktl == null && st.get_request_probe() <= 0 )
	            return new OutOI(pkt, 0);
	        
	        add_to_pktbuf(pkt);
	        this.raw_packet_buffer_remaining_size -= pkt.get_size();


	        if (st.get_request_probe() > 0) {
	            AVProbeData pd = st.get_probe_data();
	            int end;
	            Log.av_log("formatCtx", Log.AV_LOG_DEBUG, 
	            		String.format("probing stream %d pp:%d\n", st.get_index(), 
	            				st.get_probe_packets()));
	            st.set_probe_packets(st.get_probe_packets() - 1);

	            pd.add_bytes(pkt.get_data());
	            pd.add_bytes(new short[AVFormat.AVPROBE_PADDING_SIZE]);
	            

	            
	            end = ( (get_raw_packet_buffer_remaining_size() <= 0) ||
	                    (st.get_probe_packets() <= 0) ) ? 1 : 0;

	            if ( (end != 0) || 
	            	 (Common.av_log2(pd.get_buf_size()) != Common.av_log2(pd.get_buf_size() - pkt.get_size())) ) {
	                int score = set_codec_from_probe_data(st, pd);
	                if ( ( (st.get_codec().get_codec_id() != CodecID.CODEC_ID_NONE) && 
	                		(score > AVFormat.AVPROBE_SCORE_MAX / 4) ) ||
	                     (end != 0) ) {
	                    pd.av_freep();
	                    st.set_request_probe(-1);
	                    if (st.get_codec().get_codec_id() != CodecID.CODEC_ID_NONE) {
	                    	Log.av_log("formatCtx", Log.AV_LOG_DEBUG, "probed stream " + st.get_index() + "\n");
	                    } else {
	                    	Log.av_log("formatCtx", Log.AV_LOG_WARNING, "probed stream " + st.get_index() + " failed\n");
	                    }
	                }
	            }
	        }
	    }		
	}

	private int set_codec_from_probe_data(AVStream st, AVProbeData pd) {
		FmtType [] fmt_id_type = {
				new FmtType("aac"      , CodecID.CODEC_ID_AAC       , AVMediaType.AVMEDIA_TYPE_AUDIO),
				new FmtType("ac3"      , CodecID.CODEC_ID_AC3       , AVMediaType.AVMEDIA_TYPE_AUDIO),
				new FmtType("dts"      , CodecID.CODEC_ID_DTS       , AVMediaType.AVMEDIA_TYPE_AUDIO),
				new FmtType("eac3"     , CodecID.CODEC_ID_EAC3      , AVMediaType.AVMEDIA_TYPE_AUDIO),
				new FmtType("h264"     , CodecID.CODEC_ID_H264      , AVMediaType.AVMEDIA_TYPE_VIDEO),
				new FmtType("m4v"      , CodecID.CODEC_ID_MPEG4     , AVMediaType.AVMEDIA_TYPE_VIDEO),
				new FmtType("mp3"      , CodecID.CODEC_ID_MP3       , AVMediaType.AVMEDIA_TYPE_AUDIO),
				new FmtType("mpegvideo", CodecID.CODEC_ID_MPEG2VIDEO, AVMediaType.AVMEDIA_TYPE_VIDEO)};
		        		
		int score;
		
		OutOI ret_obj = pd.av_probe_input_format3(true);
		AVInputFormat fmt = (AVInputFormat)ret_obj.get_obj();
		score = ret_obj.get_ret();

		if (fmt != null) {
			int i;
			Log.av_log("formatCtx", Log.AV_LOG_DEBUG, 
					String.format("Probe with size=%d, packets=%d detected %s with score=%d\n",
					pd.get_buf_size(), AVFormat.MAX_PROBE_PACKETS - st.get_probe_packets(), 
					fmt.get_name(), score));
        	for (FmtType f : fmt_id_type) {
        		if (fmt.get_name().equals(f.get_name())) {
        			st.get_codec().set_codec_id(f.get_id());
        			st.get_codec().set_codec_type(f.get_type());
        			break;
	            }
	        }
	    }
	    return score;
	}


	OutOI av_read_frame_internal() {
		AVPacket pkt = new AVPacket();
	    AVStream st;
	    int len, ret, i;
	    
	    pkt.av_init_packet();	    

	    for(;;) {
	    	  /* select current input stream component */
	        st = get_cur_st();
	        if (st != null) {
	            if ( (st.get_need_parsing() == AVStreamParseType.AVSTREAM_PARSE_NONE) || 
	            	 (st.get_parser() == null) ) {
	                /* no parsing needed: we just output the packet as is */
	                /* raw data support */
	                pkt = st.get_cur_pkt(); 
	                //st.get_cur_pkt().set_data(null);
	                compute_pkt_fields(st, null, pkt);
	                set_cur_st(null);
	                if ( (get_iformat().has_flag(AVFormat.AVFMT_GENERIC_INDEX)) &&
	                     (pkt.has_flag(AVCodec.AV_PKT_FLAG_KEY)) && 
	                     (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) ) {
	                    ff_reduce_index(st.get_index());
	                    st.av_add_index_entry(pkt.get_pos(), pkt.get_dts(), 0, 0, AVFormat.AVINDEX_KEYFRAME);
	                }
	                break;
	            } else if ( (st.get_cur_len() > 0) && 
	            		     (st.get_discard() != AVDiscard.AVDISCARD_ALL) ) {
	                OutOI tmp_obj = st.get_parser().av_parser_parse2(st.get_codec(), 
                                       st.get_cur_ptr(), st.get_cur_len(),
                                       st.get_cur_pkt().get_pts(), st.get_cur_pkt().get_dts(),
                                       st.get_cur_pkt().get_pos());
	                len = tmp_obj.get_ret();
	                pkt.set_data((short[]) tmp_obj.get_obj());
	                st.get_cur_pkt().set_pts(AVUtil.AV_NOPTS_VALUE);
	                st.get_cur_pkt().set_dts(AVUtil.AV_NOPTS_VALUE);
	                /* increment read pointer */
	                st.set_cur_ptr(Arrays.copyOfRange(st.get_cur_ptr(), len, st.get_cur_ptr().length));
	                st.set_cur_len(st.get_cur_len() - len);

	                /* return packet if any */
	                if (pkt.get_size() != 0) {
	                    pkt.set_duration(0);
	                    pkt.set_stream_index(st.get_index());
	                    pkt.set_pts(st.get_parser().get_pts());
	                    pkt.set_dts(st.get_parser().get_dts());
	                    pkt.set_pos(st.get_parser().get_pos());
	                    if ( (Arrays.equals(pkt.get_data(), st.get_cur_pkt().get_data())) && 
	                    	 (pkt.get_size() == st.get_cur_pkt().get_size()) ){
	                        set_cur_st(null);
	                        st.get_cur_pkt().set_data(null);
	                    } 
	                    compute_pkt_fields(st, st.get_parser(), pkt);

	                    if ( (get_iformat().has_flag(AVFormat.AVFMT_GENERIC_INDEX)) && 
	                    	 (pkt.has_flag(AVCodec.AV_PKT_FLAG_KEY)) ) {
	                        long pos = (st.get_parser().has_flag(AVCodec.PARSER_FLAG_COMPLETE_FRAMES)) ? pkt.get_pos() : st.get_parser().get_frame_offset();
	                        ff_reduce_index(st.get_index());
	                        st.av_add_index_entry(pos, pkt.get_dts(), 0, 0, AVFormat.AVINDEX_KEYFRAME);
	                    }

	                    break;
	                }
	            } else {
	                /* free packet */
	                st.set_cur_pkt(null);
	                set_cur_st(null);
	            }
	        } else {
	            /* read next packet */	
	            OutOI ret_obj = av_read_packet();
	            ret = ret_obj.get_ret();
	            AVPacket cur_pkt = (AVPacket) ret_obj.get_obj();

	            if (ret < 0) {
	                if (ret == Error.AVERROR(Error.EAGAIN))
	                    return new OutOI(cur_pkt, ret);
	                /* return the last frames, if any */
	                for(AVStream st1 : streams) {
	                    if ( (st1.get_parser() != null) && 
	                    	 (st1.get_need_parsing() != AVStreamParseType.AVSTREAM_PARSE_NONE) ) {
	                    	OutOI tmp_obj = st1.get_parser().av_parser_parse2(st1.get_codec(),
	                                        null, 0,
	                                        AVUtil.AV_NOPTS_VALUE, 
	                                        AVUtil.AV_NOPTS_VALUE,
	                                        AVUtil.AV_NOPTS_VALUE);
	                    	len = tmp_obj.get_ret();
	    	                pkt.set_data((short[]) tmp_obj.get_obj());
	                        if (pkt.get_size() != 0) {
	                        	pkt.set_duration(0);
	    	                    pkt.set_stream_index(st1.get_index());
	    	                    pkt.set_pts(st1.get_parser().get_pts());
	    	                    pkt.set_dts(st1.get_parser().get_dts());
	    	                    pkt.set_pos(st1.get_parser().get_pos());
	    	                    if ( (Arrays.equals(pkt.get_data(), st1.get_cur_pkt().get_data())) && 
	    	                    	 (pkt.get_size() == st1.get_cur_pkt().get_size()) ){
	    	                        set_cur_st(null);
	    	                        st1.get_cur_pkt().set_data(null);
	    	                    } 
	    	                    compute_pkt_fields(st1, st1.get_parser(), pkt);

	    	                    if ( (get_iformat().has_flag(AVFormat.AVFMT_GENERIC_INDEX)) && 
	    	                    	 (pkt.has_flag(AVCodec.AV_PKT_FLAG_KEY)) ) {
	    	                        long pos = (st1.get_parser().has_flag(AVCodec.PARSER_FLAG_COMPLETE_FRAMES)) ? pkt.get_pos() : st.get_parser().get_frame_offset();
	    	                        ff_reduce_index(st1.get_index());
	    	                        st1.av_add_index_entry(pos, pkt.get_dts(), 0, 0, AVFormat.AVINDEX_KEYFRAME);
	    	                    }
	                        }
	                    }
	                }
	                /* no more packets: really terminate parsing */
                    return new OutOI(pkt, ret);
	            }
	            st = get_stream(cur_pkt.get_stream_index());
	            st.set_cur_pkt(cur_pkt);

	            if ( (st.get_cur_pkt().get_pts() != AVUtil.AV_NOPTS_VALUE) &&
	            	 (st.get_cur_pkt().get_dts() != AVUtil.AV_NOPTS_VALUE) &&
	                 (st.get_cur_pkt().get_pts() < st.get_cur_pkt().get_dts()) ) {
	                Log.av_log("formatCtx", Log.AV_LOG_WARNING,
	                		String.format("Invalid timestamps stream=%d, pts=%d, dts=%d, size=%d\n",
	                				st.get_cur_pkt().get_stream_index(),
				                    st.get_cur_pkt().get_pts(),
				                    st.get_cur_pkt().get_dts(),
				                    st.get_cur_pkt().get_size()));
//	                av_free_packet(st.get_cur_pkt());
//	                return -1;
	            }

	            if ( (get_debug() & AVFormat.FF_FDEBUG_TS) != 0)
	            	Log.av_log("formatCtx", Log.AV_LOG_DEBUG, 
	                		String.format("av_read_packet stream=%d, pts=%d, dts=%d, size=%d, duration=%d, flags=%d\n",
	                				st.get_cur_pkt().get_stream_index(),
				                    st.get_cur_pkt().get_pts(),
				                    st.get_cur_pkt().get_dts(),
				                    st.get_cur_pkt().get_size(),
				                    st.get_cur_pkt().get_duration(),
				                    st.get_cur_pkt().get_flags()));

	            set_cur_st(st);
	            st.set_cur_ptr(st.get_cur_pkt().get_data());
	            //st.get_cur_len = st.get_cur_pkt.size;
	            if ( (st.get_need_parsing() != AVStreamParseType.AVSTREAM_PARSE_NONE) && 
	            	 (st.get_parser() == null) && 
	            	 (!has_flag(AVFormat.AVFMT_FLAG_NOPARSE)) ) {
	                st.set_parser(AVCodecParserContext.av_parser_init(st.get_codec().get_codec_id()));
	                if (st.get_parser() == null) {
	                    /* no parser available: just output the raw packets */
	                    st.set_need_parsing(AVStreamParseType.AVSTREAM_PARSE_NONE);
	                } else if (st.get_need_parsing() == AVStreamParseType.AVSTREAM_PARSE_HEADERS) {
	                    st.get_parser().add_flag(AVCodec.PARSER_FLAG_COMPLETE_FRAMES);
	                }else if (st.get_need_parsing() == AVStreamParseType.AVSTREAM_PARSE_FULL_ONCE) {
	                    st.get_parser().add_flag(AVCodec.PARSER_FLAG_ONCE);
	                }
	            }
	        }
	    	
	    	
	    }
	    
	    if ( (debug & AVFormat.FF_FDEBUG_TS) != 0 )
	        Log.av_log("formatCtx", Log.AV_LOG_DEBUG, 
	        		String.format("av_read_frame_internal stream=%d, pts=%d, dts=%d, size=%d, duration=%d, flags=%d",
	            pkt.get_stream_index(),
	            pkt.get_pts(),
	            pkt.get_dts(),
	            pkt.get_size(),
	            pkt.get_duration(),
	            pkt.get_flags()));

	    return new OutOI(pkt, 0);
		
	/*	try {
			ret = iformat.read_packet(this, pkt);
			return new OutOI(pkt, ret);
		} catch (IOException e) {
			return new OutOI(null, -1);
		}*/
	}
	

	private void ff_reduce_index(int stream_index) {
	    AVStream st = get_stream(stream_index);
	    int max_entries = get_max_index_size();

	    if (st.get_nb_index_entries() >= max_entries){
	        int i;
	        for(i = 0 ; 2 * i < st.get_nb_index_entries() ; i++)
	            st.set_index_entries(i, st.get_index_entries(2*i));
	        st.set_nb_index_entries(i);
	    }		
	}


	private void compute_pkt_fields(AVStream st, AVCodecParserContext pc, 
			AVPacket pkt) {
		int num, den, presentation_delayed, delay, i;
	    long offset;

	    if ( (get_flags() & AVFormat.AVFMT_FLAG_NOFILLIN) != 0)
	        return;

	    if ( ((get_flags() & AVFormat.AVFMT_FLAG_IGNDTS) != 0) && 
	    	 (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) )
	        pkt.set_dts(AVUtil.AV_NOPTS_VALUE);

	    if ( (st.get_codec().get_codec_id() != CodecID.CODEC_ID_H264) && 
	    	 (pc != null) && 
	    	 (pc.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) )
	        //FIXME Set low_delay = 0 when has_b_frames = 1
	        st.get_codec().set_has_b_frames(1);

	    /* do we have a video B-frame ? */
	    delay = st.get_codec().get_has_b_frames();
	    presentation_delayed = 0;

	    // ignore delay caused by frame threading so that the mpeg2-without-dts
	    // warning will not trigger
	    if ( (delay != 0) && 
	    	 ((st.get_codec().get_active_thread_type() & AVCodec.FF_THREAD_FRAME) != 0) )
	        delay -= st.get_codec().get_thread_count() - 1;

	    /* XXX: need has_b_frame, but cannot get it if the codec is
	        not initialized */
	    if ( (delay != 0) &&
	         (pc != null) && 
	         (pc.get_pict_type() != AVPictureType.AV_PICTURE_TYPE_B) )
	        presentation_delayed = 1;

	    if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
    	     (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && 
    	     (pkt.get_dts() > pkt.get_pts()) && 
    	     (st.get_pts_wrap_bits() < 63) ) {
	       
	        pkt.set_dts(pkt.get_dts() - 1L << st.get_pts_wrap_bits());
	    }

	    // some mpeg2 in mpeg-ps lack dts (issue171 / input_file.mpg)
	    // we take the conservative approach and discard both
	    // Note, if this is misbehaving for a H.264 file then possibly presentation_delayed is not set correctly.
	    if ( (delay == 1) && 
	    	 (pkt.get_dts() == pkt.get_pts()) && 
	    	 (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (presentation_delayed != 0) ) {
	        Log.av_log("formatCtx", Log.AV_LOG_DEBUG, "invalid dts/pts combination\n");
	        pkt.set_dts(AVUtil.AV_NOPTS_VALUE);
	        pkt.set_pts(AVUtil.AV_NOPTS_VALUE);
	    }

	    if (pkt.get_duration() == 0) {
	        AVRational r = UtilsFormat.compute_frame_duration(st, pc, pkt);
	        
	        if ( (r.get_den() != 0) && (r.get_num() != 0) ) {
	            pkt.set_duration((int)Mathematics.av_rescale_rnd(1, 
	            		r.get_num() * st.get_time_base().get_den(), 
	            		r.get_den() * st.get_time_base().get_num(), 
	            		AVRounding.AV_ROUND_DOWN));

	            if ( (pkt.get_duration() != 0) && 
	            	 (get_packet_buffer() != null) )
	                update_initial_durations(st, pkt);
	        }
	    }

	    /* correct timestamps with byte offset if demuxers only have timestamps
	       on packet boundaries */
	    if ( (pc != null) && 
	    	 (st.get_need_parsing() == AVStreamParseType.AVSTREAM_PARSE_TIMESTAMPS) && 
	    	 (pkt.get_size() != 0) ){
	        /* this will estimate bitrate based on this frame's duration and size */
	        offset = Mathematics.av_rescale(pc.get_offset(), pkt.get_duration(), pkt.get_size());
	        if(pkt.get_pts() != AVUtil.AV_NOPTS_VALUE)
	            pkt.set_pts(pkt.get_pts() + offset);
	        if(pkt.get_dts() != AVUtil.AV_NOPTS_VALUE)
	            pkt.set_dts(pkt.get_dts() + offset);
	    }

	    if ( (pc != null) && (pc.get_dts_sync_point() >= 0) ) {
	        // we have synchronization info from the parser
	        long lden = st.get_codec().get_time_base().get_den() * st.get_time_base().get_num();
	        if (lden > 0) {
	            long lnum = st.get_codec().get_time_base().get_num() * st.get_time_base().get_den();
	            if (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) {
	                // got DTS from the stream, update reference timestamp
	                st.set_reference_dts(pkt.get_dts() - pc.get_dts_ref_dts_delta() * lnum / lden);
	                pkt.set_pts(pkt.get_dts() + pc.get_pts_dts_delta() * lnum / lden);
	            } else if (st.get_reference_dts() != AVUtil.AV_NOPTS_VALUE) {
	                // compute DTS based on reference timestamp
	                pkt.set_dts(st.get_reference_dts() + pc.get_dts_ref_dts_delta() * lnum / lden);
	                pkt.set_pts(pkt.get_dts() + pc.get_pts_dts_delta() * lnum / lden);
	            }
	            if (pc.get_dts_sync_point() > 0)
	                st.set_reference_dts(pkt.get_dts()); // new reference
	        }
	    }

	    /* This may be redundant, but it should not hurt. */
	    if ( (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (pkt.get_pts() > pkt.get_dts()) )
	        presentation_delayed = 1;

//	    av_log(NULL, AV_LOG_DEBUG, "IN delayed:%d pts:%"PRId64", dts:%"PRId64" cur_dts:%"PRId64" st:%d pc:%p\n", presentation_delayed, pkt.get_pts, pkt.get_dts, st.get_cur_dts, pkt.get_stream_index, pc);
	    /* interpolate PTS and DTS if they are not present */
	    //We skip H264 currently because delay and has_b_frames are not reliably set
	    if ( ( ( (delay == 0) || ((delay == 1) && (pc != null)) ) && 
	    	 (st.get_codec().get_codec_id() != CodecID.CODEC_ID_H264) ) ) {
	        if (presentation_delayed != 0) {
	            /* DTS = decompression timestamp */
	            /* PTS = presentation timestamp */
	            if (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE)
	                pkt.set_dts(st.get_last_IP_pts());
	            update_initial_timestamps(pkt.get_stream_index(), pkt.get_dts(), pkt.get_pts());
	            if (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE)
	                pkt.set_dts(st.get_cur_dts());

	            /* this is tricky: the dts must be incremented by the duration
	            of the frame we are displaying, i.e. the last I- or P-frame */
	            if (st.get_last_IP_duration() == 0)
	                st.set_last_IP_duration(pkt.get_duration());
	            if (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE)
	                st.set_cur_dts(pkt.get_dts() + st.get_last_IP_duration());
	            st.set_last_IP_duration(pkt.get_duration());
	            st.set_last_IP_pts(pkt.get_pts());
	            /* cannot compute PTS if not present (we can compute it only
	            by knowing the future */
	        } else if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) || 
	        		     (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) || 
	        		     (pkt.get_duration() != 0) ){
	            if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	            	 (pkt.get_duration() != 0) ){
	                long old_diff = Mathematics.FFABS(st.get_cur_dts() - pkt.get_duration() - pkt.get_pts());
	                long new_diff = Mathematics.FFABS(st.get_cur_dts() - pkt.get_pts());
	                if ( (old_diff < new_diff) && (old_diff < (pkt.get_duration() >> 3)) ){
	                    pkt.set_pts(pkt.get_pts() + pkt.get_duration());
	    //                av_log(NULL, AV_LOG_DEBUG, "id:%d old:%"PRId64" new:%"PRId64" dur:%d cur:%"PRId64" size:%d\n", pkt.get_stream_index, old_diff, new_diff, pkt.get_duration, st.get_cur_dts, pkt.get_size);
	                }
	            }

	            /* presentation is not delayed : PTS and DTS are the same */
	            if (pkt.get_pts() == AVUtil.AV_NOPTS_VALUE)
	                pkt.set_pts(pkt.get_dts());
	            update_initial_timestamps(pkt.get_stream_index(), pkt.get_pts(), pkt.get_pts());
	            if (pkt.get_pts() == AVUtil.AV_NOPTS_VALUE)
	                pkt.set_pts(st.get_cur_dts());
	            pkt.set_dts(pkt.get_pts());
	            if (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE)
	                st.set_cur_dts(pkt.get_pts() + pkt.get_duration());
	        }
	    }

	    if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (delay <= AVStream.MAX_REORDER_DELAY) ) {
	        st.set_pts_buffer(0, pkt.get_pts());
	        for (i = 0 ; (i < delay) && (st.get_pts_buffer(i) > st.get_pts_buffer(i+1)) ; i++) {
	        	long tmp = st.get_pts_buffer(i);
	        	st.set_pts_buffer(i, st.get_pts_buffer(i+1));
	        	st.set_pts_buffer(i+1, tmp);
	        }
	        if (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE)
	            pkt.set_dts(st.get_pts_buffer(0));
	        if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_H264) { //we skiped it above so we try here
	            update_initial_timestamps(pkt.get_stream_index(), pkt.get_dts(), pkt.get_pts()); // this should happen on the first packet
	        }
	        if (pkt.get_dts() > st.get_cur_dts())
	            st.set_cur_dts(pkt.get_dts());
	    }

//	    av_log(NULL, AV_LOG_ERROR, "OUTdelayed:%d/%d pts:%"PRId64", dts:%"PRId64" cur_dts:%"PRId64"\n", presentation_delayed, delay, pkt.get_pts, pkt.get_dts, st.get_cur_dts);

	    /* update flags */
	    if (st.get_codec().is_intra_only() != 0)
	        pkt.set_flags(pkt.get_flags() | AVCodec.AV_PKT_FLAG_KEY);
	    else if (pc != null) {
	        pkt.set_flags(0);
	        /* keyframe computation */
	        if (pc.get_key_frame() == 1)
	            pkt.set_flags(pkt.get_flags() | AVCodec.AV_PKT_FLAG_KEY);
	        else if ( (pc.get_key_frame() == -1) && (pc.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_I) )
	            pkt.set_flags(pkt.get_flags() | AVCodec.AV_PKT_FLAG_KEY);
	    }
	    if (pc != null)
	        pkt.set_convergence_duration(pc.get_convergence_duration());
		
	}



	private void update_initial_timestamps(int stream_index, long dts, long pts) { 
	    AVStream st = get_stream(stream_index);
	    AVPacketList pktl = get_packet_buffer();

	    if ( (st.get_first_dts() != AVUtil.AV_NOPTS_VALUE) || 
	    	 (dts == AVUtil.AV_NOPTS_VALUE) || 
	    	 (st.get_cur_dts() == AVUtil.AV_NOPTS_VALUE) )
	        return;

	    st.set_first_dts(dts - st.get_cur_dts());
	    st.set_cur_dts(dts);

	    for ( ; pktl != null ; pktl = pktl.get_next()) {
	    	AVPacket pkt = pktl.get_pkt();
	        if (pkt.get_stream_index() != stream_index)
	            continue;
	        //FIXME think more about this check
	        if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	        	 (pkt.get_pts() == pkt.get_dts()) )
	           	pkt.set_pts(pkt.get_pts() + st.get_first_dts());

	        if (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE)
	            pkt.set_dts(pkt.get_dts() + st.get_first_dts());

	        if ( (st.get_start_time() == AVUtil.AV_NOPTS_VALUE) && 
	        	  (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) )
	            st.set_start_time(pkt.get_pts());
	    }
	    if (st.get_start_time() == AVUtil.AV_NOPTS_VALUE)
	        st.set_start_time(pts);
	}


	private void update_initial_durations(AVStream st, AVPacket pkt) {
		AVPacketList pktl = get_packet_buffer();
	    long cur_dts= 0;

	    if (st.get_first_dts() != AVUtil.AV_NOPTS_VALUE) {
	        cur_dts = st.get_first_dts();
	        for ( ; pktl != null ; pktl = pktl.get_next()) {
	        	AVPacket p = pktl.get_pkt();
	            if (p.get_stream_index() == pkt.get_stream_index()) {
	                if ( (p.get_pts() != p.get_dts()) ||
	                     (p.get_dts() != AVUtil.AV_NOPTS_VALUE) || 
	                     (p.get_duration() != 0) )
	                    break;
	                cur_dts -= pkt.get_duration();
	            }
	        }
	        st.set_first_dts(cur_dts);
	    } else if (st.get_cur_dts() != 0)
	        return;

	    for( ; pktl != null ; pktl = pktl.get_next()) {
        	AVPacket p = pktl.get_pkt();
	    	if (p.get_stream_index() == pkt.get_stream_index())
	            continue;
	    	if ( (p.get_pts() != p.get_dts()) ||
	    		 (p.get_dts() != AVUtil.AV_NOPTS_VALUE) || 
                 (p.get_duration() == 0) ) {
	            p.set_dts(cur_dts);
	            if (st.get_codec().get_has_b_frames() == 0)
	                p.set_pts(cur_dts);
	            cur_dts += pkt.get_duration();
	            p.set_duration(pkt.get_duration());
	        } else
	            break;
	    }
	    if (st.get_first_dts() == AVUtil.AV_NOPTS_VALUE)
	        st.set_cur_dts(cur_dts);
		
	}


	public int av_seek_frame_byte(int stream_index, long timestamp, int flags) {
		return -1;
		
	}
	
	
	public int av_seek_frame(int stream_index, long timestamp, int flags) {
	    int ret;
	    AVStream st;
	    
	    ff_read_frame_flush();

	    if ( (flags & AVFormat.AVSEEK_FLAG_BYTE) == AVFormat.AVSEEK_FLAG_BYTE)
	        return av_seek_frame_byte(stream_index, timestamp, flags);

	    if (stream_index < 0) {
	        stream_index = av_find_default_stream_index();
	        if(stream_index < 0)
	            return -1;

	        st = streams.get(stream_index);
	       /* timestamp for default must be expressed in AV_TIME_BASE units */
	        timestamp = Mathematics.av_rescale(timestamp, 
	        		st.get_time_base().get_den(), 
	        		AVUtil.AV_TIME_BASE * st.get_time_base().get_num());
	    }

	    /* first, we try the format specific seek */
	    ret = iformat.read_seek(this, stream_index, timestamp, flags);
	    // If not defined ret = -1	    
	    if (ret >= 0) {
	    	this.timestamp = timestamp;
	        return 0;
	    }
	    
	    return -1;
	    

	    /*
	     if(s.get_iformat.get_read_timestamp && !(s.get_iformat.get_flags & AVFMT_NOBINSEARCH))
	        return av_seek_frame_binary(s, stream_index, timestamp, flags);
	    else if (!(s.get_iformat.get_flags & AVFMT_NOGENSEARCH))
	        return av_seek_frame_generic(s, stream_index, timestamp, flags);
	    else
	        return -1;
	    */
	}

	private void ff_read_frame_flush() {
	    AVStream st;
	    int i, j;

	    flush_packet_queue();

	    set_cur_st(null);

	    /* for each stream, reset read state */
	    for(i = 0 ; i < get_nb_streams(); i++) {
	        st = get_stream(i);

	        if (st.get_parser() != null) {
	            //st.get_parser().av_parser_close();
	            st.set_parser(null);
	            st.set_cur_pkt(null);
	        }
	        st.set_last_IP_pts(AVUtil.AV_NOPTS_VALUE);
	        st.set_cur_dts(AVUtil.AV_NOPTS_VALUE); /* we set the current DTS to an unspecified origin */
	        st.set_reference_dts(AVUtil.AV_NOPTS_VALUE);
	        /* fail safe */
	        st.set_cur_ptr(null);
	        st.set_cur_len(0);

	        st.set_probe_packets(AVFormat.MAX_PROBE_PACKETS);

	        for(j = 0 ; j < AVStream.MAX_REORDER_DELAY + 1 ; j++)
	            st.set_pts_buffer(i, AVUtil.AV_NOPTS_VALUE);
	    }
		
	}


	private void flush_packet_queue() {
	    AVPacketList pktl;

	    for (;;) {
	        pktl = get_packet_buffer();
	        if (pktl == null)
	            break;
	        set_packet_buffer(pktl.get_next());
	        pktl.set_pkt(null);
	    }
	    while (get_raw_packet_buffer() != null) {
	        pktl = get_raw_packet_buffer();
	        set_raw_packet_buffer(pktl.get_next());
	        pktl.set_pkt(null);
	    }
	    
	    set_packet_buffer_end(null);
	    set_raw_packet_buffer_end(null);
	    set_raw_packet_buffer_remaining_size(AVFormat.RAW_PACKET_BUFFER_SIZE);		
	}


	private int av_find_default_stream_index() {
		int first_audio_index = -1;
		AVStream st;

		if (get_nb_streams() <= 0)
			return -1;
		
		for(int i = 0 ; i < get_nb_streams() ; i++) {
			st = streams.get(i);
			if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				return i;
			}
			if ( (first_audio_index < 0) && 
					(st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) )
				first_audio_index = i;
		}
		return first_audio_index >= 0 ? first_audio_index : 0;
	}

	public void dump_format(int index, String url, boolean is_output) {
		av_dump_format(index, url, is_output);
	}

	
	public void av_dump_format(int index, String url, boolean is_output) {
		
		boolean [] printed = new boolean[get_nb_streams()]; 
		
		if (get_nb_streams() == 0)
			return;
		
		Log.av_log("", Log.AV_LOG_INFO, "%s #%d, %s, %s '%s':\n",
		            is_output ? "Output" : "Input",
		            index,
		            is_output ?get_oformat().get_name() : get_iformat().get_name(),
		            is_output ? "to" : "from", url);
		 
		if (metadata != null)
			metadata.dump_metadata("", "  ");
		
	    if (!is_output) {	
	    	Log.av_log("", Log.AV_LOG_INFO, "  Duration: ");        
	        if (duration != AVUtil.AV_NOPTS_VALUE) {
	            int hours, mins, secs, us;
	            secs = (int) (duration / AVUtil.AV_TIME_BASE);
	            us = (int) (duration % AVUtil.AV_TIME_BASE);
	            mins = secs / 60;
	            secs %= 60;
	            hours = mins / 60;
	            mins %= 60;
	            Log.av_log("", Log.AV_LOG_INFO, "%02d:%02d:%02d.%02d", hours, mins, secs,
	                    (100 * us) / AVUtil.AV_TIME_BASE);
	         } else {
	        	 Log.av_log("", Log.AV_LOG_INFO, "N/A");
	        }
	        
	        if (start_time != AVUtil.AV_NOPTS_VALUE) {
	            int secs, us;
	            Log.av_log("", Log.AV_LOG_INFO, ", start: ");
	            secs = (int) (start_time / AVUtil.AV_TIME_BASE);
	            us = (int) Math.abs(start_time % AVUtil.AV_TIME_BASE);
	            Log.av_log("", Log.AV_LOG_INFO, "%d.%06d",
	                    secs, (int)Mathematics.av_rescale(us, 1000000, AVUtil.AV_TIME_BASE));
	        }
	        Log.av_log("", Log.AV_LOG_INFO, ", bitrate: ");
	        if (bit_rate != 0) {
	        	Log.av_log("", Log.AV_LOG_INFO,"%d kb/s", bit_rate / 1000);
	        } else {
	        	Log.av_log("", Log.AV_LOG_INFO, "N/A");
	        }
	        Log.av_log("", Log.AV_LOG_INFO, "\n");
	    }
	    

	    for (int i = 0; i < get_nb_chapters() ; i++) {
	        AVChapter ch = get_chapter(i);
	    	Log.av_log("", Log.AV_LOG_INFO, "    Chapter #%d.%d: ", index, i);
	    	Log.av_log("", Log.AV_LOG_INFO, "start %f, ", ch.get_start() * ch.get_time_base().av_q2d());
	    	Log.av_log("", Log.AV_LOG_INFO, "end %f\n",   ch.get_end()   * ch.get_time_base().av_q2d());

	    	if (ch.get_metadata() != null)
	    		ch.get_metadata().dump_metadata("", "    ");
	    }
	    
	    
//	    if (nb_programs() != 0) {
//	        int total = 0;
//	        for(AVProgram prg : programs) {
//	            AVDictionaryEntry name = prg.get_metadata().get("name", null, 0);
//	            System.out.println("  Program " + prg.get_id() + " " + name ? name.get_value() : "");
//	            prg.get_metadata().dump("    ");
//	            for (int stream_index : prg.get_stream_indexes()) {
//	                dump_stream_format(stream_index, index, is_output);
//	                printed[stream_index] = true;
//	            }
//	            total += prg.get_nb_stream_indexes();
//	        }
//	        if ( total < nb_streams() )
//	        	System.out.println("  No Program");
//	    }
	    
	    
	    for (AVStream st : streams) {
	        if (!printed[st.get_index()])
	            dump_stream_format(st, index, is_output);
	    }

		
	}

	private void dump_stream_format(AVStream st, int index, boolean is_output) {
		int flags = (is_output ? oformat.get_flags() : iformat.get_flags());
		
		int g = Mathematics.av_gcd(st.get_time_base().get_num(), 
								   st.get_time_base().get_den());
		String lang = st.get_metadata().av_dict_get("language", 0);
		
		
		String buf = avcodec_string(st.get_codec(), is_output);
		System.out.print("    Stream #" + index + "." + st.get_index());
		// the pid is an important information, so we display it 
		if ( (flags & AVFormat.AVFMT_SHOW_IDS) != 0 )
			System.out.print(String.format("[0x%x]", st.get_id()));
		if (lang != null)
			System.out.print("(" + lang + ")");
		
		/*Log.av_log(null, Log.AV_LOG_DEBUG, String.format(", %d, %d/%d", 
				st.get_codec_info_nb_frames(), st.get_time_base().get_num() / g, 
				st.get_time_base().get_den() / g));*/
		System.out.print(": " + buf);
		
	    if ( (st.get_sample_aspect_ratio().get_num() != 0) && // default
	         ( AVRational.av_cmp_q(st.get_sample_aspect_ratio(), 
	        	  	                st.get_codec().get_sample_aspect_ratio()) != 0 ) ) {
	        AVRational display_aspect_ratio = AVRational.av_reduce(st.get_codec().get_width() * st.get_sample_aspect_ratio().get_num(),
	        													   st.get_codec().get_height() * st.get_sample_aspect_ratio().get_den(),
	        													   1024 * 1024);
	        System.out.print(", PAR " + st.get_sample_aspect_ratio().get_num() + 
	        				 ":" + st.get_sample_aspect_ratio().get_den() +
	        				 " DAR " + display_aspect_ratio.get_den() +
	        				 ":" + display_aspect_ratio.get_num());
	    }
	    
	    if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO){
	        if ( (st.get_avg_frame_rate().get_den() != 0) && 
	        		(st.get_avg_frame_rate().get_num() != 0) )
	            print_fps(st.get_avg_frame_rate().to_double(), "fps");
	        if ( (st.get_r_frame_rate().get_den() != 0) && 
	        		(st.get_r_frame_rate().get_num() != 0) )
	            print_fps(st.get_r_frame_rate().to_double(), "tbr");
	        if ( (st.get_time_base().get_den() != 0) && 
	        		(st.get_time_base().get_num() != 0) )
	            print_fps(1/st.get_time_base().to_double(), "tbn");
	        if ( (st.get_codec().get_time_base().get_den() != 0) && 
	        		(st.get_codec().get_time_base().get_num() != 0) )
	            print_fps(1/st.get_codec().get_time_base().to_double(), "tbc");
	    }
	    
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_DEFAULT) == AVFormat.AV_DISPOSITION_DEFAULT)
	    	System.out.print(" (default)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_DUB) == AVFormat.AV_DISPOSITION_DUB)
	    	System.out.print(" (dub)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_ORIGINAL) == AVFormat.AV_DISPOSITION_ORIGINAL)
	    	System.out.print(" (original)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_COMMENT) == AVFormat.AV_DISPOSITION_COMMENT)
	    	System.out.print(" (comment)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_LYRICS) == AVFormat.AV_DISPOSITION_LYRICS)
	    	System.out.print(" (lyrics)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_KARAOKE) == AVFormat.AV_DISPOSITION_KARAOKE)
	    	System.out.print(" (karaoke)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_FORCED) == AVFormat.AV_DISPOSITION_FORCED)
	    	System.out.print(" (forced)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_HEARING_IMPAIRED) == AVFormat.AV_DISPOSITION_HEARING_IMPAIRED)
	    	System.out.print(" (hearing impaired)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_VISUAL_IMPAIRED) == AVFormat.AV_DISPOSITION_VISUAL_IMPAIRED)
	    	System.out.print(" (visual impaired)");
	    if ( (st.get_disposition() & AVFormat.AV_DISPOSITION_CLEAN_EFFECTS) == AVFormat.AV_DISPOSITION_CLEAN_EFFECTS)
	    	System.out.print(" (clean effects)");
	    
	    System.out.print("\n");
	    st.get_metadata().dump("    ");
	}
	

	public String avcodec_string(AVCodecContext enc, boolean encode) {
		AVCodec p;
		String codec_name;
		String profile = "";
		
		if (encode)
			p = AVCodec.find_encoder(enc.get_codec_id());
		else
			p = AVCodec.find_decoder(enc.get_codec_id());
			
		if (p!= null) {
			codec_name = p.get_name();
			profile = p.av_get_profile_name(enc.get_profile());
		} else if (enc.get_codec_id() == CodecID.CODEC_ID_MPEG2TS) {
	        // fake mpeg2 transport stream codec (currently not registered) 
	        codec_name = "mpeg2ts";
		} else if (enc.get_codec_name() != "") {
	        codec_name = enc.get_codec_name();
		} else {
			// output avi tags
			String tag = enc.get_codec_tag_string();
			codec_name = String.format("%s / 0x%04X", tag, enc.get_codec_tag());
		}
		
		String buf = "";
		
		switch (enc.get_codec_type()) {
			case AVMEDIA_TYPE_VIDEO:
				buf += "Video: " + codec_name + ((enc.get_mb_decision() != 0) ? " (hq)" : "");
				
				if (profile != "") 
					buf += "(" + profile + ")";
				
				if (enc.get_pix_fmt() != PixelFormat.PIX_FMT_NONE) 
					buf += ", " + PixDesc.av_get_pix_fmt_name(enc.get_pix_fmt());
				
				if (enc.get_width() != 0) {
					buf += String.format(", %dx%d", enc.get_width(), enc.get_height());

					if (enc.get_sample_aspect_ratio().get_num() != 0) {
						AVRational display_aspect_ratio = AVRational.av_reduce(enc.get_width() * enc.get_sample_aspect_ratio().get_num(),
												    						   enc.get_height() * enc.get_sample_aspect_ratio().get_den(),
												    						   1024 * 1024);
						buf += "[PAR " + enc.get_sample_aspect_ratio().get_num() + ":" +
								enc.get_sample_aspect_ratio().get_den();
						buf += " DAR " + display_aspect_ratio.get_num() + ":" +
								display_aspect_ratio.get_den() + "]"; 
					}
					
		            if (Log.av_log_get_level() >= Log.AV_LOG_DEBUG){
		                int g = Mathematics.av_gcd(enc.get_time_base().get_num(), enc.get_time_base().get_den());
		                buf += String.format(", %d/%d", enc.get_time_base().get_num() / g, enc.get_time_base().get_den() / g);
		            }
				}
		        if (encode) {
		        	buf += String.format(", q=%d-%d", enc.get_qmin(), enc.get_qmax());
		        }
		        
				break;

			case AVMEDIA_TYPE_AUDIO:
				buf += "Audio: " + codec_name + ((enc.get_mb_decision() != 0) ? " (hq)" : "");
				
				if (profile != "") 
					buf += "(" + profile + ")";

				if (enc.get_sample_rate() != 0) 
					buf += ", " + enc.get_sample_rate() + " Hz";
				
				buf += ", ";
				
				buf += AudioConvert.av_get_channel_layout_string(enc.get_channels(), enc.get_channel_layout());

				if (enc.get_sample_fmt() != AVSampleFormat.AV_SAMPLE_FMT_NONE) 
					buf += ", " + SampleFmt.av_get_sample_fmt_name(enc.get_sample_fmt());
				break;
				
			case AVMEDIA_TYPE_DATA:
				buf += "Data " + codec_name;
				break;
				
			case AVMEDIA_TYPE_SUBTITLE:
				buf += "Subtitle " + codec_name;
				break;
				
			case AVMEDIA_TYPE_ATTACHMENT:
				buf += "Attachment " + codec_name;
				break;
				
			default:
				buf += "Invalid Codec type " + enc.get_codec_type();
				return buf;
		}
		
	    if (encode) {
	        if (enc.has_flag(AVCodec.CODEC_FLAG_PASS1) )
	        	buf += ", pass 1";
	        if (enc.has_flag(AVCodec.CODEC_FLAG_PASS2) )
	        	buf += ", pass 2";
	    }
					
		int bitrate = enc.get_bit_rate();
		if (bitrate != 0) {
			buf += ", " + (bitrate / 1000) + " kb/s";
		}
		
		return buf;

	}

	static void print_fps(double d, String postfix){
	    long v = Mathematics.lrintf(d * 100);
	    if( (v % 100) != 0 ) 
	    	Log.av_log(null, Log.AV_LOG_INFO, ", %3.2f %s", d, postfix);
	    else if ( (v%(100*1000)) != 0 )
	    	Log.av_log(null, Log.AV_LOG_INFO, ", %1.0f %s", d, postfix);
	    else        
	    	Log.av_log(null, Log.AV_LOG_INFO, ", %1.0fk %s", d/1000, postfix);
	}
	
	
	public int avformat_find_stream_info(AVDictionary options) {
		int ret;
		long old_offset = pb.get_reader().position();
		AVPacket pkt;
	    int orig_nb_streams = get_nb_streams();        // new streams might appear, no options for those
				
		for (int i = 0 ; i < get_nb_streams() ; i++) {
			AVStream st = get_stream(i);
			AVCodec codec;
			
			if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_AAC) {
				st.get_codec().set_sample_rate(0);
				st.get_codec().set_frame_size(0);
				st.get_codec().set_channels(0);
			}
			
			if ( (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) ||
				 (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_SUBTITLE) ) {
				if (st.get_codec().get_time_base().get_num() == 0) {
					st.get_codec().set_time_base(st.get_time_base());
				}
			}
			
			codec = AVCodec.avcodec_find_decoder(st.get_codec().get_codec_id());
			if (codec != null) {
				if (codec.has_capabilities(AVCodec.CODEC_CAP_CHANNEL_CONF)) {
					st.get_codec().set_channels(0);
				}
			}
			
	       /* if (st.get_codec().get_codec_type == AVMEDIA_TYPE_SUBTITLE
	                && codec && !st.get_codec().get_codec)
	                avcodec_open(st.get_codec(), codec);
	                */			
			
			if (!st.get_codec().has_codec_parameters()) {
				if ( (codec != null) && (st.get_codec().get_codec() == null) ) {
					UtilsFormat.avcodec_open(st.get_codec(), codec);
				}
			}
			
		}

		for (int i = 0 ; i < get_nb_streams() ; i++) {
			AVStream st = get_stream(i);
			st.get_info().set_last_dts(AVUtil.AV_NOPTS_VALUE);
		}
		
		
		int count = 0;
		int read_size = 0;
		
		for (;;) {
			
			int i = 0;
			for (i = 0 ; i < get_nb_streams() ; i++) {
				AVStream st = get_stream(i);
	        	int fps_analyze_framecount = 20;
	        	
	        	if (!st.get_codec().has_codec_parameters()) {
	        		break;
	        	}
	        	
	        	 if (st.get_time_base().to_double() > 0.0005)
	        		 fps_analyze_framecount *= 2;
	        	 
	             if (get_fps_probe_size() >= 0)
	                 fps_analyze_framecount = get_fps_probe_size();
	        	
	        	
	             if ( (st.get_codec().tb_unreliable() != 0) && 
	            	  !( (st.get_r_frame_rate().get_num() != 0) && (st.get_avg_frame_rate().get_num() != 0) ) &&
	            	  (st.get_info().get_duration_count() < fps_analyze_framecount) &&
	            	  (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) )	 {
	            	 break;
	             }
	             
	             if (st.get_parser() != null) {
	            	 if (st.get_codec().get_extradata() == null) {
	            		 break;
	            	 }
	             }
	             
	             if (st.get_first_dts() == AVUtil.AV_NOPTS_VALUE ) {
	            	 break;
	             }
			}
			
			if (i == get_nb_streams()) {
				if ( (get_ctx_flags() & AVFormat.AVFMTCTX_NOHEADER) == AVFormat.AVFMTCTX_NOHEADER) {
					ret = count;
					break;
				}
			}
			
			if (read_size >= get_probesize()) {
				ret = count;
				break;
			}
			
			OutOI res = av_read_frame_internal();
			ret = res.get_ret();
			pkt = (AVPacket) res.get_obj();
			
			if (ret == Error.AVERROR(Error.EAGAIN))
				continue;
			
			if (ret < 0) {
				ret = -1;
				
				for (i = 0 ; i < get_nb_streams() ; i++) {
					AVStream st = get_stream(i);
 					if (!st.get_codec().has_codec_parameters()) {
 						String buf = UtilsCodec.avcodec_string(st.get_codec(), false);
 						Log.av_log("formatCtx", Log.AV_LOG_WARNING, "Could not find codec parameters (%s)\n",
 								buf);
 					} else {
 						ret = 0;
 					}
				}
				break;				
			}
			
			add_to_pktbuf(pkt);
			read_size += pkt.get_size();
						
			AVStream st = streams.get(pkt.get_stream_index());
			
			if (st.get_codec_info_nb_frames() > 1) {
				long t = Mathematics.av_rescale_q(st.get_info().get_codec_info_duration(), 
												  st.get_time_base(), 
												  AVUtil.AV_TIME_BASE_Q);
				
				if ( (st.get_time_base().get_den() > 0) && (t >= get_max_analyze_duration()) ) {
					Log.av_log("formatCtx", Log.AV_LOG_WARNING, "max_analyze_duration %d reached at %d\n", 
							get_max_analyze_duration(), t);
					break;
				}
				st.get_info().set_codec_info_duration(st.get_info().get_codec_info_duration() + pkt.get_duration());
			}
			
			{
				long last = st.get_info().get_last_dts();
				long duration = pkt.get_dts() - last;
				
				if ( (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) &&
					 (last != AVUtil.AV_NOPTS_VALUE) &&
					 (duration > 0) ) {
					double dur = duration * Mathematics.av_q2d(st.get_time_base());
					
					if (st.get_info().get_duration_count() < 2) {
						st.get_info().reset_duration_error();
					}
					
					for (i = 1 ; i < st.get_info().get_duration_error().length ; i++) {
						int framerate = UtilsFormat.get_std_framerate(i);
						int ticks = (int)Math.rint(dur * framerate / (1001*12));
						double error = dur - ticks * 1001 * 12 / (double) framerate;
						st.get_info().set_duration_error(i, st.get_info().get_duration_error(i) + error * error);
					}
					st.get_info().set_duration_count(st.get_info().get_duration_count()+1);
					
	                // ignore the first 4 values, they might have some random jitter
	                if (st.get_info().get_duration_count() > 3)
	                    st.get_info().set_duration_gcd(Mathematics.av_gcd(st.get_info().get_duration_gcd(), duration));
				}
				if ( (last == AVUtil.AV_NOPTS_VALUE) || (st.get_info().get_duration_count() <= 1) ) {
					st.get_info().set_last_dts(pkt.get_dts());
				}
			}
			
			if (st.get_parser() != null) {
				if (st.get_codec().get_extradata() != null) {
					i = st.get_parser().get_parser().split(st.get_codec(), pkt.get_data());
					if (i != 0) {
						byte [] extradata = new byte[i + AVCodec.FF_INPUT_BUFFER_PADDING_SIZE];
						System.arraycopy(pkt.get_data(), 0, extradata, 0, i);
						st.get_codec().set_extradata(extradata);
					}
				}
			}
			
			
			 /* if still no information, we try to open the codec and to
	           decompress the frame. We try to avoid that in most cases as
	           it takes longer and uses more memory. For MPEG-4, we need to
	           decompress for QuickTime.

	           If CODEC_CAP_CHANNEL_CONF is set this will force decoding of at
	           least one frame of codec data, this makes sure the codec initializes
	           the channel configuration and does not only trust the values from the container.
	        */
			// TODO: options = multidimensional array ?
	        try_decode_frame(st, pkt, ( (options != null) && (i <= orig_nb_streams) ) ? options/*[i]*/ : null);

	        st.set_codec_info_nb_frames(st.get_codec_info_nb_frames() + 1);
	        count++;		
			
		}		
		
		for (AVStream st : streams) {
			if (st.get_codec().get_codec() != null) {
				UtilsCodec.avcodec_close(st.get_codec());
			}
		}
		

		for (AVStream st : streams) {
			if ( (st.get_codec_info_nb_frames() > 2) &&
				 (st.get_avg_frame_rate().get_num() == 0) &&
				 (st.get_info().get_codec_info_duration() != 0) ) {
					 st.get_avg_frame_rate().reduce((st.get_codec_info_nb_frames()-2) * st.get_time_base().get_den(), 
							 				 	    st.get_info().get_codec_info_duration() * st.get_time_base().get_num(), 
							 						60000);
				 }
			
			if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				if ( (st.get_codec().get_codec_id() == CodecID.CODEC_ID_RAWVIDEO) &&
					 (st.get_codec().get_codec_tag() == 0) &&
					 (st.get_codec().get_bits_per_coded_sample() == 0) ) {
					int tag = Raw.avcodec_pix_fmt_to_codec_tag(st.get_codec().get_pix_fmt());
					if (RawDec.ff_find_pix_fmt(Raw.ff_raw_pix_fmt_tags, tag) == st.get_codec().get_pix_fmt())
						st.get_codec().set_codec_tag(tag);					
				}

	            // the check for tb_unreliable() is not completely correct, since this is not about handling
	            // a unreliable/inexact time base, but a time base that is finer than necessary, as e.g.
	            // ipmovie.c produces.
				if ( (st.get_codec().tb_unreliable() != 0) && 
					 (st.get_info().get_duration_count() > 15) && 
					 (st.get_info().get_duration_gcd() > Mathematics.FFMAX(1, st.get_time_base().get_den() / (500 * st.get_time_base().get_num()))) && 
					 (st.get_r_frame_rate().get_num() == 0) ) {
	                st.get_r_frame_rate().reduce(st.get_time_base().get_den(), st.get_time_base().get_num() * st.get_info().get_duration_gcd(), Integer.MAX_VALUE);
				}
				
				if ( (st.get_info().get_duration_count() != 0) && 
					 (st.get_r_frame_rate().get_num() == 0) && 
					 (st.get_codec().tb_unreliable() != 0) ) {
					int num = 0;
					double best_error = 2 * st.get_time_base().to_double();
					best_error = best_error * best_error * st.get_info().get_duration_count() * 1000 * 12 * 30;
					
					for (int j = 1 ; j < st.get_info().get_duration_error().length ; j++) {
						double error = st.get_info().get_duration_error()[j] * UtilsFormat.get_std_framerate(j);
						if (error < best_error) {
							best_error = error;
							num = UtilsFormat.get_std_framerate(j);
						}
					}
	                // do not increase frame rate by more than 1 % in order to match a standard rate.
	                if ( (num != 0) && 
	                	 ( (st.get_r_frame_rate().get_num() == 0) || 
	                	   ((double)num/(12*1001) < 1.01 * st.get_r_frame_rate().to_double()) ) )
	                	st.get_r_frame_rate().reduce(num, 12 * 1001, Integer.MAX_VALUE);					
				}
				
				if (st.get_r_frame_rate().get_num() == 0) {
					if (st.get_codec().get_time_base().get_den() * st.get_time_base().get_num() 
						<= st.get_codec().get_time_base().get_num() * st.get_codec().get_ticks_per_frame() * st.get_time_base().get_den()) {
						st.get_r_frame_rate().set_num(st.get_codec().get_time_base().get_den());
						st.get_r_frame_rate().set_den(st.get_codec().get_time_base().get_num() * st.get_codec().get_ticks_per_frame());
					} else {
						st.get_r_frame_rate().set_num(st.get_time_base().get_den());
						st.get_r_frame_rate().set_den(st.get_time_base().get_num());
					}					
				}
			} else if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
				if (st.get_codec().get_bits_per_coded_sample() == 0) {
					st.get_codec().set_bits_per_coded_sample(UtilsCodec.av_get_bits_per_sample(st.get_codec().get_codec_id()));
				}
			  switch (st.get_codec().get_audio_service_type()) {
				case AV_AUDIO_SERVICE_TYPE_EFFECTS:
					st.set_disposition(AVFormat.AV_DISPOSITION_CLEAN_EFFECTS);
					break;
				case AV_AUDIO_SERVICE_TYPE_VISUALLY_IMPAIRED:
					st.set_disposition(AVFormat.AV_DISPOSITION_VISUAL_IMPAIRED);
					break;
				case AV_AUDIO_SERVICE_TYPE_HEARING_IMPAIRED:
					st.set_disposition(AVFormat.AV_DISPOSITION_HEARING_IMPAIRED);
					break;
				case AV_AUDIO_SERVICE_TYPE_COMMENTARY:
					st.set_disposition(AVFormat.AV_DISPOSITION_COMMENT);
					break;
				case AV_AUDIO_SERVICE_TYPE_KARAOKE:
					st.set_disposition(AVFormat.AV_DISPOSITION_KARAOKE);
					break;
				}
			}
			
		}
		
		
		
	    av_estimate_timings(old_offset);
	    return ret;
	}

	private int try_decode_frame(AVStream st, AVPacket avpkt, AVDictionary options) {
		int ret;
		AVCodec codec;
		AVFrame picture;
		
		
		if (st.get_codec().get_codec() == null) {
			codec = AVCodec.avcodec_find_decoder(st.get_codec().get_codec_id());
			if (codec == null)
				return -1;
			ret = st.get_codec().avcodec_open2(codec);
			if (ret < 0)
				return ret;
		}
		
		if ( (!st.get_codec().has_codec_parameters()) ||
		     (!st.has_decode_delay_been_guessed()) ||
		     ( (st.get_codec_info_nb_frames() == 0) && (st.get_codec().get_codec().has_capabilities(AVCodec.CODEC_CAP_CHANNEL_CONF))) ) {
			switch (st.get_codec().get_codec_type()) {
			case AVMEDIA_TYPE_VIDEO:
				picture = UtilsCodec.avcodec_get_frame_defaults();

				OutOI ret_obj =  st.get_codec().avcodec_decode_video2(avpkt);
				picture = (AVFrame) ret_obj.get_obj();
				ret = ret_obj.get_ret();
			}
		}
		
		
		
		return 0;
	}





	private void av_estimate_timings(long old_offset) {
		long file_size;
		
		if ( iformat.has_flag(AVFormat.AVFMT_NOFILE) ) {
			file_size = 0;
		} else {
			file_size = pb.get_reader().size();
	        if (file_size < 0)
	            file_size = 0;
		}
		
		this.file_size = file_size;
		
		if (    ( (iformat.get_name().equals("mpeg")) || (iformat.get_name().equals("mpegts")) ) 
			 && (file_size != 0) ) {
			// get accurate estimate from the PTSes 
	        av_estimate_timings_from_pts(old_offset);
		} else if (av_has_duration()) {
			// at least one component has timings - we use them for all
	        // the components 
	        fill_all_stream_timings();
		} else {
			Log.av_log("formatCtx", Log.AV_LOG_WARNING, 
					"Estimating duration from bitrate, this may be inaccurate\n");
	        // less precise: use bitrate info 
	        av_estimate_timings_from_bit_rate();
		}
	
	    av_update_stream_timings();
		
	}

	private void av_update_stream_timings() {	
	    long start_time, start_time1, start_time_text, end_time, end_time1;
	    long duration, duration1;
	    int i;
	    AVStream st;

	    start_time = Long.MAX_VALUE;
	    start_time_text = Long.MAX_VALUE;
	    end_time = Long.MIN_VALUE;
	    duration = Long.MIN_VALUE;
	    for (i = 0;i < get_nb_streams() ; i++) {
	        st = get_stream(i);
	        if (st.get_start_time() != AVUtil.AV_NOPTS_VALUE && 
	        	st.get_time_base().get_den() != 0) {
	            start_time1 = Mathematics.av_rescale_q(st.get_start_time(), 
	            									   st.get_time_base(), 
	            									   AVUtil.AV_TIME_BASE_Q);
	            if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_DVB_TELETEXT) {
	                if (start_time1 < start_time_text)
	                    start_time_text = start_time1;
	            } else if (start_time1 < start_time)
	                start_time = start_time1;
	            if (st.get_duration() != AVUtil.AV_NOPTS_VALUE) {
	                end_time1 = start_time1
	                          + Mathematics.av_rescale_q(st.get_duration(), 
	                        		  st.get_time_base(), AVUtil.AV_TIME_BASE_Q);
	                if (end_time1 > end_time)
	                    end_time = end_time1;
	            }
	        }
	        if (st.get_duration() != AVUtil.AV_NOPTS_VALUE) {
	            duration1 = Mathematics.av_rescale_q(st.get_duration(), 
	            		st.get_time_base(), AVUtil.AV_TIME_BASE_Q);
	            if (duration1 > duration)
	                duration = duration1;
	        }
	    }
	    if (start_time == Long.MAX_VALUE || 
	    	(start_time > start_time_text && start_time - start_time_text < AVUtil.AV_TIME_BASE))
	        start_time = start_time_text;
	    if (start_time != Long.MAX_VALUE) {
	        this.start_time = start_time;
	        if (end_time != Long.MIN_VALUE) {
	            if (end_time - start_time > duration)
	                duration = end_time - start_time;
	        }
	    }
	    if (duration != Long.MIN_VALUE) {
	        this.duration = duration;
	        if (this.get_file_size() > 0) {
	            /* compute the bitrate */
	            this.bit_rate = (long) ((double)this.get_file_size() * 8.0 * AVUtil.AV_TIME_BASE /
	                (double)this.duration);
	        }
	    }	
	}

	private void av_estimate_timings_from_bit_rate() {
		long filesize, duration;
	    int bit_rate = 0;

	    // if bit_rate is already set, we believe it
	    if (this.bit_rate <= 0) {
	        bit_rate = 0;
	        for (AVStream st: streams) {	        	
	            if ( st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO && 
            		 st.get_codec().get_bit_rate() > 0)
	            	bit_rate += st.get_codec().get_bit_rate();
	        }
	        this.bit_rate = bit_rate;
	    }

	    // if duration is already set, we believe it
	    if (this.duration == AVUtil.AV_NOPTS_VALUE &&
	        this.bit_rate != 0 &&
	        this.file_size != 0)  {
	        filesize = this.file_size;
	        if (filesize > 0) {
	            for (AVStream st : streams) {
	                duration = Mathematics.av_rescale(8 * filesize, 
	                		              st.get_time_base().get_den(), 
	                		              bit_rate * (long)st.get_time_base().get_num());
	                if (st.get_duration() == AVUtil.AV_NOPTS_VALUE) {
	                    st.set_duration(duration);
	                }
	            }
	        }
	    }		
	}

	private void av_estimate_timings_from_pts(long old_offset) {
		
	}

	private void fill_all_stream_timings() {
		
	}

	private boolean av_has_duration() {
		for (AVStream st: streams) {
			if (st.get_duration() != AVUtil.AV_NOPTS_VALUE)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "AVFormatContext [iformat=" + iformat
				+ ", oformat=" + oformat + ", pb="
				+ pb + ", streams=" + streams + ", uri=" + filename + ", timestamp="
				+ timestamp + ", ctx_flags=" + ctx_flags + ", packet_buffer="
				+ ", start_time=" + start_time
				+ ", duration=" + duration + ", file_size=" + file_size
				+ ", bit_rate=" + bit_rate + ", cur_st=" + cur_st
				+ ", data_offset=" + data_offset + ", mux_rate=" + mux_rate
				+ ", packet_size=" + packet_size + ", preload=" + preload
				+ ", max_delay=" + max_delay + ", loop_output=" + loop_output
				+ ", flags=" + flags + ", loop_input=" + loop_input
				+ ", probesize=" + probesize + ", max_analyze_duration="
				+ max_analyze_duration + ", key=" + Arrays.toString(key)
				+ ", programs=" + Arrays.toString(programs)
				+ ", video_codec_id=" + video_codec_id + ", audio_codec_id="
				+ audio_codec_id + ", subtitle_codec_id=" + subtitle_codec_id
				+ ", max_index_size=" + max_index_size
				+ ", max_picture_buffer=" + max_picture_buffer + ", chapters="
				+ ", debug=" + debug
				+ ", raw_packet_buffer=" + raw_packet_buffer
				+ ", raw_packet_buffer_end=" + raw_packet_buffer_end
				+ ", packet_buffer_end=" + packet_buffer_end + ", metadata="
				+ metadata + ", raw_packet_buffer_remaining_size="
				+ raw_packet_buffer_remaining_size + ", start_time_realtime="
				+ start_time_realtime + ", fps_probe_size=" + fps_probe_size
				+ ", ts_id=" + ts_id + ", width=" + width + ", height="
				+ height + ", pos_first_frame=" + pos_first_frame + "]";
	}
	
	public void print_options() {
		av_class.print_options();
	}



	public void add_flag(int flag) {	
		this.flags |= flag;
		
	}





	public int init_input(URI uri) {
		AVProbeData pd = new AVProbeData(uri);
		
		if (this.get_pb() != null) {
			add_flag(AVFormat.AVFMT_FLAG_CUSTOM_IO);
			if (iformat == null) {
				this.set_iformat(this.get_pb().av_probe_input_buffer(this, pd, 0, 0));
				if (this.get_iformat() == null)
					return -1;
				else
					return 0;
			}
		}
		
		if (get_iformat() != null) {
			if (get_iformat().has_flag(AVFormat.AVFMT_NOFILE)) {
				return 0;
			}				
		} else {
			set_iformat(pd.av_probe_input_format(false));
			if (get_iformat() != null) {
				return 0;
			}
		}
		this.set_pb(new AVIOContext(pd.getReader()));

		this.set_iformat(this.get_pb().av_probe_input_buffer(this, pd, 0, 0));
		
		if (this.get_iformat() == null)
			return -1;
		else
			return 0;
	}	
	


	private void avformat_get_context_defaults() {
		set_av_class(new AVFormatContextClass());
		av_opt_set_defaults();		
	}


	public void av_close_input_file() {
		// TODO
	}


	public void avformat_free_context() {
		// TODO Not Implemented
		/*int i;
	    AVStream *st;

	    av_opt_free(s);
	    if (s.get_iformat && s.get_iformat.get_priv_class && s.get_priv_data)
	        av_opt_free(s.get_priv_data);

	    for(i=0;i<s.get_nb_streams;i++) {
	        // free all data in a stream component 
	        st = s.get_streams[i];
	        if (st.get_parser) {
	            av_parser_close(st.get_parser);
	            av_free_packet(&st.get_cur_pkt);
	        }
	        av_dict_free(&st.get_metadata);
	        av_free(st.get_index_entries);
	        av_free(st.get_codec().get_extradata);
	        av_free(st.get_codec().get_subtitle_header);
	        av_free(st.get_codec());
	        av_free(st.get_priv_data);
	        av_free(st.get_info);
	        av_free(st);
	    }
	    for(i=s.get_nb_programs-1; i>=0; i--) {
	        av_dict_free(&s.get_programs[i].get_metadata);
	        av_freep(&s.get_programs[i].get_stream_index);
	        av_freep(&s.get_programs[i]);
	    }
	    av_freep(&s.get_programs);
	    av_freep(&s.get_priv_data);
	    while(s.get_nb_chapters--) {
	        av_dict_free(&s.get_chapters[s.get_nb_chapters].get_metadata);
	        av_free(s.get_chapters[s.get_nb_chapters]);
	    }
	    av_freep(&s.get_chapters);
	    av_dict_free(&s.get_metadata);
	    av_freep(&s.get_streams);
	    av_free(s);*/
	}


	public void set_filename_int(String filename) {
		this.filename_int = filename;
		
	}

	public int av_write_header() {
		return av_write_header(null);
	}


	private int av_write_header(AVDictionary options) {
	    int ret = 0, i;
	    AVStream st;
	    AVDictionary tmp = null;

	    if (options != null)
	    	tmp = options.av_dict_copy();
	    if ((ret = av_opt_set_dict(tmp)) < 0)
	        return ret;
	    

	    // some sanity checks
	    if ( (get_nb_streams() == 0) &&
	    	 !(get_oformat().has_flag(AVFormat.AVFMT_NOSTREAMS)) ) {
	        Log.av_log("formatCtx", Log.AV_LOG_ERROR, "no streams\n");
	        ret = Error.AVERROR(Error.EINVAL);
	        return ret;
	    }
	    

	    for (i = 0 ; i < get_nb_streams() ; i++) {
	        st = get_streams().get(i);

	        switch (st.get_codec().get_codec_type()) {
	        case AVMEDIA_TYPE_AUDIO:
	            if (st.get_codec().get_sample_rate() <= 0){
	                Log.av_log("formatCtx", Log.AV_LOG_ERROR, "sample rate not set");
	                ret = Error.AVERROR(Error.EINVAL);
	    	        return ret;
	            }
	            if (st.get_codec().get_block_align() == 0)
	                st.get_codec().set_block_align(st.get_codec().get_channels() *
	                    UtilsCodec.av_get_bits_per_sample(st.get_codec().get_codec_id()) >> 3);
	            break;
	        case AVMEDIA_TYPE_VIDEO:
	            if ( (st.get_codec().get_time_base().get_num() <= 0) || 
	            	 (st.get_codec().get_time_base().get_den() <=0) ) { //FIXME audio too?
	            	Log.av_log("formatCtx", Log.AV_LOG_ERROR, "time base not set");
	                ret = Error.AVERROR(Error.EINVAL);
	    	        return ret;
	            }
	            if ( ((st.get_codec().get_width() <= 0) || (st.get_codec().get_height() <= 0)) && 
	            	 !(get_oformat().has_flag(AVFormat.AVFMT_NODIMENSIONS)) ) {
	            	Log.av_log("formatCtx", Log.AV_LOG_ERROR, "dimensions not set");
	                ret = Error.AVERROR(Error.EINVAL);
	    	        return ret;
	            }
	            if (st.get_sample_aspect_ratio().av_cmp_q(st.get_codec().get_sample_aspect_ratio()) != 0){
	            	Log.av_log("formatCtx", Log.AV_LOG_ERROR, "Aspect ratio mismatch between encoder and muxer layer");
	                ret = Error.AVERROR(Error.EINVAL);
	    	        return ret;
	            }
	            break;
	        }

	        if (get_oformat().get_codec_tag() != null) {
	            if ( (st.get_codec().get_codec_tag() != 0) && 
	            	 (st.get_codec().get_codec_id() == CodecID.CODEC_ID_RAWVIDEO) && 
	            	 (UtilsFormat.av_codec_get_tag(get_oformat().get_codec_tag(), st.get_codec().get_codec_id()) == 0) && 
	            	 (UtilsFormat.validate_codec_tag(this, st) == 0) ){
	                //the current rawvideo encoding system ends up setting the wrong codec_tag for avi, we override it here
	                st.get_codec().set_codec_tag(0);
	            }
	            if (st.get_codec().get_codec_tag() != 0){
	                if (UtilsFormat.validate_codec_tag(this, st) == 0) {
	                    String tagbuf = UtilsCodec.av_get_codec_tag_string(st.get_codec().get_codec_tag());
	                    Log.av_log("formatCtx", Log.AV_LOG_ERROR,
	                           String.format("Tag %s/0x%08x incompatible with output codec id '%d'",
	                           tagbuf, st.get_codec().get_codec_tag(), st.get_codec().get_codec_id()) );
	                    ret = Error.AVERROR_INVALIDDATA;
	        	        return ret;
	                }
	            } else {
	                st.get_codec().set_codec_tag(UtilsFormat.av_codec_get_tag(get_oformat().get_codec_tag(), st.get_codec().get_codec_id()));
	            }
	        }

	        if ( get_oformat().has_flag(AVFormat.AVFMT_GLOBALHEADER) &&
	            !(st.get_codec().has_flag(AVCodec.CODEC_FLAG_GLOBAL_HEADER)) )
	        	Log.av_log("formatCtx", Log.AV_LOG_WARNING, "Codec for stream " + i + " does not use global headers but container format requires global headers");
	    }

	 /*   if (!s.get_priv_data && s.get_oformat.get_priv_data_size > 0) {
	        s.get_priv_data = av_mallocz(s.get_oformat.get_priv_data_size);
	        if (!s.get_priv_data) {
	            ret = AVERROR(ENOMEM);
	            goto fail;
	        }
	        if (s.get_oformat.get_priv_class) {
	            *(const AVClass**)s.get_priv_data= s.get_oformat.get_priv_class;
	            av_opt_set_defaults(s.get_priv_data);
	            if ((ret = av_opt_set_dict(s.get_priv_data, &tmp)) < 0)
	                goto fail;
	        }
	    }*/

	    /* set muxer identification string */
	    if ( (get_nb_streams() != 0) && 
	    	 !(get_stream(0).get_codec().has_flag(AVCodec.CODEC_FLAG_BITEXACT)) ) {
	        get_metadata().av_dict_set("encoder", Version.LIBAVFORMAT_IDENT, 0);
	    }
	    
	    ret = get_oformat().write_header(this);
        if (ret < 0)
            return ret;
	    


        /* init PTS generation */
        for(i = 0 ; i < get_nb_streams() ; i++) {
            long den = AVUtil.AV_NOPTS_VALUE;
            st = get_stream(i);

            switch (st.get_codec().get_codec_type()) {
            case AVMEDIA_TYPE_AUDIO:
                den = (long)st.get_time_base().get_num() * st.get_codec().get_sample_rate();
                break;
            case AVMEDIA_TYPE_VIDEO:
                den = (long)st.get_time_base().get_num() * st.get_codec().get_time_base().get_den();
                break;
            default:
                break;
            }
            if (den != AVUtil.AV_NOPTS_VALUE) {
                if (den <= 0) {
                    ret = Error.AVERROR_INVALIDDATA;
                    return ret;
                }
                st.get_pts().av_frac_init(0, 0, den);
            }
        }
	    
		return 0;
	}


	public int av_interleaved_write_frame(AVPacket pkt) {  
		AVStream st = get_stream(pkt.get_stream_index());
		
	    int ret;
	
	    //FIXME/XXX/HACK drop zero sized packets
	    if ( (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) && 
	    	 (pkt.get_size() == 0) )
	        return 0;
	
	    Log.av_dlog("formatCtx", "av_interleaved_write_frame size:%d dts:%d pts:%d\n",
	            pkt.get_size(), pkt.get_dts(), pkt.get_pts());
	    ret = compute_pkt_fields2(st, pkt);
	    if ( (ret < 0) && 
	    	 (!get_oformat().has_flag(AVFormat.AVFMT_NOTIMESTAMPS)) )
	        return ret;
	
	    if ( (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE) && 
	    	 (!get_oformat().has_flag(AVFormat.AVFMT_NOTIMESTAMPS)) )
	        return Error.AVERROR(Error.EINVAL);
	
	    for(;;){
	    	OutOI ret_obj = av_interleave_packet(pkt, 0);
	        AVPacket opkt = (AVPacket) ret_obj.get_obj();
	        ret = ret_obj.get_ret();
	        if (ret <= 0) //FIXME cleanup needed for ret<0 ?
	            return ret;
	
	        ret = get_oformat().write_packet(this, opkt);
	        if (ret >= 0)
	            get_stream(opkt.get_stream_index()).set_nb_frames(get_stream(opkt.get_stream_index()).get_nb_frames());
	
	        opkt.av_free_packet();
	        pkt = null;
	
	        if (ret < 0)
	        	return ret;
	        /*if (url_ferror(s.get_pb))
	            return url_ferror(s.get_pb);*/
	    }
	
	
	
		
	
	}


	/**
	 * Interleave an AVPacket correctly so it can be muxed.
	 * @param out the interleaved packet will be output here
	 * @param in the input packet
	 * @param flush 1 if no further packets are available as input and all
	 *              remaining packets should be output
	 * @return 1 if a packet was output, 0 if no packet could be output,
	 *         < 0 if an error occurred
	 */
	private OutOI av_interleave_packet(AVPacket in, int flush) {
    	OutOI ret_obj = get_oformat().interleave_packet(this, in, flush);
    	int ret = ret_obj.get_ret();        
	    if (ret != -1)
	        return ret_obj;
	    else
	        return av_interleave_packet_per_dts(in, flush);
	}


	private OutOI av_interleave_packet_per_dts(AVPacket pkt, int flush) {
	    AVPacketList pktl;
	    AVPacket out;
	    int stream_count = 0;
	    int i;

	    if (pkt != null) {
	        ff_interleave_add_packet(pkt);
	    }

	   /* TODO jerome
	      for (i = 0 ; i < get_nb_streams() ; i++)
	        stream_count += get_stream(i).get_last_in_packet_buffer(); 

	    if(stream_count && (s.get_nb_streams == stream_count || flush)){ */
	        pktl = packet_buffer;
	        out = pktl.get_pkt();

	        packet_buffer = pktl.get_next();
	        if(packet_buffer == null)
	            packet_buffer_end = null;

	        if (get_stream(out.get_stream_index()).get_last_in_packet_buffer() == pktl)
	            get_stream(out.get_stream_index()).set_last_in_packet_buffer(null);

		    return new OutOI(out, 1);
	    /*}else{
	        av_init_packet(out);
	        return 0;
	    }*/
	    
	    //return new OutOI(null, -1);
	}

	private boolean ff_interleave_compare_dts(AVPacket next, AVPacket pkt)	{
	    AVStream st  = get_stream(pkt.get_stream_index());
	    AVStream st2 = get_stream(next.get_stream_index());
	    int comp = Mathematics.av_compare_ts(next.get_dts(), st2.get_time_base(), 
	    				pkt.get_dts(), st.get_time_base());

	    if (comp == 0)
	        return pkt.get_stream_index() < next.get_stream_index();
	    return comp > 0;
	}


	private void ff_interleave_add_packet(AVPacket pkt) {
	    AVPacketList next_point = null, this_pktl;

	    this_pktl = new AVPacketList();
	    this_pktl.set_pkt(pkt);
	    pkt.set_destruct("");             // do not free original but only the copy
	    //av_dup_packet(this_pktl.get());  // duplicate the packet if it uses non-alloced memory

	    if (get_stream(pkt.get_stream_index()).get_last_in_packet_buffer() != null) {
	       // next_point = get_stream(pkt.get_stream_index()).get_last_in_packet_buffer().next());
	    } else
	        next_point = packet_buffer;

	    if (next_point != null) {
	        if (ff_interleave_compare_dts(packet_buffer_end.get_pkt(), pkt)) {
	            while (!ff_interleave_compare_dts(next_point.get_pkt(), pkt)) {
	                next_point= next_point.get_next();
	            }
	            this_pktl.set_next(next_point);
	            get_stream(pkt.get_stream_index()).set_last_in_packet_buffer(this_pktl);
	            next_point = this_pktl;
	            return;
	        } else {
	            next_point = packet_buffer_end.get_next();
	        }
	    }

	    packet_buffer_end = this_pktl;
        this_pktl.set_next(next_point);
        get_stream(pkt.get_stream_index()).set_last_in_packet_buffer(this_pktl);
        next_point = this_pktl;		
        packet_buffer = next_point;		
	}


	//FIXME merge with compute_pkt_fields
	private int compute_pkt_fields2(AVStream st, AVPacket pkt) {  
		int delay = (int) Mathematics.FFMAX(st.get_codec().get_has_b_frames(), st.get_codec().get_max_b_frames());
	    int frame_size, i;
	    AVRational r;
	
	    Log.av_dlog("formatCtx", String.format("compute_pkt_fields2: pts:%d dts:%d cur_dts:%d b:%d size:%d st:%d\n",
	            pkt.get_pts(), pkt.get_dts(), st.get_cur_dts(), delay, pkt.get_size(), pkt.get_stream_index()));
	
	/*    if(pkt.get_pts == AVUtil.AV_NOPTS_VALUE && pkt.get_dts == AVUtil.AV_NOPTS_VALUE)
	        return AVERROR(EINVAL);*/
	
	    /* duration field */
	    if (pkt.get_duration() == 0) {
	        r = st.compute_frame_duration(null, pkt);
	        if ( (r.get_den() != 0) && (r.get_num() != 0) ) {
	            pkt.set_duration((int) Mathematics.av_rescale(1, 
	            		r.get_num() * (long)st.get_time_base().get_den() * st.get_codec().get_ticks_per_frame(), 
	            		r.get_den() * (long)st.get_time_base().get_num()));
	        }
	    }
	
	    if ( (pkt.get_pts() == AVUtil.AV_NOPTS_VALUE) && 
	    	 (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (delay == 0) )
	        pkt.set_pts(pkt.get_dts());
	
	    //XXX/FIXME this is a temporary hack until all encoders output pts
	    if ( ( (pkt.get_pts() == 0) || (pkt.get_pts() == AVUtil.AV_NOPTS_VALUE) ) && 
	    	 (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE) && 
	    	 (delay == 0) ) {
	        pkt.set_dts(st.get_pts().get_val());
	//        pkt.set_pts(st.get_cur_dts());
	        pkt.set_pts(st.get_pts().get_val());
	    }
	
	    //calculate dts from pts
	    if ( (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (pkt.get_dts() == AVUtil.AV_NOPTS_VALUE) && 
	    	 (delay <= AVStream.MAX_REORDER_DELAY) ){
	        st.set_pts_buffer(0, pkt.get_pts());
	        for (i = 1 ; (i < delay + 1) && (st.get_pts_buffer(i) == AVUtil.AV_NOPTS_VALUE) ; i++)
	            st.set_pts_buffer(i, pkt.get_pts() + (i-delay-1) * pkt.get_duration());
	        for (i = 0 ; (i < delay) && (st.get_pts_buffer(i) > st.get_pts_buffer(i+1)) ; i++)
	            Common.FFSWAP(st.get_pts_buffer(), i, i+1);
	
	        pkt.set_dts(st.get_pts_buffer(0));
	    }
	
	    if ( (st.get_cur_dts() != 0) && 
	    	 (st.get_cur_dts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 ( ((!get_oformat().has_flag(AVFormat.AVFMT_TS_NONSTRICT)) && (st.get_cur_dts() >= pkt.get_dts())) || 
	    	   (st.get_cur_dts() > pkt.get_dts()) ) ){
	        Log.av_log("formatCtx", Log.AV_LOG_ERROR,
	               String.format("Application provided invalid, non monotonically increasing dts to muxer in stream %d: %d >= %d",
	               st.get_index(), st.get_cur_dts(), pkt.get_dts()));
	        return Error.AVERROR(Error.EINVAL);
	    }
	    if ( (pkt.get_dts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (pkt.get_pts() != AVUtil.AV_NOPTS_VALUE) && 
	    	 (pkt.get_pts() < pkt.get_dts()) ){
	        Log.av_log("formatCtx", Log.AV_LOG_ERROR, "pts < dts in stream " + st.get_index());
	        return Error.AVERROR(Error.EINVAL);
	    }
	
	//    av_log(s, AV_LOG_DEBUG, "av_write_frame: pts2:%"PRId64" dts2:%"PRId64"\n", pkt.get_pts, pkt.get_dts);
	    st.set_cur_dts(pkt.get_dts());
	    st.get_pts().set_val(pkt.get_dts());
	
	    /* update pts */
	    switch (st.get_codec().get_codec_type()) {
	    case AVMEDIA_TYPE_AUDIO:
	        frame_size = st.get_codec().get_audio_frame_size(pkt.get_size());
	
	        /* HACK/FIXME, we skip the initial 0 size packets as they are most
	           likely equal to the encoder delay, but it would be better if we
	           had the real timestamps from the encoder */
	        if ( (frame_size >= 0) && 
	        	 ( (pkt.get_size() != 0) || 
	        	   (st.get_pts().get_num() != st.get_pts().get_den() >> 1) || 
	        	   (st.get_pts().get_val() != 0) ) ) {
	        	st.get_pts().av_frac_add((long)st.get_time_base().get_den() * frame_size);
	        }
	        break;
	    case AVMEDIA_TYPE_VIDEO:
	    	st.get_pts().av_frac_add((long)st.get_time_base().get_den() * st.get_codec().get_time_base().get_num());
	        break;
	    default:
	        break;
	    }
	    return 0;
	}


	public void av_update_cur_dts(AVStream ref_st, long timestamp) {
	    int i;

	    for(i = 0 ; i < get_nb_streams() ; i++) {
	        AVStream st = get_stream(i);

	        st.set_cur_dts(Mathematics.av_rescale(timestamp,
	                                 			  st.get_time_base().get_den() * (long)ref_st.get_time_base().get_num(),
	                                 			  st.get_time_base().get_num() * (long)ref_st.get_time_base().get_den()));
	    }
		
	}
}
