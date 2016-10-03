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
 * Creation   : September 2011
 *  
 *****************************************************************************/

package uk.ac.liv.app.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVPacketList;
import uk.ac.liv.ffmpeg.Ffmpeg;
import uk.ac.liv.ffmpeg.AVStream;
import uk.ac.liv.ffmpeg.CmdUtils;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormatParameters;
import uk.ac.liv.ffmpeg.libavformat.AVInputFormat;
import uk.ac.liv.ffmpeg.libavformat.UtilsFormat;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteWriter;
import uk.ac.liv.ffmpeg.libavutil.AVOption;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;


public class VideoState {


	public static String getExtension(String s) {
		 String ext = null;
		 int i = s.lastIndexOf('.');

		 if (i > 0 &&  i < s.length() - 1) {
			 ext = s.substring(i+1).toLowerCase();
		 }
		 return ext;
	 }
	
	
	
	public static int MAX_AUDIOQ_SIZE = 5;
	public static int MAX_VIDEOQ_SIZE = 5;
	
	boolean initialized = false;	
	boolean is_playing = false;
	
	URI uri;

    AVFormatContext formatCtx;
    AVCodecContext vCodecCtx;
    AVCodec vCodec;
    AVCodecContext aCodecCtx;
    AVCodec aCodec;
    
    public String video_codec_name    = "";
	public String audio_codec_name = "";
	public String subtitle_codec_name = "";

	int video_index;
	int audio_index;
    
    BufferedImage current_frame;
    BufferedImage current_wave;
    WavImage wave_im;
	
	AVStream audio_stream;
	ArrayList <AVPacket> audioq;
	
	AVStream video_stream;		
	ArrayList <AVPacket> videoq;

	int video_delay;
	
    int cur_frame;
    int ref_frame;
	
	/*
	long current_timestamp;
	long seek_timestamp_ref; // the last seek frame (if we seek at the middle we keep that reference)
	*/
	AVRational frame_rate = new AVRational();
	
	// Video parameters
	int frame_width;
	int frame_height;
	PixelFormat frame_pix_fmt = PixelFormat.PIX_FMT_NONE;
	int video_channel = 0;
	String video_standard = "";
	
	AVRational fps = new AVRational(1, 1); 
	
	// Audio parameters
	int audio_sample_rate;
	int audio_channels;	
	ArrayList <ArrayList<Short>> channelsBuffers;
	ArrayList<Short> maxAudio;
	ArrayList<Short> minAudio;
	ArrayList<long []> audioBuffers;
	
	double video_current_pts;
	double video_current_pts_time; 
	
	 Thread thrDecode;
	
	
	public VideoState(URI uri) {
		super();
		this.uri = uri;

		this.audioq = new ArrayList <AVPacket>();
		this.videoq = new ArrayList <AVPacket>();
		
		channelsBuffers = new ArrayList<ArrayList<Short>> ();
		maxAudio = new ArrayList<Short>();
		minAudio = new ArrayList<Short>();
		audioBuffers = new ArrayList<long[]>();		
	}
	
/*
	public void set_seek_timestamp_ref(long seek_timestamp_ref) {
		this.seek_timestamp_ref = seek_timestamp_ref;
	}
*/

	
	public synchronized void inc_frame() {
		this.cur_frame += 1;
	}
	
	public int get_cur_frame() {
		return this.cur_frame;
	}
	

	public int get_width() {
		return frame_width;
	}


	public int get_height() {
		return frame_height;
	}


	public AVFormatContext getFormatCtx() {
		return formatCtx;
	}


	public void setFormatCtx(AVFormatContext formatCtx) {
		this.formatCtx = formatCtx;
	}


	public int getVideoStream() {
		return video_index;
	}


	public void setVideoStream(int videoStream) {
		this.video_index = videoStream;
	}


	public int getAudioStream() {
		return audio_index;
	}


	public void setAudioStream(int audioStream) {
		this.audio_index = audioStream;
	}


	public AVStream getAudio_st() {
		return audio_stream;
	}


	public void setAudio_st(AVStream audio_st) {
		this.audio_stream = audio_st;
	}


	public ArrayList <AVPacket> getAudioq() {
		return audioq;
	}


