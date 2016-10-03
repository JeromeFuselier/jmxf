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

package uk.ac.liv.ffmpeg.libavformat.mxf.types;

public class TimeStamp {
	
	private UInt16 year;
	private UInt8 month;
	private UInt8 day;
	private UInt8 hour;
	private UInt8 min;
	private UInt8 sec;
	private UInt8 mSec;
	
	
	public TimeStamp(UInt16 year, UInt8 month, UInt8 day, UInt8 hour, UInt8 min, UInt8 sec,
			UInt8 mSec) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.min = min;
		this.sec = sec;
		this.mSec = mSec;
	}
	
	public String toString() {
		return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + "." + mSec;
		
	}
	                                                                                           

}
