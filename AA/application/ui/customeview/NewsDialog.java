package com.application.ui.customeview;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.application.Config;
import com.application.entity.News;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.util.LogUtils;
import com.application.util.Utility;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by HungHN on 4/5/2016.
 */
public abstract class NewsDialog extends Dialog {

  private Context mContext;
  private PullToRefreshListView mPullToRefreshListView;
  private ListView mListNews;
  private NewsAdapter mNewAdapter;
  private Button mDone;
  private CheckBox mNewChecked;

  private TextView txtEmpty;
  private List<News> mNewsList;
  private PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
    }
  };

  public NewsDialog(Context context, List<News> newsList) {
    super(context, R.style.news_dialog);
    mContext = context;
    this.mNewsList = newsList;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_fragment_news);
    setCanceledOnTouchOutside(true);
    setCancelable(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Config.DEBUG) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

    int[] screenSize = Utility.getScreenSize(mContext);
    int height = (int) (screenSize[1] * 0.95); //set height to 90% of total
    int width = (int) (screenSize[0] * 0.96); //set width to 90% of total
    getWindow().setLayout(width, height); //set layout

    initialListView();
    mDone = (Button) findViewById(R.id.news_done);
    mNewChecked = (CheckBox) findViewById(R.id.new_checked);

    mDone.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mNewChecked.isChecked()) {
          onSaveDontShowNewsToday();
        }
        NewsDialog.this.dismiss();
      }
    });
  }

  /**
   * if tick checkbox don\'t show news today
   */
  protected abstract void onSaveDontShowNewsToday();

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // Tap anywhere to close dialog.
    Rect dialogBounds = new Rect();
    getWindow().getDecorView().getHitRect(dialogBounds);
    if (isShowing() && event.getAction() == MotionEvent.ACTION_DOWN && !dialogBounds
        .contains((int) event.getX(),
            (int) event.getY())) {
      dismiss();
      // stop dialog closing
    } else {
      LogUtils.d("HungHN", "Touch: " + event.getX() + " ; " + event.getY());
    }
    return false;
  }

  private void initialListView() {
    mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list_news);
    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    Resources resource = mContext.getResources();
    mPullToRefreshListView.setPullLabelFooter(resource
        .getString(R.string.pull_to_load_more_pull_label));
    mPullToRefreshListView.setReleaseLabelFooter(resource
        .getString(R.string.pull_to_load_more_release_label));
    mListNews = mPullToRefreshListView.getRefreshableView();
    txtEmpty = new TextView(mContext);
    txtEmpty.setGravity(Gravity.CENTER);
    txtEmpty.setText(R.string.common_loading);
    txtEmpty.setTextColor(Color.WHITE);
    mPullToRefreshListView.setEmptyView(txtEmpty);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      View divider = new View(mContext);
      divider.setBackgroundColor(resource.getColor(
          android.R.color.transparent));
      mListNews.addFooterView(divider);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mNewAdapter == null) {
      if (mNewsList == null) {
        mNewsList = new ArrayList<>();
      }
      mNewAdapter = new NewsAdapter(mContext, mNewsList);
    }
    mListNews.setAdapter(mNewAdapter);
  }
}
