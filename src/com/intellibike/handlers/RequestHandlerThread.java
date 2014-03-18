package com.intellibike.handlers;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.intellibike.models.EchoPacket;
import com.intellibike.utils.Log;

public class RequestHandlerThread extends Thread {
	
	private static final String TAG = RequestHandlerThread.class.getCanonicalName();
	
	private Socket socket = null;

	public RequestHandlerThread(Socket socket) {
		super(TAG);
		this.socket = socket;
		Log.i(TAG, "Received a new request");
		Log.v(TAG, "Creating a new thread to handle the request");
	}

	public void run() {

		boolean gotByePacket = false;
		
		try {
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			EchoPacket packetFromClient;
			
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
			

			while (( packetFromClient = (EchoPacket) fromClient.readObject()) != null) {
				EchoPacket packetToClient = new EchoPacket();
				
				if(packetFromClient.type == EchoPacket.ECHO_REQUEST) {
					packetToClient.type = EchoPacket.ECHO_REPLY;
					packetToClient.message = packetFromClient.message;
					System.out.println("From Client: " + packetFromClient.message);
				
					toClient.writeObject(packetToClient);
					
					continue;
				}
				
				if (packetFromClient.type == EchoPacket.ECHO_NULL || packetFromClient.type == EchoPacket.ECHO_BYE) {
					gotByePacket = true;
					packetToClient = new EchoPacket();
					packetToClient.type = EchoPacket.ECHO_BYE;
					packetToClient.message = "Bye!";
					toClient.writeObject(packetToClient);
					break;
				}

				Log.e(TAG, "Bad Request: Unknown packet code " + packetFromClient.type);
			}
			
			Log.i(TAG, "Shutting down connection with client " + socket.getInetAddress());
			fromClient.close();
			toClient.close();
			Log.v(TAG, "Streams closed");
			socket.close();
			Log.v(TAG, "Socket closed");

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
	}
}
