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

package uk.ac.liv.ffmpeg.libavcodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVProfile;
import uk.ac.liv.ffmpeg.libavcodec.mjpeg.MjpegEnc;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.Mpeg12;
import uk.ac.liv.ffmpeg.libavcodec.pcm.PCM_S16LE;
import uk.ac.liv.ffmpeg.libavcodec.pcm.PCM_S24LE;
import uk.ac.liv.ffmpeg.libavcodec.raw.RawDec;
import uk.ac.liv.ffmpeg.libavutil.AVClass;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.ImgUtils;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.DisplayOutput;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOII;

public class AVCodec {
	

	/**
	 * Identify the syntax and semantics of the bitstream.
	 * The principle is roughly:
	 * Two decoders with the same ID can decode the same streams.
	 * Two encoders with the same ID can encode compatible streams.
	 * There may be slight deviations from the principle due to implementation
	 * details.
	 *
	 * If you add a codec ID to this list, add it so that
	 * 1. no value of a existing codec ID changes (that would break ABI),
	 * 2. it is as close as possible to similar codecs.
	 */
	public static enum CodecID {
	    CODEC_ID_NONE,
	
	    /* video codecs */
	    CODEC_ID_MPEG1VIDEO,
	    CODEC_ID_MPEG2VIDEO, ///< preferred ID for MPEG-1/2 video decoding
	    CODEC_ID_MPEG2VIDEO_XVMC,
	    CODEC_ID_H261,
	    CODEC_ID_H263,
	    CODEC_ID_RV10,
	    CODEC_ID_RV20,
	    CODEC_ID_MJPEG,
	    CODEC_ID_MJPEGB,
	    CODEC_ID_LJPEG,
	    CODEC_ID_SP5X,
	    CODEC_ID_JPEGLS,
	    CODEC_ID_MPEG4,
	    CODEC_ID_RAWVIDEO,
	    CODEC_ID_MSMPEG4V1,
	    CODEC_ID_MSMPEG4V2,
	    CODEC_ID_MSMPEG4V3,
	    CODEC_ID_WMV1,
	    CODEC_ID_WMV2,
	    CODEC_ID_H263P,
	    CODEC_ID_H263I,
	    CODEC_ID_FLV1,
	    CODEC_ID_SVQ1,
	    CODEC_ID_SVQ3,
	    CODEC_ID_DVVIDEO,
	    CODEC_ID_HUFFYUV,
	    CODEC_ID_CYUV,
	    CODEC_ID_H264,
	    CODEC_ID_INDEO3,
	    CODEC_ID_VP3,
	    CODEC_ID_THEORA,
	    CODEC_ID_ASV1,
	    CODEC_ID_ASV2,
	    CODEC_ID_FFV1,
	    CODEC_ID_4XM,
	    CODEC_ID_VCR1,
	    CODEC_ID_CLJR,
	    CODEC_ID_MDEC,
	    CODEC_ID_ROQ,
	    CODEC_ID_INTERPLAY_VIDEO,
	    CODEC_ID_XAN_WC3,
	    CODEC_ID_XAN_WC4,
	    CODEC_ID_RPZA,
	    CODEC_ID_CINEPAK,
	    CODEC_ID_WS_VQA,
	    CODEC_ID_MSRLE,
	    CODEC_ID_MSVIDEO1,
	    CODEC_ID_IDCIN,
	    CODEC_ID_8BPS,
	    CODEC_ID_SMC,
	    CODEC_ID_FLIC,
	    CODEC_ID_TRUEMOTION1,
	    CODEC_ID_VMDVIDEO,
	    CODEC_ID_MSZH,
	    CODEC_ID_ZLIB,
	    CODEC_ID_QTRLE,
	    CODEC_ID_SNOW,
	    CODEC_ID_TSCC,
	    CODEC_ID_ULTI,
	    CODEC_ID_QDRAW,
	    CODEC_ID_VIXL,
	    CODEC_ID_QPEG,
	    CODEC_ID_PNG,
	    CODEC_ID_PPM,
	    CODEC_ID_PBM,
	    CODEC_ID_PGM,
	    CODEC_ID_PGMYUV,
	    CODEC_ID_PAM,
	    CODEC_ID_FFVHUFF,
	    CODEC_ID_RV30,
	    CODEC_ID_RV40,
	    CODEC_ID_VC1,
	    CODEC_ID_WMV3,
	    CODEC_ID_LOCO,
	    CODEC_ID_WNV1,
	    CODEC_ID_AASC,
	    CODEC_ID_INDEO2,
	    CODEC_ID_FRAPS,
	    CODEC_ID_TRUEMOTION2,
	    CODEC_ID_BMP,
	    CODEC_ID_CSCD,
	    CODEC_ID_MMVIDEO,
	    CODEC_ID_ZMBV,
	    CODEC_ID_AVS,
	    CODEC_ID_SMACKVIDEO,
	    CODEC_ID_NUV,
	    CODEC_ID_KMVC,
	    CODEC_ID_FLASHSV,
	    CODEC_ID_CAVS,
	    CODEC_ID_JPEG2000,
	    CODEC_ID_VMNC,
	    CODEC_ID_VP5,
	    CODEC_ID_VP6,
	    CODEC_ID_VP6F,
	    CODEC_ID_TARGA,
	    CODEC_ID_DSICINVIDEO,
	    CODEC_ID_TIERTEXSEQVIDEO,
	    CODEC_ID_TIFF,
	    CODEC_ID_GIF,
	    CODEC_ID_FFH264,
	    CODEC_ID_DXA,
	    CODEC_ID_DNXHD,
	    CODEC_ID_THP,
	    CODEC_ID_SGI,
	    CODEC_ID_C93,
	    CODEC_ID_BETHSOFTVID,
	    CODEC_ID_PTX,
	    CODEC_ID_TXD,
	    CODEC_ID_VP6A,
	    CODEC_ID_AMV,
	    CODEC_ID_VB,
	    CODEC_ID_PCX,
	    CODEC_ID_SUNRAST,
	    CODEC_ID_INDEO4,
	    CODEC_ID_INDEO5,
	    CODEC_ID_MIMIC,
	    CODEC_ID_RL2,
	    CODEC_ID_8SVX_EXP,
	    CODEC_ID_8SVX_FIB,
	    CODEC_ID_ESCAPE124,
	    CODEC_ID_DIRAC,
	    CODEC_ID_BFI,
	    CODEC_ID_CMV,
	    CODEC_ID_MOTIONPIXELS,
	    CODEC_ID_TGV,
	    CODEC_ID_TGQ,
	    CODEC_ID_TQI,
	    CODEC_ID_AURA,
	    CODEC_ID_AURA2,
	    CODEC_ID_V210X,
	    CODEC_ID_TMV,
	    CODEC_ID_V210,
	    CODEC_ID_DPX,
	    CODEC_ID_MAD,
	    CODEC_ID_FRWU,
	    CODEC_ID_FLASHSV2,
	    CODEC_ID_CDGRAPHICS,
	    CODEC_ID_R210,
	    CODEC_ID_ANM,
	    CODEC_ID_BINKVIDEO,
	    CODEC_ID_IFF_ILBM,
	    CODEC_ID_IFF_BYTERUN1,
	    CODEC_ID_KGV1,
	    CODEC_ID_YOP,
	    CODEC_ID_VP8,
	    CODEC_ID_PICTOR,
	    CODEC_ID_ANSI,
	    CODEC_ID_A64_MULTI,
	    CODEC_ID_A64_MULTI5,
	    CODEC_ID_R10K,
	    CODEC_ID_MXPEG,
	    CODEC_ID_LAGARITH,
	    CODEC_ID_PRORES,
	    CODEC_ID_JV,
	    CODEC_ID_DFA,
	    CODEC_ID_8SVX_RAW,
	
