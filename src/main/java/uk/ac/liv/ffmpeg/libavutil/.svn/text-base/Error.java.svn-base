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

public class Error {
	
	

	public static int EPERM	=  1;	/* Operation not permitted */
	public static int ENOENT	=  2;	/* No such file or directory */
	public static int ESRCH	=  3;	/* No such process */
	public static int EINTR	=  4;	/* Interrupted system call */
	public static int EIO		=  5;	/* I/O error */
	public static int ENXIO	=  6;	/* No such device or address */
	public static int E2BIG	=  7;	/* Argument list too long */
	public static int ENOEXEC	=  8;	/* Exec format error */
	public static int EBADF	=  9;	/* Bad file number */
	public static int ECHILD	= 10;	/* No child processes */
	public static int EAGAIN	= 11;	/* Try again */
	public static int ENOMEM	= 12;	/* Out of memory */
	public static int EACCES	= 13;	/* Permission denied */
	public static int EFAULT	= 14;	/* Bad address */
	public static int ENOTBLK = 15;	/* Block device required */
	public static int EBUSY	= 16;	/* Device or resource busy */
	public static int EEXIST	= 17;	/* File exists */
	public static int EXDEV	= 18;	/* Cross-device link */
	public static int ENODEV	= 19;	/* No such device */
	public static int ENOTDIR	= 20;	/* Not a directory */
	public static int EISDIR	= 21;	/* Is a directory */
	public static int EINVAL	= 22;	/* Invalid argument */
	public static int ENFILE	= 23;	/* File table overflow */
	public static int EMFILE	= 24;	/* Too many open files */
	public static int ENOTTY	= 25;	/* Not a typewriter */
	public static int ETXTBSY	= 26;	/* Text file busy */
	public static int EFBIG	= 27;	/* File too large */
	public static int ENOSPC	= 28;	/* No space left on device */
	public static int ESPIPE	= 29;	/* Illegal seek */
	public static int EROFS	= 30;	/* Read-only file system */
	public static int EMLINK	= 31;	/* Too many links */
	public static int EPIPE	= 32;	/* Broken pipe */
	public static int EDOM	= 33;	/* Math argument out of domain of func */
	public static int ERANGE =  34;	/* Math result not representable */
	
	public static final int AVERROR_BSF_NOT_FOUND      = - Common.MKTAG(0xF8, "BSF"); ///< Bitstream filter not found
	public static final int AVERROR_DECODER_NOT_FOUND  = - Common.MKTAG(0xF8, "DEC"); ///< Decoder not found
	public static final int AVERROR_DEMUXER_NOT_FOUND  = - Common.MKTAG(0xF8, "DEM"); ///< Demuxer not found
	public static final int AVERROR_ENCODER_NOT_FOUND  = - Common.MKTAG(0xF8, "ENC"); ///< Encoder not found
	public static final int AVERROR_EOF                = - Common.MKTAG("EOF "); 		///< End of file
	public static final int AVERROR_EXIT               = - Common.MKTAG("EXIT"); 		///< Immediate exit was requested; the called function should not be restarted
	public static final int AVERROR_FILTER_NOT_FOUND   = - Common.MKTAG(0xF8, "FIL"); ///< Filter not found
	public static final int AVERROR_INVALIDDATA        = - Common.MKTAG("INDA"); 		///< Invalid data found when processing input
	public static final int AVERROR_MUXER_NOT_FOUND    = - Common.MKTAG(0xF8, "MUX"); ///< Muxer not found
	public static final int AVERROR_OPTION_NOT_FOUND   = - Common.MKTAG(0xF8, "OPT"); ///< Option not found
	public static final int AVERROR_PATCHWELCOME       = - Common.MKTAG("PAWE"); 		///< Not yet implemented in FFmpeg, patches welcome
	public static final int AVERROR_PROTOCOL_NOT_FOUND = - Common.MKTAG(0xF8, "PRO"); ///< Protocol not found
	public static final int AVERROR_STREAM_NOT_FOUND   = - Common.MKTAG(0xF8, "STR"); ///< Stream not found
	
	
	
	public static int AVERROR(int e) {
		return -e;
	}

	public static String av_strerror(int errnum) {
		String errstr = "";
		
		if (errnum == AVERROR_BSF_NOT_FOUND)     
			errstr = "Bitstream filter not found";
		else if (errnum == AVERROR_DECODER_NOT_FOUND) 
	    	errstr = "Decoder not found"; 
		else if (errnum == AVERROR_DEMUXER_NOT_FOUND)
	    	errstr = "Demuxer not found"; 
    	else if (errnum == AVERROR_ENCODER_NOT_FOUND) 
	    	errstr = "Encoder not found";
    	else if (errnum == AVERROR_EOF) 
	    	errstr = "End of file";
    	else if (errnum == AVERROR_EXIT)              
	    	errstr = "Immediate exit requested";
    	else if (errnum == AVERROR_FILTER_NOT_FOUND)  
	    	errstr = "Filter not found";
    	else if (errnum == AVERROR_INVALIDDATA)  
	    	errstr = "Invalid data found when processing input";
    	else if (errnum == AVERROR_MUXER_NOT_FOUND)   
	    	errstr = "Muxer not found";
    	else if (errnum == AVERROR_OPTION_NOT_FOUND)  
	    	errstr = "Option not found";
    	else if (errnum == AVERROR_PATCHWELCOME)  
	    	errstr = "Not yet implemented in FFmpeg, patches welcome";
    	else if (errnum == AVERROR_PROTOCOL_NOT_FOUND)
	    	errstr = "Protocol not found";
    	else if (errnum == AVERROR_STREAM_NOT_FOUND)
	    	errstr = "Stream not found";
    	else
    		errstr = String.format("Error number %d occurred", errnum);
		
		return errstr;
	}

	public static void to_implement(String msg) {
		System.err.println(msg);
		
	}
}
