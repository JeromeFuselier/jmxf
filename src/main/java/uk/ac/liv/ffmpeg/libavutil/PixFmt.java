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
 * Creation   : June 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavutil;

import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;

public class PixFmt {
	

	
	public static enum PixelFormat {
	    PIX_FMT_NONE,
	    PIX_FMT_YUV420P,   // planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
	    PIX_FMT_YUYV422,   // packed YUV 4:2:2, 16bpp, Y0 Cb Y1 Cr
	    PIX_FMT_RGB24,     // packed RGB 8:8:8, 24bpp, RGBRGB...
	    PIX_FMT_BGR24,     // packed RGB 8:8:8, 24bpp, BGRBGR...
	    PIX_FMT_YUV422P,   // planar YUV 4:2:2, 16bpp, (1 Cr & Cb sample per 2x1 Y samples)
	    PIX_FMT_YUV444P,   // planar YUV 4:4:4, 24bpp, (1 Cr & Cb sample per 1x1 Y samples)
	    PIX_FMT_YUV410P,   // planar YUV 4:1:0,  9bpp, (1 Cr & Cb sample per 4x4 Y samples)
	    PIX_FMT_YUV411P,   // planar YUV 4:1:1, 12bpp, (1 Cr & Cb sample per 4x1 Y samples)
	    PIX_FMT_GRAY8,     //        Y        ,  8bpp
	    PIX_FMT_MONOWHITE, //        Y        ,  1bpp, 0 is white, 1 is black, in each byte pixels are ordered from the msb to the lsb
	    PIX_FMT_MONOBLACK, //        Y        ,  1bpp, 0 is black, 1 is white, in each byte pixels are ordered from the msb to the lsb
	    PIX_FMT_PAL8,      // 8 bit with PIX_FMT_RGB32 palette
	    PIX_FMT_YUVJ420P,  // planar YUV 4:2:0, 12bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV420P and setting color_range
	    PIX_FMT_YUVJ422P,  // planar YUV 4:2:2, 16bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV422P and setting color_range
	    PIX_FMT_YUVJ444P,  // planar YUV 4:4:4, 24bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV444P and setting color_range
	    PIX_FMT_XVMC_MPEG2_MC,// XVideo Motion Acceleration via common packet passing
	    PIX_FMT_XVMC_MPEG2_IDCT,
	    PIX_FMT_UYVY422,   // packed YUV 4:2:2, 16bpp, Cb Y0 Cr Y1
	    PIX_FMT_UYYVYY411, // packed YUV 4:1:1, 12bpp, Cb Y0 Y1 Cr Y2 Y3
	    PIX_FMT_BGR8,      // packed RGB 3:3:2,  8bpp, (msb)2B 3G 3R(lsb)
	    PIX_FMT_BGR4,      // packed RGB 1:2:1 bitstream,  4bpp, (msb)1B 2G 1R(lsb), a byte contains two pixels, the first pixel in the byte is the one composed by the 4 msb bits
	    PIX_FMT_BGR4_BYTE, // packed RGB 1:2:1,  8bpp, (msb)1B 2G 1R(lsb)
	    PIX_FMT_RGB8,      // packed RGB 3:3:2,  8bpp, (msb)2R 3G 3B(lsb)
	    PIX_FMT_RGB4,      // packed RGB 1:2:1 bitstream,  4bpp, (msb)1R 2G 1B(lsb), a byte contains two pixels, the first pixel in the byte is the one composed by the 4 msb bits
	    PIX_FMT_RGB4_BYTE, // packed RGB 1:2:1,  8bpp, (msb)1R 2G 1B(lsb)
	    PIX_FMT_NV12,      // planar YUV 4:2:0, 12bpp, 1 plane for Y and 1 plane for the UV components, which are interleaved (first byte U and the following byte V)
	    PIX_FMT_NV21,      // as above, but U and V bytes are swapped

