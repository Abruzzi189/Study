package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.NewPostItem;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ThoNh on 12/12/2017.
 */

public class ListNewPostResponse extends Response {

  public List<NewPostItem> mData;

  public ListNewPostResponse(ResponseData responseData) {
    super(responseData);
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
        JSONArray dataArrayJson = jsonObject.getJSONArray("data");
        mData = new ArrayList<>();
        for (int i = 0; i < dataArrayJson.length(); i++) {
          JSONObject dataJson = dataArrayJson.getJSONObject(i);

          NewPostItem data = new NewPostItem();
          if (dataJson.has("buzz_id")) {
            data.buzzId = dataJson.getString("buzz_id");
          }

          if (dataJson.has("user_id")) {
            data.userId = dataJson.getString("user_id");
          }

          if (dataJson.has("ava_id")) {
            data.avaId = dataJson.getString("ava_id");
          }

          if (dataJson.has("buzz_type")) {
            data.buzzType = dataJson.getInt("buzz_type");
          }

          mData.add(data);
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
