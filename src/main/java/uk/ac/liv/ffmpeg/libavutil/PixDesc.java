/******************************************************************************
 *  
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author     : Jerome Fuselier
 * Creation   : March 2012
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavutil;

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.Config;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class PixDesc {

	public static final int PIX_FMT_BE        = 1; ///< Pixel format is big-endian.
	public static final int PIX_FMT_PAL       = 2; ///< Pixel format has a palette in data[1], values are indexes in this palette.
	public static final int PIX_FMT_BITSTREAM = 4; ///< All values of a component are bit-wise packed end to end.
	public static final int PIX_FMT_HWACCEL   = 8; ///< Pixel format is an HW accelerated format.

	

	public static Map<PixelFormat, AVPixFmtDescriptor> av_pix_fmt_descriptors;

	static {
		av_pix_fmt_descriptors = new HashMap<PixelFormat, AVPixFmtDescriptor>();
		
		AVPixFmtDescriptor tmp;		
		
		tmp = new AVPixFmtDescriptor("yuv420p", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P, tmp);

		tmp = new AVPixFmtDescriptor("yuyv422", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 4, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUYV422, tmp);

		tmp = new AVPixFmtDescriptor("rgb24", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 2, 1, 0, 7));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 2, 2, 0, 7));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 2, 3, 0, 7));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB24, tmp);

		tmp = new AVPixFmtDescriptor("bgr24", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 2, 1, 0, 7));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 2, 2, 0, 7));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 2, 3, 0, 7));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR24, tmp);

		tmp = new AVPixFmtDescriptor("yuv422p", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV422P, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P, tmp);

		tmp = new AVPixFmtDescriptor("yuv410p", 3, 2, 2);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV410P, tmp);

		tmp = new AVPixFmtDescriptor("yuv411p", 3, 2, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV411P, tmp);

		tmp = new AVPixFmtDescriptor("gray", 1, 0, 0, AVPixFmtDescriptor.PIX_FMT_PAL);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_GRAY8, tmp);

		tmp = new AVPixFmtDescriptor("monow", 1, 0, 0, AVPixFmtDescriptor.PIX_FMT_BITSTREAM);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 0));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_MONOWHITE, tmp);

		tmp = new AVPixFmtDescriptor("monob", 1, 0, 0, AVPixFmtDescriptor.PIX_FMT_BITSTREAM);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 7, 0));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_MONOBLACK, tmp);

		tmp = new AVPixFmtDescriptor("pal8", 1, 0, 0, AVPixFmtDescriptor.PIX_FMT_PAL);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_PAL8, tmp);

		tmp = new AVPixFmtDescriptor("yuvj420p", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUVJ420P, tmp);

		tmp = new AVPixFmtDescriptor("yuvj422p", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUVJ422P, tmp);

		tmp = new AVPixFmtDescriptor("yuvj444p", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUVJ444P, tmp);

		tmp = new AVPixFmtDescriptor("xvmcmc");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_XVMC_MPEG2_MC, tmp);

		tmp = new AVPixFmtDescriptor("xvmcidct");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_XVMC_MPEG2_IDCT, tmp);

		tmp = new AVPixFmtDescriptor("uyvy422", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 3, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_UYVY422, tmp);

		tmp = new AVPixFmtDescriptor("uyyvyy411", 3, 2, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 5, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(0, 5, 4, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_UYYVYY411, tmp);

		tmp = new AVPixFmtDescriptor("bgr8", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 6, 1));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 1, 3, 2));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 1, 0, 2));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_PAL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR8, tmp);

		tmp = new AVPixFmtDescriptor("bgr4", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 0));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 1));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 4, 0, 0));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BITSTREAM);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR4, tmp);

		tmp = new AVPixFmtDescriptor("bgr4_byte", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 3, 0));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 1, 1, 1));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 1, 0, 0));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_PAL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR4_BYTE, tmp);

		tmp = new AVPixFmtDescriptor("rgb8", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 6, 1));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 1, 3, 2));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 1, 0, 2));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_PAL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB8, tmp);

		tmp = new AVPixFmtDescriptor("rgb4", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 0));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 1));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 4, 0, 0));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BITSTREAM);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB4, tmp);

		tmp = new AVPixFmtDescriptor("rgb4_byte", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 3, 0));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 1, 1, 1));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 1, 0, 0));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_PAL);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB4_BYTE, tmp);

		tmp = new AVPixFmtDescriptor("nv12", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(1, 1, 2, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_NV12, tmp);

		tmp = new AVPixFmtDescriptor("nv21", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 7));	/* V */
		tmp.set_comp(2, new AVComponentDescriptor(1, 1, 2, 0, 7));	/* U */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_NV21, tmp);

		tmp = new AVPixFmtDescriptor("argb", 4, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 7));	/* A */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* R */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 3, 0, 7));	/* G */
		tmp.set_comp(3, new AVComponentDescriptor(0, 3, 4, 0, 7));	/* B */
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_ARGB, tmp);

		tmp = new AVPixFmtDescriptor("rgba", 4, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 7));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 3, 0, 7));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 3, 4, 0, 7));	/* A */
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGBA, tmp);

		tmp = new AVPixFmtDescriptor("abgr", 4, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 7));	/* A */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* B */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 3, 0, 7));	/* G */
		tmp.set_comp(3, new AVComponentDescriptor(0, 3, 4, 0, 7));	/* R */
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_ABGR, tmp);

		tmp = new AVPixFmtDescriptor("bgra", 4, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 3, 1, 0, 7));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 3, 2, 0, 7));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 3, 3, 0, 7));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 3, 4, 0, 7));	/* R */
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGRA, tmp);

		tmp = new AVPixFmtDescriptor("gray16be", 1, 0, 0, AVPixFmtDescriptor.PIX_FMT_BE);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_GRAY16BE, tmp);

		tmp = new AVPixFmtDescriptor("gray16le", 1, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_GRAY16LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv440p", 3, 0, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV440P, tmp);

		tmp = new AVPixFmtDescriptor("yuvj440p", 3, 0, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUVJ440P, tmp);

		tmp = new AVPixFmtDescriptor("yuva420p", 4, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 0, 1, 0, 7));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 0, 1, 0, 7));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(3, 0, 1, 0, 7));	/* A */
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUVA420P, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_h264");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_H264, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_mpeg1");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_MPEG1, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_mpeg2");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_MPEG2, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_wmv3");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_WMV3, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_vc1");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_VC1, tmp);

		tmp = new AVPixFmtDescriptor("vdpau_mpeg4");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VDPAU_MPEG4, tmp);

		tmp = new AVPixFmtDescriptor("rgb48be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 5, 1, 0, 15));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 5, 3, 0, 15));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 5, 5, 0, 15));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB48BE, tmp);

		tmp = new AVPixFmtDescriptor("rgb48le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 5, 1, 0, 15));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 5, 3, 0, 15));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 5, 5, 0, 15));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB48LE, tmp);

		tmp = new AVPixFmtDescriptor("rgb565be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 3, 4));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB565BE, tmp);

		tmp = new AVPixFmtDescriptor("rgb565le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 3, 4));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB565LE, tmp);

		tmp = new AVPixFmtDescriptor("rgb555be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 2, 4));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 4));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB555BE, tmp);

		tmp = new AVPixFmtDescriptor("rgb555le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 2, 4));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB555LE, tmp);

		tmp = new AVPixFmtDescriptor("rgb444be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 0, 3));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 4, 3));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 3));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB444BE, tmp);

		tmp = new AVPixFmtDescriptor("rgb444le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 0, 3));	/* R */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 4, 3));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 3));	/* B */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_RGB444LE, tmp);

		tmp = new AVPixFmtDescriptor("bgr48be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 5, 1, 0, 15));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 5, 3, 0, 15));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 5, 5, 0, 15));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR48BE, tmp);

		tmp = new AVPixFmtDescriptor("bgr48le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 5, 1, 0, 15));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 5, 3, 0, 15));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 5, 5, 0, 15));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR48LE, tmp);

		tmp = new AVPixFmtDescriptor("bgr565be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 3, 4));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR565BE, tmp);

		tmp = new AVPixFmtDescriptor("bgr565le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 3, 4));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR565LE, tmp);

		tmp = new AVPixFmtDescriptor("bgr555be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 2, 4));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR555BE, tmp);

		tmp = new AVPixFmtDescriptor("bgr555le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 2, 4));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 5));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 4));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR555LE, tmp);

		tmp = new AVPixFmtDescriptor("bgr444be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 0, 0, 3));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 4, 3));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 3));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR444BE, tmp);

		tmp = new AVPixFmtDescriptor("bgr444le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 2, 0, 3));	/* B */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 1, 5, 3));	/* G */
		tmp.set_comp(2, new AVComponentDescriptor(0, 1, 1, 0, 3));	/* R */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_BGR444LE, tmp);

		tmp = new AVPixFmtDescriptor("vaapi_moco");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VAAPI_MOCO, tmp);

		tmp = new AVPixFmtDescriptor("vaapi_idct");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VAAPI_IDCT, tmp);

		tmp = new AVPixFmtDescriptor("vaapi_vld");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_VAAPI_VLD, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p9le", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 8));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 8));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 8));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P9LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p9be", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 8));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 8));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 8));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P9BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p10le", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P10LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p10be", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P10BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p16le", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P16LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv420p16be", 3, 1, 1);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV420P16BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv422p10le", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV422P10LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv422p10be", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV422P10BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv422p16le", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV422P16LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv422p16be", 3, 1, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV422P16BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p16le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P16LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p16be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 15));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 15));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 15));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P16BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p10le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P10LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p10be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 9));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 9));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 9));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P10BE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p9le", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 8));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 8));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 8));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P9LE, tmp);

		tmp = new AVPixFmtDescriptor("yuv444p9be", 3, 0, 0);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 8));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(1, 1, 1, 0, 8));	/* U */
		tmp.set_comp(2, new AVComponentDescriptor(2, 1, 1, 0, 8));	/* V */
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_BE);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_YUV444P9BE, tmp);

		tmp = new AVPixFmtDescriptor("dxva2_vld");
		tmp.set_comp(0, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(1, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_flags(AVPixFmtDescriptor.PIX_FMT_HWACCEL);
		tmp.set_log2_chroma_w(1);
		tmp.set_log2_chroma_h(1);
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_DXVA2_VLD, tmp);

		tmp = new AVPixFmtDescriptor("gray8a");
		tmp.set_nb_components(2);
		tmp.set_comp(0, new AVComponentDescriptor(0, 1, 1, 0, 7));	/* Y */
		tmp.set_comp(1, new AVComponentDescriptor(0, 1, 2, 0, 7));	/* A*/
		tmp.set_comp(2, new AVComponentDescriptor(0, 0, 0, 0, 0));
		tmp.set_comp(3, new AVComponentDescriptor(0, 0, 0, 0, 0));
		av_pix_fmt_descriptors.put(PixelFormat.PIX_FMT_GRAY8A, tmp);
		
	
	}

	
	public static String av_get_pix_fmt_name(PixelFormat pix_fmt) {
		AVPixFmtDescriptor fmt_descr = av_pix_fmt_descriptors.get(pix_fmt);
		if (fmt_descr != null)
			return fmt_descr.get_name();
		else
			return "";
	}


	public static int av_get_bits_per_pixel(AVPixFmtDescriptor pix_desc) {
		return pix_desc.av_get_bits_per_pixel();		
	}


	public static PixelFormat av_get_pix_fmt(String name) {
	    PixelFormat pix_fmt;

	    if (name.equals("rgb32")) {
	    	if (Config.HAVE_BIGENDIAN)
	    		name ="argb";
    		else
    			name = "bgra";	    
	    } else if (name.equals("bgr32")) {
	    	if (Config.HAVE_BIGENDIAN)
	    		name ="abgr";
    		else
    			name = "rgba";
	    }

	    pix_fmt = get_pix_fmt_internal(name);
	    if (pix_fmt == PixelFormat.PIX_FMT_NONE) {
	        String name2;
	        if (Config.HAVE_BIGENDIAN)
	        	name2 = name + "be";
	        else
	        	name2 = name + "le";
	        
	        pix_fmt = get_pix_fmt_internal(name2);
	    }
	    return pix_fmt;
	}


	private static PixelFormat get_pix_fmt_internal(String name) {		
		for (PixelFormat pix_fmt : PixelFormat.values()) {
			AVPixFmtDescriptor fmt_descr = av_pix_fmt_descriptors.get(pix_fmt);
			if (fmt_descr != null)
				if (fmt_descr.get_name() == name)
					return pix_fmt;			
		}

	    return PixelFormat.PIX_FMT_NONE;
	}

}
