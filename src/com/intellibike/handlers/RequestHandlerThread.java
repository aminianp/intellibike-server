package com.intellibike.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellibike.models.RequestPacket;
import com.intellibike.models.ResponsePacket;
import com.intellibike.utils.Log;
import com.intellibike.utils.PacketUtils;
import com.intellibike.utils.PacketUtils.RequestCodes;

public class RequestHandlerThread extends Thread {

	private static final String TAG = RequestHandlerThread.class.getCanonicalName();

	private Socket socket = null;

	private boolean hasTimedOut;

	public RequestHandlerThread(Socket socket) {
		super(TAG);
		this.socket = socket;
		Log.i(TAG, "Received a new request");
		Log.v(TAG, "Creating a new thread to handle the request");
	}

	public void run() {

		hasTimedOut = false;

		try {
			InputStream inboundTraffic = socket.getInputStream();
			ObjectOutputStream outboundTraffic = new ObjectOutputStream(socket.getOutputStream());

			do {
				ResponsePacket serverResponse = parseRequest(inboundTraffic);
				sendResponse(outboundTraffic, serverResponse);
			} while (!hasTimedOut);
			
			
			Log.i(TAG, "Shutting down connection with client " + socket.getInetAddress());
			inboundTraffic.close();
			outboundTraffic.close();
			Log.v(TAG, "Streams closed");
			socket.close();
			Log.v(TAG, "Socket closed");

		} catch (IOException e) {
				e.printStackTrace();
		}
	}

	private ResponsePacket parseRequest(InputStream inputStream) {
		Gson gson = new Gson();
		InputStreamReader reader = new InputStreamReader(inputStream);
		RequestPacket packet = gson.fromJson(new JsonReader(reader), RequestPacket.class);

		if (packet == null) {
			return PacketUtils.generateErrorPacket(packet);
		}

		switch (packet.getRequestCode()) {
			case RequestCodes.REGISTER:
				return PacketUtils.registerNewDevice(packet);
			case RequestCodes.POST_DATA:
				return PacketUtils.postNewData(packet);
			case RequestCodes.GET_DATA:
				return PacketUtils.getDataForDevice(packet);
			default:
				return PacketUtils.generateErrorPacket(packet);
		}
	}
	
	private void sendResponse(ObjectOutputStream outboundTraffic, ResponsePacket serverResponse) throws IOException {
		Gson gson = new Gson();
		String jsonString = gson.toJson(serverResponse);
		outboundTraffic.writeChars(jsonString);
	}
}
