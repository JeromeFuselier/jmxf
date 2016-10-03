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
 * Creation   : January 2011
 *  
 *****************************************************************************/

package uk.ac.liv.app.utils;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MXFPlayerFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1941670861201843286L;
	

	 public static final String transcodePngCmd  = "Extract frames";
	 public static final String transcodeWaveCmd = "Extract Audio";
	 public static final String transcodeCmd     = "Transcode";
	 public static final String playCmd          = "Play";
	 public static final String nextCmd          = "Next Frame";
	 public static final String next2Cmd         = "Next Frame 2";
	 public static final String prevCmd          = "Prev Frame";
	 public static final String prev2Cmd         = "Prev Frame 2";
	 public static final String openCmd          = "Open";	 
	 

	MXFDocument mxfdoc;
	
	// Swing
	JMenuItem itemTranscodePNG;
	JMenuItem itemTranscodeWAV;
	JMenuItem itemTranscode;
	
	public MXFPlayerFrame(URI arg) throws HeadlessException {		
		super();
		this.mxfdoc = new MXFDocument(arg);
		
		setTitle(mxfdoc.get_uri().toString());
		init_frame();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public MXFPlayerFrame() throws HeadlessException {
		super();
		mxfdoc = null;
		init_frame();		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	private void init_frame() {		
		init_menu();
		
		if (mxfdoc != null) {	
			init_contentPane();
		} else {
			setSize(640, 480);
		}
		
		setVisible(true);
	}
	

	private void init_contentPane() {
		getContentPane().add(mxfdoc);	
		pack();
		setPreferredSize(new Dimension(getWidth(), getHeight()));	
	}


	private void init_menu() {
		JMenuBar menuBar = new JMenuBar();

		//Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem(MXFPlayerFrame.openCmd);
		menu.add(menuItem);
		menuItem.addActionListener(this);
	
		
		itemTranscodePNG = new JMenuItem(MXFPlayerFrame.transcodePngCmd);
		menu.add(itemTranscodePNG);
	
		itemTranscodeWAV = new JMenuItem(MXFPlayerFrame.transcodeWaveCmd);
		menu.add(itemTranscodeWAV);
		itemTranscode = new JMenuItem(MXFPlayerFrame.transcodeCmd);
		menu.add(itemTranscode);
		
		itemTranscode.addActionListener(this);
		itemTranscodeWAV.addActionListener(this);
		itemTranscodePNG.addActionListener(this);
		
		if (mxfdoc == null) {
			itemTranscode.setEnabled(false);
			itemTranscodeWAV.setEnabled(false);
			itemTranscodePNG.setEnabled(false);
		}
		
		
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		
		setJMenuBar(menuBar);
		
	}
	
	
	public void actionPerformed(ActionEvent e) {
	    if (MXFPlayerFrame.openCmd.equals(e.getActionCommand())) {
	        open();
	    } else if (MXFPlayerFrame.transcodeCmd.equals(e.getActionCommand())) {
			mxfdoc.get_video_state().transcode();	
		} else if (MXFPlayerFrame.transcodePngCmd.equals(e.getActionCommand())) {
			mxfdoc.get_video_state().saveFrame();	
		} else if (MXFPlayerFrame.transcodeWaveCmd.equals(e.getActionCommand())) {
			mxfdoc.get_video_state().saveWAV();
		}
	} 



	private void open() {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new MXFFilter());
		fc.setAcceptAllFileFilterUsed(false);

		
		fc.showOpenDialog(this);
		File selFile = fc.getSelectedFile();
		
		if (selFile != null) {
			this.mxfdoc = new MXFDocument(new File(selFile.getAbsolutePath()).toURI());

			init_contentPane();

			itemTranscodePNG.setEnabled(true);		
			itemTranscodeWAV.setEnabled(true);
			itemTranscode.setEnabled(true);
			
		}
		
	}
	
	

}