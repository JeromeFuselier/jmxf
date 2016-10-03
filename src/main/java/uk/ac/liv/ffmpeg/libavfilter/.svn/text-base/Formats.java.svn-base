package uk.ac.liv.ffmpeg.libavfilter;

import java.util.ArrayList;

import uk.ac.liv.ffmpeg.libavutil.AVUtil.AVMediaType;
import uk.ac.liv.ffmpeg.libavutil.AudioConvert;
import uk.ac.liv.ffmpeg.libavutil.PixDesc;
import uk.ac.liv.ffmpeg.libavutil.PixFmt;
import uk.ac.liv.ffmpeg.libavutil.PixFmt.PixelFormat;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt;
import uk.ac.liv.ffmpeg.libavutil.SampleFmt.AVSampleFormat;

public class Formats {

	public static AVFilterFormats avfilter_make_format_list(ArrayList<Long> fmts) {
        AVFilterFormats formats = new AVFilterFormats();
        
        for (Long fmt : fmts)
        	formats.avfilter_add_format(fmt);        
        
		return formats;
	}


	public static AVFilterFormats avfilter_all_formats(AVMediaType type) {
	    AVFilterFormats ret = new AVFilterFormats();
	    int fmt;
	    
	    if (type == AVMediaType.AVMEDIA_TYPE_VIDEO) {
	    	for (PixelFormat pix_fmt : PixelFormat.values()) {
	    		if ( (PixDesc.av_pix_fmt_descriptors.get(pix_fmt).get_flags() & PixDesc.PIX_FMT_HWACCEL) == 0)
		    		ret.avfilter_add_format(pix_fmt);
	    	}
	    	
	    } else if (type == AVMediaType.AVMEDIA_TYPE_AUDIO) {
	    	for (AVSampleFormat smp_fmt : AVSampleFormat.values()) {
	    		ret.avfilter_add_format(smp_fmt);
	    	}
	    } 

	    return ret;
	}

	public static AVFilterFormats avfilter_all_channel_layouts() {
		ArrayList<Long> chlayouts = new ArrayList<Long>();
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_MONO));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_STEREO));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_4POINT0));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_QUAD));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_5POINT0));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_5POINT0_BACK));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_5POINT1));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_5POINT1_BACK));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_5POINT1|AudioConvert.AV_CH_LAYOUT_STEREO_DOWNMIX));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_7POINT1));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_7POINT1_WIDE));
		chlayouts.add(new Long(AudioConvert.AV_CH_LAYOUT_7POINT1|AudioConvert.AV_CH_LAYOUT_STEREO_DOWNMIX));

	    return avfilter_make_format_list(chlayouts);
	}


}
