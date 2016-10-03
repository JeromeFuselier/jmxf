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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class ByteWriter {
	

    ByteBuffer backBuffer;
    int position;
    
    
	public ByteWriter(int size) {
		backBuffer = ByteBuffer.allocate(size);
		
		position = 0;
		
	}
	
	public void put(byte b) {
		backBuffer.position(position);
		backBuffer.put(b);
		position++;
	}
	
	public void putle16(long value) {
		
		byte[] b = {(byte)(value >>> 8),
				    (byte) value };
        
        for (int i = 1 ; i >= 0 ; i--) {
        	put(b[i]);
        }
	}

	
	public void dump(String filename) {
		backBuffer.rewind();
		File file = new File(filename);
		
		try {
		    FileChannel wChannel = new FileOutputStream(file, false).getChannel();
		    wChannel.write(backBuffer);
		    wChannel.close();
		} catch (IOException e) {
		}
		
	}

	public void putString(String string) {
		for (int i = 0 ; i < string.length() ; i++) {
			put((byte)string.charAt(i));
		}
		
	}

	public void putle32(long value) {

		
		byte[] b = {(byte)(value >>> 24),
					(byte)(value >>> 16),
					(byte)(value >>> 8),
				    (byte) value };
        for (int i = 3 ; i >= 0 ; i--) {
        	put(b[i]);
        }
		
	}

    
    
    
    

}
