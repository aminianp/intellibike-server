package com.intellibike.models;

public class Column<T> {

	public static enum Types {
		INT, DOUBLE, STRING, TIMESTAMP
	};

	private String name;
	private int index;
	private Types type;
	private T value;

	public Column(String name, int index, Types type, T value) {
		this.name = name;
		this.index = index;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public Types getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

}
