package com.intellibike.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.intellibike.models.DataPacket.DATA_COLUMNS;

public class Data {
	private String time;
	private double lat;
	private double lng;

	public String getTime() {
		return time;
	}

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lng;
	}

	public void generateData(ResultSet result) throws SQLException {
		time = result.getString(DATA_COLUMNS.RECORDED_TIME.ordinal() + 1);
		lat = result.getDouble(DATA_COLUMNS.LATITUDE.ordinal() + 1);
		lng = result.getDouble(DATA_COLUMNS.LONGITUDE.ordinal() + 1);
	}

}
