package com.application.ui.backstage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.RemoveBackstageImageRequest;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.profile.DetailPictureBaseActivity;
import com.application.util.preferece.ImagePreferences;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;


public class DetailPictureBackstageApproveActivity extends
    DetailPictureBaseActivity implements ResponseReceiver {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadTransportData();
    String imageId = mProfilePictureData.getImageId();
    if (!imageAvaiable(imageId)) {
      // remove imageId by ""
      mProfilePictureData.setImageId("");
      showImageHasDeletedDialog();
    }
    mProfilePictureTheater = new ProfilePictureTheaterFromBackstage(this,
        mProfilePictureData, getImageFetcher());
    setContentView(mProfilePictureTheater.getView());
    ArrayList<String> imgApproved = new ArrayList<String>();
    imgApproved.add(mProfilePictureData.getImageId());
    mProfilePictureTheater.updateImage(imgApproved);
    initView();
    initialNotificationVew();
    updateTheaterTitle();
  }

  public void initView() {
    ((ProfilePictureTheaterFromBackstage) mProfilePictureTheater)
        .setNavigatorBar();

    int saveImgPrice = Preferences.getInstance().getSaveImagePoints();
    ((ProfilePictureTheaterFromBackstage) mProfilePictureTheater)
        .setBottomPanel(true, saveImgPrice);
    mProfilePictureTheater.setOnPageChangeListener(this);
    mProfilePictureTheater.setOnClickListener(this);
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
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  /**
   * @author tungdx Check has image been deleted or not notify
   */
  private boolean imageAvaiable(String imageId) {
    ImagePreferences imagePreferences = new ImagePreferences(
        getApplicationContext());
    return !imagePreferences.hasImageDeleted(imageId);

  }

  /**
   * @author tungdx show dialog when has been dialog
   */
  private void showImageHasDeletedDialog() {
    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.common_error);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.common_error);
    builder.setMessage(R.string.image_deleted);
    builder.setPositiveButton(R.string.ok, new OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
    builder.setCancelable(false);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }
}