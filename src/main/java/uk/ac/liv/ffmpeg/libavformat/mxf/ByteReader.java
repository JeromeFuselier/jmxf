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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import sun.java2d.BackBufferCapsProvider;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Bool;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.DeltaEntry;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Enum;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Int16;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Int32;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Int64;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Int8;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.Position;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.TimeStamp;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt16;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt32;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt64;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UInt8;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UMID;
import uk.ac.liv.ffmpeg.libavformat.mxf.types.UTF16;
import uk.ac.liv.ffmpeg.libavutil.AVRational;



public class ByteReader {

     ByteBuffer back_buffer;
     FileChannel fc;
     long pos;
     long remaining;
     long size;
     
     static int SIZEMAX = 500000;
     
    public ByteReader(URI uri, int sizemax) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(new File(uri), "r");
        fc = raf.getChannel();
        size = fc.size();
        remaining = size;
        pos = 0;
        SIZEMAX = sizemax;
        
        // Load at most SIZEMAX bytes of the file in the backBuffer
        update_back_buffer();
    }
    
    
    public ByteReader(URI uri) throws IOException {
    	this(uri, SIZEMAX);
    }
    
    public long size() {
    	return size;
    }
    
    
    
    public ByteReader(byte [] b) throws IOException {
    	back_buffer = ByteBuffer.wrap(b);
    	remaining = b.length;
    }
    
    public ByteReader(short[] s) throws IOException  {
    	byte [] b = new byte[s.length];
    	for (int i = 0 ; i < s.length ; i++)
    		b[i] = (byte) s[i];
    	back_buffer = ByteBuffer.wrap(b);
    	remaining = b.length;
		// TODO Auto-generated constructor stub
	}


	public void update_back_buffer() throws IOException { 
    	if (remaining > SIZEMAX) {
            back_buffer = fc.map(MapMode.READ_ONLY, pos, SIZEMAX);
        } else {
        	if (fc != null) // TODO: fix that bug, fc should never be null
        		back_buffer = fc.map(MapMode.READ_ONLY, pos, remaining);
        }    
    }
    
    public void seek(long seek_index) throws IOException {
    	pos = seek_index;
        remaining = size - pos;
    	update_back_buffer();
    }


    public long position() {
    	return pos;
    }

    
    public long remaining() {
    	return remaining;
    }
    
    
    private byte get() throws IOException {
    	byte b;
    	try {
    		b = back_buffer.get();
    	} catch (BufferUnderflowException e) {
    		update_back_buffer();
    		b = back_buffer.get();
    	}
		remaining -= 1;
    	pos += 1;
    	return b;
    }
    

    public UID read_uid() throws IOException {
    	try {
    		byte [] b = read_bytes(16);    		
    		return new UID(b);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }


    }

    public UID [] read_UIDBatch() throws IOException {
		UInt32 nbElem = read_UInt32();
		UInt32 lenElem = read_UInt32();		
		
		UID [] uids = new UID[(int)nbElem.toInt()];

	    for (int i=0 ; i < nbElem.toInt() ; i++)
	    	uids[i] = read_uid();
		
		return uids;
    }
    

    public UMID read_UMID() throws IOException {
    	try {
    		UID uid1 = read_uid();
    		UID uid2 = read_uid();
    		return new UMID(uid1, uid2);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }


    }
    
    public TimeStamp read_timestamp() throws IOException {
    	try {
    		UInt16 year = read_UInt16(); 
    		UInt8 month = read_UInt8();
    		UInt8 day = read_UInt8();
    		UInt8 hour = read_UInt8();
    		UInt8 min = read_UInt8();
    		UInt8 sec = read_UInt8();
    		UInt8 mSec = read_UInt8();
    		return new TimeStamp(year, month, day, hour, min, sec, mSec);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }
      }

    
    public int read_byte() throws IOException {
    	return get() & 0xff;
    }

    
    public Bool read_bool() throws IOException {
    	return new Bool(read_byte());
    }
    

    // An unsigned 8-bit integer
    public UInt8 read_UInt8() throws IOException {
    	return new UInt8(read_byte());
    }
    

    // A signed 8-bit integer
    public Int8 read_Int8() throws IOException {
    	return new Int8(get());
    }
    
    
    public DeltaEntry read_DeltaEntry() throws IOException {
    	Int8 posTableIndex = read_Int8();
    	UInt8 slice = read_UInt8();
    	UInt32 elementDelta = read_UInt32();
    	
    	return new DeltaEntry(posTableIndex, slice, elementDelta);
    }


    
    public DeltaEntry [] read_DeltaEntryBatch() throws IOException {
		UInt32 nbElem = read_UInt32();
		UInt32 lenElem = read_UInt32();		
		
		DeltaEntry [] values = new DeltaEntry[(int)nbElem.toInt()];

	    for (int i=0 ; i < nbElem.toInt() ; i++)
	    	values[i] = read_DeltaEntry();
		
		return values;
    }
    

    public Enum read_enum() throws IOException {
    	return new Enum(read_byte());
    }
    

    
    // An unsigned 16-bit integer
    public UInt16 read_UInt16() throws IOException {
        try {
            int ch1 = read_byte();
            int ch2 = read_byte();
            return new UInt16((ch1 << 8) + ch2);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }

    
    // An signed 16-bit integer
    public Int16 read_Int16() throws IOException {
        try {
            int ch1 = read_byte();
            int ch2 = read_byte();
            return new Int16((ch1 << 8) + ch2);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    

    // An unsigned 32-bit integer
    public UInt32 read_UInt32() throws IOException {
        try {
            int ch1 = read_byte();
            int ch2 = read_byte();
            int ch3 = read_byte();
            int ch4 = read_byte();
            return new UInt32( (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4 );
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    
    public UInt32 [] read_UInt32Batch() throws IOException {
		UInt32 nbElem = read_UInt32();
		UInt32 lenElem = read_UInt32();		
		
		UInt32 [] values = new UInt32[(int)nbElem.toInt()];

	    for (int i=0 ; i < nbElem.toInt() ; i++)
	    	values[i] = read_UInt32();
		
		return values;
    }
    

    // An unsigned 32-bit integer (low endian)
    public UInt32 read_UInt32le() throws IOException {
        try {
            int ch4 = read_byte();
            int ch3 = read_byte();
            int ch2 = read_byte();
            int ch1 = read_byte();
          
            return new UInt32( (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4 );
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    
    
    // An signed 32-bit integer
    public Int32 read_Int32() throws IOException {
        try {
            int ch1 = read_byte();
            int ch2 = read_byte();
            int ch3 = read_byte();
            int ch4 = read_byte();
            return new Int32( (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4 );
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    

    // A batch of signed 32-bit integer
    public Int32 [] read_Int32Batch() throws IOException {
		UInt32 nbElem = read_UInt32();
		UInt32 lenElem = read_UInt32();		
		
		Int32 [] values = new Int32[(int)nbElem.toInt()];

	    for (int i=0 ; i < nbElem.toInt() ; i++)
	    	values[i] = read_Int32();
		
		return values;
    }
    

    

    // A signed 64-bit integer
    public Int64 read_Int64() throws IOException {
        try {
        	byte [] b = read_bytes(8);        	
            return new Int64(b);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    

    // An unsigned 64-bit integer
    public UInt64 read_UInt64() throws IOException {
        try {
        	byte [] b = read_bytes(8);        	
            return new UInt64(b);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    

    // A position
    public Position read_position() throws IOException {
        try {
        	byte [] b = read_bytes(8);        	
            return new Position(b);
        } catch (BufferOverflowException e) {
            throw new IOException();
        }

    }
    
    
    public byte [] read_bytes(long n) throws IOException {
    	byte [] b = new byte[(int)n];
    	
    	for (int i=0 ; i < n ; i++)
    		b[i] = get();
    		
    	return b;    	
    }
    
    
    public byte [] show_bytes(long n) throws IOException {
    	byte [] b = new byte[(int)n];
    	
    	if (back_buffer.remaining() < n) {
    		update_back_buffer();
    	} 
    	
    	if (back_buffer.remaining() >= n) {
    		back_buffer.mark();        	
        	for (int i=0 ; i < n ; i++)
        		b[i] = back_buffer.get();	
    		back_buffer.reset();
    	} 
    	
    	return b;
    	
    }
    

    public UID show_UID() throws IOException {
		byte [] b = show_bytes(16);    		
		return new UID(b);
    }
    
    
    
    
    public long read_BER() throws IOException {
    	
    	byte [] lengthBuffer = read_bytes(1);
	
    	if (lengthBuffer[0] >= 0) // top bit set not set
    		return (long) lengthBuffer[0];
	
    	int berTailLength = (int) (lengthBuffer[0] & 0x7f);
    	lengthBuffer = read_bytes(berTailLength);
	
    	
		long lengthValue = 0l;
		for ( int u = 0 ; u < lengthBuffer.length ; u++ )
			lengthValue = (lengthValue << 8) + 
				(((lengthBuffer[u]) >= 0) ? lengthBuffer[u] : 256 + lengthBuffer[u]);
		
		return lengthValue;
    
    	
    }
    /*
    public KLV read_KLV(MXFFile mxfFile) throws IOException {
    	long position = position();	
    	UID key = read_uid();
    	long length = read_BER();
    	byte [] value = read_bytes(length);
    	
    	return new KLV(mxfFile, position, key, length, value);
    	
    }*/
    /*
    public KLVMeta readKLVMeta() throws IOException {    	
    	UInt16 localTag = readUInt16();
    	UInt16 length = readUInt16();
    	byte [] value = readBytes(length.toInt());
    	
    	KLVMeta klv = new KLVMeta(localTag, length, value);
    	klv.parse();
    	return klv;
    }*/
    
    
  /*  public Rational readRational() throws IOException {
    	
    	UInt32 num = readUInt32();
    	UInt32 den = readUInt32();
    	
    	return new Rational(num, den);    	
    }*/
    
    
    public AVRational read_AVRational() throws IOException {
    	UInt32 num = read_UInt32();
    	UInt32 den = read_UInt32();
    	//UInt32 num = read_UInt32();
    	
    	return new AVRational(num.toInt(), den.toInt());    	
    }
    
    public UTF16 read_UTF16() throws IOException {   
    	//TODO
    return new UTF16("implement ByteReader.read_UTF16");
    }
    
    
    public UTF16 read_UTF16(long length) throws IOException {   
    	 String s = "";
    		    
    	 for (int i=0 ; i < length/2 ; i++) {
    		 s = s + (char)read_UInt16().toInt();
    	 }

    	return new UTF16(s);
    }


	public void add_bytes(byte[] buf) {
		byte [] bb = back_buffer.array();
		byte [] new_buf = new byte[bb.length + buf.length];
		System.arraycopy(bb, 0, new_buf, 0, bb.length);
		System.arraycopy(buf, 0, new_buf, bb.length, buf.length);
		back_buffer = ByteBuffer.wrap(new_buf);	
		size += buf.length;
		remaining += buf.length;		
	}
	
	public void add_bytes(short[] buf) {
    	byte [] b = new byte[buf.length];
    	for (int i = 0 ; i < buf.length ; i++)
    		b[i] = (byte) buf[i];
    	add_bytes(b);
	}


	public void av_freep() {
		back_buffer = ByteBuffer.wrap(new byte[0]);
		size = 0;
		remaining = 0;
		pos = 0;
	}



}
