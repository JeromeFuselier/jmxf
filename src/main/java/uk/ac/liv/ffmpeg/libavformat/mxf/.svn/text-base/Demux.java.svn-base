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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import uk.ac.liv.ffmpeg.AVIOContext;
import uk.ac.liv.ffmpeg.AVPacket;
import uk.ac.liv.ffmpeg.AVStream;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.AVDiscard;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavformat.AVFormatContext;
import uk.ac.liv.ffmpeg.libavformat.AVFormatParameters;
import uk.ac.liv.ffmpeg.libavformat.AVInputFormat;
import uk.ac.liv.ffmpeg.libavformat.AVProbeData;
import uk.ac.liv.ffmpeg.libavformat.UtilsFormat;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.AVStreamParseType;
import uk.ac.liv.ffmpeg.libavformat.mxf.Constants.MetadataSetType;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.CryptographicContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Descriptor;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.IndexTableSegment;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.MetadataReadTableEntry;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Package;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Sequence;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.StructuralComponent;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Track;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt32;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.AVUtil;
import uk.ac.liv.ffmpeg.libavutil.Log;
import uk.ac.liv.ffmpeg.libavutil.Mathematics;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.UtilsArrays;



public class Demux extends AVInputFormat {
	
	long pos_first_frame;
	long editUnitByteCount;
	
	
	public Demux() {
		super();
		this.name = "mxf";
		this.long_name = "Material eXchange Format";
		
		this.add_extension("mxf");
	}