	    /* various PCM "codecs" */
	    CODEC_ID_PCM_S16LE,
	    CODEC_ID_PCM_S16BE,
	    CODEC_ID_PCM_U16LE,
	    CODEC_ID_PCM_U16BE,
	    CODEC_ID_PCM_S8,
	    CODEC_ID_PCM_U8,
	    CODEC_ID_PCM_MULAW,
	    CODEC_ID_PCM_ALAW,
	    CODEC_ID_PCM_S32LE,
	    CODEC_ID_PCM_S32BE,
	    CODEC_ID_PCM_U32LE,
	    CODEC_ID_PCM_U32BE,
	    CODEC_ID_PCM_S24LE,
	    CODEC_ID_PCM_S24BE,
	    CODEC_ID_PCM_U24LE,
	    CODEC_ID_PCM_U24BE,
	    CODEC_ID_PCM_S24DAUD,
	    CODEC_ID_PCM_ZORK,
	    CODEC_ID_PCM_S16LE_PLANAR,
	    CODEC_ID_PCM_DVD,
	    CODEC_ID_PCM_F32BE,
	    CODEC_ID_PCM_F32LE,
	    CODEC_ID_PCM_F64BE,
	    CODEC_ID_PCM_F64LE,
	    CODEC_ID_PCM_BLURAY,
	    CODEC_ID_PCM_LXF,
	    CODEC_ID_S302M,
	
	    /* various ADPCM codecs */
	    CODEC_ID_ADPCM_IMA_QT,
	    CODEC_ID_ADPCM_IMA_WAV,
	    CODEC_ID_ADPCM_IMA_DK3,
	    CODEC_ID_ADPCM_IMA_DK4,
	    CODEC_ID_ADPCM_IMA_WS,
	    CODEC_ID_ADPCM_IMA_SMJPEG,
	    CODEC_ID_ADPCM_MS,
	    CODEC_ID_ADPCM_4XM,
	    CODEC_ID_ADPCM_XA,
	    CODEC_ID_ADPCM_ADX,
	    CODEC_ID_ADPCM_EA,
	    CODEC_ID_ADPCM_G726,
	    CODEC_ID_ADPCM_CT,
	    CODEC_ID_ADPCM_SWF,
	    CODEC_ID_ADPCM_YAMAHA,
	    CODEC_ID_ADPCM_SBPRO_4,
	    CODEC_ID_ADPCM_SBPRO_3,
	    CODEC_ID_ADPCM_SBPRO_2,
	    CODEC_ID_ADPCM_THP,
	    CODEC_ID_ADPCM_IMA_AMV,
	    CODEC_ID_ADPCM_EA_R1,
	    CODEC_ID_ADPCM_EA_R3,
	    CODEC_ID_ADPCM_EA_R2,
	    CODEC_ID_ADPCM_IMA_EA_SEAD,
	    CODEC_ID_ADPCM_IMA_EA_EACS,
	    CODEC_ID_ADPCM_EA_XAS,
	    CODEC_ID_ADPCM_EA_MAXIS_XA,
	    CODEC_ID_ADPCM_IMA_ISS,
	    CODEC_ID_ADPCM_G722,
	
	    /* AMR */
	    CODEC_ID_AMR_NB,
	    CODEC_ID_AMR_WB,
	
	    /* RealAudio codecs*/
	    CODEC_ID_RA_144,
	    CODEC_ID_RA_288,
	
	    /* various DPCM codecs */
	    CODEC_ID_ROQ_DPCM,
	    CODEC_ID_INTERPLAY_DPCM,
	    CODEC_ID_XAN_DPCM,
	    CODEC_ID_SOL_DPCM,
	
	    /* audio codecs */
	    CODEC_ID_MP2,
	    CODEC_ID_MP3, ///< preferred ID for decoding MPEG audio layer 1, 2 or 3
	    CODEC_ID_AAC,
	    CODEC_ID_AC3,
	    CODEC_ID_DTS,
	    CODEC_ID_VORBIS,
	    CODEC_ID_DVAUDIO,
	    CODEC_ID_WMAV1,
	    CODEC_ID_WMAV2,
	    CODEC_ID_MACE3,
	    CODEC_ID_MACE6,
	    CODEC_ID_VMDAUDIO,
	    CODEC_ID_SONIC,
	    CODEC_ID_SONIC_LS,
	    CODEC_ID_FLAC,
	    CODEC_ID_MP3ADU,
	    CODEC_ID_MP3ON4,
	    CODEC_ID_SHORTEN,
	    CODEC_ID_ALAC,
	    CODEC_ID_WESTWOOD_SND1,
	    CODEC_ID_GSM, ///< as in Berlin toast format
	    CODEC_ID_QDM2,
	    CODEC_ID_COOK,
	    CODEC_ID_TRUESPEECH,
	    CODEC_ID_TTA,
	    CODEC_ID_SMACKAUDIO,
	    CODEC_ID_QCELP,
	    CODEC_ID_WAVPACK,
	    CODEC_ID_DSICINAUDIO,
	    CODEC_ID_IMC,
	    CODEC_ID_MUSEPACK7,
	    CODEC_ID_MLP,
	    CODEC_ID_GSM_MS, /* as found in WAV */
	    CODEC_ID_ATRAC3,
	    CODEC_ID_VOXWARE,
	    CODEC_ID_APE,
	    CODEC_ID_NELLYMOSER,
	    CODEC_ID_MUSEPACK8,
	    CODEC_ID_SPEEX,
	    CODEC_ID_WMAVOICE,
	    CODEC_ID_WMAPRO,
	    CODEC_ID_WMALOSSLESS,
	    CODEC_ID_ATRAC3P,
	    CODEC_ID_EAC3,
	    CODEC_ID_SIPR,
	    CODEC_ID_MP1,
	    CODEC_ID_TWINVQ,
	    CODEC_ID_TRUEHD,
	    CODEC_ID_MP4ALS,
	    CODEC_ID_ATRAC1,
	    CODEC_ID_BINKAUDIO_RDFT,
	    CODEC_ID_BINKAUDIO_DCT,
	    CODEC_ID_AAC_LATM,
	    CODEC_ID_QDMC,
	    CODEC_ID_CELT,
	
	    /* subtitle codecs */
	    CODEC_ID_DVD_SUBTITLE,
	    CODEC_ID_DVB_SUBTITLE,
	    CODEC_ID_TEXT,  ///< raw UTF-8 text
	    CODEC_ID_XSUB,
	    CODEC_ID_SSA,
	    CODEC_ID_MOV_TEXT,
	    CODEC_ID_HDMV_PGS_SUBTITLE,
	    CODEC_ID_DVB_TELETEXT,
	    CODEC_ID_SRT,
	    CODEC_ID_MICRODVD,
	
	    /* other specific kind of codecs (generally used for attachments) */
	    CODEC_ID_TTF,
	
	    CODEC_ID_PROBE, ///< codec_id is not known (like CODEC_ID_NONE) but lavf should attempt to identify it
	
	    CODEC_ID_MPEG2TS, /**< _FAKE_ codec to indicate a raw MPEG-2 TS
	                                * stream (only used by libavformat) */
	    CODEC_ID_FFMETADATA,   ///< Dummy codec for streams containing only metadata information.
	};

