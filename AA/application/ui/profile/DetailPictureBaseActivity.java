package com.application.ui.profile;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.application.common.GalleryActivity;
import com.application.common.ProfilePictureTheaterBase;
import com.application.common.ProfilePictureTheaterBase.OnProfilePictureClickListener;
import com.application.common.ProfilePictureTheaterBase.OnProfilePicturePageChangeListener;
import com.application.common.ProfilePictureTheaterFromProfile;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.BuzzDetailRequest;
import com.application.connection.request.DeleteBuzzRequest;
import com.application.connection.request.GetPointActionRequest;
import com.application.connection.request.LikeBuzzRequest;
import com.application.connection.request.ListPublicImageRequest;
import com.application.connection.request.ListSendImageRequest;
import com.application.connection.request.ReportRequest;
import com.application.connection.request.SaveImageRequest;
import com.application.connection.request.UpdateAvatarRequest;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.response.BuzzDetailResponse;
import com.application.connection.response.DeleteBuzzResponse;
import com.application.connection.response.GetPointActionResponse;
import com.application.connection.response.LikeBuzzResponse;
import com.application.connection.response.ListBackstageImageResponse;
import com.application.connection.response.ListPublicImageResponse;
import com.application.connection.response.ListSendImageResponse;
import com.application.connection.response.RemoveBackstageImageResponse;
import com.application.connection.response.ReportResponse;
import com.application.connection.response.SaveImageResponse;
import com.application.connection.response.UpdateAvatarResponse;
import com.application.connection.response.UploadImageResponse;
import com.application.constant.Constants;
import com.application.entity.BuzzListItem;
import com.application.event.DetailPictureEvent;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.backstage.DetailPictureBackstageActivity;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.util.ErrorString;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.StorageUtil;
import com.application.util.Utility;
import com.application.util.preferece.ImagePreferences;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.io.File;
import java.util.ArrayList;

