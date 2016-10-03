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

package uk.ac.liv.ffmpeg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecParserContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;
import uk.ac.liv.ffmpeg.libavformat.AVDictionary;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFrac;
import uk.ac.liv.ffmpeg.libavformat.AVIndexEntry;
import uk.ac.liv.ffmpeg.libavformat.AVProbeData;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.Log;

public class AVStream {
	
	 public static int MAX_REORDER_DELAY = 16;
	 public static int MAX_PROBE_PACKETS = 2500;
	 public static int MAX_STD_TIMEBASES = (60*12+5);
	
	int index;
	int id;
	AVCodecContext codec = new AVCodecContext();
    AVRational r_frame_rate = new AVRational();	// Real frame rate
    
	Object priv_data;
    long first_dts;
    AVFrac pts = new AVFrac();
	AVRational time_base;
	int pts_wrap_bits; 
	boolean stream_copy;
    AVDiscard discard = AVDiscard.AVDISCARD_NONE;
    
    float quality;
    
	long start_time;
	long duration;
	
	Constants.AVStreamParseType need_parsing;
	AVCodecParserContext parser;
	
    long cur_dts;
    int last_IP_duration;
    long last_IP_pts;
    /* av_seek_frame() support */
    ArrayList<AVIndexEntry> index_entries = new ArrayList<AVIndexEntry>(); /**< Only used if the format does not
                                    support seeking natively. */

    long index_entries_allocated_size;
    long nb_frames;
    
    int disposition; 
    
    AVProbeData probe_data;
    
    long [] pts_buffer = new long[MAX_REORDER_DELAY+1];
    
    AVRational sample_aspect_ratio;
        
    AVDictionary metadata = new AVDictionary();
    
    AVPacket cur_pkt;
    short [] cur_ptr;
    int cur_len;
    
    long reference_dts;
    
    int probe_packets;
    
    AVPacketList last_in_packet_buffer;
    
    AVRational avg_frame_rate = new AVRational();	// Average frame rate
    
    int codec_info_nb_frames;
    
    int stream_identifier;
    
    StreamInfo info = new StreamInfo();
    
    int request_probe;
    

	public AVStream(int index, int id) {
		super();
		this.index = index;
		this.id = id;
	}
	
	public float get_quality() {
		return quality;
	}

	public void set_quality(float quality) {
		this.quality = quality;
	}

	public int get_nb_index_entries() {
		return index_entries.size();
	}

	public AVDictionary get_metadata() {
		return metadata;
	}

	public int get_disposition() {
		return disposition;
	}

	public void set_disposition(int disposition) {
		this.disposition = disposition;
	}

	public int get_codec_info_nb_frames() {
		return codec_info_nb_frames;
	}

	public void set_codec_info_nb_frames(int codec_info_nb_frames) {
		this.codec_info_nb_frames = codec_info_nb_frames;
	}

	public AVDiscard get_discard() {
		return discard;
	}

	public void set_discard(AVDiscard discard) {
		this.discard = discard;
	}

	public AVRational get_r_frame_rate() {
		return r_frame_rate;
	}

	public void set_r_frame_rate(AVRational r_frame_rate) {
		this.r_frame_rate = r_frame_rate;
	}

	public AVRational get_avg_frame_rate() {
		return avg_frame_rate;
	}

	public void set_avg_frame_rate(AVRational avg_frame_rate) {
		this.avg_frame_rate = avg_frame_rate;
	}

	public StreamInfo get_info() {
		return info;
	}

	public void set_info(StreamInfo info) {
		this.info = info;
	}

	public Constants.AVStreamParseType get_need_parsing() {
		return need_parsing;
	}

	public void set_need_parsing(Constants.AVStreamParseType need_parsing) {
		this.need_parsing = need_parsing;
	}
	
	
	public void set_index(int index) {
		this.index = index;
	}

	public void set_codec(AVCodecContext codec) {
		this.codec = codec;
	}


	public AVCodecContext get_codec() {
		return codec;
	}
	
	
	public Object get_priv_data() {
		return priv_data;
	}

	public void set_priv_data(Object priv_data) {
		this.priv_data = priv_data;
	}

	public int get_index() {
		return index;
	}
	
	
	public int get_id() {
		return id;
	}


	public void set_id(int id) {
		this.id = id;
	}

	public long get_duration() {
		return duration;
	}


	public void set_duration(long duration) {
		this.duration = duration;
	}


	public long get_start_time() {
		return start_time;
	}


	public void set_start_time(long start_time) {
		this.start_time = start_time;
	}


	public AVRational get_time_base() {
		return time_base;
	}


	public void set_time_base(AVRational time_base) {
		this.time_base = time_base;
	}


	public int get_pts_wrap_bits() {
		return pts_wrap_bits;
	}


	public void set_pts_wrap_bits(int pts_wrap_bits) {
		this.pts_wrap_bits = pts_wrap_bits;
	}


	public long get_cur_dts() {
		return cur_dts;
	}

