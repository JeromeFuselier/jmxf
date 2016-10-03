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

package uk.ac.liv.ffmpeg.libavcodec;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVPictureType;
import uk.ac.liv.ffmpeg.libavfilter.AVFilterBufferRef;
import uk.ac.liv.ffmpeg.libavformat.AVPanScan;
import uk.ac.liv.ffmpeg.libavutil.AVRational;
import uk.ac.liv.ffmpeg.libavutil.Error;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.UtilsArrays;

public class AVFrame extends AVPicture {
	
	BufferedImage img;
	
	
    /**
     * pointer to the first allocated byte of the picture. Can be used in get_buffer/release_buffer.
     * This isn't used by libavcodec unless the default get/release_buffer() is used.
     * - encoding: 
     * - decoding: 
     */
    short [][] base = {null, null, null, null};
    /**
     * 1 .get_ keyframe, 0.get_ not
     * - encoding: Set by libavcodec.
     * - decoding: Set by libavcodec.
     */
    int key_frame;

    /**
     * Picture type of the frame, see ?_TYPE below.
     * - encoding: Set by libavcodec. for coded_picture (and set by user for input).
     * - decoding: Set by libavcodec.
     */
    AVPictureType pict_type = AVPictureType.AV_PICTURE_TYPE_NONE;

    /**
     * presentation timestamp in time_base units (time when frame should be shown to user)
     * If AV_NOPTS_VALUE then frame_rate = 1/time_base will be assumed.
     * - encoding: MUST be set by user.
     * - decoding: Set by libavcodec.
     */
    long pts;

    /**
     * picture number in bitstream order
     * - encoding: set by
     * - decoding: Set by libavcodec.
     */
    int coded_picture_number;
    /**
     * picture number in display order
     * - encoding: set by
     * - decoding: Set by libavcodec.
     */
    int display_picture_number;

    /**
     * quality (between 1 (good) and FF_LAMBDA_MAX (bad)) 
     * - encoding: Set by libavcodec. for coded_picture (and set by user for input).
     * - decoding: Set by libavcodec.
     */
    int quality; 

    /**
     * buffer age (1.get_was last buffer and dint change, 2.get_..., ...).
     * Set to INT_MAX if the buffer has not been used yet.
     * - encoding: unused
     * - decoding: MUST be set by get_buffer().
     */
    int age;

    /**
     * is this picture used as reference
     * The values for this are the same as the MpegEncContext.picture_structure
     * variable, that is 1.get_top field, 2.get_bottom field, 3.get_frame/both fields.
     * Set to 4 for delayed, non-reference frames.
     * - encoding: unused
     * - decoding: Set by libavcodec. (before get_buffer() call)).
     */
    int reference;

    /**
     * QP table
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    byte [] qscale_table;
    /**
     * QP store stride
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    int qstride;

    /**
     * mbskip_table[mb]>=1 if MB didn't change
     * stride= mb_width = (width+15)>>4
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    protected byte [] mbskip_table;

    /**
     * motion vector table
     * @code
     * example:
     * int mv_sample_log2= 4 - motion_subsample_log2;
     * int mb_width= (width+15)>>4;
     * int mv_stride= (mb_width << mv_sample_log2) + 1;
     * motion_val[direction][x + y*mv_stride][0.get_mv_x, 1.get_mv_y];
     * @endcode
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    protected int [][][]motion_val;

    /**
     * macroblock type table
     * mb_type_base + mb_width + 2
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    protected long [] mb_type;

    /**
     * log2 of the size of the block which a single vector in motion_val represents: 
     * (4.get_16x16, 3.get_8x8, 2.get_ 4x4, 1.get_ 2x2)
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    protected byte motion_subsample_log2;

    /**
     * for some private data of the user
     * - encoding: unused
     * - decoding: Set by user.
     */
    protected Object opaque;

    /**
     * error
     * - encoding: Set by libavcodec. if flags&CODEC_FLAG_PSNR.
     * - decoding: unused
     */
    long [] error = new long[4];

