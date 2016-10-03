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

package uk.ac.liv.app;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.util.OutOI;

public class ExtractFrames {


	public static void main(String[] args)  {
		
		//Get the jvm heap size.
		//long heapSize = Runtime.getRuntime().totalMemory();
		//Print the jvm heap size.
		//System.out.println("Heap Size = " + heapSize);

		if ( (args.length != 1) && (args.length != 3) ) {
			System.out.println("Usage: ExtractFrames movie.avi [min nb_frames]");
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
			

		if (formatCtx == null) {
			return; // Couldn't open file
		}

		// Retrieve stream information
		if (formatCtx.avformat_find_stream_info(null) < 0)
			return; // Couldn't find stream information

		formatCtx.dump_format(0, args[0], false);

		int videoStream = -1;
		for (int i = 0 ; i < formatCtx.get_nb_streams() ; i++) {
			if (formatCtx.get_stream(i).get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				videoStream = i;
				break;
			}
		}

		if (videoStream == -1)
			return ; // Didn't find a video stream

		// Get a pointer to the codec context for the video stream
		AVCodecContext codecCtx = formatCtx.get_stream(videoStream).get_codec();

		AVCodec codec = AVCodec.find_decoder(codecCtx.get_codec_id());

		if (codec == null) {
			System.out.println("Unsupported codec");
			return; // Codec not found
		}

		codecCtx.avcodec_open2(codec);
		
		int vid_w = codecCtx.get_width();
		int vid_h = codecCtx.get_height();				

		formatCtx.av_seek_frame(videoStream, min, 0);

		int ii = 0;

		while (ii < nb_frames) {
			OutOI res = formatCtx.av_read_frame();
			AVPacket pkt = (AVPacket) res.get_obj();

			if (pkt == null)
				continue;

			if (pkt.get_stream_index() == videoStream) {
				ii++;

				AVFrame picture = UtilsCodec.avcodec_get_frame_defaults();
				OutOI ret_obj = codecCtx.avcodec_decode_video2(pkt);
				picture = (AVFrame) ret_obj.get_obj();

				int [] img = codec.get_display_output().showScreen();

				BufferedImage bimage = new BufferedImage(vid_w, vid_h, BufferedImage.TYPE_INT_RGB);

				int xx = 0;
				int yy = 0;
				for (int i = 0 ; i < img.length ; i++) {
					bimage.setRGB(xx, yy, img[i]);

					xx++;

					if (xx >= vid_w) {
						xx = 0;
						yy++;
					}
				}

				try {
					File outputfile = new File("tmp"+(min+ii-1)+".png");
					ImageIO.write(bimage, "png", outputfile);
					System.out.println((min+ii) + "/" + nb_frames);
				} catch (IOException e) {
				}	
			}
		}




	}

}
