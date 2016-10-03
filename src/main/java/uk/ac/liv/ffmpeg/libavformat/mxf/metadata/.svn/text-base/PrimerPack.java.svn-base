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

package uk.ac.liv.ffmpeg.libavformat.mxf.metadata;

import java.io.IOException;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavformat.mxf.KLVPacket;
import uk.ac.liv.ffmpeg.libavformat.mxf.LocalTagEntry;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Batch;
import uk.ac.liv.ffmpeg.libavutil.Log;


public class PrimerPack extends MetadataReadTableEntry  {
		


	public PrimerPack(MetadataSetType type) {
		super(type);
	}


	public void read(AVFormatContext s, Context mxf,  KLVPacket klv) throws IOException  {	
		ByteReader reader = s.get_pb().get_reader();
		
		Batch batch = new Batch(reader);
		batch.read();
				
		if (batch.getLenElem().toInt() != 18)
			Log.av_log("mxf", Log.AV_LOG_WARNING, "Unsupported primer pack item length\n");
					
		for (int i = 0 ; i < batch.getNbElem() ; i++) {
	    	LocalTagEntry localTag = new LocalTagEntry(reader);
	    	localTag.read();
	    	mxf.addLocalTag(localTag);
	    	
			Log.av_dlog("mxf", " - %s", localTag);	
	    }
	}


	@Override
	public String toString() {
		return "PrimerPack {" + uid + "}";
	}
	
	

}