    /**
     * type of the buffer (to keep track of who has to deallocate data[*])
     * - encoding: Set by the one who allocates it.
     * - decoding: Set by the one who allocates it.
     * Note: User allocated (direct rendering) & internal buffers cannot coexist currently.
     */
    int type;
    
    /**
     * When decoding, this signals how much the picture must be delayed.
     * extra_delay = repeat_pict / (2*fps)
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    int repeat_pict;
    
    /**
     * 
     */
    int qscale_type;
    
    /**
     * The content of the picture is interlaced.
     * - encoding: Set by user.
     * - decoding: Set by libavcodec. (default 0)
     */
    int interlaced_frame;
    
    /**
     * If the content is interlaced, is top field displayed first.
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    int top_field_first;
    
    /**
     * Pan scan.
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    AVPanScan pan_scan;
    
    /**
     * Tell user application that palette has changed from previous frame.
     * - encoding: ??? (no palette-enabled encoder yet)
     * - decoding: Set by libavcodec. (default 0).
     */
    int palette_has_changed;
    
    /**
     * codec suggestion on buffer type if != 0
     * - encoding: unused
     * - decoding: Set by libavcodec. (before get_buffer() call)).
     */
    int buffer_hints;

    /**
     * DCT coefficients
     * - encoding: unused
     * - decoding: Set by libavcodec.
     */
    short [] dct_coeff;

    /**
     * motion reference frame index
     * the order in which these are stored can depend on the codec.
     * - encoding: Set by user.
     * - decoding: Set by libavcodec.
     */
    byte [][] ref_index = {null, null};

    /**
     * reordered opaque 64bit (generally an integer or a double precision float
     * PTS but can be anything). 
     * The user sets AVCodecContext.reordered_opaque to represent the input at
     * that time,
     * the decoder reorders values as needed and sets AVFrame.reordered_opaque
     * to exactly one of the values provided by the user through AVCodecContext.reordered_opaque 
     * @deprecated in favor of pkt_pts
     * - encoding: unused
     * - decoding: Read by user.
     */
    long reordered_opaque;

    /**
     * hardware accelerator private data (FFmpeg allocated)
     * - encoding: unused
     * - decoding: Set by libavcodec
     */
    Object hwaccel_picture_private;

    /**
     * reordered pts from the last AVPacket that has been input into the decoder
     * - encoding: unused
     * - decoding: Read by user.
     */
    long pkt_pts;

    /**
     * dts from the last AVPacket that has been input into the decoder
     * - encoding: unused
     * - decoding: Read by user.
     */
    long pkt_dts;

    /**
     * the AVCodecContext which ff_thread_get_buffer() was last called on
     * - encoding: Set by libavcodec.
     * - decoding: Set by libavcodec.
     */
    AVCodecContext owner;

    /**
     * used by multithreading to store frame-specific info
     * - encoding: Set by libavcodec.
     * - decoding: Set by libavcodec.
     */
    Object thread_opaque;

    /**
     * frame timestamp estimated using various heuristics, in stream time base
     * - encoding: unused
     * - decoding: set by libavcodec, read by user.
     */
    long best_effort_timestamp;

    /**
     * reordered pos from the last AVPacket that has been input into the decoder
     * - encoding: unused
     * - decoding: Read by user.
     */
    long pkt_pos;

    /**
     * reordered sample aspect ratio for the video frame, 0/1 if unknownunspecified
     * - encoding: unused
     * - decoding: Read by user.
     */
    AVRational sample_aspect_ratio = new AVRational();

    /**
     * width and height of the video frame
     * - encoding: unused
     * - decoding: Read by user.
     */
    int width, height;

    /**
     * format of the frame, -1 if unknown or unset
     * It should be cast to the corresponding enum (enum PixelFormat
     * for video, enum AVSampleFormat for audio)
     * - encoding: unused
     * - decoding: Read by user.
     */
    PixelFormat formatV = PixelFormat.PIX_FMT_NONE;
    AVSampleFormat formatA = AVSampleFormat.AV_SAMPLE_FMT_NONE;

	

	public short [][] get_base() {
		return base;
	}

	public short [] get_base(int i) {
		return base[i];
	}

