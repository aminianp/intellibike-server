package com.intellibike.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.intellibike.app.IntelliBike;
import com.intellibike.models.Column;
import com.intellibike.models.Column.Types;
import com.intellibike.models.Data;
import com.intellibike.models.PostBody;
import com.intellibike.sql.DatabaseManager;
import com.intellibike.utils.Log;

public class IntelliBikeServer extends NanoHTTPD {

	private static final String TAG = IntelliBikeServer.class.getCanonicalName();

	private static final String TABLE_NAME = "data";

	private static enum DATA_COLUMNS {
		DEVICE_ID, LATITUDE, LONGITUDE, RECORDED_TIME
	};

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
	public void start() throws IOException {
		super.start();

		try {
			DatabaseManager.getInstance().connect();
		} catch (SQLException e) {
			Log.e(TAG, "FATAL ERROR: Could not connect to the database");
			e.printStackTrace();
			IntelliBike.shutdown(this, -1);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "FATAL ERROR: Could not find JDBC database driver");
			e.printStackTrace();
			IntelliBike.shutdown(this, -1);
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		super.serve(session);
		Log.i(TAG, "New Request: " + session.getMethod().name() + " uri " + session.getUri());

		switch (session.getMethod()) {
			case GET:
				try {
					FileReader fileReader = new FileReader("./assets/test-get.txt");
					BufferedReader lineReader = new BufferedReader(fileReader);
					String line = "";
					StringBuilder builder = new StringBuilder();
					while ((line = lineReader.readLine()) != null) {
						builder.append(line);
					}

					lineReader.close();
					String json = builder.toString();
					return new Response(json);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(-1);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			case POST:
				try {
					FileWriter fileWriter = new FileWriter("./assets/test-post.txt");
					String json = session.getRawBody();
					fileWriter.write(json, 0, json.length());
					fileWriter.close();

					processInsert(json);

					return new Response("{\"result\":\"success\"}");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(-1);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				break;

			default:
				throw new UnsupportedOperationException("FATAL ERROR: Unknown Operation " + session.getMethod().name());
		}

		return null;
	}

	private void processInsert(String json) {
		Gson gson = new Gson();
		PostBody body = gson.fromJson(json, PostBody.class);

		for (Data datum : body.getData()) {
			Column<Integer> id = new Column<Integer>(DATA_COLUMNS.DEVICE_ID.name().toLowerCase(), DATA_COLUMNS.DEVICE_ID.ordinal() + 1, Types.INT, body.getDeviceId());
			Column<Double> latitude = new Column<Double>(DATA_COLUMNS.LATITUDE.name().toLowerCase(), DATA_COLUMNS.LATITUDE.ordinal() + 1, Types.DOUBLE, datum.getLatitude());
			Column<Double> longitude = new Column<Double>(DATA_COLUMNS.LONGITUDE.name().toLowerCase(), DATA_COLUMNS.LONGITUDE.ordinal() + 1, Types.DOUBLE, datum.getLongitude());
			Column<String> time = new Column<String>(DATA_COLUMNS.RECORDED_TIME.name().toLowerCase(), DATA_COLUMNS.RECORDED_TIME.ordinal() + 1, Types.STRING, datum.getTime());

			try {
				Column<?> columns[] = { id, latitude, longitude, time };
				DatabaseManager.getInstance().insert(TABLE_NAME, columns);
			} catch (SQLException e) {
				Log.e(TAG, "FATAL ERROR: Could not connect to the database");
				e.printStackTrace();
				IntelliBike.shutdown(this, -1);
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "FATAL ERROR: Could not find JDBC database driver");
				e.printStackTrace();
				IntelliBike.shutdown(this, -1);
			}
		}
	}

	@Override
	public void stop() {
		super.stop();
		try {
			DatabaseManager.getInstance().disconnect();
		} catch (SQLException e) {
			Log.e(TAG, "FATAL ERROR: Could not connect to the database");
			e.printStackTrace();
			IntelliBike.shutdown(this, -1);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "FATAL ERROR: Could not find JDBC database driver");
			e.printStackTrace();
			IntelliBike.shutdown(this, -1);
		}
	}

}
