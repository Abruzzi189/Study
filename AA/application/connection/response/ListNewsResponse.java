package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.News;
import com.application.util.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HungHN on 4/7/2016.
 */
public class ListNewsResponse extends Response {

  private static final String TAG = "ListNewsResponse";
  News newscmcode;
  private List<News> newsList;
  private int code;

  public ListNewsResponse(ResponseData responseData) {
    super(responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      newscmcode = new News();
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null) {
        return;
      }

      if (jsonObject.has("code")) {
        code = jsonObject.getInt("code");
      }
      //hiepuh
      if (jsonObject.has("flag")) {
        newscmcode.setHaveCMcode(jsonObject.getInt("flag"));
      }
      if (jsonObject.has("url")) {
        newscmcode.setUrl(jsonObject.getString("url"));
      }

      if (jsonObject.has("data")) {
        JSONArray jsonArrayNews = jsonObject.getJSONArray("data");

        if (this.newsList != null) {
          this.newsList.clear();
          this.newsList = null;
        }
        newsList = new ArrayList<>();

        for (int i = 0; i < jsonArrayNews.length(); i++) {
          News news = new News();
          JSONObject jsonNews = jsonArrayNews.getJSONObject(i);
          if (jsonNews.has("news_id")) {
            news.setId(jsonNews.getString("news_id"));
          }
          if (jsonNews.has("title")) {
            news.setTitle(jsonNews.getString("title"));
          }
          if (jsonNews.has("banner_id")) {
            news.setBanner(jsonNews.getString("banner_id"));
          }
          if (jsonNews.has("content")) {
            news.setHtmlContent(jsonNews.getString("content"));
          }
          if (jsonNews.has("from")) {
            news.setFromDate(jsonNews.getString("from"));
          }
          if (jsonNews.has("to")) {
            news.setToDate(jsonNews.getString("to"));
          }
          newsList.add(news);
        }

      }
    } catch (JSONException ex) {
      LogUtils.d(TAG, ex.toString());
    }
  }

  public List<News> getNewsList() {
    return newsList;
  }

  public News getCmcode() {
    return newscmcode;
  }

  @Override
  public int getCode() {
    return code;
  }
}
