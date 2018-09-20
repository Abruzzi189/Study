package com.application.ui.buzz;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddCommentRequest;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.AddSubCommentRequest;
import com.application.connection.request.BuzzDetailRequest;
import com.application.connection.request.DeleteBuzzRequest;
import com.application.connection.request.DeleteCommentRequest;
import com.application.connection.request.DeleteSubCommentRequest;
import com.application.connection.request.LikeBuzzRequest;
import com.application.connection.request.ListCommentRequest;
import com.application.connection.request.ListSubCommentRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.request.ReportRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.AddCommentResponse;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.AddSubCommentResponse;
import com.application.connection.response.BuzzDetailResponse;
import com.application.connection.response.DeleteBuzzResponse;
import com.application.connection.response.DeleteCommentResponse;
import com.application.connection.response.DeleteSubCommentResponse;
import com.application.connection.response.LikeBuzzResponse;
import com.application.connection.response.ListCommentResponse;
import com.application.connection.response.ListSubCommentResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.connection.response.ReportResponse;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.entity.GiftCategories;
import com.application.entity.SubComment;
import com.application.service.DataFetcherService;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.ChatFragment;
import com.application.ui.MainActivity;
import com.application.ui.TrackingBlockFragment;
import com.application.ui.buzz.BuzzItemListView.OnActionBuzzListener;
import com.application.ui.buzz.CommentItemBuzz.OnActionCommentListener;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.SendCommentEditTextView;
import com.application.ui.customeview.SendCommentEditTextView.OnHiddenKeyboardListener;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.profile.DetailPictureBaseActivity;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class BuzzDetail extends TrackingBlockFragment implements
    OnNavigationClickListener, ResponseReceiver, OnActionBuzzListener,
    OnClickListener, OnEditorActionListener,
    OnActionCommentListener, OnDeleteSubCommentListener,
    OnHiddenKeyboardListener, SlidingMenu.OnOpenListener {

  public static final int INPUT_TYPE_COMMENT = 0;
  public static final int INPUT_TYPE_SUB_COMMENT = 1;
  private static final String TAG = "BuzzDetail";
  private static final String KEY_BUZZ_ID = "buzz_id";
  private static final String KEY_BUZZ_TYPE = "buzz_type";
  private static final String KEY_INPUT_TYPE = "input_type";
  private static final String KEY_COMMENT_ID = "comment_id";
  private static final String KEY_COMMENT_POSITION = "comment_position";
  private static final int LOADER_ID_LOAD_BUZZ_DETAIL = 0;
  private static final int LOADER_ID_LOAD_BUZZ_DETAIL_COMMENT = 1;
  private static final int LOADER_ID_LIKE_BUZZ = 2;
  private static final int LOADER_ID_DELETE_BUZZ = 3;
  private static final int LOADER_ID_DELETE_COMMENT = 4;
  private static final int LOADER_ID_REPORT_BUZZ = 5;
  private static final int LOADER_ID_ADD_COMMENT = 6;
  private static final int LOADER_ID_ADD_TO_FAVORITES = 7;
  private static final int LOADER_ID_REMOVE_FROM_FAVORITES = 8;
  private static final int LOADER_ID_ADD_SUB_COMMENT = 9;
  private static final int LOADER_ID_DELETE_SUB_COMMENT = 10;
  private static final int LOADER_ID_LIST_SUB_COMMENT = 11;
  private static final int REQUEST_GIFT = 5000;
  // Use for working with List
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mBuzzListView;
  private BuzzListItem mBuzzDetail;
  private View mView;
  // private View mBuzzDetailHeader;
  private BuzzItemListView mBuzzDetailHeader;
  private View mBuzzDetailFooter;
  private TextView mTextViewCounter;
  private CommentsListAdapter mCommentsListAdapter;
  private String mBuzzId;
  private String mCommentId;
  private String mSubCommentId;
  private int mCommentPosition;
  private int mSubCommentPosition;
  private int mType;
  private int mWorkingCommentPositionInBuzz = 0;
  private int mTake;
  private int mNumberOfComments = 0;
  private BuzzListCommentItem mBuzzListCommentItem;
  private SubComment mSubComment;
  private OperationViewHolder mOperationViewHolder = new OperationViewHolder();

  private int mBuzzCommenterAvatarWidth = 0;
  private int mBuzzCommenterAvatarHeight = 0;

  private ProgressDialog mProgressDialog;

  private boolean mShowSoftkeyWhenStart = false;
  private Context mAppContext;

  private String mUserId = "";
  private String mMyId = "";

  private int mInputType = INPUT_TYPE_COMMENT;
  private String mCommentPointHint;
  private String mReplyHint;
  private String mCommentHint;
  private int mCommentPoint;

  private RelativeLayout mLauoutLoadMore;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Started");
      LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Ended");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Started");

      LogUtils.d(
          TAG,
          String.format(
              "onRefreshListener.onPullUpToRefresh: skip = %d, take = %d",
              mCommentsListAdapter.getCount(), mTake));

      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Ended");
    }
  };

  public static BuzzDetail newInstance(String buzzId, int type) {
    BuzzDetail instance = new BuzzDetail();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_BUZZ_ID, buzzId);
    bundle.putInt(KEY_BUZZ_TYPE, type);
    instance.setArguments(bundle);
    return instance;
  }

  public static BuzzDetail newInstance(String buzzId, int type, int inputType, String commentId,
      int commentPosition) {
    BuzzDetail instance = new BuzzDetail();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_BUZZ_ID, buzzId);
    bundle.putInt(KEY_BUZZ_TYPE, type);
    bundle.putInt(KEY_INPUT_TYPE, inputType);
    bundle.putString(KEY_COMMENT_ID, commentId);
    bundle.putInt(KEY_COMMENT_POSITION, commentPosition);
    instance.setArguments(bundle);
    return instance;
  }

  public void showSoftkeyWhenStart(boolean show) {
    this.mShowSoftkeyWhenStart = show;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mAppContext = activity.getApplication();
  }

  @Override
  protected void resetNavigationBar() {
    LogUtils.d(TAG, "resetNavigationBar Started");

    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.buzz_responses);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setShowUnreadMessage(false);

    LogUtils.d(TAG, "resetNavigationBar Ended");
  }

  @Override
  public void onNavigationLeftClick(View view) {
    LogUtils.d(TAG, "onNavigationLeftClick Started");

    super.onNavigationLeftClick(view);

    LogUtils.d(TAG, "onNavigationLeftClick Ended");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCommentPointHint = getResources().getString(R.string.timeline_comment_point_hint);
    mReplyHint = getResources().getString(R.string.timeline_reply_point_hint);
    mCommentHint = getResources().getString(R.string.timeline_comment_hint);

    // default take
    mTake = getResources().getInteger(R.integer.take_buzzdetail);

    if (savedInstanceState != null) {
      mBuzzId = savedInstanceState.getString(KEY_BUZZ_ID);
      mType = savedInstanceState.getInt(KEY_BUZZ_TYPE);
      mInputType = savedInstanceState.getInt(KEY_INPUT_TYPE);
      mCommentId = savedInstanceState.getString(KEY_COMMENT_ID);
      mCommentPosition = savedInstanceState.getInt(KEY_COMMENT_POSITION);
    } else {
      mBuzzId = getArguments().getString(KEY_BUZZ_ID);
      mType = getArguments().getInt(KEY_BUZZ_TYPE);
      mInputType = getArguments().getInt(KEY_INPUT_TYPE);
      mCommentId = getArguments().getString(KEY_COMMENT_ID);
      mCommentPosition = getArguments().getInt(KEY_COMMENT_POSITION);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    LogUtils.d(TAG, "onCreateView Started");
    requestDirtyWord();
    mView = inflater.inflate(R.layout.fragment_buzz_detail, container,
        false);

    initView(mView);

    LogUtils.d(TAG, "onCreateView Ended");

    startRequestBuzzDetail(mTake);

    return mView;
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Initialize image fetcher
    initLoadingImageSize();

    mCommentsListAdapter = new CommentsListAdapter();
    mBuzzListView.setAdapter(mCommentsListAdapter);
    mBuzzListView.setDividerHeight(0);
    mBuzzListView.setCacheColorHint(Color.TRANSPARENT);

    LogUtils.d(TAG, "onActivityCreated Ended");
  }

  @Override
  public void onResume() {
    super.onResume();
    mCommentsListAdapter.notifyDataSetChanged();
  }

  @Override
  public void onPause() {
    super.onPause();
    mShowSoftkeyWhenStart = false;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    toogleInputType(INPUT_TYPE_COMMENT);
    Utility.hideSoftKeyboard(getActivity());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case Activity.RESULT_OK:
        switch (requestCode) {
          case MyProfileFragment.REQUEST_CODE_GET_AVATAR:
            if (data != null) {
              String fragment_tag = data.getStringExtra(Constants.FRAGMENT_TAG);
              LogUtils.d("BuzzDetail", "onActivityResult-request get image");
              Boolean isReload = data
                  .getBooleanExtra(DetailPictureBaseActivity.KEY_NOTI_RELOAD_DATA, false);
              if (MyProfileFragment.TAG_FRAGMENT_BUZZ_DETAIL.equals(fragment_tag)) {
                LogUtils.d("BuzzDetail", "is MyProfileFragment");
                String buzzId = data.getStringExtra(Constants.INTENT_BUZZ_ID);
                if (buzzId != null && !buzzId.equals("")) {
                  BuzzDetail buzzDetailFragment = BuzzDetail
                      .newInstance(buzzId, Constants.BUZZ_TYPE_NONE);
                  replaceFragment(buzzDetailFragment, MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
                }
              } else if (MyProfileFragment.TAG_FRAGMENT_USER_PROFILE.equals(fragment_tag)) {
                mUserId = data.getStringExtra(Constants.INTENT_USER_ID);
              } else if (isReload) {
                LogUtils.d("BuzzDetail", "IsReload:" + isReload + "-" + mTake);
                // clear comment list adapter
                mCommentsListAdapter.clearAllData();
                // reload buzzDetail
                startRequestBuzzDetail(mTake);
                // update avatar left menu
             //   ((MainActivity) getActivity()).onMainMenuUpdate(MainActivity.UPDATE_AVATAR);
              }
            }
            break;
          default:
            break;
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_BUZZ_ID, mBuzzId);
    outState.putInt(KEY_BUZZ_TYPE, mType);
    outState.putInt(KEY_INPUT_TYPE, mInputType);
    outState.putString(KEY_COMMENT_ID, mCommentId);
    outState.putInt(KEY_COMMENT_POSITION, mCommentPosition);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void initView(View v) {
    mPullToRefreshListView = (PullToRefreshListView) v
        .findViewById(R.id.cv_buzz_detail);
    mBuzzListView = mPullToRefreshListView.getRefreshableView();
//        mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);
//        mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
    Resources resource = getResources();
//        mPullToRefreshListView.setPullLabelFooter(resource.getString(R.string.pull_to_load_more_pull_label));
//        mPullToRefreshListView.setReleaseLabelFooter(resource.getString(R.string.pull_to_load_more_release_label));
    mBuzzListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    mLauoutLoadMore = (RelativeLayout) v.findViewById(R.id.more_responses_list_buzz);
    mLauoutLoadMore.setOnClickListener(this);
    mBuzzDetailHeader = new BuzzItemListView(getActivity(), mType, false);
    mBuzzDetailFooter = v.findViewById(R.id.cv_buzz_footer);
    mBuzzDetailFooter.setOnClickListener(this);
    mBuzzListView.addHeaderView(mBuzzDetailHeader);
    mOperationViewHolder.tvNumberOfComment = (TextView) mBuzzDetailFooter
        .findViewById(R.id.tv_buzz_detail_comment_number_txt);
    mOperationViewHolder.tvNumberOfComment.setTextColor(getResources()
        .getColor(R.color.color_hint_bold));
    mOperationViewHolder.etInputComment = (SendCommentEditTextView) mView
        .findViewById(R.id.edt_buzz_item_input_comment);
    mOperationViewHolder.etInputComment.setOnHiddenKeyboardListener(
        getActivity(), BuzzDetail.this);
    mOperationViewHolder.tvLikeBuzz = (TextView) mBuzzDetailFooter
        .findViewById(R.id.tv_buzz_detail_like_number);
    mOperationViewHolder.ibLikeBuzz = (ImageView) mView
        .findViewById(R.id.frame_like_buzz);
    mTextViewCounter = (TextView) mBuzzDetailFooter
        .findViewById(R.id.tv_buzz_detail_view_number);
    mBuzzListView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
          getImageFetcher().setPauseWork(true);
        } else {
          getImageFetcher().setPauseWork(false);
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    });

    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setMessage(getResources().getString(R.string.waiting));

    getSlidingMenu().setOnOpenListener(this);
    getSlidingMenu().setSecondaryOnOpenListner(this);

    hideBuzzDetail();
  }

  @Override
  public void startRequest(int loaderId) {
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
//            Log.w(TAG,"receiveResponse---------> " + new Gson().toJson(response));
    }

    if (getActivity() == null || loader == null || response == null) {
      return;
    }

    int responseCode = response.getCode();
    switch (responseCode) {
      case Response.SERVER_BUZZ_NOT_FOUND:
      case Response.SERVER_ACCESS_DENIED:
        getLoaderManager().destroyLoader(LOADER_ID_LOAD_BUZZ_DETAIL);
        handleBuzzNotFound();
        return;
      case Response.SERVER_BLOCKED_USER:
        handleBlockedUser();
        return;
      case Response.SERVER_COMMENT_NOT_FOUND:
        handleCommentNotFound();
        return;
      default:
        break;
    }

    switch (loader.getId()) {
      case LOADER_ID_LOAD_BUZZ_DETAIL:
        getLoaderManager().destroyLoader(LOADER_ID_LOAD_BUZZ_DETAIL);
        handleBuzzDetail((BuzzDetailResponse) response);
        break;
      case LOADER_ID_LOAD_BUZZ_DETAIL_COMMENT:
        getLoaderManager()
            .destroyLoader(LOADER_ID_LOAD_BUZZ_DETAIL_COMMENT);
        handleCommentsList((ListCommentResponse) response);
        break;
      case LOADER_ID_LIKE_BUZZ:
        getLoaderManager().destroyLoader(LOADER_ID_LIKE_BUZZ);
        handleLikeBuzz((LikeBuzzResponse) response);
        break;
      case LOADER_ID_REPORT_BUZZ:
        getLoaderManager().destroyLoader(LOADER_ID_REPORT_BUZZ);
        handleReportBuzz((ReportResponse) response);
        break;
      case LOADER_ID_DELETE_COMMENT:
        getLoaderManager().destroyLoader(LOADER_ID_DELETE_COMMENT);
        handleDeleteComment((DeleteCommentResponse) response);
        break;
      case LOADER_ID_ADD_COMMENT:
        getLoaderManager().destroyLoader(LOADER_ID_ADD_COMMENT);
        handleAddComment((AddCommentResponse) response);
        break;
      case LOADER_ID_DELETE_BUZZ:
        getLoaderManager().destroyLoader(LOADER_ID_DELETE_BUZZ);
        handleDeleteBuzz((DeleteBuzzResponse) response);
        break;
      case LOADER_ID_ADD_TO_FAVORITES:
        getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);
        handleAddFavoriteResponse((AddFavoriteResponse) response);
        break;
      case LOADER_ID_REMOVE_FROM_FAVORITES:
        getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
        handleRemoveFavoriteResponse((RemoveFavoriteResponse) response);
        break;
      case LOADER_ID_ADD_SUB_COMMENT:
        getLoaderManager().destroyLoader(LOADER_ID_ADD_SUB_COMMENT);
        handleAddSubComment((AddSubCommentResponse) response);
        break;
      case LOADER_ID_DELETE_SUB_COMMENT:
        getLoaderManager().destroyLoader(LOADER_ID_DELETE_SUB_COMMENT);
        handleDeleteSubComment((DeleteSubCommentResponse) response);
        break;
      case LOADER_ID_LIST_SUB_COMMENT:
        getLoaderManager().destroyLoader(LOADER_ID_LIST_SUB_COMMENT);
        handleListSubComment((ListSubCommentResponse) response);
        break;
      default:
        break;
    }
  }

  private void handleCommentNotFound() {
    if (getActivity() == null) {
      return;
    }
    LogUtils.d(TAG, "handleCommentNotFound Started");

    String message = mAppContext.getString(R.string.comment_item_not_found);
    Utility.showToastMessage(mAppContext, message);
    toogleInputType(INPUT_TYPE_COMMENT);
    Utility.hideSoftKeyboard(getActivity());
    exitMe();

    LogUtils.d(TAG, "handleCommentNotFound Ended");
  }

  private void handleLikeBuzz(LikeBuzzResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      int currentLikeType = mBuzzDetail.getIsLike();
      int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
      int currentLikeNumber = mBuzzDetail.getLikeNumber();
      int newLikeTypeButtonResource = R.drawable.btn_like;
      int newTextLikeResource = R.color.primary;
      int newLikeNumber = currentLikeNumber;
      if (currentLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
        newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
        newLikeTypeButtonResource = R.drawable.btn_unlike;
        newTextLikeResource = R.color.color_hint_bold;
        newLikeNumber--;
      } else {
        newLikeNumber++;
      }
      mBuzzDetail.setIsLike(newLikeType);

      if (newLikeNumber < 0) {
        newLikeNumber = 0;
      }

      mBuzzDetail.setLikeNumber(newLikeNumber);
      mOperationViewHolder.tvLikeBuzz.setText(String.valueOf(newLikeNumber));
      mOperationViewHolder.tvLikeBuzz.setTextColor(getResources().getColor(newTextLikeResource));

      mOperationViewHolder.ibLikeBuzz.setImageResource(newLikeTypeButtonResource);
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  private void handleDeleteBuzz(DeleteBuzzResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      exitMe();
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error, response.getCode());
    }
  }

  private void handleAddComment(AddCommentResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.saveNumberPoint(response.getPoint());

      if (mBuzzListCommentItem != null) {
        mBuzzListCommentItem.cmt_id = (response.getCommentId());
        mBuzzListCommentItem.user_id = (userPreferences.getUserId());
        String userName = UserPreferences.getInstance().getUserName();
        mBuzzListCommentItem.user_name = (userName);
        mBuzzListCommentItem.gender = (userPreferences.getGender());
        mBuzzListCommentItem.ava_id = (userPreferences.getAvaId());
        mBuzzListCommentItem.can_delete = (Constants.BUZZ_COMMENT_CAN_DELETE);
        mBuzzListCommentItem.is_online = true;
        mBuzzListCommentItem.isApproved = response.getIsApprove();
        mCommentsListAdapter.append(mBuzzListCommentItem);

        mNumberOfComments++;
        updateNumberOfComments();
      }
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      int point = response.getCommentPoint();
      NotEnoughPointDialog.showForCommentBuzz(getActivity(), point);

//			Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//			intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_COMMENT);
//			startActivity(intent);
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  private void handleDeleteComment(DeleteCommentResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      mCommentsListAdapter.remove(mWorkingCommentPositionInBuzz);
      mCommentsListAdapter.notifyDataSetChanged();

      mNumberOfComments--;
      updateNumberOfComments();
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error, response.getCode());
    }
  }

  private void handleReportBuzz(final ReportResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      // Show confirm dialog

      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      Resources resource = getResources();
      Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
      String title = "";
      String message = "";
      switch (mBuzzDetail.getBuzzType()) {
        case Constants.BUZZ_TYPE_IMAGE:
          title = resource
              .getString(R.string.dialog_confirm_report_pictuer_title);
          message = resource
              .getString(R.string.dialog_confirm_report_picture_content);
          break;
        default:
          title = resource
              .getString(R.string.dialog_confirm_report_user_title);
          message = resource
              .getString(R.string.dialog_confirm_report_user_content);
          break;
      }
      //builder.setTitle(title);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
      builder.setCustomTitle(customTitle);

      builder.setMessage(message);
      DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (!response.isAppear()) {
            mNavigationManager.goBack();
          }
        }
      };

      builder.setPositiveButton(R.string.common_ok, clickListener);
      AlertDialog element = builder.show();

      int dividerId = element.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = element.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }

    } else {
      // NOP
    }
  }

  private void handleBuzzDetail(BuzzDetailResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      initBuzzDetail(response);
      if (mPullToRefreshListView != null) {
        mPullToRefreshListView.onRefreshComplete();
      }
    } else {
      // NOP
    }
  }

  private void initBuzzDetail(BuzzDetailResponse response) {
    showBuzzDetail();

    if (mBuzzDetail != null) {
      mBuzzDetail = null;
    }
    mBuzzDetail = response.getBuzzDetail();
    mBuzzDetail.setIsApproved(Constants.IS_APPROVED);
    mBuzzDetailHeader.updateView(mBuzzDetail, false, 0, null, null, this);

    mTextViewCounter.setText(String.valueOf(mBuzzDetail.getSeenNumber()));
    mOperationViewHolder.tvLikeBuzz.setText(String.valueOf(mBuzzDetail
        .getLikeNumber()));

    mNumberOfComments = mBuzzDetail.getCommentNumber();

    mOperationViewHolder.tvNumberOfComment.setText(String
        .valueOf(mBuzzDetail.getCommentNumber()));

    if (mBuzzDetail.getIsLike() == Constants.BUZZ_LIKE_TYPE_UNLIKE) {
      mOperationViewHolder.ibLikeBuzz.setImageDrawable(getResources()
          .getDrawable(R.drawable.btn_unlike));
      mOperationViewHolder.tvLikeBuzz.setTextColor(getResources()
          .getColor(R.color.color_hint_bold));
    } else {
      mOperationViewHolder.ibLikeBuzz.setImageDrawable(getResources()
          .getDrawable(R.drawable.btn_like));
      mOperationViewHolder.tvLikeBuzz.setTextColor(getResources()
          .getColor(R.color.primary));
    }

    mCommentsListAdapter.appendList(mBuzzDetail.getCommentList());
    mCommentsListAdapter.notifyDataSetChanged();

    mOperationViewHolder.ibLikeBuzz.setOnClickListener(this);
    mOperationViewHolder.etInputComment.setOnEditorActionListener(this);
    mOperationViewHolder.etInputComment.clearFocus();
    // mOperationViewHolder.etInputComment.setOnFocusChangeListener(this);

    UserPreferences preferences = UserPreferences.getInstance();
    mCommentPoint = mBuzzDetail.getCommentPoint();
    mUserId = mBuzzDetail.getUserId();
    mMyId = preferences.getUserId();
    toogleInputType(mInputType);

    if (mInputType == INPUT_TYPE_SUB_COMMENT || mShowSoftkeyWhenStart) {
      Utility.showDelayKeyboard(mOperationViewHolder.etInputComment, 500);
    }

    toggleLoadMore();
  }

  /**
   * if comment count < total => show load more
   */
  private void toggleLoadMore() {
    int totalCommentCount = mBuzzDetail.getCommentNumber();
    int commentCount = mCommentsListAdapter.getCount();
    mLauoutLoadMore.setVisibility(commentCount < totalCommentCount ? View.VISIBLE : View.GONE);
  }

  private void handleCommentsList(ListCommentResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      ArrayList<BuzzListCommentItem> result = response.getBuzzListCommentItem();
      Log.w(TAG, "handleCommentsList ---> " + result.size());
      if (result != null && result.size() > 0) {
        // Filter results
        filterListCommentResponseResult(response);
        mCommentsListAdapter.appendList(result);
      }
      if (mPullToRefreshListView != null) {
        mPullToRefreshListView.onRefreshComplete();
      }

      toggleLoadMore();
    } else {
      if (mPullToRefreshListView != null) {
        mPullToRefreshListView.onRefreshComplete();
      }
    }
  }

  private void filterListCommentResponseResult(ListCommentResponse response) {
    List<BuzzListCommentItem> listComments = response
        .getBuzzListCommentItem();
    if (listComments != null) {
      ListIterator<BuzzListCommentItem> li = listComments.listIterator();
      Context context = mAppContext;
      BuzzListCommentItem current = null;
      List<BuzzListCommentItem> distinctResult = new ArrayList<BuzzListCommentItem>();
      boolean contains;

      while (li.hasNext()) {
        current = li.next();

        contains = false;
        for (int i = 0; i < distinctResult.size(); i++) {
          if (distinctResult.get(i).cmt_id.equals(current.cmt_id)) {
            contains = true;
            break;
          }
        }

        if (contains
            || Utility.isBlockedWithUser(context, current.user_id)) {
          li.remove();
        } else {
          distinctResult.add(current);
        }
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
    Response response = null;

    switch (loaderID) {
      case LOADER_ID_LOAD_BUZZ_DETAIL:
        response = new BuzzDetailResponse(data);
        break;
      case LOADER_ID_LOAD_BUZZ_DETAIL_COMMENT:
        response = new ListCommentResponse(data);
        break;
      case LOADER_ID_LIKE_BUZZ:
        response = new LikeBuzzResponse(data);
        break;
      case LOADER_ID_REPORT_BUZZ:
        response = new ReportResponse(data);
        break;
      case LOADER_ID_ADD_COMMENT:
        response = new AddCommentResponse(data);
        break;
      case LOADER_ID_DELETE_COMMENT:
        response = new DeleteCommentResponse(data);
        break;
      case LOADER_ID_DELETE_BUZZ:
        response = new DeleteBuzzResponse(data);
        break;
      case LOADER_ID_ADD_TO_FAVORITES:
        response = new AddFavoriteResponse(data);
        break;
      case LOADER_ID_REMOVE_FROM_FAVORITES:
        response = new RemoveFavoriteResponse(data);
        break;
      case LOADER_ID_ADD_SUB_COMMENT:
        response = new AddSubCommentResponse(data);
        break;
      case LOADER_ID_DELETE_SUB_COMMENT:
        response = new DeleteSubCommentResponse(data);
        break;
      case LOADER_ID_LIST_SUB_COMMENT:
        response = new ListSubCommentResponse(data);
        break;
      default:
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  private RequestParams getBuzzDetailParams(int take) {
    return new BuzzDetailRequest(UserPreferences.getInstance().getToken(), mBuzzId, take);
  }

  private RequestParams getListCommentParams(int skip, int take) {
    return new ListCommentRequest(UserPreferences.getInstance().getToken(), mBuzzId, skip, take);
  }

  private void startRequestBuzzDetail(int take) {
    if (!mProgressDialog.isShowing()) {
      mProgressDialog.show();
    }

    restartRequestServer(LOADER_ID_LOAD_BUZZ_DETAIL,
        getBuzzDetailParams(take));
  }

  private void startRequestCommentsList(int skip, int take) {
    restartRequestServer(LOADER_ID_LOAD_BUZZ_DETAIL_COMMENT, getListCommentParams(skip, take));
  }

  private void initLoadingImageSize() {
    LogUtils.d(TAG, "initImageFetcher Started");

    mBuzzCommenterAvatarHeight = getResources().getDimensionPixelSize(
        R.dimen.img_commenter_avata_item_list_buzz_size);
    mBuzzCommenterAvatarWidth = getResources().getDimensionPixelSize(
        R.dimen.img_commenter_avata_item_list_buzz_size);

    LogUtils.d(TAG, "initImageFetcher Ended");
  }

  private void executeShowUserInfo(String userId) {
    LogUtils.d(TAG, "executeShowUserInfo Started");

    boolean isMe = false;

    if (userId.equals(UserPreferences.getInstance().getUserId())) {
      isMe = true;
    }

    if (!isMe && Utility.isBlockedWithUser(mAppContext, userId)) {
      handleBlockedUser();
      return;
    }
    // replaceFragment(new MyPageFragment());
    replaceFragment(MyProfileFragment.newInstance(userId, false));
    LogUtils.d(TAG, "executeShowUserInfo Ended");
  }

  private void executeViewDetailPicture() {
    LogUtils.d(TAG, "executeViewDetailPicture Started");

    mInputType = INPUT_TYPE_COMMENT;
    Utility.hideSoftKeyboard(getActivity());

    boolean isMe = false;

    if (mBuzzDetail.getUserId().equals(
        UserPreferences.getInstance().getUserId())) {
      isMe = true;
    }

    if (!isMe
        && Utility.isBlockedWithUser(mAppContext,
        mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    Intent intent = new Intent(mAppContext, DetailPictureBuzzActivity.class);
    Bundle userData = null;
    userData = ProfilePictureData.parseDataToBundle(mBuzzDetail);
    intent.putExtras(userData);

    startActivity(intent);

    LogUtils.d(TAG, "executeViewDetailPicture Ended");
  }

  private void executeChatWithUser() {
    LogUtils.d(TAG, "executeChatWithUser Started");

    if (Utility.isBlockedWithUser(mAppContext, mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    ChatFragment chatFragment = ChatFragment.newInstance(
        mBuzzDetail.getUserId(), mBuzzDetail.getAvatarId(),
        mBuzzDetail.getUserName(), mBuzzDetail.getGender(), true);
    replaceFragment(chatFragment, MainActivity.TAG_FRAGMENT_CHAT);

    LogUtils.d(TAG, "executeChatWithUser Ended");
  }

  private void executeDeleteBuzz() {
    LogUtils.d(TAG, "executeDeleteBuzz Started");

    DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(
        UserPreferences.getInstance().getToken(),
        mBuzzDetail.getBuzzId());
    restartRequestServer(LOADER_ID_DELETE_BUZZ, deleteBuzzRequest);

    LogUtils.d(TAG, "executeDeleteBuzz Ended");
  }

  private void executeReportBuzz() {
    LogUtils.d(TAG, "executeReportBuzz Started");

    if (Utility.isBlockedWithUser(mAppContext, mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = getResources();
    Builder builder = new Builder(getActivity());
    String title = "";
    String[] items = null;

    switch (mBuzzDetail.getBuzzType()) {
      case Constants.BUZZ_TYPE_IMAGE:
        title = resource
            .getString(R.string.dialog_confirm_report_pictuer_title);
        items = resource.getStringArray(R.array.report_content_type);
        break;
      default:
        title = resource
            .getString(R.string.dialog_confirm_report_user_title);
        items = resource.getStringArray(R.array.report_user_type);
        break;
    }

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);
    // builder.setTitle(title);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.select_dialog_item, items);

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        LogUtils.d(TAG, "onClick Started");

        LogUtils.d(TAG,
            String.format("executeReportBuzz: which = %d", which));

        if (which > 0) {
          String token = UserPreferences.getInstance().getToken();
          String subject_id = mBuzzDetail.getUserId();
          int subjectType = Constants.REPORT_TYPE_USER;
          switch (mBuzzDetail.getBuzzType()) {
            case Constants.BUZZ_TYPE_IMAGE:
              subject_id = mBuzzDetail.getBuzzValue();
              subjectType = Constants.REPORT_TYPE_IMAGE;
              break;
            default:
              break;
          }
          int reportType = 0;
          Resources resource = getResources();
          String[] reportTypes = resource
              .getStringArray(R.array.report_type);
          String[] reportUsers = resource
              .getStringArray(R.array.report_user_type);
          String reportString = reportUsers[which];
          int length = reportTypes.length;
          for (int i = 0; i < length; i++) {
            if (reportString.equals(reportTypes[i])) {
              reportType = i;
            }
          }

          ReportRequest reportRequest = new ReportRequest(token,
              subject_id, reportType, subjectType);
          restartRequestServer(LOADER_ID_REPORT_BUZZ, reportRequest);
        }

        LogUtils.d(TAG, "onClick Ended");
      }
    });
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "executeReportBuzz Ended");
  }

  private void executeLikeBuzz() {
    LogUtils.d(TAG, "executeLikeBuzz Started");

    if (Utility.isBlockedWithUser(mAppContext, mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    int currentLikeType = mBuzzDetail.getIsLike();
    int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
    if (currentLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
      newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
    }
    LogUtils.d(TAG, String.format(
        "executeLikeBuzz: CurrentLikeType = %d, NewLikeType = %d",
        currentLikeType, newLikeType));

    LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(UserPreferences
        .getInstance().getToken(), mBuzzId, newLikeType);
    restartRequestServer(LOADER_ID_LIKE_BUZZ, likeBuzzRequest);

    LogUtils.d(TAG, "executeLikeBuzz Ended");
  }

  private void startRequestAddComment(String cmt_val) {
    LogUtils.d(TAG, "executeAddComment Started");

    if (Utility.isBlockedWithUser(mAppContext, mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    mBuzzListCommentItem = new BuzzListCommentItem();
    mBuzzListCommentItem.cmt_time = (Utility.getCommentTime());
    mBuzzListCommentItem.cmt_val = (cmt_val);

    AddCommentRequest addCommentRequest = new AddCommentRequest(
        UserPreferences.getInstance().getToken(), mBuzzId, cmt_val);
    restartRequestServer(LOADER_ID_ADD_COMMENT, addCommentRequest);

    LogUtils.d(TAG, "executeAddComment Ended");
  }

  private void showBuzzDetail() {
    mBuzzDetailHeader.setVisibility(View.VISIBLE);
    mBuzzDetailFooter.setVisibility(View.VISIBLE);
    mBuzzListView.setVisibility(View.VISIBLE);
  }

  private void hideBuzzDetail() {
    mBuzzDetailHeader.setVisibility(View.GONE);
    mBuzzDetailFooter.setVisibility(View.GONE);
    mBuzzListView.setVisibility(View.GONE);
  }

  private void exitMe() {
    View navigationBar = getNavigationBar();
    if (navigationBar != null) {
      new Handler().post(new Runnable() {
        public void run() {
          LogUtils.d(TAG, "exitMe.Runnable.run Started");

          // onNavigationLeftClick(getNavigationBar());
          mNavigationManager.goBack();
          LogUtils.d(TAG, "exitMe.Runnable.run Ended");
        }
      });
    }
  }

  private void updateNumberOfComments() {
    mBuzzDetail.setCommentNumber(mNumberOfComments);
    mOperationViewHolder.tvNumberOfComment.setText(String
        .valueOf(mNumberOfComments));
  }

  private void handleBuzzNotFound() {
    if (getActivity() == null) {
      return;
    }
    LogUtils.d(TAG, "handleBuzzNotFound Started");

    String message = mAppContext.getString(R.string.buzz_item_not_found);
    Utility.showToastMessage(mAppContext, message);
    toogleInputType(INPUT_TYPE_COMMENT);
    Utility.hideSoftKeyboard(getActivity());
    exitMe();

    LogUtils.d(TAG, "handleBuzzNotFound Ended");
  }

  private void handleBlockedUser() {
    LogUtils.d(TAG, "handleBlockedUser Started");
    toogleInputType(INPUT_TYPE_COMMENT);
    Utility.hideSoftKeyboard(getActivity());
    LogUtils.d(TAG, "handleBlockedUser Ended");
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected String getUserIdTracking() {
    if (mBuzzDetail != null) {
      return mBuzzDetail.getUserId();
    }
    return "";
  }

  @Override
  public void deleteBuzzAt(int position) {
    showDialogConfirmDeleteBuzz();
  }

  @Override
  public void chatWithUserAt(int position) {
    executeChatWithUser();
  }

  @Override
  public void viewDetailPictureAt(int position) {
    executeViewDetailPicture();
  }

  @Override
  public void viewBuzzDetailAt(int position) {
  }

  @Override
  public void likeBuzzAt(int position) {
  }

  @Override
  public void openLikeAndPostCommentAt(int position) {
  }

  @Override
  public void showUserInfoAt(int position) {
    executeShowUserInfo(mBuzzDetail.getUserId());
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.frame_like_buzz:
        executeLikeBuzz();
        break;
      case R.id.more_responses_list_buzz:
        startRequestCommentsList(mCommentsListAdapter.getCount(), mTake);
        break;
    }
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if ((actionId == EditorInfo.IME_ACTION_SEND)
        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
      if (!Utility.isContainDirtyWord(getActivity(), v)) {
        String contentValue = v.getText().toString()
            .replace("\u3000", " ").trim();
        if (contentValue.length() != 0) {
          if (mInputType == INPUT_TYPE_COMMENT) {
            startRequestAddComment(contentValue);
          } else if (mInputType == INPUT_TYPE_SUB_COMMENT) {
            startRequestAddSubComment(mCommentId, contentValue,
                mBuzzId);
          }

          v.setText("");
          Utility.hideSoftKeyboard(getActivity());
          toogleInputType(INPUT_TYPE_COMMENT);
        }
      }
    }
    return true;
  }

  @Override
  public void onDeleteComment(int buzzPosition, int commentPosition) {
    mWorkingCommentPositionInBuzz = commentPosition;
    BuzzListCommentItem blci = (BuzzListCommentItem) mCommentsListAdapter
        .getItem(mWorkingCommentPositionInBuzz);
    if (blci != null) {
      showDialogConfirmDeleteComment(blci);
    }
  }

  @Override
  public void onShowMoreComment(int commentPosition, String commentId,
      int skip, int buzzPosition) {
    mCommentPosition = commentPosition;
    mCommentId = commentId;
    startRequestListSubComment(mBuzzId, mCommentId, skip, mTake);

  }

  @Override
  public void reportBuzz() {
    executeReportBuzz();
  }

  @Override
  public void handleFavorite(int position) {
    String userId = mBuzzDetail.getUserId();
    FavouritedPrefers favouritedPrefers = FavouritedPrefers.getInstance();

    if (favouritedPrefers.hasContainFav(userId)) {
      executeRemoveFromFavorites(userId);
    } else {
      executeAddToFavorites(userId);
    }
  }

  private void executeAddToFavorites(String userId) {

    String token = UserPreferences.getInstance().getToken();
    AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
        userId);
    restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
  }

  private void executeRemoveFromFavorites(String userId) {
    String token = UserPreferences.getInstance().getToken();
    RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
        token, userId);
    restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES,
        removeFavoriteRequest);
  }

  private void handleAddFavoriteResponse(AddFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);

      String userId = mBuzzDetail.getUserId();
      FavouritedPrefers.getInstance().saveFav(userId);
      UserPreferences.getInstance().increaseFavorite();

      mBuzzDetailHeader.updateView(mBuzzDetail, false, 0, null, null, this);
      showDialogAddFavoriteSuccess(mBuzzDetail);
    }
  }

  private void handleRemoveFavoriteResponse(RemoveFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);

      String userId = mBuzzDetail.getUserId();
      FavouritedPrefers.getInstance().removeFav(userId);
      UserPreferences.getInstance().decreaseFavorite();

      mBuzzDetailHeader.updateView(mBuzzDetail, false, 0, null, null, this);
      showDialogRemoveFavoritesSuccess(mBuzzDetail);
    }

  }

  private void showDialogAddFavoriteSuccess(final BuzzListItem addingItem) {
    String username = addingItem.getUserName();
    String message = String.format(
        getString(R.string.profile_add_to_favorites_message), username);
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(),
        getString(R.string.profile_add_to_favorites_title), message,
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            showGiveGiftFragment(addingItem);
          }
        })
        .setNegativeButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showDialogRemoveFavoritesSuccess(BuzzListItem removingItem) {
    String title = getString(R.string.profile_remove_from_favorites_title);
    String msg = String.format(
        getString(R.string.profile_remove_from_favorites_message),
        removingItem.getUserName());
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), title, msg, false)
        .setPositiveButton(0, null)
        .create();
    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showGiveGiftFragment(BuzzListItem addingItem) {
    GiftCategories categories = new GiftCategories("get_all_gift", 0,
        getResources().getString(R.string.give_gift_all_title), 1);
    ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
        addingItem.getUserId(), addingItem.getUserName(), categories);
    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  private void showDialogConfirmDeleteBuzz() {
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.timeline_delete_buzz),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            executeDeleteBuzz();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();

    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showDialogConfirmDeleteComment(
      final BuzzListCommentItem currentComment) {
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.timeline_delete_comment),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(
                UserPreferences.getInstance().getToken(), mBuzzDetail
                .getBuzzId(), currentComment.cmt_id);
            restartRequestServer(LOADER_ID_DELETE_COMMENT, deleteCommentRequest);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showDialogConfirmDeleteSubComment() {
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.confirm_delete_reply),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            startRequestDeleteSubComment(mBuzzId, mCommentId, mSubCommentId);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void startRequestAddSubComment(String commentId, String contentValue, String buzzId) {
    LogUtils.d(TAG, "startRequestAddSubComment Started");
    if (Utility.isBlockedWithUser(mAppContext, mBuzzDetail.getUserId())) {
      handleBlockedUser();
      return;
    }

    mSubComment = new SubComment();
    mSubComment.time = (Utility.getCommentTime());
    mSubComment.value = (contentValue);

    AddSubCommentRequest addSubCommentRequest = new AddSubCommentRequest(
        UserPreferences.getInstance().getToken(), commentId,
        contentValue, buzzId);
    restartRequestServer(LOADER_ID_ADD_SUB_COMMENT, addSubCommentRequest);

    LogUtils.d(TAG, "startRequestAddSubComment Ended");
  }

  private void handleAddSubComment(AddSubCommentResponse response) {
    LogUtils.d(TAG, "handleAddSubComment Started");

    if (response.getCode() == Response.SERVER_SUCCESS) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.saveNumberPoint(response.getPoint());

      if (mSubComment != null) {
        mSubComment.sub_comment_id = (response.getSubCommentId());
        mSubComment.user_id = (userPreferences.getUserId());
        String userName = UserPreferences.getInstance().getUserName();
        mSubComment.user_name = (userName);
        mSubComment.gender = (userPreferences.getGender());
        mSubComment.ava_id = (userPreferences.getAvaId());
        mSubComment.can_delete = true;
        mSubComment.is_online = true;
        mSubComment.isApprove = response.getIsApprove();

        ArrayList<SubComment> listSubComments = ((BuzzListCommentItem) mCommentsListAdapter
            .getItem(mCommentPosition)).sub_comments;
        if (listSubComments == null) {
          listSubComments = new ArrayList<SubComment>();
        }
        listSubComments.add(mSubComment);

        ((BuzzListCommentItem) mCommentsListAdapter
            .getItem(mCommentPosition)).sub_comments = listSubComments;

        mCommentsListAdapter.notifyDataSetChanged();
      }
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      int point = response.getSubCommentPoint();
      NotEnoughPointDialog.showForReply(getActivity(), point);

//			Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//			intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_SUB_COMMENT);
//			startActivity(intent);
    } else {
      // TODO: Handle other error code (Buzz is deleted)
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
    LogUtils.d(TAG, "handleAddSubComment Ended");
  }

  private void startRequestDeleteSubComment(String buzzId, String cmtId,
      String subCommentId) {
    LogUtils.d(TAG, "startRequestDeleteSubComment Started");

    DeleteSubCommentRequest deleteSubCommentRequest = new DeleteSubCommentRequest(
        UserPreferences.getInstance().getToken(), buzzId, cmtId,
        subCommentId);
    restartRequestServer(LOADER_ID_DELETE_SUB_COMMENT,
        deleteSubCommentRequest);

    LogUtils.d(TAG, "startRequestDeleteSubComment Ended");
  }

  private void handleDeleteSubComment(DeleteSubCommentResponse response) {
    ((BuzzListCommentItem) mCommentsListAdapter.getItem(mCommentPosition)).sub_comments
        .remove(mSubCommentPosition);
    mCommentsListAdapter.notifyDataSetChanged();
  }

  private void startRequestListSubComment(String buzzId, String commentId,
      int skip, int take) {
    LogUtils.d(TAG, "startRequestListSubComment Started");

    LogUtils.d(TAG, String.format(
        "startRequestListSubComment: skip = %d, take = %d", skip, take));

    ListSubCommentRequest listSubCommentRequest = new ListSubCommentRequest(
        UserPreferences.getInstance().getToken(), buzzId, commentId,
        skip, take);

    restartRequestServer(LOADER_ID_LIST_SUB_COMMENT, listSubCommentRequest);

    LogUtils.d(TAG, "startRequestListSubComment Ended");
  }

  private void handleListSubComment(ListSubCommentResponse response) {

    if (mCommentsListAdapter != null) {
      BuzzListCommentItem comment = ((BuzzListCommentItem) mCommentsListAdapter
          .getItem(mCommentPosition));
      if (comment != null && response != null) {
        ArrayList<SubComment> listSubComment = response.getSubComments();
        if (listSubComment != null) {
          comment.sub_comments.addAll(0, listSubComment);
          if (listSubComment.size() < 10) {
            comment.isNoMoreSubComment = true;
          }
          mCommentsListAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  private void toogleInputType(int inputType) {
    mInputType = inputType;
    if (mOperationViewHolder == null || mOperationViewHolder.etInputComment == null) {
      return;
    }
    if (mInputType == INPUT_TYPE_COMMENT && mCommentPoint > 0 && !mMyId.equals(mUserId)) {
      mOperationViewHolder.etInputComment
          .setHint(MessageFormat.format(mCommentPointHint, mCommentPoint));
    } else if (mInputType == INPUT_TYPE_SUB_COMMENT) {
      mOperationViewHolder.etInputComment.setHint(mReplyHint);
    } else {
      mOperationViewHolder.etInputComment.setHint(mCommentHint);
    }
  }

  @Override
  public void onReplyComment(String commentId, int commentPosition, int buzzPosition) {
    toogleInputType(INPUT_TYPE_SUB_COMMENT);
    mCommentId = commentId;
    mCommentPosition = commentPosition;
    Utility.showDelayKeyboard(mOperationViewHolder.etInputComment, 0);
  }

  @Override
  public void onDeleteSubComment(String commentId, String subCommentId,
      int commentPosition, int subCommentPosition) {
    mCommentId = commentId;
    mSubCommentId = subCommentId;
    mCommentPosition = commentPosition;
    mSubCommentPosition = subCommentPosition;
    showDialogConfirmDeleteSubComment();
  }

  @Override
  public void onHiddenKeyboard() {
    if (mInputType == INPUT_TYPE_SUB_COMMENT) {
      toogleInputType(INPUT_TYPE_COMMENT);
    }
  }

  @Override
  public void onOpen() {
    toogleInputType(INPUT_TYPE_COMMENT);
  }

  interface OnClickBuzzDetailAdapter {

    public void OnDeleteComment(int commentPosition);

    public void OnShowUserInfo(int commentPosition);
  }

  private static class OperationViewHolder {

    // public View vLikeAndPostComment;
    public TextView tvNumberOfComment;
    public SendCommentEditTextView etInputComment;
    public TextView tvLikeBuzz;
    public ImageView ibLikeBuzz;
  }

  private static class ViewHolder {

    public CommentItemBuzz mItemComment;
  }

  protected class CommentsListAdapter extends BaseAdapter {

    private List<BuzzListCommentItem> mBuzzDetailCommentsList = new ArrayList<BuzzListCommentItem>();

    public CommentsListAdapter() {
    }

    @Override
    public int getCount() {
      return mBuzzDetailCommentsList.size();
    }

    @Override
    public Object getItem(int position) {
      if (position >= mBuzzDetailCommentsList.size()) {
        return null;
      }

      return mBuzzDetailCommentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public Object remove(int position) {
      if (position >= mBuzzDetailCommentsList.size()) {
        return null;
      }

      return mBuzzDetailCommentsList.remove(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;
      final BuzzListCommentItem blci = mBuzzDetailCommentsList.get(position);
      if (blci == null) {
        return null;
      }

      if (convertView == null) {
        holder = new ViewHolder();
        CommentItemBuzz item = new CommentItemBuzz(getActivity());
        item.setBackgroundResource(R.drawable.bg_item_comment_list_buzz);
        holder.mItemComment = item;
        convertView = item;
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      String token = UserPreferences.getInstance().getToken();
      holder.mItemComment.updateView(blci, mBuzzId, true, token, mBuzzCommenterAvatarWidth,
          mBuzzCommenterAvatarHeight, 0, position, BuzzDetail.this, BuzzDetail.this, true);
      return convertView;
    }

    public void append(BuzzListCommentItem buzzListCommentItem) {
      if (buzzListCommentItem == null) {
        return;
      }
      mBuzzDetailCommentsList.add(buzzListCommentItem);
      this.notifyDataSetChanged();
    }

    public void appendList(List<BuzzListCommentItem> buzzListCommentItems) {
      if (buzzListCommentItems == null) {
        return;
      }
      Log.e(TAG, "buzzListCommentItems " + buzzListCommentItems.size());
      mBuzzDetailCommentsList.addAll(buzzListCommentItems);
      this.notifyDataSetChanged();
    }

    /**
     * Remove all elements from this adapter, leaving it empty
     */
    public void clearAllData() {
      mBuzzDetailCommentsList.clear();
      this.notifyDataSetChanged();
    }
  }
}