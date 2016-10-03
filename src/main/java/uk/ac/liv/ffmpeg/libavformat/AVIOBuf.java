package uk.ac.liv.ffmpeg.libavformat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.util.UtilsArrays;

public class AVIOBuf {
	

	public static int avio_open(AVIOContext s, String filename, int flags) {
		// AVIO.AVIO_FLAG_WRITE

      try {
		FileOutputStream fos = new FileOutputStream(filename);
		s.set_fos(fos);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		/*URLContext h = AVIO.ffurl_open(filename, flags);
		int err;
		
		if (h == null)
			return Error.AVERROR(Error.ENOENT);
		err = ffio_fdopen(s, h);
		if (err < 0) {
			h.ffurl_close();
			return err;
		}		*/
		return 0;
	}

	private static int ffio_fdopen(AVIOContext s, URLContext h) {
		// TODO Implem
		return 0;
	}

	public static void avio_write(AVIOContext s, short[] buf) {		
		try {
			s.get_fos().write(UtilsArrays.short_to_byte(buf));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	  /*  while (size > 0) {
	        int len = Mathematics.FFMIN(s->buf_end - s->buf_ptr, size);
	        memcpy(s->buf_ptr, buf, len);
	        s->buf_ptr += len;

	        if (s->buf_ptr >= s->buf_end)
	            flush_buffer(s);

	        buf += len;
	        size -= len;
	    }*/
	}

	public static void avio_flush(AVIOContext s) {
		
	}

	public static void avio_close(AVIOContext s) {
		try {
			s.get_fos().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
