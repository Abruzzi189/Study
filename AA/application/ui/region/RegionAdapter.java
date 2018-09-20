package com.application.ui.region;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.application.entity.Region;
import com.application.entity.RegionGroup;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegionAdapter extends BaseAdapter {

  public static final int CHOICE_MODE_SINGLE = 1;
  public static final int CHOICE_MODE_MULTI = 2;
  private static final int TYPE_GROUP = 0;
  private static final int TYPE_REGION = 1;
  private static final int TOTAL_TYPE = 2;
  private List<RegionGroup> mRegionGroups;
  private List<RegionGroup> mRegionGroupsSelected = new ArrayList<RegionGroup>();
  private List<Region> mRegionSelected = new ArrayList<Region>();
  private int mChoiceMode;
  private int resourceIdChild;
  private LayoutInflater mLayoutInflater;
  private List<Object> mObjects = new ArrayList<Object>();

  public RegionAdapter(LayoutInflater layoutInflater,
      List<RegionGroup> regionGroups, int choiceMode,
      List<Region> regionSelected) {
    mRegionGroups = regionGroups;
    mChoiceMode = choiceMode;
    mRegionSelected.addAll(regionSelected);
    mLayoutInflater = layoutInflater;
    if (mChoiceMode == CHOICE_MODE_SINGLE) {
      resourceIdChild = R.layout.item_list_region_choice_single;
    } else {
      resourceIdChild = R.layout.item_list_region_choice_multiple;
    }
    parse(mRegionGroups);
    updateGroupSelected(regionSelected);
  }

  public RegionAdapter(LayoutInflater layoutInflater,
      List<RegionGroup> regionGroups, int choiceMode,
      final Region regionSelected) {
    this(layoutInflater, regionGroups, choiceMode, Arrays
        .asList(regionSelected));
  }

  private void onGroupClicked(RegionGroup regionGroup) {
    if (mRegionGroupsSelected.contains(regionGroup)) {
      mRegionGroupsSelected.remove(regionGroup);
      for (Region region : regionGroup.getRegion()) {
        mRegionSelected.remove(region);
      }
    } else {
      mRegionGroupsSelected.add(regionGroup);
      for (Region region : regionGroup.getRegion()) {
        if (!mRegionSelected.contains(region)) {
          mRegionSelected.add(region);
        }
      }
    }
    notifyDataSetChanged();
  }

  private void parse(List<RegionGroup> regionGroups) {
    for (RegionGroup regionGroup : regionGroups) {
      mObjects.add(regionGroup);
      for (Region region : regionGroup.getRegion()) {
        mObjects.add(region);
      }
    }
  }

  private void updateGroupSelected(List<Region> regionSelected) {
    for (RegionGroup regionGroup : mRegionGroups) {
      boolean contain = true;
      for (Region region : regionGroup.getRegion()) {
        if (!regionSelected.contains(region)) {
          contain = false;
          break;
        }
      }
      if (contain) {
        mRegionGroupsSelected.add(regionGroup);
      } else {
        mRegionGroupsSelected.remove(regionGroup);
      }
    }
    notifyDataSetChanged();
  }

  private boolean isRegionGroupSelected(RegionGroup regionGroup) {
    return mRegionGroupsSelected.contains(regionGroup);
  }

  public void updateRegionForSingleChoice(Region region) {
    mRegionSelected.clear();
    updateRegionSelected(region);
  }

  public void updateRegionForMultiChoice(Region region) {
    if (!mRegionSelected.contains(region)) {
      mRegionSelected.add(region);
    } else {
      mRegionSelected.remove(region);
    }
    notifyDataSetChanged();
  }

  public void updateRegionForMultiChoice(List<Region> regions) {
    for (Region region : regions) {
      updateRegionForMultiChoice(region);
    }
  }

  public void updateRegionOfGroup(RegionGroup regionGroup) {
    for (Region region : regionGroup.getRegion()) {
      region.setSelected(true);
      if (!mRegionSelected.contains(region)) {
        mRegionSelected.add(region);
      }
    }
    notifyDataSetChanged();
  }

  private void updateRegionSelected(Region regionSelected) {
    mRegionSelected.add(regionSelected);
    notifyDataSetChanged();
  }

  public void updateRegionSelected(List<Region> regionSelected) {
    mRegionSelected.addAll(mRegionSelected);
    notifyDataSetChanged();
  }

  public List<Region> getRegionSelected() {
    return mRegionSelected;
  }

  @Override
  public int getViewTypeCount() {
    return TOTAL_TYPE;
  }

  @Override
  public int getItemViewType(int position) {
    if (mObjects.get(position) instanceof RegionGroup) {
      return TYPE_GROUP;
    } else {
      return TYPE_REGION;
    }
  }

  private boolean isRegionSelected(Region region) {
    for (Region item : mRegionSelected) {
      if (item.equals(region)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getCount() {
    return mObjects.size();
  }

  @Override
  public Object getItem(int position) {
    return mObjects.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int type = getItemViewType(position);
    switch (type) {
      case TYPE_GROUP:
        RegionGroup regionGroup = (RegionGroup) mObjects.get(position);
        return getGroupView(regionGroup, convertView, parent);
      case TYPE_REGION:
        Region region = (Region) mObjects.get(position);
        return getRegionView(region, convertView, parent);
      default:
        throw new IllegalArgumentException("Invalid view type");
    }
  }

  private View getGroupView(final RegionGroup regionGroup, View convertView,
      ViewGroup parent) {
    GroupHolder holder = null;
    if (convertView == null) {
      convertView = mLayoutInflater.inflate(
          R.layout.item_list_region_group, parent, false);
      holder = new GroupHolder();
      holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
      holder.btnSelect = (Button) convertView.findViewById(R.id.select);
      convertView.setTag(holder);
    } else {
      holder = (GroupHolder) convertView.getTag();
    }
    if (mChoiceMode == CHOICE_MODE_SINGLE) {
      holder.btnSelect.setVisibility(View.GONE);
    } else {
      holder.btnSelect.setVisibility(View.VISIBLE);
    }
    if (isRegionGroupSelected(regionGroup)) {
      holder.btnSelect.setText(R.string.deselect);
    } else {
      holder.btnSelect.setText(R.string.select);
    }
    holder.txtTitle.setText(regionGroup.getName());
    holder.btnSelect.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        onGroupClicked(regionGroup);
      }
    });
    return convertView;
  }

  private View getRegionView(Region region, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = mLayoutInflater.inflate(resourceIdChild, parent,
          false);
    }
    CheckedTextView checkedTextView = ((CheckedTextView) convertView
        .findViewById(android.R.id.text1));
    checkedTextView.setText(region.getName());
    checkedTextView.setChecked(isRegionSelected(region));
    return convertView;
  }

  public interface OnGroupSelected {

    public void onGroupSelected(RegionGroup regionGroup);
  }

  private static class GroupHolder {

    public TextView txtTitle;
    public Button btnSelect;
  }

}
