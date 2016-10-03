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

package uk.ac.liv.ffmpeg.libavformat;

public class ProbeInputFormatRet {
	
	AVInputFormat fmt;
	int score;
	
	

	public ProbeInputFormatRet(int score, AVInputFormat fmt) {
		super();
		this.fmt = fmt;
		this.score = score;
	}

	public AVInputFormat get_fmt() {
		return fmt;
	}
	
	public void set_fmt(AVInputFormat fmt) {
		this.fmt = fmt;
	}
	
	public int get_score() {
		return score;
	}
	
	public void set_score(int score) {
		this.score = score;
	}
	
	

}
