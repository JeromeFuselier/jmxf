/*
 * Java port of ffmpeg mpeg1/2 decoder.
 * Copyright (c) 2003 Jonathan Hueber.
 *
 * Copyright (c) 2000,2001 Fabrice Bellard.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * See Credits file and Readme for details
 */

package uk.ac.liv.ffmpeg.libavcodec.mpeg12;

import java.util.Arrays;



/**
 *
 */
public class ScanTable {
    private static final int[] dsp_idct_permutation = Tables.getDspIdctPermutation();
    
    protected byte[] scantable;
    protected int[] permutated = new int[ 64 ];
    protected int[] raster_end = new int[ 64 ];
    
    public int[] get_permutated() {
        return permutated;
    }
    
    public int get_permutated(int i) {
        return permutated[i];
    }
    
    public int[] get_raster_end() {
        return raster_end;
    }
    
    public byte [] get_scantable() {
        return scantable;
    }

	public void set_scantable(byte [] scantable) {
		this.scantable = Arrays.copyOf(scantable, scantable.length);		
	}

	
    protected void createScanTable() {
        int i;
        int end;

        for(i=0; i<64; i++){
            int j = scantable[i];
            permutated[ i ] = dsp_idct_permutation[j];
        }

        end=-1;
        for(i=0; i<64; i++){
            int j = permutated[i];
            if(j>end) end=j;
            raster_end[i]= end;
        }
    }

}
