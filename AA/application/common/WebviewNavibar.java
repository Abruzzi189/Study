package com.application.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.application.constant.Constants;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class WebviewNavibar extends RelativeLayout implements OnClickListener {

  private IOnNaviButtonClicked naviButtonListener;

  private ImageView imgBack;
  private ImageView imgPrevious;
  private ImageView imgTop;
  private ImageView imgRefresh;

  public WebviewNavibar(Context context) {
    super(context);
    initView();
  }

  public WebviewNavibar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public WebviewNavibar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  private void initView() {
    LayoutInflater inflater = (LayoutInflater) getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.webview_navibar, this, false);
    addView(view);

    imgBack = (ImageView) findViewById(R.id.back);
    imgBack.setOnClickListener(this);

    imgPrevious = (ImageView) findViewById(R.id.previous);
    imgPrevious.setOnClickListener(this);

    imgTop = (ImageView) findViewById(R.id.top);
    imgTop.setOnClickListener(this);

    imgRefresh = (ImageView) findViewById(R.id.refresh);
    imgRefresh.setOnClickListener(this);

    // Default initial view
    notifyDataSetChanged(false, false);
  }

  public void notifyDataSetChanged(boolean isGoBack, boolean isForward) {
    if (isGoBack) {
      imgBack.setImageResource(R.drawable.ic_action_image_navigate_before_active);
    } else {
      imgBack.setImageResource(R.drawable.ic_action_image_navigate_before_dark);
    }

    if (isForward) {
      imgPrevious.setImageResource(R.drawable.ic_action_image_navigate_next_active);
    } else {
      imgPrevious.setImageResource(R.drawable.ic_action_image_navigate_next_dark);
    }

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getUserId() != null
        && preferences.getUserId().length() > 0
        && preferences.getFinishRegister() == Constants.FINISH_REGISTER_YES) {
      imgTop.setImageResource(R.drawable.ic_action_action_home_active);
    } else {
      imgTop.setImageResource(R.drawable.ic_action_action_home_dark);
    }
  }

  public void setOnNaviButtonClicked(IOnNaviButtonClicked onNaviButtonClicked) {
    this.naviButtonListener = onNaviButtonClicked;
  }

  @Override
  public void onClick(View v) {
    if (naviButtonListener == null) {
      return;
    }

    int id = v.getId();
    switch (id) {
      case R.id.back:
        naviButtonListener.onGoBackClick();
        break;
      case R.id.previous:
        naviButtonListener.onGoForwardClick();
        break;
      case R.id.refresh:
        naviButtonListener.onRefreshClick();
        break;
      case R.id.top:
        naviButtonListener.onHomeClick();
        break;
    }
  }

  public interface IOnNaviButtonClicked {

    public void onGoBackClick();

    public void onGoForwardClick();

    public void onRefreshClick();

    public void onHomeClick();
  }
}