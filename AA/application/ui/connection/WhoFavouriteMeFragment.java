package com.application.ui.connection;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.GetAttendtionNumberRequest;
import com.application.connection.request.PeopleListRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.GetAttendtionNumberResponse;
import com.application.entity.PeopleConnection;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;


public class WhoFavouriteMeFragment extends BasePeopleListFragment {

  private onLoadWhoFarvoriteMe mOnLoad;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    mListType = ListType.WHO_FAVORITES_ME;
    return view;
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    String token = UserPreferences.getInstance().getToken();
    return new PeopleListRequest(PeopleListRequest.FAVORITED, token, skip,
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
    txt.setText(R.string.empty_whofavorites);
    txt = (TextView) view
        .findViewById(R.id.item_list_connection_common_text_guide_txt);
    txt.setText(R.string.text_guide_follower);
    return view;
  }

  @Override
  protected void onRefreshCompleted(List<PeopleConnection> peopleConnections,
      int newItems) {
  }

  @Override
  protected void onRemoveItem() {
    // NOP
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
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
      handleResponse((GetAttendtionNumberResponse) response);
    }
  }

  private void handleResponse(GetAttendtionNumberResponse response) {
    int num = response.getFvt_num();
    LogUtils.d("ConnectionResponse", response.toString());
    LogUtils.d("Number", Integer.toString(num));
    if (mOnLoad != null) {
      mOnLoad.onLoad(num);
    }
  }

  public void setOnLoadWhoFarvoriteMe(onLoadWhoFarvoriteMe mOnLoad) {
    this.mOnLoad = mOnLoad;
  }

  @Override
  protected RequestParams getRequestParamsLoadMore() {
    GetAttendtionNumberRequest getAttendRequest = new GetAttendtionNumberRequest(
        UserPreferences.getInstance().getToken());
    return getAttendRequest;

  }

  @Override
  protected Response loadMoreResponse(ResponseData data) {
    return new GetAttendtionNumberResponse(data);
  }

  @Override
  protected String getHeaderListView() {
    return getResources().getString(R.string.text_guide_follower);
  }

  public interface onLoadWhoFarvoriteMe {

    public void onLoad(int num);
  }
}
