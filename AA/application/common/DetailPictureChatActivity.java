package com.application.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.application.connection.ResponseReceiver;
import com.application.ui.profile.DetailPictureBaseActivity;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.LogUtils;
import com.application.util.StorageUtil;
import com.application.util.preferece.Preferences;
import java.io.File;

public class DetailPictureChatActivity extends DetailPictureBaseActivity
    implements ResponseReceiver {

  public static final String IMG_URI = "img_uri";
  public String imgUri = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    mProfilePictureTheater = new ProfilePictureTheaterFromChat(this,
        mProfilePictureData, getImageFetcher());
    setContentView(mProfilePictureTheater.getView());
    if (mProfilePictureData.getDataType() == ProfilePictureData.TYPE_SAVE_CHAT) {
      mProfilePictureTheater
          .updateImage(mProfilePictureData.getListImg());
      LogUtils.d("mProfilePictureData.getListImg()", mProfilePictureData
          .getListImg().get(0));
    } else {
      Bundle dataTransport = getIntent().getExtras();
      if (dataTransport != null) {
        imgUri = dataTransport.getString(IMG_URI);
        String userId = dataTransport
            .getString(ProfilePictureData.USER_ID);
        mProfilePictureData.setUserId(userId);
      }
      ((ProfilePictureTheaterFromChat) mProfilePictureTheater)
          .updateImageUri(imgUri);
    }
    initView();
    loadActionPoint();
    initialNotificationVew();
  }

  public void initView() {
    ((ProfilePictureTheaterFromChat) mProfilePictureTheater)
        .setNavigatorBar();
    int saveImgPrice = Preferences.getInstance().getSaveImagePoints();
    ((ProfilePictureTheaterFromChat) mProfilePictureTheater)
        .setBottomPanel(mProfilePictureData, saveImgPrice);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBtnSaveMyProfilePicClick(View v) {
    if (mProfilePictureData.getDataType() == ProfilePictureData.TYPE_SAVE_CHAT) {
      super.onBtnSaveMyProfilePicClick(v);
    } else {
      if (imgUri != null) {
        mProfilePictureTheater.showProgressDialog(this);
        if (StorageUtil.saveImage(getApplicationContext(), new File(
            imgUri))) {
          mProfilePictureTheater.dismissProgressDialog();
          mProfilePictureTheater.showDialogSaveDone(true);
        } else {
          mProfilePictureTheater.showDialogSaveDone(false);
        }
      } else {
        mProfilePictureTheater.showDialogSaveDone(false);
      }
    }
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }
}