	public static enum AVColorPrimaries{
	    AVCOL_PRI_BT709      , ///< also ITU-R BT1361 / IEC 61966-2-4 / SMPTE RP177 Annex B
	    AVCOL_PRI_UNSPECIFIED,
	    AVCOL_PRI_BT470M     ,
	    AVCOL_PRI_BT470BG    , ///< also ITU-R BT601-6 625 / ITU-R BT1358 625 / ITU-R BT1700 625 PAL & SECAM
	    AVCOL_PRI_SMPTE170M  , ///< also ITU-R BT601-6 525 / ITU-R BT1358 525 / ITU-R BT1700 NTSC
	    AVCOL_PRI_SMPTE240M  , ///< functionally identical to above
	    AVCOL_PRI_FILM       ,
	    AVCOL_PRI_NB         , ///< Not part of ABI
	};
	
	public static enum AVColorSpace{
	    AVCOL_SPC_RGB        ,
	    AVCOL_SPC_BT709      , ///< also ITU-R BT1361 / IEC 61966-2-4 xvYCC709 / SMPTE RP177 Annex B
	    AVCOL_SPC_UNSPECIFIED,
	    AVCOL_SPC_FCC        ,
	    AVCOL_SPC_BT470BG    , ///< also ITU-R BT601-6 625 / ITU-R BT1358 625 / ITU-R BT1700 625 PAL & SECAM / IEC 61966-2-4 xvYCC601
	    AVCOL_SPC_SMPTE170M  , ///< also ITU-R BT601-6 525 / ITU-R BT1358 525 / ITU-R BT1700 NTSC / functionally identical to above
	    AVCOL_SPC_SMPTE240M  ,
	    AVCOL_SPC_NB         , ///< Not part of ABI
	};
	
	public static enum AVChromaLocation {
	    AVCHROMA_LOC_UNSPECIFIED,
	    AVCHROMA_LOC_LEFT       , // mpeg2/4, h264 default
	    AVCHROMA_LOC_CENTER     , // mpeg1, jpeg, h263
	    AVCHROMA_LOC_TOPLEFT    , // DV
	    AVCHROMA_LOC_TOP        ,
	    AVCHROMA_LOC_BOTTOMLEFT ,
	    AVCHROMA_LOC_BOTTOM     ,
	    AVCHROMA_LOC_NB         , // Not part of ABI
	};
	
	public static enum AVColorRange {
	    AVCOL_RANGE_UNSPECIFIED,
	    AVCOL_RANGE_MPEG       , // the normal 219*2^(n-8) "MPEG" YUV ranges
	    AVCOL_RANGE_JPEG       , // the normal     2^n-1   "JPEG" YUV ranges
	    AVCOL_RANGE_NB         , // Not part of ABI
	};

	public static enum AVColorTransferCharacteristic{
	    AVCOL_TRC_BT709      , ///< also ITU-R BT1361
	    AVCOL_TRC_UNSPECIFIED,
	    AVCOL_TRC_GAMMA22    , ///< also ITU-R BT470M / ITU-R BT1700 625 PAL & SECAM
	    AVCOL_TRC_GAMMA28    , ///< also ITU-R BT470BG
	    AVCOL_TRC_NB         , ///< Not part of ABI
	};
	
	public static enum AVDiscard {
	    AVDISCARD_NONE,    // discard nothing
	    AVDISCARD_DEFAULT, // discard useless packets like 0 size packets in avi
	    AVDISCARD_NONREF,  // discard all non reference
	    AVDISCARD_BIDIR,   // discard all bidirectional frames
	    AVDISCARD_NONKEY,  // discard all frames except keyframes
	    AVDISCARD_ALL,     // discard all
	};	

	public static enum AVAudioServiceType {
	    AV_AUDIO_SERVICE_TYPE_MAIN              ,
	    AV_AUDIO_SERVICE_TYPE_EFFECTS           ,
	    AV_AUDIO_SERVICE_TYPE_VISUALLY_IMPAIRED ,
	    AV_AUDIO_SERVICE_TYPE_HEARING_IMPAIRED  ,
	    AV_AUDIO_SERVICE_TYPE_DIALOGUE          ,
	    AV_AUDIO_SERVICE_TYPE_COMMENTARY        ,
	    AV_AUDIO_SERVICE_TYPE_EMERGENCY         ,
	    AV_AUDIO_SERVICE_TYPE_VOICE_OVER        ,
	    AV_AUDIO_SERVICE_TYPE_KARAOKE           ,
	    AV_AUDIO_SERVICE_TYPE_NB                , ///< Not part of ABI
	};
	

	public static enum AVPacketSideDataType {
	    AV_PKT_DATA_PALETTE,
	};
	

	public static enum AVSubtitleType {
	    SUBTITLE_NONE,
	
	    SUBTITLE_BITMAP,                ///< A bitmap, pict will be set
	
	    /**
	     * Plain text, the text field must be set by the decoder and is
	     * authoritative. ass and pict fields may contain approximations.
	     */
	    SUBTITLE_TEXT,
	
	    /**
	     * Formatted text, the ass field must be set by the decoder and is
	     * authoritative. pict and text fields may contain approximations.
	     */
	    SUBTITLE_ASS,
	};

	/**
	 * motion estimation type.
	 */
	public static enum Motion_Est_ID {
	    ME_ZERO,    ///< no search, that is use 0,0 vector whenever one is needed
	    ME_FULL,
	    ME_LOG,
	    ME_PHODS,
	    ME_EPZS,        ///< enhanced predictive zonal search
	    ME_X1,          ///< reserved for experiments
	    ME_HEX,         ///< hexagon based search
	    ME_UMH,         ///< uneven multi-hexagon search
	    ME_ITER,        ///< iterative search
	    ME_TESA,        ///< transformed exhaustive search algorithm
	};


	/* in bytes */
	public static final int AVCODEC_MAX_AUDIO_FRAME_SIZE = 192000; // 1 second of 48khz 32bit audio

	public static final int AVPALETTE_SIZE = 1024;
	public static final int AVPALETTE_COUNT = 256;
	
	public static final int FF_MAX_B_FRAMES = 16;
	
	/* encoding support
	   These flags can be passed in AVCodecContext.flags before initialization.
	   Note: Not everything is supported yet.
	*/
	
