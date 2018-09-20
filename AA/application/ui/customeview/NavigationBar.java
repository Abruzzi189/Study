package com.application.ui.customeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.util.LogUtils;
import glas.bbsystem.R;


public class NavigationBar extends RelativeLayout implements OnClickListener {

  private LayoutInflater mInflater;
  private Button mbtnLeft;
  private Button mbtnRight;
  private TextView mtxtCenter;
  private TextView mtxtNotification;
  private ImageView mimgLeft;
  private ImageView mimgCenter;
  private ImageView mimgRight;
  private ImageView mimgProfile;
  private TextView mTextViewRemain;
  private Context mContext;

  private OnNavigationClickListener mOnNavigationClickListener;
  private boolean isShowUnread = true;

  public NavigationBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initView();
  }

  public void setOnNaviagtionClickListener(
      OnNavigationClickListener onNavigationClickListener) {
    this.mOnNavigationClickListener = onNavigationClickListener;
  }

  private void initView() {
    this.setBackgroundResource(R.drawable.nav_background);
    mInflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mInflater.inflate(R.layout.cv_navigation_bar, this, true);

    mbtnLeft = (Button) findViewById(R.id.cv_navigation_bar_btn_left);
    mbtnRight = (Button) findViewById(R.id.cv_navigation_bar_btn_right);
    mtxtCenter = (TextView) findViewById(R.id.cv_navigation_bar_txt_center);
    mtxtNotification = (TextView) findViewById(R.id.cv_navigation_bar_txt_notification);
    mimgCenter = (ImageView) findViewById(R.id.cv_navigation_bar_img_center);
    mimgLeft = (ImageView) findViewById(R.id.cv_navigation_bar_img_left);
    mimgRight = (ImageView) findViewById(R.id.cv_navigation_bar_img_right);
    mimgProfile = (ImageView) findViewById(R.id.cv_navigation_bar_img_profile);
    mTextViewRemain = (TextView) findViewById(R.id.cv_navigation_bar_tv_remain);

    if (mbtnLeft != null) {
      mbtnLeft.setOnClickListener(this);
    }
    if (mbtnRight != null) {
      mbtnRight.setOnClickListener(this);
    }
    if (mtxtCenter != null) {
      mtxtCenter.setOnClickListener(this);
    }
    if (mimgCenter != null) {
      mimgCenter.setOnClickListener(this);
    }
    if (mimgLeft != null) {
      mimgLeft.setOnClickListener(this);
    }
    if (mimgRight != null) {
      mimgRight.setOnClickListener(this);
    }
  }

  public void setNavigationLeftLogo(int resId) {
    if (mimgLeft != null && mbtnLeft != null) {
      mimgLeft.setImageResource(resId);
      mimgLeft.setVisibility(View.VISIBLE);
      mbtnLeft.setVisibility(View.GONE);
      mtxtNotification.setVisibility(View.GONE);
    }
  }

  public void setNavigationLeftTitle(int resId) {
    if (mbtnLeft != null && mimgLeft != null) {
      mbtnLeft.setText(resId);
      mbtnLeft.setVisibility(View.VISIBLE);
      mimgLeft.setVisibility(View.GONE);
      mtxtNotification.setVisibility(View.GONE);
    }
  }

  public void setNavigationLeftTitle(String text) {
    if (mbtnLeft != null && mimgLeft != null) {
      mbtnLeft.setText(text);
      mbtnLeft.setVisibility(View.VISIBLE);
      mimgLeft.setVisibility(View.GONE);
    }
  }

  public void setNavigationRightLogo(int resId) {
    if (mimgRight != null && mbtnRight != null) {
      mimgRight.setImageResource(resId);
      mimgRight.setVisibility(View.VISIBLE);
      mbtnRight.setVisibility(View.GONE);
      mtxtNotification.setVisibility(View.GONE);
    }
  }

  public void setNavigationRightTitle(int resId) {
    if (mbtnRight != null && mimgRight != null) {
      mbtnRight.setText(resId);
      mbtnRight.setVisibility(View.VISIBLE);
      mimgRight.setVisibility(View.GONE);
      mtxtNotification.setVisibility(View.GONE);
    }
  }

  public void setCenterLogo(int resId) {
    if (mimgCenter != null && mtxtCenter != null) {
      mimgCenter.setImageResource(resId);
      mimgCenter.setVisibility(View.VISIBLE);
      mtxtCenter.setVisibility(View.GONE);
    }
  }

  public void setCenterTitle(int resId) {
    if (mtxtCenter != null && mimgCenter != null) {
      mtxtCenter.setText(resId);
      mtxtCenter.setVisibility(View.VISIBLE);
      mimgCenter.setVisibility(View.GONE);
//			String title = getResources().getString(resId);
//			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mtxtCenter
//					.getLayoutParams();
//			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
//			if (title.length() < 16) {
//				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//			} else {
//				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
//				layoutParams.addRule(RelativeLayout.RIGHT_OF,
//						R.id.cb_navigation_bar_rl_left);
//				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.frmRight);
//			}
//			mtxtCenter.setLayoutParams(layoutParams);
    }
  }

  public void setCenterTitle(String title) {
    if (mtxtCenter != null && mimgCenter != null && mimgCenter != null) {
      mtxtCenter.setText(title);
      mtxtCenter.setVisibility(View.VISIBLE);
      mimgCenter.setVisibility(View.GONE);
      if (title != null) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mtxtCenter
            .getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        LogUtils.d("title.length()", String.valueOf(title.length()));
        if (title.length() < 16) {
          layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
          layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
          layoutParams.addRule(RelativeLayout.RIGHT_OF,
              R.id.cb_navigation_bar_rl_left);
          layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.frmRight);
        }
        mtxtCenter.setLayoutParams(layoutParams);
      }
    }
  }

  public void setNavigationRightVisibility(int visibility) {
    if (mimgRight != null && mbtnRight != null) {
      mimgRight.setVisibility(visibility);
      mbtnRight.setVisibility(visibility);
    }
  }

  public void setNavigationLeftVisibility(int visibility) {
    if (mimgLeft != null && mbtnLeft != null) {
      mimgLeft.setVisibility(visibility);
      mbtnLeft.setVisibility(visibility);
    }
  }

  public void setInvisiableShakeChat() {
    mimgLeft.setVisibility(View.INVISIBLE);
    mimgRight.setVisibility(View.INVISIBLE);
    mtxtNotification.setVisibility(View.GONE);
  }


  public void setCenterVisibility(int visibility) {
    if (mimgCenter != null && mtxtCenter != null) {
      mimgCenter.setVisibility(visibility);
      mtxtCenter.setVisibility(visibility);
    }
  }

  public void setProfileVisibility(int visibility) {
    if (mimgProfile != null && mbtnRight != null && mTextViewRemain != null) {
      mimgProfile.setVisibility(visibility);
      mTextViewRemain.setVisibility(visibility);
      mbtnRight.setVisibility(View.GONE);
    }
  }

  public View getProfileView() {
    return mimgProfile;
  }

  public View getRemainView() {
    return mTextViewRemain;
  }

  /**
   * Reset all value in NavigationBar
   */
  public void reset() {
    if (mtxtCenter != null && mtxtNotification != null && mbtnLeft != null
        && mbtnRight != null && mimgProfile != null
        && mimgCenter != null && mimgLeft != null && mimgRight != null
        && mTextViewRemain != null) {
      mtxtCenter.setVisibility(View.GONE);
      mtxtNotification.setVisibility(View.GONE);
      mbtnLeft.setVisibility(View.GONE);
      mbtnRight.setVisibility(View.GONE);
      mimgProfile.setVisibility(View.GONE);
      mimgCenter.setVisibility(View.GONE);
      mimgLeft.setVisibility(View.GONE);
      mimgRight.setVisibility(View.GONE);
      mTextViewRemain.setVisibility(View.GONE);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.cv_navigation_bar_btn_left:
      case R.id.cv_navigation_bar_img_left:
        if (mOnNavigationClickListener != null) {
          mOnNavigationClickListener.onNavigationLeftClick(v);
        }
        break;
      case R.id.cv_navigation_bar_btn_right:
      case R.id.cv_navigation_bar_img_right:
        if (mOnNavigationClickListener != null) {
          mOnNavigationClickListener.onNavigationRightClick(v);
        }
        break;
      default:
        break;
    }
  }

  public void setNotification(String notification) {
    if (mtxtNotification != null) {
      mtxtNotification.setText(notification);
      if (isShowUnReadMessage()) {
        mtxtNotification.setVisibility(View.VISIBLE);
        if (mtxtNotification.getText().toString().equals("0")
            || mtxtNotification.getText().length() == 0) {
          mtxtNotification.setVisibility(View.GONE);
        }
      } else {
        mtxtNotification.setVisibility(View.GONE);
      }
    }
  }

  public boolean isShowUnReadMessage() {
    return isShowUnread;
  }

  /**
   * Must use after setRightLogo()
   */
  public void setShowUnreadMessage(boolean isShow) {
    isShowUnread = isShow;
    if (isShowUnReadMessage()) {
      mtxtNotification.setVisibility(View.VISIBLE);
      if (mtxtNotification.getText().toString().equals("0")
          || mtxtNotification.getText().length() == 0) {
        mtxtNotification.setVisibility(View.GONE);
      }
    } else {
      mtxtNotification.setVisibility(View.GONE);
    }
  }

  public Button getButtonLeft() {
    return mbtnLeft;
  }

  public Button getButtonRight() {
    return mbtnRight;
  }

  public ImageView getImageLeft() {
    return mimgLeft;
  }

  public ImageView getImageRight() {
    return mimgRight;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public void setToBlackColor() {
    if (this != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        this.setBackground(getResources().getDrawable(R.drawable.nav_background_black));
      } else {
        this.setBackgroundResource(R.drawable.nav_background_black);
      }
    }
    if (mimgLeft != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mimgLeft.setBackground(getResources().getDrawable(R.drawable.nav_btn_background_black));
      } else {
        mimgLeft.setBackgroundResource(R.drawable.nav_btn_background_black);
      }
    }
    if (mimgRight != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mimgRight.setBackground(getResources().getDrawable(R.drawable.nav_btn_background_black));
      } else {
        mimgRight.setBackgroundResource(R.drawable.nav_btn_background_black);
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public void resetToOriginalColor() {
    if (this != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        this.setBackground(getResources().getDrawable(R.drawable.nav_background));
      } else {
        this.setBackgroundResource(R.drawable.nav_background);
      }
    }
    if (mimgLeft != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mimgLeft.setBackground(getResources().getDrawable(R.drawable.nav_btn_background));
      } else {
        mimgLeft.setBackgroundResource(R.drawable.nav_btn_background);
      }
    }
    if (mimgRight != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mimgRight.setBackground(getResources().getDrawable(R.drawable.nav_btn_background));
      } else {
        mimgRight.setBackgroundResource(R.drawable.nav_btn_background);
      }
    }
  }

  public interface OnNavigationClickListener {

    public void onNavigationLeftClick(View view);

    public void onNavigationRightClick(View view);
  }
}

