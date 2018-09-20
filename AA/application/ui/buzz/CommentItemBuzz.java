/**
 *
 */
package com.application.ui.buzz;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.connection.request.CircleImageRequest;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.SubComment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CustomActionBarActivity;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class CommentItemBuzz extends LinearLayout implements OnClickListener {

  private ImageView ivAvatar;
  private TextView tvComment;
  private TextView tvCommentTime;
  private TextView tvUsername;
  private ImageView imageViewDeleteButton;
  private BuzzListCommentItem mEntity;
  private String mBuzzId;
  private String mCommentId;
  private Context mContext;
  private int mBuzzPosition;
  private int mCommentPosition;
  private OnActionCommentListener mActionCommentListener;
  private OnDeleteSubCommentListener mDltSubCmtListener;
  private boolean isFromBuzzDetail;
  private BaseBuzzListFragment mBaseBuzzListFragment;
  private MyProfileFragment mMyProfileFragment;
  private TextView mShowMoreSubComment;
  private TextView mReply;
  private TextView mTextCommentApprove;
  private RelativeLayout mLayoutContentComment;

  public CommentItemBuzz(Context context) {
    super(context);
    mContext = context;
    initView();
  }

  public void setMyProfileFragment(MyProfileFragment mMyProfileFragment) {
    this.mMyProfileFragment = mMyProfileFragment;
  }

  public void setBaseBuzzListFragment(BaseBuzzListFragment mBase) {
    this.mBaseBuzzListFragment = mBase;
  }

  private void initView() {
    setBackgroundColor(getContext().getResources().getColor(
        R.color.color_white));
    setOrientation(VERTICAL);
    LayoutInflater.from(getContext()).inflate(R.layout.item_comment_item_list_buzz, this, true);
    ivAvatar = (ImageView) findViewById(R.id.ava_item_comment_item_list_buzz);
    tvComment = (TextView) findViewById(R.id.content_item_comment_item_list_buzz);
    tvCommentTime = (TextView) findViewById(R.id.time_write_item_comment_item_list_buzz);
    tvUsername = (TextView) findViewById(R.id.username_item_comment_item_list_buzz);
    tvUsername.setMaxWidth(getResources().getInteger(R.integer.max_width_item_username));
    imageViewDeleteButton = (ImageView) findViewById(
        R.id.delete_button_item_comment_item_list_buzz);
    mReply = (TextView) findViewById(R.id.tv_reply);
    mShowMoreSubComment = (TextView) findViewById(R.id.tv_show_more_sub_comment);
    mShowMoreSubComment.setOnClickListener(this);
    mTextCommentApprove = (TextView) findViewById(R.id.item_comment_approve);
    mLayoutContentComment = (RelativeLayout) findViewById(R.id.layout_content_item_comment);
  }

  public void updateView(final BuzzListCommentItem entity, String buzzId, boolean isApprove,
      String token, int avatarWidth, int avatarHeight, int buzzPosition, int commentPosition,
      OnActionCommentListener actionCommentListener,
      OnDeleteSubCommentListener dltSubCommentListener, boolean isDetailBuzz) {

    setBackgroundColor(getContext().getResources().getColor(
        android.R.color.white));
    mLayoutContentComment.setPadding(0, 0, 0, 10);

    // remove sub comment added view
    int childCount = getChildCount();
    if (childCount > 1) {
      for (int i = 1; i < childCount; i++) {
        removeViewAt(childCount - i);
      }
    }
    if (entity == null) {
      return;
    }
    mEntity = entity;
    mBuzzPosition = buzzPosition;
    mCommentPosition = commentPosition;
    mActionCommentListener = actionCommentListener;
    mDltSubCmtListener = dltSubCommentListener;
    mBuzzId = buzzId;
    isFromBuzzDetail = isDetailBuzz;
    mCommentId = entity.cmt_id;

    if (!isFromBuzzDetail) {
      tvComment.setMaxLines(1);
      tvComment.setSingleLine();
      tvComment.setEllipsize(TruncateAt.END);
    }

    CircleImageRequest imageRequestCommenter = new CircleImageRequest(
        token, entity.ava_id);
    ImageUtil.loadCircleAvataImage(getContext(), imageRequestCommenter.toURL(),
        ivAvatar);

    tvUsername.setText(entity.user_name);
    tvComment.setText(entity.cmt_val);

    try {
      Calendar calendarNow = Calendar.getInstance();
      Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date dateSend = Utility.YYYYMMDDHHMMSS.parse(entity.cmt_time);
      Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
      calendarSend.setTime(dateSend);
      tvCommentTime.setText(Utility.getTimelineDif(calendarSend,
          calendarNow));
    } catch (ParseException e) {
      e.printStackTrace();
      tvCommentTime.setText(R.string.common_now);
    }

    ivAvatar.setOnClickListener(this);
    if (isApprove) {
      tvComment.setOnClickListener(this);
      tvCommentTime.setOnClickListener(this);
    } else {
      tvComment.setOnClickListener(null);
      tvCommentTime.setOnClickListener(null);
    }

    if (entity.can_delete == Constants.BUZZ_COMMENT_CANNOT_DELETE) {
      imageViewDeleteButton.setVisibility(View.INVISIBLE);
      imageViewDeleteButton.setOnClickListener(null);
    } else {
      imageViewDeleteButton.setVisibility(View.VISIBLE);
      imageViewDeleteButton
          .setImageResource(R.drawable.ic_buzz_comment_delete_disable);

      if (isApprove && entity.isApproved == Constants.IS_APPROVED) {
        imageViewDeleteButton.setOnClickListener(this);
        imageViewDeleteButton.setVisibility(VISIBLE);
      } else {
        imageViewDeleteButton.setOnClickListener(null);
        imageViewDeleteButton.setVisibility(INVISIBLE);
      }

    }

    // show comment
    if (isDetailBuzz) {
      SubComment subComment;
      // display sub comments
      if (mEntity.sub_comments != null && mEntity.sub_comments.size() > 0) {
        for (int i = 0; i < mEntity.sub_comments.size(); i++) {
          subComment = entity.sub_comments.get(i);
          SubCommentItemBuzz subCommentView = new SubCommentItemBuzz(mContext);
          subCommentView.updateView(subComment, token, avatarWidth, avatarHeight, mCommentId,
              mCommentPosition, i, mDltSubCmtListener, isDetailBuzz);
          addView(subCommentView, i + 1);
        }
      }
    }

    // show load more sub comment if needed
    if (isDetailBuzz) {
      if (mEntity.sub_comment_number > Constants.BUZZ_LIST_SHOW_NUMBER_OF_PREVIEW_SUB_COMMENTS
          && !mEntity.isNoMoreSubComment) {
        mShowMoreSubComment.setVisibility(View.VISIBLE);
      } else {
        mShowMoreSubComment.setVisibility(View.GONE);
      }
    } else {
      mShowMoreSubComment.setVisibility(mEntity.sub_comment_number > 0 ? View.VISIBLE : View.GONE);
    }

    if (entity.isApproved == Constants.IS_APPROVED) {
      mTextCommentApprove.setVisibility(View.GONE);
    } else {
      mTextCommentApprove.setVisibility(View.VISIBLE);
    }

    if (entity.isApproved
        == Constants.IS_APPROVED) { //HungHN fix error: click button reply when comment did not approve
      mReply.setOnClickListener(this);
    } else {
      mReply.setOnClickListener(null);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ava_item_comment_item_list_buzz:
        UserPreferences userPreferences = UserPreferences.getInstance();
        int myGender = userPreferences.getGender();
        String myId = userPreferences.getUserId();
        if (mEntity.gender == myGender && !mEntity.user_id.equals(myId)) {
          return;
        }
        if (mContext instanceof BaseFragmentActivity) {
          LogUtils.d("ToanTK", "View Userinfo");
          ((CustomActionBarActivity) mContext)
              .getNavigationManager()
              .addPage(MyProfileFragment.newInstance(mEntity.user_id));
          if (mBaseBuzzListFragment != null) {
            mBaseBuzzListFragment.callRefresh(mBuzzId);
          }
          if (mMyProfileFragment != null) {
            mMyProfileFragment.setResumeBuzzID(mBuzzId);
          }

        }
        break;
      case R.id.item_comment_approve:
      case R.id.content_item_comment_item_list_buzz:
      case R.id.time_write_item_comment_item_list_buzz:
        if (isFromBuzzDetail) {
          return;
        }
        if (mContext instanceof BaseFragmentActivity) {
          BuzzDetail buzzDetailFragment = BuzzDetail.newInstance(mBuzzId,
              Constants.BUZZ_TYPE_NONE);
          buzzDetailFragment.showSoftkeyWhenStart(true);
          ((CustomActionBarActivity) mContext).getNavigationManager()
              .addPage(buzzDetailFragment);
        }
        break;
      case R.id.delete_button_item_comment_item_list_buzz:
        if (mActionCommentListener != null) {
          mActionCommentListener.onDeleteComment(mBuzzPosition, mCommentPosition);
        }
        break;
      case R.id.tv_reply:
        if (mActionCommentListener != null) {
          mActionCommentListener.onReplyComment(mCommentId, mCommentPosition, mBuzzPosition);
        }
        break;
      case R.id.tv_show_more_sub_comment:
        if (mActionCommentListener != null) {
          if (isFromBuzzDetail && mEntity.sub_comments != null) {
            mActionCommentListener
                .onShowMoreComment(mCommentPosition, mCommentId, mEntity.sub_comments.size(),
                    mBuzzPosition);
          } else {
            mActionCommentListener
                .onShowMoreComment(mCommentPosition, mCommentId, mEntity.sub_comment_number,
                    mBuzzPosition);
          }
        }
        break;
    }
  }

  public interface OnActionCommentListener {

    public void onDeleteComment(int buzzPosition, int commentPosition);

    public void onReplyComment(String commentId, int commentPosition, int buzzPosition);

    public void onShowMoreComment(int commentPosition, String commentId, int skip,
        int buzzPosition);
  }
}