	public static final int CODEC_FLAG_QSCALE = 0x0002;  ///< Use fixed qscale.
	public static final int CODEC_FLAG_4MV    = 0x0004;  ///< 4 MV per MB allowed / advanced prediction for H.263.
	public static final int CODEC_FLAG_QPEL   = 0x0010;  ///< Use qpel MC.
	public static final int CODEC_FLAG_GMC    = 0x0020;  ///< Use GMC.
	public static final int CODEC_FLAG_MV0    = 0x0040;  ///< Always try a MB with MV=<0,0>.
	public static final int CODEC_FLAG_PART   = 0x0080;  ///< Use data partitioning.
	/**
	 * The parent program guarantees that the input for B-frames containing
	 * streams is not written to for at least s->max_b_frames+1 frames, if
	 * this is not set the input will be copied.
	 */
	public static final int CODEC_FLAG_INPUT_PRESERVED = 0x0100;
	public static final int CODEC_FLAG_PASS1           = 0x0200;   ///< Use internal 2pass ratecontrol in first pass mode.
	public static final int CODEC_FLAG_PASS2           = 0x0400;   ///< Use internal 2pass ratecontrol in second pass mode.
	public static final int CODEC_FLAG_EXTERN_HUFF     = 0x1000;   ///< Use external Huffman table (for MJPEG).
	public static final int CODEC_FLAG_GRAY            = 0x2000;   ///< Only decode/encode grayscale.
	public static final int CODEC_FLAG_EMU_EDGE        = 0x4000;   ///< Don't draw edges.
	public static final int CODEC_FLAG_PSNR            = 0x8000;   ///< error[?] variables will be set during encoding.
	public static final int CODEC_FLAG_TRUNCATED       = 0x00010000; /** Input bitstream might be truncated at a random
	                                                  location instead of only at frame boundaries. */
	public static final int CODEC_FLAG_NORMALIZE_AQP  = 0x00020000; ///< Normalize adaptive quantization.
	public static final int CODEC_FLAG_INTERLACED_DCT = 0x00040000; ///< Use interlaced DCT.
	public static final int CODEC_FLAG_LOW_DELAY      = 0x00080000; ///< Force low delay.
	public static final int CODEC_FLAG_ALT_SCAN       = 0x00100000; ///< Use alternate scan.
	public static final int CODEC_FLAG_GLOBAL_HEADER  = 0x00400000; ///< Place global headers in extradata instead of every keyframe.
	public static final int CODEC_FLAG_BITEXACT       = 0x00800000; ///< Use only bitexact stuff (except (I)DCT).
	/* Fx : Flag for h263+ extra options */
	public static final int CODEC_FLAG_AC_PRED        = 0x01000000; ///< H.263 advanced intra coding / MPEG-4 AC prediction
	public static final int CODEC_FLAG_H263P_UMV      = 0x02000000; ///< unlimited motion vector
	public static final int CODEC_FLAG_CBP_RD         = 0x04000000; ///< Use rate distortion optimization for cbp.
	public static final int CODEC_FLAG_QP_RD          = 0x08000000; ///< Use rate distortion optimization for qp selectioon.
	public static final int CODEC_FLAG_H263P_AIV      = 0x00000008; ///< H.263 alternative inter VLC
	public static final int CODEC_FLAG_OBMC           = 0x00000001; ///< OBMC
	public static final int CODEC_FLAG_LOOP_FILTER    = 0x00000800; ///< loop filter
	public static final int CODEC_FLAG_H263P_SLICE_STRUCT = 0x10000000;
	public static final int CODEC_FLAG_INTERLACED_ME  = 0x20000000; ///< interlaced motion estimation
	public static final int CODEC_FLAG_SVCD_SCAN_OFFSET = 0x40000000; ///< Will reserve space for SVCD scan offset user data.
	public static final int CODEC_FLAG_CLOSED_GOP     = 0x80000000;
	public static final int CODEC_FLAG2_FAST          = 0x00000001; ///< Allow non spec compliant speedup tricks.
	public static final int CODEC_FLAG2_STRICT_GOP    = 0x00000002; ///< Strictly enforce GOP size.
	public static final int CODEC_FLAG2_NO_OUTPUT     = 0x00000004; ///< Skip bitstream encoding.
	public static final int CODEC_FLAG2_LOCAL_HEADER  = 0x00000008; ///< Place global headers at every keyframe instead of in extradata.
	public static final int CODEC_FLAG2_BPYRAMID      = 0x00000010; ///< H.264 allow B-frames to be used as references.
	public static final int CODEC_FLAG2_WPRED         = 0x00000020; ///< H.264 weighted biprediction for B-frames
	public static final int CODEC_FLAG2_MIXED_REFS    = 0x00000040; ///< H.264 one reference per partition, as opposed to one reference per macroblock
	public static final int CODEC_FLAG2_8X8DCT        = 0x00000080; ///< H.264 high profile 8x8 transform
	public static final int CODEC_FLAG2_FASTPSKIP     = 0x00000100; ///< H.264 fast pskip
	public static final int CODEC_FLAG2_AUD           = 0x00000200; ///< H.264 access unit delimiters
	public static final int CODEC_FLAG2_BRDO          = 0x00000400; ///< B-frame rate-distortion optimization
	public static final int CODEC_FLAG2_INTRA_VLC     = 0x00000800; ///< Use MPEG-2 intra VLC table.
	public static final int CODEC_FLAG2_MEMC_ONLY     = 0x00001000; ///< Only do ME/MC (I frames -> ref, P frame -> ME+MC).
	public static final int CODEC_FLAG2_DROP_FRAME_TIMECODE = 0x00002000; ///< timecode is in drop frame format.
	public static final int CODEC_FLAG2_SKIP_RD       = 0x00004000; ///< RD optimal MB level residual skipping
	public static final int CODEC_FLAG2_CHUNKS        = 0x00008000; ///< Input bitstream might be truncated at a packet boundaries instead of only at frame boundaries.
	public static final int CODEC_FLAG2_NON_LINEAR_QUANT = 0x00010000; ///< Use MPEG-2 nonlinear quantizer.
	public static final int CODEC_FLAG2_BIT_RESERVOIR = 0x00020000; ///< Use a bit reservoir when encoding if possible
	public static final int CODEC_FLAG2_MBTREE        = 0x00040000; ///< Use macroblock tree ratecontrol (x264 only)
	public static final int CODEC_FLAG2_PSY           = 0x00080000; ///< Use psycho visual optimizations.
	public static final int CODEC_FLAG2_SSIM          = 0x00100000; ///< Compute SSIM during encoding, error[] values are undefined.
	public static final int CODEC_FLAG2_INTRA_REFRESH = 0x00200000; ///< Use periodic insertion of intra blocks instead of keyframes.
	
	/* Unsupported options :
	 *              Syntax Arithmetic coding (SAC)
	 *              Reference Picture Selection
	 *              Independent Segment Decoding */
	/* /Fx */
	/* codec capabilities */
	
	public static final int CODEC_CAP_DRAW_HORIZ_BAND = 0x0001; ///< Decoder can use draw_horiz_band callback.
	/**
	 * Codec uses get_buffer() for allocating buffers and supports custom allocators.
	 * If not set, it might not use get_buffer() at all or use operations that
	 * assume the buffer was allocated by avcodec_default_get_buffer.
	 */
	public static final int CODEC_CAP_DR1             = 0x0002;
	/* If 'parse_only' field is true, then avcodec_parse_frame() can be used. */
	public static final int CODEC_CAP_PARSE_ONLY      = 0x0004;
	public static final int CODEC_CAP_TRUNCATED       = 0x0008;
	/* Codec can export data for HW decoding (XvMC). */
	public static final int CODEC_CAP_HWACCEL         = 0x0010;
	/**
	 * Codec has a nonzero delay and needs to be fed with NULL at the end to get the delayed data.
	 * If this is not set, the codec is guaranteed to never be fed with NULL data.
	 */
	public static final int CODEC_CAP_DELAY           = 0x0020;
	/**
	 * Codec can be fed a final frame with a smaller size.
	 * This can be used to prevent truncation of the last audio samples.
	 */
	public static final int CODEC_CAP_SMALL_LAST_FRAME = 0x0040;
	/**
	 * Codec can export data for HW decoding (VDPAU).
	 */
	public static final int CODEC_CAP_HWACCEL_VDPAU    = 0x0080;
	/**
	 * Codec can output multiple frames per AVPacket
	 * Normally demuxers return one frame at a time, demuxers which do not do
	 * are connected to a parser to split what they return into proper frames.
	 * This flag is reserved to the very rare category of codecs which have a
	 * bitstream that cannot be split into frames without timeconsuming
	 * operations like full decoding. Demuxers carring such bitstreams thus
	 * may return multiple frames in a packet. This has many disadvantages like
	 * prohibiting stream copy in many cases thus it should only be considered
	 * as a last resort.
	 */
	public static final int CODEC_CAP_SUBFRAMES        = 0x0100;
	/**
	 * Codec is experimental and is thus avoided in favor of non experimental
	 * encoders
	 */
	public static final int CODEC_CAP_EXPERIMENTAL     = 0x0200;
	/**
	 * Codec should fill in channel configuration and samplerate instead of container
	 */
	public static final int CODEC_CAP_CHANNEL_CONF     = 0x0400;
	
