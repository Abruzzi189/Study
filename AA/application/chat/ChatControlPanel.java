package com.application.chat;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.viewpagerindicator.CirclePageIndicator;
import glas.bbsystem.R;


public class ChatControlPanel {

  private static final int NUMBER_OF_PAGE = 2;
  private Context mContext;
  private ViewPager mViewPager;
  private CirclePageIndicator mIndicator;
  private ChatControlAdapter mAdapter;
  private IOnControlClicked mOnControlClicked;

  public ChatControlPanel(Context context, ViewPager pager,
      CirclePageIndicator indicator, IOnControlClicked listener) {
    this.mViewPager = pager;
    this.mIndicator = indicator;
    this.mContext = context;
    this.mOnControlClicked = listener;
  }

  public void initView() {
    if (mAdapter == null) {
      mAdapter = new ChatControlAdapter();
    }
    mViewPager.setAdapter(mAdapter);
    mIndicator.setViewPager(mViewPager);
  }

  public interface IOnControlClicked {

    public void onChoosePhoto();

    public void onTakePhoto();

    public void onChooseVideo();

    public void onTakeVideo();

    public void onPreviousPhoto();

    public void onRecord();

    public void onTemplate();
  }

  public class ChatControlAdapter extends PagerAdapter implements
      OnClickListener {

    @Override
    public int getCount() {
      return NUMBER_OF_PAGE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

      LayoutInflater inflater = (LayoutInflater) container.getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View view = null;
      switch (position) {
        case 0:
          view = inflater.inflate(R.layout.item_pager_media_file, null);
          view.findViewById(R.id.item_pager_media_file_choosephoto)
              .setOnClickListener(this);
          view.findViewById(R.id.item_pager_media_file_camera)
              .setOnClickListener(this);
          view.findViewById(R.id.item_pager_media_file_camcorder)
              .setOnClickListener(this);
          view.findViewById(R.id.item_pager_media_file_choosevideo)
              .setOnClickListener(this);
          view.findViewById(R.id.item_pager_media_file_previous_photo)
              .setOnClickListener(this);
          view.findViewById(R.id.item_pager_media_file_audio)
              .setOnClickListener(this);
          break;
        case 1:
          view = inflater.inflate(R.layout.item_pager_media_file_next, null);
          view.findViewById(R.id.item_pager_media_file_template).setOnClickListener(this);
          break;
      }
      ((ViewPager) container).addView(view, 0);
      return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      if (container instanceof ViewPager && object instanceof View) {
        ((ViewPager) container).removeView((View) object);
      }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.item_pager_media_file_camera:
          mOnControlClicked.onTakePhoto();
          break;
        case R.id.item_pager_media_file_camcorder:
          mOnControlClicked.onTakeVideo();
          break;
        case R.id.item_pager_media_file_choosephoto:
          mOnControlClicked.onChoosePhoto();
          break;
        case R.id.item_pager_media_file_choosevideo:
          mOnControlClicked.onChooseVideo();
          break;
        case R.id.item_pager_media_file_previous_photo:
          mOnControlClicked.onPreviousPhoto();
          break;
        case R.id.item_pager_media_file_audio:
          mOnControlClicked.onRecord();
          break;
        case R.id.item_pager_media_file_template:
          mOnControlClicked.onTemplate();
          break;
        default:
          break;
      }
    }
  }
}