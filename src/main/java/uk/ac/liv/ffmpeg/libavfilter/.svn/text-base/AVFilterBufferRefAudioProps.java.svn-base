package uk.ac.liv.ffmpeg.libavfilter;

public class AVFilterBufferRefAudioProps implements Cloneable {
	
    long channel_layout;     ///< channel layout of audio buffer
    int nb_samples;             ///< number of audio samples per channel
    int sample_rate;       ///< audio buffer sample rate
    int planar;                 ///< audio buffer - planar or packed
    
    public Object clone() {
    	AVFilterBufferRefAudioProps buf = null;
    	try {
			buf = (AVFilterBufferRefAudioProps) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	    
	    return buf;
	}
    
    
	public long get_channel_layout() {
		return channel_layout;
	}
	
	public void set_channel_layout(long channel_layout) {
		this.channel_layout = channel_layout;
	}
	
	public int get_nb_samples() {
		return nb_samples;
	}
	
	public void set_nb_samples(int nb_samples) {
		this.nb_samples = nb_samples;
	}
	
	public int get_sample_rate() {
		return sample_rate;
	}
	
	public void set_sample_rate(int sample_rate) {
		this.sample_rate = sample_rate;
	}
	
	public int get_planar() {
		return planar;
	}
	
	public void set_planar(int planar) {
		this.planar = planar;
	}
    
    

}
