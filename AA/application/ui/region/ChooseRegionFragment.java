package com.application.ui.region;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.application.entity.RegionGroup;
import com.application.ui.BaseFragment;
import com.application.util.LocationUtils.Region;
import com.application.util.RegionUtils;
import glas.bbsystem.R;
import java.util.List;


public class ChooseRegionFragment extends BaseFragment implements
    OnItemClickListener {

  public static final String EXTRA_REGION_SELECTED = "region_selected";
  public static final String EXTRA_IS_AUTO = "is_auto";
  private static final String KEY_REGION = "region";
  private static final String KEY_AUTO = "auto";
  private int mRegion = Region.REGION_NOT_SET;
  private boolean mIsAuto;
  private String[] mRegionNames;
  private RegionUtils mRegionUtils;
  private List<RegionGroup> mRegionGroups;
  private RegionAdapter mRegionAdapter;

  private ListView mListView;

  public static ChooseRegionFragment newInstance(int regionSelected,
      boolean isAuto) {
    ChooseRegionFragment chooseRegionFragment = new ChooseRegionFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_REGION, regionSelected);
    bundle.putBoolean(KEY_AUTO, isAuto);
    chooseRegionFragment.setArguments(bundle);
    return chooseRegionFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      setRegion(savedInstanceState.getInt(KEY_REGION));
      mIsAuto = savedInstanceState.getBoolean(KEY_AUTO);
    } else {
      Bundle bundle = getArguments();
      setRegion(bundle.getInt(KEY_REGION));
      mIsAuto = bundle.getBoolean(KEY_AUTO);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_choose_region,
        container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(this);

  }

  private void initData() {
    com.application.entity.Region region = new com.application.entity.Region();
    region.setCode(getRegion());
    RegionUtils regionUtils = new RegionUtils(mAppContext);
    mRegionGroups = regionUtils.getRegionGroups();
    mRegionAdapter = new RegionAdapter(getActivity().getLayoutInflater(),
        mRegionGroups, RegionAdapter.CHOICE_MODE_SINGLE, region);
    mListView.setAdapter(mRegionAdapter);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mActionBar.setBackButtonClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
          Intent intent = new Intent();
          intent.putExtra(EXTRA_REGION_SELECTED, mRegion);
          intent.putExtra(EXTRA_IS_AUTO, mIsAuto);
          fragment.onActivityResult(getTargetRequestCode(),
              Activity.RESULT_OK, intent);
        }
        mNavigationManager.goBack();
      }
    });
    initData();
  }

  public void onBackPress() {
    Fragment fragment = getTargetFragment();
    if (fragment != null) {
      Intent intent = new Intent();
      intent.putExtra(EXTRA_REGION_SELECTED, mRegion);
      intent.putExtra(EXTRA_IS_AUTO, mIsAuto);
      fragment.onActivityResult(getTargetRequestCode(),
          Activity.RESULT_OK, intent);
    }
    mNavigationManager.goBack();
  }

  public int getRegion() {
    return this.mRegion;
  }

  public void setRegion(int region) {
    this.mRegion = region;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    mIsAuto = false;
    Object object = parent.getAdapter().getItem(position);
    if (object instanceof com.application.entity.Region) {
      com.application.entity.Region region = (com.application.entity.Region) object;
      mRegion = region.getCode();
      mRegionAdapter.updateRegionForSingleChoice(region);
    }
  }

  public void onSave() {
    Fragment fragment = getTargetFragment();
    if (fragment != null) {
      Intent intent = new Intent();
      intent.putExtra(EXTRA_REGION_SELECTED, mRegion);
      intent.putExtra(EXTRA_IS_AUTO, mIsAuto);
      fragment.onActivityResult(getTargetRequestCode(),
          Activity.RESULT_OK, intent);
    }
    mNavigationManager.goBack();
  }
}