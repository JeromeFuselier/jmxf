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
 * Creation   : September 2011
 *  
 *****************************************************************************/

package uk.ac.liv.app.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import uk.ac.liv.app.MXFPlayer;
import uk.ac.liv.ffmpeg.CmdUtils;
import uk.ac.liv.ffmpeg.libavcodec.AVCodec;
import uk.ac.liv.ffmpeg.libavfilter.AVFilter;
import uk.ac.liv.ffmpeg.libavformat.AVFormat;
import uk.ac.liv.ffmpeg.libavformat.mxf.ByteWriter;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;


public class MXFDocument extends JPanel implements ActionListener {

	private static final long serialVersionUID = 4331776545327139861L;
	 
	 JButton but_play;
	 ImageIcon img_play;
	 ImageIcon img_pause;	    
	 	 	
	VideoState	is;
    
	ImagePanel video;
    ImagePanel audio;
    
    URI uri;
    

	 Thread thrPlay;
    

	public MXFDocument() {
		this.uri = null;
	}
    
    public MXFDocument(URI uri) {
    	this.uri = uri;		

		// Register Codecs and Formats
	    AVCodec.avcodec_register_all();   
	    AVFilter.avfilter_register_all();
		AVFormat.av_register_all();
		
		CmdUtils.init_opts();
		
		is = new VideoState(uri);
		
		is.initialize();
		
		
		initUI();
	}


	public URI get_uri() {
		return uri;
	}	
	

	public VideoState get_video_state() {
		return this.is;
	}
	

	private void initUI() {
		setBackground(Color.black);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel infos = new JLabel(is.get_title());
		infos.setFont(new Font("Arial", Font.PLAIN, 16));
		infos.setForeground(Color.yellow);
		
		add(infos);
		

        for (String s: is.get_metadata()) {
        	JLabel metas = new JLabel(s);
        	metas.setFont(new Font("Arial", Font.PLAIN, 16));
        	metas.setForeground(new Color(192, 192, 255));
    		add(metas);
        	
        }        
        
        
        video = new ImagePanel(is.getFirstFrame());
        video.setAlignmentX(LEFT_ALIGNMENT);
        add(video);
        

        audio = new ImagePanel(is.get_wave_im().getImage(0.0f, is.timestamp_to_second(is.get_duration())));
        audio.setAlignmentX(LEFT_ALIGNMENT);
        add(audio);

		JToolBar toolbar = new JToolBar();
		toolbar.setAlignmentX(LEFT_ALIGNMENT);
		add(toolbar);
		
	    ImageIcon img_prev2 = new ImageIcon(MXFPlayer.class.getResource("/icons/prev2.png"));
	    JButton but_prev2 = new JButton(img_prev2);
	    but_prev2.setActionCommand(MXFPlayerFrame.prev2Cmd);
		toolbar.add(but_prev2);
						
	    ImageIcon img_prev = new ImageIcon(MXFPlayer.class.getResource("/icons/prev.png"));
	    JButton but_prev = new JButton(img_prev);
	    but_prev.setActionCommand(MXFPlayerFrame.prevCmd);
		toolbar.add(but_prev);
		
	    img_play = new ImageIcon(MXFPlayer.class.getResource("/icons/play.png"));
	    img_pause = new ImageIcon(MXFPlayer.class.getResource("/icons/pause.png"));
	    but_play = new JButton(img_play);   
	    but_play.setActionCommand(MXFPlayerFrame.playCmd);
		toolbar.add(but_play);
		
	    ImageIcon img_next = new ImageIcon(MXFPlayer.class.getResource("/icons/next.png"));
	    JButton but_next = new JButton(img_next);
	    but_next.setActionCommand(MXFPlayerFrame.nextCmd);
		toolbar.add(but_next);
		
	    ImageIcon img_next2 = new ImageIcon(MXFPlayer.class.getResource("/icons/next2.png"));
	    JButton but_next2 = new JButton(img_next2);
	    but_next2.setActionCommand(MXFPlayerFrame.next2Cmd);
		toolbar.add(but_next2);
		

		but_prev2.addActionListener(this);
		but_prev.addActionListener(this);
		but_play.addActionListener(this);
		but_next.addActionListener(this);
		but_next2.addActionListener(this);

	}
	
	
	public void actionPerformed(ActionEvent e) {
	    if (MXFPlayerFrame.playCmd.equals(e.getActionCommand())) {
	        playpause();
	    } else if (MXFPlayerFrame.nextCmd.equals(e.getActionCommand())) {
	        next_frame();
	    } else if (MXFPlayerFrame.next2Cmd.equals(e.getActionCommand())) {
	    	next_frame2();
	    } else if (MXFPlayerFrame.prevCmd.equals(e.getActionCommand())) {
	    	previous_frame();
	    } else if (MXFPlayerFrame.prev2Cmd.equals(e.getActionCommand())) {
	    	previous_frame2();
	    }
	}
	

    public void playpause() {
    	if (is.is_playing()) {
    		is.setIsPlaying(false);
    		but_play.setIcon(img_play);
    	} else {
    		is.setIsPlaying(true);
    		but_play.setIcon(img_pause);
			Runnable r = new Runnable() {
				public void run() {				
					while (is.is_playing()) {
			    		is.decode_next_frame();
			    		update_frame();
					}
				}
			};
			
			thrPlay = new Thread(r);
			thrPlay.start();
    	}
    	
    }
		

	private void update_frame() {		
		BufferedImage frame = is.get_current_frame();
		if (frame != null) {
	    	video.setImage(frame);
	    	video.repaint();
		} else {
			if (is.is_playing())
				playpause();
		}
		
		BufferedImage wave = is.get_current_wave(); 
    	if (wave != null) {
	    	audio.setImage(wave);
	    	audio.repaint();
    	}
	}


	   

	 public void seek(int frame) {	
	   	if (is.is_playing())
	   		playpause();	
	   	
	   	is.seek_delta(frame);
		update_frame();
	 }


	// Fast forward (more than 1 frame in the future)
    public void next_frame2() {    
    	seek(is.get_nb_frames() * 10 / 100);
    }
    
    
    // Fast backward (more than 1 frame in the past)
    public void previous_frame2() {   	
    	seek(-is.get_nb_frames() * 10 / 100);
    }

    
    public void next_frame() {  
    	seek(1);
    }
    
    
    public void previous_frame() {  	
    	seek(-2);
    }
	


	

}