	public void set_base(short [][] base) {
		this.base = base;
	}

	public void set_base(int i, short [] base) {
		this.base[i] = base;
	}

	public int get_key_frame() {
		return key_frame;
	}

	public void set_key_frame(int key_frame) {
		this.key_frame = key_frame;
	}

	public AVPictureType get_pict_type() {
		return pict_type;
	}

	public void set_pict_type(AVPictureType pict_type) {
		this.pict_type = pict_type;
	}

	public long get_pts() {
		return pts;
	}

	public void set_pts(long pts) {
		this.pts = pts;
	}

	public int get_coded_picture_number() {
		return coded_picture_number;
	}

	public void set_coded_picture_number(int coded_picture_number) {
		this.coded_picture_number = coded_picture_number;
	}

	public int get_display_picture_number() {
		return display_picture_number;
	}

	public void set_display_picture_number(int display_picture_number) {
		this.display_picture_number = display_picture_number;
	}

	public int get_quality() {
		return quality;
	}

	public void set_quality(int quality) {
		this.quality = quality;
	}

	public int get_age() {
		return age;
	}

	public void set_age(int age) {
		this.age = age;
	}

	public int get_reference() {
		return reference;
	}

	public void set_reference(int reference) {
		this.reference = reference;
	}

	public byte[] get_qscale_table() {
		return qscale_table;
	}

	public void set_qscale_table(byte[] qscale_table) {
		this.qscale_table = qscale_table;
	}

	public int get_qstride() {
		return qstride;
	}

	public void set_qstride(int qstride) {
		this.qstride = qstride;
	}

	public byte[] get_mbskip_table() {
		return mbskip_table;
	}

	public void set_mbskip_table(byte[] mbskip_table) {
		this.mbskip_table = mbskip_table;
	}

	public int[][][] get_motion_val() {
		return motion_val;
	}

	public int[][] get_motion_val(int i) {
		return motion_val[i];
	}

	public void set_motion_val(int[][][] motion_val) {
		this.motion_val = motion_val;
	}

	public void set_motion_val(int i, int[][] motion_val) {
		this.motion_val[i] = motion_val;
	}

	public long[] get_mb_type() {
		return mb_type;
	}

	public void set_mb_type(long[] mb_type) {
		this.mb_type = mb_type;
	}

	public byte get_motion_subsample_log2() {
		return motion_subsample_log2;
	}

	public void set_motion_subsample_log2(byte motion_subsample_log2) {
		this.motion_subsample_log2 = motion_subsample_log2;
	}

	public Object get_opaque() {
		return opaque;
	}

	public void set_opaque(Object opaque) {
		this.opaque = opaque;
	}

	public long[] get_error() {
		return error;
	}
	
	public long get_error(int i) {
		return error[i];
	}

	public void set_error(long[] error) {
		this.error = error;
	}

	public int get_type() {
		return type;
	}

	public void set_type(int type) {
		this.type = type;
	}

	public int get_repeat_pict() {
		return repeat_pict;
	}

	public void set_repeat_pict(int repeat_pict) {
		this.repeat_pict = repeat_pict;
	}

	public int get_qscale_type() {
		return qscale_type;
	}

	public void set_qscale_type(int qscale_type) {
		this.qscale_type = qscale_type;
	}

	public int get_interlaced_frame() {
		return interlaced_frame;
	}

	public void set_interlaced_frame(int interlaced_frame) {
		this.interlaced_frame = interlaced_frame;
	}

	public int get_top_field_first() {
		return top_field_first;
	}

	public void set_top_field_first(int top_field_first) {
		this.top_field_first = top_field_first;
	}

	public AVPanScan get_pan_scan() {
		return pan_scan;
	}

	public void set_pan_scan(AVPanScan pan_scan) {
		this.pan_scan = pan_scan;
	}

	public int get_palette_has_changed() {
		return palette_has_changed;
	}

	public void set_palette_has_changed(int palette_has_changed) {
		this.palette_has_changed = palette_has_changed;
	}

	public int get_buffer_hints() {
		return buffer_hints;
	}

