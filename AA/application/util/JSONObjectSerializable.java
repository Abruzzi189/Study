package com.application.util;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectSerializable extends JSONObject implements Serializable {

  private static final long serialVersionUID = -4513211889078414432L;

  public JSONObjectSerializable(String arg0) throws JSONException {
    super(arg0);
  }
}