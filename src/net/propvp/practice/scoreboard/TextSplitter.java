package net.propvp.practice.scoreboard;

public class TextSplitter {

	public static String getFirstSplit(String s) {
		if(s.length() > 32) throw new IllegalStateException("The string given cannot be longer than 32 characters.");
		if(s.length() < 16) return s;
		return s.substring(0, 16);
	}
	
	public static String getSecondSplit(String s) {
		if(s.length() > 32) throw new IllegalStateException("The string given cannot be longer than 32 characters.");
		if(s.length() < 16) return "";
		return s.substring(16, (s.length() > 32) ? 32 : s.length());
	}
	
}