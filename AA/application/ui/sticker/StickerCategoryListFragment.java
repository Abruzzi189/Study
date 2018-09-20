package com.application.ui.sticker;

import android.os.Bundle;
import android.widget.ListView;
import com.application.adapters.StickerCategoryListAdapter;
import com.application.util.preferece.UserPreferences;
import com.ntq.api.model.DfeStickerList;
import com.ntq.fragments.PageFragment;
import glas.bbsystem.R;

public class StickerCategoryListFragment extends PageFragment {

  private static final String EXTRA_TYPE = "type";
  private DfeStickerList mDfeStickerList;
  private StickerCategoryListAdapter mListAdapter;
  private ListView mListView;
  private int mStickerCategoryType;

  public static StickerCategoryListFragment newInstance(int type) {
    StickerCategoryListFragment fragment = new StickerCategoryListFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(EXTRA_TYPE, type);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mStickerCategoryType = getArguments().getInt(EXTRA_TYPE);
  }

  private void initView() {
    mListView = (ListView) mDataView.findViewById(R.id.list);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_stickercategorylist;
  }

  @Override
  protected void onInitViewBinders() {

  }

  @Override
  protected void rebindViews() {

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initView();
    if (!isReady()) {
      switchToLoading();
      requestData();
    }
  }

  @Override
  public void onDataChanged() {
    super.onDataChanged();
    if (mDfeStickerList.getBucketCount() <= 0) {
      return;
    }
    mDfeStickerList.removeDataChangedListener(this);
    mDfeStickerList.removeErrorListener(this);
    mListAdapter = new StickerCategoryListAdapter(mContext,
        mDfeStickerList, mImageLoader);
    mListView.setAdapter(mListAdapter);
  }

  @Override
  protected void requestData() {
    if (mDfeStickerList == null) {
      String token = UserPreferences.getInstance().getToken();
      mDfeStickerList = new DfeStickerList(token, mDfeApi, "jp",
          mStickerCategoryType, 0, 10);
      mDfeStickerList.addDataChangedListener(this);
      mDfeStickerList.addErrorListener(this);
    }
    mDfeStickerList.startLoadItems();
  }

  private boolean isReady() {
    return mDfeStickerList != null && mDfeStickerList.isReady() ? true
        : false;
  }

}
