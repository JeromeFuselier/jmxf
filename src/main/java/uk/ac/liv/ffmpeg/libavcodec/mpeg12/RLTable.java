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
 * Manage RM Tables
 */
public class RLTable extends VLCTable {
	

	/* run length table */
	public static int MAX_RUN   = 64;
	public static int MAX_LEVEL = 64;
	
	
    protected int n;
    protected int last;
    
    protected int[][] table_vlc;
    protected int[] table_run;
    protected int[] table_level;

    protected int table_levelLength;
    protected int table_runLength;
    
    protected int[][] index_run;  
    protected int[][] max_level;  
    protected int[][] max_run;        

    public int get_n() {
		return n;
	}

	public void set_n(int n) {
		this.n = n;
	}

	public int get_last() {
		return last;
	}

	public void set_last(int last) {
		this.last = last;
	}

	public int[][] get_table_vlc() {
		return table_vlc;
	}
	
	public void set_table_vlc(int[][] table_vlc) {
		this.table_vlc = new int[table_vlc.length][];

		for(int i = 0; i < this.table_vlc.length; ++i)
			this.table_vlc[i] = Arrays.copyOf(table_vlc[i], table_vlc[i].length); 
	}

	public int[] get_table_run() {
		return table_run;
	}

	public void set_table_run(int[] table_run) {
		this.table_run = Arrays.copyOf(table_run, table_run.length);
	}
	
	public int[] get_table_level() {
		return table_level;
	}

	public void set_table_level(int[] table_level) {
		this.table_level = Arrays.copyOf(table_level, table_level.length);
	}

	public int[][] get_index_run() {
		return index_run;
	}

	public void set_index_run(int[][] index_run) {
		this.index_run = new int[index_run.length][];

		for(int i = 0; i < this.table_vlc.length; ++i)
			this.index_run[i] = Arrays.copyOf(index_run[i], index_run[i].length); 
	}
	
	public int[][] get_max_level() {
		return max_level;
	}

	public void set_max_level(int[][] max_level) {
		this.max_level = new int[max_level.length][];

		for(int i = 0; i < this.max_level.length; ++i)
			this.max_level[i] = Arrays.copyOf(max_level[i], max_level[i].length);
	}

	public int[][] get_max_run() {
		return max_run;
	}
	
	public void set_max_run(int[][] max_run) {
		this.max_run = new int[max_run.length][];

		for(int i = 0; i < this.max_run.length; ++i)
			this.max_run[i] = Arrays.copyOf(max_run[i], max_run[i].length);
	}




	protected void calculateStats() {
        /* compute max_level[], max_run[] and index_run[] */
        max_level = new int[2][n];
        index_run = new int[2][n];
        max_run   = new int[2][n];

        for( int c=0; c<2; c++) {
            int start;
            int end;
            if ( c == 0 ) {
                start = 0;
                end   = last;
            } else {
                start = last;
                end   = n;
            }
            
            for( int i = start; i < end; i++ ) {
                int run   = table_run[i];
                int level = table_level[i];
                if ( index_run[c][run] == n    ) index_run[c][run] = i;
                if ( level > max_level[c][run] ) max_level[c][run] = level;
                if ( run   > max_run[c][level] ) max_run[c][level] = run;
            }
        }
        
        /**
         * Manage table level and run look up
         */ 
        table_levelLength = table_level.length;
        table_runLength = table_run.length;
        
        int[] oldTable = table_level;
        table_level = new int[ table_levelLength * 2 ];
        for ( int i = 0; i < table_level.length; i++ ) {
            table_level[ i ] = ( i < table_levelLength ) ? oldTable[ i ] : 0;
        }
         
        oldTable = table_run;
        table_run = new int[ table_runLength * 2 ];
        for ( int i = 0; i < table_run.length; i++ ) {
            if ( i >= table_runLength ) {
                table_run[ i ] = 65;
            } else {
                table_run[ i ] = (i >= last ) ? oldTable[ i ] + 193 : oldTable[ i ] + 1;
            }
        }   
        
        createHighSpeedTable();
    }
}
