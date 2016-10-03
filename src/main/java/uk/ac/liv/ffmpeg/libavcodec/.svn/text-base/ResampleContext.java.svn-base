package uk.ac.liv.ffmpeg.libavcodec;

import java.util.Arrays;

import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;
import uk.ac.liv.util.OutOI;

public class ResampleContext {

	public static int MAX_CHANNELS = 8;
	
	
	AVResampleContext resample_context;
    short [] temp = new short[MAX_CHANNELS];
    int temp_len;
    float ratio;
    /* channel convert */
    int input_channels, output_channels, filter_channels;
    AVAudioConvert [] convert_ctx = new AVAudioConvert[2];
    AVSampleFormat [] sample_fmt = new AVSampleFormat[2]; ///< input and output sample format
    int [] sample_size = new int[2];           ///< size of one sample in sample_fmt
    short [][] buffer = new short[2][];                  ///< buffers used for conversion to S16
    int [] buffer_size = new int[2];           ///< sizes of allocated buffers
    

	public AVResampleContext get_resample_context() {
		return resample_context;
	}

	public void set_resample_context(AVResampleContext resample_context) {
		this.resample_context = resample_context;
	}
	
	public short[] get_temp() {
		return temp;
	}
	
	public void set_temp(short[] temp) {
		this.temp = temp;
	}
	
	public int get_temp_len() {
		return temp_len;
	}
	
	public void set_temp_len(int temp_len) {
		this.temp_len = temp_len;
	}
	
	public float get_ratio() {
		return ratio;
	}
	
	public void set_ratio(float ratio) {
		this.ratio = ratio;
	}
	
	public int get_input_channels() {
		return input_channels;
	}
	
	public void set_input_channels(int input_channels) {
		this.input_channels = input_channels;
	}
	
	public int get_output_channels() {
		return output_channels;
	}
	
	public void set_output_channels(int output_channels) {
		this.output_channels = output_channels;
	}
	
	public int get_filter_channels() {
		return filter_channels;
	}
	
	public void set_filter_channels(int filter_channels) {
		this.filter_channels = filter_channels;
	}
	
	public AVAudioConvert[] get_convert_ctx() {
		return convert_ctx;
	}
	
	private AVAudioConvert get_convert_ctx(int i) {
		return convert_ctx[i];
	}
	
	public void set_convert_ctx(AVAudioConvert[] convert_ctx) {
		this.convert_ctx = convert_ctx;
	}
	
	public AVSampleFormat[] get_sample_fmt() {
		return sample_fmt;
	}
	
	public void set_sample_fmt(AVSampleFormat[] sample_fmt) {
		this.sample_fmt = sample_fmt;
	}
	
	public int[] get_sample_size() {
		return sample_size;
	}
	
	public void set_sample_size(int[] sample_size) {
		this.sample_size = sample_size;
	}
	
	public short[][] get_buffer() {
		return buffer;
	}
	
	public void set_buffer(short[][] buffer) {
		this.buffer = new short[buffer.length][];
		for (int i = 0 ; i < buffer[i].length ; i++) 
			this.buffer[i] = Arrays.copyOf(buffer[i], buffer[i].length);
	}
	
	public int[] get_buffer_size() {
		return buffer_size;
	}
	
	public int get_buffer_size(int i) {
		return buffer_size[i];
	}
	
	public void set_buffer_size(int[] buffer_size) {
		this.buffer_size = buffer_size;
	}

	public void audio_resample_close() {
	    int i;
	    get_resample_context().av_resample_close();
	    convert_ctx[0].av_audio_convert_free();
	    convert_ctx[1].av_audio_convert_free();		
	}

	public static ResampleContext av_audio_resample_init(int output_channels,
			int input_channels, int output_rate, int input_rate,
			AVSampleFormat sample_fmt_out, AVSampleFormat sample_fmt_in,
			int filter_length, int log2_phase_count, int linear, double cutoff) {
		// TODO Auto-generated method stub
		return null;
	}

