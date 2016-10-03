package uk.ac.liv.ffmpeg.libswscale;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.BSwap;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;
import uk.ac.liv.util.UtilsArrays;

public class Yuv2Rgb {
	

	public static int [][] ff_yuv2rgb_coeffs = {
	    {117504, 138453, 13954, 34903}, /* no sequence_display_extension */
	    {117504, 138453, 13954, 34903}, /* ITU-R Rec. 709 (1990) */
	    {104597, 132201, 25675, 53279}, /* unspecified */
	    {104597, 132201, 25675, 53279}, /* reserved */
	    {104448, 132798, 24759, 53109}, /* FCC */
	    {104597, 132201, 25675, 53279}, /* ITU-R Rec. 624-4 System B, G */
	    {104597, 132201, 25675, 53279}, /* SMPTE 170M */
	    {117579, 136230, 16907, 35559}  /* SMPTE 240M (1987) */
	};

	public static int ff_yuv2rgb_c_init_tables(SwsContext c, int[] inv_table, int fullRange,
			int brightness, int contrast, int saturation) {
		boolean isRgb = ( (c.get_dstFormat() == PixFmt.PIX_FMT_RGB32) ||
				          (c.get_dstFormat() == PixFmt.PIX_FMT_RGB32_1) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_BGR24) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB565BE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB565LE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB555BE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB555LE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB444BE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB444LE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB8) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB4) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB4_BYTE) ||
				          (c.get_dstFormat() == PixelFormat.PIX_FMT_MONOBLACK) );
 		
		boolean isNotNe = ( (c.get_dstFormat() == PixelFormat.PIX_FMT_RGB565BE) ||
							(c.get_dstFormat() == PixelFormat.PIX_FMT_RGB555BE) ||
							(c.get_dstFormat() == PixelFormat.PIX_FMT_RGB444BE) ||
							(c.get_dstFormat() == PixelFormat.PIX_FMT_BGR565BE) ||
							(c.get_dstFormat() == PixelFormat.PIX_FMT_BGR555BE) ||
							(c.get_dstFormat() == PixelFormat.PIX_FMT_BGR444BE) );
							 
		int bpp = c.get_dstFormatBpp();
		short [] y_table;
		short [] y_table16;
		int [] y_table32;
		int i, base, rbase, gbase, bbase, abase = 0;
		boolean needAlpha;
		int yoffs = (fullRange != 0) ? 384 : 326;
		
		long crv =  inv_table[0];
		long cbu =  inv_table[1];
		long cgu = -inv_table[2];
		long cgv = -inv_table[3];
		long cy  = 1<<16;
		long oy  = 0;
		
		long yb = 0;
		
		if (fullRange == 0) {
			cy = (cy*255) / 219;
			oy = 16<<16;
		} else {
			crv = (crv*224) / 255;
			cbu = (cbu*224) / 255;
			cgu = (cgu*224) / 255;
			cgv = (cgv*224) / 255;
		}
		
		cy  = (cy *contrast             ) >> 16;
		crv = (crv*contrast * saturation) >> 32;
		cbu = (cbu*contrast * saturation) >> 32;
		cgu = (cgu*contrast * saturation) >> 32;
		cgv = (cgv*contrast * saturation) >> 32;
		oy -= 256*brightness;
		
		c.set_uOffset(0x0400040004000400L);
		c.set_vOffset(0x0400040004000400L);
		c.set_yCoeff(roundToInt16(cy *8192) * 0x0001000100010001L);
		c.set_vrCoeff(roundToInt16(crv*8192) * 0x0001000100010001L);
		c.set_ubCoeff(roundToInt16(cbu*8192) * 0x0001000100010001L);
		c.set_vgCoeff(roundToInt16(cgv*8192) * 0x0001000100010001L);
		c.set_ugCoeff(roundToInt16(cgu*8192) * 0x0001000100010001L);
		c.set_yOffset(roundToInt16(oy *   8) * 0x0001000100010001L);

		c.set_yuv2rgb_y_coeff(roundToInt16(cy <<13));
		c.set_yuv2rgb_y_offset(roundToInt16(oy << 9));
		c.set_yuv2rgb_v2r_coeff(roundToInt16(crv<<13));
		c.set_yuv2rgb_v2g_coeff(roundToInt16(cgv<<13));
		c.set_yuv2rgb_u2g_coeff(roundToInt16(cgu<<13));
		c.set_yuv2rgb_u2b_coeff(roundToInt16(cbu<<13));
		
		//scale coefficients by cy
		crv = ((crv << 16) + 0x8000) / cy;
		cbu = ((cbu << 16) + 0x8000) / cy;
		cgu = ((cgu << 16) + 0x8000) / cy;
		cgv = ((cgv << 16) + 0x8000) / cy;
		
		
		switch (bpp) {
		case 1:
			c.set_yuvTable(new byte[1024]);
			y_table = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024-110; i++) {
				y_table[i+110] = (byte) (Common.av_clip_uint8((int) ((yb + 0x8000) >> 16)) >> 7);
				yb += cy;
			}
			fill_table(c.get_table_gU(), 1, (int) cgu, y_table, yoffs);
			fill_gv_table(c.get_table_gV(), 1, (int) cgv);
			break;
		case 4:
		case 4|128:
			rbase = isRgb ? 3 : 0;
			gbase = 1;
			bbase = isRgb ? 0 : 3;
			c.set_yuvTable(new byte[1024 * 3]);
			y_table = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024-110; i++) {
				int yval = Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
				y_table[i+110     ] =  (byte) ((yval >> 7)       << rbase);
				y_table[i+ 37+1024] = (byte) (((yval + 43) / 85) << gbase);
				y_table[i+110+2048] =  (byte) ((yval >> 7)       << bbase);
				yb += cy;
			}
			fill_table(c.get_table_rV(), 1, (int) crv, y_table, yoffs);
			fill_table(c.get_table_gU(), 1, (int) cgu, y_table, yoffs + 1024);
			fill_table(c.get_table_bU(), 1, (int) cbu, y_table, yoffs + 2048);
			fill_gv_table(c.get_table_gV(), 1, (int) cgv);
			break;
		case 8:
			rbase = isRgb ? 5 : 0;
			gbase = isRgb ? 2 : 3;
			bbase = isRgb ? 0 : 6;
			c.set_yuvTable(new byte[1024 * 3]);
			y_table = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024-38; i++) {
				int yval = Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
				y_table[i+16     ] = (byte) (((yval + 18) / 36) << rbase);
				y_table[i+16+1024] = (byte) (((yval + 18) / 36) << gbase);
				y_table[i+37+2048] = (byte) (((yval + 43) / 85) << bbase);
				yb += cy;
			}
			fill_table(c.get_table_rV(), 1, (int) crv, y_table, yoffs);
			fill_table(c.get_table_gU(), 1, (int) cgu, y_table, yoffs + 1024);
			fill_table(c.get_table_bU(), 1, (int) cbu, y_table, yoffs + 2048);
			fill_gv_table(c.get_table_gV(), 1, (int) cgv);
			break;
		case 12:
			rbase = isRgb ? 8 : 0;
			gbase = 4;
			bbase = isRgb ? 0 : 8;
			c.set_yuvTable(new short[1024 * 3]);
			y_table16 = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024; i++) {
				short yval = (short) Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
				y_table16[i     ] = (short) ((yval >> 4) << rbase);
				y_table16[i+1024] = (short) ((yval >> 4) << gbase);
				y_table16[i+2048] = (short) ((yval >> 4) << bbase);
				yb += cy;
			}
			if (isNotNe)
				for (i = 0; i < 1024*3; i++)
					y_table16[i] = (short) BSwap.av_bswap16(y_table16[i]);
				fill_table(c.get_table_rV(), 2, crv, UtilsArrays.short_to_byte_be(y_table16), yoffs);
				fill_table(c.get_table_gU(), 2, cgu, UtilsArrays.short_to_byte_be(y_table16), yoffs + 1024);
				fill_table(c.get_table_bU(), 2, cbu, UtilsArrays.short_to_byte_be(y_table16), yoffs + 2048);
				fill_gv_table(c.get_table_gV(), 2, (int) cgv);
			break;
		case 15:
		case 16:
			rbase = isRgb ? bpp - 5 : 0;
			gbase = 5;
			bbase = isRgb ? 0 : (bpp - 5);
			c.set_yuvTable(new short[1024 * 3]);
			y_table16 = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024; i++) {
				short yval = (short) Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
				y_table16[i     ] = (short) ((yval >> 3)          << rbase);
				y_table16[i+1024] = (short) ((yval >> (18 - bpp)) << gbase);
				y_table16[i+2048] = (short) ((yval >> 3)          << bbase);
				yb += cy;
			}
			if(isNotNe)
				for (i = 0; i < 1024*3; i++)
					y_table16[i] = (short) BSwap.av_bswap16(y_table16[i]);
			fill_table(c.get_table_rV(), 2, crv, UtilsArrays.short_to_byte_be(y_table16), yoffs);
			fill_table(c.get_table_gU(), 2, cgu, UtilsArrays.short_to_byte_be(y_table16), yoffs + 1024);
			fill_table(c.get_table_bU(), 2, cbu, UtilsArrays.short_to_byte_be(y_table16), yoffs + 2048);
			fill_gv_table(c.get_table_gV(), 2, (int) cgv);
			break;
		case 24:
		case 48:
			c.set_yuvTable(new byte[1024]);
			y_table = (short []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024; i++) {
			y_table[i] = (byte) Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
			yb += cy;
			}
			fill_table(c.get_table_rV(), 1, crv, y_table, yoffs);
			fill_table(c.get_table_gU(), 1, cgu, y_table, yoffs);
			fill_table(c.get_table_bU(), 1, cbu, y_table, yoffs);
			fill_gv_table(c.get_table_gV(), 1, (int) cgv);
			break;
		case 32:
			base = ( (c.get_dstFormat() == PixFmt.PIX_FMT_RGB32_1) || 
					 (c.get_dstFormat() == PixFmt.PIX_FMT_BGR32_1) ) ? 8 : 0;
			rbase = base + (isRgb ? 16 : 0);
			gbase = base + 8;
			bbase = base + (isRgb ? 0 : 16);
			needAlpha = /*CONFIG_SWSCALE_ALPHA &&*/ SwScaleInternal.isALPHA(c.get_srcFormat());
			if (!needAlpha)
				abase = (base + 24) & 31;
			c.set_yuvTable(new int[1024*3]);
			y_table32 = (int []) c.get_yuvTable();
			yb = -(384<<16) - oy;
			for (i = 0; i < 1024; i++) {
				byte yval = (byte) Common.av_clip_uint8((int) ((yb + 0x8000) >> 16));
				y_table32[i     ] = (yval << rbase) + (needAlpha ? 0 : (255 << abase));
				y_table32[i+1024] = yval << gbase;
				y_table32[i+2048] = yval << bbase;
				yb += cy;
			}
			fill_table(c.get_table_rV(), 4, crv, UtilsArrays.int_to_byte_be(y_table32), yoffs);
			fill_table(c.get_table_gU(), 4, cgu, UtilsArrays.int_to_byte_be(y_table32), yoffs + 1024);
			fill_table(c.get_table_bU(), 4, cbu, UtilsArrays.int_to_byte_be(y_table32), yoffs + 2048);
			fill_gv_table(c.get_table_gV(), 4, (int) cgv);
			break;
		default:
			c.set_yuvTable(null);
			Log.av_log("SwsContext", Log.AV_LOG_ERROR, "%ibpp not supported by yuv2rgb\n", bpp);
			return -1;
		}
		return 0;
		
	}

	private static void fill_gv_table(int[] table, int elemsize, int inc) {
	    int i;
	    long cb = 0;
	    int off = -(inc >> 9);

	    for (i = 0; i < 256; i++) {
	        table[i] = (int) (elemsize * (off + (cb >> 16)));
	        cb += inc;
	    }
		
	}

	private static void fill_table(short [] table, int elemsize, long inc,
			short[] s, int y_off) {
	    int i;
	    long cb = 0;
	    short [] y_table = s;

	    y_off -= elemsize * (inc >> 9);

	    for (i = 0; i < 256; i++) {
	        table[i] = y_table[(int) (elemsize * (cb >> 16))];
	        cb += inc;
	    }
		
		
	}

	private static int roundToInt16(long f) {
	    int r = (int) (f + (1<<15))>>16;
        if (r<-0x7FFF) return 0x8000;
        else if (r> 0x7FFF) return 0x7FFF;
        else                return r;
	}

}
