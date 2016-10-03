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

package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVProfile;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavcodec.AVFrame;
import uk.ac.liv.ffmpeg.libavcodec.UtilsCodec;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.AlternateHorizontalScan;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.AlternateVerticalScan;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.Mpeg1RLTable;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.Mpeg2RLTable;
import uk.ac.liv.ffmpeg.libavformat.mxf.BitStream;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.DisplayOutput;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.UtilsArrays;


public class Mpeg12 extends AVCodec {
	
	static boolean done = false;

	static int DC_VLC_BITS = 9;
	

	static PixelFormat [] mpeg1_hwaccel_pixfmt_list_420 = {
		PixelFormat.PIX_FMT_YUV420P,
		PixelFormat.PIX_FMT_NONE
	};

	static PixelFormat [] mpeg2_hwaccel_pixfmt_list_420 = {
		PixelFormat.PIX_FMT_YUV420P,
		PixelFormat.PIX_FMT_NONE
	};
	

	VLCTable ff_dc_lum_vlc;
	VLCTable ff_dc_chroma_vlc;
	VLCTable mv_vlc;
	VLCTable mbincr_vlc;
	VLCTable mb_pat_vlc;
	VLCTable mb_ptype_vlc;
	VLCTable mb_btype_vlc;
	
    RLTable rl_mpeg1 = new Mpeg1RLTable();
    RLTable rl_mpeg2 = new Mpeg2RLTable();
    

	boolean init = false;

    /**
     * Width/Height and format (mpeg2 or mpeg1)
     */
    private int mbWidth;
    private int mbHeight;
    protected boolean mpeg2;

    protected BitStream in = new BitStream();
    
    /**
     * Pels forming a Macroblock
     */
    protected int mb_type;
    public static final int NUMBER_OF_BLOCKS = 8;
    private int[] blockWrap  = new int[ NUMBER_OF_BLOCKS ];
    private int[][] block    = new int[ NUMBER_OF_BLOCKS ][ 64 ];
    
    private int[] ptype2mb_type = Tables.getPType2mb_type();
    private int[] btype2mb_type = Tables.getBType2mb_type();

    protected int width;
    protected int height;
    protected int aspectRatio;
    protected int frame_rate_index;
    protected int bit_rate;
    protected float frameRate = 299/10;

    protected final float[] frameRateTable = Tables.getFrameRateTable();
    
    /**
     * Internal State - Quantisation matrices
     */
    private int[] intra_matrix        = new int[ 64 ];
    private int[] inter_matrix        = new int[ 64 ];
    private int[] chroma_intra_matrix = new int[ 64 ];
    private int[] chroma_inter_matrix = new int[ 64 ];
    

    public  ScanTable intraScanTable   = new ZigZagDirect();
    public  ScanTable interScanTable   = new ZigZagDirect();
    public  ScanTable intraHScanTable  = new AlternateHorizontalScan();
    public   ScanTable intraVScanTable  = new AlternateVerticalScan();
    
    int[] intra_scantable = intraScanTable.get_permutated();
    /**
     * Sequence extension information
     */
    protected int profile;
    protected int level;
    protected boolean progressive_sequence;
    protected int vdv_buf_ext;
    
    protected int intra_dc_precision;
    protected int picture_structure = MpegConstants.PICT_FRAME;
    protected boolean top_field_first;
    protected boolean frame_pred_frame_dct;
    protected boolean concealment_motion_vectors;
    protected boolean q_scale_type;
    protected boolean intra_vlc_format;
    protected boolean alternate_scan;
    protected boolean repeat_first_field;
    protected boolean chroma_420_type;
    protected boolean progressive_frame;
    
    
    /**
     * Internal State - I type macroblock
     */
    private boolean mb_intra = true;
    /**
     * Internal State - Motion code
     */
    private int[][] motion_val;
    private int mv_dir;
    private int mv_type;
    private int motion_type;
    private int[] mv = new int[ 8 ];
    private boolean[] full_pel     = new boolean[ 2 ];
    private boolean[] field_select = new boolean[ 4 ];

	RLTable rltable = MpegConstants.rl_mpeg2;
	
    protected boolean first_field;
    private int panScanWidth;
    private int panScanHeight;
    /**
     * Current macroblock positions
     */
    protected boolean field_pic;
    protected int resync_mb_x;
    protected int resync_mb_y;
    protected int mb_x;
    protected int mb_y;

    
    /**
     * scaling
     */
    protected boolean interlaced_dct;
    protected int repeat_pict;
    
