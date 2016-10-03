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

import uk.ac.liv.ffmpeg.libavcodec.mpeg12.DiscreteCosineChrominanceVlc;
import uk.ac.liv.ffmpeg.libavcodec.mpeg12.DiscreteCosineLuminanceVlc;


public class MpegConstants {
	
	public static final  int [][][] scan = { { { 0,  1,  5,  6, 14, 15, 27, 28},
	        								   { 2,  4,  7, 13, 16, 26, 29, 42},
		 								       { 3,  8, 12, 17, 25, 30, 41, 43},
		 								       { 9, 11, 18, 24, 31, 40, 44, 53},
		 								       {10, 19, 23, 32, 39, 45, 52, 54},
		 								       {20, 22, 33, 38, 46, 51, 55, 60},
		 								       {21, 34, 37, 47, 50, 56, 59, 61},
		 								       {35 ,36, 48, 49, 57, 58, 62, 63} },
		 								  
		 								     { { 0,  4,  6, 20, 22, 36, 38, 52},
		 								       { 1,  5,  7, 21, 23, 37, 39, 53},
			 							       { 2,  8, 19, 24, 34, 40, 50, 54},
			 							       { 3,  9, 18, 25, 35, 41, 51, 55},
			 							       {10, 17, 26, 30, 42, 46, 56, 60},
			 							       {11, 16, 27, 31, 43, 47, 57, 61},
			 							       {12, 15, 28, 32, 44, 48, 58, 62},
			 							       {13, 14, 29, 33, 45, 49, 59, 63} } };
	 
	public static final  int [] defaultIntra = {  8, 16, 19, 22, 26, 27, 29, 34,
		                                           16, 16, 22, 24, 27, 29, 34, 37,
		                                           19, 22, 26, 27, 29, 34, 34, 38,
		                                           22, 22, 26, 27, 29, 34, 37, 40,
		                                           22, 26, 27, 29, 32, 35, 40, 48,
		                                           26, 27, 29, 32, 35, 40, 48, 58,
		                                           26, 27, 29, 34, 38, 46, 56, 69,
		                                           27, 29, 35, 38, 46, 56, 69, 83 };
		                                       	 
   	public static final  int [] defaultNonIntra = { 16, 16, 16, 16, 16, 16, 16, 16,
		 											  16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16,
   		                                              16, 16, 16, 16, 16, 16, 16, 16 };
   	
	public static final  int [] zigzagDirect = {  0,  1,  8, 16,  9,  2,  3, 10,
												 17, 24, 32, 25, 18, 11,  4,  5,
												 12, 19, 26, 33, 40, 48, 41, 34,
												 27, 20, 13,  6,  7, 14, 21, 28,
												 35, 42, 49, 56, 57, 50, 43, 36,
												 29, 22, 15, 23, 30, 37, 44, 51,
												 58, 59, 52, 45, 38, 31, 39, 46,
												 53, 60, 61, 54, 47, 55, 62, 63};

    public static enum Profiles { Reserved,
    	 						  SP,   // Simple
    	 						  MP,   // Main
    	 						  SNR,  // Scalable
    	 						  Spt,  // Spatially Scalable
    	 						  HP }; // High
    	 						  
    public static enum Levels { Reserved,
		   					    LL,   // Low
    							ML,   // Main
    							H_14, // High-1440
    							HL }; // High    	 						  
    						    
    public static enum ChromaFormats { Reserved,
    								   c4_2_0,
    								   c4_2_2,
    								   c4_4_4 };
    								   
	public static enum PictureCodingTypes { Reserved,
											Intra,
											Predictive,
											Bidirectional,
											Forbidden };
											
	public static enum PictureStructures { Reserved,
										   TopField,
										   BottomField,
										   FramePicture };
										   
											
    public static enum ScalableModes { data_partitioning,
									   spatial_scalability,
									   snr_scalability,
									   temporal_scalability };
									   
	public static final  int [][] quantiserScale = {
		{0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36,
		 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 62},
		{0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36,
		 40, 44, 48, 52, 56, 64, 72, 80, 88, 96, 104, 112}
	};
    	

    /**
     * Synchronisation codes
     */
    public static final int SYNC_BYTES = 0x000001;

    public static final int SEQ_END_CODE         = 0x00000b7;
    public static final int SEQ_START_CODE       = 0x00000b3;
    public static final int GOP_START_CODE       = 0x00000b8;
    public static final int PICTURE_START_CODE   = 0x0000000;
    public static final int SLICE_MIN_START_CODE = 0x0000001;
    public static final int SLICE_MAX_START_CODE = 0x00000af;
    public static final int EXT_START_CODE       = 0x00000b5;
    public static final int USER_START_CODE      = 0x00000b2;

    /**
     * Extension codes
     */
    public static final int SEQUENCE_EXTENSION         = 1;
    public static final int SEQUENCE_DISPLAY_EXTENSION = 2;
    public static final int QUANT_MATRIX_EXTENSION     = 3;
    public static final int PICTURE_DISPLAY_EXTENSION  = 7;
    public static final int PICTURE_CODING_EXTENSION   = 8;

    /**
     * Picture types
     */
    public static final int I_TYPE = 1;
    public static final int P_TYPE = 2;
    public static final int B_TYPE = 3;
    public static final int SKIP_FRAME_TYPE = -1;
    
    public static final int PICT_FRAME = 3;

    public static final int MT_FIELD = 1;
    public static final int MT_FRAME = 2;
    public static final int MT_16X8  = 2;
    public static final int MT_DMV   = 3;
    
	/**
	 * Macroblock motion types
	 */
	public static final int MV_TYPE_16X16 = 0;   // 1 vector for the whole mb 
	public static final int MV_TYPE_8X8   = 1;   // 4 vectors (h263,  4MV) 
	public static final int MV_TYPE_16X8  = 2;   // 2 vectors, one per 16x8 block  
	public static final int MV_TYPE_FIELD = 3;   // 2 vectors, one per field  
	public static final int MV_TYPE_DMV   = 4;   // 2 vectors, special mpeg2 Dual Prime Vectors 
	

    /**
     * Macroblock motion direction (from last or to next I/P frame)
     */
    public static final int MV_DIR_FORWARD  = 2;
    public static final int MV_DIR_BACKWARD = 1;
   	 
    
    

    public static final VLCTable mbincr_vlc = new AddressIncrementVlc();
    public static final RLTable rl_mpeg1 = new Mpeg1RLTable();
    public static final RLTable rl_mpeg2 = new Mpeg2RLTable();    
    public static final VLCTable dc_lum_vlc = new DiscreteCosineLuminanceVlc();
    public static final VLCTable dc_chroma_vlc =  new DiscreteCosineChrominanceVlc();
    public static final VLCTable mb_ptype_vlc = new MbPTypeVLC();
    public static final VLCTable mb_btype_vlc = new MbBTypeVLC();    
    public static final VLCTable mb_pat_vlc = new PatVLC();


    public static final  ScanTable alternateVerticalScanTable   = new AlternateVerticalScan();
    public static final  ScanTable alternateHorizontalScanTable = new AlternateHorizontalScan();
    public static final  ScanTable zigZagDirect                 = new ZigZagDirect();
    

    public static final  int[] dsp_idct_permutation          = Tables.getDspIdctPermutation();

    public static final int[] non_linear_qscale = Tables.getNonLinearQscale();

    public static final VLCTable mv_vlc     = new MotionVectorVlc();
   
    
}









