package com.application.ui.chat;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.GetPointActionRequest;
import com.application.connection.request.SaveImageRequest;
import com.application.connection.response.GetPointActionResponse;
import com.application.connection.response.SaveImageResponse;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.NotEnoughPointDialog;
import com.application.util.AnimationUtils;
import com.application.util.StorageUtil;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import glas.bbsystem.R;
import java.io.File;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;


public class ChatDetailPictureActivity extends BaseFragmentActivity implements
    OnClickListener {

  private static final int LOADER_ID_SAVEIMAGE = 100;
  private static final int LOADER_ID_CHECK_POINT_ACTION = 101;
  private static final String EXTRA_IMAGEID = "imageid";
  private static final String EXTRA_IMAGEPATH = "imagepath";
  private static final String EXTRA_ISOWN = "isown";
  private static final String KEY_NEEDPOINT_TOSAVE = "needpoint";
  private View mBack;
  private ImageView mSave;
  private FrameLayout mImgLayout;
  private View mActionbar;
  private View mBottomView;
  private ProgressDialog mProgressDialog;
  private AlertDialog mAlertDialog;
  private ProgressBar mProgressBar;
  private String mImageId;
  private String mPathImage;
  private boolean mIsOwn;
  private boolean mNeedPointToSave;
  private DialogInterface.OnClickListener dialogRequestSaveImage = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      String token = UserPreferences.getInstance().getToken();
      SaveImageRequest request = new SaveImageRequest(token, mImageId);
      restartRequestServer(LOADER_ID_SAVEIMAGE, request);
    }
  };
  private boolean mShowPanel = true;
  private AnimationListener animationListener = new Animation.AnimationListener() {

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
      if (mShowPanel) {
        mActionbar.setVisibility(View.VISIBLE);
        mBottomView.setVisibility(View.VISIBLE);
      } else {
        mActionbar.setVisibility(View.INVISIBLE);
        mBottomView.setVisibility(View.INVISIBLE);
      }
    }

  };
  private Runnable showDialogSuccess = new Runnable() {

    @Override
    public void run() {
      mProgressDialog.dismiss();
      LayoutInflater inflater = LayoutInflater.from(ChatDetailPictureActivity.this);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      Builder builder = new CenterButtonDialogBuilder(ChatDetailPictureActivity.this, false);

      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.save_dialog_title);
      builder.setCustomTitle(customTitle);

      //builder.setTitle(R.string.save_dialog_title);
      builder.setMessage(R.string.save_dialog_ok);
      builder.setPositiveButton(R.string.ok, null);
      AlertDialog element = builder.show();

      int dividerId = element.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = element.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    }
  };
  private Runnable showDialogFailed = new Runnable() {

    @Override
    public void run() {
      mProgressDialog.dismiss();
      LayoutInflater inflater = LayoutInflater.from(ChatDetailPictureActivity.this);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      Builder builder = new CenterButtonDialogBuilder(ChatDetailPictureActivity.this, false);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.save_dialog_title);
      builder.setCustomTitle(customTitle);

      //builder.setTitle(R.string.save_dialog_title);
      builder.setMessage(R.string.save_dialog_error);
      builder.setPositiveButton(R.string.ok, null);
      AlertDialog element = builder.show();

      int dividerId = element.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = element.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }

    }
  };
  private DialogInterface.OnClickListener dialogSaveImage = new DialogInterface.OnClickListener() {

    @Override
    public void onClick(DialogInterface dialog, int which) {
      saveImage();
    }
  };

  public static void startChatDetailPicture(Context context, String imageId,
      String imagePath, String userId) {
    Intent intent = new Intent(context, ChatDetailPictureActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_IMAGEID, imageId);
    bundle.putString(EXTRA_IMAGEPATH, imagePath);
    String currentUserId = UserPreferences.getInstance().getUserId();
    boolean isOwn = currentUserId.equals(userId);
    bundle.putBoolean(EXTRA_ISOWN, isOwn);
    intent.putExtras(bundle);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_chat_detailpicture);
    if (bundle == null) {
      bundle = getIntent().getExtras();
    } else {
      mNeedPointToSave = bundle.getBoolean(KEY_NEEDPOINT_TOSAVE);
    }
    mImageId = bundle.getString(EXTRA_IMAGEID);
    mPathImage = bundle.getString(EXTRA_IMAGEPATH);
    mIsOwn = bundle.getBoolean(EXTRA_ISOWN);

    int price = Preferences.getInstance().getSaveImagePoints();
    if (mIsOwn) {
      mNeedPointToSave = false;
    } else {
      mNeedPointToSave = price > 0;
    }
    initView();
  }

  @Override
  protected void onStart() {
    super.onStart();
    final PhotoView photoView = new PhotoView(this);
    Picasso.with(this).load(new File(mPathImage)).fit().centerInside()
        .into(photoView, new Callback() {
          @Override
          public void onSuccess() {
            createPhotoAttacher(photoView);
            mProgressBar.setVisibility(View.GONE);
          }

          @Override
          public void onError() {
            mProgressBar.setVisibility(View.VISIBLE);
          }
        });
    createPhotoAttacher(photoView);
    mImgLayout.addView(photoView, 0);
  }

  private void createPhotoAttacher(PhotoView photoView) {
    PhotoViewAttacher viewAttacher = new PhotoViewAttacher(photoView);
    viewAttacher.setScaleType(ScaleType.FIT_CENTER);
    viewAttacher.setOnViewTapListener(new OnViewTapListener() {
      @Override
      public void onViewTap(View view, float x, float y) {
        togglePanel();
      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(EXTRA_IMAGEPATH, mPathImage);
    outState.putString(EXTRA_IMAGEID, mImageId);
    outState.putBoolean(EXTRA_ISOWN, mIsOwn);
    outState.putBoolean(KEY_NEEDPOINT_TOSAVE, mNeedPointToSave);
  }

  private void initView() {
    mBack = findViewById(R.id.back);
    mSave = (ImageView) findViewById(R.id.save);
    mImgLayout = (FrameLayout) findViewById(R.id.detail);
    mActionbar = findViewById(R.id.actionbar);
    mBottomView = findViewById(R.id.bottomView);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);

    if (mNeedPointToSave) {
      mSave.setImageResource(R.drawable.lock_save_pic);
    } else {
      mSave.setImageResource(R.drawable.unlock_save_pic);
    }

    mBack.setOnClickListener(this);
    mSave.setOnClickListener(this);
    mBottomView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back:
        finish();
        break;
      case R.id.bottomView:
      case R.id.save:
        String token = UserPreferences.getInstance().getToken();
        GetPointActionRequest request = new GetPointActionRequest(
            token, mImageId);
        restartRequestServer(LOADER_ID_CHECK_POINT_ACTION, request);
        break;
      default:
        break;
    }
  }

  private void togglePanel() {
    if (mShowPanel) {
      hideAllPanel();
      mShowPanel = false;
    } else {
      showAllPanel();
      mShowPanel = true;
    }
  }

  private void hideAllPanel() {
    // top
    Animation animation = AnimationUtils.animationSlide(-1,
        mActionbar.getHeight(), false);
    animation.setAnimationListener(animationListener);
    mActionbar.startAnimation(animation);

    // bottom
    Animation aniBottom = AnimationUtils.animationSlide(1,
        mBottomView.getHeight(), false);
    aniBottom.setAnimationListener(animationListener);
    mBottomView.startAnimation(aniBottom);
  }

  private void showAllPanel() {
    // top
    Animation animation = AnimationUtils.animationSlide(-1,
        mActionbar.getHeight(), true);
    animation.setAnimationListener(animationListener);
    mActionbar.startAnimation(animation);

    // bottom
    Animation aniBottom = AnimationUtils.animationSlide(1,
        mBottomView.getHeight(), true);
    aniBottom.setAnimationListener(animationListener);
    mBottomView.startAnimation(aniBottom);
  }

  @Override
  public void startRequest(int loaderId) {
    mProgressDialog = ProgressDialog.show(this, "",
        getString(R.string.waiting), false, false);
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_SAVEIMAGE) {
      return new SaveImageResponse(data);
    } else if (loaderID == LOADER_ID_CHECK_POINT_ACTION) {
      return new GetPointActionResponse(data);
    }
    throw new IllegalArgumentException("Not found response parser");
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    int loadId = loader.getId();
    switch (loadId) {
      case LOADER_ID_SAVEIMAGE:
        if (response.getCode() != Response.SERVER_SUCCESS) {
          if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
            int pointsNeedToSave;
            if (response instanceof SaveImageResponse) {
              pointsNeedToSave = ((SaveImageResponse) response)
                  .getSavePoint();
            } else {
              pointsNeedToSave = Preferences.getInstance()
                  .getSaveImagePoints();
            }
            NotEnoughPointDialog.showForSaveChatPicture(this,
                pointsNeedToSave);
          } else {
            com.application.ui.customeview.ErrorApiDialog
                .showAlert(this, R.string.common_error,
                    response.getCode());
          }
          return;
        }
        handleResponeSaveImage(response);
        break;
      case LOADER_ID_CHECK_POINT_ACTION:
        if (response.getCode() == Response.SERVER_SUCCESS) {
          LayoutInflater inflater = LayoutInflater.from(this);
          View customTitle = inflater.inflate(R.layout.dialog_customize, null);

          Builder builder = new CenterButtonDialogBuilder(this, true);
          ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
              .setText(R.string.save_dialog_confirm_title);
          builder.setCustomTitle(customTitle);
          //builder.setTitle(R.string.save_dialog_confirm_title);
          if (mNeedPointToSave) {
            int price = Preferences.getInstance()
                .getSaveImagePoints();
            builder.setMessage(getString(
                R.string.save_dialog_confirm_content, price));
            builder.setPositiveButton(R.string.common_yes,
                dialogRequestSaveImage);
            builder.setNegativeButton(R.string.common_no, null);
          } else {
            builder.setMessage(R.string.save_dialog_confirm_content_0_points);
            builder.setPositiveButton(R.string.ok, dialogSaveImage);
            builder.setNegativeButton(R.string.common_no, null);
          }
          mAlertDialog = builder.create();
          mAlertDialog.show();
          int dividerId = mAlertDialog.getContext().getResources()
              .getIdentifier("android:id/titleDivider", null, null);
          View divider = mAlertDialog.findViewById(dividerId);
          if (divider != null) {
            divider.setBackgroundColor(getResources().getColor(R.color.transparent));
          }

        } else {
          if (response.getCode() == Response.SERVER_NOT_ENOUGHT_MONEY) {
            int pointsNeedToSave;
            if (response instanceof SaveImageResponse) {
              pointsNeedToSave = ((SaveImageResponse) response)
                  .getSavePoint();
            } else {
              pointsNeedToSave = Preferences.getInstance()
                  .getSaveImagePoints();
            }
            NotEnoughPointDialog.showForSaveChatPicture(this,
                pointsNeedToSave);
          } else {
            com.application.ui.customeview.ErrorApiDialog
                .showAlert(this, R.string.common_error,
                    response.getCode());
          }
        }
        break;
    }
  }

  private void handleResponeSaveImage(Response response) {
    // luu so point moi
    SaveImageResponse saveImageResponse = (SaveImageResponse) response;
    UserPreferences.getInstance().saveNumberPoint(
        saveImageResponse.getPoint());
    // save image
    saveImage();
  }

  private void saveImage() {
    mProgressDialog = ProgressDialog.show(this, "",
        getString(R.string.waiting), false);
    new Thread(new Runnable() {

      @Override
      public void run() {
        // copy image to photo folder
        boolean isSuccess = StorageUtil.savePhotoChatDetail(
            getApplicationContext(), mPathImage);
        // show dialog status
        if (isSuccess) {
          runOnUiThread(showDialogSuccess);
        } else {
          runOnUiThread(showDialogFailed);
        }
      }
    }).start();
  }

  @Override
  protected boolean isActionbarShowed() {
    return false;
  }
}
