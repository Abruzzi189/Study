package com.application.connection.response;

import com.application.connection.Response;
import com.application.connection.ResponseData;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPageInfoResponse extends Response {

  /**
   *
   */
  private static final long serialVersionUID = -7233665960820439635L;

  private int backstageNum; // bckstg_num

  private int buzzNum; // buzz_number

  private int checkoutNum; // checkout_num

  private int favoritesNum; // fvt_num

  private int notiLikeNum; // noti_like_num

  private List<GiftCount> giftCounts; // gift_list - gift_id - gift_num

  public MyPageInfoResponse(ResponseData responseData) {
    super(responseData);
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
        JSONObject data = jsonObject.getJSONObject("data");

        if (data.has("bckstg_num")) {
          setBackstageNum(data.getInt("bckstg_num"));
        }

        if (data.has("buzz_number")) {
          setBuzzNum(data.getInt("buzz_number"));
        }

        if (data.has("checkout_num")) {
          setCheckoutNum(data.getInt("checkout_num"));
        }

        if (data.has("fvt_num")) {
          setFavoritesNum(data.getInt("fvt_num"));
        }

        if (data.has("noti_like_num")) {
          setNotiLikeNum(data.getInt("noti_like_num"));
        }

        if (data.has("gift_list")) {
          JSONArray array = data.getJSONArray("gift_list");

          ArrayList<GiftCount> gifts = new ArrayList<MyPageInfoResponse.GiftCount>();

          for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            GiftCount giftCount = new GiftCount();
            giftCount.setGiftId(ob.getString("gift_id"));
            giftCount.setNum(ob.getInt("gift_num"));

            gifts.add(giftCount);
          }

          setGiftCounts(gifts);
        }
      }
    } catch (JSONException e) {

      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public int getBackstageNum() {
    return backstageNum;
  }

  public void setBackstageNum(int backstageNum) {
    this.backstageNum = backstageNum;
  }

  public int getBuzzNum() {
    return buzzNum;
  }

  public void setBuzzNum(int buzzNum) {
    this.buzzNum = buzzNum;
  }

  public int getCheckoutNum() {
    return checkoutNum;
  }

  public void setCheckoutNum(int checkoutNum) {
    this.checkoutNum = checkoutNum;
  }

  public int getFavoritesNum() {
    return favoritesNum;
  }

  public void setFavoritesNum(int favoritesNum) {
    this.favoritesNum = favoritesNum;
  }

  public int getNotiLikeNum() {
    return notiLikeNum;
  }

  public void setNotiLikeNum(int notiLikeNum) {
    this.notiLikeNum = notiLikeNum;
  }

  public List<GiftCount> getGiftCounts() {
    return giftCounts;
  }

  public void setGiftCounts(List<GiftCount> giftCounts) {
    this.giftCounts = giftCounts;
  }

  public class GiftCount {

    private String giftId;
    private int num;

    public String getGiftId() {
      return giftId;
    }

    public void setGiftId(String giftId) {
      this.giftId = giftId;
    }

    public int getNum() {
      return num;
    }

    public void setNum(int num) {
      this.num = num;
    }
  }
}
