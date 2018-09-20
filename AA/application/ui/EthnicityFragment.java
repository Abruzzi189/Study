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
import com.application.entity.ItemCommonAdapter;
import com.application.ui.customeview.HolderViewMultipleOption;
import com.application.ui.customeview.MultipleOptionAdapterCommon;
import glas.bbsystem.R;
import java.util.ArrayList;


public class EthnicityFragment extends BaseFragment {

  public static final String RETURN_ETHINICITY = "andG_return_Ethinicity";
  private static final String EXTRA_ETHINICITY = "andG_Ethinicity";
  private ListView mListView;
  private int[] mDataEthnicity = new int[0];
  private ArrayList<ItemCommonAdapter> mListEthnicityItem;
  private MultipleOptionAdapterCommon mAdapter;

  public static EthnicityFragment newInstance(int[] ethnicities) {
    EthnicityFragment fragment = new EthnicityFragment();
    Bundle bundle = new Bundle();
    bundle.putIntArray(EXTRA_ETHINICITY, ethnicities);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null && bundle.containsKey(EXTRA_ETHINICITY)) {
      this.mDataEthnicity = bundle.getIntArray(EXTRA_ETHINICITY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_ethnicity, container,
        false);
    resetNavigationBar();
    initView(view);
    return view;
  }

  public void initView(View view) {
    mListEthnicityItem = new ArrayList<ItemCommonAdapter>();

    String[] displaylist = getResources().getStringArray(
        R.array.ethnicity_list);
    for (int i = 0; i < displaylist.length; i++) {
      if (i == 0) {
        if (mDataEthnicity.length == 0) {
          mListEthnicityItem.add(new ItemCommonAdapter(
              displaylist[i], true, i));
        } else {
          mListEthnicityItem.add(new ItemCommonAdapter(
              displaylist[i], false, i));
        }
      } else {
        mListEthnicityItem.add(new ItemCommonAdapter(displaylist[i],
            false, i));
      }

    }
    if (mDataEthnicity.length > 0) {
      for (int value : mDataEthnicity) {
        mListEthnicityItem.get(value).setCheck(true);
      }
    }
    mAdapter = new MultipleOptionAdapterCommon(view.getContext(),
        R.layout.item_ethnicity, mListEthnicityItem, true);
    mListView = (ListView) view
        .findViewById(R.id.activity_ethnicity_list_ethnicity_item_container);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        HolderViewMultipleOption hold = (HolderViewMultipleOption) view
            .getTag();
        if (position == 0) {
          for (ItemCommonAdapter item : mListEthnicityItem) {
            item.setCheck(false);
          }
          mListEthnicityItem.get(position).setCheck(true);
        } else {
          if (mListEthnicityItem.get(0).isCheck()) {
            mListEthnicityItem.get(0).setCheck(false);
          }
          mListEthnicityItem.get(position).setCheck(
              hold.cbValue.isChecked() ? false : true);
        }
        mAdapter.notifyDataSetChanged();
      }
    });
  }

  protected void resetNavigationBar() {
    getNavigationBar().setNavigationLeftTitle(
        R.string.ethnicity_navigator_left);
    getNavigationBar().setCenterTitle(R.string.ethnicity_title);
    getNavigationBar().setNavigationRightTitle(
        R.string.ethnicity_navigator_right);
    getNavigationBar().setShowUnreadMessage(false);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    int lenght = 0;
    for (ItemCommonAdapter item : mListEthnicityItem) {
      if (item.isCheck()) {
        lenght++;
      }
    }
    int[] ethnicitiesNew = new int[lenght];
    int j = 0;
    for (int i = 0; i < mListEthnicityItem.size(); i++) {
      if (mListEthnicityItem.get(i).isCheck()) {
        if (j <= lenght) {
          ethnicitiesNew[j] = mListEthnicityItem.get(i).getValue();
          j++;
        }
      }
    }
    Intent intent = new Intent();
    intent.putExtra(RETURN_ETHINICITY, ethnicitiesNew);
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, intent);
    getFragmentManager().popBackStack();
  }
}