	public OutOI audio_resample(short[] input, int nb_samples) {
		short[] output;
	    int i, nb_samples1;
	    short [] bufin = new short[MAX_CHANNELS];
	    short [] bufout = new short[MAX_CHANNELS];
	    short [] buftmp2 = new short[MAX_CHANNELS];
	    short [] buftmp3 = new short[MAX_CHANNELS];
	    short [] output_bak = null;
	    int lenout;

	    if ( (get_input_channels() == get_output_channels()) && 
	    	 (get_ratio() == 1.0) && 
	    	 (0 != 0) /* ???*/ ) {
	        /* nothing to do */
	        output = Arrays.copyOf(input, input.length);
	        return new OutOI(output, nb_samples);
	    }
	    return new OutOI(null, -1);
	  /*  if (get_sample_fmt(0) != AVSampleFormat.AV_SAMPLE_FMT_S16) {
	        int [] istride = { get_sample_size(0) };
	        int [] ostride = { 2 };
	        short [][] ibuf = { input };
	        short [][] obuf = new short[1][];
	        int input_size = nb_samples * get_input_channels() * 2;

	        if ( (get_buffer_size(0) == 0) ||
	        	 (get_buffer_size(0) < input_size) ) {
	            set_buffer_size(0, input_size);
	            set_buffer(0, new short[get_buffer_size(0)/2]);
	        }

	        obuf[0] = Arrays.copyOf(get_buffer(0), get_buffer(0).length);

	        if (get_convert_ctx(0).av_audio_convert(obuf, ostride,
	                             ibuf, istride, nb_samples * get_input_channels()) < 0) {
	            av_log(s->resample_context, AV_LOG_ERROR,
	                   "Audio sample format conversion failed\n");
	            return 0;
	        }

	        input = s->buffer[0];
	    }

	    lenout= 2*s->output_channels*nb_samples * s->ratio + 16;

	    if (s->sample_fmt[1] != AV_SAMPLE_FMT_S16) {
	        output_bak = output;

	        if (!s->buffer_size[1] || s->buffer_size[1] < lenout) {
	            av_free(s->buffer[1]);
	            s->buffer_size[1] = lenout;
	            s->buffer[1] = av_malloc(s->buffer_size[1]);
	            if (!s->buffer[1]) {
	                av_log(s->resample_context, AV_LOG_ERROR, "Could not allocate buffer\n");
	                return 0;
	            }
	        }

	        output = s->buffer[1];
	    }*/

	    /* XXX: move those malloc to resample init code */
	  /*  for (i = 0; i < s->filter_channels; i++) {
	        bufin[i] = av_malloc((nb_samples + s->temp_len) * sizeof(short));
	        memcpy(bufin[i], s->temp[i], s->temp_len * sizeof(short));
	        buftmp2[i] = bufin[i] + s->temp_len;
	        bufout[i] = av_malloc(lenout * sizeof(short));
	    }

	    if (s->input_channels == 2 && s->output_channels == 1) {
	        buftmp3[0] = output;
	        stereo_to_mono(buftmp2[0], input, nb_samples);
	    } else if (s->output_channels >= 2 && s->input_channels == 1) {
	        buftmp3[0] = bufout[0];
	        memcpy(buftmp2[0], input, nb_samples * sizeof(short));
	    } else if (s->input_channels == 6 && s->output_channels ==2) {
	        buftmp3[0] = bufout[0];
	        buftmp3[1] = bufout[1];
	        surround_to_stereo(buftmp2, input, s->input_channels, nb_samples);
	    } else if (s->output_channels >= s->input_channels && s->input_channels >= 2) {
	        for (i = 0; i < s->input_channels; i++) {
	            buftmp3[i] = bufout[i];
	        }
	        deinterleave(buftmp2, input, s->input_channels, nb_samples);
	    } else {
	        buftmp3[0] = output;
	        memcpy(buftmp2[0], input, nb_samples * sizeof(short));
	    }

	    nb_samples += s->temp_len;*/

	    /* resample each channel */
	 /*   nb_samples1 = 0; /* avoid warning */
	  /*  for (i = 0; i < s->filter_channels; i++) {
	        int consumed;
	        int is_last = i + 1 == s->filter_channels;

	        nb_samples1 = av_resample(s->resample_context, buftmp3[i], bufin[i],
	                                  &consumed, nb_samples, lenout, is_last);
	        s->temp_len = nb_samples - consumed;
	        s->temp[i] = av_realloc(s->temp[i], s->temp_len * sizeof(short));
	        memcpy(s->temp[i], bufin[i] + consumed, s->temp_len * sizeof(short));
	    }

	    if (s->output_channels == 2 && s->input_channels == 1) {
	        mono_to_stereo(output, buftmp3[0], nb_samples1);
	    } else if (s->output_channels == 6 && s->input_channels == 2) {
	        ac3_5p1_mux(output, buftmp3[0], buftmp3[1], nb_samples1);
	    } else if ((s->output_channels == s->input_channels && s->input_channels >= 2) ||
	               (s->output_channels == 2 && s->input_channels == 6)) {
	        interleave(output, buftmp3, s->output_channels, nb_samples1);
	    }

	    if (s->sample_fmt[1] != AV_SAMPLE_FMT_S16) {
	        int istride[1] = { 2 };
	        int ostride[1] = { s->sample_size[1] };
	        const void *ibuf[1] = { output };
	        void       *obuf[1] = { output_bak };

	        if (av_audio_convert(s->convert_ctx[1], obuf, ostride,
	                             ibuf, istride, nb_samples1 * s->output_channels) < 0) {
	            av_log(s->resample_context, AV_LOG_ERROR,
	                   "Audio sample format convertion failed\n");
	            return 0;
	        }
	    }

	    for (i = 0; i < s->filter_channels; i++) {
	        av_free(bufin[i]);
	        av_free(bufout[i]);
	    }

	    return nb_samples1;*/
	}


	private short[] get_buffer(int i) {
		return this.buffer[i];
	}

	private void set_buffer_size(int i, int j) {
		this.buffer_size[i] = j;		
	}

	private void set_buffer(int i, short[] j) {
		this.buffer[i] = Arrays.copyOf(j, j.length);
		
	}

	private int get_sample_size(int i) {
		return sample_size[i];
	}

	public AVSampleFormat get_sample_fmt(int i) {
		return sample_fmt[i];
	}

    
    
    
}
