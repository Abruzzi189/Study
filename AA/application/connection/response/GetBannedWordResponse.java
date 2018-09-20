package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetBannedWordResponse extends Response {

  @SerializedName("version")
  private int version;

  @SerializedName("list")
  private ArrayList<String> listWord;

  public GetBannedWordResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getVersion() {
    return this.version;
  }

  public ArrayList<String> getListWord() {
    if (listWord == null) {
      listWord = new ArrayList<String>();
    }
    return this.listWord;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }

      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");

        if (dataJson.has("version")) {
          this.version = dataJson.getInt("version");
        }

        if (dataJson.has("list")) {
          JSONArray dataJsonArray = dataJson.getJSONArray("list");
          ArrayList<String> listWords = new ArrayList<String>();
          for (int i = 0; i < dataJsonArray.length(); i++) {
            String word = dataJsonArray.getString(i);
            listWords.add(word);
          }
          this.listWord = listWords;
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}