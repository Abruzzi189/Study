package com.application.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.application.constant.Constants;
import com.application.imageloader.ImageFetcher;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.AndGViewPager;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.io.File;
import java.util.ArrayList;


public abstract class ProfilePictureTheaterBase implements OnPageChangeListener {

  private static ProgressBar mLoadingProfilePicture;
  // Fields for control
  protected Context mContext;
  protected boolean isPanelShowed;
  protected boolean isPanelShowing;
  protected OnProfilePictureClickListener mOnProfilePictureClickListener;
  protected OnProfilePicturePageChangeListener mOnProfilePicturePageChangeListener;
  // Fields for view
  protected DetailPictureProfileAdapter mDetailPictureProfileAdapter;
  protected View mView;
  protected View mNavigatorBar;
  protected ViewPager mViewPager;
  protected View mBottomPanel;
  private ProgressDialog mProgressDialog;
  private ImageFetcher mImageFetcher;
  private ProgressBar mProgressBar;
  private int price;

  public ProfilePictureTheaterBase(Context context,
      ProfilePictureData profilePictureData, ImageFetcher imageFetcher) {
    this.mContext = context;
    mDetailPictureProfileAdapter = new DetailPictureProfileAdapter(context,
        profilePictureData, imageFetcher);
    isPanelShowed = true;
    isPanelShowing = false;
    mImageFetcher = imageFetcher;
  }

  /**
   * show progressBar loading image
   */
  public static void showLoading() {
    if (mLoadingProfilePicture.getVisibility() == View.GONE) {
      mLoadingProfilePicture.setVisibility(View.VISIBLE);
    }
  }

  /**
   * hide progressBar loading image
   */
  public static void hideLoading() {
    if (mLoadingProfilePicture.getVisibility() == View.VISIBLE) {
      mLoadingProfilePicture.setVisibility(View.GONE);
    }
  }

