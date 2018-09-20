package com.application.connection;

import com.application.util.JSONObjectSerializable;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseData implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 8188554900430771350L;
  protected String mText;
  protected JSONObjectSerializable mJsonObject;

  private int status;

  public String getText() {
    return mText;
  }

  public void setText(String text) {
    mText = text;
  }

  public JSONObject getJSONObject() {
    return mJsonObject;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void makeJSONObject() throws JSONException {
    mJsonObject = new JSONObjectSerializable(getText());
  }
}
