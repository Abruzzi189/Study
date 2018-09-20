package com.application.ui.connection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetAttendtionNumberRequest;
import com.application.connection.response.GetAttendtionNumberResponse;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.connection.WhoFavouriteMeFragment.onLoadWhoFarvoriteMe;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.Random;


public class ConnectionFragment extends BaseFragment implements
    OnTabChangeListener, OnNavigationClickListener, ResponseReceiver,
    onLoadWhoFarvoriteMe {

  public static final int TAB_FRIENDS_INDEX = 2;
  public static final int TAB_FAVORITE_INDEX = 1;
  public static final int TAB_WHO_INDEX = 0;
  private static final String TAG = "ConnectionFragment";
  private static final String KEY_CURRENT_TAB_INDEX = "CURRENT_TAB_INDEX";
  private static final String KEY_IS_NAVIGATION_BACK = "IS_NAVIGATION_BACK";
  private static final String IS_NAVIGATION_BACK = "is_back";
  private static final String FOCUS_TAB_INDEX = "focus_tab_index";
  private static final int LOADER_ID_LOAD_ATTENDTION = 100;
  public final String TAB_FRIENDS = "Connection_Friends"
      + new Random().nextInt() + "";
  public final String TAB_FAVORITE = "Connection_Favorites"
      + new Random().nextInt() + "";
  public final String TAB_WHO = "Who" + new Random().nextInt() + "";
  public TextView mtxtFavorite;
  public TextView mtxtWhoFavoritesMe;
  public OnChangeItem onChangeItem = new OnChangeItem() {
    @Override
    public void onFavorites(int num) {
      mtxtFavorite.setText("" + num);
    }

    @Override
    public void onWhoFavorites(int num) {
      mtxtWhoFavoritesMe.setText("" + num);
    }
  };
  private TabHost mTabHost;
  private boolean mIsNavigationBack;
  private String mTabIdFocus;
  private int mCurrentIndexTab = -1;
  private Bundle mInstanceState;

  public static ConnectionFragment newInstance(boolean isNavigationBack,
      int focusTabIndex) {
    ConnectionFragment fragment = new ConnectionFragment();
    Bundle bundle = new Bundle();
    bundle.putBoolean(IS_NAVIGATION_BACK, isNavigationBack);
    bundle.putInt(FOCUS_TAB_INDEX, focusTabIndex);
    fragment.setArguments(bundle);
    return fragment;
  }

  public Bundle saveInstanceState() {
    Bundle state = new Bundle();
    state.putInt(KEY_CURRENT_TAB_INDEX, mCurrentIndexTab);
    state.putBoolean(KEY_IS_NAVIGATION_BACK, mIsNavigationBack);
    return state;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplicationContext();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // removeAllTab();
    View view = inflater.inflate(R.layout.fragment_connection, container,
        false);
    if (getArguments() != null) {
      mIsNavigationBack = getArguments().getBoolean(IS_NAVIGATION_BACK);
      mCurrentIndexTab = getArguments().getInt(FOCUS_TAB_INDEX);
    }
    initView(view);
    requestGetAttendtion();
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      mCurrentIndexTab = savedInstanceState.getInt(KEY_CURRENT_TAB_INDEX,
          -1);
    } else if (mInstanceState != null) {
      mCurrentIndexTab = mInstanceState.getInt(KEY_CURRENT_TAB_INDEX, -1);
    }
    mTabHost.setCurrentTab(mCurrentIndexTab);
    if (mCurrentIndexTab == 0) {
      // invoke listener
      onTabChanged(TAB_WHO);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // outState.putInt(KEY_CURRENT_TAB_INDEX, mCurrentIndexTab);
    outState.putBoolean(KEY_IS_NAVIGATION_BACK, mIsNavigationBack);
  }

  private void initView(View view) {
    mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
    mTabHost.setup();
    mTabHost.getTabWidget().setDividerDrawable(
        android.R.drawable.divider_horizontal_bright);
    String whoFavMe = getString(R.string.connection_tab_who_favorites_me);
    mTabHost.addTab(newTabSpec(mTabHost.getContext(), TAB_WHO,
        R.id.fragment_connection_tab_who, whoFavMe));

    String favorites = getString(R.string.connection_tab_favorites);
    mTabHost.addTab(newTabSpec(mTabHost.getContext(), TAB_FAVORITE,
        R.id.fragment_connection_tab_favorites, favorites));

    mTabHost.setOnTabChangedListener(this);
  }

  private TabSpec newTabSpec(Context context, String tag, int idContentView,
      String title) {
    TabSpec tabSpec = mTabHost.newTabSpec(tag);
    tabSpec.setIndicator(getTabIndicator(context, tag, title));
    tabSpec.setContent(idContentView);
    return tabSpec;
  }

  private View getTabIndicator(Context context, String tag, String title) {
    View view = LayoutInflater.from(context).inflate(
        R.layout.tab_connection_widget, null);
    TextView tv = null;
    if (tag.equals(TAB_FAVORITE)) {
      tv = (TextView) view
          .findViewById(R.id.tab_connection_widget_title_favorite);
      mtxtFavorite = (TextView) view
          .findViewById(R.id.tab_connection_widget_title_favorite_num);
      int favoriteNum = UserPreferences.getInstance().getNumberFavorite();
      mtxtFavorite.setText(String.valueOf(favoriteNum));
      mtxtFavorite.setTextColor(getResources().getColor(R.color.text_selected));
      mtxtFavorite.setVisibility(View.VISIBLE);
    } else if (tag.equals(TAB_WHO)) {
      tv = (TextView) view
          .findViewById(R.id.tab_connection_widget_title_who);
      mtxtWhoFavoritesMe = (TextView) view
          .findViewById(R.id.tab_connection_widget_title_who_num);
      int favoriteNum = UserPreferences.getInstance()
          .getNumberFavoritedMe();
      mtxtWhoFavoritesMe.setText("" + favoriteNum);
      mtxtWhoFavoritesMe.setTextColor(getResources().getColor(R.color.text_selected));
      mtxtWhoFavoritesMe.setVisibility(View.VISIBLE);
    }
    if (tv != null) {
      tv.setVisibility(View.VISIBLE);
      tv.setText(title);
    }
    return view;
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    // handle title
    if (mTabIdFocus != null) {
      handleTitle(mTabIdFocus);
    }
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    if (mIsNavigationBack) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    } else {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    }
    getNavigationBar().setShowUnreadMessage(true);
  }

  @Override
  public void onTabChanged(String tabId) {
    mTabIdFocus = tabId;
    FragmentManager fm = getFragmentManager();
    BaseFragment baseFragment = (BaseFragment) fm.findFragmentByTag(tabId);
    handleTitle(tabId);
    handleNativeActionbarTitle(tabId);
    if (baseFragment == null) {
      int placeholder = -1;
      if (tabId.equals(TAB_FAVORITE)) {
        placeholder = R.id.fragment_connection_tab_favorites;
        baseFragment = new FavouriteListFragment();
      } else if (tabId.equals(TAB_WHO)) {
        placeholder = R.id.fragment_connection_tab_who;
        baseFragment = new WhoFavouriteMeFragment();
        ((WhoFavouriteMeFragment) baseFragment)
            .setOnLoadWhoFarvoriteMe(this);
      }
      if (baseFragment != null) {
        ((BasePeopleListFragment) baseFragment)
            .setOnChangeItem(onChangeItem);
        fm.beginTransaction().replace(placeholder, baseFragment, tabId)
            .commit();
      }
    } else {
      LogUtils.i(TAG, tabId + " exist");
    }
  }

  // public void initTab() {
  // FragmentManager fm = getFragmentManager();
  // // String tabId = Preferences.getInstance(getActivity())
  // // .getConnectionTab();
  // String tabId = mTabIdFocus;
  // LogUtils.e("tabShow", "tabShow=" + tabId);
  // handleTitle(tabId);
  //
  // if (fm.findFragmentByTag(tabId) == null) {
  // int placeholder = R.id.fragment_connection_tab_friends;
  // BasePeopleListFragment fragment = new FriendsListFragment();
  // fm.beginTransaction().replace(placeholder, fragment, tabId)
  // .commit();
  // fragment.setOnChangeItem(onChangeItem);
  // if (tabId == TAB_FRIENDS) {
  // mTabHost.setCurrentTab(0);
  // } else if (tabId == TAB_FAVORITE) {
  // mTabHost.setCurrentTab(1);
  // } else if (tabId == TAB_WHO) {
  // mTabHost.setCurrentTab(2);
  // }
  // } else {
  // LogUtils.e("tabShow", "not null");
  // fm.beginTransaction().remove(fm.findFragmentByTag(tabId));
  // // BasePeopleListFragment fragment = null;
  // BaseFragment fragment = null;
  // int placeholder = -1;
  // if (tabId == TAB_FRIENDS) {
  // placeholder = R.id.fragment_connection_tab_friends;
  // fragment = new FriendsListFragment();
  // mTabHost.setCurrentTab(0);
  // } else if (tabId == TAB_FAVORITE) {
  // placeholder = R.id.fragment_connection_tab_favorites;
  // fragment = new FavouriteListFragment();
  // mTabHost.setCurrentTab(1);
  // } else if (tabId == TAB_WHO) {
  // placeholder = R.id.fragment_connection_tab_who;
  // // fragment = new WhoFavouriteMeFragment();
  // mTabHost.setCurrentTab(2);
  // checkUnlock();
  // }
  // if (fragment != null) {
  // if (fragment instanceof BasePeopleListFragment) {
  // ((BasePeopleListFragment) fragment)
  // .setOnChangeItem(onChangeItem);
  // }
  // fm.beginTransaction().replace(placeholder, fragment, tabId)
  // .commit();
  // }
  // }
  // }

  private void handleTitle(String tabId) {
    if (tabId.equals(TAB_FRIENDS)) {
      getNavigationBar()
          .setCenterTitle(R.string.connection_title_friends);
      mCurrentIndexTab = TAB_FRIENDS_INDEX;
    } else if (tabId.equals(TAB_FAVORITE)) {
      getNavigationBar().setCenterTitle(
          R.string.connection_title_favorites);
      mCurrentIndexTab = TAB_FAVORITE_INDEX;
    } else if (tabId.equals(TAB_WHO)) {
      getNavigationBar().setCenterTitle(
          R.string.connection_title_who_favorites_me);
      mCurrentIndexTab = TAB_WHO_INDEX;
    }
  }

  private void handleNativeActionbarTitle(String tabId) {
    if (mActionBar != null) {
      if (tabId.equals(TAB_FRIENDS)) {
        mActionBar
            .setTextCenterTitle(R.string.connection_title_friends);
        mCurrentIndexTab = TAB_FRIENDS_INDEX;
      } else if (tabId.equals(TAB_FAVORITE)) {
        mActionBar
            .setTextCenterTitle(R.string.connection_title_favorites);
        mCurrentIndexTab = TAB_FAVORITE_INDEX;
      } else if (tabId.equals(TAB_WHO)) {
        mActionBar
            .setTextCenterTitle(R.string.connection_title_who_favorites_me);
        mCurrentIndexTab = TAB_WHO_INDEX;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mInstanceState = saveInstanceState();
    // tungdx: not call removeAllTab() when click back key and
    // isSaveInstanceCalled=true
    Activity activity = getActivity();

    if (!((MainActivity) baseFragmentActivity).isBackPressed
        && !isSaveInstanceCalled && activity != null
        && !activity.isFinishing()) {
      removeAllTab();
    }

  }

  /**
   * Remove all tab in connection fragment
   */
  private void removeAllTab() {
    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager != null) {
      FragmentTransaction fragmentTransaction = fragmentManager
          .beginTransaction();
      Fragment favorite = fragmentManager.findFragmentByTag(TAB_FAVORITE);
      Fragment who = fragmentManager.findFragmentByTag(TAB_WHO);
      Fragment friends = fragmentManager.findFragmentByTag(TAB_FRIENDS);
      boolean hasTransaction = false;
      if (null != favorite) {
        hasTransaction = true;
        fragmentTransaction.remove(favorite);
        LogUtils.i(TAG, "Remove favorite");
      }
      if (null != friends) {
        hasTransaction = true;
        fragmentTransaction.remove(friends);
        LogUtils.i(TAG, "Remove friends");
      }
      if (null != who) {
        hasTransaction = true;
        fragmentTransaction.remove(who);
        LogUtils.i(TAG, "Remove who favorite you");
      }
      if (hasTransaction) {
        fragmentTransaction.commitAllowingStateLoss();
      }
    }
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    getSlidingMenu().showSecondaryMenu(true);
  }

  @Override
  public void onLoad(int num) {
    LogUtils.d("ConnectionFragment", "onLoad");
    if (mtxtWhoFavoritesMe != null) {
      mtxtWhoFavoritesMe.setText(Integer.toString(num));
    }
  }

  private void requestGetAttendtion() {
    GetAttendtionNumberRequest getAttendRequest = new GetAttendtionNumberRequest(
        UserPreferences.getInstance().getToken());
    restartRequestServer(LOADER_ID_LOAD_ATTENDTION, getAttendRequest);
  }

  @Override
  public void startRequest(int loaderId) {

  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    getLoaderManager().destroyLoader(loader.getId());
    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      return;
    }

    if (response instanceof GetAttendtionNumberResponse) {
      handleGetAttendtionResponse((GetAttendtionNumberResponse) response);
    }

  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_LOAD_ATTENDTION) {
      return new GetAttendtionNumberResponse(data);
    }
    return null;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  public void handleGetAttendtionResponse(GetAttendtionNumberResponse response) {
    int num = response.getFvt_num();
    if (mtxtWhoFavoritesMe != null) {
      mtxtWhoFavoritesMe.setText(String.valueOf(num));
    }
    UserPreferences.getInstance().saveNumberFavoritedMe(num);
  }

  public interface OnChangeItem {

    public void onFavorites(int num);

    public void onWhoFavorites(int num);
  }

}