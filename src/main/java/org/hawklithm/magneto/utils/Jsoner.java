package org.hawklithm.magneto.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class Jsoner {
	public static  Gson gson=new Gson();
	public static String toJson(Object object){
		return gson.toJson(object);
	}
	public static <T> T fromJson(String json,Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}
	public static <T> T fromJson(String json,Type typeOfT){
		return gson.fromJson(json, typeOfT);
	}
}
