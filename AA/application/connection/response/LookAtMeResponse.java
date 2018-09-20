package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.LookAtMeItem;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author tungd
 */
public class LookAtMeResponse extends Response {

  private static final String TAG = "LookAtMeResponse";
  private List<LookAtMeItem> listLookAtMe;

  public LookAtMeResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    // LogUtils.d(TAG, "Response=" + responseData.getText());
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (!jsonObject.has("data")) {
        return;
      }
      listLookAtMe = new ArrayList<LookAtMeItem>();
      JSONArray jsonArray = jsonObject.getJSONArray("data");
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject object = jsonArray.getJSONObject(i);
        boolean isOnline = object.getBoolean("is_online");
        int point = object.getInt("point");
        String userName = object.getString("user_name");
        int gender = object.getInt("gender");
        String userId = object.getString("user_id");
        int timeRemain = object.getInt("time_remain");
        String avatarId = null;
        if (object.has("ava_id")) {
          avatarId = object.getString("ava_id");
        }
        LookAtMeItem item = new LookAtMeItem(isOnline, point, userName,
            gender, userId, timeRemain, avatarId);
        listLookAtMe.add(item);
      }

    } catch (JSONException exception) {
      exception.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<LookAtMeItem> getListLookAtMe() {
    return listLookAtMe;
  }

}
