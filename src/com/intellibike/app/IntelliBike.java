package com.intellibike.app;

import java.util.Scanner;

import com.intellibike.network.IntelliBikeServer;
import com.intellibike.utils.Log;

public class IntelliBike {

	private static final String TAG = IntelliBike.class.getCanonicalName();

	private static final String HOSTNAME = "localhost";
	private static final int PORT = 6969;

	private static final String SHUTDOWN = "shutdown";

	public static void main(String[] args) throws Exception {
		IntelliBikeServer server = new IntelliBikeServer(PORT);
		Log.i(TAG, "Starting server at " + server.getServerAddress() + " on port " + server.getListeningPort());
		server.start();
		Log.v(TAG, "Server Started: Listening for incoming requests...");

		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.print("cmd> ");
			String command = scanner.nextLine();
			if (command != null && command.equals(SHUTDOWN)) {
				running = false;
			} else {
				Log.e(TAG, "Unknown Command: " + command);
			}
		}

		shutdown(server, 0);
	}

	public static void shutdown(IntelliBikeServer server, int code) {
		server.stop();
		System.exit(code);
	}

}
