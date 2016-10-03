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

import java.io.IOException;

import uk.ac.liv.ffmpeg.libavformat.mxf.ByteReader;


public class Batch {

	ByteReader reader;

	private UInt32 nbElem;
	private UInt32 lenElem;
	
	
	public Batch(ByteReader reader) {
		super();
		this.reader = reader;
	}
	
	public void read() throws IOException {
		nbElem = reader.read_UInt32();
		lenElem = reader.read_UInt32();		
	}
	
	public String toString() {
		return "nbElem: " + nbElem + " - lenElem: " + lenElem;
	}

	public int getNbElem() {
		return (int)nbElem.toInt();
	}

	public UInt32 getLenElem() {
		return lenElem;
	}
	
}
