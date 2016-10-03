package uk.ac.liv.ffmpeg.libswscale;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.Config;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.Common;
import uk.ac.liv.ffmpeg.libavutil.Cpu;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutII;
import uk.ac.liv.util.OutOOOI;
import uk.ac.liv.util.UtilsArrays;

public class SwsContext extends AVContext {
	
	

	public static String RED_DITHER            = "0*8";
	public static String GREEN_DITHER          = "1*8";
	public static String BLUE_DITHER           = "2*8";
	public static String Y_COEFF               = "3*8";
	public static String VR_COEFF              = "4*8";
	public static String UB_COEFF              = "5*8";
	public static String VG_COEFF              = "6*8";
	public static String UG_COEFF              = "7*8";
	public static String Y_OFFSET              = "8*8";
	public static String U_OFFSET              = "9*8";
	public static String V_OFFSET              = "10*8";
	public static String LUM_MMX_FILTER_OFFSET = "11*8";
	public static String CHR_MMX_FILTER_OFFSET = "11*8+4*4*256";
	public static String DSTW_OFFSET           = "11*8+4*4*256*2"; //do not change, it is hardcoded in the ASM
	public static String ESP_OFFSET            = "11*8+4*4*256*2+8";
	public static String VROUNDER_OFFSET       = "11*8+4*4*256*2+16";
	public static String U_TEMP                = "11*8+4*4*256*2+24";
	public static String V_TEMP                = "11*8+4*4*256*2+32";
	public static String Y_TEMP                = "11*8+4*4*256*2+40";
	public static String ALP_MMX_FILTER_OFFSET = "11*8+4*4*256*2+48";
	public static String UV_OFF                = "11*8+4*4*256*3+48";
	public static String UV_OFFx2              = "11*8+4*4*256*3+56";
	public static String DITHER16              = "11*8+4*4*256*3+64";
	public static String DITHER32              = "11*8+4*4*256*3+64+16";
	
	public static int MAX_FILTER_SIZE = 256;
	

    public static boolean warnedAlready = false; 
	
    /**
     * info on struct for av_log
     */
    AVClass av_class = new AVClass();

    /**
     * Note that src, dst, srcStride, dstStride will be copied in the
     * sws_scale() wrapper so they can be freely modified here.
     */
    int srcW;                     ///< Width  of source      luma/alpha planes.
    int srcH;                     ///< Height of source      luma/alpha planes.
    int dstH;                     ///< Height of destination luma/alpha planes.
    int chrSrcW;                  ///< Width  of source      chroma     planes.
    int chrSrcH;                  ///< Height of source      chroma     planes.
    int chrDstW;                  ///< Width  of destination chroma     planes.
    int chrDstH;                  ///< Height of destination chroma     planes.
    int lumXInc, chrXInc;
    int lumYInc, chrYInc;
    PixelFormat dstFormat;   ///< Destination pixel format.
    PixelFormat srcFormat;   ///< Source      pixel format.
    int dstFormatBpp;             ///< Number of bits per pixel of the destination pixel format.
    int srcFormatBpp;             ///< Number of bits per pixel of the source      pixel format.
    int scalingBpp;
    int chrSrcHSubSample;         ///< Binary logarithm of horizontal subsampling factor between luma/alpha and chroma planes in source      image.
    int chrSrcVSubSample;         ///< Binary logarithm of vertical   subsampling factor between luma/alpha and chroma planes in source      image.
    int chrDstHSubSample;         ///< Binary logarithm of horizontal subsampling factor between luma/alpha and chroma planes in destination image.
    int chrDstVSubSample;         ///< Binary logarithm of vertical   subsampling factor between luma/alpha and chroma planes in destination image.
    int vChrDrop;                 ///< Binary logarithm of extra vertical subsampling factor in source image chroma planes specified by user.
    int sliceDir;                 ///< Direction that slices are fed to the scaler (1 = top-to-bottom, -1 = bottom-to-top).
    double [] param = new double[2];              ///< Input parameters for scaling algorithms that need them.

    int [] pal_yuv = new int[256];
    int [] pal_rgb = new int[256];

    /**
     * @name Scaled horizontal lines ring buffer.
     * The horizontal scaler keeps just enough scaled lines in a ring buffer
     * so they may be passed to the vertical scaler. The pointers to the
     * allocated buffers for each line are duplicated in sequence in the ring
     * buffer to simplify indexing and avoid wrapping around between lines
     * inside the vertical scaler code. The wrapping is done before the
     * vertical scaler is called.
     */
    //@{
    int [] lumPixBuf;          ///< Ring buffer for scaled horizontal luma   plane lines to be fed to the vertical scaler.
    int [] chrUPixBuf;         ///< Ring buffer for scaled horizontal chroma plane lines to be fed to the vertical scaler.
    int [] chrVPixBuf;         ///< Ring buffer for scaled horizontal chroma plane lines to be fed to the vertical scaler.
    int [] alpPixBuf;          ///< Ring buffer for scaled horizontal alpha  plane lines to be fed to the vertical scaler.
    int       vLumBufSize;        ///< Number of vertical luma/alpha lines allocated in the ring buffer.
    int       vChrBufSize;        ///< Number of vertical chroma     lines allocated in the ring buffer.
    int       lastInLumBuf;       ///< Last scaled horizontal luma/alpha line from source in the ring buffer.
    int       lastInChrBuf;       ///< Last scaled horizontal chroma     line from source in the ring buffer.
    int       lumBufIndex;        ///< Index in ring buffer of the last scaled horizontal luma/alpha line from source.
    int       chrBufIndex;        ///< Index in ring buffer of the last scaled horizontal chroma     line from source.
    //@}

    short [] formatConvBuffer;

    /**
     * @name Horizontal and vertical filters.
     * To better understand the following fields, here is a pseudo-code of
     * their usage in filtering a horizontal line:
     * @code
     * for (i = 0; i < width; i++) {
     *     dst[i] = 0;
     *     for (j = 0; j < filterSize; j++)
     *         dst[i] += src[ filterPos[i] + j ] * filter[ filterSize * i + j ];
     *     dst[i] >>= FRAC_BITS; // The actual implementation is fixed-point.
     * }
     * @endcode
     */
    //@{
    int [] hLumFilter;          ///< Array of horizontal filter coefficients for luma/alpha planes.
    int [] hChrFilter;          ///< Array of horizontal filter coefficients for chroma     planes.
    int [] vLumFilter;          ///< Array of vertical   filter coefficients for luma/alpha planes.
    int [] vChrFilter;          ///< Array of vertical   filter coefficients for chroma     planes.
    int [] hLumFilterPos;       ///< Array of horizontal filter starting positions for each dst[i] for luma/alpha planes.
    int [] hChrFilterPos;       ///< Array of horizontal filter starting positions for each dst[i] for chroma     planes.
    int [] vLumFilterPos;       ///< Array of vertical   filter starting positions for each dst[i] for luma/alpha planes.
    int [] vChrFilterPos;       ///< Array of vertical   filter starting positions for each dst[i] for chroma     planes.
    int      hLumFilterSize;      ///< Horizontal filter size for luma/alpha pixels.
    int      hChrFilterSize;      ///< Horizontal filter size for chroma     pixels.
    int      vLumFilterSize;      ///< Vertical   filter size for luma/alpha pixels.
    int      vChrFilterSize;      ///< Vertical   filter size for chroma     pixels.
    //@}

    int lumMmx2FilterCodeSize;    ///< Runtime-generated MMX2 horizontal fast bilinear scaler code size for luma/alpha planes.
    int chrMmx2FilterCodeSize;    ///< Runtime-generated MMX2 horizontal fast bilinear scaler code size for chroma     planes.
    byte [] lumMmx2FilterCode;   ///< Runtime-generated MMX2 horizontal fast bilinear scaler code for luma/alpha planes.
    byte [] chrMmx2FilterCode;   ///< Runtime-generated MMX2 horizontal fast bilinear scaler code for chroma     planes.

    int canMMX2BeUsed;

    int dstY;                     ///< Last destination vertical line output from last slice.
    public int flags;                    ///< Flags passed by the user to select scaler algorithm, optimizations, subsampling, etc...
    Object yuvTable;            // pointer to the yuv->rgb table start so it can be freed()
    short [] table_rV = new short[256];
    short [] table_gU = new short[256];
    int [] table_gV = new int[256];
    short [] table_bU = new short[256];

    //Colorspace stuff
    int contrast, brightness, saturation;    // for sws_get_colorspaceDetails
    int [] srcColorspaceTable = new int[4];
    int [] dstColorspaceTable = new int[4];
    int srcRange;                 ///< 0 = MPG YUV range, 1 = JPG YUV range (source      image).
    int dstRange;                 ///< 0 = MPG YUV range, 1 = JPG YUV range (destination image).
    int yuv2rgb_y_offset;
    int yuv2rgb_y_coeff;
    int yuv2rgb_v2r_coeff;
    int yuv2rgb_v2g_coeff;
    int yuv2rgb_u2g_coeff;
    int yuv2rgb_u2b_coeff;


    long redDither;
    long greenDither;
    long blueDither;

    long yCoeff;
    long vrCoeff;
    long ubCoeff;
    long vgCoeff;
    long ugCoeff;
    long yOffset;
    long uOffset;
    long vOffset;
    int [] lumMmxFilter = new int[4*MAX_FILTER_SIZE];
    int [] chrMmxFilter = new int[4*MAX_FILTER_SIZE];
    int dstW;                     ///< Width  of destination luma/alpha planes.
    long esp;
    long vRounder;
    long u_temp;
    long v_temp;
    long y_temp;
    int [] alpMmxFilter = new int[4*MAX_FILTER_SIZE];
    int uv_off; ///< offset (in pixels) between u and v planes
    int uv_offx2; ///< offset (in bytes) between u and v planes
    int [] dither16 = new int[8];
    int [] dither32 = new int[8];
    

   byte [] chrDither8;
   byte [] lumDither8;

   String hScale = "";
   String swScale = "";
   String chrToYV12 = "";
   String hScale16 = "";
   String lumToYV12 = "";
   String alpToYV12 = "";
   String hyscale_fast = "";
   String hcscale_fast = "";
   String lumConvertRange = "";
   String chrConvertRange = "";
   String scale19To15Fw = "";
   String scale8To16Rv = "";
   String yuv2yuv1 = "";;
   String yuv2yuvX = "";;
   String yuv2packed1 = "";;
   String yuv2packed2 = "";;
   String yuv2packedX = "";;
    

    int needs_hcscale; ///< Set if there are chroma planes to be converted.

   
    
   
    public SwsContext() {
		super();
		
		this.av_class = new SwsContextClass();
	}
    
    
	public int get_uv_off() {
		return uv_off;
	}



	public void set_uv_off(int uv_off) {
		this.uv_off = uv_off;
	}



	public int get_uv_offx2() {
		return uv_offx2;
	}



	public void set_uv_offx2(int uv_offx2) {
		this.uv_offx2 = uv_offx2;
	}



	public byte[] get_chrDither8() {
		return chrDither8;
	}


	public void set_chrDither8(byte[] chrDither8) {
		this.chrDither8 = chrDither8;
	}


	public byte[] get_lumDither8() {
		return lumDither8;
	}


	public void set_lumDither8(byte[] lumDither8) {
		this.lumDither8 = lumDither8;
	}


	public int[] get_alpMmxFilter() {
		return alpMmxFilter;
	}

	public void set_alpMmxFilter(int[] alpMmxFilter) {
		this.alpMmxFilter = alpMmxFilter;
	}
	

	public int get_scalingBpp() {
		return scalingBpp;
	}

	public void set_scalingBpp(int scalingBpp) {
		this.scalingBpp = scalingBpp;
	}

	public AVClass get_av_class() {
		return av_class;
	}

	public void set_av_class(AVClass av_class) {
		this.av_class = av_class;
	}

	public int get_srcW() {
		return srcW;
	}

	public void set_srcW(int srcW) {
		this.srcW = srcW;
	}

	public int get_srcH() {
		return srcH;
	}

	public void set_srcH(int srcH) {
		this.srcH = srcH;
	}

	public int get_dstH() {
		return dstH;
	}

	public void set_dstH(int dstH) {
		this.dstH = dstH;
	}

	public int get_chrSrcW() {
		return chrSrcW;
	}

	public void set_chrSrcW(int chrSrcW) {
		this.chrSrcW = chrSrcW;
	}

	public int get_chrSrcH() {
		return chrSrcH;
	}

	public void set_chrSrcH(int chrSrcH) {
		this.chrSrcH = chrSrcH;
	}

	public int get_chrDstW() {
		return chrDstW;
	}

	public void set_chrDstW(int chrDstW) {
		this.chrDstW = chrDstW;
	}

	public int get_chrDstH() {
		return chrDstH;
	}

	public void set_chrDstH(int chrDstH) {
		this.chrDstH = chrDstH;
	}

	public int get_lumXInc() {
		return lumXInc;
	}

	public void set_lumXInc(int lumXInc) {
		this.lumXInc = lumXInc;
	}

	public int get_chrXInc() {
		return chrXInc;
	}

	public void set_chrXInc(int chrXInc) {
		this.chrXInc = chrXInc;
	}

	public int get_lumYInc() {
		return lumYInc;
	}

	public void set_lumYInc(int lumYInc) {
		this.lumYInc = lumYInc;
	}

	public int get_chrYInc() {
		return chrYInc;
	}

	public void set_chrYInc(int chrYInc) {
		this.chrYInc = chrYInc;
	}

	public PixelFormat get_dstFormat() {
		return dstFormat;
	}

	public void set_dstFormat(PixelFormat dstFormat) {
		this.dstFormat = dstFormat;
	}

	public PixelFormat get_srcFormat() {
		return srcFormat;
	}

	public void set_srcFormat(PixelFormat srcFormat) {
		this.srcFormat = srcFormat;
	}

