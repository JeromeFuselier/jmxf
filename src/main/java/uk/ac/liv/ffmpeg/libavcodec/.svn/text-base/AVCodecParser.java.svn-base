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

package uk.ac.liv.ffmpeg.libavcodec;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.util.OutOI;

public class AVCodecParser {
	
	CodecID [] codec_ids = new CodecID[5]; /* several codec IDs are permitted */
    int priv_data_size;
       
    
    public CodecID [] get_codec_ids() {
		return codec_ids;
	}

	public CodecID get_codec_id(int i) {
		return codec_ids[i];
	}

	public void set_codec_ids(CodecID [] codec_ids) {
		this.codec_ids = codec_ids;
	}

	public int get_priv_data_size() {
		return priv_data_size;
	}

	public void set_priv_data_size(int priv_data_size) {
		this.priv_data_size = priv_data_size;
	}

	int parser_init(AVCodecParserContext s) {
    	return -1;
    }
    
    OutOI parser_parse(AVCodecParserContext s,
                	 AVCodecContext avctx,
                     short[] buf) {
    	return new OutOI(null, -1);
    }
    
    void parser_close(AVCodecParserContext s) {
    	return ;
    }
    
    public int split(AVCodecContext avctx, short[] s) {
    	return -1;
    }

}
