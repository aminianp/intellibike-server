package com.intellibike.network;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.intellibike.app.IntelliBike;
import com.intellibike.models.Column;
import com.intellibike.models.Column.Types;
import com.intellibike.models.Data;
import com.intellibike.models.DataPacket;
import com.intellibike.models.DataPacket.DATA_COLUMNS;
import com.intellibike.sql.DatabaseManager;
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
					String param = session.getParms().get(DATA_COLUMNS.DEVICE_ID.name().toLowerCase());
					int deviceId = Integer.valueOf(param);
					String json = processGet(deviceId);
					return new Response(json);
				} catch (SQLException e) {
					Log.e(TAG, "FATAL ERROR: Could not connect to the database");
					e.printStackTrace();
					IntelliBike.shutdown(this, -1);
				} catch (ClassNotFoundException e) {
					Log.e(TAG, "FATAL ERROR: Could not find JDBC database driver");
					e.printStackTrace();
					IntelliBike.shutdown(this, -1);
				}

			case POST:
				try {
					String json = session.getRawBody();
					processInsert(json);
					return new Response("{\"result\":\"success\"}");
				} catch (SQLException e) {
					Log.e(TAG, "FATAL ERROR: Could not connect to the database");
					e.printStackTrace();
					IntelliBike.shutdown(this, -1);
				} catch (ClassNotFoundException e) {
					Log.e(TAG, "FATAL ERROR: Could not find JDBC database driver");
					e.printStackTrace();
					IntelliBike.shutdown(this, -1);
				}
				break;

			default:
				throw new UnsupportedOperationException("FATAL ERROR: Unknown Operation " + session.getMethod().name());
		}

		return null;
	}

	private String processGet(int deviceId) throws SQLException, ClassNotFoundException {
		String whereClause = DATA_COLUMNS.DEVICE_ID.name().toLowerCase() + " = ?";
		Column<Integer> deviceColumn = new Column<Integer>(DATA_COLUMNS.DEVICE_ID.name().toLowerCase(), DATA_COLUMNS.DEVICE_ID.ordinal() + 1, Types.INT, deviceId);

		Column<?> columns[] = { deviceColumn };
		Gson gson = new Gson();
		ResultSet results = DatabaseManager.getInstance().query(DataPacket.TABLE_NAME, whereClause, columns);
		DataPacket packet = new DataPacket();
		packet.generateResponsePacket(deviceId, results);
		return gson.toJson(packet);

	}

	private void processInsert(String json) throws SQLException, ClassNotFoundException {
		System.out.println(json);
		Gson gson = new Gson();
		DataPacket postBody = gson.fromJson(json, DataPacket.class);

		for (Data datum : postBody.getData()) {
			Column<Integer> id = new Column<Integer>(DATA_COLUMNS.DEVICE_ID.name().toLowerCase(), DATA_COLUMNS.DEVICE_ID.ordinal() + 1, Types.INT, postBody.getDeviceId());
			Column<Double> latitude = new Column<Double>(DATA_COLUMNS.LATITUDE.name().toLowerCase(), DATA_COLUMNS.LATITUDE.ordinal() + 1, Types.DOUBLE, datum.getLatitude());
			Column<Double> longitude = new Column<Double>(DATA_COLUMNS.LONGITUDE.name().toLowerCase(), DATA_COLUMNS.LONGITUDE.ordinal() + 1, Types.DOUBLE, datum.getLongitude());
			Column<String> time = new Column<String>(DATA_COLUMNS.RECORDED_TIME.name().toLowerCase(), DATA_COLUMNS.RECORDED_TIME.ordinal() + 1, Types.STRING, datum.getTime());

			Column<?> columns[] = { id, latitude, longitude, time };
			DatabaseManager.getInstance().insert(DataPacket.TABLE_NAME, columns);
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
