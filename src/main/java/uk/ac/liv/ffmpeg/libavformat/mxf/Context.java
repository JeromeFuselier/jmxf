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

package uk.ac.liv.ffmpeg.libavformat.mxf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.AVContext;
import uk.ac.liv.ffmpeg.libavcodec.AVCodecContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.IndexTableSegment;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.MetadataReadTableEntry;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;


public class Context extends AVContext  {
	
	ArrayList<LocalTagEntry> localTags;
	
	UID [] packagesRefs;    
	
	Map<String, MetadataReadTableEntry> metadataSets;


    IndexTableSegment indexTable;
    
    AVCodecContext audioCodec;
	AVFormatContext fc;
    
	ArrayList<ArrayList<Long>> audioBuffers;
    
	public Context() {
		super();
		localTags = new ArrayList<LocalTagEntry>(); 
		metadataSets = new HashMap<String, MetadataReadTableEntry>(); 
		audioBuffers = new ArrayList<ArrayList<Long>>();
	}

	public LocalTagEntry getLocalTag(int i) {
		return localTags.get(i);
	}

	public void addLocalTag(LocalTagEntry localTag) {
		this.localTags.add(localTag);
	}

	public int getLocalTagsCount() {
		return localTags.size();
	}
	
	
	public int getMetadataSetCount() {
		return metadataSets.size();
	}
	
	

	public Map<String, MetadataReadTableEntry> getMetadataSets() {
		return metadataSets;
	}

	public int getPackagesCount() {
		return packagesRefs.length;
	}
	
	public UID getPackagesRefs(int i) {
		return packagesRefs[i];
	}


	public void setPackagesRefs(UID[] packagesRefs) {
		this.packagesRefs = packagesRefs;
	}
	
	public IndexTableSegment getIndexTable() {
		return indexTable;
	}

	public void setIndexTable(IndexTableSegment indexTable) {
		this.indexTable = indexTable;
	}

	public AVFormatContext getFc() {
		return fc;
	}

	public void setFc(AVFormatContext fc) {
		this.fc = fc;
	}
	


	public void addMetadataSet(MetadataReadTableEntry meta) {
		this.metadataSets.put(meta.getUID().toString(), meta);
	}

	
	
	
	public MetadataReadTableEntry resolveStrongRef(UID strongRef, MetadataSetType type) {		
		String keyString = strongRef.toString();
		
	
		if ( this.metadataSets.containsKey(keyString) ) {
			MetadataReadTableEntry metadata = this.metadataSets.get(keyString);
			if ( (metadata.getType() == type) || (type == MetadataSetType.AnyType) )
				return metadata;
		}
			
		return null;
	}
	
	
	public void printMetadataSet() {
		for (String k:metadataSets.keySet()) {
			System.out.println(k + " " + metadataSets.get(k));			
		}
		
	}


	public CodecUL getCodecUL(CodecUL [] uls, UID dataUL) {
		for (CodecUL codecUL: uls) {
			if (codecUL.getUID().match(dataUL, codecUL.getMatchingLen()))
				return codecUL;
		}
		return null;
	}


	public void addAudio(ArrayList<Long> array) {
		audioBuffers.add(array);		
	}


	public void setAudioCodec(AVCodecContext codec) {
		this.audioCodec = codec;
		
	}


}
