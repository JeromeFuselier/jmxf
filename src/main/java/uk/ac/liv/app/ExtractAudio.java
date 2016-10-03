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
 * Creation   : November 2011
 *  
 *****************************************************************************/

package uk.ac.liv.app;

import java.io.File;
import java.util.ArrayList;


import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteWriter;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.util.OutOI;

public class ExtractAudio {


	public static void main(String[] args)  {
		
		//Get the jvm heap size.
		//long heapSize = Runtime.getRuntime().totalMemory();
		//Print the jvm heap size.
		//System.out.println("Heap Size = " + heapSize);

		if ( (args.length != 1) && (args.length != 3) ) {
			System.out.println("Usage: ExtractAudio movie.avi [min nb_frames]");
			return;
		}

		// Register all formats and codecs
		AVFormat.av_register_all();		

		//AVCodec.printCodecs();
		//AVFormat.printFormats();


		// Open video file
		AVFormatContext formatCtx = null;

		formatCtx = AVFormat.av_open_input_file(new File(args[0]).toURI(), null, null);
		
		
		int min = 0;
		int nb_frames = (int)formatCtx.get_duration();
		
		if (args.length == 3) {
			min = Integer.parseInt(args[1]);
			int nb_frames_arg = Integer.parseInt(args[2]);
			
			if ((min + nb_frames_arg) > nb_frames) {
				nb_frames = nb_frames - min;
			} else {
				nb_frames = nb_frames_arg + min;
			}
		}
			

		if (formatCtx == null)
			return; // Couldn't open file

		// Retrieve stream information
		if (formatCtx.avformat_find_stream_info(null) < 0)
			return; // Couldn't find stream information

		formatCtx.dump_format(0, args[0], false);

		int audioStream = -1;
		for (int i = 0 ; i < formatCtx.get_nb_streams() ; i++) {
			if (formatCtx.get_stream(i).get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
				audioStream = i;
				break;
			}
		}

		if (audioStream == -1)
			return ; // Didn't find a video stream

		// Get a pointer to the codec context for the video stream
		AVCodecContext codecCtx = formatCtx.get_stream(audioStream).get_codec();

		AVCodec codec = AVCodec.find_decoder(codecCtx.get_codec_id());

		if (codec == null) {
			System.out.println("Unsupported codec");
			return; // Codec not found
		}

		codecCtx.avcodec_open2(codec);	
		

		long ch =  codecCtx.get_channels();
		long sampleSec = codecCtx.get_sample_rate();
		long bitsSample = codecCtx.get_bits_per_coded_sample();

		formatCtx.av_seek_frame(audioStream, min, 0);
		
		ArrayList<Long []> audioBuffers = new ArrayList<Long[]>();

		int ii = 0;

		while (ii < nb_frames) {
			OutOI res = formatCtx.av_read_frame();
			AVPacket pkt = (AVPacket) res.get_obj();

			if (pkt == null)
				continue;

			if (pkt.get_stream_index() == audioStream) {
				ii++;
				OutOI ret_obj = codecCtx.avcodec_decode_audio(pkt);
				Long [] samples = (Long []) ret_obj.get_obj();
				audioBuffers.add(samples);
			}
		}

		save_audio_buffer(audioBuffers, ch, sampleSec, bitsSample, "tmp.wav");
	}
	

	public static void save_audio_buffer(ArrayList<Long []> audioBuffers, long ch,
			long sampleSec, long bitsSample, String filename) {
		int size = 0;
		for (Long [] array: audioBuffers) {
			size += array.length * 2;
		}

		long bytesSec = ch * sampleSec * bitsSample/ 8;
		long blockAlign = ch * bitsSample / 8;
		
		// 44 = Header size
		ByteWriter writer = new ByteWriter(size + 44);
		
		writer.putString("RIFF");
		writer.putle32(size + 44 - 8);	// Size of file 
			// (not including the "RIFF" and size bytes (-8 bytes)
		writer.putString("WAVE");
		writer.putString("fmt ");
		writer.putle32(16); 			// fmt length
		writer.putle16(0x0001);			// format: WAVE_FORMAT_PCM
		writer.putle16(ch);				// number of channels
		writer.putle32(sampleSec);
		writer.putle32(bytesSec);		
		writer.putle16(blockAlign);		
		writer.putle16(bitsSample);	
		writer.putString("data");
		writer.putle32(size);	
		
		for (Long [] array: audioBuffers) {
			for (Long nb: array) {
				writer.putle16(nb);
			}
		}
		
		writer.dump(filename);
	}

}
