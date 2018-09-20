package com.application.ui;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.application.Config;
import com.application.call.LinphoneVideoCall;
import com.application.call.LinphoneVoiceCall;
import com.application.chat.AuthenticationMessage;
import com.application.chat.ChatControlPanel;
import com.application.chat.ChatControlPanel.IOnControlClicked;
import com.application.chat.ChatManager;
import com.application.chat.ChatManager.IShowDialog;
import com.application.chat.ChatMessage;
import com.application.chat.ChatUtils;
import com.application.chat.DialogUploadProgress;
import com.application.chat.EmojiPanel;
import com.application.chat.EmojiPanel.IOnEmojiSelected;
import com.application.chat.FileMessage;
import com.application.chat.MessageClient;
import com.application.chat.MessageStatus;
import com.application.chat.PreviewStickerView;
import com.application.chat.PreviewStickerView.OnHandleStickerListener;
import com.application.chat.RecMicToMp3Tamtd;
import com.application.common.GalleryActivity;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddBlockUserRequest;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.CheckCallRequest;
import com.application.connection.request.CheckUnlockRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.GetNewChatMessageRequest;
import com.application.connection.request.GetPointRequest;
import com.application.connection.request.GetVideoUrlRequest;
import com.application.connection.request.HistoryRequest;
import com.application.connection.request.MarkReadsRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.request.ReportRequest;
import com.application.connection.request.UnlockRequest;
import com.application.connection.response.AddBlockUserResponse;
import com.application.connection.response.AddFavoriteResponse;
import com.application.connection.response.CheckCallResponse;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.GetBasicInfoResponse;
import com.application.connection.response.GetNewChatMessageResponse;
import com.application.connection.response.GetPointResponse;
import com.application.connection.response.GetVideoUrlResponse;
import com.application.connection.response.HistoryResponse;
import com.application.connection.response.MarkReadsResponse;
import com.application.connection.response.RemoveBlockUserResponse;
import com.application.connection.response.RemoveFavoriteResponse;
import com.application.connection.response.ReportResponse;
import com.application.connection.response.UnlockResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.constant.UnlockType;
import com.application.downloadmanager.AndGDownloadManager;
import com.application.downloadmanager.ChatDownloadManager;
import com.application.downloadmanager.DownloadState;
import com.application.downloadmanager.IDownloadManager;
import com.application.downloadmanager.IDownloadProgress;
import com.application.entity.CallUserInfo;
import com.application.entity.GiftCategories;
import com.application.entity.TimeAudioHold;
import com.application.model.ChatUser;
import com.application.provider.UriCompat;
import com.application.service.ChatService;
import com.application.service.DataFetcherService;
import com.application.status.IStatusChatChanged;
import com.application.status.MessageInDB;
import com.application.status.StatusConstant;
import com.application.status.StatusController;
import com.application.ui.buzz.UserBuzzListFragment;
import com.application.ui.chat.ChatAdapter;
import com.application.ui.chat.ChatAdapter.IOnGetVideoURL;
import com.application.ui.chat.ChatAdapter.IOnOpenImage;
import com.application.ui.chat.ChatDetailPictureActivity;
import com.application.ui.chat.ChatMoreLayout;
import com.application.ui.chat.OnChatMoreListener;
import com.application.ui.chat.VideoViewActivity;
import com.application.ui.customeview.AudioRecordViewCustom;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.DispatchTouchLayout;
import com.application.ui.customeview.DispatchTouchLayout.OnDispatchListener;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.picker.MediaPickerErrorDialog;
import com.application.ui.picker.MessageUtils;
import com.application.ui.profile.AccountStatus;
import com.application.ui.profile.DetailPicturePreviousPhotoActivity;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.uploader.UploadResponse;
import com.application.uploadmanager.ChatUploadManager;
import com.application.uploadmanager.CustomUploadService;
import com.application.uploadmanager.IUploadCustom;
import com.application.uploadmanager.IUploadResource;
import com.application.uploadmanager.UploadState;
import com.application.util.EmojiUtils;
import com.application.util.IntentUtils;
import com.application.util.LogUtils;
import com.application.util.ScreenReceiver;
import com.application.util.StorageUtil;
import com.application.util.StringUtils;
import com.application.util.Utility;
import com.application.util.ViewUtil;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.ChatDownloadPrefers;
import com.application.util.preferece.ChatMessagePreference;
import com.application.util.preferece.ChatUploadPrefers;
import com.application.util.preferece.DownloadFileTempPrefers;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.example.tux.mylab.utils.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.CirclePageIndicator;
import glas.bbsystem.R;
import java.io.File;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

enum SCREEN_STATUS {
  ON, OFF, PRESENT
}

