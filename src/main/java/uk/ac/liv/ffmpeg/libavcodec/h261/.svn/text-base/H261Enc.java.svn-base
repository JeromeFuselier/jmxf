package uk.ac.liv.ffmpeg.libavcodec.h261;

public class H261Enc {

	public static int ff_h261_get_picture_format(int width, int height) {
	    // QCIF
	    if (width == 176 && height == 144)
	        return 0;
	    // CIF
	    else if (width == 352 && height == 288)
	        return 1;
	    // ERROR
	    else
	        return -1;
	}

}
