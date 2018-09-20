package com.application.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.application.Config;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.FileMessage;
import com.application.connection.request.FileRequest;
import com.application.entity.TimeAudioHold;
import com.application.fileload.DownloadFile;
import com.application.fileload.DownloadFile.FILETYPE;
import com.application.layout.AudioChatView;
import com.application.layout.GiftChatView;
import com.application.layout.HeaderChatView;
import com.application.layout.HeaderViewHolder;
import com.application.layout.PhotoChatView;
import com.application.layout.RequestCallView;
import com.application.layout.StickerChatView;
import com.application.layout.TextMessageChatView;
import com.application.layout.TypingChatView;
import com.application.layout.VideoChatView;
import com.application.layout.VoipChatView;
import com.application.status.MessageInDB;
import com.application.status.StatusConstant;
import com.application.ui.ChatFragment;
import com.application.uploadmanager.UploadState;
import com.application.util.LogUtils;
import com.application.util.StringUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class ChatAdapter extends BaseAdapter {

  private static final String TAG = "ChatAdapter";

  private static final int TYPE_HEADER = 0;
  private static final int TYPE_PHOTO_SEND = 2;
  private static final int TYPE_PHOTO_RECEIVER = 3;
  private static final int TYPE_AUDIO_SEND = 4;
  private static final int TYPE_AUDIO_RECEIVER = 5;
  private static final int TYPE_GIFT_SEND = 6;
  private static final int TYPE_GIFT_RECEIVER = 7;
  private static final int TYPE_MESSAGE_SEND = 10;
  private static final int TYPE_MESSAGE_RECEIVER = 11;
  private static final int TYPE_STICKER_SEND = 12;
  private static final int TYPE_STICKER_RECEIVER = 13;
  private static final int TYPE_VIDEO_SEND = 14;
  private static final int TYPE_VIDEO_RECEIVER = 15;
  private static final int TYPE_VOIP_SEND = 16;
  private static final int TYPE_VOIP_RECEIVER = 17;
  private static final int TYPE_TYPING = 18;
  private static final int TYPE_CALLREQUEST_SEND = 19;
  private static final int TYPE_CALLREQUEST_RECEIVER = 20;
  private static final int TYPE_NO_DEFINED = 21;
  //NumType = max type_view value + 1;
  private static final int NUM_TYPE = TYPE_NO_DEFINED + 1;


  private List<ChatMessage> listChatMessage = new ArrayList<>();
  private boolean hasShowCheckBoxDelete = false;
  private Context mAppContext;
  private Handler mHandlerElapsed = new Handler();
  private Runnable mRunnableElapsed = null;
  private LayoutInflater mInflater;
  private ChatFragment mChatFragment;
  private MediaPlayer mMediaPlayer;
  private ListView mListView;
  private Activity mActivity;

  private String userName = "";
  private IOnGetVideoURL onGetVideoURL;
  private IOnOpenImage onOpenImage;
  private boolean isMergeredHistory;

  /**
   * Constructor with data
   */
  public ChatAdapter(Activity activity, ChatFragment chatFragment,
      ListView listView) {
    mActivity = activity;
    mAppContext = activity.getApplicationContext();
    mInflater = (LayoutInflater) mAppContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mChatFragment = chatFragment;
    mListView = listView;
    isMergeredHistory = false;
  }

  public List<ChatMessage> getListChatMessage() {
    return listChatMessage;
  }

  public int getMsgLocation(String msgId) {
    int size = listChatMessage.size();
    for (int i = 0; i < size; i++) {
      if (listChatMessage.get(i).getMessageId().equals(msgId)) {
        return i;
      }
    }
    return 0;
  }

  /**
   * Update media file message data (if exist) or create a new message in chat list.
   *
   * @param newChatMessage New message data to update
   */
  public void updateMedia(ChatMessage newChatMessage) {
    // Make sure new chat message valid
    if (newChatMessage == null || newChatMessage.getMessageId() == null) {
      return;
    }

    // Check message exist
    boolean notExist = true;
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.getFileMessage() != null) {
        String newMessageId = newChatMessage.getMessageId();
        String oldMessageId = chatMessage.getMessageId();
        if (newMessageId.equals(oldMessageId)) {
          chatMessage.setFileMessage(newChatMessage.getFileMessage());

          // Check read time message
          String readTime = chatMessage.getReadTime();
          if (!TextUtils.isEmpty(readTime)) {
            chatMessage.setStatusSend(StatusConstant.STATUS_READ);
          } else {
            chatMessage
                .setStatusSend(StatusConstant.STATUS_SUCCESS);
          }
          notExist = false;
          break;
        }
      }
    }

    // If not exist this message. Create a new message in this list
    if (notExist) {
      appendNewMessage(newChatMessage);
    }
    Log.e(TAG, "updateMedia call to notifyDataSetChanged");
    notifyDataSetChanged();
  }

  public void updateMediaInfo(String msgId, String filePath) {
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      String currentMsgId = chatMessage.getMessageId();
      if (currentMsgId.equals(msgId)
          && chatMessage.getFileMessage() != null) {
        FileMessage fileMessage = chatMessage.getFileMessage();
        fileMessage.setFilePath(filePath);
      }
    }
  }

  /**
   * Update download state success
   */
  public void updateDownloadSuccessState(String newMessageId, String filePath) {
    if (listChatMessage != null)
      for (int i = listChatMessage.size() - 1; i >= 0; i--) {
        ChatMessage chatMessage = listChatMessage.get(i);
        if (chatMessage != null) {
          String oldMessageId = chatMessage.getMessageId();
          if (oldMessageId != null && newMessageId != null)
            if (oldMessageId.equals(newMessageId)) {
              chatMessage.getFileMessage().setFilePath(filePath);

              // Disable transcript mode to keep the list stay
              mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
              notifyDataSetChanged();
              break;
            }
        }
      }
  }

  /**
   * Update upload state of media message
   */
  public void updateUploadState(String messageId, int uploadState, int percent) {
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.getMessageId().equals(messageId)) {
        chatMessage.getFileMessage().uploadProgress = percent;
        chatMessage.getFileMessage().uploadState = uploadState;
        if (uploadState == UploadState.SUCCESSFUL) {
          chatMessage.getFileMessage().setStart(false);
        }

        // Disable transcript mode to keep the list stay
        mListView
            .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

        LogUtils.e(TAG, "-->updateUploadState()....call to notifyDataSetChanged...");

        notifyDataSetChanged();
        break;
      }
    }
  }

  /**
   * Update file id
   */
  public void updateFileId(String msgId, String fileId) {
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.getMessageId().equals(msgId)) {
        chatMessage.getFileMessage().setFileId(fileId);
        // Disable transcript mode to keep the list stay
        mListView
            .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

        LogUtils.e(TAG, "-->updateFileId()....call to notifyDataSetChanged...");
        notifyDataSetChanged();
        break;
      }
    }
  }

  /**
   * Update status of message
   */