	/**
	 * Codec is able to deal with negative linesizes
	 */
	public static final int CODEC_CAP_NEG_LINESIZES    = 0x0800;
	
	/**
	 * Codec supports frame-level multithreading.
	 */
	public static final int CODEC_CAP_FRAME_THREADS    = 0x1000;
	/**
	 * Codec supports slice-based (or partition-based) multithreading.
	 */
	public static final int CODEC_CAP_SLICE_THREADS    = 0x2000;
	/**
	 * Codec is lossless.
	 */
	public static final int CODEC_CAP_LOSSLESS         = 0x80000000;

	//The following defines may change, don't expect compatibility if you use them.
	public static int MB_TYPE_INTRA4x4   = 0x0001;
	public static int MB_TYPE_INTRA16x16 = 0x0002; //FIXME H.264-specific
	public static int MB_TYPE_INTRA_PCM  = 0x0004; //FIXME H.264-specific
	public static int MB_TYPE_16x16      = 0x0008;
	public static int MB_TYPE_16x8       = 0x0010;
	public static int MB_TYPE_8x16       = 0x0020;
	public static int MB_TYPE_8x8        = 0x0040;
	public static int MB_TYPE_INTERLACED = 0x0080;
	public static int MB_TYPE_DIRECT2    = 0x0100; //FIXME
	public static int MB_TYPE_ACPRED     = 0x0200;
	public static int MB_TYPE_GMC        = 0x0400;
	public static int MB_TYPE_SKIP       = 0x0800;
	public static int MB_TYPE_P0L0       = 0x1000;
	public static int MB_TYPE_P1L0       = 0x2000;
	public static int MB_TYPE_P0L1       = 0x4000;
	public static int MB_TYPE_P1L1       = 0x8000;
	public static int MB_TYPE_L0         = (MB_TYPE_P0L0 | MB_TYPE_P1L0);
	public static int MB_TYPE_L1         = (MB_TYPE_P0L1 | MB_TYPE_P1L1);
	public static int MB_TYPE_L0L1       = (MB_TYPE_L0   | MB_TYPE_L1);
	public static int MB_TYPE_QUANT      = 0x00010000;
	public static int MB_TYPE_CBP        = 0x00020000;
	//Note bits 24-31 are reserved for codec specific use (h264 ref0, mpeg1 0mv, ...)
	

	public static int FF_QSCALE_TYPE_MPEG1 = 0;
	public static int FF_QSCALE_TYPE_MPEG2 = 1;
	public static int FF_QSCALE_TYPE_H264  = 2;
	public static int FF_QSCALE_TYPE_VP56  = 3;

	public static int FF_BUFFER_TYPE_INTERNAL = 1;
	public static int FF_BUFFER_TYPE_USER     = 2; ///< direct rendering buffers (image is (de)allocated by user)
	public static int FF_BUFFER_TYPE_SHARED   = 4; ///< Buffer from somewhere else; don't deallocate image (data/base), all other tables are not shared.
	public static int FF_BUFFER_TYPE_COPY     = 8; ///< Just a (modified) copy of some other buffer, don't deallocate anything.

	public static int FF_BUFFER_HINTS_VALID    = 0x01; // Buffer hints value is meaningful (if 0 ignore).
	public static int FF_BUFFER_HINTS_READABLE = 0x02; // Codec will read from buffer.
	public static int FF_BUFFER_HINTS_PRESERVE = 0x04; // User must not alter buffer content.
	public static int FF_BUFFER_HINTS_REUSABLE = 0x08; // Codec will reuse the buffer (update).

	public static int AV_PKT_FLAG_KEY = 0x0001;
	
	public static int FF_ASPECT_EXTENDED = 15;
	public static int FF_RC_STRATEGY_XVID = 1;

	public static final int FF_BUG_AUTODETECT       = 1;  // autodetection
	public static final int FF_BUG_OLD_MSMPEG4      = 2;
	public static final int FF_BUG_XVID_ILACE       = 4;
	public static final int FF_BUG_UMP4             = 8;
	public static final int FF_BUG_NO_PADDING       = 16;
	public static final int FF_BUG_AMV              = 32;
	public static final int FF_BUG_AC_VLC           = 0; // Will be removed, libavcodec can now handle these non-compliant files by default.
	public static final int FF_BUG_QPEL_CHROMA      = 64;
	public static final int FF_BUG_STD_QPEL         = 128;
	public static final int FF_BUG_QPEL_CHROMA2     = 256;
	public static final int FF_BUG_DIRECT_BLOCKSIZE = 512;
	public static final int FF_BUG_EDGE             = 1024;
	public static final int FF_BUG_HPEL_CHROMA      = 2048;
	public static final int FF_BUG_DC_CLIP          = 4096;
	public static final int FF_BUG_MS               = 8192; // Work around various bugs in Microsoft's broken decoders.
	public static final int FF_BUG_TRUNCATED        = 16384;
	
	public static final int FF_COMPLIANCE_VERY_STRICT  = 2; // Strictly conform to an older more strict version of the spec or reference software.
	public static final int FF_COMPLIANCE_STRICT       = 1; // Strictly conform to all the things in the spec no matter what consequences.
	public static final int FF_COMPLIANCE_NORMAL       = 0;
	public static final int FF_COMPLIANCE_UNOFFICIAL   = -1; // Allow unofficial extensions
	public static final int FF_COMPLIANCE_EXPERIMENTAL = -2; // Allow nonstandardized experimental things.
	
	public static final int FF_ER_CAREFUL         = 1;
	public static final int FF_ER_COMPLIANT       = 2;
	public static final int FF_ER_AGGRESSIVE      = 3;
	public static final int FF_ER_VERY_AGGRESSIVE = 4;
	
	public static final int FF_DCT_AUTO    = 0;
	public static final int FF_DCT_FASTINT = 1;
	public static final int FF_DCT_INT     = 2;
	public static final int FF_DCT_MMX     = 3;
	public static final int FF_DCT_MLIB    = 4;
	public static final int FF_DCT_ALTIVEC = 5;
	public static final int FF_DCT_FAAN    = 6;
	
	public static final int FF_IDCT_AUTO          = 0;
	public static final int FF_IDCT_INT           = 1;
	public static final int FF_IDCT_SIMPLE        = 2;
	public static final int FF_IDCT_SIMPLEMMX     = 3;
	public static final int FF_IDCT_LIBMPEG2MMX   = 4;
	public static final int FF_IDCT_PS2           = 5;
	public static final int FF_IDCT_MLIB          = 6;
	public static final int FF_IDCT_ARM           = 7;
	public static final int FF_IDCT_ALTIVEC       = 8;
	public static final int FF_IDCT_SH4           = 9;
	public static final int FF_IDCT_SIMPLEARM     = 10;
	public static final int FF_IDCT_H264          = 11;
	public static final int FF_IDCT_VP3           = 12;
	public static final int FF_IDCT_IPP           = 13;
	public static final int FF_IDCT_XVIDMMX       = 14;
	public static final int FF_IDCT_CAVS          = 15;
	public static final int FF_IDCT_SIMPLEARMV5TE = 16;
	public static final int FF_IDCT_SIMPLEARMV6   = 17;
	public static final int FF_IDCT_SIMPLEVIS     = 18;
	public static final int FF_IDCT_WMV2          = 19;
	public static final int FF_IDCT_FAAN          = 20;
	public static final int FF_IDCT_EA            = 21;
	public static final int FF_IDCT_SIMPLENEON    = 22;
	public static final int FF_IDCT_SIMPLEALPHA   = 23;
	public static final int FF_IDCT_BINK          = 24;
	
