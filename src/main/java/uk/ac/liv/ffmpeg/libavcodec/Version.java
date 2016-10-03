package uk.ac.liv.ffmpeg.libavcodec;

public class Version {

	public static final int LIBAVCODEC_VERSION_MAJOR = 53;
	public static final int LIBAVCODEC_VERSION_MINOR =  7;
	public static final int LIBAVCODEC_VERSION_MICRO =  0;
	
	public static final String LIBAVCODEC_VERSION = LIBAVCODEC_VERSION_MAJOR + "." +
            										  LIBAVCODEC_VERSION_MINOR + "." +
            										  LIBAVCODEC_VERSION_MICRO;
	
	public static final String LIBAVCODEC_IDENT =  "Lavc" + LIBAVCODEC_VERSION;
	
}
