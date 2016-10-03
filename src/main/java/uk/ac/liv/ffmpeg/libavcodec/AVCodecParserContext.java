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
 * Creation   : March 2012
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavcodec;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.util.OutOI;

public class AVCodecParserContext {
	
	public static int AV_PARSER_PTS_NB = 4;

	public static int PARSER_FLAG_COMPLETE_FRAMES = 0x0001;
	public static int PARSER_FLAG_ONCE            = 0x0002;
	/// Set if the parser has a valid file offset
	public static int PARSER_FLAG_FEt_cHED_OFFSET  = 0x0004;


	public static ArrayList<AVCodecParser> av_first_parser = new ArrayList<AVCodecParser>();
	

	public static void av_register_codec_parser(AVCodecParser parser) {
		av_first_parser.add(0, parser);
	}


	public static AVCodecParserContext av_parser_init(CodecID codec_id) {
	    AVCodecParserContext s;
	    int ret;

	    if(codec_id == CodecID.CODEC_ID_NONE)
	        return null;

	    for (AVCodecParser parser : av_first_parser) {
	        if ( (parser.get_codec_id(0) == codec_id) ||
	        	 (parser.get_codec_id(1) == codec_id) ||
	        	 (parser.get_codec_id(2) == codec_id) ||
	        	 (parser.get_codec_id(3) == codec_id) ||
	        	 (parser.get_codec_id(4) == codec_id) ) {
	        	s = new AVCodecParserContext();
			    s.set_parser(parser);
			    s.alloc_priv_data();
			  	
		        ret = parser.parser_init(s);
		        if (ret != 0)
		            return null;
		        
			    s.set_fetch_timestamp(1);
			    s.set_pict_type(AVPictureType.AV_PICTURE_TYPE_I);
			    s.set_key_frame(-1);
			    s.set_convergence_duration(0);
			    s.set_dts_sync_point(Integer.MIN_VALUE);
			    s.set_dts_ref_dts_delta(Integer.MIN_VALUE);
			    s.set_pts_dts_delta(Integer.MIN_VALUE);
			    return s;
	        }
	    }
	    return null;	    
	}
	
	
	Object priv_data;
	AVCodecParser parser;
	long frame_offset; /* offset of the current frame */
	long cur_offset; /* current offset
	                    (incremented by each av_parser_parse()) */
	long next_frame_offset; /* offset of the next frame */
	/* video info */
	AVPictureType pict_type = AVPictureType.AV_PICTURE_TYPE_NONE; /* XXX: Put it back in AVCodecContext. */
	/**
	 * This field is used for proper frame duration computation in lavf.
	 * It signals, how much longer the frame duration of the current frame
	 * is compared to normal frame duration.
	 *
	 * frame_duration = (1 + repeat_pict) * time_base
	 *
	 * It is used by codecs like H.264 to display telecined material.
	 */
	int repeat_pict; /* XXX: Put it back in AVCodecContext. */
	long pts;     /* pts of the current frame */
	long dts;     /* dts of the current frame */

	/* private data */
	long last_pts;
	long last_dts;
	int fetch_timestamp;

    int cur_frame_start_index;
    long [] cur_frame_offset = new long [AV_PARSER_PTS_NB];
    long [] cur_frame_pts = new long [AV_PARSER_PTS_NB];
    long [] cur_frame_dts = new long [AV_PARSER_PTS_NB];

    int flags;
    long offset;      ///< byte offset from starting packet start
    long [] cur_frame_end = new long [AV_PARSER_PTS_NB];

    /**
     * Set by parser to 1 for key frames and 0 for non-key frames.
     * It is initialized to -1, so if the parser doesn't set this flag,
     * old-style fallback using AV_PICTURE_TYPE_I picture type as key frames
     * will be used.
     */
    int key_frame;

