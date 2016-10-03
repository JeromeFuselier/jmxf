package uk.ac.liv.ffmpeg.libavutil;

public class Cpu {
	
	public static int flags;
	public static boolean checked;
	

	public static int av_get_cpu_flags() {
	    if (checked)
	        return flags;

	   //if (ARCH_ARM) flags = ff_get_cpu_flags_arm();
	    //if (ARCH_PPC) flags = ff_get_cpu_flags_ppc();
	    //if (ARCH_X86) flags = ff_get_cpu_flags_x86();

	    checked = true;
	    return flags;

	}

}
