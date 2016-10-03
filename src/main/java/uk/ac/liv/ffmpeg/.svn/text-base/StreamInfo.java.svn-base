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

package uk.ac.liv.ffmpeg;

public class StreamInfo {
	
	long last_dts;
    long duration_gcd;
    int duration_count;
    double [] duration_error = new double[AVStream.MAX_STD_TIMEBASES];
    long codec_info_duration;
    
	public long get_last_dts() {
		return last_dts;
	}
	public void set_last_dts(long last_dts) {
		this.last_dts = last_dts;
	}
	public long get_duration_gcd() {
		return duration_gcd;
	}
	public void set_duration_gcd(long duration_gcd) {
		this.duration_gcd = duration_gcd;
	}
	public int get_duration_count() {
		return duration_count;
	}
	public void set_duration_count(int duration_count) {
		this.duration_count = duration_count;
	}
	public double[] get_duration_error() {
		return duration_error;
	}
	public void set_duration_error(double[] duration_error) {
		this.duration_error = duration_error;
	}
	public long get_codec_info_duration() {
		return codec_info_duration;
	}
	public void set_codec_info_duration(long codec_info_duration) {
		this.codec_info_duration = codec_info_duration;
	}
	public void reset_duration_error() {
		for (int i = 0 ; i < duration_error.length ; i++) 
			duration_error[i] = 0;		
	}
	public double get_duration_error(int i) {
		return duration_error[i];
	}
	public void set_duration_error(int i, double d) {
		duration_error[i] = d;
	}
    
    

}
