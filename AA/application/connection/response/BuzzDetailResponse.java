package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.entity.SubComment;
import com.application.util.LogUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuzzDetailResponse extends Response {

  private static final long serialVersionUID = 3398137618109712048L;

  private static final String TAG = "BuzzDetailResponse";

  private BuzzListItem mBuzzDetail;

  public BuzzDetailResponse(ResponseData responseData) {
    super(responseData);
    LogUtils.d(TAG, "BuzzDetailResponse Started");
    LogUtils.d(TAG, "BuzzDetailResponse Ended");
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
        JSONObject jsonData = jsonObject.getJSONObject("data");

        if (mBuzzDetail != null) {
          // Release resource
          if (mBuzzDetail.getCommentList() != null) {
            mBuzzDetail.getCommentList().clear();
            mBuzzDetail.setCommentList(null);
          }
          mBuzzDetail = null;
        }
        mBuzzDetail = new BuzzListItem();

        if (jsonData.has("user_id")) {
          mBuzzDetail.setUserId(jsonData.getString("user_id"));
        }
        if (jsonData.has("user_name")) {
          mBuzzDetail.setUserName(jsonData.getString("user_name"));
        }
        if (jsonData.has("ava_id")) {
          mBuzzDetail.setAvatarId(jsonData.getString("ava_id"));
        }
        if (jsonData.has("gender")) {
          mBuzzDetail.setGender(jsonData.getInt("gender"));
        }
        if (jsonData.has("long")) {
          mBuzzDetail.setLogitude(jsonData.getDouble("long"));
        }
        if (jsonData.has("lat")) {
          mBuzzDetail.setLatitude(jsonData.getDouble("lat"));
        }
        if (jsonData.has("dist")) {
          mBuzzDetail.setDistance(jsonData.getDouble("dist"));
        }
        if (jsonData.has("is_like")) {
          mBuzzDetail.setIsLike(jsonData.getInt("is_like"));
        }
        if (jsonData.has("buzz_time")) {
          mBuzzDetail.setBuzzTime(jsonData.getString("buzz_time"));
        }
        if (jsonData.has("seen_num")) {
          mBuzzDetail.setSeenNumber(jsonData.getInt("seen_num"));
        }
        if (jsonData.has("like_num")) {
          mBuzzDetail.setLikeNumber(jsonData.getInt("like_num"));
        }
        if (jsonData.has("cmt_num")) {
          mBuzzDetail.setCommentNumber(jsonData.getInt("cmt_num"));
        }
        if (jsonData.has("buzz_id")) {
          mBuzzDetail.setBuzzId(jsonData.getString("buzz_id"));
        }
        if (jsonData.has("buzz_val")) {
          mBuzzDetail.setBuzzValue(jsonData.getString("buzz_val"));
        }
        if (jsonData.has("buzz_type")) {
          mBuzzDetail.setBuzzType(jsonData.getInt("buzz_type"));
        }
        if (jsonData.has("is_app")) {
          mBuzzDetail.setIsApproved(jsonData.getInt("is_app"));
        } else {
          mBuzzDetail.setIsApproved(Constants.IS_APPROVED);
        }
        if (jsonData.has("is_online")) {
          mBuzzDetail.setOnline(jsonData.getBoolean("is_online"));
        }
        if (jsonData.has("region")) {
          mBuzzDetail.setRegion(jsonData.getInt("region"));
        }
        if (jsonData.has("comment_buzz_point")) {
          mBuzzDetail.setCommentPoint(jsonData
              .getInt("comment_buzz_point"));
        }
        mBuzzDetail.setSelectToDelete(false);

        if (jsonData.has("comment")) {
          JSONArray jsonBuzzComment = jsonData
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
            cmt.add(buzzListCommentItem);
          }
          mBuzzDetail.setCommentList(cmt);
        }
      }
    } catch (JSONException jsone) {
      LogUtils.d(TAG, jsone.toString());
    }

    LogUtils.d(TAG, "parseData Ended (2)");
  }

  public BuzzListItem getBuzzDetail() {
    return mBuzzDetail;
  }

}
