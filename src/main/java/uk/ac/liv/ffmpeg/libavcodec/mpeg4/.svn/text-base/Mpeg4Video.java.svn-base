package uk.ac.liv.ffmpeg.libavcodec.mpeg4;

import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;

public class Mpeg4Video {

	
	public static void ff_mpeg4_init_direct_mv(MpegEncContext s) {
	    int i;
	    int tab_size = s.get_direct_scale_mv()[0].length;
	    int tab_bias = tab_size / 2;
	    for (i = 0 ; i < tab_size ; i++) {
	        s.get_direct_scale_mv()[0][i] = (i - tab_bias) * s.get_pb_time() / s.get_pp_time();
	        s.get_direct_scale_mv()[1][i] = (i - tab_bias) * (s.get_pb_time() - s.get_pp_time()) / s.get_pp_time();
	    }
	}

}
