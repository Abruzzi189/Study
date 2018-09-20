package com.application.ui.backstage;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.common.ProfilePictureTheaterBase;
import com.application.imageloader.ImageFetcher;
import com.application.ui.profile.ProfilePictureData;
import glas.bbsystem.R;


public class ProfilePictureTheaterFromBackstage extends
    ProfilePictureTheaterBase implements OnClickListener {

  public ProfilePictureTheaterFromBackstage(Context context,
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

      TextView bufferSeeAll = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_txt_see_all);
      bufferSeeAll.setVisibility(View.INVISIBLE);
    }
  }

  public void setBottomPanel(boolean isMyPicture, int saveImgPrice) {
    if (mBottomPanel != null) {
      if (isMyPicture) {
        TextView btnDeletePic = (TextView) mBottomPanel
            .findViewById(R.id.activity_detail_picture_profile_delete_pic);
        btnDeletePic.setVisibility(View.VISIBLE);
      } else {
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
        .findViewById(R.id.activity_detail_picture_profile_change_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_delete_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic);
    buffer.setOnClickListener(this);
    buffer = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_report);
    buffer.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (mOnProfilePictureClickListener != null) {
      switch (v.getId()) {
        case R.id.activity_detail_picture_profile_txt_back:
          mOnProfilePictureClickListener.onBtnBackClick(v);
          break;
        case R.id.activity_detail_picture_profile_change_pic:
          mOnProfilePictureClickListener.onBtnChangeProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_delete_pic:
          mOnProfilePictureClickListener.onBtnDeletePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_save_pic:
          mOnProfilePictureClickListener.onBtnSaveProfilePicClick(v);
          break;
        case R.id.activity_detail_picture_profile_report:
          mOnProfilePictureClickListener.onBtnReportPicClick(v);
          break;
        default:
          break;
      }
    }
  }
}