	public int get_dstFormatBpp() {
		return dstFormatBpp;
	}

	public void set_dstFormatBpp(int dstFormat_bpp) {
		this.dstFormatBpp = dstFormat_bpp;
	}

	public int get_srcFormatBpp() {
		return srcFormatBpp;
	}

	public void set_srcFormatBpp(int srcFormat_bpp) {
		this.srcFormatBpp = srcFormat_bpp;
	}

	public int get_chrSrcHSubSample() {
		return chrSrcHSubSample;
	}

	public void set_chrSrcHSubSample(int chrSrcHSubSample) {
		this.chrSrcHSubSample = chrSrcHSubSample;
	}

	public int get_chrSrcVSubSample() {
		return chrSrcVSubSample;
	}

	public void set_chrSrcVSubSample(int chrSrcVSubSample) {
		this.chrSrcVSubSample = chrSrcVSubSample;
	}

	public int get_chrDstHSubSample() {
		return chrDstHSubSample;
	}

	public void set_chrDstHSubSample(int chrDstHSubSample) {
		this.chrDstHSubSample = chrDstHSubSample;
	}

	public int get_chrDstVSubSample() {
		return chrDstVSubSample;
	}

	public void set_chrDstVSubSample(int chrDstVSubSample) {
		this.chrDstVSubSample = chrDstVSubSample;
	}

	public int get_vChrDrop() {
		return vChrDrop;
	}

	public void set_vChrDrop(int vChrDrop) {
		this.vChrDrop = vChrDrop;
	}

	public int get_sliceDir() {
		return sliceDir;
	}

	public void set_sliceDir(int sliceDir) {
		this.sliceDir = sliceDir;
	}

	public double[] get_param() {
		return param;
	}

	public double get_param(int i) {
		return param[i];
	}

	public void set_param(double[] param) {
		this.param = param;
	}

	public int[] get_pal_yuv() {
		return pal_yuv;
	}

	public int get_pal_yuv(int i) {
		return pal_yuv[i];
	}

	public void set_pal_yuv(int[] pal_yuv) {
		this.pal_yuv = pal_yuv;
	}

	public int[] get_pal_rgb() {
		return pal_rgb;
	}

	public void set_pal_rgb(int[] pal_rgb) {
		this.pal_rgb = pal_rgb;
	}

	public int[] get_lumPixBuf() {
		return lumPixBuf;
	}

	public int get_lumPixBuf(int i) {
		return lumPixBuf[i];
	}

	public void set_lumPixBuf(int[] lumPixBuf) {
		this.lumPixBuf = lumPixBuf;
	}

	public int[] get_chrUPixBuf() {
		return chrUPixBuf;
	}

	public int get_chrUPixBuf(int i) {
		return chrUPixBuf[i];
	}

	public void set_chrUPixBuf(int[] chrUPixBuf) {
		this.chrUPixBuf = chrUPixBuf;
	}

	public int[] get_chrVPixBuf() {
		return chrVPixBuf;
	}

	public int get_chrVPixBuf(int i) {
		return chrVPixBuf[i];
	}

	public void set_chrVPixBuf(int[] chrVPixBuf) {
		this.chrVPixBuf = chrVPixBuf;
	}

	public int[] get_alpPixBuf() {
		return alpPixBuf;
	}

	public int get_alpPixBuf(int i) {
		return alpPixBuf[i];
	}

	public void set_alpPixBuf(int[] alpPixBuf) {
		this.alpPixBuf = alpPixBuf;
	}

	public int get_vLumBufSize() {
		return vLumBufSize;
	}

	public void set_vLumBufSize(int vLumBufSize) {
		this.vLumBufSize = vLumBufSize;
	}

	public int get_vChrBufSize() {
		return vChrBufSize;
	}

	public void set_vChrBufSize(int vChrBufSize) {
		this.vChrBufSize = vChrBufSize;
	}

	public int get_lastInLumBuf() {
		return lastInLumBuf;
	}

	public void set_lastInLumBuf(int lastInLumBuf) {
		this.lastInLumBuf = lastInLumBuf;
	}

	public int get_lastInChrBuf() {
		return lastInChrBuf;
	}

	public void set_lastInChrBuf(int lastInChrBuf) {
		this.lastInChrBuf = lastInChrBuf;
	}

	public int get_lumBufIndex() {
		return lumBufIndex;
	}

	public void set_lumBufIndex(int lumBufIndex) {
		this.lumBufIndex = lumBufIndex;
	}

	public int get_chrBufIndex() {
		return chrBufIndex;
	}

	public void set_chrBufIndex(int chrBufIndex) {
		this.chrBufIndex = chrBufIndex;
	}

	public short [] get_formatConvBuffer() {
		return formatConvBuffer;
	}

	public short get_formatConvBuffer(int i) {
		return formatConvBuffer[i];
	}

	public void set_formatConvBuffer(short [] formatConvBuffer) {
		this.formatConvBuffer = formatConvBuffer;
	}

	public int[] get_hLumFilter() {
		return hLumFilter;
	}

	public int get_hLumFilter(int i) {
		return hLumFilter[i];
	}

	public void set_hLumFilter(int[] hLumFilter) {
		this.hLumFilter = hLumFilter;
	}

	public int[] get_hChrFilter() {
		return hChrFilter;
	}

	public int get_hChrFilter(int i) {
		return hChrFilter[i];
	}

	public void set_hChrFilter(int[] hChrFilter) {
		this.hChrFilter = hChrFilter;
	}

	public int[] get_vLumFilter() {
		return vLumFilter;
	}

	public int get_vLumFilter(int i) {
		return vLumFilter[i];
	}

	public void set_vLumFilter(int[] vLumFilter) {
		this.vLumFilter = vLumFilter;
	}

	public int[] get_vChrFilter() {
		return vChrFilter;
	}

	public int get_vChrFilter(int i) {
		return vChrFilter[i];
	}

	public void set_vChrFilter(int[] vChrFilter) {
		this.vChrFilter = vChrFilter;
	}
	

	public String get_swScale() {
		return swScale;
	}


	public void set_swScale(String swScale) {
		this.swScale = swScale;
	}


	public int[] get_hLumFilterPos() {
		return hLumFilterPos;
	}

	public int get_hLumFilterPos(int i) {
		return hLumFilterPos[i];
	}

	public void set_hLumFilterPos(int[] hLumFilterPos) {
		this.hLumFilterPos = hLumFilterPos;
	}

	public int[] get_hChrFilterPos() {
		return hChrFilterPos;
	}

	public int get_hChrFilterPos(int i) {
		return hChrFilterPos[i];
	}

	public void set_hChrFilterPos(int[] hChrFilterPos) {
		this.hChrFilterPos = hChrFilterPos;
	}

	public int[] get_vLumFilterPos() {
		return vLumFilterPos;
	}

	private int get_vLumFilterPos(int i) {
		return vLumFilterPos[i];
	}
	
	public void set_vLumFilterPos(int[] vLumFilterPos) {
		this.vLumFilterPos = vLumFilterPos;
	}

	public int[] get_vChrFilterPos() {
		return vChrFilterPos;
	}

	public int get_vChrFilterPos(int i) {
		return vChrFilterPos[i];
	}

	public void set_vChrFilterPos(int[] vChrFilterPos) {
		this.vChrFilterPos = vChrFilterPos;
	}

	public int get_hLumFilterSize() {
		return hLumFilterSize;
	}

	public void set_hLumFilterSize(int hLumFilterSize) {
		this.hLumFilterSize = hLumFilterSize;
	}

	public int get_hChrFilterSize() {
		return hChrFilterSize;
	}

	public void set_hChrFilterSize(int hChrFilterSize) {
		this.hChrFilterSize = hChrFilterSize;
	}

	public int get_vLumFilterSize() {
		return vLumFilterSize;
	}

	public void set_vLumFilterSize(int vLumFilterSize) {
		this.vLumFilterSize = vLumFilterSize;
	}

	public int get_vChrFilterSize() {
		return vChrFilterSize;
	}

	public void set_vChrFilterSize(int vChrFilterSize) {
		this.vChrFilterSize = vChrFilterSize;
	}

	public int get_lumMmx2FilterCodeSize() {
		return lumMmx2FilterCodeSize;
	}

	public void set_lumMmx2FilterCodeSize(int lumMmx2FilterCodeSize) {
		this.lumMmx2FilterCodeSize = lumMmx2FilterCodeSize;
	}

	public int get_chrMmx2FilterCodeSize() {
		return chrMmx2FilterCodeSize;
	}

	public void set_chrMmx2FilterCodeSize(int chrMmx2FilterCodeSize) {
		this.chrMmx2FilterCodeSize = chrMmx2FilterCodeSize;
	}

	public byte[] get_lumMmx2FilterCode() {
		return lumMmx2FilterCode;
	}

	public void set_lumMmx2FilterCode(byte[] lumMmx2FilterCode) {
		this.lumMmx2FilterCode = lumMmx2FilterCode;
	}

	public byte[] get_chrMmx2FilterCode() {
		return chrMmx2FilterCode;
	}

	public void set_chrMmx2FilterCode(byte[] chrMmx2FilterCode) {
		this.chrMmx2FilterCode = chrMmx2FilterCode;
	}

	public int get_canMMX2BeUsed() {
		return canMMX2BeUsed;
	}

	public void set_canMMX2BeUsed(int canMMX2BeUsed) {
		this.canMMX2BeUsed = canMMX2BeUsed;
	}

	public int get_dstY() {
		return dstY;
	}

	public void set_dstY(int dstY) {
		this.dstY = dstY;
	}