	public static final int FF_INPUT_BUFFER_PADDING_SIZE = 16;
	/**
	 * minimum encoding buffer size
	 * Used to avoid some checks during header writing.
	 */
	public static final int FF_MIN_BUFFER_SIZE = 16384;
	
	public static final int FF_EC_GUESS_MVS = 1;
	public static final int FF_EC_DEBLOCK   = 2;
	
	public static final int FF_PRED_LEFT   = 0;
	public static final int FF_PRED_PLANE  = 1;
	public static final int FF_PRED_MEDIAN = 2;
	
	public static final int FF_DEBUG_PICT_INFO   = 1;
	public static final int FF_DEBUG_RC          = 2;
	public static final int FF_DEBUG_BITSTREAM   = 4;
	public static final int FF_DEBUG_MB_TYPE     = 8;
	public static final int FF_DEBUG_QP          = 16;
	public static final int FF_DEBUG_MV          = 32;
	public static final int FF_DEBUG_DCT_COEFF   = 0x00000040;
	public static final int FF_DEBUG_SKIP        = 0x00000080;
	public static final int FF_DEBUG_STARTCODE   = 0x00000100;
	public static final int FF_DEBUG_PTS         = 0x00000200;
	public static final int FF_DEBUG_ER          = 0x00000400;
	public static final int FF_DEBUG_MMCO        = 0x00000800;
	public static final int FF_DEBUG_BUGS        = 0x00001000;
	public static final int FF_DEBUG_VIS_QP      = 0x00002000;
	public static final int FF_DEBUG_VIS_MB_TYPE = 0x00004000;
	public static final int FF_DEBUG_BUFFERS     = 0x00008000;
	public static final int FF_DEBUG_THREADS     = 0x00010000;
	
	public static final int FF_DEBUG_VIS_MV_P_FOR  = 0x00000001; //visualize forward predicted MVs of P frames
	public static final int FF_DEBUG_VIS_MV_B_FOR  = 0x00000002; //visualize forward predicted MVs of B frames
	public static final int FF_DEBUG_VIS_MV_B_BACK = 0x00000004; //visualize backward predicted MVs of B frames
	
	public static final int FF_CMP_SAD    = 0;
	public static final int FF_CMP_SSE    = 1;
	public static final int FF_CMP_SATD   = 2;
	public static final int FF_CMP_DCT    = 3;
	public static final int FF_CMP_PSNR   = 4;
	public static final int FF_CMP_BIT    = 5;
	public static final int FF_CMP_RD     = 6;
	public static final int FF_CMP_ZERO   = 7;
	public static final int FF_CMP_VSAD   = 8;
	public static final int FF_CMP_VSSE   = 9;
	public static final int FF_CMP_NSSE   = 10;
	public static final int FF_CMP_W53    = 11;
	public static final int FF_CMP_W97    = 12;
	public static final int FF_CMP_DCTMAX = 13;
	public static final int FF_CMP_DCT264 = 14;
	public static final int FF_CMP_CHROMA = 256;
	
	public static final int FF_DTG_AFD_SAME         = 8;
	public static final int FF_DTG_AFD_4_3          = 9;
	public static final int FF_DTG_AFD_16_9         = 10;
	public static final int FF_DTG_AFD_14_9         = 11;
	public static final int FF_DTG_AFD_4_3_SP_14_9  = 13;
	public static final int FF_DTG_AFD_16_9_SP_14_9 = 14;
	public static final int FF_DTG_AFD_SP_4_3       = 15;
	
	public static final int FF_DEFAULT_QUANT_BIAS = 999999;
	
	public static final int FF_CODER_TYPE_VLC     = 0;
	public static final int FF_CODER_TYPE_AC      = 1;
	public static final int FF_CODER_TYPE_RAW     = 2;
	public static final int FF_CODER_TYPE_RLE     = 3;
	public static final int FF_CODER_TYPE_DEFLATE = 4;
	
	public static final int SLICE_FLAG_CODED_ORDER   = 0x0001; ///< draw_horiz_band() is called in coded order instead of display
	public static final int SLICE_FLAG_ALLOW_FIELD   = 0x0002; ///< allow draw_horiz_band() with field slices (MPEG2 field pics)
	public static final int SLICE_FLAG_ALLOW_PLANE   = 0x0004; ///< allow draw_horiz_band() with 1 component at a time (SVQ1)
	
	public static final int FF_MB_DECISION_SIMPLE = 0;  // uses mb_cmp
	public static final int FF_MB_DECISION_BITS   = 1;  // chooses the one which needs the fewest bits
	public static final int FF_MB_DECISION_RD     = 2;  // rate distortion
	
	
	public static final int FF_PROFILE_UNKNOWN                   = -99;
	public static final int FF_PROFILE_RESERVED                  = -100;
	public static final int FF_PROFILE_AAC_MAIN                  = 0;
	public static final int FF_PROFILE_AAC_LOW                   = 1;
	public static final int FF_PROFILE_AAC_SSR                   = 2;
	public static final int FF_PROFILE_AAC_LTP                   = 3;
	public static final int FF_PROFILE_DTS                       = 20;
	public static final int FF_PROFILE_DTS_ES                    = 30;
	public static final int FF_PROFILE_DTS_96_24                 = 40;
	public static final int FF_PROFILE_DTS_HD_HRA                = 50;
	public static final int FF_PROFILE_DTS_HD_MA                 = 60;
	public static final int FF_PROFILE_MPEG2_422                 = 0;
	public static final int FF_PROFILE_MPEG2_HIGH                = 1;
	public static final int FF_PROFILE_MPEG2_SS                  = 2;
	public static final int FF_PROFILE_MPEG2_SNR_SCALABLE        = 3;
	public static final int FF_PROFILE_MPEG2_MAIN                = 4;
	public static final int FF_PROFILE_MPEG2_SIMPLE              = 5;
	public static final int FF_PROFILE_H264_CONSTRAINED  		 = (1<<9);  // 8+1; constraint_set1_flag
	public static final int FF_PROFILE_H264_INTRA                = (1<<11); // 8+3; constraint_set3_flag
	public static final int FF_PROFILE_H264_BASELINE             = 66;
	public static final int FF_PROFILE_H264_CONSTRAINED_BASELINE = (66|FF_PROFILE_H264_CONSTRAINED);
	public static final int FF_PROFILE_H264_MAIN                 = 77;
	public static final int FF_PROFILE_H264_EXTENDED             = 88;
	public static final int FF_PROFILE_H264_HIGH                 = 100;
	public static final int FF_PROFILE_H264_HIGH_10              = 110;
	public static final int FF_PROFILE_H264_HIGH_10_INTRA        = (110|FF_PROFILE_H264_INTRA);
	public static final int FF_PROFILE_H264_HIGH_422             = 122;
	public static final int FF_PROFILE_H264_HIGH_422_INTRA       = (122|FF_PROFILE_H264_INTRA);
	public static final int FF_PROFILE_H264_HIGH_444             = 144;
	public static final int FF_PROFILE_H264_HIGH_444_PREDICTIVE  = 244;
	public static final int FF_PROFILE_H264_HIGH_444_INTRA       = (244|FF_PROFILE_H264_INTRA);
	public static final int FF_PROFILE_H264_CAVLC_444            = 44;
	public static final int FF_PROFILE_VC1_SIMPLE                = 0;
	public static final int FF_PROFILE_VC1_MAIN                  = 1;
	public static final int FF_PROFILE_VC1_COMPLEX               = 2;
	public static final int FF_PROFILE_VC1_ADVANCED              = 3;

