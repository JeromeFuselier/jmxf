package uk.ac.liv.util;

import java.util.Arrays;

public class UtilsArrays {
	
	

	public static short[] short_to_byte_be(short[] s) {
		short [] b = new short[s.length*2];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*2] = (byte)(s[i] >> 8);
			b[i*2+1] = (byte)s[i];			
		}
		return b;
	}
	

	public static short[] short_to_byte_le(short[] s) {
		short [] b = new short[s.length*2];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*2] = (byte)s[i];
			b[i*2+1] = (byte)(s[i] >> 8);			
		}
		return b;
	}


	public static short[] byte_to_short_be(short[] b) {
		short [] s = new short[b.length / 2];
		for (int i = 0 ; i < s.length ; i++) {
			s[i] = (short)((b[i*2] << 8) + b[i*2+1]);			
		}
		return s;
	}


	public static short[] byte_to_short_le(short[] b) {
		short [] s = new short[b.length / 2];
		for (int i = 0 ; i < s.length ; i++) {
			s[i] = (short)((b[i*2+1] << 8) + b[i*2]);			
		}
		return s;
	}


	public static long [] byte_to_short_le_sl(short[] b) {
		long [] s = new long[b.length / 2];
		for (int i = 0 ; i < s.length ; i++) {
			s[i] = (short)((b[i*2+1] << 8) + b[i*2]);			
		}
		return s;
	}
	
	
	public static short[][] byte_to_short_be(short[][] b) {
		short [][] s = new short[b.length][];
		
		for (int i = 0 ; i < b.length ; i++) {
			s[i] = new short[b[i].length / 2];
			for (int j = 0 ; j < b[i].length ; j++) {
				s[i][j] = (short)((b[i][j*2+1] << 8) + b[i][j*2]);			
			}			
		}
		
		return s;
	}
	
	public static short[] long_to_byte_be(long[] s) {
		short [] b = new short[s.length*8];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*8] = (byte)(s[i] >> 56);
			b[i*8+1] = (byte)(s[i] >> 48);		
			b[i*8+2] = (byte)(s[i] >> 40);
			b[i*8+3] = (byte)(s[i] >> 32);		
			b[i*8+4] = (byte)(s[i] >> 24);
			b[i*8+5] = (byte)(s[i] >> 16);		
			b[i*8+6] = (byte)(s[i] >> 8);
			b[i*8+7] = (byte)s[i];						
		}
		return b;
	}
	


	public static short[] long_to_short_le(long[] s) {
		short [] b = new short[s.length*4];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*4] = (short)s[i];			
			b[i*4+1] = (short)(s[i] >> 16);	
			b[i*4+2] = (short)(s[i] >> 32);	
			b[i*4+3] = (short)(s[i] >> 64);		
		}
		return b;
	}
	


	public static long[] short_to_long(short[] b) {
		long [] s = new long[b.length / 4];
		for (int i = 0 ; i < s.length ; i++) {
			s[i] = (long)((b[i*2] << 24) + (b[i*2+1] << 16) + (b[i*2+2] << 8) + b[i*2+3]);			
		}
		return s;
	}
	

	public static short[] long_to_byte_le(long[] s) {
		short [] b = new short[s.length*8];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*8] = (byte)s[i];
			b[i*8+1] = (byte)(s[i] >> 8);	
			b[i*8+2] = (byte)(s[i] >> 16);	
			b[i*8+3] = (byte)(s[i] >> 24);	
			b[i*8+4] = (byte)(s[i] >> 32);	
			b[i*8+5] = (byte)(s[i] >> 40);	
			b[i*8+6] = (byte)(s[i] >> 48);	
			b[i*8+7] = (byte)(s[i] >> 56);			
		}
		return b;
	}
	

	public static short[] int_to_byte_be(int[] s) {
		short [] b = new short[s.length*4];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*4] = (byte)(s[i] >> 24);
			b[i*4+1] = (byte)(s[i] >> 16);		
			b[i*4+2] = (byte)(s[i] >> 8);
			b[i*4+3] = (byte)s[i];					
		}
		return b;
	}
	

	public static short[] int_to_byte_le(int[] s) {
		short [] b = new short[s.length*4];
		for (int i = 0 ; i < s.length ; i++) {
			b[i*8] = (byte)s[i];
			b[i*8+1] = (byte)(s[i] >> 8);	
			b[i*8+2] = (byte)(s[i] >> 16);	
			b[i*8+3] = (byte)(s[i] >> 24);	
		}
		return b;
	}

	
	public static void main(String[] args) {
		short [] s = {12, 13, 25};
		
		//long [] l = {1111111111111111111L, 1120833262000L};
		long [] l = {1120833262000L};
		
		/*System.out.println(Arrays.toString(s));
		System.out.println(Arrays.toString(short_to_byte_be(s)));
		System.out.println(Arrays.toString(short_to_byte_le(s)));
		
		System.out.println(Arrays.toString(byte_to_short_be(short_to_byte_be(s))));
		System.out.println(Arrays.toString(byte_to_short_le(short_to_byte_le(s))));*/
		
		System.out.println(Arrays.toString(long_to_short_le(l)));
		
	}


	public static short[] byte_to_short(byte[] b) {
		short [] s = new short[b.length];
		for (int i = 0 ; i < b.length ; i++)
			s[i] = (short) (b[i] & 0xFF);
		return s;
	}


	public static byte[] short_to_byte(short[] s) {
		byte [] b = new byte[s.length];
		for (int i = 0 ; i < s.length ; i++)
			b[i] = (byte) s[i];
		return b;
	}




	
	

}
