package com.application.connection.response;

import com.application.chat.ChatMessage;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.entity.ConversationItem;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class ConversationResponse extends Response {

  private ArrayList<ConversationItem> conversationItem;

  public ConversationResponse(ResponseData responseData) {
    super(responseData);
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
          JSONArray jsonData = jsonObject.getJSONArray("data");
          ArrayList<ConversationItem> list = new ArrayList<ConversationItem>();
          for (int i = 0; i < jsonData.length(); i++) {
            ConversationItem item = new ConversationItem();
            JSONObject conversationJson = jsonData.getJSONObject(i);
            if (conversationJson.has("frd_id")) {
              item.setFriendId(conversationJson
                  .getString("frd_id"));
            }
            if (conversationJson.has("frd_name")) {
              item.setName(conversationJson.getString("frd_name"));
            }
            if (conversationJson.has("is_online")) {
              item.setOnline(conversationJson
                  .getBoolean("is_online"));
            }
            if (conversationJson.has("last_msg")) {
              item.setLastMessage(conversationJson
                  .getString("last_msg"));
            }
            if (conversationJson.has("is_own")) {
              item.setOwn(conversationJson.getBoolean("is_own"));
            }
            if (conversationJson.has("sent_time")) {
              item.setSentTime(conversationJson
                  .getString("sent_time"));
            }
            if (conversationJson.has("unread_num")) {
              item.setUnreadNum(conversationJson
                  .getInt("unread_num"));
            }
            if (conversationJson.has("long")) {
              item.setLongtitude(conversationJson
                  .getDouble("long"));
            }
            if (conversationJson.has("lat")) {
              item.setLattitude(conversationJson.getDouble("lat"));
            }
            if (conversationJson.has("dist")) {
              item.setDistance(conversationJson.getDouble("dist"));
            }
            if (conversationJson.has("ava_id")) {
              item.setAvaId(conversationJson.getString("ava_id"));
            }

            if (conversationJson.has("gender")) {
              item.setGender(conversationJson.getInt("gender"));
            }
            if (conversationJson.has("msg_type")) {
              String msgType = conversationJson
                  .getString("msg_type");
              item.setMessageType(msgType);
              // set "" when msg is CMD
              if (msgType.equalsIgnoreCase(ChatMessage.CMD)) {
                item.setLastMessage("");
              }
            }
            if (conversationJson.has("voice_call_waiting")) {
              item.setVoiceCallWaiting(conversationJson
                  .getBoolean("voice_call_waiting"));
            }
            if (conversationJson.has("video_call_waiting")) {
              item.setVideoCallWaiting(conversationJson
                  .getBoolean("video_call_waiting"));
            }
//						item.setLastMessage(item.getLastMessage());
            item.setIsAnonymous(false);
            list.add(item);
          }
          setConversationItem(list);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
      setCode(CLIENT_ERROR_PARSE_JSON);
    }
  }

  public ArrayList<ConversationItem> getConversationItem() {
    return conversationItem;
  }

  public void setConversationItem(ArrayList<ConversationItem> conversationItem) {
    this.conversationItem = conversationItem;
  }

}
