package com.application.ui.sticker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.adapters.StickerShopAdapter;
import glas.bbsystem.R;

public class StickerShopFragment extends Fragment {

  private static final int[] STICKER_CATEGORY = new int[]{0, 1, 2, 3};
  private ViewPager mViewPager;
  private StickerShopAdapter mStickerShopAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_stickershop, container,
        false);
    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mStickerShopAdapter = new StickerShopAdapter(getChildFragmentManager(),
        STICKER_CATEGORY);
    mViewPager.setAdapter(mStickerShopAdapter);
  }

  private void initView(View view) {
    mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
  }
}