	    PIX_FMT_ARGB,      // packed ARGB 8:8:8:8, 32bpp, ARGBARGB...
	    PIX_FMT_RGBA,      // packed RGBA 8:8:8:8, 32bpp, RGBARGBA...
	    PIX_FMT_ABGR,      // packed ABGR 8:8:8:8, 32bpp, ABGRABGR...
	    PIX_FMT_BGRA,      // packed BGRA 8:8:8:8, 32bpp, BGRABGRA...

	    PIX_FMT_GRAY16BE,  //        Y        , 16bpp, big-endian
	    PIX_FMT_GRAY16LE,  //        Y        , 16bpp, little-endian
	    PIX_FMT_YUV440P,   // planar YUV 4:4:0 (1 Cr & Cb sample per 1x2 Y samples)
	    PIX_FMT_YUVJ440P,  // planar YUV 4:4:0 full scale (JPEG), deprecated in favor of PIX_FMT_YUV440P and setting color_range
	    PIX_FMT_YUVA420P,  // planar YUV 4:2:0, 20bpp, (1 Cr & Cb sample per 2x2 Y & A samples)
	    PIX_FMT_VDPAU_H264,// H.264 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_VDPAU_MPEG1,// MPEG-1 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_VDPAU_MPEG2,// MPEG-2 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_VDPAU_WMV3,// WMV3 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_VDPAU_VC1, // VC-1 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_RGB48BE,   // packed RGB 16:16:16, 48bpp, 16R, 16G, 16B, the 2-byte value for each R/G/B component is stored as big-endian
	    PIX_FMT_RGB48LE,   // packed RGB 16:16:16, 48bpp, 16R, 16G, 16B, the 2-byte value for each R/G/B component is stored as little-endian

	    PIX_FMT_RGB565BE,  // packed RGB 5:6:5, 16bpp, (msb)   5R 6G 5B(lsb), big-endian
	    PIX_FMT_RGB565LE,  // packed RGB 5:6:5, 16bpp, (msb)   5R 6G 5B(lsb), little-endian
	    PIX_FMT_RGB555BE,  // packed RGB 5:5:5, 16bpp, (msb)1A 5R 5G 5B(lsb), big-endian, most significant bit to 0
	    PIX_FMT_RGB555LE,  // packed RGB 5:5:5, 16bpp, (msb)1A 5R 5G 5B(lsb), little-endian, most significant bit to 0

	    PIX_FMT_BGR565BE,  // packed BGR 5:6:5, 16bpp, (msb)   5B 6G 5R(lsb), big-endian
	    PIX_FMT_BGR565LE,  // packed BGR 5:6:5, 16bpp, (msb)   5B 6G 5R(lsb), little-endian
	    PIX_FMT_BGR555BE,  // packed BGR 5:5:5, 16bpp, (msb)1A 5B 5G 5R(lsb), big-endian, most significant bit to 1
	    PIX_FMT_BGR555LE,  // packed BGR 5:5:5, 16bpp, (msb)1A 5B 5G 5R(lsb), little-endian, most significant bit to 1

	    PIX_FMT_VAAPI_MOCO, // HW acceleration through VA API at motion compensation entry-point, Picture.data[3] contains a vaapi_render_state struct which contains macroblocks as well as various fields extracted from headers
	    PIX_FMT_VAAPI_IDCT, // HW acceleration through VA API at IDCT entry-point, Picture.data[3] contains a vaapi_render_state struct which contains fields extracted from headers
	    PIX_FMT_VAAPI_VLD,  // HW decoding through VA API, Picture.data[3] contains a vaapi_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers

	    PIX_FMT_YUV420P16LE,  // planar YUV 4:2:0, 24bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
	    PIX_FMT_YUV420P16BE,  // planar YUV 4:2:0, 24bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
	    PIX_FMT_YUV422P16LE,  // planar YUV 4:2:2, 32bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
	    PIX_FMT_YUV422P16BE,  // planar YUV 4:2:2, 32bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
	    PIX_FMT_YUV444P16LE,  // planar YUV 4:4:4, 48bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
	    PIX_FMT_YUV444P16BE,  // planar YUV 4:4:4, 48bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
	    PIX_FMT_VDPAU_MPEG4,  // MPEG4 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
	    PIX_FMT_DXVA2_VLD,    // HW decoding through DXVA2, Picture.data[3] contains a LPDIRECT3DSURFACE9 pointer

