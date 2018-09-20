package com.application.ui.gift;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.chat.ChatManager;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetListGiftIdRequest;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.SendGiftRequest;
import com.application.connection.response.GetListGiftIdResponse;
import com.application.connection.response.SendGiftResponse;
import com.application.entity.GiftCategories;
import com.application.entity.GiftItem;
import com.application.ui.ChatFragment;
import com.application.ui.MainActivity;
import com.application.ui.MeetPeopleFragment;
import com.application.ui.TrackingBlockFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.util.LogUtils;
import com.application.util.StringUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import vn.com.ntqsolution.chatserver.pojos.message.Message;


public class ChooseGiftToSend extends TrackingBlockFragment implements
    ResponseReceiver, OnItemClickListener {

  public static final String TAG_FRAGMENT_CHOOSE_GIFT_TO_SEND = "choose_gift_to_send";
  public static final String KEY_RECEIVE_USER_ID = "receive_user_id";
  public final static String KEY_RECEIVE_USER_NAME = "receive_user_name";
  public static final String KEY_CATEGORY = "receive_category";
  public static final String KEY_FROM_CHAT = "from_chat";
  public static final String EXTRA_GIFT_SENT_ID = "gift_sent_id";
  public static final String HAS_ICON_CHAT = "has_icon_chat";
  public static final String EXTRA_GIFT_MSG_SENT = "gift_sent_msg";
  public static final String EXTRA_GIFT_OWNER = "gift_sent_from";
  public static final String EXTRA_GIFT_RECEIVER = "gift_sent_to";
  public static final String EXTRA_GIFT_MSG_ID = "gift_sent_msg_id";
  private static final int LOADER_GET_GIFT = 0;
  private static final int LOADER_SEND_GIFT = 1;
  private static final int TAKE = 30;
  private static final int SKIP = 0;
  private View mView;
  private ProgressDialog mProgressDialog;
  private GridView mGridView;
  private AlertDialog mConfirmSendGift;
  private AlertDialog mConfirmSendSuccess;
  private String mCurrentUserId;
  private String mReceiveUserId;
  private String mReceiveUserName = "";
  private GiftCategories mCategory;
  private ArrayList<GiftItem> list;
  private GiftItem item = null;
  private ChooseGiftAdapter adapter;
  private MainActivity mMainActivity;
  private boolean hasIconChat;
  private int GIFT_NAME_LIMIT = 25;

  private boolean isFromChat = false;

  public static ChooseGiftToSend newInstance(String userId, String userName,
      GiftCategories category) {
    return newInstance(userId, userName, category, true);
  }

  public static ChooseGiftToSend newInstance(String userId, String userName,
      GiftCategories category, boolean hasIconChat) {
    ChooseGiftToSend fragment = new ChooseGiftToSend();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_RECEIVE_USER_ID, userId);
    bundle.putString(KEY_RECEIVE_USER_NAME, userName);
    bundle.putSerializable(KEY_CATEGORY, category);
    bundle.putBoolean(HAS_ICON_CHAT, hasIconChat);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static ChooseGiftToSend newInstance(String userId, String userName,
      GiftCategories category, boolean hasIconChat, boolean isFromChat) {
    ChooseGiftToSend fragment = new ChooseGiftToSend();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_RECEIVE_USER_ID, userId);
    bundle.putString(KEY_RECEIVE_USER_NAME, userName);
    bundle.putSerializable(KEY_CATEGORY, category);
    bundle.putBoolean(HAS_ICON_CHAT, hasIconChat);
    bundle.putBoolean(KEY_FROM_CHAT, isFromChat);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_choose_gift_to_send,
        container, false);
    Utility.hideKeyboard(getActivity(), mView);

    mCurrentUserId = UserPreferences.getInstance().getUserId();
    if (getArguments() != null) {
      mReceiveUserId = getArguments().getString(KEY_RECEIVE_USER_ID);
      mReceiveUserName = getArguments().getString(KEY_RECEIVE_USER_NAME);
      if (getArguments().containsKey(KEY_FROM_CHAT)) {
        isFromChat = getArguments().getBoolean(KEY_FROM_CHAT);
      }
      mCategory = (GiftCategories) getArguments().getSerializable(
          KEY_CATEGORY);
      hasIconChat = getArguments().getBoolean(HAS_ICON_CHAT);
    }
    initialListview(mView);
    requestGift();
    list = new ArrayList<GiftItem>();
    adapter = new ChooseGiftAdapter(getActivity(),
        R.layout.item_choose_gift_to_send, list);
    mGridView.setAdapter(adapter);
    return mView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mActionBar.syncActionBar();
  }

  private void requestGift() {
    String token = UserPreferences.getInstance().getToken();
    GetListGiftIdRequest getListGiftIdRequest = null;
    if (mCategory != null) {
      String language = Locale.getDefault().getLanguage();
      if (mCategory.getType() == GetListGiftIdRequest.TYPE_ALL) {
        getListGiftIdRequest = new GetListGiftIdRequest(token, SKIP,
            TAKE, language);
      } else {
        getListGiftIdRequest = new GetListGiftIdRequest(token,
            mCategory.getId(), language);
      }
      LogUtils.e("mCategory",
          "mCategory=" + getListGiftIdRequest.toString());
      requestServer(LOADER_GET_GIFT, getListGiftIdRequest);
    }
  }

  private void requestSendGift(String giftId) {
    String token = UserPreferences.getInstance().getToken();
    SendGiftRequest giftRequest = new SendGiftRequest(token, giftId,
        mReceiveUserId);
    requestServer(LOADER_SEND_GIFT, giftRequest);
  }

  private void initialListview(View view) {
    mGridView = (GridView) view.findViewById(R.id.grdChooseGift);
    mGridView.setOnItemClickListener(this);
    list = new ArrayList<GiftItem>();
  }

  private void jumpToChatScreen(final Message message) {
    //#12814#note-19#note-39 - Updated By Robert on 2018 July 11 - Append gift message when send gift: Can't show gift history after sent
    String value = message.value;

    if (isFromChat) {
      LogUtils.i(TAG, "jumpToChatScreen()...isFromChat=true...");
      final Bundle bundle = new Bundle();

      if (item != null) {
        LogUtils.i(TAG,
            "jumpToChatScreen()..isFromChat=true && item != null && getTargetFragment() != null...getTargetFragment()="
                + (getTargetFragment() != null ? getTargetFragment().getClass().getSimpleName()
                : "NULL"));

        bundle.putString(EXTRA_GIFT_SENT_ID, item.getGiftId());
        bundle.putString(EXTRA_GIFT_MSG_SENT, value);
        bundle.putString(EXTRA_GIFT_OWNER,
            mCurrentUserId);//Can get from UserPreferences.getInstance()
        bundle.putString(EXTRA_GIFT_RECEIVER, mReceiveUserId);// Can get from mFriend.getId() Object
        bundle.putString(EXTRA_GIFT_MSG_ID, message.id);

      }
      Handler handler = new Handler();
      handler.post(new Runnable() {
        @Override
        public void run() {
          //Resume Chat Screen & setArguments if need
          mNavigationManager.goBack(bundle);
        }
      });
    } else {
      LogUtils
          .i(TAG, "jumpToChatScreen()...mNavigationManager.addPage ChatFragment.newInstance()...");

      mNavigationManager.addPage(ChatFragment.newInstance(mReceiveUserId, false, true
          , item.getGiftId(), value, mCurrentUserId, mReceiveUserId, message.id));
    }

    //END #12814#note-19#note-39
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
    mMainActivity.setOnNavigationClickListener(this);
  }

  @Override
  public void startRequest(int loaderId) {
    if (getActivity() == null) {
      return;
    }
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(getActivity());
    }
    if (loaderId == LOADER_SEND_GIFT) {
      mProgressDialog.setMessage(getString(R.string.send_gift_message));
    } else {
      mProgressDialog.setMessage(getString(R.string.waiting));
    }
    if (!mProgressDialog.isShowing()) {
      try {
        mProgressDialog.show();
      } catch (Exception e) {
        LogUtils.e(TAG, e.getMessage());
      }
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }

    int responseCode = response.getCode();
    if (responseCode == Response.SERVER_BLOCKED_USER) {
      handleBlockedUser();
      return;
    }

    if (response instanceof GetListGiftIdResponse) {
      if (response.getCode() == Response.SERVER_SUCCESS) {
        GetListGiftIdResponse giftIdResponse = (GetListGiftIdResponse) response;
        if (giftIdResponse.getArrayList() != null
            && giftIdResponse.getArrayList().size() > 0) {
          adapter.updateList(giftIdResponse.getArrayList());
        }
      }
    }
    if (response instanceof SendGiftResponse) {
      SendGiftResponse giftResponse = (SendGiftResponse) response;
      int code = response.getCode();
      if (code == Response.SERVER_SUCCESS) {
        UserPreferences.getInstance().saveNumberPoint(giftResponse.getPoint());
        Message message = null;

        if (!mCurrentUserId.equalsIgnoreCase(mReceiveUserId)) {

          String giftId = StringUtils.nullToEmpty(item.getGiftId()).trim();

          if (giftId.length() > 0) {
            // format({g&h&20131003045531397;g;h;GIFT;giftID|sender_name|receiver_name|point
            // ;20131003045531397})

            UserPreferences userPreferences = UserPreferences.getInstance();
            String senderName = userPreferences.getUserName();

            String value = String.format(
                getResources().getString(R.string.gift_message_value_format),
                giftId, senderName, mReceiveUserName, String.valueOf((int) item.getPrice()));

            ChatManager chatManager = getChatManager();
            if (chatManager != null) {
              message = chatManager.makeGiftMessage(mCurrentUserId, mReceiveUserId, value);
              chatManager.sendGiftMessage(message);
            }
          }
        }

        jumpToChatScreen(message);

      } else if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
        NotEnoughPointDialog.showForGiveGift(mMainActivity,
            (int) item.getPrice());

        // CUONGNV01032016 : Remove show dialog het point
//				Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//				intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_GIFT);
//				startActivity(intent);
      } else {
        // TODO subtract point or error connect

      }
      getLoaderManager().destroyLoader(LOADER_SEND_GIFT);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    if (loaderID == LOADER_GET_GIFT) {
      response = new GetListGiftIdResponse(data);
    }
    if (loaderID == LOADER_SEND_GIFT) {
      response = new SendGiftResponse(data);
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  private void showConfirmDialog() {
    String name = item.getName();
    if (name.length() > GIFT_NAME_LIMIT) {
      name = name.substring(0, GIFT_NAME_LIMIT) + "...";
    }
    String message = String.format(
        getString(R.string.send_gift_confirm_message), name,
        mReceiveUserName);
    mConfirmSendGift = new CustomConfirmDialog(getActivity(),
        getString(R.string.send_gift_confirm_title), message, true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            requestSendGift(item.getGiftId());
          }
        })
        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    mConfirmSendGift.show();

    int dividerId = mConfirmSendGift.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmSendGift.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmSendGift.getContext().getResources().getColor(R.color.transparent));
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    item = list.get(position);
    showConfirmDialog();
  }

  private void handleBlockedUser() {
    Utility.showToastMessage(getActivity(),
        getString(R.string.action_is_not_performed));

    // Navigate to Meet People screen
    MainActivity activity = (MainActivity) getActivity();
    activity.replaceAllFragment(new MeetPeopleFragment(),
        MainActivity.TAG_FRAGMENT_MEETPEOPLE);
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mConfirmSendGift != null && mConfirmSendGift.isShowing()) {
      mConfirmSendGift.dismiss();
    }
    if (mConfirmSendSuccess != null && mConfirmSendSuccess.isShowing()) {
      mConfirmSendSuccess.dismiss();
    }
  }

  @Override
  protected String getUserIdTracking() {
    return mReceiveUserId;
  }

  private ChatManager getChatManager() {
    if (mMainActivity.getChatService() == null) {
      return null;
    }
    return mMainActivity.getChatService().getChatManager();
  }

  public boolean hasIconChat() {
    return hasIconChat;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private class ChooseGiftAdapter extends ArrayAdapter<GiftItem> {

    private int columnSize = 0;
    private int mMarginLeft;
    private ArrayList<GiftItem> list;

    @SuppressWarnings("deprecation")
    public ChooseGiftAdapter(Context context, int textViewResourceId,
        ArrayList<GiftItem> objects) {
      super(context, textViewResourceId, objects);

      Point size = new Point();
      WindowManager windowManager = (WindowManager) context
          .getSystemService(Context.WINDOW_SERVICE);
      Display display = windowManager.getDefaultDisplay();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        display.getSize(size);
      } else {
        size.x = display.getWidth();
        size.y = display.getHeight();
      }
      mMarginLeft = (int) context.getResources().getDimension(
          R.dimen.item_choose_gift_margin);
      columnSize = (size.x - mMarginLeft * 5) / 4 - 20;
      list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      GiftItem item = getItem(position);
      HolderView holderView = null;
      if (convertView == null) {
        holderView = new HolderView();
        LayoutInflater inflater = (LayoutInflater) getContext()
            .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(
            R.layout.item_choose_gift_to_send, null);
        holderView.imgDisplay = (ImageView) convertView
            .findViewById(R.id.imgGiftDisplay);
        holderView.tvPoint = (TextView) convertView
            .findViewById(R.id.tvPoint);
        holderView.txtName = (TextView) convertView
            .findViewById(R.id.txtName);
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(
            columnSize, columnSize);
        paramsImage.addRule(RelativeLayout.CENTER_HORIZONTAL);
        holderView.imgDisplay.setLayoutParams(paramsImage);
        convertView.setTag(holderView);
      } else {
        holderView = (HolderView) convertView.getTag();
      }

      int giftPrice = (int) item.getPrice();
      if (giftPrice > 0) {
        String format = getResources().getString(
            R.string.send_gift_price_not_free);
        holderView.tvPoint.setText(MessageFormat.format(format,
            giftPrice));
      } else {
        holderView.tvPoint.setText(R.string.send_gift_price_free);
      }

      holderView.txtName.setText(item.getName());

      String token = UserPreferences.getInstance().getToken();
      ImageRequest giftRequest = new ImageRequest(token,
          item.getGiftId(), ImageRequest.GIFT);

//      getImageFetcher().loadImageWithoutPlaceHolder(giftRequest,
//          holderView.imgDisplay, columnSize);
      Glide.with(mAppContext).load(giftRequest.toURL()).into(holderView.imgDisplay);
      return convertView;
    }

    public void updateList(ArrayList<GiftItem> objects) {
      this.clear();
      this.list.addAll(objects);
      notifyDataSetChanged();
    }

    private class HolderView {

      public ImageView imgDisplay;
      public TextView tvPoint;
      public TextView txtName;
    }
  }
}