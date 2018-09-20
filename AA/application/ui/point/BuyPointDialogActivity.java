package com.application.ui.point;

import android.view.LayoutInflater;
import android.view.View;
import glas.bbsystem.R;


public class BuyPointDialogActivity extends BuyPointActivity {

  @Override
  protected void initActionBar() {
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_buy_point;
  }

  @Override
  protected void initView() {
    super.initView();
    LayoutInflater inflater = LayoutInflater.from(this);
    View footer = inflater.inflate(R.layout.footer_buy_point_dialog, null);
    mListView.addFooterView(footer);
  }
}
