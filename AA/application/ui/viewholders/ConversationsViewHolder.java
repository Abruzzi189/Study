package com.application.ui.viewholders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.connection.Response;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.ConversationRequest;
import com.application.connection.request.DeleteConversationRequest;
import com.application.connection.request.MarkReadsRequest;
import com.application.connection.response.ConversationResponse;
import com.application.connection.response.DeleteConversationResponse;
import com.application.entity.ConversationItem;
import com.application.event.ConversationEvent;
import com.application.event.NewMessageEvent;
import com.application.status.IStatusChatChanged;
import com.application.status.MessageInDB;
import com.application.status.StatusController;
import com.application.ui.ChatFragment;
import com.application.ui.MainActivity;
import com.application.ui.chat.ConversationsFragment;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.EmojiTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.pulltorefresh.GridViewWithHeaderAndFooter;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridViewAuto;
import com.application.util.ConversationComparator;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

/**
 * Created by Namit on 4/4/2016. there is no view of the ConversationsFragment
 */
public class ConversationsViewHolder extends BaseViewHolder implements View.OnClickListener,
    AdapterView.OnItemClickListener {

  //****************************************************************
  //* Variables
  //****************************************************************
  private final String TAG = ConversationsViewHolder.class.getName();
  /* Views */
  private TextView mtxtShake;
  private TextView mtxtAddFriend;
  private TextView mtxtEdit;
  private TextView mtxtDone;
  private LinearLayout mlnHeadNormal;
  private LinearLayout mlnHeadEdit;
  private PullToRefreshGridViewAuto pullToRefreshListView;
  private GridViewWithHeaderAndFooter mlvConversation;
  private TextView mtxtEmptyChatView;

  private ProgressDialog progressDialog;
  private ProgressDialog mDialog;

  private ConversationAdapter mConversationAdapter;
  private boolean mNeedRefreshConversationList = false;
  private ArrayList<ConversationItem> mConversationList = new ArrayList<ConversationItem>();

  private String mFirstTime = "";

  /* Action */
  private IOnActionConversations onAction;

  private PullToRefreshBase.OnRefreshListener2<GridViewWithHeaderAndFooter> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<GridViewWithHeaderAndFooter>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      try {
        refreshConversationList();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
      try {
        requestConversation(onAction.onGetTimeSpan());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
  private boolean mUserScrolled = false;

  //****************************************************************
  //* Construct
  //****************************************************************
  public ConversationsViewHolder(View root, AppCompatActivity context,
      IOnActionConversations onAction) {
    super(root, context, onAction);
    //cast action
    try {
      initView(root, context);
      this.onAction = onAction;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //****************************************************************
  //* Views
  //****************************************************************
  @Override
  protected void initView(View root, AppCompatActivity context) throws Exception {
    mtxtShake = (TextView) root.findViewById(R.id.tvMarkAsRead);
    mtxtAddFriend = (TextView) root.findViewById(R.id.tvDeleteSelected);
    mtxtEdit = (TextView) root.findViewById(R.id.tvDeleteAll);
    mtxtDone = (TextView) root
        .findViewById(R.id.sliding_menu_right_top_control_edit_txt_done);
    mlnHeadNormal = (LinearLayout) root
        .findViewById(R.id.llMessengerHeader);
    mlnHeadEdit = (LinearLayout) root
        .findViewById(R.id.sliding_menu_right_head_edit);
    pullToRefreshListView = (PullToRefreshGridViewAuto) root
        .findViewById(R.id.sliding_menu_right_list_chat);

    initRegenerativeFunction(context);
  }

  //****************************************************************
  //* Reference father
  //****************************************************************
  @Override
  public void onStart() throws Exception {
    ConversationItem anonymousConversation = null;
    if (onAction.isCurrentChatAnonymous()
        && onAction.isChatAnonymousEmpty()) {
      // save activity_main_menu_left_txt_notification_num no message with
      // anonymous
      if (mConversationList != null && mConversationList.size() > 0) {
        anonymousConversation = mConversationList.get(0);
      }
    }
    if (mNeedRefreshConversationList) {
      mConversationAdapter.clear();
      requestConversation(mFirstTime);
      if (anonymousConversation != null) {
        mConversationList.add(anonymousConversation);
      }
    }
  }

  @Override
  public void onResume() throws Exception {
    refreshConversationList();
  }

  @Override
  public void onPause() throws Exception {

  }

  @Override
  public void onSaveInstanceState() throws Exception {
    mNeedRefreshConversationList = true;
  }

  @Override
  public void onDestroy() throws Exception {
    StatusController.getInstance(context)
        .removeStatusChangedListener(mConversationAdapter);
  }

  @Override
  public void onDestroyView() throws Exception {
    if (mDialog == null || mDialog.isShowing()) {
      mDialog.dismiss();
    }
    if (progressDialog == null || progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  @Override
  protected void initRegenerativeFunction(AppCompatActivity context) throws Exception {
    pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    pullToRefreshListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    pullToRefreshListView.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
    pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    mUserScrolled = false;
//                } else {
//                    mUserScrolled = true;
//                }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
//                if (mUserScrolled)
//                    initSlideState();

        if (pullToRefreshListView.isRefreshing() || mConversationAdapter == null
            || mConversationAdapter.isEmpty()) {
          return;
        }

        if (firstVisibleItem + visibleItemCount == totalItemCount) {
          PullToRefreshBase.Mode mode = pullToRefreshListView.getMode();
          if (pullToRefreshListView.isRefreshing()) {
            LogUtils.i(TAG, "PullView.isRefreshing: true");
          } else {
            LogUtils.e(TAG, "PullView.isRefreshing: false");
          }
          if (mode == PullToRefreshBase.Mode.PULL_FROM_END
              || mode == PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY
              || mode == PullToRefreshBase.Mode.BOTH) {
            pullToRefreshListView.performPullUp();
          }
        }
      }
    });
    mlvConversation = pullToRefreshListView.getRefreshableView();
    pullToRefreshListView.setOnRefreshListener(onRefreshListener);
    pullToRefreshListView.getLoadingLayoutProxy(true, false);

    mlvConversation.setEmptyView(mtxtEmptyChatView);
    mlvConversation.setCacheColorHint(android.R.color.transparent);

    mtxtShake.setOnClickListener(this);
    mtxtEdit.setOnClickListener(this);
    mtxtDone.setOnClickListener(this);
    mtxtAddFriend.setOnClickListener(this);
    mlvConversation.setOnItemClickListener(this);

    mtxtEmptyChatView = new TextView(context);
    mtxtEmptyChatView.setBackgroundColor(context.getResources().getColor(
        android.R.color.transparent));
    mtxtEmptyChatView.setText("");
    mtxtEmptyChatView.setLayoutParams(new AbsListView.LayoutParams(
        AbsListView.LayoutParams.MATCH_PARENT,
        AbsListView.LayoutParams.MATCH_PARENT));
    mtxtEmptyChatView.setGravity(Gravity.CENTER);

    mConversationAdapter = new ConversationAdapter(context,
        R.layout.activity_main_menu_right, mConversationList);

    mlvConversation.setAdapter(mConversationAdapter);

    initVariables(context);
  }


  //****************************************************************
  //* Function
  //****************************************************************
  private void refreshConversationList() throws Exception {
    String userIdToSend = UserPreferences.getInstance()
        .getCurentFriendChat();
    if (userIdToSend != null) {
      String[] userId = new String[]{userIdToSend};
      markAsRead(userId);
    }
    if (onAction.isCurrentChatAnonymous()) {
      // namit added: keep hidden chat.
      ConversationItem item = mConversationAdapter
          .getConversation(onAction.onHiddenUserId());
      mConversationList.clear();
      mConversationList.add(item);
    } else {
      mConversationList.clear();
    }
    mConversationAdapter.notifyDataSetChanged();
    requestConversation(mFirstTime);
  }

  private void markAsRead(String[] userId) {
    try {
      String token = UserPreferences.getInstance().getToken();
      onAction.onSetListMarkAsReadUser(userId);
      MarkReadsRequest markReadsRequest = new MarkReadsRequest(token, userId);
      onAction
          .onRestartRequestServer(ConversationsFragment.LOADER_ID_MARK_AS_READ, markReadsRequest);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void requestConversation(String timeSpan) throws Exception {
    String token = UserPreferences.getInstance().getToken();
    ConversationRequest conversationRequest = null;
    LogUtils.i(TAG, "requestConversation, timeSpan=" + timeSpan.length());
    if (timeSpan.length() == 0) {
      mtxtEmptyChatView.setText(R.string.common_loading);
      conversationRequest = new ConversationRequest(token, ConversationsFragment.TAKE);
    } else {
      conversationRequest = new ConversationRequest(token, timeSpan, ConversationsFragment.TAKE);
    }
    onAction.onRestartRequestServer(ConversationsFragment.LOADER_CONVERSATION, conversationRequest);
  }

  public void clearUnreadMessage(String userId) {
    if (mConversationList != null && mConversationAdapter != null) {
      int unReadNumHaveToRemove = 0;
      for (ConversationItem item : mConversationList) {
        if (item.getFriendId().equalsIgnoreCase(userId)) {
          unReadNumHaveToRemove = item.getUnreadNum();
          item.setUnreadNum(0);
        }
      }

      Collections.sort(mConversationList, new ConversationComparator());
      mConversationAdapter.notifyDataSetChanged();

      int unread = UserPreferences.getInstance().getNumberUnreadMessage()
          - unReadNumHaveToRemove;
      // update tong unread message sau khi user xem.
      NewMessageEvent newMessageEvent = new NewMessageEvent(NewMessageEvent.SHOW, unread);
      EventBus.getDefault().post(newMessageEvent);
    }
  }

  public boolean ismNeedRefreshConversationList() {
    return mNeedRefreshConversationList;
  }

  public void setmNeedRefreshConversationList(boolean mNeedRefreshConversationList) {
    this.mNeedRefreshConversationList = mNeedRefreshConversationList;
  }

  /* pullToRefreshListView */
  public boolean isRefreshing() {
    return pullToRefreshListView.isRefreshing();
  }

  public void onRefreshComplete() {
    pullToRefreshListView.onRefreshComplete();
  }

  /* Conversation Adapter */
  public void conversationOntifyData() {
    mConversationAdapter.notifyDataSetChanged();
  }

  public ArrayList<ConversationItem> getmConversationList() {
    return mConversationList;
  }

  public void conversationListRemove(String mHiddenUserId) {
    mConversationAdapter.removeConversation(mHiddenUserId);
  }

  /* Dialog */
  public void progressDialogDismis() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  public void dialogDismis() {
    if (mDialog != null && mDialog.isShowing()) {
      mDialog.dismiss();
    }
  }

  public void startRequest(int loaderId) {
    if (loaderId == ConversationsFragment.LOADER_DELETE_CONVERSATION
        || loaderId == ConversationsFragment.LOADER_ID_MARK_AS_READ) {
      if (progressDialog == null) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.waiting));
      }
      progressDialog.show();
    }
  }

  /* Response */
  public void setConversationResponse(Response response) {
    disableDelete();
    ConversationResponse conversationResponse = (ConversationResponse) response;
    mtxtEmptyChatView.setText(R.string.no_more_items_to_show);
    if (conversationResponse.getCode() == Response.SERVER_SUCCESS) {
      if (conversationResponse.getConversationItem().size() < ConversationsFragment.TAKE) {
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
      } else {
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        Resources resource = context.getResources();
        pullToRefreshListView.setPullLabelFooter(resource
            .getString(R.string.pull_to_load_more_pull_label));
        pullToRefreshListView
            .setReleaseLabelFooter(resource
                .getString(R.string.pull_to_load_more_release_label));
      }

      // Filter results
      filterConnectionResponseResults(conversationResponse);

      boolean hasList = false;
      if (conversationResponse.getConversationItem().size() > 0) {
        for (ConversationItem itemNew : conversationResponse
            .getConversationItem()) {
          for (int j = 0; j < mConversationList.size(); j++) {
            ConversationItem itemOld = mConversationList.get(j);
            if (itemNew.getFriendId().equalsIgnoreCase(
                itemOld.getFriendId())) {
              mConversationList.set(j, itemNew);
              hasList = true;
              break;
            } else {
              hasList = false;
            }
          }
          if (!hasList) {
            mConversationList.add(itemNew);
          }
        }

        Collections.sort(mConversationList,
            new ConversationComparator());
        try {
          onAction.onSetTimeSpan(mConversationList.get(
              mConversationList.size() - 1).getSentTime());
        } catch (Exception e) {
          e.printStackTrace();
        }
        mConversationAdapter.notifyDataSetChanged();
      }

    } else {
      pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
      ErrorApiDialog.showAlert(context, R.string.common_error,
          response.getCode());
      return;
    }
    if (mConversationList.size() > 0) {
      enableDelete();
    } else {
      disableDelete();
    }
  }

  private void filterConnectionResponseResults(ConversationResponse response) {
    List<ConversationItem> listConversations = response
        .getConversationItem();
    if (listConversations != null) {
      ListIterator<ConversationItem> li = listConversations
          .listIterator();
      ConversationItem current = null;
      List<ConversationItem> distinctResult = new ArrayList<ConversationItem>();

      boolean contains;

      while (li.hasNext()) {
        current = li.next();

        contains = false;
        for (int i = 0; i < distinctResult.size(); i++) {
          if (distinctResult.get(i).getFriendId()
              .equals(current.getFriendId())) {
            contains = true;
            break;
          }
        }

        if (contains
            || Utility.isBlockedWithUser(context,
            current.getFriendId())
            || mConversationAdapter.contains(current.getFriendId())) {
          li.remove();
        } else {
          distinctResult.add(current);
        }
      }
    }
  }

  public void onGetBasicInfoResponseConversation(ConversationItem item, boolean hasList) {

    for (int i = 0; i < mConversationList.size(); i++) {
      ConversationItem itemOld = mConversationList.get(i);
      if (item.getFriendId().equalsIgnoreCase(itemOld.getFriendId())) {
        mConversationList.set(i, item);
        hasList = true;
        break;
      }
    }
    if (!hasList) {
      mConversationList.add(item);
    }
    Collections.sort(mConversationList, new ConversationComparator());
    mConversationAdapter.notifyDataSetChanged();
  }

  public void handleDeleteAllComversation(DeleteConversationResponse response) {
    enableDelete();
    if (response.getCode() == Response.SERVER_SUCCESS) {
      StatusController statusController = StatusController
          .getInstance(context);
      if (onAction.isRequestDeleteAll()) {
        statusController.clearAllMsg();
        // Do not delete conversation hidden
        ArrayList<ConversationItem> collection = new ArrayList<ConversationItem>();
        try {
          if (!TextUtils.isEmpty(onAction.onHiddenUserId())) {
            for (ConversationItem conversationItem : mConversationList) {
              if (onAction.onHiddenUserId().equals(conversationItem
                  .getFriendId())) {
                collection.add(conversationItem);
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        String[] listFriendIds = new String[collection.size()];
        int i = 0;
        for (ConversationItem item : collection) {
          listFriendIds[i] = item.getFriendId();
        }
        statusController.deleteListConversation(listFriendIds);
        mConversationList.retainAll(collection);
        mConversationAdapter.notifyDataSetChanged();
        onAction.setRequestDeleteAll(false);
      } else {
        int pos = mConversationAdapter.getDeletePosition();
        if (pos >= 0) {
          ConversationItem item = mConversationAdapter.getItem(pos);
          String userId = item.getFriendId();
          statusController
              .deleteListConversation(new String[]{userId});
          statusController.clearAllMsgFrom(userId);
          mConversationList.remove(mConversationAdapter
              .getDeletePosition());
          mConversationAdapter.notifyDataSetChanged();
          mConversationAdapter.setDeletePosition(-1);
        }
      }
      onAction.onRequestTotalUnreadMsg();
    }
    if (mConversationList.size() == 0) {
      disableDelete();
      changeEditConversationStatus(false);
    }
  }

  private void changeEditConversationStatus(boolean isEdit) {
    if (isEdit) {
      mlnHeadNormal.setVisibility(View.GONE);
      mlnHeadEdit.setVisibility(View.VISIBLE);
      mConversationAdapter.setEdit(true);
    } else {
      mlnHeadNormal.setVisibility(View.VISIBLE);
      mlnHeadEdit.setVisibility(View.GONE);
      mConversationAdapter.setEdit(false);
    }
    mConversationAdapter.notifyDataSetChanged();
  }

  private void requestDeleteConversation(List<String> userIdList) {
    String[] userIds = new String[userIdList.size()];
    for (int i = 0; i < userIdList.size(); i++) {
      userIds[i] = userIdList.get(i);
    }

    onAction.setRequestDeleteAll(true);
    String token = UserPreferences.getInstance().getToken();
    DeleteConversationRequest deleteConversation = new DeleteConversationRequest(
        token, userIds);
    try {
      onAction.onRestartRequestServer(ConversationsFragment.LOADER_DELETE_CONVERSATION,
          deleteConversation);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* Create Dialog View */
  private void initVariables(AppCompatActivity context) {
    mNeedRefreshConversationList = false;
    StatusController.getInstance(context).addStatusChangedListener(
        mConversationAdapter);
    mDialog = new ProgressDialog(context);
    mDialog.setMessage(context.getString(R.string.waiting));
    mDialog.setCancelable(false);
    mDialog.setCanceledOnTouchOutside(false);

    progressDialog = new ProgressDialog(context);
    progressDialog.setMessage(context.getString(R.string.waiting));
  }

  public void enableDelete() {
    if (mtxtAddFriend != null) {
      mtxtAddFriend.setEnabled(true);
      mtxtAddFriend.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_action_delete_selected, 0, 0, 0);
    }
    if (mtxtEdit != null) {
      mtxtEdit.setEnabled(true);
    }
  }

  public void disableDelete() {
    if (mtxtAddFriend != null) {
      mtxtAddFriend.setEnabled(false);
      mtxtAddFriend.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_action_delete_selected_disable, 0, 0, 0);
    }
    if (mtxtEdit != null) {
      mtxtEdit.setEnabled(false);
    }
  }

  //****************************************************************
  //* Event
  //****************************************************************

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tvMarkAsRead:
        if (mConversationList.size() > 0) {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              context, "",
              context.getString(R.string.confirm_mark_all_readed), true)
              .setPositiveButton(0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  String[] friends = new String[mConversationList.size()];
                  for (int i = 0; i < mConversationList.size(); i++) {
                    friends[i] = mConversationList.get(i).getFriendId();
                  }

                  markAsRead(friends);
                }
              })
              .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create();
          confirmDialog.show();
        } else {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              context, "",
              context.getString(R.string.confirm_mark_all_readed), true)
              .setPositiveButton(0, null)
              .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create();
          confirmDialog.show();
        }
        break;
      case R.id.tvDeleteSelected:
        // Handle delete selected conversation
        if (mConversationList.size() > 0) {
          mtxtEdit.setEnabled(true);
          changeEditConversationStatus(true);
        } else {
          mtxtEdit.setEnabled(false);
        }

        break;
      case R.id.tvDeleteAll:
        if (mConversationList.size() >= 0) {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              context, "",
              context.getString(R.string.confirm_delete_all_messages), true)
              .setPositiveButton(0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  List<String> userIdList = new ArrayList<String>();
                  for (int i = 0; i < mConversationList.size(); i++) {
                    String userId = mConversationList.get(i)
                        .getFriendId();
                    try {
                      if (!userId.equals(onAction.onHiddenUserId())) {
                        userIdList.add(userId);
                      }
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                  requestDeleteConversation(userIdList);
                }
              })
              .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create();
          confirmDialog.show();
        } else {
          AlertDialog confirmDialog = new CustomConfirmDialog(
              context, "",
              context.getString(R.string.confirm_delete_all_messages), true)
              .setPositiveButton(0, null)
              .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create();
          confirmDialog.show();
        }
        break;

      case R.id.sliding_menu_right_top_control_edit_txt_done:
        changeEditConversationStatus(false);
        if (mConversationList.size() == 0) {
          disableDelete();
        }
        break;

      default:
        break;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (parent.getItemAtPosition(position) instanceof ConversationItem) {
      ConversationItem item = (ConversationItem) parent
          .getItemAtPosition(position);
      if (!item.getFriendId().equalsIgnoreCase("null")
          && !item.getFriendId().equalsIgnoreCase(
          UserPreferences.getInstance().getUserId())
          && item.getFriendId().trim().length() > 0) {
        ChatFragment chatFragment = ChatFragment.newInstance(
            item.getFriendId(), true);
        onAction.onReplaceFragment(chatFragment, ConversationsFragment.TAG_FRAGMENT_CHAT);
        onAction.getSlidingMenu().showContent();
      }
    }
  }

  //****************************************************************
  //* inner class
  //****************************************************************
  private class ConversationAdapter extends ArrayAdapter<ConversationItem>
      implements IStatusChatChanged {

    private int mAvatarSize;
    private boolean isEdit = false;
    private List<ConversationItem> objects;
    private int deletePosition = -1;
    private Context context;

    public ConversationAdapter(Context context, int textViewResourceId,
        List<ConversationItem> objects) {
      super(context, textViewResourceId, objects);
      this.context = context;
      this.objects = objects;
      mAvatarSize = context.getResources().getDimensionPixelSize(
          R.dimen.activity_setupprofile_img_avatar_height);
      EventBus.getDefault().post(
          new ConversationEvent(ConversationEvent.CHANGE, objects));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ConversationItem item = objects.get(position);
      HolderView holderView = null;
      if (convertView == null) {
        holderView = new HolderView();
        LayoutInflater inflater = (LayoutInflater) getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_list_conversation,
            parent, false);
        holderView.imgAvata = (ImageView) convertView
            .findViewById(R.id.sliding_menu_right_item_img_avata);
        holderView.tvNotification = (BadgeTextView) convertView
            .findViewById(R.id.text_type_sliding_menu_right_item_txt_notification);
        holderView.tvName = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_name);
        holderView.tvChatToMe = (EmojiTextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_chat_to_me_content);
        holderView.tvDistance = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_info_distance);
        holderView.tvDistanceTime = (TextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_user_info_time_range);
        holderView.tvChatByMe = (EmojiTextView) convertView
            .findViewById(R.id.sliding_menu_right_item_txt_chat_by_me_content);
        holderView.btnDelete = (Button) convertView
            .findViewById(R.id.sliding_menu_right_item_btn_delete);
        // holderView.imgOnline = (ImageView) convertView
        // .findViewById(R.id.sliding_menu_right_item_img_online);
        holderView.layoutTimeDistance = (LinearLayout) convertView
            .findViewById(R.id.layout_time_location);
        holderView.footerView = (View) convertView
            .findViewById(R.id.sliding_menu_right_item_footer_view);
        holderView.imgWarning = convertView.findViewById(R.id.warning);
        convertView.setTag(holderView);
      } else {
        holderView = (HolderView) convertView.getTag();
      }

      // remove footer line
      if (position == (getCount() - 1)) {
        holderView.footerView.setVisibility(View.GONE);
      } else {
        holderView.footerView.setVisibility(View.VISIBLE);
      }
      // else
      // holderView.footerView.setVisibility(View.VISIBLE);
      // String distance = Utility.getDistanceString(getContext(),
      // item.getDistance());
      // holderView.tvDistance.setText(distance);
      holderView.tvDistance.setVisibility(View.GONE);

      holderView.btnDelete.setTag(position);
      holderView.btnDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          setDeletePosition(Integer.parseInt(v.getTag().toString()));
          String userId = objects.get(getDeletePosition())
              .getFriendId();
          requestDeleteConversation(new String[]{userId});
        }
      });

      if (isEdit) {
        holderView.btnDelete.setVisibility(View.VISIBLE);
      } else {
        holderView.btnDelete.setVisibility(View.GONE);
      }

      if (item.isOwn()) {
        // holderView.tvChatByMe.setVisibility(View.VISIBLE);
        // holderView.tvChatToMe.setVisibility(View.GONE);

        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_by_me_layout)
            .setVisibility(View.VISIBLE);
        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_to_me_layout)
            .setVisibility(View.GONE);

        if (item.getMessageType().equals(ChatMessage.WINK)) {
          String value = item.getLastMessage();
          if (null == value || "".equals(value)) {
            value = context.getString(R.string.message_wink_2,
                item.getName());
          }
          holderView.tvChatByMe.setText(value);
        } else if (item.getMessageType().equals(ChatMessage.FILE)) {
          String type = ChatUtils.getFileType(item.getLastMessage());
          holderView.tvChatByMe
              .setEmojiText(ChatUtils.getMessageByFileType(type,
                  context));
        } else if (item.getMessageType().equals(ChatMessage.GIFT)) {

          String content = item.getLastMessage();
          String[] split = content.split("\\|");
          int point = 0;
          if (split != null && split.length >= 4) {
            point = Integer.valueOf(split[3]);
          }

          String format = context.getResources().getString(
              R.string.send_gift_price_not_free);
          String pointStr = MessageFormat.format(format, point);

          String display = String.format(
              context.getResources().getString(
                  R.string.gift_message_display_send),
              pointStr);

          holderView.tvChatByMe.setText(display);
        } else if (item.getMessageType().equals(ChatMessage.LOCATION)) {
          holderView.tvChatByMe
              .setText(R.string.chat_item_share_a_location);
        } else if (item.getMessageType().equals(ChatMessage.STICKER)) {
          holderView.tvChatByMe.setText(R.string.sticker);

        } else if (item.getMessageType().equals(ChatMessage.STARTVIDEO)
            || item.getMessageType().equals(ChatMessage.ENDVIDEO)
            || item.getMessageType().equals(ChatMessage.STARTVOICE)
            || item.getMessageType().equals(ChatMessage.ENDVOICE)) {
          showCallIconAndContent(holderView.tvChatByMe,
              item.getLastMessage(), true);
        } else if (item.getMessageType().equals(ChatMessage.CALLREQUEST)) {
          String msg;
          if (item.getLastMessage().equals(
              ChatMessage.CALLREQUEST_VIDEO)) {
            msg = context.getString(R.string.message_video_call_request,
                item.getName());
          } else {
            msg = context.getString(R.string.message_voice_call_request,
                item.getName());
          }
          holderView.tvChatByMe.setText(msg);
        } else {
          holderView.tvChatByMe.setEmojiText(item.getLastMessage());
        }
      } else {

        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_by_me_layout)
            .setVisibility(View.GONE);
        convertView.findViewById(
            R.id.sliding_menu_right_item_txt_chat_to_me_layout)
            .setVisibility(View.VISIBLE);

        if (item.getMessageType().equals(ChatMessage.WINK)) {
          String value = item.getLastMessage();
          if (null == value || "".equals(value)) {
            value = context.getString(R.string.message_wink, item.getName());
          }
          holderView.tvChatToMe.setText(value);
        } else if (item.getMessageType().equals(ChatMessage.FILE)) {
          String type = ChatUtils.getFileType(item.getLastMessage());
          holderView.tvChatToMe
              .setEmojiText(ChatUtils.getMessageByFileType(type,
                  context));
        } else if (item.getMessageType().equals(ChatMessage.GIFT)) {

          String content = item.getLastMessage();
          String[] split = content.split("\\|");
          int point = 0;
          if (split != null && split.length >= 4) {
            point = Integer.valueOf(split[3]);
          }

          String format = context.getResources().getString(
              R.string.send_gift_price_not_free);
          String pointStr = MessageFormat.format(format, point);

          String display = String.format(
              context.getResources().getString(
                  R.string.gift_message_display_recieve),
              pointStr);

          holderView.tvChatToMe.setEmojiText(display);
        } else if (item.getMessageType().equals(ChatMessage.LOCATION)) {
          holderView.tvChatToMe
              .setText(R.string.chat_item_share_a_location);
        } else if (item.getMessageType().equals(ChatMessage.STICKER)) {
          holderView.tvChatToMe.setText(R.string.sticker);
        } else if (item.getMessageType().equals(ChatMessage.STARTVIDEO)
            || item.getMessageType().equals(ChatMessage.ENDVIDEO)
            || item.getMessageType().equals(ChatMessage.STARTVOICE)
            || item.getMessageType().equals(ChatMessage.ENDVOICE)) {
          showCallIconAndContent(holderView.tvChatToMe,
              item.getLastMessage(), false);
        } else if (item.getMessageType().equals(ChatMessage.CALLREQUEST)) {
          String msg;
          if (item.getLastMessage() != null) {
            if (item.getLastMessage().equals(
                ChatMessage.CALLREQUEST_VIDEO)) {
              msg = context.getString(R.string.message_video_call_request,
                  UserPreferences.getInstance().getUserName());
            } else {
              msg = context.getString(R.string.message_voice_call_request,
                  UserPreferences.getInstance().getUserName());
            }
            holderView.tvChatToMe.setText(msg);
          }
        } else {
          holderView.tvChatToMe.setEmojiText((item.getLastMessage()));
        }
      }
      holderView.tvNotification.setTextNumber(item.getUnreadNum());
      if (item.getSentTime() != null) {
        if (item.getSentTime().length() > 0) {
          try {
            Calendar calendarNow = Calendar.getInstance();

            Utility.YYYYMMDDHHMMSSSSS.setTimeZone(TimeZone
                .getTimeZone("GMT"));
            Date dateSend = Utility.YYYYMMDDHHMMSSSSS.parse(item
                .getSentTime());
            Calendar calendarSend = Calendar.getInstance(TimeZone
                .getDefault());
            calendarSend.setTime(dateSend);

            holderView.tvDistanceTime.setText(Utility.getDifference(
                context, calendarSend, calendarNow));
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
      }
      if (TextUtils.isEmpty(item.getName())) {
        holderView.tvName
            .setText(R.string.notification_username_default);
      } else {
        holderView.tvName.setText(item.getName());
      }
      holderView.tvDistance.setVisibility(View.VISIBLE);
      holderView.tvDistanceTime.setVisibility(View.VISIBLE);
      holderView.layoutTimeDistance.setVisibility(View.VISIBLE);

      // load avatar
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token,
          item.getAvaId());
      try {
        onAction.getImageFetcher().loadImageByGender(imageRequest,
            holderView.imgAvata, mAvatarSize, item.getGender());
      } catch (Exception e) {
        e.printStackTrace();
      }

      boolean isVisible = !item.getIsAnonymous() && item.isMsgError();
      holderView.imgWarning.setVisibility(isVisible ? View.VISIBLE
          : View.GONE);
      return convertView;
    }

    private void showCallIconAndContent(TextView textView, String content, boolean isSend) {
      ChatUtils.CallInfo callInfo = ChatUtils.getCallInfo(content);
      String msg;
      if (callInfo.voipType == ChatMessage.VoIPActionVideoEnd
          || callInfo.voipType == ChatMessage.VoIPActionVoiceEnd) {
        msg = context.getString(R.string.voip_action_video_voice_end_new,
            ChatUtils.getCallDuration(context, callInfo));
      } else {
        msg = ChatUtils.getCallDuration(context, callInfo);
      }
      textView.setText(msg);
    }

    public void setEdit(boolean isEdit) {
      this.isEdit = isEdit;
    }

    private void requestDeleteConversation(String[] userId) {
      String token = UserPreferences.getInstance().getToken();
      DeleteConversationRequest deleteConversation = new DeleteConversationRequest(
          token, userId);
      try {
        onAction.onRestartRequestServer(ConversationsFragment.LOADER_DELETE_CONVERSATION,
            deleteConversation);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private int getDeletePosition() {
      return deletePosition;
    }

    private void setDeletePosition(int deletePosition) {
      this.deletePosition = deletePosition;
    }

    public boolean contains(String friendId) {
      boolean found = false;

      for (int i = 0; i < objects.size(); i++) {
        if (objects.get(i).getFriendId().equals(friendId)) {
          found = true;
          break;
        }
      }

      return found;
    }

    private void removeConversation(String userId) {
      if (objects == null) {
        return;
      }
      for (ConversationItem item : objects) {
        try {
          if (item.getFriendId().equals(userId)
              && onAction.isChatAnonymousEmpty()) {
            objects.remove(item);
            notifyDataSetChanged();
            return;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    private ConversationItem getConversation(String userId) {
      if (objects == null) {
        return null;
      }
      for (ConversationItem item : objects) {
        if (item.getFriendId().equals(userId)) {
          return item;
        }
      }
      return null;
    }

    @Override
    public void notifyDataSetChanged() {
      super.notifyDataSetChanged();
      EventBus.getDefault().post(
          new ConversationEvent(ConversationEvent.CHANGE, objects));
      for (ConversationItem item : mConversationList) {
        boolean hasMsgError = StatusController.getInstance(
            context).hasMsgErrorWith(item.getFriendId());
        item.setMsgError(hasMsgError);
      }
    }

    /* Update status sent message error */
    @Override
    public void create(final MessageInDB msgInDB) {
      String userId = UserPreferences.getInstance().getUserId();

      if (msgInDB.getFrom().equals(userId)) {
        ((MainActivity) context).runOnUiThread(new Runnable() {

          @Override
          public void run() {
            checkHasMsgError(msgInDB);
          }
        });
      }
    }

    @Override
    public void update(final MessageInDB msgInDB) {
      String userId = UserPreferences.getInstance().getUserId();
      if (msgInDB != null) {
        if (msgInDB.getFrom().equals(userId)) {
          if (context != null) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
              @Override
              public void run() {
                checkHasMsgError(msgInDB);
              }
            });
          }
        }
      }
    }

    @Override
    public void resendFile(MessageInDB msgInDB) {
      // Do nothing
    }

    private void checkHasMsgError(MessageInDB msgInDB) {
      String sendId = msgInDB.getTo();
      for (ConversationItem item : mConversationList) {
        if (item.getFriendId().equals(sendId)) {
          if (context != null) {
            item.setMsgError(StatusController.getInstance(
                context).hasMsgErrorWith(sendId));
            notifyDataSetChanged();
            break;
          }

        }
      }
    }

    private class HolderView {

      public ImageView imgAvata;
      // public View imgOnline;
      public BadgeTextView tvNotification;
      public TextView tvName;
      public EmojiTextView tvChatToMe;
      public EmojiTextView tvChatByMe;
      public TextView tvDistance;
      public TextView tvDistanceTime;
      public Button btnDelete;
      public LinearLayout layoutTimeDistance;
      public View footerView;
      public View imgWarning;
    }

  }
}
