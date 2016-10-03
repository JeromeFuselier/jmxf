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
import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;
import uk.ac.liv.ffmpeg.libavformat.mxf.Context;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;


public class Descriptor extends MetadataReadTableEntry {
	
	UID essenceContainerUL;
	UID essenceCodecUL = new UID();
	AVRational sample_rate;
	AVRational audio_sampling_rate;
	AVRational aspectRatio;
	int width;
	int height;
	int channels;
	int  bitsPerSample;	
	int component_depth;
	UID [] subDescriptors;
	int linkedTrackID;
	PixelFormat pixFmt = PixelFormat.PIX_FMT_YUV420P;
	
	public Descriptor(MetadataSetType type) {
		super(type);
	}


	public void readChild(Context mxf, ByteReader reader, int tag, int size) throws IOException {
		switch (tag) {		
		case 0x3F01:
			subDescriptors = reader.read_UIDBatch();
			break;	
			
		case 0x3004:
			essenceContainerUL = reader.read_uid();
			break;
			
		case 0x3006:
			linkedTrackID = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x3201:/* PictureEssenceCoding */
			essenceCodecUL = reader.read_uid();
			break;
			
		case 0x3203:
			width = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x3202:
			height = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x320E:
			aspectRatio = reader.read_AVRational();
			break;
			
		case 0x3D03:
			//sample_rate = reader.read_AVRational();
			audio_sampling_rate = reader.read_AVRational();
			break;
			
		case 0x3D06:/* SoundEssenceCompression */
			essenceCodecUL = reader.read_uid();
			break;
			
		case 0x3D07:
			channels = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x3D01:
			bitsPerSample = (int) reader.read_UInt32().toInt();
			break;
			
		case 0x3401:
			/* read_pixel_layout */
			break;
			
		case 0x3301:
			component_depth = (int) reader.read_UInt32().toInt();
			break;
		 
		case 0x3001:
			sample_rate = reader.read_AVRational();
			break;

		default:
			reader.read_bytes(size);
		}
	}


	public UID getSubDescriptors(int i) {
		return subDescriptors[i];
	}
	
	public int getSubDescriptorsCount() {
		return subDescriptors.length;
	}


	public void setSubDescriptors(UID[] subDescriptors) {
		this.subDescriptors = subDescriptors;
	}


	public UID getEssenceContainerUL() {
		return essenceContainerUL;
	}


	public void setEssenceContainerUL(UID essenceContainerUL) {
		this.essenceContainerUL = essenceContainerUL;
	}


	public int getLinkedTrackID() {
		return linkedTrackID;
	}


	public void setLinkedTrackID(int linkedTrackID) {
		this.linkedTrackID = linkedTrackID;
	}


	public UID getEssenceCodecUL() {
		return essenceCodecUL;
	}


	public void setEssenceCodecUL(UID essenceCodecUL) {
		this.essenceCodecUL = essenceCodecUL;
	}


	public int get_width() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int get_height() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public AVRational get_aspect_ratio() {
		return aspectRatio;
	}


	public void set_aspect_ratio(AVRational aspectRatio) {
		this.aspectRatio = aspectRatio;
	}


	public AVRational get_sample_rate() {
		return sample_rate;
	}


	public void set_sample_rate(AVRational sampleRate) {
		this.sample_rate = sampleRate;
	}


	public int get_channels() {
		return channels;
	}


	public void set_channels(int channels) {
		this.channels = channels;
	}


	public int get_bits_per_sample() {
		return bitsPerSample;
	}


	public void set_bits_per_sample(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}
	
	
	public int get_component_depth() {
		return component_depth;
	}


	public void set_component_depth(int component_depth) {
		this.component_depth = component_depth;
	}


	public AVRational get_audio_sampling_rate() {
		return audio_sampling_rate;
	}


	public void set_audio_sampling_rate(AVRational audio_sampling_rate) {
		this.audio_sampling_rate = audio_sampling_rate;
	}


	public PixelFormat getPixFmt() {
		return pixFmt;
	}


	public void setPixFmt(PixelFormat pixFmt) {
		this.pixFmt = pixFmt;
	}


	@Override
	public String toString() {
		return type + " {" + uid + "} [subDescriptors="
				+ Arrays.toString(subDescriptors) + ", essenceContainerUL="
				+ essenceContainerUL + ", linkedTrackID=" + linkedTrackID
				+ ", essenceCodecUL=" + essenceCodecUL + ",\n width=" + width
				+ ", height=" + height + ", aspectRatio=" + aspectRatio
				+ ", sampleRate=" + sample_rate + ", channels=" + channels
				+ ", bitsPerSample=" + bitsPerSample + ", pixFmt=" + pixFmt + "]";
	}

}
