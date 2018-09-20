package com.application.ui.buzz;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.ImageRequest;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.ui.buzz.CommentItemBuzz.OnActionCommentListener;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.ImageUtil;
import com.application.util.RegionUtils;
import com.application.util.Utility;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author MTROL
 */
public class BuzzItemListView extends LinearLayout implements OnClickListener {

  private int mType;
  private BuzzListItem mEntity;
  private Context mContext;
  private ImageView mImageViewAvatar;
  // private ImageView mImageOnlineStatus;
  private ImageView mImageViewAction;
  private TextView mTextViewUserName;
  private TextView mTextViewDistance;
  private TextView mTextViewTime;
  private ImageView mShareImageViewPhoto;
  private TextView mShareImageViewPhotoImApproved;
  private RelativeLayout mRelaytiveShareGift;
  private ImageView mImageViewShareGift;
  private TextView mTextViewShareGiftDescription;
  private TextView mTextViewShareStatus;
  private TextView mTextViewStatusApprove;
  private RelativeLayout mRelaytiveShowMoreResponses;
  private ImageView mImageButtonLike;
  private TextView mTextViewInputComment;
  private TextView mTextViewLikeNumber;
  private TextView mTextViewCommentNumber;
  private RelativeLayout mLLBuzzItemStatistic;
  private RelativeLayout mRelativeBuzzItemUserInfo;
  private LinearLayout mLayoutComment;
  private ViewStub mStubView;
  private View mLine1, mLine2;
  private ImageView mReportButton;

  private int mCommentAvatarSize = 0;
  private int mBuzzGiftSize = 0;
  private int mBuzzPhotoSize = 0;
  private int mBuzzPosition;
  private OnActionCommentListener mDltCmtListener;
  private OnDeleteSubCommentListener mDltSubCmtListener;
  private OnActionBuzzListener mActionListener;
  private boolean hasStatusInflate = false;
  private boolean hasPhotoInflate = false;
  private boolean hasGiftInflate = false;
  private int mChildCount;

  private RegionUtils mRegionUtils;
  private BaseBuzzListFragment mBaseBuzzListFragment;
  private MyProfileFragment mMyProfileFragment;
  private int POSITION_COMMENT_1 = 3;
  private int POSITION_COMMENT_2 = 4;
  private int POSITION_COMMENT_3 = 5;
  private int POSITION_COMMENT_4 = 6;

  public BuzzItemListView(Context context, int type, boolean hasBackground) {
    super(context);
    mType = type;
    mContext = context;
    mRegionUtils = new RegionUtils(mContext);
    initImageLoadingSize();
    initView(hasBackground);
  }

  public void setMyProfileFragment(MyProfileFragment mMyProfileFragment) {
    this.mMyProfileFragment = mMyProfileFragment;
  }

  public void setBaseBuzzListFragment(BaseBuzzListFragment mBase) {
    this.mBaseBuzzListFragment = mBase;
  }

  private void initView(boolean hasBackground) {
    this.setOrientation(LinearLayout.VERTICAL);
    this.setBackgroundResource(R.drawable.bg_item_list_buzz);
    int padding = (int) Utility.convertDpToPixel(2, mContext);
    this.setPadding(padding, padding, padding, padding);
    LayoutInflater.from(getContext()).inflate(R.layout.item_list_buzz,
        this, true);
    mImageViewAvatar = (ImageView) findViewById(R.id.img_avata_item_list_buzz);
    mImageViewAction = (ImageView) findViewById(R.id.button_edit_item_info_list_buzz);
    mTextViewUserName = (TextView) findViewById(R.id.user_name_item_list_buzz);
    mTextViewDistance = (TextView) findViewById(R.id.distance_item_list_buzz);
    mTextViewTime = (TextView) findViewById(R.id.time_item_list_buzz);
    mRelaytiveShowMoreResponses = (RelativeLayout) findViewById(
        R.id.frame_show_more_responses_list_buzz);
    mImageButtonLike = (ImageView) findViewById(R.id.frame_like_button_list_buzz);
    mTextViewInputComment = (TextView) findViewById(R.id.tv_buzz_item_input_comment);
    mTextViewLikeNumber = (TextView) findViewById(R.id.frame_like_number_list_buzz);
    mTextViewCommentNumber = (TextView) findViewById(R.id.frame_comment_number_list_buzz_txt);
    mStubView = (ViewStub) findViewById(R.id.stub_buzz_content);
    mLLBuzzItemStatistic = (RelativeLayout) findViewById(R.id.ll_buzz_item_statistic);
    mRelativeBuzzItemUserInfo = (RelativeLayout) findViewById(R.id.rl_buzz_item_user_info);
    mLayoutComment = (LinearLayout) findViewById(R.id.layout_comment);
    mLine1 = findViewById(R.id.line_buzz_1);
    mLine2 = findViewById(R.id.line_buzz_2);
    inflateStubView();
    mChildCount = getChildCount();

    mImageViewAction.setColorFilter(ContextCompat.getColor(mContext, R.color.default_app));
  }

