package com.application.ui.backstage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ListBackstageImageRequest;
import com.application.connection.request.RemoveBackstageImageRequest;
import com.application.connection.request.ReportRequest;
import com.application.constant.Constants;
import com.application.ui.profile.DetailPictureBaseActivity;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;

public class DetailPictureBackstageActivity extends DetailPictureBaseActivity
    implements ResponseReceiver {

  public static final String KEY_BUNDER_BACKSTAGE = "backstage_data";
  public static final int BUNDER_BACKSTAGE_NOT_SET = -1;
  public static final int BUNDER_BACKSTAGE_LOCK = 0;
  public static final int BUNDER_BACKSTAGE_DELETE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    mProfilePictureTheater = new ProfilePictureTheaterFromBackstage(this,
        mProfilePictureData, getImageFetcher());
    setContentView(mProfilePictureTheater.getView());
    loadListBackstageImage();
    initView();
    initialNotificationVew();
  }

  public void initView() {
    ((ProfilePictureTheaterFromBackstage) mProfilePictureTheater)
        .setNavigatorBar();

    boolean isMyProfile = false;
    String myId = UserPreferences.getInstance().getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      isMyProfile = true;
    }
    int saveImgPrice = Preferences.getInstance().getSaveImagePoints();
    ((ProfilePictureTheaterFromBackstage) mProfilePictureTheater)
        .setBottomPanel(isMyProfile, saveImgPrice);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
  }

  public void loadListBackstageImage() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    ListBackstageImageRequest listBackstageImageRequest;
    int currentSizeListImg = 0;
    if (mProfilePictureTheater != null) {
      currentSizeListImg = mProfilePictureTheater.getNumberOfImage();
    }
    String myId = userPreferences.getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      listBackstageImageRequest = new ListBackstageImageRequest(token,
          currentSizeListImg, mProfilePictureData.getNumberOfImage());
    } else {
      listBackstageImageRequest = new ListBackstageImageRequest(token,
          userId, currentSizeListImg,
          mProfilePictureData.getNumberOfImage());
    }
    restartRequestServer(RESPONSE_LOAD_BACKSTAGE_IMAGE,
        listBackstageImageRequest);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onViewPagerClick(View v) {
    mProfilePictureTheater.changeVisibleAllPanel();
  }

  @Override
  public void onDialogConfirmDelete(DialogInterface dialog, int which) {
    String token = UserPreferences.getInstance().getToken();
    String imageId = mProfilePictureTheater.getCurrentImage();
    RemoveBackstageImageRequest removeBackstageImageRequest = new RemoveBackstageImageRequest(
        token, imageId);
    restartRequestServer(RESPONSE_DELETE_BACKSTAGE,
        removeBackstageImageRequest);
  }

  @Override
  public void onDialogConfirmReport(DialogInterface dialog, int which,
      int reportType) {
    String token = UserPreferences.getInstance().getToken();
    String subject_id = mProfilePictureTheater.getCurrentImage();
    ReportRequest reportRequest = new ReportRequest(token, subject_id,
        reportType, Constants.REPORT_TYPE_IMAGE);
    restartRequestServer(RESPONSE_REPORT_IMAGE, reportRequest);
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