package com.application.ui.notification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.application.common.webview.ActionParam;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.NotificationRequest;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.NotificationResponse;
import com.application.connection.response.UnlockResponse;
import com.application.constant.Constants;
import com.application.entity.NotificationItem;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.backstage.DetailPictureBackstageApproveActivity;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.ui.buzz.BuzzDetail;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.UnlockDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class NotificationFragment extends BaseFragment implements
    ResponseReceiver {

  private final static int LOADER_NOTIFICATION = 0;
  private final static int LOADER_ID_CHECK_UNLOCK = 1;
  private final static int LOADER_ID_UNLOCK_TYPE = 2;
  private View mView;
  private ProgressDialog progressDialog;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView lvNotification;
  private int take = 20;

  private NotificationAdapter adapter;
  private boolean isRefresh = false;
  private TextView txtEmpty;
  private UnlockDialog mUnlockDialog;
  private NotificationItem itemSelected = null;

  // private int requestType;
  private Context mAppContext;

  private boolean onlyLike = false;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      isRefresh = true;
      requestNotification(null);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      isRefresh = false;
      if (adapter.getCount() == 0) {
        mPullToRefreshListView.onRefreshComplete();
      } else {
        requestNotification(adapter.getItem(adapter.getCount() - 1)
            .getTime());
      }
    }
  };
  private IOnItemClicked onItemClicked = new IOnItemClicked() {
    @Override
    public void onItemClick(int position) {
      if (adapter.getCount() == 0) {
        return;
      }
      itemSelected = adapter.getItem(position);
      int type = itemSelected.getType();
      switch (type) {
        case Constants.NOTI_LIKE_BUZZ:
        case Constants.NOTI_LIKE_OTHER_BUZZ:
        case Constants.NOTI_COMMENT_BUZZ:
        case Constants.NOTI_COMMENT_OTHER_BUZZ:
        case Constants.NOTI_BUZZ_APPROVED:
        case Constants.NOTI_FAVORITED_CREATE_BUZZ:
        case Constants.NOTI_APPROVE_BUZZ_TEXT:
          showBuzzDetail(itemSelected, true);
          break;
        case Constants.NOTI_REPLY_YOUR_COMMENT:
        case Constants.NOTI_APPROVE_COMMENT:
        case Constants.NOTI_DENIED_COMMENT:
        case Constants.NOTI_APPROVE_SUB_COMMENT:
        case Constants.NOTI_DENI_SUB_COMMENT:
          showBuzzDetail(itemSelected, false);
          break;
        case Constants.NOTI_CHECK_OUT_UNLOCK:
        case Constants.NOTI_FAVORITED_UNLOCK:
        case Constants.NOTI_UNLOCK_BACKSTAGE:
        case Constants.NOTI_FRIEND:
        case Constants.NOTI_ONLINE_ALERT:
          showProfile(itemSelected);
          break;
        case Constants.NOTI_APPROVE_USERINFO:
        case Constants.NOTI_DENIED_USERINFO:
        case Constants.NOTI_APART_OF_USERINFO:
          showProfile(itemSelected.getUserId());
          break;
        case Constants.NOTI_DENIED_BUZZ_IMAGE:
        case Constants.NOTI_DENIED_BUZZ_TEXT:
          showMyPost(itemSelected);
          break;
        case Constants.NOTI_DAYLY_BONUS:
          Fragment fragment = WebViewFragment
              .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
          replaceFragment(fragment);
          break;
        case Constants.NOTI_BACKSTAGE_APPROVED:
          Intent intent = new Intent(mAppContext,
              DetailPictureBackstageApproveActivity.class);
          intent.putExtras(ProfilePictureData
              .parseDataToBundle(itemSelected.getImageId()));
          startActivity(intent);
          break;
        case Constants.NOTI_DENIED_BACKSTAGE:
          showListBackstage(itemSelected.getUserId());
          break;
        case Constants.NOTI_FROM_FREE_PAGE:
          String url = itemSelected.getUrl();

          if (url != null && url.contains("open_browser=1")) {
            String mUrl = url.replace("open_browser=1", "");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            startActivity(browserIntent);
            return;
          }

          ActionParam actionParam = new ActionParam();
          if (url.contains(actionParam.USER_PROFILE)) {
            int idIndex = url.lastIndexOf(actionParam.USER_PROFILE_ID)
                + actionParam.USER_PROFILE_ID.length();
            String id = url.substring(idIndex, url.length());
            if (!TextUtils.isEmpty(id)) {
              MyProfileFragment myProfileFragment = MyProfileFragment
                  .newInstance(id);
              mNavigationManager.swapPage(myProfileFragment,
                  false);
            }
          } else {
            String content = itemSelected.getContent();
            WebViewFragment webViewFragment = WebViewFragment
                .newInstance(url, content);
            replaceFragment(webViewFragment);
          }
          break;
        case Constants.NOTI_REQUEST_CALL:
          //TODO process click noti request call
          showProfile(itemSelected);
          break;
        default:
          break;
      }

    }
  };

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplicationContext();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_notification, container,
        false);
    Utility.hideSoftKeyboard(getActivity());
    initialListview(mView);
    isRefresh = true;
    requestNotification(null);
    return mView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  private void initialListview(View view) {
    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.fragment_notification_list);
    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.setMode(Mode.BOTH);
    Resources resource = getResources();
    mPullToRefreshListView.setPullLabelFooter(resource
        .getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshListView.setReleaseLabelFooter(resource
        .getString(R.string.pull_to_load_more_release_label));
    lvNotification = mPullToRefreshListView.getRefreshableView();
    txtEmpty = new TextView(getActivity());
    txtEmpty.setGravity(Gravity.CENTER);
    txtEmpty.setText(R.string.common_loading);
    txtEmpty.setTextColor(Color.BLACK);
    mPullToRefreshListView.setEmptyView(txtEmpty);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      View divider = new View(getActivity());
      divider.setBackgroundColor(getResources().getColor(
          android.R.color.transparent));
      lvNotification.addFooterView(divider);
    }

    adapter = new NotificationAdapter(getActivity(),
        R.layout.item_list_notification,
        new ArrayList<NotificationItem>());
    adapter.setOnItemClicked(onItemClicked);
    lvNotification.setAdapter(adapter);
  }

  private void requestNotification(String timeStamp) {
    String token = UserPreferences.getInstance().getToken();
    NotificationRequest notificationRequest = null;

    if (timeStamp == null || timeStamp.length() == 0) {

      if (onlyLike) {
        notificationRequest = new NotificationRequest(token, take,
            onlyLike);
      } else {
        notificationRequest = new NotificationRequest(token, take);
      }
    } else {

      if (onlyLike) {
        notificationRequest = new NotificationRequest(token, timeStamp,
            take, onlyLike);
      } else {
        notificationRequest = new NotificationRequest(token, timeStamp,
            take);
      }
    }

    restartRequestServer(LOADER_NOTIFICATION, notificationRequest);
  }

  /**
   * Setup Navigation bar for application
   */
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setCenterTitle(
        getResources().getString(R.string.application_notification_title));
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    getNavigationBar().setShowUnreadMessage(true);
  }

  private void showProfile(NotificationItem item) {
    replaceFragment(MyProfileFragment.newInstance(item.getUserId()));
  }

  private void showProfile(String userId) {
    if (TextUtils.isEmpty(userId)) {
      return;
    }
    replaceFragment(MyProfileFragment.newInstance(userId));
  }

  public void showListBackstage(String userId) {
    if (TextUtils.isEmpty(userId)) {
      return;
    }
    ManageBackstageActivity.startManagerBackstage(getActivity(), userId);
  }

  public void showMyPost(NotificationItem item) {
    replaceFragment(BuzzFragment.newInstance(false, BuzzFragment.TAB_MINE),
        MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  private void showBuzzDetail(NotificationItem item, boolean showKeyboard) {
    BuzzDetail fragmentBuzzDetail = BuzzDetail.newInstance(
        item.getBuzzId(), Constants.BUZZ_TYPE_NONE);
    fragmentBuzzDetail.showSoftkeyWhenStart(showKeyboard);
    replaceFragment(fragmentBuzzDetail,
        MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  private void handleUnlock(UnlockResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          showProfile(itemSelected.getUserId());
        }
      };
      Handler handler = new Handler();
      handler.post(runnable);
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          Fragment fragment = WebViewFragment
              .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
          ((MainActivity) getActivity()).getNavigationManager()
              .addPage(fragment);
        }
      };
      Handler handler = new Handler();
      handler.post(runnable);
    }
  }

  @Override
  public void startRequest(int loaderId) {
    if (getActivity() == null) {
      return;
    }
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getString(R.string.waiting));
      progressDialog.setCanceledOnTouchOutside(false);
    }
    if (adapter != null && adapter.getCount() > 0) {
      if (!progressDialog.isShowing()) {
        progressDialog.show();
      }
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    mPullToRefreshListView.onRefreshComplete();
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      return;
    }

    if (response instanceof NotificationResponse) {
      NotificationResponse data = (NotificationResponse) response;
      if (data.getList() != null && data.getList().size() > 0) {
        txtEmpty.setText("");
        UserPreferences.getInstance().saveNumberNotification(0);
        if (isRefresh) {
          adapter.refresh(data.getList());
          isRefresh = false;
        } else {
          adapter.append(data.getList());
        }
        if (data.getList().size() <= 0) {
          mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        }
      } else {
        txtEmpty.setText(R.string.no_more_items_to_show);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
      }
    } else if (response instanceof CheckUnlockResponse) {
      getLoaderManager().destroyLoader(LOADER_ID_CHECK_UNLOCK);
      // handleCheckUnlock((CheckUnlockResponse) response);
    } else if (response instanceof UnlockResponse) {
      getLoaderManager().destroyLoader(LOADER_ID_UNLOCK_TYPE);
      handleUnlock((UnlockResponse) response);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    if (loaderID == LOADER_NOTIFICATION) {
      response = new NotificationResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_UNLOCK) {
      response = new CheckUnlockResponse(data);
    } else if (loaderID == LOADER_ID_UNLOCK_TYPE) {
      response = new UnlockResponse(data);
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    adapter.clear();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (mUnlockDialog != null && mUnlockDialog.isShowing()) {
      mUnlockDialog.dismiss();
    }
    progressDialog = null;
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  public void onNavigationRightClick(View view) {
    getSlidingMenu().showSecondaryMenu(true);
  }

  public boolean isOnlyLike() {
    return onlyLike;
  }

  public void setOnlyLike(boolean onlyLike) {
    this.onlyLike = onlyLike;
  }

  public void refresh() {
    isRefresh = true;
    requestNotification(null);
    int resIdTitle;
    if (isOnlyLike()) {
      resIdTitle = R.string.list_like_notification;
    } else {
      resIdTitle = R.string.notifications;
    }
    mActionBar.setTextCenterTitle(resIdTitle);
  }

  public interface IOnItemClicked {

    public void onItemClick(int position);
  }

  private class NotificationAdapter extends ArrayAdapter<NotificationItem> {

    private Context context;
    private ArrayList<NotificationItem> list;

    private IOnItemClicked onItemClicked;

    public NotificationAdapter(Context context, int textViewResourceId,
        ArrayList<NotificationItem> objects) {
      super(context, textViewResourceId, objects);
      this.context = context;
      this.list = objects;
    }

    public void setOnItemClicked(IOnItemClicked onItemClicked) {
      this.onItemClicked = onItemClicked;
    }

    @Override
    public View getView(final int position, View convertView,
        ViewGroup parent) {
      NotificationItem item = getItem(position);
      HoldView holdView = null;
      if (convertView == null) {
        holdView = new HoldView();
        convertView = View.inflate(context,
            R.layout.item_list_notification, null);
        holdView.imgType = (ImageView) convertView
            .findViewById(R.id.img_notification_type);
        holdView.tvContent = (TextView) convertView
            .findViewById(R.id.tv_notification_content);
        holdView.tvDistance = (TextView) convertView
            .findViewById(R.id.tv_notification_distance);
        holdView.tvTime = (TextView) convertView
            .findViewById(R.id.tv_notification_time);

        convertView.setTag(holdView);
      } else {
        holdView = (HoldView) convertView.getTag();
      }
      filldata(holdView, item);
      if (onItemClicked != null) {
        convertView.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            onItemClicked.onItemClick(position);
          }
        });
      }
      return convertView;
    }

    private void filldata(HoldView holdView, final NotificationItem item) {
      String time = "";
      String msg = "";
      String distText = "";

      String username = item.getUserName() == null ? "" : item
          .getUserName();

      holdView.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
          0);

      int type = item.getType();
      int drawId = R.drawable.ic_notification_comment_buzz;
      switch (type) {
        case Constants.NOTI_REPLY_YOUR_COMMENT:
          drawId = R.drawable.ic_notification_comment_buzz;
          msg = String.format(
              getString(R.string.reply_comment_responded_to_notification),
              username);
          holdView.tvContent.setText(msg);
          break;

        case Constants.NOTI_COMMENT_BUZZ:
          drawId = R.drawable.ic_notification_comment_buzz;
          msg = String.format(
              getString(R.string.buzz_responded_to_notification),
              username);
          holdView.tvContent.setText(msg);
          break;

        case Constants.NOTI_ONLINE_ALERT:
          drawId = R.drawable.ic_notification_online_alert;
          msg = String.format(
              getString(R.string.came_online_notification),
              username);
          holdView.tvContent.setText(msg);
          break;

        case Constants.NOTI_DAYLY_BONUS:
          drawId = R.drawable.ic_point_bonus;
          msg = String.format(
              getString(R.string.earned_point_notification),
              item.getPoint() + "");
          holdView.tvContent.setText(msg);
          break;

        case Constants.NOTI_BACKSTAGE_APPROVED:
          drawId = R.drawable.ic_notification_image_approved;
          holdView.tvContent.setText(R.string.image_approved);
          break;

        case Constants.NOTI_BUZZ_APPROVED:
          holdView.tvContent.setText(R.string.image_approved);
          drawId = R.drawable.ic_notification_image_approved;
          break;

        case Constants.NOTI_FROM_FREE_PAGE:
          drawId = R.drawable.ic_free_page;
          msg = item.getContent();
          holdView.tvContent.setText(msg);
          break;
        case Constants.NOTI_REQUEST_CALL:
          drawId = R.drawable.ic_profile_online_alert;
          holdView.tvContent.setText(getString(R.string.request_call, username));
          break;
        case Constants.NOTI_LIKE_BUZZ:
          drawId = R.drawable.ic_notification_like_buzz;
          msg = String.format(
              getString(R.string.buzz_liked_to_notification),
              username);
          int firstIndex = msg.indexOf(username);
          int lastIndex = firstIndex + username.length();
          if (firstIndex >= 0 && lastIndex < msg.length()) {
            Spannable span = Spannable.Factory.getInstance()
                .newSpannable(msg);
            ClickableSpan cs = new ClickableSpan() {
              @Override
              public void onClick(View v) {
                MyProfileFragment fragment = MyProfileFragment
                    .newInstance(item.getUserId());
                mNavigationManager.addPage(fragment);
              }
            };
            span.setSpan(cs, firstIndex, lastIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holdView.tvContent.setText(span);
            holdView.tvContent.setMovementMethod(LinkMovementMethod
                .getInstance());
          } else {
            holdView.tvContent.setText(msg);
          }

          break;

        case Constants.NOTI_FAVORITED_CREATE_BUZZ:
          drawId = R.drawable.ic_notification_comment_buzz;
          msg = String.format(
              getString(R.string.buzz_created_notification),
              username);
          holdView.tvContent.setText(msg);
          break;

        case Constants.NOTI_DENIED_BUZZ_IMAGE:
          drawId = R.drawable.ic_notification_image_approved;
          holdView.tvContent.setText(R.string.denied_image_buzz);
          break;

        case Constants.NOTI_DENIED_BACKSTAGE:
          drawId = R.drawable.ic_notification_image_approved;
          holdView.tvContent.setText(R.string.denied_backstage);
          break;

        case Constants.NOTI_APPROVE_BUZZ_TEXT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.approve_text_buzz);
          break;

        case Constants.NOTI_APPROVE_COMMENT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.approve_comment);
          break;
        case Constants.NOTI_APPROVE_SUB_COMMENT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.approve_sub_comment);
          break;

        case Constants.NOTI_DENI_SUB_COMMENT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.denied_sub_comment);
          break;

        case Constants.NOTI_DENIED_BUZZ_TEXT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.denied_text_buzz);
          break;

        case Constants.NOTI_DENIED_COMMENT:
          drawId = R.drawable.ic_notification_comment_buzz;
          holdView.tvContent.setText(R.string.denied_comment);
          break;

        case Constants.NOTI_APPROVE_USERINFO:
          drawId = R.drawable.ic_profile_online_alert;
          holdView.tvContent.setText(R.string.approve_user_info);
          break;

        case Constants.NOTI_APART_OF_USERINFO:
          drawId = R.drawable.ic_profile_online_alert;
          holdView.tvContent.setText(R.string.apart_of_user_info);
          break;

        case Constants.NOTI_DENIED_USERINFO:
          drawId = R.drawable.ic_profile_online_alert;
          holdView.tvContent.setText(R.string.denied_user_info);
          break;
        default:
          break;
      }

      LogUtils.d("HungHN",
          "NotiFragment Message: " + holdView.tvContent.getText() + " ---- MessageType: " + type);

      distText = Utility.getDistanceString(getContext(), item.getDist());

      Date dateStart = Utility.convertLocaleToGMTNotMiliseconds(item
          .getTime());
      Calendar startDateTime = Calendar
          .getInstance(TimeZone.getDefault());
      startDateTime.setTime(dateStart);
      Calendar calendarNow = Calendar.getInstance();

      time = Utility.getDifference(getContext(), startDateTime,
          calendarNow);
      holdView.imgType.setImageResource(drawId);
      holdView.tvDistance.setText(distText);
      holdView.tvTime.setText(time);
    }

    public void append(ArrayList<NotificationItem> list) {
      this.list.addAll(list);
      notifyDataSetChanged();
    }

    public void refresh(ArrayList<NotificationItem> list) {
      this.clear();
      this.list.addAll(list);
      notifyDataSetChanged();
    }

    private class HoldView {

      public ImageView imgType;
      public TextView tvContent;
      public TextView tvDistance;
      public TextView tvTime;
    }
  }
}