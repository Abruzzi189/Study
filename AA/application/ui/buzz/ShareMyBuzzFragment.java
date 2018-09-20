package com.application.ui.buzz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddBuzzRequest;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.response.AddBuzzResponse;
import com.application.connection.response.UploadImageResponse;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import glas.bbsystem.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ShareMyBuzzFragment extends BaseFragment implements
    OnClickListener, OnNavigationClickListener, ResponseReceiver {

  private final static String KEY_FILE_PATH = "file_path";
  private final static String KEY_IS_BACK = "share_status";
  private final static int REQUEST_TYPE_COMMENT = 0;
  private final static int REQUEST_TYPE_IMAGE = 1;
  private static final int LOADER_ADD_BUZZ = 0;
  private ImageView mImgAvatar;
  private ImageView mImgCapture;
  private TextView mTxtName;
  private EditText mEdtComment;
  private ProgressDialog progressDialog;
  private int REQUEST_CODE_CAMERA_IMAGE_CAPTURE = 1;
  private String mSelectedImagePath = "";
  private boolean mHasBackNavigation = false;
  private Button mButtonSend;
  private Handler mKeyboardHandler;
  private UploadImageProgress uploadImageProgress = new UploadImageProgress() {

    @Override
    public void uploadImageSuccess(UploadImageResponse response) {
      if (getActivity() == null) {
        return;
      }
      String token = UserPreferences.getInstance().getToken();
      AddBuzzRequest buzzRequest = null;
      String msg = mEdtComment.getText().toString()
          .replace("\u3000", " ").trim();
      if (msg.length() > 0) {
        buzzRequest = new AddBuzzRequest(token, response.getImgId(),
            REQUEST_TYPE_IMAGE, msg);
      } else {
        buzzRequest = new AddBuzzRequest(token, response.getImgId(),
            REQUEST_TYPE_IMAGE);
      }
      restartRequestServer(LOADER_ADD_BUZZ, buzzRequest);
      mEdtComment.setText("");
    }

    @Override
    public void uploadImageStart() {
      // String message = getString(R.string.uploading_image);
      showDialogWaiting();
    }

    @Override
    public void uploadImageFail(int code) {
      String message = getString(R.string.upload_fail);
      Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
      if (progressDialog != null && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }
    }
  };

  public static ShareMyBuzzFragment newInstance(String path, boolean isBack) {
    ShareMyBuzzFragment buzzFragment = new ShareMyBuzzFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_FILE_PATH, path);
    bundle.putBoolean(KEY_IS_BACK, isBack);
    buzzFragment.setArguments(bundle);
    return buzzFragment;
  }

  private static Bitmap decodeFile(String path) {
    int orientation;
    try {
      if (path == null) {
        return null;
      }
      // decode image size
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 1;
      Bitmap bm = BitmapFactory.decodeFile(path, options);
      Bitmap bitmap = bm;

      ExifInterface exif = new ExifInterface(path);
      orientation = exif
          .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
      Matrix m = new Matrix();
      if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
        m.postRotate(180);
        // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
        // if(m.preRotate(90)){
        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
            bm.getHeight(), m, true);
        return bitmap;
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
        m.postRotate(90);
        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
            bm.getHeight(), m, true);
        return bitmap;
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
        m.postRotate(270);
        bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
            bm.getHeight(), m, true);
        return bitmap;
      }
      return bitmap;
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mEdtComment.isFocused()) {
      mEdtComment.clearFocus();
    }
    mEdtComment.requestFocus();
    InputMethodManager keyboard = (InputMethodManager) mAppContext
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    keyboard.showSoftInput(mEdtComment, InputMethodManager.SHOW_FORCED);
  }

  @Override
  public void onPause() {
    super.onPause();
    hideKeyboard();
    // Utility.hideSoftKeyboard(getActivity());
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_FILE_PATH, mSelectedImagePath);
    outState.putBoolean(KEY_IS_BACK, mHasBackNavigation);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    requestDirtyWord();
    View view = inflater.inflate(R.layout.fragment_share_my_buzz,
        container, false);
    initialView(view);
    initImageFetcher();
    if (getArguments() != null) {
      mEdtComment.setText("");
      mHasBackNavigation = getArguments().getBoolean(KEY_IS_BACK);
      mSelectedImagePath = getArguments().getString(KEY_FILE_PATH);
      if (!TextUtils.isEmpty(mSelectedImagePath)) {
        Bitmap bitmap = decodeFile(mSelectedImagePath);
        mImgCapture.setImageBitmap(bitmap);
        mEdtComment.setHint(getActivity().getResources().getString(
            R.string.share_my_buzz_hint_picture));
      }
    } else if (savedInstanceState != null) {
      mSelectedImagePath = savedInstanceState.getString(KEY_FILE_PATH);
      mHasBackNavigation = savedInstanceState.getBoolean(KEY_IS_BACK);
    }
    return view;
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  private void initialView(View view) {
    mImgAvatar = (ImageView) view
        .findViewById(R.id.fragment_share_my_buzz_img_avatar);
    mImgCapture = (ImageView) view
        .findViewById(R.id.fragment_share_my_buzz_img_Capture);
    mTxtName = (TextView) view
        .findViewById(R.id.fragment_share_my_buzz_txt_name);
    mEdtComment = (EditText) view
        .findViewById(R.id.fragment_share_my_buzz_edt_comment);
    mImgCapture.setOnClickListener(this);
    String userName = UserPreferences.getInstance().getUserName();
    mTxtName.setText(userName);
    mEdtComment.requestFocus();
    mKeyboardHandler = Utility.showDelayKeyboard(mEdtComment, 200);
  }

  /**
   * SetUp ImageFetcher to load avatar
   */
  private void initImageFetcher() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String imag_id = userPreferences.getAvaId();
    int gender = userPreferences.getGender();
    int avatarHeightSize = getActivity().getResources()
        .getDimensionPixelSize(
            R.dimen.activity_setupprofile_img_avatar_height);
    int avatarWidthSize = getActivity().getResources()
        .getDimensionPixelSize(
            R.dimen.activity_setupprofile_img_avatar_width);

    ImageRequest imageRequest = new ImageRequest(token, imag_id,
        ImageRequest.THUMBNAIL);
    getImageFetcher().loadImageByGender(imageRequest, mImgAvatar,
        avatarWidthSize, avatarHeightSize, gender);
  }

  /**
   * Setup Navigation bar for this fragment
   */
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setCenterTitle(R.string.share_my_buzz_title);
    getNavigationBar().setNavigationRightTitle(R.string.send);
    getNavigationBar().setShowUnreadMessage(false);
    mButtonSend = (Button) getNavigationBar().findViewById(
        R.id.cv_navigation_bar_btn_right);
    changeSendButtonStatus();
    if (mHasBackNavigation) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
      getNavigationBar().setCenterTitle(R.string.share_my_buzz_title);
    } else {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    }
  }

  private void changeSendButtonStatus() {
    if (getActivity() == null || mButtonSend == null) {
      return;
    }
    if (!TextUtils.isEmpty(mSelectedImagePath)
        || (mEdtComment != null && !TextUtils.isEmpty(mEdtComment
        .getText().toString()))) {
      mButtonSend.setTextColor(getActivity().getResources().getColor(
          android.R.color.white));
    } else {
      mButtonSend.setTextColor(getActivity().getResources().getColor(
          R.color.send_disable_share_my_buzz));
    }
  }

  @Override
  public void onNavigationLeftClick(View view) {
    hideKeyboard();
    // Utility.hideSoftKeyboard(getActivity());
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    // Utility.hideSoftKeyboard(getActivity());
    hideKeyboard();
    addBuzzRequest();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.fragment_share_my_buzz_img_Capture:
        hideKeyboard();
        captureImage();
        break;
      default:
        break;
    }
  }

  private void captureImage() {
    mSelectedImagePath = "";
//        MediaOptions.Builder builder = new MediaOptions.Builder();
//        MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//        MediaPickerActivity.open(this, REQUEST_CODE_CAMERA_IMAGE_CAPTURE, options);

    new Gallery.Builder()
        .cropOutput(true)
        .fixAspectRatio(true)
        .multiChoice(false)
        .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
        .build()
        .start(this);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
//			changeSendButtonStatus();
//			mEdtComment.setHint(getActivity().getResources().getString(
//					R.string.share_my_buzz_hint_picture));
//			ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//			if (mMediaSelectedList != null) {
//				for (final MediaItem mediaItem : mMediaSelectedList) {
//					mSelectedImagePath = mediaItem.getCroppedPath();
//					Bitmap bitmap = decodeFile(mSelectedImagePath);
//					mImgCapture.setImageBitmap(bitmap);
//				}
//			} else {
//				LogUtils.e(TAG, "Error to get media, NULL");
//			}

      switch (requestCode) {
        case Camera.REQUEST_CODE_CAMERA:
        case Gallery.REQUEST_CODE_GALLERY:
          changeSendButtonStatus();
          mEdtComment
              .setHint(getActivity().getResources().getString(R.string.share_my_buzz_hint_picture));

          Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
          for (Parcelable parcelable : files) {
            MediaFile file = (MediaFile) parcelable;
            mSelectedImagePath = file.getPath();
            Bitmap bitmap = decodeFile(mSelectedImagePath);
            mImgCapture.setImageBitmap(bitmap);
          }
          break;
      }
    }
  }

  private void addBuzzRequest() {
    LogUtils.e("addBuzzRequest", "string="
        + mEdtComment.getText().toString().replace(" ", "").length());
    String token = UserPreferences.getInstance().getToken();
    String msg = mEdtComment.getText().toString().replace("\u3000", " ")
        .trim();
    if (!Utility.isContainDirtyWord(getActivity(), mEdtComment)) {
      if (mSelectedImagePath != null && mSelectedImagePath.length() > 0) {
        uploadImageToServer(new File(mSelectedImagePath));
        mSelectedImagePath = null;
      } else if (msg.length() > 0) {
        AddBuzzRequest buzzRequest = new AddBuzzRequest(token, msg,
            REQUEST_TYPE_COMMENT);
        restartRequestServer(LOADER_ADD_BUZZ, buzzRequest);
        showDialogWaiting();
        mEdtComment.setText("");
      }
    }
  }

  /**
   * Upload image to server after register success
   */
  private void uploadImageToServer(File file) {
    ImageUploader imageUploader = new ImageUploader(uploadImageProgress);
    String token = UserPreferences.getInstance().getToken();
    String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
    UploadImageRequest imageRequest = new UploadImageRequest(token,
        UploadImageRequest.PUBLISH_IMAGE, file, md5Encrypted);
    imageUploader.execute(imageRequest);
  }

  private void showDialogWaiting() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setCancelable(false);
      progressDialog.setMessage(getString(R.string.waiting));
    }
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }

  @Override
  public void startRequest(int loaderId) {
  }

  private void showBuzzFragment() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    MainActivity activity = (MainActivity) getActivity();
    if (activity != null) {
      Preferences.getInstance().saveBuzzTab(BuzzFragment.TAB_LOCAL);
      activity.replaceAllFragment(new BuzzFragment(),
          MainActivity.TAG_FRAGMENT_MY_BUZZ);
    } else {
      LogUtils.e("activity != null", "activity != null");
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (progressDialog != null) {
      progressDialog.dismiss();
    }

    if (response instanceof AddBuzzResponse) {
      if (response.getCode() == Response.SERVER_SUCCESS) {
        showBuzzFragment();
      } else {
        ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
            response.getCode());
      }
    }
  }

  private void shareFB() {
    byte[] bytes = null;
    if (!TextUtils.isEmpty(mSelectedImagePath)) {
      try {
        bytes = convertStreamToBytes(new FileInputStream(new File(
            mSelectedImagePath)));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    String msg = mEdtComment.getText().toString().replace("\u3000", " ")
        .trim();

    if (bytes != null || msg.length() > 0) {
      getFacebookController().shareStatus(msg, bytes);
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response respone = null;
    if (loaderID == LOADER_ADD_BUZZ) {
      respone = new AddBuzzResponse(data);
    }
    return respone;
  }

  private byte[] convertStreamToBytes(InputStream stream) throws IOException {
    if (stream == null) {
      return null;
    }
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copyStream(stream, output);
    return output.toByteArray();
  }

  private void copyStream(InputStream from, OutputStream to)
      throws IOException {
    byte data[] = new byte[8192];
    int count;

    while ((count = from.read(data)) != -1) {
      to.write(data, 0, count);
    }
    from.close();
  }

  @Override
  protected boolean hasImageFetcher() {
    return true;
  }

  @Override
  protected boolean hasFacebookController() {
    return true;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void onDestroyView() {
    if (mButtonSend != null) {
      mButtonSend.setTextColor(getActivity().getResources().getColor(
          android.R.color.white));
    }
    super.onDestroyView();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void hideKeyboard() {
    if (mKeyboardHandler != null) {
      mKeyboardHandler.removeCallbacksAndMessages(null);
    }
    Utility.hideSoftKeyboard(getActivity());
  }
}