package com.intellibike.network;

import java.net.InetAddress;

import com.intellibike.utils.Log;


public class IntelliBikeServer extends NanoHTTPD {

	private static final String TAG = IntelliBikeServer.class.getCanonicalName();

	public IntelliBikeServer(int port) {
		super(port);
	}
	
	public IntelliBikeServer(String hostname, int port) {
		super(hostname, port);
	}

	public InetAddress getServerAddress() {
		return myServerSocket == null ? null : myServerSocket.getInetAddress();
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		super.serve(session);
		Log.i(TAG, "New Request: " + session.getMethod().name() + " uri " + session.getUri());
		
		switch (session.getMethod()) {
			case GET:
				return new Response("Hello!");
			case POST:
				break;

			default:
				break;
		}
		
		return null;
	}

}