  @SuppressLint("InflateParams")
  public View getView() {
    LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.activity_detail_picture_profile, null, false);
    mView = view;
    mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    mLoadingProfilePicture = (ProgressBar) view
        .findViewById(R.id.progress_bar_detail_picture_profile);
    // Init view page to show image
    mViewPager = (AndGViewPager) view
        .findViewById(R.id.activity_detail_picture_profile_image_theater);
    mViewPager.setAdapter(mDetailPictureProfileAdapter);
    // Create an buffer to inflate stub
    ViewStub bufferViewStub;
    bufferViewStub = (ViewStub) view
        .findViewById(R.id.activity_detail_picture_profile_navigator_bar);
    bufferViewStub.setLayoutResource(R.layout.navigator_bar_detail_picture);
    mNavigatorBar = bufferViewStub.inflate();
    bufferViewStub = (ViewStub) view
        .findViewById(R.id.activity_detail_picture_profile_bottom_panel);
    bufferViewStub.setLayoutResource(R.layout.panel_bottom_detail_picture);
    mBottomPanel = bufferViewStub.inflate();
    return view;
  }

  public void changeVisibleAllPanel() {
    if (isPanelShowed) {
      hideAllPanel();
    } else {
      showAllPanel();
    }
  }

  public void hideAllPanel() {
    if (!isPanelShowing) {
      mNavigatorBar.startAnimation(animationSlide(-1,
          mNavigatorBar.getHeight(), Constants.HORIZONTAL_HIDE));
      mBottomPanel.startAnimation(animationSlide(1,
          mBottomPanel.getHeight(), Constants.HORIZONTAL_HIDE));
    }
  }

  public void showAllPanel() {
    if (!isPanelShowing) {
      mNavigatorBar.startAnimation(animationSlide(-1,
          mNavigatorBar.getHeight(), Constants.HORIZONTAL_SHOW));
      mBottomPanel.startAnimation(animationSlide(1,
          mBottomPanel.getHeight(), Constants.HORIZONTAL_SHOW));
    }
  }

  /**
   * Return an animation data for slide up. This animation data describe the direct of animation,
   * the end point and duration time.<br/> Location be -1 to top or left, 1 to bottom or right<br/>
   * View size be size vertical or horizontal of view.<br/> Direction declare from Constants Class.
   */
  private Animation animationSlide(int location, int viewSize, int direction) {
    AnimationSet set = new AnimationSet(true);
    Animation animation = new TranslateAnimation(0, 0, 0, 0);
    if (direction == Constants.HORIZONTAL_SHOW) {
      animation = new TranslateAnimation(0, 0, viewSize * location, 0);
    } else if (direction == Constants.HORIZONTAL_HIDE) {
      animation = new TranslateAnimation(0, 0, 0, viewSize * location);
    } else if (direction == Constants.VERTICAL_SHOW) {
      animation = new TranslateAnimation(viewSize * location, 0, 0, 0);
    } else if (direction == Constants.VERTICAL_HIDE) {
      animation = new TranslateAnimation(0, viewSize * location, 0, 0);
    }
    animation.setDuration(200);
    if (location == -1) {
      animation.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
          isPanelShowing = true;
          if (!isPanelShowed) {
            mNavigatorBar.setVisibility(View.VISIBLE);
            mBottomPanel.setVisibility(View.VISIBLE);
          }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
          isPanelShowing = false;
          isPanelShowed = !isPanelShowed;
          if (!isPanelShowed) {
            mNavigatorBar.setVisibility(View.GONE);
            mBottomPanel.setVisibility(View.GONE);
          }
        }
      });
    }
    set.addAnimation(animation);
    return animation;
  }

  // Show dialog
  public void showProgressDialog(Context context) {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(context);
      mProgressDialog.setMessage(mContext.getString(R.string.waiting));
      mProgressDialog.setCanceledOnTouchOutside(false);
    }
    mProgressDialog.show();
    mProgressBar.setVisibility(View.INVISIBLE);
  }

  // Dismiss dialog
  public void dismissProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
      mProgressBar.setVisibility(View.VISIBLE);
    }
  }

  public File getCurrentFile() {
    return mDetailPictureProfileAdapter.getCurrentFile();
  }

  public boolean isSavable() {
    return mDetailPictureProfileAdapter.isSavable();
  }

  public int getmAvataImgIndex() {
    return mDetailPictureProfileAdapter.getmAvataImgIndex();
  }

  public void setmAvataImgIndex(int mAvataImgIndex) {
    mDetailPictureProfileAdapter.setmAvataImgIndex(mAvataImgIndex);
  }

  // Total number of image
  public int getNumberOfImage() {
    return mDetailPictureProfileAdapter.getCount();
  }

  public void setNumberOfComment(int num) {
    if (mBottomPanel == null) {
      return;
    }

    FrameLayout commentWrap = (FrameLayout) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_txt_comment_wrap);
    TextView numComment = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_num_comment);

