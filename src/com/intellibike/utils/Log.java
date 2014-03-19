package com.intellibike.utils;

public class Log {
	
	private static final String BASE_MESSAGE = "[%s][class: %s]:%s";
	
	private static final class Types {
		private static final String ERROR = "ERROR";
		private static final String EVENT = "EVENT";
		private static final String VERBOSE = "VERBOSE";
	}
	
	public static void i(String tag, String msg) {
		printMsg(Types.EVENT, tag, msg);
	}
	
	public static void e(String tag, String msg) {
		printMsg(Types.ERROR, tag, msg);
	}
	
	public static void v(String tag, String msg) {
		printMsg(Types.VERBOSE, tag, msg);
	}
	
	private static void printMsg(String colour, String tag, String msg) {
		String formattedMsg = String.format(BASE_MESSAGE, colour, tag, msg);
		System.out.println(formattedMsg);
	}
}
