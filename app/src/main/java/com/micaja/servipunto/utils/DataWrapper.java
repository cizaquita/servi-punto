package com.micaja.servipunto.utils;

import com.google.gson.Gson;

public class DataWrapper {
	public Data data;

	public static DataWrapper fromJson(String s) {
		return new Gson().fromJson(s, DataWrapper.class);
	}

	public String toString() {
		return new Gson().toJson(this);
	}
}