	public void set_cur_dts(long cur_dts) {
		this.cur_dts = cur_dts;
	}


	public int get_cur_len() {
		return cur_len;
	}

	public void set_cur_len(int cur_len) {
		this.cur_len = cur_len;
	}

	public long get_first_dts() {
		return first_dts;
	}

	public void set_first_dts(long first_dts) {
		this.first_dts = first_dts;
	}

	public int get_probe_packets() {
		return probe_packets;
	}

	public void set_probe_packets(int probe_packets) {
		this.probe_packets = probe_packets;
	}

	public long get_last_IP_pts() {
		return last_IP_pts;
	}

	public void set_last_IP_pts(long last_IP_pts) {
		this.last_IP_pts = last_IP_pts;
	}

	public long get_reference_dts() {
		return reference_dts;
	}

	public void set_reference_dts(long reference_dts) {
		this.reference_dts = reference_dts;
	}

	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}

	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}
	
	public AVFrac get_pts() {
		return pts;
	}

	public void set_pts(AVFrac pts) {
		this.pts = pts;
	}

	public boolean get_stream_copy() {
		return stream_copy;
	}

	public void set_stream_copy(boolean stream_copy) {
		this.stream_copy = stream_copy;
	}

	public AVCodecParserContext get_parser() {
		return parser;
	}

	public void set_parser(AVCodecParserContext parser) {
		this.parser = parser;
	}

	public int get_last_IP_duration() {
		return last_IP_duration;
	}

	public void set_last_IP_duration(int last_IP_duration) {
		this.last_IP_duration = last_IP_duration;
	}

	public AVIndexEntry get_index_entries(int i) {
		return index_entries.get(i);
	}


	public long get_index_entries_allocated_size() {
		return index_entries_allocated_size;
	}

	public void set_index_entries_allocated_size(long index_entries_allocated_size) {
		this.index_entries_allocated_size = index_entries_allocated_size;
	}

	public long get_nb_frames() {
		return nb_frames;
	}

	public void set_nb_frames(long nb_frames) {
		this.nb_frames = nb_frames;
	}

	public AVProbeData get_probe_data() {
		return probe_data;
	}

	public void set_probe_data(AVProbeData probe_data) {
		this.probe_data = probe_data;
	}

	public long[] get_pts_buffer() {
		return pts_buffer;
	}

	public void setPts_buffer(long[] pts_buffer) {
		this.pts_buffer = pts_buffer;
	}

	public AVPacket get_cur_pkt() {
		return cur_pkt;
	}

	public void set_cur_pkt(AVPacket cur_pkt) {
		this.cur_pkt = cur_pkt;
	}

	public AVPacketList get_last_in_packet_buffer() {
		return last_in_packet_buffer;
	}

	public void set_last_in_packet_buffer(AVPacketList last_in_packet_buffer) {
		this.last_in_packet_buffer = last_in_packet_buffer;
	}

	public int get_stream_identifier() {
		return stream_identifier;
	}

	public void set_stream_identifier(int stream_identifier) {
		this.stream_identifier = stream_identifier;
	}

	public int get_request_probe() {
		return request_probe;
	}

	public void set_request_probe(int request_probe) {
		this.request_probe = request_probe;
	}

	@Override
	public String toString() {
		return "AVStream [index=" + index + ", id=" + id + ", privData="
				+ priv_data + ", duration=" + duration + ", startTime="
				+ start_time + ", timeBase=" + time_base + ", ptsWrapBits="
				+ pts_wrap_bits + ", codec=" + codec + ", needParsing="
				+ need_parsing + ", curDts=" + cur_dts + ", firstDts=" + first_dts
				+ ", probePackets=" + probe_packets + ", lastIPPts=" + last_IP_pts
				+ ", referenceDts=" + reference_dts + ", sampleAspectRatio="
				+ sample_aspect_ratio + "]";
	}

	
	public void av_set_pts_info(int pts_wrap_bits, int pts_num, int pts_den) {
		AVRational new_tb = new AVRational(0, 1);
		
		if (new_tb.reduce(pts_num, pts_den, Integer.MAX_VALUE)) {
			if (new_tb.get_num() != pts_num)
	            Log.av_log(null, Log.AV_LOG_DEBUG, "st:%d removing common factor %d from timebase\n", 
	            		index, pts_num / new_tb.get_num());

		} else
			Log.av_log(null, Log.AV_LOG_WARNING, "st:%d has too large timebase, reducing\n", 
					index);

		if ( (new_tb.get_num() <= 0) || (new_tb.get_den() <= 0) ) {
			Log.av_log(null, Log.AV_LOG_ERROR, "Ignoring attempt to set invalid timebase for st:%d\n", 
					index);
			return;
		};
		
		
		this.time_base = new_tb;
		this.pts_wrap_bits = pts_wrap_bits;

	}

	public boolean has_decode_delay_been_guessed() {
		return ( (get_codec().get_codec_id() != CodecID.CODEC_ID_H264) ||
                 (get_codec_info_nb_frames() >= 6 + get_codec().get_has_b_frames()) );
		
	}

	public AVRational compute_frame_duration(AVCodecParserContext pc, AVPacket pkt) {
		int frame_size;

	    int pnum = 0;
	    int pden = 0;
	    switch (codec.get_codec_type()) {
	    case AVMEDIA_TYPE_VIDEO:
	        if (get_time_base().get_num() * 1000 > get_time_base().get_den()) {
	            pnum = time_base.get_num();
	            pden = time_base.get_den();
	        } else if (codec.get_time_base().get_num() * 1000 > codec.get_time_base().get_den()) {
	            pnum = codec.get_time_base().get_num();
	            pden = codec.get_time_base().get_den();
	            
	            if ( (pc != null) && (pc.get_repeat_pict() != 0) ) {
	                pnum *= (1 + pc.get_repeat_pict());
	            }
	            //If this codec can be interlaced or progressive then we need a parser to compute duration of a packet
	            //Thus if we have no parser in such case leave duration undefined.
	            if ( (get_codec().get_ticks_per_frame() > 1) && 
	                 (pc == null) ) {
	                pnum = 0;
	                pden = 0;
	            }
	        }
	        break;
	    case AVMEDIA_TYPE_AUDIO:
	        frame_size = get_codec().get_audio_frame_size(pkt.get_size());
	        if ( (frame_size <= 0) || (get_codec().get_sample_rate() <= 0) )
	            break;
	        pnum = frame_size;
	        pden = get_codec().get_sample_rate();
	        break;
	    default:
	        break;
	    }
		
		return new AVRational(pnum, pden);
	}

	public void set_pts_buffer(int i, long pts) {
		this.pts_buffer[i] = pts;		
	}

	public long get_pts_buffer(int i) {
		return this.pts_buffer[i];
	}

	public void set_index_entries(int i, AVIndexEntry entry) {
		this.index_entries.set(i, entry);		
	}

	public void set_nb_index_entries(int nb) {
		while (this.index_entries.size() > nb)
			this.index_entries.remove(this.index_entries.size() - 1);
	}

	public int av_add_index_entry(long pos, long timestamp, int size, int distance,
			int flags) {
		return ff_add_index_entry(pos, timestamp, size, distance, flags);
		
	}

	private int ff_add_index_entry(long pos, long timestamp, int size,
			int distance, int flags) {
		AVIndexEntry ie;
		
		index_entries.add(new AVIndexEntry());
		
	    index = ff_index_search_timestamp(index_entries, get_nb_index_entries()-1, timestamp, AVFormat.AVSEEK_FLAG_ANY);

	    if (index < 0) {
	        index = get_nb_index_entries();
	    	ie = index_entries.get(index);
	    } else {
	        ie = index_entries.get(index);
	        if (ie.get_timestamp() != timestamp) {
	            if (ie.get_timestamp() <= timestamp)
	                return -1;
	            index_entries.set(index + 1, index_entries.get(index));
	        } else if ( (ie.get_pos() == pos) && 
	        		     (distance < ie.get_min_distance()) ) //do not reduce the distance
	            distance = ie.get_min_distance();
	    }

	    ie.set_pos(pos);
	    ie.set_timestamp(timestamp);
	    ie.set_min_distance(distance);
	    ie.set_size(size);
	    ie.set_flags(flags);

	    return index;
	}

	private int ff_index_search_timestamp(ArrayList<AVIndexEntry> entries, int nb_entries,
			long wanted_timestamp, int flags) {
		int a, b, m;
	    long timestamp;

	    a = - 1;
	    b = nb_entries;

	    //optimize appending index entries at the end
	    if ( (b != 0) && 
	    	 (entries.get(b-1).get_timestamp() < wanted_timestamp) )
	        a = b-1;

	    while (b - a > 1) {
	        m = (a + b) >> 1;
	        timestamp = entries.get(m).get_timestamp();
	        if (timestamp >= wanted_timestamp)
	            b = m;
	        if (timestamp <= wanted_timestamp)
	            a = m;
	    }
	    m = ( (flags & AVFormat.AVSEEK_FLAG_BACKWARD) != 0) ? a : b;

	    if ((flags & AVFormat.AVSEEK_FLAG_ANY) == 0) {
	        while ( (m >= 0) && 
	        		 (m < nb_entries) && 
	        		 ((entries.get(m).get_flags() & AVFormat.AVINDEX_KEYFRAME) == 0) ) {
	            m += ( (flags & AVFormat.AVSEEK_FLAG_BACKWARD) != 0) ? -1 : 1;
	        }
	    }

	    if (m == nb_entries)
	        return -1;
	    return m;
	}

	public short [] get_cur_ptr() {
		return this.cur_ptr;
	}

	public void set_cur_ptr(short [] cur_ptr) {
		if (cur_ptr != null)
			this.cur_ptr = Arrays.copyOf(cur_ptr, cur_ptr.length);
		else
			this.cur_ptr = null;
	}

	
	
	
	
	
	
}