//  public void updateMessageStatus(String messageId, boolean isSent) {
//    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
//      ChatMessage chatMessage = listChatMessage.get(i);
//      if (chatMessage.getMessageId().equals(messageId)) {
//        chatMessage.setEnoughPointToSend(isSent);
//
//        LogUtils.e(TAG, "-->updateMessageStatus()....call to notifyDataSetChanged...");
//        notifyDataSetChanged();
//        break;
//      }
//    }
//  }
  public void updateMessageStatusError(String messageId) {
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.getMessageId().equals(messageId)) {
//        chatMessage.setEnoughPointToSend(isSent);
        LogUtils.e(TAG, "-->updateMessageStatus()....call to notifyDataSetChanged...");
        chatMessage.setStatusSend(StatusConstant.STATUS_ERROR);
        notifyDataSetChanged();
        break;
      }
    }
  }
  /**
   * Update read all message from position
   */
  public synchronized void readAllMessage() {
    for (int i = listChatMessage.size() - 1; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.isOwn() && chatMessage.getStatusSend() == StatusConstant.STATUS_SUCCESS) {
        chatMessage.setStatusSend(StatusConstant.STATUS_READ);
      }
    }

    LogUtils.e(TAG, "-->readAllMessage()....call to notifyDataSetChanged...");

    notifyDataSetChanged();
  }

  public synchronized void updateReadMessage(String messageId) {
    if (listChatMessage.size() <= 0) {
      return;
    }
    int max = listChatMessage.size() - 1;
    for (int i = max; i >= 0; i--) {
      ChatMessage chatMessage = listChatMessage.get(i);
      if (chatMessage.getMessageId().equals(messageId)) {
//                readAllMessage(i);
        if (chatMessage.isOwn() && chatMessage.getStatusSend() == StatusConstant.STATUS_SUCCESS) {
          chatMessage.setStatusSend(StatusConstant.STATUS_READ);
        }
        break;
      }
    }
    notifyDataSetChanged();
  }

  /**
   * Update message send success, failed or error from database
   *
   * @param dbMsg Message from database
   * @param status Status of this message
   */
  public void updateMessageStatus(ChatMessage dbMsg, int status) {
    Iterator<ChatMessage> ite = listChatMessage.iterator();
    ChatMessage historyMsg;
    while (ite.hasNext()) {
      historyMsg = ite.next();
      if (historyMsg.getMessageId().equals(dbMsg.getMessageId())) {
        if (status == StatusConstant.STATUS_SUCCESS) {
          if (historyMsg.isFileMessage()) {
            dbMsg.setFileMessage(historyMsg.getFileMessage());
          }

          // remove message failed
          ite.remove();

          // append message success
          appendNewMessage(dbMsg);
        } else {
          // update time send
          historyMsg.setTimeStamp(dbMsg.getTimeStamp());
          // update status message failed
          historyMsg.setStatusSend(status);
        }
        Log.e(TAG, "updateMessageStatus call to notifyDataSetChanged");
        notifyDataSetChanged();
        break;
      }
    }
  }

  /**
   * Create new message status from message in database
   */
  public void createMessageStatus(MessageInDB msgInDB) {
    Iterator<ChatMessage> ite = listChatMessage.iterator();
    ChatMessage historyMsg;
    while (ite.hasNext()) {
      historyMsg = ite.next();
      if (historyMsg.getMessageId().equals(msgInDB.getId())) {
        historyMsg.setTimeStamp(Utility.getTimeStamp(new Date(msgInDB
            .getTimeSend())));
        historyMsg.setStatusSend(msgInDB.getStatus());

        LogUtils.e(TAG, "-->createMessageStatus()....call to notifyDataSetChanged...");
        notifyDataSetChanged();
        break;
      }
    }
  }

  /**
   * Append new message to chat list
   */
  public void appendNewMessage(ChatMessage newMessage) {
    LogUtils.i(TAG, "--->appendNewMessage.newMessage=" + newMessage.toString());
    // Setting to make list view dynamic
   // mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

    if (listChatMessage == null) {
      listChatMessage = new ArrayList<ChatMessage>();
    }

    if (newMessage == null || StringUtils.isEmptyOrNull(newMessage.getMessageId())) {
      return;
    }
    int idxResource = fetchingIndexMsgResourceOfByIndex(newMessage, listChatMessage);
    if (idxResource > -1) {

      ChatMessage messageResource = listChatMessage.get(idxResource);
      if (messageResource != null) {
        LogUtils.i(TAG,
            "--->appendNewMessage(Update).update new info of messageId=" + newMessage.getMessageId()
                + "|at index=" + idxResource
                + "|messageResource.getContent()=" + messageResource.getContent()
                + "|newMessage.Content=" + newMessage.getContent());
        updateReadMessage(idxResource, messageResource.getMessageId(), newMessage);
      }
      notifyDataSetChanged();
      return;
    }

    int size = listChatMessage.size();
    int index = size;
    if (size > 0) {
      // Get the last message. If that is typing message, by pass it
      ChatMessage chatMessage = listChatMessage.get(size - 1);
      if (chatMessage.isTypingMessage()) {
        index--;
      }
    }

    if (size > 0) {
      // Get the last message except typing message
      ChatMessage lastMessage = listChatMessage.get(index - 1);
      long lastTime = lastMessage.getTimeInMilisecond();
      long newTime = newMessage.getTimeInMilisecond();

      // Check if need create a new header message
      if (newTime - lastTime > Config.THRESHOLD_HEADER_CHAT
          || Utility.checkGreaterThenDay(lastTime, newTime)) {
        ChatMessage header = new ChatMessage();
        header.setHeader(true);
        Date date = Calendar.getInstance().getTime();
        header.setContent(Utility.getTitleForHeaderInListView(date,
            mAppContext));
        header.setTimeStamp(Utility.getTimeStamp(date));
        header.setTimeInMilisecond(Utility
            .convertTimeToMilisecond(header.getTimeStamp()));

        // Add header at the end of list except typing message
        listChatMessage.add(index, header);
        // Add new message after this header
        index++;
        Log.e(TAG, "listChatMessage-----1111111---------> " + listChatMessage.size());
        listChatMessage.add(index, newMessage);
      } else {
        // Only add new message at the end of list except typing message

        Log.e(TAG, "listChatMessage------2222222222--------> " + listChatMessage.size());
        listChatMessage.add(index, newMessage);

      }
    } else {
      ChatMessage header = new ChatMessage();
      header.setHeader(true);
      Date date = Calendar.getInstance().getTime();
      header.setContent(Utility.getTitleWhenListEmpty(date, mAppContext));
      header.setTimeStamp(Utility.getTimeStamp(date));
      header.setTimeInMilisecond(Utility.convertTimeToMilisecond(header
          .getTimeStamp()));

      // Add header at the end of list except typing message
      Log.e(TAG, "listChatMessage------3333333--------> " + listChatMessage.size());
      listChatMessage.add(index, header);
      // Add new message after this header
      index++;
      Log.e(TAG, "listChatMessage------4444444--------> " + listChatMessage.size());
      listChatMessage.add(index, newMessage);
    }
    Log.e(TAG, "appendNewMessage call to notifyDataSetChanged");
    notifyDataSetChanged();
  }

  /**
   * Append all list message. This list only append from bottom up. This don't care about order in
   * this chat list fragment.
   *
   * @param mMsgHistoryNewList List chat to add to the top of list chat.
   */
  public void appendMessageHistoryList(List<ChatMessage> mMsgHistoryNewList) {
    // Validate list message
//         clearAllMessage();
    List<ChatMessage> mMessageList = normalizeMessageList(mMsgHistoryNewList, listChatMessage);
    if (mMessageList == null || mMessageList.size() < 1) {
      return;
    }
    int size = listChatMessage.size();
    LogUtils.e(TAG,
        "listChatMessage.size(111)=" + listChatMessage.size() + "|mMessageList.size(111)="
            + mMessageList.size());
    LogUtils.e(TAG,
        "=========================================================================================================");
    //for (int i = 0; i < listChatMessage.size(); i++) {
    //ChatMessage oldMsg = listChatMessage.get(i);
    //LogUtils.w(TAG, "OldMsgID=" + oldMsg.getMessageId() +"|oldMsgType=" + oldMsg.getMsgType() +"|oldMsgContent=" + oldMsg.getContent() + "|oldMsgResourceType=" + oldMsg.getMsgType() + "|oldMsgResourceIsOwn=" + oldMsg.isOwn());
    //}
    //LogUtils.e(TAG, "=========================================================================================================");
        /*for (int i = 0; i < mMessageList.size(); i++) {
            ChatMessage newMsg = mMessageList.get(i);
            LogUtils.e(TAG, "Need append more newMsgID=" + newMsg.getMessageId() +"|newMsgType=" + newMsg.getMsgType() +"|newMsgContent=" + newMsg.getContent() + "|newMsgResourceType=" + newMsg.getMsgType() + "|newMsgResourceIsOwn=" + newMsg.isOwn());
        }*/
    LogUtils.w(TAG,
        "=========================================================================================================");
    int index = 0;
    // Create a header if need
    if (size == 0) {
      ChatMessage header;
      header = new ChatMessage();
      header.setHeader(true);
      ChatMessage firstMsg = mMessageList.get(0);
      Date date = Utility.getDateFromTimeStamp(firstMsg.getTimeStamp());
      header.setContent(Utility.getTitleForHeaderInListView(date, mAppContext));
      header.setTimeInMilisecond(firstMsg.getTimeInMilisecond());
      header.setTimeStamp(firstMsg.getTimeStamp());

      // Append the first message with header
      listChatMessage.add(0, header);
      /**
       * TODO updated by Robert on 02 Mar 2017 about tickets:
       * - http://10.64.100.201/issues/7079
       * - http://10.64.100.201/issues/7086
       */
      //listChatMessage.add(1, firstMsg);
      //index++;
      LogUtils.e(TAG, "listChatMessage.size(222)=" + listChatMessage.size() + "|index=" + index);
    }

    // Load all list to add and order list message
    for (int i = index; i < mMessageList.size(); i++) {
      ChatMessage newMsg = mMessageList.get(i);
      // By pass header message
      if (newMsg.isHeader()) {
        continue;
      }
      /**
       * TODO updated by Robert on 02 Mar 2017 about tickets:
       * - http://10.64.100.201/issues/7079
       * - http://10.64.100.201/issues/7086
       */
            /*Iterator<ChatMessage> ite = listChatMessage.iterator();
            while (ite.hasNext()) {
                ChatMessage historyMsg = ite.next();
                if (historyMsg.getMessageId().equals(newMsg.getMessageId())) {
                    ite.remove();
                }
            }*/
      //LogUtils.e(TAG, "listChatMessage.size(333)=" + listChatMessage.size() + "|mMessageList.size(333)=" + mMessageList.size());

      long newTime = newMsg.getTimeInMilisecond();
      ChatMessage headerRoot = listChatMessage.get(0);
      long rootTime = headerRoot.getTimeInMilisecond();
      long threshold = rootTime - newTime;
      if (threshold > Config.THRESHOLD_HEADER_CHAT || Utility
          .checkGreaterThenDay(newTime, rootTime)) {
        // Create a new header
        ChatMessage header = new ChatMessage();
        header.setHeader(true);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newMsg.getTimeInMilisecond());
        Date date = calendar.getTime();
        header.setTimeStamp(newMsg.getTimeStamp());
        header.setTimeInMilisecond(newMsg.getTimeInMilisecond());
        header.setContent(Utility.getTitleForHeaderInListView(date, mAppContext));

        // if newMsg send success then Add the new header at the begin of list chat
        if (!checkExistHeader(header) && !isMessageNotSuccess(newMsg.getStatusSend())) {
          listChatMessage.add(0, header);
        }
        listChatMessage.add(1, newMsg);
      } else {
        if (!isMessageNotSuccess(newMsg.getStatusSend())) {
          //if newMsg send success then update headRoot
          Calendar calendar = Calendar.getInstance();
          calendar.setTimeInMillis(newMsg.getTimeInMilisecond());
          Date date = calendar.getTime();
          headerRoot.setTimeStamp(newMsg.getTimeStamp());
          headerRoot.setTimeInMilisecond(newMsg.getTimeInMilisecond());
          headerRoot.setContent(Utility.getTitleForHeaderInListView(date, mAppContext));
        }

        // Append a new message after header chat
        listChatMessage.add(1, newMsg);
      }
    }

    int lastPosition = ((listChatMessage != null && !listChatMessage.isEmpty()) ?
        listChatMessage.size() - 1 : 0);

    LogUtils.v(TAG,
        "appendMessageHistoryList().lastPositionMsgId=" + (listChatMessage.get(lastPosition) != null
            ? listChatMessage.get(lastPosition).getMessageId() + "===>lastPosition=" + lastPosition
            + "|listMsgSize=" + listChatMessage.size() : "" + lastPosition));
    Log.e(TAG, "listChatMessage------4444444--------> " + listChatMessage.size());
    notifyDataSetChanged();
  }

  private boolean isMessageNotSuccess(int status) {
    switch (status) {
      case StatusConstant.STATUS_START:
      case StatusConstant.STATUS_RETRY:
      case StatusConstant.STATUS_SENDING_FILE:
      case StatusConstant.STATUS_ERROR:
        return true;
      default:
        return false;
    }
  }

  /**
   * Append all list message new message.
   */
  public void appendMessageNewList(List<ChatMessage> listNew) {
    List<ChatMessage> mMessageList = normalizeMessageList(listNew, listChatMessage);
    // Validate list message
    if (mMessageList == null || mMessageList.size() < 1) {
      return;
    }

    // Setting to make list view dynamic
//        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

    int size = listChatMessage.size();
    int count = mMessageList.size() - 1;
    // Create a header if need
    if (size == 0) {
      ChatMessage header;
      header = new ChatMessage();
      header.setHeader(true);
      ChatMessage lastMsg = mMessageList.get(count);
      Date date = Utility.getDateFromTimeStamp(lastMsg.getTimeStamp());
      header.setContent(Utility.getTitleForHeaderInListView(date,
          mAppContext));
      header.setTimeInMilisecond(lastMsg.getTimeInMilisecond());
      header.setTimeStamp(lastMsg.getTimeStamp());

      // Append the first message with header
      listChatMessage.add(0, header);
      listChatMessage.add(1, lastMsg);
      count--;
    }

    // Load all list to add and order list message
    for (int i = count; i >= 0; i--) {
      ChatMessage newMsg = mMessageList.get(i);
      // By pass header message
      if (newMsg.isHeader()) {
        continue;
      }

      if (isExistNewChatMessage(newMsg)) {
        continue;
      }
      // add newMsg to list message ---------------------------------------
      int index = listChatMessage.size();

      // Get the last message except typing message
      ChatMessage lastMessage = listChatMessage.get(index - 1);
      long lastTime = lastMessage.getTimeInMilisecond();
      long newTime = newMsg.getTimeInMilisecond();

      // Check if need create a new header message
      if (newTime - lastTime > Config.THRESHOLD_HEADER_CHAT
          || Utility.checkGreaterThenDay(lastTime, newTime)) {
        ChatMessage header = new ChatMessage();
        header.setHeader(true);
        Date date = Calendar.getInstance().getTime();
        header.setContent(Utility.getTitleForHeaderInListView(date,
            mAppContext));
        header.setTimeStamp(Utility.getTimeStamp(date));
        header.setTimeInMilisecond(Utility
            .convertTimeToMilisecond(header.getTimeStamp()));

        if (!checkExistHeader(header)) {
          // Add header at the end of list except typing message
          listChatMessage.add(index, header);
          // Add new message after this header
          index++;
        }
        listChatMessage.add(index, newMsg);
      } else {
        // Only add new message at the end of list except typing message
        listChatMessage.add(index, newMsg);
      }
    }
    LogUtils.i(TAG, "--->appendMessageNewList... calling ... notifyDataSetChanged");
    notifyDataSetChanged();
  }


  /**
   * TODO Remove all messages that already exist in mMessageListResource
   *
   * @author Created by Robert on 03 Nov 2017
   */
  private List<ChatMessage> normalizeMessageList(List<ChatMessage> mMessageList,
      final List<ChatMessage> mMessageListResource) {

    if (mMessageList == null || mMessageList.isEmpty() || mMessageListResource == null
        || mMessageListResource.isEmpty()) {
      return mMessageList;
    }

    int size = mMessageList.size();
    LogUtils.w(TAG,
        "-------------------------------------List Resource-----------------------------------------------");
        /*for (ChatMessage chatMessage : mMessageListResource) {
            LogUtils.w(TAG, "OldMsgID=" + chatMessage.getMessageId() +"|oldMsgType=" + chatMessage.getMsgType() +"|oldMsgContent=" + chatMessage.getContent() + "|oldMsgResourceType=" + chatMessage.getMsgType() + "|oldMsgResourceIsOwn=" + chatMessage.isOwn());
        }*/
    LogUtils.w(TAG,
        "-------------------------------------------------------------------------------------------------");
    //LogUtils.i(TAG, "--->000 mMessageList.size()=" + size);
    int cs = 0;
    while (cs < size) {
      ChatMessage chatMessage = mMessageList.get(cs);
      int idxResource = fetchingIndexMsgResourceOfByIndex(chatMessage, mMessageListResource);
      if (idxResource > -1) {
        /**
         * TODO: Updated by Robert on 09 Mar 2017 about ticket http://10.64.100.201/issues/7237
         * Updating status of message by messageId
         */
        ChatMessage messageResource = mMessageListResource.get(idxResource);
        if (messageResource != null) {
          //LogUtils.i(TAG, "--->normalizeMessageList().mMessageListResource.size=" + mMessageListResource.size() + "|update new read time=" + chatMessage.getReadTime() + " for message resource index=" + idxResource + "|messageResource.getContent()=" + messageResource.getContent());
          updateReadMessage(idxResource, messageResource.getMessageId(), chatMessage);
        }

        /**
         * TODO Updated by Robert on 07 Mar 2017 about ticket http://10.64.100.201/issues/7070
         * Duplicate message content
         */
        mMessageList.remove(cs);
        size = mMessageList.size();
        //LogUtils.i(TAG, "--->111 mMessageList.new size()=" + size);
      } else {
        cs++;
      }
    }
    //LogUtils.i(TAG, "--->222 mMessageList.size()=" + mMessageList.size());
    return mMessageList;
  }

  /**
   * TODO Fetching index of by message index.
   *
   * @param msgSearchNeed the msg search need
   * @param mMessageListResource the message list
   * @return the int
   * @author Created by Robert Hoang
   */
  private int fetchingIndexMsgResourceOfByIndex(ChatMessage msgSearchNeed,
      final List<ChatMessage> mMessageListResource) {
    if (msgSearchNeed == null || mMessageListResource == null || mMessageListResource.isEmpty()) {
      return -1;
    }
    try {
      //int size = mMessageListResource.size();

      for (int idxResource = 0; idxResource < mMessageListResource.size(); idxResource++) {
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


  /**
   * TODO Update message content if already in resource chat list by msgID: - Status of message -
   * Content of message - ReadTime of message
   */
  public synchronized void updateReadMessage(final int positions, final String messageId,
      final ChatMessage newChatMessage) {

    if (listChatMessage.isEmpty() || newChatMessage == null) {
      return;
    }
    ChatMessage chatMessage = listChatMessage.get(positions);

    if (chatMessage != null && chatMessage.getMessageId().equals(messageId)) {
      if (chatMessage.getStatusSend() == StatusConstant.STATUS_SUCCESS) {

        chatMessage.setStatusSend(StatusConstant.STATUS_READ);
        if (!StringUtils.isEmptyOrNull(newChatMessage.getContent())) {
          chatMessage.setContent(newChatMessage.getContent());
        }
        if (!StringUtils.isEmptyOrNull(newChatMessage.getReadTime())) {
          chatMessage.setReadTime(newChatMessage.getReadTime());
        }
        if (!StringUtils.isEmptyOrNull(newChatMessage.getTimeStamp())) {
          chatMessage.setTimeStamp(newChatMessage.getTimeStamp());
        }
      }
    }
  }

  /**
   * Check Header existed on chat history
   */
  private boolean checkExistHeader(ChatMessage header) {
    for (ChatMessage msg : listChatMessage) {
      long lastTime = msg.getTimeInMilisecond();
      long newTime = header.getTimeInMilisecond();
      if (msg.isHeader()) {
        if (Utility.checkThisDay(lastTime, newTime)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * check new message existed on listChatMessage
   */
  private boolean isExistNewChatMessage(ChatMessage newMsg) {
    Iterator<ChatMessage> ite = listChatMessage.iterator();
    while (ite.hasNext()) {
      ChatMessage historyMsg = ite.next();
      if (historyMsg.getMessageId().equals(newMsg.getMessageId())) {
        historyMsg.setStatusSend(newMsg.getStatusSend());
        historyMsg.setReadTime(newMsg.getReadTime());
        historyMsg.setFileDelete(newMsg.isFileDelete());
        return true;
      }
    }
    return false;
  }


  /**
   * Removing all typing message in list
   */
  public void removeAllTypingMessage() {
    Iterator<ChatMessage> lisIterator = listChatMessage.iterator();
    while (lisIterator.hasNext()) {
      if (lisIterator.next().isTypingMessage()) {
        lisIterator.remove();
      }
    }
    Log.e(TAG, "removeAllTypingMessage call to notifyDataSetChanged");
    notifyDataSetChanged();
  }

  /**
   * Clear all message from chat list
   */
  public void clearAllMessage() {
    if (listChatMessage != null) {
      listChatMessage.clear();

      Log.e(TAG, "clearAllMessage call to notifyDataSetChanged");
      notifyDataSetChanged();
    }
  }

  public void setOnGetVideoURL(IOnGetVideoURL onGetVideoURL) {
    this.onGetVideoURL = onGetVideoURL;
  }

  public void setOnOpenImage(IOnOpenImage onOpenImage) {
    this.onOpenImage = onOpenImage;
  }

  public void setMergeredHistory() {
    isMergeredHistory = true;
  }

  public boolean isMergeredHistory() {
    return isMergeredHistory;
  }

  @Override
  public int getCount() {
    return listChatMessage.size();
  }

  @Override
  public ChatMessage getItem(int position) {
    if (position < 0 || position >= listChatMessage.size()) {
      return null;
    } else {
      return listChatMessage.get(position);
    }
  }

  public ChatMessage getItemByFileId(String fileId) {
    if (fileId == null) {
      return null;
    }

    for (ChatMessage chatMessage : listChatMessage) {
      FileMessage fileMessage = chatMessage.getFileMessage();
      if (fileMessage != null) {
        String currentId = fileMessage.getFileId();
        if (currentId != null && currentId.equals(fileId)) {
          return chatMessage;
        }
      }
    }

    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getViewTypeCount() {
    return NUM_TYPE;
  }

  @Override
  public int getItemViewType(int position) {
    ChatMessage chatMessage = listChatMessage.get(position);
    if (chatMessage.isHeader()) {
      return TYPE_HEADER;
    }
    String type = chatMessage.getMsgType();
    if (type.equals(ChatMessage.PHOTO)) {
      return (chatMessage.isOwn()) ? TYPE_PHOTO_SEND
          : TYPE_PHOTO_RECEIVER;
    } else if (type.equals(ChatMessage.AUDIO)) {
      return (chatMessage.isOwn()) ? TYPE_AUDIO_SEND
          : TYPE_AUDIO_RECEIVER;
    } else if (type.equals(ChatMessage.GIFT)) {
      return (chatMessage.isOwn()) ? TYPE_GIFT_SEND : TYPE_GIFT_RECEIVER;
    } else if (type.equals(ChatMessage.WINK) || type.equals(ChatMessage.PP)) {
      return (chatMessage.isOwn()) ? TYPE_MESSAGE_SEND
          : TYPE_MESSAGE_RECEIVER;
    } else if (type.equals(ChatMessage.STICKER)) {
      return (chatMessage.isOwn()) ? TYPE_STICKER_SEND
          : TYPE_STICKER_RECEIVER;
    } else if (type.equals(ChatMessage.VIDEO)) {
      return (chatMessage.isOwn()) ? TYPE_VIDEO_SEND
          : TYPE_VIDEO_RECEIVER;
    } else if (type.equalsIgnoreCase(ChatMessage.STARTVIDEO)
        || type.equalsIgnoreCase(ChatMessage.ENDVIDEO)
        || type.equalsIgnoreCase(ChatMessage.STARTVOICE)
        || type.equalsIgnoreCase(ChatMessage.ENDVOICE)) {
      return (chatMessage.isOwn()) ? TYPE_VOIP_SEND : TYPE_VOIP_RECEIVER;
    } else if (type.equals(ChatMessage.TYPING)) {
      return TYPE_TYPING;
    } else if (type.equals(ChatMessage.CALLREQUEST)) {
      return (chatMessage.isOwn()) ? TYPE_CALLREQUEST_SEND
          : TYPE_CALLREQUEST_RECEIVER;
    } else {
      return TYPE_NO_DEFINED;
    }
  }

  /**
   * get header time view
   */
  private View getSendHeaderView(ChatMessage chatMessage, View convertView,
      ViewGroup parent) {
    HeaderViewHolder holder = null;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_list_chat_header,
          parent, false);
      holder = ((HeaderChatView) convertView).fillHolder();
      convertView.setTag(holder);
    } else {
      holder = (HeaderViewHolder) convertView.getTag();
    }
    ((HeaderChatView) convertView).fillData(chatMessage, holder);
    return convertView;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);
    ChatMessage chatMessage = listChatMessage.get(position);
    switch (type) {
      case TYPE_HEADER:
        return getSendHeaderView(chatMessage, convertView, parent);
      case TYPE_PHOTO_SEND:
        return getPhotoMessageView(chatMessage, convertView, true);
      case TYPE_PHOTO_RECEIVER:
        return getPhotoMessageView(chatMessage, convertView, false);
      case TYPE_AUDIO_SEND:
        return getAudioMessageView(chatMessage, convertView, true);
      case TYPE_AUDIO_RECEIVER:
        return getAudioMessageView(chatMessage, convertView, false);
      case TYPE_GIFT_SEND:
        return getGiftMessageView(chatMessage, convertView, true);
      case TYPE_GIFT_RECEIVER:
        return getGiftMessageView(chatMessage, convertView, false);
      case TYPE_MESSAGE_SEND:
        return getTextMessageView(chatMessage, convertView, true);
      case TYPE_MESSAGE_RECEIVER:
        return getTextMessageView(chatMessage, convertView, false);
      case TYPE_STICKER_SEND:
        return getStickerMessageView(chatMessage, convertView, true);
      case TYPE_STICKER_RECEIVER:
        return getStickerMessageView(chatMessage, convertView, false);
      case TYPE_VIDEO_SEND:
        return getVideoMessageView(chatMessage, convertView, true);
      case TYPE_VIDEO_RECEIVER:
        return getVideoMessageView(chatMessage, convertView, false);
      case TYPE_VOIP_SEND:
        return getVoipMessageView(chatMessage, convertView, true);
      case TYPE_VOIP_RECEIVER:
        return getVoipMessageView(chatMessage, convertView, false);
      case TYPE_TYPING:
        return getTypingView(chatMessage, convertView);
      case TYPE_CALLREQUEST_SEND:
        return getCallRequestView(chatMessage, convertView, true);
      case TYPE_CALLREQUEST_RECEIVER:
        return getCallRequestView(chatMessage, convertView, false);
      case TYPE_NO_DEFINED:
        return getEmptyView(convertView);
      default:
        break;
    }
    throw new IllegalArgumentException("Check view type again");
  }

  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    if (getCount() == 0) {
      isMergeredHistory = false;
    }
    LogUtils.w(TAG,
        "=====listMsg.size()=" + ((listChatMessage != null && !listChatMessage.isEmpty())
            ? listChatMessage.size() : 0));
    if (listChatMessage != null && !listChatMessage.isEmpty()) {
      LogUtils.w(TAG, "=====================================");
      for (int idx = 0; idx < listChatMessage.size(); idx++) {
        ChatMessage chatMessage = listChatMessage.get(idx);
        int type = getItemViewType(idx);
        if (type == TYPE_GIFT_SEND) {
          LogUtils.w(TAG,
              "ResourceMsgID=" + chatMessage.getMessageId() + "|ResourceMsgType=" + chatMessage
                  .getMsgType() + "|ResourceMsgContent=" + chatMessage.getContent()
                  + "|ResourceMsgResourceType=" + chatMessage.getMsgType()
                  + "|ResourceMsgResourceIsOwn=" + chatMessage.isOwn());

        }
      }
      LogUtils.w(TAG, "=====================================");
    }
  }

  private View getEmptyView(View convertView) {
    if (convertView == null) {
      convertView = new View(mAppContext);
    }
    return convertView;
  }

  private View getTypingView(ChatMessage chatMessage, View convertView) {
    if (convertView == null) {
      convertView = new TypingChatView(mAppContext,
          R.layout.item_list_chat_typing);
    }
    ((TypingChatView) convertView).setUserName(userName);
    ((TypingChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getVoipMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    int flag = 0;
    //if (convertView == null) {
    int resId;
    if (isSend) {
      resId = R.layout.item_list_chat_send_voice;
      flag = 1;
    } else {
      resId = R.layout.item_list_chat_receiver_voice;
      flag = 2;
    }
    convertView = new VoipChatView(mAppContext, resId, isSend);
    //}
    ((VoipChatView) convertView).setUserName(userName);
    if (flag == 1) {
      ((VoipChatView) convertView).fillData(mChatFragment, chatMessage);
    } else if (flag == 2) {
      ((VoipChatView) convertView).fillData1(mChatFragment, chatMessage);
    }
    return convertView;
  }

  private View getVideoMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_video;
      } else {
        resId = R.layout.item_list_chat_receiver_video;
      }
      convertView = new VideoChatView(mAppContext, resId, isSend,
          onGetVideoURL);
    }
    ((VideoChatView) convertView).setUserName(userName);
    ((VideoChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getStickerMessageView(ChatMessage chatMessage,
      View convertView, boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_sticker;
      } else {
        resId = R.layout.item_list_chat_receiver_sticker;
      }
      convertView = new StickerChatView(mAppContext, resId, isSend);
    }
    ((StickerChatView) convertView).setUserName(userName);
    ((StickerChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getGiftMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_gift;
      } else {
        resId = R.layout.item_list_chat_receiver_gift;
      }
      convertView = new GiftChatView(mAppContext, resId, isSend);
    }
    ((GiftChatView) convertView).setUserName(userName);
    ((GiftChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getAudioMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_audio;
      } else {
        resId = R.layout.item_list_chat_receiver_audio;
      }
      convertView = new AudioChatView(mAppContext, resId, isSend);
    }
    ((AudioChatView) convertView).setUserName(userName);
    ((AudioChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getPhotoMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_photo;
      } else {
        resId = R.layout.item_list_chat_receiver_photo;
      }
      convertView = new PhotoChatView(mAppContext, resId, isSend,
          onOpenImage);
    }
    ((PhotoChatView) convertView).setUserName(userName);
    ((PhotoChatView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getTextMessageView(ChatMessage chatMessage, View convertView,
      boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_send_message;
      } else {
        resId = R.layout.item_list_chat_receiver_message;
      }
      convertView = new TextMessageChatView(mAppContext, resId, isSend);
    }
    ((TextMessageChatView) convertView).setUserName(userName);
    ((TextMessageChatView) convertView)
        .fillData(mChatFragment, chatMessage);
    return convertView;
  }

  private View getCallRequestView(ChatMessage chatMessage, View convertView, boolean isSend) {
    if (convertView == null) {
      int resId;
      if (isSend) {
        resId = R.layout.item_list_chat_request_call;
      } else {
        resId = R.layout.item_list_chat_request_call_receiver;
      }
      convertView = new RequestCallView(mAppContext, resId, isSend);
    }
    ((RequestCallView) convertView).setUserName(userName);
    ((RequestCallView) convertView).fillData(mChatFragment, chatMessage);
    return convertView;
  }

  public void setEnableView(boolean enable) {
    LogUtils.e(TAG, "-->setEnableView()....call to notifyDataSetChanged...");
    notifyDataSetChanged();
  }

  @SuppressWarnings("unused")
  private void sendMediaErrorMessage() {
    LocalBroadcastManager broadcastManager = LocalBroadcastManager
        .getInstance(mAppContext);
    String msg = mAppContext
        .getString(R.string.an_error_occurred_while_download_file);
    Intent intent = new Intent(ChatManager.ACTION_DOWNLOAD_FILE_ERROR);
    intent.putExtra(ChatManager.EXTRA_DATA, msg);
    broadcastManager.sendBroadcast(intent);
  }

  public void clearStatusAdioPlay() {
    if (listChatMessage != null) {
      for (ChatMessage chatMessage : listChatMessage) {
        if (chatMessage != null
            && !TextUtils.isEmpty(chatMessage.getMsgType())
            && chatMessage.getMsgType() == ChatMessage.AUDIO) {
          chatMessage.getFileMessage().setPlay(false);
        }
      }
    }
    LogUtils.e(TAG, "-->clearStatusAdioPlay()....call to notifyDataSetChanged...");
    notifyDataSetChanged();
  }

  public void stopPlayAudio(ChatMessage chatMessage) {
    if (chatMessage == null) {
      return;
    }
    if (mMediaPlayer != null) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
      }
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    chatMessage.getFileMessage().setPlay(false);
    LogUtils.e(TAG, "-->stopPlayAudio()....call to notifyDataSetChanged...");
    notifyDataSetChanged();
  }

  public void stopPlayAudio() {
    if (mMediaPlayer != null) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
      }
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  private void updateAudioState(ChatMessage chatMessage) {
    chatMessage.getFileMessage().setPlay(true);
    // other message pause.
    for (ChatMessage message : listChatMessage) {
      if (message != null && message.getFileMessage() != null
          && !message.equals(chatMessage)) {
        message.getFileMessage().setPlay(false);
      }
    }
    LogUtils.e(TAG, "-->updateAudioState()....call to notifyDataSetChanged...");
    notifyDataSetChanged();
  }

  private void downloadAudio(ChatMessage chatMessage) {
    if (!Utility.isSDCardExist()) {
      mChatFragment.showDialogWhenEmptySDCard();
      return;
    }
    // otherwise
    String token = UserPreferences.getInstance().getToken();
    FileMessage fileMessage = chatMessage.getFileMessage();
    FileRequest request = new FileRequest(token, fileMessage.getFileId());
    DownloadFile downloadFile = new DownloadFile(mAppContext,
        request.toURL(), chatMessage, FILETYPE.AUDIO);
    // start download and add to audio list for refresh
    // later when received after download
    long fileId = downloadFile.startDownload();
    String msgId = chatMessage.getMessageId();
    mChatFragment.addAudioFileReceivedToList(msgId, fileId);
    mChatFragment.onDownloadStarted(fileId);
  }

  public void startPlayAudio(final ChatMessage chatMessage) {
    if (chatMessage == null || chatMessage.getFileMessage() == null) {
      return;
    }

    String filePath = chatMessage.getFileMessage().getFilePath();
    if (TextUtils.isEmpty(filePath)) {
      downloadAudio(chatMessage);
    } else {
      mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
      if (mMediaPlayer != null) {
        if (mMediaPlayer.isPlaying()) {
          mMediaPlayer.pause();
        }
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
      }
      if (mMediaPlayer == null) {
        mMediaPlayer = new MediaPlayer();
      }
      try {
        updateAudioState(chatMessage);
        mMediaPlayer
            .setOnCompletionListener(new OnCompletionListener() {
              @Override
              public void onCompletion(MediaPlayer mp) {
                chatMessage.getFileMessage().setPlay(false);
                String seconds = Utility.getTimeString(mp
                    .getCurrentPosition());
                TimeAudioHold timeHold = new TimeAudioHold(
                    seconds, 0);
                chatMessage.getFileMessage().setTimeAudioHold(
                    timeHold);
                LogUtils.e(TAG, "-->startPlayAudio()....call to notifyDataSetChanged...");
                notifyDataSetChanged();
                mHandlerElapsed
                    .removeCallbacks(mRunnableElapsed);
              }
            });
        if (mRunnableElapsed != null) {
          mHandlerElapsed.removeCallbacks(mRunnableElapsed);
          mRunnableElapsed = null;
        }

        mRunnableElapsed = new Runnable() {
          int prePosition = 0;

          @Override
          public void run() {
            if (mMediaPlayer != null) {
              int currentPosition = mMediaPlayer
                  .getCurrentPosition();
              LogUtils.i(TAG, "total position=" + currentPosition);
              if (prePosition == currentPosition) {
                mHandlerElapsed.postDelayed(this, 200);
                return;
              }
              prePosition = currentPosition;
              String seconds = Utility
                  .getTimeString(currentPosition);
              TimeAudioHold timeHold = new TimeAudioHold(seconds,
                  currentPosition);
              chatMessage.getFileMessage().setTimeAudioHold(
                  timeHold);
              LogUtils.e(TAG,
                  "-->mRunnableElapsed = new Runnable()....call to notifyDataSetChanged...");
              notifyDataSetChanged();
              mHandlerElapsed.postDelayed(this, 200);
            }
          }
        };
        mHandlerElapsed.post(mRunnableElapsed);
        mMediaPlayer.setDataSource(mActivity, Uri.parse(filePath));
        mMediaPlayer.prepare();
        mMediaPlayer
            .setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
              @Override
              public void onPrepared(MediaPlayer mp) {
                mp.start();
              }
            });
        mMediaPlayer.start();
        TimeAudioHold timeHold = chatMessage.getFileMessage()
            .getTimeAudioHold();
        int seekTime = timeHold.getCurrentPosition();
        if (mMediaPlayer.getDuration() > seekTime) {
          mMediaPlayer.seekTo(seekTime);
        }
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        stopPlayAudio(chatMessage);
        Toast.makeText(mAppContext, R.string.can_not_play_file,
            Toast.LENGTH_LONG).show();
      } catch (SecurityException e) {
        e.printStackTrace();
        stopPlayAudio(chatMessage);
        Toast.makeText(mAppContext, R.string.can_not_play_file,
            Toast.LENGTH_LONG).show();
      } catch (IllegalStateException e) {
        e.printStackTrace();
        stopPlayAudio(chatMessage);
        Toast.makeText(mAppContext, R.string.can_not_play_file,
            Toast.LENGTH_LONG).show();
      } catch (IOException e) {
        e.printStackTrace();
        stopPlayAudio(chatMessage);
        Toast.makeText(mAppContext,
            R.string.can_not_play_file_not_found, Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void removeMessage(String id) {
    Iterator<ChatMessage> it = listChatMessage.iterator();
    ChatMessage message;
    while (it.hasNext()) {
      message = it.next();
      if (message.getMessageId().equals(id)) {
        it.remove();
        LogUtils.e(TAG, "-->removeMessage()....call to notifyDataSetChanged...");
        notifyDataSetChanged();
        break;
      }
    }
  }

  public interface IOnGetVideoURL {

    public void onGetURL(ChatMessage message, String id, boolean isMy, boolean isExpired);

    public void onFilePath(String path);

    public void onGetURLError();
  }

  public interface IOnOpenImage {

    public void onImageClick(ChatMessage chatMessage, String imgId, boolean isMys, boolean isExpired);
  }
}