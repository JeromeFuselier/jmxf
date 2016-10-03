package uk.ac.liv.ffmpeg.libavcodec.h261;

import uk.ac.liv.ffmpeg.libavcodec.mpeg12.RLTable;

public class H261Data {
	
	public static RLTable h261_rl_tcoeff;
	
	public static int [][] h261_tcoeff_vlc = {
			{ 0x2, 2 }, { 0x3, 2 },{ 0x4, 4 },{ 0x5, 5 },
			{ 0x6, 7 },{ 0x26, 8 },{ 0x21, 8 },{ 0xa, 10 },
			{ 0x1d, 12 },{ 0x18, 12 },{ 0x13, 12 },{ 0x10 , 12 },
			{ 0x1a, 13},{ 0x19, 13 }, { 0x18, 13 }, { 0x17, 13 },
			{ 0x3, 3 }, { 0x6, 6 }, { 0x25 , 8 }, { 0xc, 10 },
			{ 0x1b, 12 }, { 0x16, 13 }, { 0x15, 13 }, { 0x5, 4},
			{ 0x4, 7}, { 0xb, 10 }, { 0x14, 12 }, { 0x14, 13 },
			{ 0x7, 5 }, { 0x24, 8 }, { 0x1c, 12 }, { 0x13, 13 },
			{ 0x6, 5 }, { 0xf, 10 }, { 0x12, 12}, { 0x7, 6},
			{ 0x9 , 10 }, { 0x12, 13 }, { 0x5, 6 }, { 0x1e, 12 },
			{ 0x4, 6 }, { 0x15, 12 }, { 0x7, 7 }, { 0x11, 12},
			{ 0x5, 7 }, { 0x11, 13 }, { 0x27, 8 }, { 0x10, 13 },
			{ 0x23, 8 }, { 0x22, 8 }, { 0x20, 8 }, { 0xe , 10 },
			{ 0xd, 10 }, { 0x8, 10 },{ 0x1f, 12 }, { 0x1a, 12 },
			{ 0x19, 12 }, { 0x17, 12 }, { 0x16, 12}, { 0x1f, 13},
			{ 0x1e, 13 }, { 0x1d, 13 }, { 0x1c, 13}, { 0x1b, 13},
			{ 0x1, 6 }                                             //escape
			};

	public static int [] h261_tcoeff_level = {
	    0,  1,  2,  3,  4,  5,  6,  7,
	    8,  9, 10, 11, 12, 13, 14, 15,
	    1,  2,  3,  4,  5,  6,  7,  1,
	    2,  3,  4,  5,  1,  2,  3,  4,
	    1,  2,  3,  1,  2,  3,  1,  2,
	    1,  2,  1,  2,  1,  2,  1,  2,
	    1,  1,  1,  1,  1,  1,  1,  1,
	    1,  1,  1,  1,  1,  1,  1,  1
	};

	public static int [] h261_tcoeff_run = {
	    0,
	    0,  0,  0,  0,  0,  0,  0,  0,
	    0,  0,  0,  0,  0,  0,  0,  1,
	    1,  1,  1,  1,  1,  1,  2,  2,
	    2,  2,  2,  3,  3,  3,  3,  4,
	    4,  4,  5,  5,  5,  6,  6,  7,
	    7,  8,  8,  9,  9, 10, 10, 11,
	   12, 13, 14, 15, 16, 17, 18, 19,
	   20, 21, 22, 23, 24, 25, 26
	};
	
	
	static {
		h261_rl_tcoeff = new RLTable();
	
		h261_rl_tcoeff.set_n(64);
		h261_rl_tcoeff.set_last(64);
		h261_rl_tcoeff.set_table_vlc(h261_tcoeff_vlc);
		h261_rl_tcoeff.set_table_run(h261_tcoeff_run);
		h261_rl_tcoeff.set_table_level(h261_tcoeff_level);		    
		
	}

}
