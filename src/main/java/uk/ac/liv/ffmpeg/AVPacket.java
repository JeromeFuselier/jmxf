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

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVPacketSideDataType;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.Error;


public class AVPacket { 	



	public static short [] av_packet_get_side_data(AVPacket avpkt,
			AVPacketSideDataType type) {
		return avpkt.av_packet_get_side_data(type);
	}
	
    long pts;
    long dts;
    short [] data;
    int size;
    int stream_index;
    int flags;
    AVSideData [] side_data = null;
    int side_data_elems;
    int duration;
    Object priv;
    long pos;
    long convergence_duration;
    
    String destruct = "";
    
    public AVPacket() {
    	
    }
    
	public AVPacket(AVPacket pkt) {
		this.pts = pkt.get_pts();
		this.dts = pkt.get_dts();
	    set_data(pkt.get_data());
	    this.stream_index = pkt.get_stream_index();
	    this.flags = pkt.get_flags();
	    this.side_data = pkt.get_side_data();
	    this.side_data_elems = pkt.get_side_data_elems();
	    this.duration = pkt.get_duration();
	    this.priv = pkt.get_priv();
	    this.pos = pkt.get_pos();
	    this.convergence_duration = pkt.get_convergence_duration();
		
	}
    

	public void av_init_packet() {
	    pts   = AVUtil.AV_NOPTS_VALUE;
	    dts   = AVUtil.AV_NOPTS_VALUE;
	    pos   = -1;
	    duration = 0;
	    convergence_duration = 0;
	    flags = 0;
	    stream_index = 0;
	    side_data = null;
	    side_data_elems = 0;		
	}
	
	public int get_size() {
		return size;
	}

	public void set_size(int size) {
		this.size = size;
	}
	
	public boolean has_flag(int flag) {
		return (this.flags & flag) != 0;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public AVSideData [] get_side_data() {
		return side_data;
	}

	public void set_side_data(AVSideData [] side_data) {
		this.side_data = Arrays.copyOf(side_data, side_data.length);
	}

	public int get_side_data_elems() {
		return side_data_elems;
	}

	public void set_side_data_elems(int side_data_elems) {
		this.side_data_elems = side_data_elems;
	}

	public int get_duration() {
		return duration;
	}

	public void set_duration(int duration) {
		this.duration = duration;
	}

	public Object get_priv() {
		return priv;
	}

	public void set_priv(Object priv) {
		this.priv = priv;
	}

	public long get_convergence_duration() {
		return convergence_duration;
	}

	public void set_convergence_duration(long convergence_duration) {
		this.convergence_duration = convergence_duration;
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

	public long get_pos() {
		return pos;
	}

	public void set_pos(long pos) {
		this.pos = pos;
	}

	public short [] get_data() {
		return data;
	}

	public void set_data(short [] data) {
		if (data != null) {
			this.data = Arrays.copyOf(data, data.length);
			this.size = data.length;
		} else {
			this.data = new short[0];
			this.size = 0;
		}
	}

	public int get_stream_index() {
		return stream_index;
	}

	public void set_stream_index(int stream_index) {
		this.stream_index = stream_index;
	}
	
	

	@Override
	public String toString() {
		return "AVPacket [pts=" + pts + ", dts=" + dts
				+ ", size=" + size + ", stream_index="
				+ stream_index + ", flags=" + flags + ", side_data="
				+ side_data + ", side_data_elems=" + side_data_elems
				+ ", duration=" + duration + ", pos=" + pos
				+ ", convergence_duration=" + convergence_duration + "]";
	}

	public void add_flag(int flag) {
		this.flags |= flag;		
	}

	public void av_free_packet() {
	    this.data = null; 
	    this.size = 0;

	    if (this.side_data != null)
	    	for (AVSideData d : this.side_data)
	    		d.set_data(null);
	    this.side_data = null;
	    this.side_data_elems = 0;		
	}
	
	public void set_destruct(String destruct) {
		this.destruct = destruct;
	}
	
	public void destruct() {
		if (destruct.equals("destruct"))
			av_free_packet();
	}

	private short [] av_packet_get_side_data(AVPacketSideDataType type) {
	    for (AVSideData d : this.side_data)
	    	if (d.get_type() == type)
	    		return d.get_data();
		return null;
	}

	public static void av_packet_merge_side_data(AVPacket pkt) {
		pkt.av_packet_merge_side_data();
	}

	public int av_packet_merge_side_data() {
		if(get_side_data_elems() != 0){
	        int i;
	        short [] p;
	        long size = get_size() + 8 + AVCodec.FF_INPUT_BUFFER_PADDING_SIZE;
	        for (i = 0 ; i < get_side_data_elems() ; i++) {
	            size += get_side_data()[i].get_size() + 5;
	        }
	        
	        if (size > Integer.MAX_VALUE)
	            return Error.AVERROR(Error.EINVAL);
	        p = new short[(int)size];
	        
	        set_data(p);
	  /*      bytestream_put_buffer(&p, old.data, old.size);
	        for (i=old.side_data_elems-1; i>=0; i--) {
	            bytestream_put_buffer(&p, old.side_data[i].data, old.side_data[i].size);
	            bytestream_put_be32(&p, old.side_data[i].size);
	            *p++ = old.side_data[i].type | ((i==old.side_data_elems-1)*128);
	        }
	        bytestream_put_be64(&p, FF_MERGE_MARKER);
	        av_assert0(p-pkt->data == pkt->size);
	        memset(p, 0, FF_INPUT_BUFFER_PADDING_SIZE);
	        av_free_packet(&old);
	        pkt->side_data_elems = 0;
	        pkt->side_data = NULL;
	        return 1;*/
	    }
	    return 0;
		
	}


	
	
	
	

}
