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
 * Creation   : December 2011
 *  
 *****************************************************************************/

package uk.ac.liv.app.utils;


import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;


public class ImagePanel extends JPanel {
	
	private static final long serialVersionUID = 379069972437639557L;
	
	BufferedImage  image;
	
	public ImagePanel() {
		image = null;
	}
	
	public ImagePanel(BufferedImage img) {
		setImage(img);
	}
	

	public void setImage(BufferedImage image) {
		this.image = image;
		setSize(image.getWidth(), image.getHeight());
		setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		repaint();
	}
	
	
	public void paintComponent(Graphics g) {
		if (image != null) {
			super.paintComponent(g);
			g.drawImage( image, 0, 0, null);
		}
	}

}
