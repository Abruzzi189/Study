package com.application.ui.buzz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.connection.request.BuzzListRequest;
import com.application.connection.request.RequestParams;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class UserBuzzListFragment extends BaseBuzzListFragment {

  public static final int PAGING_TAKE = 10;

  private static final String TAG = "UserBuzzListFragment";
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_USER_NAME = "user_name";
  private String mUserId = "";
  private String mUserName = "";

  public static UserBuzzListFragment newInstance(String userId,
      String userName) {
    LogUtils.d(TAG, "newInstance Started");

    UserBuzzListFragment instance = new UserBuzzListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putString(KEY_USER_NAME, userName);
    instance.setArguments(bundle);
    instance.setPagingParams(UserBuzzListFragment.PAGING_TAKE);
    instance.mListType = ListType.USER;
    instance.showCreateNewBuzzView(false);

    LogUtils.d(TAG, "newInstance Ended");

    return instance;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);

    if (getArguments() != null) {
      mUserId = getArguments().getString(KEY_USER_ID);
      mUserName = getArguments().getString(KEY_USER_NAME);
    }

    return view;
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    String token = UserPreferences.getInstance().getToken();
    return new BuzzListRequest(token, mUserId, BuzzListRequest.USER, skip,
        take);
  }

  @Override
  protected void onRefreshCompleted() {
    // NOP
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(
        R.string.item_profile_info_view_all_buzz);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setShowUnreadMessage(false);
  }
}