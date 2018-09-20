package com.application.ui;

import android.os.Bundle;
import com.application.util.preferece.UserPreferences;
import com.ntq.activities.NTQActivity;
import com.ntq.api.model.DfeStickerInfo;
import glas.bbsystem.R;

public class StickerShopActivity extends NTQActivity {

  private DfeStickerInfo mDfeStickerInfo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!isReady()) {
      requestData();
      switchToLoading();
    }
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.activity_stickershop;
  }

  @Override
  public void onDataChanged() {
    super.onDataChanged();
  }

  @Override
  protected void requestData() {
    if (mDfeStickerInfo == null) {
      mDfeStickerInfo = new DfeStickerInfo(getDfeApi());
      mDfeStickerInfo.addDataChangedListener(this);
      mDfeStickerInfo.addErrorListener(this);
    }
    String token = UserPreferences.getInstance().getToken();
    mDfeStickerInfo.makeRequest(token);
  }

  private boolean isReady() {
    return mDfeStickerInfo != null && mDfeStickerInfo.isReady() ? true
        : false;
  }

}
