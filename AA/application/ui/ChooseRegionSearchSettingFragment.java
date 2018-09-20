package com.application.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.application.entity.Region;
import com.application.entity.RegionGroup;
import com.application.ui.region.RegionAdapter;
import com.application.util.RegionUtils;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class ChooseRegionSearchSettingFragment extends BaseFragment {

  public static final String KEY_REGION_CODE_SELECTED = "region_code_selected";
  private ListView mListView;
  private List<RegionGroup> mRegionGroups;
  private RegionAdapter mRegionAdapter;
  private ArrayList<Integer> regionCode;

  public static ChooseRegionSearchSettingFragment newInstance(
      ArrayList<Integer> regionCode) {
    ChooseRegionSearchSettingFragment fragment1 = new ChooseRegionSearchSettingFragment();
    Bundle bundle = new Bundle();
    bundle.putIntegerArrayList(KEY_REGION_CODE_SELECTED, regionCode);
    fragment1.setArguments(bundle);
    return fragment1;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      regionCode = savedInstanceState
          .getIntegerArrayList(KEY_REGION_CODE_SELECTED);
    } else if (getArguments() != null) {
      regionCode = getArguments().getIntegerArrayList(
          KEY_REGION_CODE_SELECTED);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putIntegerArrayList(KEY_REGION_CODE_SELECTED, regionCode);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_choose_region,
        container, false);
    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initData();
  }

  private void initView(View view) {
    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object instanceof com.application.entity.Region) {
          com.application.entity.Region region = (com.application.entity.Region) object;
          mRegionAdapter.updateRegionForMultiChoice(region);
        }
      }
    });
  }

  private void initData() {
    List<Region> regions = new ArrayList<Region>();
    for (Integer integer : regionCode) {
      Region region = new Region();
      region.setCode(integer);
      regions.add(region);
    }
    RegionUtils regionUtils = new RegionUtils(mAppContext);
    mRegionGroups = regionUtils.getRegionGroups();
    mRegionAdapter = new RegionAdapter(getActivity().getLayoutInflater(),
        mRegionGroups, RegionAdapter.CHOICE_MODE_MULTI, regions);
    mListView.setAdapter(mRegionAdapter);
  }

  public ArrayList<Integer> getRegionCodeSelected() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (Region region : mRegionAdapter.getRegionSelected()) {
      list.add(region.getCode());
    }
    return list;
  }

  public void onDone() {
    ArrayList<Integer> list = getRegionCodeSelected();
    Intent intent = new Intent();
    intent.putExtra(KEY_REGION_CODE_SELECTED, list);
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, intent);
    mNavigationManager.goBack();
  }

}
