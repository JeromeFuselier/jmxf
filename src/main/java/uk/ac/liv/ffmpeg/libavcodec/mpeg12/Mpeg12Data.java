package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

public class Mpeg12Data {
	

	public static int [] ff_mpeg1_default_intra_matrix = {
	        8, 16, 19, 22, 26, 27, 29, 34,
	        16, 16, 22, 24, 27, 29, 34, 37,
	        19, 22, 26, 27, 29, 34, 34, 38,
	        22, 22, 26, 27, 29, 34, 37, 40,
	        22, 26, 27, 29, 32, 35, 40, 48,
	        26, 27, 29, 32, 35, 40, 48, 58,
	        26, 27, 29, 34, 38, 46, 56, 69,
	        27, 29, 35, 38, 46, 56, 69, 83
	};

	public static int [] ff_mpeg1_default_non_intra_matrix = {
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	    16, 16, 16, 16, 16, 16, 16, 16,
	};

}
