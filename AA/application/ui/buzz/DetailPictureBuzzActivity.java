package com.application.ui.buzz;

import android.os.Bundle;
import android.support.v4.content.Loader;
import com.application.common.ProfilePictureTheaterFromBuzz;
import com.application.connection.Response;
import com.application.connection.ResponseReceiver;
import com.application.ui.profile.DetailPictureBaseActivity;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;

public class DetailPictureBuzzActivity extends DetailPictureBaseActivity
    implements ResponseReceiver {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    mProfilePictureTheater = new ProfilePictureTheaterFromBuzz(this,
        mProfilePictureData, getImageFetcher());

    // save if change activity from buzz to hide right button
    if (mProfilePictureData.getDataType() == ProfilePictureData.TYPE_BUZZ) {
      ((ProfilePictureTheaterFromBuzz) mProfilePictureTheater)
          .setWhereFrom(mProfilePictureData.getDataType());
    }

    setContentView(mProfilePictureTheater.getView());
    initView();
    loadActionPoint();
    mProfilePictureTheater.updateImage(mProfilePictureData.getListImg());
    String myId = UserPreferences.getInstance().getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      boolean isCurrentAvata = false;
      if (mProfilePictureData.getAvata().equals(
          mProfilePictureData.getListImg().get(0))) {
        isCurrentAvata = true;
      }
      mProfilePictureTheater.changeButtonWithAvataStatus(isCurrentAvata);
    }
    mProfilePictureTheater.setCurrentImg(mProfilePictureData
        .getStartLocation());
    loadComunicateBtnData();
    initialNotificationVew();
  }

  public void initView() {

    ((ProfilePictureTheaterFromBuzz) mProfilePictureTheater)
        .setNavigatorBar();

    boolean isMyProfile = false;
    String myId = UserPreferences.getInstance().getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      isMyProfile = true;
    }
    int saveImgPrice = Preferences.getInstance().getSaveImagePoints();
    ((ProfilePictureTheaterFromBuzz) mProfilePictureTheater)
        .setBottomPanel(mProfilePictureData, isMyProfile, saveImgPrice);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
  }

  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case RESPONSE_UPDATE_AVATA:
        mProfilePictureTheater.showProgressDialog(this);
      default:
        break;
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    mProfilePictureTheater.dismissProgressDialog();
    super.receiveResponse(loader, response);
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