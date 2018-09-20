package com.application.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.application.ui.sticker.StickerCategoryListFragment;

public class StickerShopAdapter extends FragmentStatePagerAdapter {

  private int[] mStickerCategories;

  public StickerShopAdapter(FragmentManager fm, int[] stickerCategories) {
    super(fm);
    mStickerCategories = stickerCategories;
  }

  @Override
  public Fragment getItem(int arg0) {
    return StickerCategoryListFragment
        .newInstance(mStickerCategories[arg0]);
  }

  @Override
  public int getCount() {
    return mStickerCategories.length;
  }

}