	public void set_buffer_hints(int buffer_hints) {
		this.buffer_hints = buffer_hints;
	}

	public short[] get_dct_coeff() {
		return dct_coeff;
	}

	public void set_dct_coeff(short[] dct_coeff) {
		this.dct_coeff = dct_coeff;
	}

	public byte[][] get_ref_index() {
		return ref_index;
	}

	public byte[] get_ref_index(int i) {
		return ref_index[i];
	}

	public void set_ref_index(byte[][] ref_index) {
		this.ref_index = ref_index;
	}

	public void set_ref_index(int i, byte[] tab) {
		this.ref_index[i] = tab;
	}

	public long get_reordered_opaque() {
		return reordered_opaque;
	}

	public void set_reordered_opaque(long reordered_opaque) {
		this.reordered_opaque = reordered_opaque;
	}

	public Object get_hwaccel_picture_private() {
		return hwaccel_picture_private;
	}

	public void set_hwaccel_picture_private(Object hwaccel_picture_private) {
		this.hwaccel_picture_private = hwaccel_picture_private;
	}

	public long get_pkt_pts() {
		return pkt_pts;
	}

	public void set_pkt_pts(long pkt_pts) {
		this.pkt_pts = pkt_pts;
	}

	public long get_pkt_dts() {
		return pkt_dts;
	}

	public void set_pkt_dts(long pkt_dts) {
		this.pkt_dts = pkt_dts;
	}

	public AVCodecContext get_owner() {
		return owner;
	}

	public void set_owner(AVCodecContext owner) {
		this.owner = owner;
	}

	public Object get_thread_opaque() {
		return thread_opaque;
	}

	public void set_thread_opaque(Object thread_opaque) {
		this.thread_opaque = thread_opaque;
	}

	public long get_best_effort_timestamp() {
		return best_effort_timestamp;
	}

	public void set_best_effort_timestamp(long best_effort_timestamp) {
		this.best_effort_timestamp = best_effort_timestamp;
	}

	public long get_pkt_pos() {
		return pkt_pos;
	}

	public void set_pkt_pos(long pkt_pos) {
		this.pkt_pos = pkt_pos;
	}

	public AVRational get_sample_aspect_ratio() {
		return sample_aspect_ratio;
	}

	public void set_sample_aspect_ratio(AVRational sample_aspect_ratio) {
		this.sample_aspect_ratio = sample_aspect_ratio;
	}

	public int get_width() {
		return width;
	}

	public void set_width(int width) {
		this.width = width;
	}

	public int get_height() {
		return height;
	}

	public void set_height(int height) {
		this.height = height;
	}

	public AVSampleFormat get_formatA() {
		return formatA;
	}

	public void set_formatA(AVSampleFormat formatA) {
		this.formatA = formatA;
	}

	public PixelFormat get_formatV() {
		return formatV;
	}

	public void set_format(PixelFormat formatV) {
		this.formatV = formatV;
	}

	public void set_formatV(PixelFormat formatV) {
		this.formatV = formatV;
	}

	public void set_img(BufferedImage img) {
		this.img = img;		
	}


	public BufferedImage get_img() {
		return img;		
	}

	public int avfilter_fill_frame_from_video_buffer_ref(AVFilterBufferRef picref) { 
		if ( (picref == null) || (picref.get_video() == null))
		        return Error.AVERROR(Error.EINVAL);

		set_data(picref.get_data());
		set_linesize(picref.get_linesize());
	    set_pkt_pos(picref.get_pos());
	    set_interlaced_frame(picref.get_video().get_interlaced());
	    set_top_field_first(picref.get_video().get_top_field_first());
	    set_key_frame(picref.get_video().get_key_frame());
	    set_pict_type(picref.get_video().get_pict_type());
	    set_sample_aspect_ratio(picref.get_video().get_sample_aspect_ratio());

	    return 0;
	}

	public void FFSWAP_DATA12() {
		short [] tmp = Arrays.copyOf(data[1], data[1].length);
		data[1] = Arrays.copyOf(data[2], data[2].length);
		data[1] = Arrays.copyOf(tmp, tmp.length);
	}


    
    
    

}
