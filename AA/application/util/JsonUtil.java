package com.application.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtil {

  public static String toJson(Object obj) {
    return new Gson().toJson(obj);
  }

  public static Object fromJson(String json, Class<?> X) {
    if (json != null) {
      try {
        return new Gson().fromJson(json, X);
      } catch (JsonSyntaxException e) {
        return null;
      }
    }
    return null;
  }
}
