package com.application.ui.connection;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.PeopleListRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.response.UserInfoResponse;
import com.application.entity.PeopleConnection;
import com.application.ui.MainActivity;
import com.application.ui.connection.ConnectionFragment.OnChangeItem;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;


public class FavouriteListFragment extends BasePeopleListFragment {

  private final int LOADER_ID_USER_INFO = 0;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    getNavigationBar().setNavigationLeftLogo(R.drawable.ic_back);
    mListType = ListType.MY_FAVORITE_LIST;
    int favoriteNum = UserPreferences.getInstance().getNumberFavorite();
    updateTitle(favoriteNum);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    onRefresh();
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    String token = UserPreferences.getInstance().getToken();
    return new PeopleListRequest(PeopleListRequest.FAVORITE, token, skip,
        take);
  }

  @Override
  protected boolean isControlNavigation() {
    return false;
  }

  @Override
  protected View getEmptyViewWhenEmptyData() {
    View view = super.getEmptyViewWhenEmptyData();
    TextView txt = (TextView) view
        .findViewById(R.id.item_list_connection_common_empty_txt);
    txt.setText(R.string.empty_favorites);
    txt = (TextView) view
        .findViewById(R.id.item_list_connection_common_text_guide_txt);
    txt.setText(R.string.text_guide_favorite);
    return view;
  }

  @Override
  protected void onRefreshCompleted(List<PeopleConnection> peopleConnections,
      int newItems) {
    if (getActivity() == null) {
      return;
    }
    ((MainActivity) getActivity())
        .onMainMenuUpdate(MainActivity.UPDATE_CONNECTIONS);
  }

  @Override
  protected void updateTitle(int count) {
    super.updateTitle(count);
    OnChangeItem changeItem = getOnChangeItem();
    if (changeItem != null) {
      changeItem.onFavorites(count);
    }
  }

  @Override
  protected void onRemoveItem() {
    FragmentActivity parent = getActivity();
    if (parent != null && parent instanceof MainActivity) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.decreaseFriend();
      ((MainActivity) parent)
          .onMainMenuUpdate(MainActivity.UPDATE_CONNECTIONS);
    }
  }

  @Override
  protected RequestParams getRequestParamsLoadMore() {
    return null;
  }

  @Override
  protected Response loadMoreResponse(ResponseData data) {
    return null;
  }

  @Override
  protected String getHeaderListView() {
    return getResources().getString(R.string.text_guide_favorite);
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    int loaderId = loader.getId();
    switch (loaderId) {
      case LOADER_ID_USER_INFO:
        if (response instanceof UserInfoResponse) {
          handleUserInfoResponse((UserInfoResponse) response);
        }
        break;
    }
  }

  private void handleUserInfoResponse(UserInfoResponse response) {
    int favoritedNum = response.getFavouritedNumber();
    updateTitle(favoritedNum);
    UserPreferences.getInstance().saveNumberFavorite(favoritedNum);
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = super.parseResponse(loaderID, data, requestType);
    switch (loaderID) {
      case LOADER_ID_USER_INFO:
        response = new UserInfoResponse(data);
        break;
      default:
    }
    return response;
  }

  @Override
  protected void onRefresh() {
    super.onRefresh();
    String token = UserPreferences.getInstance().getToken();
    UserInfoRequest request = new UserInfoRequest(token);
    restartRequestServer(LOADER_ID_USER_INFO, request);
  }
}