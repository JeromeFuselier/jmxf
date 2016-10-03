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

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.ffmpeg.libavcodec.AVCodec.CodecID;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.ContentStorage;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.CryptographicContext;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Descriptor;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.IndexTableSegment;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.MetadataReadTableEntry;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Package;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.PrimerPack;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Sequence;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.StructuralComponent;
import uk.ac.liv.ffmpeg.libavformat.mxf.metadata.Track;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;


public class Constants {
	
	Map<String, String> labels;
	
	static private Constants _instance;  
	
	public static final long AV_NOPTS_VALUE = Long.MAX_VALUE;
	
	public static final byte [] HEADER_PARTITION_PACK_KEY   = {0x06, 0x0e, 0x2b, 0x34, 0x02, 0x05, 0x01, 0x01, 0x0d, 0x01, 0x02, 0x01, 0x01, 0x02};
	public static final byte [] KLV_KEY					    = {0x06, 0x0e, 0x2b, 0x34};
	
	public static final byte [] ESSENCE_ELEMENT_KEY         = {0x06, 0x0e, 0x2b, 0x34, 0x01, 0x02, 0x01, 0x01, 0x0d, 0x01, 0x03, 0x01};
	public static final byte [] ENCRYPTED_TRIPLET_KEY       = {0x06, 0x0e, 0x2b, 0x34, 0x02, 0x04, 0x01, 0x07, 0x0d, 0x01, 0x03, 0x01, 0x02, 0x7e, 0x01, 0x00};
	public static final byte [] ENCRYPTED_ESSENCE_CONTAINER = {0x06, 0x0e, 0x2b, 0x34, 0x04, 0x01, 0x01, 0x07, 0x0d, 0x01, 0x03, 0x01, 0x02, 0x0b, 0x01, 0x00};
	
	public static Map<String, MetadataSetType> METADATA_READ_TABLE;
	

	public static enum MetadataSetType {
	    AnyType,
	    MaterialPackage,
	    SourcePackage,
	    SourceClip,
	    TimecodeComponent,
	    Sequence,
	    MultipleDescriptor,
	    Descriptor,
	    Track,
	    CryptoContext,
	    Preface,
	    Identification,
	    ContentStorage,
	    SubDescriptor,
	    IndexTableSegment,
	    EssenceContainerData,
	    PrimerPack
	};

	
	
