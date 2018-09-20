package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPointActionResponse extends Response {

  private int save_image_point;
  private int chat_point;
  private int unlock_backstage_point;
  private int unlock_backstage_bonus_point;
  private int comment_point;

  public GetPointActionResponse(ResponseData responseData) {
    super(responseData);
  }

  public int getSavePoint() {
    return save_image_point;
  }

  public void setSavePoint(int point) {
    this.save_image_point = point;
  }

  public int getChatPoint() {
    return chat_point;
  }

  public void setChatPoint(int point) {
    this.chat_point = point;
  }

  public int getBackstagePoint() {
    return unlock_backstage_point;
  }

  public void setBackstagePoint(int point) {
    this.unlock_backstage_point = point;
  }

  public int getBackstageBonusPoint() {
    return unlock_backstage_bonus_point;
  }

  public void setBackstageBonusPoint(int point) {
    this.unlock_backstage_bonus_point = point;
  }

  public int getCommentPoint() {
    return comment_point;
  }

  public void setCommentPoint(int point) {
    this.comment_point = point;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    JSONObject jsonObject = responseData.getJSONObject();
    if (jsonObject == null) {
      return;
    }
    try {
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        if (dataJson.has("save_image_point")) {
          setSavePoint(dataJson.getInt("save_image_point"));
        }
        if (dataJson.has("chat_point")) {
          setChatPoint(dataJson.getInt("chat_point"));
        }
        if (dataJson.has("unlock_backstage_point")) {
          setBackstagePoint(dataJson.getInt("unlock_backstage_point"));
        }
        if (dataJson.has("unlock_backstage_bonus_point")) {
          setBackstageBonusPoint(dataJson
              .getInt("unlock_backstage_bonus_point"));
        }
        if (dataJson.has("comment_point")) {
          setCommentPoint(dataJson.getInt("comment_point"));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }
}