	public void setAudioq(ArrayList <AVPacket> audioq) {
		this.audioq = audioq;
	}


	public AVStream getVideo_st() {
		return video_stream;
	}


	public void setVideo_st(AVStream video_st) {
		this.video_stream = video_st;
	}


	public ArrayList <AVPacket> getVideoq() {
		return videoq;
	}


	public void setVideoq(ArrayList <AVPacket> videoq) {
		this.videoq = videoq;
	}
            
    public boolean is_playing() {
    	return is_playing;
    }
   	
    public synchronized void setIsPlaying(boolean isPlaying) {
    	this.is_playing = isPlaying;    		
    }

    
	public int opt_input_file() {		
		AVFormatParameters ap = new AVFormatParameters();
	    AVInputFormat file_iformat = null;
		
		// Open video file
		formatCtx = AVFormatContext.avformat_alloc_context();
			
		ap.set_prealloced_context(1);
		ap.set_sample_rate(audio_sample_rate);
		ap.set_channels(audio_channels);
		ap.set_time_base(frame_rate);
		ap.set_width(frame_width);
		ap.set_width(frame_height);
		ap.set_pix_fmt(frame_pix_fmt);
		ap.set_channel(video_channel);
		ap.set_standard(video_standard);

		Ffmpeg.set_context_opts(formatCtx, CmdUtils.avformat_opts, AVOption.AV_OPT_FLAG_DECODING_PARAM, null);
		
		formatCtx.set_video_codec_id(Ffmpeg.find_codec_or_die(video_codec_name, AVMediaType.AVMEDIA_TYPE_VIDEO, false,
				  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_VIDEO).get_strict_std_compliance()));