	public int get_flags() {
		return flags;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public Object get_yuvTable() {
		return yuvTable;
	}

	public void set_yuvTable(Object yuvTable) {
		this.yuvTable = yuvTable;
	}

	public short[] get_table_rV() {
		return table_rV;
	}

	public void set_table_rV(short[] table_rV) {
		this.table_rV = table_rV;
	}

	public short[] get_table_gU() {
		return table_gU;
	}

	public void set_table_gU(short[] table_gU) {
		this.table_gU = table_gU;
	}

	public int[] get_table_gV() {
		return table_gV;
	}

	public void set_table_gV(int[] table_gV) {
		this.table_gV = table_gV;
	}

	public short[] get_table_bU() {
		return table_bU;
	}

	public void set_table_bU(short[] table_bU) {
		this.table_bU = table_bU;
	}

	public int get_contrast() {
		return contrast;
	}

	public void set_contrast(int contrast) {
		this.contrast = contrast;
	}

	public int get_brightness() {
		return brightness;
	}

	public void set_brightness(int brightness) {
		this.brightness = brightness;
	}

	public int get_saturation() {
		return saturation;
	}

	public void set_saturation(int saturation) {
		this.saturation = saturation;
	}

	public int[] get_srcColorspaceTable() {
		return srcColorspaceTable;
	}

	public void set_srcColorspaceTable(int[] srcColorspaceTable) {
		this.srcColorspaceTable = Arrays.copyOf(srcColorspaceTable, srcColorspaceTable.length);
	}

	public int[] get_dstColorspaceTable() {
		return dstColorspaceTable;
	}

	public void set_dstColorspaceTable(int[] dst_colorspaceTable) {
		this.dstColorspaceTable = Arrays.copyOf(dst_colorspaceTable, dst_colorspaceTable.length);
	}

	public int get_srcRange() {
		return srcRange;
	}

	public void set_srcRange(int srcRange) {
		this.srcRange = srcRange;
	}

	public int get_dstRange() {
		return dstRange;
	}

	public void set_dstRange(int dstRange) {
		this.dstRange = dstRange;
	}

	public int get_yuv2rgb_y_offset() {
		return yuv2rgb_y_offset;
	}

	public void set_yuv2rgb_y_offset(int yuv2rgb_y_offset) {
		this.yuv2rgb_y_offset = yuv2rgb_y_offset;
	}

	public int get_yuv2rgb_y_coeff() {
		return yuv2rgb_y_coeff;
	}

	public void set_yuv2rgb_y_coeff(int yuv2rgb_y_coeff) {
		this.yuv2rgb_y_coeff = yuv2rgb_y_coeff;
	}

	public int get_yuv2rgb_v2r_coeff() {
		return yuv2rgb_v2r_coeff;
	}

	public void set_yuv2rgb_v2r_coeff(int yuv2rgb_v2r_coeff) {
		this.yuv2rgb_v2r_coeff = yuv2rgb_v2r_coeff;
	}

	public int get_yuv2rgb_v2g_coeff() {
		return yuv2rgb_v2g_coeff;
	}

	public void set_yuv2rgb_v2g_coeff(int yuv2rgb_v2g_coeff) {
		this.yuv2rgb_v2g_coeff = yuv2rgb_v2g_coeff;
	}

	public int get_yuv2rgb_u2g_coeff() {
		return yuv2rgb_u2g_coeff;
	}

	public void set_yuv2rgb_u2g_coeff(int yuv2rgb_u2g_coeff) {
		this.yuv2rgb_u2g_coeff = yuv2rgb_u2g_coeff;
	}

	public int get_yuv2rgb_u2b_coeff() {
		return yuv2rgb_u2b_coeff;
	}

	public void set_yuv2rgb_u2b_coeff(int yuv2rgb_u2b_coeff) {
		this.yuv2rgb_u2b_coeff = yuv2rgb_u2b_coeff;
	}

	public long get_redDither() {
		return redDither;
	}

	public void set_redDither(long redDither) {
		this.redDither = redDither;
	}

	public long get_greenDither() {
		return greenDither;
	}

	public void set_greenDither(long greenDither) {
		this.greenDither = greenDither;
	}

	public long get_blueDither() {
		return blueDither;
	}

	public void set_blueDither(long blueDither) {
		this.blueDither = blueDither;
	}

	public long get_yCoeff() {
		return yCoeff;
	}

	public void set_yCoeff(long yCoeff) {
		this.yCoeff = yCoeff;
	}

	public long get_vrCoeff() {
		return vrCoeff;
	}

	public void set_vrCoeff(long vrCoeff) {
		this.vrCoeff = vrCoeff;
	}

	public long get_ubCoeff() {
		return ubCoeff;
	}

	public void set_ubCoeff(long ubCoeff) {
		this.ubCoeff = ubCoeff;
	}

	public long get_vgCoeff() {
		return vgCoeff;
	}

	public void set_vgCoeff(long vgCoeff) {
		this.vgCoeff = vgCoeff;
	}

	public long get_ugCoeff() {
		return ugCoeff;
	}

	public void set_ugCoeff(long ugCoeff) {
		this.ugCoeff = ugCoeff;
	}

	public long get_yOffset() {
		return yOffset;
	}

	public void set_yOffset(long yOffset) {
		this.yOffset = yOffset;
	}

	public long get_uOffset() {
		return uOffset;
	}

	public void set_uOffset(long uOffset) {
		this.uOffset = uOffset;
	}

	public long get_vOffset() {
		return vOffset;
	}

	public void set_vOffset(long vOffset) {
		this.vOffset = vOffset;
	}

	public int[] get_lumMmxFilter() {
		return lumMmxFilter;
	}

	public void set_lumMmxFilter(int[] lumMmxFilter) {
		this.lumMmxFilter = lumMmxFilter;
	}

	public int[] get_chrMmxFilter() {
		return chrMmxFilter;
	}

	public void set_chrMmxFilter(int[] chrMmxFilter) {
		this.chrMmxFilter = chrMmxFilter;
	}

	public int get_dstW() {
		return dstW;
	}

	public void set_dstW(int dstW) {
		this.dstW = dstW;
	}

	public long get_esp() {
		return esp;
	}

	public void set_esp(long esp) {
		this.esp = esp;
	}

	public long get_vRounder() {
		return vRounder;
	}

	public void set_vRounder(long vRounder) {
		this.vRounder = vRounder;
	}

	public long get_u_temp() {
		return u_temp;
	}

	public void set_u_temp(long u_temp) {
		this.u_temp = u_temp;
	}

	public long get_v_temp() {
		return v_temp;
	}

	public void set_v_temp(long v_temp) {
		this.v_temp = v_temp;
	}

	public long get_y_temp() {
		return y_temp;
	}

	public void set_y_temp(long y_temp) {
		this.y_temp = y_temp;
	}

	public int[] get_dither16() {
		return dither16;
	}

	public void set_dither16(int[] dither16) {
		this.dither16 = dither16;
	}

	public int[] get_dither32() {
		return dither32;
	}

	public void set_dither32(int[] dither32) {
		this.dither32 = dither32;
	}

	public int get_needs_hcscale() {
		return needs_hcscale;
	}

	public void set_needs_hcscale(int needs_hcscale) {
		this.needs_hcscale = needs_hcscale;
	}
    
    /* function pointers for swScale() */
    int yuv2yuv1(int[] lumSrcPtr, int[] chrUSrcPtr, int[] chrVSrcPtr, int[] alpBuf, short[] dest,
            	 short[] uDest, short[] vDest, short[] aDest, int dstW, int chrDstW, 
            	 byte[] lumDither, byte[] chrDither) {
    	return -1;
    }
    
    int yuv2yuvX(int[] is, int[] lumSrcPtr, int lumFilterSize, int[] is2, 
    		     int[] chrUSrcPtr, int[] chrVSrcPtr, int chrFilterSize, int[] alpSrcPtr,
    		     short[] dest, short[] uDest, short[] vDest, short[] aDest, int dstW, 
    		     int chrDstW, byte[] lumDither, byte[] chrDither){
    	return -1;
    }
    
    int yuv2packed1(int[] lumSrcPtr, int[] chrUSrcPtr, int[] is, int[] chrVSrcPtr, 
    		        int[] is2, int[] is3, short[] dest, int dstW, 
    		        int uvalpha, PixelFormat dstFormat, int flags, int y) {
    	return -1;
    }
    
    int yuv2packed2(int [] buf0, int [] buf1, int [] ubuf0, int [] ubuf1,
    		int [] vbuf0, int [] vbuf1, int [] abuf0, int [] abuf1,
            short[] dest, int dstW, int yalpha, int uvalpha, int y) {
    	return -1;
	}
    
    int yuv2packedX(int[] lumFilter, int[] lumSrc, int lumFilterSize, int[] chrFilter, 
		     int [] chrUSrc, int[] chrVSrc, int chrFilterSize, int[] alpSrc,
		     short[] dest, int dstW, int dstY){
    	return -1;
    };

    void lumToYV12(short[] formatConvBuffer2, short[] src1, int width, int[] pal) { ///< Unscaled conversion of luma plane to YV12 for horizontal scaler.
    	if (lumToYV12.equals("bswap16Y_c"))
    		SwScale.bswap16Y_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("yuy2ToY_c"))
    		SwScale.yuy2ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("uyvyToY_c"))
    		SwScale.uyvyToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr24ToY_c"))
    		SwScale.bgr24ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr16leToY_c"))
    		SwScale.bgr16leToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr16beToY_c"))
    		SwScale.bgr16beToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr15leToY_c"))
    		SwScale.bgr15leToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr15beToY_c"))
    		SwScale.bgr15beToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb24ToY_c"))
    		SwScale.rgb24ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb16leToY_c"))
    		SwScale.rgb16leToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb16beToY_c"))
    		SwScale.rgb16beToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb15leToY_c"))
    		SwScale.rgb15leToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb15beToY_c"))
    		SwScale.rgb15beToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("palToY_c"))
    		SwScale.palToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("monoblack2Y_c"))
    		SwScale.monoblack2Y_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("monowhite2Y_c"))
    		SwScale.monowhite2Y_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr32ToY_c"))
    		SwScale.bgr32ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr321ToY_c"))
    		SwScale.bgr321ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb32ToY_c"))
    		SwScale.rgb32ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb321ToY_c"))
    		SwScale.rgb321ToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb48BEToY_c"))
    		SwScale.rgb48BEToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("rgb48LEToY_c"))
    		SwScale.rgb48LEToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr48BEToY_c"))
    		SwScale.bgr48BEToY_c(formatConvBuffer2, src1, width, pal);
    	else if (lumToYV12.equals("bgr48LEToY_c"))
    		SwScale.bgr48LEToY_c(formatConvBuffer2, src1, width, pal);
    };
    
    void alpToYV12(short[] formatConvBuffer2, short[] src1, int width, int[] pal) { ///< Unscaled conversion of alpha plane to YV12 for horizontal scaler.
    	if (alpToYV12.equals("rgbaToA_c"))
    		SwScale.rgbaToA_c(formatConvBuffer2, src1, width, pal);
    	else if (alpToYV12.equals("abgrToA_c"))
    		SwScale.abgrToA_c(formatConvBuffer2, src1, width, pal);
    	else if (alpToYV12.equals("uyvyToY_c"))
    		SwScale.uyvyToY_c(formatConvBuffer2, src1, width, pal);
    	else if (alpToYV12.equals("palToA_c"))
    		SwScale.palToA_c(formatConvBuffer2, src1, width, pal);
    	
    };
    
    void chrToYV12(short [] dstU, short [] dstV, short [] src1, short [] src2, int width, int [] pal) { ///< Unscaled conversion of chroma planes to YV12 for horizontal scaler.
    	if (chrToYV12.equals("yuy2ToUV_c"))
    		SwScale.yuy2ToUV_c(dstU, dstV, src1, src2, width, pal);
    	else if  (chrToYV12.equals("uyvyToUV_c"))
    		SwScale.uyvyToUV_c(dstU, dstV, src1, src2, width, pal);
    	else if  (chrToYV12.equals("nv12ToUV_c"))
    		SwScale.nv12ToUV_c(dstU, dstV, src1, src2, width, pal);
    	else if  (chrToYV12.equals("nv21ToUV_c"))
    		SwScale.nv21ToUV_c(dstU, dstV, src1, src2, width, pal);
    	else if  (chrToYV12.equals("palToUV_c"))
    		SwScale.palToUV_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("bswap16UV_c"))
    		SwScale.bswap16UV_c(dstU, dstV, src1, src2, width, pal);   
    	
    	else if  (chrToYV12.equals("rgb48BEToUV_half_c"))
    		SwScale.rgb48BEToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb48LEToUV_half_c"))
    		SwScale.rgb48LEToUV_half_c(dstU, dstV, src1, src2, width, pal);
    	else if  (chrToYV12.equals("bgr48BEToUV_half_c"))
    		SwScale.bgr48BEToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr48LEToUV_half_c"))
    		SwScale.bgr48LEToUV_half_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("bgr32ToUV_half_c"))
    		SwScale.bgr32ToUV_half_c(dstU, dstV, src1, src2, width, pal);      	
    	else if  (chrToYV12.equals("bgr321ToUV_half_c"))
    		SwScale.bgr321ToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr24ToUV_half_c"))
    		SwScale.bgr24ToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr16leToUV_half_c"))
    		SwScale.bgr16leToUV_half_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("bgr16beToUV_half_c"))
    		SwScale.bgr16beToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr15leToUV_half_c"))
    		SwScale.bgr15leToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr15beToUV_half_c"))
    		SwScale.bgr15beToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb32ToUV_half_c"))
    		SwScale.rgb32ToUV_half_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("rgb321ToUV_half_c"))
    		SwScale.rgb321ToUV_half_c(dstU, dstV, src1, src2, width, pal);    
    	else if  (chrToYV12.equals("rgb24ToUV_half_c"))
    		SwScale.rgb24ToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb16leToUV_half_c"))
    		SwScale.rgb16leToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb16beToUV_half_c"))
    		SwScale.rgb16beToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb15leToUV_half_c"))
    		SwScale.rgb15leToUV_half_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("rgb15beToUV_half_c"))
    		SwScale.rgb15beToUV_half_c(dstU, dstV, src1, src2, width, pal); 
    	
    	else if  (chrToYV12.equals("rgb48BEToUV_c"))
    		SwScale.rgb48BEToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb48LEToUV_c"))
    		SwScale.rgb48LEToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr48BEToUV_c"))
    		SwScale.bgr48BEToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr48LEToUV_c"))
    		SwScale.bgr48LEToUV_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("bgr32ToUV_c"))
    		SwScale.bgr32ToUV_c(dstU, dstV, src1, src2, width, pal);    
    	else if  (chrToYV12.equals("bgr321ToUV_c"))
    		SwScale.bgr321ToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr24ToUV_c"))
    		SwScale.bgr24ToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr16leToUV_c"))
    		SwScale.bgr16leToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr16beToUV_c"))
    		SwScale.bgr16beToUV_c(dstU, dstV, src1, src2, width, pal);  
    	else if  (chrToYV12.equals("bgr15leToUV_c"))
    		SwScale.bgr15leToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("bgr15beToUV_c"))
    		SwScale.bgr15beToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb32ToUV_c"))
    		SwScale.rgb32ToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb321ToUV_c"))
    		SwScale.rgb321ToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb24ToUV_c"))
    		SwScale.rgb24ToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb16leToUV_c"))
    		SwScale.rgb16leToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb16beToUV_c"))
    		SwScale.rgb16beToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb15leToUV_c"))
    		SwScale.rgb15leToUV_c(dstU, dstV, src1, src2, width, pal); 
    	else if  (chrToYV12.equals("rgb15beToUV_c"))
    		SwScale.rgb15beToUV_c(dstU, dstV, src1, src2, width, pal); 
    	    	
    };
    


	void hyscale_fast(int [] dst, int dstW, int [] src, int srcW, int xInc) {
    	if (hyscale_fast.equals("hyscale_fast_c" ))
    		SwScale.hyscale_fast_c(dst, dstW, src, srcW, xInc);
    };
    
    void hcscale_fast(int [] dst1, int [] dst2, int dstWidth, short [] src1, 
    		short [] src2, int srcW, int xInc) { 
    	if (hcscale_fast.equals("hcscale_fast_c" ))
    		SwScale.hcscale_fast_c(dst1, dst2, dstWidth, src1, src2, srcW, xInc);
    };
    

    void hScale(int [] dst, int dstW, int [] src, int srcW, int xInc, int [] filter, 
    		   int [] filterPos, int filterSize) {
    	if (hScale.equals("hScale_c" ))
    		SwScale.hScale_c(dst, dstW, src, srcW, xInc, filter, filterPos, filterSize);
    	if (hScale.equals("hScale16_c" ))
    		SwScale.hScale16_c(this, dst, dstW, src, srcW, xInc, filter, filterPos, filterSize);
    };
    

    void hScale16(int [] dst, int dstW, int [] src, int srcW, int xInc, int [] filter, 
    			 int [] filterPos, long filterSize, int shift) {
    	if (hScale16.equals("hScale16NX_c" ))
    		SwScale.hScale16NX_c(dst, dstW, src, srcW, xInc, filter, filterPos, filterSize, shift);
    	else if (hScale16.equals("hScale16N_c" ))
    		SwScale.hScale16N_c(dst, dstW, src, srcW, xInc, filter, filterPos, filterSize, shift);
    };
    
    void lumConvertRange(int [] dst, int width) { ///< Color range conversion function for luma plane if needed.
    	if (lumConvertRange.equals("lumRangeFromJpeg_c" ))
    		SwScale.lumRangeFromJpeg_c(dst, width);
    	else if (lumConvertRange.equals("lumRangeToJpeg_c" ))
    		SwScale.lumRangeToJpeg_c(dst, width);
    	else if (lumConvertRange.equals("lumRangeFromJpeg16_c" ))
    		SwScale.lumRangeFromJpeg16_c(dst, width);
    	else if (lumConvertRange.equals("lumRangeToJpeg16_c" ))
    		SwScale.lumRangeToJpeg16_c(dst, width);
    };
    
    void chrConvertRange(int [] dstU, int [] dstV, int width) { ///< Color range conversion function for chroma planes if needed.
    	if (chrConvertRange.equals("chrRangeFromJpeg_c" ))
    		SwScale.chrRangeFromJpeg_c(dstU, dstV, width);
    	else if (chrConvertRange.equals("chrRangeToJpeg_c" ))
    		SwScale.chrRangeToJpeg_c(dstU, dstV, width);
    	else if (chrConvertRange.equals("chrRangeFromJpeg16_c" ))
    		SwScale.chrRangeFromJpeg16_c(dstU, dstV, width);
    	else if (chrConvertRange.equals("chrRangeToJpeg16_c" ))
    		SwScale.chrRangeToJpeg16_c(dstU, dstV, width);
    };
	
	
	

	public int sws_scale(short[][] src, int[] srcStride, int srcSliceY, int srcSliceH, short[][] dst,
			int[] dstStride) {
	    int i;
	    short [][] src2 = {src[0], src[1], src[2], src[3]};
	    short [][] dst2 = {dst[0], dst[1], dst[2], dst[3]};

	    // do not mess up sliceDir if we have a "trailing" 0-size slice
	    if (srcSliceH == 0)
	        return 0;

	    if (SwScaleUnscaled.check_image_pointers(src, get_srcFormat(), srcStride) == 0) {
	        Log.av_log("SwsContext", Log.AV_LOG_ERROR, "bad src image pointers\n");
	        return 0;
	    }
	    
	    if (SwScaleUnscaled.check_image_pointers(dst, get_dstFormat(), dstStride) == 0) {
	    	Log.av_log("SwsContext", Log.AV_LOG_ERROR, "bad dst image pointers\n");
	        return 0;
	    }

	    if ( (sliceDir == 0) && (srcSliceY != 0) && (srcSliceY + srcSliceH != srcH) ) {
	    	Log.av_log("SwsContext", Log.AV_LOG_ERROR, "Slices start in the middle!\n");
	        return 0;
	    }
	    if (sliceDir == 0) {
	        if (srcSliceY == 0) 
	        	sliceDir = 1; 
	        else 
	        	sliceDir = -1;
	    }

	    if (SwScaleInternal.usePal(srcFormat)) {
	        for (i = 0 ; i < 256 ; i++) {
	            int p, r, g, b, y, u, v, a = 0xff;
	            if (srcFormat == PixelFormat.PIX_FMT_PAL8) {
	            	p = src[1][i];
	                a = (p>>24)&0xFF;
	                r = (p>>16)&0xFF;
	                g = (p>> 8)&0xFF;
	                b =  p     &0xFF;
	            } else if (srcFormat == PixelFormat.PIX_FMT_RGB8) {
	                r = (i>>5    )*36;
	                g = ((i>>2)&7)*36;
	                b = (i&3     )*85;
	            } else if (srcFormat == PixelFormat.PIX_FMT_BGR8) {
	                b = (i>>6    )*85;
	                g = ((i>>3)&7)*36;
	                r = (i&7     )*36;
	            } else if(srcFormat == PixelFormat.PIX_FMT_RGB4_BYTE) {
	                r = (i>>3    )*255;
	                g = ((i>>1)&3)*85;
	                b = (i&1     )*255;
	            } else if ( (srcFormat == PixelFormat.PIX_FMT_GRAY8) || (srcFormat == PixelFormat.PIX_FMT_GRAY8A) ) {
	                r = g = b = i;
	            } else {
	                b = (i>>3    )*255;
	                g = ((i>>1)&3)*85;
	                r = (i&1     )*255;
	            }
	            y = Common.av_clip_uint8((SwScaleUnscaled.RY*r + SwScaleUnscaled.GY*g + SwScaleUnscaled.BY*b + ( 33<<(SwScaleUnscaled.RGB2YUV_SHIFT-1)))>>SwScaleUnscaled.RGB2YUV_SHIFT);
	            u = Common.av_clip_uint8((SwScaleUnscaled.RU*r + SwScaleUnscaled.GU*g + SwScaleUnscaled.BU*b + (257<<(SwScaleUnscaled.RGB2YUV_SHIFT-1)))>>SwScaleUnscaled.RGB2YUV_SHIFT);
	            v = Common.av_clip_uint8((SwScaleUnscaled.RV*r + SwScaleUnscaled.GV*g + SwScaleUnscaled.BV*b + (257<<(SwScaleUnscaled.RGB2YUV_SHIFT-1)))>>SwScaleUnscaled.RGB2YUV_SHIFT);
	            pal_yuv[i]= y + (u<<8) + (v<<16) + (a<<24);

	            switch(dstFormat) {
	            //case PixFmt.PIX_FMT_BGR32:
	            case PIX_FMT_RGBA:
	            case PIX_FMT_RGB24:
	                pal_rgb[i]=  r + (g<<8) + (b<<16) + (a<<24);
	                break;
	            case PIX_FMT_ARGB:
	            //case PIX_FMT_BGR32_1:
	                pal_rgb[i]= a + (r<<8) + (g<<16) + (b<<24);
	                break;
	            case PIX_FMT_ABGR:
	            //case PIX_FMT_RGB32_1:
	                pal_rgb[i]= a + (b<<8) + (g<<16) + (r<<24);
	                break;
	            case PIX_FMT_BGRA:
	            //case PIX_FMT_RGB32:
	            case PIX_FMT_BGR24:
	            default:
	                pal_rgb[i]=  b + (g<<8) + (r<<16) + (a<<24);
	            }
	        }
	    }

	    // copy strides, so they can safely be modified
	    if (sliceDir == 1) {
	        // slices go from top to bottom
	        int [] srcStride2 = {srcStride[0], srcStride[1], srcStride[2], srcStride[3]};
	        int [] dstStride2 = {dstStride[0], dstStride[1], dstStride[2], dstStride[3]};

	        SwScaleUnscaled.reset_ptr(src2, srcFormat);
	        SwScaleUnscaled.reset_ptr(dst2, dstFormat);

	        /* reset slice direction at end of frame */
	        if (srcSliceY + srcSliceH == srcH)
	            sliceDir = 0;

	        return swScale(src2, srcStride2, srcSliceY, srcSliceH, dst2, dstStride2);
	    } else {
	        // slices go from bottom to top => we flip the image internally
	        int [] srcStride2 = {-srcStride[0], -srcStride[1], -srcStride[2], -srcStride[3]};
	        int [] dstStride2 = {-dstStride[0], -dstStride[1], -dstStride[2], -dstStride[3]};

	        src2[0] = Arrays.copyOfRange(src2[0], (srcSliceH-1)*srcStride[0], src2[0].length);
	        if (!SwScaleInternal.usePal(srcFormat))
	            src2[1] = Arrays.copyOfRange(src2[1], ((srcSliceH>>chrSrcVSubSample)-1)*srcStride[1], src2[1].length);
	        src2[2] = Arrays.copyOfRange(src2[2], ((srcSliceH>>chrSrcVSubSample)-1)*srcStride[2], src2[2].length);
	        src2[3] = Arrays.copyOfRange(src2[3], (srcSliceH-1)*srcStride[3], src2[3].length);
	        
	        dst2[0] = Arrays.copyOfRange(dst2[0], ( dstH                    -1)*dstStride[0], dst2[0].length);
	        dst2[1] = Arrays.copyOfRange(dst2[1], ((dstH>>chrDstVSubSample)-1)*dstStride[1], dst2[1].length);
	        dst2[2] = Arrays.copyOfRange(dst2[2], ((dstH>>chrDstVSubSample)-1)*dstStride[2], dst2[2].length);
	        dst2[3] = Arrays.copyOfRange(dst2[3], ( dstH                    -1)*dstStride[3], dst2[3].length);

	        SwScaleUnscaled.reset_ptr(src2, srcFormat);
	        SwScaleUnscaled.reset_ptr(dst2, dstFormat);

	        /* reset slice direction at end of frame */
	        if (srcSliceY == 0)
	            sliceDir = 0;

	        return swScale(src2, srcStride2, srcH-srcSliceY-srcSliceH, srcSliceH, dst2, dstStride2);
	    }
	}

	public void sws_freeContext() {
		// TODO Jerome		
	}

	public void set_param(int i, double d) {
		this.param[i] = d;
	}

	public int sws_setColorspaceDetails(int[] inv_table, int srcRange, int[] table,
			int dstRange, int brightness, int contrast, int saturation) {
	    set_srcColorspaceTable(inv_table);
	    set_dstColorspaceTable(table);

	    set_brightness(brightness);
	    set_contrast(contrast);
	    set_saturation(saturation);
	    set_srcRange(srcRange);
	    set_dstRange(dstRange);
	    if (SwScaleInternal.isYUV(dstFormat) || SwScaleInternal.isGray(dstFormat)) 
	    	return -1;

	    set_dstFormatBpp(PixDesc.av_get_bits_per_pixel(PixDesc.av_pix_fmt_descriptors.get(dstFormat)));
	    set_srcFormatBpp(PixDesc.av_get_bits_per_pixel(PixDesc.av_pix_fmt_descriptors.get(srcFormat)));

	    Yuv2Rgb.ff_yuv2rgb_c_init_tables(this, inv_table, srcRange, brightness, contrast, saturation);

	    /*if (HAVE_ALTIVEC && av_get_cpu_flags() & AV_CPU_FLAG_ALTIVEC)
	        ff_yuv2rgb_init_tables_altivec(c, inv_table, brightness, contrast, saturation);*/
	    return 0;
	}

	public int sws_init_context(SwsFilter srcFilter, SwsFilter dstFilter) {
		// TODO Jerome: mmx stuff
	    int i, j;
	    boolean usesVFilter, usesHFilter;
	    boolean unscaled;
	    SwsFilter dummyFilter = new SwsFilter();
	    int srcW = get_srcW();
	    int srcH = get_srcH();
	    int dstW = get_dstW();
	    int dstH = get_dstH();
	    int dst_stride = Common.FFALIGN(dstW * 2 + 66, 16);
	    int flags, cpu_flags;
	    PixelFormat srcFormat = get_srcFormat();
	    PixelFormat dstFormat = get_dstFormat();

	    cpu_flags = Cpu.av_get_cpu_flags();
	    flags     = get_flags();
	    //emms_c();
	    
	    unscaled = ( (srcW == dstW) && (srcH == dstH) );

	    if (!UtilsScale.isSupportedIn(srcFormat)) {
	        Log.av_log("SwsContext", Log.AV_LOG_ERROR, "%s is not supported as input pixel format\n", PixDesc.av_get_pix_fmt_name(srcFormat));
	        return Error.AVERROR(Error.EINVAL);
	    }
	    if (!UtilsScale.isSupportedOut(dstFormat)) {
	    	Log.av_log("SwsContext", Log.AV_LOG_ERROR, "%s is not supported as output pixel format\n", PixDesc.av_get_pix_fmt_name(dstFormat));
	        return Error.AVERROR(Error.EINVAL);
	    }

	    i = flags & ( SwScale.SWS_POINT | SwScale.SWS_AREA  | SwScale.SWS_BILINEAR | 
	    		      SwScale.SWS_FAST_BILINEAR | SwScale.SWS_BICUBIC |
	    		      SwScale.SWS_X | SwScale.SWS_GAUSS | SwScale.SWS_LANCZOS | 
	    		      SwScale.SWS_SINC | SwScale.SWS_SPLINE | SwScale.SWS_BICUBLIN);
	    
	    if ( (i == 0) || ((i & (i-1)) != 0) ) {
	    	Log.av_log("SwsContext", Log.AV_LOG_ERROR, "Exactly one scaler algorithm must be chosen\n");
	        return Error.AVERROR(Error.EINVAL);
	    }
	    /* sanity check */
	    if ( (srcW < 4) || 
	    	 (srcH < 1) || 
	    	 (dstW < 8) || 
	    	 (dstH < 1) ) { 
	    	Log.av_log("SwsContext", Log.AV_LOG_ERROR, "%dx%d -> %dx%d is invalid scaling dimension\n",
	               srcW, srcH, dstW, dstH);
	        return Error.AVERROR(Error.EINVAL);
	    }

	    if (dstFilter == null) 
	    	dstFilter = dummyFilter;
	    if (srcFilter == null) 
	    	srcFilter = dummyFilter;

	    set_lumXInc(((srcW<<16) + (dstW>>1))/dstW);
	    set_lumYInc(((srcH<<16) + (dstH>>1))/dstH);
	    set_dstFormatBpp(PixDesc.av_get_bits_per_pixel(PixDesc.av_pix_fmt_descriptors.get(dstFormat)));
	    set_srcFormatBpp(PixDesc.av_get_bits_per_pixel(PixDesc.av_pix_fmt_descriptors.get(srcFormat)));
	    set_vRounder(4* 0x0001000100010001L);

	    usesVFilter = ( (srcFilter.get_lumV() != null) && (srcFilter.get_lumV().length > 1) ) ||
	                  ( (srcFilter.get_chrV() != null) && (srcFilter.get_chrV().length > 1) ) ||
	                  ( (dstFilter.get_lumV() != null) && (dstFilter.get_lumV().length > 1) ) ||
	                  ( (dstFilter.get_chrV() != null) && (dstFilter.get_chrV().length > 1) );
	    usesHFilter = ( (srcFilter.get_lumH() != null) && (srcFilter.get_lumH().length > 1) ) ||
	                  ( (srcFilter.get_chrH() != null) && (srcFilter.get_chrH().length > 1) ) ||
	                  ( (dstFilter.get_lumH() != null) && (dstFilter.get_lumH().length > 1) ) ||
	                  ( (dstFilter.get_chrH() != null) && (dstFilter.get_chrH().length > 1) );

	    OutII ret_obj = getSubSampleFactors(srcFormat);
	    set_chrSrcHSubSample(ret_obj.get_val1());
	    set_chrSrcVSubSample(ret_obj.get_val2());
	    
	    ret_obj = getSubSampleFactors(dstFormat);
	    set_chrDstHSubSample(ret_obj.get_val1());
	    set_chrDstVSubSample(ret_obj.get_val2());

	    // reuse chroma for 2 pixels RGB/BGR unless user wants full chroma interpolation
	    if ( (SwScaleInternal.isAnyRGB(dstFormat)) && 
	         ( (flags & SwScale.SWS_FULL_CHR_H_INT) == 0 ) )
	    	set_chrDstHSubSample(1);

	    // drop some chroma lines if the user wants it
	    set_vChrDrop((flags & SwScale.SWS_SRC_V_CHR_DROP_MASK) >> SwScale.SWS_SRC_V_CHR_DROP_SHIFT);
	    set_chrSrcVSubSample(get_chrSrcVSubSample() + get_vChrDrop());

	    // drop every other pixel for chroma calculation unless user wants full chroma
	    if ( (SwScaleInternal.isAnyRGB(srcFormat)) && 
	    	 ( (flags & SwScale.SWS_FULL_CHR_H_INP) == 0) &&
	         (srcFormat != PixelFormat.PIX_FMT_RGB8) && 
	         (srcFormat != PixelFormat.PIX_FMT_BGR8) &&
	         (srcFormat != PixelFormat.PIX_FMT_RGB4) &&
	         (srcFormat != PixelFormat.PIX_FMT_BGR4) &&
	         (srcFormat != PixelFormat.PIX_FMT_RGB4_BYTE) && 
	         (srcFormat != PixelFormat.PIX_FMT_BGR4_BYTE) &&
	         ( ( (dstW >> get_chrDstHSubSample()) <= (srcW>>1) ) || ( (flags & SwScale.SWS_FAST_BILINEAR) != 0)) )
	        set_chrSrcHSubSample(1);

	    // Note the -((-x)>>y) is so that we always round toward +inf.
	    set_chrSrcW(-((-srcW) >> get_chrSrcHSubSample()));
	    set_chrSrcH(-((-srcH) >> get_chrSrcVSubSample()));
	    set_chrDstW(-((-dstW) >> get_chrDstHSubSample()));
	    set_chrDstH(-((-dstH) >> get_chrDstVSubSample()));

	    /* unscaled special cases */
	   /* if ( unscaled && !usesHFilter && !usesVFilter && 
	    	( (get_srcRange() == get_dstRange()) || SwsScaleInternal.isAnyRGB(dstFormat) ) ) {
	        ff_get_unscaled_swscale();

	        if (get_swScale) {
	            if (flags&SWS_PRINT_INFO)
	            	Log.av_log("SwsContext", Log.AV_LOG_INFO, "using unscaled %s -> %s special converter\n",
	                       av_get_pix_fmt_name(srcFormat), av_get_pix_fmt_name(dstFormat));
	            return 0;
	        }
	    }*/

	    set_scalingBpp((Mathematics.FFMAX(PixDesc.av_pix_fmt_descriptors.get(srcFormat).get_comp(0).get_depth_minus1(),
	    								  PixDesc.av_pix_fmt_descriptors.get(dstFormat).get_comp(0).get_depth_minus1()) >= 15) ? 16 : 8);

	    if (get_scalingBpp() == 16)
	        dst_stride <<= 1;
	    
	    set_formatConvBuffer(new short[Common.FFALIGN(srcW*2+18, 76)*2]);
	    

	 /*   if (HAVE_MMX2 && cpu_flags & AV_CPU_FLAG_MMX2 && c->scalingBpp == 8) {
	        c->canMMX2BeUsed= (dstW >=srcW && (dstW&31)==0 && (srcW&15)==0) ? 1 : 0;
	        if (!c->canMMX2BeUsed && dstW >=srcW && (srcW&15)==0 && (flags&SWS_FAST_BILINEAR)) {
	            if (flags&SWS_PRINT_INFO)
	            	Log.av_log("SwsContext", Log.AV_LOG_INFO, "output width is not a multiple of 32 -> no MMX2 scaler\n");
	        }
	        if (usesHFilter || isNBPS(c->srcFormat) || is16BPS(c->srcFormat) || isAnyRGB(c->srcFormat)) c->canMMX2BeUsed=0;
	    }
	    else
	        c->canMMX2BeUsed=0;*/
	    set_canMMX2BeUsed(0);

	    set_chrXInc( ((get_chrSrcW() << 16) + (get_chrDstW() >> 1)) / get_chrDstW() );
	    set_chrYInc( ((get_chrSrcH() << 16) + (get_chrDstH() >> 1)) / get_chrDstH() );

	    // match pixel 0 of the src to pixel 0 of dst and match pixel n-2 of src to pixel n-2 of dst
	    // but only for the FAST_BILINEAR mode otherwise do correct scaling
	    // n-2 is the last chrominance sample available
	    // this is not perfect, but no one should notice the difference, the more correct variant
	    // would be like the vertical one, but that would require some special code for the
	    // first and last pixel
	   /* if (flags&SWS_FAST_BILINEAR) {
	        if (c->canMMX2BeUsed) {
	            c->lumXInc+= 20;
	            c->chrXInc+= 20;
	        }
	        //we don't use the x86 asm scaler if MMX is available
	        else if (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX && c->scalingBpp == 8) {
	            c->lumXInc = ((srcW-2)<<16)/(dstW-2) - 20;
	            c->chrXInc = ((c->chrSrcW-2)<<16)/(c->chrDstW-2) - 20;
	        }
	    }*/

	    /* precalculate horizontal scaler filter coefficients */
/*	#if HAVE_MMX2
	// can't downscale !!!
	        if (c->canMMX2BeUsed && (flags & SWS_FAST_BILINEAR)) {
	            c->lumMmx2FilterCodeSize = initMMX2HScaler(      dstW, c->lumXInc, NULL, NULL, NULL, 8);
	            c->chrMmx2FilterCodeSize = initMMX2HScaler(c->chrDstW, c->chrXInc, NULL, NULL, NULL, 4);

	#ifdef MAP_ANONYMOUS
	            c->lumMmx2FilterCode = mmap(NULL, c->lumMmx2FilterCodeSize, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
	            c->chrMmx2FilterCode = mmap(NULL, c->chrMmx2FilterCodeSize, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
	#elif HAVE_VIRTUALALLOC
	            c->lumMmx2FilterCode = VirtualAlloc(NULL, c->lumMmx2FilterCodeSize, MEM_COMMIT, PAGE_EXECUTE_READWRITE);
	            c->chrMmx2FilterCode = VirtualAlloc(NULL, c->chrMmx2FilterCodeSize, MEM_COMMIT, PAGE_EXECUTE_READWRITE);
	#else
	            c->lumMmx2FilterCode = av_malloc(c->lumMmx2FilterCodeSize);
	            c->chrMmx2FilterCode = av_malloc(c->chrMmx2FilterCodeSize);
	#endif 

	#ifdef MAP_ANONYMOUS
	            if (c->lumMmx2FilterCode == MAP_FAILED || c->chrMmx2FilterCode == MAP_FAILED)
	#else
	            if (!c->lumMmx2FilterCode || !c->chrMmx2FilterCode)
	#endif
	                return AVERROR(ENOMEM);
	            FF_ALLOCZ_OR_GOTO(c, c->hLumFilter   , (dstW        /8+8)*sizeof(int16_t), fail);
	            FF_ALLOCZ_OR_GOTO(c, c->hChrFilter   , (c->chrDstW  /4+8)*sizeof(int16_t), fail);
	            FF_ALLOCZ_OR_GOTO(c, c->hLumFilterPos, (dstW      /2/8+8)*sizeof(int32_t), fail);
	            FF_ALLOCZ_OR_GOTO(c, c->hChrFilterPos, (c->chrDstW/2/4+8)*sizeof(int32_t), fail);

	            initMMX2HScaler(      dstW, c->lumXInc, c->lumMmx2FilterCode, c->hLumFilter, c->hLumFilterPos, 8);
	            initMMX2HScaler(c->chrDstW, c->chrXInc, c->chrMmx2FilterCode, c->hChrFilter, c->hChrFilterPos, 4);

	#ifdef MAP_ANONYMOUS
	            mprotect(c->lumMmx2FilterCode, c->lumMmx2FilterCodeSize, PROT_EXEC | PROT_READ);
	            mprotect(c->chrMmx2FilterCode, c->chrMmx2FilterCodeSize, PROT_EXEC | PROT_READ);
	#endif
	        } else
	#endif /* HAVE_MMX2 */
	       
	           /* const int filterAlign=
	                (HAVE_MMX     && cpu_flags & AV_CPU_FLAG_MMX) ? 4 :
	                (HAVE_ALTIVEC && cpu_flags & AV_CPU_FLAG_ALTIVEC) ? 8 :
	                1;*/
    	int filterAlign = 1;
    	
    	
	    OutOOOI ret_obj2 = UtilsScale.initFilter(get_lumXInc(), srcW, dstW, 
	    		filterAlign, 1<<14, ((flags & SwScale.SWS_BICUBLIN) != 0) ? (flags|SwScale.SWS_BICUBIC)  : flags, 
    		   	cpu_flags,	srcFilter.get_lumH(), dstFilter.get_lumH(), get_param());
	    set_hLumFilter((int[]) ret_obj2.get_obj1());
	    set_hLumFilterPos((int[]) ret_obj2.get_obj2());
	    set_hLumFilterSize((Integer) ret_obj2.get_obj3());
	    int ret = ret_obj2.get_ret();
	    
	    if (ret < 0)
	    	return -1;
    	
	    ret_obj2 = UtilsScale.initFilter(get_chrXInc(), get_chrSrcW(), get_chrDstW(), 
	    		filterAlign, 1<<14, ((flags & SwScale.SWS_BICUBLIN) != 0) ? (flags|SwScale.SWS_BILINEAR)  : flags, 
    		   	cpu_flags,	srcFilter.get_chrH(), dstFilter.get_chrH(), get_param());
	    set_hChrFilter((int[]) ret_obj2.get_obj1());
	    set_hChrFilterPos((int[]) ret_obj2.get_obj2());
	    set_hChrFilterSize((Integer) ret_obj2.get_obj3());
	    ret = ret_obj2.get_ret();

	    if (ret < 0)
	    	return -1;

	    /* precalculate vertical scaler filter coefficients */

	    /* filterAlign=
	            (HAVE_MMX     && cpu_flags & AV_CPU_FLAG_MMX) && (flags & SWS_ACCURATE_RND) ? 2 :
	            (HAVE_ALTIVEC && cpu_flags & AV_CPU_FLAG_ALTIVEC) ? 8 :
	            1; */
    	filterAlign = 1;	            

	    ret_obj2 = UtilsScale.initFilter(get_lumYInc(), srcH, dstH, 
	    		filterAlign, 1<<12, ((flags & SwScale.SWS_BICUBLIN) != 0) ? (flags|SwScale.SWS_BICUBIC)  : flags, 
    		   	cpu_flags,	srcFilter.get_lumV(), dstFilter.get_lumV(), get_param());
	    set_vLumFilter((int[]) ret_obj2.get_obj1());
	    set_vLumFilterPos((int[]) ret_obj2.get_obj2());
	    set_vLumFilterSize((Integer) ret_obj2.get_obj3());
	    ret = ret_obj2.get_ret();

	    if (ret < 0)
	    	return -1;
    	
	    ret_obj2 = UtilsScale.initFilter(get_chrYInc(), get_chrSrcH(), get_chrDstH(), 
	    		filterAlign, 1<<12, ((flags & SwScale.SWS_BICUBLIN) != 0) ? (flags|SwScale.SWS_BILINEAR)  : flags, 
    		   	cpu_flags,	srcFilter.get_chrV(), dstFilter.get_chrV(), get_param());
	    set_vChrFilter((int[]) ret_obj2.get_obj1());
	    set_vChrFilterPos((int[]) ret_obj2.get_obj2());
	    set_vChrFilterSize((Integer) ret_obj2.get_obj3());
	    ret = ret_obj2.get_ret();


/*	#if HAVE_ALTIVEC
	        FF_ALLOC_OR_GOTO(c, c->vYCoeffsBank, sizeof (vector signed short)*c->vLumFilterSize*c->dstH, fail);
	        FF_ALLOC_OR_GOTO(c, c->vCCoeffsBank, sizeof (vector signed short)*c->vChrFilterSize*c->chrDstH, fail);

	        for (i=0;i<c->vLumFilterSize*c->dstH;i++) {
	            int j;
	            short *p = (short *)&c->vYCoeffsBank[i];
	            for (j=0;j<8;j++)
	                p[j] = c->vLumFilter[i];
	        }

	        for (i=0;i<c->vChrFilterSize*c->chrDstH;i++) {
	            int j;
	            short *p = (short *)&c->vCCoeffsBank[i];
	            for (j=0;j<8;j++)
	                p[j] = c->vChrFilter[i];
	        }*/


	    // calculate buffer sizes so that they won't run out while handling these damn slices
	    set_vLumBufSize(get_vLumFilterSize());
	    set_vChrBufSize(get_vChrFilterSize());
	    for (i = 0 ; i < dstH ; i++) {
	        int chrI = i * get_chrDstH() / dstH;
	        int nextSlice = (int) Mathematics.FFMAX(get_vLumFilterPos(i) + get_vLumFilterSize() - 1,
	                                          ( (get_vChrFilterPos(chrI) + get_vChrFilterSize() - 1) << get_chrSrcVSubSample() ));

	        nextSlice>>= get_chrSrcVSubSample();
	        nextSlice<<= get_chrSrcVSubSample();
	        if ( get_vLumFilterPos(i) + get_vLumBufSize() < nextSlice)
	            set_vLumBufSize(nextSlice - get_vLumFilterPos(i));
	        if ( get_vChrFilterPos(chrI) + get_vChrBufSize() < (nextSlice >> get_chrSrcVSubSample()) )
	            set_vChrBufSize( (nextSlice >> get_chrSrcVSubSample()) - get_vChrFilterPos(chrI));
	    }

	    // allocate pixbufs (we use dynamic allocation because otherwise we would need to
	    // allocate several megabytes to handle all possible cases)
	    set_lumPixBuf(new int[get_vLumBufSize() * 2]);
	    set_chrUPixBuf(new int[get_vChrBufSize() * 2]);
	    set_chrVPixBuf(new int[get_vChrBufSize() * 2]);
	    
	    if (Config.CONFIG_SWSCALE_ALPHA && SwScaleInternal.isALPHA(get_srcFormat()) && 
	    	SwScaleInternal.isALPHA(get_dstFormat()))
	        set_alpPixBuf(new int[get_vLumBufSize() * 2]);
	    //Note we need at least one pixel more at the end because of the MMX code (just in case someone wanna replace the 4000/8000)
	    /* align at 16 bytes for AltiVec */
	    /*for (i = 0 ; i < get_vLumBufSize() ; i++) {
	        set_lumPixBuf(i + get_vLumBufSize(), new int[dst_stride+1]);
	        set_lumPixBuf(i, get_lumPixBuf(i + get_vLumBufSize());
	    }*/
	    set_uv_off(dst_stride >> 1);
	    set_uv_offx2(dst_stride);
	    
	    /*for (i=0; i<c->vChrBufSize; i++) {
	        FF_ALLOC_OR_GOTO(c, c->chrUPixBuf[i+c->vChrBufSize], dst_stride*2+1, fail);
	        c->chrUPixBuf[i] = c->chrUPixBuf[i+c->vChrBufSize];
	        c.get_chrV()PixBuf[i] = c.get_chrV()PixBuf[i+c->vChrBufSize] = c->chrUPixBuf[i] + (dst_stride >> 1);
	    }
	    if (CONFIG_SWSCALE_ALPHA && c->alpPixBuf)
	        for (i=0; i<c->vLumBufSize; i++) {
	            FF_ALLOCZ_OR_GOTO(c, c->alpPixBuf[i+c->vLumBufSize], dst_stride+1, fail);
	            c->alpPixBuf[i] = c->alpPixBuf[i+c->vLumBufSize];
	        }
*/
	    //try to avoid drawing green stuff between the right end and the stride end
	   /* for (i=0; i<c->vChrBufSize; i++)
	        if(av_pix_fmt_descriptors[c->dstFormat].comp[0].depth_minus1 == 15){
	            av_assert0(c->scalingBpp == 16);
	            for(j=0; j<dst_stride/2+1; j++)
	                ((int32_t*)(c->chrUPixBuf[i]))[j] = 1<<18;
	        } else
	            for(j=0; j<dst_stride+1; j++)
	                ((int16_t*)(c->chrUPixBuf[i]))[j] = 1<<14;
*/
/*
	    if (flags&SWS_PRINT_INFO) {
	        if      (flags&SWS_FAST_BILINEAR) av_log("SwsContext", AV_LOG_INFO, "FAST_BILINEAR scaler, ");
	        else if (flags&SWS_BILINEAR)      av_log("SwsContext", AV_LOG_INFO, "BILINEAR scaler, ");
	        else if (flags&SWS_BICUBIC)       av_log("SwsContext", AV_LOG_INFO, "BICUBIC scaler, ");
	        else if (flags&SWS_X)             av_log("SwsContext", AV_LOG_INFO, "Experimental scaler, ");
	        else if (flags&SWS_POINT)         av_log("SwsContext", AV_LOG_INFO, "Nearest Neighbor / POINT scaler, ");
	        else if (flags&SWS_AREA)          av_log("SwsContext", AV_LOG_INFO, "Area Averaging scaler, ");
	        else if (flags&SWS_BICUBLIN)      av_log("SwsContext", AV_LOG_INFO, "luma BICUBIC / chroma BILINEAR scaler, ");
	        else if (flags&SWS_GAUSS)         av_log("SwsContext", AV_LOG_INFO, "Gaussian scaler, ");
	        else if (flags&SWS_SINC)          av_log("SwsContext", AV_LOG_INFO, "Sinc scaler, ");
	        else if (flags&SWS_LANCZOS)       av_log("SwsContext", AV_LOG_INFO, "Lanczos scaler, ");
	        else if (flags&SWS_SPLINE)        av_log("SwsContext", AV_LOG_INFO, "Bicubic spline scaler, ");
	        else                              av_log("SwsContext", AV_LOG_INFO, "ehh flags invalid?! ");

	        Log.av_log("SwsContext", Log.AV_LOG_INFO, "from %s to %s%s ",
	               av_get_pix_fmt_name(srcFormat),
	#ifdef DITHER1XBPP
	               dstFormat == PIX_FMT_BGR555 || dstFormat == PIX_FMT_BGR565 ||
	               dstFormat == PIX_FMT_RGB444BE || dstFormat == PIX_FMT_RGB444LE ||
	               dstFormat == PIX_FMT_BGR444BE || dstFormat == PIX_FMT_BGR444LE ? "dithered " : "",
	#else
	               "",
	#endif
	               av_get_pix_fmt_name(dstFormat));

	        if      (HAVE_MMX2     && cpu_flags & AV_CPU_FLAG_MMX2)    av_log("SwsContext", AV_LOG_INFO, "using MMX2\n");
	        else if (HAVE_AMD3DNOW && cpu_flags & AV_CPU_FLAG_3DNOW)   av_log("SwsContext", AV_LOG_INFO, "using 3DNOW\n");
	        else if (HAVE_MMX      && cpu_flags & AV_CPU_FLAG_MMX)     av_log("SwsContext", AV_LOG_INFO, "using MMX\n");
	        else if (HAVE_ALTIVEC  && cpu_flags & AV_CPU_FLAG_ALTIVEC) av_log("SwsContext", AV_LOG_INFO, "using AltiVec\n");
	        else                                   av_log("SwsContext", AV_LOG_INFO, "using C\n");

	        if (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) {
	            if (c->canMMX2BeUsed && (flags&SWS_FAST_BILINEAR))
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using FAST_BILINEAR MMX2 scaler for horizontal scaling\n");
	            else {
	                if (c->hLumFilterSize==4)
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 4-tap MMX scaler for horizontal luminance scaling\n");
	                else if (c->hLumFilterSize==8)
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 8-tap MMX scaler for horizontal luminance scaling\n");
	                else
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using n-tap MMX scaler for horizontal luminance scaling\n");

	                if (c->hChrFilterSize==4)
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 4-tap MMX scaler for horizontal chrominance scaling\n");
	                else if (c->hChrFilterSize==8)
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 8-tap MMX scaler for horizontal chrominance scaling\n");
	                else
	                	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using n-tap MMX scaler for horizontal chrominance scaling\n");
	            }
	        } else {
	#if HAVE_MMX
	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using x86 asm scaler for horizontal scaling\n");
	#else
	            if (flags & SWS_FAST_BILINEAR)
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using FAST_BILINEAR C scaler for horizontal scaling\n");
	            else
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using C scaler for horizontal scaling\n");
	#endif
	        }
	        if (isPlanarYUV(dstFormat)) {
	            if (c->vLumFilterSize==1)
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 1-tap %s \"scaler\" for vertical scaling (YV12 like)\n",
	                       (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	            else
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using n-tap %s scaler for vertical scaling (YV12 like)\n",
	                       (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	        } else {
	            if (c->vLumFilterSize==1 && c->vChrFilterSize==2)
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 1-tap %s \"scaler\" for vertical luminance scaling (BGR)\n"
	                       "      2-tap scaler for vertical chrominance scaling (BGR)\n",
	                       (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	            else if (c->vLumFilterSize==2 && c->vChrFilterSize==2)
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using 2-tap linear %s scaler for vertical scaling (BGR)\n",
	                       (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	            else
	            	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using n-tap %s scaler for vertical scaling (BGR)\n",
	                       (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	        }

	        if (dstFormat==PIX_FMT_BGR24)
	        	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using %s YV12->BGR24 converter\n",
	                   (HAVE_MMX2 && cpu_flags & AV_CPU_FLAG_MMX2) ? "MMX2" :
	                   ((HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C"));
	        else if (dstFormat==PIX_FMT_RGB32)
	        	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using %s YV12->BGR32 converter\n",
	                   (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	        else if (dstFormat==PIX_FMT_BGR565)
	        	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using %s YV12->BGR16 converter\n",
	                   (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	        else if (dstFormat==PIX_FMT_BGR555)
	        	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using %s YV12->BGR15 converter\n",
	                   (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");
	        else if (dstFormat == PIX_FMT_RGB444BE || dstFormat == PIX_FMT_RGB444LE ||
	                 dstFormat == PIX_FMT_BGR444BE || dstFormat == PIX_FMT_BGR444LE)
	        	Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "using %s YV12->BGR12 converter\n",
	                   (HAVE_MMX && cpu_flags & AV_CPU_FLAG_MMX) ? "MMX" : "C");

	        Log.av_log("SwsContext", Log.AV_LOG_VERBOSE, "%dx%d -> %dx%d\n", srcW, srcH, dstW, dstH);
	        Log.av_log("SwsContext", Log.AV_LOG_DEBUG, "lum srcW=%d srcH=%d dstW=%d dstH=%d xInc=%d yInc=%d\n",
	               c->srcW, c->srcH, c->dstW, c->dstH, c->lumXInc, c->lumYInc);
	        Log.av_log("SwsContext", Log.AV_LOG_DEBUG, "chr srcW=%d srcH=%d dstW=%d dstH=%d xInc=%d yInc=%d\n",
	               c->chrSrcW, c->chrSrcH, c->chrDstW, c->chrDstH, c->chrXInc, c->chrYInc);
	    }*/

	    set_swScale("ff_getSwsFunc");
	    return 0;
	}

	private int swScale(short[][] src2, int [] srcStride, int srcSliceY, int srcSliceH,
       	 short[][] dst2, int [] dstStride) {
		if (swScale == "ff_getSwsFunc")
			return ff_getSwsFunc(src2, srcStride, srcSliceY, srcSliceH, dst2, dstStride);
		else
			return -1;
	}


	private int ff_getSwsFunc(short[][] src2, int[] srcStride, int srcSliceY,
			int srcSliceH, short[][] dst2, int[] dstStride) {
		// TODO Jerome: mmx stuff
	    sws_init_swScale_c();

	   /*if (HAVE_MMX)
	        ff_sws_init_swScale_mmx(c);
	    if (HAVE_ALTIVEC)
	        ff_sws_init_swScale_altivec(c);*/

	    return swScaleInt(src2, srcStride, srcSliceY, srcSliceH, dst2, dstStride);
	}

	private void sws_init_swScale_c() {
		//TODO Jerome
		/*find_c_packed_planar_out_funcs(c, &c->yuv2yuv1, &c->yuv2yuvX,
	                                   &c->yuv2packed1, &c->yuv2packed2,
	                                   &c->yuv2packedX);
*/

	    chrToYV12 = "";
	    switch(srcFormat) {
	        case PIX_FMT_YUYV422  : chrToYV12 = "yuy2ToUV_c"; break;
	        case PIX_FMT_UYVY422  : chrToYV12 = "uyvyToUV_c"; break;
	        case PIX_FMT_NV12     : chrToYV12 = "nv12ToUV_c"; break;
	        case PIX_FMT_NV21     : chrToYV12 = "nv21ToUV_c"; break;
	        case PIX_FMT_RGB8     :
	        case PIX_FMT_BGR8     :
	        case PIX_FMT_PAL8     :
	        case PIX_FMT_BGR4_BYTE:
	        case PIX_FMT_RGB4_BYTE: chrToYV12 = "palToUV_c"; break;
	        case PIX_FMT_YUV444P9BE:
	        case PIX_FMT_YUV420P9BE:
	        case PIX_FMT_YUV444P10BE:
	        case PIX_FMT_YUV422P10BE:
	        case PIX_FMT_YUV420P10BE: hScale16 = "hScale16NX_c"; break;
	        case PIX_FMT_YUV444P9LE:
	        case PIX_FMT_YUV420P9LE:
	        case PIX_FMT_YUV422P10LE:
	        case PIX_FMT_YUV420P10LE:
	        case PIX_FMT_YUV444P10LE: hScale16= "hScale16N_c"; break;
	        case PIX_FMT_YUV420P16BE:
	        case PIX_FMT_YUV422P16BE:
	        case PIX_FMT_YUV444P16BE: chrToYV12 = "bswap16UV_c"; break;
	    }
	    if (chrSrcHSubSample != 0) {
	        switch(srcFormat) {
	        case PIX_FMT_RGB48BE : chrToYV12 = "rgb48BEToUV_half_c"; break;
	        case PIX_FMT_RGB48LE : chrToYV12 = "rgb48LEToUV_half_c"; break;
	        case PIX_FMT_BGR48BE : chrToYV12 = "bgr48BEToUV_half_c"; break;
	        case PIX_FMT_BGR48LE : chrToYV12 = "bgr48LEToUV_half_c"; break;
	        case PIX_FMT_BGRA    : chrToYV12 = "bgr32ToUV_half_c";   break;
	        case PIX_FMT_ABGR    : chrToYV12 = "bgr321ToUV_half_c";  break;
	        case PIX_FMT_BGR24   : chrToYV12 = "bgr24ToUV_half_c";   break;
	        case PIX_FMT_BGR565LE: chrToYV12 = "bgr16leToUV_half_c"; break;
	        case PIX_FMT_BGR565BE: chrToYV12 = "bgr16beToUV_half_c"; break;
	        case PIX_FMT_BGR555LE: chrToYV12 = "bgr15leToUV_half_c"; break;
	        case PIX_FMT_BGR555BE: chrToYV12 = "bgr15beToUV_half_c"; break;
	        case PIX_FMT_RGBA    : chrToYV12 = "rgb32ToUV_half_c";   break;
	        case PIX_FMT_ARGB    : chrToYV12 = "rgb321ToUV_half_c";  break;
	        case PIX_FMT_RGB24   : chrToYV12 = "rgb24ToUV_half_c";   break;
	        case PIX_FMT_RGB565LE: chrToYV12 = "rgb16leToUV_half_c"; break;
	        case PIX_FMT_RGB565BE: chrToYV12 = "rgb16beToUV_half_c"; break;
	        case PIX_FMT_RGB555LE: chrToYV12 = "rgb15leToUV_half_c"; break;
	        case PIX_FMT_RGB555BE: chrToYV12 = "rgb15beToUV_half_c"; break;
	        }
	    } else {
	        switch(srcFormat) {
	        case PIX_FMT_RGB48BE : chrToYV12 = "rgb48BEToUV_c"; break;
	        case PIX_FMT_RGB48LE : chrToYV12 = "rgb48LEToUV_c"; break;
	        case PIX_FMT_BGR48BE : chrToYV12 = "bgr48BEToUV_c"; break;
	        case PIX_FMT_BGR48LE : chrToYV12 = "bgr48LEToUV_c"; break;
	        case PIX_FMT_BGRA    : chrToYV12 = "bgr32ToUV_c";   break;
	        case PIX_FMT_ABGR    : chrToYV12 = "bgr321ToUV_c";  break;
	        case PIX_FMT_BGR24   : chrToYV12 = "bgr24ToUV_c";   break;
	        case PIX_FMT_BGR565LE: chrToYV12 = "bgr16leToUV_c"; break;
	        case PIX_FMT_BGR565BE: chrToYV12 = "bgr16beToUV_c"; break;
	        case PIX_FMT_BGR555LE: chrToYV12 = "bgr15leToUV_c"; break;
	        case PIX_FMT_BGR555BE: chrToYV12 = "bgr15beToUV_c"; break;
	        case PIX_FMT_RGBA    : chrToYV12 = "rgb32ToUV_c";   break;
	        case PIX_FMT_ARGB    : chrToYV12 = "rgb321ToUV_c";  break;
	        case PIX_FMT_RGB24   : chrToYV12 = "rgb24ToUV_c";   break;
	        case PIX_FMT_RGB565LE: chrToYV12 = "rgb16leToUV_c"; break;
	        case PIX_FMT_RGB565BE: chrToYV12 = "rgb16beToUV_c"; break;
	        case PIX_FMT_RGB555LE: chrToYV12 = "rgb15leToUV_c"; break;
	        case PIX_FMT_RGB555BE: chrToYV12 = "rgb15beToUV_c"; break;
	        }
	    }

	    lumToYV12 = "";
	    alpToYV12 = "";
	    switch (srcFormat) {
	    case PIX_FMT_YUV420P16BE:
	    case PIX_FMT_YUV422P16BE:
	    case PIX_FMT_YUV444P16BE:
	    case PIX_FMT_GRAY16BE : lumToYV12 = "bswap16Y_c"; break;
	    case PIX_FMT_YUYV422  :
	    case PIX_FMT_GRAY8A   : lumToYV12 = "yuy2ToY_c"; break;
	    case PIX_FMT_UYVY422  : lumToYV12 = "uyvyToY_c";    break;
	    case PIX_FMT_BGR24    : lumToYV12 = "bgr24ToY_c";   break;
	    case PIX_FMT_BGR565LE : lumToYV12 = "bgr16leToY_c"; break;
	    case PIX_FMT_BGR565BE : lumToYV12 = "bgr16beToY_c"; break;
	    case PIX_FMT_BGR555LE : lumToYV12 = "bgr15leToY_c"; break;
	    case PIX_FMT_BGR555BE : lumToYV12 = "bgr15beToY_c"; break;
	    case PIX_FMT_RGB24    : lumToYV12 = "rgb24ToY_c";   break;
	    case PIX_FMT_RGB565LE : lumToYV12 = "rgb16leToY_c"; break;
	    case PIX_FMT_RGB565BE : lumToYV12 = "rgb16beToY_c"; break;
	    case PIX_FMT_RGB555LE : lumToYV12 = "rgb15leToY_c"; break;
	    case PIX_FMT_RGB555BE : lumToYV12 = "rgb15beToY_c"; break;
	    case PIX_FMT_RGB8     :
	    case PIX_FMT_BGR8     :
	    case PIX_FMT_PAL8     :
	    case PIX_FMT_BGR4_BYTE:
	    case PIX_FMT_RGB4_BYTE: lumToYV12 = "palToY_c"; break;
	    case PIX_FMT_MONOBLACK: lumToYV12 = "monoblack2Y_c"; break;
	    case PIX_FMT_MONOWHITE: lumToYV12 = "monowhite2Y_c"; break;
	    case PIX_FMT_BGRA     : lumToYV12 = "bgr32ToY_c";  break;
	    case PIX_FMT_ABGR     : lumToYV12 = "bgr321ToY_c"; break;
	    case PIX_FMT_RGBA     : lumToYV12 = "rgb32ToY_c";  break;
	    case PIX_FMT_ARGB     : lumToYV12 = "rgb321ToY_c"; break;
	    case PIX_FMT_RGB48BE  : lumToYV12 = "rgb48BEToY_c"; break;
	    case PIX_FMT_RGB48LE  : lumToYV12 = "rgb48LEToY_c"; break;
	    case PIX_FMT_BGR48BE  : lumToYV12 = "bgr48BEToY_c"; break;
	    case PIX_FMT_BGR48LE  : lumToYV12 = "bgr48LEToY_c"; break;
	    }
	    
	    if (alpPixBuf != null) {
	        switch (srcFormat) {
	        case PIX_FMT_BGRA   :
	        case PIX_FMT_RGBA   :  alpToYV12 = "rgbaToA_c"; break;
	        case PIX_FMT_ABGR   :
	        case PIX_FMT_ARGB   :  alpToYV12 = "abgrToA_c"; break;
	        case PIX_FMT_GRAY8A : alpToYV12 = "uyvyToY_c"; break;
	        case PIX_FMT_PAL8   : alpToYV12 = "palToA_c"; break;
	        }
	    }

	    if ( ( SwScaleInternal.isAnyRGB(srcFormat) && (PixDesc.av_pix_fmt_descriptors.get(srcFormat).get_comp(0).get_depth_minus1() < 15) ) || 
	    	 (srcFormat == PixelFormat.PIX_FMT_PAL8) )
	        hScale16 = "hScale16N_c";

	    if (scalingBpp == 8) {
	    	hScale = "hScale_c";
		    if ( (flags & SwScale.SWS_FAST_BILINEAR) != 0) {
		        hyscale_fast = "hyscale_fast_c";
		        hcscale_fast = "hcscale_fast_c";
		    }

		    if ( (srcRange != dstRange) && !SwScaleInternal.isAnyRGB(dstFormat)) {
		        if (srcRange != 0) {
		            lumConvertRange = "lumRangeFromJpeg_c";
		            chrConvertRange = "chrRangeFromJpeg_c";
		        } else {
		            lumConvertRange = "lumRangeToJpeg_c";
		            chrConvertRange = "chrRangeToJpeg_c";
		        }
		    }
	    } else {
	        if ( (hScale16 == "hScale16NX_c") && !SwScaleInternal.isAnyRGB(srcFormat) ){
	            chrToYV12 = "bswap16UV_c";
	            lumToYV12 = "bswap16Y_c";
	        }
	        hScale16 = "";
	        hScale = "hScale16_c";
	        scale19To15Fw = "scale19To15Fw_c";
	        scale8To16Rv  = "scale8To16Rv_c";

	        if ( (srcRange != dstRange) && 
	        	 !SwScaleInternal.isAnyRGB(dstFormat) ) {
	            if (srcRange != 0) {
	                lumConvertRange = "lumRangeFromJpeg16_c";
	                chrConvertRange = "chrRangeFromJpeg16_c";
	            } else {
	                lumConvertRange = "lumRangeToJpeg16_c";
	                chrConvertRange = "chrRangeToJpeg16_c";
	            }
	        }
	    }

	    if ( ! (SwScaleInternal.isGray(srcFormat) || 
	    		SwScaleInternal.isGray(dstFormat) ||
	            (srcFormat == PixelFormat.PIX_FMT_MONOBLACK) || 
	            (srcFormat == PixelFormat.PIX_FMT_MONOWHITE) ) )
	        needs_hcscale = 1;
	}


	private int swScaleInt(short[][] src, int [] srcStride, int srcSliceY, int srcSliceH,
       	 short[][] dst, int [] dstStride) {
		int chrSrcSliceY = srcSliceY >> get_chrSrcVSubSample();
	    int chrSrcSliceH = -((-srcSliceH) >> get_chrSrcVSubSample());
	    int lastDstY;

	    boolean should_dither = SwScaleInternal.isNBPS(get_srcFormat()) || SwScaleInternal.is16BPS(get_srcFormat());


	    /* vars which will change and which we need to store back in the context */

	    if (SwScaleInternal.isPacked(get_srcFormat())) {
	        src[0]=
	        src[1]=
	        src[2]=
	        src[3]= src[0];
	        srcStride[0]=
	        srcStride[1]=
	        srcStride[2]=
	        srcStride[3]= srcStride[0];
	    }
	    srcStride[1] <<= get_vChrDrop();
	    srcStride[2] <<= get_vChrDrop();

	   /* DEBUG_BUFFERS("swScale() %p[%d] %p[%d] %p[%d] %p[%d] -> %p[%d] %p[%d] %p[%d] %p[%d]\n",
	                  src[0], srcStride[0], src[1], srcStride[1], src[2], srcStride[2], src[3], srcStride[3],
	                  dst[0], dstStride[0], dst[1], dstStride[1], dst[2], dstStride[2], dst[3], dstStride[3]);
	    DEBUG_BUFFERS("srcSliceY: %d srcSliceH: %d dstY: %d dstH: %d\n",
	                   srcSliceY,    srcSliceH,    dstY,    dstH);
	    DEBUG_BUFFERS("vLumFilterSize: %d vLumBufSize: %d vChrFilterSize: %d vChrBufSize: %d\n",
	                   vLumFilterSize,    vLumBufSize,    vChrFilterSize,    vChrBufSize);*/

	    if ( (dstStride[0]%8 != 0) || (dstStride[1]%8 != 0) || 
	    	 (dstStride[2]%8 != 0) || (dstStride[3]%8 != 0) ) {
	        if ( ((flags & SwScale.SWS_PRINT_INFO) != 0) && !warnedAlready) {
	            Log.av_log("SwsContext", Log.AV_LOG_WARNING, "Warning: dstStride is not aligned!\n" +
	                   "         ->cannot do aligned memory accesses anymore\n");
	            warnedAlready = true;
	        }
	    }

	    /* Note the user might start scaling the picture in the middle so this
	       will not get executed. This is not really intended but works
	       currently, so people might do it. */
	    if (srcSliceY == 0) {
	        lumBufIndex = -1;
	        chrBufIndex = -1;
	        dstY = 0;
	        lastInLumBuf = -1;
	        lastInChrBuf = -1;
	    }

	 /*   if (!should_dither) {
	        set_chrDither8(SwScale.ff_sws_pb_64);
	        set_lumDither8(SwScale.ff_sws_pb_64);
	    }*/
	    
	    lastDstY = dstY;

	    for (; dstY < dstH ; dstY++) {
	    	short [] dest = Arrays.copyOfRange(dst[0], dstStride[0] * dstY, dst[0].length);
	        int chrDstY = dstY >> chrDstVSubSample;
		    short [] uDest = Arrays.copyOfRange(dst[1], dstStride[1] * chrDstY, dst[1].length);
		    short [] vDest = Arrays.copyOfRange(dst[2], dstStride[2] * chrDstY, dst[2].length);
		    short [] aDest = (Config.CONFIG_SWSCALE_ALPHA && (alpPixBuf != null)) ? 
	        			       Arrays.copyOfRange(dst[3], dstStride[3] * dstY, dst[3].length) :
        			    	   null;
	        			       
	        byte [] lumDither = should_dither ? SwScale.dithers[7][dstY   &7] : SwScale.flat64;
	        byte [] chrDither = should_dither ? SwScale.dithers[7][chrDstY&7] : SwScale.flat64;

	        int firstLumSrcY = vLumFilterPos[dstY]; //First line needed as input
	        int firstLumSrcY2 = vLumFilterPos[(int) Mathematics.FFMIN(dstY | ((1<<chrDstVSubSample) - 1), dstH-1)];
	        int firstChrSrcY = vChrFilterPos[chrDstY]; //First line needed as input
	        int lastLumSrcY = firstLumSrcY + vLumFilterSize -1; // Last line needed as input
	        int lastLumSrcY2=firstLumSrcY2 + vLumFilterSize -1; // Last line needed as input
	        int lastChrSrcY= firstChrSrcY + vChrFilterSize -1; // Last line needed as input
	        boolean enough_lines;

	        //handle holes (FAST_BILINEAR & weird filters)
	        if (firstLumSrcY > lastInLumBuf) lastInLumBuf= firstLumSrcY-1;
	        if (firstChrSrcY > lastInChrBuf) lastInChrBuf= firstChrSrcY-1;
	        

	       /* DEBUG_BUFFERS("dstY: %d\n", dstY);
	        DEBUG_BUFFERS("\tfirstLumSrcY: %d lastLumSrcY: %d lastInLumBuf: %d\n",
	                         firstLumSrcY,    lastLumSrcY,    lastInLumBuf);
	        DEBUG_BUFFERS("\tfirstChrSrcY: %d lastChrSrcY: %d lastInChrBuf: %d\n",
	                         firstChrSrcY,    lastChrSrcY,    lastInChrBuf);*/

	        // Do we have enough lines in this slice to output the dstY line
	        enough_lines = lastLumSrcY2 < srcSliceY + srcSliceH && lastChrSrcY < -((-srcSliceY - srcSliceH)>>chrSrcVSubSample);

	        if (!enough_lines) {
	            lastLumSrcY = srcSliceY + srcSliceH - 1;
	            lastChrSrcY = chrSrcSliceY + chrSrcSliceH - 1;
	           /* DEBUG_BUFFERS("buffering slice: lastLumSrcY %d lastChrSrcY %d\n",
	                                            lastLumSrcY, lastChrSrcY);*/
	        }

	        //Do horizontal scaling
	        while(lastInLumBuf < lastLumSrcY) {
	        	short [] src1 = Arrays.copyOfRange(src[0], (lastInLumBuf + 1 - srcSliceY)*srcStride[0], src[0].length);
	        	short [] src2 = Arrays.copyOfRange(src[3], (lastInLumBuf + 1 - srcSliceY)*srcStride[3], src[3].length);
	            lumBufIndex++;

	             hyscale(lumPixBuf[ lumBufIndex ], dstW, src1, srcW, lumXInc,	             
	                    hLumFilter, hLumFilterPos, hLumFilterSize,
	                    formatConvBuffer,
	                    pal_yuv, 0);
	            if (Config.CONFIG_SWSCALE_ALPHA && (alpPixBuf != null))
	                hyscale(alpPixBuf[lumBufIndex], dstW, src2, srcW,
	                        lumXInc, hLumFilter, hLumFilterPos, hLumFilterSize,
	                        formatConvBuffer,
	                        pal_yuv, 1);
	                        
	            lastInLumBuf++;
	            /*DEBUG_BUFFERS("\t\tlumBufIndex %d: lastInLumBuf: %d\n",
	                               lumBufIndex,    lastInLumBuf);*/
	        }
	        while(lastInChrBuf < lastChrSrcY) {
	        	short [] src1 = Arrays.copyOfRange(src[1], (lastInChrBuf + 1 - chrSrcSliceY)*srcStride[1], src[1].length);
	        	short [] src2 = Arrays.copyOfRange(src[2], (lastInChrBuf + 1 - chrSrcSliceY)*srcStride[2], src[2].length);
	            chrBufIndex++;

	            //FIXME replace parameters through context struct (some at least)

	            if (needs_hcscale != 0)
	                hcscale(chrUPixBuf[chrBufIndex], chrVPixBuf[chrBufIndex],
	                          chrDstW, src1, src2, chrSrcW, chrXInc,
	                          hChrFilter, hChrFilterPos, hChrFilterSize,
	                          formatConvBuffer, pal_yuv);
	            lastInChrBuf++;
	           /* DEBUG_BUFFERS("\t\tchrBufIndex %d: lastInChrBuf: %d\n",
	                               chrBufIndex,    lastInChrBuf);*/
	        }
	        //wrap buf index around to stay inside the ring buffer
	        if (lumBufIndex >= vLumBufSize) lumBufIndex-= vLumBufSize;
	        if (chrBufIndex >= vChrBufSize) chrBufIndex-= vChrBufSize;
	        if (!enough_lines)
	            break; //we can't output a dstY line so let's try with the next slice
/*
	#if HAVE_MMX
	        updateMMXDitherTables(c, dstY, lumBufIndex, chrBufIndex, lastInLumBuf, lastInChrBuf);
	#endif*/
	       
	      /* TODO Jerome
	       *  if (dstY >= dstH-2) {
	            // hmm looks like we can't use MMX here without overwriting this array's tail
	            find_c_packed_planar_out_funcs( &yuv2yuv1, &yuv2yuvX,
	                                           &yuv2packed1, &yuv2packed2,
	                                           &yuv2packedX);
	        }*/

	        {
	            int [] lumSrcPtr = Arrays.copyOfRange(lumPixBuf, lumBufIndex + firstLumSrcY - lastInLumBuf + vLumBufSize, lumPixBuf.length);
	            int [] chrUSrcPtr = Arrays.copyOfRange(chrUPixBuf, chrBufIndex + firstChrSrcY - lastInChrBuf + vChrBufSize, chrUPixBuf.length);
	            int [] chrVSrcPtr = Arrays.copyOfRange(chrVPixBuf, chrBufIndex + firstChrSrcY - lastInChrBuf + vChrBufSize, chrVPixBuf.length);
	            int [] alpSrcPtr = (Config.CONFIG_SWSCALE_ALPHA && (alpPixBuf != null)) ? 
	            					 Arrays.copyOfRange(alpPixBuf, lumBufIndex + firstLumSrcY - lastInLumBuf + vLumBufSize, alpPixBuf.length) : 
	            					 null;

	            if ( SwScaleInternal.isPlanarYUV(dstFormat) || (dstFormat == PixelFormat.PIX_FMT_GRAY8) ) { //YV12 like
	                int chrSkipMask = (1 << chrDstVSubSample) - 1;
	                if ( ( (dstY & chrSkipMask) != 0 ) || SwScaleInternal.isGray(dstFormat) )
	                    uDest = vDest = null; 
	                if ( (yuv2yuv1 != "") && (vLumFilterSize == 1) && (vChrFilterSize == 1) ) { // unscaled YV12
	                    int [] alpBuf =  (Config.CONFIG_SWSCALE_ALPHA && (alpPixBuf != null)) ? alpSrcPtr : null;
	                    yuv2yuv1(lumSrcPtr, chrUSrcPtr, chrVSrcPtr, alpBuf, dest, uDest, vDest, aDest, dstW, chrDstW,
	                    		lumDither, chrDither);
	                } else { //General YV12
	                    yuv2yuvX(Arrays.copyOfRange(vLumFilter, dstY * vLumFilterSize, vLumFilter.length),
	                             lumSrcPtr, vLumFilterSize,
	                             Arrays.copyOfRange(vChrFilter, chrDstY * vChrFilterSize, vChrFilter.length),
	                             chrUSrcPtr, chrVSrcPtr, vChrFilterSize,
	                             alpSrcPtr, dest, uDest, vDest, aDest, dstW, chrDstW,
		                    		lumDither, chrDither);
	                }
	            } else {
	                if ( (yuv2packed1 != "") && (vLumFilterSize == 1) && (vChrFilterSize == 2) ) { //unscaled RGB
	                    int chrAlpha = vChrFilter[2 * dstY + 1];
	                    yuv2packed1(lumSrcPtr, chrUSrcPtr,
	                    		   Arrays.copyOfRange(chrUSrcPtr, 1, chrUSrcPtr.length),
	                    		   chrVSrcPtr, 
	                    		   Arrays.copyOfRange(chrVSrcPtr, 1, chrVSrcPtr.length),
	                                alpPixBuf != null ? alpSrcPtr : null,
	                                dest, dstW, chrAlpha, dstFormat, flags, dstY);
	                } else if ( (yuv2packed2 != "") && (vLumFilterSize == 2) && (vChrFilterSize == 2) ) { //bilinear upscale RGB
	                    int lumAlpha = vLumFilter[2 * dstY + 1];
	                    int chrAlpha = vChrFilter[2 * dstY + 1];
	                    lumMmxFilter[2] =
	                    lumMmxFilter[3] = vLumFilter[2 * dstY   ] * 0x10001;
	                    chrMmxFilter[2] =
	                    chrMmxFilter[3] = vChrFilter[2 * chrDstY] * 0x10001;
	                    yuv2packed2(lumSrcPtr, Arrays.copyOfRange(lumSrcPtr, 1, lumSrcPtr.length),
	                    			chrUSrcPtr, Arrays.copyOfRange(chrUSrcPtr, 1, chrUSrcPtr.length),
	                    		    chrVSrcPtr, Arrays.copyOfRange(chrVSrcPtr, 1, chrVSrcPtr.length),
	                                alpPixBuf != null ? alpSrcPtr : null,
	                                alpPixBuf != null ?
	                                	Arrays.copyOfRange(alpSrcPtr, 1, alpSrcPtr.length) : 
	                                	null,
	                                dest, dstW, lumAlpha, chrAlpha, dstY);
	                } else { //general RGB
	                    yuv2packedX(Arrays.copyOfRange(vLumFilter, dstY * vLumFilterSize, vLumFilter.length),
	                                lumSrcPtr, vLumFilterSize,
	                                Arrays.copyOfRange(vChrFilter, dstY * vChrFilterSize, vChrFilter.length),
	                                chrUSrcPtr, chrVSrcPtr, vChrFilterSize,
	                                alpSrcPtr, dest, dstW, dstY);
	                }
	            }
	        }
	    }

	    if ( (dstFormat == PixelFormat.PIX_FMT_YUVA420P) && (alpPixBuf == null) )
	        fillPlane(dst[3], dstStride[3], dstW, dstY-lastDstY, lastDstY, (byte) 255);
/*
	#if HAVE_MMX2
	    if (av_get_cpu_flags() & AV_CPU_FLAG_MMX2)
	        __asm__ volatile("sfence":::"memory");
	#endif
	    emms_c();*/

	    return dstY - lastDstY;
		
	}


	private void yuv2packed1(int [] buf0,
            int [] ubuf0, int [] ubuf1,
           int [] vbuf0, int [] vbuf1,
            int [] abuf0,
            byte [] dest,
            int dstW, int uvalpha, int dstFormat, int flags, int y) {
		
	}


	private void yuv2yuvX(int [] lumFilter, int [] lumSrc, int lumFilterSize,
            int [] chrFilter, int [] chrUSrc,
            int [] chrVSrc, int chrFilterSize,
            int [] alpSrc,
            byte [] dest,
            byte [] uDest, byte [] vDest, byte [] aDest,
            int dstW, int chrDstW, byte [] lumDither, byte [] chrDither) {
		
	}


	private void yuv2yuv1(int [] lumSrc, int [] chrUSrc, int [] chrVSrc, int[] alpSrc, byte[] dest,
			byte [] uDest, byte [] vDest, byte [] aDest,
            int dstW, int chrDstW, byte [] lumDither, byte [] chrDither) {
		
		
	}


	private void fillPlane(short[] dst, int stride, int width, 
			int height, int y, byte val) {
	    int i;
	    int plane_idx = stride*y;
	    
	    for (i = 0 ; i < height ; i++) {
	    	for (int j = plane_idx ; j < (plane_idx + width); j++)
	    		dst[j] = val;
	        plane_idx += stride;
	    }		
	}


	private void hcscale(int dst1, int dst2, int dstWidth, short[] src1, short[] src2,
			int srcW, int xInc, int[] hChrFilter,
			int[] hChrFilterPos, int hChrFilterSize,
			short[] formatConvBuffer2, int[] pal) {
		// TODO: Jerome
	}


	private void hyscale(int dst, int dstWidth, short[] src1, int srcW, int xInc,
			int[] hLumFilter, int[] hLumFilterPos, int hLumFilterSize,
			short [] formatConvBuffer2, int[] pal, int isAlpha) {
		// TODO: Jerome
		String toYV12 = (isAlpha != 0) ? "alpToYV12" : "lumToYV12";
		
		if (isAlpha != 0)
			alpToYV12(formatConvBuffer2, src1, srcW, pal);
		else
			lumToYV12(formatConvBuffer2, src1, srcW, pal);
		src1 = formatConvBuffer2;

	    if ( (PixDesc.av_pix_fmt_descriptors.get(srcFormat).get_comp(0).get_depth_minus1() < 8) && 
	    	 (scalingBpp == 16) && 
	    	 !SwScaleInternal.isAnyRGB(srcFormat) ) {
	        scale8To16Rv(formatConvBuffer2, src1, srcW);
	        src1 = formatConvBuffer2;
	    }
/*
	    if (hScale16 != "") {
	        int shift = isAnyRGB(c->srcFormat) || c->srcFormat==PIX_FMT_PAL8 ? 13 : av_pix_fmt_descriptors[c->srcFormat].comp[0].depth_minus1;
	        c->hScale16(dst, dstWidth, (const uint16_t*)src, srcW, xInc, hLumFilter, hLumFilterPos, hLumFilterSize, shift);
	    } else if (!c->hyscale_fast) {
	        c->hScale(c, dst, dstWidth, src, hLumFilter, hLumFilterPos, hLumFilterSize);
	    } else { // fast bilinear upscale / crap downscale
	        c->hyscale_fast(c, dst, dstWidth, src, srcW, xInc);
	    }
*/
	  
	    /*  if (convertRange)
	        convertRange(dst, dstWidth);
*/
	    /*
	    if ( (PixDesc.av_pix_fmt_descriptors.get(dstFormat).get_comp(0).get_depth_minus1() < 15) && 
	    	 (scalingBpp == 16) ) {
	        scale19To15Fw(dst, UtilsArrays.byte_to_int_be(dst), dstWidth);
	    }*/
		
	}


	private void scale8To16Rv(short[] dst, short[] src1, int len) {
		if (scale8To16Rv.equals("scale8To16Rv_c"))
			SwScale.scale8To16Rv_c(dst, src1, len);
		
	}


	private void scale19To15Fw(short[] dst, int []src, int len) {
		if (scale19To15Fw.equals("scale19To15Fw_c"))
			SwScale.scale19To15Fw_c(dst, src, len);
		
	}


	private OutII getSubSampleFactors(PixelFormat format) {
	    int h = PixDesc.av_pix_fmt_descriptors.get(format).get_log2_chroma_w();
	    int v = PixDesc.av_pix_fmt_descriptors.get(format).get_log2_chroma_h();
		return new OutII(h, v);
	}

    
    
    
    
    

}
