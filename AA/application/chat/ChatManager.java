package com.application.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.application.AndGApp;
import com.application.Config;
import com.application.constant.Constants;
import com.application.constant.NotificationSetting;
import com.application.entity.NotificationMessage;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.service.ApplicationNotificationManager;
import com.application.status.StatusConstant;
import com.application.status.StatusController;
import com.application.status.StatusDBManager;
import com.application.util.LogUtils;
import com.application.util.NotificationUtils;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.FriendPrefers;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.linphone.LinphoneService;
import vn.com.ntqsolution.chatclient.Client;
import vn.com.ntqsolution.chatclient.listeners.IAuthenListener;
import vn.com.ntqsolution.chatclient.listeners.ICMDListener;
import vn.com.ntqsolution.chatclient.listeners.ICallListener;
import vn.com.ntqsolution.chatclient.listeners.IChatListener;
import vn.com.ntqsolution.chatclient.listeners.IFileListener;
import vn.com.ntqsolution.chatclient.listeners.IGiftMessageListener;
import vn.com.ntqsolution.chatclient.listeners.IMessageStatusListener;
import vn.com.ntqsolution.chatclient.listeners.IPresenceListener;
import vn.com.ntqsolution.chatclient.listeners.IStickerListener;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class ChatManager {

  public static final String ACTION_AUTHENTICATION = "com.application.chat.authentication";
  public static final String ACTION_MESSAGE = "com.application.chat.message";
  public static final String ACTION_PRESENTATION = "com.application.chat.presentation";
  public static final String ACTION_MESSAGE_STATUS = "com.application.chat.status";
  public static final String ACTION_MESSAGE_READ = "com.application.chat.read";
  public static final String ACTION_MESSAGE_READ_ALL = "com.application.chat.read.all";
  public static final String ACTION_MESSAGE_WINK = "com.application.chat.wink";
  public static final String ACTION_MESSAGE_FILE = "com.application.chat.file";
  public static final String ACTION_MESSAGE_CMD = "com.application.chat.cmd";
  public static final String ACTION_SEND_FILE_ERROR = "com.application.chat.sendfile.error";
  public static final String ACTION_DOWNLOAD_FILE_ERROR = "com.application.chat.downloadfile.error";
  public static final String ACTION_MESSAGE_GIFT = "com.application.chat.gift";
  public static final String ACTION_MESSAGE_TYPING = "com.application.chat.typing";
  public static final String ACTION_MESSAGE_LOCATION = "com.application.chat.location";
  public static final String ACTION_MESSAGE_STICKER = "com.application.chat.sticker";
  public static final String ACTION_MESSAGE_CALL = "com.application.chat.call";
  public static final String ACTION_CUSTOM_NOTIFICATION = "com.itsherpa.andg.custom.notification";
  public static final String ACTION_MESSAGE_TERMINATE_CALL = "com.application.chat.terminatecall";
  public static final String ACTION_MESSAGE_UPDATE_FILE = "com.application.chat.update.msg.file";
  // local broadcast
  public static final String ACTION_LOCAL_MESSAGE = "local_message";
  public static final String ACTION_LOCAL_MESSAGE_WINK = "local_message_wink";
  public static final String ACTION_LOCAL_MESSAGE_FILE = "local_message_file";
  public static final String ACTION_LOCAL_MESSAGE_GIFT = "local_message_gift";
  public static final String ACTION_LOCAL_MESSAGE_LOCATION = "local_message_location";
  public static final String ACTION_LOCAL_MESSAGE_STICKER = "local_message_sticker";
  public static final String ACTION_LOCAL_MESSAGE_CALL = "local.chat.call";
  public static final String ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION = "local.chat.callrequest.conversation";
  public static final String ACTION_LOCAL_MESSAGE_TERMINATE_CALL = "local.chat.terminatecall";
  public static final String AUDIO = "a";
  public static final String VIDEO = "v";
  public static final String PHOTO = "p";
  public static final String START_TYPING = "wt";
  public static final String STOP_TYPING = "sw";
  public static final String EXTRA_DATA = "com.application.chat.data";
  public static final String EXTRA_BUNDLE = "com.application.chat.bundle";
  private static final String TAG = "ChatManager";
  private static final ChatManager mChatManager = new ChatManager();
  private LocalBroadcastManager mLocalBroadcastManager;
  private Client mChatClient;

  //  boolean isCheck=true;
//  private Message saveMess;
  private String userId;
  private String token;
  private Context mContext;
  private IShowDialog showDialog;
  private ScheduledExecutorService heartBeatSchedule;
  private IChatListener iChatListener = new IChatListener() {

    @Override
    public void handle(Message message) {
      LogUtils.i(TAG, "Value when addChatListener=" + message.value
          + ",msgType" + message.msgType);
      boolean canSend = sendBroadcastMessage(ACTION_MESSAGE, new MessageClient(message));
      if (canSend) {
        requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_TEXT);
      }
    }
  };
  private IPresenceListener iPresenceListener = new IPresenceListener() {

    @Override
    public void handle(Message message) {
      if (message.value.equalsIgnoreCase(START_TYPING)
          || message.value.equalsIgnoreCase(STOP_TYPING)) {
        LogUtils.i(TAG, "Value iPresenceListener=" + message.value
            + ",msgType" + message.msgType + " value="
            + message.value);
        sendBroadcastMessage(ACTION_MESSAGE_TYPING, new MessageClient(
            message));
      } else {
        sendBroadcastMessage(ACTION_PRESENTATION,
            new PresentationMessage(message));
      }
    }
  };
  private IAuthenListener iAuthenListener = new IAuthenListener() {

    @Override
    public void handle(Message message) {
      LogUtils.i(TAG, "Value when authentication=" + message.value);
      // sendBroadcastMessage(ACTION_AUTHENTICATION,
      // new AuthenticationMessage(message));
    }
  };
  private IFileListener iFileListener = new IFileListener() {

    @Override
    public void handle(Message message) {
      FileMessage fileMessage = new FileMessage(message);
      boolean canSend = sendBroadcastMessage(ACTION_MESSAGE_FILE, fileMessage);
      boolean isConfirmMessage = ChatUtils.isConfirmMessage(message.value);

      LogUtils.d(TAG, "Message file(iFileListener).message id=" + message.id + " from="
          + message.from + " to=" + message.to + " value="
          + message.value + "|canSend=" + canSend + "|isConfirmMessage=" + isConfirmMessage);

      if (canSend && isConfirmMessage) {
        if (fileMessage.getFileType().equalsIgnoreCase(VIDEO)) {
          requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_VIDEO);
        } else if (fileMessage.getFileType().equalsIgnoreCase(PHOTO)) {
          requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_PHOTO);
        } else if (fileMessage.getFileType().equalsIgnoreCase(AUDIO)) {
          requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_AUDIO);
        }
      }
    }
  };
  private ICMDListener iCMDListener = new ICMDListener() {

    @Override
    public void handle(Message message) {
      LogUtils.d(TAG,
          "Message CMD(iCMDListener): from=" + message.from + " to=" + message.to + "|msgId="
              + message.id + " value=" + message.value);

      String patternBlock = "\\Ablock&";
      String patternUnblock = "\\Aunblock&";
      String patternVideoOn = "\\Avoip_video_on";
      String patternVideoOff = "\\Avoip_video_off";

      if (getValuePingMessage().equals(message.value)) {
        // Start Call Service
        LinphoneService.startLogin(mContext);
        return;
      }

      if (mContext.getString(R.string.cmd_terminate_call).equals(
          message.value)) {
        sendBroadcastMessage(ACTION_LOCAL_MESSAGE_TERMINATE_CALL,
            new CMDMessage(message));
        return;
      }

      boolean match = false;

      do {
        Pattern ppp = Pattern.compile(patternBlock, Pattern.DOTALL);
        Matcher m = ppp.matcher(message.value);

        if (m.find()) {
          BlockUserPreferences.getInstance().insertBlockedUser(
              message.from);
          match = true;
          break;
        }

        ppp = Pattern.compile(patternUnblock, Pattern.DOTALL);
        m = ppp.matcher(message.value);
        if (m.find()) {
          BlockUserPreferences.getInstance().removeBlockedUser(
              message.from);
          match = true;
          break;
        }

        ppp = Pattern.compile(patternVideoOn, Pattern.DOTALL);
        m = ppp.matcher(message.value);
        if (m.find()) {
          match = true;
          break;
        }

        ppp = Pattern.compile(patternVideoOff, Pattern.DOTALL);
        m = ppp.matcher(message.value);
        if (m.find()) {
          match = true;
          break;
        }
      } while (false);

      if (match) {
        sendBroadcastMessage(ACTION_MESSAGE_CMD,
            new CMDMessage(message));
      }
    }
  };
  private IGiftMessageListener iGiftListener = new IGiftMessageListener() {

    @Override
    public void handle(Message message) {
      LogUtils.d(TAG, "Message CMD(iGiftListener): from=" + message.from + " to="
          + message.to + " value=" + message.value);

      boolean canSend = sendBroadcastMessage(ACTION_MESSAGE_GIFT, new MessageClient(message));
      if (canSend) {
        requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_GIFT);
      }
    }
  };
  private IStickerListener iStickerListener = new IStickerListener() {
    @Override
    public void handle(Message message) {
      LogUtils.d(TAG,
          "Message CMD(iStickerListener): from=" + message.from + " to=" + message.to + "|msgId="
              + message.id + " value=" + message.value);

      boolean canSend = sendBroadcastMessage(ACTION_MESSAGE_STICKER, new MessageClient(message));
      if (canSend) {
        requestUserInfo(message.from, Constants.LOCK_KEY_CHAT_LOC_MESSAGE_STICKER);
      }
    }
  };
  private ICallListener iCallListener = new ICallListener() {
    @Override
    public void handle(Message message) {
      // Check whether or not this message it [Start Voice Call] | [Start
      // Video Call].
      LogUtils.e(TAG,
          "Message CMD:(iCallListener)messageID=" + message.id + "|from=" + message.from + " to="
              + message.to + " value=" + message.value);

      if (message.msgType == MessageType.SVIDEO || message.msgType == MessageType.SVOICE) {
        // Save message id to Preferences in case of not in calling
        // or in calling with the user that sent this message.
        UserPreferences userPreferences = UserPreferences.getInstance();
        if (!userPreferences.getInCallingProcess()) {
          userPreferences.setStartedCallMessageId(message.id);
        } else {
          String callingUserId = userPreferences.getCallingUserId();
          if (message.from.equals(callingUserId)) {
            userPreferences.setStartedCallMessageId(message.id);
          }
        }
      }

      sendBroadcastMessage(ACTION_MESSAGE_CALL, new MessageClient(message));
    }
  };
  private IMessageStatusListener iMessageStatusListener = new IMessageStatusListener() {

    @Override
    public void handle(Message msg) {
      MessageStatus status = new MessageStatus(msg);

      LogUtils.e(TAG,
          "Message CMD(iMessageStatusListener): from=" + msg.from + " to=" + msg.to + " value="
              + msg.value + "|Robert.isReadALL()=" + status.isReadALL() + "|isSentSuccess()="
              + status.isSentSuccess() + "|isReaded()=" + status.isReaded() + msg.toString());

      if (status.isReadALL()) {
        sendBroadcastMessage(ACTION_MESSAGE_READ_ALL, status);
        return;
      }
      if (!status.isReaded()) {
        sendBroadcastMessage(ACTION_MESSAGE_STATUS, status);
      } else {
        sendBroadcastMessage(ACTION_MESSAGE_READ, status);
      }

      int point = status.getPoint();
      if (point > 0) {
        UserPreferences.getInstance().saveNumberPoint(point);
      }
//      StatusController.getInstance(mContext).updateMsg(status);
//      if (!status.isSentSuccess()) {
//        if (status.isNotEnoughPoint() ) {
//          if(!status.isSendFaildedMess2()){
//            if (showDialog != null) {
//              // Tuan remove point ticket #10747
//              showDialog.showNotEnoughPoint(status.getPoint());
//            }
//          }
//        }
//      }

      StatusController.getInstance(mContext).updateMsg(status);
      if (!status.isSentSuccess()) {
        if (status.isNotEnoughPoint()) {
          if (showDialog != null) {
            showDialog.showNotEnoughPoint(point);
          }
        }
      }

      // #13156-THANGPQ-update status of message in datatabase when socket get not enough point
//      String value = msg.value;
//      String statusOfMess="";
//      String split [] = value.split("\\|");
//      if(split!=null && split.length>=2){
//        statusOfMess = split[1];
//        if( statusOfMess.equals(MessageStatus.NOT_ENOUGH_POINT_DATA)){
//          if (showDialog != null) {
//            showDialog.showNotEnoughPoint(status.getPoint());
//          }
//          StatusDBManager.getInstance(mContext).updateStatus(saveMess.id,StatusConstant.STATUS_ERROR);
//        }
//      }
      LogUtils.d(TAG,
          String.format("Message status, value=[%s]", msg.value));
    }
  };

  private ChatManager() {
    mContext = AndGApp.get();
    mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
  }

  public static ChatManager getInstance(Context context) {
    // mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    // mContext=context;
    return mChatManager;
  }

  public IShowDialog getShowDialog() {
    return showDialog;
  }

  public void setShowDialog(IShowDialog showDialog) {
    this.showDialog = showDialog;
  }

  private synchronized void initChatMagager() throws SocketException {
    if (mChatClient != null) {
      mChatClient.dispose();
      mChatClient = null;
    }
    mChatClient = new Client(Config.CHAT_SERVER_IP, Config.CHAT_SERVER_PORT);
    LogUtils.i(TAG, "initChatMagager()=" + mChatClient.toString());
    mChatClient.addAuthenListener(iAuthenListener);
    mChatClient.addChatListener(iChatListener);
    mChatClient.addPresenceListener(iPresenceListener);
    mChatClient.addFileListener(iFileListener);
    mChatClient.addCMDListener(iCMDListener);
    mChatClient.addGiftListener(iGiftListener);
    mChatClient.addStickerListener(iStickerListener);
    mChatClient.addCallListener(iCallListener);
    mChatClient.addMessgeStatusListener(iMessageStatusListener);
//    return mChatClient;
  }

  public boolean sendBroadcastMessage(String action, MessageClient messageClient) {
    if (mLocalBroadcastManager == null || messageClient == null) {
      LogUtils.e(TAG, String.format(
          "mLocalBroadcastManager is %s,  messageClient is %s",
          mLocalBroadcastManager, messageClient));
      return false;
    }

    Intent intent = new Intent(action);
    intent.putExtra(ChatManager.EXTRA_DATA, messageClient);

//    Message msg = messageClient.getMessage();
//    if (!action.equals(ACTION_MESSAGE_CMD)
//        && Utility.isBlockedWithUser(mContext, msg.from)) {
//      return false;
//    }

    mLocalBroadcastManager.sendBroadcast(intent);
    return true;
  }

  /**
   * Request basic user info and push notification
   *
   * @param userId is user send.
   * @param loc_key is type message.
   */
  private void requestUserInfo(String userId, String loc_key) {
    UserPreferences preferences = UserPreferences.getInstance();
    int type = preferences.getChatNotificationType();
    if (type == NotificationSetting.NOTIFY_CHAT_NONE) {
      return;
    } else if (type == NotificationSetting.NOTIFY_CHAT_ONLY_FRIEND_FAV) {
      FavouritedPrefers favouritedPrefers = FavouritedPrefers
          .getInstance();
      FriendPrefers friendPrefers = new FriendPrefers();
      // if not Fav AND not friends => return (not notify)
      if (!favouritedPrefers.hasContainFav(userId)
          && !friendPrefers.hasContainFriend(userId)) {
        return;
      }
    }
    String userIdToSend = preferences.getCurentFriendChat();
    if (AndGApp.isApplicationVisibile() && !TextUtils.isEmpty(userIdToSend)
        && userId.equals(userIdToSend)) {
      return;
    }

    String notification = mContext.getString(R.string.notification_username_default);
    handleNotification(notification, userId, loc_key, Constants.NOTI_CHAT_TEXT);
  }

  private void handleNotification(String userName, String userId, String lockey, int notiType) {
    UserPreferences preferences = UserPreferences.getInstance();
    // push notification here
    String message = "{\"alert\":{\"loc-args\":[\"" + userName + "\"],\"loc-key\":\"" + lockey
        + "\"},\"data\":{\"userid\":\"" + userId + "\",\"noti_type\":" + notiType + "}}";
    NotificationMessage notiMessage = new NotificationMessage(message);
    ApplicationNotificationManager notificationManager = new ApplicationNotificationManager(
        mContext);

    // if app isn't in forebackgroup
    if (!AndGApp.isApplicationVisibile()) {
      // if calling not show notification
      if (preferences.getInCallingProcess()) {
        return;
      }
      notificationManager.showNotification(notiMessage);
      // if app is in forebackgroup and not in Chat screen
    } else if (TextUtils.isEmpty(preferences.getCurentFriendChat())) {
      sendBroadcastReceiveMessage(notiMessage);
    } else if (!preferences.getCurentFriendChat().equals(userId)) {
      NotificationUtils.playNotificationSound(mContext);
      NotificationUtils.vibarateNotification(mContext);
    }
  }

  public void sendBroadcastReceiveMessage(NotificationMessage message) {
    Intent intent = new Intent(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intent.putExtra(WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE, message);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  /**
   * Use to send authen message, must start ChatAsynTask because {@code Client} only initilized in
   * out Main thread.
   */
  public void sendAuthenticationMessage() {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          UserPreferences preferences = UserPreferences.getInstance();
          String userId = preferences.getUserId();
          UserPreferences userPreferences = UserPreferences.getInstance();
          String token = userPreferences.getToken();
          sendInternalAuthenticationMessage(userId,token);

        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();

  }
  private void sendInternalAuthenticationMessage(String userId, String token) {
    LogUtils.i(TAG, "send Authentication Message (1)");
    // if userId is empty do not internal authentication
    if (TextUtils.isEmpty(userId)) {
      LogUtils.w(TAG, "send Authentication Message is error when userId null");
      return;
    }

    if (mChatClient == null) {
      try {
        initChatMagager();
      } catch (SocketException e) {
        e.printStackTrace();
      }
    }

    LogUtils.i(TAG, "send Authentication Message (2)");
    LogUtils.i(TAG, "UserId=" + userId);
    LogUtils.i(TAG, "Token=" + token);
    LogUtils.i(TAG, Utility.getDateTimeInGMT() + "");
    Message message = makeAuthenMessage(userId, token);
    boolean isSuccess = mChatClient.sendMessage(message);
    if (isSuccess) {
      startHeartBeat(userId);
    }
  }

  private synchronized void startHeartBeat(String userId) {
    LogUtils.i(TAG, "startHeartBeat");
    if (heartBeatSchedule == null) {
      // 1 thread run on scheduledThreadPool.
      heartBeatSchedule = new ScheduledThreadPoolExecutor(1);
      heartBeatSchedule.scheduleWithFixedDelay(() -> sendHeartBeatMessage(userId), Constants.DELAY_INIT_TIME_HEARTBEAT, Constants.DELAY_TIME_HEARTBEAT, TimeUnit.SECONDS);
    }
  }

  public void sendHeartBeatMessage(String userId) {
    LogUtils.i(TAG, "sendHeartBeatMessage");
    Date date = Utility.getDateTimeInGMT();
    LogUtils.i(TAG, date + "");
    Message message = makeHeartBeatMessage(userId, date);
    try {
      sendReadMessage(message);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public Message makeHeartBeatMessage(String userId, Date date) {
    return new Message(date, userId, "server", MessageType.CMD, "heartbeat");
  }

  private synchronized void stopHeartBeat() {
    LogUtils.d(TAG, "stopHeartBeat");
    if (heartBeatSchedule != null) {
      heartBeatSchedule.shutdown();
      heartBeatSchedule = null;
    }
  }
  /**
   * Only use internal of this class.
   */


  public void sendGenericMessage(final Date date, final String from,
      final String to, final String value) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeGenericMessage(date, from, to, value));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendGenericMessage(final Message message) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          sendMessage(message);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendWinkMessage(final String from, final String to) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeWinkMessage(from, to));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendBlockMessage(final String from, final String to) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeBlockMessage(from, to));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };

    new Thread(runnable).start();
  }

  public void sendUnblockMessage(final String from, final String to) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeUnblockMessage(from, to));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };

    new Thread(runnable).start();
  }

  public void sendConfirmSentFileMessage(final String msgStartId, final String from,
      final String to, final String fileType, final String fileId,
      final String fileName) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          Message message = makeConfirmSentFileMessage(msgStartId, from, to, fileType, fileId,
              fileName);
          LogUtils.e(TAG,
              "===>ChatManager.sendConfirmSentFileMessage(final String msgStartId, final String from,final String to, final String fileType, final String fileId,final String fileName)messageId="
                  + message.id + "|msgStartId=" + msgStartId);
          message.id = msgStartId;

          sendMessage(message);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendConfirmSentFileMessage(final Date date, final String from,
      final String to, final String fileType, final String fileId,
      final String fileName) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeConfirmSentFileMessage(date, from, to,
              fileType, fileId, fileName));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public String sendStartSentMediaMessage(final Date date, final String from,
      final String to, final String type,
      final IStartSentMediaMessage callBack) {
    final Message message = makeMediaMessage(date, from, to, type);
    Runnable runnable = () -> {
      try {
        boolean isSuccess = sendMessage(message);
        if (callBack != null) {
          callBack.onSentResult(isSuccess, message);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    };
    new Thread(runnable).start();
    return message.id;
  }
  public String sendStartSentMediaMessage(String messId,final Date date, final String from,
      final String to, final String type,
      final IStartSentMediaMessage callBack) {
    final Message message = makeMediaMessage(date, from, to, type);
    message.id = messId;
    Runnable runnable = () -> {
      try {
        boolean isSuccess = sendMessage(message);
        if (callBack != null) {
          callBack.onSentResult(isSuccess, message);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    };
    new Thread(runnable).start();

    return message.id;
  }
  public void sendStartTypingMessage(final String from, final String to) {
    Runnable runnable = () -> {
      try {
        sendMessage(makeTypingMessage(from, to, START_TYPING));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    };
    new Thread(runnable).start();

  }

  public void sendStopTypingMessage(final String from, final String to) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeTypingMessage(from, to, STOP_TYPING));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendGiftMessage(final String from, final String to,
      final String value) {

    Message message = makeGiftMessage(from, to, value);
    sendGiftMessage(message);
  }

  public void sendGiftMessage(final Message message) {

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          boolean check = false;
          int numSend = 1;
          int totalSend = 3;
          do {
            message.printStruct();
            if (mChatClient != null) {
              check = mChatClient.sendMessage(message);
              if (check) {
                sendBroadcastMessage(ACTION_MESSAGE_GIFT, new MessageClient(message));
                return;
              }
              try {
                Thread.sleep(300);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            numSend++;
          } while (numSend <= totalSend);
          //check while send gift, error network
          if (!check) {
            StatusController.getInstance(mContext).createMessage(message);
            Date date = Utility.getDateTimeInGMT();
            String time = Utility.getTimeStamp(date);
            String messageId = message.from + "&" + message.to + "&" + time;
            StatusDBManager.getInstance(mContext)
                .updateStatus(messageId, StatusConstant.STATUS_ERROR);
          }


        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendLocationMessage(final Message message) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(message);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendStickerMessage(final String from, final String to,
      final String value) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeStickerMessage(from, to, value));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public String sendCallMessage(final String from, final String to,
      final boolean isVoiceCall, final boolean isStartCall,
      final String value) {
    final Message msg = makeCallMessage(from, to, isVoiceCall, isStartCall,
        value);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          LogUtils.e(TAG,
              "sendCallMessage().msgId=" + msg.id + "|msgType=" + msg.msgType + "|from=" + msg.from
                  + "|to=" + msg.to + "|value=" + msg.value);
          sendMessage(msg);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();

    return msg.id;
  }

  public String sendCallingMessage(String from, String to, String value) {
    final Message msg = makeCallingMessage(from, to, value);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          LogUtils.e(TAG,
              "sendCallingMessage().msgId=" + msg.id + "|msgType=" + msg.msgType + "|from="
                  + msg.from + "|to=" + msg.to + "|value=" + msg.value);

          sendMessage(msg);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();

    return msg.id;
  }

  public void sendVideoOnOffMessage(final String from, final String to,
      final String value) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          sendMessage(makeVideoOnOffMessage(from, to, value));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendCallPingMessage(final String from, final String to) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          sendMessage(makeCallPingMessage(from, to));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  public void sendTerminateCallByRealCall(final String from, final String to) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          sendMessage(makeTerminateByRealCallMessage(from, to));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };

    new Thread(runnable).start();
  }

  private Message makeConfirmSentFileMessage(String msg1id, final String from,
      final String to, String fileType, String fileId, String fileName) {
    StringBuilder builder = new StringBuilder();
    builder.append(msg1id);
    builder.append("|");
    builder.append(fileType);
    builder.append("|");
    builder.append(fileId);
    builder.append("|");
    builder.append(fileName);
    String value = builder.toString();

    // date send confirm
    Date dateSendConfirm = Utility.getDateTimeInGMT();
    return new Message(dateSendConfirm, from, to, MessageType.FILE, value);
  }

  private Message makeConfirmSentFileMessage(Date date, final String from,
      final String to, String fileType, String fileId, String fileName) {
    String d = Utility.getTimeStamp(date);
//    StringBuilder builder = new StringBuilder();
//    builder.append(from);
//    builder.append("&");
//    builder.append(to);
//    builder.append("&");
//    builder.append(d);
//    builder.append("|");
//    builder.append(fileType);
//    builder.append("|");
//    builder.append(fileId);
//    builder.append("|");
//    builder.append(fileName);
//    String value = builder.toString();
    String value = from +
        "&" +
        to +
        "&" +
        d +
        "|" +
        fileType +
        "|" +
        fileId +
        "|" +
        fileName;


    // date send confirm
    Date dateSendConfirm = Utility.getDateTimeInGMT();
    return new Message(dateSendConfirm, from, to, MessageType.FILE, value);
  }

  /**
   * Note: <b>Can not place in UI thread.</b> </br> </br> Try sent one message 3 times if sent
   * failure. If Client hasn't been initialized -> initialize it again then after sent message
   * success call {@code dispose()} (This is abnormal case when sent file, client has been destroyed
   * before confirm message has been sent)
   */

  private boolean sendMessage(Message message) throws Exception {
    int numSend = 1;
    int totalSend = 3;
    boolean hasInitialized = false;
    StatusController.getInstance(mContext).createMessage(message);
    boolean isSuccess = false;
    do {
      LogUtils.v(TAG, "sendMessage().Send " + " #turn=" + numSend + "|messageId=" + message.id
          + "|message.value=" + message.value + "|message.msgType=" + message.msgType
          + "|(mChatClient != null)=" + (mChatClient != null ? " NOT NULL.Socket.isConnected=" + (
          mChatClient.soc != null ? mChatClient.soc.isConnected() : " FALSE ") : " IS NULL "));

      message.printStruct();
      if (mChatClient != null) {
        isSuccess = mChatClient.sendMessage(message);
        if (isSuccess) {
          LogUtils.i(TAG,
              "Send message " + message.id + ", " + message.value + " success|currentUser="
                  + message.from + "|userToSend=" + message.to + "|value=" + message.value);

          StringBuilder value = new StringBuilder();
          value.append(message.id).append("|")
              .append(MessageStatus.SUCCESS_DATA).append("|")
              .append("0");
          message.value = value.toString();
          MessageStatus status = new MessageStatus(message);
          StatusController.getInstance(mContext).updateMsg(status);
          if (hasInitialized) {
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            /**
             * TODO Updated by Robert on 01 Apr 2017 about tickets
             * START - http://10.64.100.201/issues/8307
             */
            if (message.msgType != MessageType.FILE) {
              mChatClient.dispose();
            }
            // END - http://10.64.100.201/issues/8307

            hasInitialized = false;
          }
          return true;
        } else if (numSend == totalSend - 1) {
          LogUtils.v(TAG,
              "sendMessage().(numSend == totalSend - 1) " + " #turn=" + numSend + "|isSuccess="
                  + isSuccess + "|must retry init chat after totalSend-1 time retry send");

          // must retry init chat after totalSend-1 time retry send
          // (for lost connection and has connection again)
          initChatMagager();
          sendInternalAuthenticationMessage(userId,token);
        } else if (numSend == totalSend) {
          LogUtils.v(TAG,
              "sendMessage().(numSend == totalSend) " + " #turn=" + numSend + "|isSuccess="
                  + isSuccess + "|StatusController.getInstance(mContext).requestTimeout()");

          StatusController.getInstance(mContext).requestTimeout();
        }
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        LogUtils.v(TAG, "sendMessage().(mChatClient == null) " + " #turn=" + numSend + "|isSuccess="
            + isSuccess);

        hasInitialized = true;
        initChatMagager();
        try {
          sendInternalAuthenticationMessage(userId,token);
          Thread.sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      numSend++;
    } while (numSend <= totalSend);
    if (!isSuccess) {
      LogUtils.d(StatusConstant.TAG, "send message " + message.id
          + " failed");
      StatusController.getInstance(mContext).updateErrorBySocketDie(
          message);
    }
    if (hasInitialized && mChatClient != null) {
      mChatClient.dispose();
    }
    return isSuccess;
  }

  private boolean sendReadMessage(Message message) throws Exception {
    int numSend = 1;
    int totalSend = 3;
    boolean hasInitialized = false;
    boolean isSuccess = false;
    // neu khong gui duoc thi gui lai message
    do {
      if (mChatClient != null) {
        isSuccess = mChatClient.sendMessage(message);
        if (isSuccess) {
          LogUtils.i(StatusConstant.TAG, "MessageID: " + message.id);
          LogUtils.i(StatusConstant.TAG, "Value: " + message.value);
          LogUtils.i(TAG, "Sender: " + message.from);
          LogUtils.i(TAG, "Receiver: " + message.to);
          LogUtils.i(TAG, "Status: success");

          if (hasInitialized) {
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            mChatClient.dispose();
            hasInitialized = false;
          }
          return true;
        } else if (numSend == totalSend - 1) {
          // must retry init chat after totalSend-1 time retry send
          // (for lost connection and has connection again)
          initChatMagager();
          sendInternalAuthenticationMessage(userId,token);
        } else if (numSend == totalSend) {
          LogUtils.d(TAG, "send read message request time out");
        }
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        hasInitialized = true;
        initChatMagager();
        try {
          sendInternalAuthenticationMessage(userId,token);
          Thread.sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      numSend++;
    } while (numSend < totalSend);
    if (hasInitialized && mChatClient != null) {
      mChatClient.dispose();
    }
    return false;
  }

  public void sendReadMessage(final String from, final String to,
      final String msgId) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          sendReadMessage(makeReadMessage(from, to, msgId));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };

    new Thread(runnable).start();
  }

  private Message makeReadMessage(String from, String to, String msgId) {
    Date date = Utility.getDateTimeInGMT();
    String value = msgId + "|" + MessageStatus.READED_DATA;
    Message message = new Message(date, from, to, MessageType.MDS, value);
    message.id = msgId;
    return message;
  }

  private Message makeAuthenMessage(String userId, String token) {
    Date date = Utility.getDateTimeInGMT();
    return new Message(date, userId, "server", MessageType.AUTH, token);
  }

  private Message makeGenericMessage(Date date, String from, String to,
      String value) {
    // Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.PP, value);
    LogUtils.i(TAG, "OriginTime=" + message.originTime);
    LogUtils.i(TAG, "DateTime=" + date.toString());
    return message;
  }

  private Message makeCallRequestMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.CALLREQ,
        value);
    return message;
  }

  private Message makeWinkMessage(String from, String to) {
    Date date = Utility.getDateTimeInGMT();
    return new Message(date, from, to, MessageType.WINK, "");
  }

  private Message makeBlockMessage(String from, String to) {
    Date date = Utility.getDateTimeInGMT();
    String value = "block&" + to;
    return new Message(date, from, to, MessageType.CMD, value);
  }

  private Message makeUnblockMessage(String from, String to) {
    Date date = Utility.getDateTimeInGMT();
    String value = "unblock&" + to;
    return new Message(date, from, to, MessageType.CMD, value);
  }

  private Message makeTerminateByRealCallMessage(String from, String to) {
    Date date = Utility.getDateTimeInGMT();
    String value = mContext.getString(R.string.cmd_terminate_call);
    return new Message(date, from, to, MessageType.CMD, value);
  }

  private Message makeMediaMessage(Date date, String from, String to,
      String type) {
    Message message = new Message(date, from, to, MessageType.FILE, type);
    return message;
  }

  private Message makeTypingMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.PRC, value);
    return message;
  }

  public Message makeGiftMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.GIFT, value);
    return message;
  }

  private Message makeStickerMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.STK, value);
    return message;
  }

  private Message makeCallMessage(String from, String to,
      boolean isVoiceCall, boolean isStartCall, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = null;
    if (isVoiceCall) {
      if (isStartCall) {
        message = new Message(date, from, to, MessageType.SVOICE, value);
      } else {
        message = new Message(date, from, to, MessageType.EVOICE, value);
      }
    } else {
      if (isStartCall) {
        message = new Message(date, from, to, MessageType.SVIDEO, value);
      } else {
        message = new Message(date, from, to, MessageType.EVIDEO, value);
      }
    }
    return message;
  }

  private Message makeCallingMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.CMD, value);
    return message;
  }

  private Message makeVideoOnOffMessage(String from, String to, String value) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.CMD, value);
    return message;
  }

  private Message makeCallPingMessage(String from, String to) {
    Date date = Utility.getDateTimeInGMT();
    Message message = new Message(date, from, to, MessageType.CMD,
        getValuePingMessage());
    return message;
  }

  private String getValuePingMessage() {
    return "ping";
  }

  public void clearChat() {
    if (mChatClient != null) {
      LogUtils.i(TAG, "chat dispose ");
      mChatClient.dispose();
      mChatClient = null;
    }
  }

  public interface IShowDialog {

    void showNotEnoughPoint(int point);
  }

  public interface IStartSentMediaMessage {

    void onSentResult(boolean isSuccess, Message message);
  }
}