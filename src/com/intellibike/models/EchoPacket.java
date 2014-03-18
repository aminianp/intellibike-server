package com.intellibike.models;
import java.io.Serializable;

public class EchoPacket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int ECHO_NULL    = 0;
	public static final int ECHO_REQUEST = 100;
	public static final int ECHO_REPLY   = 200;
	public static final int ECHO_BYE     = 300;
	
	public int type = ECHO_NULL;
	
	public String message;
	
}
