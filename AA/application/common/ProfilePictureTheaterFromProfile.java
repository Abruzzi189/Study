package com.application.common;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.imageloader.ImageFetcher;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ProfilePictureTheaterFromProfile extends ProfilePictureTheaterBase
    implements OnClickListener {

  public ProfilePictureTheaterFromProfile(Context context,
      ProfilePictureData profilePictureData, ImageFetcher imageFetcher) {
    super(context, profilePictureData, imageFetcher);
  }

  @Override
  public View getView() {
    View view = super.getView();
    return view;
  }

  public void setNavigatorBar() {
    if (mNavigatorBar != null) {
      TextView buffer = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_txt_back);
      buffer.setVisibility(View.VISIBLE);
      buffer = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_txt_see_all);
      buffer.setVisibility(View.VISIBLE);
    }
  }

  public void setBottomPanel(ProfilePictureData profilePictureData,
      int saveImgPrice) {
    if (mBottomPanel != null) {
      showComunicateButton();
      String myId = UserPreferences.getInstance().getUserId();
      String userId = profilePictureData.getUserId();
      if (myId.equals(userId)) {
        if (mViewPager != null) {
          if (mViewPager.getCurrentItem() == 0) {
            TextView btnChangePic = (TextView) mBottomPanel
                .findViewById(R.id.activity_detail_picture_profile_change_pic);
            btnChangePic.setVisibility(View.VISIBLE);
          } else {
            TextView btnSetAvaPic = (TextView) mBottomPanel
                .findViewById(R.id.activity_detail_picture_profile_set_as_profile_pic);
            btnSetAvaPic.setVisibility(View.VISIBLE);
          }
        } else {
          TextView btnSetAvaPic = (TextView) mBottomPanel
              .findViewById(R.id.activity_detail_picture_profile_set_as_profile_pic);
          btnSetAvaPic.setVisibility(View.VISIBLE);
        }
        FrameLayout btnSavePic = (FrameLayout) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_save_pic_me_wrap);
        btnSavePic.setVisibility(View.VISIBLE);
        TextView btnDeletePic = (TextView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_delete_pic);
        btnDeletePic.setVisibility(View.VISIBLE);
      } else {
        showUserAvata(profilePictureData.getAvata(),
            profilePictureData.getGender());
        /*
         * TextView btnSavePic = (TextView) mBottomPanel
         * .findViewById(R.id.activity_detail_picture_profile_save_pic);
         */
        FrameLayout savePicWrap = (FrameLayout) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_save_pic_wrap);
        ImageView lockState = (ImageView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_lock_state);
        if (saveImgPrice > 0) {
          /*
           * btnSavePic.setCompoundDrawablesWithIntrinsicBounds(0,
           * R.drawable.ic_detail_save_pic_locked, 0, 0);
           */
          lockState.setImageResource(R.drawable.lock_save_pic);
        } else {
          /*
           * btnSavePic.setCompoundDrawablesWithIntrinsicBounds(0,
           * R.drawable.ic_detail_save_pic, 0, 0);
           */
          lockState.setImageResource(R.drawable.unlock_save_pic);
        }
        // btnSavePic.setVisibility(View.VISIBLE);
        savePicWrap.setVisibility(View.VISIBLE);
        TextView btnReportPic = (TextView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_report);
        btnReportPic.setVisibility(View.VISIBLE);
      }
    }
  }

  public void showComunicateButton() {
    FrameLayout btnComment = (FrameLayout) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_txt_comment_wrap);
    btnComment.setVisibility(View.VISIBLE);
    TextView btnLike = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_like);
    btnLike.setVisibility(View.VISIBLE);
  }

  @Override
  public void setOnClickListener(OnProfilePictureClickListener onClickListener) {
    super.setOnClickListener(onClickListener);
    TextView buffer = (TextView) mNavigatorBar
        .findViewById(R.id.activity_detail_picture_profile_txt_back);
    buffer.setOnClickListener(this);
    buffer = (TextView) mNavigatorBar
        .findViewById(R.id.activity_detail_picture_profile_txt_see_all);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_change_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_set_as_profile_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_delete_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic_me);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_report);
    buffer.setOnClickListener(this);
    TextView btnComment = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_txt_comment);
    btnComment.setOnClickListener(this);
    RelativeLayout btnUserProfile = (RelativeLayout) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_user_profile);
    btnUserProfile.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_like);
    buffer.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (mOnProfilePictureClickListener != null) {
      switch (v.getId()) {
        case R.id.activity_detail_picture_profile_txt_back:
          mOnProfilePictureClickListener.onBtnBackClick(v);
          break;
        case R.id.activity_detail_picture_profile_txt_see_all:
          mOnProfilePictureClickListener.onBtnSeeAllClick(v);
          break;
        case R.id.activity_detail_picture_profile_change_pic:
          mOnProfilePictureClickListener
              .onBtnChangeProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_set_as_profile_pic:
          mOnProfilePictureClickListener.onBtnSetProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_delete_pic:
          mOnProfilePictureClickListener.onBtnDeletePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic_me:
          mOnProfilePictureClickListener
              .onBtnSaveMyProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic:
          mOnProfilePictureClickListener.onBtnSaveProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_report:
          mOnProfilePictureClickListener.onBtnReportPicClick(v);
          break;
        case R.id.activity_detail_picture_profile_txt_comment:
          mOnProfilePictureClickListener.onBtnCommentClick(v);
          break;
        case R.id.activity_detail_picture_profile_like:
          mOnProfilePictureClickListener.onBtnLikeClick(v);
          break;
        case R.id.activity_detail_picture_profile_user_profile:
          mOnProfilePictureClickListener.onBtnUserProfileClick(v);
          break;
        default:
          break;
      }
    }
  }
}