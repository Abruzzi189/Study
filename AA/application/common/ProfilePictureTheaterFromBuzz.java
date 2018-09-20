package com.application.common;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.imageloader.ImageFetcher;
import com.application.ui.profile.ProfilePictureData;
import glas.bbsystem.R;


public class ProfilePictureTheaterFromBuzz extends ProfilePictureTheaterBase
    implements OnClickListener {

  private int mWhereFrom;

  public ProfilePictureTheaterFromBuzz(Context context,
      ProfilePictureData profilePictureData, ImageFetcher imageFetcher) {
    super(context, profilePictureData, imageFetcher);
  }

  public void setWhereFrom(int pWhereFrom) {
    this.mWhereFrom = pWhereFrom;
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

      if (this.mWhereFrom == ProfilePictureData.TYPE_BUZZ) {
        TextView rightBuffer = (TextView) mNavigatorBar
            .findViewById(R.id.activity_detail_picture_profile_txt_see_all);
        rightBuffer.setVisibility(View.INVISIBLE);
      }
    }
  }

  public void setBottomPanel(ProfilePictureData profilePictureData,
      boolean isMyProfile, int saveImgPrice) {
    if (mBottomPanel != null) {
      FrameLayout btnComment = (FrameLayout) mBottomPanel
          .findViewById(R.id.activity_detail_picture_profile_txt_comment_wrap);
      btnComment.setVisibility(View.VISIBLE);
      TextView btnLike = (TextView) mBottomPanel
          .findViewById(R.id.activity_detail_picture_profile_like);
      btnLike.setVisibility(View.VISIBLE);
      if (isMyProfile) {
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
        /*
         * TextView btnSavePic = (TextView) mBottomPanel
         * .findViewById(R.
         * id.activity_detail_picture_profile_save_pic_me);
         */
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

  @Override
  public void setOnClickListener(OnProfilePictureClickListener onClickListener) {
    super.setOnClickListener(onClickListener);
    TextView buffer = (TextView) mNavigatorBar
        .findViewById(R.id.activity_detail_picture_profile_txt_back);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_like);
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
    TextView txtComment = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_txt_comment);
    txtComment.setOnClickListener(this);
//		RelativeLayout rltBuffer = (RelativeLayout) mBottomPanel
//				.findViewById(R.id.activity_detail_picture_profile_user_profile);
//		rltBuffer.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    if (mOnProfilePictureClickListener != null) {
      switch (v.getId()) {
        case R.id.activity_detail_picture_profile_txt_back:
          mOnProfilePictureClickListener.onBtnBackClick(v);
          break;
        case R.id.activity_detail_picture_profile_like:
          mOnProfilePictureClickListener.onBtnLikeClick(v);
          break;
        case R.id.activity_detail_picture_profile_change_pic:
          mOnProfilePictureClickListener.onBtnChangeProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_set_as_profile_pic:
          mOnProfilePictureClickListener.onBtnSetProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_delete_pic:
          mOnProfilePictureClickListener.onBtnDeletePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic_me:
          mOnProfilePictureClickListener.onBtnSaveMyProfilePicClick(v);
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
//			case R.id.activity_detail_picture_profile_user_profile:
//				mOnProfilePictureClickListener.onBtnUserProfileClick(v);
//				break;
        default:
          break;
      }
    }
  }
}