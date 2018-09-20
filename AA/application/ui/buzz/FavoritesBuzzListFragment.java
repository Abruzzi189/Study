/**
 *
 */
package com.application.ui.buzz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.connection.request.BuzzListRequest;
import com.application.connection.request.RequestParams;
import com.application.util.preferece.UserPreferences;

/**
 * @author TuanPQ
 */
public class FavoritesBuzzListFragment extends BaseBuzzListFragment {

  public static final int PAGING_TAKE = 15;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    return view;
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    String token = UserPreferences.getInstance().getToken();
    return new BuzzListRequest(token, null, BuzzListRequest.FAVORITES,
        skip, take);
  }

  @Override
  protected void onRefreshCompleted() {
  }

  @Override
  protected boolean isControlNavigation() {
    return false;
  }
}