    protected int qscale;
    protected int mb_skip_run;
    
    /**
     * Cache for pel DC compoent decoding
     */
    private int[] last_dc = new int[] { 0x080, 0x080, 0x080 };
    /**
     * Picture definition codes
     */
    protected int pict_type;
    protected int picture_number;
    protected int[] mpeg_f_code = new int[ 4 ];
    protected int y_dc_scale;
    protected int c_dc_scale;
    protected boolean first_slice;
    
    
    
	public Mpeg12() {
		super();
		
		this.name = "mpeg2video";		
		this.long_name = "MPEG-2 video";
		this.type = AVMediaType.AVMEDIA_TYPE_VIDEO;
		this.id = CodecID.CODEC_ID_MPEG2VIDEO;
		this.capabilities = CODEC_CAP_DRAW_HORIZ_BAND | CODEC_CAP_DR1 | CODEC_CAP_TRUNCATED | CODEC_CAP_DELAY | CODEC_CAP_SLICE_THREADS;
		this.max_lowres = 3;
		this.decode = true;
		
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_422, "4:2:2"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_422, "4:2:2"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_HIGH, "High"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_SS, "Spatially Scalable"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_SNR_SCALABLE, "SNR Scalable"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_MAIN, "Main"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_MPEG2_SIMPLE, "Simple"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_RESERVED, "Reserved"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_RESERVED, "Reserved"));
		this.profiles.add(new AVProfile(AVCodec.FF_PROFILE_UNKNOWN, ""));
	}


	private void initialise(int width, int height) {  
        mbWidth = (width + 15) / 16;
        mbHeight = (height + 15) / 16;
        
        if (displayOutput == null ) { 
        	displayOutput = new DisplayOutput(mbWidth, mbHeight);
        }
        
        blockWrap[ 0 ] = mbWidth * 2 + 2;
        blockWrap[ 1 ] = mbWidth * 2 + 2;
        blockWrap[ 2 ] = mbWidth * 2 + 2;
        blockWrap[ 3 ] = mbWidth * 2 + 2;
        blockWrap[ 4 ] = mbWidth     + 2;
        blockWrap[ 5 ] = mbWidth     + 2;        
        blockWrap[ 6 ] = mbWidth     + 2;
        blockWrap[ 7 ] = mbWidth     + 2;

        motion_val = new int[ 1 + (mbWidth * 2 + 2) * mbHeight * 2  * 4 ][2];
        this.initialized = true;
    }
	

	public int init(AVCodecContext avctx) {
		Mpeg1Context s = new Mpeg1Context();
		avctx.set_priv_data(s);
		MpegEncContext s2 = new MpegEncContext();
		s.set_mpeg_enc_ctx(s2);
				
		// we need some permutation to store matrices,
	    // until MPV_common_init() sets the real permutation. 
	 	for (int i = 0 ; i < 64 ; i++) {
			s2.get_dsp().set_idct_permutation(i, i);
		}
	 	
	 	s2.MPVDecodeDefaults();
	 	
	 	s.get_mpeg_enc_ctx().set_avctx(avctx);
	 	s.get_mpeg_enc_ctx().set_flags(avctx.get_flags());
	 	s.get_mpeg_enc_ctx().set_flags2(avctx.get_flags2());	    

	    ff_mpeg12_common_init(s.get_mpeg_enc_ctx());
	    ff_mpeg12_init_vlcs();
	    
	    s.set_mpeg_enc_ctx_allocated(false);
	    s.get_mpeg_enc_ctx().set_picture_number(0);
	    s.set_repeat_field(false);
	    s.get_mpeg_enc_ctx().set_codec_id(avctx.get_codec().get_id());
	    avctx.set_color_range(AVColorRange.AVCOL_RANGE_MPEG);
	    
	    if (avctx.get_codec().get_id() == CodecID.CODEC_ID_MPEG1VIDEO)
	    	avctx.set_chroma_sample_location(AVChromaLocation.AVCHROMA_LOC_CENTER);
	    else
	    	avctx.set_chroma_sample_location(AVChromaLocation.AVCHROMA_LOC_LEFT);
		
		return 0;
	}
	
	

	
	void ff_mpeg12_common_init(MpegEncContext s) {
	    s.set_y_dc_scale_table(MpegVideo.ff_mpeg2_dc_scale_table[s.get_intra_dc_precision()]);
	    s.set_c_dc_scale_table(MpegVideo.ff_mpeg2_dc_scale_table[s.get_intra_dc_precision()]);
	}

	
	public void ff_mpeg12_init_vlcs() {
	    if (!done) {
	        done = true;
        
		    ff_dc_lum_vlc = new DiscreteCosineLuminanceVlc();
		    ff_dc_chroma_vlc = new DiscreteCosineChrominanceVlc(); 
		    mv_vlc = new MotionVectorVlc();
		    mbincr_vlc = new AddressIncrementVlc();
		    mb_pat_vlc = new PatVLC();	    
		    mb_ptype_vlc = new MbPTypeVLC();
		    mb_btype_vlc = new MbBTypeVLC();
		    
		    rl_mpeg1 = new Mpeg1RLTable();
		    rl_mpeg2 = new Mpeg2RLTable();
	    }
	}

	 /**
     * This packet describes the highest level video attributes:
     *   - Width, Height
     *   - Aspect ratio
     *   - Frame/Bit rate
     *   - Luminance and Chrominance matricies
     */
    private void decodeSequence() {
//    	Timer t = new Timer();
    	
        width  = in.getBits( 12 );
        height = in.getBits( 12 );
        aspectRatio = in.getBits( 4 );
        frame_rate_index = in.getBits( 4 );
        frameRate = frameRateTable[ frame_rate_index ];
        bit_rate = in.getBits( 18 ) * 400;
        in.getTrueFalse();
        in.getBits(10);
        in.getTrueFalse();
        
        /* Get Intra Matrix */
        if ( in.getTrueFalse() ) {
            for ( int i = 0; i < 64; i++ ) {
                int v = in.getBits( 8 );
                int j = intraScanTable.get_permutated()[ i ];
                intra_matrix[ j ] = v;
                chroma_intra_matrix[ j ] = v;
            }
        } else {
            for ( int i = 0; i < 64; i++ ) {
                int j = MpegConstants.dsp_idct_permutation[ i ];
                int v = Mpeg12Data.ff_mpeg1_default_intra_matrix[ i ];
                intra_matrix[ j ] = v;
                chroma_intra_matrix[ j ] = v;
            }
        }
        /* Get Non-intra Matrix */
        if ( in.getTrueFalse() ) {
            for ( int i = 0; i < 64; i++ ) {
                int v = in.getBits( 8 );
                int j = intraScanTable.get_permutated()[ i ];
                inter_matrix[ j ] = v;
                chroma_inter_matrix[ j ] = v;
            }
        } else {
            for ( int i = 0; i < 64; i++ ) {
                int j = MpegConstants.dsp_idct_permutation[ i ];
                int v = Mpeg12Data.ff_mpeg1_default_non_intra_matrix[ i ];
                inter_matrix[ j ] = v;
                chroma_inter_matrix[ j ] = v;
            }
        }

        /* Initialise variables for Mpeg1 */
        progressive_sequence = true;
        progressive_frame = true;
        picture_structure = MpegConstants.PICT_FRAME;
        frame_pred_frame_dct = true;
        
        
//        t.print("decodeSequence: ");
    }
    

    /**
     * Decode extension codes
     */
    private void decodeExtension(Mpeg1Context s1) {  
        int extensionType = in.getBits(4);
        switch (extensionType) {
            case MpegConstants.SEQUENCE_EXTENSION: {
            	decodeSequenceExtension(s1);
                break;
            }
            case MpegConstants.SEQUENCE_DISPLAY_EXTENSION: {
            	decodeSequenceDisplayExtension();
                break;
            }
            case MpegConstants.QUANT_MATRIX_EXTENSION: {
            	decodeQuantMatrixExtension();
                break;
            }
            case MpegConstants.PICTURE_DISPLAY_EXTENSION: {
            	Log.av_log("mpeg12", Log.AV_LOG_WARNING, "Unimplemented: PICTURE_DISPLAY_EXTENSION\n");
                break;
            }
            case MpegConstants.PICTURE_CODING_EXTENSION: {
                decodePictureCodingExtension();
                break;
            }
            default: {
                break;
            }
         
        }
    }

    /**
     * This packet describes a variety of display variables:
     *  - Field selection
     *  
     */    
    private void decodePictureCodingExtension() {
//    	Timer t = new Timer();

        full_pel[0] = false;
        full_pel[1] = false;
        mpeg_f_code[0]          = in.getBits(4);
        mpeg_f_code[1]          = in.getBits(4);
        mpeg_f_code[2]          = in.getBits(4);
        mpeg_f_code[3]          = in.getBits(4);
        intra_dc_precision         = in.getBits(2);
        picture_structure          = in.getBits(2);
        top_field_first            = in.getTrueFalse();
        frame_pred_frame_dct       = in.getTrueFalse();
        concealment_motion_vectors = in.getTrueFalse();
        q_scale_type               = in.getTrueFalse();
        intra_vlc_format           = in.getTrueFalse();
        alternate_scan             = in.getTrueFalse();
        repeat_first_field         = in.getTrueFalse();
        chroma_420_type            = in.getTrueFalse();
        progressive_frame          = in.getTrueFalse();

        if( picture_structure == MpegConstants.PICT_FRAME ) {
            first_field = false;
        } else {
            first_field = !first_field;
            /** Removed memset0 */
        }

        if( alternate_scan ) {
        	intraScanTable   = MpegConstants.alternateVerticalScanTable;
            interScanTable   = MpegConstants.alternateVerticalScanTable;
            intraHScanTable  = MpegConstants.alternateVerticalScanTable;
            intraVScanTable  = MpegConstants.alternateVerticalScanTable;
        } else {
        	intraScanTable   = MpegConstants.zigZagDirect;
            interScanTable   = MpegConstants.zigZagDirect;
            intraHScanTable  = MpegConstants.alternateHorizontalScanTable;
            intraVScanTable  = MpegConstants.alternateVerticalScanTable;
        }

//        t.print("decodePictureCodingExtension");
    }
    /**
     * This packet is only found in MPEG2 streams.  It defines:
     *   - Extended Width and height
     */
    private void decodeSequenceExtension(Mpeg1Context s1) {
        MpegEncContext s = s1.get_mpeg_enc_ctx();
    	
        in.getTrueFalse();    // Profile and level escape
        profile  = in.getBits(3);
        level    = in.getBits(4);
        progressive_sequence = in.getTrueFalse();
        s.set_chroma_format(in.getBits( 2 ));     //Chroma format
        width   |= (in.getBits( 2 ) << 12);
        height  |= (in.getBits( 2 ) << 12);
        bit_rate = ((bit_rate/400)|(in.getBits(12) << 12)) * 400;
        in.getTrueFalse();
        vdv_buf_ext = in.getBits(8);        

//        this.mxfFile.setWidth(width);
//        this.mxfFile.setHeight(height);
//		
//        if (!this.initialized) 
//        	initialise(width, height);

        
        in.getTrueFalse();
        int frame_rate_ext_n = in.getBits(2);
        int frame_rate_ext_d = in.getBits(5);
        
        if (!this.init) 
        	initialise(width, height);
        
        mpeg2 = true;
    } 

    private void decodeSequenceDisplayExtension() {

        in.getBits(3);
        if (in.getTrueFalse()) {
            in.getBits(24);
        }
        
        int width = in.getBits( 14 );
        in.getTrueFalse();
        int height = in.getBits( 14 );
        in.getTrueFalse();
        
        panScanWidth  = 16 * width;
        panScanHeight = 16 * height;
    }


    private void decodeQuantMatrixExtension() {
        int i, v, j;
        if (in.getTrueFalse()) {
            for(i=0;i<64;i++) {
                v = in.getBits(8);
                j= MpegConstants.zigZagDirect.get_permutated()[i];
                intra_matrix[j] = v;
                chroma_intra_matrix[j] = v;
            }
        }
        if (in.getTrueFalse()) {
            for(i=0;i<64;i++) {
                v = in.getBits(8);
                j= MpegConstants.zigZagDirect.get_permutated()[i];
                inter_matrix[j] = v;
                chroma_inter_matrix[j] = v;
            }
        }
        if (in.getTrueFalse()) {
            for(i=0;i<64;i++) {
                v = in.getBits(8);
                j= MpegConstants.zigZagDirect.get_permutated()[i];
                chroma_intra_matrix[j] = v;
            }
        }
        if (in.getTrueFalse()) {
            for(i=0;i<64;i++) {
                v = in.getBits(8);
                j= MpegConstants.zigZagDirect.get_permutated()[i];
                chroma_inter_matrix[j] = v;
            }
        }
    }


    public OutOI decode(AVCodecContext avctx, AVPacket pkt) {   
    	
        Mpeg1Context s1 = (Mpeg1Context) avctx.get_priv_data();
        MpegEncContext s = s1.get_mpeg_enc_ctx();
        
        in.addData(UtilsArrays.short_to_byte(pkt.get_data()), 0, pkt.get_data().length);
        boolean endOfFrame = false;
        while (!endOfFrame && (in.availableBits() > 24) ) {
            int currentHeader = -1;
            do {
                if (in.showBits(24) == MpegConstants.SYNC_BYTES) {
                    in.getBits(24);
                    currentHeader = in.getBits(8);
                } else {
                    in.getBits(8 -(in.getPos() % 8));
                }
            } while (currentHeader == -1);

            switch (currentHeader) {
                case MpegConstants.SEQ_START_CODE: {
                    decodeSequence();
                    break;
                }
                case MpegConstants.PICTURE_START_CODE: {
                	 if (mpeg_decode_postinit(avctx) < 0) {
                         Log.av_log("AVCodecContext", Log.AV_LOG_ERROR, "mpeg_decode_postinit() failure\n");
                         return  new OutOI(null, -1);
                     }
                	 
                    decodePicture();
                    break;
                }
                case MpegConstants.EXT_START_CODE: {
                     decodeExtension(s1);
                     break;
                }
                case MpegConstants.USER_START_CODE: {
                    break;
                }
                case MpegConstants.GOP_START_CODE: {
                    first_field = false;
                    break;
                }
                default: {
                    if ( (currentHeader >= MpegConstants.SLICE_MIN_START_CODE) &&
                         (currentHeader <= MpegConstants.SLICE_MAX_START_CODE) ) {
                        decodeSlice(currentHeader - MpegConstants.SLICE_MIN_START_CODE);
                        if (mb_y >= mbHeight) { 
                            endOfFrame = true;
                        }
                    }
                    break;
                }
            }
        }
        AVFrame frame = UtilsCodec.avcodec_get_frame_defaults();
        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int xx = 0;
		int yy = 0;
		int [] img = displayOutput.showScreen();
		for (int i = 0 ; i < img.length ; i++) {
			bi.setRGB(xx, yy, img[i]);

			xx++;

			if (xx >= 720) {
				xx = 0;
				yy++;
			}
		}
        
		frame.set_img(bi);
		frame.set_width(width);
		frame.set_height(height);
		
		s.set_aspect_ratio_info(aspectRatio);
		s.set_frame_rate_index(frame_rate_index);
		//s.set_width(width);
		//s.set_height(height);
		s.set_bit_rate(bit_rate);
		//avctx.set_bit_rate(bit_rate);
		
		
		
		return new OutOI(frame, 0);
	}

    private int mpeg_decode_postinit(AVCodecContext avctx) {
    	avctx.set_pix_fmt(mpeg_get_pixelformat(avctx));
        UtilsCodec.avcodec_set_dimensions(avctx, width, height);
        avctx.set_bit_rate(bit_rate);
    	return 0;
	}


	private PixelFormat mpeg_get_pixelformat(AVCodecContext avctx) {
	    Mpeg1Context s1 = (Mpeg1Context) avctx.get_priv_data();
	    MpegEncContext s = s1.get_mpeg_enc_ctx();

	    if (s.get_chroma_format() < 2) {
	        PixelFormat res = avctx.get_format(avctx.get_codec_id() == CodecID.CODEC_ID_MPEG1VIDEO ?
	                                			mpeg1_hwaccel_pixfmt_list_420 :
	                                			mpeg2_hwaccel_pixfmt_list_420);
	        if (res != PixelFormat.PIX_FMT_XVMC_MPEG2_IDCT && 
	            res != PixelFormat.PIX_FMT_XVMC_MPEG2_MC) {
	            avctx.set_xvmc_acceleration(0);
	        } else if (avctx.get_xvmc_acceleration() == 0) {
	        	avctx.set_xvmc_acceleration(2);
	        }
	        return res;
	    } else if (s.get_chroma_format() == 2)
	        return PixelFormat.PIX_FMT_YUV422P;
	    else
	        return PixelFormat.PIX_FMT_YUV444P;
	}


	private void decodeSlice(int sliceNumber) {    	
    	//Timer t = new Timer();
        last_dc[0] = 1 << (7 + intra_dc_precision);
        last_dc[1] = last_dc[0];
        last_dc[2] = last_dc[0];
        
        
        field_pic = (picture_structure != MpegConstants.PICT_FRAME);
        interlaced_dct = false;
        
        if (first_slice) {
            if ( first_field || !field_pic ) {
                repeat_pict = 0;
                if ( repeat_first_field ) {
                    if ( progressive_sequence ) {
                        repeat_pict = top_field_first ? 4 : 2 ;
                    } else if ( progressive_frame ) {
                        repeat_pict = 1;
                    }
                }
            } 
        }
        first_slice = false;

        qscale = get_qscale();

        while (in.getTrueFalse()) {
            in.getBits( 8 );
        }
        
        mb_x = 0;
        int code;
        do {
            try {
				code = in.getVLC(MpegConstants.mbincr_vlc);
	            if (code <= 33) 
	            	mb_x += code;
			} catch (MpegException e) {
				return;
			}
        } while (code >= 33);

        resync_mb_x = mb_x;
        resync_mb_y = sliceNumber;
        mb_y = sliceNumber;        
        
        mb_skip_run = 0;
        boolean endOfSlice = false;
    	
        while (!endOfSlice && ((mb_y << (field_pic?1:0)) < mbHeight)) {
            if (mb_x > mbWidth) 
            	Log.av_log("mpeg12", Log.AV_LOG_WARNING, "MpegDecoder.decodeSlice: mbX > mbWidth\n" );
            
            for (int i = 0 ; i < NUMBER_OF_BLOCKS ; i++)
                for (int j = 0 ; j < 64 ; j++) 
                	block[i][j] = 0;           
            
            try {
				decodeMacroblock();
			} catch (MpegException e1) {
				e1.printStackTrace();
			} 
            
            if (++mb_x >= mbWidth) {
                mb_x = 0;
                mb_y++;
                if ( (mb_y << (field_pic?1:0)) >= mbHeight ) {
                    endOfSlice = true;
                    break;
                }
            }
            
            /* Skip mb handling */
            if ( mb_skip_run == -1 ) {
                mb_skip_run = 0;
                do {
                    try {
						code = in.getVLC(MpegConstants.mbincr_vlc);
	                    if (code <= 33) 
	                    	mb_skip_run += code;
	                    if (code == 35) {
	                        endOfSlice = true;
	                        break;
	                    }
					} catch (MpegException e) {
						e.printStackTrace();
						return;
	
                    }
                } while (code >= 33);
            }
        }

        in.seek( ((in.getPos()/8)-2)*8 );

    	//t.print("decodeSlice");
        
    }

    
    /**
     * Decode a macroblock and the motion vector
     */
    private void decodeMacroblock() throws MpegException {
    	mb_skip_run--;
    	
    	if (in.getTrueFalse()) {
            mb_type = Tables.MB_TYPE_INTRA;
    	} else if (in.getTrueFalse()) {
            mb_type = Tables.MB_TYPE_INTRA | Tables.MB_TYPE_QUANT;
        } else {
            throw new MpegException("Invalid mb type");
        }
               

        if ( (picture_structure == MpegConstants.PICT_FRAME) && !frame_pred_frame_dct)
            interlaced_dct = in.getTrueFalse();
        
        if ( (Tables.MB_TYPE_QUANT & mb_type) != 0 )
            qscale = get_qscale();

            
        mb_intra = true;

        for (int i = 0 ; i < NUMBER_OF_BLOCKS ; i++) {
            decodeIntraBlock(block[i], i);
        }
        
        int x = mb_x;
        int y = mb_y;
                
        
        displayOutput.putLuminanceIdct( x * 2,     y * 2,     block[ 0 ], interlaced_dct );
        displayOutput.putLuminanceIdct( x * 2 + 1, y * 2,     block[ 1 ], interlaced_dct );
        displayOutput.putLuminanceIdct( x * 2,     y * 2 + 1, block[ 2 ], interlaced_dct );
        displayOutput.putLuminanceIdct( x * 2 + 1, y * 2 + 1, block[ 3 ], interlaced_dct );
        
        
        displayOutput.putBlueIdct( x, y,     block[ 4 ] );        
        displayOutput.putRedIdct( x, y,     block[ 5 ] );
        
      
      
    }
    

    private int decodeDC(int code) throws MpegException {
        if (code == 0) {
            return 0;
        } else {
            int diff = in.getBits(code);
            if ((diff & (1 << (code - 1))) == 0)
                diff = (-1 << code) | (diff + 1);
           
            return diff;
        }
    }
    
    private int decodeDCLum() throws MpegException {
        return decodeDC(in.getVLC(MpegConstants.dc_lum_vlc));
    }
    
    private int decodeDCChrom() throws MpegException {
        return decodeDC(in.getVLC(MpegConstants.dc_chroma_vlc));
    }
    private void decodeIntraBlock(int[] block, int blockNumber) throws MpegException {
    	int[] quant_matrix;
    	int component;

    	if (blockNumber < 4) {
    		quant_matrix = intra_matrix;
    		component = 0;
            last_dc[component] += decodeDCLum();

    	} else {
    		quant_matrix = chroma_intra_matrix;
    		if ( (blockNumber == 4) || (blockNumber == 6) )
    			component = 1;
    		else //( (blockNumber == 5) || (blockNumber == 7) )
    			component = 2;
    		
            last_dc[component] += decodeDCChrom();

    	}

        block[0] = last_dc[component] << (3 - intra_dc_precision);

        int mismatch = block[0]^1;
        
        int i = 0;
        int j = 0;

        for (;;) {
            int index = in.getVLC(rltable);
            int level = rltable.get_table_level()[index];
            int run   = rltable.get_table_run()[index];

            if (level == 127)
            	break;
            if (level != 0) {
                i += run;
                j = intra_scantable[i];
                level = (level * qscale * quant_matrix[j]) >> 4;
                if (in.getTrueFalse())
                    level = (level ^ ~0) + 1;
                
            } else {
                /* escape code */
                run = in.getBits(6) + 1;
                
                if (in.getTrueFalse())
                    level = in.getBits(11) | (~0x7ff);
                else
                    level = in.getBits(11);
                
                i += run;
                j = intra_scantable[i];
                if (level < 0) {
                    level = ((-level) * qscale * quant_matrix[j]) >> 4;
                    level = -level;
                } else {
                    level = (level * qscale * quant_matrix[j]) >> 4;
                }
            }
            if (i > 63) 
            	throw new MpegException( "Error" );

            mismatch ^= level;
            block[j] = level;
        }
        block[63] ^= mismatch & 1;
    }
    /**
     * Read the quantization scale
     */
    private int get_qscale() {
	
        int qscale;
        if (mpeg2) {
            if (q_scale_type) {
                qscale = MpegConstants.non_linear_qscale[in.getBits(5)];
            } else {
                qscale = in.getBits(5) << 1;
            }
        } else {
            /* for mpeg1, we use the generic unquant code */
            qscale = in.getBits(5);
        }
        return qscale;
    }
    /**
     * Start to decode a frame.  This includes:
     *  - Frame reference number
     *  - Frame type (I, P, or B)
     *  - Field management
     */
    private void decodePicture() {
//    	Timer t = new Timer();

        int frameReference = in.getBits( 10 );
        int f_code;
        
        pict_type = in.getBits( 3 );
        
        in.getBits(16);
        if ( pict_type == MpegConstants.P_TYPE || pict_type == MpegConstants.B_TYPE ) {
            full_pel[0] = in.getTrueFalse();
            f_code = in.getBits(3);
            mpeg_f_code[0] = f_code;
            mpeg_f_code[1] = f_code;
        }
        if ( pict_type == MpegConstants.B_TYPE ) {
            full_pel[1] = in.getTrueFalse();
            f_code = in.getBits(3);
            mpeg_f_code[2] = f_code;
            mpeg_f_code[3] = f_code;
        }
        
        y_dc_scale = 8;
        c_dc_scale = 8;
        first_slice = true;
//        t.print("decodePicture: ");
        
    }


	public static Picture avcodec_get_frame_defaults() {
		Picture pic = new Picture();
		
		pic.set_pts(AVUtil.AV_NOPTS_VALUE);
		pic.set_best_effort_timestamp(AVUtil.AV_NOPTS_VALUE);
		pic.set_pkt_pos(-1);
		pic.set_key_frame(1);
		pic.set_sample_aspect_ratio(new AVRational(0, 1));
		pic.set_formatA(null); 	/* unknown */
		pic.set_formatV(null); 	/* unknown */
		
		return pic;
	}
	
	
}