		formatCtx.set_audio_codec_id(Ffmpeg.find_codec_or_die(audio_codec_name, AVMediaType.AVMEDIA_TYPE_AUDIO, false,
						  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_AUDIO).get_strict_std_compliance()));
		
		formatCtx.set_audio_codec_id(Ffmpeg.find_codec_or_die(subtitle_codec_name, AVMediaType.AVMEDIA_TYPE_SUBTITLE, false,
						  CmdUtils.avcodec_opts.get(AVMediaType.AVMEDIA_TYPE_SUBTITLE).get_strict_std_compliance()));

		int err = UtilsFormat.av_open_input_file(formatCtx, uri, file_iformat, 0, ap);
		  
		if (err >= 0) {
			//set_context_opts(ic, avformat_opts, AV_OPT_FLAG_DECODING_PARAM, NULL);
			err = UtilsFormat.av_demuxer_open(formatCtx, ap);
    	}
		
	    int ret = formatCtx.avformat_find_stream_info(null);
	    
	    return 0;
	}

	public void init() {
		video_index = -1;
		audio_index = -1;
		
		opt_input_file();
		
		
		for (int i = 0 ; i < formatCtx.get_nb_streams() ; i++) {
			
			// Find the first video stream
			if ( (formatCtx.get_stream(i).get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO)
					&& (video_index == -1) ){
				video_index = i;
			}
			
			// Find the first audio stream
			if ( (formatCtx.get_stream(i).get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO)
					&& (audio_index == -1) ) {
				audio_index = i;
			}
		}
		
		if (video_index >= 0) {
			stream_component_open(video_index);
		}
		
		if (audio_index >= 0) {
			stream_component_open(audio_index);
		}
		
		if ( (video_stream == null) || (audio_stream == null) ) {
			System.out.println(uri.toString() + ": could not open codecs");
			return;
		}
		
		for (int i = 0 ; i < aCodecCtx.get_channels() ; i++) {
			channelsBuffers.add(new ArrayList<Short>());
			maxAudio.add((short)0);
			minAudio.add((short)0);
		}
		
		this.frame_width = vCodecCtx.get_width();
		this.frame_height = vCodecCtx.get_height();
		
		cur_frame = 0;
		ref_frame = 0;
		initialized = true;
		
	}
	
	public AVRational get_time_base() {
		return video_stream.get_time_base();
	}
	
	public float timestamp_to_second(long timestamp) {
		int secs, us;
        secs = (int) (timestamp / AVUtil.AV_TIME_BASE);
        us = (int) (timestamp % AVUtil.AV_TIME_BASE);
		
		return secs + us / AVUtil.AV_TIME_BASE;
	}

	private void stream_component_open(int stream_index) {
		AVCodecContext codecCtx;
		AVCodec codec;
		
		// Bad index
		if ( (stream_index < 0 )|| (stream_index >= formatCtx.get_nb_streams()) ) {
			return;
		}
		
		// Get a pointer to the codec context for the video stream
		codecCtx = formatCtx.get_stream(stream_index).get_codec();
		
		if (codecCtx.get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
			// Set audio settings from codec info
			audio_sample_rate = codecCtx.get_sample_rate();
			audio_channels = codecCtx.get_channels();
		}

		codec = AVCodec.find_decoder(codecCtx.get_codec_id());
		
		codecCtx.avcodec_open2(codec);
		
		if (codec== null) {
			System.out.println("Unsupported codec!");
			return;
		}
		
		switch (codecCtx.get_codec_type()) {
		case AVMEDIA_TYPE_AUDIO:
			audio_index = stream_index;
			audio_stream = formatCtx.get_stream(stream_index);
			aCodecCtx = codecCtx;
			aCodec = codec;
			break;
		case AVMEDIA_TYPE_VIDEO:
			video_index = stream_index;
			video_stream = formatCtx.get_stream(stream_index);
			vCodecCtx = codecCtx;
			vCodec = codec;
			//formatCtx.set_bit_rate(vCodecCtx.get_bit_rate());
			break;
		default:
			break;
		}
		
		
	}


	public boolean full_video_queue() {
		return videoq.size() >= MAX_VIDEOQ_SIZE;
	}


	public boolean full_audio_queue() {
		return audioq.size() >= MAX_AUDIOQ_SIZE;
	}
	
	public int get_sample_rate() {
		return aCodecCtx.get_sample_rate();
	}
	
    // Maybe need some mutex or synchronized method to manage multiple threads.
    private void packet_queue_put(ArrayList <AVPacket> q, AVPacket pkt) {
    	q.add(pkt);
    	
    	if ( q.size() >= 2 ) {
        	long pos1 = q.get(0).get_pos();
    		long pos2 = q.get(1).get_pos();
    		long editUnitByteCount = pos2 - pos1;
    		long size_data = formatCtx.get_pb().get_reader().size() - formatCtx.get_pos_first_frame();
    		/*nb_frames = (int) (size_data / editUnitByteCount);
			formatCtx.set_duration((int)nb_frames);*/
    		
    	}
    	
    	
    }
    
    
    // Maybe need some mutex or synchronized method to manage multiple threads.
    private AVPacket packet_queue_get(ArrayList <AVPacket> q) {
    	boolean hasPkt = (q.size() > 0);
    	long pos = formatCtx.get_pb().get_reader().position();
    	long size = formatCtx.get_pb().get_reader().size();
    	if (pos >= size)
    		return null;
    	
    	while (!hasPkt) {
    		try {
    			Thread.sleep(10);
    		} catch(InterruptedException e) {}
    		hasPkt = (q.size() > 0);
    	}
    	
    	AVPacket pkt = q.get(0);
    	
    	q.remove(0);
    	
    	
    	return pkt;
    }
    
    public void skip_first_frame() {
    	packet_queue_get(videoq);
    }
    
    public void skip_first_sample() {
    	packet_queue_get(audioq);
    }
    
    
	public void decode() {
		while (true) {
			// if quit break ?
			
			if ( full_audio_queue() || full_video_queue()/* || 
			     get_current_timestamp() >= formatCtx.get_duration() */) {
				try {
					Thread.sleep(100); 
				} catch (Exception e) {}  	
				continue;
			} 

			OutOI tmp = UtilsFormat.av_read_frame(formatCtx);
	        int ret = tmp.get_ret();
	        AVPacket pkt = (AVPacket) tmp.get_obj();
			
		//	System.out.println("read_frame + " + pkt.get_pos());
			
			if (pkt == null) {
				continue;//break;
			}
			
			if ( pkt.get_stream_index() == video_index ) {
				packet_queue_put(videoq, pkt);				
			} else if (pkt.get_stream_index() == audio_index) {
			    packet_queue_put(audioq, pkt);
			}
			
			
		}
			
		
	}


	private BufferedImage video_decode_frame(AVPacket pkt) {

		int vid_w = vCodecCtx.get_width();
		int vid_h = vCodecCtx.get_height();				

		BufferedImage im = new BufferedImage(vid_w, vid_h, BufferedImage.TYPE_INT_RGB);
			
		vCodecCtx.avcodec_decode_video2(pkt);
		
        OutOI ret_obj = vCodecCtx.avcodec_decode_video2(pkt);
        int ret = ret_obj.get_ret();
        AVFrame picture = (AVFrame) ret_obj.get_obj();
        
		return picture.get_img();
		
	}
	

	private OutOI audio_decode_frame(AVCodecContext avctx, AVPacket pkt) {
		return aCodecCtx.avcodec_decode_audio(pkt);
	}
	
	
	/*
	public long get_seek_timestamp_ref() {
		return seek_timestamp_ref;
	}
*/

	public BufferedImage getFirstFrame() {
		//set_current_timestamp(get_current_timestamp() + 40000);
		AVPacket pkt = packet_queue_get(videoq);
		if (pkt != null)
			return video_decode_frame(pkt);
		else
			return null;
	}


	public int get_channels() {
		return audio_channels;
	}


	public long [] getFirstSample() {
		AVPacket pkt = packet_queue_get(audioq);
		if (pkt != null) {
			OutOI ret_obj = audio_decode_frame(this.aCodecCtx, pkt);
			long [] samples = (long []) ret_obj.get_obj();
			audioBuffers.add(samples);
			return samples;
		} else
			return new long[0];
	}


	public void addSample(int channel, short sample) {
		channelsBuffers.get(channel).add(sample);		

        if (sample < minAudio.get(channel)) {
        	minAudio.set(channel, sample);
        } else if (sample > maxAudio.get(channel)) {
            maxAudio.set(channel, sample);
        }		
	}


	public ArrayList<Short> getChannel(int i) {
		if (channelsBuffers.size() > 0) 
			return channelsBuffers.get(0);
		else
			return null;
	}


	public short get_min_audio(int channel) {
		return minAudio.get(channel);
	}

	public short get_max_audio(int channel) {
		return maxAudio.get(channel);
	}


	public double get_master_clock() {
		// Maybe change if the audio is the master
		return get_video_clock();
	}


	private double get_video_clock() {
	//	  double delta = (av_gettime() - video_current_pts_time) / 1000000.0;
		 // return video_current_pts + delta;
		return 0;
	}


	public void seek_delta(int inc) {
		seek_frame(cur_frame + inc);		
	}


	public void flush_queues() {
		audioq.clear();
		videoq.clear();

		channelsBuffers.clear();
		maxAudio.clear();
		minAudio.clear();
		audioBuffers.clear();	
		
		for (int i = 0 ; i < aCodecCtx.get_channels() ; i++) {
			channelsBuffers.add(new ArrayList<Short>());
			maxAudio.add((short)0);
			minAudio.add((short)0);
		}
	}

	public void seek_frame(int frame) {
		if (frame < 0)
			frame = 0;
		if (frame > get_nb_frames())
			frame = get_nb_frames() - 1;
		
		this.cur_frame = frame;
		this.ref_frame = frame;
		formatCtx.av_seek_frame(video_index, frame, 0);		

	   	flush_queues();    	
	    decode_next_frame(); 
	}


	public ArrayList<long []> get_audioBuffers() {
		return audioBuffers;
	}
	

	public WavImage get_wave_im() {
		return this.wave_im;
	}


	public long get_bits_per_coded_sample() {
		return aCodecCtx.get_bits_per_coded_sample();
	}


	public ArrayList<String> get_metadata() {
		ArrayList<String> strings = new ArrayList<String>(); 
		
		int hours, mins, secs, us;
        secs = (int) (formatCtx.get_duration() / AVUtil.AV_TIME_BASE);
        us = (int) (formatCtx.get_duration() % AVUtil.AV_TIME_BASE);
        mins = secs / 60;
        secs %= 60;
        hours = mins / 60;
        mins %= 60;
        String duration = String.format("%02d:%02d:%02d.%02d", hours, mins, secs,
                (100 * us) / AVUtil.AV_TIME_BASE);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Duration: ");
			
		sb.append(duration);
		sb.append(", bitrate: ");
		if (formatCtx.get_bit_rate() != 0) {
			sb.append(formatCtx.get_bit_rate() / 1000);
			sb.append(" kb/s");
		} else
			sb.append("N/A");
			
		
		strings.add(sb.toString());
		
		
		for (int i = 0 ; i < formatCtx.get_nb_streams() ; i++) {
			AVStream st = formatCtx.get_stream(i);

			/*int g = Mathematics.av_gcd(st.get_time_base().get_num(), 
									   st.get_time_base().get_den());*/
			
			sb = new StringBuilder();
			sb.append(String.format("Stream #%d.%d", st.get_index(), i));
			sb.append(": ");
			sb.append(formatCtx.avcodec_string(st.get_codec(), false));

			if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				sb.append(", ");
				sb.append((int) st.get_avg_frame_rate().to_double());
				sb.append(" fps");
				fps = st.get_avg_frame_rate();
	//			inc_frame = fps.get_den() * AVUtil.AV_TIME_BASE / fps.get_num();
			}
			
			strings.add(sb.toString());
		}
		
	    return strings;
	}


	public String get_title() {
		StringBuilder sb = new StringBuilder();

		sb.append(uri);
	    
	    return sb.toString();
	}

	public long get_duration() {
		return formatCtx.get_duration();
	}

	public int get_nb_frames() {
		return (int) (get_duration() / AVUtil.AV_TIME_BASE * video_stream.get_time_base().get_den()/
				video_stream.get_time_base().get_num());
	}

	public int get_ref_frame() {
		return this.ref_frame;
	}

	public float frame_to_second(int nb_frame) {		
		float sec = (float)nb_frame * video_stream.get_time_base().get_num() /
					video_stream.get_time_base().get_den();
		return sec;
	}

	public void saveWAV() {
		TranscodeWAVDialog f = new TranscodeWAVDialog(new JFrame(), 0, get_nb_frames());
		f.setVisible(true);
		
		if (f.getRes()) {
			saveWAV(f.getDirectory().getPath(),
					  f.getMin(),
					  f.getMax());
		}
	}

	public void saveWAV(String path, int min, int max) {
		String template = "output";	
		saveWAV(path, template, min, max);
	}
	

	public void saveWAV(String path, String template, int start_frame, 
			int end_frame) {

		int cur_frame = get_cur_frame();
		int ref_frame = get_ref_frame();
		
		// Problem interfering with my threads ?
		/*ProgressMonitor mon = new ProgressMonitor(null, 
				"Extracting", "note", 
				start_frame, end_frame);*/		
		
		seek_frame(start_frame);
		
		ArrayList<long []> audioBuffers = new ArrayList<long[]>();
		
		boolean end = false;
		int i = start_frame;
		while ( i < end_frame && i < get_nb_frames() && !end )  {
			
			skip_first_frame();  // Need to also read the videoq
			long [] tmp = getFirstSample();
			if (tmp == null)
				end = true;
			audioBuffers.add(tmp);
			i++;			
		}

		save_audio_buffer(audioBuffers, path + "/" + template + ".wav");
			

		seek_frame(cur_frame);
		this.ref_frame = ref_frame;
		
	}
	

	
	public void transcode() {
		TranscodePNGDialog f = new TranscodePNGDialog(new JFrame(), 0, get_nb_frames());
		f.setVisible(true);
		
		if (f.getRes()) {
			transcode(f.getDirectory().getPath(),
					  f.getMin(),
					  f.getMax(),
					  f.getFileFormat());
		}
	}

	
	public void transcode(String path, int min, int max, String format) {
		String template = "output";	
		transcode(path, template, min, max, format.toLowerCase());
	}
	
	
	public void transcode(String path, String template, int start_frame, 
			long end_frame, String format) {

		int cur_frame = get_cur_frame();
		int ref_frame = get_ref_frame();
		
		// Problem interfering with my threads ?
		/*ProgressMonitor mon = new ProgressMonitor(null, 
				"Extracting", "note", 
				start_frame, end_frame);*/		
		
		seek_frame(start_frame);
		
		int i = start_frame;
		while ( i < end_frame && i < get_nb_frames() )  {
			BufferedImage bi = getFirstFrame();
			audioBuffers.add(getFirstSample());
			saveFrame(bi, path + "/" + template + "_" + i + "." + format); 
			i++;
		}		
		
		save_audio_buffer(audioBuffers, path + "/" + template + ".wav");		

		seek_frame(cur_frame);
		this.ref_frame = ref_frame;
		
	}
	
	
	public void initialize() {
		video_delay = 40;		
		wave_im = new WavImage(this);
	
		Runnable r = new Runnable() {
			public void run() {		
				decode_thread();
			}	
		};
	
		thrDecode = new Thread(r);
		thrDecode.start();
		
		while (!initialized) {
			try {
				Thread.sleep(100); 
			} catch (Exception e) {}  		
		}
		
	}


	private void decode_thread() {		
		init();	
		decode();
	} 

	public BufferedImage get_current_frame() {
		return current_frame;
	}
 

	public BufferedImage get_current_wave() {
		return current_wave;
	}
	 
    
    
    public void decode_next_frame() {
    	inc_frame();
    	
    	// Video decoding
    	current_frame = getFirstFrame();
    	
    	
    	// Audio Decoding
    	long [] audio_buf = getFirstSample();
		
		int i = 0;
		for (Long nb: audio_buf) {
			short sample = nb.shortValue();
			addSample(i, sample);	
			
			i++;
			if (i == get_channels())
				i = 0;
		}
		
    	current_wave = wave_im.getImage(frame_to_second(get_ref_frame()), 
    									timestamp_to_second(get_duration()));
    	
    	/*long endTime = System.currentTimeMillis();
    	long time_frame = endTime - startTime; // Time needed to decode the frame (the first will be false)
    	//System.out.println("fps = " + (1000.0f / time_frame));
    	startTime = endTime;*/

    	
    	
    }



	public void saveFrame() {
		TranscodePNGDialog f = new TranscodePNGDialog(new JFrame(), 0, 
												       get_nb_frames());
		f.setVisible(true);
		
		if ( (f.getRes()) && (f.getDirectory() != null) ) {
			saveFrame(f.getDirectory().getPath(),
					  f.getMin(),
					  f.getMax(),
					  f.getFileFormat());
		}
	}


	public void saveFrame(String path, int min, int max, String format) {
		String template = "output";	
		saveFrame(path, template, min, max, format.toLowerCase());
	}
	

	public void saveFrame(BufferedImage bi, String filename) {
		if (bi == null)
			return;
		try {
			File outputfile = new File(filename);
			String ext = getExtension(filename);
			if ( (!ext.equals("png")) && 
				 (!ext.equals("jpg")) && 
				 (!ext.equals("gif")) )
				ext = "png";
			ImageIO.write(bi, ext, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	public void saveFrame(String path, String template, 
			int start_frame, int end_frame, String format) {	
		int cur_frame = get_cur_frame();
		int ref_frame = get_ref_frame();
		
		// Problem interfering with my threads ?
		/*ProgressMonitor mon = new ProgressMonitor(null, 
				"Extracting", "note", 
				start_frame, end_frame);*/
		
		
		seek_frame(start_frame);
		
		int i = start_frame;
		while ( (i < end_frame) && (i < get_nb_frames()) )  {
			BufferedImage bi = getFirstFrame();
			skip_first_sample();  // Need to also read the audioq
			saveFrame(bi, path + "/" + template + "_" + i + "." + format);
			//mon.setProgress(i);
			i++;
		}		

		seek_frame(cur_frame);
		this.ref_frame = ref_frame;	
	}
	


	public void save_audio_buffer(ArrayList<long []> audioBuffers, String filename) {
		int size = 0;
		for (long [] array: audioBuffers) {
			size += array.length * 2;
		}

		long ch = get_channels();
		long sampleSec = get_sample_rate();
		long bitsSample = get_bits_per_coded_sample();
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
		
		for (long [] array: audioBuffers) {
			for (long nb: array) {
				writer.putle16(nb);
			}
		}
		
		writer.dump(filename);
	}

	


}
