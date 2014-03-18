package com.intellibike.models;

public class RequestPacket {

	private int hardware_id;
	private int request_code; 
	private Data[] dataset;
	
	public int getHardwareId() {
		return hardware_id;
	}
	
	public int getRequestCode() {
		return request_code;
	}
	
	public Data[] getData() {
		return dataset;
	}
	
}