  private void inflateStubView() {
    if (mType == Constants.BUZZ_TYPE_STATUS) {
      if (!hasStatusInflate) {
        initStatusContent();
        hasStatusInflate = true;
      }

    } else if (mType == Constants.BUZZ_TYPE_GIFT) {
      if (!hasGiftInflate) {
        initGiftContent();
        hasGiftInflate = true;
      }
    } else if (mType == Constants.BUZZ_TYPE_IMAGE) {
      if (!hasPhotoInflate) {
        initImageContent();
        hasPhotoInflate = true;
      }
    }
  }

  private void initStatusContent() {
    mStubView.setLayoutResource(R.layout.buzz_content_status);
    View inflated = mStubView.inflate();
    mTextViewShareStatus = (TextView) inflated
        .findViewById(R.id.buzz_status);
    mTextViewStatusApprove = (TextView) inflated.findViewById(R.id.buzz_status_approve);
    mReportButton = (ImageView) inflated
        .findViewById(R.id.iv_buzz_detail_report);
  }

  private void initGiftContent() {
    mStubView.setLayoutResource(R.layout.buzz_content_gift);
    View inflated = mStubView.inflate();

    mRelaytiveShareGift = (RelativeLayout) inflated;
    mRelaytiveShareGift.setId(R.id.buzz_gift_layout);
    mImageViewShareGift = (ImageView) inflated.findViewById(R.id.buzz_gift);
    mTextViewShareGiftDescription = (TextView) inflated
        .findViewById(R.id.buzz_gift_content);
  }

  private void initImageContent() {
    mStubView.setLayoutResource(R.layout.buzz_content_image);
    View inflated = mStubView.inflate();
    mShareImageViewPhoto = (ImageView) inflated
        .findViewById(R.id.buzz_image);
    mShareImageViewPhotoImApproved = (TextView) inflated
        .findViewById(R.id.buzz_image_status);
    mReportButton = (ImageView) inflated
        .findViewById(R.id.iv_buzz_detail_report);

    DisplayMetrics metrics = new DisplayMetrics();
    ((Activity) getContext()).getWindowManager().getDefaultDisplay()
        .getMetrics(metrics);
    int width = metrics.widthPixels;
    FrameLayout.LayoutParams mSharePhotoLayoutParams = new FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT, width);