public class DetailPictureBaseActivity extends BaseFragmentActivity implements
    ResponseReceiver, OnProfilePictureClickListener,
    OnProfilePicturePageChangeListener {

  public static final String KEY_UPDATE_AVATAR = "key_update_avatar";
  // send notification request reload data to update
  public static final String KEY_NOTI_RELOAD_DATA = "key_noti_reload_data";
  public static final Boolean NOTI_RELOAD_DATA = true; // request reload data
  protected final static int LIST_DEFAULT_SIZE = 25;
  protected final int RESPONSE_LOAD_PROFILE_IMAGE = 0;
  protected final int RESPONSE_LOAD_PREVIOUS_IMAGE = 1;
  protected final int RESPONSE_UPDATE_AVATA = 2;
  protected final int RESPONSE_DELETE_IMAGE = 3;
  protected final int RESPONSE_DELETE_BACKSTAGE = 4;
  protected final int RESPONSE_REPORT_IMAGE = 5;
  protected final int RESPONSE_LIKE_BUZZ = 6;
  protected final int RESPONSE_LOAD_BUZZ_DETAIL = 7;
  protected final int RESPONSE_LOAD_BACKSTAGE_IMAGE = 8;
  protected final int RESPONSE_SAVE_IMAGE = 9;
  protected final int RESPONSE_CHECK_POINT_ACTION = 10;
  protected final int RESPONSE_LOAD_POINT_ACTION = 11;
  protected ProfilePictureData mProfilePictureData = new ProfilePictureData();
  protected ProfilePictureTheaterBase mProfilePictureTheater;
  private boolean isAvatarDeleted = false;
  private ProgressDialog progressDialog;
  private UploadImageProgress uploadImageProgress = new UploadImageProgress() {

    @Override
    public void uploadImageSuccess(UploadImageResponse response) {
      if (this == null) {
        return;
      }
      if (progressDialog != null && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
      Toast.makeText(getApplicationContext(), R.string.upload_success,
          Toast.LENGTH_LONG).show();
      if (response.getIsApproved() == Constants.IS_APPROVED) {
        UserPreferences.getInstance().saveAvaId(response.getImgId());
        Intent intent = new Intent();
        intent.putExtra(KEY_UPDATE_AVATAR, response.getImgId());
        setResult(RESULT_OK, intent);
        finish();
      } else {
        LayoutInflater inflater = LayoutInflater.from(DetailPictureBaseActivity.this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);

        Builder builder = new Builder(DetailPictureBaseActivity.this);
        String title = getResources().getString(
            R.string.unapproved_image_dialog_title);
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);
        //builder.setTitle(title);
        String message = getResources().getString(
            R.string.unapproved_image_dialog_content);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ok, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });
        AlertDialog element = builder.show();
        int dividerId = element.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = element.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

        UserPreferences.getInstance().savePendingAva(
            response.getImgId());
      }
    }

    @Override
    public void uploadImageStart() {
      String message = getString(R.string.uploading_image);
      progressDialog = ProgressDialog.show(
          DetailPictureBaseActivity.this, "", message);
      progressDialog.setCancelable(false);
    }

    @Override
    public void uploadImageFail(int code) {
      if (progressDialog != null && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
      if (code == Response.SERVER_UPLOAD_IMAGE_ERROR) {
        com.application.ui.customeview.AlertDialog
            .showUploadImageErrorAlert(DetailPictureBaseActivity.this);
      } else {
        String message = getString(R.string.upload_fail);
        Toast.makeText(getApplicationContext(), message,
            Toast.LENGTH_LONG).show();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public void loadActionPoint() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String id = mProfilePictureData.getUserId();
    if (id == null || id.length() < 1) {
      id = userPreferences.getUserId();
    }
    GetPointActionRequest request = new GetPointActionRequest(token, "", id);
    restartRequestServer(RESPONSE_LOAD_POINT_ACTION, request);
  }

  public void loadTransportData() {
    Bundle dataTransport = getIntent().getExtras();
    if (dataTransport != null) {
      if (mProfilePictureData == null) {
        mProfilePictureData = new ProfilePictureData();
      }

      mProfilePictureData.setDataFromBundle(dataTransport);
    }
  }

  /**
   * Start call API to load all image
   */
  public void loadListImage(int sizeToLoad) {
    String token = UserPreferences.getInstance().getToken();
    ListPublicImageRequest listPublicImageRequest;
    int currentSizeListImg = 0;
    if (mProfilePictureTheater != null) {
      currentSizeListImg = mProfilePictureTheater.getNumberOfImage();
    }
    String myId = UserPreferences.getInstance().getUserId();
    String userId = mProfilePictureData.getUserId();
    if (myId.equals(userId)) {
      listPublicImageRequest = new ListPublicImageRequest(token,
          currentSizeListImg, LIST_DEFAULT_SIZE * sizeToLoad);
    } else {
      listPublicImageRequest = new ListPublicImageRequest(token, userId,
          currentSizeListImg, LIST_DEFAULT_SIZE * sizeToLoad);
    }
    restartRequestServer(RESPONSE_LOAD_PROFILE_IMAGE,
        listPublicImageRequest);
  }

  /**
   * Start call API to load all image
   */
  public void loadListPreviousImage(int sizeToLoad) {
    String token = UserPreferences.getInstance().getToken();
    ListSendImageRequest listSendImageRequest;
    int currentSizeListImg = 0;
    if (mProfilePictureTheater != null) {
      currentSizeListImg = mProfilePictureTheater.getNumberOfImage();
    }
    String userId = mProfilePictureData.getUserId();
    listSendImageRequest = new ListSendImageRequest(token, userId,
        currentSizeListImg, LIST_DEFAULT_SIZE * sizeToLoad);
    restartRequestServer(RESPONSE_LOAD_PREVIOUS_IMAGE, listSendImageRequest);
  }

  public void loadComunicateBtnData() {
    String token = UserPreferences.getInstance().getToken();
    BuzzDetailRequest buzzDetailRequest;
    int position = mProfilePictureTheater.getCurrentIndex();
    String buzz_id = mProfilePictureData.getBuzzId(position);
    buzzDetailRequest = new BuzzDetailRequest(token, buzz_id, 0);
    restartRequestServer(RESPONSE_LOAD_BUZZ_DETAIL, buzzDetailRequest);
  }

  /**
   * Update title of theater<br/> Check size of list image. If this size smaller than two, show "".
   * Title format be "<index> of <total>"
   */
  public void updateTheaterTitle() {
    int totalImage = mProfilePictureTheater.getNumberOfImage();
    int index = mProfilePictureTheater.getCurrentIndex() + 1;
    String format = getString(R.string.detail_picture_title);
    mProfilePictureTheater.setTheaterTitle(String.format(format, index,
        totalImage));
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void download(String url, String name) {
    // TODO: check with older version android API<=8
    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    Request request = new Request(Uri.parse(url));
    if (Utility.hasHoneycomb()) {
      request.allowScanningByMediaScanner();
      request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }
    request.setMimeType("image/png");
    request.setDestinationInExternalPublicDir(
        Environment.DIRECTORY_PICTURES, name);
    request.setTitle(getString(R.string.picture_downloading));

    request.setDescription(name);
    downloadManager.enqueue(request);
  }

  public void saveImage(int point) {
    mProfilePictureTheater.showProgressDialog(this);
    if (StorageUtil.saveImage(getApplicationContext(),
        mProfilePictureTheater.getCurrentFile())) {
      mProfilePictureTheater.showDialogSaveDone(true);
      mProfilePictureTheater.dismissProgressDialog();
    } else {
      mProfilePictureTheater.showDialogSaveDone(false);
    }
    UserPreferences.getInstance().saveNumberPoint(point);
  }

  /**
   * On page changed state
   */
  @Override
  public void onPageChanged(int position) {
    updateTheaterTitle();
  }

  @Override
  public void onViewPagerClick(View v) {
    mProfilePictureTheater.changeVisibleAllPanel();
  }

  @Override
  public void onBtnBackClick(View v) {
    finish();
  }

  @Override
  public void onBtnSeeAllClick(View v) {
    if (mProfilePictureTheater.getNumberOfImage() > 0) {
      Intent intent = new Intent(this, GalleryActivity.class);
      intent.putExtras(mProfilePictureData.getBundleFromData());
      startActivity(intent);
      finish();
    }
  }

  @Override
  public void onBtnCommentClick(View v) {
    if (mProfilePictureData == null || mProfilePictureData.getListImage().size() == 0) {
      return;
    }
    int position = mProfilePictureTheater.getCurrentIndex();
    String buzzId = mProfilePictureData.getBuzzId(position);
    EventBus.getDefault().post(new DetailPictureEvent(buzzId, Constants.BUZZ_DETAIL_OPTION_CMT));

    finish();
  }

  @Override
  public void onBtnChangeProfilePicClick(View v) {
//        MediaOptions.Builder builder = new MediaOptions.Builder();
//        MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//        MediaPickerActivity.open(this, ProfilePictureData.REQUEST_CODE_GET_IMAGE, options);

    new Gallery.Builder()
        .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
        .multiChoice(false)
        .fixAspectRatio(true)
        .cropOutput(true)
        .build()
        .start(this);
  }

  @Override
  public void onBtnSetProfilePicClick(View v) {
    mProfilePictureTheater.showDialogChangePicture();
  }

  private void updateAvataToServer() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String ava_id = mProfilePictureTheater.getCurrentImage();
    userPreferences.saveAvaId(ava_id);
    UpdateAvatarRequest updateAvatarRequest = new UpdateAvatarRequest(
        token, ava_id);
    restartRequestServer(RESPONSE_UPDATE_AVATA, updateAvatarRequest);
  }

  @Override
  public void onBtnSaveProfilePicClick(View v) {
    if (StorageUtil.hasExternalStorage()) {
      if (mProfilePictureTheater.isSavable()) {
        String token = UserPreferences.getInstance().getToken();
        GetPointActionRequest request = new GetPointActionRequest(
            token, mProfilePictureTheater.getCurrentImage());
        restartRequestServer(RESPONSE_CHECK_POINT_ACTION, request);
      } else {
        mProfilePictureTheater.showDialodSaveInvalid();
      }
    } else {
      mProfilePictureTheater.showDialodExternalStorageNotExist();
    }
  }

  @Override
  public void onBtnSaveMyProfilePicClick(View v) {
    if (StorageUtil.hasExternalStorage()) {
      if (mProfilePictureTheater.isSavable()) {
        int point = UserPreferences.getInstance().getNumberPoint();
        saveImage(point);
      } else {
        mProfilePictureTheater.showDialodSaveInvalid();
      }

    } else {
      mProfilePictureTheater.showDialodExternalStorageNotExist();
    }
  }

  @Override
  public void onBtnDeletePicClick(View v) {
    mProfilePictureTheater.showDialogConfirmDelete();
  }

  @Override
  public void onBtnReportPicClick(View v) {
    mProfilePictureTheater.showDialogReport();
  }

  @Override
  public void onBtnLikeClick(View v) {
    int newLikeType = mProfilePictureTheater.getLikeStatus();
    if (newLikeType != Constants.BUZZ_LIKE_TYPE_UNKNOW) {
      if (newLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
        newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
      } else {
        newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
      }
      String token = UserPreferences.getInstance().getToken();
      int position = mProfilePictureTheater.getCurrentIndex();
      String buzzId = mProfilePictureData.getBuzzId(position);
      LikeBuzzRequest likeBuzzRequest = new LikeBuzzRequest(token,
          buzzId, newLikeType);
      restartRequestServer(RESPONSE_LIKE_BUZZ, likeBuzzRequest);
      // mProfilePictureTheater.setBtnLikeStatus(newLikeType);
    }
  }

  @Override
  public void onBtnUserProfileClick(View v) {
    String userID = mProfilePictureData.getUserId();
    EventBus.getDefault()
        .post(new DetailPictureEvent(null, userID, Constants.BUZZ_DETAIL_OPTION_BACK_PROFILE));
    finish();
  }

  @Override
  public void onDialogConfirmDelete(DialogInterface dialog, int which) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = UserPreferences.getInstance().getToken();
    int currentImageIndex = mProfilePictureTheater.getCurrentIndex();
    String imageId = mProfilePictureTheater.getCurrentImage();
    String buzz_id = mProfilePictureData.getBuzzId(currentImageIndex);
    DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(token,
        buzz_id);
    restartRequestServer(RESPONSE_DELETE_IMAGE, deleteBuzzRequest);
    // tungdx added:
    if (imageId != null
        && imageId.equals(UserPreferences.getInstance().getAvaId())) {
      isAvatarDeleted = true;
    }
  }

  @Override
  public void onDialogReportImage(DialogInterface dialog, int which) {
    if (which != 0) {
      int reportType = 0;
      Resources resource = getResources();
      String[] reportTypes = resource.getStringArray(R.array.report_type);
      String[] reportUsers = resource
          .getStringArray(R.array.report_content_type);
      String reportString = reportUsers[which];
      int length = reportTypes.length;
      for (int i = 0; i < length; i++) {
        if (reportString.equals(reportTypes[i])) {
          reportType = i;
        }
      }
      mProfilePictureTheater.showDialogConfirmReport(reportType);
    }
  }

  @Override
  public void onDialogConfirmReport(DialogInterface dialog, int which,
      int reportType) {
    String token = UserPreferences.getInstance().getToken();
    // String subject_id = mProfilePictureData
    // .getBuzzId(mProfilePictureTheater.getCurrentIndex());

    String subject_id = mProfilePictureData
        .getImageId(mProfilePictureTheater.getCurrentIndex());

    ReportRequest reportRequest = new ReportRequest(token, subject_id,
        reportType, Constants.REPORT_TYPE_IMAGE);
    restartRequestServer(RESPONSE_REPORT_IMAGE, reportRequest);
  }

  private void handleCheckActionPoint(GetPointActionResponse response,
      int loaderId) {
    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      int price = response.getSavePoint();
      if (loaderId == RESPONSE_CHECK_POINT_ACTION) {
        String imgId = mProfilePictureTheater.getCurrentImage();
        mProfilePictureTheater.showDialodSave(price, imgId);
      } else if (loaderId == RESPONSE_LOAD_POINT_ACTION) {
        if (this instanceof DetailPicturePreviousPhotoActivity) {
          boolean isMy = mProfilePictureData
              .isOwn(mProfilePictureTheater.getCurrentIndex());
          mProfilePictureTheater.syncSaveButton(isMy, price);
        } else {
          mProfilePictureTheater.syncSaveButton(
              mProfilePictureData.getUserId(), price);
        }
      }
    } else {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
        int price = response.getSavePoint();
        NotEnoughPointDialog.showForSaveChatPicture(this, price);
      } else {
        com.application.ui.customeview.ErrorApiDialog.showAlert(this,
            R.string.common_error, response.getCode());
      }
    }
  }

  /**
   * Communicate with server.<br/> The base activity will connect to server and handle data call
   * back.
   */
  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case RESPONSE_LOAD_PROFILE_IMAGE:
      case RESPONSE_LOAD_BACKSTAGE_IMAGE:
      case RESPONSE_LOAD_PREVIOUS_IMAGE:
        mProfilePictureTheater.showProgressDialog(this);
      default:
        break;
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    int code = response.getCode();
    if (response instanceof SaveImageResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      if (code == Response.SERVER_NOT_ENOUGHT_MONEY) {
        int price = ((SaveImageResponse) response).getSavePoint();
        NotEnoughPointDialog.showForSavePicture(this, price);
      } else if (code == Response.CLIENT_ERROR_NO_CONNECTION) {
        ErrorApiDialog.showAlert(this, R.string.common_error, code);
      } else if (code == Response.SERVER_SUCCESS) {
        SaveImageResponse saveImageResponse = (SaveImageResponse) response;
        saveImage(saveImageResponse.getPoint());
      }
      return;
    } else if (response instanceof LikeBuzzResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      switch (code) {
        case Response.SERVER_SUCCESS:
          int newLikeType = mProfilePictureTheater.getLikeStatus();

          if (newLikeType != Constants.BUZZ_LIKE_TYPE_UNKNOW) {

            if (newLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
              newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
            } else {
              newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
            }

            mProfilePictureTheater.setBtnLikeStatus(newLikeType);
          }
          break;
        case Response.SERVER_BUZZ_NOT_FOUND:
        case Response.SERVER_ACCESS_DENIED:
        case Response.SERVER_COMMENT_NOT_FOUND:
          mProfilePictureTheater.showDialogImgNotFound(
              R.string.dialog_image_not_found_content, true);
          break;
        default:
          break;
      }
    }
    if (response instanceof UpdateAvatarResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      UpdateAvatarResponse avatarResponse = (UpdateAvatarResponse) response;

      if (avatarResponse.getCode() == Response.SERVER_SUCCESS) {
        UserPreferences.getInstance().saveAvaId(
            mProfilePictureTheater.getCurrentImage());
        Intent intent = new Intent();
        intent.putExtra(KEY_UPDATE_AVATAR,
            mProfilePictureTheater.getCurrentImage());

        // send request reload data
        intent.putExtra(KEY_NOTI_RELOAD_DATA, NOTI_RELOAD_DATA);

        setResult(RESULT_OK, intent);
        finish();
      } else if (avatarResponse.getCode() == Response.SERVER_BUZZ_NOT_FOUND
          || avatarResponse.getCode() == Response.SERVER_ACCESS_DENIED) {
        mProfilePictureTheater.showDialogImgNotFound(
            R.string.dialog_image_not_found_content, true);
      } else if (avatarResponse.getCode() == Response.SERVER_FORBIDDEN_IMAGE) {
        mProfilePictureTheater.showDialogImgNotFound(
            R.string.dialog_image_forbidden_content, true);
      }

    } else if (response instanceof ListPublicImageResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      ListPublicImageResponse listPublicImageResponse = (ListPublicImageResponse) response;

      if (listPublicImageResponse.getCode() == Response.SERVER_SUCCESS) {
        mProfilePictureData.addListImg(listPublicImageResponse
            .getListImage());
        ArrayList<String> listImage = mProfilePictureData.getListImg();
        int sizeList = listImage.size();
        LogUtils.d("sizeList", "" + sizeList);

        if (sizeList < 1) {
          mProfilePictureTheater.showDialogImgNotFound(
              R.string.dialog_image_not_found_content, true);
        } else {
          int avataIndex = -1;
          int startImgLocation = -1;
          for (int i = 0; i < sizeList; i++) {
            String imgId = listImage.get(i);

            if (imgId.equals(mProfilePictureData.getImageId())) {
              startImgLocation = i;
            }

            if (imgId.equals(mProfilePictureData.getAvata())) {
              avataIndex = i;
            }
          }
          mProfilePictureData
              .setDataType(ProfilePictureData.TYPE_UNKNOW);

          if (startImgLocation < 0) {
            startImgLocation = 0;
            mProfilePictureTheater.showDialogImgNotFound(
                R.string.dialog_image_not_found_content, false);
          }
          mProfilePictureTheater.updateImage(mProfilePictureData
              .getListImg());
          mProfilePictureTheater.setmAvataImgIndex(avataIndex);
          mProfilePictureTheater.setCurrentImg(startImgLocation);
          loadComunicateBtnData();
          updateTheaterTitle();

          if (mProfilePictureTheater instanceof ProfilePictureTheaterFromProfile) {
            String myId = UserPreferences.getInstance().getUserId();
            String userId = mProfilePictureData.getUserId();

            if (myId.equals(userId)) {
              boolean isCurrentAvata = false;

              if (startImgLocation == mProfilePictureTheater
                  .getmAvataImgIndex()) {
                isCurrentAvata = true;
              }
              mProfilePictureTheater
                  .changeButtonWithAvataStatus(isCurrentAvata);
            }
          }
        }
      } else {
        LogUtils.d("DetailPictureBaseActivity",
            "FinishActivity: list public image");
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);

        AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);

        String title = getString(R.string.common_error);
        int message = R.string.alert;
        message = ErrorString
            .getDescriptionOfErrorCode(listPublicImageResponse
                .getCode());
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);

        //builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.common_yes,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                finish();
              }
            });

        AlertDialog dialog = builder.create();
        dialog.show();
        int dividerId = dialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
      }
    } else if (response instanceof ListSendImageResponse) {
      ListSendImageResponse listSendImageResponse = (ListSendImageResponse) response;

      if (listSendImageResponse.getCode() == Response.SERVER_SUCCESS) {
        mProfilePictureData.addListImg(listSendImageResponse
            .getListImage());
        ArrayList<String> listImage = mProfilePictureData.getListImg();
        int sizeList = listImage.size();
        LogUtils.d("sizeList", "" + sizeList);

        int total = listSendImageResponse.getTotal();
        mProfilePictureData.setNumberOfImage(total);
        if (sizeList < total) {
          loadListPreviousImage(total);
        } else {
          if (mProfilePictureTheater != null) {
            mProfilePictureTheater.dismissProgressDialog();
          }
          if (sizeList < 1) {
            mProfilePictureTheater.showDialogImgNotFound(
                R.string.dialog_backstage_not_found_content,
                true);
          } else {
            int avataIndex = -1;
            int startImgLocation = -1;
            for (int i = 0; i < sizeList; i++) {
              String imgId = listImage.get(i);

              if (imgId.equals(mProfilePictureData.getImageId())) {
                startImgLocation = i;
              }

              if (imgId.equals(mProfilePictureData.getAvata())) {
                avataIndex = i;
              }
            }
            mProfilePictureData
                .setDataType(ProfilePictureData.TYPE_UNKNOW);

            if (startImgLocation < 0) {
              startImgLocation = 0;
              mProfilePictureTheater
                  .showDialogImgNotFound(
                      R.string.dialog_backstage_not_found_content,
                      false);
            }
            mProfilePictureTheater.updateImage(mProfilePictureData
                .getListImg());
            mProfilePictureTheater.setmAvataImgIndex(avataIndex);
            mProfilePictureTheater.setCurrentImg(startImgLocation);
            mProfilePictureTheater.onPageSelected(startImgLocation);
            updateTheaterTitle();

            if (mProfilePictureTheater instanceof ProfilePictureTheaterFromProfile) {
              String myId = UserPreferences.getInstance()
                  .getUserId();
              String userId = mProfilePictureData.getUserId();

              if (myId.equals(userId)) {
                boolean isCurrentAvata = false;

                if (startImgLocation == mProfilePictureTheater
                    .getmAvataImgIndex()) {
                  isCurrentAvata = true;
                }
                mProfilePictureTheater
                    .changeButtonWithAvataStatus(isCurrentAvata);
              }
            }
          }
        }
      } else {
        LogUtils.d("DetailPictureBaseActivity",
            "FinishActivity: list public image");
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);
        AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);

        String title = getString(R.string.common_error);
        int message = R.string.alert;
        message = ErrorString
            .getDescriptionOfErrorCode(listSendImageResponse
                .getCode());
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);
        //builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.common_yes,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                finish();
              }
            });

        AlertDialog dialog = builder.create();
        dialog.show();
        int dividerId = dialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
      }
    } else if (response instanceof DeleteBuzzResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      DeleteBuzzResponse deleteBuzzResponse = (DeleteBuzzResponse) response;

      if (deleteBuzzResponse.getCode() == Response.SERVER_SUCCESS) {

        if (isAvatarDeleted) {
          UserPreferences.getInstance().removeAvaId();
          isAvatarDeleted = false;
        }
        Toast.makeText(this, R.string.common_success,
            Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
      } else {
        isAvatarDeleted = false;
        Toast.makeText(this, R.string.common_fail, Toast.LENGTH_SHORT)
            .show();
      }
    } else if (response instanceof RemoveBackstageImageResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      RemoveBackstageImageResponse removeBackstageImageResponse = (RemoveBackstageImageResponse) response;

      if (removeBackstageImageResponse.getCode() == Response.SERVER_SUCCESS) {
        // tungdx added: save image id has been deleted
        ImagePreferences preferences = new ImagePreferences(
            getApplicationContext());

        if (mProfilePictureTheater != null) {
          preferences.saveImageDeleted(mProfilePictureTheater
              .getCurrentImage());
        }
        // --ended

        Intent intent = new Intent();
        intent.putExtra(
            DetailPictureBackstageActivity.KEY_BUNDER_BACKSTAGE,
            DetailPictureBackstageActivity.BUNDER_BACKSTAGE_DELETE);
        setResult(RESULT_OK, intent);
        finish();
      } else {
        Toast.makeText(this, R.string.common_fail, Toast.LENGTH_SHORT)
            .show();
      }
    } else if (response instanceof ReportResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      ReportResponse reportResponse = (ReportResponse) response;

      if (reportResponse.getCode() == Response.SERVER_SUCCESS) {
        if (!reportResponse.isAppear()) {
          finish();
        }
      } else if (reportResponse.getCode() == Response.SERVER_BUZZ_NOT_FOUND
          || reportResponse.getCode() == Response.SERVER_ACCESS_DENIED) {
        mProfilePictureTheater.showDialogImgNotFound(
            R.string.dialog_image_not_found_content, true);
      }
    } else if (response instanceof BuzzDetailResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      BuzzDetailResponse reportResponse = (BuzzDetailResponse) response;
      if (reportResponse.getCode() == Response.SERVER_SUCCESS) {
        BuzzListItem buzzItem = reportResponse.getBuzzDetail();
        if (buzzItem != null) {
          int position = mProfilePictureTheater.getCurrentIndex();
          String currentBuzzID = mProfilePictureData
              .getBuzzId(position);
          String serverBuzzId = buzzItem.getBuzzId();
          if (currentBuzzID.equals(serverBuzzId)) {
            mProfilePictureTheater.lockScreen(true);
            mProfilePictureTheater.setNumberOfComment(buzzItem
                .getCommentNumber());
            mProfilePictureTheater.setBtnLikeStatus(buzzItem
                .getIsLike());
            mProfilePictureTheater.lockScreen(false);
          }
        }
      } else if (reportResponse.getCode() == Response.SERVER_BUZZ_NOT_FOUND
          || reportResponse.getCode() == Response.SERVER_ACCESS_DENIED) {
        mProfilePictureTheater.showDialogImgNotFound(
            R.string.dialog_image_not_found_content, true);
      }
    } else if (response instanceof ListBackstageImageResponse) {
      if (mProfilePictureTheater != null) {
        mProfilePictureTheater.dismissProgressDialog();
      }
      ListBackstageImageResponse listBackstageImageResponse = (ListBackstageImageResponse) response;

      if (listBackstageImageResponse.getCode() == Response.SERVER_SUCCESS) {
        mProfilePictureData.addListImg(listBackstageImageResponse
            .getListImage());
        ArrayList<String> listImage = mProfilePictureData.getListImg();
        int sizeList = listImage.size();

        if (sizeList < 1) {
          mProfilePictureTheater.showDialogImgNotFound(
              R.string.dialog_backstage_not_found_content, true);
        } else {

          int startImgLocation = mProfilePictureData
              .getStartLocation();

          if (startImgLocation >= sizeList) {
            startImgLocation = 0;
          }

          if (!listImage.get(startImgLocation).equals(
              mProfilePictureData.getImageId())) {
            if (!listImage.get(startImgLocation).equals(
                mProfilePictureData.getImageId())) {
              String currentImgId = mProfilePictureData
                  .getImageId();
              startImgLocation = listImage.indexOf(currentImgId);
              if (startImgLocation < 0) {
                startImgLocation = 0;
                mProfilePictureTheater
                    .showDialogImgNotFound(
                        R.string.dialog_backstage_not_found_content,
                        false);
              }
            }
          }
          mProfilePictureTheater.updateImage(mProfilePictureData
              .getListImg());
          mProfilePictureTheater.setCurrentImg(startImgLocation);
          updateTheaterTitle();
        }

      } else if (listBackstageImageResponse.getCode() == Response.SERVER_LOCKED_FEARUTE) {
        Intent intent = new Intent();
        intent.putExtra(
            DetailPictureBackstageActivity.KEY_BUNDER_BACKSTAGE,
            DetailPictureBackstageActivity.BUNDER_BACKSTAGE_LOCK);
        setResult(RESULT_OK, intent);
        finish();
      } else {

        LogUtils.e("DetailPictureBaseActivity",
            "FinishActivity: list backstage");
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);
        AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);

        String title = getString(R.string.common_error);
        int message = R.string.alert;
        message = ErrorString
            .getDescriptionOfErrorCode(listBackstageImageResponse
                .getCode());
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);

        //builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.common_yes,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                finish();
              }
            });

        AlertDialog dialog = builder.create();
        dialog.show();
        int dividerId = dialog.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
      }
    } else if (response instanceof GetPointActionResponse) {
      handleCheckActionPoint((GetPointActionResponse) response,
          loader.getId());
    }
    if (code != Response.SERVER_SUCCESS) {
      if (response instanceof ListPublicImageResponse
          || response instanceof ListBackstageImageResponse) {
        if (mProfilePictureTheater != null) {
          mProfilePictureTheater.dismissProgressDialog();
        }
      } else {
        ErrorApiDialog.showAlert(this, R.string.common_error, code);
        return;
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case RESPONSE_UPDATE_AVATA:
        response = new UpdateAvatarResponse(data);
        break;
      case RESPONSE_LOAD_PROFILE_IMAGE:
        response = new ListPublicImageResponse(data);
        break;
      case RESPONSE_LOAD_PREVIOUS_IMAGE:
        response = new ListSendImageResponse(data);
        break;
      case RESPONSE_LOAD_BACKSTAGE_IMAGE:
        response = new ListBackstageImageResponse(data);
        break;
      case RESPONSE_DELETE_IMAGE:
        response = new DeleteBuzzResponse(data);
        break;
      case RESPONSE_DELETE_BACKSTAGE:
        response = new RemoveBackstageImageResponse(data);
        break;
      case RESPONSE_REPORT_IMAGE:
        response = new ReportResponse(data);
        break;
      case RESPONSE_LOAD_BUZZ_DETAIL:
        response = new BuzzDetailResponse(data);
        break;
      case RESPONSE_SAVE_IMAGE:
        response = new SaveImageResponse(data);
        break;
      case RESPONSE_LIKE_BUZZ:
        response = new LikeBuzzResponse(data);
        break;
      case RESPONSE_CHECK_POINT_ACTION:
      case RESPONSE_LOAD_POINT_ACTION:
        response = new GetPointActionResponse(data);
        break;
      default:
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
//			if (requestCode == ProfilePictureData.REQUEST_CODE_GET_IMAGE) {
//                ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//                if (mMediaSelectedList != null) {
//                    for (final MediaItem mediaItem : mMediaSelectedList) {
//                        String imgPath = mediaItem.getCroppedPath();
//                        if (imgPath == null) {
//                            imgPath = mediaItem.getOriginPath();
//                        }
//                        uploadAvatarToServer(imgPath);
//                    }
//                } else {
//                    LogUtils.e(TAG, "Error to get media, NULL");
//                }
//            }

      switch (requestCode) {
        case Camera.REQUEST_CODE_CAMERA:
        case Gallery.REQUEST_CODE_GALLERY:
          Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
          for (Parcelable parcelable : files) {
            MediaFile file = (MediaFile) parcelable;
            uploadAvatarToServer(file.getPath());
          }
          break;
      }
    }
  }

  private void uploadAvatarToServer(String imagePath) {
    ImageUploader imageUploader = new ImageUploader(uploadImageProgress);
    if (null != imagePath) {
      File file = new File(imagePath);
      String token = UserPreferences.getInstance().getToken();
      String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
      UploadImageRequest imageRequest = new UploadImageRequest(token,
          UploadImageRequest.AVATAR, file, md5Encrypted);
      imageUploader.execute(imageRequest);
    }
  }

  @Override
  public void onDialogImgNotFound(boolean isBack) {
    setResult(RESULT_OK);
    if (isBack) {
      finish();
    }
  }

  @Override
  public void onDialogChangePicture() {
    updateAvataToServer();
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  public void onDialogSave(String imgId) {
    String token = UserPreferences.getInstance().getToken();
    SaveImageRequest request = new SaveImageRequest(token, imgId);
    restartRequestServer(RESPONSE_SAVE_IMAGE, request);
  }
}