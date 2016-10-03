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
 * Creation   : January 2011
 *  
 *****************************************************************************/

package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import uk.ac.liv.ffmpeg.libavcodec.AVFrame;

public class Picture extends AVFrame implements Cloneable  {
	
	 /**
     * halfpel luma planes.
     */
    byte [][] interpolated;
    int [][][] motion_val_base = { {null, null}, {null, null} };
    long [] mb_type_base;
    
    int [] field_poc = new int[2];	///< h264 top/bottom POC
    int poc;                    ///< h264 frame POC
    int frame_num;              ///< h264 frame_num (raw frame_num from slice header)
    int mmco_reset;             ///< h264 MMCO_RESET set this 1. Reordering code must not mix pictures before and after MMCO_RESET.
    int pic_id;                 /**< h264 pic_num (short -> no wrap version of pic_num,
                                     pic_num & max_pic_num; long -> long_pic_num) */
    int long_ref;               ///< 1->long term reference 0->short term reference
    int [][][] ref_poc = new int [2][2][16]; ///< h264 POCs of the frames used as reference (FIXME need per slice)
    int [][] ref_count = new int[2][2];        ///< number of entries in ref_poc              (FIXME need per slice)
    int mbaff;                  ///< h264 1 -> MBAFF frame 0-> not MBAFF
    int field_picture;          ///< whether or not the picture was encoded in seperate fields

    int mb_var_sum;             ///< sum of MB variance for current frame
    int mc_mb_var_sum;          ///< motion compensated MB variance for current frame
    int [] mb_var;           ///< Table for MB variances
    int [] mc_mb_var;        ///< Table for motion compensated MB variances
    byte [] mb_mean;           ///< Table for MB luminance
    long [] mb_cmp_score;      ///< Table for MB cmp scores, for mb decision FIXME remove
    int b_frame_score;          /* */
    MpegEncContext owner2; ///< pointer to the MpegEncContext that allocated this picture
    
    
   
    public byte[][] get_interpolated() {
		return interpolated;
	}

	public void set_interpolated(byte[][] interpolated) {
		this.interpolated = interpolated;
	}

	public int[][][] get_motion_val_base() {
		return motion_val_base;
	}

	public void set_motion_val_base(int[][][] motion_val_base) {
		this.motion_val_base = motion_val_base;
	}

	public long [] get_mb_type_base() {
		return mb_type_base;
	}

	public void set_mb_type_base(long [] mb_type_base) {
		this.mb_type_base = mb_type_base;
	}

	public int[] get_field_poc() {
		return field_poc;
	}

	public void set_field_poc(int[] field_poc) {
		this.field_poc = field_poc;
	}

	public int get_poc() {
		return poc;
	}

	public void set_poc(int poc) {
		this.poc = poc;
	}

	public int get_frame_num() {
		return frame_num;
	}

	public void set_frame_num(int frame_num) {
		this.frame_num = frame_num;
	}

	public int get_mmco_reset() {
		return mmco_reset;
	}

	public void set_mmco_reset(int mmco_reset) {
		this.mmco_reset = mmco_reset;
	}

	public int get_pic_id() {
		return pic_id;
	}

	public void set_pic_id(int pic_id) {
		this.pic_id = pic_id;
	}

	public int get_long_ref() {
		return long_ref;
	}

	public void set_long_ref(int long_ref) {
		this.long_ref = long_ref;
	}

	public int[][][] get_ref_poc() {
		return ref_poc;
	}

	public void set_ref_poc(int[][][] ref_poc) {
		this.ref_poc = ref_poc;
	}

	public int[][] get_ref_count() {
		return ref_count;
	}

	public void set_ref_count(int[][] ref_count) {
		this.ref_count = ref_count;
	}

	public int get_mbaff() {
		return mbaff;
	}

	public void set_mbaff(int mbaff) {
		this.mbaff = mbaff;
	}

	public int get_mb_var_sum() {
		return mb_var_sum;
	}

	public void set_mb_var_sum(int mb_var_sum) {
		this.mb_var_sum = mb_var_sum;
	}

	public int get_mc_mb_var_sum() {
		return mc_mb_var_sum;
	}

	public void set_mc_mb_var_sum(int mc_mb_var_sum) {
		this.mc_mb_var_sum = mc_mb_var_sum;
	}

	public int[] get_mb_var() {
		return mb_var;
	}

	public void set_mb_var(int[] mb_var) {
		this.mb_var = mb_var;
	}

	public int[] get_mc_mb_var() {
		return mc_mb_var;
	}

	public void set_mc_mb_var(int[] mc_mb_var) {
		this.mc_mb_var = mc_mb_var;
	}

	public byte[] get_mb_mean() {
		return mb_mean;
	}

	public void set_mb_mean(byte[] mb_mean) {
		this.mb_mean = mb_mean;
	}

	public long[] get_mb_cmp_score() {
		return mb_cmp_score;
	}

	public void set_mb_cmp_score(long[] mb_cmp_score) {
		this.mb_cmp_score = mb_cmp_score;
	}

	public MpegEncContext get_owner2() {
		return owner2;
	}

	public void set_owner2(MpegEncContext owner2) {
		this.owner2 = owner2;
	}

	public int get_b_frame_score() {
		return b_frame_score;
	}

	public void set_b_frame_score(int b_frame_score) {
		this.b_frame_score = b_frame_score;
	}

	public int get_field_picture() {
		return field_picture;
	}

	public void set_field_picture(int field_picture) {
		this.field_picture = field_picture;
	}

	public Object clone() {
    	Picture pic = null;
    	try {
    		pic = (Picture) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if (interpolated != null)
			pic.interpolated = (byte [][]) interpolated.clone();
		if (motion_val_base != null)
			pic.motion_val_base = (int [][][]) motion_val_base.clone();
		if (field_poc != null)
			pic.field_poc  = (int []) field_poc.clone(); 
		if (ref_poc != null)
			pic.ref_poc = (int [][][]) ref_poc.clone();
		if (ref_count != null)
			pic.ref_count = (int [][]) ref_count.clone();  
		if (mb_var != null) 
			pic.mb_var = (int []) mb_var.clone();
		if (mc_mb_var != null)
			pic.mc_mb_var = (int []) mc_mb_var.clone();
		if (mb_mean != null)
			pic.mb_mean = (byte []) mb_mean.clone();
		if (mb_cmp_score != null)
			pic.mb_cmp_score = (long []) mb_cmp_score.clone();  
		    
		
	    return pic;
	}

	public void set_motion_val_base(int i, int[][] tab) {
		motion_val_base[i] = tab;
	}

	public int[][] get_motion_val_base(int i) {
		return motion_val_base[i];
	}

	public void set_motion_val(int i, int[][] tab) {
		motion_val[i] = tab;
	}
    
    
    

}