//		if (num < 0) {
//			numComment.setText("0");
//		} else {
//			String text = "";
//			if (num <= 9) {
//				text += num;
//				numComment.setText(text);
//			} else {
//				text = "9+";
//				numComment.setText(text);
//			}
//		}

    commentWrap.setVisibility(View.VISIBLE);
  }

  public String getCurrentImage() {
    String result = "";
    result = mDetailPictureProfileAdapter.getCurrentImg();
    return result;
  }

  public int getCurrentIndex() {
    return mDetailPictureProfileAdapter.getCurrentImgIndex();
  }

  public void setTheaterTitle(String title) {
    if (mNavigatorBar != null) {
      TextView txtTitle = (TextView) mNavigatorBar
          .findViewById(R.id.activity_detail_picture_profile_title);
      txtTitle.setText(title);
    }
  }

  public void showDialogConfirmDelete() {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = mContext.getResources();
    Builder builder;
    //builder.setTitle(resource.getString(R.string.dialog_confirm_delete_pictuer_title));
    String imgLink = mDetailPictureProfileAdapter.getCurrentImg();
    if (imgLink == null || "".equals(imgLink)) {
      builder = new CenterButtonDialogBuilder(mContext, false);
      builder.setMessage(resource
          .getString(R.string.dialog_confirm_delete_picture_content_empty));
      builder.setPositiveButton(
          resource.getString(R.string.dialog_confirm_delete_picture_negative_button),
          null);
    } else {
      builder = new CenterButtonDialogBuilder(mContext, true);
      builder.setMessage(resource
          .getString(R.string.dialog_confirm_delete_picture_content));
      builder.setNegativeButton(
          resource.getString(R.string.dialog_confirm_delete_picture_negative_button),
          new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mOnProfilePictureClickListener
                  .onDialogConfirmDelete(dialog, which);
            }
          });
      builder.setPositiveButton(
          resource.getString(R.string.dialog_confirm_delete_picture_positive_button),
          null);
    }
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(resource.getString(R.string.dialog_confirm_delete_pictuer_title));
    builder.setCustomTitle(customTitle);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialogReport() {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    Resources resource = mContext.getResources();
    Builder builder = new Builder(mContext);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(resource
        .getString(R.string.dialog_confirm_report_pictuer_title));
    builder.setCustomTitle(customTitle);

    //builder.setTitle(resource.getString(R.string.dialog_confirm_report_pictuer_title));
    String imgLink = mDetailPictureProfileAdapter.getCurrentImg();
    if (imgLink == null || "".equals(imgLink)) {
      builder.setMessage(resource
          .getString(R.string.dialog_confirm_report_picture_content_empty));
      builder.setNegativeButton(
          resource.getString(R.string.dialog_confirm_report_picture_negative_button),
          null);
    } else {
      String[] items = resource
          .getStringArray(R.array.report_content_type);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
          android.R.layout.select_dialog_item, items);
      builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mOnProfilePictureClickListener.onDialogReportImage(dialog,
              which);
        }
      });
    }
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialogConfirmReport(final int reportType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    Resources resource = mContext.getResources();
    Builder builder = new Builder(mContext);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(resource
        .getString(R.string.dialog_confirm_report_pictuer_title));
    builder.setCustomTitle(customTitle);

    //builder.setTitle(resource.getString(R.string.dialog_confirm_report_pictuer_title));
    builder.setMessage(resource
        .getString(R.string.dialog_confirm_report_picture_content));
    builder.setNegativeButton(
        resource.getString(R.string.dialog_confirm_delete_picture_negative_button),
        new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mOnProfilePictureClickListener.onDialogConfirmReport(
                dialog, which, reportType);
          }
        });
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialogImgNotFound(int contentId, final boolean isBack) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = mContext.getResources();
    Builder builder = new Builder(mContext);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(resource
        .getString(R.string.dialog_image_not_found_title));
    builder.setCustomTitle(customTitle);

    //builder.setTitle(resource.getString(R.string.dialog_image_not_found_title));

    builder.setMessage(resource.getString(contentId));
    builder.setNegativeButton(resource
            .getString(R.string.dialog_image_not_found_negative_button),
        new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mOnProfilePictureClickListener
                .onDialogImgNotFound(isBack);
          }
        });

    AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void updateImage(ArrayList<String> listImage) {
    mDetailPictureProfileAdapter.setListImageId(listImage);
    mDetailPictureProfileAdapter.notifyDataSetChanged();
  }

  public void setCurrentImg(int location) {
    if (mViewPager != null) {
      mDetailPictureProfileAdapter.setCurrentImgIndex(location);
      mViewPager.setCurrentItem(location);
    }
  }

  public void setBtnLikeStatus(int isLike) {
    if (mBottomPanel != null) {
      TextView btnLike = (TextView) mBottomPanel
          .findViewById(R.id.activity_detail_picture_profile_like);
      if (isLike == Constants.BUZZ_LIKE_TYPE_LIKE) {
        btnLike.setText(R.string.panel_bottom_detail_unlike);
      } else {
        btnLike.setText(R.string.panel_bottom_detail_like);
      }
    }
  }

  public int getLikeStatus() {
    if (mBottomPanel != null) {
      TextView btnLike = (TextView) mBottomPanel
          .findViewById(R.id.activity_detail_picture_profile_like);
      String txtLike = btnLike.getText().toString();
      Resources resource = mContext.getResources();
      if (txtLike.equals(resource
          .getString(R.string.panel_bottom_detail_like))) {
        return Constants.BUZZ_LIKE_TYPE_UNLIKE;
      } else if (txtLike.equals(resource
          .getString(R.string.panel_bottom_detail_unlike))) {
        return Constants.BUZZ_LIKE_TYPE_LIKE;
      }
      return Constants.BUZZ_LIKE_TYPE_UNKNOW;
    }
    return Constants.BUZZ_LIKE_TYPE_UNKNOW;
  }

  public void lockScreen(boolean isLock) {
    mViewPager.setFocusable(!isLock);
  }

  public void showUserAvata(String userAvata, int gender) {
//		RelativeLayout imgDisplayLayout = (RelativeLayout) mBottomPanel
//				.findViewById(R.id.activity_detail_picture_profile_user_profile);
//		imgDisplayLayout.setVisibility(View.VISIBLE);
//		Resources resources = mContext.getResources();
//		ImageView imgDisplay = (ImageView) mBottomPanel
//				.findViewById(R.id.activity_detail_picture_profile_user_ava_profile);
//		int avaHeightSize = resources
//				.getDimensionPixelSize(R.dimen.activity_setupprofile_img_avatar_height);
//		int avaWidthSize = resources
//				.getDimensionPixelSize(R.dimen.activity_setupprofile_img_avatar_width);
//		String token = UserPreferences.getInstance().getToken();
//		CircleImageRequest imageRequest = new CircleImageRequest(token,
//				userAvata);
//		mImageFetcher.loadImage(imageRequest, imgDisplay, avaWidthSize,
//                avaHeightSize);
  }

  public void showDialodSave(int price, final String imgId) {
    Resources resource = mContext.getResources();
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    String title = resource.getString(R.string.save_dialog_confirm_title);
    String format = "";
    String message = "";
    if (price > 0) {
      format = resource.getString(R.string.save_dialog_confirm_content);
      message = String.format(format, "" + price);
    } else {
      message = resource
          .getString(R.string.save_dialog_confirm_content_0_points);
    }

    Builder builder = new CenterButtonDialogBuilder(mContext, true);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.save_dialog_confirm_negative, null);
    builder.setPositiveButton(R.string.save_dialog_confirm_positive, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        mOnProfilePictureClickListener.onDialogSave(imgId);
      }
    });
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }

  }

  public void showDialodSaveInvalid() {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = mContext.getResources();
    String title = resource.getString(R.string.save_dialog_confirm_title);
    String message = resource
        .getString(R.string.save_dialog_invalid_content);
    Builder builder = new Builder(mContext);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);
    // builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.save_dialog_confirm_positive, null);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialodExternalStorageNotExist() {
    Resources resource = mContext.getResources();
    String title = resource.getString(R.string.save_dialog_confirm_title);
    String message = resource
        .getString(R.string.save_dialog_external_storage_not_exist_content);
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder builder = new Builder(mContext);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.save_dialog_confirm_negative, null);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialogSaveDone(boolean isSuccess) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = mContext.getResources();
    String title = resource.getString(R.string.save_dialog_title);
    String message = "";
    if (isSuccess) {
      message = resource.getString(R.string.save_dialog_ok);
    } else {
      message = resource.getString(R.string.save_dialog_error);
    }
    Builder builder = new Builder(mContext);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.ok, null);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void showDialogChangePicture() {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = mContext.getResources();
    String title = resource.getString(R.string.change_avata_dialog_title);
    String message = resource
        .getString(R.string.change_avata_dialog_content);
    Builder builder = new CenterButtonDialogBuilder(mContext, true);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(title);
    builder.setMessage(message);
    builder.setNegativeButton(R.string.change_avata_negative_button,
        new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mOnProfilePictureClickListener.onDialogChangePicture();
          }
        });
    builder.setPositiveButton(R.string.change_avata_positive_button, null);
    AlertDialog element = builder.show();

    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(element.getContext().getResources().getColor(R.color.transparent));
    }
  }

  public void changeButtonWithAvataStatus(boolean isCurrentAvata) {
    TextView btnSetAvaPic = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_set_as_profile_pic);
    TextView btnChangePic = (TextView) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_change_pic);
    if (isCurrentAvata) {
      btnSetAvaPic.setVisibility(View.GONE);
      btnChangePic.setVisibility(View.VISIBLE);
    } else {
      btnSetAvaPic.setVisibility(View.VISIBLE);
      btnChangePic.setVisibility(View.GONE);
    }
  }

  public void setOnClickListener(OnProfilePictureClickListener onClickListener) {
    this.mOnProfilePictureClickListener = onClickListener;
    mDetailPictureProfileAdapter.setOnClickListener(onClickListener);
  }

  public void setOnPageChangeListener(
      OnProfilePicturePageChangeListener onPageChangeListener) {
    this.mOnProfilePicturePageChangeListener = onPageChangeListener;
    if (mViewPager != null) {
      mViewPager.setOnPageChangeListener(this);
    }
  }

  public void syncSaveButton(String userId, int saveImgPrice) {
    price = saveImgPrice;
    String myId = UserPreferences.getInstance().getUserId();
    syncSaveButton(myId.equals(userId));
  }

  public void syncSaveButton(boolean isMy, int saveImgPrice) {
    price = saveImgPrice;
    syncSaveButton(isMy);
  }

  public void syncSaveButton(boolean isMy) {
    FrameLayout savePicWrap = (FrameLayout) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic_wrap);
    FrameLayout btnSavePic = (FrameLayout) mBottomPanel
        .findViewById(R.id.activity_detail_picture_profile_save_pic_me_wrap);
    if (!isMy) {
      ImageView lockState = (ImageView) mBottomPanel
          .findViewById(R.id.activity_detail_picture_profile_lock_state);
      if (price > 0) {
        lockState.setImageResource(R.drawable.lock_save_pic);
      } else {
        lockState.setImageResource(R.drawable.unlock_save_pic);
      }
      savePicWrap.setVisibility(View.VISIBLE);
      btnSavePic.setVisibility(View.GONE);
    } else {
      btnSavePic.setVisibility(View.VISIBLE);
      savePicWrap.setVisibility(View.GONE);
    }
  }

  @Override
  public void onPageScrollStateChanged(int arg0) {
  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2) {
  }

  @Override
  public void onPageSelected(int arg0) {
    mDetailPictureProfileAdapter.setCurrentImgIndex(arg0);
    if (mOnProfilePicturePageChangeListener != null) {
      mOnProfilePicturePageChangeListener.onPageChanged(arg0);
    }
  }

  public interface OnProfilePictureClickListener {

    void onBtnBackClick(View v);

    void onBtnSeeAllClick(View v);

    void onBtnCommentClick(View v);

    void onBtnChangeProfilePicClick(View v);

    void onBtnSetProfilePicClick(View v);

    void onBtnSaveProfilePicClick(View v);

    void onBtnSaveMyProfilePicClick(View v);

    void onBtnDeletePicClick(View v);

    void onBtnReportPicClick(View v);

    void onBtnLikeClick(View v);

    void onBtnUserProfileClick(View v);

    void onViewPagerClick(View v);

    void onDialogConfirmDelete(DialogInterface dialog, int which);

    void onDialogReportImage(DialogInterface dialog, int which);

    void onDialogSave(String imgId);

    void onDialogConfirmReport(DialogInterface dialog, int which, int reportType);

    void onDialogImgNotFound(boolean isBack);

    void onDialogChangePicture();
  }

  public interface OnProfilePicturePageChangeListener {

    void onPageChanged(int position);
  }
}