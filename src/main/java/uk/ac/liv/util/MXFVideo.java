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

package uk.ac.liv.util;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;


import javax.imageio.ImageIO;


public class MXFVideo {
        
    AVFormatContext formatCtx;
    AVCodecContext codecCtx;
    AVCodec codec;
    
    int vid_w= -1;
	int vid_h = -1;

	
	int videoStream;
	
	boolean isPlaying = false;
	
	BufferedImage currentFrame;
	
	int nextFrame = 40; //1000 / 25;
	
	int a = 0;
	private int nbFrames;
	
	
    
    
    public MXFVideo(String path) {
		initDecoder(path);
		nbFrames = 5;
	}
    
   
    public void previous_frame() {
    	return;
    }
    

        

	private void initDecoder(String path) {
		// Register all formats and codecs
		AVFormat.av_register_all();

		// Open video file
		try {
			formatCtx = AVFormat.av_open_input_file(new URI(path), null, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		
		formatCtx.dump_format(0, path, false);
		
		videoStream = -1;
		for (int i = 0 ; i < formatCtx.get_nb_streams() ; i++) {
			if (formatCtx.get_stream(i).get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				videoStream = i;
				break;
			}
		}
		// Get a pointer to the codec context for the video stream
		codecCtx = formatCtx.get_stream(videoStream).get_codec();
		// Find the corresponding decoder
		codec = AVCodec.find_decoder(codecCtx.get_codec_id());
		
		if (codec == null) {
			System.out.println("Unsupported codec");
			return; // Codec not found
		}
					
		codecCtx.avcodec_open2(codec);		
	}
	
	private BufferedImage getNextFrame() {		
		boolean found = false;
		BufferedImage im = null;
		
		while (!found) {

			OutOI res = formatCtx.av_read_frame();
			AVPacket pkt = (AVPacket) res.get_obj();
			//long el = t.elapsed();
//			
//			if (el > nextFrame) {
//				System.out.println("DROP\n");
//				nextFrame += 40;
//				continue;
//			}
//			System.out.println(el + "/" + nextFrame);
//			
//			a++;
//			if (a != 1) {
//				if (a == 5)
//					a = 0;
//				continue;
//			}
				
			
			
			if (pkt == null) {
				found = true;  // End of the file
			}		

			if (pkt.get_stream_index() == videoStream) {
				found = true;

				AVFrame picture = UtilsCodec.avcodec_get_frame_defaults();
				OutOI ret_obj = codecCtx.avcodec_decode_video2(pkt);
				picture = (AVFrame) ret_obj.get_obj();
		
				int [] img = codec.get_display_output().showScreen();
				vid_w = 720;
				vid_h = 608;				
		
				im = new BufferedImage(vid_w, vid_h, BufferedImage.TYPE_INT_RGB);
		
				int xx = 0;
				int yy = 0;
				for (int i = 0 ; i < img.length ; i++) {
					im.setRGB(xx, yy, img[i]);
		
					xx++;
		
					if (xx >= vid_w) {
						xx = 0;
						yy++;
					}
				}
				nextFrame = 40;
			}
		}

		currentFrame = im;
		return im;		
	}

	
	public BufferedImage getCurrentFrame() {
		return currentFrame;
	}

	public int getNbFrames() {
		return nbFrames;
	}

	public BufferedImage getFrame(int i) {
		return getNextFrame();
	}	
	
	

}
