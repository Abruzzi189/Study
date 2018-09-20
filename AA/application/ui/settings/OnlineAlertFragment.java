package com.application.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.ListOnlineAlertRequest;
import com.application.connection.response.ListOnlineAlertResponse;
import com.application.entity.OnlineAlertItem;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.notification.ManageOnlineAlertFragment;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;

public class OnlineAlertFragment extends BaseFragment implements
    OnNavigationClickListener, ResponseReceiver {

  private static final int LOADER_ID_ONLINE_ALERT_LIST = 0;

  private MainActivity mMainActivity;
  private View mView;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mOnlineAlertListView;
  // private View mOnlineAlertListFooterView;
  private ListOnlineAlertAdapter mListOnlineAlertAdapter;
  private int mAvatarSize;
  private String mToken;
  private View headerView;
  private OnAlertClick onAlertClick = new OnAlertClick() {

    @Override
    public void onItemClick(int position) {
      // navigate to profile
      OnlineAlertItem user = mListOnlineAlertAdapter.getItem(position);
      String userId = user.getUserId();
      MyProfileFragment myProfileFragment = MyProfileFragment
          .newInstance(userId, true);
      replaceFragment(myProfileFragment);
    }

    @Override
    public void onButtonClick(int position) {
      // navigate to manager online alert
      OnlineAlertItem user = mListOnlineAlertAdapter.getItem(position);
      ManageOnlineAlertFragment managerOnlineAlertFragment = ManageOnlineAlertFragment
          .newInstance(user.getUserId(), user.getAvaId(),
              user.getUserName(), 1);
      replaceFragment(managerOnlineAlertFragment);

    }
  };
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      // mOnlineAlertListView.removeFooterView(mOnlineAlertListFooterView);
      mListOnlineAlertAdapter.clearAllData();
      startRequestServer();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_online_alert, container,
        false);
    initView(mView);
    return mView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);
    mAvatarSize = getResources().getDimensionPixelSize(
        R.dimen.img_avata_ac_setup_profile);

    mListOnlineAlertAdapter = new ListOnlineAlertAdapter(mMainActivity,
        onAlertClick);
    mOnlineAlertListView.setAdapter(mListOnlineAlertAdapter);
    mOnlineAlertListView.setDividerHeight(0);
    mOnlineAlertListView.setCacheColorHint(Color.TRANSPARENT);

    startRequestServer();
  }

  private void startRequestServer() {
    mPullToRefreshListView.setPullLabelFooter(getResources().getString(
        R.string.pull_to_load_more_pull_label));
    mToken = UserPreferences.getInstance().getToken();
    restartRequestServer(LOADER_ID_ONLINE_ALERT_LIST,
        new ListOnlineAlertRequest(mToken));
  }

  private void initView(View v) {

    mPullToRefreshListView = (PullToRefreshListView) v
        .findViewById(R.id.fr_online_alert);
    mOnlineAlertListView = mPullToRefreshListView.getRefreshableView();
    // Set Header View for list
    headerView = LayoutInflater.from(mAppContext).inflate(
        R.layout.header_list_view, null);
    TextView txt = (TextView) headerView.findViewById(R.id.txt_header);
    txt.setText(R.string.text_guide_online_alert);
    headerView.setBackgroundColor(getResources().getColor(
        android.R.color.transparent));
    mOnlineAlertListView.addHeaderView(headerView);

    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);

    // mOnlineAlertListFooterView = View.inflate(mMainActivity,
    // R.layout.fragment_blocked_users_list_footer, null);
    // TextView textFooter = (TextView) mOnlineAlertListFooterView
    // .findViewById(R.id.text_footer);
    // textFooter.setText(R.string.no_more_items_to_show);

    mOnlineAlertListView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
          getImageFetcher().setPauseWork(true);
        } else {
          getImageFetcher().setPauseWork(false);
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    });

  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.settings_online_alert);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {

    Response response = null;
    switch (loaderID) {
      case LOADER_ID_ONLINE_ALERT_LIST:
        response = new ListOnlineAlertResponse(data);
        break;
      default:
        break;
    }
    return response;
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {

    if (mMainActivity == null || loader == null || response == null) {
      if (loader != null) {
        getLoaderManager().destroyLoader(loader.getId());
      }
      return;
    }

    switch (loader.getId()) {
      case LOADER_ID_ONLINE_ALERT_LIST:
        getLoaderManager().destroyLoader(LOADER_ID_ONLINE_ALERT_LIST);
        handleOnlineAlertList((ListOnlineAlertResponse) response);
        break;
      default:
        break;
    }

  }

  private void handleOnlineAlertList(ListOnlineAlertResponse response) {

    boolean endOfList = false;
    if (response.getCode() == Response.SERVER_SUCCESS) {
      if (response.getOnlineAlertList() != null) {
        if (response.getOnlineAlertList().size() > 0) {
          mListOnlineAlertAdapter.appendList(response
              .getOnlineAlertList());
        } else {
          endOfList = true;
        }
      }
    }

    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.onRefreshComplete();
    }

    if (mListOnlineAlertAdapter.getCount() == 0) {
      getLoaderManager().destroyLoader(LOADER_ID_ONLINE_ALERT_LIST);
      // mOnlineAlertListView.addFooterView(mOnlineAlertListFooterView);
      // mOnlineAlertListFooterView.setVisibility(View.VISIBLE);
      headerView.setBackgroundColor(getResources().getColor(
          android.R.color.transparent));
    } else {
      headerView.setBackgroundColor(getResources().getColor(
          android.R.color.white));
    }

    if (endOfList) {
      mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
    }

  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  public void onDetach() {
    mMainActivity = null;
    super.onDetach();
  }

  private interface OnAlertClick {

    void onItemClick(int position);

    void onButtonClick(int position);
  }

  protected class ListOnlineAlertAdapter extends
      ArrayAdapter<OnlineAlertItem> {

    private List<OnlineAlertItem> mListUsers = new ArrayList<OnlineAlertItem>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnAlertClick onAlertClick;

    private ListOnlineAlertAdapter(Context context, int resource) {
      super(context, resource);
      mContext = context;
      mLayoutInflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ListOnlineAlertAdapter(Context context, OnAlertClick onAlertClick) {
      this(context, 0);
      this.onAlertClick = onAlertClick;
    }

    @Override
    public int getCount() {
      return mListUsers.size();
    }

    @Override
    public OnlineAlertItem getItem(int position) {
      return mListUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public void remove(int position) {
      mListUsers.remove(position);
      this.notifyDataSetChanged();
    }

    public void appendList(List<OnlineAlertItem> users) {
      mListUsers.addAll(users);
      this.notifyDataSetChanged();
    }

    /**
     * Remove all elements from this adapter, leaving it empty
     */
    public void clearAllData() {
      mListUsers.clear();
      this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView,
        ViewGroup parent) {
      ViewHolder holder = null;
      final OnlineAlertItem bui = mListUsers.get(position);

      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mLayoutInflater.inflate(
            R.layout.item_list_online_alert, parent, false);
        holder.imageViewAvatar = (ImageView) convertView
            .findViewById(R.id.iv_avatar);
        holder.textViewUser = (TextView) convertView
            .findViewById(R.id.tv_user_name);
        holder.buttonAction = (Button) convertView
            .findViewById(R.id.bt_action);
        holder.viewLineTop = convertView.findViewById(R.id.top_line);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      // Show top line
      if (position == 0) {
        holder.viewLineTop.setVisibility(View.VISIBLE);
      }
      CircleImageRequest imageRequest = new CircleImageRequest(mToken,
          bui.getAvaId());
      getImageFetcher().loadImageByGender(imageRequest,
          holder.imageViewAvatar, mAvatarSize, bui.getGender());
      holder.textViewUser.setText(bui.getUserName());
      holder.buttonAction.setTag(position);
      convertView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          if (onAlertClick != null) {
            onAlertClick.onItemClick(position);
          }
        }
      });
      holder.buttonAction.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          if (onAlertClick != null) {
            onAlertClick.onButtonClick(position);
          }
        }
      });

      return convertView;
    }

    class ViewHolder {

      public ImageView imageViewAvatar;
      public TextView textViewUser;
      public Button buttonAction;
      public View viewLineTop;
    }

  }
}
