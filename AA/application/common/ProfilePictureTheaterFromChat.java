package com.application.common;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.imageloader.ImageFetcher;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ProfilePictureTheaterFromChat extends ProfilePictureTheaterBase
    implements OnClickListener {

  public ProfilePictureTheaterFromChat(Context context,
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
    }
  }

  public void setBottomPanel(ProfilePictureData profilePictureData,
      int saveImgPrice) {
    if (mBottomPanel != null) {
      String myId = UserPreferences.getInstance().getUserId();
      String userId = profilePictureData.getUserId();
      if (!myId.equals(userId)) {
        FrameLayout savePicWrap = (FrameLayout) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_save_pic_wrap);
        ImageView lockState = (ImageView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_lock_state);
        if (saveImgPrice > 0) {
          lockState.setImageResource(R.drawable.lock_save_pic);
        } else {
          lockState.setImageResource(R.drawable.unlock_save_pic);
        }
        savePicWrap.setVisibility(View.VISIBLE);
      } else {
        FrameLayout btnSavePic = (FrameLayout) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_save_pic_me_wrap);
        btnSavePic.setVisibility(View.VISIBLE);
      }
    }
  }

  // Update image changed
  public void updateImageUri(String imgUri) {
    ImageView imgView = (ImageView) mView
        .findViewById(R.id.activity_detail_picture_profile_image);
    mViewPager.setVisibility(View.INVISIBLE);
    imgView.setImageURI(Uri.parse(imgUri));
  }

  @Override
  public void setOnClickListener(OnProfilePictureClickListener onClickListener) {
    super.setOnClickListener(onClickListener);
    TextView buffer = (TextView) mNavigatorBar
        .findViewById(R.id.activity_detail_picture_profile_txt_back);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic_me);
    buffer.setOnClickListener(this);
    ImageView imageView = (ImageView) mView
        .findViewById(R.id.activity_detail_picture_profile_image);
    imageView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (mOnProfilePictureClickListener != null) {
      switch (v.getId()) {
        case R.id.activity_detail_picture_profile_txt_back:
          mOnProfilePictureClickListener.onBtnBackClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic:
          mOnProfilePictureClickListener.onBtnSaveProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic_me:
          mOnProfilePictureClickListener
              .onBtnSaveMyProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_image:
          mOnProfilePictureClickListener.onViewPagerClick(v);
          break;
        default:
          break;
      }
    }
  }
}