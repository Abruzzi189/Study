package com.application.connection.response;

import android.content.Context;
import android.text.TextUtils;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.FileMessage;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.util.StorageUtil;
import com.application.util.preferece.UserPreferences;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetNewChatMessageResponse extends Response {
  private String mUserIdToSend;

  public GetNewChatMessageResponse(Context context,
      ResponseData responseData, String userIdToSend) {
    super(context, responseData);
    mUserIdToSend = userIdToSend;
    parseData(responseData);
  }

  private static final long serialVersionUID = -2399506406486578460L;
  private List<ChatMessage> listChatMessage;

  @Override
  protected void parseData(ResponseData responseData) {
    try {
      JSONObject jsonObject = responseData.getJSONObject();
      if (jsonObject == null)
        return;
      if (jsonObject.has("code")) {
        setCode(jsonObject.getInt("code"));
      }
      if (jsonObject.has("data")) {
        List<ChatMessage> listChatMessage = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject object = jsonArray.getJSONObject(i);
          String content = object.getString("content");
          String time = object.getString("time_stamp");
          String readTime = "";
          if (object.has("read_time")) {
            readTime = object.getString("read_time");
          }
          boolean isOwn = object.getBoolean("is_own");
          String msgType = object.getString("msg_type");
          String msgId = object.getString("msg_id");

          boolean isDeleteFile = false;
          if (object.has("deleted_file")) {
            isDeleteFile = object.getBoolean("deleted_file");
          }

          boolean isUnlock = false;
          if (object.has("is_unlock")) {
            isUnlock = object.getBoolean("is_unlock");
          }

          ChatMessage chatMessage;
          if (msgType.equalsIgnoreCase(ChatMessage.FILE)) {
            FileMessage fileMessage = getFileMessage(msgId, content);
            if (fileMessage == null) {
              return;
            }
            String t = "";
            String type = fileMessage.getFileType();
            switch (type) {
              case ChatManager.PHOTO:
                t = ChatMessage.PHOTO;
                break;
              case ChatManager.AUDIO:
                t = ChatMessage.AUDIO;
                break;
              case ChatManager.VIDEO:
                t = ChatMessage.VIDEO;
                break;
            }
            String userId = mUserIdToSend;
            if (isOwn) {
              userId = UserPreferences.getInstance().getUserId();
            }

            // get path file in maps
            String filePath = StorageUtil
                .getFilePathByUserIdAndFileId(mContext,
                    userId, msgId);
            if (!TextUtils.isEmpty(filePath)) {
              File file = new File(filePath);
              // only when exists -> add to file message
              if (file.exists()) {
                fileMessage.setFilePath(filePath);
              } else {
                // must remove msgId in file because file
                // has been deleted
                StorageUtil.removeLineInFile(mContext,
                    mUserIdToSend, msgId);
              }
            }
            // pass with empty userId
            chatMessage = new ChatMessage(msgId, "", isOwn,
                time, t, fileMessage);
            chatMessage.setReadTime(readTime);
            chatMessage.setFileDelete(isDeleteFile);
            chatMessage.setUnlock(isUnlock);
          } else if (msgType
              .equalsIgnoreCase(ChatMessage.STARTVIDEO)
              || msgType
              .equalsIgnoreCase(ChatMessage.STARTVOICE)) {
            // tungdx: new requirements, not show start video,
            // voice msg in chat
            chatMessage = null;
          } else {
            // Decrypt message in history
            chatMessage = new ChatMessage(msgId, "", isOwn,
                content, time, msgType);
            chatMessage.setReadTime(readTime);
            chatMessage.decryptMessageHistory();
          }
          if (chatMessage != null)
            listChatMessage.add(chatMessage);
        }
        setListChatMessage(listChatMessage);
      }

    } catch (JSONException e) {
      e.printStackTrace();
      setCode(Response.CLIENT_ERROR_PARSE_JSON);
    }
  }

  public List<ChatMessage> getListChatMessage() {
    return listChatMessage;
  }

  public void setListChatMessage(List<ChatMessage> listChatMessage) {
    //this.listChatMessage = listChatMessage;
    LinkedHashSet<ChatMessage> uniqueChatMsgList = new LinkedHashSet<>(listChatMessage);
    this.listChatMessage = new ArrayList<>();
    this.listChatMessage.addAll(uniqueChatMsgList);
  }

  /**
   * Use this in get History
   *
   * @param content
   * @return
   */
  public FileMessage getFileMessage(String messageId, String content) {
    if (!content.contains("|")) {
      FileMessage fileMessage = new FileMessage("", content, "");
      fileMessage.setStart(true);
      return fileMessage;
    }
    try {
      return new FileMessage(content);
    } catch (Exception exception) {
      exception.printStackTrace();
      return new FileMessage("", content, "");
    }
  }

}