	public static final int FF_LEVEL_UNKNOWN = -99;

	public static int X264_PART_I4X4 = 0x001;  /* Analyze i4x4 */
	public static int X264_PART_I8X8 = 0x002;  /* Analyze i8x8 (requires 8x8 transform) */
	public static int X264_PART_P8X8 = 0x010;  /* Analyze p16x8, p8x16 and p8x8 */
	public static int X264_PART_P4X4 = 0x020;  /* Analyze p8x4, p4x8, p4x4 */
	public static int X264_PART_B8X8 = 0x100;  /* Analyze b16x8, b8x16 and b8x8 */
	
	public static final int FF_COMPRESSION_DEFAULT = -1;

	public static final int FF_THREAD_FRAME = 1; // Decode more than one frame at once
	public static final int FF_THREAD_SLICE = 2; // Decode more than one part of a single frame at once
	
	public static final int AV_PARSER_PTS_NB = 4;
	public static final int PARSER_FLAG_COMPLETE_FRAMES = 0x0001;
	public static final int PARSER_FLAG_ONCE            = 0x0002;
	public static final int PARSER_FLAG_FETCHED_OFFSET  = 0x0004;
	

	
	public static boolean IS_INTRA4x4(long a) {
		return ( a & MB_TYPE_INTRA4x4 ) == MB_TYPE_INTRA4x4;
	}	
	
	public static boolean IS_INTRA16x16(long a) {
		return ( a & MB_TYPE_INTRA16x16 ) == MB_TYPE_INTRA16x16;
	}

	public static boolean IS_PCM(long a)     {
		return ( a & MB_TYPE_INTRA_PCM ) == MB_TYPE_INTRA_PCM;
	}

	public static boolean IS_INTRA(long a)  {
		return ( a & 7 ) == 7;
	}
	
	public static boolean IS_INTER(long a)   {
		return ( a & (MB_TYPE_16x16|MB_TYPE_16x8|MB_TYPE_8x16|MB_TYPE_8x8) ) == (MB_TYPE_16x16|MB_TYPE_16x8|MB_TYPE_8x16|MB_TYPE_8x8);
	} 

	public static boolean IS_SKIP(long a)   {
		return ( a & MB_TYPE_SKIP ) == MB_TYPE_SKIP;
	}

	public static boolean IS_INTRA_PCM(long a)  {
		return ( a & MB_TYPE_INTRA_PCM ) == MB_TYPE_INTRA_PCM;
	}
	
	public static boolean IS_INTERLACED(long a) {
		return ( a & MB_TYPE_INTERLACED ) == MB_TYPE_INTERLACED;
	} 

	public static boolean IS_DIRECT(long a)   {
		return ( a & MB_TYPE_DIRECT2 ) == MB_TYPE_DIRECT2;
	}
	
	public static boolean IS_GMC(long a)   {
		return ( a & MB_TYPE_GMC ) == MB_TYPE_GMC;
	}
	
	public static boolean IS_16X16(long a)   {
		return ( a & MB_TYPE_16x16 ) == MB_TYPE_16x16;
	} 
	
	public static boolean IS_16X8(long a)   {
		return ( a & MB_TYPE_16x8 ) == MB_TYPE_16x8;
	}
	
	public static boolean IS_8X16(long a)  {
		return ( a & MB_TYPE_8x16 ) == MB_TYPE_8x16;
	}  
	
	public static boolean IS_8X8(long a)    {
		return ( a & MB_TYPE_8x8 ) == MB_TYPE_8x8;
	}

	public static boolean IS_SUB_8X8(long a)  {
		return ( a & MB_TYPE_16x16 ) == MB_TYPE_16x16;
	}

	public static boolean IS_SUB_8X4(long a) {
		return ( a & MB_TYPE_16x8 ) == MB_TYPE_16x8;
	}

	public static boolean IS_SUB_4X8(long a)  {
		return ( a & MB_TYPE_8x16 ) == MB_TYPE_8x16;
	}

	public static boolean IS_SUB_4X4(long a)   {
		return ( a & MB_TYPE_8x8 ) == MB_TYPE_8x8;
	}

	public static boolean IS_ACPRED(long a)  {
		return ( a & MB_TYPE_ACPRED ) == MB_TYPE_ACPRED;
	}

	public static boolean IS_QUANT(long a)   {
		return ( a & MB_TYPE_QUANT ) == MB_TYPE_QUANT;
	}
	
	public static boolean initialized = false;

	public static Map<CodecID,AVCodec> codecs = new HashMap<CodecID,AVCodec>();
	
	public static int [] ff_cropTbl = new int[256 + 2 * DspUtil.MAX_NEG_CROP];
	public static long [] ff_squareTbl = new long[512];
	

	public static void avcodec_register_all() {
		
		if (initialized)
			return;
		initialized = true;	
		
		dsputil_static_init();
		
		// Video
		add_codec(new Mpeg12());
		add_codec(new RawDec());
		add_codec(new MjpegEnc());
		
		// Audio
		add_codec(new PCM_S16LE());
		add_codec(new PCM_S24LE());
	}
	
	


	private static void dsputil_static_init() {
		int i;

	    for (i=0 ; i<256 ; i++) {
	    	ff_cropTbl[i + DspUtil.MAX_NEG_CROP] = i;
	    }
	    for (i=0 ; i<DspUtil.MAX_NEG_CROP ; i++) {
	        ff_cropTbl[i] = 0;
	        ff_cropTbl[i + DspUtil.MAX_NEG_CROP + 256] = 255;
	    }

	    for (i=0 ; i<512 ; i++) {
	        ff_squareTbl[i] = (i - 256) * (i - 256);
	    }

	    for(i=0 ; i<64 ; i++) {
	    	DspUtil.inv_zigzag_direct16[DspUtil.ff_zigzag_direct[i]]= i+1;
	    }
		
	}


	private static void add_codec(AVCodec codec) {
		codecs.put(codec.get_id(), codec);
		
	}


	public static void print_codecs() {
		System.out.println("Codecs:");
		System.out.println(" D..... = Decoding supported");
		System.out.println(" .E.... = Encoding supported");
		System.out.println(" ..V... = Video codec");
		System.out.println(" ..A... = Audio codec");
		System.out.println(" ..S... = Subtitle codec");
		System.out.println(" ...S.. = Supports draw_horiz_band");
		System.out.println(" ....D. = Supports direct rendering method 1");
		System.out.println(" .....T = Supports weird frame truncation");
		System.out.println(" ------");
		System.out.println();
		
		for (AVCodec c: codecs.values()) {
			String typeStr;
			
			switch (c.get_type()) {
				case AVMEDIA_TYPE_VIDEO:
					typeStr = "V";
		            break;
		        case AVMEDIA_TYPE_AUDIO:
		        	typeStr = "A";
		            break;
		        case AVMEDIA_TYPE_SUBTITLE:
		        	typeStr = "S";
		            break;
		        default:
		        	typeStr = "?";
		            break;
		    }
			
			
			System.out.print(" " + (c.is_decode() ? "D" : " "));
			System.out.print(c.is_encode() ? "E" : " ");
			System.out.print(typeStr);
			System.out.print((c.get_capabilities() & CODEC_CAP_DRAW_HORIZ_BAND) == CODEC_CAP_DRAW_HORIZ_BAND ? "S":" ");
			System.out.print((c.get_capabilities() & CODEC_CAP_DR1) == CODEC_CAP_DR1 ? "D":" ");
			System.out.print((c.get_capabilities() & CODEC_CAP_TRUNCATED) == CODEC_CAP_TRUNCATED ? "T":" ");
			System.out.print("  " + c.get_name() + "  ");
			System.out.print(c.get_long_name());
			System.out.println();
			System.out.println();
			
			System.out.println("Note, the names of encoders and decoders do not always match, so there are\n" +
							   "several cases where the above table shows encoder only or decoder only entries\n" +
							   "even though both encoding and decoding are supported. For example, the h263\n" +
							   "decoder corresponds to the h263 and h263p encoders, for file formats it is even\n" +
							   "worse.");
			}
			
			
			
	}


