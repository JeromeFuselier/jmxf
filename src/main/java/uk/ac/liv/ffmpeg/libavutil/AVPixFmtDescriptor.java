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

public class AVPixFmtDescriptor {
	

	public static final int PIX_FMT_BE        = 1; ///< Pixel format is big-endian.
	public static final int PIX_FMT_PAL       = 2; ///< Pixel format has a palette in data[1], values are indexes in this palette.
	public static final int PIX_FMT_BITSTREAM = 4; ///< All values of a component are bit-wise packed end to end.
	public static final int PIX_FMT_HWACCEL   = 8; ///< Pixel format is an HW accelerated format.
	
	String name;
	int nb_components;      ///< The number of components each pixel has, (1-4)

    /**
     * Amount to shift the luma width right to find the chroma width.
     * For YV12 this is 1 for example.
     * chroma_width = -((-luma_width) >> log2_chroma_w)
     * The note above is needed to ensure rounding up.
     * This value only refers to the chroma components.
     */
    int log2_chroma_w;      ///< chroma_width = -((-luma_width )>>log2_chroma_w)

    /**
     * Amount to shift the luma height right to find the chroma height.
     * For YV12 this is 1 for example.
     * chroma_height= -((-luma_height) >> log2_chroma_h)
     * The note above is needed to ensure rounding up.
     * This value only refers to the chroma components.
     */
    int log2_chroma_h;
    int flags;

    /**
     * Parameters that describe how pixels are packed. If the format
     * has chroma components, they must be stored in comp[1] and
     * comp[2].
     */
    AVComponentDescriptor [] comp = new AVComponentDescriptor[4];

    

	public AVPixFmtDescriptor(String name, int nb_components,
			int log2_chroma_w, int log2_chroma_h) {
		super();
		this.name = name;
		this.nb_components = nb_components;
		this.log2_chroma_w = log2_chroma_w;
		this.log2_chroma_h = log2_chroma_h;
	}

	public AVPixFmtDescriptor(String name) {
		super();
		this.name = name;
	}

	public AVPixFmtDescriptor(String name, int nb_components,
			int log2_chroma_w, int log2_chroma_h, int flags) {
		super();
		this.name = name;
		this.nb_components = nb_components;
		this.log2_chroma_w = log2_chroma_w;
		this.log2_chroma_h = log2_chroma_h;
		this.flags = flags;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String name) {
		this.name = name;
	}

	public int get_nb_components() {
		return nb_components;
	}

	public void set_nb_components(int nb_components) {
		this.nb_components = nb_components;
	}

	public int get_log2_chroma_w() {
		return log2_chroma_w;
	}

	public void set_log2_chroma_w(int log2_chroma_w) {
		this.log2_chroma_w = log2_chroma_w;
	}

	public int get_log2_chroma_h() {
		return log2_chroma_h;
	}

	public void set_log2_chroma_h(int log2_chroma_h) {
		this.log2_chroma_h = log2_chroma_h;
	}

	public int get_flags() {
		return flags;
	}
	
	public boolean has_flag(int flag) {
		return (this.flags & flag) != 0;
	}

	public void set_flags(int flags) {
		this.flags = flags;
	}

	public AVComponentDescriptor[] get_comp() {
		return comp;
	}

	public void set_comp(AVComponentDescriptor[] comp) {
		this.comp = comp;
	}

	public void set_comp(int idx, AVComponentDescriptor comp) {
		this.comp[idx] = comp;
	}

	public AVComponentDescriptor get_comp(int idx) {
		return comp[idx];
	}

	public int av_get_bits_per_pixel() {
	    int c, bits = 0;
	    int log2_pixels = get_log2_chroma_w() + get_log2_chroma_h();

	    for (c = 0 ; c < get_nb_components() ; c++) {
	        int s = ( ( c == 1 || c == 2) ? 0 : log2_pixels);
	        bits += (get_comp()[c].depth_minus1+1) << s;
	    }

	    return bits >> log2_pixels;
	}
    
    
    
}
