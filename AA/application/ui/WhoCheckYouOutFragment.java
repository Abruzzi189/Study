package com.application.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.PeopleListRequest;
import com.application.connection.request.RequestParams;
import com.application.entity.PeopleConnection;
import com.application.ui.connection.BasePeopleListFragment;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;


public class WhoCheckYouOutFragment extends BasePeopleListFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    mListType = ListType.WHO_CHECKS_OUT_ME;
    return view;
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.who_checked_you_out);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setShowUnreadMessage(true);
  }

  @Override
  protected RequestParams getRequestParams(int take, int skip) {
    String token = UserPreferences.getInstance().getToken();
    return new PeopleListRequest(PeopleListRequest.WHO_CHECK_YOU_OUT,
        token, skip, take);
  }

  @Override
  protected View getEmptyViewWhenEmptyData() {
    View view = super.getEmptyViewWhenEmptyData();
    TextView txt = (TextView) view
        .findViewById(R.id.item_list_connection_common_empty_txt);
    txt.setText(R.string.empty_whochechyouout);
    txt = (TextView) view
        .findViewById(R.id.item_list_connection_common_text_guide_txt);
    txt.setText(R.string.text_guide_footprint);
    return view;
  }

  @Override
  protected void onRefreshCompleted(List<PeopleConnection> peopleConnections,
      int newItems) {
    // NOP
  }

  @Override
  protected void onRemoveItem() {
    // NOP
  }

  @Override
  public void onNavigationRightClick(View view) {
    super.onNavigationRightClick(view);
    getSlidingMenu().showSecondaryMenu(true);
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    getLoaderManager().destroyLoader(loader.getId());
    if (response.getCode() != Response.SERVER_SUCCESS
        && response.getCode() != Response.SERVER_NOT_ENOUGHT_MONEY
        && response.getCode() != Response.SERVER_RECEIVER_NOT_ENOUGH_MONEY) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      return;
    }
    if (response.getCode() != Response.SERVER_LOCKED_FEARUTE) {
      super.receiveResponse(loader, response);
    } else {
      Toast.makeText(getActivity(), R.string.must_unlock_to_view,
          Toast.LENGTH_LONG).show();
      Runnable runnable = new Runnable() {

        @Override
        public void run() {
          mNavigationManager.goBack();
        }
      };
      new Handler().post(runnable);
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
    return getResources().getString(R.string.text_guide_footprint);
  }

  //hiepuh
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mActionBar.syncActionBar(this);
  }
  //end
}
