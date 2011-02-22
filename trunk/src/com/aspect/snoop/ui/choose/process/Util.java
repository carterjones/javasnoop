package com.aspect.snoop.ui.choose.process;

import com.aspect.snoop.ui.JavaSnoopView;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Util {

	/**
	 * Replaces the occurrences of a certain pattern in a string with a
	 * replacement String. This is the fastest replace function.
	 *
	 * @param s      the string to be inspected
	 * @param sub    the string pattern to be replaced
	 * @param with   the string that should go where the pattern was
	 *
	 * @return the string with the replacements done
	 */
	public static String stringReplace(String s, String sub, String with) {
		if ((s == null) || (sub == null) || (with == null)) {
			return s;
		}
		int c = 0;
		int i = s.indexOf(sub, c);
		if (i == -1) {
			return s;
		}
		StringBuffer buf = new StringBuffer(s.length() + with.length());
		do {
			 buf.append(s.substring(c,i));
			 buf.append(with);
			 c = i + sub.length();
		 } while ((i = s.indexOf(sub, c)) != -1);
		 if (c < s.length()) {
			 buf.append(s.substring(c,s.length()));
		 }
		 return buf.toString();
	}


	/**
	 * Character replacement in a string. All occurrencies of a character will be
	 * replaces.
	 *
	 * @param s      input string
	 * @param sub    character to replace
	 * @param with   character to replace with
	 *
	 * @return string with replaced characters
	 */
	public static String stringReplaceChar(String s, char sub, char with) {
		if (s == null) {
			return s;
		}
		char[] str = s.toCharArray();
		for (int i = 0; i < str.length; i++) {
			if (str[i] == sub) {
				str[i] = with;
			}
		}
		return new String(str);
	}
	
	public static String convertListToString(List<String> list, String delimiter) 
	{
		String result;
		
		if (list != null) 
		{
			StringBuffer resultBuffer = new StringBuffer();
			Iterator<String> i = list.iterator();
			while (i.hasNext())
			{
				resultBuffer.append(i.next());
				if (i.hasNext())
					resultBuffer.append(delimiter);
			}

			result = resultBuffer.toString();
		}
		else 
		{
			result = "";
		}
		
		return result;
	}
	
	public static List<String> convertStringToList(String value, String delimiter) 
	{
		List<String> list = null;
		
		if (value != null) 
		{
			list = Arrays.asList(value.split(","));
		}
		
		return list;
	}

	/**
	 * Returns icon image from the resource.
	 */
	public static ImageIcon createImageIcon(String iconResourcePath) {
		return new ImageIcon(JavaSnoopView.class.getResource(iconResourcePath));
	}
}
