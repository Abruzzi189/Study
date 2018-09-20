package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.entity.SubComment;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListSubCommentResponse extends Response {

  private static final long serialVersionUID = 1436357114952670841L;

  private static final String TAG = "ListSubCommentResponse";

  /**
   * List of Sub Comment
   */
  private ArrayList<SubComment> mSubComments;

  public ListSubCommentResponse(ResponseData responseData) {
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
        JSONArray jsonSubComment = jsonObject.getJSONArray("data");

        if (this.mSubComments != null) {
          this.mSubComments.clear();
          this.mSubComments = null;
        }

        this.mSubComments = new ArrayList<SubComment>();

        for (int j = 0; j < jsonSubComment.length(); j++) {
          SubComment subComment = new SubComment();
          JSONObject subCommentJSON = jsonSubComment
              .getJSONObject(j);
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
          this.mSubComments.add(subComment);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }
  }

  public ArrayList<SubComment> getSubComments() {
    return this.mSubComments;
  }
}
