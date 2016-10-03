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

package uk.ac.liv.util;

import uk.ac.liv.ffmpeg.libavfilter.GraphParser;


public class UtilsString {
	
	
	public static String join(Object [] array, String separator) {
        StringBuilder sb = new StringBuilder();
		
		
		for (int i = 0 ; i < array.length ; i++) {
			if (i > 0)
				sb.append(separator);
			sb.append(array[i]);
			
		}
        
        return sb.toString();
	}
	

    public static String repeat(String str, int repeat) {
        StringBuilder sb = new StringBuilder();

		for (int i = 0 ; i < repeat ; i++) {
			sb.append(str);
		}

        return sb.toString();
    }
	
	
	/* Returns the length of the initial portion of str1 which consists only of 
	 * characters that are part of str2.
	 */
	public static int strspn(String str1, String str2) {
		int i = 0;
		boolean instr2 = true;
		int ret = 0;
		
		while ( (i < str1.length()) && instr2 ) {
			instr2 = str2.contains(""+str1.charAt(i)); 		
			if (instr2) ret++;
			i++;
		}
		return ret;
	}    
	
	/* Returns the length of the initial portion of str1 which consists only of 
	 * characters that are part of str2, starting from the end
	 */
	public static int strspn_inv(String str1, String str2) {
		int i = str1.length()-1;
		boolean instr2 = true;
		int ret = 0;
		
		while ( (i >= 0) && instr2 ) {
			instr2 = str2.contains(""+str1.charAt(i)); 		
			if (instr2) ret++;
			i--;
		}
		return ret;
	}    
	
	/* Returns a new string which doesn't start by any character in chars */
	public static String remove_leading_chars(String buf, String chars) {
		return buf.substring(strspn(buf, chars), chars.length());
	}   
	
	/* Returns a new string which doesn't end by any character in chars */
	public static String remove_trailing_chars(String buf, String chars) {
		return buf.substring(0, buf.length() - strspn_inv(buf, GraphParser.WHITESPACES));
	}


	public static String to_string(byte[] bytes) {
		String s = "";
		for (byte b : bytes)
			s += (char)b;
		return s;
	}
	


	public static String strchr(String str, char ch) {
		int idx = str.indexOf(ch);
		
		if (idx == -1)
			return "";
		else
			return str.substring(idx);
	}
	
	
	public static void main(String[] args)  {
		
		/*
		String [] test1 = {"str1", "str2", "str3"};
		String [] test2 = {"str1"};
		String [] test3 = {};
		
		System.out.println(join(test1, " - "));
		System.out.println(join(test2, " - "));
		System.out.println(join(test3, " - "));
		System.out.println(join(test1, "\n"));
		System.out.println(join(test1, ""));
		
		*/
		
		/*
		System.out.println("repeat(\"\", 0) -> " + repeat("", 0));
		System.out.println("repeat(\"*\", 0) -> " + repeat("*", 0));
		System.out.println("repeat(\"*\", 2) -> " + repeat("*", 2));
		System.out.println("repeat(\"*\", -2) -> " + repeat("*", -2));
		*/
		
		/*
		String TEST = "abcdefghijklmnopqrstuvwxyz" +
					  "0123456789+-.";
		
		String test1 = "abcdefghijklmnopqrstuvwxyz";
		String test2 = "abcdeFGHIJNKLMNOPQRSTUVWXYZ";
		String test3 = "ABCDEFGHIJNKLMNOPQRSTUVWXYZ";
		
		System.out.println(strspn(test1, TEST));
		System.out.println(strspn(test2, TEST));
		System.out.println(strspn(test3, TEST));*/
		
		/*byte []  b = { 65, 66, 67, 68, 69 };
		System.out.println(to_string(b));
		System.out.println(to_string(b).endsWith("DE"));*/
		
		/*String filters = "	\t  dsdsdtest";
        filters = filters.substring(UtilsString.strspn(filters, GraphParser.WHITESPACES), filters.length());
		System.out.println(filters);*/
		
		/*String filters = "	\t  dsdsdtest  \t \n   ";
		System.out.println(filters);
        filters = filters.substring(0, filters.length()-UtilsString.strspn_inv(filters, GraphParser.WHITESPACES));
		System.out.println("-" + filters +"-");*/
		
		System.out.println(strchr("tis is a sample string", 's'));
		
		
		
		
		
	}


 
}
