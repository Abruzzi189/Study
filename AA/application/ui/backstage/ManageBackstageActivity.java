package com.application.ui.backstage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.actionbar.NoFragmentActionBar;
import com.application.common.Image;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CheckUnlockRequest;
import com.application.connection.request.ImageRequest;
import com.application.connection.request.ListBackstageImageRequest;
import com.application.connection.request.RateBackstageRequest;
import com.application.connection.request.UnlockRequest;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.response.CheckUnlockResponse;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.ListBackstageImageResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.RateBackstageResponse;
import com.application.connection.response.UnlockResponse;
import com.application.connection.response.UploadImageResponse;
import com.application.constant.Constants;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.customeview.AutofitTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshGridView;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.ImageUtil;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.squareup.picasso.Picasso;
import glas.bbsystem.R;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

enum BACKSTAGE_STATUS {
  MY_BACKSTAGE, USER_BACKSTAGE, UNLOCK_BACKSTAGE
}

public class ManageBackstageActivity extends BaseFragmentActivity implements
    ResponseReceiver, OnClickListener, OnItemClickListener {

  // Key to transport
  public static final String KEY_USER_ID = "user_id";
  public static final String KEY_USER_NAME = "user_name";
  public static final String KEY_AVATA = "avata";
  public static final String KEY_NUMBER_IMG = "number_img";
  // Request code - loading id
  private static final int REQUEST_REFRESH = 0;
  private static final int REQUEST_CHECK_UNLOCK = 1;
  private static final int REQUEST_RATE_BACKSTAGE = 2;
  private static final int REQUEST_UNLOCK_BACKSTAGE = 3;
  private static final int REQUEST_LOAD_BACKSTAGE_IMAGE = 4;
  private static final int REQUEST_LOAD_NEW_BACKSTAGE_IMAGE = 5;
  private static final int REQUEST_GET_IMAGE = 6;
  private static final int REQUEST_SHOW_IMAGE = 7;
  private static final int REQUEST_LOAD_BACKSTAGE_IMAGE_UNLOCK = 8;
  // Constant
  protected final int LIST_DEFAULT_SIZE = 24;
  // View
  private String mAvataId = "";
  private String mBackstageUserId = "";
  private String mBackstageUserName = "";
  private int mNumberOfBackstage = 0;
  private int mStar = 0;
  private ProgressDialog mProgressDialog;
  private BACKSTAGE_STATUS mBackstageStatus;
  private PullToRefreshGridView mPullToRefreshGridView;
  private BackstageAdapter mBackstageAdapter;

  // Ad Layout
  private TextView txtDescription;
  private NoFragmentActionBar mActionBar;

  /**
   * tungdx added to encapsulate
   */
  public static void startManagerBackstage(Activity activity, String userId,
      String userName, String avatarId, int numberImage) {
    Intent intent = new Intent(activity, ManageBackstageActivity.class);
    intent.putExtra(ManageBackstageActivity.KEY_USER_ID, userId);
    intent.putExtra(ManageBackstageActivity.KEY_USER_NAME, userName);
    intent.putExtra(ManageBackstageActivity.KEY_AVATA, avatarId);
    intent.putExtra(ManageBackstageActivity.KEY_NUMBER_IMG, numberImage);
    activity.startActivity(intent);
  }

  public static void startManagerBackstage(Activity activity, String userId) {
    Intent intent = new Intent(activity, ManageBackstageActivity.class);
    intent.putExtra(ManageBackstageActivity.KEY_USER_ID, userId);
    activity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initActionBar();
    loadTransportData();
    initView();
    initialNotificationVew();
  }

  public void loadTransportData() {
    Bundle dataTransport = getIntent().getExtras();
    if (dataTransport != null) {
      mBackstageUserId = dataTransport.getString(KEY_USER_ID);
      mBackstageUserName = dataTransport.getString(KEY_USER_NAME);
      mAvataId = dataTransport.getString(KEY_AVATA);
      mNumberOfBackstage = dataTransport.getInt(KEY_NUMBER_IMG);
    }
  }

  public void initView() {
    setContentView(R.layout.activity_manage_backstage);
    ImageView imgBackground = (ImageView) findViewById(
        R.id.activity_manage_backstage_avata_background);
    txtDescription = (TextView) findViewById(R.id.activity_manage_backstage_desciption);
    String token = UserPreferences.getInstance().getToken();
    ImageRequest imageRequest = new ImageRequest(token, mAvataId,
        ImageRequest.ORIGINAL);
    Picasso.with(this).load(imageRequest.toURL())
        .placeholder(R.drawable.dummy_avatar).into(imgBackground);
    checkUnlock(REQUEST_CHECK_UNLOCK);
  }

  private void refresh() {
    checkUnlock(REQUEST_REFRESH);
  }

  private void checkUnlock(int requestCode) {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    CheckUnlockRequest request;
    request = new CheckUnlockRequest(token, mBackstageUserId);
    restartRequestServer(requestCode, request);
  }

  private void initBackstageImageView() {
    mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.manage_backstage_list_image);
    mPullToRefreshGridView
        .setOnRefreshListener(new OnRefreshListener2<GridView>() {
          @Override
          public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
            refresh();
          }

          @Override
          public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
            if (mBackstageStatus == BACKSTAGE_STATUS.UNLOCK_BACKSTAGE) {
              int size = mNumberOfBackstage;
              size = size - mBackstageAdapter.getCount();
              if (size > LIST_DEFAULT_SIZE) {
                size = LIST_DEFAULT_SIZE;
              }
              addDummyImage(size);
              mPullToRefreshGridView.onRefreshComplete();
              if (size <= 0) {
                mPullToRefreshGridView
                    .setMode(Mode.PULL_FROM_START);
              } else {
                mPullToRefreshGridView.setMode(Mode.BOTH);
              }
            } else {
              loadListBackstageImage();
            }
          }
        });
    mPullToRefreshGridView.setMode(Mode.BOTH);
    Resources resource = getResources();
    mPullToRefreshGridView.setPullLabelFooter(resource
        .getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshGridView.setReleaseLabelFooter(resource
        .getString(R.string.pull_to_load_more_release_label));
    final GridView GRID_BACKSTAGE = mPullToRefreshGridView.getRefreshableView();
    ArrayList<String> listImg = new ArrayList<String>();
    if (mBackstageStatus == BACKSTAGE_STATUS.MY_BACKSTAGE) {
      listImg.add(BackstageAdapter.ADD_BACKSTAGE_IMAGE);
    }
    mBackstageAdapter = new BackstageAdapter(this, listImg);
    GRID_BACKSTAGE.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
          @SuppressWarnings("deprecation")
          @Override
          public void onGlobalLayout() {
            Resources resource = getResources();
            int imgWidth = resource
                .getDimensionPixelSize(R.dimen.image_thumbnail_size);
            int imgSpace = resource
                .getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
            int gridWidth = GRID_BACKSTAGE.getWidth();
            int numCols = (int) Math.floor(gridWidth
                / (imgWidth + imgSpace));
            if (numCols > 0) {
              mBackstageAdapter
                  .setAvatarSize(gridWidth / numCols);
              GRID_BACKSTAGE.setNumColumns(numCols);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                GRID_BACKSTAGE.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);
              } else {
                GRID_BACKSTAGE.getViewTreeObserver()
                    .removeGlobalOnLayoutListener(this);
              }
            }
          }
        });
    GRID_BACKSTAGE.setAdapter(mBackstageAdapter);
    GRID_BACKSTAGE.setOnItemClickListener(this);
    if (mBackstageStatus != BACKSTAGE_STATUS.UNLOCK_BACKSTAGE) {
      loadListBackstageImage();
    }
  }

  private void loadListBackstageImage() {
    String token = UserPreferences.getInstance().getToken();
    int totalImg = 0;
    if (mBackstageAdapter != null) {
      totalImg = mBackstageAdapter.getCount();
    }
    if (mBackstageStatus == BACKSTAGE_STATUS.MY_BACKSTAGE && totalImg > 0) {
      totalImg--;
    }
    ListBackstageImageRequest listBackstageImageRequest;
    if (mBackstageStatus == BACKSTAGE_STATUS.MY_BACKSTAGE) {
      listBackstageImageRequest = new ListBackstageImageRequest(token,
          totalImg, LIST_DEFAULT_SIZE);
    } else if (mBackstageStatus == BACKSTAGE_STATUS.USER_BACKSTAGE) {
      listBackstageImageRequest = new ListBackstageImageRequest(token,
          mBackstageUserId, totalImg, LIST_DEFAULT_SIZE);
    } else {
      listBackstageImageRequest = new ListBackstageImageRequest(token,
          mBackstageUserId, totalImg, LIST_DEFAULT_SIZE);
    }
    restartRequestServer(REQUEST_LOAD_BACKSTAGE_IMAGE,
        listBackstageImageRequest);
  }

  private void loadNewBackstageImage() {
    String token = UserPreferences.getInstance().getToken();
    ListBackstageImageRequest listBackstageImageRequest = new ListBackstageImageRequest(
        token, 0, 1);
    restartRequestServer(REQUEST_LOAD_NEW_BACKSTAGE_IMAGE,
        listBackstageImageRequest);
  }

  public void showTitle() {
    switch (mBackstageStatus) {
      case MY_BACKSTAGE:
        mActionBar
            .setTextCenterTitle(R.string.activity_manage_backstage_my_title);
        break;
      case USER_BACKSTAGE:
      case UNLOCK_BACKSTAGE:
        mActionBar
            .setTextCenterTitle(R.string.activity_manage_backstage_title);
        break;
    }
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  public void showDescription() {
    txtDescription.setVisibility(View.VISIBLE);
    switch (mBackstageStatus) {
      case MY_BACKSTAGE:
        int price = Preferences.getInstance().getBackstageBonus();
        if (price > 0) {
          String description = getString(R.string.activity_manage_backstage_description);
          txtDescription.setText(description);
        } else {
          txtDescription.setVisibility(View.GONE);
        }
        break;
      case UNLOCK_BACKSTAGE:
      case USER_BACKSTAGE:
        Resources resource = getResources();
        String formatUserbackstage = resource
            .getString(R.string.activity_manage_backstage_price_unlocked);
        final String rateDesc = String.format(formatUserbackstage,
            mBackstageUserName);
        txtDescription.setText(rateDesc);
        txtDescription.getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                float textWidth = txtDescription.getPaint()
                    .measureText(rateDesc);
                float textViewWidth = txtDescription.getWidth();
                if (textWidth < textViewWidth) {
                  return;
                }
                if (mBackstageStatus != BACKSTAGE_STATUS.MY_BACKSTAGE) {
                  measureDescription(textViewWidth);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                  txtDescription.getViewTreeObserver()
                      .removeOnGlobalLayoutListener(this);
                } else {
                  txtDescription.getViewTreeObserver()
                      .removeGlobalOnLayoutListener(this);
                }
              }
            });
        break;
    }
  }

  public void showPriceDescription() {
    AutofitTextView priceDescription = (AutofitTextView) findViewById(
        R.id.activity_manage_backstage_price_desciption);
    priceDescription.setMaxLines(1);
    priceDescription.setSizeToFit(true);
    priceDescription.setVisibility(View.VISIBLE);
    switch (mBackstageStatus) {
      case UNLOCK_BACKSTAGE:
      case USER_BACKSTAGE:
        String unlockTime = getString(R.string.activity_manage_backstage_unlock_time);
        int time = Preferences.getInstance().getTimeBackstage();
        priceDescription.setText(String.format(unlockTime,
            String.valueOf(time)));
        break;
      case MY_BACKSTAGE:
        String unlockPrice = getString(R.string.activity_manage_backstage_unlock_price);
        int price = Preferences.getInstance().getBackstageBonus();
        if (price > 0) {
          priceDescription.setText(Html.fromHtml(MessageFormat.format(
              unlockPrice, price)));
        } else {
          priceDescription.setVisibility(View.GONE);
        }
        break;
    }
  }

  public void setStar(int starPoint) {
    ImageView[] rateStar = new ImageView[5];
    rateStar[0] = (ImageView) findViewById(R.id.activity_manage_backstage_rate0);
    rateStar[1] = (ImageView) findViewById(R.id.activity_manage_backstage_rate1);
    rateStar[2] = (ImageView) findViewById(R.id.activity_manage_backstage_rate2);
    rateStar[3] = (ImageView) findViewById(R.id.activity_manage_backstage_rate3);
    rateStar[4] = (ImageView) findViewById(R.id.activity_manage_backstage_rate4);
    Resources resource = getResources();
    Drawable drawable = resource.getDrawable(R.drawable.ic_rate_off);
    int length = rateStar.length;
    for (int i = 0; i < length; i++) {
      rateStar[i].setImageDrawable(drawable);
      rateStar[i].setOnClickListener(null);
    }
    drawable = resource.getDrawable(R.drawable.ic_rate_on);
    for (int i = starPoint - 1; i > -1; i--) {
      rateStar[i].setImageDrawable(drawable);
    }
    if (mBackstageStatus == BACKSTAGE_STATUS.USER_BACKSTAGE) {
      for (int i = 0; i < rateStar.length; i++) {
        final int RATE = i + 1;
        rateStar[i].setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            onStarClick(v, RATE);
          }
        });
      }
    }
  }

  private void onStarClick(View v, int position) {
    setStar(position);
    String token = UserPreferences.getInstance().getToken();
    RateBackstageRequest request = new RateBackstageRequest(token,
        mBackstageUserId, position);
    restartRequestServer(REQUEST_RATE_BACKSTAGE, request);
  }

  @SuppressLint("NewApi")
  private void showBottomButton() {
    LinearLayout fmlUnlock = (LinearLayout) findViewById(
        R.id.activity_manage_backstage_unlock_price);
    TextView btnUnlock = (TextView) findViewById(R.id.unlock_price_manage_backstage_unlock_button);
    switch (mBackstageStatus) {
      case MY_BACKSTAGE:
      case USER_BACKSTAGE:
        fmlUnlock.setVisibility(View.GONE);
        break;
      case UNLOCK_BACKSTAGE:
        fmlUnlock.setVisibility(View.VISIBLE);
        btnUnlock.setOnClickListener(this);
        final Resources resource = getResources();
        String btnFormat = resource
            .getString(R.string.activity_manage_backstage_unlock_price_button_please);
        Preferences preferences = Preferences.getInstance();
        int price = preferences.getBackstagePrice();
        btnUnlock.setText(MessageFormat.format(btnFormat, price));
        break;
      default:
        break;
    }
  }

  private void onUnlockButtonClick() {
    if (mBackstageStatus == BACKSTAGE_STATUS.UNLOCK_BACKSTAGE) {
      unlockBackstage();
    }
  }

  private void unlockBackstage() {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    UnlockRequest unlockRequest = new UnlockRequest(token, mBackstageUserId);
    restartRequestServer(REQUEST_UNLOCK_BACKSTAGE, unlockRequest);
  }

  public void addDummyImage(int size) {
    for (int i = 0; i < size; i++) {
      mBackstageAdapter
          .addMoreImage(BackstageAdapter.DUMMY_BACKSTAGE_IMAGE);
    }
    mBackstageAdapter.notifyDataSetChanged();
  }

  public void handleCheckUnlock(CheckUnlockResponse response) {
    Preferences preferent = Preferences.getInstance();
    UserPreferences userPreferences = UserPreferences.getInstance();
    mNumberOfBackstage = response.getBackstageNumber();
    preferent.saveBackstageBonus(response.getBonus());
    preferent.saveBackstagePrice(response.getBackstagePrice());
    if (mBackstageUserId.equals(userPreferences.getUserId())) {
      mBackstageStatus = BACKSTAGE_STATUS.MY_BACKSTAGE;
      initBackstageImageView();
    } else {
      if (response.getIsUnlock() == Constants.UNLOCKED) {
        mBackstageStatus = BACKSTAGE_STATUS.USER_BACKSTAGE;
        initBackstageImageView();
      } else {
        mBackstageStatus = BACKSTAGE_STATUS.UNLOCK_BACKSTAGE;
        initBackstageImageView();
        int size = mNumberOfBackstage;
        if (size > LIST_DEFAULT_SIZE) {
          size = LIST_DEFAULT_SIZE;
        }
        addDummyImage(size);
      }
    }
    showTitle();
    showDescription();
    showPriceDescription();
    int rate = 0;
    mStar = response.getRatepoint();
    if (mBackstageStatus == BACKSTAGE_STATUS.USER_BACKSTAGE) {
      rate = mStar;
    } else {
      double rateDouble = response.getBackstageRate();
      rate = (int) rateDouble;
      if (rateDouble >= rate + 0.5) {
        rate++;
      }
      if (rate > 5) {
        rate = 5;
      } else if (rate < 0) {
        rate = 0;
      }
    }
    setStar(rate);
    showBottomButton();
  }

  @Override
  public void startRequest(int loaderId) {
    if (mProgressDialog == null) {
      String message = getString(R.string.waiting);
      mProgressDialog = ProgressDialog.show(this, "", message);
      mProgressDialog.setCanceledOnTouchOutside(false);
    }
    switch (loaderId) {
      // case REQUEST_REFRESH:
      case REQUEST_CHECK_UNLOCK:
      case REQUEST_LOAD_BACKSTAGE_IMAGE:
      case REQUEST_UNLOCK_BACKSTAGE:
        mProgressDialog.show();
        break;
      default:
        break;
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    if (mPullToRefreshGridView != null
        && mPullToRefreshGridView.isRefreshing()) {
      mPullToRefreshGridView.onRefreshComplete();
    }
    if (mProgressDialog != null || mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    int code = response.getCode();
    if (code == Response.SERVER_ITEM_NOT_AVAIABLE) {
      setResult(RESULT_OK);
      finish();
    }
    if (code != Response.SERVER_SUCCESS
        && code != Response.SERVER_NOT_ENOUGHT_MONEY) {
      ErrorApiDialog.showAlert(this, R.string.common_error, code);
      return;
    }
    int loadId = loader.getId();
    switch (loadId) {
      case REQUEST_REFRESH:
      case REQUEST_CHECK_UNLOCK:
        handleCheckUnlock((CheckUnlockResponse) response);
        break;
      case REQUEST_RATE_BACKSTAGE:
        break;
      case REQUEST_UNLOCK_BACKSTAGE:
        handleUnlockBackstage((UnlockResponse) response);
        break;
      case REQUEST_LOAD_BACKSTAGE_IMAGE:
        handleLoadImage((ListBackstageImageResponse) response);
        break;
      case REQUEST_LOAD_NEW_BACKSTAGE_IMAGE:
        handleLoadNewImage((ListBackstageImageResponse) response);
        break;
      case REQUEST_LOAD_BACKSTAGE_IMAGE_UNLOCK:
        handleLoadNewImageUnlock((ListBackstageImageResponse) response);
        break;
      default:
        break;
    }
  }

  private void handleUnlockBackstage(UnlockResponse response) {
    UserPreferences preferences = UserPreferences.getInstance();
    preferences.saveNumberPoint(response.getPoint());
    if (response.getCode() == Response.SERVER_SUCCESS) {
      String token = UserPreferences.getInstance().getToken();
      int skip = 0;
      int take = mNumberOfBackstage;
      ListBackstageImageRequest request = new ListBackstageImageRequest(
          token, mBackstageUserId, skip, take);
      restartRequestServer(REQUEST_LOAD_BACKSTAGE_IMAGE_UNLOCK, request);
    } else if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
      // Current point
      int point = response.getPrice();
      NotEnoughPointDialog.showForUnlockBackstage(this, point);
    }
  }

  private void handleLoadImage(ListBackstageImageResponse response) {
    ArrayList<Image> listImage = response.getListImage();
    int size = listImage.size();
    if (size <= 0) {
      mPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
    } else {
      mPullToRefreshGridView.setMode(Mode.BOTH);
    }
    for (int i = 0; i < size; i++) {
      String imgId = listImage.get(i).getImg_id();
      if (!mBackstageAdapter.isContain(imgId)) {
        mBackstageAdapter.addMoreImage(imgId);
      }
    }
    mBackstageAdapter.notifyDataSetChanged();
  }

  private void handleLoadNewImage(ListBackstageImageResponse response) {
    ArrayList<Image> listImage = response.getListImage();
    if (listImage != null && listImage.size() > 0) {
      mBackstageAdapter.addNewImage(listImage.get(0).getImg_id());
      mBackstageAdapter.notifyDataSetChanged();
      mNumberOfBackstage++;
    }
  }

  private void handleLoadNewImageUnlock(ListBackstageImageResponse response) {
    mBackstageStatus = BACKSTAGE_STATUS.USER_BACKSTAGE;
    showTitle();
    showDescription();
    showPriceDescription();
    setStar(mStar);
    showBottomButton();
    ArrayList<Image> listImage = response.getListImage();
    int newListNumber = listImage.size();
    int oldListNumber = mBackstageAdapter.getCount();
    int changeNumber = oldListNumber - newListNumber;
    for (int i = 0; i < newListNumber && i < oldListNumber; i++) {
      mBackstageAdapter.setImg(i, listImage.get(i).getImg_id());
    }
    if (changeNumber > 0) {
      for (int i = 0; i < changeNumber; i++) {
        mBackstageAdapter.remove(mBackstageAdapter.getCount() - 1);
      }
    }
    mBackstageAdapter.notifyDataSetChanged();
    mNumberOfBackstage = mBackstageAdapter.getCount();
  }

  @Override
  public Response parseResponse(int id, ResponseData data, int requestType) {
    Response response = null;
    switch (id) {
      case REQUEST_REFRESH:
      case REQUEST_CHECK_UNLOCK:
        response = new CheckUnlockResponse(data);
        break;
      case REQUEST_RATE_BACKSTAGE:
        response = new RateBackstageResponse(data);
        break;
      case REQUEST_UNLOCK_BACKSTAGE:
        response = new UnlockResponse(data);
        break;
      case REQUEST_LOAD_BACKSTAGE_IMAGE:
      case REQUEST_LOAD_NEW_BACKSTAGE_IMAGE:
      case REQUEST_LOAD_BACKSTAGE_IMAGE_UNLOCK:
        response = new ListBackstageImageResponse(data);
        break;
      case LOADER_RETRY_LOGIN:
        response = new LoginResponse(data);
        break;
      case LOADER_GET_USER_STATUS:
        response = new GetUserStatusResponse(data);
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
  public void onClick(View v) {
    int viewId = v.getId();
    switch (viewId) {
      case R.id.unlock_price_manage_backstage_unlock_button:
        onUnlockButtonClick();
        break;
      default:
        break;
    }
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_OK);
    super.onBackPressed();
  }

  @Override
  public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
    Intent intent = new Intent(ManageBackstageActivity.this,
        DetailPictureBackstageActivity.class);
    int viewType = mBackstageAdapter.getItemViewType(position);
    switch (viewType) {
      case BackstageAdapter.ITEM_TYPE_ADD_VIEW:
        showDialogUploadImgType();
        break;
      case BackstageAdapter.ITEM_TYPE_IMAGE_VIEW:
        intent.putExtras(ProfilePictureData.parseDataToBundle(position,
            mNumberOfBackstage, mBackstageUserId,
            mBackstageAdapter.getItem(position)));
        startActivityForResult(intent, REQUEST_SHOW_IMAGE);

      case BackstageAdapter.ITEM_TYPE_DUMMY_VIEW:
      default:
        break;
    }
  }

  private void showDialogUploadImgType() {
//		MediaOptions.Builder builder = new MediaOptions.Builder();
//		MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//		MediaPickerActivity.open(this, REQUEST_GET_IMAGE, options);

    new Gallery.Builder()
        .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
        .multiChoice(false)
        .fixAspectRatio(true)
        .cropOutput(true)
        .build()
        .start(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
//				case REQUEST_GET_IMAGE:
//					ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//					if (mMediaSelectedList != null) {
//						for (final MediaItem mediaItem : mMediaSelectedList) {
//							String imgPath = mediaItem.getCroppedPath();
//							if (imgPath == null) {
//								imgPath = mediaItem.getOriginPath();
//							}
//							uploadAvatarToServer(imgPath);
//						}
//					} else {
//						LogUtils.e(TAG, "Error to get media, NULL");
//					}
//					break;
        case Camera.REQUEST_CODE_CAMERA:
        case Gallery.REQUEST_CODE_GALLERY:
          Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
          for (Parcelable parcelable : files) {
            MediaFile file = (MediaFile) parcelable;
            String imgPath = file.getPath();
            uploadAvatarToServer(imgPath);
          }
          break;
        case REQUEST_SHOW_IMAGE:
          if (data == null) {
            refresh();
            return;
          }
          int backstageBundle = data
              .getIntExtra(
                  DetailPictureBackstageActivity.KEY_BUNDER_BACKSTAGE,
                  DetailPictureBackstageActivity.BUNDER_BACKSTAGE_NOT_SET);
          switch (backstageBundle) {
            case DetailPictureBackstageActivity.BUNDER_BACKSTAGE_NOT_SET:
              break;
            case DetailPictureBackstageActivity.BUNDER_BACKSTAGE_LOCK:
              refresh();
              break;
            case DetailPictureBackstageActivity.BUNDER_BACKSTAGE_DELETE:
              initBackstageImageView();
              break;
            default:
              break;
          }
          break;
        default:
          break;
      }
    }
  }

  private void uploadAvatarToServer(String imagePath) {
    final ProgressDialog DIALOG = new ProgressDialog(this);
    String message = getString(R.string.uploading_image);
    DIALOG.setMessage(message);
    DIALOG.setCancelable(false);
    ImageUploader imageUploader = new ImageUploader(
        new UploadImageProgress() {
          @Override
          public void uploadImageSuccess(UploadImageResponse response) {
            DIALOG.dismiss();
            if (response.getIsApproved() == Constants.IS_APPROVED) {
              loadNewBackstageImage();
            } else {
              BackstageDialog.showDialogUploadImageDone(
                  ManageBackstageActivity.this, true);
            }
          }

          @Override
          public void uploadImageStart() {
            DIALOG.show();
          }

          @Override
          public void uploadImageFail(int code) {
            DIALOG.dismiss();
            BackstageDialog.showDialogUploadImageDone(
                ManageBackstageActivity.this, false);
          }
        });
    if (null != imagePath) {
      File file = new File(imagePath);
      String token = UserPreferences.getInstance().getToken();
      String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
      UploadImageRequest imageRequest = new UploadImageRequest(token, UploadImageRequest.BACKSTAGE,
          file, md5Encrypted);
      imageUploader.execute(imageRequest);
    }
  }

  private void measureDescription(float textViewWidth) {
    final String ELLIPSIS = "...";
    String formatUserbackstage = getResources().getString(
        R.string.activity_manage_backstage_price_unlocked);
    String userName = mBackstageUserName;
    boolean isLongName = false;
    TextPaint paint = txtDescription.getPaint();
    float textWidth = paint.measureText(userName);
    float formatWidth = paint.measureText(String.format(
        formatUserbackstage, " "));
    textViewWidth -= formatWidth;
    if (textWidth > textViewWidth) {
      textViewWidth = textViewWidth - paint.measureText(ELLIPSIS);
    }
    while (textWidth > textViewWidth) {
      isLongName = true;
      int length = userName.length();
      if (length < 1) {
        break;
      }
      userName = userName.substring(0, length - 1);
      textWidth = paint.measureText(userName);
    }
    if (isLongName) {
      userName = userName.trim() + ELLIPSIS;
    }
    String rateDesc = String.format(formatUserbackstage, userName);
    txtDescription.setText(rateDesc);
  }

  @Override
  public boolean hasImageFetcher() {
    return true;
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  @Override
  protected boolean isNoTitle() {
    return false;
  }

  private void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
  }
}