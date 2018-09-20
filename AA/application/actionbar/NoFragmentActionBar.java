package com.application.actionbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.common.GalleryActivity;
import com.application.ui.account.ChangePasswordActivity;
import com.application.ui.account.ForgotPasswordSendCode;
import com.application.ui.account.ForgotPasswordSendEmail;
import com.application.ui.account.LoginActivity;
import com.application.ui.account.RegisterActivity;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.ui.point.BuyPointActivity;
import com.application.ui.point.freepoint.FreePointGetActivity;
import com.application.ui.point.freepoint.FreePointGetEndActivity;
import glas.bbsystem.R;


public class NoFragmentActionBar {

  private Button mBtnLeft;
  private Button mBtnRight;
  private ImageView mImgLeft;
  private ImageView mImgRight;
  private TextView mTxtCenter;

  private AppCompatActivity mActivity;
  private ActionBar mActionBar;
  private View.OnClickListener defaultBackButtonClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      mActivity.finish();
    }
  };

  public NoFragmentActionBar(AppCompatActivity activity) {
    mActivity = activity;
    mActionBar = activity.getSupportActionBar();
    int newResId = findResourceIdForActionbar(activity);
    mActionBar.setDisplayShowHomeEnabled(false);
    mActionBar.setDisplayShowCustomEnabled(true);
    mActionBar.setDisplayShowTitleEnabled(false);
    mActionBar.setCustomView(newResId);
    View customView = mActionBar.getCustomView();
    ViewParent parent = customView.getParent();
    if (parent instanceof Toolbar) {
      ((Toolbar) parent).setContentInsetsAbsolute(0, 0);
    }
  }

  public void syncActionBar() {
    findChildViews();
    setupChildViews();
  }

  private int findResourceIdForActionbar(AppCompatActivity activity) {
    if (activity instanceof ManageBackstageActivity) {
      return R.layout.cv_native_actionbar_only_back_black;
    } else if (activity instanceof ChangePasswordActivity) {
      return R.layout.cv_native_actionbar_button_right;
    } else if (activity instanceof GalleryActivity) {
      return R.layout.cv_native_actionbar_button_left;
    }
    return R.layout.cv_native_actionbar_only_back;
  }

  public void setTextCenterColor(int resColor) {
    mTxtCenter.setTextColor(mActivity.getResources().getColor(resColor));
  }

  private void findChildViews() {
    View actionbarView = mActionBar.getCustomView();
    mBtnLeft = (Button) actionbarView
        .findViewById(R.id.cv_navigation_bar_btn_left);
    mBtnRight = (Button) actionbarView
        .findViewById(R.id.cv_navigation_bar_btn_right);
    mImgLeft = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_left);
    mImgRight = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_right);
    mTxtCenter = (TextView) actionbarView
        .findViewById(R.id.cv_navigation_bar_txt_center);
  }

  private void setupChildViews() {
    if (mImgLeft != null) {
      setupImgLeft();
    }
    if (mImgRight != null) {
      setupImgRight();
    }
    if (mBtnLeft != null) {
      setupLeftButton();
    }
    if (mBtnRight != null) {
      setupRightButton();
    }
    if (mTxtCenter != null) {
      setupCenterTextview();
    }
  }

  protected void setupLeftButton() {
    mBtnLeft.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        defaultBackButtonClickListener.onClick(v);
      }
    });

    if (mActivity instanceof GalleryActivity) {
      if (!((GalleryActivity) mActivity).isGalleryView()) {
        mBtnLeft.setBackgroundColor(mActivity.getResources().getColor(
            android.R.color.transparent));
        mBtnLeft.setText(R.string.common_close);
        mBtnLeft.setVisibility(View.VISIBLE);
        mImgLeft.setVisibility(View.GONE);
      } else {
        mBtnLeft.setVisibility(View.GONE);
      }
    }
  }

  protected void setupRightButton() {
    mBtnRight.setVisibility(View.VISIBLE);
    if (mActivity instanceof ChangePasswordActivity) {
      mBtnRight.setText(R.string.common_done);
    }
    mBtnRight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mActivity instanceof ChangePasswordActivity) {
          ((ChangePasswordActivity) mActivity)
              .onChangeEmailPassword();
        }
      }
    });
  }

  protected void setupCenterTextview() {
    mTxtCenter.setVisibility(View.VISIBLE);
    int resId = -1;
    if (mActivity instanceof ChangePasswordActivity) {
      resId = R.string.change_email_password_title;
    } else if (mActivity instanceof BuyPointActivity) {
      resId = R.string.buy_points;
    } else if (mActivity instanceof LoginActivity) {
      resId = R.string.login_login_button;
    } else if (mActivity instanceof ForgotPasswordSendCode) {
      resId = R.string.login_forgot_verify_dialog_title;
    } else if (mActivity instanceof ForgotPasswordSendEmail) {
      resId = R.string.forgot_password;
    } else if (mActivity instanceof FreePointGetActivity) {
      resId = R.string.free_point_title;
    } else if (mActivity instanceof FreePointGetEndActivity) {
      resId = R.string.free_point_end_title;
    } else if (mActivity instanceof RegisterActivity) {
      resId = R.string.title_register;
    }
    if (resId != -1) {
      mTxtCenter.setText(resId);
    } else {
      mTxtCenter.setText("");
    }
    mTxtCenter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }

  protected void setupImgLeft() {
    int resId = R.drawable.nav_btn_back;
    mImgLeft.setImageResource(resId);
    mImgLeft.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        defaultBackButtonClickListener.onClick(v);
      }
    });

    if (mActivity instanceof GalleryActivity) {
      if (((GalleryActivity) mActivity).isGalleryView()) {
//				mImgLeft.setImageResource(R.drawable.ic_back);
        mImgLeft.setVisibility(View.VISIBLE);
        mBtnLeft.setVisibility(View.GONE);
      } else {
        mImgLeft.setVisibility(View.GONE);
      }
    }
    //hiepuh
    if (mActivity instanceof ManageBackstageActivity) {
      mImgLeft.setImageResource(R.drawable.ic_back_backstage);
      mImgLeft.setVisibility(View.VISIBLE);
    }
  }

  protected void setupImgRight() {
    mImgRight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      }
    });
  }

  public void setTextCenterTitle(String title) {
    mTxtCenter.setText(title);
  }

  public void setTextCenterTitle(int resId) {
    mTxtCenter.setText(mActionBar.getThemedContext().getString(resId));
  }

}