	    PIX_FMT_RGB444LE,  // packed RGB 4:4:4, 16bpp, (msb)4A 4R 4G 4B(lsb), little-endian, most significant bits to 0
	    PIX_FMT_RGB444BE,  // packed RGB 4:4:4, 16bpp, (msb)4A 4R 4G 4B(lsb), big-endian, most significant bits to 0
	    PIX_FMT_BGR444LE,  // packed BGR 4:4:4, 16bpp, (msb)4A 4B 4G 4R(lsb), little-endian, most significant bits to 1
	    PIX_FMT_BGR444BE,  // packed BGR 4:4:4, 16bpp, (msb)4A 4B 4G 4R(lsb), big-endian, most significant bits to 1
	    PIX_FMT_GRAY8A,    // 8bit gray, 8bit alpha
	    PIX_FMT_BGR48BE,   // packed RGB 16:16:16, 48bpp, 16B, 16G, 16R, the 2-byte value for each R/G/B component is stored as big-endian
	    PIX_FMT_BGR48LE,   // packed RGB 16:16:16, 48bpp, 16B, 16G, 16R, the 2-byte value for each R/G/B component is stored as little-endian

	    //the following 10 formats have the disadvantage of needing 1 format for each bit depth, thus
	    //If you want to support multiple bit depths, then using PIX_FMT_YUV420P16* with the bpp stored seperately
	    //is better
	    PIX_FMT_YUV420P9BE, // planar YUV 4:2:0, 13.5bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
	    PIX_FMT_YUV420P9LE, // planar YUV 4:2:0, 13.5bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
	    PIX_FMT_YUV420P10BE,// planar YUV 4:2:0, 15bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
	    PIX_FMT_YUV420P10LE,// planar YUV 4:2:0, 15bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
	    PIX_FMT_YUV422P10BE,// planar YUV 4:2:2, 20bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
	    PIX_FMT_YUV422P10LE,// planar YUV 4:2:2, 20bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
	    PIX_FMT_YUV444P9BE, // planar YUV 4:4:4, 27bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
	    PIX_FMT_YUV444P9LE, // planar YUV 4:4:4, 27bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
	    PIX_FMT_YUV444P10BE,// planar YUV 4:4:4, 30bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
	    PIX_FMT_YUV444P10LE,// planar YUV 4:4:4, 30bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
	    PIX_FMT_NB,;
	}

	public static PixelFormat PIX_FMT_Y400A     = PixelFormat.PIX_FMT_GRAY8A;	

	// Little endian
	public static PixelFormat PIX_FMT_RGB32     = PixelFormat.PIX_FMT_BGRA;
	public static PixelFormat PIX_FMT_RGB32_1   = PixelFormat.PIX_FMT_ABGR;
	public static PixelFormat PIX_FMT_BGR32     = PixelFormat.PIX_FMT_RGBA;
	public static PixelFormat PIX_FMT_BGR32_1   = PixelFormat.PIX_FMT_ARGB;
	
	public static PixelFormat PIX_FMT_GRAY16    = PixelFormat.PIX_FMT_GRAY16LE;
	public static PixelFormat PIX_FMT_RGB48     = PixelFormat.PIX_FMT_RGB48LE;
	public static PixelFormat PIX_FMT_RGB565    = PixelFormat.PIX_FMT_RGB565LE;
	public static PixelFormat PIX_FMT_RGB555    = PixelFormat.PIX_FMT_RGB555LE;
	public static PixelFormat PIX_FMT_RGB444    = PixelFormat.PIX_FMT_RGB444LE;
	public static PixelFormat PIX_FMT_BGR48     = PixelFormat.PIX_FMT_BGR48LE;
	public static PixelFormat PIX_FMT_BGR565    = PixelFormat.PIX_FMT_BGR565LE;
	public static PixelFormat PIX_FMT_BGR555    = PixelFormat.PIX_FMT_BGR555LE;
	public static PixelFormat PIX_FMT_BGR444    = PixelFormat.PIX_FMT_BGR444LE;
	
