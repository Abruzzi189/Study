package com.application.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.application.chat.ChatManager;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddBlockUserRequest;
import com.application.connection.request.BlockedUsersListRequest;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.RemoveBlockUserRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.AddBlockUserResponse;
import com.application.connection.response.BlockedUsersListResponse;
import com.application.connection.response.RemoveBlockUserResponse;
import com.application.entity.BlockedUserItem;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.profile.AccountStatus;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Flow - startRequestServer(0, mTake);  (168) - parseResponse                  (355) -
 * receiveResponse                (377) - handleBlockedUsersList         (187)
 *
 * --------------------------------------------------------
 *
 * - Button action onCLick          (168) - requestBlockUser(bui);         (172)
 */

public class BlockedUsersFragment extends BaseFragment implements OnNavigationClickListener,
    ResponseReceiver {

  private static final String TAG = "BlockedUsersFragment";
  private static final int LOADER_ID_BLOCKED_USERS_LIST = 0;
  private static final int LOADER_ID_ADD_BLOCK_USER = 1;
  private static final int LOADER_ID_REMOVE_BLOCK_USER = 2;
  private int mTake;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mBlockedUsersListView;
  private View mBlockedUsersListFooter;
  private ListBlockedUsersAdapter mListBlockedUsersAdapter;
  private int mWorkingPosition;
  private String mWorkingUserId;
  private boolean mIsCurrentUserBlocked;
  private int mAvatarSize;
  private ProgressDialog mProgressDialog;
  private MainActivity mMainActivity;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

      mBlockedUsersListFooter.setVisibility(View.GONE);
      mListBlockedUsersAdapter.clearAllData();
      startRequestServer(0, mTake);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, String
          .format(Locale.getDefault(), "onRefreshListener.onPullUpToRefresh: skip = %d, take = %d",
              0, mTake));
      startRequestServer(mListBlockedUsersAdapter.getCount(), mTake);
    }
  };

  public static BlockedUsersFragment newInstance() {
    return new BlockedUsersFragment();
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.settings_account_block_users);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTake = getResources().getInteger(R.integer.take_blockuser);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_blocked_users_list, container, false);
    initView(view);
    return view;
  }

  private void initView(View v) {

    mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.fr_blocked_users_list);
    mBlockedUsersListView = mPullToRefreshListView.getRefreshableView();
    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);

    mBlockedUsersListFooter = View
        .inflate(getActivity(), R.layout.fragment_blocked_users_list_footer, null);
    mBlockedUsersListView.addFooterView(mBlockedUsersListFooter);
    mBlockedUsersListFooter.setVisibility(View.GONE);

    mBlockedUsersListView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
          getImageFetcher().setPauseWork(true);
        } else {
          getImageFetcher().setPauseWork(false);
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
      }
    });

    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setMessage(getResources().getString(R.string.waiting));

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mAvatarSize = getResources().getDimensionPixelSize(R.dimen.img_avata_ac_setup_profile);
    mListBlockedUsersAdapter = new ListBlockedUsersAdapter(getActivity());
    mBlockedUsersListView.setAdapter(mListBlockedUsersAdapter);
    mBlockedUsersListView.setDividerHeight(0);
    mBlockedUsersListView.setCacheColorHint(Color.TRANSPARENT);

    mListBlockedUsersAdapter
        .setOnClickBlockedUsersListAdapter(new OnClickBlockedUsersListAdapter() {
          @Override
          public void OnBlockUser(int position) {
            mWorkingPosition = position;
            BlockedUserItem bui = (BlockedUserItem) mListBlockedUsersAdapter.getItem(position);
            requestBlockUser(bui);
          }
        });
    startRequestServer(0, mTake);
  }


  private void startRequestServer(int skip, int take) {
    mPullToRefreshListView.setMode(Mode.BOTH);
    Resources resource = getResources();
    mPullToRefreshListView
        .setPullLabelFooter(resource.getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshListView
        .setReleaseLabelFooter(resource.getString(R.string.pull_to_load_more_release_label));

    restartRequestServer(LOADER_ID_BLOCKED_USERS_LIST, getRequestParams(skip, take));
  }

  protected RequestParams getRequestParams(int skip, int take) {
    String token = UserPreferences.getInstance().getToken();
    return new BlockedUsersListRequest(token, skip, take);
  }

  private void handleBlockedUsersList(BlockedUsersListResponse response) {
    boolean endOfList = false;

    if (response.getCode() == Response.SERVER_SUCCESS) {
      if (response.getBlockedUsersList() != null) {
        if (response.getBlockedUsersList().size() > 0) {
          mListBlockedUsersAdapter.appendList(response.getBlockedUsersList());
        } else {
          endOfList = true;
        }
      }
    }

    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.onRefreshComplete();
    }

    if (mListBlockedUsersAdapter.getCount() == 0) {
      mBlockedUsersListFooter.setVisibility(View.VISIBLE);
      getLoaderManager().destroyLoader(LOADER_ID_BLOCKED_USERS_LIST);
    }

    if (endOfList) {
      mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
    }
  }


  /**
   * Occur when item list view onClick
   */
  private void requestBlockUser(BlockedUserItem bui) {
    String title;
    String message;

    mIsCurrentUserBlocked = bui.getBlockedStatus();
    mWorkingUserId = bui.getUserId();

    if (mIsCurrentUserBlocked) {
      title = getString(R.string.chat_screen_unblock_dialog_title);
      message = String
          .format(getString(R.string.chat_screen_unblock_dialog_message), bui.getUserName());
    } else {
      title = getString(R.string.chat_screen_block_dialog_title);
      message = String
          .format(getString(R.string.chat_screen_block_dialog_message), bui.getUserName());
    }

    AlertDialog confirmDialog = new CustomConfirmDialog(getActivity(), title, message, true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
              return;
            }
            if (mProgressDialog != null) {
              mProgressDialog.show();
            }

            String token = UserPreferences.getInstance().getToken();
            if (mIsCurrentUserBlocked) {
              RemoveBlockUserRequest rbur = new RemoveBlockUserRequest(token, mWorkingUserId);
              restartRequestServer(LOADER_ID_REMOVE_BLOCK_USER, rbur);
            } else {
              AddBlockUserRequest abur = new AddBlockUserRequest(token, mWorkingUserId);
              restartRequestServer(LOADER_ID_ADD_BLOCK_USER, abur);
            }
          }
        })
        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    confirmDialog.show();

    int dividerId = confirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }


  private void handleAddBlockedUser(AddBlockUserResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      BlockedUserItem bui = (BlockedUserItem) mListBlockedUsersAdapter.getItem(mWorkingPosition);
      bui.setBlockedStatus(true);
      mListBlockedUsersAdapter.notifyDataSetChanged();

      UserPreferences userPreferences = UserPreferences.getInstance();
      BlockUserPreferences blockUserPreferences = BlockUserPreferences.getInstance();
      String from = userPreferences.getUserId();
      String to = bui.getUserId();
      sendBlockMessage(from, to);
      blockUserPreferences.insertBlockedUser(to);

      // update connection started
      int numOfFriend = response.getFriendsNum();
      int numOfFavorite = response.getFavouriteFriendsNum();
      userPreferences.saveNumberConnection(numOfFriend, numOfFavorite);

      // Send LocalBroadcast
      Intent intent = new Intent(AccountStatus.ACTION_BLOCKED);
      intent.putExtra(AccountStatus.EXTRA_DATA, bui.getUserId());
      Utility.sendLocalBroadcast(getActivity(), intent);
    }
  }

  private void handleRemoveBlockedUser(RemoveBlockUserResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      BlockedUserItem bui = (BlockedUserItem) mListBlockedUsersAdapter
          .getItem(mWorkingPosition);
      bui.setBlockedStatus(false);
      mListBlockedUsersAdapter.notifyDataSetChanged();

      UserPreferences userPreferences = UserPreferences.getInstance();
      BlockUserPreferences blockUserPreferences = BlockUserPreferences
          .getInstance();
      String from = userPreferences.getUserId();
      String to = bui.getUserId();
      sendUnblockMessage(from, to);
      blockUserPreferences.removeBlockedUser(to);

      int numOfFriend = response.getFriendsNum();
      int numOfFavorite = response.getFavouriteFriendsNum();
      userPreferences.saveNumberConnection(numOfFriend, numOfFavorite);
    }
  }

  private void sendBlockMessage(final String from, final String to) {
    ChatManager chatManager = getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendBlockMessage(from, to);

  }

  private void sendUnblockMessage(final String from, final String to) {
    ChatManager chatManager = getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendUnblockMessage(from, to);
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void startRequest(int loaderId) {

  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {

    Response response = null;
    switch (loaderID) {
      case LOADER_ID_BLOCKED_USERS_LIST:
        response = new BlockedUsersListResponse(data);
        break;
      case LOADER_ID_ADD_BLOCK_USER:
        response = new AddBlockUserResponse(data);
        break;
      case LOADER_ID_REMOVE_BLOCK_USER:
        response = new RemoveBlockUserResponse(data);
        break;
      default:
        break;
    }
    return response;
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }

    if (getActivity() == null
        || loader == null
        || response == null) {
      if (loader != null) {
        getLoaderManager().destroyLoader(loader.getId());
      }
      return;
    }

    switch (loader.getId()) {
      case LOADER_ID_BLOCKED_USERS_LIST:
        getLoaderManager().destroyLoader(LOADER_ID_BLOCKED_USERS_LIST);
        handleBlockedUsersList((BlockedUsersListResponse) response);
        break;
      case LOADER_ID_ADD_BLOCK_USER:
        getLoaderManager().destroyLoader(LOADER_ID_ADD_BLOCK_USER);
        handleAddBlockedUser((AddBlockUserResponse) response);
        break;
      case LOADER_ID_REMOVE_BLOCK_USER:
        getLoaderManager().destroyLoader(LOADER_ID_REMOVE_BLOCK_USER);
        handleRemoveBlockedUser((RemoveBlockUserResponse) response);
        break;
      default:
        break;
    }
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  private ChatManager getChatManager() {
    if (mMainActivity.getChatService() == null) {
      return null;
    }
    return mMainActivity.getChatService().getChatManager();
  }

  interface OnClickBlockedUsersListAdapter {

    void OnBlockUser(int position);
  }

  private static class ViewHolder {

    private ImageView imageViewAvatar;
    private TextView textViewUser;
    private Button buttonAction;
  }

  /**
   * -------------------------------------Inner class--------------------------------------------
   */

  private class ListBlockedUsersAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<BlockedUserItem> mListUsers = new ArrayList<BlockedUserItem>();
    private OnClickBlockedUsersListAdapter mOnClickBlockedUsersListAdapter;

    private ListBlockedUsersAdapter(Context context) {
      mContext = context;
      mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return mListUsers.size();
    }

    @Override
    public Object getItem(int position) {
      return mListUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public void remove(int position) {
      mListUsers.remove(position);
      notifyDataSetChanged();
    }

    private void appendList(List<BlockedUserItem> users) {
      mListUsers.addAll(users);
      notifyDataSetChanged();
    }


    private void clearAllData() {
      mListUsers.clear();
      notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      BlockedUserItem bui = mListUsers.get(position);

      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mLayoutInflater.inflate(R.layout.item_list_blocked_users, parent, false);
        holder.imageViewAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        holder.textViewUser = (TextView) convertView.findViewById(R.id.tv_user_name);
        holder.buttonAction = (Button) convertView.findViewById(R.id.bt_action);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token, bui.getAvatarId());
      getImageFetcher()
          .loadImageByGender(imageRequest, holder.imageViewAvatar, mAvatarSize, bui.getGender());
      holder.textViewUser.setText(bui.getUserName());

      if (bui.getBlockedStatus()) {
        holder.buttonAction.setText(R.string.unblock);
      } else {
        holder.buttonAction.setText(R.string.block);
      }

      holder.buttonAction.setTag(position);
      holder.buttonAction.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
          }

          int currentPosition = Integer.parseInt(v.getTag().toString());

          if (mOnClickBlockedUsersListAdapter != null) {
            mOnClickBlockedUsersListAdapter.OnBlockUser(currentPosition);
          }
        }
      });

      return convertView;
    }

    private void setOnClickBlockedUsersListAdapter(OnClickBlockedUsersListAdapter listener) {
      this.mOnClickBlockedUsersListAdapter = listener;
    }
  }
}
