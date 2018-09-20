package com.application.ui.buzz;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.connection.request.CircleImageRequest;
import com.application.constant.Constants;
import com.application.entity.SubComment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CustomActionBarActivity;
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


public class SubCommentItemBuzz extends RelativeLayout implements View.OnClickListener {

  private Context mContext;
  private ImageView ivAvatar;
  private TextView tvComment, tvCommentTime, tvUsername;
  private ImageView imageViewDeleteButton;
  private SubComment mSubComment;
  private String mCommentId;
  private TextView mTextSubCommentApprove;
  private RelativeLayout mLayoutContentComment;

  private OnDeleteSubCommentListener mListener;
  private boolean isFromBuzzDetail;

  private int mCmtPosition, mSubCmtPosition;

  public SubCommentItemBuzz(Context context) {
    super(context);
    mContext = context;
    initView();
  }

  private void initView() {
    setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
    LayoutInflater.from(getContext()).inflate(R.layout.item_comment_item_list_buzz, this, true);
    ivAvatar = (ImageView) findViewById(R.id.ava_item_comment_item_list_buzz);
    tvComment = (TextView) findViewById(R.id.content_item_comment_item_list_buzz);
    tvCommentTime = (TextView) findViewById(R.id.time_write_item_comment_item_list_buzz);
    tvUsername = (TextView) findViewById(R.id.username_item_comment_item_list_buzz);
    tvUsername.setMaxWidth(getResources().getInteger(R.integer.max_width_item_username_large));
    imageViewDeleteButton = (ImageView) findViewById(
        R.id.delete_button_item_comment_item_list_buzz);
    findViewById(R.id.tv_reply).setVisibility(View.GONE);
    mTextSubCommentApprove = (TextView) findViewById(R.id.item_comment_approve);
    mLayoutContentComment = (RelativeLayout) findViewById(R.id.layout_content_item_comment);
  }

  public void updateView(final SubComment subComment, String token, int avatarWidth,
      int avatarHeight, String commentId, int cmtPos, int subCmtPos,
      OnDeleteSubCommentListener listener, boolean isDetailBuzz) {
    if (subComment == null) {
      return;
    }

    mLayoutContentComment.setPadding(80, 0, 0, 10);
    mSubComment = subComment;
    mCommentId = commentId;
    mSubCmtPosition = subCmtPos;
    mCmtPosition = cmtPos;
    mListener = listener;
    isFromBuzzDetail = isDetailBuzz;

    if (!isFromBuzzDetail) {
      tvComment.setMaxLines(1);
      tvComment.setSingleLine();
      tvComment.setEllipsize(TruncateAt.END);
    }

    CircleImageRequest imageRequestCommenter = new CircleImageRequest(token, mSubComment.ava_id);
    ImageUtil.loadAvataImage(mContext, imageRequestCommenter.toURL(), ivAvatar, avatarWidth);

    tvUsername.setText(mSubComment.user_name);
    tvComment.setText(mSubComment.value);

    try {
      Calendar calendarNow = Calendar.getInstance();
      Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date dateSend = Utility.YYYYMMDDHHMMSS.parse(mSubComment.time);
      Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
      calendarSend.setTime(dateSend);
      tvCommentTime.setText(Utility.getTimelineDif(calendarSend,
          calendarNow));
    } catch (ParseException e) {
      e.printStackTrace();
      tvCommentTime.setText(R.string.common_now);
    }

    ivAvatar.setOnClickListener(this);

    if (!mSubComment.can_delete) {
      imageViewDeleteButton.setVisibility(View.INVISIBLE);
      imageViewDeleteButton.setOnClickListener(null);
    } else {
      imageViewDeleteButton.setVisibility(View.VISIBLE);
      imageViewDeleteButton
          .setImageResource(R.drawable.ic_buzz_comment_delete_disable);

      if (mSubComment.isApprove == Constants.IS_APPROVED) {
        imageViewDeleteButton.setOnClickListener(this);
        imageViewDeleteButton.setVisibility(VISIBLE);
      } else {
        imageViewDeleteButton.setOnClickListener(null);
        imageViewDeleteButton.setVisibility(INVISIBLE);
      }
    }
    if (subComment.isApprove == Constants.IS_APPROVED) {
      mTextSubCommentApprove.setVisibility(View.GONE);
      mTextSubCommentApprove.setOnClickListener(null);
    } else {
      mTextSubCommentApprove.setVisibility(View.VISIBLE);
      mTextSubCommentApprove.setOnClickListener(this);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ava_item_comment_item_list_buzz:
        UserPreferences userPreferences = UserPreferences.getInstance();
        int myGender = userPreferences.getGender();
        String myId = userPreferences.getUserId();
        if (mSubComment.gender == myGender && !mSubComment.user_id.equals(myId)) {
          return;
        }
        if (mContext instanceof BaseFragmentActivity) {
          LogUtils.d("ToanTK", "View Userinfo");
          ((CustomActionBarActivity) mContext)
              .getNavigationManager()
              .addPage(MyProfileFragment.newInstance(mSubComment.user_id));
        }
        break;
      case R.id.delete_button_item_comment_item_list_buzz:
        if (mListener != null) {
          mListener.onDeleteSubComment(mCommentId, mSubComment.sub_comment_id, mCmtPosition,
              mSubCmtPosition);
        }
        break;

      default:
        break;
    }

  }

  public interface OnDeleteSubCommentListener {

    public void onDeleteSubComment(String commentId, String subCommentId, int commentPosition,
        int subCommentPosition);

  }

}