	public static PixelFormat PIX_FMT_YUV420P9  = PixelFormat.PIX_FMT_YUV420P9LE;
	public static PixelFormat PIX_FMT_YUV444P9  = PixelFormat.PIX_FMT_YUV444P9LE;
	public static PixelFormat PIX_FMT_YUV420P10 = PixelFormat.PIX_FMT_YUV420P10LE;
	public static PixelFormat PIX_FMT_YUV422P10 = PixelFormat.PIX_FMT_YUV422P10LE;
	public static PixelFormat PIX_FMT_YUV444P10 = PixelFormat.PIX_FMT_YUV444P10LE;
	public static PixelFormat PIX_FMT_YUV420P16 = PixelFormat.PIX_FMT_YUV420P16LE;
	public static PixelFormat PIX_FMT_YUV422P16 = PixelFormat.PIX_FMT_YUV422P16LE;
	public static PixelFormat PIX_FMT_YUV444P16 = PixelFormat.PIX_FMT_YUV444P16LE;

	/*
	// Big endian
	public static PixelFormat PIX_FMT_RGB32     = PixelFormat.PIX_FMT_ARGB;
	public static PixelFormat PIX_FMT_RGB32_1   = PixelFormat.PIX_FMT_RGBA;
	public static PixelFormat PIX_FMT_BGR32     = PixelFormat.PIX_FMT_ABGR;
	public static PixelFormat PIX_FMT_BGR32_1   = PixelFormat.PIX_FMT_BGRA;
	
	public static PixelFormat PIX_FMT_GRAY16    = PixelFormat.PIX_FMT_GRAY16BE;
	public static PixelFormat PIX_FMT_RGB48     = PixelFormat.PIX_FMT_RGB48BE;
	public static PixelFormat PIX_FMT_RGB565    = PixelFormat.PIX_FMT_RGB565BE;
	public static PixelFormat PIX_FMT_RGB555    = PixelFormat.PIX_FMT_RGB555BE;
	public static PixelFormat PIX_FMT_RGB444    = PixelFormat.PIX_FMT_RGB444BE;
	public static PixelFormat PIX_FMT_BGR48     = PixelFormat.PIX_FMT_BGR48BE;
	public static PixelFormat PIX_FMT_BGR565    = PixelFormat.PIX_FMT_BGR565BE;
	public static PixelFormat PIX_FMT_BGR555    = PixelFormat.PIX_FMT_BGR555BE;
	public static PixelFormat PIX_FMT_BGR444    = PixelFormat.PIX_FMT_BGR444BE;
	
	public static PixelFormat PIX_FMT_YUV420P9  = PixelFormat.PIX_FMT_YUV420P9BE;
	public static PixelFormat PIX_FMT_YUV444P9  = PixelFormat.PIX_FMT_YUV444P9BE;
	public static PixelFormat PIX_FMT_YUV420P10 = PixelFormat.PIX_FMT_YUV420P10BE;
	public static PixelFormat PIX_FMT_YUV422P10 = PixelFormat.PIX_FMT_YUV422P10BE;
	public static PixelFormat PIX_FMT_YUV444P10 = PixelFormat.PIX_FMT_YUV444P10BE;
	public static PixelFormat PIX_FMT_YUV420P16 = PixelFormat.PIX_FMT_YUV420P16BE;
	public static PixelFormat PIX_FMT_YUV422P16 = PixelFormat.PIX_FMT_YUV422P16BE;
	public static PixelFormat PIX_FMT_YUV444P16 = PixelFormat.PIX_FMT_YUV444P16BE;
	*/
	
	

}
