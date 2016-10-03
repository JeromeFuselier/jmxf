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

public class ChannelLayout {
	
	String name;
    int    nb_channels;
    long layout;
    

	public ChannelLayout(String name, int nb_channels, long layout) {
		super();
		this.name = name;
		this.nb_channels = nb_channels;
		this.layout = layout;
	}
	
    
	public String get_name() {
		return name;
	}


	public int get_nb_channels() {
		return nb_channels;
	}


	public long get_layout() {
		return layout;
	}

    
    
}
