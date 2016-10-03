package uk.ac.liv.ffmpeg.libavformat;

public class OSSupport {

	public static boolean is_dos_path(String path) {
		if (path.length() >= 2)
			return path.charAt(1) == ':';
		return false;
	}

}
