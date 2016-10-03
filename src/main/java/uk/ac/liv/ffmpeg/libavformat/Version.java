package uk.ac.liv.ffmpeg.libavformat;

import uk.ac.liv.ffmpeg.libavutil.AVUtil;

public class Version {

	public static String LIBAVFORMAT_IDENT = "Lavf" + LIBAVFORMAT_VERSION();

	public static final int LIBAVFORMAT_VERSION_MAJOR = 53;
	public static final int LIBAVFORMAT_VERSION_MINOR = 5;
	public static final int LIBAVFORMAT_VERSION_MICRO = 0;
	
	
	public static String LIBAVFORMAT_VERSION() {
		return AVUtil.AV_VERSION(LIBAVFORMAT_VERSION_MAJOR, LIBAVFORMAT_VERSION_MINOR, LIBAVFORMAT_VERSION_MICRO);
	}

}
