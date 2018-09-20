package com.application.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RestCaller {

  public static String execute(String url) throws IOException {
    URL url2;
    url2 = new URL(url);
    URLConnection connection = url2.openConnection();
    InputStream inputStream = connection.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(inputStream));

    StringBuilder stringBuilder = new StringBuilder();
    String data;
    while ((data = bufferedReader.readLine()) != null) {
      stringBuilder.append(data);
    }
    inputStream.close();
    return stringBuilder.toString();
  }
}
