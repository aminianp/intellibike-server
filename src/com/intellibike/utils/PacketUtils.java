package com.intellibike.utils;

import com.intellibike.models.RequestPacket;
import com.intellibike.models.ResponsePacket;

public class PacketUtils {

	public static final class RequestCodes {
		public static final int NULL = 0; // This code is for testing purposes
											// ONLY
		public static final int REGISTER = 100;
		public static final int DEREGISTER = 101;
		public static final int GET_DATA = 200;
		public static final int POST_DATA = 201;
		public static final int DISCONNECT = 300;
	}

	public static final class ResponseCodes {
		public static final int NULL = 0;
		public static final int SUCCESS = 400;
		public static final int FAILURE = 500;
		public static final int ERROR = 600;
	}

	public static ResponsePacket registerNewDevice(RequestPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ResponsePacket postNewData(RequestPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ResponsePacket getDataForDevice(RequestPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ResponsePacket generateErrorPacket(RequestPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

}
