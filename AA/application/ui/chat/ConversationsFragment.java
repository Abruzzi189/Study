package com.application.ui.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.chat.ChatManager;
import com.application.chat.ChatMessage;
import com.application.chat.MessageClient;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.ConversationResponse;
import com.application.connection.response.DeleteConversationResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.GetTotalUnreadMesageResponse;
import com.application.connection.response.MarkReadsResponse;
import com.application.entity.ConversationItem;
import com.application.event.ConversationEvent;
import com.application.event.NewMessageEvent;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.ui.BaseFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.MainActivity;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.profile.AccountStatus;
import com.application.ui.viewholders.ConversationsViewHolder;
import com.application.ui.viewholders.IOnActionConversations;
import com.application.util.ConversationComparator;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

/**
 * Created by Namit on 4/4/2016.
 */
public class ConversationsFragment extends BaseFragment implements ResponseReceiver,
    IOnActionConversations {

  public static final String TAG_FRAGMENT_CHAT = "chat";
  public static final int LOADER_CONVERSATION = 0;
  public static final int LOADER_ID_BASIC_USER_INFO = 1;
  /* Declare Variables */
  public static final int LOADER_DELETE_CONVERSATION = 2;
  public static final int LOADER_ID_TOTAl_UNREAD_MSG = 3;
  public static final int LOADER_ID_MARK_AS_READ = 4;
  public final static int LOADER_ID_CHECK_UNLOCK = 5;
  public final static int LOADER_ID_USER_INFO_CALL = 11;
  public final static int LOADER_GET_UPDATE_INFO_FLAG = 8;
  public static final int TAKE = 20;
  public static final String ACTION_STREAMING_ERROR = "andg.main.streaming.error";
  //****************************************************************
  //* Variables
  //****************************************************************
  private static final String TAG = ConversationsFragment.class.getName();
  private ConversationsViewHolder viewHolder;
  private boolean isRequestDeleteAll = false;
  private String[] listMarkAsReadUser = null;
  private String mTimeSpan = "";
  private BroadcastReceiver mBroadcastReceiver;
  private boolean isHasOnConversationList = false;
  private String mNewMessage;
  private String mNewDate;
  private Message mMessage;
  private AlertDialog dialogCanNotStreaming;
  private String messageType = ChatMessage.PP;
  private String messageFromUserId;


  private MainActivity mainActivity;

  //****************************************************************
  //* Construct
  //****************************************************************
  public static ConversationsFragment newInstance() {
    ConversationsFragment fragment = new ConversationsFragment();
    return fragment;
  }

  //****************************************************************
  //* Life cycle
  //****************************************************************
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainActivity = (MainActivity) activity;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mActionBar.syncActionBar(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_conversations,
        container, false);
//        initViews(rootView);
    viewHolder = new ConversationsViewHolder(rootView, mainActivity, this);
//        initVariables();
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    try {
      viewHolder.onStart();
    } catch (Exception e) {
      e.printStackTrace();
    }
    registerReceiveMessage();
  }

  @Override
  public void onResume() {
    EventBus.getDefault().registerSticky(this);
    super.onResume();
    try {
      viewHolder.onResume();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPause() {
    EventBus.getDefault().unregister(this);
    try {
      viewHolder.onPause();
    } catch (Exception e) {
      e.printStackTrace();
    }
    unregisterReceiveMessage();
    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    try {
      viewHolder.onSaveInstanceState();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroy() {
    try {
      viewHolder.onDestroy();
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.onDestroy();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    try {
      viewHolder.onDestroyView();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleGetBasicInfoResponse(GetBasicInfoResponse response) {
    LogUtils.d(TAG, "handleGetBasicInfoResponse Started");
    if (response.getCode() == Response.SERVER_SUCCESS) {
      // Add to list conversation
      ConversationItem item = new ConversationItem();
      item.setAvaId(response.getAvataId());
      item.setFriendId(response.getUserId());
      item.setLastMessage(mNewMessage);
//            item.setSentTime(String.valueOf(mMessage.originTime));
      item.setSentTime(String.valueOf(mMessage.serverTime));
      item.setLattitude(response.getLatitude());
      item.setLongtitude(response.getLongitude());
      item.setOnline(response.isOnline());
      item.setMessageType(messageType);
      item.setDistance(response.getDistance());

      UserPreferences userPreferences = UserPreferences.getInstance();
      String currentUserId = userPreferences.getUserId();
      if (mMessage.from.equalsIgnoreCase(currentUserId)) {
        item.setUnreadNum(0);
      } else {
        String currentUserIdToSend = userPreferences
            .getCurentFriendChat();
        if (currentUserIdToSend != null
            && currentUserIdToSend.equals(mMessage.from)) {
          item.setUnreadNum(0);
        } else {
          item.setUnreadNum(1);
        }
      }

      item.setName(response.getUserName());
      item.setGender(response.getGender());
      if (currentUserId.equals(messageFromUserId)) {
        item.setOwn(true);
      } else {
        item.setOwn(false);
      }

      boolean hasList = false;
      //TODO
      viewHolder.onGetBasicInfoResponseConversation(item, hasList);

    } else if (response.getCode() == Response.SERVER_BLOCKED_USER) {
      // NOP
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "handleGetBasicInfoResponse Ended");
  }

  //****************************************************************
  //* Function
  //****************************************************************

  /* Action hoder */
  @Override
  public void onRestartRequestServer(int loaderID, int requestType, RequestParams data)
      throws Exception {
    restartRequestServer(loaderID, requestType, data);
  }

  //****************************************************************
  //* Event
  //****************************************************************

  //****************************************************************
  //* Interface
  //****************************************************************

  @Override
  public void onRestartRequestServer(int loaderID, RequestParams data) throws Exception {
    restartRequestServer(loaderID, data);
  }

  @Override
  public void onRestartRequestServer(int loaderID, RequestParams data, int timeoutConnect,
      int timeoutRead) throws Exception {
    restartRequestServer(loaderID, data, timeoutConnect, timeoutRead);
  }

  @Override
  public boolean isCurrentChatAnonymous() {
    return mainActivity.isCurrentChatAnonymous();
  }

  @Override
  public boolean isChatAnonymousEmpty() throws Exception {
    return mainActivity.isChatAnonymousEmpty();
  }

  @Override
  public String onHiddenUserId() throws Exception {
    return mainActivity.mHiddenUserId;
  }

  @Override
  public String onGetTimeSpan() throws Exception {
    return mTimeSpan;
  }

  @Override
  public void onSetTimeSpan(String timeSpan) throws Exception {
    this.mTimeSpan = timeSpan;
  }

  @Override
  public int onRemain() throws Exception {
    return mainActivity.mRemain;
  }

  @Override
  public String[] onGetListMarkAsReadUser() throws Exception {
    return listMarkAsReadUser;
  }

  @Override
  public void onSetListMarkAsReadUser(String[] strings) throws Exception {
    this.listMarkAsReadUser = strings;
  }

  @Override
  public void onRequestTotalUnreadMsg() {
    mainActivity.requestTotalUnreadMsg();
  }

  @Override
  public boolean isRequestDeleteAll() {
    return isRequestDeleteAll;
  }

  @Override
  public void setRequestDeleteAll(boolean isRequestDeleteAll) {
    this.isRequestDeleteAll = isRequestDeleteAll;
  }

  @Override
  public void onReplaceFragment(BaseFragment fragment, String tag) {
    replaceFragment(fragment, tag);
  }

  /* Request */
  @Override
  public void startRequest(int loaderId) {
    viewHolder.startRequest(loaderId);
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    viewHolder.progressDialogDismis();
    if (viewHolder.isRefreshing()) {
//            pullToRefreshListView.onRefreshComplete();
      viewHolder.onRefreshComplete();
    }

    if (loader.getId() == LOADER_GET_UPDATE_INFO_FLAG) {
      viewHolder.dialogDismis();
    }

    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      viewHolder.onRefreshComplete();
      getActivity().getSupportLoaderManager().destroyLoader(
          loader.getId());
      return;
    } else if (response instanceof ConversationResponse) {

      viewHolder.setConversationResponse(response);

    } else if (response instanceof DeleteConversationResponse) {
      viewHolder.handleDeleteAllComversation((DeleteConversationResponse) response);
    } else if (response instanceof MarkReadsResponse) {
      if (response.getCode() == Response.SERVER_SUCCESS
          && listMarkAsReadUser != null) {
        int numberOfMarkAsRead = listMarkAsReadUser.length;
        if (numberOfMarkAsRead > 0) {
          for (ConversationItem item : viewHolder.getmConversationList()) {
            for (String userId : listMarkAsReadUser) {
              if (item.getFriendId().equals(userId)) {
                item.setUnreadNum(0);
                break;
              }
            }
          }
        }
        listMarkAsReadUser = null;
        viewHolder.conversationOntifyData();
        mainActivity.requestTotalUnreadMsg();
      }
    } else if (response instanceof GetBasicInfoResponse) {
      handleGetBasicInfoResponse((GetBasicInfoResponse) response);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_CONVERSATION:
        response = new ConversationResponse(data);
        break;
      case LOADER_ID_USER_INFO_CALL:
      case LOADER_ID_BASIC_USER_INFO:
        LogUtils.e(TAG, "parseResponse LOADER_ID_BASIC_USER_INFO");
        response = new GetBasicInfoResponse(getActivity(), data);
        break;
      case LOADER_DELETE_CONVERSATION:
        response = new DeleteConversationResponse(data);
        break;
      case LOADER_ID_TOTAl_UNREAD_MSG:
        response = new GetTotalUnreadMesageResponse(data);
        break;
      case LOADER_ID_MARK_AS_READ:
        response = new MarkReadsResponse(data);
        break;
      case LOADER_ID_CHECK_UNLOCK:
        response = new CheckUnlockResponse(data);
        break;
      default:
        break;
    }
    LogUtils.i(TAG, response.toString());
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  //****************************************************************
  //* Receiver
  //****************************************************************
  private void unregisterReceiveMessage() {
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
        mBroadcastReceiver);
  }

  private void registerReceiveMessage() {
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // assign type for message: pp or wink
        if (action.equals(ChatManager.ACTION_LOCAL_MESSAGE)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_WINK)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_FILE)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_GIFT)
            || action
            .equals(ChatManager.ACTION_LOCAL_MESSAGE_LOCATION)
            || action
            .equals(ChatManager.ACTION_LOCAL_MESSAGE_STICKER)
            || action.equals(ChatManager.ACTION_MESSAGE_WINK)
            || action.equals(ChatManager.ACTION_MESSAGE)
            || action.equals(ChatManager.ACTION_MESSAGE_FILE)
            || action.equals(ChatManager.ACTION_MESSAGE_GIFT)
            || action.equals(ChatManager.ACTION_MESSAGE_LOCATION)
            || action.equals(ChatManager.ACTION_MESSAGE_STICKER)
            || action.equals(ChatManager.ACTION_MESSAGE_CALL)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_CALL)
            || action.equals(ChatManager.ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION)) {
          viewHolder.enableDelete();
          MessageClient compat = (MessageClient) intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          mMessage = compat.getMessage();
          messageFromUserId = mMessage.from;
          if (Utility.isBlockedWithUser(mAppContext,
              messageFromUserId)
              || Utility.isBlockedWithUser(mAppContext,
              mMessage.to)) {
            return;
          }
          if (mMessage.msgType == MessageType.WINK) {
            messageType = ChatMessage.WINK;
          } else if (mMessage.msgType == MessageType.PP) {
            messageType = ChatMessage.PP;
          } else if (mMessage.msgType == MessageType.FILE) {
            // If confirm message-> not show in list
            // conversation
//                        if (ChatUtils.isConfirmMessage(mMessage.value)) {
//                            return;
//                        }
            messageType = ChatMessage.FILE;
          } else if (mMessage.msgType == MessageType.GIFT) {
            messageType = ChatMessage.GIFT;
          } else if (mMessage.msgType == MessageType.LCT) {
            messageType = ChatMessage.LOCATION;
          } else if (mMessage.msgType == MessageType.STK) {
            messageType = ChatMessage.STICKER;
          } else if (mMessage.msgType == MessageType.SVIDEO) {
            messageType = ChatMessage.STARTVIDEO;
          } else if (mMessage.msgType == MessageType.EVIDEO) {
            messageType = ChatMessage.ENDVIDEO;
          } else if (mMessage.msgType == MessageType.SVOICE) {
            messageType = ChatMessage.STARTVOICE;
          } else if (mMessage.msgType == MessageType.EVOICE) {
            messageType = ChatMessage.ENDVOICE;
          } else if (mMessage.msgType == MessageType.CALLREQ) {
            messageType = ChatMessage.CALLREQUEST;
//                       restartRequestBasicUserInfo(mMessage.from);
          } else {
            // NOP
          }

          String userFrom = mMessage.from;
          String userTo = mMessage.to;
          UserPreferences userPreferences = UserPreferences
              .getInstance();
          String currentUserId = userPreferences.getUserId();
          String isChatUserId = userPreferences.getCurentFriendChat();

          LogUtils.d(TAG, "userFrom=" + userFrom + ", currentUserId="
              + currentUserId);
          isHasOnConversationList = false;
          for (int i = 0; i < viewHolder.getmConversationList().size(); i++) {
            if (viewHolder.getmConversationList().get(i).getFriendId()
                .equalsIgnoreCase(userFrom)
                || viewHolder.getmConversationList().get(i).getFriendId()
                .equalsIgnoreCase(userTo)) {
              if (userFrom.equalsIgnoreCase(isChatUserId)) {
                viewHolder.getmConversationList().get(i).setLastMessage(
                    mMessage.value);
                viewHolder.getmConversationList()
                    .get(i)
                    .setSentTime(
                        String.valueOf(mMessage.serverTime));
//                                                Utility.convertLocalDate(mMessage.originTime));
                viewHolder.getmConversationList().get(i).setMessageType(
                    messageType);
                if (userTo.equalsIgnoreCase(isChatUserId)) {
                  viewHolder.getmConversationList().get(i).setOwn(true);
                } else {
                  viewHolder.getmConversationList().get(i).setOwn(false);
                }

              } else {
                viewHolder.getmConversationList().get(i).setMessageType(
                    messageType);
                if (userFrom.equalsIgnoreCase(currentUserId)) {
                  viewHolder.getmConversationList().get(i).setOwn(true);
                } else {
                  viewHolder.getmConversationList().get(i).setUnreadNum(
                      viewHolder.getmConversationList().get(i)
                          .getUnreadNum() + 1);
//                                    UserPreferences.getInstance()
//                                            .increaseUnreadMessage(1);
                  viewHolder.getmConversationList().get(i).setOwn(false);
                }

                // handle wink message
                if (mMessage.msgType == MessageType.WINK) {
                  if (null == mMessage.value
                      || "".equals(mMessage.value)) {
                    if (viewHolder.getmConversationList().get(i).isOwn()) {
                      String value = getString(
                          R.string.message_wink_2,
                          viewHolder.getmConversationList().get(i)
                              .getName());
                      mMessage.value = value;
                    } else {
                      String value = getString(
                          R.string.message_wink,
                          viewHolder.getmConversationList().get(i)
                              .getName());
                      mMessage.value = value;
                    }
                  }
                }
                viewHolder.getmConversationList().get(i).setLastMessage(
                    mMessage.value);

                viewHolder.getmConversationList()
                    .get(i)
                    .setSentTime(
                        String.valueOf(mMessage.serverTime));
                //       Utility.convertLocalDate(mMessage.originTime));
              }
              Collections.sort(viewHolder.getmConversationList(),
                  new ConversationComparator());
              viewHolder.conversationOntifyData();
              isHasOnConversationList = true;
              break;
            }
          }
          if (isHasOnConversationList) {
            isHasOnConversationList = false;
          } else {
            LogUtils.e("get info", "GetBasicInfoRequest");
            String token = UserPreferences.getInstance().getToken();
            GetBasicInfoRequest basicInfoRequest = null;
            if (userFrom.equalsIgnoreCase(currentUserId)) {
              basicInfoRequest = new GetBasicInfoRequest(token,
                  userTo);
            } else {
              basicInfoRequest = new GetBasicInfoRequest(token,
                  userFrom);
            }
            mNewMessage = mMessage.value;
            mNewDate = Utility
                .convertLocalDate(mMessage.originTime);
            restartRequestServer(LOADER_ID_BASIC_USER_INFO,
                basicInfoRequest);
            isHasOnConversationList = false;
          }
        } else if (action.equals(ChatManager.ACTION_MESSAGE_CMD)) {
          MessageClient compat = (MessageClient) intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          Message message = compat.getMessage();
          handleBlockMessage(message);
        } else if (action.equals(AccountStatus.ACTION_BLOCKED)) {
          String blockedUserId = intent
              .getStringExtra(AccountStatus.EXTRA_DATA);
          handleBlockUser(blockedUserId);
        } else if (action.equals(ACTION_STREAMING_ERROR)) {
          showDialogCanNotStreaming();
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(
        ChatManager.ACTION_AUTHENTICATION);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_WINK);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_FILE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_GIFT);
    intentFilter.addAction(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_LOCATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CALL);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_STICKER);
    intentFilter.addAction(AccountStatus.ACTION_BLOCKED);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_WINK);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CMD);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_FILE);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_GIFT);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_LOCATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_STICKER);
    intentFilter.addAction(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_CALL);
    intentFilter.addAction(ACTION_STREAMING_ERROR);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_CALLREQUEST_CONVERSATION);

    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
        mBroadcastReceiver, intentFilter);
  }

  private void restartRequestBasicUserInfo(String userID) {
    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getUserId().equals(userID)) {
      return;
    }
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, userID);
    restartRequestServer(LOADER_ID_USER_INFO_CALL, request);
  }

  private void showDialogCanNotStreaming() {
    if (dialogCanNotStreaming != null && dialogCanNotStreaming.isShowing()) {
      return;
    }
    AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    builder.setMessage(R.string.voip_stream_error);
    builder.setPositiveButton(R.string.common_ok, null);
    dialogCanNotStreaming = builder.create();
    dialogCanNotStreaming.setCancelable(false);
    dialogCanNotStreaming.show();
  }

  private void handleBlockMessage(Message message) {
    String patternBlock = "\\Ablock&";
    Pattern ppp = Pattern.compile(patternBlock, Pattern.DOTALL);
    Matcher m = ppp.matcher(message.value);
    boolean blocked = false;

    if (m.find()) {
      blocked = true;
    }

    if (blocked && viewHolder.getmConversationList() != null) {
      // Remove item from list of conversations and update number of
      // unread messages
      handleBlockUser(message.from);
    }
  }

  private void handleBlockUser(String userId) {
    int notification = 0;
    boolean found = false;

    for (int i = 0; i < viewHolder.getmConversationList().size(); i++) {
      if (userId.equals(viewHolder.getmConversationList().get(i).getFriendId())) {
        viewHolder.getmConversationList().remove(i);
        found = true;
      } else {
        notification = notification
            + viewHolder.getmConversationList().get(i).getUnreadNum();
      }
    }

    if (found) {
      viewHolder.conversationOntifyData();
      //update total unread message sau khi blog userId
      NewMessageEvent newMessageEvent = new NewMessageEvent(NewMessageEvent.SHOW, notification);
      EventBus.getDefault().post(newMessageEvent);
    }
    // check if in state chat hidden -> stop hidden
    if (userId.equals(mainActivity.mHiddenUserId)) {
      mainActivity.stopChatHiddenUser();
    }
  }

  /*
   * Events
   */
  public void onEvent(ConversationEvent event) {
    if (event != null) {
      switch (event.mode) {
        case ConversationEvent.UPDATE: {
          viewHolder.conversationOntifyData();
        }
        break;
        case ConversationEvent.REMOVE: {
          String mHiddenUserId = event.userId;
          viewHolder.conversationListRemove(mHiddenUserId);
        }
        break;
        case ConversationEvent.CLEAR: {
          String userId = event.userId;
          viewHolder.clearUnreadMessage(userId);
        }
        break;
        default:
          break;
      }
    }
  }

  public interface OnChangeConversationList {

    void onChange(List<ConversationItem> items);
  }
}
