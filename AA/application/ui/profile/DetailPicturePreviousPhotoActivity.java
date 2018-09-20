package com.application.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import com.application.common.GalleryActivity;
import com.application.common.ProfilePictureTheaterFromPreviousPhoto;
import com.application.connection.Response;
import com.application.constant.Constants;

public class DetailPicturePreviousPhotoActivity extends
    DetailPictureBaseActivity {

  public static final String GALLERY_ACTIVITY = "gallery_activity";
  public static final String IMAGE_PICKER_ACTIVITY = "image_picker_activity";
  public static final String KEY_IS_GALLERY = "is_gallery";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    mProfilePictureTheater = new ProfilePictureTheaterFromPreviousPhoto(
        this, mProfilePictureData, getImageFetcher());
    setContentView(mProfilePictureTheater.getView());
    int sizeToLoad = mProfilePictureData.getNumberOfImage();
    loadListPreviousImage(sizeToLoad);
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
      loadListPreviousImage(maxSizeToLoad);
    }
    mProfilePictureTheater.setCurrentImg(position);
  }

  public void initView() {
    Bundle dataTransport = getIntent().getExtras();
    boolean isGallery = dataTransport.getBoolean(KEY_IS_GALLERY);
    ((ProfilePictureTheaterFromPreviousPhoto) mProfilePictureTheater)
        .setNavigatorBar(isGallery);
    ((ProfilePictureTheaterFromPreviousPhoto) mProfilePictureTheater)
        .setBottomPanel(mProfilePictureData);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
  }

  @Override
  public void onPageChanged(int position) {
    super.onPageChanged(position);
    if (mProfilePictureTheater instanceof ProfilePictureTheaterFromPreviousPhoto) {
      boolean isOwn = mProfilePictureData.isOwn(position);
      ProfilePictureTheaterFromPreviousPhoto view = (ProfilePictureTheaterFromPreviousPhoto) mProfilePictureTheater;
      view.onPageChanged(isOwn);
    }
  }

  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case RESPONSE_LOAD_PREVIOUS_IMAGE:
      case RESPONSE_UPDATE_AVATA:
        mProfilePictureTheater.showProgressDialog(this);
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
  }

  @Override
  public void onBtnDeletePicClick(View v) {
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

  @Override
  public void onBtnSeeAllClick(View v) {
    if (mProfilePictureTheater.getNumberOfImage() > 0) {
      Intent intent = new Intent(this, GalleryActivity.class);
      intent.putExtras(mProfilePictureData
          .getBundleFromDataForPreviousImage());
      startActivity(intent);
      finish();
    }
  }
}