    mShareImageViewPhoto.setLayoutParams(mSharePhotoLayoutParams);
    mShareImageViewPhotoImApproved.setLayoutParams(mSharePhotoLayoutParams);
  }

  public void updateView(BuzzListItem entity, boolean isUpdateComment, int postion,
      OnActionCommentListener dltCmtListener, OnDeleteSubCommentListener dltSubCmtListener,
      OnActionBuzzListener actionListener) {
    mDltCmtListener = dltCmtListener;
    mDltSubCmtListener = dltSubCmtListener;
    mActionListener = actionListener;
    mEntity = entity;
    mBuzzPosition = postion;
    mType = mEntity.getBuzzType();

    boolean buzzOwner = false;
    if (UserPreferences.getInstance().getUserId()
        .equals(entity.getUserId())) {
      buzzOwner = true;
    }

    inflateStubView();

    String token = UserPreferences.getInstance().getToken();
    String avata = entity.getAvatarId();
    CircleImageRequest imageRequest = new CircleImageRequest(token, avata);
    String imgUrl = imageRequest.toURL();
    ImageUtil.loadCircleAvataImage(getContext(), imgUrl, mImageViewAvatar);

    mTextViewUserName.setText(entity.getUserName());
    mTextViewDistance.setText(mRegionUtils.getRegionName(mEntity
        .getRegion()));
    try {
      Calendar calendarNow = Calendar.getInstance();
      Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date dateSend = Utility.YYYYMMDDHHMMSS.parse(entity.getBuzzTime());
      Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
      calendarSend.setTime(dateSend);
      mTextViewTime.setText(Utility.getTimelineDif(calendarSend,
          calendarNow));
    } catch (ParseException e) {
      e.printStackTrace();
      mTextViewTime.setText(R.string.common_now);
    }

    switch (mType) {
      case Constants.BUZZ_TYPE_GIFT:
        mRelaytiveShareGift.setVisibility(View.VISIBLE);
        ImageRequest giftRequest = new ImageRequest(token,
            entity.getBuzzValue(), ImageRequest.GIFT);
        ImageUtil.loadGiftImage(getContext(), giftRequest.toURL(),
            mImageViewShareGift, mBuzzGiftSize);
        mTextViewShareGiftDescription.setText(String.format(getResources()
                .getString(R.string.item_buzz_list_owner_received_a_gift),
            entity.getUserName()));
        break;
      case Constants.BUZZ_TYPE_IMAGE:
        if (entity.getIsApproved() == Constants.IS_NOT_APPROVED) {
          mShareImageViewPhotoImApproved.setVisibility(View.VISIBLE);
          mImageViewAction.setVisibility(View.INVISIBLE);
        } else {
          mShareImageViewPhotoImApproved.setVisibility(View.GONE);
          mImageViewAction.setVisibility(View.VISIBLE);
        }
        mShareImageViewPhoto.setVisibility(View.VISIBLE);
        ImageRequest imageRequestPhoto = new ImageRequest(token,
            entity.getBuzzValue(), ImageRequest.ORIGINAL);
        ImageUtil.loadBuzzImage(getContext(), imageRequestPhoto.toURL(),
            mShareImageViewPhoto, mBuzzPhotoSize);
        break;
      case Constants.BUZZ_TYPE_STATUS:
        if (entity.getIsApproved() == Constants.IS_NOT_APPROVED) {
          mTextViewStatusApprove.setVisibility(View.VISIBLE);
          mImageViewAction.setVisibility(View.INVISIBLE);
        } else {
          mTextViewStatusApprove.setVisibility(View.GONE);
          mImageViewAction.setVisibility(View.VISIBLE);
        }
        mTextViewShareStatus.setText(entity.getBuzzValue());
        break;
      default:
        return;
    }

    if (isUpdateComment) {
      updateComment(entity, postion, token);
    } else {
      if (mReportButton != null) {
        if (buzzOwner) {
          mReportButton.setVisibility(View.GONE);
          mReportButton.setOnClickListener(null);
        } else {
          mReportButton.setVisibility(View.VISIBLE);
          mReportButton.setOnClickListener(this);
        }
      }
    }

    if (buzzOwner) {
      if (entity.getSelectToDelete()) {
        mImageViewAction
            .setImageResource(R.drawable.ic_buzz_comment_delete_enable);
      } else {
        mImageViewAction
            .setImageResource(R.drawable.ic_buzz_comment_delete_disable);
      }
      mImageViewAction.setBackgroundColor(Color.TRANSPARENT);
    } else {
      FavouritedPrefers favouritedPrefers = FavouritedPrefers
          .getInstance();
      if (favouritedPrefers.hasContainFav(entity.getUserId())) {
        mImageViewAction
            .setImageResource(R.drawable.ic_list_buzz_item_favorited);
        mImageViewAction.setColorFilter(ContextCompat.getColor(mContext, R.color.default_app));
      } else {
        mImageViewAction
            .setImageResource(R.drawable.ic_list_buzz_item_favorite);
      }
    }

    mTextViewInputComment.setLongClickable(false);
    if (entity.getIsApproved() == Constants.IS_APPROVED) {
      mImageViewAction.setOnClickListener(this);
      if (mTextViewShareStatus != null) {
        mTextViewShareStatus.setOnClickListener(this);
      }
      if (mRelaytiveShareGift != null) {
        mRelaytiveShareGift.setOnClickListener(this);
      }
      if (mShareImageViewPhoto != null) {
        mShareImageViewPhoto.setOnClickListener(this);
      }
      mImageButtonLike.setOnClickListener(this);
      mTextViewInputComment.setOnClickListener(this);
      mLLBuzzItemStatistic.setOnClickListener(this);
    } else {
      mImageViewAction.setOnClickListener(null);
      if (mTextViewShareStatus != null) {
        mTextViewShareStatus.setOnClickListener(null);
      }
      if (mRelaytiveShareGift != null) {
        mRelaytiveShareGift.setOnClickListener(null);
      }
      if (mShareImageViewPhoto != null) {
        mShareImageViewPhoto.setOnClickListener(null);
      }
      mImageButtonLike.setOnClickListener(null);
      mTextViewInputComment.setOnClickListener(null);
      mLLBuzzItemStatistic.setOnClickListener(null);
    }

    mRelativeBuzzItemUserInfo.setOnClickListener(this);

    String userID = entity.getUserId();
    UserPreferences preferences = UserPreferences.getInstance();
    String myId = preferences.getUserId();
    int commentPoint = entity.getCommentPoint();
    if (commentPoint > 0 && !myId.equals(userID)) {
      String format = getResources().getString(
          R.string.timeline_comment_point_hint);
      mTextViewInputComment.setHint(MessageFormat.format(format,
          commentPoint));
    } else {
      mTextViewInputComment.setHint(getResources().getString(
          R.string.timeline_comment_hint));
    }
  }

  private void updateComment(BuzzListItem entity, int postion, String token) {
    mLLBuzzItemStatistic.setVisibility(View.VISIBLE);
    mLayoutComment.setVisibility(View.VISIBLE);
    mLine1.setVisibility(View.VISIBLE);
    mLine2.setVisibility(View.VISIBLE);
    int childCount = getChildCount();

    if (childCount == mChildCount + 1) {
      removeViewAt(POSITION_COMMENT_1);
    } else if (childCount == mChildCount + 2) {
      removeViewAt(POSITION_COMMENT_2);
      removeViewAt(POSITION_COMMENT_1);
    } else if (childCount == mChildCount + 3) {
      removeViewAt(POSITION_COMMENT_3);
      removeViewAt(POSITION_COMMENT_2);
      removeViewAt(POSITION_COMMENT_1);
    } else if (childCount == mChildCount + 4) {
      removeViewAt(POSITION_COMMENT_4);
      removeViewAt(POSITION_COMMENT_3);
      removeViewAt(POSITION_COMMENT_2);
      removeViewAt(POSITION_COMMENT_1);
    }

    int loopCounter = 0;
    if (entity.getCommentList() != null) {
      loopCounter = Math.min(entity.getCommentList().size(),
          entity.getCommentNumber());
    }

    BuzzListCommentItem blci = null;
    int min = Math.min(loopCounter,
        Constants.BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_COMMENTS);
    for (int i = 0; i < min; i++) {
      blci = entity.getCommentList().get(i);
      CommentItemBuzz comment = new CommentItemBuzz(mContext);
      comment.updateView(blci, entity.getBuzzId(), entity.getIsApproved() == Constants.IS_APPROVED,
          token, mCommentAvatarSize, mCommentAvatarSize, postion, i, mDltCmtListener,
          mDltSubCmtListener, false);
      comment.setBaseBuzzListFragment(this.mBaseBuzzListFragment);
      comment.setMyProfileFragment(mMyProfileFragment);
      addView(comment, i + POSITION_COMMENT_1);
    }

    if (entity.getCommentNumber() > Constants.BUZZ_LIST_SHOW_LOAD_MORE) {
      mRelaytiveShowMoreResponses.setVisibility(View.VISIBLE);
      if (entity.getIsApproved() == Constants.IS_APPROVED) {
        mRelaytiveShowMoreResponses.setOnClickListener(this);
      } else {
        mRelaytiveShowMoreResponses.setOnClickListener(null);
      }
    } else {
      mRelaytiveShowMoreResponses.setVisibility(View.GONE);
    }

    Resources resource = getResources();

    mTextViewLikeNumber.setText(String.valueOf(entity.getLikeNumber()));
    mTextViewCommentNumber
        .setText(String.valueOf(entity.getCommentNumber()));
    if (entity.getIsLike() == Constants.BUZZ_LIKE_TYPE_UNLIKE) {
      mImageButtonLike.setImageDrawable(resource
          .getDrawable(R.drawable.btn_unlike));
      mTextViewLikeNumber.setTextColor(getResources().getColor(
          R.color.color_hint_bold));
    } else {
      mImageButtonLike.setImageDrawable(resource
          .getDrawable(R.drawable.btn_like));
      mTextViewLikeNumber.setTextColor(getResources().getColor(
          R.color.text_selected));
    }
  }

  private void initImageLoadingSize() {
    mCommentAvatarSize = getResources().getDimensionPixelSize(
        R.dimen.img_commenter_avata_item_list_buzz_size);
    mBuzzGiftSize = getResources().getDimensionPixelSize(
        R.dimen.img_gift_item_list_buzz_size);
    mBuzzPhotoSize = getResources().getDimensionPixelSize(
        R.dimen.img_photo_item_list_buzz_size);
  }

  @Override
  public void onClick(View v) {
    if (mActionListener == null) {
      return;
    }
    switch (v.getId()) {
      // mImageViewAction
      case R.id.button_edit_item_info_list_buzz:
        viewActionOnClick();
        break;
      case R.id.buzz_status:
      case R.id.buzz_gift_layout:
      case R.id.ll_buzz_item_statistic:
      case R.id.frame_show_more_responses_list_buzz:
        mActionListener.viewBuzzDetailAt(mBuzzPosition);
        break;
      case R.id.buzz_image:
        mActionListener.viewDetailPictureAt(mBuzzPosition);
        break;
      case R.id.frame_like_button_list_buzz:
        mActionListener.likeBuzzAt(mBuzzPosition);
        break;
      case R.id.tv_buzz_item_input_comment:
        inputComment();
        break;
      case R.id.rl_buzz_item_user_info:
        mActionListener.showUserInfoAt(mBuzzPosition);
        break;
      case R.id.iv_buzz_detail_report:
        mActionListener.reportBuzz();
        break;
      default:
        return;
    }
  }

  private void inputComment() {
    if (mEntity.getCommentNumber() >= Constants.BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_COMMENTS) {
      mActionListener.viewBuzzDetailAt(mBuzzPosition);
    } else {
      mActionListener.openLikeAndPostCommentAt(mBuzzPosition);
    }
  }

  private void viewActionOnClick() {
    if (mActionListener == null) {
      return;
    }
    if (mEntity.getUserId().equals(
        UserPreferences.getInstance().getUserId())) {
      mActionListener.deleteBuzzAt(mBuzzPosition);
    } else {
      mActionListener.handleFavorite(mBuzzPosition);
    }
  }

  public void setDeleteResource(int res) {
    mImageViewAction.setImageResource(res);
  }

  public interface OnActionBuzzListener {

    public void deleteBuzzAt(int position);

    public void chatWithUserAt(int position);

    public void viewDetailPictureAt(int position);

    public void viewBuzzDetailAt(int position);

    public void likeBuzzAt(int position);

    public void openLikeAndPostCommentAt(int position);

    public void showUserInfoAt(int position);

    public void reportBuzz();

    public void handleFavorite(int position);
  }
}
