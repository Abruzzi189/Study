package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.BlockedUserItem;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlockedUsersListResponse extends Response {

  /**
   * TAG, used for defined this class name.
   */
  private static final String TAG = "BlockedUsersListResponse";

  /**
   * List of blocked users
   */
  private ArrayList<BlockedUserItem> mBlockedUsersList;

  /**
   * @param responseData
   */
  public BlockedUsersListResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "BlockedUsersListResponse Started");
    LogUtils.d(TAG, "BlockedUsersListResponse Ended");
  }

  /* (non-Javadoc)
   * @see com.application.connection.Response#parseData(com.application.connection.ResponseData)
   */
  @Override
  protected void parseData(ResponseData responseData) {
    LogUtils.d(TAG, "parseData Started");

    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        LogUtils.d(TAG, "parseData Ended (1)");
        return;
      }

      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }

      if (jsonObject.has("data")) {
        JSONArray jsonData = jsonObject.getJSONArray("data");

        if (this.mBlockedUsersList != null) {
          this.mBlockedUsersList.clear();
          this.mBlockedUsersList = null;
        }

        this.mBlockedUsersList = new ArrayList<BlockedUserItem>();

        for (int i = 0; i < jsonData.length(); i++) {
          BlockedUserItem bui = new BlockedUserItem();
          JSONObject bliJson = jsonData.getJSONObject(i);

          if (bliJson.has("user_id")) {
            bui.setUserId(bliJson.getString("user_id"));
          }
          if (bliJson.has("user_name")) {
            bui.setUserName(bliJson.getString("user_name"));
          }
          if (bliJson.has("ava_id")) {
            bui.setAvatarId(bliJson.getString("ava_id"));
          }
          if (bliJson.has("age")) {
            bui.setAge(bliJson.getInt("age"));
          }
          if (bliJson.has("gender")) {
            bui.setGender(bliJson.getInt("gender"));
          }

          //This user was blocked.
          bui.setBlockedStatus(true);

          this.mBlockedUsersList.add(bui);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

  public ArrayList<BlockedUserItem> getBlockedUsersList() {
    LogUtils.d(TAG, "getBlockedUsersList Started");
    LogUtils.d(TAG, "getBlockedUsersList Ended");
    return this.mBlockedUsersList;
  }

}