    /**
     * Time difference in st_ream time base units from the pts of this
     * packet to the point at which the out_put from the decoder has converged
     * independent from the availability of previous frames. That is, the
     * frames are virtually identical no matter if decoding started from
     * the very first frame or from this keyframe.
     * Is AV_NOPTS_VALUE if unknown.
     * This field is not the display duration of the current frame.
     * This field has no meaning if the packet does not have AV_PKT_FLAG_KEY
     * set.
     *
     * The purpose of this field is to allow seeking in st_reams that have no
     * keyframes in the conventional sense. It corresponds to the
     * recovery point SEI in H.264 and mat_ch_time_delta in NUT. It is also
     * essential for some types of subtit_le st_reams to ensure that all
     * subtit_les are correct_ly displayed after seeking.
     */
    long convergence_duration;

    // Timestamp generation support:
    /**
     * Synchronization point for start of timestamp generation.
     *
     * Set to >0 for sync point, 0 for no sync point and <0 for undefined
     * (default).
     *
     * For example, this corresponds to presence of H.264 buffering period
     * SEI message.
     */
    int dts_sync_point;

    /**
     * Offset of the current timestamp against last timestamp sync point in
     * units of AVCodecContext.time_base.
     *
     * Set to INT_MIN when dts_sync_point unused. Otherwise, it must
     * contain a valid timestamp offset.
     *
     * Note that the timestamp of sync point has usually a nonzero
     * dts_ref_dts_delta, which refers to the previous sync point. Offset of
     * the next frame after timestamp sync point will be usually 1.
     *
     * For example, this corresponds to H.264 cpb_removal_delay.
     */
    int dts_ref_dts_delta;

    /**
     * Presentation delay of current frame in units of AVCodecContext.time_base.
     *
     * Set to INT_MIN when dts_sync_point unused. Otherwise, it must
     * contain valid non-negative timestamp delta (presentation time of a frame
     * must not lie in the past).
     *
     * This delay represents the difference between decoding and presentation
     * time of the frame.
     *
     * For example, this corresponds to H.264 dpb_out_put_delay.
     */
    int pts_dts_delta;

    /**
     * Position of the packet in file.
     *
     * Analogous to cur_frame_pts/dts
     */
    long [] cur_frame_pos = new long[AV_PARSER_PTS_NB];

    /**
     * Byte position of current_ly parsed frame in st_ream.
     */
    long pos;

    /**
     * Previous frame byte position.
     */
    long last_pos;

	public Object get_priv_data() {
		return priv_data;
	}

	public void set_priv_data(Object priv_data) {
		this.priv_data = priv_data;
	}

	public AVCodecParser get_parser() {
		return parser;
	}

	public void set_parser(AVCodecParser parser) {
		this.parser = parser;
	}

	public long get_frame_offset() {
		return frame_offset;
	}

	public void set_frame_offset(long frame_offset) {
		this.frame_offset = frame_offset;
	}

	public long get_cur_offset() {
		return cur_offset;
	}

	public void set_cur_offset(long cur_offset) {
		this.cur_offset = cur_offset;
	}

	public long get_next_frame_offset() {
		return next_frame_offset;
	}

	public void set_next_frame_offset(long next_frame_offset) {
		this.next_frame_offset = next_frame_offset;
	}

	public AVPictureType get_pict_type() {
		return pict_type;
	}

	public void set_pict_type(AVPictureType pict_type) {
		this.pict_type = pict_type;
	}

	public int get_repeat_pict() {
		return repeat_pict;
	}

	public void set_repeat_pict(int repeat_pict) {
		this.repeat_pict = repeat_pict;
	}

	public long get_pts() {
		return pts;
	}

	public void set_pts(long pts) {
		this.pts = pts;
	}

	public long get_dts() {
		return dts;
	}

	public void set_dts(long dts) {
		this.dts = dts;
	}

	public long get_last_pts() {
		return last_pts;
	}

	public void set_last_pts(long last_pts) {
		this.last_pts = last_pts;
	}

	public long get_last_dts() {
		return last_dts;
	}

	public void set_last_dts(long last_dts) {
		this.last_dts = last_dts;
	}

