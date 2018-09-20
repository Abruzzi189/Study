/**
 *
 */
package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author TuanPQ
 */
public class BuzzListResponse extends Response {

  private static final long serialVersionUID = -7108928473475370457L;

  /**
   * TAG, used for defined this class name.
   */
  private static final String TAG = "BuzzListResponse";

  /**
   * List of Buzz List Item(s)
   */
  private ArrayList<BuzzListItem> mBuzzListItem;

  /**
   * @param responseData
   */
  public BuzzListResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "BuzzListResponse Started");
    LogUtils.d(TAG, "BuzzListResponse Ended");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.application.connection.Response#parseData(com.application.connection
   * .ResponseData)
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

        if (this.mBuzzListItem != null) {
          this.mBuzzListItem.clear();
          this.mBuzzListItem = null;
        }

        this.mBuzzListItem = new ArrayList<BuzzListItem>();

        for (int i = 0; i < jsonData.length(); i++) {
          BuzzListItem bli = new BuzzListItem();
          JSONObject bliJson = jsonData.getJSONObject(i);

          if (bliJson.has("user_id")) {
            bli.setUserId(bliJson.getString("user_id"));
          }
          if (bliJson.has("user_name")) {
            bli.setUserName(bliJson.getString("user_name"));
          }
          if (bliJson.has("ava_id")) {
            bli.setAvatarId(bliJson.getString("ava_id"));
          }
          if (bliJson.has("gender")) {
            bli.setGender(bliJson.getInt("gender"));
          }
          if (bliJson.has("long")) {
            bli.setLogitude(bliJson.getDouble("long"));
          }
          if (bliJson.has("lat")) {
            bli.setLatitude(bliJson.getDouble("lat"));
          }
          if (bliJson.has("dist")) {
            bli.setDistance(bliJson.getDouble("dist"));
          }
          if (bliJson.has("is_like")) {
            bli.setIsLike(bliJson.getInt("is_like"));
          }
          if (bliJson.has("buzz_time")) {
            bli.setBuzzTime(bliJson.getString("buzz_time"));
          }
          if (bliJson.has("seen_num")) {
            bli.setSeenNumber(bliJson.getInt("seen_num"));
          }
          if (bliJson.has("like_num")) {
            bli.setLikeNumber(bliJson.getInt("like_num"));
          }
          if (bliJson.has("cmt_num")) {
            bli.setCommentNumber(bliJson.getInt("cmt_num"));
          }
          if (bliJson.has("buzz_id")) {
            bli.setBuzzId(bliJson.getString("buzz_id"));
          }
          if (bliJson.has("buzz_val")) {
            bli.setBuzzValue(bliJson.getString("buzz_val"));
          }
          if (bliJson.has("buzz_type")) {
            bli.setBuzzType(bliJson.getInt("buzz_type"));
          }
          if (bliJson.has("is_app")) {
            bli.setIsApproved(bliJson.getInt("is_app"));
          } else {
            bli.setIsApproved(Constants.IS_APPROVED);
          }
          if (bliJson.has("is_online")) {
            bli.setOnline(bliJson.getBoolean("is_online"));
          }
          if (bliJson.has("region")) {
            bli.setRegion(bliJson.getInt("region"));
          }
          if (bliJson.has("comment_buzz_point")) {
            bli.setCommentPoint(bliJson
                .getInt("comment_buzz_point"));
          }
          bli.setSelectToDelete(false);
          if (bliJson.has("comment")) {
            JSONArray jsonBuzzComment = bliJson
                .getJSONArray("comment");
            ArrayList<BuzzListCommentItem> cmt = new ArrayList<BuzzListCommentItem>();

            for (int j = 0; j < jsonBuzzComment.length(); j++) {
              BuzzListCommentItem buzzListCommentItem = new BuzzListCommentItem();
              JSONObject commentJSON = jsonBuzzComment
                  .getJSONObject(j);

              if (commentJSON.has("sub_comment_number")) {
                buzzListCommentItem.sub_comment_number = (commentJSON
                    .getInt("sub_comment_number"));
              }

              if (commentJSON.has("sub_comment_point")) {
                buzzListCommentItem.sub_comment_point = (commentJSON
                    .getInt("sub_comment_point"));
              }

              if (commentJSON.has("can_delete")) {
                buzzListCommentItem.can_delete = (commentJSON
                    .getInt("can_delete"));
              }
              if (commentJSON.has("cmt_id")) {
                buzzListCommentItem.cmt_id = (commentJSON
                    .getString("cmt_id"));
              }
              if (commentJSON.has("user_id")) {
                buzzListCommentItem.user_id = (commentJSON
                    .getString("user_id"));
              }
              if (commentJSON.has("user_name")) {
                buzzListCommentItem.user_name = (commentJSON
                    .getString("user_name"));
              }
              if (commentJSON.has("ava_id")) {
                buzzListCommentItem.ava_id = (commentJSON
                    .getString("ava_id"));
              }
              if (commentJSON.has("gender")) {
                buzzListCommentItem.gender = (commentJSON
                    .getInt("gender"));
              }
              if (commentJSON.has("cmt_val")) {
                buzzListCommentItem.cmt_val = (commentJSON
                    .getString("cmt_val"));
              }
              if (commentJSON.has("cmt_time")) {
                buzzListCommentItem.cmt_time = (commentJSON
                    .getString("cmt_time"));
              }
              if (commentJSON.has("is_online")) {
                buzzListCommentItem.is_online = commentJSON
                    .getBoolean("is_online");
              }
              if (commentJSON.has("is_app")) {
                buzzListCommentItem.isApproved = commentJSON
                    .getInt("is_app");
              } else {
                buzzListCommentItem.isApproved = Constants.IS_APPROVED;
              }
              cmt.add(buzzListCommentItem);
            }
            bli.setCommentList(cmt);
          }
          this.mBuzzListItem.add(bli);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

  public ArrayList<BuzzListItem> getBuzzListItem() {
    LogUtils.d(TAG, "getBuzzListItem Started");
    LogUtils.d(TAG, "getBuzzListItem Ended");
    return this.mBuzzListItem;
  }
}
