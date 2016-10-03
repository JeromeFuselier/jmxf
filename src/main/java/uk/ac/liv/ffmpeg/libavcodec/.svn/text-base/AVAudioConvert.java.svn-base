package uk.ac.liv.ffmpeg.libavcodec;

public class AVAudioConvert {

    int in_channels, out_channels;
    int fmt_pair;
    
	public int get_in_channels() {
		return in_channels;
	}
	
	public void set_in_channels(int in_channels) {
		this.in_channels = in_channels;
	}
	
	public int get_out_channels() {
		return out_channels;
	}
	
	public void set_out_channels(int out_channels) {
		this.out_channels = out_channels;
	}
	
	public int get_fmt_pair() {
		return fmt_pair;
	}
	
	public void set_fmt_pair(int fmt_pair) {
		this.fmt_pair = fmt_pair;
	}

	public void av_audio_convert_free() {
		
	}

	public int av_audio_convert(short[][] out, int[] out_stride, short[][] in,
			int[] in_stride, int len) {
		int ch;

	    //FIXME optimize common cases
/*
	    for (ch = 0 ; ch < get_out_channels() ; ch++){
	        int is = in_stride[ch];
	        int os = out_stride[ch];
	        short [] pi = in[ch];
	        short [] po = out[ch];
	        
	        if 

	        	 CONV(SampleFmt.AV_SAMPLE_FMT_U8 , uint8_t, SampleFmt.AV_SAMPLE_FMT_U8 ,  *(const uint8_t*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S16, int16_t, SampleFmt.AV_SAMPLE_FMT_U8 , (*(const uint8_t*)pi - 0x80)<<8)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S32, int32_t, SampleFmt.AV_SAMPLE_FMT_U8 , (*(const uint8_t*)pi - 0x80)<<24)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_FLT, float  , SampleFmt.AV_SAMPLE_FMT_U8 , (*(const uint8_t*)pi - 0x80)*(1.0 / (1<<7)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_DBL, double , SampleFmt.AV_SAMPLE_FMT_U8 , (*(const uint8_t*)pi - 0x80)*(1.0 / (1<<7)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_U8 , uint8_t, SampleFmt.AV_SAMPLE_FMT_S16, (*(const int16_t*)pi>>8) + 0x80)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S16, int16_t, SampleFmt.AV_SAMPLE_FMT_S16,  *(const int16_t*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S32, int32_t, SampleFmt.AV_SAMPLE_FMT_S16,  *(const int16_t*)pi<<16)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_FLT, float  , SampleFmt.AV_SAMPLE_FMT_S16,  *(const int16_t*)pi*(1.0 / (1<<15)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_DBL, double , SampleFmt.AV_SAMPLE_FMT_S16,  *(const int16_t*)pi*(1.0 / (1<<15)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_U8 , uint8_t, SampleFmt.AV_SAMPLE_FMT_S32, (*(const int32_t*)pi>>24) + 0x80)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S16, int16_t, SampleFmt.AV_SAMPLE_FMT_S32,  *(const int32_t*)pi>>16)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S32, int32_t, SampleFmt.AV_SAMPLE_FMT_S32,  *(const int32_t*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_FLT, float  , SampleFmt.AV_SAMPLE_FMT_S32,  *(const int32_t*)pi*(1.0 / (1U<<31)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_DBL, double , SampleFmt.AV_SAMPLE_FMT_S32,  *(const int32_t*)pi*(1.0 / (1U<<31)))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_U8 , uint8_t, SampleFmt.AV_SAMPLE_FMT_FLT, av_clip_uint8(  lrintf(*(const float*)pi * (1<<7)) + 0x80))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S16, int16_t, SampleFmt.AV_SAMPLE_FMT_FLT, av_clip_int16(  lrintf(*(const float*)pi * (1<<15))))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S32, int32_t, SampleFmt.AV_SAMPLE_FMT_FLT, av_clipl_int32(llrintf(*(const float*)pi * (1U<<31))))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_FLT, float  , SampleFmt.AV_SAMPLE_FMT_FLT, *(const float*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_DBL, double , SampleFmt.AV_SAMPLE_FMT_FLT, *(const float*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_U8 , uint8_t, SampleFmt.AV_SAMPLE_FMT_DBL, av_clip_uint8(  lrint(*(const double*)pi * (1<<7)) + 0x80))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S16, int16_t, SampleFmt.AV_SAMPLE_FMT_DBL, av_clip_int16(  lrint(*(const double*)pi * (1<<15))))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_S32, int32_t, SampleFmt.AV_SAMPLE_FMT_DBL, av_clipl_int32(llrint(*(const double*)pi * (1U<<31))))
	        else CONV(SampleFmt.AV_SAMPLE_FMT_FLT, float  , SampleFmt.AV_SAMPLE_FMT_DBL, *(const double*)pi)
	        else CONV(SampleFmt.AV_SAMPLE_FMT_DBL, double , SampleFmt.AV_SAMPLE_FMT_DBL, *(const double*)pi)
	        else return -1;
	    }
	    return 0;*/
		return -1;
	}
    
    
}