	public int read_probe(AVProbeData p) {
		ByteReader reader = p.getReader();
		try {
			for (int i = 0 ; i < AVProbeData.PROBE_BUF_MIN ; i++) {
				UID key;
				// Read a potential 16 bytes key
				key = reader.show_UID();
				
				if (key.equals(Constants.HEADER_PARTITION_PACK_KEY))
					return AVProbeData.AVPROBE_SCORE_MAX;
				reader.read_byte();
			}
		
		} catch (IOException e) {
				e.printStackTrace();
		};			

		return 0;
	}

	
    public int read_seek(AVFormatContext s, int stream_index, long sample_time, int flags) {
/*
    	AVStream st = s.get_stream(stream_index);
    	long seconds;

    	if (s.get_bit_rate() == 0)
    		return -1;
    	if (sample_time < 0)
    		sample_time = 0;
    	seconds = Mathematics.av_rescale(sample_time, st.get_time_base().get_num(), st.get_time_base().get_den());
		try {
	//		s.get_pb().get_reader().seek((s.get_bit_rate() * seconds) >> 3);
			s.get_pb().get_reader().seek((s.get_bit_rate() * 45) >> 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	s.av_update_cur_dts(st, sample_time);
    	return 0;*/
    	
    	try {
			
			long new_index = pos_first_frame + editUnitByteCount * sample_time;
	//		long new_index = (s.get_bit_rate() * 30) >> 3;
			s.get_pb().get_reader().seek(new_index);
				
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
    	
		return 0;
    }

	

	public boolean read_sync(AVIOContext pb, byte [] matchKey) throws IOException {
		ByteReader reader = pb.get_reader();
		boolean found = false;
		
		while ( (reader.remaining() > 0) && (!found) ) {
			byte [] readKey = reader.show_bytes(matchKey.length);
			found = Arrays.equals(readKey, matchKey);
			
			if (!found)
				reader.read_byte();
		}
		return found;
	}
	
	public KLVPacket readKLVPacket(AVIOContext pb) throws IOException {
		ByteReader reader = pb.get_reader();
		
		if (!read_sync(pb, Constants.KLV_KEY))
			return null;

		KLVPacket klv = new KLVPacket();
		klv.setOffset(reader.position());
		
		klv.set_key(reader.read_uid());
		klv.set_length(reader.read_BER());
		//System.out.println(klv.get_offset() + "\t" + klv.get_key() + "\t" + klv.get_length());

		return klv;
	}
	

    public int read_packet(AVFormatContext s, AVPacket pkt) {
    	KLVPacket klv;
		ByteReader reader = s.get_pb().get_reader();
		
		try {
			while (reader.remaining() > 0) {	
				klv = readKLVPacket(s.get_pb());
													
				if (klv.get_key().equals(Constants.ESSENCE_ELEMENT_KEY)) {
					int index = get_stream_index(s, klv);
					
					if (index < 0) {
	    				Log.av_log("mxf", Log.AV_LOG_WARNING, "Error getting stream index %d\n",
	    						klv.get_key().get_track_number());
	    				reader.read_bytes(klv.get_length());
	    				continue;
	    			}
					
					
					if (s.get_stream(index).get_discard() == AVDiscard.AVDISCARD_ALL) {
						reader.read_bytes(klv.get_length());
						continue;
					}
	    			
					// check for 8 channels AES3 element 
	    			if ( (klv.get_key().get(12) == 0x06) && 
	    				 (klv.get_key().get(13) == 0x01) && 
	    				 (klv.get_key().get(14) == 0x10) ) {
	
	    				/*pkt.set_data(UtilsArrays.byte_to_short(reader.read_bytes(klv.get_length())));
	    				pkt.set_size((int)klv.get_length());*/
	    				if (get_d10_aes3_packet(s.get_pb(), s.get_stream(index),
	    						pkt, klv.get_length()) < 0) {
	    					Log.av_log("AVFormatContext", Log.AV_LOG_ERROR, "error reading D-10 aes3 frame\n");
	    					return -1;
	    				}
	    			} else {
	    				UtilsFormat.av_get_packet(s.get_pb(), pkt, (int) klv.get_length());
	    			}
	    			pkt.set_stream_index(index);
	    			pkt.set_pos(klv.get_offset());
	    			return 0;
	    			
				} else
					reader.read_bytes(klv.get_length());
	    	}
		
		} catch (IOException e) {
			return -1;
		}
		
    	return -1;
    }
    

    private int get_d10_aes3_packet(AVIOContext pb, AVStream st, AVPacket pkt, long length) {
    	
    	try {
			pkt.set_data(UtilsArrays.byte_to_short(pb.get_reader().read_bytes(length)));
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
    	short[] buf = pkt.get_data();
		ArrayList<Short> output = new ArrayList<Short>();
    	
		// 4 => skip SMPTE 331M header 
		for ( int idx_buf = 4 ; idx_buf < buf.length ; ) {

			for (int i = 0 ; i < st.get_codec().get_channels() ; i++) {
				long sample = (long) (buf[idx_buf+3] << 24) + (long) (buf[idx_buf+2] << 16) +
				  				(long) (buf[idx_buf+1] <<  8) + (long) (buf[idx_buf]);
				idx_buf += 4;
				
				if ( st.get_codec().get_bits_per_coded_sample() == 24) {
					output.add((short) ((sample >> 4) & 0xff));
					output.add((short) ((sample >> 12) & 0xff));
					output.add((short) ((sample >> 20) & 0xff));
				} else {
					output.add((short) ((sample >> 12) & 0xff));
					output.add((short) ((sample >> 20) & 0xff));
				}

				
			}
			// always 8 channels stored SMPTE 331M
			idx_buf += 32 - st.get_codec().get_channels() * 4;
		}
		
		short [] samples = new short[output.size()];
		for (int i = 0 ; i < output.size() ; i++)
			samples[i] = output.get(i);
		pkt.set_data(samples);
		pkt.set_size(output.size());
		
		// TODO Auto-generated method stub
		return 0;
	}



	static int get_stream_index(AVFormatContext s, KLVPacket klv)
    {
    	for (int i = 0 ; i < s.get_nb_streams() ; i++) {
    		AVStream st = s.get_stream(i);
    		Track track = (Track) st.get_priv_data();

    		/* SMPTE 379M 7.3 */
    		if (track.get_number() == klv.get_key().get_track_number())
    			return i;
    	}
    	/* return 0 if only one stream, for OP Atom files with 0 as track number */
    	return s.get_nb_streams() == 1 ? 0 : -1;
    }
    
    
    public int decrypt_triplet(AVFormatContext s, AVPacket pkt, KLVPacket klv) {
    	// TODO
    	byte [] checkv = {0x43, 0x48, 0x55, 0x4b, 0x43, 0x48, 0x55, 0x4b, 
    			          0x43, 0x48, 0x55, 0x4b, 0x43, 0x48, 0x55, 0x4b};
    	Context mxf = (Context)s.get_priv_data();
    	AVIOContext pb = s.get_pb();
    	
    /*	uint64_t size;
    	uint64_t orig_size;
    	uint64_t plaintext_size;
    	uint8_t ivec[16];
    	uint8_t tmpbuf[16];
    	int index;
    	
    	if (!mxf->aesc && s->key && s->keylen == 16) {
    		mxf->aesc = av_malloc(av_aes_size);
    		if (!mxf->aesc)
    			return -1;
    		av_aes_init(mxf->aesc, s->key, 128, 1);
    	}
    	// crypto context
    	avio_skip(pb, klv_decode_ber_length(pb));
    	// plaintext offset
    	klv_decode_ber_length(pb);
    	plaintext_size = avio_rb64(pb);
    	// source klv key
    	klv_decode_ber_length(pb);
    	avio_read(pb, klv->key, 16);
    	if (!IS_KLV_KEY(klv, mxf_essence_element_key))
    		return -1;
    	index = mxf_get_stream_index(s, klv);
    	if (index < 0)
    		return -1;
    	// source size
    	klv_decode_ber_length(pb);
    	orig_size = avio_rb64(pb);
    	if (orig_size < plaintext_size)
    		return -1;
    	// enc. code
    	size = klv_decode_ber_length(pb);
    	if (size < 32 || size - 32 < orig_size)
    		return -1;
    	avio_read(pb, ivec, 16);
    	avio_read(pb, tmpbuf, 16);
    	if (mxf->aesc)
    		av_aes_crypt(mxf->aesc, tmpbuf, tmpbuf, 1, ivec, 1);
    	if (memcmp(tmpbuf, checkv, 16))
    		av_log(s, AV_LOG_ERROR, "probably incorrect decryption key\n");
    	size -= 32;
    	av_get_packet(pb, pkt, size);
    	size -= plaintext_size;
    	if (mxf->aesc)
    		av_aes_crypt(mxf->aesc, &pkt->data[plaintext_size],
    				&pkt->data[plaintext_size], size >> 4, ivec, 1);
    	pkt->size = orig_size;
    	pkt->stream_index = index;
    	avio_skip(pb, end - avio_tell(pb));
    	*/
    	return -1;
    }

	public int read_header(AVFormatContext s, AVFormatParameters ap) {
		try {
			Context mxf = new Context();
			s.set_priv_data(mxf);
			ByteReader reader = s.get_pb().get_reader();
			s.set_priv_data(mxf);
			//TODO: Improve this. Where is the bitrate ??
			//s.set_bit_rate(50000);
			
			if (!read_sync(s.get_pb(), Constants.HEADER_PARTITION_PACK_KEY)) {
				Log.av_log("mxf", Log.AV_LOG_FATAL, "Could not find header partition pack key\n");
				return -1;
			}
			
			mxf.setFc(s);
			
			while (reader.remaining() > 0) {		
				UID key = reader.show_UID();
				
				KLVPacket klv = readKLVPacket(s.get_pb());
				if (klv == null)
					return -1;
				
				if ( (klv.get_key().equals(Constants.ENCRYPTED_TRIPLET_KEY)) ||
				     (klv.get_key().equals(Constants.ESSENCE_ELEMENT_KEY)) ) {
					// Not header anymore
					reader.seek(klv.get_offset());
					break;
				}
				
				MetadataReadTableEntry metadata = Constants.getMetadata(klv.get_key().toString());
				
				
				if (metadata != null) {
					if (klv.get_key().get(5) == 0x53)
						readLocalTags(s, mxf, klv, metadata);
					else
						metadata.read(s, mxf, klv);
					
					//System.out.println(metadata);
					
				} else {
					reader.read_bytes(klv.get_length());
				}				
				     	   
	    	}
			
			return parseStructuralMetadata(mxf);

		} catch (IOException e) {
			return -1;
		}
	}
	
	

	public int parseStructuralMetadata(Context mxf) {
		Package materialPackage = null;
		Package sourcePackage = null;
		Track materialTrack = null;
		Track sourceTrack = null;
		StructuralComponent component = null;
		MetadataReadTableEntry metadata;
		Package tempPackage;
		Track tempTrack;
		AVStream st;
		CodecUL codecUL;
		CodecUL containerUL;
		Descriptor descriptor = null;
		Descriptor subDescriptor;
		UID essenceContainerUL;
		AVFormatContext s = mxf.getFc(); 
		
		
		// TODO: handle multiple material packages (OP3x) 
		for (int i = 0 ; i < mxf.getPackagesCount() ; i++) {
			metadata = mxf.resolveStrongRef(mxf.getPackagesRefs(i), MetadataSetType.MaterialPackage);
			if (metadata != null) {
				materialPackage = (Package) metadata;
				break;
			}			
		}
		
		if (materialPackage == null) {
			Log.av_log("mxf", Log.AV_LOG_WARNING, "No material package found\n");
			return -1;
		}

		for (int i = 0 ; i < materialPackage.getTracksCount() ; i++) {			
			
			metadata = mxf.resolveStrongRef(materialPackage.getTracksRef(i), MetadataSetType.Track);
			if (metadata == null) {
				Log.av_log("mxf", Log.AV_LOG_WARNING, "Could not resolve material track strong ref\n");
				continue;
			}			
			materialTrack = (Track) metadata;
			
			metadata = mxf.resolveStrongRef(materialTrack.get_sequence_ref(), MetadataSetType.Sequence);
			if (metadata == null) {
				Log.av_log("mxf", Log.AV_LOG_WARNING, "Could not resolve material track sequence strong ref\n");
				continue;
			}			
			materialTrack.setSequence((Sequence) metadata);			

			// TODO: handle multiple source clips 
			for (int j = 0 ; j < materialTrack.get_sequence().getStructuralComponentsCount() ; j++) {

				// TODO: handle timecode component
				metadata = mxf.resolveStrongRef(materialTrack.get_sequence().getStructuralComponentsRef(j), 
						MetadataSetType.SourceClip);
				if (metadata == null) {	
					continue;
				}	
				component = (StructuralComponent) metadata;
				
				sourcePackage = null;
				for (int k = 0 ; k < mxf.getPackagesCount() ; k++) {
					metadata = mxf.resolveStrongRef(mxf.getPackagesRefs(k), 
							MetadataSetType.SourcePackage);
					if (metadata == null) {					
						continue;
					}	
					tempPackage = (Package) metadata;
					
					if ( component.getSourcePackageUID().equals(tempPackage.getPackageUID()) ) {
						sourcePackage = tempPackage;
						break;						
					}
				}
				
				if (sourcePackage == null) {
					Log.av_log("mxf", Log.AV_LOG_WARNING, "Material track %d: no corresponding source package found\n",
							materialTrack.get_track_id());
					break;					
				}
				
				tempTrack = null;
				for (int k = 0 ; k < sourcePackage.getTracksCount() ; k++) {
					metadata = mxf.resolveStrongRef(sourcePackage.getTracksRef(k), 
							MetadataSetType.Track);
					if (metadata == null) {	
						Log.av_log("mxf", Log.AV_LOG_WARNING, "Could not resolve source track strong ref\n");
						return -1;
					}	
					tempTrack = (Track) metadata;
					if ( tempTrack.get_track_id() == component.getSourceTrackID() ) {
						sourceTrack = tempTrack;
						break;
					}
				}
				
				if (sourceTrack == null) {
					Log.av_log("mxf", Log.AV_LOG_WARNING, "Material track %d: no corresponding source package found\n",
							materialTrack.get_track_id());
					break;
				}
				
			}
			
			if (sourceTrack == null)
				continue;
			
			st = mxf.getFc().av_new_stream(sourceTrack.get_track_id());
			
			st.set_priv_data(sourceTrack);
			
			st.set_duration(component.getDuration());
			
			if (st.get_duration() == -1)
				st.set_duration(Constants.AV_NOPTS_VALUE);
			st.set_start_time(component.getStartPosition());
			st.av_set_pts_info(64, 
							materialTrack.get_edit_rate().get_num(), 
							materialTrack.get_edit_rate().get_den());
			
			metadata = mxf.resolveStrongRef(sourceTrack.get_sequence_ref(), 
					MetadataSetType.Sequence);
			if (metadata == null) {	
				Log.av_log("mxf", Log.AV_LOG_WARNING, "Could not resolve source track sequence strong ref\n");
				return -1;
			}	
			sourceTrack.setSequence((Sequence) metadata);
			
			codecUL = mxf.getCodecUL(Constants.DATA_DEFINITION_ULS, 
									 sourceTrack.get_sequence().getDataDefinitionUL());
			
			st.get_codec().set_codec_type(codecUL.getType());	
			
			descriptor = null;
			metadata = mxf.resolveStrongRef(sourcePackage.getDescriptorRef(), 
											MetadataSetType.AnyType);
			if (metadata == null)
				sourcePackage.setDescriptor(null);
			else {
				sourcePackage.setDescriptor((Descriptor) metadata);
				if (sourcePackage.getDescriptor().getType() == MetadataSetType.MultipleDescriptor) {
					
					for (int j = 0 ; j < sourcePackage.getDescriptor().getSubDescriptorsCount() ; j++ ) {

						metadata = mxf.resolveStrongRef(sourcePackage.getDescriptor().getSubDescriptors(j), 
														MetadataSetType.Descriptor);
						if (metadata == null) {
							Log.av_log("mxf", Log.AV_LOG_WARNING, "Could not resolve sub descriptor strong ref\n");
							continue;							
						}
						
						subDescriptor = (Descriptor) metadata;	
						if (subDescriptor.getLinkedTrackID() == 
							sourceTrack.get_track_id()) {
							descriptor = subDescriptor;
							break;			
						}					
						
					}					
				}
			}
			
			if (descriptor == null) {
				Log.av_log("mxf", Log.AV_LOG_WARNING, "source track %d: stream %d, no descriptor found\n",
						sourceTrack.get_track_id(), st.get_index());
				continue;
			}
			
			essenceContainerUL = descriptor.getEssenceContainerUL();
			
			/* HACK: replacing the original key with mxf_encrypted_essence_container
			   is not allowed according to s429-6, try to find correct information anyway */
			if (essenceContainerUL.equals(Constants.ENCRYPTED_ESSENCE_CONTAINER)) {
				Log.av_log("mxf", Log.AV_LOG_WARNING, "broken encrypted mxf file\n");
				
				for (MetadataReadTableEntry meta : mxf.getMetadataSets().values()) {
					if  (meta.getType() == MetadataSetType.CryptoContext) {
						essenceContainerUL = ((CryptographicContext) meta).getSourceContainerUL();
						break;
					}					
				}				
			}

			// TODO: drop PictureEssenceCoding and SoundEssenceCompression, only check EssenceContainer 
			codecUL = mxf.getCodecUL(Constants.CODECS_ULS, descriptor.getEssenceCodecUL());			

			st.get_codec().set_codec_id(codecUL.getID());

			/*if ( descriptor.getExtradata() != null) {
				st.getCodec().setExtradata(descriptor.getExtradata());
			}*/
			
			
			if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_VIDEO) {
				
				containerUL = mxf.getCodecUL(Constants.ESSENCE_CONTAINER_ULS, essenceContainerUL);			
				
				if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_NONE)
					st.get_codec().set_codec_id(containerUL.getID());
				st.get_codec().set_width(descriptor.get_width());
				st.get_codec().set_height(descriptor.get_height());
				//s.set_width(descriptor.get_width());
				//s.set_height(descriptor.get_height() * 2);
				
				st.get_codec().set_bits_per_coded_sample(descriptor.get_bits_per_sample()); /* Uncompressed */
				
				if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_RAWVIDEO)
					st.get_codec().set_pix_fmt(descriptor.getPixFmt());
				
				if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_RAWVIDEO) {
					if ( (descriptor.get_aspect_ratio().get_num() > 0) &&
						 (descriptor.get_aspect_ratio().get_den() > 0) ) {
						if ( descriptor.get_sample_rate().equals(new AVRational(25, 1)) ) {
							if ( descriptor.get_aspect_ratio().equals(new AVRational(4, 3)) )
								st.get_codec().set_sample_aspect_ratio(new AVRational(59, 54));
							else if ( descriptor.get_aspect_ratio().equals(new AVRational(16, 9)) ) 
								st.get_codec().set_sample_aspect_ratio(new AVRational(118, 81));
						} else if  ( descriptor.get_sample_rate().equals(new AVRational(30000, 1001)) ) {

							if ( descriptor.get_aspect_ratio().equals(new AVRational(4, 3)) )
								st.get_codec().set_sample_aspect_ratio(new AVRational(10, 11));
							else if ( descriptor.get_aspect_ratio().equals(new AVRational(16, 9)) ) 
								st.get_codec().set_sample_aspect_ratio(new AVRational(40, 33));
						}
					}
					if (descriptor.get_component_depth() == 8) {
						st.get_codec().set_bits_per_raw_sample(8);
						st.get_codec().set_pix_fmt(PixelFormat.PIX_FMT_UYVY422);
					}
				}
				
				st.set_need_parsing(AVStreamParseType.AVSTREAM_PARSE_HEADERS);
			} else if (st.get_codec().get_codec_type() == AVMediaType.AVMEDIA_TYPE_AUDIO) {
				containerUL = mxf.getCodecUL(Constants.ESSENCE_CONTAINER_ULS, essenceContainerUL);
				if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_NONE)
					st.get_codec().set_codec_id(containerUL.getID());
				st.get_codec().set_channels((int)descriptor.get_channels());
				st.get_codec().set_bits_per_coded_sample(descriptor.get_bits_per_sample());	
				//st.get_codec().set_sample_rate((int)descriptor.get_sample_rate().to_double());
				st.get_codec().set_sample_rate((int)descriptor.get_audio_sampling_rate().to_double());
				// TODO: implement CODEC_ID_RAWAUDIO 
				if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_PCM_S16LE) {
					if (descriptor.get_bits_per_sample() > 24)
						st.get_codec().set_codec_id(CodecID.CODEC_ID_PCM_S32LE);
					else if (descriptor.get_bits_per_sample() > 16)
						st.get_codec().set_codec_id(CodecID.CODEC_ID_PCM_S24LE);
				} else if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_PCM_S16BE) {					
					if (descriptor.get_bits_per_sample() > 24)
						st.get_codec().set_codec_id(CodecID.CODEC_ID_PCM_S32BE);
					else if (descriptor.get_bits_per_sample() > 16)
						st.get_codec().set_codec_id(CodecID.CODEC_ID_PCM_S24BE);
				}  else if (st.get_codec().get_codec_id() == CodecID.CODEC_ID_MP2) {
					st.set_need_parsing(AVStreamParseType.AVSTREAM_PARSE_FULL);
				}
				
			}
			
			if ( (st.get_codec().get_codec_type() != AVMediaType.AVMEDIA_TYPE_DATA) && 
					 ( (essenceContainerUL.get(15) & 0x03) > 0x01) ) {
					Log.av_log("mxf", Log.AV_LOG_WARNING, "Only frame wrapped mappings are correctly supported\n");
					st.set_need_parsing(AVStreamParseType.AVSTREAM_PARSE_FULL);
				}
		}
		
		pos_first_frame = s.get_pb().get_reader().position();
		s.set_pos_first_frame(pos_first_frame);
		
		if (mxf.getIndexTable() != null) {
			editUnitByteCount = mxf.getIndexTable().getEditUnitByteCount();
			long nb_frames = s.get_pb().get_reader().remaining() / editUnitByteCount;
			/*s.set_duration(nb_frames);*/		
		} else {
			editUnitByteCount = -1;
			//s.set_duration(-1);
		}
		
		AVRational time_base = mxf.getIndexTable().getIndexEditRate();
		
		int nb_frames = (int) (s.get_pb().get_reader().size() / editUnitByteCount);
		s.set_duration(nb_frames * time_base.get_den() /
				time_base.get_num() * AVUtil.AV_TIME_BASE);
		
		
		return 0;
	}
	
	
	public int readLocalTags(AVFormatContext s, Context mxf, KLVPacket klv, 
			MetadataReadTableEntry metadata) throws IOException {

		ByteReader r = s.get_pb().get_reader();
		ByteReader reader = new ByteReader(r.read_bytes(klv.get_length()));
		
		
		while (reader.remaining() > 0) {
			
			int tag = (int) reader.read_UInt16().toInt();
	    	int size = (int) reader.read_UInt16().toInt();   
	    	
	    	if (tag == 0x3C0A) {
	    		metadata.setUID(reader.read_uid());
	    	} else {
	    		metadata.readChild(mxf, reader, tag, size);
	    	}	    	
	    }	
		
		if (metadata.getType() == MetadataSetType.Descriptor) {
			Descriptor descr = (Descriptor) metadata;
			if ( (descr.get_height() == 0) &&
				 (descr.getEssenceCodecUL() == null) ) {
				// Audio descriptor and audio codec UL null
				byte [] b = {0x06,0x0E,0x2B,0x34,0x04,0x01,0x01,0x01,
						 	 0x04,0x02,0x02,0x01,0x00,0x00,0x00,0x00};
				
				descr.setEssenceCodecUL(new UID(b));
			}
		} else if (metadata.getType() == MetadataSetType.IndexTableSegment) {
			mxf.setIndexTable((IndexTableSegment)metadata);
		}
		
		mxf.addMetadataSet(metadata);
		
		
		return 0;
	}
	
	
	
	

}
