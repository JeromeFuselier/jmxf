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

package uk.ac.liv.ffmpeg.libavcodec.pcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt32;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.UtilsArrays;

public class PCM extends AVCodec {
	
	public static final int MAX_CHANNELS = 64;
	
	AVCodecContext avctx;
	
	public PCM() {
		super();
		this.type = AVMediaType.AVMEDIA_TYPE_AUDIO;
		this.decode = true;
		this.has_priv_data = true;
	}
	

	public int init(AVCodecContext avctx) {
		this.avctx = avctx;
		
		avctx.set_sample_fmt(avctx.get_codec().get_sample_fmt(0));
	    if (avctx.get_sample_fmt() == AVSampleFormat.AV_SAMPLE_FMT_S32)
	        avctx.set_bits_per_raw_sample(UtilsCodec.av_get_bits_per_sample(avctx.get_codec().get_id()));

		return 0;
	}

	
	public OutOI decodeAudio(AVCodecContext avctx, AVPacket avpkt) {
		short [] src = avpkt.get_data();
		int buf_size = avpkt.get_size();
	    PCMDecode s = (PCMDecode) avctx.get_priv_data();
	    int sample_size, c, n;
	    long [] samples = new long[0];
			
	    if (avctx.get_sample_fmt() != avctx.get_codec().get_sample_fmt(0)) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "invalid sample_fmt\n");
	        return new OutOI(new long[0], -1);
	    }

	    if (avctx.get_channels() <= 0 || avctx.get_channels() > MAX_CHANNELS){
	    	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "PCM channels out of bounds\n");
	        return new OutOI(new long[0], -1);
	    }

	    sample_size = UtilsCodec.av_get_bits_per_sample(avctx.get_codec_id()) / 8;
	    
	    /* av_get_bits_per_sample returns 0 for CODEC_ID_PCM_DVD */
	    if (CodecID.CODEC_ID_PCM_DVD == avctx.get_codec_id())
	        /* 2 samples are interleaved per block in PCM_DVD */
	        sample_size = avctx.get_bits_per_coded_sample() * 2 / 8;
	    else if (avctx.get_codec_id() == CodecID.CODEC_ID_PCM_LXF)
	        /* we process 40-bit blocks per channel for LXF */
	        sample_size = 5;

	    if (sample_size == 0) {
	        Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "Invalid sample_size\n");
	        return new OutOI(new long[0], Error.AVERROR(Error.EINVAL));
	    }

	    n = avctx.get_channels() * sample_size;

	    if (n != 0 && (buf_size % n) != 0) {
	        if (buf_size < n) {
	        	Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "invalid PCM packet\n");
	            return new OutOI(new long[0], -1);
	        } else {
	            buf_size -= buf_size % n;
	        }
	    }

	    n = buf_size / sample_size;

	    switch (avctx.get_codec().get_id()) {
	    
	    case CODEC_ID_PCM_S24LE:
			ArrayList<Long> output = new ArrayList<Long>();
			for (int idx_src = 0 ; idx_src < src.length ; idx_src += 3) {
				long sample = (long) (src[idx_src+2] << 16) + 
				               (long) (src[idx_src+1] <<  8) + 
				               (long) (src[idx_src]);
				output.add((sample >> 8) & 0xffff);					
			}

			samples = new long[output.size()];
			for (int i = 0 ; i < samples.length ; i ++)
				samples[i] = output.get(i);
			
	        //DECODE(int32_t, le24, src, samples, n, 8, 0)
	        break;

	    case CODEC_ID_PCM_F64LE:
	    case CODEC_ID_PCM_F32LE:
	    case CODEC_ID_PCM_S32LE:
	    case CODEC_ID_PCM_S16LE:
	    case CODEC_ID_PCM_U8:
	    	//samples = UtilsArrays.byte_to_short_le_sl(Arrays.copyOfRange(src, n * sample_size, src.length));
	    	samples = UtilsArrays.byte_to_short_le_sl(src);

//	        memcpy(samples, src, n*sample_size);
//	        samples = 
//	        src += n*sample_size;
//	        samples = (short*)((uint8_t*)data + n*sample_size);
	    	break;
	    default:
	        return new OutOI(new long[0], -1);
	    }
	   
	    

		/*ByteReader reader;
		
		try {			
			reader = new ByteReader(avpkt.get_data());
		
			// skip SMPTE 331M header 
			byte[] t = reader.read_bytes(4);
			
			//mxf.setAudioCodec(stream.get_codec());
			
			while (reader.remaining() > 0) {
				for (i = 0 ; i < avctx.get_channels() ; i++) {
	
					UInt32 sample = reader.read_UInt32le();		
					
					switch(avctx.get_codec().get_id()) {
				    case CODEC_ID_PCM_F64LE:
				    case CODEC_ID_PCM_F32LE:
				    case CODEC_ID_PCM_S32LE:
				    case CODEC_ID_PCM_S16LE:
						output.add((sample.toInt() >> 12) & 0xffff);
						break;
					}
	
					/*if ( avctx.get_bits_per_coded_sample() == 24) {
						//bytestream_put_le24(&data_ptr,   0xffffff);
						output.add((sample.toInt() >> 4) & 0xffffff);
					} else {
						//writer.putle16((sample.toInt() >> 12) & 0xffff);
						output.add((sample.toInt() >> 12) & 0xffff);
						//	bytestream_put_le16(&data_ptr, (sample >> 12) & 0xffff);
					}*/
	
			/*	}
	
				// always 8 channels stored SMPTE 331M
				reader.read_bytes(32 - avctx.get_channels() * 4);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} */
		
		/*(long) 
		return new OutOI(samples, 0);*/
		return new OutOI(samples, 0);
	}
	
	

}