	public int get_fetch_timestamp() {
		return fetch_timestamp;
	}

	public void set_fetch_timestamp(int fetch_timestamp) {
		this.fetch_timestamp = fetch_timestamp;
	}

	public int get_cur_frame_start_index() {
		return cur_frame_start_index;
	}

	public void set_cur_frame_start_index(int cur_frame_start_index) {
		this.cur_frame_start_index = cur_frame_start_index;
	}

	public long[] get_cur_frame_offset() {
		return cur_frame_offset;
	}

	public void set_cur_frame_offset(long[] cur_frame_offset) {
		this.cur_frame_offset = cur_frame_offset;
	}

	public long[] get_cur_frame_pts() {
		return cur_frame_pts;
	}

	public void set_cur_frame_pts(long[] cur_frame_pts) {
		this.cur_frame_pts = cur_frame_pts;
	}

	public long[] get_cur_frame_dts() {
		return cur_frame_dts;
	}

	public void set_cur_frame_dts(long[] cur_frame_dts) {
		this.cur_frame_dts = cur_frame_dts;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public long get_offset() {
		return offset;
	}

	public void set_offset(long offset) {
		this.offset = offset;
	}

	public long[] get_cur_frame_end() {
		return cur_frame_end;
	}

	public void set_cur_frame_end(long[] cur_frame_end) {
		this.cur_frame_end = cur_frame_end;
	}

	public int get_key_frame() {
		return key_frame;
	}

	public void set_key_frame(int key_frame) {
		this.key_frame = key_frame;
	}

	public long get_convergence_duration() {
		return convergence_duration;
	}

	public void set_convergence_duration(long convergence_duration) {
		this.convergence_duration = convergence_duration;
	}

	public int get_dts_sync_point() {
		return dts_sync_point;
	}

	public void set_dts_sync_point(int dts_sync_point) {
		this.dts_sync_point = dts_sync_point;
	}

	public int get_dts_ref_dts_delta() {
		return dts_ref_dts_delta;
	}

	public void set_dts_ref_dts_delta(int dts_ref_dts_delta) {
		this.dts_ref_dts_delta = dts_ref_dts_delta;
	}

	public int get_pts_dts_delta() {
		return pts_dts_delta;
	}

	public void set_pts_dts_delta(int pts_dts_delta) {
		this.pts_dts_delta = pts_dts_delta;
	}

	public long[] get_cur_frame_pos() {
		return cur_frame_pos;
	}

	public void set_cur_frame_pos(long[] cur_frame_pos) {
		this.cur_frame_pos = cur_frame_pos;
	}

	public long get_pos() {
		return pos;
	}

	public void set_pos(long pos) {
		this.pos = pos;
	}

	public long get_last_pos() {
		return last_pos;
	}

	public void set_last_pos(long last_pos) {
		this.last_pos = last_pos;
	}

	public boolean has_flag(int flag) {
		return (this.flags & flag) != 0;
	}

	public OutOI av_parser_parse2(AVCodecContext avctx, short [] bs,
			int buf_size, long pts, long dts, long pos) {
		int index, i;
		short [] dummy_buf = new short[AVCodec.FF_INPUT_BUFFER_PADDING_SIZE];

	    if (!has_flag(AVCodec.PARSER_FLAG_FETCHED_OFFSET)) {
	        set_next_frame_offset(pos);
	        set_cur_offset(pos);
	        this.flags |= AVCodec.PARSER_FLAG_FETCHED_OFFSET;
	    }

	    if (buf_size == 0) {
	        /* padding is always necessary even if EOF, so we add it here */
	        bs = Arrays.copyOf(dummy_buf, dummy_buf.length);
	    } else if (get_cur_offset() + buf_size !=
	               get_cur_frame_end(get_cur_frame_start_index())) { /* skip remainder packets */
	        /* add a new packet descriptor */
	            i = (get_cur_frame_start_index() + 1) & (AVCodec.AV_PARSER_PTS_NB - 1);
	            set_cur_frame_start_index(i);
	            set_cur_frame_offset(i, get_cur_offset());
	            set_cur_frame_end(i, get_cur_offset() + buf_size);
	            set_cur_frame_pts(i, pts);
	            set_cur_frame_dts(i, dts);
	            set_cur_frame_pos(i, pos);
	    }

	    if (get_fetch_timestamp() != 0){
	        set_fetch_timestamp(0);
	        set_last_pts(get_pts());
	        set_last_dts(get_dts());
	        set_last_pos(get_pos());
	        ff_fetch_timestamp(0, 0);
	    }

	    /* WARNING: the returned index can be negative */
	    OutOI ret_obj = get_parser().parser_parse(this, avctx, bs);
	    index = ret_obj.get_ret();
	    short [] poutbuf = (short []) ret_obj.get_obj();
	//av_log(NULL, AV_LOG_DEBUG, "parser: in:%"PRId64", %"PRId64", out:%"PRId64", %"PRId64", in:%d out:%d id:%d\n", pts, dts, s->last_pts, s->last_dts, buf_size, *poutbuf_size, avctx->codec_id);
	    /* update the file pointer */
	    if (poutbuf != null) {
	        /* fill the data for the current frame */
	        set_frame_offset(get_next_frame_offset());

	        /* offset of the next frame */
	        set_next_frame_offset(get_cur_offset() + index);
	        set_fetch_timestamp(1);
	    }
	    if (index < 0)
	        index = 0;
	    set_cur_offset(get_cur_offset() + index);
	    return ret_obj;
	}

	private void ff_fetch_timestamp(int off, int remove) {
	    int i;

	    set_dts(AVUtil.AV_NOPTS_VALUE);
	    set_pts(AVUtil.AV_NOPTS_VALUE);
	    set_pos(-1);
	    set_offset(0);
	    for (i = 0; i < AVCodec.AV_PARSER_PTS_NB; i++) {
	        if ( (get_cur_offset() + off >= get_cur_frame_offset(i)) && 
	        	 ( (get_frame_offset() < get_cur_frame_offset(i)) ||
	               ( (get_frame_offset() == 0) && (get_next_frame_offset() == 0)) ) && // first field/frame
	            //check is disabled  because mpeg-ts doesnt send complete PES packets
	            /*s->next_frame_offset + off <*/  (get_cur_frame_end(i) != 0) ){
	            set_dts(get_cur_frame_dts(i));
	            set_pts(get_cur_frame_pts(i));
	            set_pos(get_cur_frame_pos(i));
	            set_offset(get_next_frame_offset() - get_cur_frame_offset(i));
	            if (remove != 0)
	                set_cur_frame_offset(i, Long.MAX_VALUE);
	            if (get_cur_offset() + off < get_cur_frame_end(i))
	                break;
	        }
	    }
	}

	private long get_cur_frame_pos(int i) {
		return cur_frame_pos[i];
	}

	private long get_cur_frame_offset(int i) {
		return cur_frame_offset[i];
	}

	private long get_cur_frame_pts(int i) {
		return cur_frame_pts[i];
	}

	private long get_cur_frame_dts(int i) {
		return cur_frame_dts[i];
	}

	private void set_cur_frame_pos(int i, long val) {
		this.cur_frame_pos[i] = val;
	}

	private void set_cur_frame_dts(int i, long val) {
		this.cur_frame_dts[i] = val;
	}

	private void set_cur_frame_end(int i, long val) {
		this.cur_frame_end[i] = val;
	}

	private void set_cur_frame_pts(int i, long val) {
		this.cur_frame_pts[i] = val;
	}

	private void set_cur_frame_offset(int i, long val) {
		this.cur_frame_offset[i] = val;
	}

	private long get_cur_frame_end(int idx) {
		return this.cur_frame_end[idx];
	}


	private void alloc_priv_data() {
		return;		
	}


	public void add_flag(int flag) {
		this.flags |= flag;		
	}
    
    

}
