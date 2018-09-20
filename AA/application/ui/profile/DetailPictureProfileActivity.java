package com.application.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import com.application.common.GalleryActivity;
import com.application.common.ProfilePictureTheaterFromProfile;
import com.application.connection.Response;
import com.application.constant.Constants;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;

public class DetailPictureProfileActivity extends DetailPictureBaseActivity {

  public static final String GALLERY_ACTIVITY = "gallery_activity";
  public static final String IMAGE_PICKER_ACTIVITY = "image_picker_activity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    mProfilePictureTheater = new ProfilePictureTheaterFromProfile(this,
        mProfilePictureData, getImageFetcher());
    setContentView(mProfilePictureTheater.getView());
    int sizeToLoad = mProfilePictureData.getNumberOfImage();
    loadListImage(sizeToLoad);
    initView();
    loadActionPoint();
    initialNotificationVew();
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    String fragmentTag = intent.getStringExtra(Constants.FRAGMENT_TAG);
    if (fragmentTag.equals(GALLERY_ACTIVITY)) {
      getDataFromGallery(intent);
    }
  }

  private void getDataFromGallery(Intent intent) {
    int position = intent.getIntExtra(GalleryActivity.GALLERY_INDEX, 0);
    String userId = intent.getStringExtra(ProfilePictureData.USER_ID);
    if (!userId.equals(mProfilePictureData.getUserId())) {
      int startImgLocation = mProfilePictureData.getStartLocation();
      int maxSizeToLoad = (startImgLocation + 1) / LIST_DEFAULT_SIZE + 1;
      int currentSizeListImg = 0;
      if (mProfilePictureTheater != null) {
        currentSizeListImg = mProfilePictureTheater.getNumberOfImage();
      }
      maxSizeToLoad = maxSizeToLoad
          - (currentSizeListImg / LIST_DEFAULT_SIZE);
      loadListImage(maxSizeToLoad);
    }
    mProfilePictureTheater.setCurrentImg(position);
  }

  public void initView() {
    ((ProfilePictureTheaterFromProfile) mProfilePictureTheater)
        .setNavigatorBar();

    int saveImgPrice = Preferences.getInstance().getSaveImagePoints();
    ((ProfilePictureTheaterFromProfile) mProfilePictureTheater)
        .setBottomPanel(mProfilePictureData, saveImgPrice);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
  }

  @Override
  public void onPageChanged(int position) {
    super.onPageChanged(position);
    loadComunicateBtnData();
    mProfilePictureTheater.setNumberOfComment(-1);
    String myId = UserPreferences.getInstance().getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      boolean isCurrentAvata = false;
      if (position == mProfilePictureTheater.getmAvataImgIndex()) {
        isCurrentAvata = true;
      }
      mProfilePictureTheater.changeButtonWithAvataStatus(isCurrentAvata);
    }
  }

  @Override
  public void onBtnDeletePicClick(View v) {
    super.onBtnDeletePicClick(v);
    // Remove from theater
  }

  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case RESPONSE_LOAD_PROFILE_IMAGE:
      case RESPONSE_UPDATE_AVATA:
        mProfilePictureTheater.showProgressDialog(this);
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    mProfilePictureTheater.dismissProgressDialog();
    super.receiveResponse(loader, response);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }
}
