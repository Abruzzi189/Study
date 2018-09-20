package com.application.ui;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import com.application.adapters.TemplateAdapter;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.DeleteTemplateRequest;
import com.application.connection.request.ListTemplateRequest;
import com.application.connection.response.DeleteTemplateResponse;
import com.application.connection.response.ListTemplateResponse;
import com.application.entity.Template;
import com.application.event.TemplateEvent;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.preferece.UserPreferences;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.util.ArrayList;

public class TemplateFragment extends BaseFragment implements OnClickListener,
    ResponseReceiver {

  public static final int STYLE_CHAT = 0;
  public static final int STYLE_MY_PAGE = 1;
  private static final String KEY_STYLE = "key_style";
  private static final String KEY_FRIEND_ID = "key_friend_id";
  private static final int LOADER_LIST_TEMPLATE = 0;
  private static final int LOADER_DELETE_TEMPLATE = 1;
  private static final int LIMITED_TEMPLATE = 20;
  private ExpandableListView mList;
  private TextView mDescription, mCreate;
  private ArrayList<Template> mTemplates;
  private TemplateAdapter mAdapter;
  private int mTempPos;
  private int mStyle;
  private String mFriendId;

  public static TemplateFragment newInstance(int style) {
    TemplateFragment fragment = new TemplateFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_STYLE, style);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static TemplateFragment newInstance(int style, String friendId) {
    TemplateFragment fragment = new TemplateFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_STYLE, style);
    bundle.putString(KEY_FRIEND_ID, friendId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    if (savedInstanceState != null) {
      mStyle = savedInstanceState.getInt(KEY_STYLE);
      mFriendId = savedInstanceState.getString(KEY_FRIEND_ID);
    } else {
      mStyle = getArguments().getInt(KEY_STYLE);
      mFriendId = getArguments().getString(KEY_FRIEND_ID);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_STYLE, mStyle);
    outState.putString(KEY_FRIEND_ID, mFriendId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_template, container,
        false);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    mList = (ExpandableListView) view.findViewById(R.id.list);
    mDescription = (TextView) view.findViewById(R.id.tv_description);
    mCreate = (TextView) view.findViewById(R.id.btn_create);
    mCreate.setOnClickListener(this);

    mList.setOnGroupExpandListener(new OnGroupExpandListener() {
      int previousGroup = -1;

      @Override
      public void onGroupExpand(int groupPosition) {
        if (groupPosition != previousGroup) {
          mList.collapseGroup(previousGroup);
        }
        previousGroup = groupPosition;
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    requestListTemplate();
  }

  private void requestListTemplate() {
    String token = UserPreferences.getInstance().getToken();
    ListTemplateRequest request = new ListTemplateRequest(token);
    restartRequestServer(LOADER_LIST_TEMPLATE, request);
  }

  private void requestDeleteTemplate(String tempId) {
    String token = UserPreferences.getInstance().getToken();
    DeleteTemplateRequest request = new DeleteTemplateRequest(token, tempId);
    restartRequestServer(LOADER_DELETE_TEMPLATE, request);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_create:
        replaceFragment(AddOrUpdateTemplateFragment
            .newInstance(AddOrUpdateTemplateFragment.FUNCTION_ADD));
        break;
      default:
        break;
    }
  }

  @SuppressWarnings("deprecation")
  private void handleListTemplateResponse(ListTemplateResponse data) {
    getLoaderManager().destroyLoader(LOADER_LIST_TEMPLATE);
    mTemplates = data.getTemplates();
    if (mTemplates != null && mTemplates.size() > 0) {
      mAdapter = new TemplateAdapter(getActivity(), mTemplates,
          mNavigationManager, mStyle, mFriendId);
      mList.setAdapter(mAdapter);
      mList.setVisibility(View.VISIBLE);
      mDescription.setVisibility(View.GONE);
      if (mTemplates.size() >= LIMITED_TEMPLATE) {
        mCreate.setEnabled(false);
        mCreate.setBackgroundDrawable(getActivity()
            .getResources()
            .getDrawable(R.drawable.bg_btn_negative_create_template));
      } else {
        mCreate.setEnabled(true);
        mCreate.setBackgroundDrawable(getActivity().getResources()
            .getDrawable(R.drawable.bg_btn_create_template));
      }
    } else {
      mList.setVisibility(View.GONE);
      mDescription.setVisibility(View.VISIBLE);
    }
    setupTitle(mTemplates.size());
    mCreate.setVisibility(View.VISIBLE);
  }

  @SuppressWarnings("deprecation")
  private void handleDeleteTemplateResponse() {
    getLoaderManager().destroyLoader(LOADER_DELETE_TEMPLATE);
    mTemplates.remove(mTempPos);
    if (mTemplates.size() <= 0) {
      mList.setVisibility(View.GONE);
      mDescription.setVisibility(View.VISIBLE);
    }

    if (mTemplates.size() < LIMITED_TEMPLATE) {
      mCreate.setEnabled(true);
      mCreate.setBackgroundDrawable(getActivity().getResources()
          .getDrawable(R.drawable.bg_btn_create_template));
    }
    setupTitle(mTemplates.size());
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void startRequest(int loaderId) {
    showWaitingDialog();
  }

  private void setupTitle(int size) {
    mActionBar.setTextCenterTitle(getActivity()
        .getString(R.string.template)
        + " ("
        + size
        + "/"
        + LIMITED_TEMPLATE + ")");
  }

  private void setupTitle() {
    mActionBar.setTextCenterTitle(getActivity()
        .getString(R.string.add_friends_chat_with_your_friends_content1));
  }
  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    hideWaitingDialog();
    if (response.getCode() != Response.SERVER_SUCCESS) {
      setupTitle();
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      getLoaderManager().destroyLoader(loader.getId());
      return;
    }

    if (response instanceof ListTemplateResponse) {
      handleListTemplateResponse((ListTemplateResponse) response);
    } else if (response instanceof DeleteTemplateResponse) {
      handleDeleteTemplateResponse();
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_LIST_TEMPLATE:
        response = new ListTemplateResponse(data);
        break;
      case LOADER_DELETE_TEMPLATE:
        response = new DeleteTemplateResponse(data);
        break;
      default:
        break;
    }

    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    hideWaitingDialog();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    if (mAdapter != null) {
      EventBus.getDefault().unregister(mAdapter);
    }
  }

  public void onEvent(TemplateEvent event) {
    switch (event.getMode()) {
      case TemplateEvent.DELETE:
        mTempPos = event.getPosition();
        requestDeleteTemplate(event.getTemplate().getTempId());
        break;
      default:
        break;
    }
  }
}
