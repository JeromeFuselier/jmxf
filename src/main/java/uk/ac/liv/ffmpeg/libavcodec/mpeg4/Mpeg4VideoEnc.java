package uk.ac.liv.ffmpeg.libavcodec.mpeg4;

import uk.ac.liv.ffmpeg.libavcodec.mpeg12.MpegEncContext;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavutil.Common;

public class Mpeg4VideoEnc {

	public static void ff_set_mpeg4_time(MpegEncContext s) {
	    if (s.get_pict_type() == AVPictureType.AV_PICTURE_TYPE_B) {
	        Mpeg4Video.ff_mpeg4_init_direct_mv(s);
	    } else {
	        s.set_last_time_base(s.get_time_base());
	        s.set_time_base((int) Common.FFUDIV(s.get_time(), s.get_avctx().get_time_base().get_den()));
	    }
	}

	public static void ff_clean_mpeg4_qscales(MpegEncContext s) {
		// TODO Jerome
	}

}
