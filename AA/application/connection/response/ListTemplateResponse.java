package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.Template;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListTemplateResponse extends Response {

  private static final long serialVersionUID = -4948058973242154697L;

  private ArrayList<Template> templates;

  public ListTemplateResponse(ResponseData responseData) {
    super(responseData);
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }
      if (jsonObject != null) {
        if (jsonObject.has("code")) {
          setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
          JSONArray dataArrayJson = jsonObject.getJSONArray("data");
          templates = new ArrayList<Template>();
          for (int i = 0; i < dataArrayJson.length(); i++) {
            JSONObject dataJson = dataArrayJson.getJSONObject(i);
            Template template = new Template();
            if (dataJson.has("template_id")) {
              template.setTempId(dataJson
                  .getString("template_id"));
            }
            if (dataJson.has("template_title")) {
              template.setTempTitle(dataJson
                  .getString("template_title"));
            }
            if (dataJson.has("template_content")) {
              template.setTempContent(dataJson
                  .getString("template_content"));
            }
            templates.add(template);
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Template> getTemplates() {
    return templates;
  }
}
