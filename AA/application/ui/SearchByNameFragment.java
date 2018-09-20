package com.application.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.SearchByNameRequest;
import com.application.connection.response.SearchByNameResponse;
import com.application.entity.MeetPeople;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.TrimmedTextView;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.LogUtils;
import com.application.util.RegionUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class SearchByNameFragment extends BaseFragment implements
    ResponseReceiver, OnItemClickListener, OnClickListener {

  private static final String TAG = "SearchByNameFragment";
  private static final int LOADER_ID_SEARCH_BY_NAME = 100;

  private int mTake = 0;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mListViewSearch;
  private EditText mEdtQuery;
  private TextView mTxtNotFound;
  private ImageView mImgClearText;
  private View emptyView;

  private RegionUtils mRegionUtils;
  private ProgressDialog mProgressDialog;
  private SearchByNameAdapter mSearchByNameAdapter;
  private String mQuery;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      queryServer(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      queryServer(false);
    }
  };
  private TextWatcher textWatcher = new TextWatcher() {

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
        int count) {

      if (TextUtils.isEmpty(s)) {
        mImgClearText.setVisibility(View.GONE);

      } else {
        mImgClearText.setVisibility(View.VISIBLE);
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  public static SearchByNameFragment newInstance() {
    return new SearchByNameFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTake = getResources().getInteger(R.integer.take_people_list);
    LogUtils.e(TAG, "onCreate");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_search_by_name,
        container, false);

    initViews(view);

    LogUtils.e(TAG, "onCreateView");
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mRegionUtils = new RegionUtils(getActivity());

    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setMessage(getString(R.string.waiting));
    mProgressDialog.setCancelable(false);

    if (mSearchByNameAdapter == null) {
      List<MeetPeople> people = new ArrayList<MeetPeople>();
      mSearchByNameAdapter = new SearchByNameAdapter(getActivity(),
          people);

    } else {
      if (mSearchByNameAdapter.getCount() % mTake == 0) {
        mPullToRefreshListView.setMode(Mode.BOTH);

      } else {
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
      }
    }
    mListViewSearch.setAdapter(mSearchByNameAdapter);

    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);

    mEdtQuery.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId,
          KeyEvent event) {
        boolean handled = false;

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          handled = true;
          String query = mEdtQuery.getText().toString()
              .replace("\u3000", " ").trim();

          if (TextUtils.isEmpty(query)) {
            ErrorApiDialog
                .showAlert(
                    getActivity(),
                    getString(R.string.search_by_name_dilog_title),
                    getString(R.string.search_by_name_dialog_message));

          } else {
            Utility.hideSoftKeyboard(getActivity());
            mProgressDialog.show();

            mQuery = query;
            queryServer(true);
          }
        }

        return handled;
      }
    });

    LogUtils.e(TAG, "onActivityCreated");
  }

  @Override
  public void onResume() {
    super.onResume();

    LogUtils.e(TAG, "onResume");
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    Utility.hideSoftKeyboard(getActivity());

    LogUtils.e(TAG, "onDestroyView");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.e(TAG, "onDestroy");
  }

  private void initViews(View view) {
    mEdtQuery = (EditText) view.findViewById(R.id.edt_search_by_name);
    mTxtNotFound = (TextView) view
        .findViewById(R.id.txt_search_by_name_not_found);
    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.lv_search_by_name);
    mImgClearText = (ImageView) view
        .findViewById(R.id.img_clear_search_by_name);

    mPullToRefreshListView.setMode(Mode.DISABLED);

    mListViewSearch = mPullToRefreshListView.getRefreshableView();
    mListViewSearch.setOnItemClickListener(this);
    emptyView = getActivity().getLayoutInflater().inflate(
        R.layout.empty_people_view, mListViewSearch, false);
    mListViewSearch.setEmptyView(emptyView);

    mEdtQuery.addTextChangedListener(textWatcher);
    mImgClearText.setOnClickListener(this);
  }

  private void queryServer(boolean isRefresh) {
    if (isRefresh) {
      mSearchByNameAdapter.clear();
      mTxtNotFound.setVisibility(View.GONE);
    }

    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();

    SearchByNameRequest request = new SearchByNameRequest(token, mQuery,
        mSearchByNameAdapter.getCount(), mTake);
    requestServer(LOADER_ID_SEARCH_BY_NAME, request);

    LogUtils.i(TAG, "queryServer --- " + request.toString());
  }

  @Override
  public void startRequest(int loaderId) {

  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }

    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }

    getLoaderManager().destroyLoader(loader.getId());

    if (response.getCode() != Response.SERVER_SUCCESS) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());

      if (mPullToRefreshListView.getMode() == Mode.DISABLED) {
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
      }

      return;
    }

    if (response instanceof SearchByNameResponse) {
      SearchByNameResponse searchByNameResponse = (SearchByNameResponse) response;

      mPullToRefreshListView.onRefreshComplete();

      if (searchByNameResponse.getPeople().size() == mTake) {
        mPullToRefreshListView.setMode(Mode.BOTH);

      } else if (searchByNameResponse.getPeople().size() < mTake) {
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
      }

      if (searchByNameResponse.getPeople().isEmpty()) {
        if (mSearchByNameAdapter.getCount() == 0) {
          mTxtNotFound.setVisibility(View.VISIBLE);
          emptyView.setVisibility(View.GONE);
        }
      } else {
        mTxtNotFound.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        mSearchByNameAdapter.append(searchByNameResponse.getPeople());
      }
    }

    LogUtils.e(TAG, "receiveResponse");
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {

    Response response = null;

    if (loaderID == LOADER_ID_SEARCH_BY_NAME) {
      response = new SearchByNameResponse(data);
    }

    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {

    Object object = parent.getItemAtPosition(position);

    if (object instanceof MeetPeople) {
      MeetPeople meetPeople = (MeetPeople) object;
      replaceFragment(MyProfileFragment.newInstance(meetPeople
          .getUserId()));
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.img_clear_search_by_name) {
      mEdtQuery.setText("");
    }
  }

  private class SearchByNameAdapter extends BaseAdapter {

    private List<MeetPeople> mListPeople;

    private Context mContext;

    private int mAvatarSize;

    public SearchByNameAdapter(Context context, List<MeetPeople> people) {
      this.mContext = context;
      this.mListPeople = people;

      mAvatarSize = getResources().getDimensionPixelSize(
          R.dimen.activity_setupprofile_img_avatar_width);
    }

    @Override
    public int getCount() {
      return mListPeople.size();
    }

    @Override
    public Object getItem(int position) {
      return mListPeople.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;

      if (convertView == null) {
        holder = new ViewHolder();

        convertView = View.inflate(mContext,
            R.layout.item_list_meetpeople, null);

        holder.imgAvatar = (ImageView) convertView
            .findViewById(R.id.item_list_connection_common_img_avatar);
        holder.txtName = (TrimmedTextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_name);
        holder.txtLocation = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_location);
        holder.txtTime = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_time);
        holder.txtStatus = (TextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_status);
        holder.tvNotification = (BadgeTextView) convertView
            .findViewById(R.id.item_list_connection_common_txt_notification);
        holder.imgState = (ImageView) convertView
            .findViewById(R.id.item_list_connection_common_img_status);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      MeetPeople meetPeople = mListPeople.get(position);

      try {
        Calendar calendarNow = Calendar.getInstance();
        Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateSend = Utility.YYYYMMDDHHMMSS.parse(meetPeople
            .getLastLogin());
        Calendar calendarSend = Calendar.getInstance(TimeZone
            .getDefault());
        calendarSend.setTime(dateSend);

        holder.txtTime.setText(Utility.getDifference(mContext,
            calendarSend, calendarNow));
      } catch (ParseException e) {
        e.printStackTrace();
        holder.txtTime.setText(R.string.common_now);
      }

      // load avatar that has cover by gender
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token,
          meetPeople.getAva_id());
      getImageFetcher().loadImageByGender(imageRequest, holder.imgAvatar,
          mAvatarSize, meetPeople.getGender());

      // set about
      String about = meetPeople.getAbout();
      if (!TextUtils.isEmpty(about)) {
        holder.txtStatus.setText(about);
        holder.txtStatus.setVisibility(View.VISIBLE);
      } else {
        holder.txtStatus.setVisibility(View.GONE);
      }

      if (meetPeople.isVideoCallWaiting()) {
        holder.imgState
            .setImageResource(R.drawable.ic_online_video);
      } else if (meetPeople.isVoiceCallWaiting()) {
        holder.imgState
            .setImageResource(R.drawable.ic_online_voice);
      } else {
        holder.imgState
            .setImageResource(R.drawable.ic_online_chat);
      }

      // set location
      holder.txtLocation.setText(mRegionUtils.getRegionName(meetPeople
          .getRegion()));

      // set name and age
      String age = meetPeople.getAge() + getString(R.string.yo);
      String name = mListPeople.get(position).getUser_name();
      SpannableStringBuilder text = new SpannableStringBuilder();
      text.append(TrimmedTextView.ellipsizeText(name));
      text.append(" " + age);
      holder.txtName.setTextSpanned(text);
      holder.tvNotification.setTextNumber(meetPeople.getUnreadNum());

      return convertView;
    }

    public void clear() {
      mListPeople.clear();
      notifyDataSetChanged();
    }

    public void append(List<MeetPeople> people) {
      mListPeople.addAll(people);
      notifyDataSetChanged();
    }

    private class ViewHolder {

      public ImageView imgAvatar;
      public TrimmedTextView txtName;
      public ImageView imgState;
      public TextView txtStatus;
      public TextView txtLocation;
      public TextView txtTime;
      public BadgeTextView tvNotification;
    }
  }
}