public class ChatFragment extends TrackingBlockFragment implements
    OnClickListener, ResponseReceiver, OnTouchListener,
    OnItemClickListener, IUploadCustom, IDownloadProgress, IShowDialog,
    IOnGetVideoURL, IOnOpenImage {

  public static final String ACTION_STOP_RECORD = "stop_record";
  private static final String TAG = "ChatFragment";
  // Intent key
  public static final String ACTION_UPDATE_UNREAD_MSG_OF_FRIEND = "update.unread.msg.when.join.chat.room";
  public static final String EXTRA_UPDATE_UNREAD_MSG_OF_FRIEND = "extra.unread.msg.when.join.chat.room";

  private static final String KEY_PARTNER_ID = "partner_id";
  private static final String KEY_PARTNER_NAME = "partner_name";
  private static final String KEY_PARTNER_AVATA = "partner_avata";
  private static final String KEY_PARTNER_GENDER = "partner_gender";
  private static final String KEY_RECEIVED_MESSAGE_NUM = "received_message_num";
  private static final String KEY_DOWNLOAD_ID = "download_id";
  private static final String KEY_IS_WAITING_DOWNLOAD = "is_download_waiting";
  private static final String KEY_IS_NAVIGATION_BACK = "is_navi_back";
  private static final String KEY_SEND_GIFT_FROM_PROFILE = "is_send_gift_from_profile";
  private static final String KEY_TAKE = "take";
  private static final String KEY_IS_VOICE_CALL_WAITING = "is_video_call_waiting";
  private static final String KEY_IS_VIDEO_CALL_WAITING = "is_voice_call_waiting";
  // Request loader
  private static final int LOADER_ID_HISTORY = 1;
  private static final int LOADER_ID_MARK_AS_READ = 2;
  private static final int LOADER_ID_ADD_BLOCK_USER = 3;
  private static final int LOADER_ID_REMOVE_BLOCK_USER = 4;
  private static final int LOADER_ID_REPORT_USER = 5;

  private static final int LOADER_ID_ADD_TO_FAVORITES = 6;
  private static final int LOADER_ID_GET_BASE_USER_INFO = 7;
  private static final int LOADER_ID_REMOVE_FROM_FAVORITES = 8;
  private static final int LOADER_ID_CHECK_CALL_VIDEO = 9;
  private static final int LOADER_ID_CHECK_CALL_VOICE = 10;

  private static final int LOADER_ID_GET_VIDEO_URL = 11;
  private static final int LOADER_ID_CHECK_UNLOCK = 12;
  private static final int LOADER_ID_UNLOCK = 13;
  private static final int LOADER_ID_BASIC_USER_INFO_CALL = 14;
  private static final int LOADER_ID_CHECK_NEW_MESSAGE = 15;
  private static final int LOADER_ID_GET_POINT = 19;
  private static final int LOADER_ID_TEMPLATE = 18;   //Add one easy way to insert template to chat. #9706

  private static final int STEP_NUMBER_BACK_TO_PROFILE = 2;
  // Request code
  private static final int REQUEST_PHOTO = 1000;
  private static final int REQUEST_VIDEO = 3000;
  private static final int REQUEST_IMAGE_ID = 4000;
  private static final int REQUEST_GIFT = 5000;
  private static final int STOP_TYING_TIME = 10 * 1000;
  // Configure
  private final int RECORD_DELAY_TIME = 500;
  private final long DELAY_REQUEST_MORE_HISTORY = 5 * 1000;
  // Listener
  private PullToRefreshListView mPullToRefreshListView;
  private Runnable mRunnableRecord;
  private Handler mHandlerRecord;
  private LocalBroadcastManager mLocalBroadcastManager;
  private BroadcastReceiver mStickerReceiver;
  private BroadcastReceiver mChatReceiver;
  private BroadcastReceiver mRecordReceiver;
  private ScreenReceiver mScreenReceiver;
  private BroadcastReceiver mMessageStatusReceiver;
  private ChatAdapter mChatAdapter;
  // Activity
  private MainActivity mMainActivity;
  private Context mContext;

  /**
   * Update status for chat message
   */
  IStatusChatChanged updateStatusChat = new IStatusChatChanged() {

    @Override
    public void update(final MessageInDB msgInDB) {
      final ChatMessage chatMsg = msgInDB.makeChatMessage();
      Objects.requireNonNull(ChatFragment.this.getActivity()).runOnUiThread(() -> {

        LogUtils.i(TAG, "IStatusChatChanged ---> " + msgInDB.getStatus());

        if (msgInDB.getStatus() == StatusConstant.STATUS_DELETE) {
          ChatFragment.this.mChatAdapter.removeMessage(msgInDB.getChatClientId());
        } else {
          if (msgInDB.getStatus() == StatusConstant.STATUS_ERROR
              || msgInDB.getStatus() == StatusConstant.STATUS_SUCCESS
              || msgInDB.getStatus() == StatusConstant.STATUS_UNKNOW) {
            dismissUploading();
          }
          ChatFragment.this.mChatAdapter.updateMessageStatus(chatMsg, msgInDB.getStatus());
        }
      });

    }

    @Override
    public void create(final MessageInDB msgInDB) {
      Objects.requireNonNull(ChatFragment.this.getActivity()).runOnUiThread(() -> ChatFragment.this.mChatAdapter.createMessageStatus(msgInDB));

//      ChatFragment.this.getActivity().runOnUiThread(new Runnable() {
//
//        @Override
//        public void run() {
//          ChatFragment.this.mChatAdapter.createMessageStatus(msgInDB);
//        }
//      });

    }

    @Override
    public void resendFile(final MessageInDB msgInDB) {
      ChatFragment.this.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          String filePath = msgInDB.getFilePath();
          if (TextUtils.isEmpty(filePath)) {
            return;
          }
          ChatService chatService = mMainActivity.getChatService();
          if (chatService == null) {
            return;
          }
          String fileType = msgInDB.getFileType();
          File file = new File(filePath);
          Date date = Utility.getDateTimeInGMT();

          // Remove message from database
          int position = mChatAdapter.getMsgLocation(msgInDB
              .getChatClientId());
          ChatMessage chatMessage = mChatAdapter.getItem(position);
          StatusController.getInstance(mContext).deleteMsg(
              chatMessage);

          if (fileType.equals(ChatManager.PHOTO)) {
            chatService.sendPhoto(msgInDB.getChatClientId(), date,
                msgInDB.getTo(), file.getName(), file);
          } else if (fileType.equals(ChatManager.AUDIO)) {
            long audioTime = msgInDB.getAudioTime();
            int audioTimeInt = parseLongTimeToInt(audioTime);
            chatService.sendAudio(msgInDB.getChatClientId(), date, msgInDB.getTo(),
                String.valueOf(audioTimeInt), file, audioTime);
          } else if (fileType.equals(ChatManager.VIDEO)) {
            chatService.sendVideo(msgInDB.getChatClientId(), date,
                msgInDB.getTo(), file.getName(), file);
          }
        }
      });

    }
  };
  private ClipboardManager mClipboardManager;
  // Navigate feature
  private ProgressDialog progressDialog;
  private Dialog mDialogChatWithUserMore;
  private Dialog mErrorDialog;
  private AlertDialog mConfirmDialog;
  private android.app.AlertDialog mAlertDialog;
  private android.app.AlertDialog mLimitTimeDialog;
  private boolean isNavigationBack;
  private String mScrollToMsgId;
  // Chat feature
  private EditText mEdtContent;
  private ListView mListChat;
  private TableRow mTableRowChat;
  private TableRow mTableRowRecord;
  private ImageView mImgRecycle;
  private ImageView mImgChatVoiceCall;
  private TextView mTxtChatSend;
  private View mChatBoxSpace;
  private ChatUser mMe = new ChatUser();
  private ChatUser mFriend = new ChatUser();
  private boolean isSendAudio = false;
  private int take = 0;
  private int mNumMessageReceived = 0;
  private boolean mNewAddFavoriteRequest = false;
  // Call feature
  private CallUserInfo callUserInfo;
  private boolean isVoiceCallWaiting = false;
  private boolean isVideoCallWaiting = false;
  // Record feature
  private TextView mTvRecorderTime;
  private TextView mTvRecorderMessage;
  private Button mBtnRecord;
  private View mViewFreezedLayer;
  private RelativeLayout mRelativeLayoutRecorder;
  private boolean isTouchRecordAllowed = true;
  private boolean isDragToDelete = false;
  private String mFilePath = null;
  private RecMicToMp3Tamtd mRecMicToMp3;
  private AudioRecordViewCustom audioRecordVisualizer;
  private MediaPlayer mMediaPlayer = null;
  // Loading feature
  private DispatchTouchLayout mLayoutMain;
  private LinearLayout mLayoutData;
  private FrameLayout mLayoutLoading;
  private TextView mtxtStatus;
  // Sticker feature
  private ImageView mImgAddReturn;
  private ImageView mImgEmojiReturn;
  private ImageView mImgKeybroad;
  private ImageView ivAdd;
  private ImageView mbtnAddMedia;
  private View viewPanel;
  private PopupWindow popupWindow;
  private View mPannelMediaFile;
  private View mPanelMedia;
  private PreviewStickerView previewStickerView;
  private EmojiPanel mEmojiPanel;
  private ChatControlPanel mPanelMediaFile;
  private int defaultHeight;
  private int mKeyboardHeight = 0;
  private Handler mKeyboardHandler;
  private boolean isShowPanelNext = false;
  private boolean isShowKbNext = false;
  private boolean isShowKb = false;
  private boolean isShowEmoji = false;
  public InputFilter[] fArray = new InputFilter[1];
  //set limit text msg
  boolean limited = false;
  private List<ChatMessage> listConversationBackup = new ArrayList<>();
  private FrameLayout layoutUploading;
  private ProgressBar progressUploading;
  private TextView txtProgressUploading;
  // Download feature
  private ChatUploadManager mChatUploadManager;
  private ChatDownloadManager mChatDownloadManager;
  private IDownloadManager mIDownloadManager;
  private long mDownloadId;
  private boolean mWaitingDownload = false;
  private List<Long> mVideoFileReceivedList;
  private HashMap<String, Long> mAudioFileReceivedList;
  private List<Long> mPhotoFileReceivedList;
  private int checkUnlockType = UnlockType.UNKNOW;
  private String checkUnlockID = "";
  private boolean isSendGiftFromProfile = false;
  private int mCurrentCallType = Constants.CALL_TYPE_VOICE;
  private SCREEN_STATUS mScreenStatus = SCREEN_STATUS.PRESENT;

  private Handler historyRequestHandle = new Handler();
  private Runnable requestMoreHistory = new Runnable() {
    @Override
    public void run() {
      String token = UserPreferences.getInstance().getToken();
      GetNewChatMessageRequest request = new GetNewChatMessageRequest(
          token, mFriend.getId());
      restartRequestServer(LOADER_ID_CHECK_NEW_MESSAGE, request);
    }
  };
  private boolean mIsTyping = false;
  private Handler mTypingHandler = new Handler();
  private Runnable mSendStopTypingRunnable = new Runnable() {
    @Override
    public void run() {
      mIsTyping = false;
      sendTypingMessage(mMe.getId(), mFriend.getId(),
          ChatManager.STOP_TYPING);
    }
  };
  private ActionMode.Callback textActionMode = new ActionMode.Callback() {
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case android.R.id.cut:
          int startCut = mEdtContent.getSelectionStart();
          int endCut = mEdtContent.getSelectionEnd();
          processClickCopyText(startCut, endCut);
          mEdtContent.getText().delete(startCut, endCut);
          mEdtContent.setSelection(startCut);
          mode.finish();
          return true;
        case android.R.id.copy:
          int startCopy = mEdtContent.getSelectionStart();
          int endCopy = mEdtContent.getSelectionEnd();
          processClickCopyText(startCopy, endCopy);
          mode.finish();
          return true;
        case android.R.id.paste:
          return true;
        default:
          break;
      }
      return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
  };
  private OnRefreshListener2<ListView> onChatListRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      if (mChatAdapter != null
          && mChatAdapter.getListChatMessage().size() > 0) {
        mListChat
            .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
        ChatMessage chatMessage = mChatAdapter.getListChatMessage()
            .get(0);
        String token = UserPreferences.getInstance().getToken();

        String time = chatMessage.getTimeStamp();
        LogUtils.i(TAG, "time request to load more history Locale="
            + time);
        time = Utility.convertLocaleToGMT(time);
        LogUtils.i(TAG, "time request to load more history GMT=" + time);
        HistoryRequest historyRequest = new HistoryRequest(token,
            mFriend.getId(), time, take);
        requestHistory(historyRequest);
      } else {
        mPullToRefreshListView.onRefreshComplete();
      }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }
  };
  private OnItemChatClickListener mOnItemChatClickListener = new OnItemChatClickListener() {
    @Override
    public void onItemUserProfileClick() {
      String friendId = mFriend.getId();
      UserPreferences.getInstance().removeCurentFriendChat();
      replaceFragment(MyProfileFragment.newInstance(friendId),
          MyProfileFragment.TAG_FRAGMENT_USER_PROFILE);
    }

    @Override
    public void onItemMyprofileClick() {
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.removeCurentFriendChat();
      String userId = userPreferences.getUserId();
      replaceFragment(MyProfileFragment.newInstance(userId, true));
    }
  };
  private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
    private boolean isKeyboardVisible = false;

    @Override
    public void onGlobalLayout() {
      Rect r = new Rect();

      mLayoutMain.getWindowVisibleDisplayFrame(r);
      int screenHeight = mLayoutMain.getRootView().getHeight();
      int heightDifference = screenHeight - r.bottom;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        heightDifference -= Utility.getSoftNavigationBarSize(getActivity());
      }
      if (heightDifference > 0) {
        if (heightDifference != mKeyboardHeight) {
          mKeyboardHeight = heightDifference;
          setHeightChatBoxSpace(mKeyboardHeight);
        }
        if (!isKeyboardVisible) {
          // show keyboard
          isKeyboardVisible = true;
          isShowKb = true;
          hidePanel();
          mChatBoxSpace.setVisibility(View.GONE);
          }
        gotoEndListChat();
        isShowKbNext = false;
      } else {
        if (mKeyboardHeight < defaultHeight) {
          mKeyboardHeight = defaultHeight;
          setHeightChatBoxSpace(defaultHeight);
        }
        // hide keyboard
        isKeyboardVisible = false;
        isShowKb = false;
        if (isShowPanelNext) {
          showPanel();
          isShowPanelNext = false;
        } else {
          if (!isShowKbNext && !isShowEmoji) {
            // hide keyboard and mediaPanel
            mChatBoxSpace.setVisibility(View.GONE);
            getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
          }
        }
      }
      // hideChatMoreOptions();

    }
  };
  private boolean isShowDialogResend = false;

  public boolean isShowDialogResend() {
    return isShowDialogResend;
  }

  public void setIsShowDialogResend(boolean isShowDialogResend) {
    this.isShowDialogResend = isShowDialogResend;
  }
  private CustomUploadService mUploadService;
  private ProgressDialog mProgressDialogDownload;
  private ChatMoreLayout mChatMoreLayout;
  private PopupWindow mPopupChatMoreOptions;
  private IOnControlClicked mOnControlClicked = new IOnControlClicked() {
    @Override
    public void onChoosePhoto() {
      if (hasShowDialogValidate())
        return;
      mMainActivity.setUnbindChatOnStop(true);

      new Gallery.Builder()
          .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
          .multiChoice(false)
          .fixAspectRatio(false)
          .cropOutput(true)
          .build()
          .start(ChatFragment.this);

//            MediaOptions.Builder builder = new MediaOptions.Builder();
//            MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(false).selectPhoto().build();
//            MediaPickerActivity.open(ChatFragment.this, REQUEST_PHOTO, options);
    }

    @Override
    public void onTakePhoto() {
      if (hasShowDialogValidate())
        return;
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
      mMainActivity.setUnbindChatOnStop(true);

      new Camera.Builder()
          .cropOutput(true)
          .fixAspectRatio(false)
          .videoMode(false)
          .lock(true)
          .build()
          .start(ChatFragment.this);

//            MediaOptions.Builder builder = new MediaOptions.Builder();
//            MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(false).selectPhoto().build();
//            TakeMediaActivity.open(ChatFragment.this, REQUEST_PHOTO, options);
    }

    @Override
    public void onChooseVideo() {
      if (hasShowDialogValidate())
        return;
//            mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//            mMainActivity.setUnbindChatOnStop(true);
//            goToTakeVideo();
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
      mMainActivity.setUnbindChatOnStop(true);
      MediaPickerErrorDialog errorDialog = MediaPickerErrorDialog
          .newInstance(
              getActivity().getResources().getString(R.string.picker_video_duration_max_fish));
      errorDialog.show(getChildFragmentManager(), null);
      errorDialog.setOnOKClickListener(new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

          new Gallery.Builder()
              .viewType(Gallery.VIEW_TYPE_VIDEOS_ONLY)
              .multiChoice(false)
              .build()
              .start(ChatFragment.this);

//                    MediaOptions.Builder builder = new MediaOptions.Builder();
//                    MediaOptions options = builder.setMaxVideoDuration(Config.TIME_LIMIT_VIDEO * 1000).setShowWarningBeforeRecordVideo(true).selectVideo().build();
//                    MediaPickerActivity.open(ChatFragment.this, REQUEST_VIDEO, options);
        }
      });
    }

    @Override
    public void onPreviousPhoto() {
      if (hasShowNetworkNotConnected(true))
        return;
      hidePanel();
      hideChatMoreOptions();
      hideKeyboard();
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
      mMainActivity.setUnbindChatOnStop(false);
      ProfilePictureData profilePictureData = new ProfilePictureData();
      profilePictureData.setGender(mFriend.getGender());
      profilePictureData.setUserId(mFriend.getId());
      profilePictureData.setAvata(mFriend.getAvatar());
      Intent intent = new Intent(mContext, GalleryActivity.class);
      intent.putExtras(profilePictureData
          .getBundleFromDataForPreviousImage());
      startActivityForResult(intent, REQUEST_IMAGE_ID);
    }

    @Override
    public void onTakeVideo() {
      if (hasShowDialogValidate())
        return;
//            mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//            mMainActivity.setUnbindChatOnStop(true);
//            goToTakeVideo();
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
      mMainActivity.setUnbindChatOnStop(true);

      int max = Config.TIME_LIMIT_VIDEO;
      MediaPickerErrorDialog dialog = MediaPickerErrorDialog
          .newInstance(MessageUtils.getWarningMessageVideoDuration(getActivity(), max));
      dialog.setOnOKClickListener(new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

          new Camera.Builder()
              .videoMode(true)
              .maxDuration(Config.TIME_LIMIT_VIDEO * 1000)
              .lock(true)
              .build()
              .start(ChatFragment.this);
//                    TakeMediaActivity.open(ChatFragment.this, REQUEST_VIDEO, options);
        }
      });
      dialog.show(getChildFragmentManager(), null);
    }

    @Override
    public void onRecord() {
      if (hasShowDialogValidate())
        return;
      hidePanel();
      hideChatMoreOptions();
      hideKeyboard();
      setChatVisiable();
    }

    @Override
    public void onTemplate() {
      hidePanel();
      hideChatMoreOptions();
      hideKeyboard();
      mNavigationManager
          .addPage(TemplateFragment.newInstance(TemplateFragment.STYLE_CHAT, mFriend.getId()),
              false);
    }

  };
  private OnChatMoreListener chatMoreListener = new OnChatMoreListener() {

    @Override
    public void onVoiceCall() {
      if (hasShowDialogValidate())
        return;

      hideChatMoreOptions();
      executeVoiceCall();
    }

    @Override
    public void onVideoCall() {
      if (hasShowDialogValidate())
        return;

      hideChatMoreOptions();

      if (Utility.isBlockedWithUser(mContext, mFriend.getId())) {
        exitMeWhenBlocked();
        return;
      }

      CallUserInfo userInfo = new CallUserInfo(mFriend.getName(),
          mFriend.getId(), mFriend.getAvatar(), mFriend.getGender());
      callUserInfo = userInfo;
      mCurrentCallType = Constants.CALL_TYPE_VIDEO;
      Utility.showDialogAskingVideoCall(mMainActivity, userInfo,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              restartRequestBasicUserInfo();
            }
          });
    }

    @Override
    public void onSendGift() {
      hideChatMoreOptions();
      mMainActivity.setUnbindChatOnStop(true);

      GiftCategories categories = new GiftCategories("get_all_gift", 0,
          getResources().getString(R.string.give_gift_all_title), 1);
      ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend
          .newInstance(mFriend.getId(), mFriend.getName(),
              categories, false, true);
      chooseGiftToSend.setTargetFragment(getRootParentFragment(ChatFragment.this), REQUEST_GIFT);
      replaceFragment(chooseGiftToSend);
    }

    @Override
    public void onReport() {
      hideChatMoreOptions();
      executeReportUser();
    }

    @Override
    public void onFavorite() {
      hideChatMoreOptions();
      if (FavouritedPrefers.getInstance().hasContainFav(mFriend.getId())) {
        executeRemoveFromFavorites();
      } else {
        executeAddToFavorites();
      }
    }

    @Override
    public void onBlock() {
      hideChatMoreOptions();
      executeBlockUser();
    }

    @Override
    public void onShown() {
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          getSlidingMenu().setSlidingEnabled(true);
        }
      }, 200);
    }

    @Override
    public void onAlertOnline() {

    }
  };
  private ImageGetter emojiGetter = new ImageGetter() {

    @Override
    public Drawable getDrawable(String source) {
      int id = getResources().getIdentifier(source, "drawable",
          mContext.getPackageName());
      Drawable emoji = getResources().getDrawable(id);
      int w = (int) (emoji.getIntrinsicWidth() * 0.8);
      int h = (int) (emoji.getIntrinsicHeight() * 0.8);
      emoji.setBounds(0, 0, w, h);
      return emoji;
    }
  };
  private TextWatcher textWatcher = new TextWatcher() {
    private int lengthTextChang = 0;

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
      String userId = mFriend.getId();
      String msg = s.toString();

      if (msg.replace("\u3000", " ").trim().length() == 0) {
        mImgChatVoiceCall.setVisibility(View.VISIBLE);
        mTxtChatSend.setVisibility(View.GONE);
      } else {
        mImgChatVoiceCall.setVisibility(View.GONE);
        mTxtChatSend.setVisibility(View.VISIBLE);
      }

      if (lengthTextChang > 2) {
        String textChange = s.subSequence(start, start + count).toString();
        if (EmojiUtils.hasEmojiCode(textChange)) {
          processUpdateEmojiTextChange(textChange, start, start + count);
        }
      }

      if (java.text.Normalizer.normalize(msg, Normalizer.Form.NFKC).length() == 2000) {
        limited = true;
        fArray[0] = new InputFilter.LengthFilter(msg.length());
        mEdtContent.setFilters(fArray);
      } else {
        if (limited) {
          limited = false;
          fArray[0] = new InputFilter.LengthFilter(Integer.MAX_VALUE);
          mEdtContent.setFilters(fArray);
        }
      }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      hideChatMoreOptions();
      lengthTextChang = after;
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() > 0) {
        if (!mIsTyping) {
          mIsTyping = true;
          sendTypingMessage(mMe.getId(), mFriend.getId(),
              ChatManager.START_TYPING);
        }
        mTypingHandler.removeCallbacks(mSendStopTypingRunnable);
        mTypingHandler.postDelayed(mSendStopTypingRunnable,
            STOP_TYING_TIME);
      } else {
        mIsTyping = false;
        sendTypingMessage(mMe.getId(), mFriend.getId(),
            ChatManager.STOP_TYPING);
      }


    }
  };
  private IOnEmojiSelected mOnEmojiSelected = new IOnEmojiSelected() {
    @Override
    public void onEmojiSelected(int emoji, String content) {
      String emojiHtml = EmojiUtils.convertCodeToEmoji(content);
      int startSelection = 0;
      int endSelection = 0;
      if (mEdtContent.getSelectionStart() >= 0) {
        startSelection = mEdtContent.getSelectionStart();
      }
      if (mEdtContent.getSelectionEnd() >= 0) {
        endSelection = mEdtContent.getSelectionEnd();
      }

      mEdtContent.getText().replace(startSelection, endSelection,
          Html.fromHtml(emojiHtml, emojiGetter, null));
      mEdtContent.setSelection(mEdtContent.getSelectionEnd());
    }

    @Override
    public void onStickerSelected(Uri sticker, String content) {
      previewStickerView.setVisibility(View.VISIBLE);
      previewStickerView.setContent(sticker, content);
    }

    @Override
    public void onStickerPanelHide() {
      if (previewStickerView != null) {
        previewStickerView.setVisibility(View.GONE);
      }
    }
  };

  public static ChatFragment newInstance(String friendId, String friendAva,
      String friendName, int friendGender, boolean isNavigationBack) {
    ChatFragment chatFragment = new ChatFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_PARTNER_ID, friendId);
    bundle.putString(KEY_PARTNER_AVATA, friendAva);
    bundle.putString(KEY_PARTNER_NAME, friendName);
    bundle.putInt(KEY_PARTNER_GENDER, friendGender);
    bundle.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    chatFragment.setArguments(bundle);
    return chatFragment;
  }

  public static ChatFragment newInstance(String friendId, String friendAva,
      String friendName, int friendGender, boolean isVoiceCallWaiting,
      boolean isVideoCallWaiting, boolean isNavigationBack) {
    ChatFragment chatFragment = new ChatFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_PARTNER_ID, friendId);
    bundle.putString(KEY_PARTNER_AVATA, friendAva);
    bundle.putString(KEY_PARTNER_NAME, friendName);
    bundle.putInt(KEY_PARTNER_GENDER, friendGender);
    bundle.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    bundle.putBoolean(KEY_IS_VOICE_CALL_WAITING, isVoiceCallWaiting);
    bundle.putBoolean(KEY_IS_VIDEO_CALL_WAITING, isVideoCallWaiting);
    chatFragment.setArguments(bundle);
    return chatFragment;
  }

  public static ChatFragment newInstance(String friendId,
      boolean isNavigationBack) {
    ChatFragment chatFragment = new ChatFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_PARTNER_ID, friendId);
    bundle.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    chatFragment.setArguments(bundle);
    return chatFragment;
  }

  /**
   * Called when send gift
   */
  public static ChatFragment newInstance(String userIdToSend,
      boolean isNavigationBack, boolean isSendGiftFromProfile,
      String giftId, String value, String mCurrentUserId, String mReceiveUserId, String messageId) {
    ChatFragment chatFragment = new ChatFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_PARTNER_ID, userIdToSend);
    bundle.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    bundle.putBoolean(KEY_SEND_GIFT_FROM_PROFILE, isSendGiftFromProfile);

    bundle.putString(ChooseGiftToSend.EXTRA_GIFT_SENT_ID, giftId);
    bundle.putString(ChooseGiftToSend.EXTRA_GIFT_MSG_SENT, value);
    bundle.putString(ChooseGiftToSend.EXTRA_GIFT_OWNER,
        mCurrentUserId);//Can get from UserPreferences.getInstance()
    bundle.putString(ChooseGiftToSend.EXTRA_GIFT_RECEIVER,
        mReceiveUserId);// Can get from mFriend.getId() Object
    bundle.putString(ChooseGiftToSend.EXTRA_GIFT_MSG_ID, messageId);

    chatFragment.setArguments(bundle);
    return chatFragment;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    LogUtils.i(TAG, "onCreate()...is called..." + com.application.util.Utils.dumpBundle(bundle));

    if (bundle != null) {
      // Get friend information
      mFriend.setId(bundle.getString(KEY_PARTNER_ID));
      mFriend.setAvatar(bundle.getString(KEY_PARTNER_AVATA));
      mFriend.setName(bundle.getString(KEY_PARTNER_NAME));
      mFriend.setGender(bundle.getInt(KEY_PARTNER_GENDER));

      isSendGiftFromProfile = bundle
          .getBoolean(KEY_SEND_GIFT_FROM_PROFILE);

      // Get call information
      isVoiceCallWaiting = bundle.getBoolean(KEY_IS_VOICE_CALL_WAITING);
      isVideoCallWaiting = bundle.getBoolean(KEY_IS_VIDEO_CALL_WAITING);

      // Get navigation data
      isNavigationBack = bundle.getBoolean(KEY_IS_NAVIGATION_BACK);

      // Get number of message received
      mNumMessageReceived = bundle.getInt(KEY_RECEIVED_MESSAGE_NUM);

      // Get download status
      mDownloadId = bundle.getLong(KEY_DOWNLOAD_ID);
      mWaitingDownload = bundle.getBoolean(KEY_IS_WAITING_DOWNLOAD);

      // Get default view paging
      if (bundle.containsKey(KEY_TAKE) && bundle.getInt(KEY_TAKE) > 0) {
        take = bundle.getInt(KEY_TAKE);
      } else {
        take = 24;
      }
    } else {
      Bundle arguments = getArguments();
      if (arguments != null) {
        // Get friend information
        mFriend.setId(arguments.getString(KEY_PARTNER_ID));
        mFriend.setName(arguments.getString(KEY_PARTNER_NAME));
        mFriend.setAvatar(arguments.getString(KEY_PARTNER_AVATA));
        mFriend.setGender(arguments.getInt(KEY_PARTNER_GENDER));

        isSendGiftFromProfile = arguments
            .getBoolean(KEY_SEND_GIFT_FROM_PROFILE);

        // Get call information
        isVoiceCallWaiting = arguments
            .getBoolean(KEY_IS_VOICE_CALL_WAITING);
        isVideoCallWaiting = arguments
            .getBoolean(KEY_IS_VIDEO_CALL_WAITING);

        // Get navigation data
        isNavigationBack = arguments.getBoolean(KEY_IS_NAVIGATION_BACK);
      }
    }

    // Get my information
    UserPreferences userPreferences = UserPreferences.getInstance();
    mMe.setId(userPreferences.getUserId());
    mMe.setName(userPreferences.getUserName());
    mMe.setAvatar(userPreferences.getAvaId());
    mMe.setGender(userPreferences.getGender());
    LogUtils.i(TAG, "My information:\n" + mMe.toString());

    // Request server clear unread message of this friend
    ((MainActivity) getActivity()).clearUnreadMessage(mFriend.getId());

    // Register chat service
    mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    mChatUploadManager = new ChatUploadPrefers();
    mChatDownloadManager = new ChatDownloadPrefers();
    mIDownloadManager = AndGDownloadManager.getInstance(new Handler(), mAppContext);
    StatusController.getInstance(mContext).addStatusChangedListener(updateStatusChat);


    // Initial basic view
    initialAudio();
    initEmojiView();

    // Reset auto scroll when this screen first create
    userPreferences.removeCurrentMsgChatId();

    mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    LogUtils.i(TAG, "onCreateView()...is called...");
    View view = inflater.inflate(R.layout.fragment_chat, container, false);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getSlidingMenu().setSlidingEnabled(true);
    // Request sticker each time user visit to chat screen
    requestSticker();

    // Request banned word each time user visit to chat screen
    requestDirtyWord();

    // Initial view when view created
    initView(view);
    switchToLoading();

    // Enable sliding menu action
    setEnableSlidingMenu(true);

    registerScreenReceiver();
    LogUtils.i(TAG,
        "onViewCreated()...is called..." + com.application.util.Utils.dumpBundle(savedInstanceState)
            + "|getArguments=" + com.application.util.Utils.dumpBundle(getArguments()));
  }

  private void registerScreenReceiver() {
    // Register screen status
    mScreenReceiver = new ScreenReceiver(new ScreenReceiver.ScreenListener() {
      @Override
      public void onScreenPresent() {
        LogUtils.w(TAG, "onScreenPresent");
        mScreenStatus = SCREEN_STATUS.PRESENT;
        checkReadMessage();
      }

      @Override
      public void onScreenOn() {
        LogUtils.w(TAG, "onScreenOn");
        mScreenStatus = SCREEN_STATUS.ON;
      }

      @Override
      public void onScreenOff() {
        LogUtils.w(TAG, "onScreenOff");
        mScreenStatus = SCREEN_STATUS.OFF;
      }
    });

    IntentFilter screenFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
    screenFilter.addAction(Intent.ACTION_USER_PRESENT);
    getActivity().registerReceiver(mScreenReceiver, screenFilter);
  }

  @Override
  public void onStart() {
    super.onStart();

    LogUtils
        .i(TAG, "onStart().getArguments=" + com.application.util.Utils.dumpBundle(getArguments()));

    ChatService chatService = mMainActivity.getChatService();
    if (chatService != null) {
      ChatManager chatManager = chatService.getChatManager();
      if (chatManager != null) {
        chatManager.sendAuthenticationMessage();
      }
    }


    registerReceiveChat();
    registerStickerReload();
    registerRecordReceiver();
    UserPreferences.getInstance().saveCurentFriendChat(mFriend.getId());

    // If this screen out of fragment. It must be clear and reload
    if (isSaveInstanceCalled && mMainActivity.getUnbindOnStop()) {
      requestNewestHistory();
      switchToLoading();
    }

    // need unbindOnStop
    mMainActivity.setUnbindChatOnStop(true);
    bindDownloadProgress();
  }

  @Override
  public void onStop() {
    super.onStop();
    LogUtils.i(TAG, "onStop()...is called...");

    mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
    if (mKeyboardHandler != null) {
      mKeyboardHandler.removeCallbacksAndMessages(null);
    }
    // tungdx: remove all message pending
    mTypingHandler.removeCallbacksAndMessages(null);
    if (mIsTyping) {
      mTypingHandler.post(mSendStopTypingRunnable);
    }
    // THANGPQ http://10.64.100.201/issues/14055: save old list chat message
    listConversationBackup.clear();
    listConversationBackup.addAll(mChatAdapter.getListChatMessage());
    Collections.reverse(listConversationBackup);
    //end

    hideKeyboard();
    // // Cancel record audio
    // cancelRecordAudio();
    //markAsRead(mFriend.getId());

    unregisterStickerReload();
    unregisterRecordReceiver();
    hidePanel();
    unbindDownloadProgress();
    hideChatMoreOptions();
    int selectedItem = mListChat.getFirstVisiblePosition();
    ChatMessage chatMsg = mChatAdapter.getItem(selectedItem);
    if (chatMsg != null) {
      mScrollToMsgId = chatMsg.getMessageId();
    } else {
      mScrollToMsgId = "";
    }
    UserPreferences.getInstance().saveCurrentMsgChatId(mScrollToMsgId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    LogUtils.i(TAG, "onDestroy()...is called...");
    stopPlayAudio();
    mVideoFileReceivedList = null;
    mAudioFileReceivedList = null;
    mPhotoFileReceivedList = null;
    mIDownloadManager.terminate();
    if (mUploadService != null) {
      mUploadService.removeUploadCustomListener(this);
    }
    if (mEdtContent != null) {
      mEdtContent.removeTextChangedListener(textWatcher);
    }
    mTypingHandler.removeCallbacksAndMessages(null);
    mTypingHandler = null;
    StatusController.getInstance(mContext).removeStatusChangedListener(updateStatusChat);
    unregisterChat();
  }

  /**
   * Initial Emoji panel view
   */
  private void initEmojiView() {
    defaultHeight = (int) getResources().getDimension(R.dimen.keyboard_height);
    viewPanel = View.inflate(mMainActivity,
        R.layout.panel_emoji_soft_key_board, null);
    popupWindow = new PopupWindow(viewPanel, LayoutParams.MATCH_PARENT,
        mKeyboardHeight, false);
    popupWindow.setAnimationStyle(R.style.PanelAnimation);
  }

  @Override
  public void onResume() {
      super.onResume();
    LogUtils.i(TAG, "onResume()...is called...");
    ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
    String friendID = mFriend.getId();
    String tempMsg = chatMessagePreference.getMessage(friendID);

    if (!TextUtils.isEmpty(tempMsg)) {
      chatMessagePreference.removeMessage(friendID);
      mEdtContent.setText(tempMsg);
      mImgChatVoiceCall.setVisibility(View.GONE);
      mTxtChatSend.setVisibility(View.VISIBLE);
    } else {
      mImgChatVoiceCall.setVisibility(View.VISIBLE);
      mTxtChatSend.setVisibility(View.GONE);
    }
    setUploadListener();
    mChatAdapter.notifyDataSetChanged();
    requestMoreMessage(false);
    markAsRead(mFriend.getId());
    checkReadMessage();

    // #13798 fix user name not showing when send gift error
    mActionBar.setTextCenterTitle(mFriend.getName());
  }

  /**
   * Notify data service to download list sticker
   */
  private void requestSticker() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    DataFetcherService.startLoadDirtyWord(mContext);
//    Activity activity = getActivity();
//    if (activity != null) {
//      DataFetcherService.startLoadDirtyWord(activity);
//    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    if (getActivity() instanceof MainActivity) {
      ((MainActivity) getActivity()).clearUnreadMessage(mFriend.getId());
    }
  }

  /**
   * Mark all message as read
   */
  private void markAsRead(String userIdToSendMessage) {
    String[] userId = new String[]{userIdToSendMessage};
    String token = UserPreferences.getInstance().getToken();
    MarkReadsRequest markReadsRequest = new MarkReadsRequest(token, userId);
    restartRequestServer(LOADER_ID_MARK_AS_READ, markReadsRequest);
  }

  private void registerReceiveChat() {
    mChatReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG, "onReceive().mChatReceiver is calling...");
        if (getActivity() == null) {
          return;
        }

        // Get action
        String action = intent.getAction();
        if (action.equals(ChatManager.ACTION_MESSAGE_UPDATE_FILE)) {
          Bundle bundle = intent
              .getBundleExtra(ChatManager.EXTRA_BUNDLE);
          if (bundle != null && mChatAdapter != null) {
            String msgId = bundle
                .getString(StatusConstant.ARG_CHAT_MSG_ID);
            String filePath = bundle
                .getString(StatusConstant.ARG_FILE_PATH);
            mChatAdapter.updateMediaInfo(msgId, filePath);
          }
        } else {
          // Get message object data
          Serializable objectSent = intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          if (objectSent instanceof String) {
            // handle download, upload file error
            if (action.equals(ChatManager.ACTION_SEND_FILE_ERROR)) {
              String msg = intent
                  .getStringExtra(ChatManager.EXTRA_DATA);
              handleActionSentFileError(msg);
            } else if (action
                .equals(ChatManager.ACTION_DOWNLOAD_FILE_ERROR)) {
              String msg = intent
                  .getStringExtra(ChatManager.EXTRA_DATA);
              handleActionDownloadFileError(msg);
            }
          } else {
            MessageClient compat = (MessageClient) objectSent;
            // action not need check userCome (because always come
            // from
            // server)
            if (action.equals(ChatManager.ACTION_MESSAGE_STATUS)) {
              MessageStatus messageStatus = (MessageStatus) compat;
              handleMessageStatusReceived(messageStatus);
              return;
            } else if (action
                .equals(ChatManager.ACTION_AUTHENTICATION)) {
              AuthenticationMessage authentication = (AuthenticationMessage) compat;
              handleAuthenticationMessage(authentication);
              return;
            } else if (action
                .equals(ChatManager.ACTION_LOCAL_MESSAGE_CALL)) {
              handleLocalCallMessageReceived(compat);
            }
            // below always check from
            Message message = compat.getMessage();
            String from = StringUtils.nullToEmpty(message.from);
            String to = StringUtils.nullToEmpty(message.to);
            // must equals match user
            LogUtils.e(TAG, "===>from=" + from + "|to=" + to + "|mFriend.getId()=" + mFriend.getId()
                + "|isMyMessage(message)=" + isMyMessage(message));

            if ((!from.equals(mFriend.getId())) && !(
                from.equals(UserPreferences.getInstance().getUserId())
                    && to.equals(mFriend.getId()))) {
              return;
            }

            if (action.equals(ChatManager.ACTION_MESSAGE)) {
              handleChatMessageReceived(compat);
            }
//            else if (action
//                .equals(ChatManager.ACTION_AUTHENTICATION)) {
//              AuthenticationMessage authentication = (AuthenticationMessage) compat;
//              handleAuthenticationMessage(authentication);
//            }
            else if (action
                .equals(ChatManager.ACTION_MESSAGE_WINK)) {
              handleWinkMessageReceived(compat);
            } else if (action
                .endsWith(ChatManager.ACTION_MESSAGE_FILE)) {
              handleFileMessageReceived(compat);
            } else if (action
                .equals(ChatManager.ACTION_MESSAGE_TYPING)) {
              handleTypingMessageReceived(compat);
            } else if (action
                .equals(ChatManager.ACTION_MESSAGE_GIFT)) {
              handleGiftMessageReceived(compat);
            } else if (action
                .equals(ChatManager.ACTION_MESSAGE_LOCATION)) {
              handleLocationMessageReceived(compat);
            } else if (action
                .equals(ChatManager.ACTION_MESSAGE_STICKER)) {
              handleStickerMessageReceived(compat);
            }
//            else if (action
//                .equals(ChatManager.ACTION_MESSAGE_CALL)) {
//              handleCallMessageReceived(compat);
//            } else if (action
//                .equals(ChatManager.ACTION_MESSAGE_STATUS)) {
//              MessageStatus messageStatus = (MessageStatus) compat;
//              handleMessageStatusReceived(messageStatus);
//            }
          }
        }
        requestMoreMessage(false);
      }
    };
    IntentFilter intentFilter = new IntentFilter(ChatManager.ACTION_MESSAGE);
    intentFilter.addAction(ChatManager.ACTION_AUTHENTICATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_WINK);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_FILE);
    intentFilter.addAction(ChatManager.ACTION_SEND_FILE_ERROR);
    intentFilter.addAction(ChatManager.ACTION_DOWNLOAD_FILE_ERROR);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_TYPING);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_GIFT);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_LOCATION);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_STICKER);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_CALL);
    intentFilter.addAction(ChatManager.ACTION_LOCAL_MESSAGE_CALL);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_STATUS);
    intentFilter.addAction(ChatManager.ACTION_MESSAGE_UPDATE_FILE);

    /*
     * In ChatFragment, when user has blocked a partner, it has executed
     * close the ChatFragment and show right panel, so it is not necessary
     * to process when received AccountStatus.ACTION_BLOCKED.
     */
    mLocalBroadcastManager.registerReceiver(mChatReceiver, intentFilter);

    mMessageStatusReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ChatManager.ACTION_MESSAGE_READ)) {
          requestMoreMessage(false);

          //Todo update message read khi nhan read message call
          Serializable objectSent = intent
              .getSerializableExtra(ChatManager.EXTRA_DATA);
          MessageClient compat = (MessageClient) objectSent;
          MessageStatus status = (MessageStatus) compat;
          mChatAdapter.updateReadMessage(status.getMessageCheckedId());
        } else if (action.equals(ChatManager.ACTION_MESSAGE_READ_ALL)) {
          requestMoreMessage(false);

          //Todo update all message read khi nhan readAll message call
          mChatAdapter.readAllMessage();
        }

      }
    };
    IntentFilter msgSttFilter = new IntentFilter(
        ChatManager.ACTION_MESSAGE_READ);
    msgSttFilter.addAction(ChatManager.ACTION_MESSAGE_READ_ALL);
    mLocalBroadcastManager.registerReceiver(mMessageStatusReceiver,
        msgSttFilter);
  }

  private void registerStickerReload() {
    mStickerReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(DataFetcherService.ACTION_RELOAD_STICKER)) {
          if (mEmojiPanel != null) {
            mEmojiPanel.onPanelShowed();
          }
        } else if (action
            .equals(DataFetcherService.ACTION_STICKER_DOWNLOAD_DONE)) {
          if (mEmojiPanel != null) {
            mEmojiPanel.setIsLoadingSticker(false);
          }
        }
      }
    };

    IntentFilter intentFilter = new IntentFilter(
        DataFetcherService.ACTION_RELOAD_STICKER);
    intentFilter.addAction(DataFetcherService.ACTION_STICKER_DOWNLOAD_DONE);
    getActivity().registerReceiver(mStickerReceiver, intentFilter);
  }

  private void registerRecordReceiver() {
    mRecordReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_STOP_RECORD)) {
          SlidingMenu slidingMenu = getSlidingMenu();
          if (slidingMenu != null) {
            slidingMenu.setSlidingEnabled(true);
          }
          stopRecordAudio();
        }
      }
    };

    IntentFilter intentFilter = new IntentFilter(ACTION_STOP_RECORD);
    mLocalBroadcastManager.registerReceiver(mRecordReceiver, intentFilter);
  }

  private void handleChatMessageReceived(MessageClient compat) {
    mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    Message message = compat.getMessage();

    String time = (message.serverTime > 0 ? String.valueOf(message.serverTime)
        : Utility.getTimeInGMT());

    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        false, message.value, time, ChatMessage.PP);
    // Sent read message if need
    if (!chatMessage.isOwn()) {
      // If it's not my message, check read time
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        // If not exist message read time, send RD message
        String sendUserId = chatMessage.getUserId();
        String msgId = chatMessage.getMessageId();
        sendReadMessage(mMe.getId(), sendUserId, msgId);
      }
    }

    String msg = ChatUtils.decryptMessageReceived(chatMessage.getContent());
    msg = EmojiUtils.convertTag(msg);
    chatMessage.setContent(msg);
    mChatAdapter.removeAllTypingMessage();
    mChatAdapter.appendNewMessage(chatMessage);
        /*
		mNumMessageReceived++;
		if (mNumMessageReceived == 10) {
			LogUtils.i(TAG,
					"Request mark as read message from " + mFriend.getId());
			markAsRead(mFriend.getId());
		}
		*/
  }

  private void handleWinkMessageReceived(MessageClient messageClient) {
    Message message = messageClient.getMessage();

    String time = (message.serverTime > 0 ? String.valueOf(message.serverTime)
        : Utility.getTimeInGMT());

    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        false, message.value, time, ChatMessage.WINK);
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
    mChatAdapter.appendNewMessage(chatMessage);
  }

  private void handleFileMessageReceived(MessageClient compat) {
    String userCome = compat.getMessage().from;
    if (!userCome.equals(mFriend.getId())) {
      return;
    }
    FileMessage fileMessage = (FileMessage) compat;
    LogUtils.d(TAG, "File Name received=" + fileMessage.getFileName());
    LogUtils.d(TAG, "File Id received=" + fileMessage.getFileId());
    LogUtils.d(TAG, "File Type received=" + fileMessage.getFileType());
    // If file is start send, add new chat message to chat list
    // in Adapter
    if (fileMessage.isStartSent()) {
      String type = fileMessage.getFileType();
      String t = null;
      if (type.equals(ChatManager.PHOTO)) {
        t = ChatMessage.PHOTO;
      } else if (type.equals(ChatManager.AUDIO)) {
        t = ChatMessage.AUDIO;
      } else if (type.equals(ChatManager.VIDEO)) {
        t = ChatMessage.VIDEO;
      }
      if (t != null) {
        ChatMessage chatMessage = new ChatMessage(
            fileMessage.getMessage().id,
            fileMessage.getMessage().from, false,
            fileMessage.getMessage().value, Utility.getTimeInGMT(),
            t, fileMessage);
        mChatAdapter.appendNewMessage(chatMessage);
      }
    } else {
      // If message (2) of sent file ->updateMedia()
      String type = fileMessage.getFileType();
      String t = null;
      if (type.equals(ChatManager.PHOTO)) {
        t = ChatMessage.PHOTO;
      } else if (type.equals(ChatManager.AUDIO)) {
        t = ChatMessage.AUDIO;
      } else if (type.equals(ChatManager.VIDEO)) {
        t = ChatMessage.VIDEO;
      }
      ChatMessage chatMessage = new ChatMessage(
          fileMessage.getMessageId(), fileMessage.getMessage().from,
          false, fileMessage.getMessage().value,
          Utility.getTimeInGMT(), t, fileMessage);
      mChatAdapter.updateMedia(chatMessage);
      if (!chatMessage.isOwn()) {
        String readTime = chatMessage.getReadTime();
        if (readTime == null || readTime.length() < 1) {
          sendReadMessage(mMe.getId(), chatMessage.getUserId(),
              chatMessage.getMessageId());
        }
      }
    }

  }

  private void handleActionSentFileError(String msg) {
    if (mErrorDialog != null && mErrorDialog.isShowing()) {
      return;
    }
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.sent_file);
    builder.setCustomTitle(customTitle);
    //builder.setTitle(R.string.sent_file);
    builder.setMessage(msg);
    builder.setPositiveButton(R.string.common_ok, null);
    mErrorDialog = builder.create();
    mErrorDialog.show();

    int dividerId = mErrorDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mErrorDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void handleActionDownloadFileError(String msg) {
    if (mErrorDialog != null && mErrorDialog.isShowing()) {
      return;
    }
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.download_file);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.download_file);
    builder.setMessage(msg);
    builder.setPositiveButton(R.string.common_ok, null);
    mErrorDialog = builder.create();
    mErrorDialog.show();

    int dividerId = mErrorDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mErrorDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void handleTypingMessageReceived(MessageClient compat) {
    // handle typing receive
    Message message = compat.getMessage();
    if (message.value.equalsIgnoreCase(ChatManager.START_TYPING)) {
      // Add new message for start typing
      String msgId = message.id;
      String from = message.from;
      String value = message.value;
      boolean isOwn = false;
      ChatMessage chatMessage = new ChatMessage(msgId, from, isOwn,
          value, Utility.getTimeInGMT(), ChatMessage.TYPING);
      mChatAdapter.appendNewMessage(chatMessage);
    } else {
      // Remove all typing for stop typing message
      mChatAdapter.removeAllTypingMessage();
    }
  }

  private void handleGiftMessageReceived(MessageClient compat) {
    Message message = compat.getMessage();

    String time = (message.serverTime > 0 ? String.valueOf(message.serverTime)
        : Utility.getTimeInGMT());

    boolean isOwner = (
        StringUtils.nullToEmpty(UserPreferences.getInstance().getUserId()).equals(message.from)
            ? true : false);
    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        isOwner, message.value, time, ChatMessage.GIFT);
    mChatAdapter.appendNewMessage(chatMessage);
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
  }

  private void handleLocationMessageReceived(MessageClient compat) {
    Message message = compat.getMessage();

    String time = (message.serverTime > 0 ? String.valueOf(message.serverTime)
        : Utility.getTimeInGMT());

    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        false, message.value, time,
        ChatMessage.LOCATION);
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
    mChatAdapter.appendNewMessage(chatMessage);
  }

  private void handleStickerMessageReceived(MessageClient compat) {
    Message message = compat.getMessage();

    String time = (message.serverTime > 0 ? String.valueOf(message.serverTime)
        : Utility.getTimeInGMT());

    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        false, message.value, time,
        ChatMessage.STICKER);
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
    mChatAdapter.appendNewMessage(chatMessage);
  }

  private void handleCallMessageReceived(MessageClient compat) {
    Message message = compat.getMessage();
    String msgType = "";
    if (message.msgType == MessageType.SVIDEO) {
      msgType = ChatMessage.STARTVIDEO;
      return;
    } else if (message.msgType == MessageType.EVIDEO) {
      msgType = ChatMessage.ENDVIDEO;
    } else if (message.msgType == MessageType.SVOICE) {
      msgType = ChatMessage.STARTVOICE;
      return;
    } else if (message.msgType == MessageType.CALLREQ) {
      msgType = ChatMessage.CALLREQUEST;
    } else {
      msgType = ChatMessage.ENDVOICE;
    }
    /**
     * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
        /*ChatMessage chatMessage = new ChatMessage(message.id, message.from,
                false, message.value, Utility.getTimeInGMT(), msgType);*/
    ChatMessage chatMessage = new ChatMessage(message.id, message.from, isMyMessage(message),
        message.value, String.valueOf(message.serverTime), msgType);
    /**
     * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
    /**
     * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     * http://10.64.100.201/issues/10267
     * http://10.64.100.201/issues/11092
     */
    if (message.msgType == MessageType.EVOICE || message.msgType == MessageType.EVIDEO
        || message.msgType == MessageType.CALLREQ) {
      Log.d(TAG, "handleCallMessageReceived.appendNewMessage.id=" + compat.getMessage().id);

      mChatAdapter.appendNewMessage(chatMessage);
    }

    //requestNewMessage();
    /**
     * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     * http://10.64.100.201/issues/10267
     */
  }

  private void handleLocalCallMessageReceived(MessageClient compat) {
    Message message = compat.getMessage();
    String msgType = "";
    if (message.msgType == MessageType.SVIDEO) {
      msgType = ChatMessage.STARTVIDEO;
      return;
    } else if (message.msgType == MessageType.EVIDEO) {
      msgType = ChatMessage.ENDVIDEO;
    } else if (message.msgType == MessageType.SVOICE) {
      msgType = ChatMessage.STARTVOICE;
      return;
    } else if (message.msgType == MessageType.CALLREQ) {
      msgType = ChatMessage.CALLREQUEST;
    } else {
      msgType = ChatMessage.ENDVOICE;
    }
    /**
     * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
        /*ChatMessage chatMessage = new ChatMessage(message.id, message.from,
                false, message.value, Utility.getTimeInGMT(), msgType);*/
    ChatMessage chatMessage = new ChatMessage(message.id, message.from, isMyMessage(message),
        message.value, String.valueOf(message.serverTime), msgType);
    /**
     * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     */
    if (!chatMessage.isOwn()) {
      String readTime = chatMessage.getReadTime();
      if (readTime == null || readTime.length() < 1) {
        sendReadMessage(mMe.getId(), chatMessage.getUserId(),
            chatMessage.getMessageId());
      }
    }
    /**
     * TODO START - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     * http://10.64.100.201/issues/10267
     */
    if (message.msgType == MessageType.EVOICE || message.msgType == MessageType.EVIDEO) {
      Log.d(TAG, "handleCallMessageReceived.appendNewMessage.id=" + compat.getMessage().id);

      mChatAdapter.appendNewMessage(chatMessage);
    }

    //requestNewMessage();
    /**
     * TODO END - Updated by Robert on 2017 Nov 03 about text time is 00:00 and "cancel", detail in two tickets:
     * http://10.64.100.201/issues/10589
     * http://10.64.100.201/issues/10577
     * http://10.64.100.201/issues/10267
     */
  }

  /**
   * TODO Detect yourself is owner of message
   *
   * @return Boolean = true if your is Owner of message else your friend's is Owner
   * @author Created by Robert on 2017 Nov 06
   */
  private boolean isMyMessage(final Message message) {
    if (message == null) {
      return false;
    }
    if (StringUtils.isEmptyOrNull(UserPreferences.getInstance().getUserId())) {
      return false;
    }
    if (mFriend == null || StringUtils.isEmptyOrNull(mFriend.getId())) {
      return true;
    }
    if (StringUtils.nullToEmpty(UserPreferences.getInstance().getUserId()).equals(message.from)) {
      return true;
    }

    /*Example
     * msgId = 597961870cf209e3580afac6&59fc23f50cf23eb5d0dfd7e7&86e82a12c2d011e78 <=>
     *       = yourUserID + FriendID + CallID or
     *       = yourUserID + FriendID + Timestamp or
     *       = FriendID + yourUserID + CallID
     *       = FriendID + yourUserID + Timestamp
     */
    String msgID = message.id;

    String[] parts = message.id.split("&");

    if (parts != null && parts.length == 3 && UserPreferences.getInstance().getUserId()
        .equals(parts[0])) {
      return true;
    }

    return false;
  }

  private void handleMessageStatusReceived(MessageStatus messageStatus) {
//    if (messageStatus != null) {
//      mChatAdapter.updateMessageStatus(
//          messageStatus.getMessageCheckedId(),
//          messageStatus.isSentSuccess());
//    }
    if (messageStatus != null) {
      final boolean isError = !messageStatus.isSentSuccess() || messageStatus.isNotEnoughPoint();
      LogUtils.i(TAG, "==================================Ready================================================");

      LogUtils.i(TAG, "handleMessageStatusReceived ----> MessageStatus = " + messageStatus.toString());
      LogUtils.i(TAG, String.format("handleMessageStatusReceived ----> MessageStatus=[%1$s|%2$s] =>%3$s", messageStatus.isSentSuccess(), messageStatus.isNotEnoughPoint(), isError));

      for (ChatMessage item : mChatAdapter.getListChatMessage()) {
        LogUtils.i(TAG, String.format("handleMessageStatusReceived ----> ChatMessage=[%1$s]", item.toString()));
      }
      if (isError) {
        dismissUploading();


        StatusController.getInstance(mContext).updateStatus(messageStatus.getMessageCheckedId());
        mChatAdapter.updateMessageStatusError(messageStatus.getMessageCheckedId());
      }
      LogUtils.i(TAG, "==================================End Ready============================================");
    }

  }

  /**
   * control when authenticate with chat server
   */
  private void handleAuthenticationMessage(
      AuthenticationMessage authenticationMessage) {
    if (!authenticationMessage.isSuccess()) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          Response.CLIENT_ERROR_AUTHEN_WITH_CHAT_SERVER);
    }
  }

  private void unregisterChat() {
    if (mLocalBroadcastManager != null) {
      mLocalBroadcastManager.unregisterReceiver(mChatReceiver);
      mLocalBroadcastManager.unregisterReceiver(mMessageStatusReceiver);
    }
  }

  private void unregisterStickerReload() {
    getActivity().unregisterReceiver(mStickerReceiver);
  }

  private void unregisterRecordReceiver() {
    mLocalBroadcastManager.unregisterReceiver(mRecordReceiver);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mMainActivity = (MainActivity) activity;
    mContext = mMainActivity.getApplicationContext();
    mUploadService = mMainActivity.mUploadService;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    LogUtils
        .d(TAG, "--->onActivityCreated is calling...before call to startInitializeData method...");

    mChatAdapter = new ChatAdapter(getActivity(), this, mListChat);
    mChatAdapter.setOnGetVideoURL(this);
    mChatAdapter.setOnOpenImage(this);
    mChatAdapter.setUserName(mFriend.getName());
    mListChat.setAdapter(mChatAdapter);

    // Chi bat dau initialize data khi da ket noi voi UploadService (
    // Khi app
    // bi kill va click vao message tren notification bar thi
    // se khong kip ket noi voi UploadService khi man hinh chat goi toi
    // method nay, do do se khong the start initialize data, khi nay se
    // initialize data khi ket noi thanh cong voi UploadService, method nay
    // la: onUploadServiceConnected())
    if (mUploadService != null) {
      startInitializeData();
    }
    requestUserInfo(mFriend.getId());
  }

  /**
   * load userInfo when has only userId
   */

  private void requestGetPoint() {
    String token = UserPreferences.getInstance().getToken();
    GetPointRequest getPointRequest = new GetPointRequest(token);
    requestServer(LOADER_ID_GET_POINT, getPointRequest);
  }

  private void handleGetPointResponse(GetPointResponse getPointResponse) {
    LogUtils.i(TAG, "---------------> GetPointResponse");
    getLoaderManager().destroyLoader(LOADER_ID_GET_POINT);
    UserPreferences.getInstance().saveNumberPoint(getPointResponse.getPoint());
  }
  private void requestUserInfo(String userId) {
    String token = UserPreferences.getInstance().getToken();
    GetBasicInfoRequest basicInfoRequest = new GetBasicInfoRequest(token,
        userId);
    switchToLoading();
    requestServer(LOADER_ID_GET_BASE_USER_INFO, basicInfoRequest);
  }

  private void handleGetBasicInfoResponse(GetBasicInfoResponse infoResponse) {
    LogUtils.i(TAG, "---------------> handleGetBasicInfoResponse");
    getLoaderManager().destroyLoader(LOADER_ID_GET_BASE_USER_INFO);
    Preferences preferences = Preferences.getInstance();
    preferences.saveChatPoint(infoResponse.getChatPoint());
    preferences.saveViewImageChatPoint(infoResponse.getChatPoint());
    preferences.saveWatchVideoChatPoint(infoResponse.getChatPoint());
    preferences.saveListenAudioChatPoint(infoResponse.getChatPoint());
    if (infoResponse.getCode() == Response.SERVER_SUCCESS) {
      mFriend.setAvatar(infoResponse.getAvataId());
      mFriend.setName(infoResponse.getUserName());
      mFriend.setGender(infoResponse.getGender());


      isVoiceCallWaiting = infoResponse.isVoiceWaiting();
      isVideoCallWaiting = infoResponse.isVideoWaiting();
      if (isVoiceCallWaiting) {
        mImgChatVoiceCall.setBackgroundResource(R.drawable.bg_chat_record);
      } else {
        mImgChatVoiceCall.setBackgroundResource(R.drawable.bg_ground_gray);
      }
      // ADD_20160801_ITS#21207_HungHN_in call. call waiting setting auto switch off
      FavouritedPrefers favouritedPrefers = FavouritedPrefers
          .getInstance();
      if (infoResponse.isFav() == 1) {
        favouritedPrefers.saveFav(mFriend.getId());
      } else {
        favouritedPrefers.removeFav(mFriend.getId());
      }

      mActionBar.setTextCenterTitle(mFriend.getName());
      mChatAdapter.setUserName(mFriend.getName());
      mChatAdapter.notifyDataSetChanged();
      resetNavigationBar();

    } else if (infoResponse.getCode() == Response.SERVER_USER_NOT_EXIST) {

      mActionBar.setTextCenterTitle(mFriend.getName());
    } else if (infoResponse.getCode() == Response.CLIENT_ERROR_NO_CONNECTION) {
      mActionBar.setTextCenterTitle(mFriend.getName());
    }
  }

  public void executeAddToFavorites() {
    mNewAddFavoriteRequest = true;
    String token = UserPreferences.getInstance().getToken();
    AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
        mFriend.getId());
    restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
  }

  public void executeViewBuzz() {
    replaceFragment(
        UserBuzzListFragment.newInstance(mFriend.getId(),
            mFriend.getName()), "user_buzz_list");
  }

  private void showGiveGiftFragment() {
    GiftCategories categories = new GiftCategories("get_all_gift", 0,
        getResources().getString(R.string.give_gift_all_title), 1);
    ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend.newInstance(
        mFriend.getId(), mFriend.getName(), categories, false, true);
    chooseGiftToSend.setTargetFragment(getRootParentFragment(this), REQUEST_GIFT);
    replaceFragment(chooseGiftToSend);
  }

  private void requestHistory(HistoryRequest historyRequest) {
    restartRequestServer(LOADER_ID_HISTORY, historyRequest);
  }

  private void requestNewestHistory() {
    String token = UserPreferences.getInstance().getToken();
    if (mChatAdapter != null) {
      mChatAdapter.clearAllMessage();
    }
    // Set time="" caused by first request (by API description)
    String time = "";
    HistoryRequest historyRequest = new HistoryRequest(token,
        mFriend.getId(), time, take);
    requestHistory(historyRequest);
  }

  public void requestMoreMessage(boolean isNow) {
    dismissRequestMoreMessage();
    if (isNow) {
      //historyRequestHandle.post(requestMoreHistory);
    } else {
//            historyRequestHandle.postDelayed(requestMoreHistory,
//                    DELAY_REQUEST_MORE_HISTORY);
    }
  }

  public void dismissRequestMoreMessage() {
    historyRequestHandle.removeCallbacks(requestMoreHistory);
  }

  private void requestCheckUnlock(int type, String id) {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    String userId = mFriend.getId();
    checkUnlockType = type;
    checkUnlockID = id;
    CheckUnlockRequest request = new CheckUnlockRequest(token, userId,
        type, id);
    getLoaderManager().destroyLoader(LOADER_ID_CHECK_UNLOCK);
    restartRequestServer(LOADER_ID_CHECK_UNLOCK, request);
  }

  private void requestVideoURL(String id) {
    String token = UserPreferences.getInstance().getToken();
    GetVideoUrlRequest request = new GetVideoUrlRequest(token, id);
    restartRequestServer(LOADER_ID_GET_VIDEO_URL, request);
  }

  private void requestUnlock() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    String userId = mFriend.getId();
    UnlockRequest request = new UnlockRequest(token, checkUnlockType,
        userId, checkUnlockID);
    restartRequestServer(LOADER_ID_UNLOCK, request);
  }

  @Override
  public void onPause() {
    super.onPause();
    LogUtils.i(TAG, "onPause()...is called...");
    // save text in editext when move to VideoCallFragment
    ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
    String userId = mFriend.getId();
    String msg = mEdtContent.getText().toString();
    if (msg.replace("\u3000", " ").trim().length() == 0) {
      chatMessagePreference.removeMessage(userId);
    } else {
      String htmlWithEmojiImgTagMsg = Html.toHtml(mEdtContent.getText());
      htmlWithEmojiImgTagMsg = htmlWithEmojiImgTagMsg.replaceAll("&#12288;", "&nbsp;");
      // Parse EMOJI IMG tag to EMOJI code
      String htmlWithEmojiCodeMsg = EmojiUtils.convertEmojiToCode(htmlWithEmojiImgTagMsg);
      // Remove all HTML tag in message
      String content = Html.fromHtml(htmlWithEmojiCodeMsg).toString().trim();
      chatMessagePreference.saveMessage(userId, content);
    }
    hideKeyboard();
    mChatAdapter.stopPlayAudio();
    mChatAdapter.clearStatusAdioPlay();
  }

  private void stopPlayAudio() {
    if (mMediaPlayer != null) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
      }
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    if (mChatAdapter != null) {
      mChatAdapter.stopPlayAudio();
    }
  }

  // ================End HoanDC===========================

  private void playAudio(String id) {
    ChatMessage chatMessage = mChatAdapter.getItemByFileId(id);
    if (chatMessage != null) {
      mChatAdapter.startPlayAudio(chatMessage);
    }
  }

  private void showImage(String imgId) {
    mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
    Intent intent = new Intent(getActivity(),
        DetailPicturePreviousPhotoActivity.class);
    UserInfoResponse user = new UserInfoResponse(new ResponseData());
    user.setAvataId(mFriend.getAvatar());
    user.setGender(mFriend.getGender());
    user.setUserId(mFriend.getId());
    Bundle userData = ProfilePictureData.parseDataToBundle(0, user, imgId);
    intent.putExtras(userData);
    intent.putExtra(DetailPicturePreviousPhotoActivity.KEY_IS_GALLERY,
        false);
    startActivity(intent);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_PARTNER_ID, mFriend.getId());
    outState.putString(KEY_PARTNER_AVATA, mFriend.getAvatar());
    outState.putInt(KEY_TAKE, take);
    outState.putBoolean(KEY_IS_NAVIGATION_BACK, isNavigationBack);
    outState.putString(mFriend.getName(), mFriend.getName());
    outState.putInt(KEY_RECEIVED_MESSAGE_NUM, mNumMessageReceived);
    outState.putLong(KEY_DOWNLOAD_ID, mDownloadId);
    outState.putBoolean(KEY_IS_WAITING_DOWNLOAD, mWaitingDownload);
    outState.putBoolean(KEY_IS_VOICE_CALL_WAITING, isVoiceCallWaiting);
    outState.putBoolean(KEY_IS_VIDEO_CALL_WAITING, isVideoCallWaiting);
    outState.putBoolean(KEY_SEND_GIFT_FROM_PROFILE, isSendGiftFromProfile);
  }

  /**
   * Initial reference for view
   */
  private void initView(View view) {
    mLayoutMain = (DispatchTouchLayout) view.findViewById(R.id.rootLayout);
    mLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(
        mOnGlobalLayoutListener);
    mLayoutMain.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
      }
    });
    mLayoutLoading = (FrameLayout) view.findViewById(R.id.layout_loading);
    mViewFreezedLayer = (View) view
        .findViewById(R.id.ib_chat_freezed_layer);
    mViewFreezedLayer.setOnTouchListener(this);
    mLayoutData = (LinearLayout) view.findViewById(R.id.layout_data);
    mtxtStatus = (TextView) view.findViewById(R.id.txtStatus);
    mEdtContent = (EditText) view
        .findViewById(R.id.fragment_chat_edt_content);
    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.fragment_chat_list_chat);
    Resources resource = getResources();
    mPullToRefreshListView.setPullLabelHeader(resource
        .getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshListView.setReleaseLabelHeader(resource
        .getString(R.string.pull_to_load_more_release_label));
    mListChat = mPullToRefreshListView.getRefreshableView();
    // fix issue:  http://10.64.100.201/issues/11774
    // mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
    mPannelMediaFile = viewPanel.findViewById(R.id.panelMediaFile);
    mPanelMedia = viewPanel.findViewById(R.id.panelMedia);
    ivAdd = (ImageView) view.findViewById(R.id.fragment_chat_btn_add);
    mbtnAddMedia = (ImageView) view
        .findViewById(R.id.fragment_chat_btn_add_emoji);

    mPullToRefreshListView.setOnRefreshListener(onChatListRefreshListener);
    Utility.hideKeyboard(baseFragmentActivity, mPullToRefreshListView);
    mListChat.setOnItemClickListener(this);
    mListChat.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        hidePanel();
        hideKeyboard();
        return false;
      }
    });
    ivAdd.setOnClickListener(this);
    mbtnAddMedia.setOnClickListener(this);
    mEdtContent.setOnClickListener(this);
    // ================Start HoanDC====================
    mEdtContent.addTextChangedListener(textWatcher);

    mEdtContent.setOnTouchListener(this);
    mEdtContent.setCustomSelectionActionModeCallback(textActionMode);
    // ================End HoanDC====================
    // Add view for panel media
    ViewPager viewPager = (ViewPager) viewPanel
        .findViewById(R.id.item_fragment_chat_media_viewpager);
    Button btnEmoji = (Button) viewPanel
        .findViewById(R.id.item_fragment_chat_media_btn_emoji);
    Button btnSticker = (Button) viewPanel
        .findViewById(R.id.item_fragment_chat_media_btn_sticker);
    ImageView btnBack = (ImageView) viewPanel
        .findViewById(R.id.item_fragment_chat_media_btn_back);
    TableRow stickerBar = (TableRow) viewPanel
        .findViewById(R.id.item_fragment_chat_media_tbr_sticker);
    TableRow emojiBar = (TableRow) viewPanel
        .findViewById(R.id.item_fragment_chat_media_tbr_emoji);
    LinearLayout linearLayoutSticker = (LinearLayout) viewPanel
        .findViewById(R.id.item_fragment_chat_media_linearlayout_sticker);
    FrameLayout loadingLayout = (FrameLayout) viewPanel
        .findViewById(R.id.progress);
    CirclePageIndicator circlePageIndicator = (CirclePageIndicator) viewPanel
        .findViewById(R.id.item_fragment_chat_media_indicator);
    mEmojiPanel = new EmojiPanel(mContext, mOnEmojiSelected, viewPager,
        circlePageIndicator, btnEmoji, btnSticker, btnBack, stickerBar,
        emojiBar, linearLayoutSticker, loadingLayout);
    // Add view for penel media file
    ViewPager pager = (ViewPager) viewPanel
        .findViewById(R.id.item_fragment_chat_media_file_viewpager);
    CirclePageIndicator indicator = (CirclePageIndicator) viewPanel
        .findViewById(R.id.item_fragment_chat_media_file_indicator);
    mPanelMediaFile = new ChatControlPanel(getActivity(), pager, indicator,
        mOnControlClicked);
    mPanelMediaFile.initView();
    mEmojiPanel.onPanelShowed();
    // HoanDC
    initialRecordView(view);
    initPreviewSticker(view);
    mChatBoxSpace = view.findViewById(R.id.chat_box_space);

    indicator.setPageColor(getResources().getColor(R.color.divider_dark));
    indicator.setFillColor(getResources().getColor(R.color.primary));

    circlePageIndicator.setPageColor(getResources().getColor(R.color.divider_dark));
    circlePageIndicator.setFillColor(getResources().getColor(R.color.primary));

    setHeightChatBoxSpace(mKeyboardHeight);
  }

  // ================Start HoanDC===========================
  @SuppressLint("NewApi")
  private void initialRecordView(View view) {
    mTableRowChat = (TableRow) view.findViewById(R.id.panelHandle);
    mTableRowRecord = (TableRow) view.findViewById(R.id.tbrRecord);
    mRelativeLayoutRecorder = (RelativeLayout) view
        .findViewById(R.id.rlRecorder);
    audioRecordVisualizer = (AudioRecordViewCustom) mRelativeLayoutRecorder
        .findViewById(R.id.audioRecordViewCustom1);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      audioRecordVisualizer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    mBtnRecord = (Button) view.findViewById(R.id.fragment_chat_btnRecord);
    mBtnRecord.setOnTouchListener(this);
    mBtnRecord.setOnClickListener(this);
    mTvRecorderTime = (TextView) view.findViewById(R.id.tvRecorderTime);
    mTvRecorderMessage = (TextView) view
        .findViewById(R.id.tvRecorderMessage);
    mImgRecycle = (ImageView) view.findViewById(R.id.imgRecycle);

    mImgAddReturn = (ImageView) view
        .findViewById(R.id.fragment_chat_btn_add_return);
    mImgEmojiReturn = (ImageView) view
        .findViewById(R.id.fragment_chat_btn_add_emoji_return);
    mImgKeybroad = (ImageView) view
        .findViewById(R.id.fragment_chat_img_key_broad);
    mImgEmojiReturn.setOnClickListener(this);
    mImgAddReturn.setOnClickListener(this);
    mImgKeybroad.setOnClickListener(this);
    mImgChatVoiceCall = (ImageView) view
        .findViewById(R.id.fragment_chat_img_voice_call);

    if (isVoiceCallWaiting) {
      mImgChatVoiceCall.setBackgroundResource(R.drawable.bg_chat_record);
    } else {
//            mImgChatVoiceCall.setBackgroundResource(R.drawable.bg_ground_gray);
      mImgChatVoiceCall.setBackgroundResource(R.drawable.bg_chat_record);
    }

    mImgChatVoiceCall.setOnClickListener(this);
    mTxtChatSend = (TextView) view
        .findViewById(R.id.fragment_chat_img_send);
    mTxtChatSend.setOnClickListener(this);
    mRecMicToMp3.linkToVisualizer(audioRecordVisualizer);
    mRecMicToMp3.setTxtRecorderTime(mTvRecorderTime);
  }

  private void processClickCopyText(int start, int end) {
    String htmlWithImgEmojiTagMsg = Html.toHtml((Spanned) mEdtContent.getText().
        subSequence(start, end));
    String htmlWithEmojiCodeMsg = EmojiUtils.convertEmojiToCode(htmlWithImgEmojiTagMsg);
    String contentCopy = Html.fromHtml(htmlWithEmojiCodeMsg).toString().trim();
    ClipData clipData = ClipData.newPlainText("text-plain", contentCopy);
    mClipboardManager.setPrimaryClip(clipData);
    LogUtils.d("HungHN", "Text Copy: " + contentCopy);
  }

  private void processUpdateEmojiTextChange(String text, int start, int end) {
    LogUtils.d("HungHN", "Text paste: " + text);
    String emojiHtml = EmojiUtils.convertCodeToEmoji(text);
    mEdtContent.getText().replace(start, end, Html.fromHtml(emojiHtml, emojiGetter, null));
    mEdtContent.setSelection(mEdtContent.getSelectionEnd());
  }

  private void setChatVisiable() {
    if (mTableRowChat.getVisibility() == View.VISIBLE) {
      mTableRowChat.setVisibility(View.GONE);
      mTableRowRecord.setVisibility(View.VISIBLE);
    } else {
      mTableRowChat.setVisibility(View.VISIBLE);
      mTableRowRecord.setVisibility(View.GONE);
    }
  }

  private void changeTextMove(boolean isRelease) {
    if (isRelease) {
      mTvRecorderMessage.setText(mContext.getResources().getString(
          R.string.recorder_drag_to_delete));
      mTvRecorderMessage.setTextColor(mContext.getResources().getColor(
          R.color.record_drag));
      mImgRecycle.setImageResource(R.drawable.ic_recycle_bin_none);
      mBtnRecord.setText(mContext.getResources().getString(
          R.string.recorder_release_to_send));
    } else {
      mTvRecorderMessage.setText(mContext.getResources().getString(
          R.string.recorder_release_to_delete));
      mTvRecorderMessage.setTextColor(mContext.getResources().getColor(
          R.color.record_delete));
      mImgRecycle.setImageResource(R.drawable.ic_recycle_bin);
      mBtnRecord.setText(mContext.getResources().getString(
          R.string.recorder_release_to_delete));
    }
  }

  private void setEnableUIRecord(boolean enable) {
    if (mListChat != null) {
      mListChat.setEnabled(enable);
      mListChat.setFocusable(enable);
      mListChat.setFocusableInTouchMode(enable);
      mListChat.setClickable(false);
    }

    if (mPullToRefreshListView != null) {
      mPullToRefreshListView.setEnabled(enable);
      mPullToRefreshListView.setFocusable(enable);
      mPullToRefreshListView.setFocusableInTouchMode(enable);
      mPullToRefreshListView.setClickable(false);
    }

    if (mImgAddReturn != null) {
      mImgAddReturn.setEnabled(enable);
    }
    if (mImgEmojiReturn != null) {
      mImgEmojiReturn.setEnabled(enable);
    }
    if (mImgKeybroad != null) {
      mImgKeybroad.setEnabled(enable);
    }
    mActionBar.setAllEnable(enable);
    if (getNavigationBar() != null) {
      if (getNavigationBar().getButtonLeft() != null) {
        getNavigationBar().getButtonLeft().setEnabled(enable);
      }
      if (getNavigationBar().getButtonRight() != null) {
        getNavigationBar().getButtonRight().setEnabled(enable);
      }
      if (getNavigationBar().getProfileView() != null) {
        getNavigationBar().getProfileView().setEnabled(enable);
      }
      if (getNavigationBar().getImageLeft() != null) {
        getNavigationBar().getImageLeft().setEnabled(enable);
      }
      if (getNavigationBar().getImageRight() != null) {
        getNavigationBar().getImageRight().setEnabled(enable);
      }
    }
  }

  private void setEnableSlidingMenu(boolean enable) {
    SlidingMenu slidingMenu = getSlidingMenu();
    if (slidingMenu == null) {
      return;
    }
    if (enable) {
      slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    } else {
      slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();

    getNavigationBar().setProfileVisibility(View.VISIBLE);
    getNavigationBar().getRemainView().setVisibility(View.GONE);

    // In case of non-hidden user, show profile, title and hide remain
    getNavigationBar().getProfileView().setVisibility(View.VISIBLE);
//    getNavigationBar().setCenterTitle(mFriend.getName());

    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    if (isNavigationBack) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    } else {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    }

    getNavigationBar().getProfileView().setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            clickMoreOptions();
          }
        });
    getNavigationBar().setShowUnreadMessage(true);
  }

  public void clickMoreOptions() {
    if (mPopupChatMoreOptions != null && mPopupChatMoreOptions.isShowing()) {
      hideChatMoreOptions();
    } else {
      showChatMoreOptions();
    }
  }

  private void checkReadMessage() {
    if (mChatAdapter == null) {
      return;
    }

    List<ChatMessage> listMessage = mChatAdapter.getListChatMessage();
    for (ChatMessage message : listMessage) {
      String userId = message.getUserId();
      // If it is not my message
      if (!mMe.getId().equals(userId) && !message.isHeader()
          && !message.isTypingMessage()) {
        String readTime = message.getReadTime();
        // Sent message status read
        if (readTime == null || readTime.length() == 0) {
          String messageId = message.getMessageId();
          sendReadMessage(mMe.getId(), userId, messageId);
        }
      }
    }
  }

  private void sendReadMessage(String currentId, String toSendId, String msgId) {
    Fragment fragment = mNavigationManager.getActivePage();
    if (!(fragment instanceof ChatFragment)) {
      return;
    }
    if (mScreenStatus != SCREEN_STATUS.PRESENT) {
      return;
    }
    ChatManager chatManager = null;
    if (mMainActivity.getChatService() != null) {
      chatManager = mMainActivity.getChatService().getChatManager();
    }
    if (chatManager == null) {
      return;
    }
    chatManager.sendReadMessage(currentId, toSendId, msgId);
  }

  private void sendStickerMessage(String currentUserId, String userIdToSend,String content) {
    if (hasShowDialogValidate())
      return;

    content = content.replace("\u3000", " ").trim();
    if (content.length() > 0) {
      LogUtils.d(TAG, "Sent Message, CurrentUser=" + currentUserId
          + " vs UserToSend=" + userIdToSend + " value=" + content);
      Date date = Utility.getDateTimeInGMT();
      String time = Utility.getTimeStamp(date);
      String messageId = currentUserId + "&" + userIdToSend + "&" + time;
      ChatMessage chatMessage = new ChatMessage(messageId, currentUserId,
          true, content, time, ChatMessage.STICKER);
      Message message = new Message(date, currentUserId, userIdToSend,
          MessageType.STK, chatMessage.getMessageToSend());
      //Chuẩn hóa chỉ sử dụng một messageId xuyên suốt quá trình xl mesage
      message.id = messageId;
      mChatAdapter.appendNewMessage(chatMessage);

      ChatManager chatManager = null;
      if (mMainActivity.getChatService() != null) {
        chatManager = mMainActivity.getChatService().getChatManager();
      }
      if (chatManager == null) {
        return;
      }
      chatManager.setShowDialog(this);
      chatManager.sendGenericMessage(message);
      // sent broadcast to ConversationList
      chatManager.sendBroadcastMessage(
          ChatManager.ACTION_LOCAL_MESSAGE_STICKER,
          new MessageClient(message));
    }
  }

  private void sendMessage(String currentUserId, String userIdToSend,
      String content) {
    if (hasShowDialogValidate())
      return;
    content = content.replace("\u3000", " ").trim();
    if (content.length() > 0) {
      LogUtils.d(TAG, "Sent Message, CurrentUser=" + currentUserId
          + " vs UserToSend=" + userIdToSend + " value=" + content);
      Date date = Utility.getDateTimeInGMT();
      String time = Utility.getTimeStamp(date);
      String messageId = currentUserId + "&" + userIdToSend + "&" + time;
      ChatMessage chatMessage = new ChatMessage(messageId, currentUserId,
          true, content, time, ChatMessage.PP);
      Message message = new Message(date, currentUserId, userIdToSend,
          MessageType.PP, chatMessage.getMessageToSend());
      //Chuẩn hóa chỉ sử dụng một messageId xuyên suốt quá trình xl mesage
      message.id = messageId;

      Message localMessage = new Message(date, currentUserId,
          userIdToSend, MessageType.PP,
          chatMessage.getMessageToSend());
      chatMessage.setContent(EmojiUtils.convertTag(content));
      mChatAdapter.appendNewMessage(chatMessage);
      ChatManager chatManager = null;
      if (mMainActivity.getChatService() != null) {
        chatManager = mMainActivity.getChatService().getChatManager();
      }
      if (chatManager == null) {
        return;
      }
      chatManager.setShowDialog(this);
      // sent broadcast to ConversationList
      chatManager.sendBroadcastMessage(ChatManager.ACTION_LOCAL_MESSAGE,
          new MessageClient(localMessage));
      chatManager.sendGenericMessage(message);
    }
  }

  private void sendTypingMessage(String currentUserId, String userIdToSend,
      String content) {
    if (mMainActivity.getChatService() == null) {
      return;
    }
    ChatManager chatManager = mMainActivity.getChatService()
        .getChatManager();
    if (chatManager == null) {
      return;
    }
    if (ChatManager.START_TYPING.equals(content)) {
      chatManager.sendStartTypingMessage(currentUserId, userIdToSend);
    } else {
      // ChatService null when application has been destroy by OS,
      // don't know why EditText call change text :(
      // so workaround: check null
      chatManager.sendStopTypingMessage(currentUserId, userIdToSend);
    }
  }

  private void sendPhoto(String currentUserId, String userIdToSend, File file) {
    if (file != null && file.exists()) {
      FileMessage fileMessage = new FileMessage(file.getName(),
          ChatMessage.PHOTO, file.getPath());
      // append to ChatAdapter
      Date date = Utility.getDateTimeInGMT();
      String time = Utility.getTimeStamp(date);

      ChatMessage chatMessage = new ChatMessage(mMe.getId(), true, time,
          ChatMessage.PHOTO, fileMessage);
      String messageId = mMe.getId() + "&" + mFriend.getId() + "&" + time;
      chatMessage.setMessageId(messageId);
      if (mMainActivity.getChatService() == null) {
        return;
      }
      ChatManager chatManager = mMainActivity.getChatService()
          .getChatManager();
      if (chatManager == null) {
        return;
      }
      chatManager.setShowDialog(this);
      // send to chat server
      mMainActivity.getChatService().sendPhoto(messageId, date,
          mFriend.getId(), file.getName(), file);
      mChatAdapter.appendNewMessage(chatMessage);
      // boardcast to Conversation List
      Message message = new Message(date, currentUserId, userIdToSend,
          MessageType.FILE, ChatManager.PHOTO);
      message.id = messageId;
      // Message message = new Message(date, currentUserId, userIdToSend,
      // MessageType.FILE, "msgid|" + ChatManager.PHOTO + "|fileId"
      // + "|fileName");
      chatManager.sendBroadcastMessage(
          ChatManager.ACTION_LOCAL_MESSAGE_FILE, new MessageClient(
              message));
    }
  }

  /**
   * Use for send previous photo
   */
  private void sendPhoto(Date date, String currentUserId,
      String userIdToSend, String imgId) {
    if (getActivity() != null) {
      FileMessage fileMessage = new FileMessage(ChatMessage.PHOTO, imgId);
      // must not start send message because actal has imgId
      fileMessage.setStart(false);
      fileMessage.uploadState = UploadState.SUCCESSFUL;
      fileMessage.uploadProgress = 100;
      // append to ChatAdapter
      String time = Utility.getTimeStamp(date);

      ChatMessage chatMessage = new ChatMessage(mMe.getId(), true, time,
          ChatMessage.PHOTO, fileMessage);
      String messageId = mMe.getId() + "&" + mFriend.getId() + "&" + time;
      chatMessage.setMessageId(messageId);
      ChatManager chatManager = null;
      if (mMainActivity.getChatService() != null) {
        chatManager = mMainActivity.getChatService().getChatManager();
      }
      if (chatManager == null) {
        return;
      }
      chatManager.setShowDialog(this);
      // send to chat server
      chatManager.sendConfirmSentFileMessage(date, mMe.getId(),
          mFriend.getId(), ChatManager.PHOTO, imgId, " ");

      mChatAdapter.appendNewMessage(chatMessage);
      // boardcast to Conversation List
      Message message = new Message(date, currentUserId, userIdToSend,
          MessageType.FILE, ChatManager.PHOTO);
      message.id = messageId;
      // Message message = new Message(date, currentUserId, userIdToSend,
      // MessageType.FILE, "msgid|" + ChatManager.PHOTO + "|"
      // + imgId + "|filename");
      chatManager.sendBroadcastMessage(
          ChatManager.ACTION_LOCAL_MESSAGE_FILE, new MessageClient(
              message));
    }
  }

  private void sendPreviousPhoto(final String imageId) {
    if (mMainActivity.getChatService() == null) {
      return;
    }
    ChatManager chatManager = mMainActivity.getChatService()
        .getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.setShowDialog(this);
    Date date = Utility.getDateTimeInGMT();
    chatManager.sendStartSentMediaMessage(date, mMe.getId(),
        mFriend.getId(), ChatManager.PHOTO, null);
    sendPhoto(date, mMe.getId(), mFriend.getId(), imageId);
  }

  private void sendVideo(String currentUserId, String userIdToSend, File file) {
    if (file != null && file.exists()) {
      FileMessage fileMessage = new FileMessage(file.getName(),
          ChatMessage.VIDEO, file.getPath());

      // Append to ChatAdapter
      Date date = Utility.getDateTimeInGMT();
      String time = Utility.getTimeStamp(date);

      ChatMessage chatMessage = new ChatMessage(mMe.getId(), true, time,
          ChatMessage.VIDEO, fileMessage);
      String messageId = mMe.getId() + "&" + mFriend.getId() + "&" + time;
      chatMessage.setMessageId(messageId);

      if (mMainActivity.getChatService() == null) {
        return;
      }
      ChatManager chatManager = mMainActivity.getChatService()
          .getChatManager();
      if (chatManager == null) {
        return;
      }
      chatManager.setShowDialog(this);

      // Send to chat server
      mMainActivity.getChatService().sendVideo(messageId, date,
          userIdToSend, file.getName(), file);
      mChatAdapter.appendNewMessage(chatMessage);
      // Broadcast to Conversation List
      Message message = new Message(date, currentUserId, userIdToSend,
          MessageType.FILE, ChatManager.VIDEO);
      message.id = messageId;
      chatManager.sendBroadcastMessage(
          ChatManager.ACTION_LOCAL_MESSAGE_FILE, new MessageClient(
              message));
    }
  }

  private String sendAudio(Date date, String audioDuration, File file) {
    // Date date = Utility.getDateTimeInGMT();
    String time = Utility.getTimeStamp(date);
    TimeAudioHold audioHold = new TimeAudioHold(audioDuration, 0);
    FileMessage fileMessage = new FileMessage(ChatManager.AUDIO, audioHold,
        file.getPath());

    String messageId = mMe.getId() + "&" + mFriend.getId() + "&" + time;
    ChatMessage chatMessage = new ChatMessage(mMe.getId(), true, time,
        ChatMessage.AUDIO, fileMessage);
    chatMessage.setMessageId(messageId);
    setEnableUIRecord(true);
    setEnableUIRecord(true);
    mChatAdapter.setEnableView(true);
    mChatAdapter.appendNewMessage(chatMessage);

    // boardcast to Conversation List
    Message message = new Message(date, mMe.getId(), mFriend.getId(),
        MessageType.FILE, ChatManager.AUDIO);
    //Chuẩn hóa chỉ sử dụng một messageId xuyên suốt quá trình xl mesage
    message.id = messageId;
    // Message message = new Message(date, mCurrentUserId, mUserIdToSend,
    // MessageType.FILE, "msgid|" + ChatManager.AUDIO + "|audioId"
    // + "|fileName");
    //
    ChatManager chatManager = null;
    if (mMainActivity.getChatService() != null) {
      chatManager = mMainActivity.getChatService().getChatManager();
    }
    if (chatManager == null) {
      return "";
    }
    chatManager.setShowDialog(this);
    chatManager.sendBroadcastMessage(ChatManager.ACTION_LOCAL_MESSAGE_FILE,
        new MessageClient(message));
    // Toast.makeText(mContext, R.string.chat_audio_sending,
    // Toast.LENGTH_LONG)
    // .show();
    return messageId;
  }

  @Override
  public void onClick(View v) {
    // replaceFragment(new StickerShopFragment());
    switch (v.getId()) {
      case R.id.fragment_chat_btn_add_return:
        setChatVisiable();
        clickMediaPanel(ivAdd);
        break;
      case R.id.fragment_chat_btn_add:
        clickMediaPanel(ivAdd);
        break;
      case R.id.fragment_chat_btn_add_emoji_return:
        setChatVisiable();
        clickMediaPanel(mbtnAddMedia);
        break;
      case R.id.fragment_chat_btn_add_emoji:
        clickMediaPanel(mbtnAddMedia);
        break;
      case R.id.fragment_chat_edt_content:
        hideChatMoreOptions();
        hidePanel();
        showKeyboard();
        break;
      case R.id.fragment_chat_img_key_broad:
        hideChatMoreOptions();
        hidePanel();
        setChatVisiable();
        hideKeyboard();
        break;
      case R.id.fragment_chat_img_voice_call:
        hideChatMoreOptions();
        hidePanel();
        Utility.hideSoftKeyboard(getActivity());
        executeVoiceCall();
        break;
      case R.id.fragment_chat_img_send:
        // Check message include dirty word
        if (!Utility.isContainDirtyWord(getActivity(), mEdtContent)) {
          // Get content from EditText. Keep Editable type to get HTML
          String htmlWithEmojiImgTagMsg = Html.toHtml(mEdtContent
              .getText());
          // Parse EMOJI IMG tag to EMOJI code
          String htmlWithEmojiCodeMsg = EmojiUtils
              .convertEmojiToCode(htmlWithEmojiImgTagMsg);
          // Remove all HTML tag in message
          String content = Html.fromHtml(htmlWithEmojiCodeMsg).toString();

          // Check message content length after parse process
          if (content.length() > 0) {
            sendMessage(mMe.getId(), mFriend.getId(), content);
            mEdtContent.setText("");
          }
        }
        break;
      default:
        break;
    }
  }

  private void handleChatHistoryResponse(HistoryResponse response) {
    LogUtils.e(TAG, "-->handleChatHistoryResponse().....");
    List<ChatMessage> list = response.getListChatMessage();
    ChatMessage giftMessage = null;
    // http://10.64.100.201/issues/14055: save old list chat message
    if (list.isEmpty()) {
      if (response.getCode() == Response.CLIENT_ERROR_NO_CONNECTION) {
        list.addAll(listConversationBackup);
      }
    }
    //end
    //TODO END #12814#note-39

    StatusController statusController = StatusController.getInstance(mContext);

    // Merger history
    boolean isMergered = mChatAdapter.isMergeredHistory();
    statusController.mergeWithHistory(list, mFriend.getId(), isMergered);
    mChatAdapter.setMergeredHistory();
    LogUtils.e("TuanPA",
        "handleChatHistoryResponse response ===== " + list.toString() + "isMergered " + isMergered);
    //#12814#note-39 - Updated By Robert on 2018 July 18 - Get gift item in Arguments passing through ChooseGiftToSend
    if (getArguments() != null && getArguments().containsKey(ChooseGiftToSend.EXTRA_GIFT_SENT_ID)
        && getArguments().containsKey(ChooseGiftToSend.EXTRA_GIFT_MSG_SENT)
        && getArguments().containsKey(ChooseGiftToSend.EXTRA_GIFT_OWNER)
        && getArguments().containsKey(ChooseGiftToSend.EXTRA_GIFT_RECEIVER)
        && getArguments().containsKey(ChooseGiftToSend.EXTRA_GIFT_MSG_ID)) {

      //Append to View when Server respond empty data and ensurance has been send gift

      String value = getArguments().getString(ChooseGiftToSend.EXTRA_GIFT_MSG_SENT);
      String giftMessageId = getArguments().getString(ChooseGiftToSend.EXTRA_GIFT_MSG_ID);

      if (!StringUtils.isEmptyOrNull(giftMessageId)) {
        giftMessage = new ChatMessage(giftMessageId, UserPreferences.getInstance().getUserId(),
            true, value, Utility.getTimeInGMT(), ChatMessage.GIFT);
      }
      getArguments().clear();
    }
    if (list == null || list.isEmpty()) {
      LogUtils.e(TAG,
          "-->handleChatHistoryResponse().....list == null || list.isEmpty().giftMessage.id=" + (
              giftMessage != null ? giftMessage.getMessageId() : " null"));

      if (giftMessage != null) {
        //Append to View when Server respond empty data and ensurance has been send gift
        mChatAdapter.appendNewMessage(giftMessage);
      }
      return;
    }

    LogUtils.e(TAG, "-->handleChatHistoryResponse().....list.size()=" + list.size());

    if (giftMessage != null
        && ChatUtils.fetchingIndexMsgResourceOfByIndex(giftMessage, list) == -1) {
      //After call get_chat_history API.
      // Server still did not make it in time to update giftMessage into Database & list message not empty & not contains giftMessage -> append
      list.add(0, giftMessage);
    }
    Iterator<ChatMessage> ite = list.iterator();
    ChatMessage chatMessage;
    while (ite.hasNext()) {
      chatMessage = ite.next();
      if (chatMessage.isOwn()) {
        chatMessage.setUserId(mMe.getId());

        // Set upload state for my message
        if (chatMessage.isFileMessage()) {
          FileMessage fileMessage = chatMessage.getFileMessage();
          if (fileMessage != null) {
            // Default upload state from server is unknown
            fileMessage.uploadState = UploadState.UNKNOW;
            int msgStatus = chatMessage.getStatusSend();

            // Only update when done.
            if (msgStatus == StatusConstant.STATUS_START
                || msgStatus == StatusConstant.STATUS_SENDING_FILE
                || msgStatus == StatusConstant.STATUS_RETRY) {
              long uploadId = mChatUploadManager
                  .getUploadId(chatMessage.getMessageId());

              // If do not upload
              if (uploadId == ChatUploadPrefers.INVALID_UPLOAD_ID) {
                chatMessage
                    .setStatusSend(StatusConstant.STATUS_ERROR);
              } else {
                // Update msg process
                int progress = mUploadService
                    .getProgress(uploadId);
                fileMessage.uploadState = mUploadService
                    .getState(uploadId);
                fileMessage.uploadProgress = progress;
              }
            }
          }
        }
      } else {
        chatMessage.setUserId(mFriend.getId());
      }
    }

    if (list.size() < 1) {
      mPullToRefreshListView.setMode(Mode.DISABLED);
      return;
    }

    mChatAdapter.appendMessageHistoryList(list);
    // fix scroll issue: http://10.64.100.201/issues/11774
    gotoEndListChat();

//        // Disable always scroll for first load
//        if (!isMergered) {
//            int index = 0;
//            mScrollToMsgId = UserPreferences.getInstance()
//                    .getCurrentMsgChatId();
//            if (mScrollToMsgId != null) {
//                index = mChatAdapter.getMsgLocation(mScrollToMsgId);
//            }
//
//            if (index != 0) {
//                final int position = index;
//                mListChat.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mListChat
//                                .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//                        mListChat.setSelection(position);
//                    }
//                });
//            } else {
//                mListChat.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mListChat
//                                .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                    }
//                });
//            }
//        } else {
//            mListChat.post(new Runnable() {
//                @Override
//                public void run() {
//                    mListChat
//                            .setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                }
//            });
//        }
    checkReadMessage();
  }

  /**
   * scroll to end of chat list
   *
   * @see #handleChatHistoryResponse(HistoryResponse)
   */
  private void gotoEndListChat() {
    // Setting to make list view dynamic
    mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    mListChat.setSelection(mChatAdapter.getCount());
  }

  private void handleNewChatMessage(GetNewChatMessageResponse response) {
    List<ChatMessage> list = response.getListChatMessage();
    if (list == null) {
      return;
    }
    boolean isFirstLoad = (mChatAdapter.getCount() == 0);
    StatusController.getInstance(mContext).mergeWithHistory(list,
        mFriend.getId(), isFirstLoad);

    Iterator<ChatMessage> ite = list.iterator();
    ChatMessage chatMessage;
    while (ite.hasNext()) {
      chatMessage = ite.next();
      if (chatMessage.isOwn()) {
        chatMessage.setUserId(mMe.getId());
      } else {
        chatMessage.setUserId(mFriend.getId());
      }
      // set upload state
      if (chatMessage.isFileMessage()) {
        long uploadId = mChatUploadManager.getUploadId(chatMessage
            .getMessageId());
        if (mUploadService != null) {
          chatMessage.getFileMessage().uploadState = mUploadService
              .getState(uploadId);
          chatMessage.getFileMessage().uploadProgress = mUploadService
              .getProgress(uploadId);
        }
      }
    }
    mChatAdapter.appendMessageNewList(list);
    checkReadMessage();
  }

  @Override
  public void startRequest(int loaderId) {
    if (loaderId == LOADER_ID_CHECK_CALL_VIDEO
        || loaderId == LOADER_ID_CHECK_CALL_VOICE
        || loaderId == LOADER_ID_CHECK_UNLOCK) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getString(R.string.waiting));
      progressDialog.show();
    }
  }

  public void finishRequest(int loaderId) {
    if (loaderId == LOADER_ID_CHECK_CALL_VIDEO
        || loaderId == LOADER_ID_CHECK_CALL_VOICE
        || loaderId == LOADER_ID_CHECK_UNLOCK) {
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
    }
  }
  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    Activity activity = getActivity();
    if (activity == null) {
      return;
    }

    int loaderId = loader.getId();
    finishRequest(loaderId);

    int errorCode = response.getCode();

    if (loaderId == LOADER_ID_GET_POINT) {
      if (errorCode != Response.CLIENT_ERROR_NO_CONNECTION && errorCode == Response.SERVER_SUCCESS) {
        handleGetPointResponse((GetPointResponse) response);
      }
      getLoaderManager().destroyLoader(LOADER_ID_GET_POINT);
      return;
    }
    if (loaderId == LOADER_ID_MARK_AS_READ) {
      if (errorCode != Response.CLIENT_ERROR_NO_CONNECTION
          && errorCode == Response.SERVER_SUCCESS) {
        LogUtils.i(TAG, "Read from " + mFriend.getId() + " success");
        mNumMessageReceived = 0;
      }
      getLoaderManager().destroyLoader(LOADER_ID_MARK_AS_READ);
      return;
    }
    if (errorCode != Response.SERVER_SUCCESS
        && errorCode != Response.SERVER_RECEIVER_NOT_ENOUGH_MONEY
        && errorCode != Response.SERVER_NOT_ENOUGHT_MONEY) {
      if (loaderId == LOADER_ID_GET_BASE_USER_INFO) {
        switchToError(getString(R.string.can_not_get_user_info));
      }
      ErrorApiDialog.showAlert(activity, R.string.common_app_name,
          errorCode);
    }

    switch (loaderId) {
      case LOADER_ID_HISTORY:
        if (mChatAdapter.getCount() == 0) {
          requestMoreMessage(false);
        }
        handleChatHistoryResponse((HistoryResponse) response);

        if (mPullToRefreshListView != null) {
          mPullToRefreshListView.onRefreshComplete();
        }

        // Trick to make this only active after notify data set done
        mListChat.post(new Runnable() {
          @Override
          public void run() {
            switchToData();
          }
        });
        break;
//      case LOADER_ID_MARK_AS_READ:
//        LogUtils.i(TAG, "Read from " + mFriend.getId() + " success");
//        mNumMessageReceived = 0;
//        break;
      case LOADER_ID_ADD_BLOCK_USER:
        handleBlockUserResponse((AddBlockUserResponse) response);
        break;
      case LOADER_ID_REMOVE_BLOCK_USER:
        handleUnblockUserResponse((RemoveBlockUserResponse) response);
        break;
      case LOADER_ID_REPORT_USER:
        handleReportUserResponse();
        break;
      case LOADER_ID_ADD_TO_FAVORITES:
        getLoaderManager().destroyLoader(LOADER_ID_ADD_TO_FAVORITES);
        if (mNewAddFavoriteRequest) {
          handleAddFavorite();
        }
        break;
      case LOADER_ID_REMOVE_FROM_FAVORITES:
        handleRemoveFavorite();
        break;
      case LOADER_ID_GET_BASE_USER_INFO:
        handleGetBasicInfoResponse((GetBasicInfoResponse) response);
        break;
      case LOADER_ID_CHECK_CALL_VIDEO:
        handleCheckCall(true, (CheckCallResponse) response);
        break;
      case LOADER_ID_CHECK_CALL_VOICE:
        handleCheckCall(false, (CheckCallResponse) response);
        break;
      case LOADER_ID_GET_VIDEO_URL:
        handleGetVideoURL((GetVideoUrlResponse) response);
        break;
      case LOADER_ID_CHECK_UNLOCK:
        handleCheckUnlock((CheckUnlockResponse) response);
        break;
      case LOADER_ID_UNLOCK:
        handleUnlock((UnlockResponse) response);
        break;
      case LOADER_ID_BASIC_USER_INFO_CALL:
        handlerCheckRequestCall((GetBasicInfoResponse) response);
        getLoaderManager().destroyLoader(LOADER_ID_BASIC_USER_INFO_CALL);
        break;
      case LOADER_ID_CHECK_NEW_MESSAGE:
        getLoaderManager().destroyLoader(LOADER_ID_CHECK_NEW_MESSAGE);
        if (response.getCode() == Response.SERVER_SUCCESS) {
          handleNewChatMessage((GetNewChatMessageResponse) response);
        }
        requestMoreMessage(false);
        break;
    }
  }

  private void handleUnlock(UnlockResponse response) {
    getLoaderManager().destroyLoader(LOADER_ID_UNLOCK);
    if (checkUnlockType == UnlockType.UNKNOW) {
      return;
    }

    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      handleCheckUnlockSuccess(checkUnlockType, checkUnlockID);
    } else if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
      if (checkUnlockType == UnlockType.IMAGE) {
        NotEnoughPointDialog.showForUnlockViewImage(getActivity(),
            response.getPrice(), response.getPoint());
      } else if (checkUnlockType == UnlockType.AUDIO) {
        NotEnoughPointDialog.showForUnlockListenAudio(getActivity(),
            response.getPrice(), response.getPoint());
      } else {
        NotEnoughPointDialog.showForUnlockWatchVideo(getActivity(),
            response.getPrice(), response.getPoint());
      }
    }

    checkUnlockType = UnlockType.UNKNOW;
    checkUnlockID = "";
  }

  private void handleCheckUnlock(CheckUnlockResponse response) {
    getLoaderManager().destroyLoader(LOADER_ID_CHECK_UNLOCK);
    if (checkUnlockType == UnlockType.UNKNOW) {
      return;
    }

    if (response.getIsUnlock() == Constants.UNLOCKED) {
      handleCheckUnlockSuccess(checkUnlockType, checkUnlockID);
      checkUnlockType = UnlockType.UNKNOW;
      checkUnlockID = "";
    } else {
      int pricePoint = response.getPrice();
      Builder builder = new CenterButtonDialogBuilder(getActivity(), true);
      String msg = "";
      if (checkUnlockType == UnlockType.IMAGE) {
        String format = getString(R.string.dialog_unlock_view_image);
        msg = String.format(format, String.valueOf(pricePoint));
      } else if (checkUnlockType == UnlockType.AUDIO) {
        String format = getString(R.string.dialog_unlock_listen_audio);
        msg = String.format(format, String.valueOf(pricePoint));
      } else {
        String format = getString(R.string.dialog_unlock_watch_video);
        msg = String.format(format, String.valueOf(pricePoint));
      }
      builder.setMessage(msg);
      builder.setNegativeButton(R.string.common_cancel, null);
      builder.setPositiveButton(R.string.common_ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              requestUnlock();
            }
          });
      mAlertDialog = builder.create();
      mAlertDialog.show();
    }
  }

  private void handleCheckUnlockSuccess(int type, String id) {
    switch (type) {
      case UnlockType.VIDEO:
        requestVideoURL(id);
        break;
      case UnlockType.AUDIO:
        playAudio(id);
        break;
      case UnlockType.IMAGE:
        showImage(id);
        break;
    }
  }

  private void handleGetVideoURL(GetVideoUrlResponse response) {
    getLoaderManager().destroyLoader(LOADER_ID_GET_VIDEO_URL);
    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
      String url = response.getURL();
      Intent intent = new Intent(getActivity(), VideoViewActivity.class);
      intent.putExtra(VideoViewActivity.INTENT_URL, url);
      startActivity(intent);
    } else if (code == Response.VIDEO_BEING_PROCESSED) {
      showDialogVideoBeingProcessed();
    } else {
      Toast.makeText(getActivity(), R.string.can_not_play_file_not_found,
          Toast.LENGTH_LONG).show();
    }
  }

  /**
   * show dialog when upload video but not ready to use
   */
  private void showDialogVideoBeingProcessed() {
    Builder builder = new CenterButtonDialogBuilder(mMainActivity, false);
    builder.setMessage(R.string.video_being_processed);
    builder.setPositiveButton(R.string.ok, null);
    builder.show();
  }

  private void handleCheckCall(boolean isVideo, CheckCallResponse response) {
    if (isVideo) {
      getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VIDEO);
    } else {
      getLoaderManager().destroyLoader(LOADER_ID_CHECK_CALL_VOICE);
    }

    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      if (isVideo) {
        if (LinphoneVideoCall.instance == null) {
          LinphoneVideoCall.startOutGoingCall(getActivity(),
              callUserInfo);
        }
      } else {
        if (LinphoneVoiceCall.instance == null) {
          LinphoneVoiceCall.startOutGoingCall(getActivity(),
              callUserInfo);
        }
      }
    } else if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
      if (isVideo) {
        NotEnoughPointDialog.showForVideoCall(getActivity(),
            response.getPoint());

        // CUONGNV01032016 : Remove show dialog het point
//                Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//                intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_VIDEO_CALL);
//                startActivity(intent);

        NotEnoughPointDialog.showForVideoCall(getActivity(), response.getPoint());
      } else {
        NotEnoughPointDialog.showForVoiceCall(getActivity(),
            response.getPoint());

        // CUONGNV01032016 : Remove show dialog het point
//                Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//                intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_VOICE_CALL);
//                startActivity(intent);

        NotEnoughPointDialog.showForVoiceCall(getActivity(), response.getPoint());
      }
    } else if (code == Response.SERVER_RECEIVER_NOT_ENOUGH_MONEY) {
      NotEnoughPointDialog.showForCallRecever(getActivity());
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_HISTORY) {
      return new HistoryResponse(mAppContext, data, mFriend.getId());
    } else if (loaderID == LOADER_ID_CHECK_NEW_MESSAGE) {
      return new GetNewChatMessageResponse(mAppContext, data,
          mFriend.getId());
    } else if (loaderID == LOADER_ID_MARK_AS_READ) {
      return new MarkReadsResponse(data);
    } else if (loaderID == LOADER_ID_ADD_BLOCK_USER) {
      return new AddBlockUserResponse(data);
    } else if (loaderID == LOADER_ID_REMOVE_BLOCK_USER) {
      return new RemoveBlockUserResponse(data);
    } else if (loaderID == LOADER_ID_REPORT_USER) {
      return new ReportResponse(data);
    } else if (loaderID == LOADER_ID_ADD_TO_FAVORITES) {
      return new AddFavoriteResponse(data);
    } else if (loaderID == LOADER_ID_GET_BASE_USER_INFO) {
      return new GetBasicInfoResponse(mAppContext, data);
    } else if (loaderID == LOADER_ID_GET_POINT) {
      return new GetPointResponse(data);
    } else if (loaderID == LOADER_ID_REMOVE_FROM_FAVORITES) {
      return new RemoveFavoriteResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_CALL_VIDEO
        || loaderID == LOADER_ID_CHECK_CALL_VOICE) {
      return new CheckCallResponse(data);
    } else if (loaderID == LOADER_ID_GET_VIDEO_URL) {
      return new GetVideoUrlResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_UNLOCK) {
      return new CheckUnlockResponse(data);
    } else if (loaderID == LOADER_ID_UNLOCK) {
      return new UnlockResponse(data);
    } else if (loaderID == LOADER_ID_BASIC_USER_INFO_CALL) {
      return new GetBasicInfoResponse(mAppContext, data);
    }

    return null;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onNavigationLeftClick(View view) {
    // super.onNavigationLeftClick(view);
    hideKeyboard();
    int count = getFragmentManager().getBackStackEntryCount();
    String name = null;
    if (count > 2) {
      name = getFragmentManager().getBackStackEntryAt(count - 2)
          .getName();
      if (name.equalsIgnoreCase(MyProfileFragment.TAG_FRAGMENT_USER_PROFILE)) {
        LogUtils.e("chat", "popBackStack");
        getFragmentManager().popBackStack(
            MyProfileFragment.TAG_FRAGMENT_USER_PROFILE, 0);
      } else {
        super.onNavigationLeftClick(view);
      }
    } else {
      super.onNavigationLeftClick(view);
    }
  }

  public void dismissDialog() {
    if (mAlertDialog != null && mAlertDialog.isShowing())
      mAlertDialog.dismiss();
  }


  @Override
  public void onNavigationRightClick(View view) {
    super.onNavigationRightClick(view);
    getSlidingMenu().showSecondaryMenu();
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  @Override
  public void onDestroyView() {
    dismissUploading();
    super.onDestroyView();
    LogUtils.i(TAG, "onDestroyView()...is called...");
    dismissUploading();


    setEnableSlidingMenu(true);
    stopPlayAudio();
    // Must place removeUserIdToSend here (onDetach() called after onStart()
    // of another chatFragment)
    UserPreferences.getInstance().removeCurentFriendChat();

    if (mDialogChatWithUserMore != null
        && mDialogChatWithUserMore.isShowing()) {
      mDialogChatWithUserMore.dismiss();
    }
//    if (mAlertDialog != null && mAlertDialog.isShowing()) {
//      mAlertDialog.dismiss();
//    }
    dismissDialog();

    if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
      mConfirmDialog.dismiss();
    }
    if (mLimitTimeDialog != null && mLimitTimeDialog.isShowing()) {
      mLimitTimeDialog.dismiss();
    }
    if (mProgressDialogDownload != null) {
      mProgressDialogDownload.dismiss();
    }
    if (mErrorDialog != null) {
      mErrorDialog.dismiss();
    }

    ChatMessagePreference chatMessagePreference = ChatMessagePreference.getInstance();
    String userId = mFriend.getId();
    String msg = mEdtContent.getText().toString();
    if (msg.replace("\u3000", " ").trim().length() == 0) {
      chatMessagePreference.removeMessage(userId);
    } else {
      String htmlWithEmojiImgTagMsg = Html.toHtml(mEdtContent.getText());
      htmlWithEmojiImgTagMsg = htmlWithEmojiImgTagMsg.replaceAll("&#12288;", "&nbsp;");
      // Parse EMOJI IMG tag to EMOJI code
      String htmlWithEmojiCodeMsg = EmojiUtils.convertEmojiToCode(htmlWithEmojiImgTagMsg);
      // Remove all HTML tag in message
      String content = Html.fromHtml(htmlWithEmojiCodeMsg).toString().trim();
      chatMessagePreference.saveMessage(userId, content);
    }

    // need unbindOnStop
    mMainActivity.setUnbindChatOnStop(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      mLayoutMain.getViewTreeObserver().removeOnGlobalLayoutListener(
          mOnGlobalLayoutListener);
    } else {
      mLayoutMain.getViewTreeObserver().removeGlobalOnLayoutListener(
          mOnGlobalLayoutListener);
    }
    mLayoutMain.removeDispatchListener();

    if (mScreenReceiver != null) {
      getActivity().unregisterReceiver(mScreenReceiver);
    }
  }

  private void handleBlockUserResponse(AddBlockUserResponse response) {
    getLoaderManager().destroyLoader(LOADER_ID_ADD_BLOCK_USER);
    String fromId = UserPreferences.getInstance().getUserId();
    String toId = mFriend.getId();
    BlockUserPreferences.getInstance().insertBlockedUser(toId);
    sendBlockMessage(fromId, toId);

    // Send LocalBroadcast
    Intent intent = new Intent(AccountStatus.ACTION_BLOCKED);
    intent.putExtra(AccountStatus.EXTRA_DATA, mFriend.getId());
    Utility.sendLocalBroadcast(mContext, intent);

    int friends = response.getFriendsNum();
    int favorites = response.getFavouriteFriendsNum();
    UserPreferences.getInstance().saveNumberConnection(friends, favorites);

    exitMeWhenBlocked();
  }

  private void handleUnblockUserResponse(RemoveBlockUserResponse response) {
    getLoaderManager().destroyLoader(LOADER_ID_REMOVE_BLOCK_USER);
    String fromId = UserPreferences.getInstance().getUserId();
    String toId = mFriend.getId();
    BlockUserPreferences.getInstance().removeBlockedUser(toId);

    int friends = response.getFriendsNum();
    int favorites = response.getFavouriteFriendsNum();
    UserPreferences.getInstance().saveNumberConnection(friends, favorites);

    sendUnblockMessage(fromId, toId);
  }

  private void handleAddFavorite() {
    mNewAddFavoriteRequest = false;
    String userName = mFriend.getName();
    FavouritedPrefers.getInstance().saveFav(mFriend.getId());

    UserPreferences userPreferences = UserPreferences.getInstance();
    int numberFavorite = userPreferences.getNumberFavorite();
    numberFavorite++;
    userPreferences.saveNumberFavorite(numberFavorite);

    String title = getString(R.string.profile_add_to_favorites_title);
    String msg = String.format(
        getString(R.string.profile_add_to_favorites_message), userName);
    mConfirmDialog = new CustomConfirmDialog(getActivity(), title, msg,
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            showGiveGiftFragment();
          }
        })
        .setNegativeButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    mConfirmDialog.setOnCancelListener(null);
    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void handleRemoveFavorite() {
    getLoaderManager().destroyLoader(LOADER_ID_REMOVE_FROM_FAVORITES);
    String userName = mFriend.getName();
    FavouritedPrefers.getInstance().removeFav(mFriend.getId());
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.decreaseFavorite();

    String title = getString(R.string.profile_remove_from_favorites_title);
    String msg = String.format(
        getString(R.string.profile_remove_from_favorites_message),
        userName);

    mConfirmDialog = new CustomConfirmDialog(getActivity(), title, msg, false)
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

  private void sendBlockMessage(final String from, final String to) {
    if (mMainActivity.getChatService() == null) {
      return;
    }
    ChatManager chatManager = mMainActivity.getChatService()
        .getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendBlockMessage(from, to);
  }

  private void sendUnblockMessage(final String from, final String to) {
    if (mMainActivity.getChatService() == null) {
      return;
    }
    ChatManager chatManager = mMainActivity.getChatService()
        .getChatManager();
    if (chatManager == null) {
      return;
    }
    chatManager.sendUnblockMessage(from, to);
  }

  private void executeBlockUser() {
    LogUtils.d(TAG, "executeBlockUser Started");

    UserPreferences userPreferences = UserPreferences.getInstance();
    if (userPreferences.getInRecordingProcess()) {
      LogUtils.d(TAG, "executeBlockUser Ended (1)");
      return;
    }

    String title = "";
    String message = "";

    title = getString(R.string.chat_screen_block_dialog_title);
    message = String.format(
        getString(R.string.chat_screen_block_dialog_message),
        mFriend.getName());

    mConfirmDialog = new CustomConfirmDialog(getActivity(), title, message,
        true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            UserPreferences userPreferences = UserPreferences.getInstance();
            String token = userPreferences.getToken();
            String userToSend = userPreferences.getCurentFriendChat();
            AddBlockUserRequest abur = new AddBlockUserRequest(token,
                userToSend);
            restartRequestServer(LOADER_ID_ADD_BLOCK_USER, abur);
          }
        })
        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
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
    LogUtils.d(TAG, "executeBlockUser Ended (2)");
  }

  private void executeReportUser() {
    LogUtils.d(TAG, "executeReportUser Started");

    if (UserPreferences.getInstance().getInRecordingProcess()) {
      LogUtils.d(TAG, "executeReportUser Ended (1)");
      return;
    }

    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = getResources();
    Builder builder = new Builder(getActivity());
    String title = "";
    String[] items = null;

    title = resource.getString(R.string.dialog_confirm_report_user_title);
    items = resource.getStringArray(R.array.report_user_type);

    // builder.setTitle(title);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        android.R.layout.select_dialog_item, items);

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        LogUtils.d(TAG, "onClick Started");

        LogUtils.d(TAG,
            String.format("executeReportUser: which = %d", which));

        if (which > 0) {
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
          String token = UserPreferences.getInstance().getToken();
          String subject_id = UserPreferences.getInstance()
              .getCurentFriendChat();
          ReportRequest reportRequest = new ReportRequest(token,
              subject_id, reportType, Constants.REPORT_TYPE_USER);
          restartRequestServer(LOADER_ID_REPORT_USER, reportRequest);
        }

        LogUtils.d(TAG, "onClick Ended");
      }
    });
    mAlertDialog = builder.create();
    mAlertDialog.show();

    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "executeReportUser Ended (2)");
  }

  private void handleReportUserResponse() {
    getLoaderManager().destroyLoader(LOADER_ID_REPORT_USER);
    // Show confirm dialog
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = getResources();
    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    String title = "";
    String message = "";
    title = resource.getString(R.string.dialog_confirm_report_user_title);
    message = resource
        .getString(R.string.dialog_confirm_report_user_content);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
      }
    };
    builder.setPositiveButton(R.string.common_ok, clickListener);
    mAlertDialog = builder.create();
    mAlertDialog.show();

    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LogUtils.d(TAG, "onActivityResult is calling..." + com.application.util.Utils.dumpIntent(data));
    switch (resultCode) {
      case Activity.RESULT_OK:
        if (data == null) {
          LayoutInflater inflater = LayoutInflater.from(getActivity());
          View customTitle = inflater.inflate(R.layout.dialog_customize, null);

          Builder builder = new Builder(getActivity());
          ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
              .setText(R.string.dialog_error_not_found_gallery_title);
          builder.setCustomTitle(customTitle);
          //builder.setTitle(R.string.dialog_error_not_found_gallery_title);
          builder.setMessage(R.string.dialog_error_not_found_gallery_content);
          builder.setNegativeButton(R.string.common_ok, null);
          AlertDialog element = builder.show();

          int dividerId = element.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = element.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(getResources().getColor(R.color.transparent));
          }

          return;
        }
        if (hasShowDialogValidate())
          return;
        switch (requestCode) {
          case Camera.REQUEST_CODE_CAMERA:
          case Gallery.REQUEST_CODE_GALLERY:
            Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
            for (Parcelable parcelable : files) {
              MediaFile file = (MediaFile) parcelable;
              if (hasShowDialogCheckMB(file.getPath()))
                return;

              if (Utils.isPhoto(file.getMineType())) {
                requestPhoto(file.getPath());
              } else {
                requestVideo(file.getPath());
              }
            }
            break;

//                    case REQUEST_PHOTO: {
//                        ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//                        if (mMediaSelectedList != null) {
//                            for (final MediaItem mediaItem : mMediaSelectedList) {
//                                AlertDialog confirmDialog = new CustomConfirmDialog(getActivity(), getString(R.string.confirm), getString(R.string.confirm_send_previous_photo_msg), true)
//                                        .setPositiveButton(0, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                String originalPath = mediaItem.getOriginPath();
//                                                String croppedPath = mediaItem.getOriginPath();
//                                                String myID = mMe.getId();
//                                                String friendID = mFriend.getId();
//                                                if (!TextUtils.isEmpty(croppedPath)) {
//                                                    sendPhoto(myID, friendID, new File(croppedPath));
//                                                } else if (!TextUtils.isEmpty(originalPath)) {
//                                                    sendPhoto(myID, friendID, new File(originalPath));
//                                                }
//                                            }
//                                        })
//                                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        })
//                                        .create();
//                                confirmDialog.setCancelable(false);
//                                confirmDialog.show();
//
//                                int dividerId = confirmDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//                                View divider = confirmDialog.findViewById(dividerId);
//                                if (divider != null) {
//                                    divider.setBackgroundColor(confirmDialog.getContext().getResources().getColor(R.color.transparent));
//                                }
//                            }
//                        } else {
//                            LogUtils.e(TAG, "Error to get media, NULL");
//                        }
//                    }
//                    break;
//                    case REQUEST_VIDEO: {
//                        ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity
//                                .getMediaItemSelected(data);
//                        if (mMediaSelectedList != null) {
//                            for (MediaItem mediaItem : mMediaSelectedList) {
//                                String videoPath = mediaItem.getOriginPath();
//                                Uri videoUri = UriCompat.fromFile(getContext(), mediaItem.getOriginPath());
//                                int duration = Utility.getVideoDuration(mAppContext, videoUri, videoPath);
//                                if (duration <= 0 || duration >= (Config.TIME_LIMIT_VIDEO + 1) * 1000) {
//                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
//                                    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
//
//                                    Builder builder = new Builder(getActivity());
//                                    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(R.string.dialog_error_video_record_limit_time_title);
//                                    builder.setCustomTitle(customTitle);
//                                    //builder.setTitle(R.string.dialog_error_video_record_limit_time_title);
//                                    builder.setMessage(R.string.dialog_error_video_record_limit_time_content);
//                                    builder.setNegativeButton(R.string.common_ok, null);
//                                    AlertDialog element = builder.show();
//
//                                    int dividerId = element.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//                                    View divider = element.findViewById(dividerId);
//                                    if (divider != null) {
//                                        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
//                                    }
//                                } else {
//                                    sendVideo(mMe.getId(), mFriend.getId(), new File(videoPath));
//                                }
//                            }
//                        } else {
//                            LogUtils.e(TAG, "Error to get media, NULL");
//                        }
//                        break;
//                    }
          case REQUEST_IMAGE_ID:
            String imageId = data.getStringExtra(GalleryActivity.GALLERY_IMAGE_ID);
            LogUtils.d(TAG, "Image Id received=" + imageId);
            sendPreviousPhoto(imageId);
            break;
        }
        break;
    }
  }

  private void requestVideo(@NonNull final String videoPath) {
    File file = new File(videoPath);
    Uri videoUri = Utils.getFileUri(getContext(), file);
    int duration = Utility.getVideoDuration(mAppContext, videoUri, videoPath);
    Log.e("ThangPham1","duration: "+duration);
    if (duration < 1000 || duration >= (Config.TIME_LIMIT_VIDEO + 1) * 1000) {
      // TODO: 8/15/18  #14052 should show message to user, unfortunately no feedback for that after ask many people
    } else {
      if (com.application.util.Utils.isOnline(mContext)) {
        // show confirm dialog before send video
        AlertDialog confirmDialog = new CustomConfirmDialog(getActivity(),
            getString(R.string.confirm),
            getString(R.string.confirm_send_previous_photo_msg), true)
            .setPositiveButton(R.string.save_dialog_confirm_positive,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    String myID = mMe.getId();
                    String friendID = mFriend.getId();
                    sendVideo(myID, friendID, new File(videoPath));
                  }
                })
            .setNegativeButton(R.string.save_dialog_confirm_negative,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                })
            .create();
        confirmDialog.setCancelable(false);
        confirmDialog.show();

        int dividerId = confirmDialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = confirmDialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(
              confirmDialog.getContext().getResources().getColor(R.color.transparent));
        }
      } else {
        notifyErrorSendVideo(new File(videoPath));
      }
    }
  }

  private void notifyErrorSendVideo(final File filePath) {
    AlertDialog confirmDialog = new CustomConfirmDialog(getActivity(),
        getString(R.string.confirm_send_video_error),
        getString(R.string.confirm_send_previous_video_msg), true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            sendVideo(mMe.getId(), mFriend.getId(), filePath);
          }
        })
        .create();
    confirmDialog.setCancelable(false);
    confirmDialog.show();

    int dividerId = confirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void requestPhoto(@NonNull final String filePath) {
    AlertDialog confirmDialog = new CustomConfirmDialog(getActivity(), getString(R.string.confirm),
        getString(R.string.confirm_send_previous_photo_msg), true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String myID = mMe.getId();
            String friendID = mFriend.getId();
            sendPhoto(myID, friendID, new File(filePath));
          }
        })
        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    confirmDialog.setCancelable(false);
    confirmDialog.show();

    int dividerId = confirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void startPlayVideo(String path) {
    if (TextUtils.isEmpty(path)) {
      return;
    }
    mMainActivity.setUnbindChatOnStop(true);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    File file = new File(path);
    intent.setDataAndType(UriCompat.fromFile(getContext(), file), "video/*");
    boolean avaiable = IntentUtils.isIntentAvailable(mAppContext, intent);
    if (avaiable) {
      mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
      startActivity(intent);
    } else {
      Toast.makeText(mAppContext, R.string.not_found_app_play_video,
          Toast.LENGTH_LONG).show();
    }
  }

  // ================Start HoanDC===========================
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (v.getId() == R.id.fragment_chat_btnRecord) {
      SlidingMenu slidingMenu = getSlidingMenu();
      float y = event.getY();
      switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
          isTouchRecordAllowed = true;
          if (!Utility.isSDCardExist()) {
            // only alert mount sdcard when touch down
            Toast.makeText(mContext,
                R.string.must_have_sdcard_to_record,
                Toast.LENGTH_LONG).show();
            isTouchRecordAllowed = false;
            break;
          }
          stopPlayAudio();
          mChatAdapter.clearStatusAdioPlay();
          if (slidingMenu != null) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            slidingMenu.setSlidingEnabled(false);
          }
          setEnableUIRecord(false);
          showRecordView();
          mChatAdapter.setEnableView(false);
          break;
        case MotionEvent.ACTION_MOVE:
          if (!isTouchRecordAllowed) {
            break;
          }
          if (y > -50) {
            changeTextMove(true);
            isDragToDelete = false;
            isSendAudio = true;
          } else {
            changeTextMove(false);
            isDragToDelete = true;
            isSendAudio = false;
          }
          break;
        case MotionEvent.ACTION_UP:
          if (!isTouchRecordAllowed) {
            break;
          }
          if (slidingMenu != null) {
            slidingMenu
                .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            slidingMenu.setSlidingEnabled(true);
          }
          setEnableUIRecord(true);
          mChatAdapter.setEnableView(true);
          if (mRelativeLayoutRecorder != null) {
            if (mRelativeLayoutRecorder.getVisibility() == View.VISIBLE) {
              mRelativeLayoutRecorder.setVisibility(View.GONE);
              changeTextMove(false);
            }
          }
          if (mBtnRecord != null) {
            try {
              mBtnRecord.setText(mContext.getResources().getString(
                  R.string.recorder_hold_to_talk));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

          if (isDragToDelete) {
            if (mFilePath != null) {
              File file = new File(mFilePath);
              if (file.exists()) {
                file.delete();
              }
            }
            isSendAudio = false;
          }
          if (mRecMicToMp3.isRecording()) {
            stopRecordAudio();
          }

          mHandlerRecord.removeCallbacks(mRunnableRecord);
          break;
      }
    }

    if (v.getId() == R.id.ib_chat_freezed_layer) {
      hideChatMoreOptions();
    }

    if (v.getId() == R.id.fragment_chat_edt_content) {
      hideChatMoreOptions();
      return false;
    }
    return true;
  }

  private void showRecordView() {
    isSendAudio = true;
    changeTextMove(true);
    mRelativeLayoutRecorder.setVisibility(View.VISIBLE);
    isDragToDelete = false;
    if (mHandlerRecord == null) {
      mHandlerRecord = new Handler();
    } else {
      mHandlerRecord.removeCallbacks(mRunnableRecord);
    }

    if (mRunnableRecord == null) {
      mRunnableRecord = new Runnable() {

        @Override
        public void run() {
          startRecordAudio();
        }
      };
    }
    mTvRecorderTime.setText("0:0");
    audioRecordVisualizer.clear();
    audioRecordVisualizer.invalidate();
    mHandlerRecord.postDelayed(mRunnableRecord, RECORD_DELAY_TIME);
  }

  private void startRecordAudio() {
    // Set recording status is true
    UserPreferences.getInstance().setInRecordingProcess(true);

    mFilePath = StorageUtil.getAudioRecord(mContext).getAbsolutePath();
    mRecMicToMp3.setFilePath(mFilePath);
    mRecMicToMp3.start();
  }

  private void sendRecoredAudio() {
    if (!isSendAudio) {
      return;
    }

    isSendAudio = false;
    final File fileSource = new File(mFilePath);
    long fileSize = fileSource.length();
    if (fileSize > 0) {
      if (getActivity() != null) {
        final long audioTime = Utility.getAudioDurationTimeLong(fileSource.getAbsolutePath());
        final int time = parseLongTimeToInt(audioTime);
        if (time > 0) {
          final Date date = Utility.getDateTimeInGMT();
          final String messageId = sendAudio(date, Utility.getTimeString(audioTime), fileSource);
          final String userId = mFriend.getId();
          final String fileName = String.valueOf(time);

          ChatService chatService = mMainActivity.getChatService();
          if (chatService != null) {
            chatService.sendAudio(messageId, date, userId, fileName, fileSource, audioTime);
          }
        }
      }
    } else {
      if (fileSource.exists()) {
        fileSource.delete();
      }
    }

  }

  private void stopRecordAudio() {
    if (mRecMicToMp3.isRecording()) {
      new FileObserver(mFilePath) {
        @Override
        public void onEvent(int event, String path) {
          if (event == FileObserver.CLOSE_WRITE) {
            sendRecoredAudio();
          }
        }
      }.startWatching();
      mRecMicToMp3.stop();
    } else {
      sendRecoredAudio();
    }

    // Set recording status is false
    UserPreferences.getInstance().setInRecordingProcess(false);

    SlidingMenu slidingMenu = getSlidingMenu();
    if (slidingMenu != null) {
      slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
      slidingMenu.setSlidingEnabled(true);
    }
    if (mHandlerRecord != null) {
      mHandlerRecord.removeCallbacks(mRunnableRecord);
    }
    if (mRelativeLayoutRecorder != null) {
      if (mRelativeLayoutRecorder.getVisibility() == View.VISIBLE) {
        mRelativeLayoutRecorder.setVisibility(View.GONE);
        changeTextMove(false);
      }
    }
    if (mBtnRecord != null) {
      mBtnRecord.setText(mContext.getResources().getString(
          R.string.recorder_hold_to_talk));
    }
    audioRecordVisualizer.clear();
    audioRecordVisualizer.invalidate();
  }

  private int parseLongTimeToInt(long audioTime) {
    int result = 0;
    result = (int) (audioTime) / 1000;
    if (audioTime % 1000 >= 500) {
      result++;
    }
    return result;
  }

  private void initialAudio() {
    mRecMicToMp3 = new RecMicToMp3Tamtd(8000);
  }

  // ================End HoanDC===========================
  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @SuppressWarnings("unused")
  private void showSubMenu() {
    if (mDialogChatWithUserMore == null) {
      mDialogChatWithUserMore = new Dialog(getActivity());
      mDialogChatWithUserMore.getWindow().requestFeature(
          Window.FEATURE_NO_TITLE);
      mDialogChatWithUserMore
          .setContentView(R.layout.dialog_chat_with_user_more);
      // mSelectCallTypeDialog.setCancelable(false);
      TextView profile = (TextView) mDialogChatWithUserMore
          .findViewById(R.id.tv_dialog_chat_with_user_more_profile);
      TextView voiceCall = (TextView) mDialogChatWithUserMore
          .findViewById(R.id.tv_dialog_chat_with_user_more_voice_call);
      TextView videoCall = (TextView) mDialogChatWithUserMore
          .findViewById(R.id.tv_dialog_chat_with_user_more_video_call);
      Button cancel = (Button) mDialogChatWithUserMore
          .findViewById(R.id.bt_dialog_chat_with_user_more_cancel);
      TextView block = (TextView) mDialogChatWithUserMore
          .findViewById(R.id.tv_dialog_chat_with_user_more_block);
      TextView report = (TextView) mDialogChatWithUserMore
          .findViewById(R.id.tv_dialog_chat_with_user_more_report);

      profile.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View arg0) {
          if (Utility.isBlockedWithUser(mContext, mFriend.getId())) {
            exitMeWhenBlocked();
            return;
          }

          mDialogChatWithUserMore.dismiss();

          mOnItemChatClickListener.onItemUserProfileClick();
        }
      });

      voiceCall.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View arg0) {
          if (Utility.isBlockedWithUser(mContext, mFriend.getId())) {
            exitMeWhenBlocked();
            return;
          }
          mDialogChatWithUserMore.dismiss();
          CallUserInfo userInfo = new CallUserInfo(mFriend.getName(),
              mFriend.getId(), mFriend.getAvatar(), mFriend
              .getGender());
          if (LinphoneVoiceCall.instance == null) {
            LinphoneVoiceCall.startOutGoingCall(mMainActivity,
                userInfo);
          }
        }
      });

      videoCall.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View arg0) {
          if (Utility.isBlockedWithUser(mContext, mFriend.getId())) {
            exitMeWhenBlocked();
            return;
          }

          mDialogChatWithUserMore.dismiss();
          CallUserInfo userInfo = new CallUserInfo(mFriend.getName(),
              mFriend.getId(), mFriend.getAvatar(), mFriend
              .getGender());
          LinphoneVideoCall
              .startOutGoingCall(mMainActivity, userInfo);
        }
      });

      block.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mDialogChatWithUserMore.dismiss();
          executeBlockUser();
        }
      });

      report.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mDialogChatWithUserMore.dismiss();
          executeReportUser();
        }
      });

      cancel.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mDialogChatWithUserMore.dismiss();
        }
      });
    }

    mDialogChatWithUserMore.show();
  }

  public void addVideoFileReceivedToList(long fileId) {
    if (fileId != -1) {
      if (mVideoFileReceivedList == null) {
        mVideoFileReceivedList = new ArrayList<Long>();
      }
      mVideoFileReceivedList.add(fileId);
    }
  }

  public void addPhotoFileReceivedToList(long fileId) {
    if (fileId != -1) {
      if (mPhotoFileReceivedList == null) {
        mPhotoFileReceivedList = new ArrayList<Long>();
      }
      mPhotoFileReceivedList.add(fileId);
    }
  }

  private boolean isContainReceivedPhotoFileList(long fileId) {
    if (mPhotoFileReceivedList == null) {
      return false;
    }
    return mPhotoFileReceivedList.contains(fileId);
  }

  private boolean isContainReceivedVideoFileList(long fileId) {
    if (mVideoFileReceivedList == null) {
      return false;
    }
    return mVideoFileReceivedList.contains(fileId);
  }

  private String getPathFileDownloaded(long fileId) {
    DownloadManager.Query query = new Query();
    query.setFilterById(fileId);
    DownloadManager downloadManager = (DownloadManager) mContext
        .getApplicationContext().getSystemService(
            Context.DOWNLOAD_SERVICE);
    Cursor cursor = downloadManager.query(query);
    if (cursor != null) {
      cursor.moveToFirst();
      try {
        Uri uri = Uri.parse(cursor.getString(cursor
            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
        return uri.getPath();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      cursor.close();
    }
    return null;
  }

  public void addAudioFileReceivedToList(String messageId, long idDownload) {
    if (idDownload == -1) {
      return;
    }
    if (mAudioFileReceivedList == null) {
      mAudioFileReceivedList = new HashMap<String, Long>();
    }
    mAudioFileReceivedList.put(messageId, idDownload);
  }

  private String getChatMessageIdFromDownloadId(long downloadId) {
    if (mAudioFileReceivedList == null) {
      return null;
    }
    for (Map.Entry<String, Long> entry : mAudioFileReceivedList.entrySet()) {
      if (entry.getValue() == downloadId) {
        return entry.getKey();
      }
    }
    return null;
  }

  private void updateAudioMessage(long downloadId) {
    if (mChatAdapter == null || mChatAdapter.getListChatMessage() == null) {
      return;
    }
    LogUtils.v(TAG, "update audio message with downloadId=" + downloadId);
    String chatMessageId = getChatMessageIdFromDownloadId(downloadId);
    for (ChatMessage message : mChatAdapter.getListChatMessage()) {
      if (message.getMessageId().equals(chatMessageId)
          && message.getFileMessage() != null) {
        message.getFileMessage().setFilePath(
            getPathFileDownloaded(downloadId));
        mChatAdapter.notifyDataSetChanged();
        mChatAdapter.startPlayAudio(message);
        break;
      }
    }
  }

  @Override
  protected String getUserIdTracking() {
    return mFriend.getId();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    hideKeyboard();
  }

  private void switchToLoading() {
    mLayoutLoading.setVisibility(View.VISIBLE);
    mLayoutData.setVisibility(View.INVISIBLE);
    mtxtStatus.setText(R.string.common_loading);
  }

  private void switchToData() {
    mLayoutLoading.setVisibility(View.GONE);
    mLayoutData.setVisibility(View.VISIBLE);
  }

  private void switchToError(String string) {
    mtxtStatus.setText(string);
  }

  private void setHeightChatBoxSpace(int heightChatBoxSpace) {
    mChatBoxSpace
        .setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightChatBoxSpace));
    Preferences.getInstance().saveKeyboardHeight(heightChatBoxSpace);
  }

  private void showPanel() {
    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    mChatBoxSpace.setVisibility(View.VISIBLE);
    popupWindow.setHeight(mKeyboardHeight);
    Rect rect = new Rect();
    getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
    int winHeight = getActivity().getWindow().getDecorView().getHeight();
    popupWindow.showAtLocation(mLayoutMain, Gravity.BOTTOM, 0, winHeight - rect.bottom);
    mTableRowChat.setVisibility(View.VISIBLE);
    mTableRowRecord.setVisibility(View.GONE);
    isShowEmoji = true;
  }

  public void hidePanel() {
    popupWindow.dismiss();
    previewStickerView.setVisibility(View.GONE);
    mbtnAddMedia.setImageResource(R.drawable.ic_emoji_sticker);
    ivAdd.setImageResource(R.drawable.ic_chat_more);
    isShowEmoji = false;
  }

  private void clickMediaPanel(View view) {
    mEdtContent.requestFocus();
    View pannel = getPanelToShow(view);
//        if (pannel.getHeight() > 0)
    Log.w(TAG, "clickMediaPanel --> " + pannel.getHeight());
    if (popupWindow.isShowing()) {
      View mediaShowed = getPanelMediaShowed();
      if (pannel.getId() != mediaShowed.getId()) {
        showPanelMedia(pannel);
      } else {
        hidePanel();
        showKeyboard();
      }
    } else {
      showPanelMedia(pannel);
      if (isShowKb) {
        hideSoftKeyboard();
        isShowPanelNext = true;
      } else {
        showPanel();
      }
    }
    hideChatMoreOptions();
    gotoEndListChat();
  }

  private void hideSoftKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mEdtContent.getWindowToken(), 0);

  }

  private void showKeyboard() {
    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    mChatBoxSpace.setVisibility(View.GONE);
    isShowKbNext = true;
    mKeyboardHandler = Utility.showDelayKeyboard(mEdtContent, 100);

  }

  public void hideKeyboard() {
    mEdtContent.clearFocus();
    mLayoutMain.requestFocus();
    hideSoftKeyboard();
  }

  private View getPanelMediaShowed() {
    return mPanelMedia.getVisibility() == View.VISIBLE ? mPanelMedia
        : mPannelMediaFile;
  }

  private View getPanelToShow(View view) {
    if (view.getId() == mbtnAddMedia.getId()) {
      return mPanelMedia;
    } else if (view.getId() == ivAdd.getId()) {
      return mPannelMediaFile;
    }
    return null;
  }

  private void showPanelMedia(final View view) {
    if (view == null) {
      return;
    }

    view.setVisibility(View.VISIBLE);
    if (view.getId() == mPanelMedia.getId()) {

      mbtnAddMedia.setImageResource(R.drawable.ic_keybroad);
      ivAdd.setImageResource(R.drawable.ic_chat_more);

      mPannelMediaFile.setVisibility(View.GONE);
    } else if (view.getId() == mPannelMediaFile.getId()) {

      mbtnAddMedia.setImageResource(R.drawable.ic_emoji_sticker);
      ivAdd.setImageResource(R.drawable.ic_keybroad);

      mPanelMedia.setVisibility(View.GONE);
    }
    view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {

        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int winHeight = view.getHeight();

        LogUtils.e(TAG,
            "-->winHeight=" + winHeight + "|rect.height=" + rect.height() + "|view.getVisibility()="
                + view.getVisibility());
      }
    });

  }

  public ListView getListChat() {
    return mListChat;
  }

  public String getUserIdToSend() {
    return mFriend.getId();
  }

  public String getCurrentUserId() {
    return mMe.getId();
  }

  public String getAvatarToSend() {
    return mFriend.getAvatar();
  }

  public String getAvatar() {
    return mMe.getAvatar();
  }

  public int getGenderToSend() {
    return mFriend.getGender();
  }

  public int getGender() {
    return mMe.getGender();
  }

  public String getNameUserToSend() {
    return mFriend.getName();
  }

  public String getUserName() {
    return mMe.getName();
  }

  public void startAdapterPlayAudio(ChatMessage chatMessage) {
    if (hasShowNetworkNotConnected(true))
      return;


    TimeAudioHold timeHold = chatMessage.getFileMessage()
        .getTimeAudioHold();
    int seekTime = timeHold.getCurrentPosition();
    if (seekTime == 0 && !chatMessage.isOwn()) {
      requestCheckUnlock(UnlockType.AUDIO, chatMessage.getFileMessage()
          .getFileId());
    } else {
      mChatAdapter.startPlayAudio(chatMessage);
    }
  }

  public void stopAdapterPlayAudio(ChatMessage chatMessage) {
    mChatAdapter.stopPlayAudio(chatMessage);
  }

  public OnItemChatClickListener getItemChatClickListener() {
    return mOnItemChatClickListener;
  }

  @Override
  public void onPending(long uploadId, IUploadResource uploadResource) {
    LogUtils.i(TAG, String.format("onPending uploadId=%s", uploadId));

  }

  @Override
  public void onInprogress(long uploadId, IUploadResource uploadResource,
      int progress) {
    if (dialogUploadProgress == null) {
      dialogUploadProgress = new DialogUploadProgress();
      dialogUploadProgress.show(mMainActivity.getSupportFragmentManager(), DialogUploadProgress.TAG);
    }
    LogUtils.d(TAG, String.format("onInprogress uploadId=%s, progress=%d",
        uploadId, progress));
    String messageId = mChatUploadManager.getMessageId(uploadId);
    mChatAdapter.updateUploadState(messageId,
        com.application.uploadmanager.UploadState.RUNNING, progress);
  }

  @Override
  public void onSuccess(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    dismissUploading();

    LogUtils.d(TAG, String.format("onSuccess uploadId=%s", uploadId));
    String messageId = mChatUploadManager.getMessageId(uploadId);
    if (response instanceof UploadResponse) {
      String fileId = ((UploadResponse) response).getFileId();
      if (fileId != null && fileId.length() > 0) {
        mChatAdapter.updateFileId(messageId, fileId);
      }
    }
    mChatAdapter.updateUploadState(messageId,
        com.application.uploadmanager.UploadState.SUCCESSFUL, 100);
  }

  @Override
  public void onFailed(long uploadId, IUploadResource uploadResource,
      int responseCode, Object response) {
    LogUtils.i(TAG, String.format("onFailed uploadId=%s", uploadId));
    String messageId = mChatUploadManager.getMessageId(uploadId);
    mChatAdapter.updateUploadState(messageId,
        com.application.uploadmanager.UploadState.FAILED, 100);
  }

  @Override
  public void onCancel(long uploadId) {
    dismissUploading();
    String messageId = mChatUploadManager.getMessageId(uploadId);
    mChatAdapter.updateUploadState(messageId,
        com.application.uploadmanager.UploadState.CANCEL, 100);
  }

  private DialogUploadProgress dialogUploadProgress;

  //=================================================================//
  //=========================== Upload service ======================//
  //=================================================================//
  @MainThread
  private void showUploading() {
    mMainActivity.runOnUiThread(() -> {
      if (dialogUploadProgress == null)
        dialogUploadProgress = new DialogUploadProgress();
      dialogUploadProgress.show(mMainActivity.getSupportFragmentManager(), DialogUploadProgress.TAG);
    });

  }

  @MainThread
  private void dismissUploading() {
    if (dialogUploadProgress != null) {
      dialogUploadProgress.dismissAllowingStateLoss();
      dialogUploadProgress = null;
    }
  }

  @Override
  public void onAdded(long uploadId, IUploadResource uploadResource) {
    showUploading();
    // do nothing
  }

  @Override
  public void onPaused(long uploadId, IUploadResource uploadResource,
      int responseCode) {

    dismissUploading();


    // do nothing rightnow
  }

  public ChatDownloadManager getChatDownloadManager() {
    return mChatDownloadManager;
  }

  public IDownloadManager getDownloadManager() {
    return mIDownloadManager;
  }

  public void onDownloadStarted(long downloadId) {
    mWaitingDownload = true;
    mDownloadId = downloadId;
    mIDownloadManager.appendDownloadId(downloadId);
    mProgressDialogDownload = new ProgressDialog(getActivity());
    mProgressDialogDownload
        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialogDownload.setMax(100);
    mProgressDialogDownload.setTitle(R.string.download_file);
    mProgressDialogDownload.setCancelable(false);
    mProgressDialogDownload.show();
  }

  @Override
  public void onDownloadPending(long downloadId, int progress) {
    if (mProgressDialogDownload != null) {
      mProgressDialogDownload.setIndeterminate(true);
    }
  }

  @Override
  public void onDownloadRunning(long downloadId, int progress) {
    if (mProgressDialogDownload != null) {
      mProgressDialogDownload.setProgress(progress);
    }
  }

  @Override
  public void onDownloadPaused(long downloadId, int progress) {
  }

  @Override
  public void onDownloadSuccessful(long downloadId, Uri fileUri) {
    mWaitingDownload = false;
    mDownloadId = -1;
    if (mProgressDialogDownload != null) {
      mProgressDialogDownload.dismiss();
    }
    DownloadFileTempPrefers prefers = new DownloadFileTempPrefers();
    if (fileUri != null) {
      String path = fileUri.getPath();
      String messageId = prefers.getMessageId(downloadId + "");
      mChatAdapter.updateDownloadSuccessState(messageId, path);
    }
    if (isContainReceivedVideoFileList(downloadId)) {
      startPlayVideo(getPathFileDownloaded(downloadId));
    } else if (isContainReceivedPhotoFileList(downloadId)) {
      String fileId = prefers.getFileId(downloadId + "");
      String filePath = getPathFileDownloaded(downloadId);
      String userId = prefers.getUserId(downloadId + "");
      ChatDetailPictureActivity.startChatDetailPicture(getActivity(),
          fileId, filePath, userId);
    } else {
      updateAudioMessage(downloadId);
    }
    mIDownloadManager.clearDownloadIds();
  }

  @Override
  public void onDownloadFailed(long downloadId, int progress, int reason) {
    mWaitingDownload = false;
    mDownloadId = -1;
    mProgressDialogDownload.dismiss();
    showDialogDownloadFileFailed();
    mIDownloadManager.clearDownloadIds();
  }

  private void bindDownloadProgress() {
    mIDownloadManager.registerDownloadProgressChange(this);
    if (mProgressDialogDownload == null) {
      return;
    }
    if (!mWaitingDownload) {
      return;
    }
    int state = mIDownloadManager.getState(mDownloadId);
    if (state != DownloadState.SUCCESSFUL && state != DownloadState.UNKNOW
        && state != DownloadState.FAILED) {
      int progress = mIDownloadManager.getProgress(mDownloadId);
      mProgressDialogDownload.setProgress(progress);
    } else if (state == DownloadState.FAILED) {
      mProgressDialogDownload.dismiss();
      showDialogDownloadFileFailed();
      mIDownloadManager.removeDownloadId(mDownloadId);
    } else {
      mProgressDialogDownload.dismiss();
      mIDownloadManager.removeDownloadId(mDownloadId);
    }
  }

  private void unbindDownloadProgress() {
    mIDownloadManager.unregisterDownloadProgressChange(this);
  }

  private void showDialogDownloadFileFailed() {
    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    builder.setMessage(R.string.an_error_occurred_while_download_file);
    builder.setPositiveButton(R.string.common_ok, null);
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }

  public void showDialogWhenEmptySDCard() {
    Builder builder = new CenterButtonDialogBuilder(mMainActivity, false);
    builder.setMessage(R.string.must_have_sdcard_to_download_file);
    builder.setPositiveButton(R.string.ok, null);
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }

  public void setUploadListener() {
    if (mUploadService != null) {
      mUploadService.addUploadCustomListener(this);
    }
  }

  /**
   * Method nay duoc goi khi Chat ket noi thanh cong voi UploadService.
   */
  public void onUploadServiceConnected(CustomUploadService uploadService) {
    mUploadService = uploadService;
    mUploadService.addUploadCustomListener(this);

    LogUtils.d(TAG,
        "--->onUploadServiceConnected is calling...before call to startInitializeData method...");
    startInitializeData();
  }

  private void startInitializeData() {

    LogUtils.d(TAG, "startInitializeData() is calling...preparing to call get_chat_history API");

    requestNewestHistory();
  }

  private void checkCall(boolean isVideoCall) {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    int type = isVideoCall ? Constants.CALL_TYPE_VIDEO
        : Constants.CALL_TYPE_VOICE;
    CheckCallRequest request = new CheckCallRequest(token,
        callUserInfo.getUserId(), type);
    if (isVideoCall) {
      restartRequestServer(LOADER_ID_CHECK_CALL_VIDEO, request);
    } else {
      restartRequestServer(LOADER_ID_CHECK_CALL_VOICE, request);
    }
  }

  private void showChatMoreOptions() {
    getSlidingMenu().setSlidingEnabled(false);
    mChatMoreLayout = new ChatMoreLayout(mContext, chatMoreListener,
        mFriend.getId(), isVoiceCallWaiting, isVideoCallWaiting, true, 0);
    mPopupChatMoreOptions = new PopupWindow(mChatMoreLayout,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);

    mPopupChatMoreOptions.showAsDropDown(getNavigationBar());

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        mViewFreezedLayer.setVisibility(View.VISIBLE);
      }
    };

    Handler handler = new Handler();
    handler.post(runnable);
  }

  public void hideChatMoreOptions() {
    if (mPopupChatMoreOptions != null && mPopupChatMoreOptions.isShowing()) {
      mPopupChatMoreOptions.dismiss();
      mViewFreezedLayer.setVisibility(View.GONE);
    }
  }

  private void restartRequestBasicUserInfo() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token, callUserInfo.getUserId());
    restartRequestServer(LOADER_ID_BASIC_USER_INFO_CALL, request);
  }

  private boolean handlerCheckRequestCall(GetBasicInfoResponse response) {
    if (!response.isOnline()) {
      if (mCurrentCallType == Constants.CALL_TYPE_VOICE) {
        if (!response.isVoiceWaiting()) {
          if (!response.isVideoWaiting()) {
            Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
                Utility.REQUEST_VOICE_VIDEO_OFF, mMe.getId(), response.getUserName(),
                response.getUserId(), mChatAdapter);
            return false;
          }
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VOICE_CALL_OFF, mMe.getId(), response.getUserName(),
              response.getUserId(), mChatAdapter);
          return false;
        } else {
          checkCall(false);
          return true;
        }
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VIDEO) {
        if (!response.isVideoWaiting()) {
          if (!response.isVoiceWaiting()) {
            Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
                Utility.REQUEST_VOICE_VIDEO_OFF, mMe.getId(), response.getUserName(),
                response.getUserId(), mChatAdapter);
            return false;
          }
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VIDEO_CALL_OFF, mMe.getId(), response.getUserName(),
              response.getUserId(), mChatAdapter);
          return false;
        } else {
          checkCall(true);
          return true;
        }
      }
      return false;
    } else {
      if (!response.isVideoWaiting() && !response.isVoiceWaiting()) {
        Utility
            .showDialogRequestCall(getActivity(), mCurrentCallType, Utility.REQUEST_VOICE_VIDEO_OFF,
                mMe.getId(), response.getUserName(), response.getUserId(), mChatAdapter);
        return false;
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VOICE) {
        if (response.isVoiceWaiting()) {
          checkCall(false);
          return true;
        } else {
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VOICE_CALL_OFF, mMe.getId(), response.getUserName(),
              response.getUserId(), mChatAdapter);
          return false;
        }
      }

      if (mCurrentCallType == Constants.CALL_TYPE_VIDEO) {
        if (response.isVideoWaiting()) {
          checkCall(true);
          return true;
        } else {
          Utility.showDialogRequestCall(getActivity(), mCurrentCallType,
              Utility.REQUEST_VIDEO_CALL_OFF, mMe.getId(), response.getUserName(),
              response.getUserId(), mChatAdapter);
          return false;
        }
      }
    }
    return false;
  }

  private void executeVoiceCall() {
    hideChatMoreOptions();

    if (Utility.isBlockedWithUser(mContext, mFriend.getId())) {
      exitMeWhenBlocked();
      return;
    }

    CallUserInfo userInfo = new CallUserInfo(mFriend.getName(),
        mFriend.getId(), mFriend.getAvatar(), mFriend.getGender());
    callUserInfo = userInfo;

    mCurrentCallType = Constants.CALL_TYPE_VOICE;
    Utility.showDialogAskingVoiceCall(mMainActivity, userInfo,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            restartRequestBasicUserInfo();
          }
        });
  }

  public void executeRemoveFromFavorites() {
    String token = UserPreferences.getInstance().getToken();
    RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
        token, mFriend.getId());
    restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES,
        removeFavoriteRequest);
  }

  public void gotoUserProfile() {
    mMainActivity.setUnbindChatOnStop(true);
    MyProfileFragment fragment = MyProfileFragment.newInstance(mFriend
        .getId());
    replaceFragment(fragment);
  }

  private void initPreviewSticker(View view) {
    previewStickerView = (PreviewStickerView) view
        .findViewById(R.id.preview_sticker_view);
    previewStickerView
        .setHandleStickerListener(new OnHandleStickerListener() {

          @Override
          public void sendGift(String content) {
            content = content.replaceAll(ChatUtils.IMG_EXTENSION,
                "");
            sendStickerMessage(mMe.getId(), mFriend.getId(),
                content);
          }
        });
    mLayoutMain.setDispatchListener(new OnDispatchListener() {

      @Override
      public void onTouchDown(float x, float y) {
        // hide preview sticker when touch outside
        boolean isClickOnPrevStickerView = ViewUtil.isViewContains(
            mLayoutMain, previewStickerView, (int) x, (int) y);
        if (!isClickOnPrevStickerView) {
          previewStickerView.setVisibility(View.GONE);
        }
      }
    });
  }

  public void showDialogResendMessage(final ChatMessage chatMessage) {
    Builder builder = new android.app.AlertDialog.Builder(mMainActivity);
    builder.setItems(R.array.resend, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case 0:
            if (hasShowDialogValidate())
              return;
            // resend
            ChatService chatService = mMainActivity.getChatService();
            if (chatService != null) {
              ChatManager chatManager = chatService.getChatManager();
              if (chatManager != null && chatManager.getShowDialog() == null) {
                chatManager.setShowDialog(ChatFragment.this);
              }
            }
            chatService.getChatManager().setShowDialog(ChatFragment.this);
            StatusController.getInstance(mContext).resendMsg(
                chatMessage);
            break;
          case 1:
            // delete
            StatusController.getInstance(mContext).deleteMsg(
                chatMessage);
            break;
          default:
            break;
        }
      }
    });
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }
  private boolean hasShowNetworkNotConnected(boolean isShow) {
    if (!Utility.isNetworkConnected(mMainActivity)) {
      LogUtils.i(TAG, "-----------------------------------> hasShowNetworkNotConnected");
      if (!isShow)
        return true;
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          Response.CLIENT_ERROR_NO_CONNECTION);
      return true;
    }
    return false;
  }
  @Override
  public void showNotEnoughPoint(final int point) {
    if (getActivity() != null) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
//                    NotEnoughPointDialog.showForChat(mMainActivity);

          // CUONGNV01032016 : Remove show dialog het point
//                    Intent intent = new Intent(getActivity(), BuyPointDialogActivity.class);
//                    intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, Constants.PACKAGE_POINT_CHAT);
//                    startActivity(intent);

          NotEnoughPointDialog.showForChat(mMainActivity);
        }
      });
    }
  }

  @Override
  public void onGetURL(ChatMessage message, String id, boolean isOwn, boolean isExpired) {

    if (hasShowNetworkNotConnected(true))
      return;
    if (!isExpired) {
      if (isOwn) {
        requestVideoURL(id);
      } else {
        requestCheckUnlock(UnlockType.VIDEO, id);
      }
    } else {
      Builder builder = new Builder(getActivity());
      builder.setMessage(R.string.file_expired);
      builder.setPositiveButton(R.string.common_ok, null);
      mAlertDialog = builder.create();
      mAlertDialog.show();
    }

  }

  @Override
  public void onGetURLError() {
    Toast.makeText(getActivity(), R.string.can_not_play_file_not_found,
        Toast.LENGTH_LONG).show();
  }

  @Override
  public void onFilePath(String path) {
    mListChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
    Intent intent = new Intent(getActivity(), VideoViewActivity.class);
    intent.putExtra(VideoViewActivity.INTENT_PATH, path);
    startActivity(intent);
  }

  @Override
  public void onImageClick(ChatMessage chatMessage, String imgId, boolean isOwn, boolean isExpired) {
//    if (isOwn) {
//      showImage(imgId);
//    } else {
//
//      requestCheckUnlock(UnlockType.IMAGE, imgId);
//    }
    if (hasShowNetworkNotConnected(true))
      return;

    if (!isExpired) {
      if (isOwn) {
        showImage(imgId);
      } else {
        if (hasShowNetworkNotConnected(true))
          return;
        requestCheckUnlock(UnlockType.IMAGE, imgId);
      }
    } else {
      Builder builder = new Builder(getActivity());
      builder.setMessage(R.string.file_expired);
      builder.setPositiveButton(R.string.common_ok, null);
      mAlertDialog = builder.create();
      mAlertDialog.show();
    }
  }

  public void goBack() {
    if (isSendGiftFromProfile) {
      mNavigationManager.goBackSteps(STEP_NUMBER_BACK_TO_PROFILE);
    } else {
      mNavigationManager.goBack();
    }
  }
  private boolean hasShowDialogValidate() {
    if (hasShowNetworkNotConnected(true)) {
      return true;
    } else if ((UserPreferences.getInstance().getGender() == Constants.GENDER_TYPE_MAN)
            &&(UserPreferences.getInstance().getNumberPoint() < Preferences.getInstance().getChatPoint() || UserPreferences.getInstance().getNumberPoint() <= 0) ) {
      LogUtils.i(TAG, "-----------------------------------> showNotEnoughPoint");
      showNotEnoughPoint(Preferences.getInstance().getChatPoint());
      return true;
    }
    return false;
  }
  public void showAlertFileDeleteDialog() {
    Builder builder = new Builder(getActivity());
    builder.setMessage(R.string.an_error_file_delete);
    builder.setPositiveButton(R.string.common_ok, null);
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }
  private boolean hasShowNetworkNotConnected() {
    if (!Utility.isNetworkConnected(mMainActivity)) {
      Builder builder = new Builder(getActivity());
      builder.setTitle(R.string.common_error);
      builder.setMessage(R.string.msg_common_no_connection);
      builder.setPositiveButton(R.string.common_ok, null);
      mAlertDialog = builder.create();
      mAlertDialog.show();
      return true;
    }
    return false;
  }
  private boolean hasShowDialogCheckMB(String path) {
    File file = new File(path);
    long fileSizeInBytes = file.length();
    long fileSizeInKB = fileSizeInBytes / 1024;
    long fileSizeInMB = fileSizeInKB / 1024;
    if (fileSizeInMB > 50) {
      Builder builder = new Builder(getActivity());
      builder.setTitle(R.string.common_error);
      builder.setMessage(R.string.msg_common_50_mp);
      builder.setPositiveButton(R.string.common_ok, null);
      mAlertDialog = builder.create();
      mAlertDialog.show();
      return true;
    }
    return false;
  }

  // Listener
  public interface OnItemChatClickListener {

    public void onItemMyprofileClick();

    public void onItemUserProfileClick();
  }


  public interface ChatWithHiddenUserListener {

    public void onUpdateTime(final int remain);

    public void onStopHiding();
  }
}