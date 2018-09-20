package com.application.ui;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.application.ui.settings.MeetPeopleSetting;
import com.application.util.RegionUtils;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class RegionSearchSettingFragment extends BaseFragment {

  public final static String RETURN_REGION = "return_region";
  public final static String RETURN_DISTANCE = "return_distance";
  public final static int REQUEST_REGION = 100;
  private final static int REGION_INDEX = 0;
  private final static int WORLD_INDEX = 1;
  private final static int REGION_VALUE = MeetPeopleSetting.REGION_VALUE;
  private final static int WORLD_VALUE = MeetPeopleSetting.WORLD_VALUE;
  private final static String EXTRA_REGIONS = "extra_regions";
  private final static String EXTRA_DISTANCE = "extra_distance";
  private ListView mRegionListView;
  private List<String> mRegionNames = new ArrayList<String>();
  private int mDistance;
  private List<Integer> mRegions = new ArrayList<Integer>();
  private RegionUtils mRegionUtils;

  private OnItemClickListener onRegionClickListener = new OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
      if (position == WORLD_INDEX) {
        // world
        mRegionListView.clearChoices();
        mDistance = WORLD_VALUE;
        mRegionListView.setItemChecked(position, true);
      } else if (position == REGION_INDEX) {
        // all regions
        mRegionListView.clearChoices();
        mDistance = REGION_VALUE;
        mRegionListView.setItemChecked(REGION_INDEX, true);
        // mRegionListView.setItemChecked(ALL_REGION_INDEX, true);

        ChooseRegionSearchSettingFragment fragment1 = ChooseRegionSearchSettingFragment
            .newInstance((ArrayList<Integer>) mRegions);
        fragment1.setTargetFragment(getRootParentFragment(RegionSearchSettingFragment.this),
            REQUEST_REGION);
        mNavigationManager.addPage(fragment1);
      }
    }
  };
  private ArrayAdapter<String> regionAdapter;

  public static RegionSearchSettingFragment newInstance(int[] regions,
      int distance) {
    RegionSearchSettingFragment fragment = new RegionSearchSettingFragment();
    Bundle bundle = new Bundle();
    bundle.putIntArray(EXTRA_REGIONS, regions);
    bundle.putInt(EXTRA_DISTANCE, distance);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      int[] regionSelected = bundle.getIntArray(EXTRA_REGIONS);
      if (regionSelected != null) {
        for (int item : regionSelected) {
          mRegions.add(item);
        }
      }
      mDistance = bundle.getInt(EXTRA_DISTANCE);
    }
    mRegionNames
        .add(getString(R.string.region_search_setting_all_region_text));
    mRegionNames
        .add(getString(R.string.region_search_setting_world_region_text));
    mRegionUtils = new RegionUtils(mAppContext);
  }

  /**
   * tungdx: not use this
   */
  public void initilize() {
    mRegionNames.remove(3);
    StringBuilder builder = new StringBuilder();
    builder.append(getString(R.string.region_search_setting_all_region_text));
    builder.append(":");
    builder.append(getRegionNameList());
    mRegionNames.add(3, builder.toString());
    regionAdapter = new ArrayAdapter<String>(mAppContext,
        R.layout.item_list_region_search_setting, mRegionNames);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_region_search_setting,
        container, false);
    mRegionListView = (ListView) view.findViewById(R.id.region_list);
    regionAdapter = new ArrayAdapter<String>(mAppContext,
        R.layout.item_list_region_search_setting, mRegionNames);
    mRegionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    mRegionListView.setAdapter(regionAdapter);
    mRegionListView.setOnItemClickListener(onRegionClickListener);

    switch (mDistance) {
      case WORLD_VALUE:
        mRegionListView.setItemChecked(WORLD_INDEX, true);
        break;
      case REGION_VALUE:
        mRegionListView.setItemChecked(REGION_INDEX, true);
        if (mRegions.size() == 0) {
          // mRegionListView.setItemChecked(ALL_REGION_INDEX, true);
        } else {
          for (Integer item : mRegions) {
            String name = mRegionUtils.getRegionName(item);
            int position = mRegionNames.indexOf(name);
            mRegionListView.setItemChecked(position, true);
          }
        }
        break;
      default:
        break;
    }

    return view;
  }

  private String getRegionNameList() {
    StringBuilder builder = new StringBuilder();
    for (Integer item : mRegions) {
      builder.append(mRegionUtils.getRegionName(item));
      builder.append(", ");
    }
    return builder.toString();
  }

  public void onSave(View view) {
    if (mDistance == REGION_VALUE && (mRegions == null || mRegions.isEmpty())) {
      android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
      builder.setMessage(R.string.dialog_select_regions_message);
      builder.setPositiveButton(R.string.common_ok, null);
      builder.show();
      return;
    }

    Intent intent = new Intent();
    if (mDistance == REGION_VALUE) {
      int len = mRegions.size();
      int[] regions = new int[len];
      for (int i = 0; i < len; i++) {
        regions[i] = mRegions.get(i);
      }
      intent.putExtra(RETURN_REGION, regions);
    } else {
      int[] regions = new int[0];
      intent.putExtra(RETURN_REGION, regions);
    }
    intent.putExtra(RETURN_DISTANCE, mDistance);
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, intent);
    mNavigationManager.goBack();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_REGION) {
      if (Activity.RESULT_OK == resultCode) {
        mRegions = data
            .getIntegerArrayListExtra(ChooseRegionSearchSettingFragment.KEY_REGION_CODE_SELECTED);
        mRegionListView.setItemChecked(REGION_INDEX, true);
      }
    }
  }
}
