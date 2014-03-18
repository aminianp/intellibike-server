package com.intellibike.utils;

public class Log {
	
	private static final String BASE_MESSAGE = "%s;\t\t[%s]:\t\t%s%s;";
	
	private static final class Colours {
		private static final String ANSI_RESET = "\u001B[0m";
		private static final String ANSI_RED = "\u001B[31m";
		private static final String ANSI_GREEN = "\u001B[32m";
		private static final String ANSI_BLUE = "\u001B[34m";
	}
	
	public static void i(String tag, String msg) {
		printMsg(Colours.ANSI_GREEN, tag, msg);
	}
	
	public static void d(String tag, String msg) {
		printMsg(Colours.ANSI_BLUE, tag, msg);
	}
	
	public static void e(String tag, String msg) {
		printMsg(Colours.ANSI_RED, tag, msg);
	}
	
	public static void v(String tag, String msg) {
		printMsg(Colours.ANSI_RESET, tag, msg);
	}
	
	private static void printMsg(String colour, String tag, String msg) {
		String formattedMsg = String.format(BASE_MESSAGE, colour, tag, msg, Colours.ANSI_RESET);
		System.out.println(formattedMsg);
	}
}
