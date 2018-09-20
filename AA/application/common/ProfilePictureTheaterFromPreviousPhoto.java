package com.application.common;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.imageloader.ImageFetcher;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ProfilePictureTheaterFromPreviousPhoto extends
    ProfilePictureTheaterBase implements OnClickListener {

  public ProfilePictureTheaterFromPreviousPhoto(Context context,
      ProfilePictureData profilePictureData, ImageFetcher imageFetcher) {
    super(context, profilePictureData, imageFetcher);
  }

  @Override
  public View getView() {
    View view = super.getView();
    return view;
  }

  public void setNavigatorBar(boolean isGallery) {
    if (mNavigatorBar != null) {
      TextView buffer = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_txt_back);
      buffer.setText(R.string.common_close);
      buffer.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
          null);
      buffer.setVisibility(View.VISIBLE);
      buffer = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_txt_see_all);
      if (isGallery) {
        buffer.setVisibility(View.GONE);
      } else {
        buffer.setVisibility(View.VISIBLE);
      }
    }
  }

  public void setBottomPanel(ProfilePictureData profilePictureData) {
    if (mBottomPanel != null) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      String userId = profilePictureData.getUserId();
      String myId = userPreferences.getUserId();
      if (myId.equals(userId)) {
        TextView btnDeletePic = (TextView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_delete_pic);
        btnDeletePic.setVisibility(View.VISIBLE);
      } else {
        FrameLayout savePicWrap = (FrameLayout) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_save_pic_wrap);
        ImageView lockState = (ImageView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_lock_state);

        Preferences preferences = Preferences.getInstance();
        int saveImgPrice = preferences.getSaveImagePoints();
        if (saveImgPrice > 0) {
          lockState.setImageResource(R.drawable.lock_save_pic);
        } else {
          lockState.setImageResource(R.drawable.unlock_save_pic);
        }
        savePicWrap.setVisibility(View.VISIBLE);
      }
    }
  }

  public void onPageChanged(boolean isOwn) {
    syncSaveButton(isOwn);
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
        .findViewById(R.id.activity_detail_picture_profile_save_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic_me);
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
        case R.id.activity_detail_picture_profile_save_pic:
          mOnProfilePictureClickListener.onBtnSaveProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic_me:
          mOnProfilePictureClickListener
              .onBtnSaveMyProfilePicClick(v);
          break;
        default:
          break;
      }
    }
  }
}