	public static final CodecUL [] DATA_DEFINITION_ULS = {
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0103-020201000000}"), 13, AVMediaType.AVMEDIA_TYPE_VIDEO),
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0103-020202000000}"), 13, AVMediaType.AVMEDIA_TYPE_AUDIO),
		new CodecUL(UID.fromString("{00000000-0000-0000-0000-000000000000}"),  0, AVMediaType.AVMEDIA_TYPE_DATA)
	};

	
	public static final CodecUL [] CODECS_ULS = {
	    /* PictureEssenceCoding */
		new CodecUL(UID.fromString("{060E2B34-0401-0103-0401-020201011100}"), 14, CodecID.CODEC_ID_MPEG2VIDEO), /* MP@ML Long GoP */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0401-020201020101}"), 14, CodecID.CODEC_ID_MPEG2VIDEO), /* D-10 50Mbps PAL */
		new CodecUL(UID.fromString("{060E2B34-0401-0103-0401-020201030300}"), 14, CodecID.CODEC_ID_MPEG2VIDEO), /* MP@HL Long GoP */
		new CodecUL(UID.fromString("{060E2B34-0401-0103-0401-020201040200}"), 14, CodecID.CODEC_ID_MPEG2VIDEO), /* 422P@HL I-Frame */
		new CodecUL(UID.fromString("{060E2B34-0401-0103-0401-020201200203}"), 14,      CodecID.CODEC_ID_MPEG4), /* XDCAM proxy_pal030926.mxf */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0401-020202010200}"), 13,    CodecID.CODEC_ID_DVVIDEO), /* DV25 IEC PAL */
		new CodecUL(UID.fromString("{060E2B34-0401-0107-0401-020203010100}"), 14,   CodecID.CODEC_ID_JPEG2000), /* JPEG2000 Codestream */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0401-02017F000000}"), 13,   CodecID.CODEC_ID_RAWVIDEO), /* Uncompressed */
		new CodecUL(UID.fromString("{060E2B34-0401-010A-0401-020101020101}"), 16,   CodecID.CODEC_ID_RAWVIDEO), /* Uncompressed, 8-bit, interleaved, UYVY 4:2:2 */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0401-020203020000}"), 14,      CodecID.CODEC_ID_DNXHD), /* SMPTE VC-3/DNxHD */
		/* SoundEssenceCompression */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0402-020100000000}"), 13,  CodecID.CODEC_ID_PCM_S16LE), /* Uncompressed */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0402-02017F000000}"), 13,  CodecID.CODEC_ID_PCM_S16LE),
		new CodecUL(UID.fromString("{060E2B34-0401-0107-0402-02017E000000}"), 13,  CodecID.CODEC_ID_PCM_S16BE), /* From Omneon MXF file */
		new CodecUL(UID.fromString("{060E2B34-0401-0104-0402-020203010100}"), 15,   CodecID.CODEC_ID_PCM_ALAW), /* XDCAM Proxy C0023S01.mxf */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0402-020203020100}"), 15,        CodecID.CODEC_ID_AC3),
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0402-020203020500}"), 15,        CodecID.CODEC_ID_MP2), /* MP2 or MP3 */
		new CodecUL(UID.fromString("{00000000-0000-0000-0000-000000000000}"),  0,       CodecID.CODEC_ID_NONE)
	};
	

	public static final CodecUL [] ESSENCE_CONTAINER_ULS = {
		// video essence container uls
		new CodecUL(UID.fromString("{060E2B34-0401-0102-0D01-030102046001}"), 14, CodecID.CODEC_ID_MPEG2VIDEO), /* MPEG-ES Frame wrapped */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0D01-030102024101}"), 14, CodecID.CODEC_ID_DVVIDEO), /* DV 625 25mbps */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0D01-030102050000}"), 14, CodecID.CODEC_ID_RAWVIDEO), /* Uncompressed Picture */
		// sound essence container uls	
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0D01-030102060100}"), 14, CodecID.CODEC_ID_PCM_S16LE), /* BWF Frame wrapped */
		new CodecUL(UID.fromString("{060E2B34-0401-0102-0D01-030102044001}"), 14,       CodecID.CODEC_ID_MP2), /* MPEG-ES Frame wrapped, 40 ??? stream id */
		new CodecUL(UID.fromString("{060E2B34-0401-0101-0D01-030102010101}"), 14, CodecID.CODEC_ID_PCM_S16LE), /* D-10 Mapping 50Mbps PAL Extended Template */
		new CodecUL(UID.fromString("{00000000-0000-0000-0000-000000000000}"),  0,      CodecID.CODEC_ID_NONE),
	};
	
	
	public static enum AVStreamParseType {
	    AVSTREAM_PARSE_NONE,
	    AVSTREAM_PARSE_FULL,       /**< full parsing and repack */
	    AVSTREAM_PARSE_HEADERS,    /**< Only parse headers, do not repack. */
	    AVSTREAM_PARSE_TIMESTAMPS, /**< full parsing and interpolation of timestamps for frames not starting on a packet boundary */
	    AVSTREAM_PARSE_FULL_ONCE,  /**< full parsing and repack of the first frame only, only implemented for H.264 currently */
	};
	
	
	

	static {
		METADATA_READ_TABLE = new HashMap<String, MetadataSetType>();
		METADATA_READ_TABLE.put("{060E2B34-0205-0101-0D01-020101050100}", 
				MetadataSetType.PrimerPack);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101011800}", 
				MetadataSetType.ContentStorage);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101013700}", 
				MetadataSetType.SourcePackage);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101013600}",
				MetadataSetType.MaterialPackage);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101010F00}", 
				MetadataSetType.Sequence);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101011100}", 
				MetadataSetType.SourceClip);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101014400}", 
				MetadataSetType.MultipleDescriptor);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101014200}", 
				MetadataSetType.Descriptor); //Generic Sound
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101012800}", 
				MetadataSetType.Descriptor); // CDCI
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101012900}", 
				MetadataSetType.Descriptor); // RGBA
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101015100}", 
				MetadataSetType.Descriptor); // MPEG 2 video
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101014800}", 
				MetadataSetType.Descriptor); // Wave
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101014700}", 
				MetadataSetType.Descriptor); // AES3
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101013A00}", 
				MetadataSetType.Track); // Static Track
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-010101013B00}", 
				MetadataSetType.Track); // Generic Track
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-040102020000}", 
				MetadataSetType.CryptoContext);
		METADATA_READ_TABLE.put("{060E2B34-0253-0101-0D01-020101100100}", 
				MetadataSetType.IndexTableSegment);
	}
		
	
	static MetadataReadTableEntry getMetadata(String key) {
		MetadataSetType type = Constants.METADATA_READ_TABLE.get(key);
		
		if (type == null)
			return null;
		
		switch (type) {
		case PrimerPack:
			return new PrimerPack(type);
		case ContentStorage:
			return new ContentStorage(type);
		case SourcePackage:
		case MaterialPackage:
			return new Package(type);
		case Sequence:
			return new Sequence(type);
		case SourceClip:
			return new StructuralComponent(type);
		case MultipleDescriptor:
		case Descriptor:
			return new Descriptor(type);
		case Track:
			return new Track(type);
		case CryptoContext:
			return new CryptographicContext(type);
		case IndexTableSegment:
			return new IndexTableSegment(type);
			
		default:
			return null;
		}
		
	}
		
	static public Constants getInstance() {   
		 if (_instance == null)     
		  _instance = new Constants();   
		 return _instance;  
	}  
	
	
	public Constants() {
		super();
		labels = new HashMap<String, String>();
		labels.put("{060E2B34-0401-0101-0D01-020101010100}", "MXF OP1a SingleItem SinglePackage");
		labels.put("{060E2B34-0401-0101-0D01-020101010900}", "MXF OP1a SingleItem SinglePackage");
		labels.put("{060E2B34-0401-0100-0D01-030102010000}", "MXF-GC SMPTE D-10 Mappings");
		labels.put("{060E2B34-0401-0101-0D01-030102010101}", "MXF-GC SMPTE D-10 Mappings");

		/*labels.put("{060E2B34-0401-0101-0D01-020101010100}", "MXF OP1x SingleItem");
		labels.put("{060E2B34-0401-0101-0D01-020101010100}", "MXF OP1a SingleItem SinglePackage");
		labels.put("{060E2B34-0401-0101-0D01-020102010100}", "MXF OP2a PlaylistItems SinglePackage");
		labels.put("{060E2B34-0401-0101-0D01-020103010100}", "MXF OP3a EditItems SinglePackage");
		labels.put("{060E2B34-0401-0101-0D01-020101020100}", "MXF OP1b SingleItem GangedPackages");
		labels.put("{060E2B34-0401-0101-0D01-020102020100}", "MXF OP2b PlaylistItems GangedPackages");
		labels.put("{060E2B34-0401-0101-0D01-020103020100}", "MXF OP3b EditItems GangedPackages");
		labels.put("{060E2B34-0401-0101-0D01-020101030100}", "MXF OP1c SingleItem AlternatePackages");
		labels.put("{060E2B34-0401-0101-0D01-020102030100}", "MXF OP2c PlaylistItems AlternatePackages");
		labels.put("{060E2B34-0401-0101-0D01-020103030100}", "MXF OP3c EditItems AlternatePackages");
		labels.put("{060E2B34-0401-0100-0D01-020100000000}", "MXF Specialized OP");
		labels.put("{060E2B34-0401-0102-0D01-020110000000}", "MXF Specialized OP Atom");
		labels.put("{060E2B34-0401-0100-0D01-030000000000}", "MXF Essence Containers");
		labels.put("{060E2B34-0401-0100-0D01-030102000000}", "MXF Generic Container");
		labels.put("{060E2B34-0401-0100-0D01-030102020000}", "MXF-GC DV-DIF Mappings");
		labels.put("{060E2B34-0401-0100-0D01-030102030000}", "MXF-GC SMPTE D-11 Mappings");
		labels.put("{060E2B34-0401-0100-0D01-030102040000}", "MXF-GC MPEG Elementary Streams");
		labels.put("{060E2B34-0401-0100-0D01-030102050000}", "MXF-GC Uncompressed Pictures");
		labels.put("{060E2B34-0401-0100-0D01-030102060000}", "MXF-GC AES-BWF Audio");
		labels.put("{060E2B34-0401-0100-0D01-030102070000}", "MXF-GC MPEG Packetised Elementary Streams");
		labels.put("{060E2B34-0401-0100-0D01-030102080000}", "MXF-GC MPEG Program Streams");
		labels.put("{060E2B34-0401-0100-0D01-030102090000}", "MXF-GC MPEG Transport Streams");
		labels.put("{060E2B34-0401-0100-0D01-0301020A0000}", "MXF-GC A-law Audio Mappings");
		labels.put("{060E2B34-0401-0100-0D01-0301020B0000}", "MXF-GC Encrypted Data Mappings");
		labels.put("{060E2B34-0401-0100-0D01-0301020C0000}", "MXF-GC JPEG-2000 Picture Mappings");
		labels.put("{060E2B34-0401-0100-0D01-0301020D0000}", "MXF-GC Generic VBI Data Mapping Undefined Payload");
		labels.put("{060E2B34-0401-0103-0D01-0301027f0100}", "MXF-GC Generic Essence Multiple Mappings");
		labels.put("{060E2B34-0401-0101-0103-020101000000}", "SMPTE 12M Timecode Track");
		labels.put("{060E2B34-0401-0101-0103-020102000000}", "SMPTE 12M Timecode Track with active user bits");
		labels.put("{060E2B34-0401-0101-0103-020103000000}", "SMPTE 309M Timecode Track");
		labels.put("{060E2B34-0401-0101-0103-020110000000}", "Descriptive Metadata Track");
		labels.put("{060E2B34-0401-0101-0103-020201000000}", "Picture Essence Track");
		labels.put("{060E2B34-0401-0101-0103-020202000000}", "Sound Essence Track");
		labels.put("{060E2B34-0401-0101-0103-020203000000}", "Data Essence Track");
		labels.put("{060E2B34-0401-0101-0D01-040101000000}", "MXF Descriptive Metadata Scheme 1");
		labels.put("{060E2B34-0401-0104-0D01-040101020101}", "MXF DMS-1 Production Framework constrained to the standard version");
		labels.put("{060E2B34-0401-0104-0D01-040101020102}", "MXF DMS-1 Clip Framework constrained to the extended version");
		labels.put("{060E2B34-0401-0104-0D01-040101020201}", "MXF DMS-1 Clip Framework constrained to the standard version");
		labels.put("{060E2B34-0401-0104-0D01-040101020202}", "MXF DMS-1 Production Framework constrained to the extended version");
		labels.put("{060E2B34-0401-0104-0D01-040101020301}", "MXF DMS-1 Scene Framework constrained to the standard version");
		labels.put("{060E2B34-0401-0104-0D01-040101020302}", "MXF DMS-1 Scene Framework constrained to the extended version");
		labels.put("{060E2B34-0401-0107-0D01-040102000000}", "MXF Cryptographic DM Scheme");
		labels.put("{060E2B34-0401-0107-0D01-040102010100}", "Cryptographic framework for the DC28 MXF cryptographic DM scheme");*/
	}
	
	public String getLabel(String key) {
		String value = labels.get(key);
		if (value != null)
			return value;
		else
			return "Unknown key";
	}
	
	public String getLabel(UID key) {		
		String value = labels.get(key.toString());
		if (value != null)
			return value;
		else
			return "Unknown key";
	}
}