package uk.ac.liv.ffmpeg.libavformat.img2;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVStream;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.AVIO;
import uk.ac.liv.ffmpeg.libavformat.AVIOBuf;
import uk.ac.liv.ffmpeg.libavformat.AVOutputFormat;
import uk.ac.liv.ffmpeg.libavformat.UtilsFormat;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.util.OutOI;

public class Img2Out extends AVOutputFormat {
	
	

	public static int [][] sizes = {
	    { 640, 480 },
	    { 720, 480 },
	    { 720, 576 },
	    { 352, 288 },
	    { 352, 240 },
	    { 160, 128 },
	    { 512, 384 },
	    { 640, 352 },
	    { 640, 240 },
	};


	
	public Img2Out() {
		super();
		this.name = "image2";
		this.long_name = "image2 sequence";
		
		this.video_codec = CodecID.CODEC_ID_MJPEG;
		this.add_extension("bmp");
		this.add_extension("dpx");
		this.add_extension("jls");
		this.add_extension("jpeg");
		this.add_extension("jpg");
		this.add_extension("ljpg");
		this.add_extension("pam");
		this.add_extension("pbm");
		this.add_extension("pcx");
		this.add_extension("pgm");
		this.add_extension("pgmyuv");
		this.add_extension("png");
		this.add_extension("ppm");
		this.add_extension("sgi");
		this.add_extension("tga");
		this.add_extension("tif");
		this.add_extension("tiff");
		this.add_extension("jp2");
		this.set_flags(AVFormat.AVFMT_NOTIMESTAMPS | AVFormat.AVFMT_NODIMENSIONS | AVFormat.AVFMT_NOFILE);
	
	}
	
	public int write_header(AVFormatContext s) {
	    //VideoData img = (VideoData) s.get_priv_data();
		VideoData img = new VideoData();
		s.set_priv_data(img);
	    String str;

	    img.set_img_number(1);
	    img.set_path(s.get_filename());

	    /* find format */
	    if (s.get_oformat().has_flag(AVFormat.AVFMT_NOFILE))
	        img.set_is_pipe(0);
	    else
	        img.set_is_pipe(1);

	    str = img.get_path().substring(img.get_path().lastIndexOf('.'), img.get_path().length());
	    img.set_split_planes( (str != null) && (str.toLowerCase().equals(".y")) ? 1 : 0);
	    return 0;
	}
	



	protected int write_packet(AVFormatContext s, AVPacket pkt) {
	    VideoData img = (VideoData) s.get_priv_data();
	    AVIOContext [] pb = new AVIOContext[3];
	    String filename;
	    AVCodecContext codec = s.get_stream(pkt.get_stream_index()).get_codec();
	    int i;
	
	    if (img.get_is_pipe() == 0) {
	    	OutOI ret_obj = UtilsFormat.av_get_frame_filename(img.get_path(), img.get_img_number());
	    	filename = (String) ret_obj.get_obj();
	    	int ret = ret_obj.get_ret();
	    	
	        if ( ret < 0 && img.get_img_number() > 1 ) {
	            Log.av_log("AVFormatContext", Log.AV_LOG_ERROR,
	                   "Could not get frame filename number %d from pattern '%s'\n",
	                   img.get_img_number(), img.get_path());
	            return Error.AVERROR(Error.EINVAL);
	        }
	        for (i = 0 ; i < 3 ; i++) {
	        	pb[i] = new AVIOContext();
	            if (AVIOBuf.avio_open(pb[i], filename, AVIO.AVIO_FLAG_WRITE) < 0) {
	            	Log.av_log("AVFormatContext", Log.AV_LOG_ERROR, "Could not open file : %s\n", filename);
	                return Error.AVERROR(Error.EIO);
	            }
	
	            if (img.get_split_planes() == 0)
	                break;
	            filename += 'U' + i;
	        }
	    } else {
	        pb[0] = s.get_pb();
	    }
	
	    if (img.get_split_planes() != 0) {
	        int ysize = codec.get_width() * codec.get_height();
	        /*AVIOBuf.avio_write(pb[0], Arrays.copyOfRange(pkt.get_data(), 
	        											 0,
	        											 ysize));
	        AVIOBuf.avio_write(pb[1], Arrays.copyOfRange(pkt.get_data(), 
	        											 ysize,
	        											 ysize + (pkt.get_size() - ysize) / 2 ));
	        AVIOBuf.avio_write(pb[2], Arrays.copyOfRange(pkt.get_data(), 
	        											 ysize + (pkt.get_size() - ysize) / 2, 
	        											 ysize + (pkt.get_size() - ysize) / 2) );
	        AVIOBuf.avio_flush(pb[1]);
	        AVIOBuf.avio_flush(pb[2]);
	        AVIOBuf.avio_close(pb[1]);
	        AVIOBuf.avio_close(pb[2]);*/
	    } else {
	        if (Img2.av_str2id(Img2.img_tags, s.get_filename()) == CodecID.CODEC_ID_JPEG2000) {
	           /* AVStream st = s.get_stream(0);
	            if (st.get_codec().get_extradata_size() > 8 &&
	               AV_RL32(st.get_codec().get_extradata() + 4) == MKTAG('j','p','2','h')) {
	            	if (pkt.get_size() < 8 || AV_RL32(pkt.get_data()+4) != MKTAG('j','p','2','c')) {
		            	Log.av_log("AVFormatContext", Log.AV_LOG_ERROR, "malformated jpeg2000 codestream\n");
		                return -1;
	            	}
	            	
                	avio_wb32(pb[0], 12);
                	ffio_wfourcc(pb[0], "jP  ");
                	avio_wb32(pb[0], 0x0D0A870A); // signature
                	avio_wb32(pb[0], 20);
                	ffio_wfourcc(pb[0], "ftyp");
                	ffio_wfourcc(pb[0], "jp2 ");
                	avio_wb32(pb[0], 0);
                	ffio_wfourcc(pb[0], "jp2 ");
                	avio_write(pb[0], st.get_codec().get_extradata(), st.get_codec().get_extradata_size());
	            } else if (pkt.get_size() < 8 ||
	                        (st.get_codec().get_extradata_size() == 0 && AV_RL32(pkt.get_data()+4) != MKTAG('j','P',' ',' '))){ // signature
	            	Log.av_log("AVFormatContext", Log.AV_LOG_ERROR, "malformated jpeg2000 codestream\n");
	                return -1;
	            }*/
	        }
	        AVIOBuf.avio_write(pb[0], pkt.get_data());
	    }
	    AVIOBuf.avio_flush(pb[0]);
	    if (img.get_is_pipe() == 0) {
	    	AVIOBuf.avio_close(pb[0]);
	    }
	
	    img.set_img_number(img.get_img_number() + 1);
	    return 0;
	}
	
	
	
	

}
