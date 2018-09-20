package com.application.ui.buzz;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddBuzzRequest;
import com.application.connection.request.AddCommentRequest;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.DeleteBuzzRequest;
import com.application.connection.request.DeleteCommentRequest;
import com.application.connection.request.LikeBuzzRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.response.AddBuzzResponse;
import com.application.connection.response.AddCommentResponse;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.BuzzListResponse;
import com.application.connection.response.DeleteBuzzResponse;
import com.application.connection.response.DeleteCommentResponse;
import com.application.connection.response.LikeBuzzResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.connection.response.UploadImageResponse;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.entity.GiftCategories;
import com.application.facebook.IFacebookPermission;
import com.application.imageloader.ImageResizer;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragment;
import com.application.ui.ChatFragment;
import com.application.ui.MainActivity;
import com.application.ui.buzz.BuzzItemListView.OnActionBuzzListener;
import com.application.ui.buzz.CommentItemBuzz.OnActionCommentListener;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import glas.bbsystem.R;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TuanPQ
 */
public abstract class BaseBuzzListFragment extends BaseFragment implements
    ResponseReceiver, OnActionCommentListener, OnDeleteSubCommentListener, OnActionBuzzListener,
    OnClickListener, OnFocusChangeListener,
    TextWatcher, OnEditorActionListener, OnTouchListener {

  protected static final String TAG = "BaseBuzzListFragment";
  private static final String KEY_COMMENT = "buzz.comment";
  private static final int LOADER_ID_LOAD_BUZZ_LIST = 0;
  private static final int LOADER_ID_DELETE_BUZZ = 1;
  private static final int LOADER_ID_DELETE_COMMENT = 2;
  private static final int LOADER_ID_LIKE_BUZZ = 3;
  private static final int LOADER_ID_ADD_COMMENT = 4;
  private static final int LOADER_ID_ADD_BUZZ = 5;
  private static final int LOADER_ID_ADD_TO_FAVORITES = 6;
  private static final int LOADER_ID_REMOVE_FROM_FAVORITES = 7;
  private static final int REQUEST_CODE_GET_IMAGE = 100;
  private static final int REQUEST_GIFT = 5000;
  public ListType mListType = ListType.LOCAL;
  protected BuzzListAdapter mBuzzListAdapter;
  protected View mFreezedLayer;
  protected View mShareMyStatusView;
  protected LinearLayout mLikeAndPostCommentView;
  protected boolean mOpenShareMyStatus = false;
  protected ImageView mImageViewTakeAPhoto;
  protected EditText mEditTextStatus;
  protected Button mButtonSend;
  IFacebookPermission facebookPermission = new IFacebookPermission() {

    @Override
    public List<String> getPermissions() {
      return IFacebookPermission.PUBLISH_STREAM;
    }
  };
  private MakeBuzzListener mMakeBuzzListener;
  private OnActionNoRefresh mOnActionNoRefresh;
  private OnActionRefresh mOnActionRefresh;
  private OnAddBuzzFromFavorite mOnAddBuzzFromFavorite;
  private PullToRefreshListView mPullToRefreshListView;
  private int mWorkingBuzzPosition = 0;
  private int mWorkingCommentPositionInBuzz = 0;
  private ListView mBuzzListView;
  private int mFirstSkip = 0;
  private int mTake;
  private View mBuzzListFooter;
  private View mEmptyData;
  private boolean mShowCreateNewBuzzView = true;
  private String mSelectedImagePath;
  private ProgressDialog progressDialog;
  private boolean mOpenLikeAndPostComment = false;
  private EditText mEditTextComment;
  private InputMethodManager mInputMethodManager;
  private String mShareMessage;
  private boolean isButtonEnable = true;
  private String newBuzzID;
  private String newCommentID;
  private boolean isNeedToRefresh = false;
  private int resumePosition = -1;
  private int size = -1;
  private int resumeTop = -1;
  private String resumeBuzzID;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Started");

      LogUtils.d(
          TAG,
          String.format(
              "onRefreshListener.onPullUpToRefresh: skip = %d, take = %d",
              mBuzzListAdapter.getCount(), mTake));
      startRequestServer(mBuzzListAdapter.getCount(), mTake);

      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Ended");
    }
  };
  private boolean endOfList;
  private List<BuzzListItem> listItem;
  private UploadImageProgress uploadImageProgress = new UploadImageProgress() {

    @Override
    public void uploadImageSuccess(UploadImageResponse response) {
      if (getActivity() == null) {
        return;
      }
      if (progressDialog.isShowing()) {
        progressDialog.dismiss();
      }

      deleteCapturedFile(mSelectedImagePath);
      mSelectedImagePath = null;

      String token = UserPreferences.getInstance().getToken();
      AddBuzzRequest buzzRequest = null;

      if (mShareMessage.length() > 0) {
        buzzRequest = new AddBuzzRequest(token, response.getImgId(),
            Constants.BUZZ_TYPE_IMAGE, mShareMessage);
      } else {
        buzzRequest = new AddBuzzRequest(token, response.getImgId(),
            Constants.BUZZ_TYPE_IMAGE);
      }

      restartRequestServer(LOADER_ID_ADD_BUZZ, buzzRequest);
      mEditTextStatus.setText("");
    }

    @Override
    public void uploadImageStart() {
      if (progressDialog == null) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.waiting));
        progressDialog.setCancelable(false);
      }

      if (!progressDialog.isShowing()) {
        progressDialog.show();
      }
    }

    @Override
    public void uploadImageFail(int code) {
      if (getActivity() == null) {
        return;
      }
      if (code == Response.SERVER_UPLOAD_IMAGE_ERROR) {
        com.application.ui.customeview.AlertDialog
            .showUploadImageErrorAlert(getActivity());
      } else {
        String message = getString(R.string.upload_fail);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
            .show();
      }
      if (progressDialog != null && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
    }
  };
  //set limit text msg

