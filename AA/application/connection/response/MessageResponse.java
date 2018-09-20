package com.application.connection.response;

import android.content.Context;
import com.application.chat.ChatMessage;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageResponse extends Response {

  private final String TAG = "MessageResponse";

  public MessageResponse(Context context, ResponseData responseData) {
    super(context, responseData);
  }

  @Override
  protected void parseData(ResponseData responseData) {
  }

  /**
   * Remove all message duplicate in chat message list resource input
   *
   * @param listChatMessage The ArrayList message input need to check duplicate
   * @return ChatMessage List normalized
   * @author Created by Robert on 21 Mar 2017
   */
  protected List<ChatMessage> removeDuplicateMessage(final List<ChatMessage> listChatMessage) {
    List<ChatMessage> mMessageListResult = new ArrayList<ChatMessage>();
    try {
      if (listChatMessage == null || listChatMessage.isEmpty()) {
        return mMessageListResult;
      }

      for (ChatMessage chatMsg : listChatMessage) {
        int idxResource = fetchingIndexMsgResourceOfByIndex(chatMsg, mMessageListResult);

        if (idxResource < 0) {
          //Add new message into list message resource when not found duplicate
          mMessageListResult.add(chatMsg);
          continue;
        }
        /**
         * TODO When idxResource > -1 then update new content of message. Updating status of message by messageId
         */
        //Update into list message resource with new content of message
        ChatMessage msgResource = mMessageListResult.get(idxResource);
        //Keep the message with time_stamp newer
        if (chatMsg.getTimeInMilisecond() > 0 && chatMsg.getTimeInMilisecond() > msgResource
            .getTimeInMilisecond()) {
          mMessageListResult.set(idxResource, chatMsg);
        }
      }


    } catch (Exception e) {
    }
    return mMessageListResult;
  }

  /**
   * Remove all duplicate message in the list
   *
   * @return List<ChatMessage>
   * @author Created by Robert on 2017 Nov 30
   */
  protected List<ChatMessage> removeDuplicate(List<ChatMessage> mNewMessageList) {

    if (mNewMessageList == null || mNewMessageList.isEmpty()) {
      return mNewMessageList;
    }
    List<ChatMessage> mMessageListTemplate = new CopyOnWriteArrayList<>();
    ;
    mMessageListTemplate.addAll(mNewMessageList);

    //Now clear the mNewMessageList, and start adding them again
    mNewMessageList.clear();
    for (ChatMessage message : mMessageListTemplate) {
      int idxResource = fetchingIndexMsgResourceOfByIndex(message, mNewMessageList);
      if (idxResource < 0) {
        //Add new message into list message resource when not found duplicate
        mNewMessageList.add(message);
        continue;
      }
      /**
       * TODO When idxResource > -1 then update new content of message. Updating status of message by messageId
       */
      //Update into list message resource with new content of message
      mNewMessageList.set(idxResource, message);
    }
    return mNewMessageList;
  }

  /**
   * TODO Fetching index of by message index.
   *
   * @param msgSearchNeed the msg search need
   * @param mMessageListResource the message list
   * @return the int number index of message in Message list resource
   * @author Created by Robert Hoang on 21 Mar 2017
   */
  private int fetchingIndexMsgResourceOfByIndex(ChatMessage msgSearchNeed,
      final List<ChatMessage> mMessageListResource) {
    if (msgSearchNeed == null || mMessageListResource == null || mMessageListResource.isEmpty()) {
      return -1;
    }
    try {
      int size = mMessageListResource.size();

      for (int idxResource = 0; idxResource < size; idxResource++) {
        ChatMessage message = mMessageListResource.get(idxResource);

        if (msgSearchNeed.getMessageId().equals(message.getMessageId())) {
          return idxResource;
        }
        if (msgSearchNeed.getContent().startsWith(message.getMessageId() + "|")) {
          return idxResource;
        }

        if (msgSearchNeed.getContent().contains("|" + message.getMessageId() + "|")) {
          return idxResource;
        }
      }
    } catch (Exception e) {
    }
    return -1;
  }
}
