package com.intellibike.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataPacket extends GenericPacket {

	public static final String TABLE_NAME = "data";

	public static enum DATA_COLUMNS {
		DEVICE_ID, LATITUDE, LONGITUDE, RECORDED_TIME
	};

	private Data[] dataset;

	public Data[] getData() {
		return dataset;
	}

	public void generateResponsePacket(int deviceId, ResultSet results) throws SQLException {
		List<Data> data = new ArrayList<Data>();

		device_id = deviceId;

		results.beforeFirst();
		while (results.next()) {
			Data datum = new Data();
			datum.generateData(results);
			data.add(datum);
		}

		dataset = data.toArray(new Data[0]);

	}
}
