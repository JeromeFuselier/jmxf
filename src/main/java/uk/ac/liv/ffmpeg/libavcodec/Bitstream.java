package uk.ac.liv.ffmpeg.libavcodec;

public class Bitstream {

	public static void ff_put_string(PutBitContext pb, String string, int terminate_string) {
		for (int i = 0 ; i < string.length() ; i++)
			PutBits.put_bits(pb, 8, string.charAt(i));
		
		if (terminate_string != 0)
			PutBits.put_bits(pb, 8, 0);
				
	}


}