//	boolean limited =false;
//	public InputFilter [] fArray = new InputFilter[1];
  private boolean isAddingToFavorites = false;
  private int positionAddingToFavorite = -1;
  private boolean isRemovingFromFavorites = false;
  private int positionRemovingFromFavorites = -1;
  private String newBuzzIDFromFavorite;

  protected abstract RequestParams getRequestParams(int take, int skip);

  public void setPagingParams(int take) {
    this.mTake = take;
  }

  public void setOnActionNoRefresh(OnActionNoRefresh mOnActionNoRefresh) {
    this.mOnActionNoRefresh = mOnActionNoRefresh;
  }

  public void setOnActionRefresh(OnActionRefresh mOnActionRefresh) {
    this.mOnActionRefresh = mOnActionRefresh;
  }

  public void showCreateNewBuzzView(boolean show) {
    this.mShowCreateNewBuzzView = show;
  }

  public void setOnAddBuzzFromFavorite(
      OnAddBuzzFromFavorite mOnAddBuzzFromFavorite) {
    this.mOnAddBuzzFromFavorite = mOnAddBuzzFromFavorite;
  }

  public void setMakeBuzzListener(MakeBuzzListener mMakeBuzzListener) {
    this.mMakeBuzzListener = mMakeBuzzListener;
  }

  public void onCreateOrigin(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    mTake = getResources().getInteger(R.integer.take_buzzlist);
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mShareMessage = savedInstanceState.getString(KEY_COMMENT);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    requestDirtyWord();
    View view = inflater.inflate(R.layout.item_content_buzz, container,
        false);
    if (view != null) {
      initView(view);
    }
    return view;
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
  public void onStart() {
    LogUtils.d("ToanTK", "onStart");
    super.onStart();
    // refresh();
  }

  public void onStartOrigin() {
    super.onStart();
  }

  public void onActivityCreatedOrigin(Bundle saveInstanceState) {
    super.onActivityCreated(saveInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    LogUtils.d(TAG, "onActivityCreated Started");
    super.onActivityCreated(savedInstanceState);
    if (mBuzzListAdapter == null) {
      mBuzzListAdapter = new BuzzListAdapter(getActivity(), this, this, this);
      mBuzzListAdapter.setBaseBuzzListFragment(this);
      mBuzzListView.setAdapter(mBuzzListAdapter);
      mBuzzListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
      mBuzzListView.setDividerHeight((int) Utility.convertDpToPixel(10,
          mAppContext));
      mBuzzListView.setCacheColorHint(Color.TRANSPARENT);
      startRequestServer(mFirstSkip, mTake);
    } else {
      LogUtils.e(TAG, "mBuzzListAdapter khac null");
      mBuzzListView.setAdapter(mBuzzListAdapter);
      mBuzzListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
      mBuzzListView.setDividerHeight((int) Utility.convertDpToPixel(10,
          mAppContext));
      mBuzzListView.setCacheColorHint(Color.TRANSPARENT);
    }
    LogUtils.d(TAG, "onActivityCreated Ended");
  }

  private void visibleCommentBar(int visibility) {
    if (mLikeAndPostCommentView != null) {
      mLikeAndPostCommentView.setVisibility(visibility);
    }
  }

  @Override
  public void startRequest(int loaderId) {
    LogUtils.d(TAG, "startRequest Started");

    LogUtils.d(TAG, String.format("startRequest: loaderId = %d", loaderId));

    LogUtils.d(TAG, "startRequest Ended");
  }

  private void startRequestServer(int skip, int take) {
    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
    mPullToRefreshListView
        .setPullLabelFooter(getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshListView
        .setReleaseLabelFooter(getString(R.string.pull_to_load_more_release_label));
    restartRequestServer(LOADER_ID_LOAD_BUZZ_LIST,
        getRequestParams(take, skip));
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void initView(View view) {
    LogUtils.d(TAG, "initView Started");

    View vCreateNewBuzz = view
        .findViewById(R.id.ll_buzz_list_create_new_buzz);
    if (!mShowCreateNewBuzzView) {
      vCreateNewBuzz.setVisibility(View.GONE);
    }

    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.frame_list_item_buzz);
    mBuzzListView = mPullToRefreshListView.getRefreshableView();
    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);

    // FrameLayout btAction = null;
    if (mListType == ListType.FRIENDS) {
      mEmptyData = View.inflate(getActivity(),
          R.layout.fragment_friends_buzz_list_footer, null);

    } else if (mListType == ListType.FAVORITES) {
      mEmptyData = View.inflate(getActivity(),
          R.layout.fragment_favorites_buzz_list_footer, null);

    }

    mBuzzListFooter = View.inflate(getActivity(),
        R.layout.fragment_buzz_list_footer, null);
    mBuzzListView.addFooterView(mBuzzListFooter);
    mBuzzListFooter.setVisibility(View.GONE);

    mFreezedLayer = view.findViewById(R.id.ib_buzz_freezed_layer);
    mShareMyStatusView = view.findViewById(R.id.ll_share_my_status);
    mLikeAndPostCommentView = (LinearLayout) view
        .findViewById(R.id.ll_like_and_post_comment);
    mImageViewTakeAPhoto = (ImageView) view
        .findViewById(R.id.iv_buzz_take_photo);
    mEditTextStatus = (EditText) view.findViewById(R.id.ed_input_my_status);
    mButtonSend = (Button) mShareMyStatusView
        .findViewById(R.id.bt_buzz_send);
    mEditTextComment = (EditText) mLikeAndPostCommentView
        .findViewById(R.id.ed_input_comment);
    ImageButton ibTakeAPhoto = (ImageButton) view
        .findViewById(R.id.ib_buzz_take_a_photo);
    TextView tvInputAStatus = (TextView) view
        .findViewById(R.id.tv_buzz_input_a_status);
    if (mShareMessage == null) {
      mEditTextComment.setText(mShareMessage);
    }

    mImageViewTakeAPhoto.setOnClickListener(this);
    mEditTextStatus.setOnFocusChangeListener(this);
    mEditTextStatus.addTextChangedListener(this);

//		if (isFocus){
//			mEditTextStatus.post(new Runnable() {
//				@Override
//				public void run() {
//					mEditTextStatus.setFocusableInTouchMode(true);
//					mEditTextStatus.setFocusable(true);
//					mEditTextStatus.requestFocus();
//					openShareMyStatusView(Constants.BUZZ_TYPE_STATUS);
//				}
//			});
//		}

    mEditTextComment.setOnEditorActionListener(this);
    mEditTextComment.setOnFocusChangeListener(this);
    mButtonSend.setOnClickListener(this);
    ibTakeAPhoto.setOnClickListener(this);

    // disable paste
    tvInputAStatus.setLongClickable(false);
    tvInputAStatus.setOnClickListener(this);
    mFreezedLayer.setOnClickListener(this);
    mFreezedLayer.setOnTouchListener(this);

    LogUtils.d(TAG, "initView Ended");
  }

  private void refresh() {
    LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Started");

    LogUtils.d(TAG, String.format(
        "onRefreshListener.onPullDownToRefresh: skip = %d, take = %d",
        mFirstSkip, mTake));
    mBuzzListFooter.setVisibility(View.GONE);
    mBuzzListAdapter.clearAllData();
    // Remove Empty View
    if (mListType == ListType.FRIENDS || mListType == ListType.FAVORITES) {
      mEmptyData.setVisibility(View.GONE);
      mPullToRefreshListView.removeView(mEmptyData);
    }
    startRequestServer(mFirstSkip, mTake);

    LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Ended");
  }

  public void onResumeOrigin() {
    super.onResume();
  }

  @Override
  public void onResume() {
    super.onResume();
    isButtonEnable = true;
    if (isNeedToRefresh) {
      refreshData();
    } else {
      mBuzzListAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    LogUtils.d(TAG, "receiveResponse Started");

    if (getActivity() == null || loader == null || response == null) {
      LogUtils.d(TAG, String.format(
          "Parent Activity: %s, Loader: %s, Response: %s",
          getActivity(), loader, response));

      LogUtils.d(TAG, "receiveResponse Ended (1)");

      return;
    }

    int responseCode = response.getCode();
    switch (responseCode) {
      case Response.SERVER_BUZZ_NOT_FOUND:
      case Response.SERVER_ACCESS_DENIED:
        handleBuzzNotFound();
        mBuzzListAdapter.remove(mWorkingBuzzPosition);
        mBuzzListAdapter.notifyDataSetChanged();
        break;
      case Response.SERVER_COMMENT_NOT_FOUND:
        handleCommentNotFound();
        break;
      default:
        break;
    }

    switch (loader.getId()) {

      case LOADER_ID_LOAD_BUZZ_LIST:
        getLoaderManager().destroyLoader(LOADER_ID_LOAD_BUZZ_LIST);
        handleBuzzList((BuzzListResponse) response);
        break;
      case LOADER_ID_DELETE_BUZZ:
        handleDeleteBuzz((DeleteBuzzResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_DELETE_BUZZ);
        break;
      case LOADER_ID_DELETE_COMMENT:
        handleDeleteComment((DeleteCommentResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_DELETE_COMMENT);
        break;
      case LOADER_ID_LIKE_BUZZ:
        handleLikeBuzz((LikeBuzzResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_LIKE_BUZZ);
        break;
      case LOADER_ID_ADD_COMMENT:
        mShareMessage = null;
        handleAddComment((AddCommentResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_ADD_COMMENT);
        break;
      case LOADER_ID_ADD_BUZZ:
        handleAddBuzz((AddBuzzResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_ADD_BUZZ);
        break;
      case LOADER_ID_ADD_TO_FAVORITES:
        handleAddFavoriteResponse((AddFavoriteResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);
        break;

      case LOADER_ID_REMOVE_FROM_FAVORITES:
        handleRemoveFavoriteResponse((RemoveFavoriteResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
        break;

      default:
        break;
    }

    LogUtils.d(TAG, "receiveResponse Ended (2)");
  }

  private void handleAddBuzz(AddBuzzResponse response) {
    LogUtils.d(TAG, "handleAddBuzz Started");

    if (response.getCode() == Response.SERVER_SUCCESS) {

      // Close Share My Status view
      if (mOpenShareMyStatus) {
        closeShareMyStatusView();
      }
      newBuzzID = response.getBuzzId();
      // Refresh buzz list
      mBuzzListAdapter.clearAllData();
      startRequestServer(mFirstSkip, mTake);
      if (mMakeBuzzListener != null) {
        mMakeBuzzListener.onSuccess(newBuzzID);
      }
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "handleAddBuzz Ended");
  }

  private void handleAddComment(AddCommentResponse response) {
    LogUtils.d(TAG, "AddCommentResponse Started");

    LogUtils.d(TAG, String.format(
        "AddCommentResponse: mWorkingBuzzPosition = %d",
        mWorkingBuzzPosition));

    if (response.getCode() == Response.SERVER_SUCCESS) {
      UserPreferences.getInstance().saveNumberPoint(response.getPoint());

      // Close Like And Post Comment view
      if (mOpenLikeAndPostComment) {
        closeLikeAndPostCommentView();
      }
      newCommentID = response.getCommentId();
      // Refresh buzz list
      mBuzzListAdapter.clearAllData();
      startRequestServer(mFirstSkip, mTake);
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      int point = response.getCommentPoint();
      NotEnoughPointDialog.showForCommentBuzz(getActivity(), point);

      // CUONGNV01032016 : Remove show dialog het point
//			Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//			intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_COMMENT);
//			startActivity(intent);
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "AddCommentResponse Ended");
  }

  private void handleDeleteBuzz(DeleteBuzzResponse response) {
    LogUtils.d(TAG, "handleDeleteBuzz Started");

    LogUtils.d(TAG, String.format(
        "handleDeleteBuzz: mWorkingBuzzPosition = %d",
        mWorkingBuzzPosition));

    if (response.getCode() == Response.SERVER_SUCCESS) {
      BuzzListItem item = (BuzzListItem) (mBuzzListAdapter
          .remove(mWorkingBuzzPosition));
      if (mOnActionNoRefresh != null) {
        mOnActionNoRefresh.onDeleteBuzzSuccess(item);
      }
      mBuzzListAdapter.notifyDataSetChanged();
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "handleDeleteBuzz Ended");
  }

  private void handleDeleteComment(DeleteCommentResponse response) {
    LogUtils.d(TAG, "handleDeleteComment Started");

    LogUtils.d(
        TAG,
        String.format(
            "handleDeleteComment: mWorkingBuzzPosition = %d, mWorkingCommentPositionInBuzz = %d",
            mWorkingBuzzPosition, mWorkingCommentPositionInBuzz));

    if (response.getCode() == Response.SERVER_SUCCESS) {
      BuzzListItem current = (BuzzListItem) mBuzzListAdapter
          .getItem(mWorkingBuzzPosition);

      if (current != null) {
        ArrayList<BuzzListCommentItem> commentList = current
            .getCommentList();
        if (commentList != null
            && commentList.size() > mWorkingCommentPositionInBuzz) {
          BuzzListCommentItem deletedComment = commentList
              .remove(mWorkingCommentPositionInBuzz);

          int commentNumber = current.getCommentNumber();
          commentNumber--;
          if (commentNumber < 0) {
            commentNumber = 0;
          }
          current.setCommentNumber(commentNumber);

          mBuzzListAdapter.notifyDataSetChanged();
          if (mOnActionNoRefresh != null) {
            mOnActionNoRefresh.onDeleteCommentSuccess(current,
                deletedComment.cmt_id);
          }
        }
      }
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "handleDeleteComment Ended");
  }

  private void handleLikeBuzz(LikeBuzzResponse response) {
    LogUtils.d(TAG, "handleLikeBuzz Started");

    LogUtils.d(TAG, String.format(
        "handleLikeBuzz: mWorkingBuzzPosition = %d",
        mWorkingBuzzPosition));

    if (response.getCode() == Response.SERVER_SUCCESS) {
      BuzzListItem current = (BuzzListItem) mBuzzListAdapter
          .getItem(mWorkingBuzzPosition);
      if (current != null) {
        int currentLikeType = current.getIsLike();
        int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
        int currentLikeNumber = current.getLikeNumber();
        int newLikeNumber = currentLikeNumber;
        if (currentLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
          newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
          newLikeNumber--;
        } else {
          newLikeNumber++;
        }

        if (newLikeNumber < 0) {
          newLikeNumber = 0;
        }
        current.setIsLike(newLikeType);
        current.setLikeNumber(newLikeNumber);
        mBuzzListAdapter.notifyDataSetChanged();
        mOnActionNoRefresh.onLikeSuccess(current, newLikeType,
            newLikeNumber);
      }

    } else if (response.getCode() != Response.SERVER_SUCCESS) {
      if (mOpenLikeAndPostComment) {
        closeLikeAndPostCommentView();
      }

      if (response.getCode() == Response.SERVER_BUZZ_NOT_FOUND) {
        // Remove this item from buzz list.
        mBuzzListAdapter.remove(mWorkingBuzzPosition);
        mBuzzListAdapter.notifyDataSetChanged();
      }

      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    LogUtils.d(TAG, "handleLikeBuzz Ended");
  }

  private void handleBuzzList(BuzzListResponse response) {
    LogUtils.d(TAG, "handleBuzzList Started");
    endOfList = false;
    if (response.getCode() == Response.SERVER_SUCCESS) {
      listItem = response.getBuzzListItem();
      if (listItem != null) {
        if (listItem.size() > 0) {
          // Filter results
          filterBuzzListResponseResult(listItem);
          mBuzzListAdapter.appendList(listItem);
          // tro ve sau khi chon 1 buzz nao do
          if (resumeBuzzID != null) {
            BuzzListItem resumeItem = mBuzzListAdapter
                .getItemByBuzzID(resumeBuzzID);
            LogUtils.d("ToanTK", "resumeBuzzID  : " + resumeBuzzID);
            if (resumeItem != null) {
              int position = mBuzzListAdapter
                  .getPosition(resumeItem);
              LogUtils.d("ToanTK", "Position resume : "
                  + position);
              mBuzzListView.setSelection(position + 1);
            }
            resumeBuzzID = null;
          }
          // tro ve khi bi destroy
          else if (resumePosition != -1 && size != -1
              && resumeTop != -1) {
            mBuzzListView.setSelectionFromTop(resumePosition,
                resumeTop);
            LogUtils.d("ToanTk", "resumePosition : "
                + resumePosition + " size : " + size
                + " resumeTop : " + resumeTop);
            resumePosition = -1;
            size = -1;
            resumeTop = -1;

          }
          // If has new Buzz from AddBuzz, send this Buzz to other Tabs.
          if (newBuzzID != null) {
            BuzzListItem newAddedItem = mBuzzListAdapter
                .getItemByBuzzID(newBuzzID);
            if (newAddedItem != null) {
              if (mOnActionNoRefresh != null) {
                mOnActionNoRefresh
                    .onAddBuzzSuccess(newAddedItem);
              }
            }
            newBuzzID = null;
          }
          // Neu co BuzzID moi tu Favorite
          if (newBuzzIDFromFavorite != null) {
            BuzzListItem newAddedItem = mBuzzListAdapter
                .getItemByBuzzID(newBuzzIDFromFavorite);
            LogUtils.d("ToanTK addfromfavor",
                newAddedItem.getBuzzId());
            if (mOnAddBuzzFromFavorite != null) {
              mOnAddBuzzFromFavorite.onAddBuzz(newAddedItem);
            }
            newBuzzIDFromFavorite = null;
          }

          if (newCommentID != null) {
            mOnActionNoRefresh.onAddCommentSuccess(mBuzzListAdapter
                .getItemByCommentID(newCommentID));
            newCommentID = null;
          }
        } else {
          endOfList = true;
        }
      }
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }

    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.onRefreshComplete();
    }

    // Check whether list is empty and this has Friends or Favorites type
    if (mBuzzListAdapter.getCount() == 0
        && (mListType == ListType.FRIENDS || mListType == ListType.FAVORITES)) {
      mEmptyData.setVisibility(View.VISIBLE);
      mPullToRefreshListView.setEmptyView(mEmptyData);
      mPullToRefreshListView
          .setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    } else {
      if (endOfList) {
        mBuzzListFooter.setVisibility(View.VISIBLE);
        mPullToRefreshListView
            .setMode(PullToRefreshBase.Mode.PULL_FROM_START);
      }
    }

    onRefreshCompleted();

    LogUtils.d(TAG, "handleBuzzList Ended");
  }

  private boolean filterBuzzListResponseResult(List<BuzzListItem> listBuzzes) {
    boolean hasChanges = false;

    if (listBuzzes != null) {
      Context context = getActivity();
      List<BuzzListItem> distinctResult = new ArrayList<BuzzListItem>();
      boolean contains;
      int size = listBuzzes.size();
      BuzzListItem item = null;
      String itemId = null;
      int distinctSize;
      for (int j = size - 1; j >= 0; j--) {
        contains = false;
        item = listBuzzes.get(j);
        itemId = item.getBuzzId();
        distinctSize = distinctResult.size();
        for (int i = 0; i < distinctSize; i++) {
          if (distinctResult.get(i).getBuzzId().equals(itemId)) {
            contains = true;
            break;
          }
        }

        if (contains
            || Utility.isBlockedWithUser(context, item.getUserId())
            || mBuzzListAdapter.contains(itemId)) {
          listBuzzes.remove(j);
          hasChanges = true;
        } else {
          // Filter Buzz's comments
          boolean commentFiltered = filterBuzzComments(item);
          hasChanges = hasChanges | commentFiltered;
          distinctResult.add(item);
        }
      }
    }

    return hasChanges;
  }

  private boolean filterBuzzComments(BuzzListItem buzz) {
    ArrayList<BuzzListCommentItem> listComments = buzz.getCommentList();
    boolean hasChanges = false;
    if (listComments != null) {
      Context context = getActivity();
      int size = listComments.size();
      BuzzListCommentItem item;
      for (int i = size - 1; i >= 0; i--) {
        item = listComments.get(i);
        if (Utility.isBlockedWithUser(context, item.user_id)) {
          listComments.remove(i);
          buzz.setCommentNumber(buzz.getCommentNumber() - 1);
          hasChanges = true;
        }
      }
    }
    return hasChanges;
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_ID_LOAD_BUZZ_LIST:
        response = new BuzzListResponse(data);
        break;
      case LOADER_ID_DELETE_BUZZ:
        response = new DeleteBuzzResponse(data);
        break;
      case LOADER_ID_DELETE_COMMENT:
        response = new DeleteCommentResponse(data);
        break;
      case LOADER_ID_LIKE_BUZZ:
        response = new LikeBuzzResponse(data);
        break;
      case LOADER_ID_ADD_COMMENT:
        response = new AddCommentResponse(data);
        break;
      case LOADER_ID_ADD_BUZZ:
        response = new AddBuzzResponse(data);
        break;

      case LOADER_ID_ADD_TO_FAVORITES:
        response = new AddFavoriteResponse(data);
        break;

      case LOADER_ID_REMOVE_FROM_FAVORITES:
        response = new RemoveFavoriteResponse(data);
        break;

      default:
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  protected abstract void onRefreshCompleted();

  protected void openShareMyStatusView(int type) {
    mOpenShareMyStatus = true;

    mFreezedLayer.setVisibility(View.VISIBLE);
    mShareMyStatusView.setVisibility(View.VISIBLE);

    visibleCommentBar(View.GONE);

    if (mShareMessage != null) {
      mEditTextStatus.setText(mShareMessage);
    } else {
      switch (type) {
        case Constants.BUZZ_TYPE_STATUS:
          mEditTextStatus.setText("");
          mEditTextStatus.setTextColor(getResources().getColor(R.color.color_text_default));
          break;
        case Constants.BUZZ_TYPE_IMAGE:
          mEditTextStatus.setText("");
          break;
        default:
          break;
      }
    }

    mEditTextStatus.requestFocus();
    syncShareButton();
  }

  private void uploadImageToServer(File file) {
    ImageUploader mImageUploader = new ImageUploader(uploadImageProgress);

    String token = UserPreferences.getInstance().getToken();
    String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
    UploadImageRequest imageRequest = new UploadImageRequest(token,
        UploadImageRequest.PUBLISH_IMAGE, file, md5Encrypted);
    mImageUploader.execute(imageRequest);
  }

  protected void closeShareMyStatusView() {
    Utility.hideSoftKeyboard(getActivity());
    if (mImageViewTakeAPhoto != null) {
      setThumbUploadDefault();
    }
    deleteCapturedFile(mSelectedImagePath);
    mSelectedImagePath = null;
    mShareMessage = null;

    mFreezedLayer.setVisibility(View.GONE);
    mShareMyStatusView.setVisibility(View.GONE);
    visibleCommentBar(View.GONE);

    mOpenShareMyStatus = false;
  }

  private void openLikeAndPostCommentView() {
    mOpenLikeAndPostComment = true;

    mFreezedLayer.setVisibility(View.VISIBLE);
    mShareMyStatusView.setVisibility(View.GONE);
    visibleCommentBar(View.VISIBLE);

    BuzzListItem bli = (BuzzListItem) mBuzzListAdapter
        .getItem(mWorkingBuzzPosition);

    mEditTextComment.setText("");
    String userID = bli.getUserId();
    UserPreferences preferences = UserPreferences.getInstance();
    String myId = preferences.getUserId();
    int commentPoint = bli.getCommentPoint();
    if (commentPoint > 0 && !myId.equals(userID)) {
      String format = getResources().getString(
          R.string.timeline_comment_point_hint);
      mEditTextComment
          .setHint(MessageFormat.format(format, commentPoint));
    } else {
      mEditTextComment.setHint(getResources().getString(
          R.string.timeline_comment_hint));
    }

    mEditTextComment.requestFocus();
  }

  protected void closeLikeAndPostCommentView() {
    mFreezedLayer.setVisibility(View.GONE);
    mShareMyStatusView.setVisibility(View.GONE);
    visibleCommentBar(View.GONE);

    mOpenLikeAndPostComment = false;
  }

  private boolean hasNewSharedBuzz(String pathToImage, String status) {
    if (pathToImage != null && pathToImage.trim().length() > 0) {
      return true;
    } else {
      if (status != null
          && status.replace("\u3000", " ").trim().length() > 0) {
        return true;
      }
    }

    return false;
  }

  protected void discardInput() {
    if (mOpenShareMyStatus) {
      closeShareMyStatusView();
    }

    if (mOpenLikeAndPostComment) {
      closeLikeAndPostCommentView();
    }

    mEditTextStatus.setText("");

    deleteCapturedFile(mSelectedImagePath);
    mSelectedImagePath = null;
  }

  private void showSoftKey(View v) {
    LogUtils.d(TAG, "showSoftKey Started");

    v.requestFocus();
    if (mInputMethodManager == null) {
      mInputMethodManager = (InputMethodManager) getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    mInputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

    LogUtils.d(TAG, "showSoftKey Ended");
  }

  private void hideSoftKey(View v) {
    LogUtils.d(TAG, "hideSoftKey Started");
    if (mInputMethodManager == null) {
      mInputMethodManager = (InputMethodManager) getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    LogUtils.d(TAG, "hideSoftKey Ended");
  }

  private void handleBuzzNotFound() {
    LogUtils.d(TAG, "handleBuzzNotFound Started");

    Context context = getActivity();
    String message = context.getString(R.string.buzz_item_not_found);
    Utility.showToastMessage(context, message);

    LogUtils.d(TAG, "handleBuzzNotFound Ended");
  }

  private void handleCommentNotFound() {
    LogUtils.d(TAG, "handleCommentNotFound Started");

    Context context = getActivity();
    String message = context.getString(R.string.comment_item_not_found);
    Utility.showToastMessage(context, message);

    LogUtils.d(TAG, "handleCommentNotFound Ended");
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case Activity.RESULT_OK:
        switch (requestCode) {
//				case REQUEST_CODE_GET_IMAGE:
//					ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//					if (mMediaSelectedList != null) {
//						for (MediaItem mediaItem : mMediaSelectedList) {
//							mSelectedImagePath = mediaItem.getCroppedPath();
//							// Open Share My Status view
//							openShareMyStatusView(Constants.BUZZ_TYPE_IMAGE);
//							// Set data to image view (decoded bitmap)
//							setThumbUploadImage(mSelectedImagePath);
//							syncShareButton();
//						}
//					} else {
//						LogUtils.e(TAG, "Error to get media, NULL");
//					}
//					break;
          case Camera.REQUEST_CODE_CAMERA:
          case Gallery.REQUEST_CODE_GALLERY:
            Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
            for (Parcelable parcelable : files) {
              MediaFile file = (MediaFile) parcelable;
              mSelectedImagePath = file.getPath();
              // Open Share My Status view
              openShareMyStatusView(Constants.BUZZ_TYPE_IMAGE);
              // Set data to image view (decoded bitmap)
              setThumbUploadImage(mSelectedImagePath);
              syncShareButton();
            }
            break;
          case MyProfileFragment.REQUEST_CODE_GET_AVATAR:
            if (data != null) {
              String fragment_tag = data
                  .getStringExtra(Constants.FRAGMENT_TAG);
              if (MyProfileFragment.TAG_FRAGMENT_BUZZ_DETAIL
                  .equals(fragment_tag)) {
                String buzzId = data
                    .getStringExtra(Constants.INTENT_BUZZ_ID);
                if (buzzId != null && !buzzId.equals("")) {
                  BuzzDetail buzzDetailFragment = BuzzDetail
                      .newInstance(buzzId,
                          Constants.BUZZ_TYPE_NONE);
                  replaceFragment(buzzDetailFragment,
                      MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
                }
              } else if (MyProfileFragment.TAG_FRAGMENT_USER_PROFILE
                  .equals(fragment_tag)) {
                String userId = data
                    .getStringExtra(Constants.INTENT_USER_ID);
                if (userId != null && !userId.equals("")) {
                  replaceFragment(
                      MyProfileFragment.newInstance(userId),
                      MyProfileFragment.TAG_FRAGMENT_USER_PROFILE);
                }
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

  private void deleteCapturedFile(String path) {
    if (path != null && path.length() > 0) {
      File f = new File(path);
      f.delete();
    }
  }

  @Override
  public void clearAllFocusableFields() {
    mEditTextStatus.clearFocus();
    mEditTextComment.clearFocus();
  }

  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected boolean hasFacebookController() {
    return true;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mShareMessage = mEditTextStatus.getText().toString();
    outState.putString(KEY_COMMENT, mShareMessage);
  }

  protected void deleteComment(int buzzPosition, int commentPosition) {
    mWorkingBuzzPosition = buzzPosition;
    mWorkingCommentPositionInBuzz = commentPosition;
    BuzzListItem currentBuzz = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);

    if (currentBuzz != null) {
      ArrayList<BuzzListCommentItem> commentList = currentBuzz
          .getCommentList();
      if (commentList != null && commentPosition < commentList.size()) {
        BuzzListCommentItem currentComment = commentList
            .get(commentPosition);

        showDialogConfirmDeleteComment(currentBuzz, currentComment);
      }
    }
  }

  public void deleteBuzzAt(int position) {
    mWorkingBuzzPosition = position;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(position);

    if (current != null) {
      showDialogConfirmDeleteBuzz(current);
    }
  }

  @Override
  public void chatWithUserAt(int buzzPosition) {
    mWorkingBuzzPosition = buzzPosition;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);
    if (current != null) {
      ChatFragment chatFragment = ChatFragment.newInstance(
          current.getUserId(), current.getAvatarId(),
          current.getUserName(), current.getGender(), true);
      replaceFragment(chatFragment, MainActivity.TAG_FRAGMENT_CHAT);
    }
  }

  @Override
  public void viewDetailPictureAt(int buzzPosition) {
    if (!isButtonEnable) {
      return;
    }
    isButtonEnable = false;
    mWorkingBuzzPosition = buzzPosition;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);
    if (current == null) {
      return;
    }
    // Increase number of Views
    this.callRefresh(current.getBuzzId());
    current.setSeenNumber(current.getSeenNumber() + 1);
    mBuzzListAdapter.notifyDataSetChanged();

    Intent intent = new Intent(getActivity(),
        DetailPictureBuzzActivity.class);
    Bundle userData = ProfilePictureData.parseDataToBundle(current);
    intent.putExtras(userData);

    getActivity().startActivity(intent);
  }

  @Override
  public void viewBuzzDetailAt(int buzzPosition) {
    mWorkingBuzzPosition = buzzPosition;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);

    if (current != null) {
      BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(
          current.getBuzzId(), current.getBuzzType());
      buzzDetailFragment.showSoftkeyWhenStart(true);
      this.callRefresh(current.getBuzzId());
      replaceFragment(buzzDetailFragment,
          MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
    }
  }

  @Override
  public void likeBuzzAt(int buzzPosition) {
    mWorkingBuzzPosition = buzzPosition;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);
    if (current != null) {
      int currentLikeType = current.getIsLike();
      int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
      // int currentLikeNumber = current.getLikeNumber();
      // int newLikeNumber = currentLikeNumber;
      if (currentLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
        newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
        // newLikeNumber--;
      } else {
        // newLikeNumber++;
      }

      // if (newLikeNumber < 0) {
      // newLikeNumber = 0;
      // }
      //
      // current.setIsLike(newLikeType);
      // current.setLikeNumber(newLikeNumber);
      // mBuzzListAdapter.notifyDataSetChanged();

      LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(
          UserPreferences.getInstance().getToken(),
          current.getBuzzId(), newLikeType);
      restartRequestServer(LOADER_ID_LIKE_BUZZ, likeBuzzRequest);
    }
  }

  @Override
  public void openLikeAndPostCommentAt(int buzzPosition) {
    mWorkingBuzzPosition = buzzPosition;
    openLikeAndPostCommentView();
  }

  @Override
  public void showUserInfoAt(int buzzPosition) {
    mWorkingBuzzPosition = buzzPosition;
    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);

    if (current != null) {
      this.callRefresh(current.getBuzzId());
      replaceFragment(MyProfileFragment.newInstance(current.getUserId()),
          MainActivity.TAG_FRAGMENT_MY_PROFILE);
    }
  }

  @Override
  public void reportBuzz() {
  }

  @Override
  public void onDeleteComment(int buzzPosition, int commentPosition) {
    deleteComment(buzzPosition, commentPosition);
  }

  @Override
  public void onReplyComment(String commentId, int commentPosition, int buzzPosition) {
    navigateToBuzzDetail(true, commentId, buzzPosition, commentPosition);
  }

  @Override
  public void onShowMoreComment(int commentPosition, String commentId, int skip, int buzzPosition) {
    navigateToBuzzDetail(false, commentId, buzzPosition, commentPosition);
  }

  private void navigateToBuzzDetail(boolean isReply, String commentId, int buzzPosition,
      int commentPosition) {
    BuzzListItem item = (BuzzListItem) mBuzzListAdapter
        .getItem(buzzPosition);
    if (item.getIsApproved() == Constants.IS_NOT_APPROVED) {
      return;
    }
    BuzzDetail buzzDetail;
    if (isReply) {
      buzzDetail = BuzzDetail.newInstance(item.getBuzzId(),
          Constants.BUZZ_TYPE_NONE, BuzzDetail.INPUT_TYPE_SUB_COMMENT, commentId, commentPosition);

    } else {
      buzzDetail = BuzzDetail.newInstance(item.getBuzzId(),
          Constants.BUZZ_TYPE_NONE);
    }
    resumeBuzzID = item.getBuzzId();
    replaceFragment(buzzDetail, MainActivity.TAG_FRAGMENT_BUZZ_DETAIL);
  }

  @Override
  public void onDeleteSubComment(String commentId, String subCommentId, int commentPosition,
      int subCommentPosition) {
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_buzz_take_photo:
        selectImageHandler();
        break;
      case R.id.bt_buzz_send:
        sendStatus();
        break;
      case R.id.ib_buzz_take_a_photo:
        selectImageHandler();
        break;
      case R.id.tv_buzz_input_a_status:
        openShareMyStatusView(Constants.BUZZ_TYPE_STATUS);
        break;
      case R.id.ib_buzz_freezed_layer:
        if (mOpenShareMyStatus) {
          closeShareMyStatusView();
        }
        if (mOpenLikeAndPostComment) {
          closeLikeAndPostCommentView();
        }
        break;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (mOpenShareMyStatus) {
      closeShareMyStatusView();
    }
    if (mOpenLikeAndPostComment) {
      closeLikeAndPostCommentView();
    }
    return false;
  }

  private void selectImageHandler() {
    mShareMessage = mEditTextStatus.getText().toString();
//        MediaOptions.Builder builder = new MediaOptions.Builder();
//        MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//        FragmentActivity activity = getActivity();
//        MediaPickerActivity.open(activity.getSupportFragmentManager().findFragmentById(R.id.activity_main_content), REQUEST_CODE_GET_IMAGE, options);

    new Gallery.Builder()
        .cropOutput(true)
        .fixAspectRatio(true)
        .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
        .multiChoice(false)
        .build()
        .start(BaseBuzzListFragment.this);
  }

  private void sendStatus() {
    String token = UserPreferences.getInstance().getToken();
    mShareMessage = mEditTextStatus.getText().toString()
        .replace("\u3000", " ").trim();
    if (!Utility.isContainDirtyWord(getActivity(), mEditTextStatus)) {
      if (mSelectedImagePath != null && mSelectedImagePath.length() > 0) {
        uploadImageToServer(new File(mSelectedImagePath));
        mSelectedImagePath = null;
      } else {
        if (mShareMessage.length() > 0) {
          AddBuzzRequest buzzRequest = new AddBuzzRequest(token,
              mShareMessage, Constants.BUZZ_TYPE_STATUS);
          restartRequestServer(LOADER_ID_ADD_BUZZ, buzzRequest);
          mEditTextStatus.setText("");
        }
      }
    }
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (!hasFocus) {
      hideSoftKey(v);
    } else {
      showSoftKey(v);
    }
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    boolean enableSend = hasNewSharedBuzz(mSelectedImagePath, s.toString());
    mButtonSend.setEnabled(enableSend);
    syncShareButton();

//		String msg = s.toString();
//		if(java.text.Normalizer.normalize(msg, Normalizer.Form.NFKC).length()==2000)
//		{
//			limited = true;
//			fArray[0] = new InputFilter.LengthFilter(msg.length());
//			mEditTextStatus.setFilters(fArray);
//		}
//		else
//		{
//			if(limited)
//			{
//				limited = false;
//				fArray[0] = new InputFilter.LengthFilter(Integer.MAX_VALUE);
//				mEditTextStatus.setFilters(fArray);
//			}
//		}
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count,
      int after) {
  }

  @Override
  public void afterTextChanged(Editable s) {
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if ((actionId == EditorInfo.IME_ACTION_SEND)
        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
      if (!Utility.isContainDirtyWord(getActivity(), mEditTextComment)) {
        String commentValue = v.getText().toString()
            .replace("\u3000", " ").trim();
        if (commentValue.length() != 0) {
          BuzzListItem currentBuzz = (BuzzListItem) mBuzzListAdapter
              .getItem(mWorkingBuzzPosition);
          if (currentBuzz != null) {
            String token = UserPreferences.getInstance().getToken();
            AddCommentRequest addCommentRequest = new AddCommentRequest(
                token, currentBuzz.getBuzzId(), commentValue);
            restartRequestServer(LOADER_ID_ADD_COMMENT,
                addCommentRequest);
            closeLikeAndPostCommentView();
            // Hide keyboard
            v.setText("");
            hideSoftKey(v);
          }
        }
      }
    }

    return true;
  }

  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
      getImageFetcher().setPauseWork(true);
    } else {
      getImageFetcher().setPauseWork(false);
    }
  }

  @Override
  public void handleFavorite(int position) {
    mWorkingBuzzPosition = position;

    BuzzListItem current = (BuzzListItem) mBuzzListAdapter
        .getItem(position);

    String userId = current.getUserId();
    FavouritedPrefers favouritedPrefers = FavouritedPrefers.getInstance();

    if (favouritedPrefers.hasContainFav(userId)) {
      executeRemoveFromFavorites(position, userId);
    } else {
      executeAddToFavorites(position, userId);
    }
  }

  private void executeAddToFavorites(int position, String userId) {
    if (!isAddingToFavorites) {
      isAddingToFavorites = true;
      positionAddingToFavorite = position;

      String token = UserPreferences.getInstance().getToken();
      AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(
          token, userId);
      restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
    }
  }

  private void executeRemoveFromFavorites(int position, String userId) {
    if (!isRemovingFromFavorites) {
      isRemovingFromFavorites = true;
      positionRemovingFromFavorites = position;

      String token = UserPreferences.getInstance().getToken();
      RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
          token, userId);
      restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES,
          removeFavoriteRequest);
    }
  }

  private void handleAddFavoriteResponse(AddFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS
        && isAddingToFavorites) {
      getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);

      if (positionAddingToFavorite > -1
          && positionAddingToFavorite < mBuzzListAdapter.getCount()) {
        BuzzListItem addingItem = (BuzzListItem) mBuzzListAdapter
            .getItem(positionAddingToFavorite);

        String userId = addingItem.getUserId();
        FavouritedPrefers.getInstance().saveFav(userId);
        UserPreferences.getInstance().increaseFavorite();

        mBuzzListAdapter.notifyDataSetChanged();
        showDialogAddFavoriteSuccess(addingItem);
        positionAddingToFavorite = -1;
      }
    }

    isAddingToFavorites = false;
  }

  private void handleRemoveFavoriteResponse(RemoveFavoriteResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS
        && isRemovingFromFavorites) {
      getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);

      if (positionRemovingFromFavorites > -1
          && positionRemovingFromFavorites < mBuzzListAdapter
          .getCount()) {
        BuzzListItem removingItem = (BuzzListItem) mBuzzListAdapter
            .getItem(positionRemovingFromFavorites);

        String userId = removingItem.getUserId();
        FavouritedPrefers.getInstance().removeFav(userId);
        UserPreferences.getInstance().decreaseFavorite();

        mBuzzListAdapter.notifyDataSetChanged();
        showDialogRemoveFavoritesSuccess(removingItem);
        positionRemovingFromFavorites = -1;
      }
    }

    isRemovingFromFavorites = false;
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
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

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

  private void showGiveGiftFragment(BuzzListItem addingItem) {
    GiftCategories categories = new GiftCategories("get_all_gift", 0,
        getResources().getString(R.string.give_gift_all_title), 1);
    ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
        addingItem.getUserId(), addingItem.getUserName(), categories);
    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  private void showDialogConfirmDeleteBuzz(final BuzzListItem current) {
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.timeline_delete_buzz),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(
                UserPreferences.getInstance().getToken(), current
                .getBuzzId());
            restartRequestServer(LOADER_ID_DELETE_BUZZ, deleteBuzzRequest);
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

  private void showDialogConfirmDeleteComment(final BuzzListItem currentBuzz,
      final BuzzListCommentItem currentComment) {
    AlertDialog mConfirmDialog = new CustomConfirmDialog(
        getActivity(), "", getString(R.string.timeline_delete_comment),
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest(
                UserPreferences.getInstance().getToken(), currentBuzz
                .getBuzzId(), currentComment.cmt_id);
            restartRequestServer(LOADER_ID_DELETE_COMMENT,
                deleteCommentRequest);
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

  private void setThumbUploadDefault() {
    mImageViewTakeAPhoto.setImageResource(R.drawable.ic_mypage_camera);
  }

  private void setThumbUploadImage(String imgPath) {
    int size = getResources().getDimensionPixelSize(
        R.dimen.upload_buzz_thumb_size);
    Bitmap thumb = ImageResizer.decodeSampledBitmapFromFile(imgPath, size,
        size, null);
    mImageViewTakeAPhoto.setImageBitmap(thumb);
  }

  private void syncShareButton() {
    if (mEditTextStatus.getText().length() <= 0
        && TextUtils.isEmpty(mSelectedImagePath)) {
      mButtonSend.setEnabled(false);
    } else {
      mButtonSend.setEnabled(true);
    }
  }

  public void updateData() {
    if (mBuzzListAdapter != null) {
      mBuzzListAdapter.notifyDataSetChanged();
    }
  }

  public boolean removeItem(BuzzListItem item) {
    boolean isRemoveSuccess = false;
    if (mBuzzListAdapter != null) {
      int position = mBuzzListAdapter.getPosition(item);
      if (position != -1) {
        mBuzzListAdapter.remove(position);
        isRemoveSuccess = true;
      }
    }
    return isRemoveSuccess;
  }

  public boolean removeComment(BuzzListItem item, String commentID) {
    if (mBuzzListAdapter != null) {
      int position = mBuzzListAdapter.getPosition(item);
      if (position != -1) {
        BuzzListItem workingItem = (BuzzListItem) mBuzzListAdapter
            .getItem(position);
        ArrayList<BuzzListCommentItem> commentList = workingItem
            .getCommentList();

        if (commentList != null) {
          for (BuzzListCommentItem commentItem : commentList) {
            if (commentItem.cmt_id.equals(commentID)) {
              commentList.remove(commentItem);
              int commentNumber = workingItem.getCommentNumber();
              commentNumber--;
              if (commentNumber < 0) {
                commentNumber = 0;
              }
              workingItem.setCommentNumber(commentNumber);
              return true;
            }
          }
        }
      }
    }
    return false;

  }

  public void likeBuzz(BuzzListItem item, int newLikeType, int newLikeNumber) {
    if (mBuzzListAdapter != null) {
      int position = mBuzzListAdapter.getPosition(item);
      if (position != -1) {
        BuzzListItem current = (BuzzListItem) mBuzzListAdapter
            .getItem(position);
        if (current != null) {
          current.setIsLike(newLikeType);
          current.setLikeNumber(newLikeNumber);
        }
      }

    }
  }

  public void addBuzz(BuzzListItem item) {
    if (mBuzzListAdapter != null) {
      if (!mBuzzListAdapter.contains(item.getBuzzId())) {
        mBuzzListAdapter.addBuzzToTop(item);
      }

    }
  }

  public void addComment(BuzzListItem item) {
    if (mBuzzListAdapter != null) {
      int position = mBuzzListAdapter.getPosition(item);
      if (position != -1) {
        mBuzzListAdapter.remove(position);
        mBuzzListAdapter.addBuzzListItem(item, position);
      }
    }
  }

  public void setRefresh() {
    isNeedToRefresh = true;
  }

  public boolean isNeedToRefresh() {
    return isNeedToRefresh;
  }

  public void refreshData() {
    isNeedToRefresh = false;
    if (size != -1) {
      mBuzzListAdapter.clearAllData();
      startRequestServer(mFirstSkip, size);
    }
  }

  public void callRefresh(String resumeBuzzID) {
    if (resumeBuzzID != null) {
      this.setResumeBuzzID(resumeBuzzID);
    }
    if (mOnActionRefresh != null) {
      mOnActionRefresh.onRefresh();
    }
  }

  public void setResumeBuzzID(String resumeBuzzID) {
    this.resumeBuzzID = resumeBuzzID;
  }

  public void setNewBuzzID(String newBuzzIDFromFavorite) {
    this.newBuzzIDFromFavorite = newBuzzIDFromFavorite;
  }

  @Override
  public void onStop() {
    if (mBuzzListView != null) {
      resumePosition = mBuzzListView.getFirstVisiblePosition();
      View v = mBuzzListView.getChildAt(0);
      resumeTop = (v == null) ? 0 : v.getTop();
      size = mBuzzListAdapter.getCount();
    }
    LogUtils.d("ToanTK", "size = " + size);
    this.callRefresh(null);
    super.onStop();
  }

  public boolean isOpenShareStatus() {
    return mOpenShareMyStatus;
  }

  public enum ListType {
    LOCAL, FRIENDS, FAVORITES, USER
  }

  public interface MakeBuzzListener {

    void onSuccess(String buzzID);
  }

  public interface OnActionNoRefresh {

    void onDeleteBuzzSuccess(BuzzListItem item);

    void onDeleteCommentSuccess(BuzzListItem item, String commentID);

    void onAddBuzzSuccess(BuzzListItem item);

    void onLikeSuccess(BuzzListItem item, int newLikeType, int newLikeNumber);

    void onAddCommentSuccess(BuzzListItem item);
  }

  public interface OnActionRefresh {

    void onRefresh();
  }

  public interface OnAddBuzzFromFavorite {

    void onAddBuzz(BuzzListItem buzzItem);
  }

}
