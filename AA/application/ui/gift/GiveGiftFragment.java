package com.application.ui.gift;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AllCategoriesGiftRequest;
import com.application.connection.response.AllCategoriesGiftResponse;
import com.application.entity.GiftCategories;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.TrackingBlockFragment;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.Locale;


public class GiveGiftFragment extends TrackingBlockFragment implements
    ResponseReceiver, OnItemClickListener, OnNavigationClickListener {

  public static final String TAG_FRAGMENT_GIVE_GIFT = "giveGiftfragment";
  public final static String KEY_RECEIVE_USER_ID = "receive_user_id";
  public final static String KEY_RECEIVE_USER_NAME = "receive_user_name";
  private static final int REQUEST_SEND_GIFT = 1000;
  private final static int LOADER_ALL_CATEGORIES_GIFT = 0;
  private View mView;
  private String receiveUserId;
  private String receiveUserName;
  private ProgressDialog progressDialog;
  private ListView listCategory;
  private CategoriesAdapter adapter;
  private ArrayList<GiftCategories> list;

  public static GiveGiftFragment newInstance(String userId, String userName) {
    GiveGiftFragment fragment = new GiveGiftFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_RECEIVE_USER_ID, userId);
    bundle.putString(KEY_RECEIVE_USER_NAME, userName);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_give_gift, container, false);
    Utility.hideKeyboard(getActivity(), mView);
    if (getArguments() != null) {
      receiveUserId = getArguments().getString(KEY_RECEIVE_USER_ID);
      receiveUserName = getArguments().getString(KEY_RECEIVE_USER_NAME);
    }
    initialListview(mView);
    requestAllCategoriesGift();
    return mView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  private void initialListview(View view) {
    listCategory = (ListView) view
        .findViewById(R.id.fragment_gift_lv_categories);
    listCategory.setOnItemClickListener(this);
    list = new ArrayList<GiftCategories>();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) activity;
    baseFragmentActivity.setOnNavigationClickListener(this);
  }

  /**
   * Setup Navigation bar for application
   */
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setCenterTitle(
        getResources().getString(
            R.string.title_textview_my_profile_give_gift));
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setShowUnreadMessage(true);
  }

  private void requestAllCategoriesGift() {
    String token = UserPreferences.getInstance().getToken();
    AllCategoriesGiftRequest allCategoriesGiftRequest = new AllCategoriesGiftRequest(
        token, Locale.getDefault().getLanguage());
    requestServer(LOADER_ALL_CATEGORIES_GIFT, allCategoriesGiftRequest);
  }

  @Override
  public void startRequest(int loaderId) {
    if (getActivity() == null) {
      return;
    }
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage(getActivity().getResources().getString(
        R.string.waiting));
    progressDialog.show();
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    if (response instanceof AllCategoriesGiftResponse) {
      AllCategoriesGiftResponse data = (AllCategoriesGiftResponse) response;
      if (response.getCode() == Response.SERVER_SUCCESS) {
        list.add(new GiftCategories("get_all_gift", 0, getResources()
            .getString(R.string.give_gift_all_title), 1));
        list.addAll(data.getCategories());
        adapter = new CategoriesAdapter(getActivity(),
            R.layout.item_give_gift, list);
        listCategory.setAdapter(adapter);
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    if (loaderID == LOADER_ALL_CATEGORIES_GIFT) {
      response = new AllCategoriesGiftResponse(data);
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
        receiveUserId, receiveUserName, list.get(position));
    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_SEND_GIFT);
    replaceFragment(chooseGiftToSend);
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_SEND_GIFT
        && resultCode == Activity.RESULT_OK) {
      if (getTargetFragment() != null) {
        getTargetFragment().onActivityResult(getTargetRequestCode(),
            Activity.RESULT_OK, data);
      }
    }
  }

  @Override
  protected String getUserIdTracking() {
    return receiveUserId;
  }

  private class CategoriesAdapter extends ArrayAdapter<GiftCategories> {

    public CategoriesAdapter(Context context, int textViewResourceId,
        ArrayList<GiftCategories> objects) {
      super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView tvDisplay = null;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) getContext()
            .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_give_gift, null);
        tvDisplay = (TextView) convertView.findViewById(R.id.tvDisplay);
        convertView.setTag(tvDisplay);
      } else {
        tvDisplay = (TextView) convertView.getTag();
      }
      tvDisplay.setText(getItem(position).getName());
      return convertView;
    }

  }

}