	public static AVCodec find_decoder(CodecID codecID) {
		return codecs.get(codecID);
	}

	public static AVCodec find_encoder(CodecID codecID) {
		return codecs.get(codecID);
	}
	
	

	public static AVCodec avcodec_find_encoder_by_name(String name) {
		for (AVCodec p : codecs.values()) {
			if (p.is_encode() && (p.get_name().equals(name)))
				return p;
		}
		return null;
	}

	
	public static AVCodec avcodec_find_decoder_by_name(String name) {
		for (AVCodec p : codecs.values()) {
			if (p.is_decode() && (p.get_name().equals(name)))
				return p;
		}
		return null;
	}
	
	
	public static AVCodec avcodec_find_decoder(CodecID id) {
		for (AVCodec p : codecs.values()) {
			if (p.is_decode() && (p.get_id() == id))
				return p;
		}
		return null;
	}
	
	
	public static AVCodec avcodec_find_encoder(CodecID id) {
		for (AVCodec p : codecs.values()) {
			if (p.is_encode() && (p.get_id() == id))
				return p;
		}
		return null;
	}

	
    /**
     * Name of the codec implementation.
     */
    protected String name;
    protected String long_name = "";
    protected AVMediaType type;
    protected CodecID id;
    protected boolean has_priv_data = false;
    protected int capabilities;
    protected int max_lowres; // maximum value for lowres supported by the decoder
    protected AVClass priv_class;              ///< AVClass for the private context
    protected ArrayList<AVProfile> profiles;///< array of recognized profiles, or NULL if unknown
    protected ArrayList<PixelFormat> pix_fmts;
    protected boolean encode = false;
    protected boolean decode = false;
    protected ArrayList<AVRational> supported_framerates; ///< array of supported framerates, or NULL if any
    protected ArrayList<Integer> supported_samplerates; ///< array of supported samplerates, or NULL if any
        
    protected ArrayList<AVSampleFormat> sample_fmts; ///< array of supported sample formats
    protected ArrayList<Long> channel_layouts; ///< array of supported channel layouts, or NULL if unknown
    

    protected DisplayOutput displayOutput;
    
    
    public AVCodec() {
		super();
		this.profiles = new ArrayList<AVProfile>();
		this.pix_fmts = new ArrayList<PixelFormat>();
		this.supported_framerates = new ArrayList<AVRational>();
		this.supported_samplerates = new ArrayList<Integer>();
		this.sample_fmts = new ArrayList<AVSampleFormat>();
		this.channel_layouts = new ArrayList<Long>();
	}

    
	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public String get_long_name() {
		return long_name;
	}

	public void set_long_name(String long_name) {
		this.long_name = long_name;
	}

	public AVMediaType get_type() {
		return type;
	}

	public void set_type(AVMediaType type) {
		this.type = type;
	}

	public CodecID get_id() {
		return id;
	}

	public void set_id(CodecID id) {
		this.id = id;
	}

	public boolean has_priv_data() {
		return has_priv_data;
	}

	public int get_capabilities() {
		return capabilities;
	}
	
	public boolean has_capabilities(int cap) {
		return (capabilities & cap) == cap;
	}

	public void set_capabilities(int capabilities) {
		this.capabilities = capabilities;
	}

	public AVProfile get_profile(int i) {
		return profiles.get(i);
	}
	
	public ArrayList<AVProfile> get_profiles() {
		return profiles;
	}

	public void add_profile(AVProfile profile) {
		this.profiles.add(profile);
	}

	public ArrayList<PixelFormat> get_pix_fmts() {
		return pix_fmts;
	}

	public PixelFormat get_pix_fmt(int index) {
		return pix_fmts.get(index);
	}

	public void add_pix_fmts(PixelFormat pix_fmt) {
		this.pix_fmts.add(pix_fmt);
	}
	
	public ArrayList<AVRational> get_supported_framerates() {
		return supported_framerates;
	}
	
	public AVRational get_supported_framerate(int index) {
		return supported_framerates.get(index);
	}

	public void add_supported_framerates(AVRational supported_framerate) {
		this.supported_framerates.add(supported_framerate);
	}

	public ArrayList<Integer> get_supported_samplerates() {
		return supported_samplerates;
	}

	public int get_supported_samplerate(int index) {
		return supported_samplerates.get(index);
	}

	public void add_supported_samplerates(int supported_samplerate) {
		this.supported_samplerates.add(supported_samplerate);
	}

	public ArrayList<AVSampleFormat> get_sample_fmts() {
		return sample_fmts;
	}

	public AVSampleFormat get_sample_fmt(int index) {
		return sample_fmts.get(index);
	}

	public void add_sample_fmts(AVSampleFormat sample_fmt) {
		this.sample_fmts.add(sample_fmt);
	}

	public ArrayList<Long> get_channel_layouts() {
		return channel_layouts;
	}

	public Long get_channel_layout(int index) {
		return channel_layouts.get(index);
	}

	public void add_channel_layouts(Long channel_layout) {
		this.channel_layouts.add(channel_layout);
	}

	public DisplayOutput get_display_output() {
		return this.displayOutput;
	}

	public void set_display_output(DisplayOutput displayOutput) {
		this.displayOutput = displayOutput;
	}

	public AVClass get_priv_class() { 
		return priv_class; 
	}
	
	public void set_priv_class(AVClass priv_class) {
		this.priv_class = priv_class;
	}

	public boolean is_decode() {
		return decode;
	}
	public boolean is_encode() { 
		return encode; 
		}
		
	public int get_max_lowres() {
		return max_lowres;
	}

	public void set_max_lowres(int max_lowres) {
		this.max_lowres = max_lowres;
	}

	public int init(AVCodecContext avctx) {
		System.out.println(name + " - Unimplemented init");
    	return -1;
    }
        
    public OutOI encode(AVCodecContext avctx, short[] buf, 
			int buf_size, Object data) {
    	return new OutOI(null, -1);
    }
    
    
    public int close(AVCodecContext avctx) {
    	return -1;
    }
    
    public OutOI decode(AVCodecContext avctx, AVPacket pkt) {
		return new OutOI(null, -1);
	}

	public OutOI decodeAudio(AVCodecContext avctx, AVPacket pkt) {
		return new OutOI(null, -1);
	}
    

    public void flush(/*AVCodecContext */) {
    	return;
    }
    
    

	public String av_get_profile_name(int profile) {		
		if ( (profile == FF_PROFILE_UNKNOWN) || (profiles.size() == 0) )
			return "";
		
		for (AVProfile p : profiles) {
			if (p.get_profile() == profile)
				return p.get_name();
		}
		
		return "";	
		}

	
	public static OutOI avcodec_encode_video(AVCodecContext avctx, short [] buf, int buf_size, AVFrame pict) {
		return avctx.avcodec_encode_video(buf, buf_size, pict);
	}



	

}
