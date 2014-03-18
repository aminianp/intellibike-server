package com.intellibike.app;

import java.net.ServerSocket;

import com.intellibike.handlers.RequestHandlerThread;
import com.intellibike.utils.Log;

public class IntelliBike {
	
	private static final String TAG = IntelliBike.class.getCanonicalName();
	
	public static final int PORT_NUMBER = 4000;
	
	public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = null;
        boolean listening = true;

        serverSocket = new ServerSocket(PORT_NUMBER);
        Log.i(TAG, "Created Server Socket: Server is now ready to accept connectings");
        Log.v(TAG, "Server running on " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        
        while (listening) {
        	Log.v(TAG, "Listening for connections...");
        	new RequestHandlerThread(serverSocket.accept()).start();
        }

        serverSocket.close();
	}
}
