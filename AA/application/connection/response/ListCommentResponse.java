package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.SubComment;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListCommentResponse extends Response {

  private static final long serialVersionUID = -1001420332882063141L;

  private static final String TAG = "ListCommentResponse";

  /**
   * List of Buzz List Item(s)
   */
  private ArrayList<BuzzListCommentItem> mBuzzListCommentItem;

  /**
   * @param responseData
   */
  public ListCommentResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "ListCommentResponse Started");
    LogUtils.d(TAG, "ListCommentResponse Ended");
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
        JSONArray jsonBuzzComment = jsonObject.getJSONArray("data");

        if (this.mBuzzListCommentItem != null) {
          this.mBuzzListCommentItem.clear();
          this.mBuzzListCommentItem = null;
        }

        this.mBuzzListCommentItem = new ArrayList<BuzzListCommentItem>();

        for (int j = 0; j < jsonBuzzComment.length(); j++) {
          BuzzListCommentItem buzzListCommentItem = new BuzzListCommentItem();
          JSONObject commentJSON = jsonBuzzComment.getJSONObject(j);

          if (commentJSON.has("sub_comment_number")) {
            buzzListCommentItem.sub_comment_number = (commentJSON
                .getInt("sub_comment_number"));
          }

          if (commentJSON.has("sub_comment_point")) {
            buzzListCommentItem.sub_comment_point = (commentJSON
                .getInt("sub_comment_point"));
          }

          if (commentJSON.has("can_delete")) {
            buzzListCommentItem.can_delete = (commentJSON.getInt("can_delete"));
          }
          if (commentJSON.has("cmt_id")) {
            buzzListCommentItem.cmt_id = (commentJSON.getString("cmt_id"));
          }
          if (commentJSON.has("user_id")) {
            buzzListCommentItem.user_id = (commentJSON.getString("user_id"));
          }
          if (commentJSON.has("user_name")) {
            buzzListCommentItem.user_name = (commentJSON.getString("user_name"));
          }
          if (commentJSON.has("ava_id")) {
            buzzListCommentItem.ava_id = (commentJSON.getString("ava_id"));
          }
          if (commentJSON.has("gender")) {
            buzzListCommentItem.gender = (commentJSON.getInt("gender"));
          }
          if (commentJSON.has("cmt_val")) {
            buzzListCommentItem.cmt_val = (commentJSON.getString("cmt_val"));
          }
          if (commentJSON.has("cmt_time")) {
            buzzListCommentItem.cmt_time = (commentJSON.getString("cmt_time"));
          }
          if (commentJSON.has("is_app")) {
            buzzListCommentItem.isApproved = (commentJSON.getInt("is_app"));
          } else {
            buzzListCommentItem.isApproved = Constants.IS_APPROVED;
          }
          if (commentJSON.has("sub_comment")) {
            JSONArray jsonSubComment = commentJSON
                .getJSONArray("sub_comment");
            ArrayList<SubComment> subCmt = new ArrayList<SubComment>();
            for (int k = 0; k < jsonSubComment.length(); k++) {
              SubComment subComment = new SubComment();
              JSONObject subCommentJSON = jsonSubComment
                  .getJSONObject(k);
              if (subCommentJSON.has("sub_comment_id")) {
                subComment.sub_comment_id = subCommentJSON
                    .getString("sub_comment_id");
              }
              if (subCommentJSON.has("user_id")) {
                subComment.user_id = subCommentJSON
                    .getString("user_id");
              }
              if (subCommentJSON.has("value")) {
                subComment.value = subCommentJSON
                    .getString("value");
              }
              if (subCommentJSON.has("time")) {
                subComment.time = subCommentJSON
                    .getString("time");
              }
              if (subCommentJSON.has("can_delete")) {
                subComment.can_delete = (subCommentJSON
                    .getBoolean("can_delete"));
              }
              if (subCommentJSON.has("ava_id")) {
                subComment.ava_id = (subCommentJSON
                    .getString("ava_id"));
              }
              if (subCommentJSON.has("gender")) {
                subComment.gender = (subCommentJSON
                    .getInt("gender"));
              }
              if (subCommentJSON.has("is_online")) {
                subComment.is_online = (subCommentJSON
                    .getBoolean("is_online"));
              }
              if (subCommentJSON.has("user_name")) {
                subComment.user_name = (subCommentJSON
                    .getString("user_name"));
              }
              if (subCommentJSON.has("is_app")) {
                subComment.isApprove = (subCommentJSON
                    .getInt("is_app"));
              } else {
                subComment.isApprove = Constants.IS_APPROVED;
              }
              subCmt.add(subComment);
            }
            buzzListCommentItem.sub_comments = subCmt;
          }
          this.mBuzzListCommentItem.add(buzzListCommentItem);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

  public ArrayList<BuzzListCommentItem> getBuzzListCommentItem() {
    LogUtils.d(TAG, "getBuzzListCommentItem Started");
    LogUtils.d(TAG, "getBuzzListCommentItem Ended");
    return this.mBuzzListCommentItem;
  }
}
